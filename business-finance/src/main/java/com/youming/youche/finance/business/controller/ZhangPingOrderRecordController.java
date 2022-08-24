package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.finance.api.IZhangPingOrderRecordService;
import com.youming.youche.finance.domain.ZhangPingOrderRecord;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
* <p>
*  前端控制器
* </p>
* @author WuHao
* @since 2022-04-13
*/
    @RestController
@RequestMapping("zhang/ping/order/record")
        public class ZhangPingOrderRecordController extends BaseController<ZhangPingOrderRecord, IZhangPingOrderRecordService> {

    @DubboReference(version = "1.0.0")
    IZhangPingOrderRecordService iZhangPingOrderRecordService;

    @Override
    public IZhangPingOrderRecordService getService() {
        return iZhangPingOrderRecordService;
    }
}
