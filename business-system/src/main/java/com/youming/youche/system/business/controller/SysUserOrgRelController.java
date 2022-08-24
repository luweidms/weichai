package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.domain.SysUserOrgRel;
import com.youming.youche.system.vo.CreateSysUserOrgVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户组织关系表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@RestController
@RequestMapping("/sys/user/org/rel")
public class SysUserOrgRelController extends BaseController<SysUserOrgRel, ISysUserOrgRelService> {

    @DubboReference(version = "1.0.0", retries = 0)
    ISysUserOrgRelService sysUserOrgRelService;

    @Override
    public ISysUserOrgRelService getService() {
        return sysUserOrgRelService;
    }

    /**
     * 方法实现说明 解绑用户与部门关系
     * @author terry
     * @param orgId 部门id
     * @param userInfoIds 用户信息id集合
     * @return com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date 2022/5/31 15:30
     */
    @DeleteMapping({"remove/{orgId}/{userInfoIds}"})
    public ResponseResult removes(@PathVariable("orgId") Long orgId, @PathVariable("userInfoIds") String userInfoIds) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean deleted = sysUserOrgRelService.remove(orgId, userInfoIds, accessToken);
        return deleted ? ResponseResult.success("删除成功")
                : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * 方法实现说明 将用户与部门绑定
     * @author      terry
     * @param sysUserOrgVo
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:30
     */
    @PostMapping({"creates"})
    public ResponseResult create(@Valid @RequestBody CreateSysUserOrgVo sysUserOrgVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean created = sysUserOrgRelService.create(sysUserOrgVo, accessToken);
        return created ? ResponseResult.success("创建成功") : null;
    }

}
