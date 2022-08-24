package com.youming.youche.market.vo.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantServiceVo  implements Serializable {
    /**
     * 联系人手机
     */
    private String loginAcct;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 地址
     */
    private String companyAddress;
    /**
     * 联系人姓名
     */
    private String linkman;
    /**
     * '服务商类型（1.油站、2.维修、3.etc供应商）',
     */
    private String serviceType;

    private Long serviceUserId;

    private Long relId;



}
