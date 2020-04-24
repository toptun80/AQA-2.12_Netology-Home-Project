import Response.Card;
import Response.Token;
import Service.RequestGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;
import java.util.List;

public class APITests {

    RequestGenerator requestGenerator = new RequestGenerator();
    Token token;
    List<Card> cards;

    @ParameterizedTest
    @DisplayName("Перевод с карты на карту половины остатка")
    @CsvFileSource(resources = "/validTransferParams.csv", numLinesToSkip = 1)
    void transferFundsBetweenCards(String login, String password, String from, String to, long amount) throws SQLException {
        requestGenerator.auth(login, password);
        token = requestGenerator.verification(login);
        cards = requestGenerator.getCardsInfo(token.getToken());
        System.out.println(cards.get(0).getBalance());
        System.out.println(cards.get(1).getBalance());
        System.out.println("before");
        requestGenerator.card2CardTransfer(from, to, amount, token.getToken());
        System.out.println(cards.get(0).getBalance());
        System.out.println(cards.get(1).getBalance());
        System.out.println("after");
    }
}
