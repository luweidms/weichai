package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import lombok.Data;

@Data
public class OrderDriverSwitchInfoDto extends OrderDriverSwitchInfo {

    /**
     * 切换状态名称
     */
    private String stateName;

    /**
     * 原始里程
     */
    private Double formerMileageStr;

    /**
     * 接收里程
     */
    private Double receiveMileageStr;


}
