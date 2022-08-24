package com.youming.youche.conts;

/**
 * Created by dacheng on 2018/3/27.
 */
public class OrderAccountConst {

    public static  class ORDER_BY{
        public static final String BALANCE = "blance";
        public static final String MARGIN_BALANCE = "marginBalance";
        public static final String OIL_BALANCE = "oilBalance";
        public static final String ETC_BALANCE = "etcBalance";
        public static final String REPAIR_FUND = "repairFund";
        public static final String VEHICLE_AFFILIATION = "vehicleAffiliation";
    }

    //订单限制表未付字段
    public static class NO_PAY{
        public static final String NO_PAY_CASH = "NoPayCash";
        public static final String NO_PAY_OIL = "NoPayOil";
        public static final String NO_PAY_ETC = "NoPayEtc";
        public static final String NO_PAY_FINAL = "NoPayFinal";
        public static final String NO_PAY_DEBT = "NoPayDebt";
        public static final String ACCOUNT_BALANCE = "accountBalance"; 
    }

    //油卡类型
    public static class OIL_CARD_TYPE{
        //混合油卡
        public static final int OIL_CARD_TYPE3 = 3;
    }
    
    //redis key
    public static class AC_REDIS_KEY{
    	//加油key
    	public static final String PAY_FOR_OIL = ":payForOil";
    }

    //会员等级
    public static class USER_CREDIT{
        public static final String LEVEL1 = "1";
        public static final String LEVEL2 = "2";
        public static final String LEVEL3 = "3";
        public static final String LEVEL4 = "4";
    }
    
    //账户状态
    public static class ORDER_ACCOUNT_STATE {
    	//无效
    	public static final int STATE0 = 0;
    	//有效
    	public static final int STATE1 = 1;
    }
    
    public static class SYS_OIL_INFO {
    	//无效
    	public static final int STATE0 = 0;
    	//有效
    	public static final int STATE1 = 1;
    }
    
    //订单限制表是否到期
    public static class ORDER_LIMIT_STS {
    	//初始化
    	public static final int FIANL_STS0 = 0;
    	//已到期
    	public static final int FIANL_STS1 = 1;
    	//未到期转已到期失败
    	public static final int FIANL_STS2 = 2;

    	//全部业务类型（订单尾款到期、油到期、维修到期）
    	public static final String DRIVER_SIGN0 = "0";
    	//司机订单
    	public static final String DRIVER_SIGN1 = "1";
    	//服务商(油老板)
    	public static final String SERVICE_SIGN2 = "2";
    	//服务商(维修商)
    	public static final String SERVICE_SIGN3 = "3";
    	
    }
    //到期款类型
    public static class MARGIN_TYPE {
    	//尾款到期
    	public static final int FIANL_MARGIN = 1;
    	//油到期
    	public static final int OIL_MARGIN = 2;
    	//维修保养费到期
    	public static final int REPAIR_MARGIN = 3;
    	
    }
    public static class STATE {
    	//初始化
    	public static final int INIT = 0;
    	//成功
    	public static final int SUCCESS = 1;
    	//失败
    	public static final int FAIL = 2;
    }
    
    public static class TXN_TYPE {
    	//车队网商(车队网商对公打给司机、平台)
    	public static final String FLEET_TXN_TYPE = "000";
    	//实体-实体
    	public static final String PLATFORM_TXN_TYPE = "100";
		//200 虚拟-虚拟
		public static final String XX_TXN_TYPE = "200";
		//300 虚拟-实体
		public static final String XS_TXN_TYPE = "300";
    }
    public static class IS_DRIVER {
    	//司机提现
    	public static final int DRIVER = 0;
    	//服务商提现
    	public static final int SERVICE = 1;
    	//车队
    	public static final int FLEET = 2;
    	//车队往平台对公账户
    	public static final int PLATFORM = 3;
    	//车队往平台对公账户打用户服务费
    	public static final int PLATFORM_USER_SERVICE = 4;
    	//平台收益提现(票据服务费)
    	public static final int PLATFORM_BILL_SERVICE = 5;
    }
    //资金渠道
    public static class VEHICLE_AFFILIATION {
    	/*//群鹰
    	public static final String VEHICLE_AFFILIATION1 = "1";
    	//握物流
    	public static final String VEHICLE_AFFILIATION2 = "2";
    	//志鸿
    	public static final String VEHICLE_AFFILIATION3 = "3";
    	//路歌
    	public static final String VEHICLE_AFFILIATION4 = "4";
    	//润宝祥
    	public static final String VEHICLE_AFFILIATION5 = "5";*/
    	
