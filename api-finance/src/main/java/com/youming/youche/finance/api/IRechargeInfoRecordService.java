package com.youming.youche.finance.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.RechargeInfoRecord;

/**
* <p>
    * 充值记录表 服务类
    * </p>
* @author WuHao
* @since 2022-04-15
*/
    public interface IRechargeInfoRecordService extends IBaseService<RechargeInfoRecord> {

    RechargeInfoRecord getObjectById(Long soNbr);


    /**
     * 根据RechargeFlowId查找查找记录
     */
    RechargeInfoRecord getRechargeInfoRecordByRechargeFlowId(String rechargeFlowId);

    /**
     * 22000032  1680
     * 服务商圈退打款成功，写入充值记录
     *
     * @param offline 是否线下打款
     */
    void createOilEntityInfo(String rechargeFlowId, boolean offline, String accessToken);

    }
