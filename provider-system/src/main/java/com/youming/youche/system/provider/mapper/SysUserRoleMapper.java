package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.system.domain.SysUserRole;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.OrganizeStaffDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统角色操作员关系表;ROLE_ID : 对应sys_role中的role_id;op_id : 对应sys_user中的operator_idMapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

	IPage<OrganizeStaffDto> selectAllByRoleIdAndTenantId(Page<SysUserRole> page, @Param("roleId") Long roleId,
			@Param("tenantId") Long tenantId, @Param("linkman") String linkman, @Param("loginacct") String loginacct,
			@Param("staffPosition") String staffPosition);

	int delByRoleIdAndUserIdAndUserIdAndTenantId(@Param("roleId") Long roleId, @Param("userIds") List<Long> userIds,
			@Param("tenantId") Long tenantId);

}
