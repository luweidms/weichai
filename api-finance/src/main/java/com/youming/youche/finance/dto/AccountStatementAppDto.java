package com.youming.youche.finance.dto;

import com.youming.youche.finance.domain.AccountStatement;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/19 11:22
 */
@Data
public class AccountStatementAppDto extends AccountStatement implements Serializable {

    /**
     * 状态名称
     */
    private String stateName;

    /**
     * 费用扣取方式名称
     */
    private String deductionTypeName;
}
