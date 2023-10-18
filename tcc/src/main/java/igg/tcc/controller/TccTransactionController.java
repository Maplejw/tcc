package igg.tcc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igg.boot.framework.autoconfigure.tcc.exception.TccTransactionException;
import com.igg.boot.framework.autoconfigure.tcc.exception.TccTransactionExceptionCode;
import com.igg.boot.framework.rest.api.RestResponseBody;
import igg.tcc.service.TccTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RestResponseBody
@Slf4j
public class TccTransactionController {
    @Autowired
    private TccTransactionService tccTransactionService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @PostMapping("/getTransactionNo")
    public String getTransactionNo(@RequestParam Map<String,Object> params) throws InterruptedException {
        String uuid = UUID.randomUUID().toString();
        String topic = params.get("topic")+"";
        if(tccTransactionService.saveTccTransaction(uuid,topic) > 0){
            return uuid;
        }else{
            throw new TccTransactionException(TccTransactionExceptionCode.TRANSACTION_GET_ERROR);
        }
    }

    @PostMapping("/notifyPath")
    public String notifyPath(@RequestParam Map<String,Object> params){
        try {
            String transactionNo = params.get("transaction_no") + "";
            //防止消息二次发送
            if(tccTransactionService.updateTccTransactionByTransactionNo(transactionNo,
                    Integer.parseInt(params.get(
                            "action_type") + ""),System.currentTimeMillis()/1000) > 0){
                kafkaTemplate.send(params.get("topic") + "",
                        transactionNo,
                        objectMapper.writeValueAsString(params));
                tccTransactionService.updateTccTransactionSendStatus(transactionNo);
                log.info(transactionNo + ": send success");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "success";
    }
}
