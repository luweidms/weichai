package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统菜单表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

	public List<SysMenu> getAll(@Param("userId") long userId,@Param("tenantId")Long tenantId);

	List<SysMenu> selectAllByRoleId(@Param("roleId") Long roleId);

}
