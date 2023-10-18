package com.igg.boot.framework.autoconfigure.tcc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igg.boot.framework.autoconfigure.tcc.annotation.TccTopic;
import com.igg.boot.framework.autoconfigure.tcc.exception.TccTransactionException;
import com.igg.boot.framework.autoconfigure.tcc.exception.TccTransactionExceptionCode;
import com.igg.boot.framework.rest.api.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Order(1)
@Configuration
@Slf4j
@EnableConfigurationProperties(TccProperties.class)
@ConditionalOnProperty(prefix = "igg.tcc", name = "enable", matchIfMissing = false)
public class TccTopicAspect {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TccProperties tccProperties;
    private final static String ACTION_TYPE = "action_type";
    private final static String TRANSACTION = "transaction_no";
    private final static String TOPIC = "topic";

    @Pointcut("@annotation(com.igg.boot.framework.autoconfigure.tcc.annotation.TccTopic)")
    public void tccPointcut(){}

    @Before(value = "tccPointcut()")
    public void setTransactionNo(JoinPoint point){
        try{
            TccTopic tccTopic = ((MethodSignature)point.getSignature()).getMethod().getAnnotation(TccTopic.class);
            String topic = tccTopic.name();
            MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
            param.add(TOPIC,topic);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(param,headers);
            String url = "http://" + tccProperties.getHost() + "/"+tccProperties.getPath();
            String ret = restTemplate.postForObject(url,request,String.class);
            RestResponse<Map<String,String>> data = objectMapper.readValue(ret,
                    new TypeReference<RestResponse<Map<String,String>>>(){});
            if(data.getCode() == 0){
                TccTranscationNo.setTranscationNo(data.getData().get("result"));
            }else{
                throw new TccTransactionException(TccTransactionExceptionCode.TRANSACTION_GET_ERROR,data.getMessage());
            }
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            throw new TccTransactionException(TccTransactionExceptionCode.TRANSACTION_GET_ERROR);
        }
    }

    @AfterReturning(value = "tccPointcut()")
    public void doConfirm(JoinPoint point) {
        String txNo = TccTranscationNo.getTranscationNo();
        if(!doAction(point,TccActionEnum.Confirm,null)){
            TccTranscationNo.setTranscationNo(txNo);
            System.out.print("33333333");
            throw new TccTransactionException(TccTransactionExceptionCode.TRANSACTION_CONFIRM_ERROR);
        }
    }

    @AfterThrowing(value = "tccPointcut()",throwing = "ex")
    public void doCancel(JoinPoint point, Exception ex) {
        System.out.print("2222222222222");
        doAction(point,TccActionEnum.Cancel,ex);
    }

    /**
     * 发送消息到指定的业务topic进行confirm或者cancel操作
     * @param point
     * @param tccActionEnum
     */
    private boolean doAction(JoinPoint point,TccActionEnum tccActionEnum,Exception ex){
        try{
            String url = "http://" + tccProperties.getHost() + "/"+tccProperties.getNotifyPath();
            String txNo = TccTranscationNo.getTranscationNo();
            MultiValueMap<String,Object> param = new LinkedMultiValueMap<>();
            param.add(ACTION_TYPE,tccActionEnum.getAction());
            param.add(TRANSACTION,txNo);
            if(ex instanceof TccTransactionException){
                TccTransactionException tc = (TccTransactionException)ex;
                if(tc.getCode() != TccTransactionExceptionCode.TRANSACTION_GET_ERROR.getCode()){
                    TccTopic tccTopic = ((MethodSignature)point.getSignature()).getMethod().getAnnotation(TccTopic.class);
                    String topic = tccTopic.name();
                    param.add("topic",topic);
                }else{
                    //无需处理
                    return true;
                }
            }else{
                TccTopic tccTopic = ((MethodSignature)point.getSignature()).getMethod().getAnnotation(TccTopic.class);
                String topic = tccTopic.name();
                param.add("topic",topic);
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(param,headers);
            try {
                String ret = restTemplate.postForObject(url,request,String.class);
                RestResponse<Map<String,String>> data = objectMapper.readValue(ret,
                        new TypeReference<RestResponse<Map<String,String>>>(){});
                if(data.getCode() != 0){
                    log.info("notify tcc fail:" + data.getMessage());
                    return false;
                }else{
                    return true;
                }
            } catch (Exception e) {
                //TCC中心存在异常可能通知成功可能通知失败
                log.error(e.getMessage(),e);
                return false;
            }
        }finally {
            TccTranscationNo.removeTranscationNo();
        }
    }


}
