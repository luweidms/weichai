package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SubjectsInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 处理备注
     */
    private String note;
    /**
     * 科目名称
     */
    private String subjectsName;
    /**
     * 状态
     */
    private Integer subjectsStatus;
    /**
     * 科目类型
     */
    private Integer subjectsType;
    /**
     * 车队id
     */
    private Long tenantId;


}
