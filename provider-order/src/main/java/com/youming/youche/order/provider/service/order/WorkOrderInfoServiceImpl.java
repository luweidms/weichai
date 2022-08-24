package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IWorkOrderInfoService;
import com.youming.youche.order.domain.order.WorkOrderInfo;
import com.youming.youche.order.provider.mapper.order.WorkOrderInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-20
 */
@DubboService(version = "1.0.0")
@Service
public class WorkOrderInfoServiceImpl extends BaseServiceImpl<WorkOrderInfoMapper, WorkOrderInfo> implements IWorkOrderInfoService {


}
