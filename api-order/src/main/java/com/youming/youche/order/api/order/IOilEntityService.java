package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.OilEntity;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;

/**
 * <p>
 * 油充值核销表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IOilEntityService extends IBaseService<OilEntity> {
    /**
     * 保存油卡充值信息
     * @param oilcardNum
     *            油卡号
     * @param tenantId
     *            租户ID
     * @param userId
     *            司机/租户ID
     * @param scheduler
     * @param orderInfo
     * @param oilFee
     * @param oilAffiliation 油资金渠道
     */
    void saveOilCardLog(String oilcardNum, Long tenantId, Long userId, OrderScheduler scheduler,
                        OrderInfo orderInfo, Long oilFee, OilCardManagement management, String oilAffiliation);
}
