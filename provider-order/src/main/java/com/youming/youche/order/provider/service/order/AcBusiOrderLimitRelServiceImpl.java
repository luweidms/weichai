package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IAcBusiOrderLimitRelService;
import com.youming.youche.order.domain.order.AcBusiOrderLimitRel;
import com.youming.youche.order.provider.mapper.order.AcBusiOrderLimitRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
@DubboService(version = "1.0.0")
@Service
public class AcBusiOrderLimitRelServiceImpl extends BaseServiceImpl<AcBusiOrderLimitRelMapper, AcBusiOrderLimitRel> implements IAcBusiOrderLimitRelService {


}
