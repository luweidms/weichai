package com.youming.youche.order.provider.service.impl.transit;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.transit.ITransitCustHService;
import com.youming.youche.order.domain.transit.TransitCustH;
import com.youming.youche.order.provider.mapper.transit.TransitCustHMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 客户停经历史表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
public class TransitCustHServiceImpl extends BaseServiceImpl<TransitCustHMapper, TransitCustH> implements ITransitCustHService {


}
