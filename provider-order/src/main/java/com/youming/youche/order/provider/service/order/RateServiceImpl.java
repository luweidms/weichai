package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IRateItemService;
import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.domain.order.Rate;
import com.youming.youche.order.domain.order.RateItem;
import com.youming.youche.order.provider.mapper.order.RateMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;


/**
 * <p>
 * 费率设置 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class RateServiceImpl extends BaseServiceImpl<RateMapper, Rate> implements IRateService {
    @Autowired
    private IRateItemService rateItemService;

    @Override
    public Rate getRateById(Long rateId) {
        LambdaQueryWrapper<Rate> lambda=new QueryWrapper<Rate>().lambda();
        lambda.eq(Rate::getRateId,rateId);
        Rate rate = this.getOne(lambda);
        List<RateItem> rateItems = rateItemService.queryRateItem(rateId);
        rate.setRateItemList(rateItems);
        return rate;
    }

    @Override
    public Double getRateValue(Long rateId, Long amount) {
        if (rateId <= 0 || amount < 0) {
            return null;
        }

        Double value = getDoubleFormatLongMoney(amount, 2);

        RateItem item = rateItemService.getRateItem(rateId, value);
        return item == null ? null : item.getRateValue();
    }
    /**
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(Long balance, int bl) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return 0.0;
        }
        Double money = ((double)balance)/100;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }
}
