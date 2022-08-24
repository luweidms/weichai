package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户组织关系表
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysUserOrgRel extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户ID
	 */
	private Long userInfoId;

	/**
	 * 组织（部门）ID
	 */
	private Long orgId;

	/**
	 * 数据状态 0无效 1有效
	 */
	private Integer state;

	/**
	 * 操作员ID
	 */
	private Long opId;

	/**
	 * 操作时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime opDate;

	private Long tenantId;

}
