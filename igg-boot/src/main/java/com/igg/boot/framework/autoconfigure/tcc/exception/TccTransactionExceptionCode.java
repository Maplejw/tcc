package com.igg.boot.framework.autoconfigure.tcc.exception;

import com.igg.boot.framework.exception.CommonExceptionCode;
import com.igg.boot.framework.exception.FrameworkSystemExceptionCode;
import org.springframework.http.HttpStatus;

public class TccTransactionExceptionCode extends FrameworkSystemExceptionCode {
    public static final TccTransactionExceptionCode TRANSACTION_GET_ERROR= new TccTransactionExceptionCode(10020,
            "获取全局事务ID失败:",
            HttpStatus.INTERNAL_SERVER_ERROR);
    public static final TccTransactionExceptionCode TRANSACTION_CONFIRM_ERROR = new TccTransactionExceptionCode(10021
            ,"请求全局事务中心确认事务失败");

    public TccTransactionExceptionCode(int code, String message) {
        super(code, message);
    }

    public TccTransactionExceptionCode(int code, String message, HttpStatus httpStatus) {
        super(code, message,httpStatus);
    }
}
