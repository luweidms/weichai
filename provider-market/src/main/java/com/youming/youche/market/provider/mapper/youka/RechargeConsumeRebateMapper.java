package com.youming.youche.market.provider.mapper.youka;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.youka.RechargeConsumeRebate;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* <p>
* 油卡消费返利记录表Mapper接口
* </p>
* @author XXX
* @since 2022-03-23
*/
    public interface RechargeConsumeRebateMapper extends BaseMapper<RechargeConsumeRebate> {
     @Select("SELECT o.* FROM recharge_consume_rebate o WHERE consume_Flow_Id in (#{consumeFlowIds}) ")
    List<RechargeConsumeRebate> getRechargeConsumeRebateList(List<Long> consumeFlowIds);

}
