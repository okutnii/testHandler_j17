package blockchain;

import java.io.*;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Blockchain {
    private final int quantity = 10;
    private int zeros = 0;

    public final int lowerTimeLimit = 3;
    public final int upperTimeLimit = 10;
    private static long identifier = 0;
    private static long verifierMessageId = 0;
    public static final String BLOCKCHAIN_FILENAME = "blockchain.ser";
    private final Miner[] miners;
    private Queue<Message> messagePool;
    private final Queue<Message> messageBuffer;
    private List<Block> chain = null;
    private Thread thread;
    static ReentrantLock generatorLock = new ReentrantLock();
    static ReentrantLock bufferLock = new ReentrantLock();

    public Blockchain() {
        Map<String, Integer> l = new LinkedHashMap<>();
        l.put(null, 1);
        l.put(null, 2);

        clearFile(new File(BLOCKCHAIN_FILENAME));
        miners = new Miner[quantity];
        messagePool = new LinkedList<>();
        messageBuffer = new LinkedList<>();
        initChainFromFileIfExists();
        thread = new Thread(this::generateBlock);

        for (int i = 0; i < quantity; i++) {
            miners[i] = new Miner();
        }
    }

    /**
     * @param transaction transaction to sign
     * @return message of signed transaction
     * @throws IllegalStateException in case transaction is null or invalid
     */
    public Message createSignedTransaction(Transaction transaction) throws Exception {
        if (transaction == null || !transaction.isValidTransaction()) {
            throw new IllegalStateException();
        }
        KeyPair keyPair = GenerateKeys.generateKeys();

        return Message.getSignedDataOf(transaction.toString(), keyPair, this.generateId());
    }

    /**
     * Adds a message to the buffer queue to be placed in a block
     *
     * @param message massage to add in the buffer
     */
    private void addMessageToBuffer(Message message) {
        bufferLock.lock();
        if (message == null) {
            bufferLock.unlock();
            return;
        }
        if (verifierMessageId == message.getId()) {
            messageBuffer.add(message);
        }
        verifierMessageId++;
        bufferLock.unlock();
    }

    /**
     * Releases buffer and fulfil the pull
     */
    private void releaseBuffer() {
        bufferLock.lock();
        messagePool = new LinkedList<>(messageBuffer);
        messageBuffer.clear();
        bufferLock.unlock();
    }

    /**
     * Appends message to the buffer and starts generating block if it is not generating.
     *
     * @param message message to append
     */
    private void appendMessage(Message message) {
        addMessageToBuffer(message);
        if (!generatorLock.isLocked()) {
            startGenerating();
        }
    }

    /**
     * Starts generating a block if it is not generating
     */
    private void startGenerating() {
        generatorLock.lock();
        if (!thread.isAlive()) {
            thread = new Thread(this::process);
            thread.start();
        }
        generatorLock.unlock();
    }

    /**
     * Processes generating blocks until they are coming
     */
    private void process() {
        do {
            generateBlock();
        }
        while (!messageBuffer.isEmpty());
    }

    /**
     * @param messages list of messages to process them into a block
     * @return generated block
     */
    public synchronized Block mineBlock(List<Message> messages) {
        for (int i = 0; i < quantity; ++i) {
            miners[i].reassembleThread(this, messages);
            miners[i].start();
        }
        Block block = new Block();
        while (block.getBlockState() != BlockState.VALID) {
            for (Miner miner : miners) {
                if (!miner.isActive()) {
                    if (isValidBlock(miner.getBlock())) {
                        block.copyData(miner.getBlock());
                        block.setBlockState(BlockState.VALID);
                        miner.addReward();
                        break;
                    }
                }
            }
        }
        return block;
    }

    /**
     * Disables all miners
     */
    private void disableAllMiners() {
        for (Miner miner : miners) {
            if (miner.isActive()) {
                miner.disable();
            }
        }
    }

    /**
     * generate block with miners
     */
    public synchronized void generateBlock() {
        releaseBuffer();
        List<Message> list = new ArrayList<>(messagePool);
        try {
            messagePool = verifyAll(messagePool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messagePool.clear();
        Block block = mineBlock(list);
        disableAllMiners();
        long generatingTime = block.getGeneratingTime();
        calibrateZeros(generatingTime);
        chain.add(block);
        writeChainIntoFile();
    }

    /**
     * Calibrates zero numbers according to limits defined
     *
     * @param generatingTime time of block generation
     */
    private void calibrateZeros(long generatingTime) {
        if (generatingTime < lowerTimeLimit) {
            zeros++;
        } else {
            if (generatingTime >= upperTimeLimit && zeros > 0) {
                zeros--;
            }
        }
    }

    /**
     * @param messagePool message pool to verify
     * @return pool of verified messages
     */
    private Queue<Message> verifyAll(Queue<Message> messagePool) throws Exception {
        Queue<Message> verifiedPool = new LinkedList<>();
        for (Message message : messagePool) {
            message = VerifyMessage.verifyMessageAndGet(message);
            if (message != null) {
                verifiedPool.add(message);
            }
        }
        return verifiedPool;
    }

    /**
     * Writes resulting chain in the file
     */
    private void writeChainIntoFile() {
        try {
            File file = new File(BLOCKCHAIN_FILENAME);

            FileOutputStream fs = new FileOutputStream(file);
            try (ObjectOutputStream os = new ObjectOutputStream(fs)) {
                os.writeObject(this.chain);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initiates chain from file if exists
     */
    public void initChainFromFileIfExists() {
        try {
            File file = new File(BLOCKCHAIN_FILENAME);
            if (file.createNewFile()) {
                this.chain = new LinkedList<>();
            } else {
                this.chain = readChainFromFile(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file file to read
     * @return read chain from file or empty list if file is empty
     */
    private List<Block> readChainFromFile(File file) throws IOException {
        List<Block> chain = null;
        FileInputStream fis = new FileInputStream(file);

        if(file.createNewFile()) {
            try (ObjectInputStream ois = new ObjectInputStream(fis)) {
                chain = (List<Block>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (chain == null || !checkChain(chain)) {
            chain = new LinkedList<>();
        }
        clearFile(file);
        return chain;
    }

    private void clearFile(File file) {
        FileOutputStream writer;
        try {
            writer = new FileOutputStream(file);
            writer.write(("").getBytes());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns true if chain is valid
     */
    public boolean checkChain(List<Block> chain) {
        String prevHash = "0";
        String prefix = "0".repeat(this.zeros);

        for (Block block : chain) {
            if (!block.getPrevHash().equals(prevHash) ||
                    !block.getPrevHash().startsWith(prefix))
                return false;

            prevHash = block.getHash();
        }

        return true;
    }

    /**
     * Compares the previous hash of the given block with the hash of the last block in the blockchain
     *
     * @param block block to validate
     * @return true if block is valid, false otherwise
     */
    private boolean isValidBlock(Block block) {
        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();
        return block.getPrevHash().equals(prevHash);
    }

    public int getZeros() {
        return zeros;
    }

    public Miner[] getMiners() {
        return miners.clone();
    }

    public List<Block> getChain() {
        return new LinkedList<>(chain);
    }

    public long generateId() {
        return identifier++;
    }

    public void joinThread() throws InterruptedException {
        thread.join();
    }

    /**
     * Commits transaction and appends signed message of the given transaction
     *
     * @param transaction transaction to process
     */
    public void processTransaction(Transaction transaction) throws Exception {
        Message msg = createSignedTransaction(transaction);
        if (msg != null) {
            transaction.commitTransaction();
            appendMessage(msg);
        }
    }

    public void setZeros(int zeros) {
        this.zeros = zeros;
    }
}

