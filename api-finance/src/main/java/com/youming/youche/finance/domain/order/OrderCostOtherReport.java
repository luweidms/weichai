package com.youming.youche.finance.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 订单费用上报其他项
 * </p>
 * @author Terry
 * @since 2022-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostOtherReport extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    private String auditRemark;

    private Long consumeFee;

    private Long consumeNum;

    private Long fileId1;

    private Long fileId2;

    private Long fileId3;

    private Long fileId4;

    private Long fileId5;

    private String fileUrl1;

    private String fileUrl2;

    private String fileUrl3;

    private String fileUrl4;

    private String fileUrl5;

    private Integer isAudit;

    private Long orderId;

    private Long priceUnit;

    private Long relId;

    private String remark;

    private Integer state;

    private LocalDateTime subTime;

    private Long subUserId;

    private String subUserName;

    private Long tenantId;

    private Long typeId;

    private String typeName;

    private Long userId;

    private String cardNo;
    /**
     * 加油升数
     */
    private Double oilRise;

    @TableField(exist=false)
    private  String stateName;
    @TableField(exist=false)
    private  String consumeFeeStr;
    @TableField(exist = false)
    private String  oilMileageStr;

    /**
     * 付款方式 1:油卡 2:现金 3:ETC卡
     */
    private Integer paymentWay;

    private Long oilMileage;
    /**
     * 数据类型 1油费 2其他
     */
    private Integer tableType;
}
