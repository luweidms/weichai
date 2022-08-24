package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2021/12/24
 */
@Data
public class InvitationDto implements Serializable {

     /**
      * recordType  0我邀请的   1邀请我的
      */
    private Integer recordType;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 盛情类型
     */
    private Integer applyType;

    /**
     *车牌号码
     */
    private String plateNumber;

    /**
     *归属车队
     */
    private String tenantName;

    /**
     *车队手机
     */
    private String linkPhone;

    /**
     *申请车辆类型
     */
    private Integer applyVehicleClass;

    /**
     *状态0 : 处理中  1 : 已通过 2：被驳回 3：平台仲裁中 4：平台仲裁通过 5：平台仲裁未通过
     */
    private Integer state;

    /**
     *司机手机
     */
    private String driverMobile;

    /**
     *司机姓名
     */
    private String driverName;
}
