package com.youming.youche.market.provider.mapper.youka;

import com.youming.youche.market.domain.youka.ServiceProductOils;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* <p>
* Mapper接口
* </p>
* @author XXX
* @since 2022-03-24
*/
    public interface ServiceProductOilsMapper extends BaseMapper<ServiceProductOils> {


    Integer count(String oilCardNum, Integer oilCardType, Integer etcCardType);
}
