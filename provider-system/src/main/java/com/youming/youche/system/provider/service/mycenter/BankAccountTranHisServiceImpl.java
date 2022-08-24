package com.youming.youche.system.provider.service.mycenter;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.api.mycenter.IBankAccountTranHisService;
import com.youming.youche.system.domain.mycenter.CmbAccountTransactionRecordHis;
import com.youming.youche.system.provider.mapper.mycenter.BankAccountTranHisMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    * 招行交易记录历史表 服务实现类
    * </p>
* @author zag
* @since 2022-04-24
*/
@DubboService(version = "1.0.0")
public class BankAccountTranHisServiceImpl extends BaseServiceImpl<BankAccountTranHisMapper, CmbAccountTransactionRecordHis> implements IBankAccountTranHisService {


}
