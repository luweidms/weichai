package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 账单核销费用明细 前端控制器
* </p>
* @author WuHao
* @since 2022-04-15
*/
    @RestController
@RequestMapping("order-bill-check-info")
        public class OrderBillCheckInfoController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
