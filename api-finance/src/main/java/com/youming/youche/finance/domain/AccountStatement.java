package com.youming.youche.finance.domain;

    import java.time.LocalDateTime;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author luona
* @since 2022-04-11
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AccountStatement extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账单月份
     */
    private String billMonth;

    /**
     * 对账单编号
     */
    private String billNumber;

    /**
     * 车辆数
     */
    private Integer carNum;

    /**
     * 车辆总费用
     */
    private Long carTotalFee;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 费用扣取方式：1尾款扣除，2现金收取
     */
    private Integer deductionType;

    /**
     * ETC转现（单位分）
     */
    private Long etcTurnCash;

    /**
     * 异常补偿（单位分）
     */
    private Long exceptionIn;

    /**
     * 异常扣减（单位分）
     */
    private Long exceptionOut;

    /**
     * 是否处理：null初始化，1成功，2失败
     */
    private Integer isReport;

    /**
     * 账户未到期金额（单位分）
     */
    private Long marginBalance;

    /**
     * 未付运费（单位分）
     */
    private Long noPayFee;

    /**
     * 订单油卡未退还押金（单位分）
     */
    private Long oilCardDeposit;

    /**
     * 油转现（单位分）
     */
    private Long oilTurnCash;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     * 订单总费用（单位分）
     */
    private Long orderTotalFee;

    /**
     * 已付总金额（单位分）
     */
    private Long paidFee;

    /**
     * 付款方类型
     */
    private Integer payUserType;

    /**
     * 收款方类型
     */
    private Integer receUserType;

    /**
     * 账单接收人名称
     */
    private String receiverName;

    /**
     * 账单接收人手机号
     */
    private String receiverPhone;

    /**
     * 账单接收人用户id
     */
    private Long receiverUserId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 处理描述
     */
    private String reportRemark;

    /**
     * 处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reportTime;

    /**
     * 发送时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sendTime;

    /**
     * 结算金额
     */
    private Long settlementAmount;

    /**
     * 结算说明
     */
    private String settlementRemark;

    /**
     * 状态：1未发送，2确认中(待确认)，3已确认(结算中)，4已结算，5被驳回(已驳回)
     */
    private Integer state;

    /**
     * 车队账号
     */
    private String tenantBill;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 时效罚款（单位分）
     */
    private Long timePenalty;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    /**
     * 更新操作员ID
     */
    private Long updateOpId;

    /**
     * 版本号
     */
    private Long ver;

    /**
     * 核销状态：0待核销，1订单尾款，2线上收款，3线下收款
     */
    private Integer verificationState;

}
