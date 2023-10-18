package tcc.business.param;

import lombok.Data;

@Data
public class UserParam {
//    @NotEmpty
    private String transactionNo;
    private long userId;
    private int credit;
}
