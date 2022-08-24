package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.domain.AccountStatementTemplate;
import com.youming.youche.finance.api.IAccountStatementTemplateService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.provider.mapper.AccountStatementTemplateMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-04-14
*/
@DubboService(version = "1.0.0")
    public class AccountStatementTemplateServiceImpl extends BaseServiceImpl<AccountStatementTemplateMapper, AccountStatementTemplate> implements IAccountStatementTemplateService {


}
