package com.youming.youche.finance.business.controller;


import com.youming.youche.finance.api.IOaFilesService;
import com.youming.youche.finance.domain.OaFiles;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.RequestMapping;

    import org.springframework.web.bind.annotation.RestController;
    import com.youming.youche.commons.base.BaseController;

/**
* <p>
* 借支报销文件表 前端控制器
* </p>
* @author luona
* @since 2022-04-14
*/
    @RestController
@RequestMapping("oa/files")
        public class OaFilesController extends BaseController<OaFiles, IOaFilesService> {

    @DubboReference(version = "1.0.0")
    IOaFilesService iOaFilesService;
    @Override
    public IOaFilesService getService() {
        return iOaFilesService;
    }
}
