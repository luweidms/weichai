package com.youming.youche.order.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.IPlatformServiceChargeService;
import com.youming.youche.order.domain.PlatformServiceCharge;
import com.youming.youche.order.provider.mapper.PlatformServiceChargeMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-04-27
*/
@DubboService(version = "1.0.0")
    public class PlatformServiceChargeServiceImpl extends BaseServiceImpl<PlatformServiceChargeMapper, PlatformServiceCharge> implements IPlatformServiceChargeService {

    @Resource
    PlatformServiceChargeMapper platformServiceChargeMapper;

    @Override
    public List<Object> getNoVerificationAmountByUserId(Long userId) {
        List<Object> objects = platformServiceChargeMapper.selectVer(userId);
        return objects;
    }

    @Override
    public List<Object> getVerificationPlatformAmountByUserId(Long userId) {
        List<Object> objects = platformServiceChargeMapper.selectIfi(userId);
        return objects;
    }

}