    	//不开票
    	public static final String VEHICLE_AFFILIATION0 = "0";
    	//承运方开票
    	public static final String VEHICLE_AFFILIATION1 = "1";
    	//平台开票(路哥)
    	public static final String VEHICLE_AFFILIATION2 = "2";
    	//平台开票(淮安)
    	public static final String VEHICLE_AFFILIATION10 = "10";
    	
    }
    
    public static class SOURCE_ORDER_OIL {
    	public static final String SOURCE_LIST = "sourceList";
    	public static final String RECHARGE_SOURCE_LIST = "rechargeSourceList";
    }
    
    public static class SERVICE_PRODUCT {
    	//有效
    	public static final int STATE1 = 1;
    	//无效
    	public static final int STATE2 = 2;
    }
    
    //服务商能否开票
    public static class SERVICE_OPEN_BILL {
    	//能
    	public static final int YES = 1;
    	//不能
    	public static final int NO = 2;
    }
    
    public static class TENANT_ID {
    	//无租户
    	public static final long NO_TENANT_ID = -1L;
    }
    //预支
    public static class IS_ADVANCE {
    	//1可以预支 、提现
    	public static final int YES = 1;
    	//0不可以预知,提现
    	public static final int NO = 0;
    }
    public static class ACCOUNT_KEY {
    	//总现金
    	public static final String totalBalance = "totalBalance";
		//总油
    	public static final String totalOilBalance = "totalOilBalance";
		//总ETC
    	public static final String totalEtcBalance = "totalEtcBalance";
		//总未到期
    	public static final String totalMarginBalance = "totalMarginBalance";
		//总欠款金额
    	public static final String totalDebtAmount = "totalDebtAmount";
		//冻结未到期金额
    	public static final String frozenMarginBalance = "frozenMarginBalance";
		//冻结现金
    	public static final String frozenBalance = "frozenBalance";
		//冻结油
    	public static final String frozenOilBalance = "frozenOilBalance";
		//冻结etc
    	public static final String frozenEtcBalance = "frozenEtcBalance";
		//可预支金额
    	public static final String canAdvance = "canAdvance";
		//不可预支金额
    	public static final String cannotAdvance = "cannotAdvance";
		//可使用金额
    	public static final String canUseBalance = "canUseBalance";
		//可使用油
    	public static final String canUseOilBalance = "canUseOilBalance";
		//可使用ETC
    	public static final String canUseEtcBalance = "canUseEtcBalance";
    	
    	//账户集合
    	public static final String orderAccount = "orderAccount";
    	public static final String orderAccountOut = "orderAccountOut";
    	
    	//订单集合
    	public static final String orderLimit = "orderLimit";
    	//是否开票
    	public static final String isNeedBill = "isNeedBill";
    	
    	//消费预支类型
    	public static final String advanceType = "advanceType";
    	
    	public static final String ConsumeOilFlowList = "ConsumeOilFlowList";
    	public static final String UserRepairMarginlist = "UserRepairMarginlist";
    }
    
    //订单是否需要开票
    public static class ORDER_IS_NEED_BILL {
    	//不开票
    	public static final int NO = 0;
    	//开票
    	public static final int YES = 1;
    }
    //订单开票类型
    public static class ORDER_BILL_TYPE {
    	//不开票
    	public static final int notNeedBill = 0;
    	//承运方开票
    	public static final int carrierBill = 1;
    	//平台开票
    	public static final int platformBill = 2;
    }
    public static class CONSUME_COST_TYPE {
    	//司机
    	public static final int TYPE1 = 1;
    	//油老板
    	public static final int TYPE2 = 2;
    	
    	//维修费司机付
    	public static final int REPAIR_TYPE1 = 1;
    	//维修费公司付
    	public static final int REPAIR_TYPE2 = 2;
    }
    
    public static class CONSUME_OIL_FLOW {
    	//未评价
    	public static final int IS_EVALUATE_NO = 0;
    	//已评价
    	public static final int IS_EVALUATE_YES = 1;
    }
    
    //是否系统自动打款
    public static class IS_AUTOMATIC {
    	//手动核销
    	public static final int AUTOMATIC0 = 0;
    	//系统自动打款
    	public static final int AUTOMATIC1 = 1;
    }
    //银行卡类型
    public static class BANK_TYPE {
    	//对私
    	public static final int TYPE0 = 0;
    	//网商对公
    	public static final int TYPE1 = 1;
    	//微信
    	public static final int TYPE2 = 2;
    	//路歌账户
    	public static final int TYPE3 = 3;
    	//收款对公账户 
    	public static final int TYPE4 = 4;
    }
    
