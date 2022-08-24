package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateOrderGoodsInDto implements Serializable {
    private String goodsName;
    private Float square;
    private Float weight;
    private String reciveName;
    private String recivePhone;
    /**
     * 回单地址-市
     */
    private Long reciveCityId;
    /**
     * 回单地址-省
     */
    private Long reciveProvinceId;
    private String reciveAddr;
    private String source;
    private String des;
    private String addrDtl;
    private String nand;
    private String eand;
    private String nandDes;
    private String eandDes;
    private String desDtl;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatDesLocation;
    /**
     * 文字描述出发地址，不填写默认使用百度定位地址,做为百度地址的备注
     */
    private String navigatSourceLocation;
    private String contactPhone;
    private String contactName;
    private String customName;
    private Long customUserId;
    /**
     * 公司名称
     */
    private String companyName;
    private String customNumber;
    private Integer goodsType;
    private String linkName;
    private String linkPhone;
    /**
     * 地址
     */
    private String address;
    private Long localUser;
    private String localPhone;
    private String localUserName;
    /**
     * 回单类型
     */
    private Integer reciveType;
    private String vehicleLengh;
    private Integer vehicleStatus;
    /**
     * 联系人名称
     */
    private String lineName;
    /**
     * 联系人电话
     */
    private String linePhone;
}
