package com.youming.youche.record.common;


/**
 * 系统常量定义
 * @author wengxk
 *
 */
public class EnumConsts {

	/**
	 * 远程缓存Key定义
	 * @author wengxk
	 *
	 */
	public static class RemoteCache {
		public static final String PORTAL_HITS = "SYS_PORTAL_HITS"; // 门户访问数
		public static final String SMS_MD5 = "SMS_MD5";//短息MD5值

		public static final String SMS_BILL = "SMS_BILL";//限制一个用户，一天只能收到多少条短信

		public static final String SMS_BILL_TEMPLATE="SMS_BILL_TEMPLATE";//限制一个用户，一天只能收到某个模版的短信的条数
		/**短信黑名单前缀，key值，Map结构，key:手机号码,value:0表示只是接收推送信息，不接收短信，1表示不接受推送，短信**/
		public static final String SMS_BLACKLIST="SMS_BLACKLIST";

		public static final String SYS_GOODS_NUM = "SYS_GOODS_NUM"; // 货源数总数
		public static final String SYS_CARS_NUM = "SYS_CARS_NUM"; // 车源数总数
		public static final String SYS_LINES_NUM = "SYS_LINES_NUM"; // 专线总数
		public static final String MY_GOODS_NUM = "MY_GOODS_NUM_";//我的发货统计
		public static final String MY_GOODS_DATE_NUM = "MY_GOODS_DATE_NUM";//我的发货日期统计
		public static final String MY_CARS_NUM = "MY_CARS_NUM_";//我的发车统计
		public static final String MY_CARS_DATE_NUM = "MY_CARS_DATE_NUM";//我的发车日期统计

		public static final String SYS_UPDATED_USER_SET = "SYS_UPDATED_USER_SET"; // 完善资料用户SET
		public static final String SYS_AUDIT_USER_SET = "SYS_AUDIT_USER_SET"; // 审核资料用户SET

		public static final String HX_REGISTER_TOKEN = "HX_REGISTER_TOKEN"; // 环信注册token

		public static final String SEND_INTEGRAL = "SEND_INTEGRAL"; // 赠送积分rpush/lpop

		public static final String UPLOAD_LATITUDE  = "UPLOAD_LATITUDE"; // 上传经纬度 rpush/lpop

		public static final String UPLOAD_LATITUDE_G7  = "UPLOAD_LATITUDE_G7"; // 上传经纬度 rpush/lpop

		public static final String UPLOAD_LATITUDE_YL  = "UPLOAD_LATITUDE_YL"; // 上传经纬度 rpush/lpop

		public static final String UPLOAD_LATITUDE_BD  = "UPLOAD_LATITUDE_BD"; // 北斗上传经纬度 rpush/lpop

		public static final String G7_VEHICLE  = "G7_VEHICLE"; // G7的车辆

		public static final String G7_VEHICLE_NEW  = "G7_VEHICLE_NEW"; // G7的车辆（临时，每次获取后要跟之前的做比较）

		public static final String G7_LATITUDE  = "G7_LATITUDE"; // G7的经纬度

		public static final String TENANT_E6_TEMPORARY_VEHICLE  = "TENANT_E6_TEMPORARY_VEHICLE"; //租户在易流临时存储的车辆set集合

		public static final String TENANT_E6_HISTORY_VEHICLE  = "TENANT_E6_HISTORY_VEHICLE"; //租户在易流前一次使用的车辆set集合,会和当前做比较


		public static final String VEHICLE_GPS_EQUIPMENT_INFO  = "VEHICLE_GPS_EQUIPMENT_INFO"; //车辆设备使用情况

		public static final String SERVIC_ECENTER_TOKEN  = "SERVIC_ECENTER_TOKEN"; // 服务中心token

		public static final String USER_OIL_FLOW  = "USER_OIL_FLOW_"; // 支付油流水号
		/**
		 * 手机展示车源或货源的总条数
		 */
		public static final String TOTALNUM = "TOTALNUM";

		//TODO:是否考虑匹配批次？
		/**
		 * 智能匹配, 元数据
		 */
		public static final String Match_META_AllRegionKeySet = "AllRegionKeySet"; // 所有发布货源的出发地|到达地key值集合(key: AllRegionKeySet, value: Set[S1|E1, S1|E2, S2|E2, ... Sn|En])
		public static final String Match_META_GoodsRegionSet_Profix = "GRegionSet_"; // 发布货源的出发地|到达地有序集合(前缀)(key: GRegionSet_Si|Ej, value: ZSet[G1, G2, G3, ... Gn])
		public static final String Match_META_CarsRegionSet_Profix  = "CRegionSet_"; // 发布车源的出发地|到达地有序集合(前缀)(key: CRegionSet_Si|Ej, value: ZSet[C1, C2, C3, ... Cm])
		public static final String Match_META_GoodsType_Profix = "GType_"; // 货物类型前缀(key: GType_G1, value: 冷链|生鲜)
		public static final String Match_META_CarsType_Profix = "CType_"; // 车辆类型前缀(key: CType_C1, value: 生鲜)
		public static final String Match_META_GoodsLoad_Profix = "GLoad_"; // 货物重量前缀(key: GType_G1, value: 10)
		public static final String Match_META_CarsLoad_Profix = "CLoad_"; // 车辆载重前缀(key: CType_C1, value: 20)
		public static final String Match_META_GoodsSquare_Profix = "GSquare_"; // 货物平方数前缀(key: GSquare_G1, value: 10)
		public static final String Match_META_CarsSquare_Profix = "CSquare_"; // 车辆平方数前缀(key: CSquare_C1, value: 20)
		public static final String Match_META_GoodsLength_Profix = "GLen_"; // 货物需求长度前缀(key: GLen_G1, value: 8)
		public static final String Match_META_CarsLength_Profix = "CLen_"; // 车辆长度前缀(key: CLen_C1, value: 10)
		public static final String Match_META_USER_HASH = "GCUserMap"; //存放发布ID和对应的用户ID(key: GCUserMap, value: Map[(G1, GU_ID1), ... (Gn, GU_IDn), (C1, CU_ID), ... (Cn, CU_IDn)])
		public static final String Match_META_Goods_Priority = "GP_";//存放货源对应的优先级列表(key: GP_Gn, value: 优先级排序|车源ID)
		public static final String Match_META_GoodsRootOrg_Profix = "GOrg_";//货源对应的顶级组织ID(key: GOrg_Gn, value: 201)
		public static final String Match_META_CarsRootOrg_Profix = "COrg_";//车源对应的顶级组织ID(key: COrg_Cn, value: 201)
		/**
		 * 智能匹配, 结果
		 */
		public static final String Match_Result_TEMP_HASH = "GCMatchedMap"; //匹配中间结果哈希集合,存放匹配的货源|车源ID和状态(key: GCMatchedMap, value: Map[(G1|C1, bit位(如：001)), (G1|C2, 011), ... (Gn|Cm, 111)])
		//最终写入Match_Result结果表时设置key，并且设置失效时间
		public static final String Match_Result_Goods_Profix = "GResult_"; // 货源已匹配结果次数(前缀)(key: GResult_G1, value: 5)
		public static final String Match_Result_Cars_Profix = "CResult_"; // 车源已匹配结果次数(前缀)(key: CResult_C1, value: 5)
		public static final String Match_Result_Goods_Cars_Profix = "GCResult_"; // 匹配结果重复校验(前缀)(key: GCResult_G1|C1, value: 1)

		//gps经纬度缓存
		public static final String Vehicle_Gps_Position = "VGP_";// 记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|地市city|省份province|区域district)
		//g7经纬度缓存
		public static final String Vehicle_G7_Position = "VGP7_";// 记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|地市city|省份province|区域district)
		//易流经纬度缓存
		public static final String Vehicle_YL_Position = "VGPYL_";// 记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|地市city|省份province|区域district)
		//北斗经纬度缓存
		public static final String Vehicle_BD_Position = "VGPBD_";// 记录车辆当前位置信息(key:VGP_随车手机,value:纬度|经度|地市city|省份province|区域district)

		public static final String Vehicle_Gps_X_Set = "VGP_X_SHARD_";	//记录纬度分片(key: VGP_X_SHARD_分片值, value: Set[billId])
		public static final String Vehicle_Gps_Y_Set = "VGP_Y_SHARD_";	//记录经度分片(key: VGP_Y_SHARD_分片值, value: Set[billId])

		public static final String Vehicle_Gps_Set = "VEHICLE_GPS_SET_";	//记录经纬度(key: VGP_X_SHARD_billId, value: Set[gps])
		//订单始点、终点位置信息
		public static final String Vehicle_Order_Position = "ORD_";// 记录订单位置信息(key:ORD_司机手机号码,value:A经度|A纬度|Z经度|Z纬度)


		public static final String INVITE_REGISTERE="INVITE_REGISTERE";//邀请注册 1000000001
		public static final String SHORTCUT_REGISTERE="SHORTCUT_REGISTERE";//快捷注册 1000000001
		public static final String BUNDING_PHONENUMBER="BUNDING_PHONENUMBER";
		public static final String RESET_PAY_PASSWORD="RESET_PAY_PASSWORD";//设置支付密码      1000000076
		public static final String UPDATE_PAY_PASSWORD="UPDATE_PAY_PASSWORD";//修改支付密码     1000000059
		public static final String FORGET_PAY_PASSWORD="FORGET_PAY_PASSWORD";// 忘记支付密码    1000000066(待确认)
		public static final String FEIGET_LOGIN_PASSWORD="FEIGET_LOGIN_PASSWORD";
		public static final String DRAWING="DRAWING";//提款   1000000061
		public static final String TRANSFER_ACCOUNTS="TRANSFER_ACCOUNTS";//转账   1000000057
		public static final String ORDER_PAY="ORDER_PAY";//订单支付       1000000063
		public static final String SAFE_PAY="SAFE_PAY";//保险支付  1000000060
		public static final String LOGIN_CODE="LOGIN_CODE";//登陆验证码
		public static final String INFORMAT_PAY="INFORMAT_PAY";//信息费支付验证码
		public static final String UPDATE_LOGIN_PASSWORD="UPDATE_LOGIN_PASSWORD";
		public static final String PAY_CASH_RESET_CODE="PAY_CASH_RESET_CODE";//现金支付验证码
		/**修改手机号码或者绑定手机号码**/
		//public static final String ValidKey_BINd_PHONE="ValidKey_Bind_Phone";
		/** 将用户是否为黑名单的信息保存到redis缓存中*/
		public static final String SYS_BLACK_LIST_USER_KEY = "SYS_BLACK_LIST_USER";


		/** 排行榜数据 */
		public static final String GOLD_NUM = "GOLD_NUM";
		public static final String CAR_NUM = "CAR_NUM";
		public static final String GOOD_NUM = "GOOD_NUM";
		public static final String ORDER_NUM = "ORDER_NUM";
		public static final String VEHICLE_NUM = "VEHICLE_NUM";


		/**#########安力新增 ##########*/

		/**记录纬度*/
		public static final String Vehicle_Gps_NAND = "WLPT_AL_VGP_NAND";
		/**记录经度*/
		public static final String Vehicle_Gps_EAND = "WLPT_AL_VGP_EAND";

		/**与记录经纬度相乘 的数字 */
		public static final long number = 100000000;
		/**支付验证码**/
		public static final String VALIDKEY_PAY="VALIDKEY_PAY";
		/**注册验证码 */
		public static final String VALIDKEY_CODE="VALIDKEY_CODE";
		/**登录验证码*/
		public static final String VALIDKEY_CODE_LOGIN="VALIDKEY_CODE_LOGIN";
		/**修改登录手机号码验证码*/
		public static final String UPDATE_PHONENUMBER="UPDATE_PHONENUMBER";
		/**重置登录密码验证码*/
		public static final String VALIDKEY_CODE_RESET="VALIDKEY_CODE_RESET";
		/**油站地理位置信息集合*/
		public static final String  OIL_LOCATION_INFO = "OIL_LOCATION_INFO";
		/**志鸿－redis－订单ID*/
		public static final String ZH_ORDER_ID = "ZH_ORDER_ID";
		public static final String LINK_ID = "LINK_ID";//联程单
		public static final String PLAN_ID = "PLAN_ID";//自动发单种单号
		public static final String OILCARD_ID = "OILCARD_ID";//油卡编号
		public static final String MAIL_NO = "MAIL_NO";//菜鸟运单号
		public static final String TD_ORDER_ID  = "TD_ORDER_ID";//第三方订单号
		public static final String OALOAN_ID = "OALOAN_ID";//借支编号
		public static final String RISK_FEE_SN = "RISK_FEE_SN";//风险金打印编号
		public static final String BACKHAUL_NUMBER = "BACKHAUL_NUMBER";//往返编码
		public static final String EXPENSE_NUMBER = "EXPENSE_NUMBER";//核销编码
		public static final String CONSUME_OIL_ORDER_ID = "CONSUME_OIL_ORDER_ID";//加油记录单号
		public static final String BILL_BUM = "BILL_BUM";//创建开票申请号
		public static final String RECHARGE_CARD_NUM = "RECHARGE_CARD_NUM";//创建油卡充值单号
		public static final String ACCOUNT_STATEMENT = "ACCOUNT_STATEMENT";//招商车挂靠车
		public static final String BEIDOU_PAYMENTNUM = "BEIDOU_PAYMENTNUM";//招商车挂靠车
		public static final String Oil_RECHARGE_PAYMENTNUM = "Oil_RECHARGE_PAYMENTNUM";//充值油账户业务单号
		public static final String ADDITIONAL_FEE = "ADDITIONAL_FEE";//附加运费业务单号
		/**违章订单缴费单号*/
		public static final String VIOLATION_ORDER_NUM = "VIOLATION_ORDER_NUM";

		public static final String IVR_MD5 = "IVR_MD5";//IVR MD5值
		public static final String CONSUME_OIL_ORDER_ID_TO_DRIVER = "CONSUME_OIL_ORDER_ID_TO_DRIVER";//加油记录司机单号

		/**获取服务商站点评分*/
		public static final String  SERVICE_PRODUCT_MARK = "SERVICE_PRODUCT_MARK";

		/*订单最大实体油*/
		public static final String  ORDER_ENTITY_OIL_MAX ="ORDER_ENTITY_OIL_MAX";
		/*充值油*/
		public static final String  RECHARGE_OIL_ORDER_ID ="RECHARGE_OIL_ORDER_ID";

		/**绑定银行卡鉴权*/
		public static final String BANK_AUTHENTICATION = "BANK_AUTHENTICATION";

		/**租户填写满意度调查表情况*/
		public static final String TENANT_SURVEY = "TENANT_SURVEY";
		/**订单靠台位置信息*/
		public static final String ORDER_DEPEND_LOCATION_INFO = "ORDER_DEPEND_LOCATION_INFO_";
		/**订单离台位置信息*/
		public static final String ORDER_LEAVE_LOCATION_INFO = "ORDER_LEAVE_LOCATION_INFO_";
		/**时效计算进程启动时间**/
		public static final String MONITOR_TASK_DATE = "MONITOR_TASK_DATE";
		/**时效计算上次订单经纬度**/
		public static final String MONITOR_ORDER_LOCATION = "MONITOR_ORDER_LOCATION_";
		/**时效计算上次订单异常事件状态**/
		public static final String MONITOR_ORDER_ABNORMAL_TYPE = "MONITOR_ORDER_ABNORMAL_TYPE_";
		/**订单上次靠台预估时间*/
		public static final String ORDER_LAST_DEPEND_EST_DATE = "ORDER_LAST_DEPEND_EST_DATE_";
		/**是否进入历史*/
		public static final String IS_HIS_ORDER_AGING = "IS_HIS_ORDER_AGING_";
		/**时效计算上次订单异常事件信息**/
		public static final String MONITOR_ORDER_ABNORMAL_INFO = "MONITOR_ORDER_ABNORMAL_INFO_";
		/**智能匹配客户**/
		public static final String INTELLIGENT_MATCH_CUSTOMER = "INTELLIGENT_MATCH_CUSTOMER_";
		/**智能匹配客户线路**/
		public static final String INTELLIGENT_MATCH_CUSTOMER_LINE = "INTELLIGENT_MATCH_CUSTOMER_LINE_";

		public static final String PINGAN_BALANCE = "PINGAN_BALANCE_";
		/**运营平台上次订单数量**/
		public static final String OBMS_ORDER_LAST_COUNT = "OBMS_ORDER_LAST_COUNT";
		/**运营平台上次订单数量**/
		public static final String OBMS_ORDER_LAST_FEE = "OBMS_ORDER_LAST_FEE";
		/**基础配置枚举**/
		public static final String BASIS_TYPE_INFO = "BASIS_TYPE_INFO";

		/** 北斗服务收款账户*/
		public static final String BEIDOU_SERVICE_ACCT_ID = "BEIDOU_SERVICE_ACCT_ID";
		/** 北斗服务按天收费金额*/
		public static final String BEIDOU_DATE_FEE = "BEIDOU_DATE_FEE";
		/** 北斗服务按天收费折扣 如果连续购买N天，延续到月底*/
		public static final String BEIDOU_DATE_DISCOUNT_DAYS = "BEIDOU_DATE_DISCOUNT_DAYS";
		/** 北斗服务是否启用按天收费折扣 true启用 false不启用*/
		public static final String BEIDOU_DATE_DISCOUNT_ENABLED = "BEIDOU_DATE_DISCOUNT_ENABLED";
		/** 北斗服务按月收费金额*/
		public static final String BEIDOU_MONTH_FEE = "BEIDOU_MONTH_FEE";
		/** 北斗服务按月收费折扣 如果连续购买N月，延续到年底*/
		public static final String BEIDOU_MONTH_DISCOUNT_MONTHS = "BEIDOU_MONTH_DISCOUNT_MONTHS";
		/** 北斗服务是否启用按月收费折扣 true启用 false不启用*/
		public static final String BEIDOU_MONTH_DISCOUNT_ENABLED = "BEIDOU_MONTH_DISCOUNT_ENABLED";
		/** 北斗服务按年收费金额*/
		public static final String BEIDOU_YEAR_FEE = "BEIDOU_YEAR_FEE";
		/**路歌会员认证标识**/
		public static final String LUGE_DRIVER_AUTHENTICATION ="LUGE_DRIVER_AUTHENTICATION";
		/** 平台开票日期设置 */
		public static final String OPEN_BILL_DATE = "OPEN_BILL_DATE";
		/** 线路规划标识 */
		public static final String TRACK_FILTRATION_LIST = "TRACK_FILTRATION_LIST_";
	}

	/**
	 * 枚举数据常量定义
	 * @author wengxk
	 *
	 */
	public static class SysStaticData {
		public static final String COST_TYPE = "COST_TYPE";//消费类型 1、消费 2、充值
		public static final String BUSINESS_TYPES = "BUSINESS_TYPES";//业务类型
		public static final String SUBJECT_COST = "SUBJECT_COST";//费用科目（预付费、担保金、月租、年租、信息费（如证件查询费、短信费用等）、保险金、运费）
		public static final String SUBJECTS_TYPE = "SUBJECTS_TYPE";//科目类型 1、固定费用 2、非固定费用
		public static final String PHONE_NUMBER = "PHONE_NUMBER";//公司手机号
		public static final String APPLICATION_TYPE = "APPLICATION_TYPE";//加盟枚举
		public static final String OBJ_TYPE = "OBJ_TYPE";//对象类型枚举
		public static final String SMS_TYPE = "SMS_TYPE";//消息类型枚举
		public static final String IS_PAY_PHONE ="IS_PAY_PHONE";//允许支付功能的号码
		public static final String IS_LOCATION_PHONE ="IS_LOCATION_PHONE";//定位黑名单
		public static final String SYS_USER_TYPE ="SYS_USER_TYPE";//用户类型
		public static final String MARGIN_LEVEL = "MARGIN_LEVEL";//保证金级别
		public static final String SOURCE_FLAG = "SOURCE_FLAG";//数据来源
		public static final String ORG_PIC = "ORG_PIC";//第三方认证组织图片
		public static final String GOODS_TYPE = "GOODS_TYPE";//货源类型
		public static final String CURR_STATE="CURR_STATE"; //车辆调度的当前状态
		public static final String PAY_WAY="PAY_WAY"; //车辆调度的当前状态
		public static final String PACKGE_WAY="PACKGE_WAY"; //包装类型
		public static final String ORDER_STATE="ORDER_STATE"; //订单状态
		public static final String ORDER_TYPE="ORDER_TYPE"; //订单类型
		public static final String COST_PROBLEM_TYPE="RECRIVE_ORDER_PROBLEM_TYPE@COST"; //异常类型
		public static final String INCOME_PROBLEM_TYPE="RECRIVE_ORDER_PROBLEM_TYPE@INCOME"; //异常类型
		public static final String PRO_STATE="EXPENSE_STATE"; //异常类型
		public static final String CHARGE_TYPE="CHARGE_TYPE"; //异常责任方
		public static final int AL_COMMON_GOODS_TYPE=1; //普通货物常量,安利系统默认字段
		public static final String CUSTOMER_LEVEL="CUSTOMER_LEVEL"; //客户级别
		public static final String CUCUSTOMER_LINE_STATE="CUCUSTOMER_LINE_STATE"; //线路状态
		public static final String PRICE_ENUM="PRICE_ENUM"; //单价枚举
		public static final String VEHICLE_LENGTH="VEHICLE_LENGTH"; //车长枚举
		public static final String VEHICLE_STATUS="VEHICLE_STATUS@ORD"; //车型枚举
		public static final String PROBLEM_REPORT_TYPES="PROBLEM_REPORT_TYPES";//异常报备类型

		public static final String SYS_PROVINCE="SYS_PROVINCE"; //省枚举
		public static final String SYS_CITY="SYS_CITY"; //市枚举
		public static final String SYS_DISTRICT="SYS_DISTRICT"; //区枚举

		public static final String SYS_STATE_DESC="SYS_STATE_DESC"; //组织有效枚举
		public static final String ORG_TYPE="ORG_TYPE"; //组织的部门类型（是否为职能部门 0 不是 1 是的）

		public static final String USER_TYPE="USER_TYPE"; //用户类型

		public static final String LOCK_FLAG="LOCK_FLAG"; //启用状态
		public static final String DRIVER_TYPE="DRIVER_TYPE"; //车主（司机）类型
		public static final String ACC_LEVEL="ACC_LEVEL"; //用户等级

		public static final String BUSINESS_TYPE="BUSINESS_TYPE"; //油卡类型

		public static final String OILCARD_TYPE="OILCARD_TYPE"; //油卡类型

		public static final String OILCARD_STATUS="OILCARD_STATUS"; //油卡状态

		public static final String PAYMENT_OBJ_TYPE="PAYMENT_OBJ_TYPE";//支付类目

		public static final String TXN_TYPE="TXN_TYPE";//支付渠道

		public static final String COST_DISCOUNT_TYPE="COST_DISCOUNT_TYPE";//成本折扣类型

		public static final String CAPITAL_CHANNEL="capital_channel";//资金渠道

		public static final String SOURCE_PROBLEM="SOURCE_PROBLEM";//异常来源

