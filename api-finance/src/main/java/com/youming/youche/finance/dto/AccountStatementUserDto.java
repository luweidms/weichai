package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/25 15:07
 */
@Data
public class AccountStatementUserDto implements Serializable {

    /**
     * 代收人ID
     */
    private Long billReceiverUserId;

    /**
     * 代收人手机
     */
    private String billReceiverMobile;

    /**
     * 代收人名称
     */
    private String billReceiverName;
}
