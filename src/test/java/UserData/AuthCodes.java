package UserData;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AuthCodes {
    private String id;
    private String user_id;
    private String code;
    private String created;
}