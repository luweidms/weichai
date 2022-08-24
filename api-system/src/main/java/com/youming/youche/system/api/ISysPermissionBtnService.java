package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysPermissionBtn;

import java.util.List;

/**
 * <p>
 * 角色权限表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
public interface ISysPermissionBtnService extends IBaseService<SysPermissionBtn> {

    /**
     * 方法实现说明  权限id与操作权限绑定
     * @author      terry
     * @param sysBtnIds 操作权限集合
    * @param permissionId 权限id
     * @return      boolean
     * @exception
     * @date        2022/5/31 13:39
     */
    boolean creates(List<Long> sysBtnIds, Long permissionId);

    /**
     * 方法实现说明  修改角色对应的操作权限
     * @author      terry
     * @param btnIds 操作权限集合
     * @param role 角色id
     * @return      boolean
     * @exception
     * @date        2022/5/31 13:40
     */
    boolean updateAll(List<Long> btnIds, Long role);
}
