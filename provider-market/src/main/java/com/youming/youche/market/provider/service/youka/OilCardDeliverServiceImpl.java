package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IOilCardDeliverService;
import com.youming.youche.market.domain.youka.OilCardDeliver;
import com.youming.youche.market.provider.mapper.youka.OilCardDeliverMapper;
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
    public class OilCardDeliverServiceImpl extends BaseServiceImpl<OilCardDeliverMapper, OilCardDeliver> implements IOilCardDeliverService {


    }
