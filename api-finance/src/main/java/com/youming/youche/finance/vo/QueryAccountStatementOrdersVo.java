package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/27 15:39
 */
@Data
public class QueryAccountStatementOrdersVo implements Serializable {

    /**
     * 账单ID
     */
    private Long flowId;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 订单状态
     */
    private Integer orderState;

    /**
     * 是否代收
     */
    private Integer isCollection;

    /**
     * 尾款状态
     */
    private Integer finalState;

    /**
     * 客户名称
     */
    private String companyName;

    /**
     * 线路名称
     */
    private String sourceName;

    /**
     * 司机名称
     */
    private String carDriverMan;

    /**
     * 司机手机
     */
    private String carDriverPhone;

    /**
     * 收款人名称
     */
    private String payee;

    /**
     * 收款人手机
     */
    private String payeePhone;

    /**
     * 靠台开始时间
     */
    private String dependStartTime;

    /**
     * 靠台结束时间
     */
    private String dependEndTime;

    /**
     * 订单状态
     */
    private String orderStates;

    /**
     * 订单状态
     */
    private List<Integer> orderStateList;

}
