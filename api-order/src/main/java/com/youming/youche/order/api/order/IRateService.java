package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.Rate;

/**
 * <p>
 * 费率设置 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IRateService extends IBaseService<Rate> {
    /**
     * 根据费率设置ID获取费率设置
     * @param rateId
     * @return
     */
    Rate getRateById(Long rateId);

    /**
     * 开票率
     * @param rateId
     * @param amount
     * @return
     */
    Double getRateValue(Long rateId, Long amount);
}
