package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilSourceRecordService;
import com.youming.youche.order.domain.order.OilSourceRecord;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("oil-source-record")
public class OilSourceRecordController extends BaseController<OilSourceRecord, IOilSourceRecordService> {
    @DubboReference(version = "1.0.0")
    IOilSourceRecordService oilSourceRecordService;
    @Override
    public IOilSourceRecordService getService() {
        return oilSourceRecordService;
    }
}
