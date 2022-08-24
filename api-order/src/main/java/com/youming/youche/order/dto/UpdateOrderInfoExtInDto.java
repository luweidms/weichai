package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderInfoExtInDto implements Serializable {
    private Integer paymentWay;
    private Integer isBackhaul;
    private Long backhaulPrice;
    private String backhaulLinkMan;
    private Long backhaulLinkManId;
    private String backhaulLinkManBill;
    private Float capacityOil;//空载油耗
    private Float runOil;//载重油耗
    private Long runWay;//空载距离
    /**
     * 自有车，油使用方式
     */
    private Integer oilUseType;
    /**
     * 自有车，油是否需要开票
     */
    private Integer oilIsNeedBill;

    private String oilAffiliation;
}
