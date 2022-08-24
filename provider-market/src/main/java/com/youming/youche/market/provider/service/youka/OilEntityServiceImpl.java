package com.youming.youche.market.provider.service.youka;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.youka.IOilEntityService;
import com.youming.youche.market.domain.youka.OilEntity;
import com.youming.youche.market.provider.mapper.youka.OilEntityMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
@DubboService(version = "1.0.0")
public class OilEntityServiceImpl extends BaseServiceImpl<OilEntityMapper, OilEntity> implements IOilEntityService {


}
