package com.youming.youche.finance.provider.service;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.IOaFilesService;
import com.youming.youche.finance.domain.OaFiles;
import com.youming.youche.finance.provider.mapper.OaFilesMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 借支报销文件表 服务实现类
    * </p>
* @author luona
* @since 2022-04-14
*/
@DubboService(version = "1.0.0")
    public class OaFilesServiceImpl extends BaseServiceImpl<OaFilesMapper, OaFiles> implements IOaFilesService {


    }
