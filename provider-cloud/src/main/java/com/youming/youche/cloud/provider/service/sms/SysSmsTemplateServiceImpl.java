package com.youming.youche.cloud.provider.service.sms;

import com.youming.youche.cloud.api.sms.ISysSmsTemplateService;
import com.youming.youche.cloud.provider.mapper.sms.SysSmsTemplateMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.cloud.domin.sms.SysSmsTemplate;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@DubboService(version = "1.0.0")
public class SysSmsTemplateServiceImpl extends BaseServiceImpl<SysSmsTemplateMapper, SysSmsTemplate> implements ISysSmsTemplateService {


}
