package com.youming.youche.finance.api;

import com.youming.youche.finance.domain.AccountStatementTemplate;
import com.youming.youche.finance.domain.AccountStatementTemplateField;
import com.youming.youche.commons.base.IBaseService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author zengwen
* @since 2022-04-14
*/
    public interface IAccountStatementTemplateFieldService extends IBaseService<AccountStatementTemplateField> {

    AccountStatementTemplate getTenantTemplate(Long ver, long tenantId);

    /**
     * 根据ver获取模板信息
     * @param ver
     * @param tenantId
     * @return
     */
    List<AccountStatementTemplateField> getFieldsByVer(Long ver, long tenantId);
    }
