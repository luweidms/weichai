package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderSchedulerVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 抢单轮次（目前总共为三次）
     */
    private Integer appointCount;
    /**
     * 指派方式
     */
    private Integer appointWay;
    /**
     * 到达时限  单位小时
     */
    private Float arriveTime;
    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;
    /**
     * 账单接收人姓名
     */
    private String billReceiverName;
    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;
    /**
     * 实际到达时间
     */
    private LocalDateTime carArriveDate;
    /**
     * 离场时间
     */
    private LocalDateTime carDepartureDate;
    /**
     * 实际靠台时间
     */
    private LocalDateTime carDependDate;
    /**
     * 主驾ID
     */
    private Long carDriverId;
    /**
     * 司机姓名
     */
    private String carDriverMan;
    /**
     * 司机手机
     */
    private String carDriverPhone;
    /**
     * 车长
     */
    private String carLengh;
    /**
     * 离台时间
     */
    private LocalDateTime carStartDate;
    /**
     * 车辆种类
     */
    private Integer carStatus;
    /**
     * 车辆种类
     */
    @TableField(exist = false)
    private String carStatusName;
    /**
     * 用户类型
     */
    private Integer carUserType;
    /**
     * 校验到达时间
     */
    private LocalDateTime checkArriveTime;
    /**
     * 校验出发时间
     */
    private LocalDateTime checkGoingTime;
    /**
     * 校准时间
     */
    private LocalDateTime checkTime;
    /**
     * 线路合同ID
     */
    private Long clientContractId;
    /**
     * 线路合同URL
     */
    private String clientContractUrl;
    /**
     * 代收人
     */
    private Long collectionUserId;
    /**
     * 代收人名称
     */
    private String collectionUserName;
    /**
     * 代收人手机
     */
    private String collectionUserPhone;
    /**
     * 副驾驶姓名
     */
    private String copilotMan;
    /**
     * 副驾驶手机
     */
    private String copilotPhone;
    /**
     * 副驾驶id
     */
    private Long copilotUserId;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 会员等级
     */
    private Integer creditLevel;
    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;
    /**
     * 调度时间
     */
    private LocalDateTime dispatchTime;
    /**
     * 调度员手机
     */
    private String dispatcherBill;
    /**
     * 调度员ID
     */
    private Long dispatcherId;
    /**
     * 调度员名字
     */
    private String dispatcherName;
    /**
     * 行驶距离
     */
    private Long distance;
    /**
     * 是否代收 0 不代收 1 代收
     */
    private Integer isCollection;
    /**
     * 是否为紧急加班车（1 是的 0 不是 ）
     */
    private Integer isUrgent;
    /**
     * 车辆类型(整车/拖头)
     */
    private Integer licenceType;
    /**
     * 线路信息:供应商公里数
     */
    private Integer mileageNumber;
    /**
     * 值班司机
     */
    private Long onDutyDriverId;
    /**
     * 值班司机名称
     */
    private String onDutyDriverName;
    /**
     * 值班司机手机
     */
    private String onDutyDriverPhone;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 订单编号
     */
    private Long orderId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 收款方
     */
    private String receiveUser;
    /**
     * 回单地址
     */
    private String reciveAddr;
    /**
     * 定标时间
     */
    private LocalDateTime scalingTime;
    /**
     * 线路编码
     */
    private String sourceCode;
    /**
     * 线路ID
     */
    private Long sourceId;
    /**
     * 线路名称
     */
    private String sourceName;
    /**
     * 账户归属租户id
     */
    private Long tenantId;
    /**
     * 挂车ID
     */
    private Long trailerId;
    /**
     * 挂车车牌
     */
    private String trailerPlate;
    /**
     * 修改数据的操作人id
     */
    private Long updateOpId;
    /**
     * 车辆类型
     */
    private Integer vehicleClass;
    /**
     * 车辆编码
     */
    private Long vehicleCode;
    /**
     * 校验到达时间
     */
    private LocalDateTime verifyArriveDate;
    /**
     * 校验到达附件ID
     */
    private Long verifyArriveFileId;
    /**
     * 校验到达附件URL
     */
    private String verifyArriveFileUrl;
    /**
     * 校验离场时间
     */
    private LocalDateTime verifyDepartureDate;
    /**
     * 校验离场附件ID
     */
    private Long verifyDepartureFileId;
    /**
     * 校验离场附件URL
     */
    private String verifyDepartureFileUrl;
    /**
     * 校验靠台时间
     */
    private LocalDateTime verifyDependDate;
    /**
     * 校验靠台附件ID
     */
    private Long verifyDependFileId;
    /**
     * 校验靠台附件URL
     */
    private String verifyDependFileUrl;
    /**
     * 校验离台时间
     */
    private LocalDateTime verifyStartDate;
    /**
     * 校验离台附件ID
     */
    private Long verifyStartFileId;
    /**
     * 校验离台附件URL
     */
    private String verifyStartFileUrl;
    /**
     * 部门id
     */
    private Long departmentId;
    /**
     * 部门名称
     */
    private String departmentName;
    /**
     * 车长
     */
    @TableField(exist = false)
    private String carLenghName;
    /**
     * 代收人名称
     */
    @TableField(exist = false)
    private String collectionName;
    /**
     * 失败原因
     */
    @TableField(exist = false)
    private String failure;
    /**
     * 车辆类型
     */
    @TableField(exist = false)
    private String vehicleClassName;

}
