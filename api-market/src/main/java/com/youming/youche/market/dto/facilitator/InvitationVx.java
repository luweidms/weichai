package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class InvitationVx implements Serializable {
    private Long id; // 邀请ID
    private Integer authState; // 审核状态
    private String remark; // 理由
    private Integer cooperationType; // 申请合作类型 1：初次合作，2：修改合作'
    private String quotaAmt; // 授信金额
}
