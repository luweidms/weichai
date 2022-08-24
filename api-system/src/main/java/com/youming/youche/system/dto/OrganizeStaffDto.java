package com.youming.youche.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class OrganizeStaffDto implements Serializable {

	/**
	 * 登录账号id/操作人Id
	 */
	private Long userId;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createTime;

	/** 登录账号 */
	private String loginacct;

	/** 用户信息id */
	private Long userInfoId;

	/** 用户姓名 */
	private String linkman;

	/** 用户职位 */
	private String staffPosition;

}
