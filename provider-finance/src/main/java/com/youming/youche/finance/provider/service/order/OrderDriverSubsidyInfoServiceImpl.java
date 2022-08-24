package com.youming.youche.finance.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.IOrderDriverSubsidyInfoService;
import com.youming.youche.finance.domain.ac.OrderDriverSubsidyInfo;
import com.youming.youche.finance.provider.mapper.ac.OrderDriverSubsidyInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-05-05
*/
@DubboService(version = "1.0.0")
    public class OrderDriverSubsidyInfoServiceImpl extends BaseServiceImpl<OrderDriverSubsidyInfoMapper, OrderDriverSubsidyInfo> implements IOrderDriverSubsidyInfoService {


    }
