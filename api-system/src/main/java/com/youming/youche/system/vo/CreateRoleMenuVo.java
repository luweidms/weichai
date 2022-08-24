package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

@Data
public class CreateRoleMenuVo implements Serializable {

	private static final long serialVersionUID = 3597734548245029622L;

	@NotBlank(message = "请输入角色名称")
	private String roleName;

//	private String menuIds;

	private List<Long> buttons;
	private List<Long> menus;

}
