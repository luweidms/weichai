package com.youming.youche.cloud.business.controller.sms;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-26
*/
    @RestController
@RequestMapping("sms/controller")
        public class SmsControllerController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
