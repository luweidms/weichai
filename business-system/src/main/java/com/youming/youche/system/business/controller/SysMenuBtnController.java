package com.youming.youche.system.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysMenuBtnService;
import com.youming.youche.system.domain.SysMenuBtn;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统菜单按钮表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@RestController
@RequestMapping("sys/menu/btn")
public class SysMenuBtnController extends BaseController<SysMenuBtn, ISysMenuBtnService> {

    @DubboReference(version = "1.0.0")
    ISysMenuBtnService sysMenuBtnService;

    @Override
    public ISysMenuBtnService getService() {
        return sysMenuBtnService;
    }

    /**
     * 方法实现说明 根据菜单id，查询按钮操作
     * @author      terry
     * @param Id
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 11:44
     */
    @GetMapping({"getMenu/{Id}"})
    public ResponseResult getMenu(@PathVariable Long Id) {
        List<SysMenuBtn> sysMenuBtn = sysMenuBtnService.selectByMenuId(Id);
        return ResponseResult.success(sysMenuBtn);
    }
}
