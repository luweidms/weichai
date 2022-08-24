package com.youming.youche.finance.dto.order;

import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 10:03
 */
@Data
public class OrderInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;//订单号
    private String dependDate;//靠台时间
    private Integer paymentWay;//付款方式 1:智能模式 2:报账模式 3:承包模式
    private String companyName;//客户全称
    private String customName;//客户名称（简称）
    private Long carDriverId;//司机id
    private String checkName;//对账名称
    private String customerId;//客户回单号
    private String cutomerNum;//客户单号数量
    private Integer sourceRegion;//始发市
    private String sourceRegionName;//始发市
    private Integer desRegion;//到达市
    private String desRegionName;//到达市
    private Integer orderType;//订单类型
    private String orderTypeName;//订单类型
    private Long costPrice;//预估应收
    private Long incomeExceptionFee;//收入异常
    private Long diffFee;//差异金额
    private Long getAmount;//核销金额(实收金额)
    private String billNumber;//账单号
    private String receiptNumber;//发票号（查询列表无字段返回）
    private Integer orderState;//订单状态
    private String orderStateName;//订单状态
    private Integer financeSts;//核销状态
    private String financeStsName;//核销状态
    private Long customUserId;//客户ID
    private String plateNumber;//车牌号
    private Integer carStatus;//车辆种类  例如:箱车
    private String carStatusName;//车辆种类  例如:箱车
    private Integer vehicleClass;//车辆类型
    private String trailerPlate;//挂车号
    private Long totalFee;//中标价
    private String opName;//业务员
    private String carLengh;//车长
    private Long orgId;//订单归属部门
    private String orgName;
    private Long insuranceFee;//保费
    private Long fromOrderId;//来源订单
    private Long tenantId;
    private Long pontagePer;//路桥费
    private Long salary;//补贴
    //需计算
    private Long estimateIncomeFee;//预估收入 = 预估应收+异常款
    private Long affirmIncomeFee;//确认收入 = 预估收入+确认差异
    private Long handleFee;//应付运费 = 中标价 + 异常费 - 保费
    private Long marginFee;//毛利 = 确认收入 - 应付运费
    private String marginFeeString;//毛利率
    private Long preTotalFee;//预付总计金额
    private Long finalFee;//尾款现金
    private Long preCashFee;//预付现金
    private Long preOilVirtualFee;//预付虚拟油卡金额
    private Long preOilFee;//油卡金额
    private Long preEtcFee;//预付etc

    //需要外部赋值
    private String customerCategory;//客户分类
    private String customerAbbreviation;//客户简称
    private Long billDiff;//对账差异
    private Long kplDiff;//KPL差异
    private Long oilFeeDiff;//油价差异
    private Long billIngDiff;//开单差异
    private Long otherDiff;//其他差异
    private String openBillDate;//开票时间
    private String openBillName;//开票人
    private String checkBillName;//核销人
    private String checkBillDate;//核销时间
    private String billCreatorName;//账单创建人
    private String billCreatorDate;//账单时间

    private Double totalFeeDouble;//中标价
    private Double costPriceDouble;//预估应收
    private Double incomeExceptionFeeDouble;//收入异常
    private Double diffFeeDouble;//差异金额
    private Double getAmountDouble;//核销金额(实收金额)
    private Double estimateIncomeFeeDouble;//预估收入 = 预估应收+异常款
    private Double affirmIncomeFeeDouble;//确认收入 = 预估收入+确认差异
    private Double handleFeeDouble;//应付运费 = 中标价 + 异常费  - 保费
    private Double marginFeeDouble;//毛利 = 确认收入 - 应付运费
    private Double billDiffDouble;//对账差异
    private Double kplDiffDouble;//KPL差异
    private Double oilFeeDiffDouble;//油价差异
    private Double billIngDiffDouble;//开单差异
    private Double otherDiffDouble;//其他差异
    private Long realIncome;//实收
    private Double realIncomeDouble;//实收
    private String receiptNumbers;//发票号（返回：[发票号:999,金额:2,000.00],[发票号:888,金额:1,000.00]）
    private String sourceName;//线路名称
    private String customNumber;//客户单号

    private String carDependDate;//实际靠台时间
    private String carStartDate;//实际离台时间
    private String carArriveDate;//实际到达时间

    private String carType;//车型

    private String goodsInfo;//货物信息

    // 智能模式 -- 预估成本
    private Long intelligent;

    // 报账模式 -- 总成本
    private Long reimbursement;

    public Long getEstimateIncomeFee() {
        setEstimateIncomeFee((getCostPrice() == null ? 0 : getCostPrice()) + (getIncomeExceptionFee() == null ? 0 : getIncomeExceptionFee()));
        return estimateIncomeFee;
    }

    public Long getAffirmIncomeFee() {
        setAffirmIncomeFee((getDiffFee() == null ? 0 : getDiffFee()) + (getEstimateIncomeFee() == null ? 0 : getEstimateIncomeFee()));
        return affirmIncomeFee;
    }

    /**
     * 1:智能模式 2:报账模式 3:承包模式
     * 成本计算：
     * 承保模式：应付运费 = 中标价 + 异常费 - 保费
     * 智能模式：预估成本 + 异常费
     * 报账模式：总成本 + 异常费
     */
    public Long getHandleFee() {
        if (null != getOrderId()) {
            if (null != getPaymentWay()) {
                if (getPaymentWay() == 3) {
                    setHandleFee((getTotalFee() == null ? 0 : getTotalFee()) + (getIncomeExceptionFee() == null ? 0 : getIncomeExceptionFee()) - (getInsuranceFee() == null ? 0 : getInsuranceFee()));
                }
                if (getPaymentWay() == 1) {
                    setHandleFee((getIntelligent() == null ? 0 : getIntelligent()) + (getIncomeExceptionFee() == null ? 0 : getIncomeExceptionFee()));
                }
                if (getPaymentWay() == 2) {
                    setHandleFee((getReimbursement() == null ? 0 : getReimbursement()) + (getIncomeExceptionFee() == null ? 0 : getIncomeExceptionFee()));
                }
            } else {
                setHandleFee((getTotalFee() == null ? 0 : getTotalFee()) + (getIncomeExceptionFee() == null ? 0 : getIncomeExceptionFee()) - (getInsuranceFee() == null ? 0 : getInsuranceFee()));

            }
        }
        return handleFee;
    }

    /**
     * 毛利 = 确认收入 - 成本（应付运费）
     */
    public Long getMarginFee() {
        if (null != getOrderId()) {
            setMarginFee((getAffirmIncomeFee() == null ? 0 : getAffirmIncomeFee()) - (getHandleFee() == null ? 0 : getHandleFee()));
        }
        return marginFee;
    }

    public String getMarginFeeString() {
        if (null != getOrderId()) {
            DecimalFormat df = new DecimalFormat("#.##");
            setMarginFeeString(getMarginFee() == null ? "0%" : (getAffirmIncomeFee() == null ? "0" : (getAffirmIncomeFee() == 0 ? "0" : df.format((getMarginFee() * 1.0 / getAffirmIncomeFee() * 1.0) * 100)) + "%"));
        }
        return marginFeeString;
    }

    public Double getTotalFeeDouble() {
        if (getTotalFee() != null) {
            setTotalFeeDouble(CommonUtil.getDoubleFormatLongMoney(getTotalFee(), 2));
        } else {
            setTotalFeeDouble(0.0);
        }
        return totalFeeDouble;
    }

    public Double getCostPriceDouble() {
        if (getCostPrice() != null) {
            setCostPriceDouble(CommonUtil.getDoubleFormatLongMoney(getCostPrice(), 2));
        } else {
            setCostPriceDouble(0.0);
        }
        return costPriceDouble;
    }

    public Double getIncomeExceptionFeeDouble() {
        if (getIncomeExceptionFee() != null) {
            setIncomeExceptionFeeDouble(CommonUtil.getDoubleFormatLongMoney(getIncomeExceptionFee(), 2));
        } else {
            setIncomeExceptionFeeDouble(0.0);
        }
        return incomeExceptionFeeDouble;
    }

    public Double getDiffFeeDouble() {
        if (getDiffFee() != null) {
            setDiffFeeDouble(CommonUtil.getDoubleFormatLongMoney(getDiffFee(), 2));
        } else {
            setDiffFeeDouble(0.0);
        }
        return diffFeeDouble;
    }

    public Double getGetAmountDouble() {
        if (getGetAmount() != null) {
            setGetAmountDouble(CommonUtil.getDoubleFormatLongMoney(getGetAmount(), 2));
        } else {
            setGetAmountDouble(0.0);
        }
        return getAmountDouble;
    }

    public Double getEstimateIncomeFeeDouble() {
        if (getEstimateIncomeFee() != null) {
            setEstimateIncomeFeeDouble(CommonUtil.getDoubleFormatLongMoney(getEstimateIncomeFee(), 2));
        } else {
            setEstimateIncomeFeeDouble(0.0);
        }
        return estimateIncomeFeeDouble;
    }

    public Double getAffirmIncomeFeeDouble() {
        if (getAffirmIncomeFee() != null) {
            setAffirmIncomeFeeDouble(CommonUtil.getDoubleFormatLongMoney(getAffirmIncomeFee(), 2));
        } else {
            setAffirmIncomeFeeDouble(0.0);
        }
        return affirmIncomeFeeDouble;
    }

    public Double getHandleFeeDouble() {
        if (getHandleFee() != null) {
            setHandleFeeDouble(CommonUtil.getDoubleFormatLongMoney(getHandleFee(), 2));
        } else {
            setHandleFeeDouble(0.0);
        }
        return handleFeeDouble;
    }

    public Double getMarginFeeDouble() {
        if (getMarginFee() != null) {
            setMarginFeeDouble(CommonUtil.getDoubleFormatLongMoney(getMarginFee(), 2));
        } else {
            setMarginFeeDouble(0.0);
        }
        return marginFeeDouble;
    }

    public Double getBillDiffDouble() {
        if (getBillDiff() != null) {
            setBillDiffDouble(CommonUtil.getDoubleFormatLongMoney(getBillDiff(), 2));
        } else {
            setBillDiffDouble(0.0);
        }
        return billDiffDouble;
    }

    public Double getKplDiffDouble() {
        if (getKplDiff() != null) {
            setKplDiffDouble(CommonUtil.getDoubleFormatLongMoney(getKplDiff(), 2));
        } else {
            setKplDiffDouble(0.0);
        }
        return kplDiffDouble;
    }

    public Double getOilFeeDiffDouble() {
        if (getOilFeeDiff() != null) {
            setOilFeeDiffDouble(CommonUtil.getDoubleFormatLongMoney(getOilFeeDiff(), 2));
        } else {
            setOilFeeDiffDouble(0.0);
        }
        return oilFeeDiffDouble;
    }

    public Double getBillIngDiffDouble() {
        if (getBillIngDiff() != null) {
            setBillIngDiffDouble(CommonUtil.getDoubleFormatLongMoney(getBillIngDiff(), 2));
        } else {
            setBillIngDiffDouble(0.0);
        }
        return billIngDiffDouble;
    }

    public Double getOtherDiffDouble() {
        if (getOtherDiff() != null) {
            setOtherDiffDouble(CommonUtil.getDoubleFormatLongMoney(getOtherDiff(), 2));
        } else {
            setOtherDiffDouble(0.0);
        }
        return otherDiffDouble;
    }

    public Double getRealIncomeDouble() {
        setRealIncomeDouble(CommonUtil.getDoubleFormatLongMoney(realIncome, 2));
        return realIncomeDouble;
    }

    public String getCarDependDate() {

        return carDependDate;
    }

    public String getCarStartDate() {
        return carStartDate;
    }

    public String getCarArriveDate() {
        return carArriveDate;
    }

    private Integer balanceType; // 结算方式

    private String createTime; // 全款 1 创建时间

    private Integer collectionTime; // 收款期限 预付+尾款账期 2

    private String updateTime; // 审核通过后的时间

    private Integer collectionMonth; // 预付+尾款月结 3 收款月

    private Integer collectionDay; // 预付+尾款月结 3 收款天

    private String carDependDateT; // 靠台时间

    private String receivableDate; // 应收日期

}
