package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class SureDto implements Serializable {
    private String serviceUserId; // 服务商ID
    private Boolean pass; // 状态 true启用 false 停用
    private String describe; // 原因描述
}
