package tcc.business.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igg.boot.framework.autoconfigure.tcc.TccTranscationNo;
import com.igg.boot.framework.autoconfigure.tcc.annotation.TccTopic;
import com.igg.boot.framework.rest.annotation.ModelForm;
import com.igg.boot.framework.rest.api.RestResponse;
import com.igg.boot.framework.rest.api.RestResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import tcc.business.exception.StockException;
import tcc.business.exception.StockExceptionCode;
import tcc.business.param.StockParam;
import tcc.business.param.UserParam;
import tcc.business.service.TccProductService;
import tcc.business.service.TccUserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class BusinessController {
    @Autowired
    private TccProductService tccProductService;
    @Autowired
    private TccUserService tccUserService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/test")
    @RestResponseBody
    @TccTopic(name = "tcc-test-business")
    public String test() throws IOException, InterruptedException {
        Map<String,Object> param = new HashMap<>();
        String txNo = TccTranscationNo.getTranscationNo();
        param.put("product_id",1);
        param.put("stock",10);
        param.put("transaction_no",txNo);

        String ret = restTemplate.getForObject("http://127.0.0.1:8020/stock?transaction_no={transaction_no" +
                "}&product_id={product_id}&stock={stock}",String.class,param);
//        System.out.println(ret);
        RestResponse<Boolean> restResponse = objectMapper.readValue(ret,RestResponse.class);
        if(restResponse.getCode() == 0 && restResponse.getData()){
            param.clear();
            //如果这里代码休眠100秒，而TCC定时任务轮训30S前未处理的事务，就会发送取消，而当休眠时间过了，就会继续执行业务逻辑导致数据不一致
            //Thread.sleep(3000);
            param.put("user_id",1);
            param.put("credit",5);
            param.put("transaction_no",txNo);
            ret = restTemplate.getForObject("http://127.0.0.1:8020/user?transaction_no={transaction_no" +
                    "}&user_id={user_id}&credit={credit}",String.class,param);
            restResponse = objectMapper.readValue(ret,RestResponse.class);
            if(restResponse.getCode() == 0 && restResponse.getData()){
                return "hello world";
            }else{
                throw new StockException(StockExceptionCode.CREDIT_NOT_ENOUGH);
            }
        }else{
            throw new StockException(StockExceptionCode.STOCK_NOT_ENOUGH);
        }
    }

    @GetMapping("/stock")
    @RestResponseBody
    public boolean stockMinus(@ModelForm StockParam stockParam){
        return tccProductService.updateProduct(stockParam);
    }

    @GetMapping("/user")
    @RestResponseBody
    public boolean userCredit(@ModelForm UserParam userParam){
        return tccUserService.updateUser(userParam);
    }
}
