package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 应收账单
 *
 * @author hzx
 * @date 2022/2/8 16:25
 */
@Data
public class OrderBillInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int billSts; // 账单状态
    private String billStsName; // 账单状态名称
    private long costPrice; // 成本价格
    private Double costPriceDouble;// 成本价格
    private String billNumber; // 账单号
    private String createReceiptDate; // 收据创建时间
    private long confirmDiffAmount; // 差异金额
    private Double confirmDiffAmountDouble;// 差异金额
    private long confirmAmount; // 确认金额
    private Double confirmAmountDouble;// 确认金额
    private long incomeExceptionFee; // 异常款
    private Double incomeExceptionFeeDouble;// 异常款
    private long checkAmount; // 核销金额
    private Double checkAmountDouble;// 核销金额
    private String createDate; // 创建时间
    private String customName; // 客户名称
    private String receiptNumber; // 收据号
    private long getAmount; //实收
    private Double getAmountDouble;//实收
    private String creatorName; // 创建用户名称
    private int orderNum; // 订单号
    private Double ygsrDouble; //预估收入=预估应收+异常款（收入异常） costPrice +  incomeExceptionFee
    private Double qrsrDouble;  // 确认收入=预估收入+确认异常    不能为负数，最小是0 ygsrDouble +  confirmDiffAmount
    private Long rootOrgId; // 组织ID
    private String rootOrgIdName; // 组织名称
    private Double syCheckDouble; //剩余核销金额
    private String openBillName ;//开票人
    private String checkBillName;//核销人
    private String checkBillDate ;//核销时间
    private String customCategory;//客户归类
    private String customerName;//客户简称
    private String checkName;//对账名称
    private Long orgId; // 组织ID
    private String orgIdName; // 组织名称
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
    private String openBillDate; // 开单日期
    private Long realIncome;//实收
    private Double realIncomeDouble;//实收

    public Double getSyCheckDouble() {
        setSyCheckDouble(getQrsrDouble() - getCheckAmountDouble());
        return syCheckDouble;
    }

    public Double getYgsrDouble() {
        setYgsrDouble(getIncomeExceptionFeeDouble() + getCostPriceDouble());
        return ygsrDouble;
    }

    public Double getQrsrDouble() {
        setQrsrDouble(getIncomeExceptionFeeDouble() + getCostPriceDouble() + getConfirmDiffAmountDouble());
        return qrsrDouble;
    }

    public Double getCostPriceDouble() {
        setCostPriceDouble(CommonUtil.getDoubleFormatLongMoney(costPrice, 2));
        return costPriceDouble;
    }

    public Double getConfirmDiffAmountDouble() {
        setConfirmDiffAmountDouble(CommonUtil.getDoubleFormatLongMoney(confirmDiffAmount, 2));
        return confirmDiffAmountDouble;
    }

    public Double getConfirmAmountDouble() {
        setConfirmAmountDouble(CommonUtil.getDoubleFormatLongMoney(confirmAmount, 2));
        return confirmAmountDouble;
    }

    public Double getIncomeExceptionFeeDouble() {
        setIncomeExceptionFeeDouble(CommonUtil.getDoubleFormatLongMoney(incomeExceptionFee, 2));
        return incomeExceptionFeeDouble;
    }

    public Double getCheckAmountDouble() {
        setCheckAmountDouble(CommonUtil.getDoubleFormatLongMoney(checkAmount, 2));
        return checkAmountDouble;
    }

    public Double getGetAmountDouble() {
        setGetAmountDouble(CommonUtil.getDoubleFormatLongMoney(getAmount, 2));
        return getAmountDouble;
    }

    public Double getCheckSumFee() {
        setCheckSumFee(checkFee1 + checkFee2+ checkFee3 + checkFee4 + checkFee5);
        return checkSumFee;
    }

    public Double getRealIncomeDouble() {
        setRealIncomeDouble(CommonUtil.getDoubleFormatLongMoney(realIncome, 2));
        return realIncomeDouble;
    }
}
