package com.youming.youche.order.domain.order;

    import java.io.Serializable;
    import java.time.LocalDateTime;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 充值油来源关系表
    * </p>
* @author CaoYaJie
* @since 2022-03-19
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class RechargeOilSource extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 业务操作类型：1客户油充值,2车队油充值
            */
    private Integer rechargeType;

            /**
            * 充值来源:为0使用本车队油，不为0使用客户油(来自order_oil_source表主键)
            */
    private Long fromFlowId;

            /**
            * 充值单号
            */
    private String rechargeOrderId;

            /**
            * 用户id
            */
    private Long userId;

            /**
            * 来源单号
            */
    private String sourceOrderId;

            /**
            * 来源金额
            */
    private Long sourceAmount;

            /**
            * 未付油
            */
    private Long noPayOil;

            /**
            * 已付油
            */
    private Long paidOil;

            /**
            * 订单来源租户id
            */
    private Long tenantId;

            /**
            * 订单来源资金渠道类型
            */
    private String vehicleAffiliation;

            /**
            * 来源订单靠台时间
            */
    private LocalDateTime orderDate;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 来源单号是否需要开票 0无需 1需要
            */
    private Integer isNeedBill;

            /**
            * 资金(油)渠道类型
            */
    private String oilAffiliation;

            /**
            * 油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
            */
    private Integer oilAccountType;

            /**
            * 油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
            */
    private Integer oilBillType;

            /**
            * 油费消费类型:1自有油站，2共享油站
            */
    private Integer oilConsumer;

            /**
            * 返利油
            */
    private Long rebateOil;

            /**
            * 未付返利油
            */
    private Long noRebateOil;

            /**
            * 已付返利油
            */
    private Long paidRebateOil;

            /**
            * 授信油
            */
    private Long creditOil;

            /**
            * 未付授信油
            */
    private Long noCreditOil;

            /**
            * 已付授信油
            */
    private Long paidCreditOil;

            /**
            * 资金来源租户id
            */
    private Long sourceTenantId;

            /**
            * 收款人用户类型
            */
    private Integer userType;


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
