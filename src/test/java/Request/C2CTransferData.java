package Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class C2CTransferData {

    private String from;
    private String to;
    private long amount;
}
