package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OaLoanAuditDto implements Serializable {
    private static final long serialVersionUID = -2912355553167044998L;
    private  String linkman; // 一级审核人
    private String linkman1 ; //二级审核人
    private String linkman2 ;//三级审核人
    private String linkman3 ;//四级审核人
    private String linkman4 ;//五级审核人
}
