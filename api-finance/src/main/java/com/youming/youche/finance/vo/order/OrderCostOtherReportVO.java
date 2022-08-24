package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class OrderCostOtherReportVO  implements Serializable {

    private Long id;
    private Long relId;
    private Long orderId;
    private Long consumeFee;
    private Long priceUnit;
    private Long typeId;
    private String typeName;
    private Integer consumeNum;
    private Long fileId1;
    private String fileUrl1;
    private Long fileId2;
    private String fileUrl2;
    private Long fileId3;
    private String fileUrl3;
    private String remark;
    private Long tenantId;
    private Integer isAudit;
    private String auditRemark;
    private Long userId;
    private Integer state;
    private String stateName;
    private Date subTime;
    private Date createTime;
    private Long subUserId;
    private String subUserName;
}