    public static class COMMON_KEY {
    	public static final String userId = "userId";
    	public static final String vehicleAffiliation = "vehicleAffiliation";
    	public static final String tenantId = "tenantId";
    	public static final String amountFee = "amountFee";
    	public static final String payoutIntf = "payoutIntf";
    	public static final String orderId = "orderId";
    	public static final String success = "success";
    	public static final String fail = "fail";
    	public static final String billId = "billId";
    	public static final String businessId = "businessId";
    	public static final String flowId = "flowId";
    	public static final String batchId = "batchId";
    	public static final String orderInfo = "orderInfo";
    	public static final String zhangPingType = "zhangPingType";
    	public static final String list = "list";
    	public static final String MARGIN_STATE = "MARGIN_STATE";
    	public static final String serviceMatchOrders = "serviceMatchOrders";
    	public static final String RUN_CM_SALARY_INFO = "RUN_CM_SALARY_INFO";
    	public static final String PLEDGE_OILCARD_FEE_DAY = "PLEDGE_OILCARD_FEE_DAY";
    }
  
    //油卡抵押释放类型
    public static class PLEDGE_RELEASE_TYPE {
    	//抵押
    	public static final int PLEDGE = 0;
    	//释放
    	public static final int RELEASE = 1;
    }
    
    //工资
    public static class SALARY {
    	//初始化(未发送账单给司机)
    	public static final int INIT= 1;
    	//已发送账单给司机(司机还未确认)
    	public static final int SEND_OUT_BILL = 2;
    	//司机已确认账单
    	public static final int CONFIRM_BILL = 3;
    	//部分核销(部分结算工资给司机)
    	public static final int PART_VERIFICATION = 4;
    	//已核销(已结算工资给司机)
    	public static final int VERIFICATION = 5;
    	//工资未发放
    	public static final int NO_GRANT = 1;
    	//部分发放
    	public static final int PART_GRANT = 2;
    	//工资已发放
    	public static final int GRANT = 3;
    	//主驾驶
    	public static final int PRIMARY = 1;
    	//副驾驶
    	public static final int SECONDARY = 2;
    	//发放工资月份没有未完成订单
    	public static final int FINISHED_STATE0 = 0;
    	//发放工资月份有未完成订单
    	public static final int FINISHED_STATE1 = 1;
    	
    	//补贴天数状态
    	//已发
    	public static final String ALREADY_ISSUED = "1";
    	//未发
    	public static final String UNISSUED = "2";
    	//已发补
    	public static final String ALREADY_ISSUED_MEND = "3";
    	//未发补
    	public static final String UNISSUED_MEND = "4";
    	
    }
    //考勤
    public static class ATTENDANCE_MANAGEMENT_INFO {
    	//待确认
    	public static final int NO_CONFIRM = 0;
    	//已确认
    	public static final int CONFIRM = 1;
    }
    //审核
    public static class EXAMINE {
    	//待审核
    	public static final int NO_EXAMINE = 0;
    	//审核通过
    	public static final int EXAMINE_PASS = 1;
    	//审核不通过
    	public static final int EXAMINE_REFUSE = 2;
    }
    //工资日志
    public static class SALARY_LOG {
    	//operType 1:新增、2系统修改、3操作员修改、3核销、4发放工资
    	public static final int OPER_TYPE_NEW = 1;
    	public static final int OPER_TYPE_SYS_UPDATE = 2;
    	public static final int OPER_TYPE_OP_UPDATE = 3;
    	public static final int OPER_TYPE_GRANT = 4;
    	
    }
    //是否核销
    public static class IS_VERIFICATION {
    	//未核销
    	public static final int NO_VERIFICATION = 0;
    	//部分核销
    	public static final int PART_VERIFICATION = 1;
    	//已核销
    	public static final int VERIFICATION = 2;
    }
    //消费类型
    public static class PLATFORM_SERVICE_CHARGE_TYPE {
    	//油消费
    	public static final int OIL_TYPE = 1;
    	//维修保养消费
    	public static final int REPAIR_TYPE = 2;
    	//ETC消费
    	public static final int ETC_TYPE = 3;
    	//票据服务费
    	public static final int PLATFORM_TYPE = 4;
    }
    //服务商收入来源
    public static class SERVICE_MATCH_ORDER {
    	//来源订单现金
    	public static final int FROM_STATE_CASH = 0;
    	//来源订单油
    	public static final int FROM_STATE_OIL = 1;
    	//来源司机账户充值
    	public static final int FROM_STATE_ACCOUNT = 2;
    }
    public static class ADVANCE_TYPE {
    	//消费油预支
    	public static final int OIL_ADVANCE = 0;
    	//维修保养预支
    	public static final int REPAIR_ADVANCE = 1;
    }
    
