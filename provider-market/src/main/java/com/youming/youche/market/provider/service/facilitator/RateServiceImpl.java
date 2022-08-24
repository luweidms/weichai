package com.youming.youche.market.provider.service.facilitator;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IRateItemService;
import com.youming.youche.market.api.facilitator.IRateService;
import com.youming.youche.market.domain.facilitator.Rate;
import com.youming.youche.market.domain.facilitator.RateItem;
import com.youming.youche.market.provider.mapper.facilitator.RateMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 费率设置 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
@Service
public class RateServiceImpl extends BaseServiceImpl<RateMapper, Rate> implements IRateService {
    @Resource
    private IRateItemService rateItemService;

    @Override
    public Rate getRateById(Long rateId) {
        Rate rate = this.getById(rateId);
        List<RateItem> rateItems = rateItemService.queryRateItem(rateId);
        rate.setRateItemList(rateItems);
        return rate;
    }
}
