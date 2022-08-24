package com.youming.youche.record.dto;

import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/6/15 15:26
 */
@Data
public class UserSalaryDto implements Serializable {

    private UserDataInfo userDataInfo; // 用户信息

    private TenantUserSalaryRel tenantUserSalaryRel; // 租户自有司机收入信息
}
