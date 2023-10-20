package tcc.business.service;

import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import com.igg.boot.framework.jdbc.persistence.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tcc.business.dao.TccUserDao;
import tcc.business.dao.TccUserRecordDao;
import tcc.business.model.TccUserRecordModel;
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

    //通过唯一索引，来处理当轮询消息初始化进行取消操作，遇到业务阻塞也同时进行操作，通过检测是否重复写入，进行数据回滚操作,因此需要将事务隔离级别调整为READ_COMMITTED,允许有不可重复度
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateUserRollback(String transactionNo,int status) {
        Condition condition = Condition.eq("transaction_no",transactionNo);
        List<TccUserRecordModel> tccUserRecordModelList = tccUserRecordDao.query(condition);
        boolean duplicateKey = false;
        if(tccUserRecordModelList.size() <= 0){
            TccUserRecordModel tccUserRecordModel = new TccUserRecordModel();
            tccUserRecordModel.setUserId(0l);
            tccUserRecordModel.setTransactionNo(transactionNo);
            tccUserRecordModel.setStatus(status);
            tccUserRecordModel.setCredit(0);
            try{
                tccUserRecordDao.save(tccUserRecordModel);
            }catch (DuplicateKeyException e){
                duplicateKey = true;
                tccUserRecordModelList = tccUserRecordDao.query(condition);
            }
        }else{
            duplicateKey = true;
        }
        //幂等性处理，防止重复
        if(duplicateKey && setUserRecord(transactionNo,status) > 0){
            Map<String,Object> param = new HashMap<>();
            param.put("credit",tccUserRecordModelList.get(0).getCredit());
            param.put("id",tccUserRecordModelList.get(0).getUserId());
            tccUserDao.update("credit=credit-:credit where id=:id",param);
        }

    }

    @Transactional
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
