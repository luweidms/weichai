package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;

/**
 * 异常监控-各事件车辆统计输出类</p >
 *
 * @author hzx
 */
@Data
public class EventsStaticDataDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 事件编号
     */
    private String eventCode;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 车辆数量
     */
    private int vehicleCount;

    public EventsStaticDataDto() {
    }

    public EventsStaticDataDto(String eventCode, String eventName, int vehicleCount) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.vehicleCount = vehicleCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((eventCode == null) ? 0 : eventCode.hashCode());
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
        EventsStaticDataDto other = (EventsStaticDataDto) obj;
        if (eventCode == null) {
            if (other.eventCode != null)
                return false;
        } else if (!eventCode.equals(other.eventCode))
            return false;
        return true;
    }

}
