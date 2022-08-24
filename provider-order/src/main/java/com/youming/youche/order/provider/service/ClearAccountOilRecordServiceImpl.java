package com.youming.youche.order.provider.service;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.api.IClearAccountOilRecordService;
import com.youming.youche.order.domain.oil.ClearAccountOilRecord;
import com.youming.youche.order.provider.mapper.oil.ClearAccountOilRecordMapper;
import org.apache.dubbo.config.annotation.DubboService;


/**
* <p>
    *  服务实现类
    * </p>
* @author WuHao
* @since 2022-04-13
*/
@DubboService(version = "1.0.0")
    public class ClearAccountOilRecordServiceImpl extends BaseServiceImpl<ClearAccountOilRecordMapper, ClearAccountOilRecord> implements IClearAccountOilRecordService {


    }
