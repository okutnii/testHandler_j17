package extra.banking;

import java.sql.*;
import java.util.Optional;

public class CardDAO {
    private final String conName;
    private final String username;
    private final String pass;

    public CardDAO(String conName, String username, String pass){
        this.conName = conName;
        this.username = username;
        this.pass = pass;
    }

    public Optional<Card> getCard(String number) {
        Optional<Card> cardOptional = Optional.empty();

        try (Connection connection = DriverManager.getConnection(conName);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("select * from card where number="+number);

            if(resultSet.next()) {
                int id = (resultSet.getInt("id"));
                int balance = resultSet.getInt("balance");
                String pin = (resultSet.getString("pin"));

                cardOptional = Optional.of(new Card(number, pin, balance, id));
            }
        }catch (Exception e) {
            //  e.printStackTrace();
        }

        return cardOptional;
    }

    public void insertCard(Card card) {
        String sql = "INSERT INTO card(id, number,pin,balance) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(conName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, card.getId());
            pstmt.setString(2, card.getCodeNumber());
            pstmt.setString(3, card.getPin());
            pstmt.setInt(4, card.getBalance());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            //  System.out.println(e.getMessage());
        }
    }

    public boolean doTransfer(Card card, Card recipient, int val){
        if(!canTransfer(card, val))
            return false;

        addIncome(card, -val);
        addIncome(recipient, val);

        return true;
    }

    private boolean canTransfer(Card card, int val) {
        Card updCard = getCard(card.getCodeNumber()).get();

        return updCard.canTransfer(val);
    }

    public void addIncome(Card card, int val) {
        String sql = "UPDATE card SET balance = ? "
                + "WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(conName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int newBalance = card.getBalance() + val;

            pstmt.setInt(1, newBalance);
            pstmt.setString(2, card.getCodeNumber());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteCard(Card card) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(conName);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, card.getCodeNumber());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
