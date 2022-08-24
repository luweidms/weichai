package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单对账调整入参
 *
 * @author hzx
 * @date 2022/2/16 14:46
 */
@Data
public class OrderDiffVo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 订单号
    private Long orderId;

    // 对账差异集合
    private List<OrderDiffFeils> listData;

}