    public static class PLATFORM_BILL_CFG {
    	//用户服务费_油配置项
    	public static final String USER_SERVICE_CHARGE_OIL = "USER_SERVICE_CHARGE_OIL";
    	//用户服务费_ETC配置项
    	public static final String USER_SERVICE_CHARGE_ETC = "USER_SERVICE_CHARGE_ETC";
    	//淮安成本_运费配置项
    	public static final String HA_COST_CASH = "HA_COST_CASH";
    	//淮安成本_油配置项
    	public static final String HA_COST_OIL = "HA_COST_OIL";
    	//淮安成本_ETC配置项
    	public static final String HA_COST_ETC = "HA_COST_ETC";
		//油卡抵押的释放时间
		public static final String PLEDGE_OILCARD_FEE_DAY = "PLEDGE_OILCARD_FEE_DAY";
    }
    
    //费用类型
    public static class FEE_TYPE {
    	//现金
    	public static final int CASH_TYPE = 1;
    	//ETC
    	public static final int ETC_TYPE = 2;
    	//实体油
    	public static final int ENTITY_OIL_TYPE = 3;
    	//虚拟油
    	public static final int FICTITIOUS_OIL_TYPE = 4;
    }


    public static class PAY_TYPE{
		//司机id
		public static final int USER = 0;
		//服务商
		public static final int SERVICE = 1;
		//租户id
		public static final int TENANT = 2;
		//HA虚拟
		public static final int HAVIR = 3;
		//HA实体
		public static final int HAEN = 4;
		//车队员工
		public static final int STAFF = 5;
	}
	//付款优先级别
	public static class PRIORITY_LEVEL{
		public static final int PRIORITY_LEVEL1 = 1;
		public static final int PRIORITY_LEVEL2 = 2;
		public static final int PRIORITY_LEVEL3 = 3;
		public static final int PRIORITY_LEVEL4 = 4;
		public static final int PRIORITY_LEVEL5 = 5;
		public static final int PRIORITY_LEVEL6 = 6;
		public static final int PRIORITY_LEVEL7 = 7;
		public static final int PRIORITY_LEVEL8 = 8;
		public static final int PRIORITY_LEVEL9 = 9;
		public static final int PRIORITY_LEVEL10 = 10;
	}
	//支付预付款类型
	public static class PAY_ADVANCE_TYPE{
		//普通支付方式（车队支付给车队、车队支付给司机）
		public static final int ORDINARY_PAY = 1;
		//车队支付给小车队(临时车队)
		public static final int FLEET_PAY_TEMPORARY_FLEET = 2;
		//小车队支付给车司机
		public static final int TEMPORARY_FLEET_PAY_DRIVER = 3;
		//车队支付给代收人、司机(油费)
		public static final int FLEET_PAY_COLLECTION_DRIVER = 4;
	}
	/**
	 * 资金监控
	 */
	public static class FUND_MONITORING_TYPE{
		//起始位置查询
		public static final int START_ROW_TYPE = 1;
		//页数查询
		public static final int PAGE_NUMBER_TYPE = 2;
		//1查询本车队应收应付
		public static final int OWN_TYPE = 1;
		//2查询其他车队或司机应收应付本车队
		public static final int OTHER_TYPE = 2;
		//1提现表应收
		public static final int RECEIVABLE_TYPE = 1;
		//2提现表应付
		public static final int PAYABLE_TYPE = 2;
		//1有异常未处理
		public static final int IS_REPORT1 = 1;
		//2无异常无需处理
		public static final int IS_REPORT2 = 2;
		//3有异常并已处理
		public static final int IS_REPORT3 = 3;
		public static final String FUND_MONITORING_TASK_USER_ID = "FUND_MONITORING_TASK_USER_ID";
		
	}
	
