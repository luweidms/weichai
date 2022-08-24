package com.youming.youche.finance.vo.ac;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/21 15:46
 */
@Data
public class OrderListInVo implements Serializable {

    /**
     *
     */
    private Long salaryId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 开始靠台时间
     */
    private String beginDependTime;

    /**
     * 终止靠台时间
     */
    private String endDependTime;

    /**
     * 线路发起地市
     */
    private Integer sourceRegion;

    /**
     * 线路目的地市
     */
    private Integer desRegion;

    /**
     * 客户名称
     */
    private String customName;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 订单号
     */
    private String orderIds;

    /**
     * 订单号集合
     */
    private List<String> orderIdList;

}
