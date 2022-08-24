package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgingInfoDto implements Serializable {

    /**
     * 订单到达时间
     */
    private String orderArriveDate;

    /**
     * 订单离台时间
     */
    private String orderStartDate;

    /**
     * 线路节点
     */
    private String lineNode;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 时效罚款ID
     */
    private Long id;

    /**
     * 到达时限
     */
    private String arriveTime;

    /**
     * 罚款金额
     */
    private String finePrice;

    /**
     * 说明
     */
    private String remark;

    /**
     * 时限要求
     */
    private String arriveHour;

    /**
     * 责任人
     */
    private Long  userId;

    /**
     * 责任人名称
     */
    private String userName;

    /**
     * 责任人手机
     */
    private String userPhone;

    /**
     * 地址纬度
     */
    private String nand;


    /**
     * 地址经度
     */
    private String eand;


    /**
     * 目的纬度
     */
    private String nandDes;


    /**
     * 目的经度
     */
    private String eandDes;


    /**
     * 始发市
     */
    private Integer sourceRegion;


    /**
     * 到达市
     */
    private Integer desRegion;
}
