package blockchain;

import java.util.ArrayDeque;
import java.util.Queue;

class Participant {
    protected final String name;
    protected long balance;
    protected long startingBalance;
    protected final Queue<Transaction> history;

    public Participant(String name, long startingBalance) {
        this.name = name;
        this.history = new ArrayDeque<>();
        if (startingBalance >= 0) {
            this.startingBalance = startingBalance;
        } else {
            this.startingBalance = 0;
        }
    }

    /**
     * Calculates the balance by looking at all committed transactions
     */
    protected void countBalance() {
        balance = startingBalance;
        for (Transaction transaction : history) {
            balance += transaction.getResult(this);
        }
    }

    /**
     * Commits transaction by adding it in the history and counts current balance
     *
     * @param transaction transaction
     */
    public void commitTransaction(Transaction transaction) {
        history.add(transaction);
        countBalance();
    }


    public String getName() {
        return name;
    }

    /**
     * @param transaction transaction to validate
     * @return true if transaction is valid, false otherwise
     */
    public boolean isValidTransaction(Transaction transaction) {
        long res = startingBalance + transaction.getResult(this);
        return res >= 0;
    }
}
