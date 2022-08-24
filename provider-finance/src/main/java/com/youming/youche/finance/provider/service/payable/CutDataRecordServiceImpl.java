package com.youming.youche.finance.provider.service.payable;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.payable.ICutDataRecordService;
import com.youming.youche.finance.domain.payable.CutDataRecord;
import com.youming.youche.finance.provider.mapper.payable.CutDataRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author zag
* @since 2022-04-12
*/
@DubboService(version = "1.0.0")
public class CutDataRecordServiceImpl extends BaseServiceImpl<CutDataRecordMapper, CutDataRecord> implements ICutDataRecordService {


}
