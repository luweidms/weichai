package com.youming.youche.order.dto;


import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class ClaimExpenseInfoDto extends ClaimExpenseInfo implements Serializable {
	/**
	 * 費用
	 */
	private Double amountDouble;

	/**
	 * 申请人id
	 */
	private Long accUserId;
//	/**
//	 * 用戶名
//	 */
//	private String userName;

	/**
	 * 是否具有审核权限
	 */
	private Boolean isHasPermission;
	/**
	 * 票据类型
	 */
	private String isNeedBillName;


}
