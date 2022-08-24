package com.youming.youche.market.domain.youka;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 油卡消费返利记录表
    * </p>
* @author XXX
* @since 2022-03-23
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class RechargeConsumeRebate extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 油卡消费记录表id(recharge_consume_record)
            */
    private Long consumeFlowId;

            /**
            * 卡来源服务商id
            */
    private Long serviceUserId;

            /**
            * 服务商名称
            */
    private String serviceName;

            /**
            * 卡号来源站点id
            */
    private Long productId;

            /**
            * 卡号申请时是否开票：0不开票，1开票
            */
    private Integer isNeedBill;

            /**
            * 消费时间
            */
    private LocalDateTime consumeDate;

            /**
            * 卡号
            */
    private String cardNum;

            /**
            * 卡类型：1中石油，2中石化
            */
    private Integer cardType;

            /**
            * 持卡人
            */
    private String cardHolder;

            /**
            * 记录类型：1充值，2油卡消费
            */
    private Integer recordType;

            /**
            * 充值/消费金额（单位分）
            */
    private Long amount;

            /**
            * 余额（单位分）
            */
    private Long balance;

            /**
            * 加油量（毫升）
            */
    private Long oilRise;

            /**
            * 单价（单位分）
            */
    private Long unitPrice;

            /**
            * 返利金额（单位分）
            */
    private Long rebateAmount;

            /**
            * 油品名称
            */
    private String goodsName;

            /**
            * 消费地点
            */
    private String consuemStation;

            /**
            * 租户id
            */
    private Long tenantId;

            /**
            * 车队名称
            */
    private String tenantName;

            /**
            * 车队账户
            */
    private String tenantBill;

            /**
            * 创建时间
            */
    private LocalDateTime createDate;

            /**
            * 修改时间
            */
    private LocalDateTime updateDate;

            /**
            * 操作员id
            */
    private Long opId;

            /**
            * 修改操作员id
            */
    private Long updateOpId;

            /**
            * 处理状态(反写消费记录返利计算)：null初始化，1成功，2失败
            */
    private Integer dealState;

            /**
            * 处理描述
            */
    private String dealRemark;

    private LocalDateTime dealTime;


}
