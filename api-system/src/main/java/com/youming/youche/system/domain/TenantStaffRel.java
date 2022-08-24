package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 员工信息
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantStaffRel extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户编号
	 */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long userInfoId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
	private Long tenantId;

	/**
	 * 员工姓名
	 */
	private String staffName;

	/**
	 * 员工工号
	 */
	private String employeeNumber;

	/**
	 * 职位
	 */
	private String staffPosition;

	/**
	 * 是否启用（1：启用，2停用）（对应静态数据LOCK_FLAG）
	 */
	private Integer lockFlag;

	/**
	 * 是否删除，0：已删除，1：未删除
	 */
	private Integer state;
	/**
	 * 跟单人手机号
	 */
	@TableField(exist=false)
	private String mobilePhone;

}
