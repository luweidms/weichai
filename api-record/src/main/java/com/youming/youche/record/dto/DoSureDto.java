package com.youming.youche.record.dto;

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
    private Long flowId; // 主键
    private Integer actionState; // 审核状态
    private String auditContent; // 审核意见
}