package com.youming.youche.system.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantOrUserBindCardDto implements Serializable {

    private static final long serialVersionUID = -4895593750470048412L;

    private Integer driverVer;

    private String result;

}
