package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryPayManagerVo implements Serializable {
    private String startTime;
    private String endTime;
    private String payTypeName;
    private Integer state;
    private String startAmt;
    private String endAmt;
    private String applyUserName;
    private String receName;
    private String receBankNo;
    private String busiCode;
    private Long payNo;
    private Boolean waitDeal;
    private String stateWX;
    private String mobilePhone;
    private List<Long> lids;
    private String [] payTypeNames;
    private String []stateWXs;
    private Boolean hasAllDataPermission;
    private List stateWXList;
    private List payTypeNameList;
    private List orgList;
    private Long tenantId;
}
