package com.youming.youche.finance.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 支付限制表
 * </p>
 *
 * @author hzx
 * @since 2022-04-14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AcBusiOrderLimitRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 业务类型
     */
    private Long businessId;

    /**
     * 科目id
     */
    private Long subjectsId;

    /**
     * 映射对象类型  0已收已付操作limit表字段 1配置已收已付科目  3配置应收应付科目 3应收应付操作limit表字段
     */
    private Integer relType;

    /**
     * 映射对象
     */
    private String relObj;

    /**
     * 类型
     */
    private String operType;

    /**
     * 租户id
     */
    private Long tenantId;


}
