package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OilSourceRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 金额
     */
    private Long balance;
    /**
     * 已生成账单金额
     */
    private Long billBalance;
    /**
     * 未用金额
     */
    private Long noPayBalance;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * order_oil_source表单号、recharge_oil_source表单号
     */
    private String orderId;
    /**
     * 已用金额
     */
    private Long paidBalance;
    /**
     * oil_recharge_account表ID
     */
    private Long rechargeId;
    /**
     * 记录类型
     */
    private Integer sourceRecordType;
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 修改时间
     */
    private LocalDateTime updateDate;
    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 匹配金额.
     */
    @TableField(exist = false)
    private Long matchAmount;


}
