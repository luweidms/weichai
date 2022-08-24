package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayForOilInDto  implements Serializable {

    private static final long serialVersionUID = -3623122504536910183L;
    private Long userId;//消费用户编号
    private Long serviceUserId;//服务商用户编号
    private Long productId;//产品id
    private Long oilPrice;//油价
    private Float oilRise; //油升数
    private Long amountFee; //加油金额
    private Long objId;
    private Long tenantId;
    private String plateNumber; //车牌号
    private String localeBalanceState;//是否现场价加油 0不是，1是
    private Integer fromType;//1扫码加油，2找油网
    private String orderNum;
    private long id;
}
