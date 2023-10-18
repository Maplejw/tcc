package tcc.business.param;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class StockParam {
//    @NotEmpty
    private String transactionNo;
    private long productId;
    private int stock;
}
