package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IRechargeConsumeRebateService;
import com.youming.youche.market.domain.youka.RechargeConsumeRebate;
import com.youming.youche.market.provider.mapper.youka.RechargeConsumeRebateMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 油卡消费返利记录表 服务实现类
    * </p>
* @author XXX
* @since 2022-03-23
*/
@DubboService(version = "1.0.0")
    public class RechargeConsumeRebateServiceImpl extends BaseServiceImpl<RechargeConsumeRebateMapper, RechargeConsumeRebate> implements IRechargeConsumeRebateService {


    }
