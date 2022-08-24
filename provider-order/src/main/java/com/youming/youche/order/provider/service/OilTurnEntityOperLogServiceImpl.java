package com.youming.youche.order.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.IOilTurnEntityOperLogService;
import com.youming.youche.order.domain.OilTurnEntityOperLog;
import com.youming.youche.order.provider.mapper.OilTurnEntityOperLogMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author wuhao
* @since 2022-04-25
*/
@DubboService(version = "1.0.0")
    public class OilTurnEntityOperLogServiceImpl extends BaseServiceImpl<OilTurnEntityOperLogMapper, OilTurnEntityOperLog> implements IOilTurnEntityOperLogService {


    }
