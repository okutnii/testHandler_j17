package extra.phoneBook;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static extra.phoneBook.PhoneBookSearcher.*;
import static extra.phoneBook.PhoneBookSearcher.performHash;
import static org.junit.jupiter.api.Assertions.*;

class PhoneBookSearcherTest {

    private static List<String> entryList;
    static String sourcePath = "src/main/resources/pb/small_directory.txt";

    @BeforeAll
    public static void init() throws FileNotFoundException {
        String entryPath = "src/main/resources/pb/small_find.txt";

        entryList = readData(entryPath);

//        var strings = performLinear(sourcePath, entryList);
//        var strings3 = performBubbleAndJump(sourcePath, entryList);
//        var strings1 = performQuickAndBinary(sourcePath, entryList);
//        var strings1 = performHash(sourcePath, entryList);
    }

    @Test
    void testPerformHash() throws FileNotFoundException {
        //given
        List<String> strings;

        //when
        strings = performHash(sourcePath, entryList);

        //then
        assertEquals(strings.size(), entryList.size());
    }

    @org.junit.jupiter.api.Test
    void testPerformQuickAndBinary() throws FileNotFoundException {
        //given
        List<String> strings;

        //when
        strings = performQuickAndBinary(sourcePath, entryList);

        //then
        assertEquals(strings.size(), entryList.size());
    }

    @org.junit.jupiter.api.Test
    void testPerformBubbleAndJump() throws FileNotFoundException {
        //given
        List<String> strings;

        //when
        strings = performBubbleAndJump(sourcePath, entryList);

        //then
        assertEquals(strings.size(), entryList.size());
    }

    @org.junit.jupiter.api.Test
    void testPerformLinear() throws FileNotFoundException {
        //given
        List<String> strings;

        //when
        strings = performLinear(sourcePath, entryList);

        //then
        assertEquals(strings.size(), entryList.size());
    }

    @Test
    void testQuickSort() throws FileNotFoundException {
        //given
        List<String> source = readData(sourcePath);
        String[] arr = new String[source.size()];
        String[] sorted = source.toArray(arr);

        //when
        quickSort(sorted, 0, source.size() - 1);

        //then
        System.out.println(Arrays.toString(sorted));
        assertTrue(isSorted(sorted));
    }

    @Test
    void testBubbleSort() throws FileNotFoundException {
        //given
        List<String> source = readData(sourcePath);

        //when
        String[] sorted = bubbleSort(source, System.currentTimeMillis());

        //then
        assertTrue(isSorted(sorted));
    }

    private boolean isSorted(List<? extends Comparable> array) {
        for (int i = 0; i < array.size() - 1; ++i) {
            if (array.get(i).compareTo(array.get(i + 1)) > 0)
                return false;
        }
        return true;
    }

    private boolean isSorted(String[] arr) {
        return isSorted(Arrays.stream(arr).toList());
    }
}