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
 * @since 2022-03-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AcBusiOrderLimitRel extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 业务id
     */
    private Long businessId;

    /**
     * 业务操作类型
     */
    private String operType;

    /**
     * 科目ID
     */
    private String relObj;

    /**
     * 科目类型
     */
    private Integer relType;

    /**
     * 科目id
     */
    private Long subjectsId;

    /**
     * 车队id
     */
    private Long tenantId;


}
