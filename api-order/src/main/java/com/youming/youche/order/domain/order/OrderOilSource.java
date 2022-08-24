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
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderOilSource extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 授信油
     */
    private Long creditOil;
    /**
     * 是否开票
     */
    private Integer isNeedBill;
    /**
     * 未付授信油
     */
    private Long noCreditOil;
    /**
     * 未付油
     */
    private Long noPayOil;
    /**
     * 未付返利油
     */
    private Long noRebateOil;
    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;
    /**
     * 油渠道 0不开票 1开票
     */
    private String oilAffiliation;
    /**
     * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     */
    private Integer oilBillType;
    /**
     * 油费消费类型:1自有油站，2共享油站
     */
    private Integer oilConsumer;
    /**
     * 操作员ID
     */
    private Long opId;
    /**
     * 来源订单靠台时间
     */
    private LocalDateTime orderDate;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 已付授信油
     */
    private Long paidCreditOil;
    /**
     * 已付油
     */
    private Long paidOil;
    /**
     * 已付返利油
     */
    private Long paidRebateOil;
    /**
     * 返利油
     */
    private Long rebateOil;
    /**
     * 来源金额
     */
    private Long sourceAmount;
    /**
     * 来源单号
     */
    private Long sourceOrderId;
    /**
     * 资金来源租户id
     */
    private Long sourceTenantId;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改数据的操作人id
     */
    private Long updateOpId;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 用户类型：1-主驾驶 2-副驾驶 3- 经停驾驶 4-车队
     */
    private Integer userType;
    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;
    /**
     * 匹配金额.
     */

    @TableField(exist = false)
    private Long matchAmount;
    @TableField(exist = false)
    private Long matchNoPayOil;
    @TableField(exist = false)
    private Long matchNoRebateOil;
    @TableField(exist = false)
    private Long matchNoCreditOil;

}
