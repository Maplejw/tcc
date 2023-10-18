package com.igg.boot.framework.autoconfigure.tcc.exception;

import com.igg.boot.framework.exception.CommonExceptionCode;
import com.igg.boot.framework.exception.FrameworkSystemException;
import com.igg.boot.framework.exception.FrameworkSystemExceptionCode;
import org.springframework.http.HttpStatus;

public class TccTransactionException extends FrameworkSystemException {
    private static final long serialVersionUID = 6502491918007587465L;
    private TccTransactionExceptionCode exCode;

    public TccTransactionException(TccTransactionExceptionCode code) {
        this(code, null, "");
    }

    public TccTransactionException(TccTransactionExceptionCode code, Throwable cause) {
        this(code, cause, "");
    }

    public TccTransactionException(TccTransactionExceptionCode code, String appendMsg) {
        this(code, null, appendMsg);
    }

    public TccTransactionException(TccTransactionExceptionCode code, Throwable cause, String appendMsg) {
        super(code.getMessage() + appendMsg, cause);
        exCode = code;
    }

    @Override
    public int getCode() {
        return exCode.getCode();
    }

    @Override
    public HttpStatus getHttpStatus() {
        return exCode.getHttpStatus();
    }

    @Override
    public FrameworkSystemExceptionCode getExceptionCode() {
        return exCode;
    }

}
