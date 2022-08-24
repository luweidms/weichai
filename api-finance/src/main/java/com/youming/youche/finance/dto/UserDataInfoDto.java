package com.youming.youche.finance.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor(staticName = "of")
public class UserDataInfoDto implements Serializable {

	private static final long serialVersionUID = 6464874712466007048L;

	private Long id;

	/**
	 * 联系人姓名
	 */
	private String linkman;

	/**
	 * 联系人手机
	 */
	private String mobilePhone;

}
