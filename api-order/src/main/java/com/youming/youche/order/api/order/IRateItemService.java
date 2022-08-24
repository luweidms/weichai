package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.RateItem;

import java.util.List;

/**
 * <p>
 * 费率设置项（字表） 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */

public interface IRateItemService extends IBaseService<RateItem> {
    /**
     * 根据费率设置ID获取费率设置
     * @param rateId
     * @return
     */
    List<RateItem> queryRateItem(Long rateId);

    /**
     * 获取配置项
     * @param rateId
     * @param amount
     * @return
     */
    RateItem getRateItem(Long rateId, Double amount);

    /**
     * 开票率
     * @param rateId
     * @param amount
     * @return
     */
    Double getRateValue(Long rateId, Long amount);
}
