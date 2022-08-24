package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCountDto implements Serializable {

    private static final long serialVersionUID = -2095111467277052658L;

    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 车队数量
     */
    private Long cont;

}