	/**
	 * 油账户充值
	 */
	public static class RECHARGE_ORDER_ACCOUNT_OIL{
		//客户油
		public static final String CUSTOMER_OIL = "1";
		//车队油
		public static final String FLEET_OIL = "2";
		//车队返利给司机
		public static final String FLEET_OIL_REBATE = "3";
	}
	/**
	 * 油账户清零
	 */
	public static class CLEAR_ACCOUNT_OIL{
		//客户油
		public static final int ORDER_OIL_SOURCE = 1;
		//车队油
		public static final int RECHARGE_OIL_SOURCE = 2;
		//维修保养
		public static final int USER_REPAIR_MARGIN = 3;
		//加油
		public static final int CONSUME_OIL_FLOW = 4;
		//强制平账
		public static final int ZHANG_PING_TYPE = 1;
		//清零油账户
		public static final int CLEAR_OIL_TYPE = 2;
	}
	public static class BILL_MANAGE{
		//打款记录成功
		public static final int PAYOUTINTF_SUCCESS = 1;
		//打款记录未发起或发起还没有结果
		public static final int PAYOUTINTF_INT = 2;
		
		//核销状态
		//可以核销
		public static final int VERIFICATION_STATE0 = 0;
		//不可以核销
		public static final int VERIFICATION_STATE1 = 1;
		//有异常
		public static final int VERIFICATION_STATE2 = 2;
		//支付中心返回结果异常
		public static final int VERIFICATION_STATE3 = 3;
		/*
		 * 1未完结
		 */
		public static final int ORDER_STATE1 = 1;
		/*
		 * 2已完结
		 */
		public static final int ORDER_STATE2 = 2;
		/*
		 * 1未完结
		 */
		public static final int ORDER_FUND1 = 1;
		/*
		 * 2已完结
		 */
		public static final int ORDER_FUND2 = 2;
		/*
		 * 1非平台票
		 */
		public static final int BILL_TYPE1 = 1;
		/*
		 * 2平台票
		 */
		public static final int BILL_TYPE2 = 2;
		/*
		 * 3油卡消费开票
		 */
		public static final int BILL_TYPE3 = 3;
	}
	public static class BASE_BILL_INFO{
		//开票状态
		/*
		 * 1不可申请开票
		 */
		public static final int BILL_STATE1 = 1;
		/*
		 * 2可申请开票
		 */
		public static final int BILL_STATE2 = 2;
		/*
		 * 3开票中
		 */
		public static final int BILL_STATE3 = 3;
		/*
		 * 4已开票
		 */
		public static final int BILL_STATE4 = 4;
		
	}
	public static class APPLY_OPEN_BILL{
		/*
		 * 1主申请记录
		 */
		public static final int FLOW_TYP1 = 1;
		/*
		 * 2抵扣票记录
		 */
		public static final int FLOW_TYP2 = 2;
		/*
		 * 1待支付
		 */
		public static final int APPLY_STATE1 = 1;
		/*
		 * 2付款中
		 */
		public static final int APPLY_STATE2 = 2;
		/*
		 * 3开票中
		 */
		public static final int APPLY_STATE3 = 3;
		/*
		 * 4待确认收票
		 */
		public static final int APPLY_STATE4 = 4;
		/*
		 * 5已收票
		 */
		public static final int APPLY_STATE5 = 5;
		/*
		 * 6拒绝开票（审核拒绝）
		 */
		public static final int APPLY_STATE6 = 6;
		
		//开票状态
		/*
		 * 1未开票
		 */
		public static final int BILL_STATE1 = 1;
		/*
		 * 2已开票
		 */
		public static final int BILL_STATE2 = 2;
		/*
		 * 3开票完成
		 */
		public static final int BILL_STATE3 = 3;
		/*
		 * 4开票失败
		 */
		public static final int BILL_STATE4 = 4;
		
		//同步支付中心状态
		/*
		 * 1成功
		 */
		public static final int SYN_STATE_SUCCESS = 1;
		/*
		 * 2失败
		 */
		public static final int SYN_STATE_FAIL = 2;
		//支付中心是否收票
		/*
		 * 0未收票
		 */
		public static final int PAYCENTER_NO_COLLECT_BILL = 0;
		/*
		 * 1确认收票
		 */
		public static final int PAYCENTER_COLLECT_BILL = 1;
		//是否有效
		/*
		 * 0无效
		 */
		public static final int INVALID = 0;
		/*
		 * 1有效
		 */
		public static final int EFFECTIVE = 1;
		
		/*
		 * 不允许开票的渠道
		 */
		public static final String NO_ALLOW_BILL_AFFILIATION="NO_ALLOW_BILL_AFFILIATION";
		/*
		 * 开票日期
		 */
		public static final String OPEN_BILL_DATE = "OPEN_BILL_DATE";
		//同心智行服务费提成打款状态
		/*
		 * 处理中
		 */
		public static final String REBATE_STATE0 = "0";
		/*
		 * 成功
		 */
		public static final String REBATE_STATE1 = "1";
		/*
		 * 失败
		 */
		public static final String REBATE_STATE9 = "9";
		
