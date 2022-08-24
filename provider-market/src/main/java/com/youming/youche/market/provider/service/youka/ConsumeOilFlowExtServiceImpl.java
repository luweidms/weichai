package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IConsumeOilFlowExtService;
import com.youming.youche.market.domain.youka.ConsumeOilFlowExt;
import com.youming.youche.market.provider.mapper.youka.ConsumeOilFlowExtMapper;
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
    public class ConsumeOilFlowExtServiceImpl extends BaseServiceImpl<ConsumeOilFlowExtMapper, ConsumeOilFlowExt> implements IConsumeOilFlowExtService {


    }
