package com.youming.youche.capital.vo;

import lombok.Data;

import java.io.Serializable;


@Data
public class TenantServiceVo  implements Serializable {


    private static final long serialVersionUID = -8684803442555557109L;
    private long tenantId;

    private int agentServiceType;

    private String serviceName;

    private String quotaAmtType;


}
