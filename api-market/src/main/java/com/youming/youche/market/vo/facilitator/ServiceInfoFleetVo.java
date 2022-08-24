package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ServiceInfoFleetVo implements Serializable {
    /**
     * 关系主键
     */
    private Long relId;

    private Long serviceUserId;

    private Integer hasVer;
    /**
     * 登录账号
     */
    private String loginAcct;
    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 企业注册住所
     */
    private String companyAddress;
    /**
     * tenantid车队id
     */
    private Long sourceTenantId;

    /**
     * 联系人姓名
     */
    private String linkman;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    private String serviceTypeName;

    //合作车队数
    private Integer productNum;

    /**
     * 认证状态
     */
    private Integer state;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;

    private String authStateName;

    private Boolean isBindCard;

    private String bindTypeName;

    /**
     * 审核原因
     */
    private String authReason;
    /**
     * 邀请状态，1待处理，2已通过，3被驳回
     */
    private Integer invitationState;

    private String invitationStateName;

    private String stateName;

    private Integer isAuth;

    private LocalDateTime createTime;

    private Long tenantId;
}
