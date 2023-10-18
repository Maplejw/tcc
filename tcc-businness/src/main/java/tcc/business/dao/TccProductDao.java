package tcc.business.dao;

import com.igg.boot.framework.jdbc.persistence.dao.impl.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import tcc.business.model.TccProductModel;

@Repository
public class TccProductDao extends BaseDaoImpl<TccProductModel,Long> {
}
