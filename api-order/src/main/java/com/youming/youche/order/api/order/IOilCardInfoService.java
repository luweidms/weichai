package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OilCardInfo;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IOilCardInfoService extends IBaseService<OilCardInfo> {
    /**
     * 获取油卡号
     * @param oilCardInfo
     * @return
     */
    Integer getOilCardInfo(OilCardInfo oilCardInfo);
}
