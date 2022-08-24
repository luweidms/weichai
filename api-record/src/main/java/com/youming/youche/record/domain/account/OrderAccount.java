package com.youming.youche.record.domain.account;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 订单账户表
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderAccount extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 帐户状态 0无效（冻结账户）；1正常
     */
    private Integer accState;

    /**
     * 可用金额
     */
    private Long balance;

    /**
     * 未到期金额
     */
    private Long marginBalance;

    /**
     * 油卡金额
     */
    private Long oilBalance;

    /**
     * ETC金额
     */
    private Long etcBalance;

    /**
     * 预支金额
     */
    private Long beforePay;

    /**
     * 维修基金
     */
    private Long repairFund;

    /**
     * 欠款金额
     */
    private Long debtBalance;

    /**
     * 应收逾期金额
     */
    private Long receivableOverdueBalance;

    /**
     * 应付逾期金额
     */
    private Long payableOverdueBalance;

    /**
     * 充值油
     */
    private Long rechargeOilBalance;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账户级别
     */
    private Integer accLevel;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 金币余额
     */
    private Long goldBalance;

    /**
     * 积分余额
     */
    private Long integraBalance;

    /**
     * 账户名称
     */
    private String acctName;

    /**
     * 招商车/自有车加油消费金额
     */
    private Long businessOrOwnCarPayAmount;

    /**
     * 社会车加油消费金额
     */
    private Long socialCarPayAmount;

    /**
     * 自由资金
     */
    private Long freeAmount;

    /**
     * 内置资金
     */
    private Long internalAmount;

    /**
     * 调拨资金
     */
    private Long switchAmount;

    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;

    /**
     * 油卡抵押金额
     */
    private Long pledgeOilcardFee;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 资金来源租户id
     */
    private Long sourceTenantId;

    /**
     * 密码
     */
    private String accPassword;

    /**
     * 修改密码时间
     */
    private LocalDateTime modPwdtime;

    /**
     * 资金(油)渠道类型
     */
    private String oilAffiliation;

    /**
     * 收款人用户类型
     */
    private Integer userType;


}
