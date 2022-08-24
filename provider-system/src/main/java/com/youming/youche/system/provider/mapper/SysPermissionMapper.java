package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysPermission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

	List<SysPermission> selectAllByRoleIdAndType(@Param("roleId") Long roleId, @Param("type") Integer type);

	List<SysPermission> selectAllByRoleId(@Param("roleId") Long roleId);
}
