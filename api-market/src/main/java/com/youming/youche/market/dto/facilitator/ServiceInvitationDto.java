package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;
@Data
public class ServiceInvitationDto implements Serializable {
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 联系人
     */
    private String linkman;
    /**
     * 登录账户
     */
    private String loginAcct;
    /**
     * 申请开始日期
     */
    private String applyDateBegin;
    /**
     * 申请结束日期
     */
    private String applyDateEnd;
    /**
     *  '审核状态（1.未审核、2.审核通过、3.审核未通过）',
     */
    private Integer authState;
}
