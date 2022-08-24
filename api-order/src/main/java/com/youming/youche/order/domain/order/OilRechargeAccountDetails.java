package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

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
    public class OilRechargeAccountDetails extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     *账户余额
     */
    private Long accountBalance;
    /**
     *账户类型 1母卡 2子卡
     */
    private Integer accountType;
    /**
     *渠道类型
     */
    private String channelType;
    /**
     *已分配金额
     */
    private Long distributedAmount;
    /**
     *操作员ID
     */
    private Long opId;
    /**
     *操作员
     */
    private String opName;
    /**
     *平安虚拟账户ID
     */
    private String pinganAccId;
    /**
     *来源平安虚拟账户ID
     */
    private String sourcePinganAccId;
    /**
     *充值来源  1返利充值 2现金充值 3授信充值 4继承 5抵扣票现金充值  6转移账户充值
     */
    private Integer sourceType;
    /**
     *来源用户
     */
    private Long sourceUserId;
    /**
     *1有效 0无效
     */
    private Integer state;
    /**
     *租户
     */
    private Long tenantId;
    /**
     *已分配(未消费)余额
     */
    private Long unUseredBalance;
    /**
     *修改操作员ID
     */
    private Long updateOpId;
    /**
     *用户编号
     */
    private Long userId;
    /**
     *资金渠道（只有运输专票才记录，其他的不记录）
     */
    private String vehicleAffiliation;


}
