package com.youming.youche.record.vo.tenant;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/14 13:44
 */
@Data
public class TenantVehicleRelQueryVo implements Serializable {

    /**
     * 创建时间
     */
    private List<String> monList;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 司机
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人名称
     */
    private String billReceiverName;

    /**
     * 司机ID
     */
    private List<String> driverUserId;

    /**
     * 车辆Id
     */
    private List<String> vehicleCode;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 滤掉的车牌
     */
    private List<String> notInPlateNumber;
}
