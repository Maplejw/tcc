package igg.tcc.dao;

import com.igg.boot.framework.jdbc.persistence.dao.impl.BaseDaoImpl;
import igg.tcc.model.TccTransactionModel;
import org.springframework.stereotype.Repository;

@Repository
public class TccTransactionDao extends BaseDaoImpl<TccTransactionModel,Long> {
}
