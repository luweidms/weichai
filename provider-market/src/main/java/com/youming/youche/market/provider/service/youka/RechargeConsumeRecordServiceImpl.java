package com.youming.youche.market.provider.service.youka;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IRechargeConsumeRecordService;
import com.youming.youche.market.domain.youka.RechargeConsumeRecord;
import com.youming.youche.market.provider.mapper.youka.RechargeConsumeRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 油卡充值/消费(返利)记录表 服务实现类
    * </p>
* @author XXX
* @since 2022-03-23
*/
@DubboService(version = "1.0.0")
    public class RechargeConsumeRecordServiceImpl extends BaseServiceImpl<RechargeConsumeRecordMapper, RechargeConsumeRecord> implements IRechargeConsumeRecordService {


    }
