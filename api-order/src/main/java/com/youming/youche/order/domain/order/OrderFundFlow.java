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
    public class OrderFundFlow extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 银行户名
     */
    private String accName;

    private String accNumber;
    /**
     * 交易金额
     */
    private Long amount;
    /**
     * 返现金额
     */
    private Long backIncome;
    /**
     * 银行名称
     */
    private String bankName;
    /**
     * 操作总金额
     */
    private Long batchAmount;
    /**
     * 批次
     */
    private String batchId;
    /**
     * 手机
     */
    private String billId;
    /**
     * 账户类型
     */
    private Long bookType;
    /**
     * 账户类型
     */
    private String bookTypeName;
    /**
     * 业务流水ID
     */
    private Long busiKey;
    /**
     * 业务对象
     */
    private String busiTable;
    /**
     * 业务类型
     */
    private Long businessId;
    /**
     * 业务名称
     */
    private String businessName;
    /**
     * 油卡ID
     */
    private String cardId;
    /**
     * 支付对方成本（单位分）
     */
    private Long cost;
    /**
     * 对方可以使用金额（未到期转移已到期
     */
    private Long faceBalanceUnused;
    /**
     * 已用对方使用金额
     */
    private Long faceBalanceUsed;
    /**
     * 对方类型:1油老板2车老板3经纪人4司机5维修商
     */
    private Integer faceType;
    /**
     * 对方用户ID
     */
    private Long faceUserId;
    /**
     * 对方用户名
     */
    private String faceUserName;
    /**
     * 利润
     */
    private Long income;
    /**
     * 收支状态
     */
    private String inoutSts;
    /**
     * 上报费用
     */
    private Integer isReport;
    /**
     * 油卡未提现金额
     */
    private Long noWithdrawOil;
    /**
     * 操作日期
     */
    private LocalDateTime opDate;
    /**
     * 操作人id
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;

    private LocalDateTime orderFinishTime;

    private Long orderId;

    private Long paidAmount;

    private Integer payState;
    /**
     * 处理描述
     */
    private String reportRemark;

    private LocalDateTime reportTime;

    private Long shouldAmount;

    private Long subjectsId;

    private String subjectsName;

    private Long tenantId;

    private Long toOrderId;

    private Long updateOpId;
    /**
     * 用户id
     */
    private Long userId;

    private String userName;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;


}
