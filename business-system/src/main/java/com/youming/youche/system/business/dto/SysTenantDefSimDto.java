package com.youming.youche.system.business.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@RequiredArgsConstructor(staticName = "of")
@Accessors(chain = true)
public class SysTenantDefSimDto implements Serializable {


	private static final long serialVersionUID = -465823680196774188L;
	private Long id;

	/**
	 * 租户CODE
	 */
	private String tenantCode;

	/**
	 * 租户名称(车队全称)
	 */
	private String name;

	/**
	 * 车队简称
	 */
	private String shortName;

	/**
	 * 常用办公地址
	 */
	private String address;

	private String logo;

	/**
	 * 租户联系人
	 */
	private String linkMan;

	/**
	 * 租户联系人电话
	 */
	private String linkPhone;

	/**
	 * 租户联系人邮箱
	 */
	private String linkEmail;

	/**
	 * 租户域名配置
	 */
	private String domain;

	/**
	 * 租户皮肤配置
	 */
	private String style;

	private Long adminUser;

}
