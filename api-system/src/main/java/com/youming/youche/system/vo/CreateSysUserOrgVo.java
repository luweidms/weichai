package com.youming.youche.system.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class CreateSysUserOrgVo implements Serializable {

	private static final long serialVersionUID = 7504460201388569015L;

	@NotBlank(message = "请输入用户信息id")
	private String userInfoIds;

	@NotNull
	private Long orgId;

}
