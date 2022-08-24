package com.youming.youche.table.domain.vehicleReport;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.finance.commons.util.CommonUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.text.DecimalFormat;

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
public class VehicleReport extends BaseDomain implements Serializable {

    /**
     * 部门
     */
    @TableField(exist = false)
    private Long orgId;
    @TableField(exist = false)
    private String orgName;

    /**
     * 当前线路
     */
    private String currentLine;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String carNumber;

    /**
     * 月份 （yyyy-MM）
     */
    private String mouth;

    /**
     * 费用合计 = 车辆折旧费+维修费+保养费+油费+路桥费+车辆事故+违章罚款+杂费+车辆年审+车辆保险
     */
    @TableField(exist = false)
    private Double totalExpenses;

    /**
     * 车辆折旧费 (购车价格 - 残值后 / 折旧月数 * 当前已过折旧月)
     */
    private Long vehicleDepreciation;
    @TableField(exist = false)
    private Double vehicleDepreciationDouble;

    /**
     * 维修费
     */
    private Long maintenanceCost;
    @TableField(exist = false)
    private Double maintenanceCostDouble;

    /**
     * 保养费
     */
    private Long maintenanceCosts;
    @TableField(exist = false)
    private Double maintenanceCostsDouble;

    /**
     * 油费
     */
    private Long oilCost;
    @TableField(exist = false)
    private Double oilCostDouble;

    /**
     * 设备油耗，对接车联网设备获取
     */
    private Long equipmentFuelConsumption;
    @TableField(exist = false)
    private Double equipmentFuelConsumptionDouble;

    /**
     * 设备里程，对接车联网设备获取
     */
    private Long equipmentMileage;
    @TableField(exist = false)
    private Double equipmentMileageDouble;

    /**
     * 路桥费
     */
    private Long roadAndBridgeFee;
    @TableField(exist = false)
    private Double roadAndBridgeFeeDouble;

    /**
     * 车辆事故
     */
    private Long vehicleAccident;

    /**
     * 违章罚款
     */
    private Long penaltyForViolation;
    @TableField(exist = false)
    private Double penaltyForViolationDouble;

    /**
     * 杂费
     */
    private Long incidental;
    @TableField(exist = false)
    private Double incidentalDouble;

    /**
     * 车辆年审
     */
    private Long annualVehicleReview;

    /**
     * 车辆保险
     */
    private Long vehicleInsurance;

    /**
     * 车辆收入
     */
    private Long vehicleRevenue;
    @TableField(exist = false)
    private Double vehicleRevenueDouble;

    /**
     * 车辆毛利 = 车辆收入-费用合计
     */
    @TableField(exist = false)
    private Double vehicleGrossProfit;

    /**
     * 车辆毛利率 = 车辆毛利/车辆收入
     */
    @TableField(exist = false)
    private String grossProfitMarginOfVehicle;

    private Long tenantId;

    public Double getVehicleGrossProfit() {
        vehicleGrossProfit = getVehicleRevenueDouble() - getTotalExpenses();
        return (double) Math.round(vehicleGrossProfit * 100) / 100;
    }

    public String getGrossProfitMarginOfVehicle() {
        Double grossProfitMarginOfVehicleDouble = getVehicleGrossProfit() / getVehicleRevenueDouble();
        if(Double.isInfinite(grossProfitMarginOfVehicleDouble)){
            return "";
        }
        if (grossProfitMarginOfVehicleDouble != null) {
            grossProfitMarginOfVehicleDouble = (double) Math.round(grossProfitMarginOfVehicleDouble * 100);
            DecimalFormat df = new DecimalFormat("#");
            return df.format(grossProfitMarginOfVehicleDouble) + "%";
        }
        return 0 + "%";
    }

    public Double getTotalExpenses() {
        totalExpenses = getVehicleDepreciationDouble() +
                getMaintenanceCostDouble() +
                getMaintenanceCostsDouble() +
                getOilCostDouble() +
                getRoadAndBridgeFeeDouble() +
                getVehicleAccident() +
                getPenaltyForViolation() +
                getIncidentalDouble() +
                getAnnualVehicleReview() +
                getVehicleInsurance();
        return (double) Math.round(totalExpenses * 100) / 100;
    }


    public Double getVehicleDepreciationDouble() {
        if (getVehicleDepreciation() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getVehicleDepreciation(), 2);
    }

    public Double getMaintenanceCostDouble() {
        if (getMaintenanceCost() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getMaintenanceCost(), 2);
    }

    public Double getMaintenanceCostsDouble() {
        if (getMaintenanceCosts() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getMaintenanceCosts(), 2);
    }

    public Double getOilCostDouble() {
        if (getOilCost() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getOilCost(), 2);
    }

    public Double getRoadAndBridgeFeeDouble() {
        if (getRoadAndBridgeFee() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getRoadAndBridgeFee(), 2);
    }

    public Double getPenaltyForViolationDouble() {
        if (getPenaltyForViolation() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getPenaltyForViolation(), 2);
    }

    public Double getIncidentalDouble() {
        if (getIncidental() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getIncidental(), 2);
    }

    public Double getVehicleRevenueDouble() {
        if (getVehicleRevenue() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getVehicleRevenue(), 2);
    }

    public Double getEquipmentFuelConsumptionDouble() {
        if (getEquipmentFuelConsumption() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getEquipmentFuelConsumption(), 2);
    }

    public Double getEquipmentMileageDouble() {
        if (getEquipmentMileage() == 0) {
            return 0.0;
        }
        return CommonUtil.getDoubleFormatLongMoney(getEquipmentMileage(), 2);
    }

}
