package com.youming.youche.capital.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountOilFixedDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String serviceName;
    private String quotaAmtType;
    private int pageNum=1;
    private int pageSize=10;
}
