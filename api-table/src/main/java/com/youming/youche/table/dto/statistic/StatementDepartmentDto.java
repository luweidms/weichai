package com.youming.youche.table.dto.statistic;

import lombok.Data;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 部门报表汇总
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@Data
public class StatementDepartmentDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自有收入
     */
    private String ownIncome;

    /**
     * 自有成本
     */
    private String ownCost;

    /**
     * 自有毛利
     */
    private String ownGrossMargin;

    /**
     * 外调收入
     */
    private String diversionIncome;

    /**
     * 外调成本
     */
    private String diversionCost;

    /**
     * 外调毛利
     */
    private String diversionGrossMargin;

    /**
     * 招商收入
     */
    private String merchantsIncome;

    /**
     * 招商成本
     */
    private String merchantsCost;

    /**
     * 招商毛利
     */
    private String merchantsGrossMargin;

    /**
     * 收入汇总
     */
    private String sumIncome;

    /**
     * 成本汇总
     */
    private String sumCost;

    /**
     * 总毛利润
     */
    private String sumMargin;

    /**
     * 总毛利率
     */
    private String sumMarginRate;

    /**
     * 油费
     */
    private String oilFee;

    /**
     * 路桥费
     */
    private String tollFee;

    /**
     * 补贴 （日报表）
     */
    private String subsidyFee;

    /**
     * 保费
     */
    private String insuranceFee;

    /**
     * 司机借支
     */
    private String driverCreditFee;

    /**
     * 司机报销
     */
    private String driverExpenseFee;

    /**
     * 司机工资 （月报表）
     */
    private String wageFee;

    /**
     * 维保费
     */
    private String maintenanceFee;

    /**
     * 保养费
     */
    private String maintainFee;

    /**
     * 停车费
     */
    private String parkingFee;

    /**
     * 杂费
     */
    private String miscellaneousFee;

    /**
     * 车辆年审
     */
    private String vehicleInspectionFee;

    /**
     * 车辆事故
     */
    private String vehicleAccidentFee;

    /**
     * 车辆违章
     */
    private String vehicleViolationFee;

    /**
     * 员工借支
     */
    private String employeesCreditFee;

    /**
     * 现金
     */
    private String cash;

    /**
     * 创建日期
     */
    private LocalDate createDate;

    /**
     * 月份
     */
    private String monthStr;

    /**
     * 日报明细
     */
    private List<StatementDepartmentDetailDto> statementDepartmentDays;


    /**
     * 总毛利润=收入汇总-成本汇总
     */
    public String getSumMargin() {
        if (sumIncome != null && sumCost != null) {
            DecimalFormat df2 = new DecimalFormat("#0.00");
            this.sumMargin = df2.format(Double.valueOf(this.sumIncome) - Double.valueOf(this.sumCost));
        } else {
            sumMargin = "0.00";
        }
        return sumMargin;
    }

    /**
     * 总毛利率=总毛利润/收入汇总
     */
    public String getSumMarginRate() {
        if (sumMargin != null && sumIncome != null && !sumMargin.equals("0.00")) {
            DecimalFormat df2 = new DecimalFormat("#0.00%");
            df2.setRoundingMode(RoundingMode.HALF_UP);
            if(sumIncome != null && Double.parseDouble(sumIncome) != 0) {
                this.sumMarginRate = df2.format(Double.valueOf(this.sumMargin) / Double.valueOf(this.sumIncome));
            }else{
                this.sumMarginRate = "";
            }
        }else{
            sumMarginRate = "";
        }
        return sumMarginRate;
    }

    public static void main(String[] args) {
        DecimalFormat df2 = new DecimalFormat("#0.00%");
        df2.setRoundingMode(RoundingMode.HALF_UP);
        System.out.println(df2.format(Double.valueOf(-21.00) / Double.valueOf(21.00)));
    }

}
