package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class LineOilDepotSchemeDataDto  implements Serializable {

    private Long id;

    /**
     * 油站id
     */
    private Long oilId;
    /**
     * 油站名称
     */
    private String oilName;

    private String oilPhone;
    /**
     * 消费人名
     */
    private String userName;

    /**
     * 详细地址
     */
    private String address;

    private String distance;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 油站经度
     */
    private String nand;

    /**
     * 沿途距离
     */
    private Long alongDistance;

    /**
     * 服务商
     */
    private String serviceName;

    /**
     * 油费单价
     */
    private Double oilPrice;

    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;







    private Long tenantId;


    private Integer provincePrice;

    private String floatBalanceBill;


    private Long fixedBalanceBill;


    private String serviceChargeBill;

    private Long fixedBalance;

    private String floatBalance;

    private String serviceCall;

    private String serviceCharge;

    private String linkman;

    private String productName;
}
