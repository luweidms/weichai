package com.youming.youche.market.vo.facilitator.criteria;

import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfoVos implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 账号（手机号
     */
    private String loginAcct;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 负责人
     */
    private String linkman;

}
