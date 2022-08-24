package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderProblemInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/20 17:12
 */
@Data
public class OrderProblemInfoOutDto extends OrderProblemInfo implements Serializable {

    private Double problemPriceDouble;//登记金额
    private Double problemDealPriceDouble;//处理金额
}
