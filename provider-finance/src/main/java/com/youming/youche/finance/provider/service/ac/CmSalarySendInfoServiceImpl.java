package com.youming.youche.finance.provider.service.ac;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.ac.ICmSalarySendInfoService;
import com.youming.youche.finance.domain.ac.CmSalarySendInfo;
import com.youming.youche.finance.provider.mapper.ac.CmSalarySendInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 驾驶员工资发送信息表 服务实现类
    * </p>
* @author zengwen
* @since 2022-06-29
*/
@DubboService(version = "1.0.0")
public class CmSalarySendInfoServiceImpl extends BaseServiceImpl<CmSalarySendInfoMapper, CmSalarySendInfo> implements ICmSalarySendInfoService {


}
