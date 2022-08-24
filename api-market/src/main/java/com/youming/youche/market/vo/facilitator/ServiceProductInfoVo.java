package com.youming.youche.market.vo.facilitator;


import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceProductInfoVo  implements Serializable  {
    /**
     * 站点ID
     */
    private Long productId;
    //站点名称
    private String productName;
    /**
     * 后服电话
     */
    private String servicePhone;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 是否认证
     */
    private Integer isAuth;

    //不通过的原因
    private String stateReason;
    //是否要审核 1 要审核  0 不要审核
    private Integer isAudit;

    /**
     *  认证
     */
    private Integer authState;

    private String serviceLoginAcct;
    //类型
    private String serviceTypeName;

    private String serviceName;
    /**
     *  '服务商类型（1.油站、2.维修、3.etc供应商）',
     */
    private Integer serviceType;

    private Long tenantId;
    //创建车队
    private String tenantName;
    //车队联系人
    private String tenantLinkMan;

    //车队联系电话
    private String tenantLinkPhone;
    //联系电话
    private String linkPhone;
    //认证
    private String authStateName;

    private Integer state;
    //状态
    private String stateName;
    /**
     * 是否共享（1.是，2.否）
     */
    private Integer isShare;
    //是否共享
    private String isShareName;


    /**
     * 理由
     */
    private String authReason;
    //联系人
    private String linkman;
    //日志id
    private Long relId;
    //合作车队数
    private Integer productNum;
    //地址
    private String address;

    private Long serviceUserId;

    //服务商审核状态
    private Integer serviceAuthState;

    private String serviceAuthStateName;



}
