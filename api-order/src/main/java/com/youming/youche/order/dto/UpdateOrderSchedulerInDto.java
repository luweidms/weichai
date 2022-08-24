package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class UpdateOrderSchedulerInDto implements Serializable {

    private Long distance;
    /**
     * 车牌号
     */
    private String plateNumber;
    private Long vehicleCode;
    private Long vehicleClass;
    /**
     * 车长
     */
    private String carLengh;
    /**
     * 车辆种类
     */
    private Integer carStatus;
    /**
     * 司机类型
     */
    private Integer carUserType;
    private Long trailerId;
    private String trailerPlate;
    private Integer licenceType;
    private Integer creditLevel;
    /**
     * 到达时限
     */
    private Float arriveTime;
    private Integer isUrgent;
    private String reciveAddr;
    private String carDriverPhone;
    private Long carDriverId;
    private String carDriverMan;
    private String copilotMan;
    private String copilotPhone;
    private Long copilotUserId;
    /**
     * 承运方公里数
     */
    private Long mileageNumber;
    private Date dependTime;
    private String sourceCode;
    private String sourceId;
    private String sourceName;
    private Integer appointWay;

    private Long collectionUserId;
    private String collectionName;
    private String collectionUserName;
    private String collectionUserPhone;
    private Integer isCollection;

    public String billReceiverMobile;//账单接收人手机号
    public Long billReceiverUserId;//账单接收人用户编号
    public String billReceiverName;//账单接收人名称
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 部门名称
     */
    private String departmentName;
}
