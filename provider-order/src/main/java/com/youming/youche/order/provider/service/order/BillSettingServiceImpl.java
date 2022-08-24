package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IBillSettingService;
import com.youming.youche.order.api.order.IRateService;
import com.youming.youche.order.domain.order.BillSetting;
import com.youming.youche.order.domain.order.Rate;
import com.youming.youche.order.provider.mapper.order.BillSettingMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 车队的开票设置 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class BillSettingServiceImpl extends BaseServiceImpl<BillSettingMapper, BillSetting> implements IBillSettingService {
    @Autowired
    private IRateService rateService;

    @Override
    public Boolean supportNotOtherCarGetPlatformBill(Long tenantId) {
        BillSetting billSetting = getBillSettingByTenantId(tenantId);
        if (null == billSetting) {
            throw new BusinessException("获取车队开票信息失败");
        }

        return billSetting.getNotOtherCarGetPlatformBill() == 1;
    }



    @Override
    public Boolean getInvokeAble(Long tenantId) {
        BillSetting billSetting = this.getTenantBillSetting(tenantId);
        if(billSetting !=null&& billSetting.getBillAbility()!=null&& billSetting.getBillAbility()== SysStaticDataEnum.BILL_ABILITY.ENABLE){
            return true;
        }
        return false;
    }

    @Override
    public BillSetting getBillSetting(Long tenantId) {
        BillSetting info = this.getBillSettingByTenantId(tenantId);
        if (null != info && null != info.getRateId()) {
            Rate rate = rateService.getRateById(info.getRateId());
            info.setRateName(rate.getRateName());
        }
        return info;
    }

    @Override
    public Long getBillMethodByTenantId(Long tenantId) {
        BillSetting billSetting = getBillSettingByTenantId(tenantId);
        return null == billSetting ? null : billSetting.getBillMethod();
    }

    @Override
    public BillSetting getBillSettingByTenantId(Long tenantId) {
        LambdaQueryWrapper<BillSetting> lambda=new QueryWrapper<BillSetting>().lambda();
        lambda.eq(BillSetting::getTenantId,tenantId);
        return this.getOne(lambda);
    }

    public BillSetting getTenantBillSetting(Long tenantId){
        if (tenantId <= 0) {
            throw new BusinessException("tenantId不能为空");

        }
        return   this.getBillSettingByTenantId(tenantId);
    }
}
