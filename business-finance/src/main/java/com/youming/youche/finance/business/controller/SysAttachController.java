package com.youming.youche.finance.business.controller;


import com.youming.youche.finance.api.ISysAttachService;
import com.youming.youche.finance.domain.sys.SysAttach;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
* <p>
* 图片资源表 前端控制器
* </p>
* @author luona
* @since 2022-04-14
*/
    @RestController
@RequestMapping("sys-attach")
        public class SysAttachController extends BaseController<SysAttach, ISysAttachService> {
    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;
    @Override
    public ISysAttachService getService() {
        return iSysAttachService;
    }
}
