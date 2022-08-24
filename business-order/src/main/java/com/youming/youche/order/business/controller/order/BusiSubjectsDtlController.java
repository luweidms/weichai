package com.youming.youche.order.business.controller.order;


import com.youming.youche.order.api.order.IBusiSubjectsDtlService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("busi-subjects-dtl")
public class BusiSubjectsDtlController extends BaseController<BusiSubjectsDtl, IBusiSubjectsDtlService> {
    @DubboReference(version = "1.0.0")
    IBusiSubjectsDtlService busiSubjectsDtlService;
    @Override
    public IBusiSubjectsDtlService getService() {
        return busiSubjectsDtlService;
    }
}
