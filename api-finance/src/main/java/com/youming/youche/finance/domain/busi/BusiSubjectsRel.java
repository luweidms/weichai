package com.youming.youche.finance.domain.busi;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
    * 
    * </p>
* @author Terry
* @since 2022-03-08
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BusiSubjectsRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Long busiCode;

    private Integer bookMold;

    private String bookType;

    private Long businessId;

    private Long businessType;

    private Long disSubjectsId;

    private String remarks;

    private Integer rootInoutType;

    private Integer ruleType;

    private String ruleValue;

    private Integer sortid;

    private Integer status;

    private Long subjectsId;

    private Long tenantId;

    @TableField(exist = false)
    private Long amountFee;

}
