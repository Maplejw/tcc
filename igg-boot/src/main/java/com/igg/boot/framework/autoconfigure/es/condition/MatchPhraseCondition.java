package com.igg.boot.framework.autoconfigure.es.condition;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

public class MatchPhraseCondition extends Condition {
    private String fieldName;
    private String text;
    private boolean prefix = false;

    public MatchPhraseCondition(String fieldName, String text){
        this.fieldName = fieldName;
        this.text = text;
    }

    public MatchPhraseCondition(String fieldName, String text,boolean prefix){
        this.fieldName = fieldName;
        this.text = text;
        this.prefix = prefix;
    }

    @Override
    public QueryBuilder toQueryBuilder() {
        if(prefix){
            return QueryBuilders.matchPhrasePrefixQuery(fieldName,text);
        }else{
            return QueryBuilders.matchPhraseQuery(fieldName,text);
        }

    }
}
