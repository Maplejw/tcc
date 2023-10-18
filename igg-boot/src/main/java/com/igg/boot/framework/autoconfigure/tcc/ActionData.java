package com.igg.boot.framework.autoconfigure.tcc;

import lombok.Data;

@Data
public class ActionData{
    private String transactionNo;
    private int actionType;
    private String topic;
}
