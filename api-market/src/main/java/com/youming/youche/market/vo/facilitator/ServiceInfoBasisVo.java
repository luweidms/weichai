package com.youming.youche.market.vo.facilitator;

import lombok.Data;

import java.io.Serializable;

@Data
public class ServiceInfoBasisVo implements Serializable {
    /**
     * 登录账户
     */
    private String loginAcct;
    /**
     * 联系人姓名
     */
    private String linkman;

    private Long id;
    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;

    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;



}
