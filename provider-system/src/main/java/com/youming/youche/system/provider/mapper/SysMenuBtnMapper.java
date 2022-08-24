package com.youming.youche.system.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.system.domain.SysMenu;
import com.youming.youche.system.domain.SysMenuBtn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统菜单按钮表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
public interface SysMenuBtnMapper extends BaseMapper<SysMenuBtn> {


    List<SysMenuBtn> selectAllByUserIdAndTenandId(@Param("userId") long userId, @Param("tenantId")Long tenantId);

    List<SysMenuBtn> selectAllByUserIdAndData(@Param("userId") long userId);

    List<SysMenuBtn> selectAllByRoleId(@Param("roleId") Long roleId);
}
