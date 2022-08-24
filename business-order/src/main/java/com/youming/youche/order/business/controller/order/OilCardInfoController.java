package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IOilCardInfoService;
import com.youming.youche.order.domain.order.OilCardInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("oil-card-info")
public class OilCardInfoController  {
    @DubboReference(version = "1.0.0")
    IOilCardInfoService oilCardInfoService;

    public IOilCardInfoService getService() {
        return oilCardInfoService;
    }
}
