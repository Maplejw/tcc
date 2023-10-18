package com.igg.boot.framework.es.model;

import com.igg.boot.framework.autoconfigure.es.Routing;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.Mapping;


@Document(indexName = "novel_book",type="maindata",replicas=1,shards=3,refreshInterval="1s")
@Mapping
@Data
public class BookModel {
    @Id
    private String id;
    
    private String bookDesc;
    
    private String bookName;


}
