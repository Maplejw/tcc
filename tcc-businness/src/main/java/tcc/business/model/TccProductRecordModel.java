package tcc.business.model;

import com.igg.boot.framework.jdbc.persistence.Entity;
import com.igg.boot.framework.jdbc.persistence.annotation.Column;
import com.igg.boot.framework.jdbc.persistence.annotation.Id;
import com.igg.boot.framework.jdbc.persistence.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Table("tcc_product_record")
@Getter
@Setter
public class TccProductRecordModel extends Entity {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private Long id;

    @Column
    private Integer stock;

    @Column("transaction_no")
    private String transactionNo;

    @Column
    private Integer status;

    @Column("product_id")
    private Long productId;
}
