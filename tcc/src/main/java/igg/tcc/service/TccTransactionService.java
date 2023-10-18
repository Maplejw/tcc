package igg.tcc.service;

import igg.tcc.model.TccTransactionModel;

import java.util.List;

public interface TccTransactionService {
    int saveTccTransaction(String uuid,String topic);

    int updateTccTransactionByTransactionNo(String transactionNo,int status,long updateTime);

    int updateTccTransactionSendStatus(String transactionNo);

    int updateTccTransactionSendAndStatus(String transactionNo,int status);

    List<TccTransactionModel> getTccTransactionModel(long addTime);
}
