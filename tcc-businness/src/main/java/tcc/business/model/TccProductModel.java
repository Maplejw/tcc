package tcc.business.model;

import com.igg.boot.framework.jdbc.persistence.Entity;
import com.igg.boot.framework.jdbc.persistence.annotation.Column;
import com.igg.boot.framework.jdbc.persistence.annotation.Id;
import com.igg.boot.framework.jdbc.persistence.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Table("tcc_product")
@Getter
@Setter
public class TccProductModel extends Entity {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private Long id;

    @Column
    private Integer stock;
}
