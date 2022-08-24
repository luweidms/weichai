package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 *  上报费用明细  返回值
 *  聂杰伟
 *  2022-4-22
 */
@Data
public class OrderMainReportDto implements Serializable {

    /**
     *  OrderMainReport 主键
     */
    private  Long id;
    /**
     * 上报时间
     */
    private String subTime;
    /**
     * 上报金额
     */
    private  Long consumeFee;
    /**
     * 消费类型名称
     */
    private  String typeName;
    /**
     * 消费类型id
     */
    private  Long typeId ;
    /**
     * 付款方式 1:油卡 2:现金   暂时没有 3:ETC卡'
     */
    private   Integer  paymentWay;

    /**
     * 订单号
     */
    private  String orderId;
    /**
     *  卡号
     */
    private  String cardNo;
    /**
     * 加油公里数
     */
    private   Long  oilMileage;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 附件id
     */
    private  Long fileId1;

    private  Long fileId2;

    private  Long fileId3;
    private  Long fileId4;
    private  Long fileId5;

    /**
     * 附件路径
     */
    private  String fileUrl1;

    private  String fileUrl2;

    private  String fileUrl3;
    private  String fileUrl4;
    private  String fileUrl5;

    /**
     * 加油公里数
     */
    private String  oilMileageStr;

    /**
     * 上报金额
     */
    private Double  consumeFeeStr;
    /**
     * 付款方式 1:油卡 2:现金   暂时没有 3:ETC卡'
     */
    private  String paymentWayStr;
    /**
     * 司机姓名
     */
    private  String userName;
}
