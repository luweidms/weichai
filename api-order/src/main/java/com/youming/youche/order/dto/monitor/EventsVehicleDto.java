package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzx
 * @date 2022/3/10 20:14
 */
@Data
public class EventsVehicleDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 车牌号
     */
    private String plateNumber;

    private List<Object> monitorOrderAgings;

    /**
     * 当前位置经度
     */
    private double currLongitude = 121.55045;

    /**
     * 当前位置纬度
     */
    private double currLatitude = 31.227348;

    /**
     * 是否有订单
     */
    private Long orderId;

    private int vehicleClass;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 车架号
     */
    private String vinNo;

}
