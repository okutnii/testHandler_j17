package search;

import org.apache.commons.collections4.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static search.SearchEngine.computeQuery;
import static search.SearchEngine.getMappedData;

public class SearchMain {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws FileNotFoundException {
        execute();
    }


    /**
     * Executes search engine
     *
     * @throws FileNotFoundException
     */
    private static void execute() throws FileNotFoundException {
        String path = "src/main/resources/names.txt";
        List<String> data = readData(path);
        startEngine(data);
        sc.close();
    }

    /**
     * Returns ArrayList of data by path
     *
     * @param path path of data file
     * @return List of data given in file by path
     */
    private static List<String> readData(String path) throws FileNotFoundException {
        List<String> data = new ArrayList<>();
        File source = new File(path);
        try (Scanner reader = new Scanner(source)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                data.add(line);
            }
        }
        return data;
    }

    /**
     * Method to start search engine
     *
     * @param data data source
     */
    private static void startEngine(List<String> data) {
        Map<String, List<Integer>> dataMap = getMappedData(data);

        while (true) {
            System.out.println("""
                    === Menu ===
                    1. Find a person
                    2. Print all people
                    0. Exit
                    """);
            int command = sc.nextInt();
            sc.nextLine();
            switch (command) {
                case 1 -> requestQuery(data, dataMap);
                case 2 -> printData(data);
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Incorrect option! Try again.");
            }
        }
    }

    /**
     * Prints data by lines
     *
     * @param data data list to print
     */
    private static void printData(List<String> data) {
        System.out.println("=== List of people ===");
        data.forEach(System.out::println);
    }

    /**
     * Requests query for user. User must define strategy(ALL, ANY or NONE) and perform a query
     *
     * @param data    list of data
     * @param dataMap data collected in a map
     */
    private static void requestQuery(List<String> data, Map<String, List<Integer>> dataMap) {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strStrategy = sc.nextLine();
        Strategy strategy = Strategy.valueOf(strStrategy);
        System.out.println("Enter a name or email to search all suitable people.");
        String target = sc.nextLine();
        List<String> results = computeQuery(data, dataMap, target, strategy);
        if (results.size() > 0) {
            results.forEach(System.out::println);
        } else {
            System.out.println("No matching people found.");
        }
    }
}