		/******提醒静态数据******/
		/**未到达提醒*/
		public static final String ALREADY_DEPEND = "ALREADY_DEPEND@warn";
		/**将要到达提醒*/
		public static final String NO_DEPEND = "NO_DEPEND@warn";
		/**没有gps提醒*/
		public static final String NO_GPS = "NO_GPS@warn";
		/**回单提醒*/
		public static final String RECEIVE = "RECEIVE@warn";
		/**对账提醒*/
		public static final String BALANCE = "BALANCE@warn";
		/**开票提醒*/
		public static final String BILLING = "BILLING@warn";
		/**收款提醒*/
		public static final String FEE = "FEE@warn";
		/**到达*/
		public static final String ARRIVE = "ARRIVE@warn";

		/**一级维修项*/
		public static final String REPAIR_ROOT_ID = "REPAIR_ROOT_ID";
		/**二级维修项*/
		public static final String REPAIR_CHILD_ID = "REPAIR_CHILD_ID";

		/**合作类型*/
		public static final String COOPERATION_TYPE = "COOPERATION_TYPE";

		/*** 服务商审核状态*/
		public static final String SERVICE_AUTH_STATE = "SERVICE_AUTH_STATE";

		/*** 站点合作状态*/
		public static final String COOPERATION_STATE = "COOPERATION_STATE";

		/**服务商维修状态*/
		public static final String REPAIR_STATE = "REPAIR_STATE";
		/**司机维修状态*/
		public static final String APP_REPAIR_STATE = "APP_REPAIR_STATE";

		/**维修保养支付方式*/
		public static final String REPAIR_PAY_WAY = "REPAIR_PAY_WAY";

		/**服务商类型*/
		public static final String SERVICE_BUSI_TYPE = "SERVICE_BUSI_TYPE";

		public static final String CUSTOMER_AUTH_STATE = "CUSTOMER_AUTH_STATE";

		public static final String BALANCE_TYPE = "BALANCE_TYPE";

		/**客户满意度调查*/
		public static final String SYSTEM_SKILLED = "SYSTEM_SKILLED";//系统熟练度
		public static final String TRAINER_ATTITUDE = "TRAINER_ATTITUDE";//培训人员态度
		public static final String TRAINER_PROFESSION = "TRAINER_PROFESSION";//培训人员专业度
		public static final String TRAINER_RESPONSE = "TRAINER_RESPONSE";//培训人员疑问响应
	}

	/**
	 * 系统参数常量定义
	 * @author wengxk
	 *
	 */
	public static class SysCfg {
		public static final String SYS_CFG_NAME = "sys_cfg:";
		public static final String IS_SEND_MEG = "IS_SEND_MEG"; // 是否发送短信

		public static final String SMS_SEND_THREAD_COUNT = "SMS_SEND_THREAD_COUNT"; // 短信发送的进程数量
		public static final String CENSUSINFO_TASK_THREAD_COUNT = "CENSUSINFO_TASK_THREAD_COUNT"; // 用户全量信息扫描进程数量
		public static final String CENSUSINFO_ADD_TASK_THREAD_COUNT = "CENSUSINFO_ADD_TASK_THREAD_COUNT"; // 用户增量信息扫描进程数量
		public static final String RECOMMEND_TASK_THREAD_COUNT = "RECOMMEND_TASK_THREAD_COUNT"; // 用户邀请增量进程数量
		//APP推送（货主版）
		public static final String SYS_ANDROID_APPID = "SYS_ANDROID_APPID";
		public static final String SYS_ANDROID_APIKEY = "SYS_ANDROID_APIKEY";
		public static final String SYS_ANDROID_SECRETKEY = "SYS_ANDROID_SECRETKEY";


		public static final String SYS_IOS_APPID = "SYS_IOS_APPID";
		public static final String SYS_IOS_APIKEY = "SYS_IOS_APIKEY";
		public static final String SYS_IOS_SECRETKEY = "SYS_IOS_SECRETKEY";

		//APP推送（车主版）
		public static final String SYS_ANDROID_APIKEY_CAR = "SYS_ANDROID_APIKEY_CAR";
		public static final String SYS_ANDROID_SECRETKEY_CAR = "SYS_ANDROID_SECRETKEY_CAR";
		public static final String SYS_IOS_APIKEY_CAR = "SYS_IOS_APIKEY_CAR";
		public static final String SYS_IOS_SECRETKEY_CAR = "SYS_IOS_SECRETKEY_CAR";

		public static final String EFFECTIVE_TIME = "EFFECTIVE_TIME";   //单位为秒

		//门户相关
		public static final String JOIN_IS_SEND_MSG = "JOIN_IS_SEND_MSG";   //申请联盟是否发送短信

		//APP相关
		public static final String APP_VERSION_IOS ="APP_VERSION_IOS";//苹果手机客户端版本cfg_system=0不强制，cfg_system=1强制
		public static final String APP_VERSION_ANDROID = "APP_VERSION_ANDROID";//安卓手机客户端版本cfg_system=0不强制，cfg_system=1强制
		public static final String APP_CACHE_IDENT = "APP_CACHE_IDENT"; //app缓存标识 1++
		public static final String INTERVAL_TIME="INTERVAL_TIME";//超过分钟再去查询发货统计和发车统计，单位是毫秒
		public static final String INVITE_URL="INVITE_URL";//好友推荐短息url
		public static final String ANDROID_INVITATION_URL = "ANDROID_INVITATION_URL"; //安卓邀请下载地址
		public static final String IOS_INVITATION_URL = "IOS_INVITATION_URL"; //IOS邀请下载地址
		public static final String ANDROID_DOWNLOADS_URL = "ANDROID_DOWNLOAD_URL"; //安卓下载安装包
		public static final String IOS_DOWNLOADS_URL = "IOS_DOWNLOAD_URL"; //IOS下载安装包
		//保险web页面
		public static final String PREMIUM_FLAG="PREMIUM_FLAG";//保险显示与隐藏
		public static final String PREMIUM_MIN="PREMIUM_MIN";//保险的最低费用
		/**综合险费率*/
		public static final String PREMIUM_RATE="PREMIUM_RATE";//保险的综合险费率
		/**基本险费率*/
		public static final String PREMIUM_RATE_J="PREMIUM_RATE_J";//保险的基本险费率
		//二维码扫码签收验证码的有效时间
		public static final String ER_WEI_MA_PASSWORD_DAY="ER_WEI_MA_PASSWORD_DAY";
		public static final String IS_OPEN_CAPTCHA="IS_OPEN_CAPTCHA";//验证码后台校验
		public static final String IS_PAY="IS_PAY";//屏蔽支付参数

		//提现金额限制
		public static final String CASH_MAX_MONEY ="CASH_MAX_MONEY";
		public static final String CASH_MIN_MONEY ="CASH_MIN_MONEY";
		public static final String PINGAN_MAX_PAY_MONEY ="PINGAN_MAX_PAY_MONEY";
		//当天可提现总次数
		public static final String CASH_NUM = "CASH_NUM";

		//充值金额次数
		public static final String  RECHARGE_NUM = "RECHARGE_NUM";
		public static final String  RECHARGE_MAX_MONEY  = "RECHARGE_MAX_MONEY";
		public static final String  RECHARGE_MIN_MONEY  = "RECHARGE_MIN_MONEY";

		public static final String  PINGAN_MAX_PAY300_TIME  = "PINGAN_MAX_PAY300_TIME";//虚拟到实体打款超时的配置（次数）

		//预支金额限制
		public static final String  PREPAY_MIN_MONEY  = "PREPAY_MIN_MONEY";
		public static final String  PREPAY_MAX_MONEY  = "PREPAY_MAX_MONEY";

		/**个人名片的url配置**/
		public static final String  QR_CODE_URL  = "QR_CODE_URL";

		public static final String HELPER_URL ="HELPER_URL ";

		/**环信注册用户名前缀**/
		public static final String  HX_USER_NAME  = "HX_USER_NAME";

		/**短信发送配置*/
		//验证类短信发送
		public static final String SMS_VERIFY = "SMS_VERIFY";
		//其余短信发送方式
		public static final String SMS_APP_SEND = "SMS_APP_SEND";
		//营销短信发送
		public static final String SMS_MARKET_SEND = "SMS_MARKET_SEND";

		//判断租户是否需要发送短信参数，该参数中包含租户id的都不需要发送短信
		public static final String IS_SEND_OF_TENANT_ID = "IS_SEND_OF_TENANT_ID";

		public static final String RECOMMEND_TASK_SMS_TIME = "RECOMMEND_TASK_SMS_TIME"; // 用户邀请增量进程数量

		public static final String WITHDRAW_LEAST_AMOUNT = "WITHDRAW_LEAST_AMOUNT";//提现最少金额
		/**手机号码检查**/
		public static final String CHK_PHONE = "CHK_PHONE";
		/**固定号码检查**/
		public static final String CHK_TELPHONE = "CHK_TELPHONE";
		/*************安力 new****************/
		/**我要发货装货地数量控制*/
		public static final String  LOAD_GOODS_MAX = "LOAD_GOODS_MAX";
		/**我要发货卸货地数量控制*/
		public static final String  UNLOAD_GOODS_MAX = "UNLOAD_GOODS_MAX";

		/**邀请红包统计查询最大月数控制*/
		public static final String  QUERY_INVITE_MAX_MONTH = "QUERY_INVITE_MAX_MONTH";

		/**消息中心查询最大月数控制*/
		public static final String  QUERY_SEND_MAX_MONTH = "QUERY_SEND_MAX_MONTH";

		/**修改/输入支付密码失败最大次数控制*/
		public static final String  MODIFY_PASS_ERROR_TIMES = "MODIFY_PASS_ERROR_TIMES";

		/**修改支付密码成功最大次数控制*/
		public static final String  MODIFY_PASS_SUCCESS_TIMES = "MODIFY_PASS_SUCCESS_TIMES";

		/**客服电话配置*/
		public static final String  CUSTOM_SERVICE_PHONE = "CUSTOM_SERVICE_PHONE";

		/**异常图片最大个数限制*/
		public static final String  MAX_PROBLEM_NUMS = "MAX_PROBLEM_NUMS";

		/**控制需要输入支付验证码的支付密码最大错误次数*/
		public static final String  MAX_NEED_PAY_CODE = "MAX_NEED_PAY_CODE";

		/**查询附近司机的控制距离单位米*/
		public static final String  MAX_DISTANCE = "MAX_DISTANCE";

		/**财务管理手机设置－提现管理*/
		public static final String LOGBI_FD_MANAGER = "LOGBI_FD_MANAGER";

		/** 安力虚拟流水账号必须与数据库手动配置的一致*/
		public static final String V_W_ACCOUNT = "V_W_ACCOUNT";

		/**志鸿－ 评标时间配置*/
		public static final String SCALING_TIME = "SCALING_TIME";

		/**油站二维码的图片路径*/
		public static final String ER_CODE_LOGO_PIC = "ER_CODE_LOGO_PIC";

		/**不可删除角色*/
		public static final String NOT_DEL_ROLE = "NOT_DEL_ROLE";

		/**市场部老大*/
		public static final String SALE_BOSS_ROLE = "SALE_BOSS_ROLE";


		/**匹配订单距离和时间限制*/
		public static final String ORDER_MATCH_DISTANCE = "ORDER_MATCH_DISTANCE";
		public static final String ORDER_MATCH_DAY = "ORDER_MATCH_DAY";
		public static final String ORDER_MATCH_HOUR = "ORDER_MATCH_HOUR";

		/**APP展示的图标*/
		public static final String SHOWABLE_APP_ICONS = "SHOWABLE_APP_ICONS";

		public static final String IS_SEND_IVR = "IS_SEND_IVR"; // 是否发送IVR
		public static final String IVR_SEND_THREAD_COUNT = "IVR_SEND_THREAD_COUNT"; // IVR发送的进程数量

		public static final String REFRESH_SYSTEM="REFRESH_SYSTEM";

		/***转单超时时间的限制， 单位小时***/
		public static final String TIME_OUT_TRANSFER_ORDER="TIME_OUT_TRANSFER_ORDER";

		/**附近范围油站*/
		public static final String NEARBY_OIL_POSITION = "NEARBY_OIL_POSITION";
		/**催单次数限制**/
		public static final String REMINDER_COUNT="REMINDER_COUNT";
		/**催单的间隔时间，单位为分**/
		public static final String REMINDER_INTERVAL_TIME="REMINDER_INTERVAL_TIME";
		/**附近范围维修站*/
		public static final String NEARBY_REPAIR_POSITION = "NEARBY_REPAIR_POSITION";

		/**附近范围站点*/
		public static final String NEARBY_PRODUCT = "NEARBY_PRODUCT";

		/**违章消息提醒周期（天）*/
		public static final String VIOLATION_NOTIFY_INTERVAL_DATE = "VIOLATION_NOTIFY_INTERVAL_DATE";

		/**违章报价失效周期（天）*/
		public static final String VIOLATION_QUOTATION_INTERVAL_DATE = "VIOLATION_QUOTATION_INTERVAL_DATE";

		/**账户余额缓存redis开关 0关（实时查询银行）1开 */
		public static final String BALANCE_CACHE_SWITCH="BALANCE_CACHE_SWITCH";

		/**虚拟油充值每笔最低限制*/
		public static final String OIL_RECHARGE_LOWEST="OIL_RECHARGE_LOWEST";

	}

	/**
	 * 临时性的系统参数常量定义
	 * @author wengxk
	 *
	 */
	public static class SysCfgTemp {
		//V1.7IOS上线后删除 （50086接口）
		public static final String TMP_VER_TYPE = "TMP_VER_TYPE"; //50086接口versionsType入参做兼容，v1.7限制必传(主要考虑iosV1.6已经在审核)
		//V2.3.0IOS上线后删除 （50031接口）
		public static final String  TEM_COMMENT= "TEM_COMMENT"; //50031接口versionsType入参做兼容，v1.7限制必传(主要考虑iosV1.6已经在审核)
	}

	/**
	 * 短信模版ID
	 * @author wangbq
	 *
	 */
	public static class SmsTemplate{

		public static final long WARM_TEMPLATE_ID = 1000000;
		public static final long CAR_TEMPLATE_ID = 1000000042;//车源对应车辆提交订单后，取消其它对应车源订单短信,发给车主
		public static final long GOODS_TEMPLATE_ID = 1000000043;//车源对应车辆提交订单后，取消其它对应车源订单短信，发给货主


		public static final long send_message_car = 1000000006; //发布货源时发短信给收藏车辆和我的车辆
		public static final long car_huan_che = 1000000007; //车主换车发送信息给货主
		public static final long good_huan_che = 1000000008; //车主换车发送信息给货主
		public static final long good_qu_xiao = 1000000019; //货主取消订单（发给车主）
		public static final long car_qu_xiao = 1000000020; //车主取消订单（发给货主）
		public static final long car_pi_pei= 1000000021; //车主匹配并确认订单（发给货主）
		public static final long good_pi_pei= 1000000022; //货主匹配并确认订单（发给车主）
		public static final long car_wan_cheng= 1000000023;//车主完成运输后（发给货主要货主确认完成）
		public static final long good_que_ren= 1000000025;//货主确认（发给车主）
		public static final long car_que_ren= 1000000026;//车主确认（发给货主）
		public static final long good_zx_que_ren= 1000000027;//货主发货发到专线（发给专线联系人）
		public static final long good_que_ren_wan_bi= 1000000028;//双方确认后（发给货主）
		public static final long car_que_ren_wan_bi= 1000000029;//双方确认后（发给车主）
		public static final long good_tou_bao= 1000000033;//发信息给货主提醒投保
		public static final long good_fa_bu= 1000000035;//货主发布信息成功通知货主
		public static final long car_fa_bu= 1000000034;//车主发布信息成功通知车主
		public static final long good_ti_jiao= 1000000036;//货主提交订单发给货主
		public static final long car_ti_jiao= 1000000037;//车主提交订单发给车主
		public static final long car_wan_cheng_good= 1000000038;//货主完成发信息给车主


		public static final long REVOKE_TEMPLATE_ID = 1000000050;//支付撤销短信模板id
		public static final long CAR_CONF_TEMPLATE_ID = 1000000040;//车主没确认取消订单短信模板ID
		public static final long GOODS_CONF_TEMPLATE_ID = 1000000041;//货主没确认取消订单短信模板ID
		public static final long GOODS_REGI_TEMPLATE_ID = 1000000046;//邀请收货人注册短信模板ID
		public static final long CARS_LINK_REGI_TEMPLATE_ID = 1000000045;//邀请车源联系人注册短信模板ID
		public static final long INVITE_FRIENDS_ID=1000000047;//邀请好友短息模版ID
		public static final long REGISTER_ID=1000000049;//组织方新增用户的告知短信模板
		public static final long MATCH_RESULT=100001001; // 匹配结果 通知车主、货主双方模版
		public static final long COLLECTION_CARS=1000000053;//收藏发送短信模板
		public static final long GOOD_ZHUANG_HUO_WAN_CHENG = 1000000054; //装货完成,发送给货方
		public static final long CAR_ZHUANG_HUO_WAN_CHENG = 1000000055; //装货完成，发送给车方
		public static final long GOODS_BUY_PREMIUM = 1000000056;//双方确认后、发信息提醒给货方购买保险
		public static final long TRAMSFER_REGIST_CODE= 1000000057; //转账短信验证码
		public static final long MODIFY_PWD_CODE= 1000000059; //支付密码修改短信验证码
		public static final long ZHI_FU_CODE= 1000000060; //支付货物保险短信验证码
		public static final long WITHDRAWALS_ROLL_TEMPLATE_ID= 100001082; //提现短信模版ID
		public static final long WITHDRAWALS_REGIST_CODE = 1000000061;  //提现短信验证码
		public static final long ORDER_PAY = 1000000063;  //订单支付短信验证码
		public static final long RESET_PWD_CODE = 1000000066;  //我的钱包重置密码验证码
		public static final long TELL_CAR_TIPS = 1000000064;  //推送短信给车主，说明货主选线下支付。
		public static final long PI_PEI_GOOD = 1000000069;  //增加匹配时订单已提交发给货主的短信
		public static final long PI_PEI_CAR = 1000000070;  //增加匹配时订单已提交发给车主的短信
		public static final long NO_SUBMIT=1000000071; // 未认证不能确认订单
		public static final long NO_COMPLETE=1000000072; //未认证不能签收订单
		public static final long PREMIUM_SEND=1000000073; //保险费用支付通知

		public static final long PAY_INTERFACE=1000000076;//接口发送支付验证码接口模板
		public static final long JOIN_TO_COMPANY=1000000032L;//申请加盟短信

		public static final long SEND_PWD=1000000002L;//短信秘密
		public static final long REGISTER_TIPS=1000000081L;//注册成功提醒用户完善资料
		public static final long MACTH_BIDD_GOOD=1000000082L;//注册成功提醒用户完善资料


		public static final long REGIST_HTML5=1000000084;//html5注册密码提醒
		public static final long ATTENTION_CAR=1000000092; //关注车源线路发短信
		public static final long ATTENTION_GOOD=1000000093; //关注货源线路发短信
		public static final long BOND_RECHARGE=1000000091;//充值保证金短信
		public static final long RECOMMEND_NOTIFY=1000000111;//邀请每天增量通知短信
		public static final long LOGIN_SMS=1000000112;//登陆短信验证码信息
		/**信息费支付验证码的短信模板id**/
		public static final long INFORMAT_PAY=1000000118L;
		/**货主（或中介）主动申请信息费，车主也可以进行“确认支付”短信模板id**/
		public static final long APPLY_FOR_CREDIT=1000000115L;
		/**车主预付信息费，平台发短信通知货主短信模板id**/
		public static final long PREPAY =1000000116L;
		/**车主申请退回信息费，平台发短信通知货主短信模板id**/
		public static final long APPLY_FOR_RETURN=1000000117L;

		/**在发布评论时需要增加短信实时通知给永州招商粮食局管理员短信模板id**/
		public static final long YONGZHOU_SAY=1000000120L;

		public static final long MATCH_UPDATE_INFO = 1000000121L; //匹配提醒完善资料
		/**确认订单（货主）  货主确认订单-未支付*/
		public static final long GOODS_CON_WAIT_PAY = 1000000147L;//
		/**确认订单（货主）   货主确认订单-已支付*/
		public static final long GOODS_CON_FINISH_PAY = 1000000122L;
		/***确认订单（货主）  货主确认订单-已支付发给车方*/
		public static final long GOODS_CON_FINISH_PAY_TO_CAR = 1000000123L; //
		/**确认订单（货主）   货主短信：车主已支付*/
		public static final long CAR_FINISH_PAY = 1000000124L;
		/***确认订单（货主）  车主短信：车主已支付*/
		public static final long CAR_FINISH_PAY_TO_GOOD = 1000000125L;
		/***确认订单（车主） 车主短信：车主确认订单-未支付*/
		public static final long CARS_CON_WAIT_PAY = 1000000126L;
		/*****确认订单（车主） 车主短信：车主确认订单-已支付（未支付—已支付）*/
		public static final long CARS_CON_FINISH_PAY = 1000000127L;
		/***确认订单（车主） 货主短信：车主确认订单-已支付（未支付—已支付）*/
		public static final long CARS_CON_FINISH_PAY_TO_GOOD = 1000000128L;
		/***确认订单（车主） 车主短信：货主已支付*/
		public static final long CARS_CON_GOOD_FINISH_PAY_TO_CAR = 1000000129L;
		/****确认订单（车主） 货主短信：货主已支付*/
		public static final long CARS_CON_GOOD_FINISH_PAY_TO_GOOD = 1000000130L;
		/*****取消订单（货主）货主短信：货主取消----由我担责*/
		public static final long GOOD_CANCE_ME_LIABILITY = 1000000131L;
		/****取消订单（货主）车主短信：货主取消----由我担责***/
		public static final long GOOD_CANCE_ME_LIABILITY_TO_CAR = 1000000132L;
		/***取消订单（货主）货主短信：货主取消----对方担责*/
		public static final long GOOD_CANCE_OPPOSITE_LIABILITY = 1000000133L;
		/**取消订单（货主）车主短信：货主取消----对方担责*/
		public static final long GOOD_CANCE_OPPOSITE_LIABILITY_TO_CAE = 1000000134L;
		/****取消订单（货主） 货主（车主）短信：货主（车主）取消----双方担责*/
		public static final long GOOD_CANCE_TOGETHER_LIABILITY= 1000000135L;
		/***取消订单（车主） 车主短信：车主取消----由我担责*/
		public static final long CAR_CANCE_ME_LIABILITY = 1000000136L;
		/**取消订单（车主） 货主短信：车主取消----由我担责*/
		public static final long CAR_CANCE_ME_LIABILITY_TO_GOOD = 1000000137L;
		/**取消订单（车主） 车主短信：车主取消----对方担责*/
		public static final long CAR_CANCE_OPPOSITE_LIABILITY = 1000000138L;
		/**取消订单（车主） 货主短信：车主取消----对方担责*/
		public static final long CAR_CANCE_OPPOSITE_LIABILITY_TO_GOOD = 1000000139L;
		/**对方同意 货主短信：由我担责*/
		public static final long OPPOSITE_AGREE_ME_LIABILITY_TO_GOOD = 1000000140L;
		/***对方同意 车主短信：对方担责*/
		public static final long OPPOSITE_AGREE_OPPOSITE_LIABILITY_TO_CAR = 1000000141L;
		/***对方同意 货主短信：对方担责*/
		public static final long OPPOSITE_AGREE_OPPOSITE_LIABILITY_TO_GOOD = 1000000142L;
		/**对方同意 车主短信：由我担责*/
		public static final long OPPOSITE_AGREE_ME_LIABILITY_TO_CAR = 1000000143L;
		/***对方同意 货主短信：双方担责*/
		public static final long OPPOSITE_AGREE_TOGETHER_LIABILITY_TO_GOOD = 1000000144L;
		/**对方同意 车主短信：双方担责*/
		public static final long OPPOSITE_AGREE_TOGETHER_LIABILITY_TO_CAR = 1000000145L;
		/***对方拒绝 双方短信*/
		public static final long OPPOSITE_REFUSE_TOGETHER_LIABILITY = 1000000146L;

