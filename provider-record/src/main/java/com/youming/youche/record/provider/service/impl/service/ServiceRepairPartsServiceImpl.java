package com.youming.youche.record.provider.service.impl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.service.IServiceRepairPartsService;
import com.youming.youche.record.domain.service.ServiceRepairParts;
import com.youming.youche.record.provider.mapper.service.ServiceRepairPartsMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 维修零配件 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceRepairPartsServiceImpl extends BaseServiceImpl<ServiceRepairPartsMapper, ServiceRepairParts> implements IServiceRepairPartsService {

    @Override
    public List<ServiceRepairParts> getRepairOrderParts(List<Long> flowIdList) {
        LambdaQueryWrapper<ServiceRepairParts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ServiceRepairParts::getRepairOrderId, flowIdList);
        return this.list(queryWrapper);
    }

}
