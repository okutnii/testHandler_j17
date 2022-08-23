package blockchain;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class MainTest {

    @Test
    public void testSystem() throws InterruptedException, Exception {
        //  create blockchain       //
        Blockchain blockchain = new Blockchain();


        Participant p1 = new Participant("Steve", 50);
        Participant p2 = new Participant("Bob", 50);
        Participant p3 = new Participant("Robert", 50);
        Participant p4 = new Participant("Mary", 50);
        Participant p5 = new Participant("Alex", 50);
        Participant p6 = new Participant("Trinity", 50);
        Participant p7 = new Participant("Clare", 50);
        Participant p8 = new Participant("Kate", 50);
        Participant p9 = new Participant("Sasha", 50);
        Participant p10 = blockchain.getMiners()[0];

        Queue<Transaction> transactionPool = new ArrayDeque<>();

        transactionPool.add(new Transaction(p1, p2, 10));
        transactionPool.add(new Transaction(p1, p3, 10));
        transactionPool.add(new Transaction(p1, p5, 10));
        transactionPool.add(new Transaction(p1, p7, 10));
        transactionPool.add(new Transaction(p3, p7, 10));
        transactionPool.add(new Transaction(p4, p5, 10));
        transactionPool.add(new Transaction(p7, p2, 10));
        transactionPool.add(new Transaction(p7, p8, 10));
        transactionPool.add(new Transaction(p4, p1, 10));
        transactionPool.add(new Transaction(p2, p1, 10));
        transactionPool.add(new Transaction(p2, p2, 10));
        transactionPool.add(new Transaction(p9, p3, 10));
        transactionPool.add(new Transaction(p3, p9, 23));
        transactionPool.add(new Transaction(p6, p10, 14));
        transactionPool.add(new Transaction(p6, p5, 10));
        transactionPool.add(new Transaction(p5, p6, 13));
        transactionPool.add(new Transaction(p3, p9, 2));
        transactionPool.add(new Transaction(p10, p1, 100));
        transactionPool.add(new Transaction(p1, p2, 10));

        blockchain.generateBlock();

        for (Transaction transaction : transactionPool) {
            blockchain.processTransaction(transaction);
            TimeUnit.MILLISECONDS.sleep(200);
        }

        blockchain.joinThread();
        List<Block> chain = blockchain.getChain();

        for (Block block : chain) {
            System.out.println(block.toString() + "\n");
        }
    }
}
