package com.youming.youche.market.dto.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfoFleetDto implements Serializable {
    /**
     * 登录账号
     */
    private String loginAcct;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 联系人
     */
    private String linkman;
    /**
     * 服务商类型
     */
    private Integer serviceType;
    /**
     * 状态
     */
    private Integer state;

    private Integer authState;

    private Integer isAuth;

}
