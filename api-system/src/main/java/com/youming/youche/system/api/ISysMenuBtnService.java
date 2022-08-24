package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysMenuBtn;

import java.util.List;

/**
 * <p>
 * 系统菜单按钮表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
public interface ISysMenuBtnService extends IBaseService<SysMenuBtn> {

    /**
     * 方法实现说明  查询所有的系统按钮
     * @author      terry
     * @param
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/1/21 0:00
     */
    List<SysMenuBtn> selectAll();

    /**
     * 方法实现说明  根据菜单id 查询按钮
     * @author      terry
     * @param id
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/1/21 0:14
     */
    List<SysMenuBtn> selectByMenuId(Long id);


    /**
     * 方法实现说明 查询用户的全部操作权限
     * @author      terry
     * @param id
     * @param tenantId
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/2/7 16:00
     */
    List<SysMenuBtn> selectByUserId(Long id,Long tenantId);

    /**
     * 方法实现说明 查询用户的数据操作权限
     * @author      terry
     * @param accessToken
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/2/7 15:59
     */
    List<SysMenuBtn> selectAllByUserIdAndData(String accessToken);

    /**
     * 方法实现说明 查询用户的数据操作权限
     * @author      terry
     * @param userId
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/2/7 15:59
     */
    List<SysMenuBtn> selectAllByUserIdAndData(long userId);

    /**
     * 方法实现说明 查询角色的全部操作权限
     * @author      terry
     * @param roleId
     * @return      java.util.List<com.youming.youche.system.domain.SysMenuBtn>
     * @exception
     * @date        2022/2/7 16:00
     */
    List<SysMenuBtn> getRoleId(Long roleId);

}
