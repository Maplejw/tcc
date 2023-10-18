package igg.tcc.component.impl;

import igg.tcc.component.TccTransactionComponent;
import igg.tcc.service.TccTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TccTransactionComponentImpl implements TccTransactionComponent {
    @Autowired
    private TccTransactionService tccTransactionService;


    public boolean doAction(Map<String, Object> param) {
        return false;
    }
}
