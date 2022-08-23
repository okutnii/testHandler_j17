package blockchain;

import java.io.Serializable;
import java.util.Date;

class Block implements Serializable {
    private int id;
    private int magicNumber;
    private int zeros;
    private String minerName;
    private long timestamp;
    private int lowerTimeLimit;
    private int upperTimeLimit;
    private String data;
    private String hash;
    private String prevHash;
    private long generatingTime;
    private BlockState state = BlockState.EMPTY;


    public Block() {
    }

    public Block(MinerThread minerThread, String prevHash, int prevId) {
        this.prevHash = prevHash;
        this.zeros = minerThread.getZeros();
        this.minerName = minerThread.getMinerName();
        this.hash = minerThread.getHash();
        this.magicNumber = minerThread.getMagicNumber();
        this.generatingTime = minerThread.getGeneratingTime();
        this.data = minerThread.getInput();

        this.lowerTimeLimit = minerThread.lowerTimeLimit;
        this.upperTimeLimit = minerThread.upperTimeLimit;

        this.timestamp = new Date().getTime();

        this.id = ++prevId;
    }

    /**
     * Copies data from given block
     *
     * @param block source of data to copy
     */
    public void copyData(Block block) {
        this.prevHash = block.prevHash;
        this.zeros = block.zeros;
        this.minerName = block.minerName;
        this.hash = block.hash;
        this.magicNumber = block.magicNumber;
        this.generatingTime = block.generatingTime;
        this.timestamp = block.timestamp;
        this.id = block.id;
        this.data = block.data;
        this.lowerTimeLimit = block.lowerTimeLimit;
        this.upperTimeLimit = block.upperTimeLimit;
        this.state = BlockState.UNCHECKED;
    }

    public BlockState getBlockState() {
        return state;
    }

    public void setBlockState(BlockState state) {
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public long getGeneratingTime() {
        return generatingTime;
    }

    public String getHash() {
        return hash;
    }

    public String getPrevHash() {
        return prevHash;
    }

    @Override
    public String toString() {
        String zerosInfo = generatingTime < lowerTimeLimit ? "\nN was increased to " + ++zeros :
                generatingTime > upperTimeLimit ? "\nN was decreased by " + --zeros :
                        "\nN stays the same";
        if (data == null || data.isEmpty()) {
            data = "\nNo transaction";
        }
        return "Block:" +
                "\nCreated by: " + minerName +
                "\n" + minerName + " gets 100 VC" +
                "\nId: " + id +
                "\nTimestamp: " + timestamp +
                "\nMagic number: " + magicNumber +
                "\nHash of the previous block:\n" + prevHash +
                "\nHash of the block:\n" + hash +
                "\nBlock data: " + data +
                "\nBlock was generating for " + generatingTime + " seconds" +
                zerosInfo;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
