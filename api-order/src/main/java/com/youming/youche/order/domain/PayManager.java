package com.youming.youche.order.domain;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author caoyajie
* @since 2022-04-15
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PayManager extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;
    /**
     * 申请方id
     */
    private Long applyUserId;
    /**
     * 申请方
     */
    private String applyUserName;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    /**
     * 审核人id
     */
    private Long auditUserId;
    /**
     * 审核人
     */
    private String auditUserName;
    /**
     * 业务单号
     */
    private String busiCode;
    /**
     * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAudit;
    /**
     * 收款方开票
     */
    private Integer isNeedBiil;
    /**
     * 部門id
     */
    private Long orgId;
    /**
     *充值银行帐号
     */
    private String payAccNo;
    /**
     *付款金额
     */
    private Long payAmt;

            /**
            * 对应payout_intf的主键
            */
    private Long payNo;

            /**
            * 付款原因
            */
    private String payRemark;

            /**
            * 付款租户id
            */
    private Long payTenantId;

            /**
            * 付款类型
            */
    private Integer payType;

            /**
            * 付款类型名称
            */
    private String payTypeName;

            /**
            * 付款方id
            */
    private Long payUserId;

            /**
            * 付款方名称
            */
    private String payUserName;

            /**
            * 付款方类型
            */
    private Integer payUserType;

            /**
            * 收款方虚拟账号
            */
    private String receAccNo;

            /**
            * 收款方银行卡号
            */
    private String receBankName;

            /**
            * 收款方银行
            */
    private String receBankNo;

            /**
            * 付款方名称
            */
    private String receName;

            /**
            * 收款方id
            */
    private Long receUserId;

            /**
            * 收款方名称
            */
    private String receUserName;

            /**
            * 收款方类型
            */
    private Integer receUserType;

            /**
            * 状态 （0待审核、1审核不通过、2付款中、3已付款）
            */
    private Integer state;

            /**
            * 租户id
            */
    private String tenantId;

            /**
            * 收款方类型(会员体系)
            */
    private Integer userType;


}
