package blockchain;

import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        execute();
    }

    private static void execute() throws Exception{

        //  create blockchain       //
        Blockchain blockchain = new Blockchain();


        //  add participants        //
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



        //  prepare transaction pool //
        Queue<Transaction> transactionPool = new ArrayDeque<>();{
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
        }

        //  start    generation     //
        blockchain.generateBlock();

        //  simulate input          //
        for(Transaction transaction: transactionPool){

            blockchain.processTransaction(transaction);

            //  simulate user delay //
            Thread.sleep(2000);
        }

        blockchain.joinThread();


        //  input blocks            //
        List<Block> chain = blockchain.getChain();

        // input first M blocks     //
        int M = chain.size();       //  15; //  chain.size();
        for(int i = 0; i < M; i++) {
            System.out.println(chain.get(i).toString() + "\n");
        }
    }
}





