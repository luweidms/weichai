package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OilRechargeAccountFlow;
import com.youming.youche.order.dto.order.PayoutIntfDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IOilRechargeAccountFlowService extends IBaseService<OilRechargeAccountFlow> {

    /**
     * 退回接口
     * @param userId
     * @param orderNum
     * @param sourceUserId
     * @param sourceMap
     * @param subjectsId
     * @param recallType
     * @return
     */
     Long recallOilAccount(Long userId, String orderNum, Long sourceUserId,
                           Map sourceMap, Long subjectsId, Integer recallType,
                           LoginInfo user);

    /**
     * 支付成功回调
     * @param payoutIntfDto
     * @param accessToken
     */
    void payWithdrawSucess(PayoutIntfDto payoutIntfDto, String accessToken);

    /**
     * 母卡资金流水查询
     *
     * @param busiCode 业务单号
     */
    List<OilRechargeAccountFlow> getAccountFlows(String busiCode);

}
