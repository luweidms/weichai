package com.youming.youche.market.api.facilitator;

import com.youming.youche.market.domain.facilitator.ServiceProductEtcVer;
import com.youming.youche.commons.base.IBaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-17
 */
public interface IServiceProductEtcVerService extends IBaseService<ServiceProductEtcVer> {

    /**
     * 查询ETC站点版本数据
     *
     * @return
     */
    ServiceProductEtcVer getServiceProductEtcVer(Long productId);
}
