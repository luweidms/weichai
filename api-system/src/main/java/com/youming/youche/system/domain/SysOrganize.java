package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织表
 * </p>
 *
 * @author Terry
 * @since 2022-01-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysOrganize extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 组织名称
     */
    @NotBlank(message = "请上传部门名称")
    private String orgName;

    /**
     * 操作员编号
     */
    private Long opId;

    /**
     * 上级组织编号
     */
    @NotNull(message = "请上传部门名称")
    private Long parentOrgId;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 状态;默认0(不可用),1(可用)
     */
    private Integer state;

    /**
     * 部门负责人ID
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userInfoId;

    /**
     * 部门负责人姓名
     */
    @NotBlank(message = "请上传负责人姓名")
    private String linkMan;

    @TableField(exist = false)
    private List<SysOrganize> children = new ArrayList<>();

}
