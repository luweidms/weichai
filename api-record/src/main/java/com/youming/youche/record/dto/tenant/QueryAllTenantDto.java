package com.youming.youche.record.dto.tenant;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/5/23 10:25
 */
@Data
public class QueryAllTenantDto implements Serializable {

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 租户联系人电话
     */
    private String linkPhone;

    /**
     * 车主（司机）类型（DRIVER_TYPE）
     */
    private Integer carUserType;

    /**
     * 类型名称
     */
    private String carUserTypeName;
}
