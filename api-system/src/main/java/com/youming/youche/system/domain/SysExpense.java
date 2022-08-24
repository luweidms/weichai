package com.youming.youche.system.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * <p>
 * 费用类型
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysExpense extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 费用类型
     */
    @NotBlank(message = "请输入费用名称")
    private String name;

    /**
     * 必填附件：1非必填 2必填
     */
    @Valid
    @NotNull(message = "请选择是否必填")
    @Min(value = 1, message = "请输入合理的值")
    private Integer required;

    /**
     * 操作人id
     */
    private Long opId;


}
