package com.youming.youche.finance.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-03-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostDetailReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long amount;

    private String auditRemark;

    private String cardNo;

    private Long fileId;

    private Long fileId1;

    private Long fileId2;

    private String fileUrl;

    private String fileUrl1;

    private String fileUrl2;

    private Integer isAudit;

    private Long oilMileage;

    private String oilProductName;

    private Double oilRise;

    private Long orderId;

    private Integer paymentWay;

    private Long relId;

    private String remark;

    private Integer sortId;

    private Integer state;

    private LocalDateTime subTime;

    private Long subUserId;

    private String subUserName;

    private Integer tableType;

    private Long tenantId;

    private Long userId;

    @TableField(exist=false)
    private  String paymentWayName;

    @TableField(exist=false)
    private  String stateName;
}
