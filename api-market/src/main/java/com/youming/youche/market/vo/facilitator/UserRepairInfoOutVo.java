package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRepairInfoOutVo  implements Serializable {

    private static final long serialVersionUID = -8473155795004634079L;
    Long serviceUserId;
    String capitalChannel;
    Long productId;
    Long consumeFee;
    String payPasswd;
    String payCode;
    Integer priceStar;
    Integer qualityStar;
    Integer serviceStar;
    Integer isSure;
    Integer payWay;
    String driverDes;
    Long repairId;
    String repairCode;
    Long repairFund;
    Long payTenantId;
    /*
     * 当payWay支付方式为 2(自付-账户 )时，selectType:0代表同时勾选了维修基金和账户支付，1代表只勾选了账户支付,
     * 当payWay支付方式为 4(自付现金 )时，selectType:0代表同时勾选了维修基金，1没有勾选维修基金,
     * 其他payWay支付时传空值就行
     */
    String selectType;



}
