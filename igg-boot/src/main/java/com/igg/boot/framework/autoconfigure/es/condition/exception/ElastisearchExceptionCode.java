package com.igg.boot.framework.autoconfigure.es.condition.exception;

import com.igg.boot.framework.exception.CommonExceptionCode;
import org.springframework.http.HttpStatus;

public class ElastisearchExceptionCode extends CommonExceptionCode {
    public static final ElastisearchExceptionCode AND_CONDITION_ERROR = new ElastisearchExceptionCode(10020,
            "andCondition必须配有filter/must/mustnot/should的过滤条件",
            HttpStatus.INTERNAL_SERVER_ERROR);
    public static final ElastisearchExceptionCode ES_RANGE_CONDITION = new ElastisearchExceptionCode(10021,"范围查询没有指定范围");
    public static final ElastisearchExceptionCode ES_ANNOTATION_NOT_FOUND_ERROR = new ElastisearchExceptionCode(10022
            ,"ES指定的注解不存在");
    public static final ElastisearchExceptionCode ES_ANNOTATION_ROUTING_NOT_FOUND_ERROR =
            new ElastisearchExceptionCode(10023,"ES指定的routing不存在");

    public ElastisearchExceptionCode(int code, String message) {
        super(code, message);
    }

    public ElastisearchExceptionCode(int code, String message, HttpStatus httpStatus) {
        super(code, message, httpStatus);
    }
}
