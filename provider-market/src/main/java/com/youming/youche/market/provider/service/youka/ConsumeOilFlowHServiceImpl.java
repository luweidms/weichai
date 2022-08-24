package com.youming.youche.market.provider.service.youka;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IConsumeOilFlowHService;
import com.youming.youche.market.domain.youka.ConsumeOilFlowH;
import com.youming.youche.market.provider.mapper.youka.ConsumeOilFlowHMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    *  服务实现类
    * </p>
* @author XXX
* @since 2022-03-17
*/
@DubboService(version = "1.0.0")
    public class ConsumeOilFlowHServiceImpl extends BaseServiceImpl<ConsumeOilFlowHMapper, ConsumeOilFlowH> implements IConsumeOilFlowHService {


    }
