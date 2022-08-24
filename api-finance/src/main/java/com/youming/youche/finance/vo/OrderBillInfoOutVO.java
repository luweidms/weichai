package com.youming.youche.finance.vo;



import lombok.Data;

import java.util.Date;

@Data
public class OrderBillInfoOutVO implements java.io.Serializable {
	private int billSts;
	private String billStsName;
	private long costPrice;
	private Double costPriceDouble;
	private String billNumber;
	private String createReceiptDate;
	private long confirmDiffAmount;
	private Double confirmDiffAmountDouble;
	private long confirmAmount;
	private Double confirmAmountDouble;
	private long incomeExceptionFee;
	private Double incomeExceptionFeeDouble;
	private long checkAmount;
	private Double checkAmountDouble;
	private Date createDate;
	private String customName;
	private String receiptNumber;
	private long getAmount;
	private Double getAmountDouble;
	private String creatorName;
	private int orderNum;
	private Double ygsrDouble; //预估收入=预估应收+异常款（收入异常） costPrice +  incomeExceptionFee
	private Double qrsrDouble;  // 确认收入=预估收入+确认异常    不能为负数，最小是0 ygsrDouble +  confirmDiffAmount
	private Long rootOrgId;
	private String rootOrgIdName;
	private Double syCheckDouble; //剩余核销金额
	private String openBillName ;//开票人
	private String checkBillName;//核销人
	private String checkBillDate ;//核销时间
	private String customCategory;//客户归类
	private String customerName;//客户简称
	private String checkName;//对账名称
	private Long orgId;
	private String orgIdName;
	private Double checkFee1 = 0.0;//现汇核销
	private Double checkFee2 = 0.0;//承兑核销
	private Double checkFee3 = 0.0;//冲抵核销
	private Double checkFee4 = 0.0;//扣款核销
	private Double checkFee5 = 0.0;//其它核销
	private Double checkSumFee;//核销总费用
	private Double diffFee1;//对账差异
	private Double diffFee2;//品质罚款
	private Double diffFee3;//油价差异
	private Double diffFee4;//开单差异
	private Double diffFee5;//其它差异
	private String openBillDate;
	private Long realIncome;//实收
	private Double realIncomeDouble;//实收
}
