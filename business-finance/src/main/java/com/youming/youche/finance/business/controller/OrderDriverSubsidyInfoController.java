package com.youming.youche.finance.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author wuhao
* @since 2022-05-05
*/
    @RestController
@RequestMapping("order-driver-subsidy-info")
        public class OrderDriverSubsidyInfoController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
