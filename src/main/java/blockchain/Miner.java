package blockchain;

import java.util.List;

class Miner extends Participant{
    private static long idCount = 1;

    private MinerThread currentThread;

    public Miner() {
        super("miner" + idCount++, 100);

    }

    public MinerThread reassembleThread(Blockchain chainContext, List<Message> msg){
        currentThread = new MinerThread(this, chainContext, msg);

        return currentThread;
    }

    public void start(){
        currentThread.start();
    }

    public boolean isActive() {
        return currentThread.isAlive();
    }

    public void disable() {
        currentThread.disable();
    }

    public Block getBlock(){
        return currentThread.getBlock();
    }

    public void addReward() {
        this.startingBalance += 100;
    }
}
