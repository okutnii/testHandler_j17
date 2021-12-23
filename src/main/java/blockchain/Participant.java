package blockchain;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

class Participant {
    protected final String name;
    protected long balance;
    protected long startingBalance;
    protected final Queue<Transaction> history;


    public Participant(String name, long startingBalance){
        this.name            = name;
        this.history         = new ArrayDeque<>();

        //  can't be negative
        if(startingBalance >= 0) {
            this.startingBalance = startingBalance;
        }else {
            this.startingBalance = 0;
        }
    }

    /**
     *  calculates the balance by looking at all committed transactions
     *  and returns it
     * */
    protected long countBalance(){
        balance = startingBalance;

        for(Transaction transaction: history){
            balance += transaction.getResult(this);
        }

        return balance;
    }

    public boolean commitTransaction(Transaction transaction){

        history.add(transaction);

        countBalance();

        return true;
    }

    public long getBalance(){
        return balance;
    }

    public String getName() {
        return name;
    }

    public boolean isValidTransaction(Transaction transaction){
        long res = startingBalance + transaction.getResult(this);

        return res >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return balance == that.balance && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, balance);
    }

    @Override
    public String toString(){
        return name;
    }
}
