package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class OrderCostOtherReportVO implements Serializable {
    private static final long serialVersionUID=23245435435430999L;

    private Long id;
    private Long relId;

    private LocalDateTime updateTime;
    /**
     * 卡号
     */
    private String cardNo;
    private Long orderId;
    private Long consumeFee;
    /**
     * 单价
     */
    private Long priceUnit;
    private Long typeId;
    private String typeName;
    private int consumeNum;
    private Long fileId1;
    private String fileUrl1;
    private Long fileId2;
    private String fileUrl2;
    private Long fileId3;
    private String fileUrl3;
    private Long fileId4;
    private String fileUrl4;
    private Long fileId5;
    private String fileUrl5;
    private Long carNo;
    /**
     * 备注
     */
    private String remark;
    private Long tenantId;
    private int isAudit;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 用户id
     */
    private Long userId;
    private int state;
    private String stateName;
    private LocalDateTime subTime;
    private Date createTime;
    private Long subUserId;
    private String subUserName;
    /**
     * 加油升数
     */
    private Double oilRise;

    /**
     * 付款方式 1:油卡 2:现金 3:ETC卡
     */
    private Integer paymentWay;

    /**
     * 数据类型 1油费 2其他
     */
    private Integer tableType;

    /**
     * 油耗里程
     */
    private Long oilMileage;

    private String  oilMileageStr;

    /**
     * 临时油站名称
     */
    private String oilProductName;
    /**
     * 部门id
     */
    private Long orgId;
    public OrderCostOtherReportVO(Long id) {
        this.id=id;
    }
    public OrderCostOtherReportVO() {}
}
