package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/28 11:10
 */
@Data
public class OrderFeeDto implements Serializable {

    private Long orderId;

    private Long fee;
}
