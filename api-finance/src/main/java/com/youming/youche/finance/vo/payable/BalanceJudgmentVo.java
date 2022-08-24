package com.youming.youche.finance.vo.payable;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/25 15:04
 */
@Data
public class BalanceJudgmentVo implements Serializable {

    /**
     * 1:对公  2:对私
     */
    private Integer type;

    /**
     * 付款方用户id
     */
    private Long userId;

    /**
     * 付款金额
     */
    private Long balance;

    /**
     * 付款卡号
     */
    private String payPinganPayAcctId;

    /**
     * 用户类型
     */
    private Integer userType;
}
