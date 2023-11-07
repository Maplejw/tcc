package igg.tcc.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igg.boot.framework.autoconfigure.tcc.TccActionEnum;
import igg.tcc.configuration.TccCenterTransactionProperties;
import igg.tcc.model.TccTransactionModel;
import igg.tcc.service.TccTransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@EnableScheduling
public class TccScheduler {
    @Autowired
    private TccTransactionService tccTransactionService;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TccCenterTransactionProperties tccCenterTransactionProperties;

    @Scheduled(cron = "0 * * * * ?")
    public void tccCenterScheduler(){
        //todo 分布式事务锁
        log.info("scheduler start");
        long time = (System.currentTimeMillis()/1000) - tccCenterTransactionProperties.getDelyTime();
        List<TccTransactionModel> tccTransactionModelList = tccTransactionService.getTccTransactionModel(time);
        log.info("scheduler proccess num:" + tccTransactionModelList.size());
        tccTransactionModelList.forEach(tccTransactionModel -> {
            Map<String,Object> params = new HashMap<>();
            if(tccTransactionModel.getStatus() == TccActionEnum.Init.getAction()){
                //初始化的事务状态一律设置为取消
                params.put("action_type", TccActionEnum.Cancel.getAction());
            }else{
                params.put("action_type",tccTransactionModel.getStatus());
            }
            params.put("transaction_no",tccTransactionModel.getTranscationNo());
            if(tccTransactionModel.getStatus() == TccActionEnum.Init.getAction()){
                //防止消息二次发送
                if(tccTransactionService.updateTccTransactionByTransactionNo(tccTransactionModel.getTranscationNo(),
                        TccActionEnum.Cancel.getAction(),System.currentTimeMillis()/1000) > 0){
                    try {
                        kafkaTemplate.send(tccTransactionModel.getTopic(),
                                tccTransactionModel.getTranscationNo(),
                                objectMapper.writeValueAsString(params));
                        tccTransactionService.updateTccTransactionSendStatus(tccTransactionModel.getTranscationNo());
                        log.info(tccTransactionModel.getTranscationNo() + ":resend success");
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(),e);
                    }
                }
            }else{
                try {
                    kafkaTemplate.send(tccTransactionModel.getTopic(),
                            tccTransactionModel.getTranscationNo(),
                            objectMapper.writeValueAsString(params));
                    tccTransactionService.updateTccTransactionSendStatus(tccTransactionModel.getTranscationNo());
                    log.info(tccTransactionModel.getTranscationNo() + ":resend success");
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(),e);
                }
            }
        });
        log.info("scheduler end");
    }
}
