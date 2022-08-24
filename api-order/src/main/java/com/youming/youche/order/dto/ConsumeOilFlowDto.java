package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: luona
 * @date: 2022/4/24
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ConsumeOilFlowDto implements Serializable {
    private Long amount;
    /**
     * 平台服务费金额(分)
     */
    private Long platformAmount;
    //最多能加油升数(升)
    private String oilRise;


    //APP接口-优惠加油-加油记录 返回值
    private Long flowId;
    /**
     * 用戶id
     */
    private Long userId;
    private String productName;
    /**
     * 是否评价 0未评价 1已评价(费用类型为1才有)
     */
    private Integer isEvaluate;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 总费用
     */
    private  Long amountSum;
    /**
     * 总费用
     */
    private  Long amountSumL;
    private Long orderAmt;
    private Long oilAmt;

    private  Long suorceTenantId;
    /**
     * 油账户明细 返回 值
     */
    private  Long oilBalance;
}
