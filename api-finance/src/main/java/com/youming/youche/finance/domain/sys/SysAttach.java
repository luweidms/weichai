package com.youming.youche.finance.domain.sys;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 图片资源表
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysAttach extends BaseDomain {

	/**
	 * 主键
	 */
	private Long id;

	//  老表 的id
//	private Long flowId;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 文件类型
	 */
	private String fileType;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 存储路径
	 */
	private String storePath;

	/**
	 * 操作Id
	 */
	private Long opId;

	/**
	 * 创建时间
	 */
	private LocalDateTime createDate;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 租户Id
	 */
	private Long tenantId;

}
