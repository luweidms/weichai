package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/23
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class DoSureDto implements Serializable {
    /**
     * 流水号
     */
    private Long flowId;
    /**
     * 状态
     */
    private Integer actionState;
    /**
     * 审核原因
     */
    private String auditContent;
}