		/*
		 * 56K开票申请 （新增）
		 */
		public static final String DO_TYPE_56K_ADD="0";
		
		/*
		 * 56K开票申请 （删除）
		 */
		public static final String DO_TYPE_56K_DEL="1";
		/*
		 * 票据服务费
		 */
		public static final int SERVICE_FEE_FORMULA = 1;
		/*
		 * 票据成本
		 */
		public static final int SERVICE_COST_FORMULA = 2;
		/*
		 * 路歌申请开票允许同步一次订单数
		 */
		public static final String SYNC_LU_GE_BILL_ORDER = "SYNC_LU_GE_BILL_ORDER";
	}
	
	public static class HURRY_BILL_RECORD{
		/*
		 * 1催平台
		 */
		public static final int HURRY_BILL_TYPE1 = 1;
		/*
		 * 2非平台票
		 */
		public static final int HURRY_BILL_TYPE2 = 2;
	}
	//找油网
	public static class PAY_ORDER_OIL{
		public static final String FROM_TYPE = "FROM_TYPE";
		public static final String IS_BILL = "IS_BILL";
		/*
		 * 1原有扫码加油
		 */
		public static final int FROM_TYPE1 = 1;
		/*
		 * 2找油网加油
		 */
		public static final int FROM_TYPE2 = 2;
		/*
		 * 0待支付
		 */
		public static final int NO_PAY = 0;
		/*
		 * 1成功支付
		 */
		public static final int SUCCESS_PAY = 1;
	}
	/*
	 * 油卡充值
	 */
	public static class VOUCHER_INFO {
		/*
		 * 1勾选代金券
		 */
		public static final int USE_VOUCHER = 1;
		/*
		 * 2未勾选代金券
		 */
		public static final int NO_USE_VOUCHER = 2;
		
		/*
		 * 0代金券状态无效
		 */
		public static final int VOUCHER_INFO0 = 0;
		/*
		 * 1代金券状态有效
		 */
		public static final int VOUCHER_INFO1 = 1;
		/*
		 * 2代金券状态已过期
		 */
		public static final int VOUCHER_INFO2 = 2;
		
		/*
		 * 代金券类型：1油卡
		 */
		public static final int VOUCHER_TYPE1 = 1;
		
	}
	public static class RECHARGE_OIL_CARD {
		/*
		 * 1支付预付款充值
		 */
		public static final int RECHARGE_FROM1 = 1;
		/*
		 * 2油卡管理发起充值
		 */
		public static final int RECHARGE_FROM2 = 2;
		
		/*
		 * 1待付款
		 */
		public static final int PAY_STATE1 = 1;
		/*
		 * 2已付款
		 */
		public static final int PAY_STATE2 = 2;
		/*
		 * 3服务商充值完成
		 */
		public static final int PAY_STATE3 = 3;
		/*
		 * 4已充值
		 */
		public static final int PAY_STATE4 = 4;
		/*
		 * 5已充值
		 */
		public static final int PAY_STATE5 = 5;
		
		/*
		 * 是否自动提现阈值
		 */
		public static final String RECHARGE_WITHDRAWAL_THRESHOLD = "RECHARGE_WITHDRAWAL_THRESHOLD";
		/*
		 * 代金券有效时间
		 */
		public static final String VOUCHER_EFFECTIVE_TIME = "VOUCHER_EFFECTIVE_TIME";
		/*
		 * 月账单开票有效时间
		 */
		public static final String BILL_EFFECTIVE_TIME = "BILL_EFFECTIVE_TIME";
	}
	/*
	 * 月账单
	 */
	public static class SERVICE_MONTH_BILL{
		/*
		 * 1待开票
		 */
		public static final int BILL_STATE1 = 1;
		/*
		 * 2已开票
		 */
		public static final int BILL_STATE2 = 2;
		/*
		 * 3已过期
		 */
		public static final int BILL_STATE3 = 3;
		
		/*
		 * 1账单类型：1油卡消费 2、电子油卡
		 */
		public static final int BILL_TYPE1 = 1;
		public static final int BILL_TYPE2 = 2;

	}
	/*
	 * 月账单明细
	 */
	public static class BILL_APPLY_DETAILS{
		/*
		 * 账单类型类型：1油卡消费
		 */
		public static final int BILL_TYPE1 = 1;
		
