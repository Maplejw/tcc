package com.igg.boot.framework;

import java.util.ArrayList;
import java.util.List;

import com.igg.boot.framework.es.model.BookModel;
import org.aspectj.weaver.ast.And;
import org.elasticsearch.search.Scroll;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ScrolledPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.junit4.SpringRunner;

import com.igg.boot.framework.autoconfigure.es.ElasticsearchDao;
import com.igg.boot.framework.autoconfigure.es.condition.AndCondition;
import com.igg.boot.framework.autoconfigure.es.condition.Condition;
import com.igg.boot.framework.es.dao.TestDao;
import com.igg.boot.framework.es.model.TestModel;
import com.igg.boot.framework.es.model.VpnTrial;



@RunWith(SpringRunner.class)
@SpringBootTest
public class EsTest {
    @Autowired
    private ElasticsearchDao elastisearch;
    @Autowired
    private TestDao testDao;

    @Test
    public void condition() {
        AndCondition andCondition = Condition.and();
        AndCondition tmpCondition = Condition.and();
        tmpCondition.add(Condition.term("country", "unknown")).add(Condition.term("snId", "100039")).filter();
        andCondition.add(Condition.term("country", "CN")).add(tmpCondition).should();

        List<VpnTrial> list = elastisearch.query(andCondition, VpnTrial.class);
        
        Assert.assertEquals(7, list.size());
    }

    @Test
    public void match(){
        AndCondition andCondition = Condition.and();
//        andCondition.add(Condition.wildcard("bookName","*ma*")).filter();
        andCondition.add(Condition.matchPhrase("bookDesc","a")).filter();
        PageRequest page = PageRequest.of(0,20);

        ScrolledPage<BookModel> scroll = elastisearch.startScroll(andCondition,BookModel.class,page,3000);
        List<BookModel> list = scroll.getContent();
//        System.out.print("ss:" + scroll.get);
        System.out.println("size:" + list.size());
        System.out.println("scrollId:" + scroll.getScrollId());
        ScrolledPage<BookModel> scrollV = elastisearch.continueScroll(scroll.getScrollId(),3000l,BookModel.class);
        List<BookModel> a = scrollV.getContent();
        System.out.print("ss:" + scroll.getNumberOfElements());
        System.out.println("size:" + a.size());
        System.out.println("scrollId:" + scrollV.getScrollId());

        ScrolledPage<BookModel> scrollV1 = elastisearch.continueScroll(scroll.getScrollId(),3000l,BookModel.class);
        List<BookModel> ab = scrollV1.getContent();
        System.out.print("ss:" + scroll.getNumberOfElements());
        System.out.println("size:" + ab.size());
        System.out.println("scrollId:" + scrollV1.getScrollId());


//        List<BookModel> list = elastisearch.query(andCondition,BookModel.class);
//
//        System.out.println(list.size());
//        andCondition = Condition.and();
//        andCondition.add(Condition.matchPhrase("bookDesc","a")).filter();
//
//        list = elastisearch.query(andCondition,BookModel.class);
//        System.out.println(list);
    }
    
    @Test
    public void ss() throws InterruptedException {
        TestModel testModel = new TestModel();
        testModel.setUserName("hello world3");
        testModel.setAddTime(20191117);
        IndexQuery indexQuery = new IndexQuery();
        indexQuery.setObject(testModel);
        testDao.save(testModel);
        elastisearch.saveWithRouting(testModel);
       
        AndCondition andCondition = Condition.and("20191116");
        andCondition.add(Condition.term("add_time", 20191116)).filter();
        List<TestModel> list = elastisearch.query(andCondition, TestModel.class);
        System.out.println(list.toString());
   
    }
    
    
}
