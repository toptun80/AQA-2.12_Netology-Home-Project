package Service;

import Request.AuthData;
import Request.C2CTransferData;
import Request.VerificationData;
import Response.Card;
import Response.ErrorMessage;
import Response.Token;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;

import java.sql.SQLException;
import java.util.List;

import static io.restassured.RestAssured.given;

@Data
public class RequestGenerator {

    private static final String BASE_PATH = "http://localhost";
    private static final String AUTH_ENDPOINT = "/api/auth";
    private static final String VERIFICATION_ENDPOINT = "/api/auth/verification";
    private static final String CARDS_ENDPOINT = "/api/cards";
    private static final String TRANSFER_ENDPOINT = "/api/transfer";
    private AuthData authData;
    private VerificationData verificationData;
    private Token token;
    private List <Card> cards;
    private ErrorMessage errorMessage;
    private C2CTransferData c2CTransferData;

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_PATH)
            .setPort(9999)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public void auth(String login, String password) {

        authData = new AuthData(login, password);

        given()
                 .spec(requestSpec)
                 .body(authData)
                 .when()
                 .post(AUTH_ENDPOINT)
                 .then()
                 .statusCode(200);
    }

    public Token verification(String login) throws SQLException {

        verificationData = VerificationData.getVerificationData(login);

        token = given()
                .spec(requestSpec)
                .body(verificationData)
                .when()
                .post(VERIFICATION_ENDPOINT)
                .then()
                .extract()
                .response()
                .as(Token.class);

        return token;
    }

    public List<Card> getCardsInfo(String token) {

        cards = given()
                .spec(requestSpec)
                .auth()
                .oauth2(token)
                .when()
                .get(CARDS_ENDPOINT)
                .then()
                .extract()
                .body()
                .jsonPath()
                .getList(".", Card.class);

        return cards;
    }

    public void card2CardTransfer(String from, String to, long amount, String token) {

        c2CTransferData = new C2CTransferData(from, to, amount);

        given()
                .spec(requestSpec)
                .auth()
                .oauth2(token)
                .body(c2CTransferData)
                .when()
                .post(TRANSFER_ENDPOINT)
                .then()
                .statusCode(200);
    }

    public ErrorMessage card2CardInvalidTransfer(String from, String to, long amount, String token) {

        c2CTransferData = new C2CTransferData(from, to, amount);

        errorMessage = given()
                .spec(requestSpec)
                .auth()
                .oauth2(token)
                .body(c2CTransferData)
                .when()
                .post(TRANSFER_ENDPOINT)
                .then()
                .statusCode(500)
                .extract()
                .response()
                .as(ErrorMessage.class);

        return errorMessage;
    }

    public ErrorMessage invalidAuth(String login, String password) {

        authData = new AuthData(login, password);

        errorMessage = given()
                .spec(requestSpec)
                .body(authData)
                .when()
                .post(AUTH_ENDPOINT)
                .then()
                .statusCode(400)
                .extract()
                .response().as(ErrorMessage.class);

        return errorMessage;
    }

    public ErrorMessage invalidVerification(String login, String additionalText) throws SQLException {

        verificationData = VerificationData.getVerificationData(login);
        verificationData.setCode(verificationData.getCode().concat(additionalText));

        errorMessage = given()
                .spec(requestSpec)
                .body(verificationData)
                .when()
                .post(VERIFICATION_ENDPOINT)
                .then()
                .statusCode(400)
                .extract()
                .response()
                .as(ErrorMessage.class);

        return errorMessage;
    }

    public void getCardsInfoWithoutToken() {

         given()
                .spec(requestSpec)
                .when()
                .get(CARDS_ENDPOINT)
                .then()
                .statusCode(401);
    }
}
