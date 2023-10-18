package tcc.business.model;

import com.igg.boot.framework.jdbc.persistence.Entity;
import com.igg.boot.framework.jdbc.persistence.annotation.Column;
import com.igg.boot.framework.jdbc.persistence.annotation.Id;
import com.igg.boot.framework.jdbc.persistence.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Table("tcc_user_record")
@Getter
@Setter
public class TccUserRecordModel extends Entity {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private Long id;

    @Column
    private Integer credit;

    @Column("transaction_no")
    private String transactionNo;

    @Column
    private Integer status;

    @Column("user_id")
    private Long userId;
}
