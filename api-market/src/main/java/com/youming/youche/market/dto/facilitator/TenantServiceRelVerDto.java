package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantServiceRelVerDto implements Serializable {
    private String loginAcct; // 服务商账号
    private String serviceName; // 服务商名称
    private String linkman; // 联系人
    private Integer serviceType; // 服务商发类型
}
