package extra.banking;

class Card {
    private static int idCounter;
    private final int id;
    private final String codeNumber;
    private final String pin;

    private final int balance;


    public Card(String bin, String ai, String checksum, String pin) {
        this.pin = pin;
        this.codeNumber = bin + ai + checksum;
        this.balance = 0;

        this.id = idCounter++;
    }

    public Card(String number, String pin, int balance,int id) {
        this.pin = pin;
        this.codeNumber = number;
        this.balance = balance;

        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean canTransfer(int val){
        return balance - val >= 0;
    }

    public int getBalance() {
        return balance;
    }

    public String getPin() {
        return pin;
    }

    public String getCodeNumber() {
        return codeNumber;
    }
}
