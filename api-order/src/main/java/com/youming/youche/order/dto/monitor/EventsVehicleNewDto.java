package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 异常车辆信息
 *
 * @author hzx
 * @date 2022/3/16 15:51
 */
@Data
public class EventsVehicleNewDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 司机电话
     */
    private String carDriverPhone;

    /**
     * 车牌号码
     */
    private String vehicleCode;
    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 事件编号
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 当前位置经度
     */
    private double currLongitude;

    /**
     * 当前位置纬度
     */
    private double currLatitude;

    /**
     * 是否有订单
     */
    private boolean hasOrderFlag;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 车帘类型
     */
    private int vehicleClass;

    /**
     * 车架号
     */
    private String vinNo;

}
