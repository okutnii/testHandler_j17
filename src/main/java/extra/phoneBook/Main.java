package extra.phoneBook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        execute();
    }

    private static void execute() throws FileNotFoundException {
        String entryPath = "src/main/resources/pb/find.txt";
        String sourcePath = "src/main/resources/pb/directory.txt";

        List<String> entryList = readData(entryPath);

        performLinear(sourcePath, entryList);
        performBubbleAndJump(sourcePath, entryList);
        performQuickAndBinary(sourcePath, entryList);
        performHash(sourcePath, entryList);
    }

    private static void performHash(String sourcePath, List<String> entryList) throws FileNotFoundException {
        System.out.println("Start searching (hash table)...");

        long startTime = System.currentTimeMillis();
        Map<String, Integer> dataMap = collectDataMap(sourcePath);

        long creatingTime = System.currentTimeMillis() - startTime;

        List<String> result;
        long startSearching = System.currentTimeMillis();

        result = hashSearchEntries(entryList, dataMap);

        long searchTime = System.currentTimeMillis() - startSearching;
        long timeTaken = System.currentTimeMillis() - startTime;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);

        printMetaCreating(creatingTime);

        printMetaSearch(searchTime);
    }

    private static List<String> refactorList(List<String> source) {
        List<String> res = new ArrayList<>();
        Pattern patternName = Pattern.compile("[1234567890]");
        Pattern patternNum = Pattern.compile("[A-Za-z-]");

        for (String str: source) {
            String name     = str.replaceAll(patternName.pattern(), "").trim();
            String number   = str.replaceAll(patternNum.pattern(), "").trim();

            res.add(name + " " + number);
        }

        return res;
    }

    private static void performQuickAndBinary(String sourcePath, List<String> entryList) throws FileNotFoundException {
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
    }

    private static void performBubbleAndJump(String sourcePath, List<String> entryList) throws FileNotFoundException {
        System.out.println("Start searching (bubble sort + jump search)...");
        boolean broken = false;

        long startTime = System.currentTimeMillis();
        List<String> source = readData(sourcePath);

        String[] sorted = bubbleSort(source, startTime);
        long sortTime = System.currentTimeMillis() - startTime;


        List<String> result;
        long startSearching = System.currentTimeMillis();
        if(sorted != null) {
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
    }

    private static String binarySearch(String[] srcArr, int l, int r, String str) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            String t = srcArr[mid].replaceAll("[1234567890]", "").trim();

            // If the element is present at the
            // middle itself
            if (t.compareTo(str) == 0)
                return srcArr[mid];

            // If element is smaller than mid, then
            // it can only be present in left subarray
            if (str.compareTo(t) < 0)
                return binarySearch(srcArr, l, mid - 1, str);

            // Else the element can only be present
            // in right subarray
            return binarySearch(srcArr, mid + 1, r, str);
        }

        // We reach here when element is not present
        // in array
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

    private static List<String> jumpingSearchEntries(List<String> entryList, String[] source) {
        List<String> res = new ArrayList<>();

        for (String str: entryList) {
            String t = jumpSearch(source, str);

            if(t != null){
                res.add(t);
            }
        }

        return res;
    }

    public static String jumpSearch(String[] arr, String x) {
        Pattern patternName = Pattern.compile("[1234567890]");


        int blockSize = (int) Math.floor(Math.sqrt(arr.length));

        int currentLastIndex = blockSize-1;

        // Jump to next block as long as target element is > currentLastIndex
        // and the array end has not been reached
        while (currentLastIndex < arr.length ) {
            String t = arr[currentLastIndex].replaceAll(patternName.pattern(), "").trim();
            if(x.compareTo(t) <= 0)
                break;
            currentLastIndex += blockSize;
        }

        // Find accurate position of target element using Linear Search
        for (int currentSearchIndex = currentLastIndex - blockSize + 1;
             currentSearchIndex <= currentLastIndex && currentSearchIndex < arr.length; currentSearchIndex++) {

            String name = arr[currentSearchIndex].replaceAll(patternName.pattern(), "").trim();
            if (x.equals(name)) {
                return arr[currentSearchIndex];
            }
        }
        // Target element not found. Return negative integer as element position.
        return null;
    }

    private static List<String> binarySearchEntries(List<String> entryList, String[] srcArr) {
        List<String> res = new ArrayList<>();

        for (String str: entryList) {
            String t = binarySearch(srcArr, 0, srcArr.length - 1, str);

            if(t != null){
                res.add(t);
            }
        }

        return res;
    }

    private static List<String> simpleSearchEntries(List<String> entryList, String sourcePath) throws FileNotFoundException {
        File source = new File(sourcePath);


        return findEntriesToMap(source, entryList);
    }

    private static List<String> hashSearchEntries(List<String> entryList, Map<String, Integer> dataMap) {
        List<String> res = new ArrayList<>();

        for (String target: entryList) {
            res.add(dataMap.get(target) + " " + target);
        }

        return res;
    }

    private static List<String> findEntriesToMap(File source, List<String> entryList) throws FileNotFoundException {
        List<String> data = new ArrayList<>();

        try(Scanner reader = new Scanner(source)){

            while (reader.hasNextLine()) {
                int number = reader.nextInt();
                String line = reader.nextLine().trim();

                for (String str: entryList) {
                    if(line.equals(str)) {
                        data.add(line + " : " + number);
                    }
                }
            }
        }

        return data;
    }

    private static Map<String, Integer> collectDataMap(String sourcePath) throws FileNotFoundException {
        Map<String, Integer> data = new HashMap<>();

        File source = new File(sourcePath);

        try(Scanner reader = new Scanner(source)){

            while (reader.hasNextLine()) {
                Integer number = reader.nextInt();
                String line = reader.nextLine();

                data.put(line, number);
            }
        }

        return data;
    }

    static void swap(String[] arr, int i, int j) {
        String temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    static int partition(String[] arr, int low, int high) {
        String pivot = arr[high];

        // Index of smaller element and
        // indicates the right position
        // of pivot found so far
        int i = (low - 1);

        for(int j = low; j <= high - 1; j++) {

            // If current element is smaller
            // than the pivot
            if (arr[j].compareTo(pivot) < 0) {

                // Increment index of
                // smaller element
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return (i + 1);
    }

    private static void quickSort(String[] arr, int low, int high) {
        if (low < high)
        {

            // pi is partitioning index, arr[p]
            // is now at right place
            int pi = partition(arr, low, high);

            // Separately sort elements before
            // partition and after partition
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private static String[] bubbleSort(List<String> source, long timestamp) {
        source = refactorList(source);

        String[] arr = new String[source.size()];
        source.toArray(arr);

        int n = source.size();

        for (int i = 0; i < n-1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j].compareTo(arr[j + 1]) > 0) {
                    String temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }

                if(System.currentTimeMillis() - timestamp > 600_000)
                    return null;
            }
        }

        return arr;
    }

    private static void printMetaSearch(long searchTime) {
        long min = searchTime / 60000;
        searchTime %= 60000;
        long sec = searchTime / 1000;
        searchTime %= 1000;
        long ms = searchTime;

        System.out.println("Searching time: "
                + min + " min. " + sec + " sec. " + ms + " ms.");
    }

    private static void printMetaSort(long sortTime, boolean broken) {
        long min = sortTime / 60000;
        sortTime %= 60000;
        long sec = sortTime / 1000;
        sortTime %= 1000;
        long ms = sortTime;

        System.out.print("Sorting time: "
                + min + " min. " + sec + " sec. " + ms + " ms.");

        if(broken) {
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

    private static void performLinear(String sourcePath, List<String> entryList) throws FileNotFoundException {

        System.out.println("Start searching (linear search)...");
        long startTime = System.currentTimeMillis();
        List<String> result = simpleSearchEntries(entryList, sourcePath);

        long timeTaken = System.currentTimeMillis() - startTime;

        printMetaFoundRes(result.size(), entryList.size(), timeTaken);
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
