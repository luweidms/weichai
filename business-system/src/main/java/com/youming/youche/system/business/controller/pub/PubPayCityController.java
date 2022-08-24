package com.youming.youche.system.business.controller.pub;


import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 平安城市表 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-05-12
 */
@RestController
@RequestMapping("pub-pay-city")
public class PubPayCityController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }

}
