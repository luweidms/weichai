package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class QueryDriverOilByOrderIdVo implements Serializable {
    /**
     * 线路总费用
     */
    private Long sourceAmountSum;
    /**
     * 用户id
     */
    private Long userId;
}
