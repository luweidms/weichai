package com.youming.youche.order.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.order.domain.PlatformServiceCharge;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author wuhao
* @since 2022-04-27
*/
    public interface PlatformServiceChargeMapper extends BaseMapper<PlatformServiceCharge> {

    List<Object> selectVer(Long userId);

    List<Object> selectIfi(Long userId);
}
