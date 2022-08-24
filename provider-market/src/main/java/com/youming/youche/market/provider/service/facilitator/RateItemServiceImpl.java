package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IRateItemService;
import com.youming.youche.market.domain.facilitator.RateItem;
import com.youming.youche.market.provider.mapper.facilitator.RateItemMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 费率设置项（字表） 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
@Service
public class RateItemServiceImpl extends ServiceImpl<RateItemMapper, RateItem> implements IRateItemService {


    @Override
    public List<RateItem> queryRateItem(Long rateId) {
        LambdaQueryWrapper<RateItem> lambda=new QueryWrapper<RateItem>().lambda();
        lambda.eq(RateItem::getRateId,rateId);
        return this.list(lambda);
    }
}
