package extra.banking;

class Card {
    private static int idCounter;
    private final int id;
    private final String bin;
    private final String ai;
    private final String checksum;
    private final String codeNumber;
    private final String pin;

    private int balance;


    public Card(String bin, String ai, String checksum, String pin) {
        this.pin = pin;
        this.bin = bin;
        this.ai = ai;
        this.checksum = checksum;
        this.codeNumber = bin + ai + checksum;
        this.balance = 0;

        this.id = idCounter++;
    }

    public Card(String number, String pin, int balance,int id) {
        this.pin = pin;
        this.bin = number.substring(0,6);
        this.ai = number.substring(6,15);
        this.checksum = number.substring(15);
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
