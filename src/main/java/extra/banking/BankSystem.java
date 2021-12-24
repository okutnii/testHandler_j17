package extra.banking;

import java.util.*;

class BankSystem {
    private final Random random = new Random(777);
    private static final String bin = "" + 400000;

    private final CardDAO cardDAO ;

    public BankSystem(String path){
        cardDAO = new CardDAO(path, null, null);
    }

    public void bankMenu(Scanner scanner) {

        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            String input = scanner.nextLine();
            int respond;

            try {
                respond = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Wrong input. Try again.");
                continue;
            }

            switch (respond) {
                case 1 -> createAccount();
                case 2 -> logInto(scanner);
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid item.");
            }
        }
    }

    public void createAccount() {

        Card card = generateCard();

        cardDAO.insertCard(card);

        System.out.println("Your card number:");
        System.out.println(card.getCodeNumber());

        System.out.println("Your card PIN:");
        System.out.println(card.getPin());

    }

    private Card generateCard() {
        long aiI = random.nextInt(1_000_000_000);
        int pinI = random.nextInt(10000);

        String ai = String.format("%09d", aiI);
        String pin = String.format("%04d", pinI);

        byte checksumI = luhnAlgorithm(bin + ai);
        String checksum = String.format("%d", checksumI);

        return new Card(bin, ai, checksum, pin);
    }

    private byte luhnAlgorithm(String number) {
        int[] numbers = Arrays.stream(number.split(""))
                .map(Integer::parseInt)
                .mapToInt(i -> i).toArray();

        for (int i = 0; i < numbers.length; i++) {
            if (i % 2 == 0) {
                numbers[i] *= 2;
                if (numbers[i] > 9) {
                    numbers[i] -= 9;
                }
            }
        }

        int total = Arrays.stream(numbers).reduce(0, Integer::sum);
        int tail = total % 10;

        return (byte) ((10 - tail) % 10);
    }

    public void logInto(Scanner scanner) {
        System.out.println("Enter your card number:");
        String number = scanner.nextLine();

        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();

        Optional<Card> optionalCard = cardDAO.getCard(number);

        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();

            if (card.getPin().equals(pin)) {
                System.out.println("You have successfully logged in!");
                cardMenu(card, scanner);

                return;
            }
        }
        System.out.println("Wrong card number or PIN!");
    }

    private void cardMenu(Card card, Scanner scanner) {

        while (true) {
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit\n");

            String input = scanner.nextLine();
            int respond;

            try {
                respond = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Wrong input. Try again.");
                continue;
            }

            switch (respond) {
                case 1 -> getBalance(card);
                case 2 -> {
                    int income = requestIncome(scanner);
                    addIncome(card, income);
                }
                case 3 -> {
                    String recipient = requestRecipient(scanner);
                    if (isValidNumber(recipient)) {
                        Optional<Card> optRecipientCard = getCardNumber(recipient);

                        if (isValidRecipient(card, optRecipientCard)) {
                            requestTransfer(scanner, card, optRecipientCard.get());
                        }
                    } else {
                        System.out.println("Probably you made a mistake in the card number. Please try again!");
                    }
                }
                case 4 -> {
                    closeCard(card);
                    return;
                }
                case 5 -> {
                    logOut();
                    return;
                }
                case 0 -> {
                    System.out.println("Bye!");
                    System.exit(0);
                    return;
                }
                default -> System.out.println("Invalid item.");
            }
        }
    }

    private void closeCard(Card card) {
        cardDAO.deleteCard(card);

        System.out.println("The account has been closed!\n");
    }

    private boolean isValidNumber(String recipient) {
        int length = recipient.length();
        String binAndAi = recipient.substring(0, length-1);

        String checksum = "" + luhnAlgorithm(binAndAi);

        return checksum.equals(recipient.substring(length-1));
    }

    private boolean isValidRecipient(Card card, Optional<Card> optRecipientCard) {

        if (optRecipientCard.isEmpty()) {
            System.out.println("Such a card does not exist.");

            return false;
        } else if (optRecipientCard.get()
                .getCodeNumber()
                .equals(card.getCodeNumber())) {
            System.out.println("You can't transfer money to the same account!");

            return false;
        }

        return true;
    }

    private String requestRecipient(Scanner scanner) {
        System.out.println("Transfer");
        System.out.println("Enter card number:");

        return scanner.nextLine();
    }

    private int requestIncome(Scanner scanner) {
        System.out.println("Enter income:");

        return Integer.parseInt(scanner.nextLine());
    }

    private void requestTransfer(Scanner scanner, Card card, Card recipient) {
        System.out.println("Enter how much money you want to transfer:");
        int val = Integer.parseInt(scanner.nextLine());

        transfer(card, recipient, val);
    }

    private void transfer(Card card, Card recipient, int val){
        Card cardUpd = synchronizeCard(card);
        Card recipientUpd = synchronizeCard(recipient);

        if (cardDAO.doTransfer(cardUpd,recipientUpd, val)) {
            System.out.println("Success!");
        } else {
            System.out.println("Not enough money!");
        }
    }

    private Card synchronizeCard(Card card) {
        return cardDAO.getCard(card.getCodeNumber()).get();
    }

    private Optional<Card> getCardNumber(String recipient) {

        return cardDAO.getCard(recipient);
    }

    private void addIncome(Card card, int income) {
        cardDAO.addIncome(card, income);

        System.out.println("Income was added!");
    }

    private void getBalance(Card card) {
        int balance = synchronizeCard(card).getBalance();


        System.out.println("Balance: " + balance);
    }

    private void logOut() {

        System.out.println("You have successfully logged out!");
    }
}
