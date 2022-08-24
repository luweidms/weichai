package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 司机账户 账单明细
 *
 * @author zengwen
 * @date 2022/4/12 17:32
 */
@Data
public class AccountQueryDetailVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务编码
     */
    private Long businessNumber;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 操作开始时间
     */
    private String startTime;

    /**
     * 操作结束时间
     */
    private String endTime;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 业务月份
     */
    private String yyyyMonth;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 租户ID
     */
    private Long tenantId;

}
