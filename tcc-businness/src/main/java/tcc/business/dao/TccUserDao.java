package tcc.business.dao;

import com.igg.boot.framework.jdbc.persistence.dao.impl.BaseDaoImpl;
import org.springframework.stereotype.Repository;
import tcc.business.model.TccUserModel;

@Repository
public class TccUserDao extends BaseDaoImpl<TccUserModel,Long> {
}
