package tcc.business.exception;

import com.igg.boot.framework.exception.CommonExceptionCode;

public class StockExceptionCode extends CommonExceptionCode {
    public static final StockExceptionCode STOCK_NOT_ENOUGH = new StockExceptionCode(30001,"商品库存不足");
    public static final StockExceptionCode CREDIT_NOT_ENOUGH = new StockExceptionCode(30002,"用户积分不足");

    public StockExceptionCode(int code, String message) {
        super(code, message);
    }
}
