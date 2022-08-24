package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 充值记录表 前端控制器
* </p>
* @author WuHao
* @since 2022-04-15
*/
    @RestController
@RequestMapping("recharge-info-record")
        public class RechargeInfoRecordController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
