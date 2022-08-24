package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 事件详细信息
 *
 * @author hzx
 * @date 2022/3/16 16:26
 */
@Data
public class EventsInfoDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 当前位置经度
     */
    private double currLongitude;

    /**
     * 当前位置纬度
     */
    private double currLatitude;

    /**
     * 订单信息
     */
    private EventOrderInfoDto eventOrderInfo;

    /**
     * 车辆时效异常事件
     */
    private List<BaseEventsInfoDto> eventsInfoArr;

    /**
     * 车辆盘点信息
     */
    private CheckInfoDto checkInfo;

}
