package Request;

import UserData.AuthCodes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.DriverManager;
import java.sql.SQLException;

@Data
@AllArgsConstructor
public class VerificationData {

    private String login;
    private String code;

    public static VerificationData getVerificationData(String login) throws SQLException {
        String code;
        val doSelectCode = "SELECT * FROM auth_codes ORDER BY created DESC LIMIT 1;";
        val runner = new QueryRunner();
        try (val conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/db", "user", "pass"
        );
        ) {
            val authCode = runner.query(conn, doSelectCode, new BeanHandler<>(AuthCodes.class));
            code = authCode.getCode();
        }
        return new VerificationData(login, code);
    }


}
