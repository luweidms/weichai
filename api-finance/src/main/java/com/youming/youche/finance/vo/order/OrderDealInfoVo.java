package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 司机账户未到期明细入参
 *
 * @author zengwen
 * @date 2022/4/12 13:11
 */
@Data
public class OrderDealInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 始发地
     */
    private Long sourceRegion;

    /**
     * 目的地
     */
    private Long desRegion;

    /**
     * 到期开始时间
     */
    private String finalStartTime;

    /**
     * 到期结束时间
     */
    private String finalEndTime;

    /**
     * 业务类型
     */
    private Long businessNumber;

    /**
     * 回单开始时间
     */
    private String startTime;

    /**
     * 回单结束时间
     */
    private String endTime;

    private Long tenantId;

    private Integer pageNum;

    private Integer pageSize;


}
