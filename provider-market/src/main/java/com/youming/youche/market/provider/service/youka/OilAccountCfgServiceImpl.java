package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IOilAccountCfgService;
import com.youming.youche.market.domain.youka.OilAccountCfg;
import com.youming.youche.market.provider.mapper.youka.OilAccountCfgMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    *  服务实现类
    * </p>
* @author XXX
* @since 2022-03-24
*/
@DubboService(version = "1.0.0")
    public class OilAccountCfgServiceImpl extends BaseServiceImpl<OilAccountCfgMapper, OilAccountCfg> implements IOilAccountCfgService {


    }
