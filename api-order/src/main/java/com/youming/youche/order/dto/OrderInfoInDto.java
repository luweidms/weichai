package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class OrderInfoInDto extends BaseAcctInfoInDto implements Serializable {


    private static final long serialVersionUID = -8250940728622034858L;

    private Long orderId;//外系统订单编号
    private Long tenantId;//租户编号
    private String custName;//客户名称
    private String custLinkman;//客户联系人
    private String custLinkphone;//客户联系电话
    private String custAddress;//客户公司地址
    private String goodsName;//品名
    private Float goodsWeight;//重量:单位kg
    private Float goodsVolume;//体积:单位m³
    private String vehicleType;//车型  中文
    private Integer sourceProvince;//始发省份
    private Integer sourceCity;//始发城市
    private String sourceAddressDetail;//始发详细地址
    private String sourceNand;//始发导航纬度
    private String sourceEand;//始发导航经度
    private Integer desProvince;//目的省份
    private Integer desCity;//目的城市
    private String desAddressDetail;//目的详细地址
    private String desNand;//目的导航纬度
    private String desEand;//目的导航经度
    private Float distance;//运输距离:单位km
    private LocalDateTime dependDate;//靠台时间
    private Float arriveTime;//到达时限：单位小时
    private String receiveName;//收货人
    private String receivePhone;//收货人电话
    private String contractUrl;//承运合同URL
    private String receiptsUrl;//电子回单URL
    private String plateNumber;//车牌
    private Long driverUserId;//司机编号
    private String driverUserName;//司机名称
    private String driverPhone;//司机手机
    private LocalDateTime orderCreateDate;//录入时间
    private Long cashFee;//现金金额，分为单位
    private Long etcFee;//ETC金额，分为单位
    private Long entityOilFee;//实体油金额，分为单位
    private Long virtualOilFee;//虚拟油金额，分为单位
    private Long totalFee;//总计金额，分为单位

}
