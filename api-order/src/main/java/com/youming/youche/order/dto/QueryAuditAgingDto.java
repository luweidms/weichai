package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryAuditAgingDto implements Serializable {
    private Long orderId;
    private Integer sourceRegion;
    private Integer desRegion;
    /**
     * 到达时限
     */
    private Long arriveTime;

    /**
     * 时限要求
     */
    private Long arriveHour;
    /**
     * 罚款金额
     */
    private Long finePrice;


    private Integer agingSts;


    private Integer appealSts;


    /**
     * 用户编号
     */
    private Long userId;


    private String userName;


    private String userPhone;

    /**
     * 时效罚款id
     */
    private Long agingId;


    private Long appealId;
    /**
     * 线路节点
     */
    private String lineNode;

    private String orderStartDate;
    private String orderArriveDate;
}
