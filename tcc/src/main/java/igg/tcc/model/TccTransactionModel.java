package igg.tcc.model;

import com.igg.boot.framework.jdbc.persistence.Entity;
import com.igg.boot.framework.jdbc.persistence.annotation.Column;
import com.igg.boot.framework.jdbc.persistence.annotation.Id;
import com.igg.boot.framework.jdbc.persistence.annotation.Table;
import lombok.Getter;
import lombok.Setter;

@Table("tcc_transcation_no")
@Getter
@Setter
public class TccTransactionModel extends Entity {
    private static final long serialVersionUID = 1L;

    @Column
    @Id
    private Long id;
    @Column("transcation_no")
    private String transcationNo;

    /**
     * 0-初始状态
     * 1-确认
     * 2-回滚
     * 当前事务状态
     */
    @Column
    private Integer status;
    @Column("add_time")
    private Long addTime;
    @Column("update_time")
    private Long updateTime;
    /**
     * 0-未发送
     * 1-已发送
     * 用于事务补偿，当为0时需要进行重试confirm或者cancel
     */
    @Column("send_status")
    private Integer sendStatus;
    @Column
    private String topic;
    @Column("send_time")
    private long sendTime;
}
