package igg.tcc.service.impl;

import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import com.igg.boot.framework.jdbc.persistence.bean.PagingParameter;
import com.igg.boot.framework.jdbc.persistence.condition.Condition;
import igg.tcc.dao.TccTransactionDao;
import igg.tcc.model.TccTransactionModel;
import igg.tcc.service.TccTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TccTransactionServiceImpl implements TccTransactionService {
    @Autowired
    private TccTransactionDao tccTransactionDao;

    public int saveTccTransaction(String uuid,String topic) {
        TccTransactionModel tccTransactionModel = new TccTransactionModel();
        tccTransactionModel.setUpdateTime(0l);
        tccTransactionModel.setStatus(TccActionEnum.Init.getAction());
        tccTransactionModel.setSendStatus(0);
        tccTransactionModel.setSendTime(0l);
        tccTransactionModel.setTranscationNo(uuid);
        tccTransactionModel.setAddTime(System.currentTimeMillis()/1000);
        tccTransactionModel.setTopic(topic);

        return tccTransactionDao.save(tccTransactionModel);
    }

    public int updateTccTransactionByTransactionNo(String transactionNo, int status,long updateTime) {
        Map<String,Object> params = new HashMap<>();
        params.put("status",status);
        params.put("transcation_no",transactionNo);
        params.put("update_time",updateTime);

        return tccTransactionDao.update("status=:status,update_time=:update_time where transcation_no=:transcation_no" +
                        " and status="+TccActionEnum.Init.getAction(),
                params);
    }

    @Override
    public int updateTccTransactionSendStatus(String transactionNo) {
        Map<String,Object> params = new HashMap<>();
        params.put("transcation_no",transactionNo);
        params.put("send_time",System.currentTimeMillis()/1000);

        return tccTransactionDao.update("send_status=1,send_time=:send_time where " +
                        "transcation_no=:transcation_no",
                params);
    }

    @Override
    public int updateTccTransactionSendAndStatus(String transactionNo, int status) {
        long time = System.currentTimeMillis()/1000;
        Map<String,Object> params = new HashMap<>();
        params.put("transcation_no",transactionNo);
        params.put("status",status);
        params.put("send_time",time);
        params.put("update_time",time);

        return tccTransactionDao.update("status=:status,send_status=1,update_time=:update_time,send_time=:send_time " +
                        "where " +
                        "transcation_no=:transcation_no",
                params);
    }

    /**
     * 获取还未发送的消息
     * @param addTime
     * @return
     */
    @Override
    public List<TccTransactionModel> getTccTransactionModel(long addTime) {
        Condition condition =
                Condition.and(Condition.eq("send_status",0)).add(Condition.le(
                "add_time"
                ,addTime));
        PagingParameter page = new PagingParameter(0,100);

        return tccTransactionDao.query(condition,"id asc",page);
    }
}
