package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class TurnCashDto implements Serializable {
	
	private Long userId; //用户ID
	private String vehicleAffiliation;//资金渠道类型
	private Long canTurnMoney;//可转移金额
	private Double canTurnMoneyDouble;
	private Long orderMoney;//订单油卡或ETC总金额
	private Double orderMoneyDouble;
	private Long consumeMoney;//消费总金额
	private Double consumeMoneyDouble;
	private Long turnDiscount;//转移折扣
	private Double turnDiscountDouble;
	private String turnDiscountString;
	private Long oaLoanOilAmount;//借支油
	private Double oaLoanOilAmountDouble;
	private Long turnBalance;//已转金额
	private Double turnBalanceDouble;
	private Long deductibleMargin;//待抵扣欠款
	private Double deductibleMarginDouble;
	private Date createDate;// 创建时间

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getVehicleAffiliation() {
		return vehicleAffiliation;
	}
	public void setVehicleAffiliation(String vehicleAffiliation) {
		this.vehicleAffiliation = vehicleAffiliation;
	}
	public Long getCanTurnMoney() {
		return canTurnMoney;
	}
	public void setCanTurnMoney(Long canTurnMoney) {
		this.canTurnMoney = canTurnMoney;
	}
	public Double getCanTurnMoneyDouble() {
		return canTurnMoneyDouble;
	}
	public void setCanTurnMoneyDouble(Double canTurnMoneyDouble) {
		this.canTurnMoneyDouble = canTurnMoneyDouble;
	}
	public Long getOrderMoney() {
		return orderMoney;
	}
	public void setOrderMoney(Long orderMoney) {
		this.orderMoney = orderMoney;
	}
	public Double getOrderMoneyDouble() {
		return orderMoneyDouble;
	}
	public void setOrderMoneyDouble(Double orderMoneyDouble) {
		this.orderMoneyDouble = orderMoneyDouble;
	}
	public Long getConsumeMoney() {
		return consumeMoney;
	}
	public void setConsumeMoney(Long consumeMoney) {
		this.consumeMoney = consumeMoney;
	}
	public Double getConsumeMoneyDouble() {
		return consumeMoneyDouble;
	}
	public void setConsumeMoneyDouble(Double consumeMoneyDouble) {
		this.consumeMoneyDouble = consumeMoneyDouble;
	}
	public Long getTurnDiscount() {
		return turnDiscount;
	}
	public void setTurnDiscount(Long turnDiscount) {
		this.turnDiscount = turnDiscount;
	}
	public Double getTurnDiscountDouble() {
		return turnDiscountDouble;
	}
	public void setTurnDiscountDouble(Double turnDiscountDouble) {
		this.turnDiscountDouble = turnDiscountDouble;
	}
	public String getTurnDiscountString() {
		return turnDiscountString;
	}
	public void setTurnDiscountString(String turnDiscountString) {
		this.turnDiscountString = turnDiscountString;
	}
	public Long getOaLoanOilAmount() {
		return oaLoanOilAmount;
	}
	public void setOaLoanOilAmount(Long oaLoanOilAmount) {
		this.oaLoanOilAmount = oaLoanOilAmount;
	}
	public Double getOaLoanOilAmountDouble() {
		return oaLoanOilAmountDouble;
	}
	public void setOaLoanOilAmountDouble(Double oaLoanOilAmountDouble) {
		this.oaLoanOilAmountDouble = oaLoanOilAmountDouble;
	}
	public Long getTurnBalance() {
		return turnBalance;
	}
	public void setTurnBalance(Long turnBalance) {
		this.turnBalance = turnBalance;
	}
	public Double getTurnBalanceDouble() {
		return turnBalanceDouble;
	}
	public void setTurnBalanceDouble(Double turnBalanceDouble) {
		this.turnBalanceDouble = turnBalanceDouble;
	}
	public Long getDeductibleMargin() {
		return deductibleMargin;
	}
	public void setDeductibleMargin(Long deductibleMargin) {
		this.deductibleMargin = deductibleMargin;
	}
	public Double getDeductibleMarginDouble() {
		return deductibleMarginDouble;
	}
	public void setDeductibleMarginDouble(Double deductibleMarginDouble) {
		this.deductibleMarginDouble = deductibleMarginDouble;
	}

}
