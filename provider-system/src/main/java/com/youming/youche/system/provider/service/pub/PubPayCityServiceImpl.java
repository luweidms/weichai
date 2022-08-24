package com.youming.youche.system.provider.service.pub;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.pub.IPubPayCityService;
import com.youming.youche.system.domain.pub.PubPayCity;
import com.youming.youche.system.provider.mapper.pub.PubPayCityMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * <p>
 * 平安城市表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@DubboService(version = "1.0.0")
public class PubPayCityServiceImpl extends BaseServiceImpl<PubPayCityMapper, PubPayCity> implements IPubPayCityService {

    @Override
    public List<PubPayCity> getPubPayCityList(String provId) {
        LambdaQueryWrapper<PubPayCity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PubPayCity::getProvNodecode, provId);
        queryWrapper.orderByAsc(PubPayCity::getCityOraareacode);
        return this.list(queryWrapper);
    }

}
