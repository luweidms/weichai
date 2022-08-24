package com.youming.youche.record.provider.service.impl.tenant;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.record.dto.UserSalaryDto;
import com.youming.youche.record.provider.mapper.tenant.TenantUserSalaryRelMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 租户自有司机收入信息 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class TenantUserSalaryRelServiceImpl extends ServiceImpl<TenantUserSalaryRelMapper, TenantUserSalaryRel>
        implements ITenantUserSalaryRelService {


    @Override
    public TenantUserSalaryRel getTenantUserRalaryRelByUserId(Long userId) throws Exception {
        QueryWrapper<TenantUserSalaryRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_Id", userId).orderByDesc("id");
        List<TenantUserSalaryRel> list = baseMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public TenantUserSalaryRel getTenantUserRalaryRelByRelId(Long tenantUserRelId) throws BusinessException {
        QueryWrapper<TenantUserSalaryRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("rel_Id", tenantUserRelId).orderByDesc("id");
        List<TenantUserSalaryRel> list = baseMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public TenantUserSalaryRel getTenantUserRalaryRelByUserId(Long userId, Long tenantId) {
        QueryWrapper<TenantUserSalaryRel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).orderByDesc("id");
        List<TenantUserSalaryRel> list = baseMapper.selectList(queryWrapper);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<UserSalaryDto> doQueryOwnDriverAll(Long tenantId) {
        return baseMapper.doQueryUserSalaryList(0L, tenantId);
    }
}
