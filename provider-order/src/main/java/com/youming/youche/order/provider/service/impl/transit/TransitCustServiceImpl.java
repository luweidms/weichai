package com.youming.youche.order.provider.service.impl.transit;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.transit.ITransitCustService;
import com.youming.youche.order.domain.transit.TransitCust;
import com.youming.youche.order.provider.mapper.transit.TransitCustMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 客户停经点 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
public class TransitCustServiceImpl extends BaseServiceImpl<TransitCustMapper, TransitCust> implements ITransitCustService {

}
