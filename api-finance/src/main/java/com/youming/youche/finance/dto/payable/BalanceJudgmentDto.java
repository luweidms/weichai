package com.youming.youche.finance.dto.payable;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/25 15:12
 */
@Data
public class BalanceJudgmentDto implements Serializable {

    private Integer state;

    private Double amount;
}
