import Response.Card;
import Response.Token;
import Service.RequestGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITests {

    RequestGenerator requestGenerator = new RequestGenerator();
    Token token;
    List<Card> cards;

    @ParameterizedTest
    @DisplayName("Перевод с карты на карту половины остатка")
    @CsvFileSource(resources = "/validTransferParams.csv", numLinesToSkip = 1)
    void transferFundsBetweenCards(String login,
                                   String password,
                                   String from,
                                   String to,
                                   long amount,
                                   long expectedFirstCardBalance,
                                   long expectedSecondCardBalance
    ) throws SQLException
    {
        requestGenerator.auth(login, password);
        token = requestGenerator.verification(login);
        cards = requestGenerator.getCardsInfo(token.getToken());
        requestGenerator.card2CardTransfer(from, to, amount, token.getToken());
        cards = requestGenerator.getCardsInfo(token.getToken());
        long factFirstCardBalance = cards.get(1).getBalance();
        long factSecondCardBalance = cards.get(0).getBalance();
        assertEquals (expectedFirstCardBalance, factFirstCardBalance);
        assertEquals (expectedSecondCardBalance, factSecondCardBalance);
    }

    @ParameterizedTest
    @DisplayName("Перевод с карты на карту больше остатка")
    @CsvFileSource(resources = "/invalidTransferParams.csv", numLinesToSkip = 1)
    void invalidTransferFundsBetweenCards(String login,
                                   String password,
                                   String from,
                                   String to,
                                   long amount,
                                   long expectedFirstCardBalance,
                                   long expectedSecondCardBalance
    ) throws SQLException
    {
        requestGenerator.auth(login, password);
        token = requestGenerator.verification(login);
        cards = requestGenerator.getCardsInfo(token.getToken());
        requestGenerator.card2CardTransfer(from, to, amount, token.getToken());
        cards = requestGenerator.getCardsInfo(token.getToken());
        long factFirstCardBalance = cards.get(1).getBalance();
        long factSecondCardBalance = cards.get(0).getBalance();
        assertEquals (expectedFirstCardBalance, factFirstCardBalance);
        assertEquals (expectedSecondCardBalance, factSecondCardBalance);
    }
}
