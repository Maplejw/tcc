package tcc.business.service;

import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import com.igg.boot.framework.jdbc.persistence.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tcc.business.dao.TccProductDao;
import tcc.business.dao.TccProductRecordDao;
import tcc.business.dao.TccUserDao;
import tcc.business.dao.TccUserRecordDao;
import tcc.business.model.TccProductRecordModel;
import tcc.business.model.TccUserRecordModel;
import tcc.business.param.StockParam;
import tcc.business.param.UserParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TccUserService {
    @Autowired
    private TccUserDao tccUserDao;
    @Autowired
    private TccUserRecordDao tccUserRecordDao;

    public int setUserRecord(String transactionNo,int status) {
        Map<String,Object> param = new HashMap<>();
        param.put("status",status);
        param.put("transaction_no",transactionNo);
        return tccUserRecordDao.update("status=:status where transaction_no=:transaction_no and status="+TccActionEnum.Init.getAction(),
                param);
    }


    public void updateUserRollback(String transactionNo,int status){
        Condition condition = Condition.eq("transaction_no",transactionNo);
        List<TccUserRecordModel> tccUserRecordModelList = tccUserRecordDao.query(condition);
        if (tccUserRecordModelList.size() > 0){
            TccUserRecordModel tccUserRecordModel = tccUserRecordModelList.get(0);
            if(setUserRecord(transactionNo,status) > 0){
                Map<String,Object> param = new HashMap<>();
                param.put("credit",tccUserRecordModel.getCredit());
                param.put("id",tccUserRecordModel.getUserId());
                tccUserDao.update("credit=-:credit where id=:id",param);
            }
        }
    }


    public boolean updateUser(UserParam userParam){
        Map<String,Object> param = new HashMap<>();
        param.put("credit",userParam.getCredit());
        param.put("id",userParam.getUserId());
        int row = tccUserDao.update("credit=credit-:credit where id=:id and credit>=:credit",param);
        if(row > 0){
            TccUserRecordModel tccUserRecordModel = new TccUserRecordModel();
            tccUserRecordModel.setTransactionNo(userParam.getTransactionNo());
            tccUserRecordModel.setCredit(-userParam.getCredit());
            tccUserRecordModel.setStatus(TccActionEnum.Init.getAction());
            tccUserRecordModel.setUserId(userParam.getUserId());
            tccUserRecordDao.save(tccUserRecordModel);
            return true;
        }else{
            return false;
        }
    }
}
