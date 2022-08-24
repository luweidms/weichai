package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 聂杰伟
 * 订单成本上报主表
 * </p>
 * @author Terry
 * @since 2022-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderMainReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private String auditRemark;//审核备注

    private Integer isAudit;//是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH

    private Integer isAuditPass;//是否曾经审核通过 0否 1是 枚举SysStaticDataEnum.IS_AUTH

    private Long opId;//操作人

    private String opName;//操作人

    private Long orderId;//订单ID

    private Integer state;//上报状态 0未提交  3审核中 4审核不通过 5审核通过

    private LocalDateTime subTime;//提交时间

    private Long subUserId;//提交id

    private String subUserName;//提交名称

    private Long tenantId;//租户ID


}
