package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderMainReport extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 审核备注
            */
    private String auditRemark;

            /**
            * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
            */
    private Integer isAudit;

            /**
            * 是否曾经审核通过 0否 1是 枚举SysStaticDataEnum.IS_AUTH
            */
    private Integer isAuditPass;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 操作人
            */
    private String opName;

            /**
            * 订单ID
            */
    private Long orderId;

            /**
            * 上报状态 0未提交  3审核中 4审核不通过 5审核通过
            */
    private Integer state;

            /**
            * 提交时间
            */
    private LocalDateTime subTime;

            /**
            * 提交人
            */
    private Long subUserId;

            /**
            * 提交人
            */
    private String subUserName;

            /**
            * 租户ID
            */
    private Long tenantId;


}
