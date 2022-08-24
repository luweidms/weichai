package com.youming.youche.order.provider.service.impl.transit;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.transit.ITransitGoodsHService;
import com.youming.youche.order.domain.transit.TransitGoodsH;
import com.youming.youche.order.provider.mapper.transit.TransitGoodsHMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 运输货物历史表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
public class TransitGoodsHServiceImpl extends BaseServiceImpl<TransitGoodsHMapper, TransitGoodsH> implements ITransitGoodsHService {


}
