package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysPermissionMenu;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 角色权限表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface SysPermissionMenuMapper extends BaseMapper<SysPermissionMenu> {

	// Integer deleteBatchByPermissionIds(@Param("permissionId") Long permissionId);

	Integer insertBatchByMenuIds(@Param("permissionId") Long permissionId, @Param("menuIds") List<Long> menuIds);

}
