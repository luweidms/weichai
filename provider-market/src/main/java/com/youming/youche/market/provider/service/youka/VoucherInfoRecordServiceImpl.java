package com.youming.youche.market.provider.service.youka;

import com.youming.youche.market.api.youka.IVoucherInfoRecordService;
import com.youming.youche.market.domain.youka.VoucherInfoRecord;
import com.youming.youche.market.provider.mapper.youka.VoucherInfoRecordMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;



/**
* <p>
    * 油卡充值代金券使用记录表 服务实现类
    * </p>
* @author Terry
* @since 2022-03-08
*/
@DubboService(version = "1.0.0")
    public class VoucherInfoRecordServiceImpl extends BaseServiceImpl<VoucherInfoRecordMapper, VoucherInfoRecord> implements IVoucherInfoRecordService {


    }
