package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: luona
 * @date: 2022/5/20
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class QueryDriverOrderDto implements Serializable {
    private String plateNumber;
    private String tenantName;
    private LocalDateTime dependTime;
    private Long orderId;

}
