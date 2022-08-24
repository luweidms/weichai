package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class FacilitatorVo implements Serializable {
    //服务商id
    private Long serviceUserId;
    //服务商名称
    private String serviceName;
    //创建时间
    private String createDate;
    //类型
    private String serviceTypeName;
    //类型
    private Integer serviceType;
    //联系人
    private String linkman;
    //联系电话
    private String linkPhone;
    //创建租户
    private Long tenantId;
    //创建车队
    private String tenantName;
    //车队联系人
    private String tenantLinkMan;
    //车队联系电话
    private String tenantLinkPhone;
    //状态
    private String stateName;
    private Integer state;
    //认证
    private String authStateName;
    //认证
    private String authState;
    //理由
    private String authReason;
    //日志id
    private Long relId;
    //合作站点数
    private Integer productNum;
    //是否可以审核,1:不能审核，0：要审核
    private Integer hasVer;
    private String loginAcct;

    //是否认证，1认证，0未认证
    private Integer isAuth;
    //0未绑定  1 已绑定
    private Boolean isBindCard;
    private String bindTypeName;


}
