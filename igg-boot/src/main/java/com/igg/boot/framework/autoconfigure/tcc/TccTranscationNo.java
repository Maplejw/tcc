package com.igg.boot.framework.autoconfigure.tcc;

public class TccTranscationNo {
    private static ThreadLocal<String> transcation = new ThreadLocal<>();

    public static void setTranscationNo(String datasource) {
        transcation.set(datasource);
    }

    public static String getTranscationNo() {
        return transcation.get();
    }

    public static void removeTranscationNo() {
        transcation.remove();
    }
}
