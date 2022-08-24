package com.youming.youche.cloud.provider.service.sms;

import com.youming.youche.cloud.api.sms.IMsgNotifySettingService;
import com.youming.youche.cloud.domin.sms.MsgNotifySetting;
import com.youming.youche.cloud.provider.mapper.sms.MsgNotifySettingMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;


/**
 * <p>
 * 短信通知配置 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
public class MsgNotifySettingServiceImpl extends BaseServiceImpl<MsgNotifySettingMapper, MsgNotifySetting> implements IMsgNotifySettingService {


}
