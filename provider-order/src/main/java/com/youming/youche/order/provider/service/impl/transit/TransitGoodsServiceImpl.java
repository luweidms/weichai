package com.youming.youche.order.provider.service.impl.transit;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.transit.ITransitGoodsService;
import com.youming.youche.order.domain.transit.TransitGoods;
import com.youming.youche.order.provider.mapper.transit.TransitGoodsMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 运输货物表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
public class TransitGoodsServiceImpl extends BaseServiceImpl<TransitGoodsMapper, TransitGoods> implements ITransitGoodsService {


}
