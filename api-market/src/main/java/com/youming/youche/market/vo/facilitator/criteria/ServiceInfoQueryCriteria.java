package com.youming.youche.market.vo.facilitator.criteria;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfoQueryCriteria implements Serializable {
    /**
     * 供应商账号
     */
    private String loginAcct;
    /**
     * 服务商
     */
    private String serviceName;
    /**
     * 联系人
     */
    private String linkman;
    /**
     * 归属车队
     */
    private String tenantName;
    /**
     * 车队联系人
     */
    private String tenantLinkMan;
    /**
     *车队联系电话
     */
    private String tenantCall;
    /**
     * 开始时间
     */
    private String beginTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 认证状态
     */
    private Integer authState;
    /**
     * 有效状态
     */
    private Integer state;
    /**
     * 归属平台或者车队
     */
    private Integer whoseTenant;
    /**
     * 平台 不是必须的 后端传值
     */
    private Integer tenantId;

    /**
     * 服务商类型
     */
    private Integer serviceType;


    //是否可以审核,1:不能审核，0：要审核
    private Integer hasVer;


}
