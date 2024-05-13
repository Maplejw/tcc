package com.igg.boot.framework.autoconfigure.es.condition.exception;

import com.igg.boot.framework.exception.CommonException;

public class ElastisearchException extends CommonException {
	private static final long serialVersionUID = 3695531363803355998L;
	
	public ElastisearchException(ElastisearchExceptionCode code) {
        super(code,"");
    }


}
