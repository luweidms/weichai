package com.youming.youche.order.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author wuhao
* @since 2022-04-25
*/
    @RestController
@RequestMapping("turn-cash-log")
        public class TurnCashLogController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
