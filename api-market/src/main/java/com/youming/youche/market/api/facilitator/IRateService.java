package com.youming.youche.market.api.facilitator;

import com.youming.youche.market.domain.facilitator.Rate;
import com.youming.youche.commons.base.IBaseService;

/**
 * <p>
 * 费率设置 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
public interface IRateService extends IBaseService<Rate> {
    /**
     * 根据费率设置ID获取费率设置
     * @param rateId
     * @return
     */
    Rate getRateById(Long rateId);
}
