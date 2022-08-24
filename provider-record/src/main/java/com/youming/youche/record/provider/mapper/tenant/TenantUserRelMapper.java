package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 租户会员关系Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface TenantUserRelMapper extends BaseMapper<TenantUserRel> {

    List<TenantUserRel> getTenantUserRels(@Param("userId")Long userId,
                                          @Param("tenantId")Long tenantId);

    List<TenantUserRel> getTenantUserRelList(@Param("userId")Long userId,
                                             @Param("tenantId")Long tenantId,
                                             @Param("carUserType")Integer carUserType);
}
