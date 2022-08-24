package com.youming.youche.market.provider.mapper.youka;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.youka.ConsumeOilFlowExt;

import java.util.List;

/**
* <p>
* Mapper接口
* </p>
* @author XXX
* @since 2022-03-17
*/
    public interface ConsumeOilFlowExtMapper extends BaseMapper<ConsumeOilFlowExt> {


    void selectByIds(List<Long> flowIds);

    List<ConsumeOilFlowExt> selectQuaryState(Integer rebate);


    List<ConsumeOilFlowExt> selectByresultFlowIds(List<Long> resultFlowIds);

}
