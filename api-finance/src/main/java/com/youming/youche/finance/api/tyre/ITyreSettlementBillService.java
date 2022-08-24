package com.youming.youche.finance.api.tyre;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.tyre.TyreSettlementBill;

/**
 * <p>
 * 轮胎结算账单汇总 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
public interface ITyreSettlementBillService extends IBaseService<TyreSettlementBill> {

    /**
     * 代收服务商轮胎租赁
     * @param busiCode
     * @return
     */
    TyreSettlementBill getTyreSettlementBillVOByBusiCode(String busiCode);

    /**
     * 修改轮胎结算账单汇总
     * @param id
     * @param state 1待确认支付、2待付款、3确认中 4已付款 5已结算
     * @param desc
     * @param accessToken
     */
    void updTyreSettlementBillState(Long id, Integer state, String desc, String accessToken);



}