		/***欢迎${serviceName}进驻同心智行平台，平台67万车辆期待您的站点/服务，请尽快维护：在微信搜索小程序“同心智行-服务商”并关注，您的登录账号是${mobilePhone}。[同心智行];*/
		public static final long ADD_SERVICE_INFO = 4000000001L;

		/**违章、保险类型服务商注册发送短信**/
		public static final long ADD_VIOLATION_SERVICE_INFO = 4000000020L;
		/***
		 * ETC卡删除通知
		 */
		public static final long ETC_CARD_DEL = 4000000021L;






		/***####安力###**/
		/**注册验证码模板**/
		public static final long REGIST_CODE = 1000000000;
		/**登录验证码模板**/
		public static final long LOGIN_CODE = 1000000001;
		public static final long H5_LOGIN_CODE = 1100000001;
		/**支付验证码模板**/
		public static final long PAY_CODE = 1000000002;
		/**充值短信模板**/
		public static final long RE_TEMPLATE_ID = 1000000003;
		/**下单通知模版**/
		public static final long USER_PLACE_ORDER = 1000000004;
		/**重置登录密码模版**/
		public static final long RESET_CODE = 1000000005;
		/**告知用户已绑定银行卡模版**/
		public static final long BINGING_BANK=1000000006;
		/**修改登录手机验证码模版**/
		public static final long MODIFY_PHONE=1000000007;
		/**提现短信模版**/
		public static final long WITHDRAWALS_TEMPLATE_ID= 1000000008;
		/**支付转出短信模板*/
		public static final long TURNOUT_TEMPLATE_ID = 1000000009;
		/**支付转入短信模板*/
		public static final long TURNIN_TEMPLATE_ID = 1000000010;
		/***我的钱包扣减模板*/
		public static final long PAY_TEMPLATE_ID = 1000000011;
		/***验证身份证发送短信*/
		public static final long IDENTITY_CHECK=1000000012;
		/***系统管理员注册发短信通知被注册用户*/
		public static final long SYS_REGISTE_ID=1000000013;

		/***审核通过发短信通知用户*/
		public static final long SYS_AUTH_ID=1000000014;

		/***新增/修改车辆随车司机通知司机用户*/
		public static final long N_M_SEND_DRIVER=1000000015;

		/***修改车辆随车司机通知旧司机用户*/
		public static final long N_M_SEND_OLD_DRIVER=1000000016;

		/***司机确认绑定发送给司机*/
		public static final long COMFIRM_CAR=1000000017;

		/**现金付款密码校验失败次数超过3次后发送给车队长*/
		public static final long PAY_CASH_RESET_CODE=1000000030;
		/**线下打款超出5天为确认收款发送短信*/
		public static final long PAY_CASH_SMS5=1000000040;
		/**线下打款超出7天为确认收款发送短信*/
		public static final long PAY_CASH_SMS7=1000000050;
		/**线上打款成功*/
		public static final long PAY_CASH_SMS=1000000060;
		/**司机车辆信息不完整发生短信**/
		public static final long PAY_VEHICLE_OR_DRIVER=1000000070;
		/**车队撤回打款通知司机**/
		public static final long PAY_DRIVER=1000000080;

		/**订单确认－发送给货主*/
		public static final long COMFIRM_GOODS=200000001;
		/***订单调度－发送给货主**/
		public static final long DISPATCH_GOODS=200000002;
		/***订单取消－发送给业务员**/
		public static final long CANCEL_BUSI=200000003;
		/***订单取消－发送给货主**/
		public static final long CANCEL_GOODS=200000004;
		/***订单发起取消－发送给业务员**/
		public static final long CANCEL_HAS_BUSI=200000005;
		/***订单发起取消－发送给货主**/
		public static final long CANCEL_HAS_GOODS=200000006;
		/***订单调度－发送给司机**/
		public static final long DISPATCH_CAR=200000007;
		/***订单确认－司机接单**/
		public static final long COMFIRM_TO_CAR=200000008;
		/***异常处理-发送给车主和货主**/
		public static final long EXCEPTION_DEAL=200000009;

		/**志鸿推送模版－竞价结果*/
		public static final long BIDD_RESULT=200000010;

		/**志鸿推送模版－指派给司机*/
		public static final long APPOINT_CAR=200000011;

		/**志鸿推送模版－竞价中标结果*/
		public static final long BIDD_RESULT_SUCC=1000010002;

		/**志鸿推送模版－竞价未中标结果*/
		public static final long APPOINT_CAR_FAIL=1000010003;

		/**志鸿推送模版－司机到达*/
		public static final long WARN_CAR_ARRIVE = 3000000002L;
		/**志鸿推送模版－司机未到达*/
		public static final long WARN_CAR_NO_ARRIVE = 3000000001L;
		/**志鸿推送模版－司机上传回单*/
		public static final long WARN_CAR_RECEIVE = 3000000004L;
		/**志鸿推送模版－司机上传回单*/
		public static final long WARN_CAR_RECEIVE_2 = 3000000005L;
		/**志鸿推送模版－财务审核通过*/
		public static final long FINANCE_CHECK_PASS = 3000000008L;
		/**会员权益短信验证码*/
		public static final long MEMBER_BENEFITS = 3000000010L;

		/**添加司机，司机有租户*/
		public static final long ADD_DRIVER_HAVE_TENANT = 3000000011L;
		/**添加司机，司机无租户*/
		public static final long ADD_DRIVER_NO_TENANT = 3000000012L;



		/**添加车辆，车辆有租户*/
		public static final long ADD_DRIVER_HAVE_TENANT_VEHICLE = 3000000013L;
		/**添加车辆，车辆无租户*/
		public static final long ADD_DRIVER_NO_TENANT_VEHICLE = 3000000014L;

		/**添加司机审核通过*/
		public static final long AUDIT_DRIVER_YES = 3000000015L;
		/**添加司机审核未通过*/
		public static final long AUDIT_DRIVER_NO = 3000000016L;

		/**首次添加司机，外调车司机*/
		public static final long ADD_OTHER_DRIVER_ATTENTION = 3000000017L;
		/**首次添加司机，自有车司机*/
		public static final long ADD_OWN_DRIVER_ATTENTION = 3000000018L;
		public static final long QUICK_ADD_OTHER_DRIVER_ATTENTION = 3000000019L;

		/** 公司注册（车队）*/
		public static final long TENANT_REGISTER = 4000000002L;
		/**催单 ：给接单车队的超管发送短信。内容：尊敬的XXX车队，XXX车队给您推送了一个订单，请尽快处理。**/
		public static final long REMINDER=4000000003L;

		/** 创建车队超级管理员密码 */
		public static final long CREATE_TENANT_SUPPER_MANAGER_PASSWORD = 4000000004L;
		/** 重置车队超级管理员密码 */
		public static final long RESET_TENANT_SUPPER_MANAGER_PASSWORD = 4000000005L;
		public static final long MSG_NOTIFY = 4000000012L;
		/** 指派订单模板  {tenantName}给您的车辆{plateNumber}指派了一个订单(订单号{orderId})，请要求完成订单。 **/
		public static final long ORDER_REASSIGN=4000000006L;
		/**订单操作  {tenantName}{opType}了您承运的订单{orderId}。**/
		public static final long ORDER_OPERATE=4000000007L;
		/**'订单异常/时效 {tenantName}给您承运的订单{orderId}，添加{info}**/
		public static final long ORDER_PRO_AGING=4000000008L;
		/**指派订单（第一次）模板
		 * XXX（司机名称）师傅，XXXX（车队简称）给你指派了XX-XX，07-28 20：00靠台（靠台时间）的订单，请尽快下载登录同心智行APP绑定银行卡收取运费，
		 * 下载地址www.xiazai.com (点击可分发到APP store/下载安卓包)
		 * **/
		public static final long FIRST_ORDER_REASSIGN=4000000009L;
		/**临时车队-指派订单
		 * 您好，${tenantName}车队给您的车辆${plateNumber}推送了一个订单，
		 * 请登录微信小程序查看详情及提取运费（在微信搜索小程序“同心智行-车队”并关注，您的登录账号是${userPhone}。
		 * [同心智行]
		 * **/
		public static final long ORDER_REASSIGN_TEMP=4000000011L;

		/**车辆违章处理通知
		 * 亲爱的${LINK_MAN}用户,您的车牌号${PLATE_NUMBER}存在一个罚款为￥${VIOLATION_FINE}的违章，请尽快处理!
		 * [同心智行]
		 * **/
		public static final long VIOLATION_NOTIFY_TEMP=4000000030L;

		/**车管操作切换司机通知
		 * ${tenantName}给您指派了订单${orderId}，请按要求完成订单。
		 */
		public static final long TUBE_SWITCH_TEMP= 4000000031L;
		/**司机操作切换司机通知
		 * 司机${carDriverName}(${carDriverPhone})申请转移订单${orderId}。
		 */
		public static final long DRIVER_SWITCH_TEMP= 4000000032L;
		/**
		 * 平安银行对账异常通知
		 */
		public static final long BANK_RECONCILIA_TEMP= 4000000033L;

		/**车辆违章 发短信给车队管理员
		 * 截止${deadline}，贵公司共有${recordCount}条未处理的违章，可在违章管理中处理或联系${serviceName}（电话${servicePhone}）处理。
		 * **/
		public static final long VIOLATION_TENANT_TEMP=4000000034L;

		/**车辆违章 发短信给车队对应的售后
		 * 截止${deadline}，车队${tenantName}共有${recordCount}条未处理的违章，请提醒车队处理。
		 * **/
		public static final long VIOLATION_TENANT_SERVICE_TEMP=4000000035L;

		/**发送司机工资单
		 * ${tenantName}车队给您发送了一个工资单，请及时确认。
		 * **/
		public static final long SEND_DRIVER_SALARY_TEMP=4000000036L;

		/**运营报表更新完成提示
		 * ${reportUpdateMag}
		 * **/
		public static final long REPORT_UPDATE_MSG_TEMP=4000000037L;

		/**充值完成后，发短信通知充值人员
		 * 提醒：尾号为${cardNum}的卡充值${rechargeAmount}元已到账，请在油卡充值中去核销。
		 * **/
		public static final long OIL_RECHARGE_FINISH_TEMP=4000000038L;

		/**
		 * 修改员工手机号码
		 * 尊敬的XXX（员工姓名），您更换手机的验证码为674601，两分钟内有效，请尽快验证哦！[同心智行]
		 */
		public static final long UPDATE_STAFF_PHONE=4000000039L;

		/**
		 * 线路即将过期提醒
		 * 有XX条客户线路即将到期，查看详情>>[同心智行]
		 */
		public static final long LINE_INVALID_DAYS_WARN = 4000000042L;
		public static final long DRIVER_EXPIRED_REMIND = 4000000043L;

		/**审核提醒
		 * 尊敬的${auditMan}，${applyMan}申请了一笔${amount}元的${busiName}，单号${busiId}，请审核
		 * **/
		public static final long AUDIT_TIP=5000000001L;
		/**油卡充值修改卡号提醒
		 * 订单[${orderId}]的充值卡号已被修改，查看详情>>。
		 * **/
		public static final long UPDATE_OIL_CARD=5000000002L;
	}

	public static class MacroVariables {
		public static final String PREFIX_CHAR = "${";
		public static final String SUFFIX_CHAR = "}";
	}
	public static class SysSmsSendConstant {
		public static final int MARKETING_SMS_SEND_FLAG_0 = 0;//未发送
		public static final int MARKETING_SMS_SEND_FLAG_1 = 1;// 已发送

		public static final int MARKETING_SMS_SRC_TYPE_0 = 0;// 短信来源；0：单条发送, 1：批量发送, 2：TASK批量发送
		public static final int MARKETING_SMS_SRC_TYPE_1 = 1;// 短信来源；0：单条发送, 1：批量发送, 2：TASK批量发送
		public static final int MARKETING_SMS_SRC_TYPE_2 = 2;// 短信来源；0：单条发送, 1：批量发送, 2：TASK批量发送
	}

	public static class RetInfo {
		public static final String RESULT_CODE = "resultCode";
		public static final String RESULT_CODE_SUCCESS = "1"; // 业务受理成功返回码
		public static final String RESULT_CODE_FAILURE = "0";// 业务受理失败返回码
		public static final String RESULT_CODE_SESSION_FAILURE = "-1";// 登录失效返回码
		public static final String RESULT_MESSAGE = "resultMessage";
		public static final String RESULT_MESSAGE_SUCCESS_DEFAULT_MESSAGE = "业务受理成功";
		public static final String RESULT_MESSAGE_FAILURE_DEFAULT_MESSAGE = "业务受理失败";
		public static final String RESULT_MESSAGE_SESSION_FAILURE_DEFAULT_MESSAGE = "登录会话失效，请重新登录";
		public static final String RESULT_DATA = "resultData";
	}

	/**短信黑名单类型**/
	public static class SmsBlackListType{
		/**接收推送信息，不接收短信 0**/
		public static final String RECEIVE_PUSH_TYPE="0";
		/**不接收推送信息，不接收短信 1**/
		public static final String RECEIVE_NO_TYPE="1";

	}

	public static class WX{
		public static final String BODY = "平台充值";
	}

	/**
	 * 进程静态变量
	 */
	public static class Task{

		public static final String KEEP_MATCH_DATA_DAY="KEEP_MATCH_DATA_DAY";//保留匹配时间参数
		public static final String SEND_INTEGRAL_THREAD_COUNT="SEND_INTEGRAL_THREAD_COUNT";//赠送积分线程数
		/***配置每次进程查询的数据量***/
		public static final String TIME_OUT_ORDER_COUNT="TIME_OUT_ORDER_COUNT";
	}

	/**
	 * 保险系统参数
	 */
	public static class Premium{
		//保险执行业务逻辑
		public static final String  INSURE = "INSURE"; //投保
		public static final String  QUERY_PREMIUM = "QUERY_PREMIUM"; //查询
		public static final String  DOWNLOAD_PREMIUM = "DOWNLOAD_PREMIUM";//电子保单下载

		public static final String PREMIUM_USERNAME = "PREMIUM_USERNAME"; //保险登录名
		public static final String MD5KEY_STRING="MD5KEY_STRING"; //保险密匙
		public static final String DOWNLOAD_STRING = "DOWNLOAD_STRING"; //电子保单下载url
		public static final String ROUTINE_PREMIUM_URL = "ROUTINE_PREMIUM_URL";  //投保
		public final static long MIN_PREMIUM = 1; ///最低保费设置为1分
	}
	/**
	 * 保险业务逻辑
	 */
	public static class BusinessLogic{
		public static final String ADD_BUSINESS_LOGIC = "AddFreightPolicyWeb";  //投保业务逻辑
		public static final String QUERY_BUSINESS_LOGIC = "QueryFreightPolicy";  //查询保险业务逻辑
		public static final String RECONCILIATION_BUSINESS_LOGIC = "QueryFreightPolicyByActionDate"; //对账业务逻辑
		public static final String DO_QUERY_PREMIUM = "QueryFreightPolicy"; // 查询保险根据投保单号
		public static final String DO_QUERY_PREMIUM_MY = "QueryFreightPolicyByUserDesc"; // 自定义查询保险
		public static final String PREMIUM_DUI_ZHANG = "QueryFreightPolicyByActionDate"; // 对账功能（根据日期查询保单信息）
		public static final String UPDATE_PREMIUM = "UpdateFreightPolicy"; // 单号修改
		public static final String CANCAL_PREMIUM = "CancelFreightPolicy";//单号撤销
		public static final String DOWNLOAD_PREMIUM = "DownXMLSignPolicyLong";//电子保单下载
		public static final String HISTORY_PREMIUM_SHEN_HE = "QueryAudHistoryByPolicyNo"; //根据投保单号查询该保单的审核历史
		public static final String HISTORY_PREMIUM_QUERY = "QueryFreightHistoryByPolicyNo";//根据投保单号查询保单的历史
		public static final String CHONG_FU_FA_PIAO ="VerificationInvNo"; //验证发票号重复
		public static final String DOWNLOAD_TOU_PDF = "DownInsurePdf"; //下载投保单pdf抄件
		public static final String DOWNLOAD_PDF = "DownPolicyPdf"; //下载保单pdf抄件
		public static final String PRINT_PREMIUM = "PrintXMLFreightPolicy"; // 保单打印
		public static final String ZHU_XIAN_TIAO_KUANG ="QueryFreightMainClauseCode"; //获取主险条款数据
		public static final String FU_JIA_XIAN_TIAO_KUANG = "QueryFreightAdditiveClauseCode";//获取附加险条款数据
		public static final String HUO_QU_GOOD_TYPE = "QueryGoodsType"; //获取货物类型数据
		public static final String QUERY_PIE_CODE = "QueryAgentLocCode"; //获取理赔代理数据
		public static final String BI_TYPE = "QueryCurrency"; //获取币种数据
		public static final String INSURANCE_NAME = "国内货物运输保险"; //获取币种数据
	}


	public static class ZhPayInter{
		//充值
		public static final long RECHARGE_CODE = 21000000L;
		//托管入账
		public static final long ENTRUST_CODE=21000001L;
		//预付款
		public static final long BEFORE_PAY_CODE=21000002L;
		//未到期转可用
		public static final long PAYFOR_CODE=21000003L;
		//提现
		public static final long WITHDRAWALS_CODE=21000004L;
		//保费
		public static final long INSURANCE_CODE=21000005L;
		//预支手续费
		public static final long POUNDAGE_CODE=21000006L;
		//消费预付油卡
		public static final long CONSUME_OIL_CODE=21000007L;
		//消费购买油卡
		public static final long BUY_OIL_CODE=21000008L;
		//托管转账
		public static final long TRANSFER_TRUST_CODE=21000009L;
		//ETC消费
		public static final long CONSUME_ETC_CODE=21000010L;

	}

	public static class Invitation{
		//web邀请
		public static final String WEB_INVITATION_DES = "WEB_INVITATION_DES";
		public static final String WEB_INVITATION_URL = "WEB_INVITATION_URL";
		public static final String WEB_INVITATION_PIC = "WEB_INVITATION_PIC";
	}

	public static class SHARE{
		//分享
		public static final String SHARE_GOODS_DES = "SHARE_GOODS_DES";
		public static final String SHARE_CARS_DES = "SHARE_CARS_DES";
		public static final String WEB_INVITATION_PIC = "WEB_INVITATION_PIC";
		public static final String SHARE_GOODS_URL = "SHARE_GOODS_URL";
		public static final String SHARE_CARS_URL = "SHARE_CARS_URL";

	}



	public static class GPS_SHARDING {
		public static final int shardingNum = 20;
		public static final double[] latitude = {65, 140};  //纬度范围X(中国）
		public static final double[] longitude = {3, 55};	//经度范围Y(中国）
		public static final double xRang = (latitude[1] - latitude[0]) / shardingNum;
		public static final double yRang = (longitude[1] - longitude[0]) / shardingNum;
	}

	public static class SmsParam{
		/**定义使用哪个短信平台：0 表示用开始的，1表示用E讯通，2表示用创蓝平台 如果没有配置默认用0**/
		//TODO 该定义已经拆分成两个配置，SMS_VERIFY 和  SMS_APP_SEND 请参考syscfg 中配置 WEB V2.6 zc
		public static final String SWITCH="SMS_PARAM_SWITCH";
		/**E讯通 平台**/
		public static final String SWITCH_E="1";
		/**默认的平台**/
		public static final String SWITCH_DEFAULT="0";
		/**创蓝 平台**/
		public static final String SWITCH_CHUANGLAN="2";
		/**玄武 平台**/
		public static final String SWITCH_XUANWU="3";


		/**创蓝平台 发送地址**/
		public static final String SYS_SMS_SEND_PATH_CHUANGLAN="SYS_SMS_SEND_PATH_CHUANGLAN";
		/**创蓝平台 发送账号**/
		public static final String SYA_SMS_ACCT_CHUANGLAN="SYA_SMS_ACCT_CHUANGLAN";
		/**创蓝平台 发送密码**/
		public static final String SYS_SMS_PWD_CHUANGLAN="SYS_SMS_PWD_CHUANGLAN";
		/**创蓝平台 服务器ip**/
		public static final String SYS_SMS_CHUANGLAN_IP="SYS_SMS_CHUANGLAN_IP";
		/**创蓝平台 上行短信的用户名**/
		public static final String SYS_SMS_CHUANGLAN_RECEIVER="SYS_SMS_CHUANGLAN_RECEIVER";
		/**创蓝平台 上行短信的密码**/
		public static final String SYS_SMS_CHUANGLAN_PWD="SYS_SMS_CHUANGLAN_PWD";


		/**玄武平台 发送地址**/
		public static final String SYS_SMS_SEND_PATH_XUANWU="SYS_SMS_SEND_PATH_XUANWU";
		/**玄武平台 发送账号**/
		public static final String SYA_SMS_ACCT_XUANWU="SYA_SMS_ACCT_XUANWU";
		/**玄武平台 发送密码**/
		public static final String SYS_SMS_PWD_XUANWU="SYS_SMS_PWD_XUANWU";
		/**玄武平台 服务器端口**/
		public static final String SYS_SMS_PORT_XUANWU="SYS_SMS_PORT_XUANWU";
		/**玄武平台 服务器ip**/
		public static final String SYS_SMS_IP_XUANWU="SYS_SMS_IP_XUANWU";
		/**玄武平台 营销发送账号**/
		public static final String SYA_SMS_ACCT_XUANWU_MARKET="SYA_SMS_ACCT_XUANWU_MARKET";
		/**玄武平台 营销发送密码**/
		public static final String SYS_SMS_PWD_XUANWU_MARKET="SYS_SMS_PWD_XUANWU_MARKET";
		/**玄武平台 发送地址**/
		public static final String SYS_SMS_SEND_REST_PATH_XUANWU="SYS_SMS_SEND_REST_PATH_XUANWU";

		/**E讯通 发送地址**/
		public static final String SYS_SMS_SEND_PATH_E="SYS_SMS_SEND_PATH_E";
		/**E讯通 发送账号**/
		public static final String SYA_SMS_ACCT_E="SYA_SMS_ACCT_E";
		/**E讯通 发送密码**/
		public static final String SYS_SMS_PWD_E="SYS_SMS_PWD_E";
		/**E讯通 上行的地址**/
		public static final String SYS_SMS_RECEVICE_PATH_E="SYS_SMS_RECEVICE_PATH_E";

		/**发送地址**/
		public static final String SYS_SMS_SEND_PATH="SYS_SMS_SEND_PATH";
		/**发送账号**/
		public static final String SYA_SMS_ACCT="SYA_SMS_ACCT";
		/**发送密码**/
		public static final String SYS_SMS_PWD="SYS_SMS_PWD";
		/**上行的地址**/
		public static final String SYS_SMS_RECEVICE_PATH="SYS_SMS_RECEVICE_PATH";
	}


