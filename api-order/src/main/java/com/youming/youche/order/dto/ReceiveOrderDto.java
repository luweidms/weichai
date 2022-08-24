package com.youming.youche.order.dto;

import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @program two
 * @description: 在线接单接口入参
 * @author: qinyunfeng
 * @create: 2022/03/20 10:08
 */
@Data
public class ReceiveOrderDto  implements Serializable {
    private  OrderDispatchInDto dispatchInfo;
    private OrderIncomeInDto incomeIn;
    private OrderPaymentDaysInfo dispatchBalanceData;
    private String remark;
}
