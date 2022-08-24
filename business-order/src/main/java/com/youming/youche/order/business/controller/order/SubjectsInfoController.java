package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.order.api.order.ISubjectsInfoService;
import com.youming.youche.order.domain.order.SubjectsInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("subjects-info")
public class SubjectsInfoController extends BaseController<SubjectsInfo, ISubjectsInfoService> {
    @DubboReference(version = "1.0.0")
    ISubjectsInfoService subjectsInfoService;
    @Override
    public ISubjectsInfoService getService() {
        return subjectsInfoService;
    }
}
