package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class AuditServiceInfoDto implements Serializable {
   private Long serviceUserId; // 服务商用户编号
   private Boolean isPass; // 是否通过
   private String auditReason; // 审核意见
   private Long productId;
}
