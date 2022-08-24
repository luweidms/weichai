package com.youming.youche.finance.constant;

/**
 *
 *
 * Copyright: Copyright (c) 2018 zxkj
 *
 * @ClassName: com.business.consts.ClaimExpenseConsts.java
 * @Description: 报销枚举值
 * @version: v1.1.0
 * @author: liujl
 * @date:2018年5月7日
 *
 * Modification History: Date Author Version Description
 * --------------------------------------------------- --------- 2018年5月7日 liujl v1.1.0
 * 修改原因
 */
public class ClaimExpenseConsts {

	/**
	 * 报销分类类型
	 *
	 * @author huangqb
	 *
	 */
	public static class EXPENSE_TYPE {

		public static final int VEHICLE = 1;// 车管报销

		public static final int DRIVER = 2;// 司机报销

	}

	/**
	 * 报销状态
	 *
	 * @author huangqb
	 *
	 */
	public static class EXPENSE_STS {

		public static final int WAIT_AUDIT = 1;// 待审核

		public static final int UNDER_AUDIT = 2;// 审核中

		public static final int AUDIT_PASS = 3;// 审核通过

		public static final int AUDIT_NOT_PASS = 4;// 审核不通过

		public static final int CANCEL = 5;// 取消

	}

}
