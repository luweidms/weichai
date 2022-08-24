package com.youming.youche.finance.provider.mapper.ac;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;

/**
 * <p>
 * 模板版本表Mapper接口
 * </p>
 *
 * @author zengwen
 * @Date 2022-04-11
 */
public interface CmSalaryTemplateMapper extends BaseMapper<CmSalaryTemplate> {


    CmSalaryTemplate getCmSalaryTemplate(Long tenantId);


    CmSalaryTemplate getDefaultCmSalaryTemplate();


}
