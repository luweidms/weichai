package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.IBaseService;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author xxx
 * @since 2022-03-28
 */
@RestController
@RequestMapping("etc-maintain")
public class EtcMaintainController extends BaseController {

    @Override
    public IBaseService getService() {
        return null;
    }
}
