package com.youming.youche.record.provider.service.impl.order;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.order.IOrderInfoServie;
import com.youming.youche.record.domain.order.OrderInfo;
import com.youming.youche.record.provider.mapper.order.OrderInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @Date:2021/12/22
 */
@DubboService(version = "1.0.0")
public class OrderInfoServieImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoServie {


}
