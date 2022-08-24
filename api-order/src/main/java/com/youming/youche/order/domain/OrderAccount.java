package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @author CaoYaJie
 * @since 2022-03-18
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

    private String accPassword;

    private LocalDateTime modPwdtime;

    /**
     * 资金(油)渠道类型
     */
    private String oilAffiliation;

    /**
     * 收款人用户类型
     */
    private Integer userType;

    /**
     * 油卡抵押金额
     */
    @TableField(exist = false)
    private Long pledgeOilCardFee;


    @TableField(exist = false)
    private String accStateName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAccState() {
        return accState;
    }
    public String getAccStateName() {
        return accStateName;
    }
    public void setAccStateName(String accStateName) {
        this.accStateName = accStateName;
    }
    public void setAccState(Integer accState) {
        this.accState = accState;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Long getMarginBalance() {
        return marginBalance;
    }

    public void setMarginBalance(Long marginBalance) {
        this.marginBalance = marginBalance;
    }

    public Long getOilBalance() {
        return oilBalance;
    }

    public void setOilBalance(Long oilBalance) {
        this.oilBalance = oilBalance;
    }

    public Long getEtcBalance() {
        return etcBalance;
    }

    public void setEtcBalance(Long etcBalance) {
        this.etcBalance = etcBalance;
    }

    public Long getBeforePay() {
        return beforePay;
    }

    public void setBeforePay(Long beforePay) {
        this.beforePay = beforePay;
    }

    public Long getRepairFund() {
        return repairFund;
    }

    public void setRepairFund(Long repairFund) {
        this.repairFund = repairFund;
    }

    public Long getDebtBalance() {
        return debtBalance;
    }

    public void setDebtBalance(Long debtBalance) {
        this.debtBalance = debtBalance;
    }

    public Long getReceivableOverdueBalance() {
        return receivableOverdueBalance;
    }

    public void setReceivableOverdueBalance(Long receivableOverdueBalance) {
        this.receivableOverdueBalance = receivableOverdueBalance;
    }

    public Long getPayableOverdueBalance() {
        return payableOverdueBalance;
    }

    public void setPayableOverdueBalance(Long payableOverdueBalance) {
        this.payableOverdueBalance = payableOverdueBalance;
    }

    public Long getRechargeOilBalance() {
        return rechargeOilBalance;
    }

    public void setRechargeOilBalance(Long rechargeOilBalance) {
        this.rechargeOilBalance = rechargeOilBalance;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getAccLevel() {
        return accLevel;
    }

    public void setAccLevel(Integer accLevel) {
        this.accLevel = accLevel;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getGoldBalance() {
        return goldBalance;
    }

    public void setGoldBalance(Long goldBalance) {
        this.goldBalance = goldBalance;
    }

    public Long getIntegraBalance() {
        return integraBalance;
    }

    public void setIntegraBalance(Long integraBalance) {
        this.integraBalance = integraBalance;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public Long getBusinessOrOwnCarPayAmount() {
        return businessOrOwnCarPayAmount;
    }

    public void setBusinessOrOwnCarPayAmount(Long businessOrOwnCarPayAmount) {
        this.businessOrOwnCarPayAmount = businessOrOwnCarPayAmount;
    }

    public Long getSocialCarPayAmount() {
        return socialCarPayAmount;
    }

    public void setSocialCarPayAmount(Long socialCarPayAmount) {
        this.socialCarPayAmount = socialCarPayAmount;
    }

    public Long getFreeAmount() {
        return freeAmount;
    }

    public void setFreeAmount(Long freeAmount) {
        this.freeAmount = freeAmount;
    }

    public Long getInternalAmount() {
        return internalAmount;
    }

    public void setInternalAmount(Long internalAmount) {
        this.internalAmount = internalAmount;
    }

    public Long getSwitchAmount() {
        return switchAmount;
    }

    public void setSwitchAmount(Long switchAmount) {
        this.switchAmount = switchAmount;
    }

    public String getVehicleAffiliation() {
        return vehicleAffiliation;
    }

    public void setVehicleAffiliation(String vehicleAffiliation) {
        this.vehicleAffiliation = vehicleAffiliation;
    }

    public Long getPledgeOilcardFee() {
        return pledgeOilcardFee;
    }

    public void setPledgeOilcardFee(Long pledgeOilcardFee) {
        this.pledgeOilcardFee = pledgeOilcardFee;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Long getUpdateOpId() {
        return updateOpId;
    }

    public void setUpdateOpId(Long updateOpId) {
        this.updateOpId = updateOpId;
    }

    public Long getOpId() {
        return opId;
    }

    public void setOpId(Long opId) {
        this.opId = opId;
    }

    public Long getSourceTenantId() {
        return sourceTenantId;
    }

    public void setSourceTenantId(Long sourceTenantId) {
        this.sourceTenantId = sourceTenantId;
    }

    public String getAccPassword() {
        return accPassword;
    }

    public void setAccPassword(String accPassword) {
        this.accPassword = accPassword;
    }

    public LocalDateTime getModPwdtime() {
        return modPwdtime;
    }

    public void setModPwdtime(LocalDateTime modPwdtime) {
        this.modPwdtime = modPwdtime;
    }

    public String getOilAffiliation() {
        return oilAffiliation;
    }

    public void setOilAffiliation(String oilAffiliation) {
        this.oilAffiliation = oilAffiliation;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Long getPledgeOilCardFee() {
        return pledgeOilCardFee;
    }

    public void setPledgeOilCardFee(Long pledgeOilCardFee) {
        this.pledgeOilCardFee = pledgeOilCardFee;
    }
}