	public static class isPublicParam{
		public static final int WX_KEY_QUERY_NUM =6; // 微信关键字查询货源车源条数
		public static final String IS_PUBLIC4 ="4"; // 平台可见 枚举值
		public static final int IS_VIEW66 =66; // 查询进行中的枚举值
		public static final int IS_VIEW99 =99; // 查询历史的枚举值
		public static final int IS_VIEW11 =11; // 查询求车中的枚举值
		public static final int IS_YAN_Z =5; // 车辆验证通过的枚举值
	}
	/**第三方登录类型**/
	public static class threeLoginType{
		/**QQ*/
		public static final String QQ ="1";
		/**微信**/
		public static final String WECHAT ="2";
	}
	/**指引**/
	public static class NAVIGATION{
		/**发布车源页面指引**/
		public static final String NAVIGATION_CAR = "NAVIGATION_CAR_";
		/**发布货源页面指引**/
		public static final String NAVIGATION_GOOD = "NAVIGATION_GOOD_";
	}
	/**积分赠送类型**/
	public static class SEND_INTEGRAL_TYPE{
		/**登陆*/
		public static final int LOGIN =1;
		/**发布车源*/
		public static final int PUB_CAR_SOURCE =2;
		/**发布货源*/
		public static final int PUB_GOOD_SOURCE =3;
		/**完成订单*/
		public static final int FINISH_ORDER =4;
		/**我的钱包支付订单*/
		public static final int WALLET_PAY =5;
		/**购买保险*/
		public static final int PURCHASE_OF_INSURANCE =6;
		/**每月成交数*/
		public static final int DEAL_ORDER_COUNT =7;
		/**车辆审核*/
		public static final int AUT_VEHICEL =8;

	}
	/**因为握物流，握同城、安力用的银联商务号是同一个，需要在对账的时候指拿自己需要的数据进行对账，通过添加一个前缀进行处理**/
	public static final String RECONCILIATION="vpal";


	/**
	 * 枚举数据常量定义
	 * @author wengxk
	 *
	 */
	public static class SysStaticDataAL {
		/**省份**/
		public static final String SYS_PROVINCE = "SYS_PROVINCE";
		/**地市**/
		public static final String SYS_CITY = "SYS_CITY";
		/**县区**/
		public static final String SYS_DISTRICT = "SYS_DISTRICT";

	}

	/**
	 * 租户id
	 * @author zhouchao
	 *
	 */
	public static class TenantIdVip{
		public static final long VP_AL = 100L;
		public static final long ZC_HX = 103L;
	}




	/**
	 * 语音模板
	 * @author dacheng
	 *
	 */
	public static class IvrTemplate{

		public static final long DEPEND_TEMPLATE_ID = 1000000000L;//1小时内距离靠台
		public static final long NOARRIVE_TEMPLATE_ID = 1000000001L;//超过30分钟没有到达目地点
		public static final long PAY_TEMPLATE_ID = 1000000002L;//预付款支付呼叫 app
		public static final long BURST_TEMPLATE_ID = 1000000003L;//预计发车/运输过程中 app
		public static final long OVERTIME_TEMPLATE_ID = 1000000004L;//超时司机
		public static final long ROBORDER_TEMPLATE_ID = 1000000005L;//竞价抢单呼叫
		public static final long BID_TEMPLATE_ID = 1000000006L;//中标呼叫

		public static final long GPS_TEMPLATE_ID = 1000000007L;//GPS未在线呼叫
		public static final long RETAINAGE_TEMPLATE_ID = 1000000008L;//尾款到账呼叫
		public static final long RECRUIT_TEMPLATE_ID = 1000000009L;//司机招聘呼叫
		public static final long EXCEPTIONCALL_TEMPLATE_ID = 1000000010L;//异常呼叫
		public static final long LATERISK1_TEMPLATE_ID = 1000000011L;//车辆迟到风险
		public static final long LATERISK2_TEMPLATE_ID = 1000000012L;//车辆迟到风险
		public static final long LATERISK3_TEMPLATE_ID = 1000000013L;//车辆迟到风险
		public static final long LATERISK4_TEMPLATE_ID = 1000000014L;//车辆迟到风险
		public static final long TRAVELEXCEPTION_TEMPLATE_ID = 1000000015L;//司机行驶异常提醒
		public static final long DRIVERREPORT_TEMPLATE_ID = 1000000016L;//堵车司机报备
		public static final long PREAMOUNT1_TEMPLATE_ID = 1000000017L;//支付预付款
		public static final long PREAMOUNT2_TEMPLATE_ID = 1000000018L;//支付预付款
		public static final long PREAMOUNT3_TEMPLATE_ID = 1000000019L;//支付预付款
		public static final long RECEIVEVERIFY1_TEMPLATE_ID = 1000000020L;//回单审核
		public static final long RECEIVEVERIFY2_TEMPLATE_ID = 1000000021L;//回单审核
		public static final long RECEIVEVERIFY3_TEMPLATE_ID = 1000000022L;//回单审核
		public static final long REPORT_TEMPLATE_ID = 1000000023L;//报备
		public static final long CHECKBILL_TEMPLATE_ID = 1000000024L;//账单核对
		public static final long OVERDUERECEIVABLES_TEMPLATE_ID = 1000000025L;//催收款项


	}

	public static class IvrParam{
		//待放置到配置文件
		/**IVR 接口地址**/
		public static final String SYS_IVR_URL="http://a1.7x24cc.com/commonInte";
		/**账号**/
		public static final String SYS_IVR_ACCT="8001@zhwl";
		/**密码**/
		public static final String SYS_IVR_PWD="ZHWL1111";
		/**FLAG**/
		public static final String SYS_IVR_FLAG="107";
		/**ACCOUNT**/
		public static final String SYS_IVR_ACCOUNT="N000000010537";
		/**KEY**/
		public static final String SYS_IVR_KEY="4520d790-2b14-11e7-b57e-13b30839214c";
		/**SERVICENO**/
		public static final String SYS_IVR_SERVICENO= "82404301";//"02028050903";

	}














	/**
	 * 特殊时期 key
	 */
	public final static String SPECIAL_GUIDE_MULRIPLE = "SPECIAL_GUIDE_MULRIPLE";

	/**
	 * 业务类型
	 * @author zx
	 *
	 */
	public static class PayInter{
		//充值
		public static final long RECHARGE_CODE = 21000000L;
		//托管入账
		public static final long ENTRUST_CODE=21000001L;
		//预付款
		public static final long BEFORE_PAY_CODE=21000002L;
		//未到期转可用
		public static final long PAYFOR_CODE=21000003L;
		//提现
		public static final long WITHDRAWALS_CODE=21000004L;
		//保费
		public static final long INSURANCE_CODE=21000005L;
		//预支手续费
		public static final long POUNDAGE_CODE=21000006L;
		//消费预付油卡
		public static final long CONSUME_OIL_CODE=21000007L;
		//消费购买油卡
		public static final long BUY_OIL_CODE=21000008L;
		//托管转账
		public static final long TRANSFER_TRUST_CODE=21000009L;
		//ETC消费
		public static final long CONSUME_ETC_CODE=21000010L;
		//预支
		public static final long ADVANCE_PAY_MARGIN_CODE=21000011L;
		//购买油
		public static final long PAY_FOR_OIL_CODE=21000012L;
		//收入油费
		public static final long INCOME_OIL_CODE=21000013L;
		//尾款收入
		public static final long PAY_FINAL=21000014L;
		//异常补偿
		public static final long EXCEPTION_FEE=21000015L;
		//ETC充值
		public static final long ETC_RECHARGE=21000016L;
		//油卡充值
		public static final long OIL_RECHARGE=21000017L;
		//异常罚款
		public static final long EXCEPTION_FEE_OUT=21000018L;
		//回退提现款
		public static final long BACK_RECHARGE=21000019L;
		//操作招商车费用
		public static final long ZHAOSHANG_FEE=21000020L;
		//油卡结余和ETC结余(进程)
		public static final long OIL_AND_ETC_SURPLUS=21000021L;


		//油卡和ETC转现(功能化)
		public static final long OIL_AND_ETC_TURN_CASH=21000025L;
		//
		public static final long OIL_SURPLUS=21000022L;
		//司机借支金额审核通过转可用
		public static final long OA_LOAN_AVAILABLE=21000023L;
		//车管借支金额审核通过转可用
		public static final long OA_LOAN_AVAILABLE_TUBE=21000113L;
		//司机借支核销
		public static final long OA_LOAN_VERIFICATION=21000024L;


		//车管报销金额审核通过转可用
		public static final long TUBE_EXPENSE_ABLE=21000100L;

		//司机报销金额审核通过转可用
		public static final long DRIVER_EXPENSE_ABLE=21000101L;




		//强制平账
		public static final long FORCE_ZHANG_PING=21000026L;

		//发放工资
		public static final long CAR_DRIVER_SALARY = 220000001L;
		//固定成本反写
		public static final long FIXED_FEE_BACKWRITE = 220000002L;
		//实体油卡核销
		public static final long OIL_ENTITY_VERIFICATION = 220000003L;

		//欠款
		public static final long DEBT = 220000004L;
		//修改订单
		public static final long UPDATE_ORDER_PRICE = 220000010L;
		//撤销订单
		public static final long REPEAL_ORDER_PRICE = 220000011L;
		//ETC自动充值
		public static final long ETC_PAY_CODE = 220000012L;
		//支付维修单
		public static final long PAY_FOR_REPAIR = 22000013L;
		//收入维修费
		public static final long INCOME_REPAIR = 22000014L;
		//报销费用
		public static final long EXPENSE_CODE = 220000366L;
		//维修基金转现(提现到银行卡)
		public static final long REPAIR_FUND_TURN_CASH = 22000015L;
		//撤单订单回退金额
		public static final long CANCEL_THE_ORDER = 22000016L;
		//抵押释放油卡金额
		public static final long  PLEDGE_RELEASE_OILCARD = 22000017L;
		//平安账户资金转移
		public static final long  PINGAN_ACCOUNT_TURN_CASH = 22000018L;
		//修改订单(新)
		public static final long UPDATE_THE_ORDER = 22000019L;
		//平台服务费核销
		public static final long VERIFICATION_SERVICE_CHARGE = 22000020L;
		//违章费用
		public static final long VIOLATION_FEE = 22000021L;
		//订单费用上报
		public static final long ORDER_COST_REPORT = 22000022L;
		//充值油费
		public static final long RECHARGE_ACCOUNT_OIL = 22000023L;
		//清零油账户(实报实销模式油)
		public static final long CLEAR_ACCOUNT_OIL = 22000024L;
		/**
		 * 外系统支付类科目
		 */
		public static final long BANK_PAYMENT_OUT = 30000000L;
		//开票服务费(申请开平台票)
		public static final long PLATFORM_BILL_SERVICE_FEE = 22000025L;
		//付款管理
		public static final long PAY_MANGER = 22000026L;
		//后服充值(油卡充值)
		public static final long SERVICE_RECHARGE = 22000027L;
		//到付款
		public static final long PAY_ARRIVE_CHARGE = 22000028L;
		//招商车/挂靠车对账单
		public static final long ACCOUNT_STATEMENT = 22000029L;
		//北斗缴费
		public static final long BEIDOU_PAYMENT = 22000030L;
		//后服充值(服务商圈退)
		public static final long SERVICE_RETREAT = 22000032L;
		//已付待提现（收款方未绑卡付款）
		public static final long PAY_DRIVER = 22000033L;
		//已付撤回（收款方未绑卡付款）
		public static final long PAY_WITHDRAW = 22000034L;
		//支付（收款方未绑卡付款）
		public static final long PAY_CASH = 22000035L;
		//提现（收款方未绑卡付款）
		public static final long PAY_MENTION = 22000036L;
		//路歌最后一笔运费服务费
		public static final long LUGE_SERVICE_FEE = 22000037L;

		/*//订单业务ID
		public static final long BUSS_ORDER_CODE = 21000001L;

		//转账业务ID
		public static final long TRANSFER_CODE=21000004L;
		//平台发送邀请红包ID
		public static final long WALLET_RED_CODE=21000006L;
		//收款码支付业务ID
		public static final long RECEIVE_MONEY_MA=21000007L;
		//提现反冲业务ID
		public static final long WITHDRAWALS_ROLL=21000008L;
		//验证身份证
		public static final long IDENTITY_CHECK=21000009L;
		//保证金费用
		public static final long BOND_FEE_CODE=21000012L;
		//保险反冲
		public static final long INSURANCE_ROLL=21000013L;
		//积分兑换扣减业务ID
		public static final long COST_INTEGRAL=21000014L;
		*//**登陆积分赠送*//*
		public static final long LOGIN =21000020L;
		*//**发布车源积分赠送*//*
		public static final long PUB_CAR_SOURCE =21000021L;
		*//**发布货源积分赠送*//*
		public static final long PUB_GOOD_SOURCE =21000022L;
		*//**完成订单积分赠送*//*
		public static final long FINISH_ORDER =21000023L;
		*//**我的钱包支付订单积分赠送*//*
		public static final long WALLET_PAY =21000024L;
		*//**购买保险积分赠送*//*
		public static final long PURCHASE_OF_INSURANCE =21000025L;
		*//**每月成交数积分赠送*//*
		public static final long DEAL_ORDER_COUNT =21000026L;
		*//**车辆审核积分赠送*//*
		public static final long AUT_VEHICEL =21000027L;*/
		//业务大类订单
		public static final int ORDER_TYPE_CODE=1;
		//业务大类保险
		public static final int INSURANCE_TYPE_CODE=2;
		//业务大类充值
		public static final int RECHARGE_TYPE_CODE=3;
		//业务大类转账
		public static final int TRANSFER_TYPE_CODE=4;
		//业务大类提现
		public static final int WITHDRAWALS_TYPE_CODE=5;
		//业务大类反冲
		public static final int RECOIL_TYPE_CODE=8;
		//业务大类红包
		public static final int WALLET_TYPE_CODE=6;
		//业务大类积分
		public static final int INTEGRAL_TYPE_CODE=7;
		//业务大类其他
		public static final int OTHER_TYPE_CODE=9;
		//账本科目(可用金额)
		public static final long ACCOUNT_CODE=3001L;
		//红包科目
		public static final long WALLET_CODE=3002L;
		//未到期金额科目
		public static final long BOND_CODE=3003L;
		//账本科目(油卡)
		public static final long OIL_CODE=3004L;
		//未到期转可用（未到期金额操作）
		public static final long CHANGE_CODE=3005L;
		//预支账户操作
		public static final long BEFOREPAY_CODE=3006L;
		//ETC账户
		public static final long ETC_CODE=3007L;
		//维修基金账户
		public static final long REPAIR_FUND_CODE=3009L;
		//油卡抵押金额
		public static final long OIL_CARD_PLEDGE_CODE=3010L;
		//应收逾期
		public static final long RECEIVABLE_OVERDUE_CODE=3011L;
		//应付逾期
		public static final long PAYABLE_OVERDUE_CODE=3012L;

		//已收逾期
		public static final long RECEIVABLED_OVERDUE_CODE=3013L;
		//已付逾期
		public static final long PAYABLED_OVERDUE_CODE=3014L;
		//充值油账户
		public static final long RECHARGE_OIL_CODE=3015L;


		//充值账户
		public static final long PAY_CASH_CODE=4001L;

		//工资发放
		public static final long SALARY_DUAL = 3099L;
		//邀请一个人给的红包10元
		public static final long WALLET_RED_MONEY=1000L;  //单位分
		//发红包的科目ID （要跟数据库的一致）
		public static final long WALLET_RED_SUBJECT_ID=1015L;
		/**收款码支付科目ID 1016L（要跟数据库的一致）*/
		public static final long ER_WEI_SUBJECT_ID=1016L;
		/**订单支付科目ID  1007L（要跟数据库的一致）*/
		public static final long ORDER_PAY_SUBJECT_ID=1007L;

		/**提现反冲科目ID  1017L（要跟数据库的一致）*/
		public static final long CASE_SUBJECT_ID=1017L;
		/**积分扣减科目id（要跟数据库的一致）*/
		public static final long SUB_INTEGRAL =1022L;
		/**登陆积分赠送科目id（要跟数据库的一致）*/
		public static final long LOGIN_SUBJECT_ID =1023L;
		/**发布车源积分赠送科目id（要跟数据库的一致）*/
		public static final long PUB_CAR_SOURCE_SUBJECT_ID =1025L;
		/**发布货源积分赠送科目id（要跟数据库的一致）*/
		public static final long PUB_GOOD_SOURCE_SUBJECT_ID =1025L;
		/**完成订单积分赠送科目id（要跟数据库的一致）*/
		public static final long FINISH_ORDER_SUBJECT_ID =1026L;
		/**我的钱包支付订单积分赠送科目id（要跟数据库的一致）*/
		public static final long WALLET_PAY_SUBJECT_ID =1027L;
		/**购买保险积分赠送科目id（要跟数据库的一致）*/
		public static final long PURCHASE_OF_INSURANCE_SUBJECT_ID =1028L;
		/**每月成交数积分赠送科目id（要跟数据库的一致）*/
		public static final long DEAL_ORDER_COUNT_SUBJECT_ID =1029L;
		/**车辆审核积分赠送科目id（要跟数据库的一致）*/
		public static final long AUT_VEHICEL_SUBJECT_ID =1030L;
		//算费费用类型：不固定
		public static final long NON_FIXATION=1L;
		//算费费用类型：比例
		public static final long PROPORTION=3L;
		//算费费用类型：实现类
		public static final long CLASS=4L;
		//算费费用类型：固定
		public static final long FIXED=2L;
		//支出
		public static final int FEE_OUT=1;
		//支入
		public static final int FEE_IN=2;
		//收支根据金额正负决定
		public static final int FEE_INOUT=3;
		//现金充值费用科目
		public static final long CASH_CHARGE=1001L;
		//手续费费用科目
		public static final long COUNTER_FEE=1012L;
		//充值保证金科目
		public static final long BOND_CHARGE=1021L;
		//转账
		public static final long TRANSFER=1009L;
		//提现
		public static final long WITHDRAWALS=1013L;
		//提现
		public static final int WITHDRAWALS_OBJID=6;
		//信息费用科目
		public static final long INFO_FEE_SUBJECT_ID=1005L;
		//信息费
		public static final long INFO_FEE_CODE=21000019L;
		/**放空费业务id*/
		public static final long EMPTY_FEES_BUSS_ID=31000003L;
		/**放空费科目id*/
		public static final long EMPTY_FEES_CODE=1031L;


		// LOGBI手工充值业务ID
		public static final long HAND_RECHARGE_CODE = 31000001L;
		// LOGBI手工提现业务ID
		public static final long HAND_WITHDRAWALS_CODE = 31000002L;

		//打款记录拆分
		public static final String SPLIT_CODE="SPLIT_";

		//ETC 账单付款
		public static final long ETC_BILL_RECHARGE_CODE = 23000000L;
	}
	/**
	 * 业务大类
	 * @author zx
	 *
	 */
	public static class BusiType{
		//充值
		public static final int RECHARGE_TYPE_CODE = 1;
		//出入账
		public static final int ACCOUNT_INOUT_CODE=2;
		//收入款
		public static final int INCOMING_CODE=3;
		//手续费
		public static final int POUNDAGE_CODE=4;
		//消费
		public static final int CONSUME_CODE=5;
		//异常
		public static final int EXCEPTION_CODE=6;

	}
	public static class PinganLockType{
		public static final int oil=1;//油卡
	}
	/**
	 * 费用科目
	 * @author zx
	 *
	 */
	public static class SubjectIds{
		//充值
		public static final long RECHARGE_SUB = 1000L;
		//托管入账
		public static final long ENTRUST_SUB=1001L;
		//预付款(可用账户)
		public static final long BEFORE_PAY_SUB=1002L;
		//预付款(油费)
		public static final long BEFORE_OILPAY_SUB=1011L;
		//预付款(混合油卡实体金额)
		public static final long BEFORE_Entity_SUB=2236L;
		//预付款(混合油卡虚拟金额)
		public static final long BEFORE_Virtual_SUB=2237L;
		//预付款(ETC)
		public static final long BEFORE_ETC_SUB=1012L;
		//未到期转可用
		public static final long PAYFOR_SUB=1003L;
		//提现
		public static final long WITHDRAWALS_SUB=1004L;
		//提现（HA虚拟-HA实体）
		public static final long WITHDRAWALS_HA=2004L;
		//提现（外系统提现）
		public static final long WITHDRAWALS_OUT=2006L;
		//自动提现（HA实体-收款方）
		public static final long WITHDRAWALS_AUTO=2007L;
		//退款
		public static final long REFUND_SUB=2005L;
		//保费
		public static final long INSURANCE_SUB=1005L;
		//预支手续费
		public static final long POUNDAGE_SUB=1006L;
		//消费预付油卡(油卡金额)
		public static final long CONSUME_OIL_SUB=1007L;
		//消费购买油卡(可用金额)
		public static final long BUY_OIL_SUB=1008L;
		//托管转账
		public static final long TRANSFER_TRUST_SUB=1009L;
		//ETC消费
		public static final long CONSUME_ETC_SUB=1010L;
		//ETC消费现金
		public static final long CONSUME_ETC_CASH_SUB=1026L;
		//抵扣预支-抵扣预支（未到期转可用抵扣司机的预支款）
		public static final long RELEASE_BEFOREPAY_SUB=1013L;
		//预支金额-预支金额（预支金额（未到期金额扣减））
		public static final long BEFOREPAY_SUB=1014L;
		//预支账户新增金额   预支操作账户表的before_pay字段
		public static final long BEFOREPAY_ADD_SUB=1015L;
		//收入油费（油老板收入）
		public static final long INCOME_OIL_SUB=1016L;
		//收入油费手续费
		public static final long INCOME_OIL_FEE=1024L;
		//收入油费（油老板收入）针对资金渠道上线
		public static final long INCOME_OIL_SUB_NEW=2016L;
		//收入油费手续费  针对资金渠道上线
		public static final long INCOME_OIL_FEE_NEW=2024L;
		//尾款收入
		public static final long FINAL_CHARGE=1017L;
		//异常补偿
		public static final long EXCEPTION_FEE=1018L;
		//etc充值
		public static final long ETC_RECHARGE=1019L;
		//油卡充值
		public static final long OIL_RECHARGE=1020L;
		//异常罚款
		public static final long EXCEPTION_FEE_OUT=1021L;
		//回退提现款
		public static final long BACK_RECHARGE=1022L;
		//操作招商车费用
		public static final long ZHAOSHANG_FEE=1023L;
		//油费优惠返现
		public static final long DISCOUNT_FEE=1025L;
		//经纪人预付信息费
		public static final long BROKER_BEFORE_PAY=1027L;
		//经纪人尾款信息费
		public static final long BROKER_FINAL_PAY=1028L;
		//操作招商车费用 租金(尾款扣除)
		public static final long ZHAOSHANG_RENT=1029L;
		//操作招商车费用 etc金额(尾款扣除)
		public static final long ZHAOSHANG_ETC=1030L;
		//操作招商车费用 管理(尾款扣除)
		public static final long ZHAOSHANG_ADMINISTRATIVE=1031L;
		//操作招商车费用 gps(尾款扣除)
		public static final long ZHAOSHANG_GPS=1032L;
		//操作招商车费用 加油(尾款扣除)
		public static final long ZHAOSHANG_FUELFILLING=1033L;
		//操作招商车费用 维修(尾款扣除)
		public static final long ZHAOSHANG_REPAIR=1034L;
		//操作招商车费用 车船税(尾款扣除)
		public static final long ZHAOSHANG_VEHICLETRAVELTAX=1035L;
		//操作招商车费用 轮胎(尾款扣除)
		public static final long ZHAOSHANG_TYRE=1036L;
		//操作招商车费用 挂车审验(尾款扣除)
		public static final long ZHAOSHANG_TRAILERCERTIFICATION=1037L;
		//操作招商车费用 车辆审验(尾款扣除)
		public static final long ZHAOSHANG_MOTORVEHICLES=1038L;
		//操作招商车费用 挂车保险(尾款扣除)
		public static final long ZHAOSHANG_TRAILERINSURANCEPREMIUM=1039L;
		//操作招商车费用 违章(尾款扣除)
		public static final long ZHAOSHANG_ILLEGAL=1040L;
		//操作招商车费用 车辆保险(尾款扣除)
		public static final long ZHAOSHANG_VEHICLEINSURANCE=1041L;
		//操作招商车费用 挂车租赁费(尾款扣除)
		public static final long ZHAOSHANG_SPARE1=1042L;
		//操作招商车费用 其他配件(尾款扣除)
		public static final long ZHAOSHANG_SPARE2=1043L;
		//操作招商车费用 交强险(尾款扣除)
		public static final long ZHAOSHANG_SPARE3=1044L;
		//操作招商车费用 商业险(尾款扣除)
		public static final long ZHAOSHANG_SPARE4=1045L;
		//操作招商车费用 挂车车船税(尾款扣除)
		public static final long ZHAOSHANG_SPARE5=1046L;
		//操作招商车费用 挂车轮胎磨损(尾款扣除)
		public static final long ZHAOSHANG_SPARE6=1047L;
		//操作招商车费用 其他费用(尾款扣除)
		public static final long ZHAOSHANG_SPARE7=1048L;
		//ETC消费未到期金额扣除
		public static final long CONSUME_ETC_PAYFOR_SUB=1049L;
		//ETC消费预支增加
		public static final long CONSUME_ETC_BEFOREPAY_SUB=1050L;
		//操作招商车费用 租金(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_RENT=1051L;
		//操作招商车费用 etc金额(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_ETC=1052L;
		//操作招商车费用 管理(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_ADMINISTRATIVE=1053L;
		//操作招商车费用 gps(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_GPS=1054L;
		//操作招商车费用 加油(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_FUELFILLING=1055L;
		//操作招商车费用 维修(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_REPAIR=1056L;
		//操作招商车费用 车船税(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_VEHICLETRAVELTAX=1057L;
		//操作招商车费用 轮胎(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_TYRE=1058L;
		//操作招商车费用 挂车审验(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_TRAILERCERTIFICATION=1059L;
		//操作招商车费用 车辆审验(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_MOTORVEHICLES=1060L;
		//操作招商车费用 挂车保险(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_TRAILERINSURANCEPREMIUM=1061L;
		//操作招商车费用 违章(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_ILLEGAL=1062L;
		//操作招商车费用 车辆保险(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_VEHICLEINSURANCE=1063L;
		//操作招商车费用 挂车租赁费(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE1=1064L;
		//操作招商车费用 其他配件(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE2=1065L;
		//操作招商车费用 交强险(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE3=1066L;
		//操作招商车费用 商业险(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE4=1067L;
		//操作招商车费用 挂车车船税(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE5=1068L;
		//操作招商车费用 挂车轮胎磨损(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE6=1069L;
		//操作招商车费用 其他费用(预支增加)
		public static final long ZHAOSHANG_BEFOREPAY_SPARE7=1070L;
		//ETC金额转未到期
		public static final long ETC_TURN_NOTDUE=1071L;
		//ETC未到期金额转可用
		public static final long ETC_TURN_AVAILABLE=1072L;
		//司机借支金额审核通过转可用
		public static final long OA_LOAN_AVAILABLE=1073L;
		//司机借支金额支出
		public static final long OA_LOAN_AVAILABLE_OUT=1420L;
		//司机借支金额违章罚款支出
		public static final long OA_LOAN_VIOLATION_OUT=1421L;
		//司机借支核销
		public static final long OA_LOAN_VERIFICATION=1074L;

