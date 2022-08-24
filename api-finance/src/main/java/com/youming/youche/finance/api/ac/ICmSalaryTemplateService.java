package com.youming.youche.finance.api.ac;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.ac.CmTemplateField;

import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/11 16:58
 */
public interface ICmSalaryTemplateService extends IBaseService<CmSalaryTemplate> {

    /**
     * 获取租户的自定义工资单模板
     * @param accessToken
     * @return
     */
    List<CmTemplateField> getMaxTemplateFiel(String accessToken);

    /**
     * 查询模板所有字段
     * @param accessToken
     * @param settleMonth
     * @return
     */
    List<CmTemplateField> getTemplateAllField(String accessToken, String settleMonth);

    /**
     * 获取指定月份的模板信息
     */
    List<CmTemplateField> getCmSalaryTemplateByMon(Long tenantId, String settleMonth);
}
