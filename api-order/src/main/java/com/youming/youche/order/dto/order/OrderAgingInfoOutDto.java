package com.youming.youche.order.dto.order;

import com.youming.youche.order.domain.order.OrderAgingInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/20 17:39
 */
@Data
public class OrderAgingInfoOutDto extends OrderAgingInfo implements Serializable {

    private Double finePriceDouble;//罚款金额
    private Double arriveTimeDouble;//到达时限
    private Double arriveHourDouble;//时限要求
}
