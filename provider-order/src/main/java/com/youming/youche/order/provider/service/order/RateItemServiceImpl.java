package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IRateItemService;
import com.youming.youche.order.domain.order.RateItem;
import com.youming.youche.order.provider.mapper.order.RateItemMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 费率设置项（字表） 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class RateItemServiceImpl extends BaseServiceImpl<RateItemMapper, RateItem> implements IRateItemService {


    @Override
    public List<RateItem> queryRateItem(Long rateId) {
        LambdaQueryWrapper<RateItem> lambda=new QueryWrapper<RateItem>().lambda();
        lambda.eq(RateItem::getRateId,rateId);
        return this.list(lambda);
    }

    @Override
    public RateItem getRateItem(Long rateId, Double amount) {
        LambdaQueryWrapper<RateItem> lambda=new QueryWrapper<RateItem>().lambda();
        if (amount == 0.0) {
            lambda.eq(RateItem::getRateId, rateId);
            lambda.eq(RateItem::getRateId, amount);
            return this.getOne(lambda);
        }

        lambda.eq(RateItem::getRateId,rateId);
        lambda.lt(RateItem::getStartValue,rateId);
        lambda.gt(RateItem::getEndValue,amount);


        RateItem item = this.getOne(lambda);
        if (null != item) {
            return item;
        }
        LambdaQueryWrapper<RateItem> lambdaOne=new QueryWrapper<RateItem>().lambda();
        lambdaOne.eq(RateItem::getRateId,rateId)
                 .lt(RateItem::getStartValue,amount)
                 .isNull(RateItem::getEndValue);


        item = this.getOne(lambdaOne);
        if (null != item) {
            return item;
        }
        LambdaQueryWrapper<RateItem> lambdaTwo=new QueryWrapper<RateItem>().lambda();
        lambdaTwo.eq(RateItem::getRateId,rateId)
                 .eq(RateItem::getStartValue,amount)
                 .isNull(RateItem::getEndValue);

        return  this.getOne(lambdaTwo);
    }

    @Override
    public Double getRateValue(Long rateId, Long amount) {
        if (rateId <= 0 || amount < 0) {
            return null;
        }

        Double value = CommonUtil.getDoubleFormatLongMoney(amount, 2);

        RateItem item = this.getRateItem(rateId, value);
        return item == null ? null : item.getRateValue();
    }
}
