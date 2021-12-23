package blockchain;

class Transaction {
    private final Participant recipient;
    private final Participant sender;
    private final long amount;

    public Transaction(Participant sender, Participant recipient, long amount){
        this.sender     = sender;
        this.recipient  = recipient;
        this.amount     = amount;
    }

    /**
     * returns true if the sender has enough VC
     * */
    public boolean isValidTransaction(){
        return sender.isValidTransaction(this);
    }


    public synchronized boolean commitTransaction(){
        if(!isValidTransaction())
            return false;

        sender.commitTransaction(this);
        recipient.commitTransaction(this);


        return true;
    }

    /**
     * determine the consequences of transaction
     * depending on Participant: whether recipient, or sender
     * */
    public long getResult(Participant participant) {
        if(participant.equals(recipient))
            return amount;

        if(participant.equals(sender))
            return -amount;

        throw new IllegalArgumentException("Who the heck is this??\nParticipant: " + participant +
                "\nTransaction: " + this.toString());
    }

    @Override
    public String toString() {
        return "\n" + sender + " sent " + amount + " VC to " + recipient
                //  + "\t\t--------- " + sender + " has " + sender.getBalance() + " & "
                //  + recipient + " has " + recipient.getBalance()
                ;
    }
}
