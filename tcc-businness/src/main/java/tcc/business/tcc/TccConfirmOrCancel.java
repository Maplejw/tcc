package tcc.business.tcc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igg.boot.framework.autoconfigure.tcc.ActionData;
import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import com.igg.boot.framework.autoconfigure.tcc.annotation.DoTccAction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tcc.business.service.TccProductService;
import tcc.business.service.TccUserService;

import java.io.IOException;

@Component
public class TccConfirmOrCancel {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TccProductService tccProductService;
    @Autowired
    private TccUserService tccUserService;

    @DoTccAction(topics = {"tcc-test-business"},groupId="user-test")
    public void tccUser(ConsumerRecord<String, String> cr, Acknowledgment ack){
        String record = cr.value();
        try {
            ActionData actionData = objectMapper.readValue(record,ActionData.class);
            if(actionData.getActionType() == TccActionEnum.Confirm.getAction()){//confirm
                System.out.println("user confirm");
                tccUserService.setUserRecord(actionData.getTransactionNo(),TccActionEnum.Confirm.getAction());
            }else{ //rollback
                System.out.println("user cancel");
                tccUserService.updateUserRollback(actionData.getTransactionNo(),TccActionEnum.Cancel.getAction());
            }
            ack.acknowledge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DoTccAction(topics={"tcc-test-business"})
    public void tccStock(ConsumerRecord<String, String> cr, Acknowledgment ack){
        String record = cr.value();
        try {
            ActionData actionData = objectMapper.readValue(record,ActionData.class);
            if(actionData.getActionType() == TccActionEnum.Confirm.getAction()){//confirm
                System.out.println("stock confirm");
                tccProductService.setProductRecord(actionData.getTransactionNo(),TccActionEnum.Confirm.getAction());
            }else{ //rollback
                System.out.println("stock cancel");
                tccProductService.updateProductRollback(actionData.getTransactionNo(),TccActionEnum.Cancel.getAction());
            }
            ack.acknowledge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
