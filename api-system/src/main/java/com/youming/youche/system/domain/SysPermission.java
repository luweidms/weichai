package com.youming.youche.system.domain;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author Terry
 * @since 2021-12-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysPermission extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 权限名称
	 */
	private String name;

	/**
	 * 权限英文名称
	 */
	private String enname;

	/**
	 * 备注
	 */
	private String description;

	/**
	 * 权限类型：1菜单2操作权限
	 */
	private Integer type;

}
