package tcc.business.exception;

import com.igg.boot.framework.exception.CommonException;
import com.igg.boot.framework.exception.CommonExceptionCode;

public class StockException extends CommonException {
    public StockException(CommonExceptionCode code) {
        super(code);
    }
}
