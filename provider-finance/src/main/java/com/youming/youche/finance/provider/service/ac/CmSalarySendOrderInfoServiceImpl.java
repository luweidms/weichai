package com.youming.youche.finance.provider.service.ac;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.ac.ICmSalarySendOrderInfoService;
import com.youming.youche.finance.domain.ac.CmSalarySendOrderInfo;
import com.youming.youche.finance.provider.mapper.ac.CmSalarySendOrderInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 驾驶员工资发送订单表 服务实现类
    * </p>
* @author zengwen
* @since 2022-06-29
*/
@DubboService(version = "1.0.0")
public class CmSalarySendOrderInfoServiceImpl extends BaseServiceImpl<CmSalarySendOrderInfoMapper, CmSalarySendOrderInfo> implements ICmSalarySendOrderInfoService {


}
