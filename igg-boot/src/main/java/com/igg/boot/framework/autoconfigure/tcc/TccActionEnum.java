package com.igg.boot.framework.autoconfigure.tcc;

public enum TccActionEnum {
    Init(0),Confirm(1),Cancel(2);
    private int action;
    TccActionEnum(int action){
        this.action = action;
    }

    public int getAction(){
        return this.action;
    }
}
