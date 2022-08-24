package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.AdditionalFee;

/**
 * <p>
 * 附加运费 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-03-28
 */
public interface IAdditionalFeeService extends IBaseService<AdditionalFee> {

    /**
     * 根据订单号查询附加运费
     * @param orderId
     * @return
     * @throws Exception
     */
    AdditionalFee getAdditionalFeeByOrderId(Long orderId);

}
