package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IPayoutIntfExpansionService extends IBaseService<PayoutIntfExpansion> {

    /**
     * 获取打款扩展表信息
     */
    PayoutIntfExpansion getPayoutIntfExpansion(Long flowId);
}
