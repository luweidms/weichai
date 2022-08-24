package com.youming.youche.table.dto.vehiclereport;

import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author wuhao
 * @since 2022-05-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleReportDto extends BaseDomain{

    /**
     * 部门
     */
    private Long orgId;
    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 当前线路
     */
    private Long currentLine;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 月份
     */
    private String mouth;

    /**
     * 费用合计
     */
    private Long totalExpenses;
    private Long totalExpensesDouble;

    /**
     * 车辆折旧费
     */
    private Long vehicleDepreciation;
    private Long vehicleDepreciationDouble;

    /**
     * 维修费
     */
    private Long maintenanceCost;
    private Long maintenanceCostDouble;

    /**
     * 保养费
     */
    private Long maintenanceCosts;
    private Long maintenanceCostsDouble;

    /**
     * 邮费
     */
    private Long oilCost;
    private Long oilCostDouble;

    /**
     * 设备油耗
     */
    private String equipmentFuelConsumption;

    /**
     * 设备里程
     */
    private String equipmentMileage;

    /**
     * 路桥费
     */
    private Long roadAndBridgeFee;
    private Long roadAndBridgeFeeDouble;

    /**
     * 车辆事故
     */
    private String vehicleAccident;

    /**
     * 违章罚款
     */
    private Long penaltyForViolation;
    private Long penaltyForViolationDouble;

    /**
     * 杂费
     */
    private Long incidental;
    private Long incidentalDouble;

    /**
     * 车辆年审
     */
    private String annualVehicleReview;

    /**
     * 车辆保险
     */
    private String vehicleInsurance;

    /**
     * 车辆收入
     */
    private Long vehicleRevenue;
    private Long vehicleRevenueDouble;

    /**
     * 车辆毛利
     */
    private Long vehicleGrossProfit;
    private Long vehicleGrossProfitDouble;

    /**
     * 车辆毛利率
     */
    private Long grossProfitMarginOfVehicle;
    private Long grossProfitMarginOfVehicleDouble;

    private Long tenantId;

    public double getTotalExpensesDouble() {
        if (null == getTotalExpenses()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getTotalExpenses(), 2);
    }

    public double getVehicleDepreciationDouble() {
        if (null == getVehicleDepreciation()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getVehicleDepreciation(), 2);
    }

    public double getMaintenanceCostDouble() {
        if (null == getMaintenanceCost()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getMaintenanceCost(), 2);
    }

    public double getMaintenanceCostsDouble() {
        if (null == getMaintenanceCosts()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getMaintenanceCosts(), 2);
    }

    public double getOilCostDouble() {
        if (null == getOilCost()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getOilCost(), 2);
    }

    public double getRoadAndBridgeFeeDouble() {
        if (null == getRoadAndBridgeFee()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getRoadAndBridgeFee(), 2);
    }

    public double getPenaltyForViolationDouble() {
        if (null == getPenaltyForViolation()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getPenaltyForViolation(), 2);
    }

    public double getIncidentalDouble() {
        if (null == getIncidental()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getIncidental(), 2);
    }

    public double getVehicleRevenueDouble() {
        if (null == getVehicleRevenue()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getVehicleRevenue(), 2);
    }

    public double getVehicleGrossProfitDouble() {
        if (null == getVehicleGrossProfit()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getVehicleGrossProfit(), 2);
    }

    public double getGrossProfitMarginOfVehicleDouble() {
        if (null == getGrossProfitMarginOfVehicle()) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getGrossProfitMarginOfVehicle(), 2);
    }
}
