package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/14 11:12
 */
@Data
public class OrderInfoSalaryDto implements Serializable {

    private Long orderId;
    private Long estRunTime;
    private Long relRunTime;
}
