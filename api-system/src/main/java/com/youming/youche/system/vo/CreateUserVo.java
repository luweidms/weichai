package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class CreateUserVo implements Serializable {

	private static final long serialVersionUID = 5067707171408933827L;

	@NotBlank
	private String loginAcct;

	/**
	 * 账号状态 1启用 2关闭
	 */
	@Min(0)
	private Integer lockFlag;

	/**
	 * 密码
	 */
	@NotBlank
	private String password;

	/**
	 * 员工姓名
	 */
	@NotBlank
	private String linkman;

	/**
	 * 员工身份证
	 */
	@NotBlank
	private String identification;

	/**
	 * 员工工号
	 */
	private String employeeNumber;

	/**
	 * 员工职位
	 */
	private String staffPosition;

	/**
	 * 部门id 多个已逗号隔开
	 */
	@NotBlank
	private String orgIds;

}
