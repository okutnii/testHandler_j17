package blockchain;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


class MinerThread extends Thread {
    private final List<Block> chain;
    private final List<Message> messages;
    private final String input;
    private String hash;
    private final String minerName;
    private Block block;
    private int magicNumber;
    private final int zeros;
    private long generatingTime;
    public final int upperTimeLimit;
    public final int lowerTimeLimit;
    private boolean isActive = true;

    MinerThread(Miner owner, Blockchain chainContext, List<Message> messages) {
        this.messages = messages;
        this.zeros = chainContext.getZeros();
        this.chain = chainContext.getChain();
        this.lowerTimeLimit = chainContext.lowerTimeLimit;
        this.upperTimeLimit = chainContext.upperTimeLimit;
        this.minerName = owner.getName();
        this.input = getMessageTexts();
        this.block = new Block();
    }

    /**
     * @return string of concatenated list message elements
     */
    private String getMessageTexts() {
        StringBuilder sb = new StringBuilder();
        for (Message message : messages) {
            sb.append(new String(message.getMessageText()));
        }
        return sb.toString();
    }

    /**
     * @param input message in generate hash
     * @param zeros quantity of zeros prefix
     * @return string hash which starts with certain quantity of numbers
     */
    public String generateMagicHash(String input, int zeros) {
        int maybeMagic;
        String prefix = "0".repeat(zeros);
        String hash;
        String test;
        Random random = new Random();
        do {
            maybeMagic = random.nextInt();
            test = input.concat(String.valueOf(maybeMagic));
            hash = StringUtil.applySha256(test);
        } while (isActive && !hash.startsWith(prefix));
        if (!isActive) {
            return null;
        }
        magicNumber = maybeMagic;
        this.hash = hash;
        return hash;
    }

    public void disable() {
        this.isActive = false;
    }

    /**
     * Generates magic hash, places it in the block,
     * measures generating time and collects data in the block
     */
    @Override
    public void run() {
        long startGenerating = System.nanoTime();
        hash = generateMagicHash(input, zeros);
        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();
        int prevId = chain.isEmpty() ? 0 : chain.get(chain.size() - 1).getId();
        this.generatingTime = TimeUnit.SECONDS.convert(System.nanoTime() - startGenerating, TimeUnit.NANOSECONDS);
        this.block = new Block(this, prevHash, prevId);
        this.block.setBlockState(BlockState.UNCHECKED);
    }

    public String getHash() {
        return hash;
    }

    public String getInput() {
        return input;
    }

    public String getMinerName() {
        return minerName;
    }

    public Block getBlock() {
        return block;
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public int getZeros() {
        return zeros;
    }

    public long getGeneratingTime() {
        return generatingTime;
    }
}
