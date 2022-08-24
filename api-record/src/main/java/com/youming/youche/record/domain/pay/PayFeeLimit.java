package com.youming.youche.record.domain.pay;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 付款限制表
 * </p>
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class PayFeeLimit extends BaseDomain {

	private static final long serialVersionUID = 1L;


	/**
	 * 类别(详情见表sys_static_data字段PAY_FEE_LIMIT_TYPE)
	 */
	private Integer type;

	/**
	 * 科目(详情见表sys_static_data字段PAY_FEE_LIMIT_SUB_TYPE)
	 */
	private Integer subType;

	/**
	 * 科目名称
	 */
	private String subTypeName;

	/**
	 * 上限值(涉及钱的科目保留单位为分)
	 */
	private Long value;

	/**
	 * 创建时间
	 */
	private LocalDateTime createDate;

	/**
	 * 操作人id
	 */
	private Long opId;

	/**
	 * 操作人姓名
	 */
	private String opName;

	/**
	 * 操作日期
	 */
	private LocalDateTime opDate;

	/**
	 * 操作备注
	 */
	private String remark;

	/**
	 * 车队id
	 */
	private Long tenantId;

}
