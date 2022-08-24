package com.youming.youche.finance.provider.mapper.ac;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.ac.CmTemplateField;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 模板版本字段表Mapper接口
 * </p>
 *
 * @author zengwen
 * @Date 2022-04-11
 */
public interface CmTemplateFieldMapper extends BaseMapper<CmTemplateField> {

    /**
     * 获取模板字段
     *
     * @param templateId
     * @param tableName
     * @param isCancel
     * @param isDefault
     * @return
     */
    List<CmTemplateField> getCmTemplateFiel(@Param("templateId") Long templateId, @Param("tableName") String tableName, @Param("isCancel") Integer isCancel, @Param("isDefault") Integer isDefault);

    /**
     * 保存新模板
     * @param cmSalaryTemplate
     */
    void addTemplateNew(@Param("cmSalaryTemplate") CmSalaryTemplate cmSalaryTemplate);

    /**
     *  保存新模板数据
     * @param cmTemplateField
     */
    void  addTemplateField(@Param("cmTemplateField") CmTemplateField cmTemplateField);

    List<CmTemplateField> getCmSalaryTemplateByTmpId();

    List<CmTemplateField> getCmSalaryTemplateByMon(@Param("tenantId") Long tenantId, @Param("month") String month);
}
