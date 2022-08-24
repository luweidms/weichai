package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class CustomerDto implements Serializable {
    /**
     * 客户单号
     */
    private Long customerId;
    /**
     * 公司名称(全称)
     */
    private String companyName;

    /**
     * 客户简称
     */
    private String customerName;


    /**
     * 联系人名称
     */
    private String lineName;


    /**
     * 线路联系手机
     */
    private String lineTel;

    /**
     * 常用办公地址
     */
    private String address;
    /**
     * 发票抬头
     */
    private String lookupName;


    /**
     * 财务编码
     */
    private String yongyouCode;

    /**
     * 回单地址的省份id
     */
    private Long reciveProvinceId;


    /**
     * 回单地址-市
     */
    private Long reciveCityId;


    /**
     * 回单详细地址
     */
    private String reciveAddress;

}
