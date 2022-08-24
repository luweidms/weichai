package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.record.dto.UserSalaryDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 租户自有司机收入信息Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface TenantUserSalaryRelMapper extends BaseMapper<TenantUserSalaryRel> {

    List<UserSalaryDto> doQueryUserSalaryList(@Param("userId") Long userId, @Param("tenantId") Long tenantId);
}
