package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IBaseBillInfoService;
import com.youming.youche.order.domain.order.BaseBillInfo;
import com.youming.youche.order.provider.mapper.order.BaseBillInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.lang.ref.WeakReference;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
@DubboService(version = "1.0.0")
@Service
public class BaseBillInfoServiceImpl extends BaseServiceImpl<BaseBillInfoMapper, BaseBillInfo> implements IBaseBillInfoService {


    @Override
    public List<BaseBillInfo> getBaseBillInfo(Long orderId) {
        LambdaQueryWrapper<BaseBillInfo> lambda= Wrappers.lambdaQuery();
        lambda.eq(BaseBillInfo::getOrderId,orderId);
        return this.list(lambda);
    }
}
