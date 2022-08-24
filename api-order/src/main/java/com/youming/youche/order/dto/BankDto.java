package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class BankDto implements Serializable {
    /**
     * 状态
     */
    private String state;
    /**
     * 费用
     */
    private Double amount;
}
