package com.youming.youche.finance.constant;

public class FinanceBusinessConstant {

	/**
	 * 费用上报审核流程 统计类型 //queryType : 1待审核，2审核中，3待支付，4已完成
	 */
	public static class EXPENSE_TYPE {

		/** 待处理 **/
		public static final Integer WAIT = 1;

		/** 进行中 **/
		public static final Integer CHECH = 2;

		/** 已完成（历史） */
		public static final Integer PAY = 3;

		/** 完成 */
		public static final Integer FINISH = 4;

	}

}
