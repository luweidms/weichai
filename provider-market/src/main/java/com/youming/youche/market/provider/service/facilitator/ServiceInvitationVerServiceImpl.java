package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.market.api.facilitator.IServiceInvitationVerService;
import com.youming.youche.market.domain.facilitator.ServiceInvitationVer;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInvitationVerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务商申请合作 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-28
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInvitationVerServiceImpl extends ServiceImpl<ServiceInvitationVerMapper, ServiceInvitationVer> implements IServiceInvitationVerService {


}
