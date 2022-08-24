package com.youming.youche.market.provider.service.facilitator;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IBillSettingService;
import com.youming.youche.market.api.facilitator.IRateService;
import com.youming.youche.market.domain.facilitator.BillSetting;
import com.youming.youche.market.domain.facilitator.Rate;
import com.youming.youche.market.provider.mapper.facilitator.BillSettingMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
 * <p>
 * 车队的开票设置 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
public class BillSettingServiceImpl extends BaseServiceImpl<BillSettingMapper, BillSetting> implements IBillSettingService {
    @Resource
    private IRateService rateService;

    @Override
    public BillSetting getBillSetting(Long tenantId) {
        LambdaQueryWrapper<BillSetting> lambda= new QueryWrapper<BillSetting>().lambda();
        lambda.eq(BillSetting::getTenantId,tenantId);
        BillSetting info = this.getOne(lambda);
        if (null != info && null != info.getRateId()) {
            Rate rate = rateService.getRateById(info.getRateId());
            info.setRateName(rate.getRateName());
        }
        return info;
    }
}
