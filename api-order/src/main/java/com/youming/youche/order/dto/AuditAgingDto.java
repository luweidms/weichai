package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuditAgingDto  implements Serializable {
    /**
     * 时效罚款id
     */
    private  Long agingId;
    /**
     * 审核备注
     */
    private String verifyDesc;
}
