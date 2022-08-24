package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class OrderPaymentWayOilOutDto implements Serializable {

    /*
     * 订单成本模式油
     */
    private Long costOil;
    /*
     * 订单实报实销模式油
     */
    private Long expenseOil;
    /*
     * 订单承包模式油
     */
    private Long contractOil;
}
