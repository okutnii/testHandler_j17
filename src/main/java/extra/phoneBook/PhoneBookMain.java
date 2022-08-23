package extra.phoneBook;

import java.io.FileNotFoundException;
import java.util.List;

import static extra.phoneBook.PhoneBookSearcher.*;

public class PhoneBookMain {
    public static void main(String[] args) throws FileNotFoundException {
        execute();
    }


    /**
     * Execution method to perform test calculations
     *
     * @throws FileNotFoundException if some files are not present
     */
    private static void execute() throws FileNotFoundException {
        String entryPath = "src/main/resources/pb/find.txt";
        String sourcePath = "src/main/resources/pb/directory.txt";

        List<String> entryList = readData(entryPath);

        var strings = performLinear(sourcePath, entryList);
        var strings3 = performBubbleAndJump(sourcePath, entryList);
        var strings1 = performQuickAndBinary(sourcePath, entryList);
        var strings2 = performHash(sourcePath, entryList);
    }

}
