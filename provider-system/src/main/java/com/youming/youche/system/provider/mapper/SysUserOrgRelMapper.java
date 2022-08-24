package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysUserOrgRel;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * <p>
 * 用户组织关系表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
public interface SysUserOrgRelMapper extends BaseMapper<SysUserOrgRel> {

	List<SysUserOrgRel> selectByTenantIdAndOrgIds(@Param("tenantId") Long tenantId, @Param("orgId") List<Long> orgIds);

	int deleteByOrgIdAndUserInfoIdAndTenantId(@Param("orgId") Long orgId, @Param("userIds") List<Long> userIds,
			@Param("tenantId") Long tenantId);

	int deleteByUserInfoIdAndTenantId( @Param("userIds") List<Long> userIds,
			@Param("tenantId") Long tenantId);

	List<String> getOrgNameByUserId(@Param("userId")Long userId);

}
