package com.youming.youche.order.vo;

import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author hzx
 * @date 2022/3/28 11:01
 */
@Data
public class PayArriveChargeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;//用户id
    private Long orderId;//订单号
    private Long arriveFee;//到付款
    private List<OrderProblemInfo> problemInfos;//异常扣减对象集合
    private List<OrderAgingInfo> agingInfos;//时效罚款对象集合
    private Long tenantId;//租户id

}
