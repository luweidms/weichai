package com.youming.youche.conts;


/**
 * 订单审核的枚举
 * @author liyiye
 *
 */
public class AuditConsts {


	
	public static class CallBackMapKey{
		public static final String  NODE_NUM = "_node_num"; 
	}
	
	/**
	 * 审核的业务编码的枚举
	 * @author liyiye
	 *
	 */
	public static class AUDIT_CODE {
		/**异常审核业务ID*/
		public static final String  PROBLEM_CODE = "300001"; 
		/**订单修改审核业务ID*/
		public static final String  ORDER_UPDATE_CODE = "300002"; 
		/**时效罚款审核业务ID*/
		public static final String  ORDER_AGING_CODE = "300004"; 
		/**时效申诉审核业务ID*/
		public static final String  AGING_APPEAL_CODE = "300005"; 
		public static final String AUDIT_CODE_USER = "100002";//司机档案
		public static final String AUDIT_CODE_APPLY = "100011";//邀请司机
		public static final String AUDIT_CODE_CAPITALCONTROL = "100003";//资金风控
		public static final String STAFF_BORROW = "21000021";//员工借支
		public static final String TUBE_BORROW = "21000022";//车管借支
		public static final String DRIVER_BORROW = "21000023";//司机借支
		public static final String TubeExpense = "21000100";//车管报销
		public static final String DriverExpense = "21000101";//司机报销

		public static final String SERVICE_INFO = "400000";//服务商审核
		public static final String SERVICE_PRODUCT = "400001";//后服站点审核
		public static final String REPAIR_INFO = "400002";//维修保养审核
		public static final String VIOLATION_MANAGE = "400007";//违章管理
		public static final String SERVICE_REPAIR_INFO = "400008";//对接神汽-维修保养

		public static final String AUDIT_CODE_VEHICLE = "100005";//车辆档案
		public static final String AUDIT_CODE_APPLY_VEHICLE = "100006";//邀请车辆
		public static final String AUDIT_CODE_TRAILER = "100009";//挂车档案
		public static final String AUDIT_CODE_CUST_LINE = "100001";//客户线路
		public static final String AUDIT_CODE_CUST = "100000";//客户档案
		public static final String ORDER_PRICE_CODE = "300003"; //订单价格
		public static final String ORDER_COST_REPORT = "22000022"; //费用上报
		public static final String PAY_MANAGER = "22000023"; //付款管理
		
		/**支付预付款审核业务ID*/
		public static final String  ORDER_PAY_PRE_FEE_CODE = "300006"; 
		/**支付到付款审核业务ID*/
		public static final String  ORDER_PAY_ARRIVE_FEE_CODE = "300007"; 
		/**支付尾款审核业务ID*/
		public static final String  ORDER_PAY_FINAL_FEE_CODE = "300008"; 
		/**现金付款业务ID**/
		public static final String PAY_CASH_CODE = "230003";
		/**车辆费用审核ID**/
		public static final String VEHICLE_EXPENSE_CODE = "500005";
	}
	/**
	 * 流程审核的结果
	 * @author liyiye
	 *
	 */
	public static class RESULT{
		/**待审核*/
		public static final int TO_AUDIT = 0; 
		/**审核通过*/
		public static final int SUCCESS = 1; 
		/**审核不通过*/
		public static final int FAIL = 2; 
		/**审核取消*/
		public static final int CANCEL = 3; 
	}
	/**
	 * 规则的类型
	 * @author liyiye
	 *
	 */
	public static class RuleType{
		/**大于*/
		public static final int GT = 1; 
		/**大于等于*/
		public static final int GE = 2; 
		/**等于*/
		public static final int EQ = 3; 
		/**小于*/
		public static final int LT = 4; 
		/**小于等于*/
		public static final int LE = 5; 
	}
	/**
	 * 
	 * @author liyiye
	 *
	 */
	public static class Status{
		/**审核流程完成*/
		public static final int FINISH = 0; 
		/**流程审核中*/
		public static final int  AUDITING= 1; 
	}
	
	/**
	 * 节点的审核的对象的类型
	 * @author liyiye
	 *
	 */
	public static class TargetObjType{
		/**角色类型*/
		public static final int ROLE_TYPE = 0; 
		/**组织类型*/
		public static final int  ORG_TYPE= 1; 
		/**用户类型*/
		public static final int  USER_TYPE= 2; 
		
	}

	public static class busiType{
		/**基础*/
		public static final int  BASE= 1;
		/**业务*/
		public static final int  BUSI= 2;
	}

	public static class RULE_CODE{
		/*中标价超出指导价*/
		public static final String EXCEED_GUIDE_PRICE ="100000";
		/*高于预付标准*/
		public static final String HIGHER_PREPAYMENT ="100001";
		/*高于油卡标准*/
		public static final String HIGHER_OIL ="100003";
		/*低于油卡标准*/
		public static final String LOWER_OIL ="100004";
		/*高于ETC标准*/
		public static final String HIGHER_ETC ="100005";
		/*低于ETC标准*/
		public static final String LOWER_ETC ="100006";
	}
	/**
	 * 规则的对应的key
	 * @author liyiye
	 *
	 */
	public static class RuleMapKey{
		/**指导价**/
		public static final String  GUIDE_PRICE = "GUIDE_PRICE"; 
		/**中标价，总费用*/
		public static final String TOTAL_FEE="TOTAL_FEE";
		/**预付总计的标准比例**/
		public static final String PRE_TOTAL_SCALE_STANDARD ="PRE_TOTAL_SCALE_STANDARD";
		/**预付总计的实际比例**/
		public static final String PRE_TOTAL_SCALE ="PRE_TOTAL_SCALE";
		/***油卡比例标准**/
		public static final String PRE_OIL_TOTAL_STANDARD="PRE_OIL_TOTAL_STANDARD";
		/***油卡实际比例**/
		public static final String PRE_OIL_TOTAL="PRE_OIL_TOTA";
		/***ETC比例标准**/
		public static final String PRE_ETC_TOTAL_STANDARD="PRE_ETC_TOTAL_STANDARD";
		/***ETC实际比例**/
		public static final String PRE_ETC_TOTAL="PRE_ETC_TOTAL";
		
		/**是否更新*/
		public static final String IS_UPDATE="IS_UPDATE";
	}
	/**
	 * 
	 * 现金打款的流程的特殊处理
	 *
	 */
	public static class OperType{
		/**web 操作**/
		public static final int  WEB = 1; 
		/**task 操作**/
		public static final int  TASK = 2; 
	}
}
