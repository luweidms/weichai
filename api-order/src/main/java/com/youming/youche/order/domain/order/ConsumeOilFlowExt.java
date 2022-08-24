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
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class ConsumeOilFlowExt extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账单ID
     */
    private String billFlowId;

    /**
     * 是否已经生成账单:0否，1是
     */
    private Integer billState;

    /**
     * 自有油站消费时，是否受授信额度限制：0否，1是
     */
    private Integer creditLimit;

    /**
     * 授信油
     */
    private Long creditOil;

    /**
     * consume_oil_flow表主键
     */
    private Long flowId;

    /**
     * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
     */
    private Integer oilAccountType;

    /**
     * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
     */
    private Integer oilBillType;

    /**
     * 油品公司服务商用户ID
     */
    private Long oilComUserId;

    /**
     * 油费消费类型:1自有油站，2共享油站
     */
    private Integer oilConsumer;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * order_oil_source/recharge_oil_source表主键
     */
    private Long otherFlowId;

    /**
     * 返利
     */
    private Long rebate;

    /**
     * 返利油
     */
    private Long rebateOil;

    /**
     * 充值油
     */
    private Long rechargeOil;

    /**
     * 备注
     */
    private String remark;

    /**
     * 记录类型：1(order_oil_source),2(recharge_oil_source)
     */
    private Integer sourceRecordType;

    /**
     * 返利状态:0未返利，1返利中 2返利成功 3返利失败
     */
    private Integer state;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 修改人
     */
    private Long updateOpId;


}
