package com.youming.youche.market.provider.service.facilitator;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlVerService;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtlVer;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInvitationDtlVerMapper;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 服务商申请合作明细版本 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInvitationDtlVerServiceImpl extends ServiceImpl<ServiceInvitationDtlVerMapper, ServiceInvitationDtlVer> implements IServiceInvitationDtlVerService {


    @Override
    public void checkHisSetNot(Long id, LoginInfo user) {
        LambdaQueryWrapper<ServiceInvitationDtlVer> lambda=new QueryWrapper<ServiceInvitationDtlVer>().lambda();
        lambda.eq(ServiceInvitationDtlVer::getId,id)
                .eq(ServiceInvitationDtlVer::getIsUse,1);
        List<ServiceInvitationDtlVer> list = this.list(lambda);
        if(list != null && list.size() > 0){
            for(ServiceInvitationDtlVer serviceInvitationDtlVer : list){
                LambdaUpdateWrapper<ServiceInvitationDtlVer> updateWrapper=new UpdateWrapper<ServiceInvitationDtlVer>().lambda();
                updateWrapper.set(ServiceInvitationDtlVer::getIsUse,0)
                             .set(ServiceInvitationDtlVer::getUpdateOpId,user.getId())
                              .set(ServiceInvitationDtlVer::getUpdateTime, LocalDateTime.now())
                              .eq(ServiceInvitationDtlVer::getId,serviceInvitationDtlVer.getId());
                this.update(updateWrapper);
            }
        }
    }
}
