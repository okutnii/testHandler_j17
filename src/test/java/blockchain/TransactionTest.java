package blockchain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    @Test
    public void testCommitTransactionOfInvalid() throws Exception {
        Participant bruce = new Participant("Bruce", 0);
        Participant mary = new Participant("Mary", 0);
        Transaction transaction = new Transaction(bruce, mary, 10);

        assertFalse(transaction.commitTransaction());
    }

    @Test
    public void testCommitTransactionOfValid() throws Exception {
        Participant bruce = new Participant("Bruce", 10);
        Participant mary = new Participant("Mary", 0);
        Transaction transaction = new Transaction(bruce, mary, 10);

        assertTrue(transaction.commitTransaction());
    }
}
