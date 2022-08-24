package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class EventOrderInfoDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long orderId;
    /**
     * 司机电话
     */
    private String carDriverPhone;

    /**
     * 车辆code
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 客户名称
     */
    private String customName;
    /**
     * 要求靠台时间
     */
    private Date dependTime;
    /**
     * 实际靠台时间
     */
    private Date carDependDate;
    /**
     * 离台时间
     */
    private Date carStartDate;

    /**
     * 离场时间
     */
    private Date carDepartureDate;

    /**
     * 车类型
     */
    private Integer vehicleClass;

    private String vehicleClassName;

    /**
     * 实际到达时间
     */
    private Date carArriveDate;
    /**
     * 到达时效
     */
    private Double arriveTime;
    /**
     * 出发地纬度
     */
    private Double nand;
    /**
     * 出发地经度
     */
    private Double eand;
    /**
     * 目的地纬度
     */
    private Double nandDes;
    /**
     * 目的地经度
     */
    private Double eandDes;
    /**
     * 订单状态
     */
    private Integer orderState;
    /**
     * 订单里程
     */
    private Long distance;
    /**
     * 目的城市
     */
    private Long desRegion;

    private String desRegionName;

    /**
     * 出发城市
     */
    private Long sourceRegion;

    private String sourceRegionName;

    /**
     * 主驾ID
     */
    private Long carDriverId;
    /**
     * 主驾姓名
     */
    private String carDriverMan;
    /**
     * 副驾ID
     */
    private Long copilotUserId;
    /**
     * 副驾姓名
     */
    private String copilotMan;
    /**
     * 副驾电话
     */
    private String copilotPhone;
    /**
     * 跟单人姓名
     */
    private String localUserName;
    /**
     * 跟单人电话
     */
    private String localPhone;

    /**
     * 挂车车牌
     */
    private String trailerNumber;
    /**
     * 挂车ID
     */
    private Long trailerId;
    /**
     * 部门Id
     */
    private Integer orgId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((arriveTime == null) ? 0 : arriveTime.hashCode());
        result = prime * result + ((carArriveDate == null) ? 0 : carArriveDate.hashCode());
        result = prime * result + ((carDepartureDate == null) ? 0 : carDepartureDate.hashCode());
        result = prime * result + ((carDependDate == null) ? 0 : carDependDate.hashCode());
        result = prime * result + ((carDriverPhone == null) ? 0 : carDriverPhone.hashCode());
        result = prime * result + ((carStartDate == null) ? 0 : carStartDate.hashCode());
        result = prime * result + ((dependTime == null) ? 0 : dependTime.hashCode());
        result = prime * result + ((distance == null) ? 0 : distance.hashCode());
        result = prime * result + ((eand == null) ? 0 : eand.hashCode());
        result = prime * result + ((eandDes == null) ? 0 : eandDes.hashCode());
        result = prime * result + ((localPhone == null) ? 0 : localPhone.hashCode());
        result = prime * result + ((localUserName == null) ? 0 : localUserName.hashCode());
        result = prime * result + ((nand == null) ? 0 : nand.hashCode());
        result = prime * result + ((nandDes == null) ? 0 : nandDes.hashCode());
        result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
        result = prime * result + ((orderState == null) ? 0 : orderState.hashCode());
        result = prime * result + ((plateNumber == null) ? 0 : plateNumber.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventOrderInfoDto other = (EventOrderInfoDto) obj;
        if (arriveTime == null) {
            if (other.arriveTime != null)
                return false;
        } else if (!arriveTime.equals(other.arriveTime))
            return false;
        if (carArriveDate == null) {
            if (other.carArriveDate != null)
                return false;
        } else if (!carArriveDate.equals(other.carArriveDate))
            return false;
        if (carDepartureDate == null) {
            if (other.carDepartureDate != null)
                return false;
        } else if (!carDepartureDate.equals(other.carDepartureDate))
            return false;
        if (carDependDate == null) {
            if (other.carDependDate != null)
                return false;
        } else if (!carDependDate.equals(other.carDependDate))
            return false;
        if (carDriverPhone == null) {
            if (other.carDriverPhone != null)
                return false;
        } else if (!carDriverPhone.equals(other.carDriverPhone))
            return false;
        if (carStartDate == null) {
            if (other.carStartDate != null)
                return false;
        } else if (!carStartDate.equals(other.carStartDate))
            return false;
        if (dependTime == null) {
            if (other.dependTime != null)
                return false;
        } else if (!dependTime.equals(other.dependTime))
            return false;
        if (distance == null) {
            if (other.distance != null)
                return false;
        } else if (!distance.equals(other.distance))
            return false;
        if (eand == null) {
            if (other.eand != null)
                return false;
        } else if (!eand.equals(other.eand))
            return false;
        if (eandDes == null) {
            if (other.eandDes != null)
                return false;
        } else if (!eandDes.equals(other.eandDes))
            return false;
        if (localPhone == null) {
            if (other.localPhone != null)
                return false;
        } else if (!localPhone.equals(other.localPhone))
            return false;
        if (localUserName == null) {
            if (other.localUserName != null)
                return false;
        } else if (!localUserName.equals(other.localUserName))
            return false;
        if (nand == null) {
            if (other.nand != null)
                return false;
        } else if (!nand.equals(other.nand))
            return false;
        if (nandDes == null) {
            if (other.nandDes != null)
                return false;
        } else if (!nandDes.equals(other.nandDes))
            return false;
        if (orderId == null) {
            if (other.orderId != null)
                return false;
        } else if (!orderId.equals(other.orderId))
            return false;
        if (orderState == null) {
            if (other.orderState != null)
                return false;
        } else if (!orderState.equals(other.orderState))
            return false;
        if (plateNumber == null) {
            if (other.plateNumber != null)
                return false;
        } else if (!plateNumber.equals(other.plateNumber))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EventOrderInfoDto [orderId=" + orderId + ", carDriverPhone=" + carDriverPhone + ", plateNumber="
                + plateNumber + ", customName=" + customName + ", dependTime=" + dependTime + ", carDependDate="
                + carDependDate + ", carStartDate=" + carStartDate + ", carDepartureDate=" + carDepartureDate
                + ", vehicleClass=" + vehicleClass + ", vehicleClassName=" + vehicleClassName + ", carArriveDate="
                + carArriveDate + ", arriveTime=" + arriveTime + ", nand=" + nand + ", eand=" + eand + ", nandDes="
                + nandDes + ", eandDes=" + eandDes + ", orderState=" + orderState + ", distance=" + distance
                + ", desRegion=" + desRegion + ", desRegionName=" + desRegionName + ", sourceRegion=" + sourceRegion
                + ", sourceRegionName=" + sourceRegionName + ", carDriverId=" + carDriverId + ", carDriverMan="
                + carDriverMan + ", copilotUserId=" + copilotUserId + ", copilotMan=" + copilotMan + ", copilotPhone="
                + copilotPhone + ", localUserName=" + localUserName + ", localPhone=" + localPhone + ", trailerNumber="
                + trailerNumber + ", trailerId=" + trailerId + ", orgId=" + orgId + "]";
    }


}
