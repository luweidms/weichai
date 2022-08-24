package com.youming.youche.record.domain.user;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 司机里程模式表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserSalaryInfo extends BaseDomain {

	private static final long serialVersionUID = 1L;


	/**
	 * 归属用户ID
	 */
	private Long userId;

	/**
	 * 归属用户姓名
	 */
	private String userName;

	/**
	 * 模式，1里程模式 2按趟模式
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Integer salaryPattern;

	/**
	 * 起始数
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Double startNum;

	/**
	 * 结束数
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Double endNum;

	/**
	 * 单价
	 */
	@TableField(updateStrategy = FieldStrategy.IGNORED)
	private Double price;

	/**
	 * 操作人,对应sys_operator的operator_id
	 */
	private Long opId;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createDate;

	/**
	 * 租户ID
	 */
	private Long tenantId;

}
