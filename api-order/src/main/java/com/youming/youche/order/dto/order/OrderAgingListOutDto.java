package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderAgingListOutDto implements Serializable {


    private static final long serialVersionUID = 8009742423251321451L;


    private Integer selectType;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 到达时限
     */
    private Long arriveTime;

    /**
     *
     */
    private Integer orderType;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 靠台时间
     */
    private String dependTime;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 时效罚款ID
     */
    private Long agingId;

    /**
     * 时效罚款申诉ID
     */
    private Long appealId;

    /**
     * 审核状态
     */
    private Integer auditState;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 说明
     */
    private String remark;

    /**
     * 审核状态名称
     */
    private String auditStateName;

    /**
     * 订单类型名称
     */
    private String orderTypeName;

    /**
     * 始发市名称
     */
    private String sourceRegionName;

    /**
     * 到达市名称
     */
    private String desRegionName;

    /**
     * 订单状态名称
     */
    private String orderStateName;

    /**
     * 选择类型名称
     */
    private String selectTypeName;

    /**
     *
     */
    private Boolean appealJurisdiction;

    /**
     *
     */
    private Boolean agingJurisdiction;

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
     * 时效罚款审核状态
     */
    private Integer agingSts;

    /**
     * 时效罚款申诉审核状态
     */
    private Integer appealSts;

    /**
     * 是否可申诉：1-不可申诉  0-可申诉
     */
    private Integer appealType;

    /**
     * 时效罚款审核状态名称
     */
    private String agingStsName;

    /**
     * 时效罚款申诉审核状态名称
     */
    private String appealStsName;

    /**
     * 责任人
     */
    private Long userId;

    /**
     * 责任人名称
     */
    private String userName;

    /**
     * 责任人手机
     */
    private String userPhone;

    /**
     * 线路节点
     */
    private String lineNode;

    /**
     * 订单总线路
     */
    private String orderLine;

    /**
     * 是否有经停城市
     */
    private Boolean isTransitLine;

    /**
     *
     */
    private Long sourceId;

    /**
     * 日志
     */
    private List<SysOperLogDto> operLogs;
}
