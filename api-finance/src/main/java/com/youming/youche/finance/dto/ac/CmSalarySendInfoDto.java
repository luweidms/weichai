package com.youming.youche.finance.dto.ac;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/29 16:13
 */
@Data
public class CmSalarySendInfoDto implements Serializable {

    /**
     * 发送时间
     */
    private String sendTime;

    /**
     * 补贴金额
     */
    private Long subsidyFee;

    private Double subsidyFeeDouble;

    /**
     * 订单数
     */
    private Integer orderCount;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 状态名称
     */
    private String stateName;

    /**
     * 发送ID
     */
    private Long sendId;
}
