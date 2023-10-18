package com.igg.boot.framework.autoconfigure.tcc.model;

import com.igg.boot.framework.jdbc.persistence.Entity;
import com.igg.boot.framework.jdbc.persistence.annotation.Column;
import com.igg.boot.framework.jdbc.persistence.annotation.Id;

public class TccTranscationNoModel extends Entity {
    private static final long serialVersionUID = 1L;
    @Column
    @Id
    private Long id;
    @Column
    private String transcationNo;
    @Column
    private Integer status = 0;
    @Column
    private Long addTime;
    @Column
    private Long updateTime;
}