		//车管借支支出
		public static final long OA_LOAN_CAR_AVAILABLE_OUT=1120L;


		//应付逾期(司机借支)
		public static final long OA_LOAN_HANDLE=1430L;
		//应收逾期(司机借支)
		public static final long OA_LOAN_RECEIVABLE=1431L;

		//已付逾期(司机借支)
		public static final long OA_LOAN_PAID=5003L;

		//已收逾期(司机借支)
		public static final long OA_LOAN_RECEIVED=5002L;

		//应付逾期(车管借支)
		public static final long OA_LOAN_TUBE_HANDLE=1440L;
		//应收逾期(车管借支)
		public static final long OA_LOAN_TUBE_RECEIVABLE=1441L;



		//应付逾期(司机报销)
		public static final long EXPENSE_HANDLE=1442L;
		//应收逾期(司机报销)
		public static final long EXPENSE_RECEIVABLE=1443L;

		//已付逾期(司机报销)
		public static final long EXPENSE_PAID=50011L;
		//已收逾期(司机报销)
		public static final long EXPENSE_RECEIVED=50010L;

		//应付逾期(车管报销)
		public static final long EXPENSE_TUBE_HANDLE=1444L;
		//应收逾期(车管报销)
		public static final long EXPENSE_TUBE_RECEIVABLE=1445L;

		//应付逾期(工资发放)
		public static final long SALARY_HANDLE=1730L;
		//应收逾期(工资发放)
		public static final long SALARY_RECEIVABLE=1731L;

		//驾驶员工资补贴明细结算
		public static final long SALARY_RECEIVABLES=60002L;
		//已付逾期(工资发放)
		public static final long SALARY_PAID=50043L;
		//已收逾期(工资发放)
		public static final long SALARY_RECEIVED=50042L;


		//已付逾期(司机借支核销)
		public static final long OA_LOAN_VERIFICATION_HANDLE=1446L;
		//已收逾期(司机借支核销)
		public static final long OA_LOAN_VERIFICATION_RECEIVABLE=1447L;

		//司机保养费借支
		public static final long LOAN_SUBJECT0=1209L;
		//司机保养费核销
		public static final long VERIFICATION_SUBJECT0=1208L;
		//司机停车费费借支
		public static final long LOAN_SUBJECT1=1210L;
		//司机停车费核销
		public static final long VERIFICATION_SUBJECT1=1215L;
		//司机违章罚款借支
		public static final long LOAN_SUBJECT2=1212L;
		//司机违章罚款核销
		public static final long VERIFICATION_SUBJECT2=1211L;
		//司机过磅费借支
		public static final long LOAN_SUBJECT3=1213L;
		//司机过磅费核销
		public static final long VERIFICATION_SUBJECT3=1214L;
		//司机维修费借支
		public static final long LOAN_SUBJECT4=1217L;
		//司机维修费核销
		public static final long VERIFICATION_SUBJECT4=1216L;
		//司机轮胎费借支
		public static final long LOAN_SUBJECT5=1219L;
		//司机轮胎费核销
		public static final long VERIFICATION_SUBJECT5=1218L;
		//司机其他费借支
		public static final long LOAN_SUBJECT6=1221L;
		//司机其他费核销
		public static final long VERIFICATION_SUBJECT6=1220L;



		//工资借支扣减
		public static final long SALARY_BORROW_DEDUCTION=1200L;
		//工资异常扣减
		public static final long SALARY_EXCEPTION_DEDUCTION=1201L;
		//个人社保扣减
		public static final long SALARY_INS_DEDUCTION=1202L;
		//工资个税扣减
		public static final long SALARY_TAX_DEDUCTION=1203L;
		//工资其它增加
		public static final long SALARY_OTHER_ADD=1204L;
		//工资其它减少
		public static final long SALARY_OTHER_DEDUCTION=1205L;
		//实发工资
		public static final long SALARY_WAIT_DEDUCTION=1206L;
		//实发补贴
		public static final long SALARY_SUB_DEDUCTION=1207L;
		//迟到罚款扣减
		public static final long SALARY_AGING_DEDUCTION=1701L;
		//个人公积金扣减
		public static final long SALARY_FUND_DEDUCTION=1702L;
		//风险金扣减
		public static final long SALARY_RISK_DEDUCTION=1703L;

		/**
		 * 违章罚款扣减
		 */
		public static final long SALARY_VIOLATION_DEDUCTION=1704L;
		/**
		 * 工资停车费扣减
		 */
		public static final long SALARY_BORROW_DEDUCTION3=1705L;
		/**
		 * 工资过磅费扣减
		 */
		public static final long SALARY_BORROW_DEDUCTION4=1706L;
		/**
		 * 工资路桥费扣减
		 */
		public static final long SALARY_BORROW_DEDUCTION6=1707L;
		/**
		 * 工资油费扣减
		 */
		public static final long SALARY_BORROW_DEDUCTION8=1708L;
		/**
		 * 里程工资
		 */
		public static final long SALARY_AWARD9=1709L;
		/**
		 * 时效考核
		 */
		public static final long SALARY_AWARD10=1710L;
		/**
		 * 星级奖励
		 */
		public static final long SALARY_AWARD11=1711L;
		/**
		 * 安全奖
		 */
		public static final long SALARY_AWARD12=1712L;
		/**
		 * 全勤奖
		 */
		public static final long SALARY_AWARD13 = 1713L;
		/**
		 * 其他应发
		 */
		public static final long SALARY_AWARD14=1714L;
		/**
		 * 上月欠款
		 */
		public static final long SALARY_AWARD15 = 1715L;
		/**
		 * 订单欠款
		 */
		public static final long SALARY_AWARD16 = 1716L;
		/**
		 * 按趟工资
		 */
		public static final long SALARY_TRIP =1719L;
		/**
		 * 工资确认抵扣订单欠款(对账户欠款操作)
		 */
		public static final long CONFIRM_SALARY_DEDUCTION_ORDER_DEBT =1720L;

		//核销工资
		public static final long SALARY_VERIFICATION = 1721L;
		/**
		 * 工资借支差旅费扣减
		 */
		public static final long SALARY_BORROW_DEDUCTION11=1722L;

		//路桥费(自有车支付预付款)只记录流水不做扣减
		public static final long BEFOREPAY_BRIDGE=1075L;
		//补贴(自有车支付预付款-主驾驶)
		public static final long BEFOREPAY_MASTER_SUBSIDY=1076L;
		//补贴(自有车支付预付款-副驾驶)
		public static final long BEFOREPAY_SLAVE_SUBSIDY=1077L;
		//实体油卡(自有车支付预付款)
		public static final long BEFORE_ENTIY_OIL_FEE=1078L;
		//虚拟油卡(自有车支付预付款)
		public static final long BEFORE_FICTITIOUS_OIL_FEE=1079L;
		//抵扣欠款
		public static final long DEDUCTION_ARREARS=1080L;
		//预支(预支未到期到现金)
		public static final long COLLECT_IN_ADVANCE=1081L;
		//异常罚款
		public static final long EXCEPTION_FINE_FEE=1082L;
		//抵扣其他单欠款
		public static final long DEDUCTION_OTHER_ORDER_DEBT=1083L;
		//ET消费
		public static final long ETC_CONSUME_FEE = 1300L;
		//订单ETC消费
		public static final long ORDER_ETC_CONSUME_FEE = 1301L;
		//ETC消费可用金额
		public static final long ETC_AVAILABLE_FEE = 1327L;
		//ETC消费欠款(应付逾期)
		public static final long ETC_ARREARS_FEE = 1328L;
		//ETC消费欠款(应收逾期)
		public static final long ETC_ARREARS_CO_FEE = 1329L;

		//ETC消费欠款(已付逾期)
		public static final long ETC_ARREARS_FEEED = 5001L;
		//ETC消费欠款(已收逾期)
		public static final long ETC_ARREARS_CO_FEEED = 5000L;


		//预支未到期来(ETC消费导致预支未到期)
		public static final long ADVANCE_BEFORE_ETC_CONSUME_FEE = 1302L;
		//订单现金
		public static final long ORDER_CASH_CONSUME_FEE = 1326L;
		//欠款(消费导致欠款)
		public static final long ARREARS_ETC_CONSUME_FEE = 1303L;
		//未到期抵扣ETC消费(ETC消费导致其它单未到期来抵扣)
		public static final long BEFORE_DEDUCTIBLE_ETC_CONSUME_FEE = 1304L;

		//消费油
		public static final long CONSUME_OIL=1305L;
		//自由资金消费油(充值账户)
		public static final long RECHARGE_CONSUME_OIL=1306L;
		//消费油返现(只记录流水，不对账户金额操作)
		public static final long CONSUME_OIL_CASHBACK=1307L;

		//ETC结余
		public static final long ETC_SURPLUS=1308L;
		//ETC结余转未到期
		public static final long ETC_SURPLUS_TRUN_NOTDUE=1309L;
		//ETC结余转现金
		public static final long ETC_SURPLUS_TRUN_CASH=1310L;
		//ETC结余转现手续费
		public static final long ETC_SURPLUS_TRUN_CASH_SERVICE=3515L;
		//油卡结余转实体油卡
		public static final long OIL_TURN_ENTITY_CARD_OUT=3516L;

		//油卡结余
		public static final long OIL_SURPLUS=1311L;
		//油卡结余转未到期
		public static final long OIL_SURPLUS_TRUN_NOTDUE=1312L;
		//油卡结余转现金
		public static final long OIL_SURPLUS_TRUN_CASH=1313L;
		//油卡结余抵扣司机借支油
		public static final long OIL_SURPLUS_DEDUCTIBLE_LOAN=1314L;
		//油结余转现手续费
		public static final long OIL_SURPLUS_TRUN_CASH_SERVICE=3514L;

		//操作招商车费用 行程补偿(尾款扣除)
		public static final long ZHAOSHANG_TRIPCOMPENSATE=1315L;

		//尾款到期转可用(未到期扣减)
		public static final long NOTDUE_TRUN_AVAILABLE_REDUCE=1316L;
		//尾款到期转可用(可用增加)
		public static final long NOTDUE_TRUN_AVAILABLE_ADD=1317L;

		//虚拟油到期转可用(未到期扣减)
		public static final long OIL_TRUN_AVAILABLE_REDUCE=1336L;
		//虚拟油到期转可用(可用增加)
		public static final long OIL_TRUN_AVAILABLE_ADD=1337L;
		//维修基金到期转可用(未到期扣减)
		public static final long REPAIR_TRUN_AVAILABLE_REDUCE=1338L;
		//维修基金期转可用(可用增加)
		public static final long REPAIR_TRUN_AVAILABLE_ADD=1339L;

		//收入预付油卡油费(司机油卡金额)
		public static final long INCOME_CONSUME_OIL_SUB=1318L;
		//收入预付油卡油费手续费
		public static final long INCOME_CONSUME_OIL_FEE=1319L;
		//收入购买油卡油费(司机可用金额)
		public static final long INCOME_BUY_OIL_SUB=1320L;
		//收入购买油卡油费手续费(司机可用金额)
		public static final long INCOME_BUY_OIL_FEE=1321L;
		//收入充值账户油费(司机充值账户金额)
		public static final long INCOME_RECHARGE_OIL_SUB=1322L;
		//收入充值账户油费手续费(司机充值账户金额)
		public static final long INCOME_RECHARGE_OIL_FEE=1323L;
		//自由资金消费油优惠返现
		public static final long RECHARGE_OIL_DISCOUNT=1324L;
		//购买油卡优惠返现
		public static final long BUY_OIL_DISCOUNT=1325L;

		//补回补贴差额
		public static final long DRIVER_SUBSIDY=1400L;
		//车辆固定成本
		public static final long CAR_FIXED_FEE=1401L;
		//司机固定成本
		public static final long DRIVER_FIXED_FEE=1402L;
		//车管借支反写固定成本
		public static final long LAON_FIXED_FEE=1403L;
		//车管报销反写固定成本
		public static final long EXPENSE_FIXED_FEE=1404L;
		//挂车固定成本反写
		public static final long TRAILER_FIXED_FEE=1405L;
		//实体油卡核销
		public static final long OIL_ENTITY_VERIFICATION=1410L;
		//修改订单现金（改小）
		public static final long CASH_UPDATE_ORDER_MINIFY=1411L;
		//修改订单现金（改大)
		public static final long CASH_UPDATE_ORDER_LARGEN=1412L;
		//修改订单欠款
		public static final long CASH_UPDATE_ORDER_DEPT=1413L;
		//修改订单实体油卡（改小）
		public static final long CASH_UPDATE_ORDER_ENTITY_OIL_MINIFY=1415L;
		//修改订单实体油卡（改大）
		public static final long CASH_UPDATE_ORDER_ENTITY_OIL_LARGEN=1414L;
		//修改订单虚拟油卡（改小)
		public static final long CASH_UPDATE_ORDER_VIRTUAL_OIL_MINIFY=1417L;
		//修改订单虚拟油卡（改大)
		public static final long CASH_UPDATE_ORDER_VIRTUAL_OIL_LARGEN=1416L;
		//修改订单ETC（改小)
		public static final long CASH_UPDATE_ORDER_ETC_MINIFY=1419L;
		//修改订单ETC（改大)
		public static final long CASH_UPDATE_ORDER_ETC_LARGEN=1418L;
		//产生虚拟油欠费
		public static final long UPDATE_ORDER_VIRTUAL_OIL_ARREARS=1433L;
		//产生ETC欠费
		public static final long UPDATE_ORDER_ETC_ARREARS=1434L;
		//产生现金欠费
		public static final long UPDATE_ORDER_CASH_ARREARS=1432L;
		//产生路桥费(改小)
		public static final long UPDATE_ORDER_PONTAGE_MINIFY=1435L;
		//产生路桥费(改大)
		public static final long UPDATE_ORDER_PONTAGE_LARGEN=1436L;

		//油和ETC转现(功能化)
		//油转现(油支出)
		public static final long OIL_TURN_CASH_OIL = 3500L;
		//油转现(现金收入)
		public static final long OIL_TURN_CASH_CASH = 3501L;
		//撤回款(油转现)
		public static final long OIL_TURN_CASH_SERVICE = 3502L;
		//油转实体油卡(油支出)
		public static final long OIL_TURN_ENTITY_OIL = 3503L;
		//油转实体油卡(实体油卡收入)
		public static final long OIL_TURN_ENTITY_CARD = 3504L;
		//油转实体油卡(手续费)
		public static final long OIL_TURN_ENTITY_SERVICE = 3505L;
		//ETC转现(ETC支出)
		public static final long ETC_TURN_CASH_ETC = 3506L;
		//ETC转现(现金收入)
		public static final long ETC_TURN_CASH_CASH = 3507L;
		//ETC转现(手续费)
		public static final long ETC_TURN_CASH_SERVICE = 3508L;
		//油转现金抵扣借支油
		public static final long TURN_CASH_DEDUCTIBLE_LOAN_OIL = 3509L;
		//油转实体油抵扣借支油
		public static final long TURN_ENTITY_DEDUCTIBLE_LOAN_OIL = 3510L;
		//油转现抵扣欠款
		public static final long OIL_TURN_CASH_DEDUCTIBLE_MARGIN = 3511L;
		//油转实体油卡抵扣欠款
		public static final long OIL_TURN_ENTITY_DEDUCTIBLE_MARGIN = 3512L;
		//ETC转现抵扣欠款
		public static final long ETC_TURN_CASH_DEDUCTIBLE_MARGIN = 3513L;

		//油卡抵押金
		public static final long PLEDEG_OIL_CARD_TYPE1=4468L;
		//油卡押金退还
		public static final long PLEDEG_OIL_CARD_TYPE2=4469L;

		//强制平账
		//强制平账现金收入
		public static final long ZHANG_PING_BALANCE_IN = 3600L;
		//强制平账现金支出
		public static final long ZHANG_PING_BALANCE_OUT = 3601L;
		//强制平账油收入
		public static final long ZHANG_PING_OIL_IN = 3602L;
		//强制平账油支出
		public static final long ZHANG_PING_OIL_OUT = 3603L;
		//强制平账ETC收入
		public static final long ZHANG_PING_ETC_IN = 3604L;
		//强制平账ETC支出
		public static final long ZHANG_PING_ETC_OUT = 3605L;
		//强制平账未到期收入
		public static final long ZHANG_PING_MARGIN_IN = 3606L;
		//强制平账未到期支出
		public static final long ZHANG_PING_MARGIN_OUT = 3607L;
		//强制平账未到期收入
		public static final long SELECT_ZHANG_PING_MARGIN_IN = 3608L;
		//强制平账未到期支出
		public static final long SELECT_ZHANG_PING_MARGIN_OUT = 3609L;
		//强制平账油卡抵押金收入
		public static final long OIL_CARD_PLEDGE_IN = 3610L;
		//强制平账油卡抵押金支出
		public static final long OIL_CARD_PLEDGE_OUT = 3611L;
		//强制平账维修基金收入
		public static final long REPAIR_FUND_IN = 3612L;
		//强制平账维修基金支出
		public static final long REPAIR_FUND_OUT = 3613L;
		//强制平账充值油收入
		public static final long RECHARGE_OIL_IN = 3614L;
		//强制平账充值油支出
		public static final long RECHARGE_OIL_OUT = 3615L;
		//油账户分配(强制平账)
		public static final long ZHANG_PING_ACCOUNT_OIL_ALLOT = 3616L;

		//ETC自动充值消费
		public static final long ETC_PAY_QIAN_TONG_OUT = 1450L;
		//ETC自动充值产生欠款
		public static final long ETC_PAY_QIAN_TONG_DEBT = 1451L;

	/*	//可用金额支付维修单
		public static final long PAY_REPAIR_BALANCE = 1480L;
		//未到期金额支付维修单
		public static final long PAY_REPAIR_MARGIN = 1481L;
		//收入账户可用金额维修单
		public static final long INCOME_REPAIR_BALANCE = 1482L;
		//收入账户未到期金额维修单
		public static final long INCOME_REPAIR_MARGIN = 1483L;*/

		//报销入账-司机
		public static final long EXPENSE_FEE_IN= 1452L;
		//提现报销-司机
		public static final long EXPENSE_FEE_OUT= 1453L;
		//报销入账-车管
		public static final long EXPENSE_VEHICLE_FEE_IN= 1454L;
		//提现报销-车管
		public static final long EXPENSE_VEHICLE_FEE_OUT= 1455L;


		//维修基金收入(工资发放)
		public static final long REPAIR_FUND_SALARY= 1500L;
		//支付维修保养费(维修基金)
		public static final long REPAIR_FUND_PAY_REPAIR= 1501L;
		//维修基金提现
		public static final long REPAIR_FUND_WITHDRAWALS= 1502L;
		//未到期(服务商维修费收入)
		public static final long REPAIR_FEE_MARGIN= 1503L;
		//手续费(服务商维修费收入)
		//public static final long REPAIR_FEE_MARGIN_SERVICE= 1504L;
		//支付维修保养费(可用)
		public static final long BALANCE_PAY_REPAIR= 1505L;
		//支付维修保养费(返现)
		public static final long BALANCE_PAY_REPAIR_BACK= 1506L;
		//维修基金支付维修保养费(返现)
		public static final long REPAIR_FUND_PAY_REPAIR_BACK= 1507L;

