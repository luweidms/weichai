package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class QueryPayManagerDto implements Serializable {
    private String busiCode;
    private Long payId;
    private String applyUserName;
    private LocalDateTime applyTime;
    private Long payAmt;
    private Long payType;
    /**
     * 备注
     */
    private String payRemark;
    private String receUserName;
    private String receBankName;
    private String receBankNo;
    private Long isNeedBiil;
    private String auditUserName;
    private LocalDateTime auditTime;
    private Long state;
    private String payTypeName;
    private String mobilePhone;
    private Integer isAudit;

}
