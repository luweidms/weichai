package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.SysRoleDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统角色表;一个角色可以关联多个操作员，一个操作员可以有多个角色。;一个角色可以关联多个权限，一个权限可以被多个角色拥有。Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

	List<SysRole> getOwn(@Param("userId") Long id, @Param("tenantId") Long tenantId);


	IPage<SysRoleDto> selectAllByRoleNameAndOpNameAndTenantId(Page<SysRole> page,
															  @Param("roleName")String roleName,
															  @Param("opName")String opName,
															  @Param("tenantId")Long tenantId);

    Integer selectCountByUserIdAndTenantIdAndMenuId(@Param("userId")Long userId, @Param("tenantId")Long tenantId, @Param("menuId")Long menuId);
    Integer selectCountByUserIdAndTenantIdAndBtnId(@Param("userId")Long userId, @Param("tenantId")Long tenantId, @Param("btnId")Long btnId);
}
