package com.youming.youche.order.domain.order;

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
    public class ServiceMatchOrder extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户余额
     */
    private Long accountBalance;
    /**
     * 预支手续费(分)
     */
    private Long advanceFee;
    /**
     * 匹配金额
     */
    private Long amount;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 来源订单
     */
    private Integer fromState;
    /**
     * 是否开票
     */
    private Integer isNeedBill;
    /**
     * 未提现金额
     */
    private Long noWithdrawAmount;
    /**
     * 油资金渠道
     */
    private String oilAffiliation;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 订单id
     */
    private Long orderId;
    /**
     * order_oil_source/recharge_oil_source表主键
     */
    private Long otherFlowId;
    /**
     * 油站id
     */
    private Long productId;
    /**
     * 1油老板，2维修商，3ETC
     */
    private Integer serviceType;
    /**
     * 维修商id
     */
    private Long serviceUserId;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改人id
     */
    private Long updateOpId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 资金渠道
     */
    private String vehicleAffiliation;
    /**
     * 未提现金额
     */
    private Long withdrawAmount;


}
