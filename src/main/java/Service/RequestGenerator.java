package Service;

import Request.AuthData;
import Request.C2CTransferData;
import Request.VerificationData;
import Response.CardsData;
import Response.Token;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Data;

import java.sql.SQLException;

import static io.restassured.RestAssured.given;

@Data
public class RequestGenerator {

    private static final String BASE_PATH = "http://localhost";
    private static final String AUTH_ENDPOINT = "/api/auth";
    private static final String VERIFICATION_ENDPOINT = "/api/auth/verification";
    private static final String CARDS_ENDPOINT = "/api/cards";
    private static final String TRANSFER_ENDPOINT = "/api/transfer";

    RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_PATH)
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public void auth(String login, String password) {

         AuthData authData = new AuthData(login, password);

         given()
                 .spec(requestSpec)
                 .body(authData)
                 .when()
                 .post(AUTH_ENDPOINT)
                 .then()
                 .statusCode(200);
    }

    public Token verification(String login) throws SQLException {

        VerificationData verificationData = VerificationData.getVerificationData(login);

        Token token = given()
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

    public CardsData getCardsInfo(String token) {

        CardsData cardsData = given()
                .spec(requestSpec)
                .auth()
                .oauth2(token)
                .when()
                .get(CARDS_ENDPOINT)
                .then()
                .extract()
                .response()
                .as(CardsData.class);
        return cardsData;
    }

    public void card2CardTransfer(String from, String to, long amount, String token) {

        C2CTransferData c2CTransferData = new C2CTransferData(from, to, amount);

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
}
