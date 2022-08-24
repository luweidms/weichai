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
* @author wuhao
* @since 2022-04-27
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class PlatformServiceCharge extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开票申请号
     */
            //@TableId(value = "flow_id", type = IdType.AUTO)
    private Long flowId;
    /**
     * 消费金额
     */
    private Long consumeAmount;

    private Long consumeFlowId;
    /**
     * 费用类型 1：司机消费  2：油老板收入
     */
    private Integer costType;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 是否核销，0未核销，1部分核销，2已核销
     */
    private Integer isVerification;
    /**
     * 手机号
     */
    private String mobilePhone;
    /**
     * 未核销平台金额(分)
     */
    private Long noVerificationAmount;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 平台服务费金额
     */
    private Long platformServiceCharge;
    /**
     *车队id
     */
    private Long tenantId;
    /**
     * 更新时间
     */
    private LocalDateTime updateDate;
    /**
     * 修改人
     */
    private Long updateOpId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 核销平台金额(分)
     */
    private Long verificationAmount;


}
