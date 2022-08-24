package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.RechargeOilSource;

import java.util.List;

/**
 * <p>
 * 充值油来源关系表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IRechargeOilSourceService extends IBaseService<RechargeOilSource> {
    /**
     * 根据userId查找充值油来源关系
     * @param userId
     * @param userType
     * @return
     * @throws Exception
     */
    List<RechargeOilSource> getRechargeOilSourceByUserId(Long userId, Integer userType);
}
