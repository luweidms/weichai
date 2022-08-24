package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IServiceProductEtcVerService;
import com.youming.youche.market.domain.facilitator.ServiceProductEtcVer;
import com.youming.youche.market.provider.mapper.facilitator.ServiceProductEtcVerMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-17
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceProductEtcVerServiceImpl extends BaseServiceImpl<ServiceProductEtcVerMapper, ServiceProductEtcVer> implements IServiceProductEtcVerService {


    @Override
    public ServiceProductEtcVer getServiceProductEtcVer(Long productId) {
        LambdaQueryWrapper<ServiceProductEtcVer> lambda= new QueryWrapper<ServiceProductEtcVer>().lambda();
        lambda.eq(ServiceProductEtcVer::getProductId,productId)
                .orderByDesc(ServiceProductEtcVer::getId)
                .last("limit 1");
        return this.getOne(lambda);
    }
}
