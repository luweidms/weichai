package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.finance.domain.AccountStatementTemplate;
import com.youming.youche.finance.domain.AccountStatementTemplateField;
import com.youming.youche.finance.api.IAccountStatementTemplateFieldService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.provider.mapper.AccountStatementTemplateFieldMapper;
import com.youming.youche.finance.provider.mapper.AccountStatementTemplateMapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-04-14
*/
@DubboService(version = "1.0.0")
    public class AccountStatementTemplateFieldServiceImpl extends BaseServiceImpl<AccountStatementTemplateFieldMapper, AccountStatementTemplateField> implements IAccountStatementTemplateFieldService {

    @Resource
    private AccountStatementTemplateMapper accountStatementTemplateMapper;

    public AccountStatementTemplate getTenantLastTemplate(long tenantId) {
        // 获取租户最新的一个模板
        AccountStatementTemplate accountStatementTemplate = accountStatementTemplateMapper.getLastTemplatByTenant(tenantId);

        if (accountStatementTemplate == null) {
            accountStatementTemplate = this.getDefaultTemplate();
        }
        return accountStatementTemplate;
    }


    @Override
    public AccountStatementTemplate getTenantTemplate(Long ver, long tenantId) {
        if(ver==null||ver<=0L){
            return this.getTenantLastTemplate(tenantId);
        }

        AccountStatementTemplate accountStatementTemplate = accountStatementTemplateMapper.getTemplateByVer(ver, tenantId);

        if(accountStatementTemplate==null){//如果没有获取到就取最新模板
            accountStatementTemplate = this.getTenantLastTemplate(tenantId);
        }

        return accountStatementTemplate;
    }

    public AccountStatementTemplate getDefaultTemplate() {
        return accountStatementTemplateMapper.selectById(1L);
    }

    @Override
    public List<AccountStatementTemplateField> getFieldsByVer(Long ver, long tenantId) {
        AccountStatementTemplate accountStatementTemplate = this.getTenantTemplate(ver, tenantId);

        LambdaQueryWrapper<AccountStatementTemplateField> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatementTemplateField::getTemplateId, accountStatementTemplate.getId());
        queryWrapper.orderByAsc(AccountStatementTemplateField::getFieldIndex);
        return baseMapper.selectList(queryWrapper);
    }
}
