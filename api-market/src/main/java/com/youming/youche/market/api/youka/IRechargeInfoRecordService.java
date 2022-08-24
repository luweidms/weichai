package com.youming.youche.market.api.youka;

import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.market.domain.youka.RechargeInfoRecord;

/**
* <p>
    * 充值记录表 服务类
    * </p>
* @author zag
* @since 2022-04-15
*/
    public interface IRechargeInfoRecordService extends IBaseService<RechargeInfoRecord> {

    /**
     * 查询充值记录
     *
     * @param rechargeFlowId 充值流水号
     */
    RechargeInfoRecord getRechargeInfoRecordByRechargeFlowId(String rechargeFlowId);

    }
