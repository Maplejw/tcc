package com.igg.boot.framework.autoconfigure.es.condition;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;

public abstract class Condition {
	public abstract QueryBuilder toQueryBuilder();
	
	public static TermCondition term(String key,Object value) {
		return new TermCondition(key,value);
	}
	
	public static RangeCondition range(String key) {
		return new RangeCondition(key);
	}
	
	public static AndCondition and(Condition condition) {
		return new AndCondition(condition);
	}
	
	public static ExitCondition exit(String key) {
	    return new ExitCondition(key);
	} 
	
	public static AndCondition and() {
		return new AndCondition("");
	}
	
	public static AndCondition and(String routing) {
        return new AndCondition(routing);
    }

	/**
	 * 支持text类型的字段，keyword类型的字段只能匹配全值
	 * 支持分词查询
	 * match("filedName","hello world")
	 * 支持匹配到hello、world、hello world的所有记录
	 * @param fieldName
	 * @param text
	 * @return
	 */
	public static MatchCondition match(String fieldName,String text) {return new MatchCondition(fieldName,text);}

	/**
	 * 支持text类型的字段，keyword类型的字段只能匹配全值
	 * 只能支持联系短语的匹配，不能分词查询
	 * matchPhrase("filedName","hello world")
	 * 支持匹配到hello world的所有记录
	 * @param fieldName
	 * @param text
	 * @return
	 */
	public static MatchPhraseCondition matchPhrase(String fieldName,String text) {return new MatchPhraseCondition(fieldName,text);}

	/**
	 * 支持text类型的字段，keyword类型的字段只能匹配全值
	 * 只能支持联系短语的匹配，不能分词查询,而且是前缀匹配的查询
	 * matchPhrase("filedName","hello world",true)
	 *
	 * @param fieldName
	 * @param text
	 * @param prefix
	 * @return
	 */
	public static MatchPhraseCondition matchPhrase(String fieldName,String text,boolean prefix) {return new MatchPhraseCondition(fieldName,text,prefix);}

	/**
	 * 前缀查询，可以支持text和keyword类型
	 * @param fieldName
	 * @param prefix
	 * @return
	 */
	public static PrefixCondition prefix(String fieldName,String prefix){ return new PrefixCondition(fieldName,prefix);}

	/**
	 * 模糊查询，支持*text*模糊匹配，但最好左边不要用*，会极大降低性能
	 * text类型的字段 要转小写查询，keyword类型严格参照大小写
	 * @param fieldName
	 * @param text
	 * @return
	 */
	public static WildcardCondition wildcard(String fieldName,String text){return new WildcardCondition(fieldName,text);}
}
