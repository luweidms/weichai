package com.youming.youche.finance.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 车管类目表
 * </p>
 *
 * @author Terry
 * @since 2022-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(excludeProperty = { "createTime", "updateTime" })
public class ClaimExpenseCategory extends BaseDomain {

	private static final long serialVersionUID = 1L;

	/**
	 * 类目值
	 */
	private Integer cateValue;

	/**
	 * 类目名称
	 */
	private String cateName;

	/**
	 * 类目等级 1 一级类目 2 二级类目
	 */
	private Integer cateLevel;

	/**
	 * 类目状态
	 */
	private Integer state;

	/**
	 * 上级类目
	 */
	private Long parentCateId;

	/**
	 * 租户ID
	 */
	private Long tenantId;

}