		/*
		 * 1待开票
		 */
		public static final int BILL_STATE1 = 1;
		/*
		 * 2已开票
		 */
		public static final int BILL_STATE2 = 2;
		/*
		 * 3已失效
		 */
		public static final int BILL_STATE3 = 3;
		
	}
	/*
	 * 开票记录
	 */
	public static class SERVICE_BILL_APPLY {
		/*
		 * 1开票中
		 */
		public static final int APPLY_STATE1 = 1;
		/*
		 * 2待确认收票
		 */
		public static final int APPLY_STATE2 = 2;
		/*
		 * 3已收票
		 */
		public static final int APPLY_STATE3 = 3;
		/*
		 * 1未开票
		 */
		public static final int BILL_STATE1 = 1;
		/*
		 * 2已开票
		 */
		public static final int BILL_STATE2 = 2;
		/*
		 * 3开票完成
		 */
		public static final int BILL_STATE3 = 3;
		/*
		 * 0未核准
		 */
		public static final int APPROVAL_STATE0 = 0;
		/*
		 * 1已核准
		 */
		public static final int APPROVAL_STATE1 = 1;
		
	}
	public static class RECHARGE_CONSUME_RECORD {
		/*
		 * 1代金券
		 */
		public static final int VOUCHER_INFO = 1;
		/*
		 * 2月账单
		 */
		public static final int SERVICE_MONTH_BILL = 2;
		/*
		 * 匹配订单
		 */
		public static final int MATCH_ORDER = 3;
		/*
		 * 1充值
		 */
		public static final int RECORD_TYPE1 = 1;
		/*
		 * 2消费
		 */
		public static final int RECORD_TYPE2 = 2;
		/*
		 * 数据来源：1车队
		 */
		public static final int RECORD_SOURCE1 = 1;
		/*
		 * 数据来源：2服务商
		 */
		public static final int RECORD_SOURCE2 = 2;
		
	}
	public static class TASK_STATE {
		/*
		 * 1成功
		 */
		public static final int STATE_SUCCESS = 1;
		/*
		 * 2失败
		 */
		public static final int STATE_FAIL = 2;
	}
	public static class OIL_ENTITY {
		/*
		 * 1未充值/待发起
		 */
		public static final int RECHARGE_STATE1 = 1;
		/*
		 * 2待付款
		 */
		public static final int RECHARGE_STATE2 = 2;
		/*
		 * 3已付款
		 */
		public static final int RECHARGE_STATE3 = 3;
		/*
		 * 4服务商已充值
		 */
		public static final int RECHARGE_STATE4 = 4;
		/*
		 * 5已充值
		 */
		public static final int RECHARGE_STATE5 = 5;
		/*
		 * 6已撤销
		 */
		public static final int RECHARGE_STATE6 = 6;
		/*
		 * 0不走线上充值
		 */
		public static final int LINE_STATE0 = 0;
		/*
		 * 1线上充值
		 */
		public static final int LINE_STATE1 = 1;
	}
	/**
	 * 招商车挂靠车对账单
	 */
	public static class ACCOUNT_STATEMENT {
		
		//创建类型1按接收人创建，2个性化创建
		/*
		 * 账单创建方式：1按接收人创建
		 */
		public static final int CREATE_TYPE1 = 1;
		/*
		 * 账单创建方式： 2个性化创建
		 */
		public static final int CREATE_TYPE2 = 2;
		
		/*
		 * 费用扣取方式：1尾款扣除
		 */
		public static final int DEDUCTION_TYPE1 = 1;
		/*
		 * 费用扣取方式： 2现金收取
		 */
		public static final int DEDUCTION_TYPE2 = 2;
		
		
		//状态 1未发送，2确认中(待确认)，3已确认(结算中)，4已结算，5被驳回(已驳回)',
		/*
		 * 0 已失效（删除）
		 */
		public static final int STATE0 = 0;
		/*
		 * 1未发送
		 */
		public static final int STATE1 = 1;
		/*
		 * 2确认中(待确认)
		 */
		public static final int STATE2 = 2;
		/*
		 * 3已确认(结算中)
		 */
		public static final int STATE3 = 3;
		/*
		 * 4已结算
		 */
		public static final int STATE4 = 4;
		/*
		 * 5被驳回(已驳回)
		 */
		public static final int STATE5 = 5;


		//核销状态：0待核销，1订单尾款核销，2线上收款核销，3线下收款核销
		/*
		 * 0待核销
		 */
		public static final int VERIFICATION_STATE0 = 0;
		/*
		 * 1订单尾款核销
		 */
		public static final int VERIFICATION_STATE1 = 1;
		/*
		 * 2线上收款核销
		 */
		public static final int VERIFICATION_STATE2 = 2;
		/*
		 * 3线下收款核销
		 */
		public static final int VERIFICATION_STATE3 = 3;
		
