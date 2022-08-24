package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TenantServiceDto  implements Serializable {
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
    private Integer serviceType;

    /**
     * 用户编码
     */
    private Long serviceUserId;

    /**
     * 车队关系ID
     */
    private Long relId;

    /**
     * 服务商类型名称
     */
    private String serviceTypeName;


}
