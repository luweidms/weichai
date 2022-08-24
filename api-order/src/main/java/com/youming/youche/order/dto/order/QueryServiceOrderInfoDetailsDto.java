package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/5/23 14:44
 */
@Data
public class QueryServiceOrderInfoDetailsDto implements Serializable {

    /**
     * 服务商订单ID
     */
    private Long serviceOrderId;

    /**
     * 找油网的订单id
     */
    private String zhaoYouOrderId;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 详细地址
     */
    private String productAddress;

    /**
     * 服务电话
     */
    private String productPhone;

    /**
     * 消费总金额
     */
    private Long oilFee;

    /**
     * 油余额
     */
    private Long oilBalance;

    /**
     * 现金余额
     */
    private Long cashBalance;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 加油升数
     */
    private Long oilLitre;

    /**
     * 订单状态:0 支付失败 1 支付成功 2 待支付 3 撤单 4 支付中
     */
    private Integer orderState;

    /**
     * 订单状态名称
     */
    private String orderStateName;

    /**
     * 订单类型: 1:扫码加油 2:找油网加油
     */
    private Integer orderType;

    /**
     * 评价服务
     */
    private Integer evaluateService;

    /**
     * 评价质量
     */
    private Integer evaluateQuality;

    /**
     * 评价价格
     */
    private Integer evaluatePrice;

    /**
     * 是否评价：0-可评价  1-已评价
     */
    private Integer isEvaluate;


}
