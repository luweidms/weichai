package com.youming.youche.system.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 车队档案列表页的查询参数
 */
@Data
@Accessors(chain = true)
public class TenantQueryVo implements Serializable {

	private String startTime;

	private String endTime;

	private String linkPhone;

	private String companyName;

	private String actualController;

	private Integer state;

	private Integer frozenState;

	/** 售前售后跟踪 2018-6-14 */
	private String preSaleServiceName;

	private String afterSaleServiceName;

	private Integer ownCarStart;

	private Integer ownCarEnd;

	private Integer otherCarStart;

	private Integer otherCarEnd;

	private Integer payState;

	/**
	 * 归属部门
	 */
	private Long orgId;

	private Integer virtualState;

}
