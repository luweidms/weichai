package com.youming.youche.cloud.provider.service.sms;

import com.youming.youche.cloud.api.sms.ISmsControllerService;
import com.youming.youche.cloud.domin.sms.SmsController;
import com.youming.youche.cloud.provider.mapper.sms.SmsControllerMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-26
*/
@DubboService(version = "1.0.0")
public class SmsControllerServiceImpl extends BaseServiceImpl<SmsControllerMapper, SmsController> implements ISmsControllerService {


}
