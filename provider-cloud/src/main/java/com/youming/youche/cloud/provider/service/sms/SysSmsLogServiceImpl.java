package com.youming.youche.cloud.provider.service.sms;


import com.youming.youche.cloud.api.sms.ISysSmsLogService;
import com.youming.youche.cloud.domin.sms.SysSmsLog;
import com.youming.youche.cloud.provider.mapper.sms.SysSmsLogMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
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
public class SysSmsLogServiceImpl extends BaseServiceImpl<SysSmsLogMapper, SysSmsLog> implements ISysSmsLogService {


}
