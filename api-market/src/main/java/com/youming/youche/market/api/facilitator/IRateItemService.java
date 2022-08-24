package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.market.domain.facilitator.RateItem;

import java.util.List;

/**
 * <p>
 * 费率设置项（字表） 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-14
 */
public interface IRateItemService extends IService<RateItem> {

    /**
     * 根据费率设置ID获取费率设置
     * @param rateId
     * @return
     */
    List<RateItem> queryRateItem(Long rateId);
}
