package extra.banking;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankSystemTest {

    private BankSystem bankSystem = new BankSystem("MOCK");

    @Test
    public void generateCardTest() {
        //given
        Card card;

        //when
        card = bankSystem.generateCard();
        int result = card.getCodeNumber().length();

        System.out.println(card.getCodeNumber());
        //then
        assertEquals(16, result);
    }

    @Test
    public void luhnAlgorithmTest() {
        //given
        String number = "400000518762130";  // checksum should be 6

        //when
        byte result = bankSystem.luhnAlgorithm(number);

        //then
        assertEquals(6, result);
    }

    @Test
    public void isValidNumberTest_true() {
        //given
        String number = "4000005187621306";

        //when
        boolean result = bankSystem.isValidNumber(number);

        //then
        assertTrue(result);
    }

    @Test
    public void isValidNumberTest_false() {
        //given
        String number = "4000005187621303";

        //when
        boolean result = bankSystem.isValidNumber(number);

        //then
        assertFalse(result);
    }

    @Test
    public void isValidRecipient_true() {
        //given
        Card card1 = new Card("4000005187621303", "1234", 0, 1);
        Card card2 = new Card("4000005187621306", "1234", 0, 1);

        //when
        boolean result = bankSystem.isValidRecipient(card1,
                Optional.of(card2));

        //then
        assertTrue(result);
    }

    @Test
    public void isValidRecipient_false1() {
        //given
        Card card1 = new Card("4000005187621303", "1234", 0, 1);
        Card card2 = new Card("4000005187621306", "1234", 0, 1);

        //when
        boolean result = bankSystem.isValidRecipient(card1,
                Optional.empty());

        //then
        assertFalse(result);
    }

    @Test
    public void isValidRecipient_false2() {
        //given
        Card card1 = new Card("4000005187621303", "1234", 0, 1);

        //when
        boolean result = bankSystem.isValidRecipient(card1,
                Optional.of(card1));

        //then
        assertFalse(result);
    }
}