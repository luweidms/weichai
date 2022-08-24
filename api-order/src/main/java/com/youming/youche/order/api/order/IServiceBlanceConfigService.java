package com.youming.youche.order.api.order;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.ServiceBlanceConfig;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
public interface IServiceBlanceConfigService extends IBaseService<ServiceBlanceConfig> {

    /***
     *
     * @param anentId  油品公司id
     * @param serviceId  油服务商id
     * @param productId   油站id
     * @param amount   金额(单位 分)
     * @throws Exception
     */
    void doUpdServiceBlanceConfig(Long anentId,Long serviceId,Long productId,Long amount);

    /**
     * 获取代收服务商余额
     * @param anentId
     * @param serviceId
     * @param productId
     * @return
     */
    ServiceBlanceConfig getServiceBlanceConfig(Long anentId, Long serviceId, Long productId);
}
