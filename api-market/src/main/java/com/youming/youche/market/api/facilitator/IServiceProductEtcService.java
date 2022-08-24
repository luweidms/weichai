package com.youming.youche.market.api.facilitator;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.market.domain.facilitator.ServiceProductEtc;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.dto.facilitator.EtcProductDetailDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-17
 */
public interface IServiceProductEtcService extends IService<ServiceProductEtc> {

     /**
      * 查询服务商etc数据
      *
      * @param productId 服务商产品id
      */
     EtcProductDetailDto getEtcCardProductInfo(Long productId, Boolean isVer);

}
