package com.youming.youche.finance.provider.mapper;

import com.youming.youche.finance.domain.AccountStatementTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* <p>
* Mapper接口
* </p>
* @author zengwen
* @since 2022-04-14
*/
    public interface AccountStatementTemplateMapper extends BaseMapper<AccountStatementTemplate> {

    AccountStatementTemplate getTemplateByVer(@Param("ver") long ver, @Param("tenantId") long tenantId);

    AccountStatementTemplate getLastTemplatByTenant(@Param("tenantId") long tenantId);

    }
