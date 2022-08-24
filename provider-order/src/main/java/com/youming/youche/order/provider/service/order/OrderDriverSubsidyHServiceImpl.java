package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOrderDriverSubsidyHService;
import com.youming.youche.order.domain.order.OrderDriverSubsidyH;
import com.youming.youche.order.provider.mapper.order.OrderDriverSubsidyHMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
@DubboService(version = "1.0.0")
public class OrderDriverSubsidyHServiceImpl extends BaseServiceImpl<OrderDriverSubsidyHMapper, OrderDriverSubsidyH> implements IOrderDriverSubsidyHService {


}
