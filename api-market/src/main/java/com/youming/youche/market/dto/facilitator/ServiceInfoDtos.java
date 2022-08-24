package com.youming.youche.market.dto.facilitator;

import com.youming.youche.market.domain.facilitator.UserDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceInfoDtos implements Serializable {

    /**
     * 服务商
     */
    private String serviceName;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 地址
     */
    private String companyAddress;

    private Long serviceUserId;

    /**
     * 登录账户
     */
    private String loginAcct;

    /**
     * 负责人
     */
    private String linkman;
}
