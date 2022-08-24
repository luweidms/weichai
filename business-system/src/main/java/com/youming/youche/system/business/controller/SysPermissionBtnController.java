package com.youming.youche.system.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.api.ISysPermissionBtnService;
import com.youming.youche.system.domain.SysPermissionBtn;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 角色权限表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
@RestController
@RequestMapping("sys/permission/btn")
public class SysPermissionBtnController extends BaseController<SysPermissionBtn, ISysPermissionBtnService> {

    @DubboReference(version = "1.0.0")
    ISysPermissionBtnService sysPermissionBtnService;

    @Override
    public ISysPermissionBtnService getService() {
        return sysPermissionBtnService;
    }

    /**
     * 方法实现说明 修改角色权限
     * @author      terry
     * @param btnIds 权限id集合
    * @param role 角色id
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 13:36
     */
    @PutMapping({ "updateAll/{role}" })
    public ResponseResult updateAll(@RequestBody List<Long> btnIds,
                                    @PathVariable(value = "role") Long role) {
        sysPermissionBtnService.updateAll(btnIds, role);
        return ResponseResult.success();
    }
}
