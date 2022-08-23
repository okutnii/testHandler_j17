package blockchain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BlockchainTest {
    @Test
    public void testCheckChainTrue() {
        Blockchain blockchain = new Blockchain();
        blockchain.setZeros(0);
        List<Block> chain = new ArrayList<>();
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "0", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash1", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash2", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash3", 0));

        chain.get(0).setHash("hash1");
        chain.get(1).setHash("hash2");
        chain.get(2).setHash("hash3");
        chain.get(3).setHash("hash4");

        assertTrue(blockchain.checkChain(chain));
    }

    @Test
    public void testCheckChainFalse() {
        Blockchain blockchain = new Blockchain();
        blockchain.setZeros(0);
        List<Block> chain = new ArrayList<>();
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "0", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash1", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash2", 0));
        chain.add(new Block(new MinerThread(new Miner(), new Blockchain(), new ArrayList<>()), "hash3", 0));

        chain.get(0).setHash("hash1");
        chain.get(1).setHash("WRONG");
        chain.get(2).setHash("hash3");
        chain.get(3).setHash("hash4");

        assertFalse(blockchain.checkChain(chain));
    }

    @Test
    public void testCreateSignedTransactionOfNull() throws Exception {
        assertThrows(IllegalStateException.class,
                () -> new Blockchain().createSignedTransaction(null));
    }

    @Test
    public void testCreateSignedTransactionOfInvalid() throws Exception {
        Participant bruce = new Participant("Bruce", 0);
        Participant mary = new Participant("Mary", 0);
        Transaction transaction = new Transaction(bruce, mary, 10);

        assertThrows(IllegalStateException.class,
                () -> new Blockchain().createSignedTransaction(transaction));
    }

    @Test
    public void testCreateSignedTransactionOfValid() throws Exception {
        Participant bruce = new Participant("Bruce", 10);
        Participant mary = new Participant("Mary", 0);
        Transaction transaction = new Transaction(bruce, mary, 10);

        assertNotNull(new Blockchain().createSignedTransaction(transaction));
    }

    @Test
    public void testMineBlock() throws Exception {
        Blockchain blockchain = new Blockchain();
        blockchain.setZeros(0);

        Participant bruce = new Participant("Bruce", 10);
        Participant mary = new Participant("Mary", 0);
        Transaction transaction = new Transaction(bruce, mary, 10);

        Message msg = blockchain.createSignedTransaction(transaction);

        Block actual = blockchain.mineBlock(List.of(msg));

        assertNotNull(actual);
    }
}
