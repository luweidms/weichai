package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IOilCardInfoService;
import com.youming.youche.market.domain.youka.OilCardInfo;
import com.youming.youche.market.provider.mapper.youka.OilCardInfoMapper;
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
    public class OilCardInfoServiceImpl extends BaseServiceImpl<OilCardInfoMapper, OilCardInfo> implements IOilCardInfoService {


    }
