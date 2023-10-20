package tcc.business.service;

import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import com.igg.boot.framework.jdbc.persistence.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tcc.business.dao.TccProductDao;
import tcc.business.dao.TccProductRecordDao;
import tcc.business.model.TccProductRecordModel;
import tcc.business.param.StockParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TccProductService {
    @Autowired
    private TccProductDao tccProductDao;
    @Autowired
    private TccProductRecordDao tccProductRecordDao;

    public int setProductRecord(String transactionNo,int status) {
        Map<String,Object> param = new HashMap<>();
        param.put("status",status);
        param.put("transaction_no",transactionNo);
        return tccProductRecordDao.update("status=:status where transaction_no=:transaction_no and status="+TccActionEnum.Init.getAction(),
                param);
    }

    @Transactional
    public void updateProductRollback(String transactionNo,int status){
        Condition condition = Condition.eq("transaction_no",transactionNo);
        List<TccProductRecordModel> tccProductRecordModelList = tccProductRecordDao.query(condition);
        if(tccProductRecordModelList.size() <= 0){
            TccProductRecordModel tccProductRecordModel = new TccProductRecordModel();
            tccProductRecordModel.setProductId(0l);
            tccProductRecordModel.setTransactionNo(transactionNo);
            tccProductRecordModel.setStatus(status);
            tccProductRecordModel.setStock(0);
            tccProductRecordDao.save(tccProductRecordModel);
            return;
        }
        //幂等性处理，防止重复
        if(setProductRecord(transactionNo,status) > 0){
            TccProductRecordModel tccProductRecordModel = tccProductRecordModelList.get(0);
            Map<String,Object> param = new HashMap<>();
            param.put("stock",tccProductRecordModel.getStock());
            param.put("id",tccProductRecordModel.getProductId());
            tccProductDao.update("stock=stock-:stock where id=:id",param);
        }
    }

    @Transactional
    public boolean updateProduct(StockParam stockParam){
        Map<String,Object> param = new HashMap<>();
        param.put("stock",stockParam.getStock());
        param.put("id",stockParam.getProductId());
        int row = tccProductDao.update("stock=stock-:stock where id=:id and stock>=:stock",param);
        if(row > 0){
            TccProductRecordModel tccProductRecordModel = new TccProductRecordModel();
            tccProductRecordModel.setTransactionNo(stockParam.getTransactionNo());
            tccProductRecordModel.setStock(-stockParam.getStock());
            tccProductRecordModel.setStatus(TccActionEnum.Init.getAction());
            tccProductRecordModel.setProductId(stockParam.getProductId());
            tccProductRecordDao.save(tccProductRecordModel);
            return true;
        }else{
            return false;
        }
    }
}
