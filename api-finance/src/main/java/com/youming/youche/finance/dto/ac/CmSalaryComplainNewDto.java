package com.youming.youche.finance.dto.ac;

import com.youming.youche.finance.domain.ac.CmSalaryComplainNew;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
* <p>
    * 
    * </p>
* @author zengwen
* @since 2022-04-18
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmSalaryComplainNewDto extends CmSalaryComplainNew {

    /**
     * 状态名称
     */
    private String stateName;

    /**
     * 审核结果
     */
    private String complainResult;
}
