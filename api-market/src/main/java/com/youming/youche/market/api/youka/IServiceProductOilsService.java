package com.youming.youche.market.api.youka;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.youka.OilCardInfo;
import com.youming.youche.market.domain.youka.ServiceProductOils;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author XXX
 * @since 2022-03-24
 */
public interface IServiceProductOilsService extends IBaseService<ServiceProductOils> {

    /**
     * 判断该卡号为平台服务商拥有
     *
     * @param oilCardInfo 油卡日志信息
     * @return
     */
    long getOilCardInfo(OilCardInfo oilCardInfo);

    /**
     * 获取服务商油信息
     *
     * @param oilsId 油品唯一标识
     */
    List<ServiceProductOils> getServiceProductOils(String oilsId);

    /**
     * 获取服务商油信息
     *
     * @param oilsId 油品唯一标识
     */
    ServiceProductOils getServiceProductOilsByOilsId(String oilsId);

}
