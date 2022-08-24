package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;

public interface IManualPaymentService {
    /**
     * 油卡充值列表查询
     * @return
     */

    IPage<OilEntityInfoDto> getOilEntitys(OilEntityVo oilEntityVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 油卡充值列表核销
     *
     * @param orderIds 订单编号
     */
    void batchVerificatOrder(String orderIds,String accessToken);

    /**
     * 解冻
     */
    void doAccountIn(Long userId, Integer state, String remark, Long sourceTenantId, Long userType,String accessToken);

    /**
     * 修改卡号
     */
    void updateOilCarNum(String orderId, String oilCarNum, String accessToken);
}
