package com.youming.youche.record.provider.service.impl.base;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.record.api.base.IBeidouPaymentRecordService;
import com.youming.youche.record.domain.base.BeidouPaymentRecord;
import com.youming.youche.record.provider.mapper.base.BeidouPaymentRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @Date:2021/12/22
 */
@DubboService(version = "1.0.0")
public class BeidouPaymentRecordServiceImpl extends ServiceImpl<BeidouPaymentRecordMapper, BeidouPaymentRecord>
        implements IBeidouPaymentRecordService {

    @Resource
    private BeidouPaymentRecordMapper beidouPaymentRecordMapper;
}
