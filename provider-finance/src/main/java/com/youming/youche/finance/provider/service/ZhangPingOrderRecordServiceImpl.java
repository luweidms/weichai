package com.youming.youche.finance.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.IZhangPingOrderRecordService;
import com.youming.youche.finance.domain.ZhangPingOrderRecord;
import com.youming.youche.finance.provider.mapper.ZhangPingOrderRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author WuHao
* @since 2022-04-13
*/
@DubboService(version = "1.0.0")
public class ZhangPingOrderRecordServiceImpl extends BaseServiceImpl<ZhangPingOrderRecordMapper, ZhangPingOrderRecord> implements IZhangPingOrderRecordService {


}
