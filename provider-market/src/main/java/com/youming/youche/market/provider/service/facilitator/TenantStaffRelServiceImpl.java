package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.api.facilitator.ITenantStaffRelService;
import com.youming.youche.market.domain.facilitator.TenantStaffRel;
import com.youming.youche.market.provider.mapper.facilitator.TenantStaffRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 员工信息 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-07
 */
@DubboService(version = "1.0.0")
@Service
public class TenantStaffRelServiceImpl extends BaseServiceImpl<TenantStaffRelMapper, TenantStaffRel> implements ITenantStaffRelService {


    @Override
    public List<TenantStaffRel> getTenantStaffRel(Long userId, LoginInfo user, Boolean tenantFlag) {
        LambdaQueryWrapper<TenantStaffRel> lambda=new QueryWrapper<TenantStaffRel>().lambda();
        lambda.eq(TenantStaffRel::getUserId,userId);
        if(tenantFlag){
            lambda.eq(TenantStaffRel::getTenantId,user.getTenantId());
        }
        List<TenantStaffRel> list = this.list(lambda);
        return list;
    }

    @Override
    public List<TenantStaffRel> getTenantStaffRelByUserId(Long userId) {
        LambdaQueryWrapper<TenantStaffRel> lambda=new QueryWrapper<TenantStaffRel>().lambda();
        lambda.eq(TenantStaffRel::getUserId,userId);
        return this.list(lambda);
    }
}
