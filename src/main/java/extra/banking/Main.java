package extra.banking;

import java.sql.*;
import java.util.*;

public class Main {

    private static final Scanner scanner;

    static {
        scanner = new Scanner(System.in);
    }
    public static void createNewDatabase(String fileName) {
        String url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable(String fileName) {
        String url = "jdbc:sqlite:" + fileName;

        String sql = """
                CREATE TABLE IF NOT EXISTS card (
                	id INTEGER PRIMARY KEY,
                	number TEXT,
                	pin TEXT,
                	balance INTEGER DEFAULT 0
                );""";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {

        String dbName = getDataBaseName(args); //  getDataBaseName(args);  // db.s3db

        createNewDatabase(dbName);
        createNewTable(dbName);

        start("jdbc:sqlite:" + dbName);

        scanner.close();
    }

    private static String getDataBaseName(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].equals("--fileName"))
                return args[i+1];
        }

        return null;
    }

    private static void start(String dbPath) {

        BankSystem bankSystem = new BankSystem(dbPath);

        bankSystem.bankMenu(scanner);
    }
}


