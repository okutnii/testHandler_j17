package blockchain;


import java.io.*;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

class Blockchain{
    //  quantity of miners
    private final int quantity = 10;

    //  number of required start-zeros in hash
    private int zeros = 0;

    //  desirable time limits for block generation
    public final int lowerTimeLimit = 5;
    public final int upperTimeLimit = 15;

    //  message block id
    private static long identifier = 0;
    private static long verifierMessageId = 0;

    public static final String BLOCKCHAIN_FILENAME = "blockchain.ser";

    private final Miner[] miners;

    //  messagePool receives messages that are comes from messageBuffer and sends them to miners
    private Queue<Message> messagePool;

    //  messageBuffer store messages for a while messagePool is used
    private final Queue<Message> messageBuffer;

    private List<Block> chain = null;

    //  thread to process the block
    private Thread thread;

    static ReentrantLock generatorLock = new ReentrantLock();
    static ReentrantLock bufferLock = new ReentrantLock();

    public Blockchain() {
        clearFile(new File(BLOCKCHAIN_FILENAME));

        miners = new Miner[quantity];

        initMiners();

        messagePool = new LinkedList<>();
        messageBuffer = new LinkedList<>();

        getChainFromFileIfExists();

        thread =  new Thread(this::generateBlock);
    }

    private Message createSignedTransaction(Transaction transaction) throws Exception {

        if(transaction == null || !transaction.isValidTransaction())
            return null;

        KeyPair keyPair = GenerateKeys.generateKeys();

        return Message.getSignedDataOf(transaction.toString(), keyPair, this.generateId());
    }

    private void initMiners() {
        for(int i = 0; i < quantity; i++){
            miners[i] = new Miner();
        }
    }

    private void addMessageToBuffer(Message message){
        bufferLock.lock();

        if(message == null) {
            bufferLock.unlock();
            return;
        }

        if(verifierMessageId == message.getId()) {
            messageBuffer.add(message);
        }

        verifierMessageId++;

        bufferLock.unlock();
    }

    private void releaseBuffer(){
        bufferLock.lock();

        messagePool = new LinkedList<>(messageBuffer);
        messageBuffer.clear();

        bufferLock.unlock();
    }

    private void appendMessage(Message message) {

        addMessageToBuffer(message);

        if(!generatorLock.isLocked()) {
            startGenerating();
        }
    }

    private void startGenerating() {
        generatorLock.lock();

        if(!thread.isAlive()) {

            thread =  new Thread(this::process);

            thread.start();
        }

        generatorLock.unlock();
    }

    private void process(){
        do {
            generateBlock();
        }
        while(!messageBuffer.isEmpty());
    }

    private synchronized Block mineBlock(List<Message> msg)   {

        //  start   miners      //
        for (int i = 0; i < quantity; ++i) {

            miners[i].reassembleThread(this, msg);

            miners[i].start();
        }

        //  block to be filled  //
        Block block = new Block();

        //  receiving   block   //
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

    private void disableAllMiners(){
        for (Miner miner : miners) {
            if (miner.isActive()) {
                miner.disable();
            }
        }
    }

    public synchronized void generateBlock()  {

        releaseBuffer();

        //  appends messages
        List<Message> list = new ArrayList<>(messagePool);

        //  verify message pool
        try {
            messagePool = verifyAll(messagePool);
        } catch (Exception e) {
            e.printStackTrace();
        }
        messagePool.clear();

        Block block = mineBlock(list);

        //  stop unlucky miners //
        disableAllMiners();

        long generatingTime = block.getGeneratingTime();
        calibrateZeros(generatingTime);

        chain.add(block);

        writeChainIntoFile();

    }

    private void calibrateZeros(long generatingTime) {
        if(generatingTime < lowerTimeLimit) {
            zeros++;
        } else {
            if (generatingTime >= upperTimeLimit && zeros > 0) {
                zeros--;
            }
        }
    }

    private Queue<Message> verifyAll(Queue<Message> messagePool) throws Exception {
        Queue<Message> verifiedPool = new LinkedList<>();

        for(Message message: messagePool){

            message = VerifyMessage.verifyMessageAndGet(message);

            if(message != null) {
                verifiedPool.add(message);
            }

        }

        return verifiedPool;
    }

    private void writeChainIntoFile() {
        try {
            File file = new File(BLOCKCHAIN_FILENAME);

            file.createNewFile();

            FileOutputStream fs = new FileOutputStream(file);
            try(ObjectOutputStream os = new ObjectOutputStream(fs)) {

                os.writeObject(this.chain);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Block> getChainFromFileIfExists() {
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

        return this.chain;

    }

    private List<Block> readChainFromFile(File file) throws IOException {
        List<Block> chain = null;

        //  reading chain from file                 //
        FileInputStream fis = new FileInputStream(file);
        try(ObjectInputStream ois = new ObjectInputStream(fis)) {

            chain = (List<Block>) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            //  e.printStackTrace();
        }

        if(chain == null || !checkChain(chain))
            chain = new LinkedList<>();

        //  if file consists wrong data, purify it  //
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
     *  returns true if chain is valid
     *  */
    private boolean checkChain(List<Block> chain) {
        String prevHash = "0";
        String prefix = "0".repeat(this.zeros);

        for(Block block: chain){
            if(!block.getPrevHash().equals(prevHash) ||
                    !block.getPrevHash().startsWith(prefix))
                return false;

            prevHash = block.getHash();
        }

        return true;
    }

    private boolean isValidBlock(Block block){
        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();

        return block.getPrevHash().equals(prevHash);
    }

    public int getZeros(){
        return zeros;
    }

    public Miner[] getMiners(){
        return miners.clone();
    }

    public List<Block> getChain(){
        return new LinkedList<>(chain);
    }

    public long generateId() {
        return identifier++;
    }

    public void joinThread() throws InterruptedException {
        thread.join();
    }

    public void processTransaction(Transaction transaction) throws Exception {
        Message msg = createSignedTransaction(transaction);

        if(msg != null){
            transaction.commitTransaction();

            appendMessage(msg);
        }
    }
}

