package com.igg.boot.framework.autoconfigure.es.condition;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class PrefixCondition extends Condition {
    private String fieldName;
    private String text;

    public PrefixCondition(String fieldName, String text){
        this.fieldName = fieldName;
        this.text = text;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        return QueryBuilders.prefixQuery(fieldName,text);
    }
}
