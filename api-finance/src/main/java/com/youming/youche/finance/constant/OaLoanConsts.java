package com.youming.youche.finance.constant;

/**
 *
 *
 * Copyright: Copyright (c) 2018 zxkj
 *
 * @ClassName: com.business.consts.OaLoanConsts.java
 * @Description: 借支枚举值
 *
 * @version: v1.1.0
 * @author: huangqb
 * @date:2018年4月13日下午8:30:14
 *
 * 							Modification History: Date Author Version
 *                           Description
 *                           ---------------------------------------------------
 *                           --------- 2018年4月13日 huangqb v1.1.0 修改原因
 */
public class OaLoanConsts {

	/**
	 * 借支类型
	 *
	 * @author huangqb
	 *
	 */
	public static class LOAN_TYPE {
		public static final int LOANTYPE0 = 0;// 费用类借支
		public static final int LOANTYPE1 = 1;// 费用备用金
		public static final int LOANTYPE2 = 2;// 成本类借支
		public static final int LOANTYPE3 = 3;// 成本备用金
	}

	/**
	 *
	 */
	public static class QUERY_TYPE {
		public static final int advances = 1;// 员工借支
		public static final int vehicle = 2;// 车管借支
		public static final int driver = 3;// 司机借支
	}

	/**
	 * 科目类型
	 *
	 * @author huangqb
	 *
	 */
	public static class LOAN_SUBJECT {
		public static final int LOANSUBJECT1 = 1;// 维修费 2
		public static final int LOANSUBJECT2 = 2;// 轮胎费 2
		public static final int LOANSUBJECT3 = 3;// 停车费 1
		public static final int LOANSUBJECT4 = 4;// 过磅费 1
		public static final int LOANSUBJECT5 = 5;// 违章罚款 2
		public static final int LOANSUBJECT6 = 6;// 路桥费 1
		public static final int LOANSUBJECT7 = 7;// 保养费 2
		public static final int LOANSUBJECT8 = 8;// 油费 1
		public static final int LOANSUBJECT9 = 9;// 车祸事故 2
		public static final int LOANSUBJECT10 = 10;// 出险预报 2
		public static final int LOANSUBJECT11 = 11;// 差旅费 1
	}

	/**
	 * 借支分类类型
	 *
	 * @author huangqb
	 *
	 */
	public static class LOAN_CLASSIFY {
		public static final int LOAN_CLASSIFY1 = 1;// 内部借支
		public static final int LOAN_CLASSIFY2 = 2;// 车管中心借支
	}

	/**
	 * 审核状态
	 *
	 * @author huangqb
	 *
	 */
	public static class STS {
		public static final int STS0 = 0;// 待审核
		public static final int STS1 = 1;// 审核中
		public static final int STS2 = 2;// 审核未通过
		public static final int STS3 = 3;// 未核销
		public static final int STS4 = 4;// 核销中
		public static final int STS5 = 5;// 已核销
		public static final int STS6 = 6;// 已审核
		public static final int STS7 = 7;// 逾期
		public static final int STS8 = 8;// 撤销
		public static final int STS9 = 9;// 失效
	}
	/**
	 * 借支发起
	 *
	 * @author huangqb
	 *
	 */
	public static class LAUNCH{
		public static final int LAUNCH1 = 1;//1车管中心
		public static final int LAUNCH2 = 2;//2司机
	}


	/**
	 * 报销分类类型
	 *
	 * @author liujl
	 *
	 */
	public static class EXPENSE_TYPE {
		public static final int VEHICLE = 1;// 车管报销
		public static final int DRIVER = 2;// 司机报销
	}

}
