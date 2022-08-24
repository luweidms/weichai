package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/6/25
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderSalaryInfoVo implements Serializable {
    private String orderId;
    private LocalDateTime dependDate;
    private String customName;
    private String sourceName;
    private String plateNumber;
}
