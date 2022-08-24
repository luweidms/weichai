package com.youming.youche.finance.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author zengwen
 * @date 2022/4/12 16:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountDetails extends BaseDomain {

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 业务类型
     */
    private Integer businessTypes;

    /**
     * 业务编码
     */
    private Long businessNumber;

    /**
     * 费用科目ID
     */
    private Long subjectsId;

    /**
     * 费用科目名称
     */
    private String subjectsName;

    /**
     * 金额（分）
     */
    private Long amount;

    /**
     * 当前金额（分）
     */
    private Long currentAmount;

    /**
     * 欠款金额
     */
    private Long debtAmount;

    /**
     * 资金转移
     */
    private Long transferAmount;

    /**
     * 当前欠款金额
     */
    private Long currentDebtAmount;

    /**
     * 费用类型 1：消费  2：充值
     */
    private Integer costType;

    /**
     * 备注
     */
    private String note;

    /**
     * 流水号
     */
    private Long soNbr;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 对方用户编号
     */
    private Long otherUserId;

    /**
     * 对方名称
     */
    private String otherName;

    /**
     * 账本科目
     */
    private Long bookType;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 收支途径
     */
    private String payWay;

    /**
     * etc卡号
     */
    private String etcNumber;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 是否油老板提现:0-非油老板  1-是油老板
     */
    private Integer isOilPay;

    /**
     * 0-未被报表收集 1-已经收集
     */
    private Integer isReported;

    /**
     * 报表收集日期
     */
    private LocalDateTime reportDate;

    /**
     * 报表核销金额
     */
    private Long reportAmount;

    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;

    /**
     * 发生时间
     */
    private LocalDateTime happenDate;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 收款人用户类型
     */
    private Integer userType;


    private LocalDateTime createDate;

}