		//撤单现金
		public static final long CANCEL_ORDER_CASH = 1508L;
		//撤单虚拟油
		public static final long CANCEL_ORDER_VIRTUAL_OIL = 1509L;
		//撤单实体油
		public static final long CANCEL_ORDER_ENTITY_OIL = 1510L;
		//撤单etc
		public static final long CANCEL_ORDER_ETC = 1511L;
		//现金撤单欠款
		public static final long CANCEL_ORDER_DEBT = 1512L;

		//主驾驶补贴费撤单 自有车
		public static final long CANCEL_ORDER_MASTER_SUBSIDY = 1513L;
		//虚拟油卡撤单 自有车
		public static final long CANCEL_ORDER_FICTITIOUS_OIL = 1514L;
		//实体油卡撤单 自有车
		public static final long CANCEL_ORDER_OWN_ENTITY_OIL = 1515L;
		//路桥费撤单 自有车
		public static final long CANCEL_ORDER_BRIDGE = 1516L;
		//主驾驶补贴欠款(撤单) 自有车
		public static final long CANCEL_ORDER_OWN_DEBT = 1517L;
		//副驾驶补贴费撤单 自有车
		public static final long CANCEL_ORDER_SLAVE_SUBSIDY = 1518L;
		//油卡实体金支出（预付款）
		public static final long CONSUME_BEFORE_Entity_SUB=1519L;
		//实体油卡支出(自有车支付预付款)
		public static final long CONSUME_BEFORE_ENTIY_OIL_FEE=1520L;

		//车队油账户分配(预付款)
		public static final long ACCOUNT_OIL_ALLOT=1521L;

		//时效罚款(支付尾款)
		public static final long PRESCRIPTION_FINE=1522L;

		//车队油账户分配(撤单)
		public static final long ACCOUNT_OIL_ALLOT_CANCEL_ORDER=1523L;

		//副驾驶补贴欠款(撤单)
		public static final long CANCEL_ORDER_SLAVE_SUBSIDY_DEBT = 1524L;

		//虚拟油欠款(撤单)
		public static final long CANCEL_ORDER_OIL_DEBT = 1525L;

		//ETC欠款(撤单)
		public static final long CANCEL_ORDER_ETC_DEBT = 1526L;

		//虚拟油卡欠款(撤单)
		public static final long CANCEL_ORDER_OIL_CARD_DEBT = 1527L;

		//收入维修费(可用支付维修费)
		public static final long INCOME_BALANCE_PAY_REPAIR = 1528L;

		//支付维修费（公司付）
		public static final long COMPANY_PAY_REPAIR = 1529L;

		//支付预付款
		//应收逾期（支付预付款）
		public static final long BEFORE_RECEIVABLE_OVERDUE_SUB = 1530L;
		//已收逾期（支付预付款）
		public static final long BEFORE_RECEIVABLE_OVERDUE_SUBED = 5004L;
		//应付逾期（支付预付款）
		public static final long BEFORE_PAYABLE_OVERDUE_SUB = 1531L;
		//已付逾期（支付预付款）
		public static final long BEFORE_PAYABLE_OVERDUE_SUBED = 5005L;

		//应收逾期（异常补偿）
		public static final long EXCEPTION_IN_RECEIVABLE_OVERDUE_SUB = 1532L;
		//已收逾期（异常补偿）
		public static final long EXCEPTION_IN_RECEIVABLE_OVERDUE_SUBED = 50012L;
		//应付逾期（异常补偿）
		public static final long EXCEPTION_IN_PAYABLE_OVERDUE_SUB = 1533L;
		//已付逾期（异常补偿）
		public static final long EXCEPTION_IN_PAYABLE_OVERDUE_SUBED = 50013L;

		//未到期抵扣异常(异常扣减)
		public static final long MARGINBALANCE_EXCEPTION_OUT_SUB = 1534L;
		//应收逾期（异常扣减）
		public static final long EXCEPTION_OUT_RECEIVABLE_OVERDUE_SUB = 1535L;
		//已收逾期（异常扣减）
		public static final long EXCEPTION_OUT_RECEIVABLE_OVERDUE_SUBED = 50014L;
		//应付逾期（异常扣减）
		public static final long EXCEPTION_OUT_PAYABLE_OVERDUE_SUB = 1536L;
		//已付逾期（异常扣减）
		public static final long EXCEPTION_OUT_PAYABLE_OVERDUE_SUBED = 50015L;

		//外调车撤单
		//应收逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_RECEIVABLE_OUT = 1537L;
		//已收逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_RECEIVABLE_OUTED = 50016L;
		//应付逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_PAYABLE_OUT = 1538L;
		//已付逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_PAYABLE_OUTED = 50017L;
		//应收逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_RECEIVABLE_IN = 1539L;
		//应付逾期(现金撤单)
		public static final long CANCEL_ORDER_CASH_PAYABLE_IN = 1540L;
		//未到期抵扣现金(现金撤单)
		public static final long CANCEL_ORDER_CASH_DEDUCTION = 1541L;
		//应收逾期(虚拟油撤单)
		public static final long CANCEL_ORDER_VIRTUAL_OIL_RECEIVABLE_IN = 1542L;
		//已收逾期(虚拟油撤单)
		public static final long CANCEL_ORDER_VIRTUAL_OIL_RECEIVABLE_INED = 50018L;
		//应付逾期(虚拟油撤单)
		public static final long CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN = 1543L;
		//已付逾期(虚拟油撤单)
		public static final long CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_INED = 50019L;
		//未到期抵扣油(虚拟油撤单)
		public static final long CANCEL_ORDER_VIRTUAL_OIL_DEDUCTION = 1544L;
		//应收逾期(ETC撤单)
		public static final long CANCEL_ORDER_ETC_RECEIVABLE_IN = 1545L;
		//已收逾期(ETC撤单)
		public static final long CANCEL_ORDER_ETC_RECEIVABLE_INED = 50020L;
		//应付逾期(ETC撤单)
		public static final long CANCEL_ORDER_ETC_PAYABLE_IN = 1546L;
		//已付逾期(ETC撤单)
		public static final long CANCEL_ORDER_ETC_PAYABLE_INED = 50021L;
		//未到期抵扣ETC(ETC撤单)
		public static final long CANCEL_ORDER_ETC_DEDUCTION = 1547L;

		//自有车撤单
		//应收逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_OUT = 1548L;
		//已收逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_OUTED = 50022L;
		//应付逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_OUT = 1549L;
		//已付逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_OUTED = 50023L;
		//应收逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_IN = 1550L;
		//应付逾期(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN = 1551L;
		//未到期抵扣补贴(主驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_MASTERCASH_DEDUCTION = 1552L;
		//应收逾期(虚拟油卡撤单)
		public static final long CANCEL_ORDER_OWN_VIRTUAL_OIL_RECEIVABLE_IN = 1553L;
		//应付逾期(虚拟油卡撤单)
		public static final long CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN = 1554L;

		//已收逾期(虚拟油卡撤单)
		public static final long CANCEL_ORDER_OWN_VIRTUAL_OIL_RECEIVABLE_INED = 50026L;
		//已付逾期(虚拟油卡撤单)
		public static final long CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_INED = 50027L;


		//未到期抵扣油(虚拟油卡撤单)
		public static final long CANCEL_ORDER_OWN_VIRTUAL_OIL_DEDUCTION = 1555L;
		//应收逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_OUT = 1556L;
		//应付逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUT = 1557L;

		//已收逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_OUTED = 50024L;
		//已付逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUTED = 50025L;

		//应收逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_IN = 1558L;
		//应付逾期(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN = 1559L;
		//未到期抵扣补贴(副驾驶补贴撤单)
		public static final long CANCEL_ORDER_OWN_SALVECASH_DEDUCTION = 1560L;

		//油和ETC转现
		//应收逾期(油转现)
		public static final long OIL_TURN_CASH_RECEIVABLE_IN = 1561L;
		//应付逾期(油转现)
		public static final long OIL_TURN_CASH_PAYABLE_IN = 1562L;
		//应收逾期(ETC转现)
		public static final long ETC_TURN_CASH_RECEIVABLE_IN = 1563L;
		//应付逾期(ETC转现)
		public static final long ETC_TURN_CASH_PAYABLE_IN = 1564L;



		//已收逾期(油转现)
		public static final long OIL_TURN_CASH_RECEIVABLE_INED = 50028L;
		//已付逾期(油转现)
		public static final long OIL_TURN_CASH_PAYABLE_INED = 50029L;
		//已收逾期(ETC转现)
		public static final long ETC_TURN_CASH_RECEIVABLE_INED = 50030L;
		//已付逾期(ETC转现)
		public static final long ETC_TURN_CASH_PAYABLE_INED = 50031L;

		//油卡抵押释放
		//应收逾期(油卡抵押)
		public static final long OIL_CARD_PLEDGE_RECEIVABLE_IN = 1565L;
		//应付逾期(油卡抵押)
		public static final long OIL_CARD_PLEDGE_PAYABLE_IN = 1566L;
		//应收逾期(油卡释放)
		public static final long OIL_CARD_RELEASE_RECEIVABLE_OUT = 1567L;
		//应付逾期(油卡释放)
		public static final long OIL_CARD_RELEASE_PAYABLE_OUT = 1568L;

		//扫码加油
		//司机可提现余额消费油
		public static final long BALANCE_CONSUME_OIL_SUB = 1569L;
		//服务商收入可提现消费油费
		public static final long INCOME_BALANCE_CONSUME_OIL_SUB = 1570L;

		//维修保养
		//司机支付维修保养
		public static final long BALANCE_PAY_FOR_REPAIR_SUB = 1571L;
		//服务商收入维修保养
		public static final long INCOME_BALANCE_PAY_FOR_REPAIR_SUB = 1572L;

		//确认工资核销借支
		//应收逾期(核销借支)
		public static final long LOAN_VERIFICATION_RECEIVABLE_OUT = 1573L;
		//应付逾期(核销借支)
		public static final long LOAN_VERIFICATION_PAYABLE_OUT = 1574L;

		//修改订单模块
		//应收逾期(修改订单_现金减少)
		public static final long UPDATE_ORDER_RECEIVABLE_CASH_LOW = 1575L;
		//应付逾期(修改订单_现金减少)
		public static final long UPDATE_ORDER_PAYABLE_CASH_LOW = 1576L;
		//应收逾期(修改订单_现金增加)
		public static final long UPDATE_ORDER_RECEIVABLE_CASH_UPP = 1577L;
		//应付逾期(修改订单_现金增加)
		public static final long UPDATE_ORDER_PAYABLE_CASH_UPP = 1578L;
		//虚拟油(修改订单_虚拟油减少)
		public static final long UPDATE_ORDER_VIRTUALOIL_LOW = 1579L;
		//虚拟油(修改订单_虚拟油增加)
		public static final long UPDATE_ORDER_VIRTUALOIL_UPP = 1580L;
		//应收逾期(修改订单_虚拟油减少)
		public static final long UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW = 1581L;
		//应付逾期(修改订单_虚拟油减少)
		public static final long UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW = 1582L;
		//实体油(修改订单_实体油减少)
		public static final long UPDATE_ORDER_ENTITYOIL_LOW = 1583L;
		//实体油(修改订单_实体油增加)
		public static final long UPDATE_ORDER_ENTITYOIL_UPP = 1584L;
		//ETC(修改订单_ETC减少)
		public static final long UPDATE_ORDER_ETC_LOW = 1585L;
		//ETC(修改订单_ETC增加)
		public static final long UPDATE_ORDER_ETC_UPP = 1586L;
		//应收逾期(修改订单_ETC减少)
		public static final long UPDATE_ORDER_RECEIVABLE_ETC_LOW = 1587L;
		//应付逾期(修改订单_ETC减少)
		public static final long UPDATE_ORDER_PAYABLE_ETC_LOW = 1588L;
		//应收逾期(修改订单_主驾驶补贴减少)
		public static final long UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_LOW = 1589L;
		//应付逾期(修改订单_主驾驶补贴减少)
		public static final long UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW = 1590L;
		//应收逾期(修改订单_主驾驶补贴增加)
		public static final long UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP = 1591L;
		//应付逾期(修改订单_主驾驶补贴增加)
		public static final long UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP = 1592L;
		//虚拟油卡(修改订单_虚拟油卡减少)
		public static final long UPDATE_ORDER_VIRTUALOIL_CARD_LOW = 1593L;
		//虚拟油卡(修改订单_虚拟油卡增加)
		public static final long UPDATE_ORDER_VIRTUALOIL_CARD_UPP = 1594L;
		//应收逾期(修改订单_虚拟油卡减少)
		public static final long UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_CARD_LOW = 1595L;
		//应付逾期(修改订单_虚拟油卡减少)
		public static final long UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW = 1596L;
		//实体油卡(修改订单_实体油卡减少)
		public static final long UPDATE_ORDER_ENTITYOIL_CARD_LOW = 1597L;
		//实体油卡(修改订单_实体油卡增加)
		public static final long UPDATE_ORDER_ENTITYOIL_CARD_UPP = 1598L;
		//路桥费(修改订单_路桥费减少)
		public static final long UPDATE_ORDER_LUQIAO_LOW = 1599L;
		//路桥费(修改订单_路桥费增加)
		public static final long UPDATE_ORDER_LUQIAO_UPP = 1600L;
		//应收逾期(修改订单_副驾驶补贴减少)
		public static final long UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_LOW = 1601L;
		//应付逾期(修改订单_副驾驶补贴减少)
		public static final long UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW = 1602L;
		//应收逾期(修改订单_副驾驶补贴增加)
		public static final long UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP = 1603L;
		//应付逾期(修改订单_副驾驶补贴增加)
		public static final long UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP = 1604L;
		//平台服务费核销
		public static final long VERIFICATION_SERVICE_CHARGE = 1605L;
		//平台服务费核销失败
		public static final long VERIFICATION_SERVICE_CHARGE_FAIL = 1606L;
		//车队油账户分配(修改订单,虚拟油减少)
		public static final long ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW = 1607L;
		//车队油账户分配(修改订单,虚拟油增加)
		public static final long ACCOUNT_OIL_ALLOT_UPDATE_ORDER_UPP = 1608L;
		//实体油(修改订单_实体油支出)
		public static final long UPDATE_ORDER_ENTITYOIL_UPP_OUT = 1609L;
		//实体油卡(修改订单_实体油卡支出)
		public static final long UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT = 1610L;
		//开票总运费(预付款) --车队
		public static final long BEFORE_HA_TOTAL_FEE = 1611L;
		//油卡抵押(未到期)
		public static final long OIL_CARD_PLEDGE_MARGIN = 1612L;
		//油卡释放(未到期)
		public static final long OIL_CARD_RELEASE_MARGIN = 1613L;
		//应收逾期(油卡释放)
		public static final long OIL_CARD_RELEASE_RECEIVABLE_IN = 1614L;
		//应付逾期(油卡释放)
		public static final long OIL_CARD_RELEASE_PAYABLE_IN = 1615L;

		//费用上报 油费_油卡
		public static final long ORDER_COST_REPORT_OIL_CARD = 1616L;
		//费用上报 油费_现金
		public static final long ORDER_COST_REPORT_OIL_CASH = 1617L;
		//费用上报 路桥费_ETC卡号
		public static final long ORDER_COST_REPORT_LUQIAO_CARD = 1618L;
		//费用上报 路桥费_现金
		public static final long ORDER_COST_REPORT_LUQIAO_CASH = 1619L;
		//充值油费
		public static final long RECHARGE_ACCOUNT_OIL_FEE = 1620L;
		//车队油账户分配(充值油费)
		public static final long RECHARGE_ACCOUNT_OIL_ALLOT=1621L;
		//油账户清零(充值油)
		public static final long CLEAR_RECHARGE_ACCOUNT_OIL_FEE = 1622L;
		//车队油账户分配(油账户清零)
		public static final long CLEAR_ACCOUNT_OIL_ALLOT=1623L;
		//油账户清零(订单油)
		public static final long CLEAR_ORDER_ACCOUNT_OIL_FEE = 1624L;
		//消费充值油(扫码加油)
		public static final long CONSUME_RECHARGE_OIL_SUB=1625L;
		//应收逾期（票据服务费）
		public static final long BILL_SERVICE_RECEIVABLE_OVERDUE_SUB = 1626L;
		//应付逾期（票据服务费）
		public static final long BILL_SERVICE_PAYABLE_OVERDUE_SUB = 1627L;
		//票据服务费同心智行平台提成
		public static final long BILL_SERVICE_PLATFORM_ROYALTY = 1628L;
		//开票平台支付票据服务费同心智行提成
		public static final long PAY_BILL_SERVICE_PLATFORM_ROYALTY = 1629L;
		//应收逾期（油品公司油卡充值）
		public static final long RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB = 1630L;
		//应付逾期（油品公司油卡充值）
		public static final long RECHARGE_ENTITY_OIL_PAYABLE_OVERDUE_SUB = 1631L;
		//应收逾期（到付款）
		public static final long ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB = 1632L;
		//应付逾期（到付款）
		public static final long ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB = 1633L;
		//异常罚款（到付款）
		public static final long ARRIVE_CHARGE_DEDUCTION_EXCEPTION = 1634L;
		//时效罚款（到付款）
		public static final long ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION = 1635L;
		//应收逾期(到付款撤单)
		public static final long CANCEL_ORDER_ARRIVE_RECEIVABLE_OUT = 1636L;
		//应付逾期(到付款撤单)
		public static final long CANCEL_ORDER_ARRIVE_PAYABLE_OUT = 1637L;
		//应收逾期(到付款撤单)
		public static final long CANCEL_ORDER_ARRIVE_RECEIVABLE_IN = 1638L;
		//应付逾期(到付款撤单)
		public static final long CANCEL_ORDER_ARRIVE_PAYABLE_IN = 1639L;

		//应收逾期（56K票据服务费）
		public static final long BILL_56K_RECEIVABLE_OVERDUE_SUB = 1640L;
		//应付逾期（56K票据服务费）
		public static final long BILL_56K_PAYABLE_OVERDUE_SUB = 1641L;

		//应收逾期(修改订单_到付款减少)
		public static final long UPDATE_ORDER_RECEIVABLE_ARRIVE_LOW = 1642L;
		//应付逾期(修改订单_到付款减少)
		public static final long UPDATE_ORDER_PAYABLE_ARRIVE_LOW = 1643L;
		//应收逾期(修改订单_到付款增加)
		public static final long UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP = 1644L;
		//应付逾期(修改订单_到付款增加)
		public static final long UPDATE_ORDER_PAYABLE_ARRIVE_UPP = 1645L;

		//未到期抵扣招商车/挂靠车费用
		public static final long ACCOUNT_STATEMENT_DEDUCTION = 1646L;
		//应收逾期(未到期发放招商车/挂靠车结算金额)
		public static final long ACCOUNT_STATEMENT_MARGIN_RECEIVABLE = 1647L;
		//应付逾期(未到期发放招商车/挂靠车结算金额)
		public static final long ACCOUNT_STATEMENT_MARGIN_PAYABLE = 1648L;
		//应收逾期(招商车/挂靠车车辆费用)
		public static final long ACCOUNT_STATEMENT_CAR_RECEIVABLE = 1649L;
		//应付逾期(招商车/挂靠车车辆费用)
		public static final long ACCOUNT_STATEMENT_CAR_PAYABLE = 1650L;
		//未到期(未到期发放招商车/挂靠车结算金额)
		public static final long ACCOUNT_STATEMENT_MARGIN = 1659L;
		//应收逾期（油品公司油卡充值撤销）
		public static final long RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB_CANCEL = 1660L;
		//应付逾期（油品公司油卡充值撤销）
		public static final long RECHARGE_ENTITY_OIL_PAYABLE_OVERDUE_SUB_CANCEL = 1661L;
		//开票总运费(预付款) --司机
		public static final long BEFORE_HA_TOTAL_FEE_RECEIVABLE = 1662L;
		//开票总运费(预付款) --司机
		public static final long BEFORE_HA_TOTAL_FEE_PAYABLE = 1663L;

		//应收逾期(订单尾款转可提现)
		public static final long FINAL_PAYFOR_RECEIVABLE_IN = 1651L;
		//应付逾期(订单尾款转可提现)
		public static final long FINAL_PAYFOR_PAYABLE_IN = 1652L;

		//应收逾期（服务商油卡圈退）
		public static final long SERVICE_RETREAT_RECEIVABLE_OVERDUE_SUB = 1680L;
		//应付逾期（服务商油卡圈退）
		public static final long SERVICE_RETREAT_PAYABLE_OVERDUE_SUB = 1681L;

		//已收逾期(订单尾款转可提现)
		public static final long FINAL_PAYFOR_RECEIVED_IN = 50032L;
		//已付逾期(订单尾款转可提现)
		public static final long FINAL_PAYFOR_PAID_IN = 50033L;

		//应收逾期(订单油到期转可提现)
		public static final long Oil_PAYFOR_RECEIVABLE_IN = 1653L;
		//应付逾期(订单油到期转可提现)
		public static final long Oil_PAYFOR_PAYABLE_IN = 1654L;

		//应收逾期(订单油到期转可提现)
		public static final long REPAIR_PAYFOR_RECEIVABLE_IN = 1657L;
		//应付逾期(订单油到期转可提现)
		public static final long REPAIR_PAYFOR_PAYABLE_IN = 1658L;

		//应收逾期(预支)
		public static final long ADVANCE_PAY_RECEIVABLE_IN = 1655L;
		//应付逾期(预支)
		public static final long ADVANCE_PAY_PAYABLE_IN = 1656L;

		//油预存资金锁定(预付款)
		public static final long OIL_FEE_PRESTORE_LOCK_1664 = 1664L;
		//油预存资金锁定(修改订单_油增加)
		public static final long OIL_FEE_PRESTORE_LOCK_1665 = 1665L;
		//油预存资金释放(修改订单_油减少)
		public static final long OIL_FEE_PRESTORE_RELEASE_1666 = 1666L;
		//油预存资金释放(撤单)
		public static final long OIL_FEE_PRESTORE_RELEASE_1667 = 1667L;
		//油预存资金释放(油转现)
		public static final long OIL_FEE_PRESTORE_RELEASE_1668 = 1668L;
		//油预存资金释放(油转实体油卡)
		public static final long OIL_FEE_PRESTORE_RELEASE_1669 = 1669L;
		//油预存资金释放(油转ETC)
		public static final long OIL_FEE_PRESTORE_RELEASE_1670 = 1670L;
		//油预存资金释放(平账)
		public static final long OIL_FEE_PRESTORE_RELEASE_1671 = 1671L;
		//油预存资金锁定(油账户充值)
		public static final long OIL_FEE_PRESTORE_LOCK_1672 = 1672L;
		//油预存资金释放(油账户清零)
		public static final long OIL_FEE_PRESTORE_RELEASE_1673 = 1673L;
		//应收逾期(北斗缴费)
		public static final long BEIDOU_PAYMENT_RECEIVABLE_IN = 1674L;
		//应付逾期(北斗缴费)
		public static final long BEIDOU_PAYMENT_PAYABLE_IN = 1675L;
		//应收逾期(北斗缴费撤销)
		public static final long BEIDOU_PAYMENT_CANCEL_RECEIVABLE_IN = 1676L;
		//应付逾期(北斗缴费撤销)
		public static final long BEIDOU_PAYMENT_CANCEL_PAYABLE_IN = 1677L;
		//油账户分配（油转现）
		public static final long ACCOUNT_OIL_ALLOT_OIL_TRUN_CASH = 1678L;
		//油账户分配（强制平账oil_balance）
		public static final long ACCOUNT_OIL_ALLOT_ZHANG_PING_OIL_BALANCE = 1679L;
		//强制平账recharge_oil
		public static final long ZHANG_PING_RECHARGE_OIL_OUT = 1682L;
		//服务商收入油费，不操作账户
		public static final long INCOME_CONSUME_OIL_SUB_1683 = 1683L;
		//虚拟油(修改订单_虚拟油减少)
		public static final long UPDATE_ORDER_VIRTUALOIL_LOW_1684 = 1684L;
		//虚拟油(修改订单_虚拟油增加)
		public static final long UPDATE_ORDER_VIRTUALOIL_UPP_1685 = 1685L;
		//消费油，释放预存油
		public static final long OIL_FEE_PRESTORE_RELEASE_1686 = 1686L;
		//加油返利
		public static final long RECHARGE_ACCOUNT_OIL_FEE_1687 = 1687L;
		//应收逾期(预付款_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1688 = 1688L;
		//应收逾期(修改订单_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1689 = 1689L;
		//应收逾期(撤单_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1690 = 1690L;
		//应收逾期(油转现_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1691 = 1691L;
		//应收逾期(ETC转现_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1692 = 1692L;
		//应收逾期(异常补偿_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1693 = 1693L;
		//应收逾期(到付款_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1694 = 1694L;
		//应收逾期(订单尾款转可提现_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1695 = 1695L;
		//应收逾期(修改订单到付款_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1696 = 1696L;
		//应收逾期(油卡抵押释放_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1697 = 1697L;
		//应收逾期(未到期结算金额_开票服务费)
		public static final long RECEIVABLE_IN_SERVICE_FEE_1698 = 1698L;

