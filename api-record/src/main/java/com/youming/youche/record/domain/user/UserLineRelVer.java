package com.youming.youche.record.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户心愿线路版本表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserLineRelVer extends BaseDomain {

	private static final long serialVersionUID = 1L;



	/**
	 * 关系ID
	 */
	private Long relId;

	/**
	 * 用户编号
	 */
	private Long userId;

	/**
	 * 线路ID
	 */
	private Long lineId;

	/**
	 * 线路编号
	 */
	private String lineCodeRule;

	/**
	 * 操作人员
	 */
	private Long opId;

	/**
	 * 操作时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime opDate;

	/**
	 * 修改时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateDate;

	/**
	 * 修改操作人
	 */
	private Long updateOpId;

	/**
	 * 线路状态
	 */
	private Integer state;

	/**
	 * 0不可用 1可用 9被移除
	 */
	private Integer verState;

	/**
	 * 车队id
	 */
	private Long tenantId;

}
