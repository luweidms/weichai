package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class DelSysUserRoleVo implements Serializable {

	private static final long serialVersionUID = 6974623877591456108L;

	@NotNull(message = "请选择授权角色")
	@Min(0)
	private Long roleId;

	@NotBlank(message = "请选择授权员工")
	private String userIds;

}
