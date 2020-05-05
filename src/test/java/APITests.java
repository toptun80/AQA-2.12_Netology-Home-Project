import Response.Card;
import Response.ErrorMessage;
import Response.Token;
import Service.RequestGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITests {

    public static RequestGenerator requestGenerator = new RequestGenerator();
    private Token token;
    private List<Card> cards;
    private ErrorMessage errorMessage;

    @AfterAll
    @DisplayName("Приведение БД к начальному состоянию")
    static void cleanDB() throws SQLException {
        requestGenerator.cleanDB();
    }

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
        requestGenerator.card2CardTransfer(from, to, amount, token.getToken());
        cards = requestGenerator.getCardsInfo(token.getToken());
        long factFirstCardBalance = cards.get(1).getBalance();
        long factSecondCardBalance = cards.get(0).getBalance();
        assertEquals (expectedFirstCardBalance, factFirstCardBalance);
        assertEquals (expectedSecondCardBalance, factSecondCardBalance);
    }

    @ParameterizedTest
    @DisplayName("Перевод с карты на карту с недопустимыми параметрами перевода")
    @CsvFileSource(resources = "/invalidTransferParams.csv", numLinesToSkip = 1)
    void invalidTransferFundsBetweenCards(String login,
                                   String password,
                                   String from,
                                   String to,
                                   long amount,
                                   String expectedErrorMessage,
                                   long expectedFirstCardBalance,
                                   long expectedSecondCardBalance
    ) throws SQLException
    {
        requestGenerator.auth(login, password);
        token = requestGenerator.verification(login);
        ErrorMessage errorMessage = requestGenerator.card2CardInvalidTransfer(from, to, amount, token.getToken());
        cards = requestGenerator.getCardsInfo(token.getToken());
        long factFirstCardBalance = cards.get(1).getBalance();
        long factSecondCardBalance = cards.get(0).getBalance();
        assertEquals(expectedFirstCardBalance, factFirstCardBalance);
        assertEquals(expectedSecondCardBalance, factSecondCardBalance);
        assertEquals(expectedErrorMessage, errorMessage.getCode());
    }

    @ParameterizedTest
    @DisplayName("Авторизация с неправильными данными")
    @CsvFileSource(resources = "/invalidAuthParams.csv", numLinesToSkip = 1)
    void invalidAuth(String login, String password, String expectedErrorMessage) {
        errorMessage = requestGenerator.invalidAuth(login, password);
        assertEquals(expectedErrorMessage, errorMessage.getCode());
    }

    @Test
    @DisplayName("Верификация с неправильным кодом")
    void invalidVerification() throws SQLException {
        String login = "vasya";
        String password = "qwerty123";
        String additionalText = "321";
        String expectedErrorMessage = "AUTH_INVALID";
        requestGenerator.auth(login, password);
        errorMessage = requestGenerator.invalidVerification(login, additionalText);
        assertEquals(expectedErrorMessage, errorMessage.getCode());
    }

    @Test
    @DisplayName("Запрос информации о картах без токена")
    void getCardsInfoWithoutToken() {
        String login = "vasya";
        String password = "qwerty123";
        requestGenerator.auth(login, password);
        requestGenerator.getCardsInfoWithoutToken();
    }
}