		//确认账单是否通过
		/*
		 * 1通过
		 */
		public static final String IS_PASS_Y = "1";
		/*
		 * 2驳回
		 */
		public static final String IS_PASS_N = "2";
		
		//车辆费用修改
		/*
		 * insert 新增
		 */
		public static final String INSERT = "insert";
		/*
		 * delete 删除
		 */
		public static final String DELETE = "delete";
		/*
		 * update 修改
		 */
		public static final String UPDATE = "update";
	}
	//油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
	public static class OIL_BILL_TYPE {
		/*
		 * 1获取油票（非抵扣）
		 */
		public static final int OIL_BILL_TYPE1 = 1;
		/*
		 * 2获取运输专票（抵扣）
		 */
		public static final int OIL_BILL_TYPE2 = 2;
	}
	//油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
	public static class OIL_ACCOUNT_TYPE {
		/*
		 * 1授信账户
		 */
		public static final int OIL_ACCOUNT_TYPE1 = 1;
		/*
		 * 2已开票账户（客户油、返利油、转移油）
		 */
		public static final int OIL_ACCOUNT_TYPE2 = 2;
		/*
		 * 3充值账户
		 */
		public static final int OIL_ACCOUNT_TYPE3 = 3;
	}
	//北斗缴费
	public static class BEIDOU_PAYMENT {
		//缴费记录是否支付
		/*
		 * 0未支付
		 */
		public static final int PAY_STATE0 = 0;
		/*
		 * 1已支付
		 */
		public static final int PAY_STATE1 = 1;
		
		//缴费记录状态
		/*
		 * 0无效
		 */
		public static final int STATE0 = 0;
		/*
		 * 1有效
		 */
		public static final int STATE1 = 1;
		
		//缴费类型
		/*
		 * 1按天
		 */
		public static final int PAY_TYPE1 = 1;
		/*
		 * 2按月
		 */
		public static final int PAY_TYPE2 = 2;
		/*
		 * 3按年
		 */
		public static final int PAY_TYPE3 = 3;
		
		//缴费渠道类型
		/*
		 * 1车辆发起缴费
		 */
		public static final int CHANNEL_TYPE1 = 1;
		/*
		 * 2单发起的缴费
		 */
		public static final int CHANNEL_TYPE2 = 2;
	}
	//分配共享油费
	public static class OIL_SOURCE_RECORD {
		//渠道类型
		/*
		 * 1订单发起分配
		 */
		public static final int SOURCE_RECORD_TYPE1 = 1;
		/*
		 * 2司机账户发起分配
		 */
		public static final int SOURCE_RECORD_TYPE2 = 2;


		public static final String OIL_SOURCE_DATE = "OIL_SOURCE_DATE";
	}
	//分配共享油费
	public static class CONSUME_OIL_FLOW_EXT {
		//自有油站消费时，是否受授信额度限制：0否，1是
		/*
		 * 0
		 */
		public static final int CREDIT_LIMIT0 = 0;
		/*
		 * 1
		 */
		public static final int CREDIT_LIMIT1 = 1;
	}
	/**
	 * 路歌服务费
	 * @author dacheng
	 *
	 */
	public static class LUGE_SERVICE_FEE{
		/*
		 * 路歌最后一笔打款服务费阈值（单位分）
		 */
		public static final String LUGE_SERVICE_FEE_THRESHOLD = "LUGE_SERVICE_FEE_THRESHOLD";
	}
	/**
	 * 附加运费
	 * @author dacheng
	 *
	 */
	public static class ADDITIONAL_FEE{
		/*
		 * 1待付款
		 */
		public static final int STATE1 = 1;
		/*
		 * 2已付款
		 */
		public static final int STATE2 = 2;
		/*
		 * 3已返还
		 */
		public static final int STATE3 = 3;
		/*
		 * 4失效
		 */
		public static final int STATE4 = 4;
	}
	
	/**
	 * 附加运费 处理状态
	 * @author qingbin
	 *
	 */
	public static class ADDITIONAL_FEE_DEAL_STATE{
		/*
		 * 0待发起申请
		 */
		public static final int STATE0 = 0;
		/*
		 * 1成功
		 */
		public static final int STATE1 = 1;
		/*
		 * 2失败
		 */
		public static final int STATE2 = 2;
		/*
		 * 3已撤销
		 */
		public static final int STATE3 = 3;
	}
}