		//应付逾期(预付款_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4000 = 4000L;
		//应付逾期(修改订单_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4001 = 4001L;
		//应付逾期(撤单_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4002 = 4002L;
		//应付逾期(油转现_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4003 = 4003L;
		//应付逾期(ETC转现_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4004 = 4004L;
		//应付逾期(异常补偿_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4005 = 4005L;
		//应付逾期(到付款_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4006 = 4006L;
		//应付逾期(订单尾款转可提现_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4007 = 4007L;
		//应付逾期(修改订单到付款_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4008 = 4008L;
		//应付逾期(油卡抵押释放_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4009 = 4009L;
		//应付逾期(未到期结算金额_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4010 = 4010L;
		//开票总运费(预付款) --车队
		public static final long BEFORE_HA_PAY_TOTAL_FEE = 4011L;
		//应付逾期(预支_开票服务费)
		public static final long PAYABLE_IN_SERVICE_FEE_4012 = 4012L;
		//应收逾期(维修保养费)
		public static final long RECEIVABLE_IN_REPAIR_FEE_4013 = 4013L;
		//应付逾期(维修保养费)
		public static final long PAYABLE_IN_REPAIR_FEE_4014 = 4014L;
		//应收逾期(订单尾款转到期回退)
		public static final long MARGIN_TURN_CASH_BACK_4015 = 4015L;
		//未到期(订单尾款转到期回退)
		public static final long MARGIN_TURN_CASH_BACK_4016 = 4016L;
		//应付逾期(订单尾款转到期回退)
		public static final long MARGIN_TURN_CASH_BACK_4017 = 4017L;
		//应付逾期(路歌最后一笔运费服务费)
		public static final long LUGE_LAST_SERVICE_FEE_4018 = 4018L;
		//附加运费
		public static final long PAYABLE_IN_ADDITIONAL_FEE_4019 = 4019L;

		//应收逾期(轮胎租赁费)
		public static final long TIRE_RENTAL_FEE_4020 = 4020L;
		//应付逾期(轮胎租赁费)
		public static final long TIRE_RENTAL_FEE_4021 = 4021L;
		//应收逾期(轮胎租赁费回退)
		public static final long TIRE_RENTAL_CASH_BACK_4022 = 4022L;
		//应付逾期(轮胎租赁费回退)
		public static final long TIRE_RENTAL_CASH_BACK_4023 = 4023L;
		//已收逾期(预支)
		public static final long ADVANCE_PAY_RECEIVED_IN = 50038L;
		//已付逾期(预支)
		public static final long ADVANCE_PAY_PAID_IN = 50039L;
		//平安虚拟账户资金转移(扣减)
		public static final long  VIRTUAL_ACCOUNT_REDUCE_CASH = 5101L;
		//平安虚拟账户资金转移(增加)
		public static final long  VIRTUAL_ACCOUNT_ADD_CASH = 5102L;

		//平安虚拟账户资金转移(收款账户扣减)
		public static final long  RECEIVABLES_ACCOUNT_REDUCE_CASH = 5104L;
		//平安虚拟账户资金转移(收款账户增加)
		public static final long  RECEIVABLES_ACCOUNT_ADD_CASH = 5103L;
		//平安虚拟账户资金转移(付款账户扣减)
		public static final long  PAYMENT_ACCOUNT_REDUCE_CASH = 5106L;
		//平安虚拟账户资金转移(付款账户增加)
		public static final long  PAYMENT_ACCOUNT_ADD_CASH = 5105L;
		//应收逾期(司机借支还款)
		public static final long  SUBJECTIDS50044 = 50044L;
		//应付逾期(司机借支还款)
		public static final long  SUBJECTIDS50045 = 50045L;

		//应收逾期(车队服务费)
		public static final long  SUBJECTIDS1801 = 1801L;
		//应付逾期(车队服务费)
		public static final long  SUBJECTIDS1800 = 1800L;

		//应付逾期(违章代缴服务费)
		public static final long  SUBJECTIDS1802 = 1802L;
		//应收逾期(违章代缴服务费)
		public static final long  SUBJECTIDS1803 = 1803L;

		//应付逾期(司机违章费用)
		public static final long  SUBJECTIDS1804 = 1804L;
		//应收逾期(司机违章费用)
		public static final long  SUBJECTIDS1805 = 1805L;


		//应付逾期(违章代缴服务费退还)
		public static final long  SUBJECTIDS1806 = 1806L;
		//应收逾期(违章代缴服务费退还)
		public static final long  SUBJECTIDS1807 = 1807L;

		//应付逾期(违章代缴费退还)
		public static final long  SUBJECTIDS1808 = 1808L;
		//应收逾期(违章代缴费退还)
		public static final long  SUBJECTIDS1809 = 1809L;


		//已付逾期(车队服务费)
		public static final long  SUBJECTIDS50065 = 50065L;
		//已收逾期(车队服务费)
		public static final long  SUBJECTIDS50064 = 50064L;
		//已收逾期(违章代缴服务费)
		public static final long  SUBJECTIDS50066 = 50066L;
		//已付逾期(违章代缴服务费)
		public static final long  SUBJECTIDS50067 = 50067L;

		//已收逾期(油卡释放)
		public static final long  SUBJECTIDS50068 = 50068L;
		//已付逾期(油卡释放)
		public static final long  SUBJECTIDS50069 = 50069L;

		//支付违章费用
		public static final long SUBJECTIDS50070 = 50070L;
		//收入违章费用
		public static final long SUBJECTIDS50071 = 50071L;

		//支付违章服务费用
		public static final long SUBJECTIDS50072 = 50072L;
		//收入违章服务费用
		public static final long SUBJECTIDS50073 = 50073L;

		//已收逾期(司机违章费用)
		public static final long  SUBJECTIDS50074 = 50074L;
		//已付逾期(司机违章费用)
		public static final long  SUBJECTIDS50075 = 50075L;

		//已收逾期(违章代缴服务费退还)
		public static final long  SUBJECTIDS50076 = 50076L;
		//已付逾期(违章代缴服务费退还)
		public static final long  SUBJECTIDS50077 = 50077L;

		//已收逾期(外系统提现手续费)
		public static final long  SUBJECTIDS50090 = 50090L;
		//已付逾期(外系统提现手续费)
		public static final long  SUBJECTIDS50091 = 50091L;

		//已收逾期(违章代缴费退还)
		public static final long  SUBJECTIDS50078 = 50078L;
		//已付逾期(违章代缴费退还)
		public static final long  SUBJECTIDS50079 = 50079L;
		//应付逾期(司机补贴)  1811
		public static final long SUBJECTIDS1811 = 1811L;
		//#应收逾期(司机补贴)  1810
		public static final long SUBJECTIDS1810 = 1810L;


		//应付逾期(修改订单司机补贴增加)  1813
		public static final long SUBJECTIDS1813 = 1813L;
		//#应收逾期(修改订单司机补贴增加)  1812
		public static final long SUBJECTIDS1812 = 1812L;


		//应付逾期(修改订单司机补贴减少)  1815
		public static final long SUBJECTIDS1815 = 1815L;
		//#应收逾期(修改订单司机补贴减少)  1814
		public static final long SUBJECTIDS1814 = 1814L;


		//外系统提现手续费(收款-收款方银行账户增加)
		public static final long SUBJECTIDS1818 = 1818L;
		//外系统提现手续费(付款-收款方银行账户扣减)
		public static final long SUBJECTIDS1819 = 1819L;

		//外系统会员交易(收款-收款方银行账户增加)
		public static final long SUBJECTIDS1900 = 1900L;
		//外系统会员交易(付款-收款方银行账户扣减)
		public static final long SUBJECTIDS1901 = 1901L;

		//应收逾期(付款管理)
		public static final long  SUBJECTIDS1816 = 1816L;
		//应付逾期(付款管理)
		public static final long  SUBJECTIDS1817 = 1817L;

		//应付逾期(ETC账单支付-共享)
		public static final long SUBJECTIDS2301=2301L;
		//应收逾期(ETC账单支付-共享)
		public static final long SUBJECTIDS2302=2302L;

		//应付逾期(ETC账单支付-私有)
		public static final long SUBJECTIDS2303=2303L;
		//应收逾期(ETC账单支付-私有)
		public static final long SUBJECTIDS2304=2304L;
		//油充值账户充值
		public static final long SUBJECTIDS1130=1130L;
		//应收逾期（油账户充值）
		public static final long SUBJECTIDS1131=1131L;
		//应付逾期（油账户充值）
		public static final long SUBJECTIDS1132=1132L;

		//已收逾期（油账户充值）
		public static final long SUBJECTIDS50131=50131L;
		//已付逾期（油账户充值）
		public static final long SUBJECTIDS50132=50132L;

		//应收逾期（油账户提现）
		public static final long SUBJECTIDS1133=1133L;
		//应付逾期（油账户提现）
		public static final long SUBJECTIDS1134=1134L;
		//油充值账户提现
		public static final long SUBJECTIDS1135=1135L;

		//还款
		public static final long SUBJECTIDS1136=1136L;

		//返利
		public static final long SUBJECTIDS1137=1137L;

		//已收逾期（油账户提现）
		public static final long SUBJECTIDS50133=50133L;
		//已付逾期（油账户提现）
		public static final long SUBJECTIDS50134=50134L;


		//已付逾期（已付待提现收款方未绑卡付款）
		public static final long SUBJECTIDS50135=50135L;
		//已收逾期（已付待提现收款方未绑卡付款）
		public static final long SUBJECTIDS50136=50136L;
		//已付逾期（已付撤回未绑卡付款）
		public static final long SUBJECTIDS50137=50137L;
		//已收逾期（已付撤回收款方未绑卡付款）
		public static final long SUBJECTIDS50138=50138L;
		//已付逾期（支付收款未绑卡付款）
		public static final long SUBJECTIDS50139=50139L;
		//已收逾期（支付收款方未绑卡付款）
		public static final long SUBJECTIDS50140=50140L;
		//已付逾期（已付待提现收款方未绑卡付款服务费）
		public static final long SUBJECTIDS50141=50141L;
		//已收逾期（已付待提现收款方未绑卡付款服务费）
		public static final long SUBJECTIDS50142=50142L;
		//已付逾期（已付撤回未绑卡付款服务费）
		public static final long SUBJECTIDS50143=50143L;
		//已收逾期（已付撤回收款方未绑卡付款服务费）
		public static final long SUBJECTIDS50144=50144L;
		//已付逾期（支付收款未绑卡付款服务费）
		public static final long SUBJECTIDS50145=50145L;
		//已收逾期（支付收款方未绑卡付款服务费）
		public static final long SUBJECTIDS50146=50146L;

		//已付逾期（提现收款未绑卡付款服务费）
		public static final long SUBJECTIDS50147=50147L;
		//已收逾期（提现收款方未绑卡付款服）
		public static final long SUBJECTIDS50148=50148L;
		//已付逾期（提现收款未绑卡付款）
		public static final long SUBJECTIDS50149=50149L;

	}


	public static class isOperLog{

		public static final int  RECHARGE=41; // 充值
		public static final int  TRANSE_ACCOUNTS=42; // 转账
		public static final int  WITH_DRAW_CASE=43; // 提现
		public static final int  REGISTER=51; // 注册
		public static final int  UPDATE_USER=52; // 更新用户资料
		public static final int  AUDITING=53;//用户审核
		public static final int  SAVE_VEHICLE=61; // 注册车辆
		public static final int  UPDATE_VEHICLE=62; // 更新车辆
		public static final int  DEL_VEHICLE=63; //取消车辆
		public static final int  VEHICLE_AUDITING=64; //车辆审核
		public static final int  SAVE_ZX=71;//保存专线
		public static final int  UPDATE_ZX=72; // 更新专线
		public static final int DEL_ZX=73; // 取消专线
		public static final int  IDENTITY_VERIFICATION=81;//身份验证
		public static final int  LOADING_CANCEL=82;//装货中取消
		public static final int  BEFOREPAY=90; // 预付
		public static final int  ENTRUST=91; // 托管入账
		public static final int  TRANSFER_TRUST=92; //托管转账
		public static final int  PAYFOR=93; //未到期转可用
		public static final int  ADVANCE_PAY=95; //预支
		public static final int  ETC_PAY=96; //ETC消费
		public static final int  WIDTHDRAWS=97; //提现
		public static final int  PAY_OIL=98; //消费油卡
		public static final int  INCOME_OIL=99; //收入油费
		public static final int  INCOME_FINAL=100; //收入尾款
		public static final int  EXCEPTION=101; //异常
		public static final int  ETC_RECHARGE=102; //ETC充值
		public static final int  OIL_RECHARGE=103; //油卡充值
		public static final int  BACK_RECHARGE=104; //回退提现款
		public static final int  ZHAOSHANG_FEE=105; //招商车
		public static final int  OIL_AND_ETC_SURPLUS=106; //ETC结余
		public static final int  OIL_SURPLUS=107;
		public static final int OA_LOAN_AVAILABLE=108;//司机借支金额审核通过
		public static final int OA_LOAN_VERIFICATION=109;//司机借支核销
		public static final int SALARY_OWN_CAR=118;//系统发放自有车金额
		public static final int UPDATE_PRICE=119;//修改订单
		public static final int REPEAL_PRICE=120;//撤销订单
		public static final int OA_LOAN_CAR_AVAILABLE=121;//车管借支金额审核通过
		public static final int  PAY_REPAIR=122; //支付维修单
		public static final int  INCOME_REPAIR=123; //收入维修费
		public static final int  FEPAIR_FUND=124; //维修基金
		public static final int  REPAY_LOAN=201; //还款期数维护

		public static final int  CARS_SOURCE=1; // 车源
		public static final int  HY_GOODS=2; //货源
		public static final int  ORDER_DATE=3; //订单
		public static final int  WALLET=4;//钱包
		public static final int  USER_DATE=5; //用户资料
		public static final int  VEHICLE_CARS=6; // 车辆
		public static final int  ZX_DATE=7;//专线
		public static final int  DOT_INFO=8;//网点
		public static final int  WEB_DATA=10;// 积分管理
		public static final int  WEB_OA=20;// OA

		//1、新增2、修改3、删除4、其他
		public static final int  SAVE=1;//新增
		public static final int  UPDATE=2;//修改
		public static final int  DELETE=3;//删除
		public static final int  OTHERS=4;//其他


		public static final int  APP_ANDROID=1;//ANDROID
		public static final int  APP_IOS=2;//IOS
		public static final int  WEB=3;//WEB

		public static final int  COST_INTEGREAL=3;//扣减积分z

		public static final int  PUB_CARS=11; // 发布车源
		public static final int  UPDATE_CARS=12; // 更新车源
		public static final int  DEL_CARS=13; // 取消车源
		public static final int  PUB_GOODS=21; // 发布货源
		public static final int  UPDATE_GOODS=22; // 更新货源
		public static final int  DEL_GOODS=23; // 取消货源
		public static final int  SAVE_ORDER=31; // 保存订单
		public static final int  MODIFY_ORDER=311; // 订单-修改
		public static final int  DEL_ORDER=33; // 删除订单
		public static final int  SURE_ORDER=34; // 确认订单
		public static final int  GOODS_ORDER=35; // 订单-装货
		public static final int  SERVICE_ORDER=36; // 订单-送达
		public static final int  SIGN_IN_ORDER=37; // 订单-签收
		public static final int  PAY_ORDER=38; // 订单-支付
		public static final int  PAY_INFO_FEES=39; // 订单-支付信息费
		public static final int  PAY_EMPTY_FEES=40; // 订单-支付信息费


		public static final int  WEB_COST_INTEGREAL=82; // 积分管理-积分兑换扣减

		/*
		 * 提现管理日志
		 */
		public static final int  LOGBI_PAY_MONEY=915; // 体现管理-提现发起转账
		public static final int  LOGBI_DATA=9;//Logbi
		public static final int  LOGBI_RECHARGE=44; // LOGBI客服充值
		public static final int  LOGBI_TRANSE_ACCOUNTS=45; // LOGBI提现

	}

	/**
	 * 订单状态枚举
	 */
	public static class OrderState{

		/**待调度*/
		@Deprecated
		public static final int START = 1;
		/**调度中*/
		public static final int DISPATCH = 2;
		/**事业部审核*/
		public static final int BUSI_VERIFY = 3;
		/**事业部审核不通过*/
		public static final int BUSI_NOT_VERIFY = 4;
		/**大区审核*/
		public static final int LEADING_VERIFY = 5;
		/**大区审核不通过*/
		public static final int LEADING_NOT_VERIFY = 6;
		/**待装货*/
		public static final int WILL_LOAD = 7;
		/**装货中*/
		public static final int LOADING = 8;
		/**运输中*/
		public static final int TRANSIT =9;
		/**已到达*/
		public static final int ARRIVE =10;
		/**已卸货*/
		@Deprecated
		public static final int UNLOAD=11;
		/**回单审核*/
		public static final int RECE_VERIFY=12;
		/**回单审核不通过*/
		public static final int RECE_NOT_VERIFY = 13;
		/**核销*/
		public static final int VERIFICATION=14;
		/**已完成*/
		public static final int FINISH=15;
		/**撤销*/
		public static final int CANCEL = 16;
	}

	/**
	 * 状态有效性 0无效 1有效
	 * @author daixb
	 *
	 */
	public static class STATE{
		public static final int STATE_NO = 0;
		public static final int STATE_YES = 1;
	}

	public static class OIL{
		//类型:1消费油流水2ETC消费流水3维修费
		public static final int TYPE_OIL = 1;
		public static final int TYPE_ETC= 2;
		public static final int TYPE_REPAIR= 3;
		// 1：消费  2：充值
		public static final int COST_TYPE_OUT = 1;
		public static final int COST_TYPE_IN = 2;
	}

	/**
	 * redis变量
	 *
	 */
	public static class REDIS{
		public static final String PREPAY_LOCK_LEVEL = "PREPAY_LOCK_LEVEL_NEW";
		public static final String WITHDRAWAL_NUM_LOCK_LEVEL = "WITHDRAWAL_NUM_LOCK_LEVEL";
	}
	/**
	 * 核销状态
	 *
	 */
	public static class FINANCE_STS{
		public static final int FINANCE_STS_0 = 0;//未核销
		public static final int FINANCE_STS_1 = 1;//已核销
		public static final int FINANCE_STS_2 = 2;//部分核销
	}

	/**
	 * 账单核销类型
	 */
	public static class OrderBillCHECK_TYPE {
		public static final int CASH = 1;// 现汇核销
		public static final int ACCEPTANCE = 2;// 承兑核销
		public static final int RUSH_OFF = 3;// 冲抵核销
		public static final int OTHER = 5;// 其它核销
		public static final int BAD_DEBT = 6;// 坏账

	}
	/**
	 * 打印配置业务编码
	 * @author mcfly
	 *
	 */
	public static class PrintConfigBizCode {
		/** 打印运单 */
		public static final int BIZ_CODE_10000 = 10000;
		/** 打印回单 */
		public static final int BIZ_CODE_10001 = 10001;
		/** 打印标签 */
		public static final int BIZ_CODE_10002 = 10002;
		/***成本支付***/
		public static final int BIZ_CODE_20000 = 20000;
		/***借支单***/
		public static final int BIZ_CODE_30000 = 30000;
		/***风险金***/
		public static final int BIZ_CODE_40000 = 40000;
	}

	/**
	 * 异常登记的处理状态
	 */
	public static enum Exception_Deal_Status {
		/**登记未处理*/
		CHECK_IN(1),
		/**事业部处理通过*/
		DEAl_PASS(2),
		/**财务审核通过*/
		FINANCIAL_DEPARTMENT_PASS(3),
		/**财务审核失败*/
		FINANCIAL_DEPARTMENT_FAIL(4),
		/**事业部取消异常*/
		DEAL_CANCEL(5),
		/**区总通过*/
		REGIONAL_GENERAL_MANAGER_PASS(6),
		/**区总驳回*/
		REGIONAL_GENERAL_MANAGER_FAIL(7),
		/**市场部通过*/
		MARKETING_MANAGER_PASS(8),
		/**市场部驳回*/
		MARKETING_MANAGER_CANCEL(9);

		private int status;

		Exception_Deal_Status(int status) {
			this.status = status;
		}
		public int getStatus() {
			return status;
		}
		public static Exception_Deal_Status getStatus(int status) {
			Exception_Deal_Status[] values = Exception_Deal_Status.values();

			for (Exception_Deal_Status s: values) {
				if(s.getStatus() == status)
					return s;
			}
			return null;
		}

	}
	/**
	 * 异常登记的处理类型
	 */
	public static enum Exception_Deal_Type {
		/**扣司机钱*/
		REDUCEMONEY(1),
		/**补偿司机钱*/
		ADDMONEY(2);

		private int type;

		Exception_Deal_Type(int type) {
			this.type = type;
		}
		public int getType() {
			return type;
		}
		public static Exception_Deal_Type getType(int status) {
			Exception_Deal_Type[] values = Exception_Deal_Type.values();

			for (Exception_Deal_Type s: values) {
				if(s.getType() == status)
					return s;
			}
			return null;
		}

	}
	/**
	 * 审核状态
	 */
	public static class HAS_VER_STATE {
		//未审核
		public static final int HAS_VER_NO = 0;
		//已审核
		public static final int HAS_VER_YES = 1;
	}
	/**
	 * 资金渠道
	 */
	public static class CAPITAL_CHANNEL {
		//群鹰
		public static final String  CAPITAL_CHANNEL1 = "1";
		//握物流
		public static final String  CAPITAL_CHANNEL2 = "2";
		//志鸿
		public static final String  CAPITAL_CHANNEL3 = "3";
		//路歌
		public static final String  CAPITAL_CHANNEL4 = "4";
		//润宝祥
		public static final String  CAPITAL_CHANNEL5 = "5";
		//现金充值(专门来标识现金充值)
		public static final String  CAPITAL_CHANNEL0 = "0";
	}

