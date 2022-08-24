package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ConsumeOilFlowDetailsOutDto implements Serializable {


    private static final long serialVersionUID = -1552998631134402953L;
    /**
     * 流水号
     */
    private String flowId;
    /**
     * 用戶id
     */
    private Long userId;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 产品名称
     */
    private String productName;
    /**
     * 油账户明细 返回 值
     */
    private Long oilBalance;
    /**
     * 可用金额
     */
    private Long balance;
    /**
     * 加油使用未到期金额(分)
     */
    private Long marginBalance;
    /**
     * 预支手续费(分)
     */
    private Long advanceFee;
    /**
     * 是否评价 0未评价 1已评价(费用类型为1才有)
     */
    private Integer isEvaluate;
    /**
     * 评价质量
     */
    private Integer evaluateQuality;
    /**
     * 评价价格
     */
    private Integer evaluatePrice;
    /**
     * 评价服务
     */
    private Integer evaluateService;
    /**
     * 創建时间
     */
    private Date createDate;
    /**
     * 服务电话
     */
    private String serviceCall;
    /**
     * 地址
     */
    private String address;
    /**
     * 核销金额（分）
     */
    private Long payAmount;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 服务商订单ID
     */
    private Long serviceOrderId;
    /**
     * 状态
     */
    private  String flag;
    /**
     * 总费用
     */
    private  Long amountSum ;
    private   List<OilAccountOutDto>  list;
}
