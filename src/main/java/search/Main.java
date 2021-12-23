package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//  Map<K, V[]> can be replaced with multimap
public class Main {

    private enum Strategy{ALL, ANY, NONE}

    private static final Scanner sc = new Scanner(System.in);

    private static final String regexSplitterWhitespace = " ";

    public static void main(String[] args)  throws FileNotFoundException {

        String path = getPath(args);

        List<String> data = readData(path);   //  readData(path); //  requestData();

        Map<String, List<Integer>> dataMap = getMappedData(data);

        startEngine(data, dataMap);

        sc.close();
    }

    private static Map<String, List<Integer>> getMappedData(List<String> data) {

        Map<String, List<Integer>> dataMap = new HashMap<>();

        for(int i = 0; i < data.size(); i++){

            String[] line = data.get(i).split(" ");

            for(String key: line) {
                List<Integer> values = dataMap.get(key.toLowerCase(Locale.ROOT));

                if(values == null) {
                    values = new ArrayList<>();
                }

                values.add(i);

                dataMap.put(key.toLowerCase(Locale.ROOT),values);
            }

        }

        return dataMap;
    }

    private static String getPath(String[] args) {

        for(int i = 0; i < args.length-1; i++) {

            if (args[i].equals("--data"))
                return args[i + 1];
        }

        return null;
    }

    private static List<String> readData(String path) throws FileNotFoundException {

        List<String> data = new ArrayList<>();

        File source = new File(path);

        try(Scanner reader = new Scanner(source)){

            while (reader.hasNextLine()) {

                String line = reader.nextLine();

                data.add(line);
            }
        }

        return data;
    }

    private static void startEngine(List<String> data, Map<String, List<Integer>> dataMap) {


        while(true){

            System.out.println("""
                    === Menu ===
                    1. Find a person
                    2. Print all people
                    0. Exit""");

            int command = sc.nextInt();

            //  carriage shift
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

    private static void printData(List<String> data) {

        System.out.println("=== List of people ===");

        data.forEach(System.out::println);
    }

    private static void requestQuery(List<String> data, Map<String, List<Integer>> dataMap) {

        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strStrategy = sc.nextLine();

        Strategy strategy = Strategy.valueOf(strStrategy);

        System.out.println("Enter a name or email to search all suitable people.");
        String target = sc.nextLine();

        List<String> results = computeQuery(data, dataMap, target, strategy);

        if(results.size() > 0) {
            results.forEach(System.out::println);
        } else {
            System.out.println("No matching people found.");
        }
    }

    private static List<String> computeQuery(List<String> data, Map<String, List<Integer>> dataMap, String target, Strategy strategy) {

        List<String> results = switch (strategy) {
            case ALL    -> findAll  (data, dataMap, target);
            case ANY    -> findAny  (data, dataMap, target);
            case NONE   -> findNone (data, dataMap, target);
        };


        List<Integer> indexes = dataMap.get(target);

        if(indexes != null) {

            for (int i : indexes)
                results.add(data.get(i));
        }

        return results;
    }

    private static List<String> findNone(List<String> data, Map<String, List<Integer>> dataMap, String targets) {

        List<String> results = new ArrayList<>();

        Set<Integer> setInd = new TreeSet<>();

        for(String target: targets.split( regexSplitterWhitespace )) {

            List<Integer> list = dataMap.get(target.toLowerCase(Locale.ROOT));

            if(list != null) {
                setInd.addAll(list);
            }
        }

        for(int i = 0; i < data.size(); i++){
            if(!setInd.contains(i)) {
                results.add(data.get(i));
            }
        }

        return results;
    }

    private static List<String> findAll(List<String> data, Map<String, List<Integer>> dataMap, String targets) {

        List<String> result = data;

        for(String target: targets.split( regexSplitterWhitespace )) {

            result = findAny(result, dataMap, target.toLowerCase(Locale.ROOT));

            dataMap = getMappedData(result);

        }

        return result;
    }

    private static List<String> findAny(List<String> data, Map<String, List<Integer>> dataMap, String targets) {

        Set<String> resultSet = new TreeSet<>();

        for(String target: targets.split( regexSplitterWhitespace )) {

            List<Integer> indexes = dataMap.get(target.toLowerCase(Locale.ROOT));

            if (indexes != null) {

                for (int i : indexes) {
                    resultSet.add(data.get(i));
                }
            }
        }

        return new ArrayList<>(resultSet);
    }

    @Deprecated
    private static List<String> findAny(List<String> data, String target) { // query

        List<String> results = new ArrayList<>();

        for(String element: data) {

            String s1 = element.toLowerCase(Locale.ROOT);
            String s2 = target.toLowerCase(Locale.ROOT);

            if (s1.contains(s2)){
                results.add(element);
            }
        }

        return results;
    }

    private static List<String> requestData(){

        List<String> data = new ArrayList<>();

        System.out.println("Enter the number of people:");
        int n = sc.nextInt();

        //  carriage shift
        sc.nextLine();

        System.out.println("Enter all people:");
        for(int i = 0;i < n; i++){

            data.add(sc.nextLine());
        }

        return data;
    }
}
