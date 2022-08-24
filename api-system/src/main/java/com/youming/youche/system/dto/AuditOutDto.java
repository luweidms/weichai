package com.youming.youche.system.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuditOutDto implements Serializable {
    private Long busId;

    private String auditUserName;

    private Boolean isAuditJurisdiction;

    private Boolean isFinallyNode;
}
