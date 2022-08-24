package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.system.api.ISysPermissionBtnService;
import com.youming.youche.system.api.ISysPermissionService;
import com.youming.youche.system.api.ISysRolePermissionService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.constant.RoleConstant;
import com.youming.youche.system.domain.SysPermission;
import com.youming.youche.system.domain.SysPermissionBtn;
import com.youming.youche.system.domain.SysPermissionMenu;
import com.youming.youche.system.domain.SysRole;
import com.youming.youche.system.domain.SysRolePermission;
import com.youming.youche.system.provider.mapper.SysPermissionBtnMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
@DubboService(version = "1.0.0")
public class SysPermissionBtnServiceImpl extends BaseServiceImpl<SysPermissionBtnMapper, SysPermissionBtn> implements ISysPermissionBtnService {


    @Resource
    ISysPermissionService sysPermissionService;

    @Resource
    ISysRoleService sysRoleService;

    @Resource
    ISysRolePermissionService sysRolePermissionService;


    @Override
    public boolean creates(List<Long> sysBtnIds, Long permissionId) {
        if (sysBtnIds.size()>0){
            Integer integer = baseMapper.insertBatchByBtnIds(permissionId, sysBtnIds);
            if (integer != sysBtnIds.size()) {
                throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
            }
        }
        return true;
    }

    @Override
    public boolean updateAll(List<Long> btnIds, Long role) {
        List<SysPermission> own = sysPermissionService.getRole(role, RoleConstant.PermissionType.BUTTON);
        // 理论上一种权限对应一条记录
        Long permissionId ;
        if (CollectionUtils.isEmpty(own)){
            // 权限表与角色表关联
            SysRole sysRole = sysRoleService.get(role);
            if (null==sysRole){
                throw new BusinessException("未找到用户");
            }
// 建立角色和权限表类型关联（菜单和按钮，文件等）
            // 简历权限表
            SysPermission sysPermission = new SysPermission();
            sysPermission.setName(sysRole.getRoleName());
            sysPermission.setType(RoleConstant.PermissionType.BUTTON);
            boolean save = sysPermissionService.save(sysPermission);
            if (!save) {
                throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
            }
            // 权限表与角色表关联
            SysRolePermission sysRolePermission = new SysRolePermission();
            sysRolePermission.setRoleId(sysRole.getId());
            sysRolePermission.setPermissionId(sysPermission.getId());
            boolean save1 = sysRolePermissionService.save(sysRolePermission);
            if (!save1) {
                throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
            }
            permissionId = sysPermission.getId();
        }else {
            permissionId = own.get(0).getId();
        }

        QueryWrapper<SysPermissionBtn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("permission_id", permissionId);
        baseMapper.delete(queryWrapper);
        List<SysPermissionBtn> list = Lists.newArrayList();
        for (Long id : btnIds) {
            list.add(new SysPermissionBtn().setBtnId(id).setPermissionId(permissionId));
        }
        Integer integer = baseMapper.insertBatchByBtnIds(permissionId, btnIds);
        if (integer != btnIds.size()) {
            throw new BusinessException(ResponseCode.MYSQL_TRANSIENT);
        }
        return true;
    }
}
