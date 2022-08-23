package extra.phoneBook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

public class PhoneBookSearcher {

    /**
     * Finds occurrences by collecting data into a map and performing a hash search
     *
     * @param sourcePath path to the source file
     * @param entryList  list of targeting strings
     * @return list of founded occurrences in source by entryList
     * @throws FileNotFoundException if file by path are not present
     */
    public static List<String> performHash(String sourcePath, List<String> entryList) throws FileNotFoundException {
        System.out.println("Start searching (hash table)...");

        long startTime = System.currentTimeMillis();
        Map<String, Integer> dataMap = collectDataMap(sourcePath);

        long creationTime = System.currentTimeMillis() - startTime;
        List<String> result;
        long startSearching = System.currentTimeMillis();
        result = hashSearchEntries(entryList, dataMap);

        long searchTime = System.currentTimeMillis() - startSearching;
        long timeTaken = System.currentTimeMillis() - startTime;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);

        printMetaCreating(creationTime);

        printMetaSearch(searchTime);

        return result;
    }

    /**
     * Refactors list by swapping number and names {380455555 Name} -> {Name 380455555}
     *
     * @param source source list
     * @return refactored list
     */
    private static List<String> refactorList(List<String> source) {
        List<String> res = new ArrayList<>();
        Pattern patternName = Pattern.compile("[1234567890]");
        Pattern patternNum = Pattern.compile("[A-Za-z-]");

        for (String str : source) {
            String name = str.replaceAll(patternName.pattern(), "").trim();
            String number = str.replaceAll(patternNum.pattern(), "").trim();

            res.add(name + " " + number);
        }

        return res;
    }

    /**
     * Finds occurrences by performing a quick sort and a binary search
     *
     * @param sourcePath path to the source file
     * @param entryList  list of targeting strings
     * @return list of founded occurrences in source by entryList
     * @throws FileNotFoundException if file by path are not present
     */
    public static List<String> performQuickAndBinary(String sourcePath, List<String> entryList) throws FileNotFoundException {
        System.out.println("Start searching (quick sort + binary search)...");
        List<String> source = readData(sourcePath);

        long startTime = System.currentTimeMillis();

        source = refactorList(source);

        String[] arr = new String[source.size()];
        String[] sorted = source.toArray(arr);

        quickSort(sorted, 0, source.size() - 1);
        long sortTime = System.currentTimeMillis() - startTime;

        List<String> result;
        long startSearching = System.currentTimeMillis();

        result = binarySearchEntries(entryList, sorted);

        long searchTime = System.currentTimeMillis() - startSearching;
        long timeTaken = System.currentTimeMillis() - startTime;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);

        printMetaSort(sortTime, false);

        printMetaSearch(searchTime);

        return result;
    }

    /**
     * Finds occurrences by performing a bubble sort and a jump search
     *
     * @param sourcePath path to the source file
     * @param entryList  list of targeting strings
     * @return list of founded occurrences in source by entryList
     * @throws FileNotFoundException if file by path are not present
     */
    public static List<String> performBubbleAndJump(String sourcePath, List<String> entryList) throws FileNotFoundException {
        System.out.println("Start searching (bubble sort + jump search)...");
        boolean broken = false;

        long startTime = System.currentTimeMillis();
        List<String> source = readData(sourcePath);

        String[] sorted = bubbleSort(source, startTime);
        long sortTime = System.currentTimeMillis() - startTime;


        List<String> result;
        long startSearching = System.currentTimeMillis();
        if (sorted != null) {
            result = jumpingSearchEntries(entryList, sorted);
        } else {
            broken = true;
            result = simpleSearchEntries(entryList, sourcePath);
        }
        long timeTaken = System.currentTimeMillis() - startTime;
        long searchTime = System.currentTimeMillis() - startSearching;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);

        printMetaSort(sortTime, broken);

        printMetaSearch(searchTime);

        return result;
    }

    /**
     * Method to perform a binary search
     *
     * @param srcArr source array
     * @param l      left border of considering array
     * @param r      right border of considering array
     * @param str    target of search
     * @return
     */
    private static String binarySearch(String[] srcArr, int l, int r, String str) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            String t = srcArr[mid].replaceAll("[1234567890]", "").trim();

            if (t.compareTo(str) == 0) {
                return srcArr[mid];
            }
            if (str.compareTo(t) < 0) {
                return binarySearch(srcArr, l, mid - 1, str);
            }
            return binarySearch(srcArr, mid + 1, r, str);
        }
        return null;
    }


    /**
     * Reads data from a file and collects data in a list by lines
     *
     * @param path source path
     * @return result data
     * @throws FileNotFoundException
     */
    public static List<String> readData(String path) throws FileNotFoundException {
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
     * Method to perform a jumping search for every entry
     *
     * @param entryList entries to find
     * @param source    source array
     * @return result of performing the search
     */
    private static List<String> jumpingSearchEntries(List<String> entryList, String[] source) {
        List<String> res = new ArrayList<>();
        for (String str : entryList) {
            String t = jumpSearch(source, str);
            if (t != null) {
                res.add(t);
            }
        }
        return res;
    }


    /**
     * Method to perform a jumping search
     *
     * @param arr source array
     * @param x   target string
     * @return result of search: string, if exists; null if no occurrence
     */
    public static String jumpSearch(String[] arr, String x) {
        Pattern patternName = Pattern.compile("[1234567890]");
        int blockSize = (int) Math.floor(Math.sqrt(arr.length));
        int currentLastIndex = blockSize - 1;

        while (currentLastIndex < arr.length) {
            String t = arr[currentLastIndex].replaceAll(patternName.pattern(), "").trim();
            if (x.compareTo(t) <= 0)
                break;
            currentLastIndex += blockSize;
        }

        for (int currentSearchIndex = currentLastIndex - blockSize + 1;
             currentSearchIndex <= currentLastIndex && currentSearchIndex < arr.length; currentSearchIndex++) {
            String name = arr[currentSearchIndex].replaceAll(patternName.pattern(), "").trim();
            if (x.equals(name)) {
                return arr[currentSearchIndex];
            }
        }
        return null;
    }

    /**
     * Method to perform a binary search for every entry
     *
     * @param entryList entries to find
     * @param srcArr    source array
     * @return result of performing the search
     */
    private static List<String> binarySearchEntries(List<String> entryList, String[] srcArr) {
        List<String> res = new ArrayList<>();
        for (String str : entryList) {
            String t = binarySearch(srcArr, 0, srcArr.length - 1, str);
            if (t != null) {
                res.add(t);
            }
        }
        return res;
    }

    private static List<String> simpleSearchEntries(List<String> entryList, String sourcePath) throws FileNotFoundException {
        File source = new File(sourcePath);
        return findEntriesToMap(source, entryList);
    }

    /**
     * Method to perform a hash search for every entry
     *
     * @param entryList entries to find
     * @param dataMap   source map
     * @return result of performing the search
     */
    private static List<String> hashSearchEntries(List<String> entryList, Map<String, Integer> dataMap) {
        List<String> res = new ArrayList<>();
        for (String target : entryList) {
            res.add(dataMap.get(target) + " " + target);
        }
        return res;
    }

    /**
     * Reads file and writes it in a list in {name : number} format
     *
     * @param entryList entries to find
     * @param source    source file
     * @return list of refactored list
     */
    private static List<String> findEntriesToMap(File source, List<String> entryList) throws FileNotFoundException {
        List<String> data = new ArrayList<>();
        try (Scanner reader = new Scanner(source)) {
            while (reader.hasNextLine()) {
                int number = reader.nextInt();
                String line = reader.nextLine().trim();
                for (String str : entryList) {
                    if (line.equals(str)) {
                        data.add(line + " : " + number);
                    }
                }
            }
        }
        return data;
    }

    /**
     * Reads a file and assemble it in a map
     *
     * @param sourcePath source path
     * @return a map with string-number as a key-value pair
     * @throws FileNotFoundException
     */
    private static Map<String, Integer> collectDataMap(String sourcePath) throws FileNotFoundException {
        Map<String, Integer> data = new HashMap<>();
        File source = new File(sourcePath);
        try (Scanner reader = new Scanner(source)) {
            while (reader.hasNextLine()) {
                Integer number = reader.nextInt();
                String line = reader.nextLine();
                data.put(line, number);
            }
        }
        return data;
    }

    /**
     * Swaps values in array arr with specific indexes
     *
     * @param arr array
     * @param i   first index
     * @param j   second index
     */
    static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * shifts a partition in array
     *
     * @param arr  source array
     * @param low  left index
     * @param high right index
     * @return index of a partition
     */
    private static int partition(String[] arr, int low, int high) {
        String pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j <= high - 1; j++) {
            if (arr[j].compareTo(pivot) < 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    /**
     * Method performs quick sorting
     *
     * @param arr  target array
     * @param low  left index
     * @param high right index
     */
    public static void quickSort(String[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    /**
     * Method performs a bubble sort
     *
     * @param source    source list
     * @param timestamp upper border to perform a bubble sorting in milliseconds
     * @return sorted array if time is not over, else returns null
     */
    public static String[] bubbleSort(List<String> source, long timestamp) {
        source = refactorList(source);

        String[] arr = new String[source.size()];
        source.toArray(arr);

        int n = source.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    String temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }

                if (System.currentTimeMillis() - timestamp > 600_000)
                    return null;
            }
        }

        return arr;
    }

    /**
     * Prints formatted time of search time
     *
     * @param searchTime time in milliseconds
     */
    private static void printMetaSearch(long searchTime) {
        long min = searchTime / 60000;
        searchTime %= 60000;
        long sec = searchTime / 1000;
        searchTime %= 1000;
        long ms = searchTime;

        System.out.println("Searching time: "
                + min + " min. " + sec + " sec. " + ms + " ms.");
    }

    /**
     * Prints formatted time of sort time
     *
     * @param sortTime time in milliseconds
     * @param broken   flag to label sorting took too much time
     */
    private static void printMetaSort(long sortTime, boolean broken) {
        long min = sortTime / 60000;
        sortTime %= 60000;
        long sec = sortTime / 1000;
        sortTime %= 1000;
        long ms = sortTime;

        System.out.print("Sorting time: "
                + min + " min. " + sec + " sec. " + ms + " ms.");

        if (broken) {
            System.out.println(" - STOPPED, moved to linear search");
        } else {
            System.out.println();
        }
    }

    private static void printMetaFoundRes(int resSize, int listSize, long timeTaken) {

        long min = timeTaken / 60000;
        timeTaken %= 60000;
        long sec = timeTaken / 1000;
        timeTaken %= 1000;
        long ms = timeTaken;

        System.out.println("Found " + resSize + " / " + listSize
                + " entries. Time taken: "
                + min + " min. " + sec + " sec. " + ms + " ms.");
    }


    /**
     * Performs linear search
     *
     * @param sourcePath source path
     * @param entryList  list of target entries
     * @return result of searching
     * @throws FileNotFoundException
     */
    public static List<String> performLinear(String sourcePath, List<String> entryList) throws FileNotFoundException {

        System.out.println("Start searching (linear search)...");
        long startTime = System.currentTimeMillis();
        List<String> result = simpleSearchEntries(entryList, sourcePath);

        long timeTaken = System.currentTimeMillis() - startTime;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);

        return result;
    }

    private static void printMetaCreating(long creatingTime) {
        long min = creatingTime / 60000;
        creatingTime %= 60000;
        long sec = creatingTime / 1000;
        creatingTime %= 1000;
        long ms = creatingTime;

        System.out.println("Creating time: "
                + min + " min. " + sec + " sec. " + ms + " ms.");
    }
}
