package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlService;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtl;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInvitationDtlMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 服务商申请合作明细表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInvitationDtlServiceImpl extends BaseServiceImpl<ServiceInvitationDtlMapper, ServiceInvitationDtl> implements IServiceInvitationDtlService {


    @Override
    public List<ServiceInvitationDtl> getInviteDtlList(Long id) {
        LambdaQueryWrapper<ServiceInvitationDtl> lambda= Wrappers.lambdaQuery();
        lambda.eq(ServiceInvitationDtl::getInviteId,id);
        return this.list(lambda);
    }
}
