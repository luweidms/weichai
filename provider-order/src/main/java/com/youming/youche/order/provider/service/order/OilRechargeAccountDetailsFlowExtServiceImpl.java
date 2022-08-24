package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowExtService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlowExt;
import com.youming.youche.order.provider.mapper.order.OilRechargeAccountDetailsFlowExtMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OilRechargeAccountDetailsFlowExtServiceImpl extends BaseServiceImpl<OilRechargeAccountDetailsFlowExtMapper, OilRechargeAccountDetailsFlowExt> implements IOilRechargeAccountDetailsFlowExtService {


}
