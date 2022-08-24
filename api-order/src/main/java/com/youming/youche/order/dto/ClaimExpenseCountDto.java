package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/13
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class ClaimExpenseCountDto implements Serializable {
    private int count;//次數
    private Long amount;//費用
}
