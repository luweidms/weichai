package com.youming.youche.finance.provider.service;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.api.ISysAttachService;
import com.youming.youche.finance.domain.sys.SysAttach;
import com.youming.youche.finance.provider.mapper.SysAttachMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;


/**
* <p>
    * 图片资源表 服务实现类
    * </p>
* @author luona
* @since 2022-04-14
*/
@DubboService(version = "1.0.0")
    public class SysAttachServiceImpl extends BaseServiceImpl<SysAttachMapper, SysAttach> implements ISysAttachService {


    }
