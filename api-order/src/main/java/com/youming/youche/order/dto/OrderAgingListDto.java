package com.youming.youche.order.dto;

import com.youming.youche.record.domain.sys.SysOperLog;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author: luona
 * @date: 2022/5/17
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderAgingListDto implements Serializable {
    private static final long serialVersionUID = -8889652512658458898L;

    private Integer selectType;
    private Long orderId;
    /**
     * 公司名称
     */
    private String companyName;
    /**
     * 到达时限
     */
    private Long arriveTime;
    private Integer orderType;
    private Integer sourceRegion;
    private Integer desRegion;
    private Date dependTime;
    private String plateNumber;
    /**
     * 时效罚款id
     */
    private Long agingId;
    private Long appealId;
    private Integer auditState;
    private Integer orderState;
    /**
     * 备注
     */
    private String remark;

    private String auditStateName;
    private String orderTypeName;
    private String sourceRegionName;
    private String desRegionName;
    private String orderStateName;
    private String selectTypeName;

    private Boolean appealJurisdiction;
    private Boolean agingJurisdiction;

    private LocalDateTime orderStartDate;
    private LocalDateTime orderArriveDate;
    private Long arriveHour;
    private Long finePrice;

    private Integer agingSts;
    private Integer appealSts;

    private Integer appealType;

    private String agingStsName;
    private String appealStsName;
    /**
     * 用户id
     */
    private Long userId;
    private String userName;
    private String userPhone;
    private String lineNode;

    private String orderLine;//订单总线路
    private Boolean isTransitLine;//是否有经停城市
    private Long sourceId;//始发地id
    private List<SysOperLog> operLogs;
}
