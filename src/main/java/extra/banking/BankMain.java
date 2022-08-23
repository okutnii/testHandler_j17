package extra.banking;

import java.util.Scanner;

public class BankMain {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);) {
            String dbName = "db.s3db";
            DBHelper dbHelper = new DBHelper(dbName);
            dbHelper.createNewDatabase();
            dbHelper.createNewTable();
            start("jdbc:sqlite:" + dbName, scanner);
        }
    }

    private static void start(String dbPath, Scanner scanner) {
        BankSystem bankSystem = new BankSystem(dbPath);
        bankSystem.bankMenu(scanner);
    }
}