	/**
	 * 油卡和etc转现、维修基金转现
	 */
	public static class TURN_CASH {
		//油卡转现
		public static final String TURN_TYPE1 = "1";

		//ETC转现
		public static final String TURN_TYPE2 = "2";

		//油和ETC同时转现(进程)
		public static final String TURN_TYPE3 = "3";

		//维修基金转现
		public static final String TURN_TYPE4 = "4";

		//转移折扣
		public static final Long TURN_DISCOUNT1 = 100L;
		public static final Long TURN_DISCOUNT2 = 93L;

		//油转移到现金账户
		public static final String TURN_OIL_TYPE1 = "1";
		//油转成实体油卡
		public static final String TURN_OIL_TYPE2 = "2";

		//进程转现折扣
		public static final Long TASK_TURN_DISCOUNT1 = 100L;
		public static final Long TASK_TURN_DISCOUNT2 = 93L;
		public static final Long TASK_TURN_DISCOUNT3 = 80L;

		//维修基金
		public static final Long REPAIR_FUND_TASK_TURN_DISCOUNT1 = 50L;
	}

	/**
	 * 强制平账
	 *
	 */
	public static class ZHANG_PING {
		//单账户平账
		public static final String ZHANH_PING_TYPE1 = "1";
		//选择账户平账
		public static final String ZHANH_PING_TYPE2 = "2";
		//司机油账户平账
	    public static final String ZHANH_PING_TYPE3 = "3";
		//强制平账的账户
		public static final int ZHANH_PING_ACCOUNT_TYPE1 = 1;
		//选择账户平账时勾选的账户
		public static final int ZHANH_PING_ACCOUNT_TYPE2 = 2;
		//作为司机订单
		public static final int IS_DRIVER_ORDER_TYPE1 = 1;
		//作为经济人订单
		public static final int IS_DRIVER_ORDER_TYPE2 = 2;
		//司机油账户充值
		public static final int RECHARGE_OIL_TYPE3 = 3;
	}

	/**
	 * 支出费用上限
	 */
	public static class PAY_FEE_LIMIT {
		//类型
		public static final String TYPE = "type";
		//订单支出
		public static final String TYPE1 = "1";
		//司机工资
		public static final String TYPE2 = "2";
		//会员消费
		public static final String TYPE3 = "3";

		//科目
		public static final String SUB_TYPE = "subType";
		//中标价上限
		public static final String SUB_TYPE1 = "1";
		//成本异常上限
		public static final String SUB_TYPE2 = "2";
		//收入异常上限
		public static final String SUB_TYPE3 = "3";
		//司机借支上限
		public static final String SUB_TYPE4 = "4";
		//招商车费用补款上限
		public static final String SUB_TYPE5 = "5";
		//司机基本工资上限
		public static final String SUB_TYPE6 = "6";
		//基本模式补贴上限
		public static final String SUB_TYPE7 = "7";
		//自有车司机总工资上限
		public static final String SUB_TYPE8 = "8";
		//合作个体户总工资上限
		public static final String SUB_TYPE9 = "9";
		//里程模式补贴上限
		public static final String SUB_TYPE10 = "10";
		//扫码加油上限
		public static final String SUB_TYPE11 = "11";
		//扫码加油次数上限
		public static final String SUB_TYPE12 = "12";
		//上限单位
		public static final String UNIT = "元";
		//基本模式补贴上限上限单位
		public static final String UNIT7 = "元/天";
		//里程模式补贴上限上限单位
		public static final String UNIT10 = "元/公里";
		//扫码加油上限上限单位
		public static final String UNIT11 = "元/次";
		//扫码加油次数上限上限单位
		public static final String UNIT12 = "次/天";

	}

	/**
	 * 事故司机：枚举主驾/副驾
	 * @author EditTheLife
	 */
	public static class LOAN_ACCIDENT_DRIVER{
		public static final int LOAN_ACCIDENT_DRIVER1 = 1;//主驾
		public static final int LOAN_ACCIDENT_DRIVER2 = 2;//副驾
	}
	/**
	 * 责任划分：枚举无责/次责/主责/全责
	 * @author EditTheLife
	 */
	public static class LOAN_DUTY_DIVIDE{
		public static final int LOAN_DUTY_DIVIDE1 = 1;//无责
		public static final int LOAN_DUTY_DIVIDE2 = 2;//次责
		public static final int LOAN_DUTY_DIVIDE3 = 3;//主责
		public static final int LOAN_DUTY_DIVIDE4 = 4;//全责
	}
	/**
	 * 事故类型：枚举 车损/伤人/单方事故/其他
	 * @author EditTheLife
	 */
	public static class LOAN_ACCIDENT_TYPE{
		public static final int LOAN_ACCIDENT_TYPE1 = 1;//车损
		public static final int LOAN_ACCIDENT_TYPE2 = 2;//伤人
		public static final int LOAN_ACCIDENT_TYPE3 = 3;//单方事故
		public static final int LOAN_ACCIDENT_TYPE4 = 4;//其他
	}
	/**
	 * 事故原因：疏忽大意/追尾/疲劳驾驶/变线/路口车辆操作不当/其他
	 * @author EditTheLife
	 */
	public static class LOAN_ACCIDENT_REASON{
		public static final int LOAN_ACCIDENT_REASON1= 1;//疏忽大意
		public static final int LOAN_ACCIDENT_REASON2= 2;//追尾
		public static final int LOAN_ACCIDENT_REASON3 = 3;//疲劳驾驶
		public static final int LOAN_ACCIDENT_REASON4 = 4;//变线
		public static final int LOAN_ACCIDENT_REASON5 = 5;//路口车辆操作不当
		public static final int LOAN_ACCIDENT_REASON6 = 6;//其他
	}

	/*
	 * APP司机端流水改造
	 */
	public static class APP_ACCOUNT_DETAILS_OUT {
		//支出
		public static final String COST_TYPE1 = "1";
		//收入
		public static final String COST_TYPE2 = "2";
		//其他
		public static final String COST_TYPE3 = "3";

		//查询截止月份
		public static final Long LAST_MONTH = 201805L;

		//账龄表截止月份
		public static final Long PAY_MONTH = 201809L;

	}
	/**
	 * 支付维修单类型
	 */
	public static class REPAIR_TYPE {

		//公司付
		public static final String SELECT_PAYWAY1 = "1";
		//自付-账户
		public static final String SELECT_PAYWAY2 = "2";
		//自付微信
		public static final String SELECT_PAYWAY3 = "3";
		//自付现金
		public static final String SELECT_PAYWAY4 = "4";

		//勾选了维修基金
		public static final String SELECT_TYPE0 = "0";
		//未勾选维修基金
		public static final String SELECT_TYPE1 = "1";
	}

	/**
	 * 客户状态
	 * @author aaron
	 *
	 */
	public static class CUSTOMER_TYPE{
		public static final Integer YES = 1;//在客户档案展示的数据
		public static final Integer NO = 0;//不在客户档案展示数据
	}
	/**
	 * 客户认证状态
	 * @author aaron
	 *
	 */
	public static class CUSTOMER_AUTH_STATE{
		public static final Integer STATE1 = 1;//未认证
		public static final Integer STATE2 = 2;//已认证
		public static final Integer STATE3 = 3;//认证失败
	}

	public static class ACCOUNT_BANK_REL{
		/** 私人账户 */
		public static final Integer BANK_TYPE_0 = 0;//私人账户
		/** 对公账户 */
		public static final Integer BANK_TYPE_1 = 1;//对公账户
		/** 服务费对公账户 */
		public static final Integer BANK_TYPE_SERVICE = 5;//服务费对公账户
		/** 微信账户 */
		@Deprecated
		public static final Integer BANK_TYPE_2 = 2;//微信账户
		/** 路歌对公账户 */
		@Deprecated
		public static final Integer BANK_TYPE_3 = 3;//路歌对公账户
		/** 收款对公账户 */
		@Deprecated
		public static final Integer BANK_TYPE_4 = 4;//收款对公账户
	}

	public static class BIND_STATE{
		/** 未绑定 */
		public static final Integer BIND_STATE_NONE = 0;//未绑定
		/** 已绑定 */
		public static final Integer BIND_STATE_ALL = 1;//已绑定
		/** 部分绑定 */
		public static final Integer BIND_STATE_PART = 2;//部分绑定
	}



	public static class BALANCE_BANK_TYPE{
		/** 私人收款账户余额 */
		public static final Integer PRIVATE_RECEIVABLE_ACCOUNT = 2;//私人收款账户余额
		/** 私人付款账户余额 */
		public static final Integer PRIVATE_PAYABLE_ACCOUNT = 22;//私人付款账户余额
		/** 对公收款账户 余额*/
		public static final Integer BUSINESS_RECEIVABLE_ACCOUNT = 1;//对公收款账户余额
		/** 对公付款账户余额 */
		public static final Integer BUSINESS_PAYABLE_ACCOUNT = 11;//对公付款账户余额

		/** 付款账户余额 */
		public static final Integer PAYABLE_ACCOUNT = 1122;//付款账户余额
		/** 收款账户余额 */
		public static final Integer RECEIVABLE_ACCOUNT = 12;//收款账户余额


		/** 对公账户余额 */
		public static final Integer BUSINESS_ACCOUNT = 111;//对公账户余额
		/** 对私账户余额 */
		public static final Integer PRIVATE_ACCOUNT = 222;//对私账户余额

	}

	public static class TASK_USER{
		/** 进程用户ID */
		public static final String TASK_USER_ID="TASK_USER_ID";
		/** 进程用户姓名  */
		public static final String TASK_USER_NAME="TASK_USER_NAME";
	}

	/**
	 * 是否删除
	 */
	public static class SERVICE_IS_DEL{
		/** 是 */
		public static final int YES = 1;
		/** 否  */
		public static final int NO = 0;
	}

	public static class SYNC_IS_REPORT{
		/**
		 * 待同步
		 */
		public static final int IS_REPORT_0 = 0;

		/**
		 * 同步成功
		 */
		public static final int IS_REPORT_1 = 1;
		/**
		 * 同步失败
		 */
		public static final int IS_REPORT_2 = 2;
	}




	/**
	 * 是否延迟
	 */
	public static class QUALITY_TYPE{
		/**
		 * 空
		 */
		public static final int QUALITY_TYPE_0= 0;

		/**
		 * 正常
		 */
		public static final int QUALITY_TYPE_1 = 1;

		/**
		 * 延迟
		 */
		public static final int QUALITY_TYPE_2 = 2;
	}

	/**
	 * 记录油卡日志类型枚举
	 */
	public static class OIL_LOG_TYPE{
		/**油卡回收*/
		public static final int RECOVER = 1;
		/**油卡充值核销*/
		public static final int WRITE_OFF = 2;
		/**使用油卡*/
		public static final int USE = 3;
		/**油卡理论余额增减*/
		public static final int ADD_OR_REDUCE = 4;
	}

	public static class PAYMENT_OBJ_TYPE{
		/**现金*/
		public static final int cashPayment = 1;
		/**虚拟油卡*/
		public static final int fictitiousOilPayment = 2;
		/**ETC*/
		public static final int ectPayment = 3;
		/**实体油卡*/
		public static final int entityOilPayment = 4;
		/**司机借支*/
		public static final int driverLoanPayment = 5;
		/**个人支出*/
		public static final int personPayment = 6;
		/**车管借支*/
		public static final int tubeLoanPayment = 7;
		/**维修保养*/
		public static final int repairPayment = 9;
		/**司机报销*/
		public static final int driverExpensePayment = 8;
		/**车管报销*/
		public static final int tubeExpensePayment = 10;
		/**司机工资*/
		public static final int driverSalaryPayment = 11;
	}


	public static class PAY_CONFIRM{
		/**已预期*/
		public static final int withdraw = 0;
		/**车队确认中*/
		public static final int recharge = 1;
		/**车队已确认*/
		public static final int transfer = 2;

	}


	public static class DUE_AND_PAYABLE_SUBJECTS_ID {
		/**应收应付逾期SUBJECTS_ID*/
		public static final String SUBJECTS_ID = "1530,1532,1536,1540,1543,1546,1551,1559,1554,1561,1563,1328,1329,"
				+ "1651,1652,50032,50033,1653,1654,1657,1658,1655,1656,50038,50039,5101,5102";

	}


	public static class PAY_RESULT_STS{
		//成功
		public static final int payResultSts0 = 0;
		//网络超时
		public static final int payResultSts1 = 1;
		//余额不足
		public static final int payResultSts2 = 2;
		//未绑定银行卡
		public static final int payResultSts3 = 3;
		//其他异常
		public static final int payResultSts4 = 4;
	}

	public static class PAY_OUT_OPER{
		public static final int ORDER = 0;
		public static final int ACCOUNT= 1;
	}

	/**
	 * 车牌种类
	 */
	public static class PLATE_NUMBER_TYPE{
		public static final String PLATE_NUMBER_TYPE_01 = "01";//01 大型汽车
		public static final String PLATE_NUMBER_TYPE_02 = "02";//02 小型汽车
	}

	/**
	 * 请求的地址
	 */
	public static class URL_SUFFIX{
		public static final String URL_SUFFIX01 = "/api/query/violation";//查询违章及费用
		public static final String URL_SUFFIX02 = "/api/order/onLineorder/ids";//电子眼在线下单
		public static final String URL_SUFFIX03 = "/api/order/vio/orderinfo";//电子眼订单状态查询
		public static final String URL_SUFFIX04 = "/api/order/orderfine";//罚款代缴下单
		public static final String URL_SUFFIX05 = "/api/order/orderinfo";//罚款代缴订单状态查询
		public static final String URL_SUFFIX06 = "/api/order/fineorder/offer";//罚款代缴订单报价
		public static final String URL_SUFFIX07 = "/api/order/cancel";//取消订单接口
		public static final String URL_SUFFIX08 = "/api/trafficticket/query";//文书号查询
		public static final String URL_SUFFIX09 = "/api/config/";//支持城市及条件
	}

	/**
	 * 车马炮违章订单结果查询状态 0：已撤销 1：待付款 2：已付款 3：处理中 5：已退单 7：已完成
	 */
	public static class VIOLATION_ORDER_STATUS{
		public static final int STATUS0 = 0;
		public static final int STATUS1 = 1;
		public static final int STATUS2 = 2;
		public static final int STATUS3 = 3;
		public static final int STATUS5 = 5;
		public static final int STATUS7 = 7;
	}

	/**
	 * 现场违章获取方式 1:通过手动录入
	 */
	public static class ACCESS_TYPE{
		public static final int ACCESS_TYPE01 = 1;
	}

	//平台数据统计，起始值REDIS配置
	public static class OBMS_STATISTICS_BASE_NUMBER {
		public static final String TENANT = "OBMS_TENANT";
		public static final String USER = "OBMS_USER";
		public static final String VEHICLE = "OBMS_VEHICLE";
		public static final String ORDER = "OBMS_ORDER";
		public static final String ORDER_FEE = "OBMS_ORDER_FEE";
		public static final String SERVICE_FEE = "OBMS_SERVICE_FEE";
		public static final String PLATEFORM_MONEY = "OBMS_PLATEFORM_MONEY";
		public static final String INCOME = "OBMS_INCOME";
		public static final String BANK_FLOW = "OBMS_BANK_FLOW";
		public static final String BANK_FLOW_TODAY_MULTIPLE = "OBMS_BANK_FLOW_TODAY_MULTIPLE";
		public static final String RECHARGE = "OBMS_RECHARGE";
		public static final String RECHARGE_TODAY_MULTIPLE = "OBMS_RECHARGE_TODAY_MULTIPLE";
	}

	public static class G7_STATISTICS_CONSTS {
		//签名算法HmacSha256
	    public static final String HMAC_SHA256 = "HmacSHA256";
	    //编码UTF-8
	    public static final String ENCODING = "UTF-8";
	    //UserAgent
	    public static final String USER_AGENT = "altair.G7.java";

	    public static final String AUTH_PREFIX = "g7ac";
	    //换行符
	    public static final String LF = "\n";
	    //串联符
	    public static final String SPE1_COMMA = ",";
	    //示意符
	    public static final String SPE2_COLON = ":";
	    //连接符
	    public static final String SPE3_CONNECT = "&";
	    //赋值符
	    public static final String SPE4_EQUAL = "=";
	    //问号符
	    public static final String SPE5_QUESTION = "?";

	    //默认请求超时时间,单位毫秒
	    public static final int DEFAULT_TIMEOUT = 5000;
	    //签名Header
	    public static final String X_CA_SIGNATURE = "Authorization";
	}

	public static class HttpHeader {
	    //请求Header Accept
	    public static final String HTTP_HEADER_ACCEPT = "Accept";
	    //请求Body内容MD5 Header
	    public static final String HTTP_HEADER_CONTENT_MD5 = "Content-MD5";
	    //请求Header Content-Type
	    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
	    //请求Header UserAgent
	    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
//	    //请求Header Date
//	    public static final String HTTP_HEADER_DATE = "Date";

	    public static final String HTTP_HEADER_G7_TIMESTAMP= "X-G7-OpenAPI-Timestamp";
	    //参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
	    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-G7-Ca-";
	}
	public static class HttpSchema {
	    //HTTP
	    public static final String HTTP = "http://";
	    //HTTPS
	    public static final String HTTPS = "https://";

	}

	/**
	 * 车队调查问卷填写状态 1正常填写;2跳过填写
	 */
	public static class SURVEY_FILL_STATE{
		public static final int WRITE = 1;
		public static final int SKIP= 2;
	}

	/**
	 * 交付撤场（交付撤场日期）后7天，30天、90天、180天、360天客户
	 */
	public static class DAY_AFTER_LEAVE_DATE{
		public static final int AFTER_LEAVE_DATE_7 = 7;
		public static final int AFTER_LEAVE_DATE_30 = 30;
		public static final int AFTER_LEAVE_DATE_90 = 90;
		public static final int AFTER_LEAVE_DATE_180 = 180;
		public static final int AFTER_LEAVE_DATE_360 = 360;
	}

	public static class PLATFORM_PAY_RESULT{
		public static final int PLATFORM_PAY_RESULT_1 = 1;//平台支付结果：成功
		public static final int PLATFORM_PAY_RESULT_2 = 2;//平台支付结果：无
	}
	public static class BANK_PAY_RESULT{
		public static final int BANK_PAY_RESULT_1 = 1;//银行支付结果：成功
		public static final int BANK_PAY_RESULT_2 = 2;//银行支付结果：无
	}
	public static class SETTLE_RESULT{
		public static final int SETTLE_RESULT_1 = 1;//对账结果:成功
		public static final int SETTLE_RESULT_2 = 2;//对账结果:异常
	}
	public static class SETTLE_DEAL_RESULT{
		public static final int SETTLE_DEAL_RESULT_0 = 0;//对账异常处理结果:无需处理
		public static final int SETTLE_DEAL_RESULT_1 = 1;//对账异常处理结果:未处理
		public static final int SETTLE_DEAL_RESULT_2 = 2;//对账异常处理结果:已处理
	}

	public static class CM_SALARY_TABLE{
		public static final String CM_SALARY_TEMPLATE ="cm_salary_template";//工资单模板
		public static final String CM_TEMPLATE_FIELD ="cm_template_field";//工资单模板字段
		public static final String CM_SALARY_INFO_NEW ="cm_salary_info_new";//工资单
		public static final String CM_SALARY_INFO_NEW_EXT ="cm_salary_info_new_ext";//工资单拓展
	}

	public static class ACCOUNT_STATEMENT_TABLE{
		public static final String ACCOUNT_STATEMENT_DETAILS ="account_statement_details";//对账单车辆明细
		public static final String ACCOUNT_STATEMENT_DETAILS_EXT ="account_statement_details_ext";//对账单车辆明细拓展
	}

	public static class SAL_SALARY_STATUS{
		public static final int SAL_SALARY_STATUS_0 = 0;//待发送
		public static final int SAL_SALARY_STATUS_2 = 2;//待确认
		public static final int SAL_SALARY_STATUS_3 = 3;//已确认
		public static final int SAL_SALARY_STATUS_4 = 4;//部分结算
		public static final int SAL_SALARY_STATUS_5 = 5;//已结算
	}


	public static class BILL_56K{
		public static  final String BILL_56K = "56K";
		public static  final String PREFECT_DATA="PREFECT_DATA";
	}

	public static class USER_TYPE{
		public static  final int USER_TYPE0 = 0;//司机
		public static  final int USER_TYPE1 = 1;//车队长
	}

	public static class PAY_STATUS{
		//付款结果(0:未操作 1:申请办理中 2:申请办理成功 3:申请办理失败 4:付款成功 5:付款失败)
		public static  final String PAY_STATUS0 = "0";//未操作
		public static  final String PAY_STATUS1 = "1";//申请办理中
		public static  final String PAY_STATUS2 = "2";//申请办理成功
		public static  final String PAY_STATUS3 = "3";//申请办理失败
		public static  final String PAY_STATUS4 = "4";//付款成功
		public static  final String PAY_STATUS5 = "5";//付款失败
		public static  final String PAY_STATUS6 = "6";//未申请支付
		public static  final String PAY_STATUS7 = "7";//未提交银行

	}

	public static class SYNC_STATE{
		public  static  final Integer SYNC_STATE0=0;//0初始化
		public  static  final Integer SYNC_STATE1=1;//1失败
		public  static  final Integer SYNC_STATE2=2;//2成功
	}


	public static class  BUSI_TYPE{
		public  static  final Integer BUSI_TYPE1=1;//1分配
		public  static  final Integer BUSI_TYPE2=2;//2消费
	}


	public static class AMOUNT_TYPE{
		public  static  final Integer AMOUNT_TYPE1=1;//現金
		public  static  final Integer AMOUNT_TYPE2=2;//ETC
		public  static  final Integer AMOUNT_TYPE3=3;//油
		public  static  final Integer AMOUNT_TYPE4=4;//油
	}

	public static class AMOUNT_TYPE56K{
		public  static  final Integer AMOUNT_TYPE56K1=1;//邮费
		public  static  final Integer AMOUNT_TYPE56K2=2;//ETC
		public  static  final Integer AMOUNT_TYPE56K3=3;//现金
	}

	public static class BEIDOU_PAYMENT_CLASS{
		public  static  final int PAYMENT_DATE=1;//按天
		public  static  final int PAYMENT_MONTH=2;//按月
		public  static  final int PAYMENT_YEAR=3;//按年
	}

	public static class OIL_RECHARGE_ACCOUNT_TYPE{
		public  static  final int ACCOUNT_TYPE1=1;//母卡
		public  static  final int ACCOUNT_TYPE2=2;//子卡
	}

	public static class OIL_RECHARGE_BILL_TYPE{
		public static final int BILL_TYPE1 = 1;//承运商开票账户
		public static final int BILL_TYPE2 = 2;//抵扣票账户
		public static final int BILL_TYPE3 = 3;//已开票账户
	}

	public static class OIL_RECHARGE_RECALL_TYPE{
		public static final int RECALL_TYPE1 = 1;//原路返回
		public static final int RECALL_TYPE2 = 2;//到转移账户
	}

}
