package com.youming.youche.market.provider.service.youka;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IRechargeInfoRecordService;
import com.youming.youche.market.domain.youka.RechargeInfoRecord;
import com.youming.youche.market.provider.mapper.youka.RechargeInfoRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 充值记录表 服务实现类
    * </p>
* @author zag
* @since 2022-04-15
*/
@DubboService(version = "1.0.0")
    public class RechargeInfoRecordServiceImpl extends BaseServiceImpl<RechargeInfoRecordMapper, RechargeInfoRecord> implements IRechargeInfoRecordService {


    @Override
    public RechargeInfoRecord getRechargeInfoRecordByRechargeFlowId(String rechargeFlowId) {
        LambdaQueryWrapper<RechargeInfoRecord> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(RechargeInfoRecord::getRechargeFlowId,rechargeFlowId);
        return this.getOne(queryWrapper);
    }
}
