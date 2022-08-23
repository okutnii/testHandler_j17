package extra.banking;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CardDAOTest {

    @Test
    void doTransferTest_true() {
        //given
        String conName = "jdbc:sqlite:db.s3db";
        CardDAO dao = new CardDAO(conName, null, null);

        Card card = new Card("4300005187721306", "1234", 10, 201);
        Card card2 = new Card("4400005187621305", "1234", 10, 200);
        insertCard(conName, card);
        insertCard(conName, card2);

        //when
        boolean result = dao.doTransfer(card,
                card2, 1);

        //then
        assertTrue(result);
    }

    @Test
    void doTransferTest_false() {
        //given
        String conName = "jdbc:sqlite:db.s3db";
        CardDAO dao = new CardDAO(conName, null, null);

        Card card = new Card("4100005187621303", "1234", 0, 413);
        Card card2 = new Card("4200005187621306", "1234", 0, 412);
        insertCard(conName, card);
        insertCard(conName, card2);

        //then
        assertFalse(dao.doTransfer(card, card2, 1111));
    }

    private void insertCard(String conName, Card card) {
        String sql = "INSERT INTO card(id, number,pin,balance) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(conName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, card.getId());
            pstmt.setString(2, card.getCodeNumber());
            pstmt.setString(3, card.getPin());
            pstmt.setInt(4, card.getBalance());

            pstmt.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}