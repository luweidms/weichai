package com.youming.youche.order.dto;

import com.youming.youche.order.dto.order.OrderDriverSwitchInfoDto;
import com.youming.youche.order.dto.order.QueryOrderTitleBasicsInfoDto;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/20
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderDriverSwitchInfoOutDto implements Serializable {
    private OrderDriverSwitchInfoDto dto;
    private QueryOrderTitleBasicsInfoDto infoDto;
    /**
     * 切换状态：1-无切换记录 切换司机与当前登录账户司机一致  0-无切换记录  切换司机与当前登录账户司机不一致  2-有切换记录
     */
    private Integer switchType;
}
