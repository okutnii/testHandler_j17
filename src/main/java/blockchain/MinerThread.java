package blockchain;


import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static blockchain.StringUtil.applySha256;


class MinerThread extends Thread{
    private final List<Block> chain;
    private final List<Message> messages;

    private final String input;
    private String hash;
    private final String minerName;


    private Block block;

    private int magicNumber;
    private final int zeros;
    private long generatingTime;

    //  time limits for block generation
    public final int lowerTimeLimit;
    public final int upperTimeLimit;

    private boolean isActive = true;

    MinerThread(Miner owner, Blockchain chainContext, List<Message> messages){
        this.messages           = messages;
        this.zeros              = chainContext.getZeros();
        this.chain              = chainContext.getChain();

        this.lowerTimeLimit     = chainContext.lowerTimeLimit;
        this.upperTimeLimit     = chainContext.upperTimeLimit;

        this.minerName          = owner.getName();

        this.input              = getMessageTexts();

        this.block              = new Block();
    }

    private String getMessageTexts(){
        StringBuilder sb = new StringBuilder();

        for(Message message: messages){
            sb.append(new String(message.getMessageText()));
        }

        return sb.toString();
    }

    public String generateMagicHash(){
        int maybeMagic;
        String prefix = "0".repeat(zeros);
        String hash;
        String test;
        Random random = new Random();

        do {
            maybeMagic = random.nextInt();
            test = input.concat(String.valueOf(maybeMagic));

            hash = applySha256(test);
        } while (isActive && !hash.startsWith(prefix));

        magicNumber = maybeMagic;
        this.hash = hash;

        return hash;
    }

    public void disable(){
        this.isActive = false;
    }

    @Override
    public void run() {
        long startGenerating = System.nanoTime();

        hash = generateMagicHash();

        String prevHash = chain.isEmpty() ? "0" : chain.get(chain.size() - 1).getHash();
        int prevId      = chain.isEmpty() ?  0  : chain.get(chain.size() - 1).getId();

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
