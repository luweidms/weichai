package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销
 */
@Data
public class OrderCheckInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核销金额
     */
    private Long checkFee;

    /**
     * 核销创建时间
     */
    private String createTime;

}
