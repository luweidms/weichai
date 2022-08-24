package com.youming.youche.record.provider.service.impl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.record.api.service.IServiceRepairItemsService;
import com.youming.youche.record.domain.service.ServiceRepairItems;
import com.youming.youche.record.provider.mapper.service.ServiceRepairItemsMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
 * <p>
 * 服务商维修项 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
@DubboService(version = "1.0.0")
public class ServiceRepairItemsServiceImpl extends BaseServiceImpl<ServiceRepairItemsMapper, ServiceRepairItems> implements IServiceRepairItemsService {

    @Override
    public List<ServiceRepairItems> getRepairOrderItems(List<Long> flowIdList) {
        LambdaQueryWrapper<ServiceRepairItems> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ServiceRepairItems::getRepairOrderId, flowIdList);
        return this.list(queryWrapper);
    }

}
