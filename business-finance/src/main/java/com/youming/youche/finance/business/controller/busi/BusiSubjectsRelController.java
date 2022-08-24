package com.youming.youche.finance.business.controller.busi;


import com.youming.youche.finance.api.busi.IBusiSubjectsRelService;
import com.youming.youche.finance.domain.busi.BusiSubjectsRel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-03-08
 */
@RestController
@RequestMapping("busi-subjects-rel")
public class BusiSubjectsRelController extends BaseController<BusiSubjectsRel, IBusiSubjectsRelService> {

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Override
    public IBusiSubjectsRelService getService() {
        return iBusiSubjectsRelService;
    }
}
