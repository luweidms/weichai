package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.ICmCustomerInfoService;
import com.youming.youche.order.domain.order.CmCustomerInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 客户信息表/客户档案表 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-21
 */
@RestController
@RequestMapping("cm-customer-info")
public class CmCustomerInfoController extends BaseController<CmCustomerInfo, ICmCustomerInfoService> {
    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService cmCustomerInfoService;
    @Override
    public ICmCustomerInfoService getService() {
        return cmCustomerInfoService;
    }
}
