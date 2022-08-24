package com.youming.youche.order.dto.monitor;

import java.io.Serializable;
import java.util.Date;

/**
 * 盘点信息
 *
 * @author hzx
 */
public class CheckInfoDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 盘点项目编码
     */
    private String checkCode;
    /**
     * 盘点项目名称
     */
    private String checkName;

    /**
     * 上次年审
     */
    private Date annualVeriTime;

    /**
     * 上次季审
     */
    private Date seasonalVeriTime;

    /**
     * 上次保险时间
     */
    private Date insuranceTime;

    /**
     * 上次保养时间
     */
    private Date prevMaintainTime;

    /**
     * 行驶证有效期
     */
    private Date vehicleValidityTime;

    /**
     * 营运证有效期
     */
    private Date operateValidityTime;

    /**
     * 备用时间
     */
    private double spareHour;

    /**
     * 余下天数
     */
    private double remainingDay;

    /**
     * 余下公里数
     */
    private double remainingMileage;

    /**
     * 保养周期
     */
    private double maintainDis;

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public Date getAnnualVeriTime() {
        return annualVeriTime;
    }

    public void setAnnualVeriTime(Date annualVeriTime) {
        this.annualVeriTime = annualVeriTime;
    }

    public Date getSeasonalVeriTime() {
        return seasonalVeriTime;
    }

    public void setSeasonalVeriTime(Date seasonalVeriTime) {
        this.seasonalVeriTime = seasonalVeriTime;
    }

    public Date getInsuranceTime() {
        return insuranceTime;
    }

    public void setInsuranceTime(Date insuranceTime) {
        this.insuranceTime = insuranceTime;
    }

    public Date getPrevMaintainTime() {
        return prevMaintainTime;
    }

    public void setPrevMaintainTime(Date prevMaintainTime) {
        this.prevMaintainTime = prevMaintainTime;
    }

    public Date getVehicleValidityTime() {
        return vehicleValidityTime;
    }

    public void setVehicleValidityTime(Date vehicleValidityTime) {
        this.vehicleValidityTime = vehicleValidityTime;
    }

    public Date getOperateValidityTime() {
        return operateValidityTime;
    }

    public void setOperateValidityTime(Date operateValidityTime) {
        this.operateValidityTime = operateValidityTime;
    }

    public double getSpareHour() {
        return spareHour;
    }

    public void setSpareHour(double spareHour) {
        this.spareHour = spareHour;
    }

    public double getRemainingDay() {
        return remainingDay;
    }

    public void setRemainingDay(double remainingDay) {
        this.remainingDay = remainingDay;
    }

    public double getRemainingMileage() {
        return remainingMileage;
    }

    public void setRemainingMileage(double remainingMileage) {
        this.remainingMileage = remainingMileage;
    }

    public double getMaintainDis() {
        return maintainDis;
    }

    public void setMaintainDis(double maintainDis) {
        this.maintainDis = maintainDis;
    }

}
