package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.provider.mapper.order.RechargeOilSourceMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * <p>
 * 充值油来源关系表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class RechargeOilSourceServiceImpl extends BaseServiceImpl<RechargeOilSourceMapper, RechargeOilSource> implements IRechargeOilSourceService {


    @Override
    public List<RechargeOilSource> getRechargeOilSourceByUserId(Long userId, Integer userType) {
        LambdaQueryWrapper<RechargeOilSource> lambda=new QueryWrapper<RechargeOilSource>().lambda();
        lambda.eq(RechargeOilSource::getUserId,userId);
        if(userType != null && userType > 0 ){
            lambda.eq(RechargeOilSource::getUserType, userType);
        }
        lambda.and(wq->wq.or()
                .gt(RechargeOilSource::getNoPayOil,  0L)
                .or()
                .gt(RechargeOilSource::getNoCreditOil,  0L)
                .or()
                .gt(RechargeOilSource::getNoRebateOil,  0L));
        lambda.orderByAsc(RechargeOilSource::getCreateTime);
        List<RechargeOilSource> list = this.list(lambda);
        return list;
    }
}
