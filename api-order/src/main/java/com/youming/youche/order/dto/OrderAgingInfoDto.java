package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class OrderAgingInfoDto  implements Serializable {
    private Long orderId;
    /**
     * 到达时限
     */
    private Float arriveTime;
    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 接单方的订单状态
     */
    private Integer orderState;

    private String appealStsName;

    private String orderStateName;

    private Integer appealSts;

    private String agingStsName;
    /**
     * 线路ID
     */
    private Long sourceId;

    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 始发市
     */
    private Integer sourceRegion;

    private String orderTypeName;


    private String sourceRegionName;


    /**
     * 到达市
     */
    private Integer desRegion;

    private String desRegionName;

    // '预估靠台时间',
    private String dependTime;

    private Integer selectType;


    private String plateNumber;//车牌号


    /**
     * 时效罚款id
     */
    private Long agingId;


    /**
     * 订单离台时间
     */
    private String orderStartDate;


    /**
     * 订单到达时间
     */
    private String orderArriveDate;

    /**
     * 时限要求
     */
    private Long arriveHour;

    /**
     * 罚款金额
     */
    private Long finePrice;


    /**
     * 审核状态：1-待审核2-审核通过3-审核不通过
     */
    private Integer auditState;

    private Boolean appealJurisdiction;

    private Long appealId;

    private String orderLine;//订单总线路

    private Boolean isTransitLine;//是否有经停城市

    private Boolean agingJurisdiction;

    private String auditStateName;

    private String selectTypeName;












    private Integer agingSts;





    /**
     * 用户编号
     */
    private Long userId;


    private String userName;


    private String userPhone;


    /**
     * 线路节点
     */
    private String lineNode;
}
