package com.youming.youche.record.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SysStaticDataEnum {

	public static long PT_TENANT_ID = 1L;

	public static long PT_TENANT_USER_ID = 999999999L;

	public static long PT_ACCOUNT_BANK_REL_ID = 1000000000L;

	public static long APP_CONFIGURE_ENTITY_ID = 9999999L;

	/** 分割符 **/
	public final static String SPLIT_CHAR = "^";

	/**
	 * 黑名单类型
	 */
	public static class BACK_TYPE {

		/**
		 * 车队黑名单
		 */
		public static final Integer tenant = 1;

		/**
		 * 平台黑名单
		 */
		public static final Integer pt = 2;

	}

	/**
	 * 用户类型
	 *
	 * @author dxb
	 */
	public static class USER_TYPE {

		/** 普通员工 **/
		public static final int CUSTOMER_USER = 1;

		/** 服务商 **/
		public static final int SERVICE_USER = 2;

		/** 司机 **/
		public static final int DRIVER_USER = 3;

		/** 服务商子账号 */
		public static final int SERVER_CHILD_USER = 4;

		/** 票据服务商 */
		public static final int BILL_SERVER_USER = 5;

		/**
		 * 超管
		 */
		public static final int ADMIN_USER = 6;

		/**
		 * 收款人
		 */
		public static final int RECEIVER_USER = 7;

	}

	/**
	 * 是否快速创建
	 */
	public static class QUICK_FLAG {

		/** 是快速创建 */
		public static final Integer IS_QUICK = 1;

		/** 不是快速创建 */
		public static final Integer NOT_QUICK = 0;

	}

	public static class TENANT_TYPE {

		/** 车队 */
		public static final Integer TENANT = 1;

		/** 三方 */
		public static final Integer THREE_PARTIES = 2;

		/** 混合 */
		public static final Integer MIXED = 3;

	}

	public static class TENANT_PAY_STATE {

		/** 未到期 */
		public static final Integer UNEXPIRED = 0;

		/** 付款中 */
		public static final Integer PAYING = 1;

		/** 已付款 */
		public static final Integer PAYED = 2;

		/** 已逾期 */
		public static final Integer EXPIRED = 3;

	}

	/**
	 *
	 * 数据状态 定义
	 *
	 * @author dxb
	 **/
	public static class SYS_STATE_DESC {

		/** 无效 **/
		public static final int STATE_NO = 0;

		/** 有效 **/
		public static final int STATE_YES = 1;

	}

	/**
	 * 是否认证0、未认证 1、已认证
	 *
	 */
	public static class SYS_USER_STATE {

		/** 未认证 **/
		public static final int AUDIT_NOT = 1;

		/** 已认证 **/
		public static final int AUDIT_APPROVE = 2;

	}

	public static class QUOTATION_STATE {

		/** 询价中 */
		public static final Integer PROCESSING = 1;

		/** 询价失效 */
		public static final Integer NO_EFFECT = 2;

		/** 不处理 */
		public static final Integer NO_DEAL = 3;

		/** 已报价 */
		public static final Integer PROCESSED = 4;

	}

	/**
	 *
	 * 帐户状态 0无效（冻结账户）；1正常 2限制（冻结预支功能）
	 *
	 * @author dxb
	 **/
	public static class SYS_ACC_STATE {

		/** 无效 **/
		public static final int STATE_NO = 0;

		/** 有效 **/
		public static final int STATE_YES = 1;

		/** 限制 **/
		public static final int STATE_YES_XZ = 2;

	}

	/**
	 * 小车队状态
	 */
	public static class VIRTUAL_TENANT_STATE {

		/**
		 * 默认值：不是小车队
		 */
		public static final Integer NOT_VIRTUAL = 0;

		/**
		 * 新建的小车队
		 */
		public static final Integer IS_VIRTUAL = 1;

		/**
		 * 申请升级
		 */
		@Deprecated
		public static final Integer UPGRADE = 2;

		/**
		 * 小车队升级为车队审核不通过
		 */
		@Deprecated
		public static final Integer AUDIT_FAIL = 3;

		/**
		 * 小车队升级为车队审核通过
		 */
		@Deprecated
		public static final Integer AUDIT_PASS = 4;

	}

	/**
	 * 车队账户冻结状态
	 */
	public static class TENANT_FORZEN_STATE {

		/**
		 * 未冻结、正常、已解冻
		 */
		public static final Integer UNFORZEN = 1;

		/**
		 * 已冻结
		 */
		public static final Integer FORZEN = 2;

	}

	/**
	 * 开票能力
	 */
	public static class BILL_ABILITY {

		/**
		 * 有开票能力
		 */
		public static final Integer ENABLE = 1;

		/**
		 * 没有开票能力
		 */
		public static final Integer DISABLE = 2;

	}

	/**
	 * 平台开票方式
	 */
	public static class BILL_METHOD {

		/**
		 * 无
		 */
		public static final Integer NONE = 0;

		/**
		 * 淮安
		 */
		public static final Integer HUAI_AN = 10;

	}

	/**
	 *
	 * 帐户级别1：普通会员2银卡会员3：金卡会员4砖石卡会员
	 *
	 * @author dxb
	 **/
	public static class SYS_ACC_LEVEL {

		public static final int ACC_LEVEL1 = 1;

		public static final int ACC_LEVEL2 = 2;

		public static final int ACC_LEVEL3 = 3;

		public static final int ACC_LEVEL4 = 4;

	}

	/**
	 *
	 * 操作员是否启用锁定
	 *
	 * @author dxb 1：启用 2：锁定
	 **/
	public static class LOCK_FLAG {

		public static final int LOCK_YES = 1;

		public static final int LOCK_NO = 2;

	}

	/**
	 *
	 * 异常状态 1、待处理 2、已处理通过
	 *
	 */
	public static class PRO_STATE {

		/** 待处理 */
		public static final int STATE_NO = 1;

		/** 已处理 */
		public static final int STATE_YES = 2;

	}

	/**
	 *
	 * 异常责任：1、车主责任 2、货主责任
	 *
	 */
	public static class CHARGE_TYPE {

		public static final int CARER_DUTY = 1;

		public static final int GOODER_DUTY = 2;

	}

	/**
	 *
	 * 系统参数定义字符串
	 *
	 */
	public static class SYS_CFG_NAME {

		public static final String VEHICLE_SPEED = "VEHICLE_SPEED"; // 车辆速度

		public static final String CORRECT_RANGE = "CORRECT_RANGE"; // 时间矫正范围

		public static final String OLI_GET_DISTANCE = "OLI_GET_DISTANCE"; // 查询油站距离范围

	}

	/**
	 * 是否定标
	 */
	public static class BIDD_SCAL {

		/** 定标 */
		public static final int IS_SCAL = 1;

		/** 未定标 */
		public static final int NO_SCAL = 0;

	}

	/**
	 * 是否有效标
	 */
	public static class BIDD_STATE {

		/** 有效 */
		public static final int IS_VALIDATE = 1;

		/** 无效 */
		public static final int NO_VALIDATE = 0;

	}

	/**
	 * 挂靠托管类型
	 */
	public static class VEHICLE_AFFILIATE_TYPE {

		/** 挂靠 */
		public static final int ANCHORED = 1;

		/** 托管 */
		public static final int MANAGED = 2;

	}

	/**
	 * 挂靠托管类型
	 */
	public static class VEHICLE_CUSTODIAL_VERIFY_STATUS {

		/** 取消托管申请中 */
		public static final int MANAGED_CANCEL_VERIFYING = 1;

	}

	/**
	 * 1 市场部门 2 事业部 3 项目部 4 职能部门 5 大区 (组织类型)
	 */
	@Deprecated
	public static class ORG_TYPE {

		/** 市场部门 */
		public static final int ORG_TYPE1 = 1;

		/** 事业部 */
		public static final int ORG_TYPE2 = 2;

		/** 项目部 */
		public static final int ORG_TYPE3 = 3;

		/** 职能部门 */
		public static final int ORG_TYPE4 = 4;

		/** 大区 */
		public static final int ORG_TYPE5 = 5;

		/** 总裁办 */
		public static final int ORG_TYPE6 = 6;

		/** 财务 */
		public static final int ORG_TYPE7 = 7;

		/** 车管组 */
		public static final int ORG_TYPE8 = 8;

	}

	/**
	 * 1 运势界油卡 2 中石油（化）油卡
	 */
	public static class OILCARD_TYPE {

		/** 运势界油卡 */
		public static final int OILCARD_TYPE1 = 1;

		/** 中石油（化）油卡 */
		public static final int OILCARD_TYPE2 = 2;

		/** 混合油卡 */
		public static final int OILCARD_TYPE3 = 3;

	}

	public static class TRAILER_TYPE {

		/** 在途 */
		public static final int TRAILER_TYPE1 = 1;

		/** 在台 */
		public static final int TRAILER_TYPE2 = 2;

	}

	/**
	 * 志鸿充值提现业务
	 *
	 */
	public static class OBJ_TYPE {

		/** 订单提醒 **/
		public static final int ORDER = 1;

		/** 货源 **/
		public static final int GOODS = 2;

		/** 车源 **/
		public static final int CARS = 3;

		/** 待支付 **/
		public static final int PAY_WAIT = 4;

		/** 充值提醒 **/
		public static final int TOP_UP = 5;

		/** 邀请 */
		public static final int INVITE = 7;

		/** 通知 */
		public static final int NOTIFY = 8;

		/** 保证金 */
		public static final int DEPOSIT = 9;

		/** 关注线路 */
		public static final int ATTENTION = 10;

		/** 异常处理通知 **/
		public static final int EXCEPTION = 11;

		/** 指派订单 */
		public static final int ORDER_REASSIGN = 12;

		/** 修改订单 */
		public static final int ORDER_UPDATE = 13;

		/** 取消订单 **/
		public static final int ORDER_CANCEL = 14;

		/** 异常信息 */
		public static final int ORDER_EXCEPTION = 15;

		/** 时效罚款 **/
		public static final int ORDER_AGING = 16;

		// 充值
		public static final int RECHARGE = 12;

		// 充值油卡
		public static final int RECHARGE_OIL = 13;

		// ETC
		public static final int RECHARGE_ETC = 14;

		// 提现
		public static final int WITHDRAWAL = 15;

		// 预支
		public static final int PREPAY = 16;

		// G7打款提现
		public static final int WITHDRAWAL_G7 = 17;

		// 车队邀请
		public static final int TENANT_INVITE = 18;

		// 到期转可用
		public static final int TURN_CASH = 19;

		// 切换司机
		public static final int ORDER_DRIVER_SWITCH = 21;

		// 发送司机工资
		public static final int SEND_DRIVER_SALARY = 22;

	}

	/**
	 * 订单异常类型
	 */
	public static class PROBLEM_TYPE {

		public static final String CAR_BREA = "1";

		public static final String CAR_PROMISE = "2";

		public static final String GOODS_BAD = "3";

		public static final String GOODS_CROSS = "4";

		public static final String CUSTOM_BREA = "5";

		public static final String GOODS_BREAD = "6";

		public static final String OTHER = "7";

		/** 迟到罚款 **/
		public static final String CAR_LATE = "16";

	}

	/**
	 * 车辆类别
	 */
	public static class VEHICLE_CLASS {

		/** 自有公司车 */
		public static final int VEHICLE_CLASS1 = 1;

		/** 招商挂靠车 */
		public static final int VEHICLE_CLASS2 = 2;

		/** 临时外调车 */
		public static final int VEHICLE_CLASS5 = 3;

		/** 外来挂靠车 */
		public static final int VEHICLE_CLASS4 = 4;

		/** 外调合同车 */
		public static final int VEHICLE_CLASS6 = 5;

	}

	/**
	 * 价格枚举
	 */
	public static class PRICE_ENUM {

		/** 车 */
		public static final int CAR = 1;

		/** 千克 */
		public static final int KG = 2;

		/** 方 */
		public static final int SQ = 3;

		/** 公里 */
		public static final int DISTANCE = 4;

	}

	/**
	 * 车辆类型
	 */
	public static enum VEHICLE_STATUS_ENUM {

		/** 厢车(1) */
		VEHICLE_VAN(1),
		/** 平板(2) */
		VEHICLE_FLAT(2),
		/** 高栏(3) */
		VEHICLE_HIGH_HURDLES(3),
		/** 冷柜(4) */
		VEHICLE_COOLER(4),
		/** 其它车(5) */
		VEHICLE_OTHER(5),
		/** 厢车_高栏(6) */
		VEHICLE_VAN_OR_HIGH_HURDLES(6),

		/**
		 * 油罐车
		 */
		VEHICLE_OIL_BOX(7),
		/**
		 * 轿运车
		 */
		VEHICLE_CAR_BOX(8),

		/**
		 * 奶罐车
		 */
		VEHICLE_MILK_BOX(9);

		private int type;

		VEHICLE_STATUS_ENUM(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}

		public static VEHICLE_STATUS_ENUM getType(int status) {
			VEHICLE_STATUS_ENUM[] values = VEHICLE_STATUS_ENUM.values();

			for (VEHICLE_STATUS_ENUM s : values) {
				if (s.getType() == status)
					return s;
			}
			return null;
		}

	}

	/** 车辆类型 */
	public static class LICENCE_TYPE {

		/** 整车 */
		public static final int ZC = 1;

		/** 拖头 */
		public static final int TT = 2;

	}

	/**
	 * 告警接收角色
	 */
	public static class RECEIVE_ROLE {

		/** 自定义角色 */
		public static final int ZERO = 0;

		/** 驻场 */
		public static final int LOCAL = 1;

		/** 事业部领导 */
		public static final int BUSINESS = 2;

		/** 大区领导 */
		public static final int LEAD = 3;

		/*** 总裁办 */
		public static final int BOSS = 4;

	}

	/**
	 * 提醒类型
	 */
	public static class WARN_TYPE {

		/** 未到达提醒 */
		public static final int ALREADY_DEPEND = 1;

		/** 将要到达提醒 */
		public static final int NO_DEPEND = 2;

		/** 没有gps提醒 */
		public static final int NO_GPS = 3;

		/** 回单提醒 */
		public static final int RECEIVE = 4;

		/** 对账提醒 */
		public static final int BALANCE = 5;

		/** 开票提醒 */
		public static final int BILLING = 6;

		/** 收款提醒 */
		public static final int FEE = 7;

		/** 到达 */
		public static final int ARRIVE = 8;

		/** 价格审核提醒 */
		public static final int VERIFY = 9;

		/** 异常审核 **/
		public static final int EXCEPTION = 10;

		/*** 回单审核 **/
		public static final int RECEIVE_VERIFY = 11;

	}

	/**
	 * 线路社会车审核流程
	 *
	 * 区总审核：初始状态 0 0516改后－ 事业部审核 区总审核不通过：2 事业部不通过 总裁办审核：4 区总待审核 总裁办审核不通过：3 区总不通过 通过：1
	 */
	public static class CAR_LINE_STATE {

		public static final int AREA = 0;

		public static final int AREA_NO = 2;

		public static final int PRESIDENT = 4;

		public static final int PRESIDENT_NO = 3;

		public static final int PASS = 1;

	}

	/**
	 * 线路财务审核流程 招商部审核：0 招商部审核不通过：6 财务审核：2 财务不通过：3 总裁办审核：4 总裁办不通过：5 市场部审核 7 市场部不通过 8 通过：1
	 */
	public static class CAR_LINE_STATE_MERCHANT {

		public static final int BUSI = 0;

		public static final int BUSI_NO = 6;

		public static final int FINANCE = 2;

		public static final int FINANCE_NO = 3;

		public static final int PRESIDENT = 4;

		public static final int PRESIDENT_NO = 5;

		public static final int PASS = 1;

		public static final int MARKET = 7;

		public static final int MARKET_NO = 8;

	}

	/**
	 * 车辆属性
	 *
	 */
	public static class CAR_USER_TYPE {

		/** 公司自有司机 */
		public static final int OWN_CAR = 1;

		/** 业务招商司机 */
		public static final int BUSINESS_CAR = 2;

		/** 临时外调司机 */
		public static final int SOCIAL_CAR = 3;

		/** 外来挂靠司机 */
		public static final int ATTACH_CAR = 4;

	};

	/**
	 * 油卡状态
	 *
	 */
	public static class OILCARD_STATUS {

		/** 有效 */
		public static final int STATUS_YES = 1;

		/** 失效 */
		public static final int STATUS_NO = 2;

		/** 服务方已回收 */
		public static final int STATUS_CB = 3;

		/** 挂失 */
		public static final int STATUS_LOSS = 4;

	};

	/**
	 * 打款状态
	 *
	 */
	public static class PAYOUT_STATUS {

		/** 未打款 */
		public static final int STATUS_NO = 0;

		/** 已打款 */
		public static final int STATUS_YES = 1;

	};

	/**
	 * 账单状态 :0新建1已核销2部分核销3已开票 4 废弃 流程 0 > 2 > 1 > 3
	 */
	public static class BILL_STS {

		/** 新建 */
		public static final int NEW = 0;

		/** 已核销 */
		public static final int CHECK_ALL = 1;

		/** 部分核销 */
		public static final int CHECK_PART = 2;

		/** 已开票 */
		public static final int MAKE_RECEPIT = 3;

		/** 已废弃 */
		public static final int DEL_STS = 4;

	}

	/**
	 * 异常报备类型
	 */
	public static class PROBLEM_REPORT_TYPES {

		/** 延迟报备 */
		public static final int DELAY_REPORT = 1;

	}

	/**
	 * 油卡自动充值类型
	 *
	 * @author EditTheLife
	 */
	public static class SERVICE_TYPE {

		/** G7自动充值渠道 */
		public static final int G7 = 1;

		/** 金润自动充值渠道 */
		public static final int JRErDangJia = 2;

	}

	/**
	 * 借支类型 枚举
	 */
	public static class LOAN_SUBJECT {

		// 维修费
		public static final int LOAN_SUBJECT_1 = 1;

		// 轮胎费
		public static final int LOAN_SUBJECT_2 = 2;

		// 停车费 1
		public static final int LOAN_SUBJECT_3 = 3;

		// 过磅费 1
		public static final int LOAN_SUBJECT_4 = 4;

		// 违章罚款
		public static final int LOAN_SUBJECT_5 = 5;

		// 路桥费 1
		public static final int LOAN_SUBJECT_6 = 6;

		// 保养费
		public static final int LOAN_SUBJECT_7 = 7;

		// 油费 1
		public static final int LOAN_SUBJECT_8 = 8;

		// 车祸事故
		public static final int LOAN_SUBJECT_9 = 9;

	}

	/**
	 * 支付明细资金渠道类型
	 *
	 * @author EditTheLife
	 */
	public static class FUND_CHANNEL {

		/** 路歌资金渠道 */
		public static final int LUGE_CHANNEL = 1;

		/** 网商资金渠道 */
		public static final int MERCHANT_CHANNEL = 2;

	}

	/**
	 * payout_intf 打款资金来源
	 */
	public static class SOURCE_REMARK {

		// 可用提现
		public static final int SOURCE_REMARK_1 = 1;

		// 车管借支打款
		public static final int SOURCE_REMARK_2 = 2;

		// 内部借支打款
		public static final int SOURCE_REMARK_3 = 3;

		// 工资打款
		public static final int SOURCE_REMARK_4 = 4;

		// 报销打款
		public static final int SOURCE_REMARK_5 = 5;

		// 维修打款
		public static final int SOURCE_REMARK_6 = 6;

		// 维修基金转现
		public static final int SOURCE_REMARK_8 = 8;

		// 油和ETC转现
		public static final int SOURCE_REMARK_9 = 9;

	}

	/**
	 * 招商对账单状态
	 *
	 */
	public static class ACCOUNT_STATEMENT_STS {

		public static final int STS_0 = 0;// 未确认

		public static final int STS_1 = 1;// 事业部已确认

		public static final int STS_2 = 2;// 财务－已结算

		public static final int STS_3 = 3;// 财务－已发送账单

		public static final int STS_4 = 4;// 车老板确认

		public static final int STS_5 = 5;// 财务－已支付

		public static final int STS_6 = 6;// 部分结算

	}

	/**
	 * 招商对账单状态 结算表状态
	 */
	public static class ACCOUNT_STATEMENT_SETTLEMENT_STS {

		public static final int STS_0 = 0;// 未发送

		public static final int STS_1 = 1;// 已发送

		public static final int STS_2 = 2;// 司机确认

		public static final int STS_3 = 3;// 财务支付

	}

	/**
	 * 资金渠道枚举
	 */
	public static class VEHICLE_AFFILIATION {

		public static final String VEHICLE_AFFILIATION_1 = "1";// 群鹰

		public static final String VEHICLE_AFFILIATION_2 = "2";// 握物流

		public static final String VEHICLE_AFFILIATION_3 = "3";// 志鸿

		public static final String VEHICLE_AFFILIATION_4 = "4";// 路歌

		public static final String VEHICLE_AFFILIATION_5 = "5";// 润宝祥

		public static final String VEHICLE_AFFILIATION_6 = "6";// 旧数据

	}

	/**
	 * 订单修改状态
	 */
	public static class ORDER_UPDATE_STATE {

		public static final int NOT_UPDATE = 0;// 待修改

		public static final int UPDATING = 1;// 修改中

		public static final int VERIFYING = 2;// 审核中

	}

	/**
	 * 是否通过修改订单添加过实体油 [0:否、1:是]
	 */
	public static class HAS_INCR_ENTITY_FEE_FLAG {

		public static final int NO = 0;// 否

		public static final int YES = 1;// 是

	}

	/**
	 * 服务商类型
	 */
	public static class BUSINESS_TYPE {

		public static final int OIL_BUSINESS = 1;// 油卡供应商

		public static final int ETC_BUSINESS = 2;// etc供应商

		public static final int REPAIR_BUSINESS = 3;// 维修供应商

	}

	/**
	 * 服务商是否有效
	 */
	public static class BUSINESS_STATE {

		public static final int YES = 1;// 有效

		public static final int NO = 2;// 无效

	}

	/**
	 * 维修单状态
	 */
	public static class REPAIR_STATE {

		/** 待确认 **/
		public static final int TO_BE_DRIVER_CONFIRMED = 0;

		/** 司机驳回 **/
		public static final int DRIVER_REBUTTABLE = 1;

		/** 待车管中心确认 **/
		public static final int TO_BE_CARCENTS_CONFIRMED = 2;

		/** 车管中心驳回 **/
		public static final int CARCENTS_REBUTTABLE = 3;

		/** 待财务部确认 **/
		public static final int TO_BE_FINANCE_CONFIRMED = 4;

		/** 财务部驳回 **/
		public static final int FINANCE_REBUTTABLE = 5;

		/** 未到期 **/
		public static final int UNDUE = 6;

		/** 已到期 **/
		public static final int DUE = 7;

		/** 支付中 **/
		public static final int IN_PAY = 8;

		/** 已支付 **/
		public static final int HAVE_PAY = 9;

	}

	/**
	 * 司机报销类型
	 */
	public static class DRIVER_EXPENSE_TYPE {

		/**
		 * 停车费
		 */
		public static final int EXPENSE_CATEGORY1 = 1;

		/**
		 * 过磅费
		 */
		public static final int EXPENSE_CATEGORY2 = 2;

		/**
		 * 差旅费
		 */
		public static final int EXPENSE_CATEGORY3 = 3;

		/**
		 * 维修费
		 */
		public static final int EXPENSE_CATEGORY4 = 4;

		/**
		 * 其他
		 */
		public static final int EXPENSE_CATEGORY5 = 5;

	}

	/**
	 * 维修单支付方式
	 */
	public static class PAY_WAY {

		/** 公司付 **/
		public static final int PAY_COMPANY = 1;

		/** 自付-账户 **/
		public static final int PAY_ACCOUNT = 2;

		/** 自付-微信 **/
		public static final int PAY_WX = 3;

		/** 自付-现金 **/
		public static final int PAY_CASH = 4;

	}

	/**
	 * 异常情况 1:成本异常；2收入异常
	 */
	public static class PROBLEM_CONDITION {

		public static final int COST = 1;

		public static final int INCOME = 2;

	}

	/**
	 * 订单类型
	 */
	public static class ORDER_TYPE {

		public static final int FIXED_LINE = 1;

		public static final int TMP_LINE = 2;

		public static final int THIRD_PARTY = 3;

	}

	/**
	 * 用户审核状态
	 *
	 * @author Administrator
	 */
	public static class VERIFY_STS {

		/** 未认证 **/
		public static final int UNAUTHORIZED = 1;

		/** 认证中 **/
		public static final int IN_THE_AUTHENTICATION = 2;

		/** 未通过 **/
		public static final int AUTHENTICATION_FAILED = 3;

		/** 虚拟用户 **/
		public static final int VIRTUAL_USERS = 4;

		/** 已认证 **/
		public static final int CERTIFICATION_BY = 5;

		/** 路歌认证中 **/
		public static final int LUGE_AUTHING = 6;

		/** 路歌认证失败 **/
		public static final int LUGE_FAILED = 7;

	}

	/** 判断资料是否完善 **/
	public static class IS_PERFECT_INFO {

		/** 未完善资料 **/
		public static final int NOT_PERFECT = 0;

		/** 已完善资料 **/
		public static final int IS_PERFECT = 1;

	}

	/**
	 * 路线状态
	 */
	public static class CUCUSTOMER_LINE_STATE {

		/** 1:有效；2失效；3临时 */
		public static final int AVAILABILITY = 1;

		public static final int INVALID = 2;

		public static final int TMP = 3;

	}

	/**
	 * 路线是否需要找回货
	 */
	public static class IS_REVERT_GOODS {

		/** 0:不需要找回货; 1:需要找回货 */
		public static final int REVERT_GOODS_NO = 0;

		public static final int REVERT_GOODS_YES = 1;

	};

	/**
	 * 静态数据静态字段的定义 CODE_TYPE
	 */
	public static class SysStaticData {

		/** 投诉其他类主题枚举 **/
		public static final String COMPLAINT_OTHER_TYPE = "COMPLAINT_OTHER_TYPE";

		/** 投诉车主订单主题枚举 **/
		public static final String COMPLAINT_ORDER_CAR_TYPE = "COMPLAINT_ORDER_CAR_TYPE";

		/** 投诉货主订单主题枚举 **/
		public static final String COMPLAINT_ORDER_GOOD_TYPE = "COMPLAINT_ORDER_GOOD_TYPE";

		/** 投诉货主订单主题枚举 **/
		public static final String COMPLAINT_STATE = "COMPLAINT_STATE";

		/** 邀请来源类型途径 **/
		public static final String INVITE_MODE = "INVITE_MODE";

		/** 邀请结果 **/
		public static final String INVITE_RESULT = "INVITE_RESULT";

		/** 短信业务类型 **/
		public static final String OBJ_TYPE = "OBJ_TYPE";

		/** 短信类型 **/
		public static final String SMS_TYPE = "SMS_TYPE";

		/** 短信发送标志 **/
		public static final String SMS_SEND_FLAG = "SMS_SEND_FLAG";

		/** 交易类型 **/
		public static final String COST_TYPE = "COST_TYPE";

		/** 交易业务类型 **/
		public static final String ACCOUNT_DETAILS_BUSINESS_NUMBER = "ACCOUNT_DETAILS_BUSINESS_NUMBER";

		/** 账目类型 **/
		public static final String ACCOUNT_DETAILS_BOOK_TYPE = "ACCOUNT_DETAILS_BOOK_TYPE";

		/** 支付方式 **/
		public static final String PAY_WAY = "PAY_WAY";

		/** 增值服务 **/
		public static final String ORDER_SERVICE = "ORDER_SERVICE";

		/** 包装方式 **/
		public static final String PACKGE_WAY = "PACKGE_WAY";

		/** 认证状态(用户) **/
		public static final String USER_STATE = "USER_STATE";

		/** 用户类型定义 **/
		public static final String USER_TYPE = "USER_TYPE";

		/** 订单支付状态枚举 */
		public static final String PAY_STATE_AL = "PAY_STATE_AL";

		/** 车辆认证状态枚举 */
		public static final String VERIFY_STS = "VERIFY_STS";

		/** 成本异常类型 */
		public static final String COST_RECRIVE_ORDER_PROBLEM_TYPE = "RECRIVE_ORDER_PROBLEM_TYPE@COST";

		/** 收入异常类型 */
		public static final String INCOME_RECRIVE_ORDER_PROBLEM_TYPE = "RECRIVE_ORDER_PROBLEM_TYPE@INCOME";

		/** 是否已确认绑定车辆 */
		public static final String IS_BIND_TRUE = "IS_BIND_TRUE";

		/** 回单类型 **/
		public static final String RECEIPTS_TYPE = "RECEIPTS_TYPE";

		/** 日志渠道类型类型 **/
		public static final String LOG_CHANNEL_TYPE = "LOG_CHANNEL_TYPE";

		/** 日志类型 **/
		public static final String LOG_TYPE = "LOG_TYPE";

		/** 票据平台静态枚举 **/
		public static final String BILL_FORM_STATIC = "BILL_FORM_STATIC";

	}

	/**
	 * 系统参数 0。为公用系统参数，1。为平台门户系统参数、2，后台管理系统参数,3.手机系统参数
	 */
	public static class SYSTEM_CFG {

		/** 公用管理参数 **/
		public static final int CFG_0 = 0;

		/** 门户管理参数 **/
		public static final int CFG_1 = 1;

		/** 后台管理参数 **/
		public static final int CFG_2 = 2;

		/** APP手机管理参数 **/
		public static final int CFG_3 = 3;

	}

	/**
	 * 支付状态
	 *
	 * @author Administrator
	 */
	public static class PAY_TYPE {

		/** 待支付 **/
		public static final int WAIT_PAY = 0;

		/** 已支付 **/
		public static final int HAVE_TO_PAY = 1;

	}

	/**
	 * 短信类型
	 *
	 * @author Administrator
	 *
	 */
	public static class SMS_TYPE {

		/** 活动公告 1 **/
		public static final int EVENT_ANNOUNCEMENTS = 1;

		/** 进度通知 2 **/
		public static final int PROGRESS_NOTIFICATIONS = 2;

		/** 操作确认 3 **/
		public static final int OPERATIONAL_QUALIFIVATION = 3;

		/** 短信验证 4 **/
		public static final int MESSAGE_AUTHENTICATION = 4;

		/** 订单助手 5 **/
		public static final int ORDER_ASSISTANT = 5;

		/** 邀请信息 6 **/
		public static final int INVITE_INFO = 6;

		/** 系统提醒 7 **/
		public static final int SYSTEM_NOTIFY = 7;

	}

	public static class WX_SMS_TYPE {

		/** 微信提醒 20 **/
		public static final int WX_NOTIFY = 20;

	}

	public static class WEB_SMS_TYPE {

		/**
		 * 打开内部页面opentab(url)->内部通知或业务页
		 */
		public static final int OPEN_TAB = 11;

		/**
		 * 打开外部页面openwindow(url)->外部公告或推广
		 */
		public static final int OPEN_WINDOW = 12;

		/**
		 * 触发回调方
		 */
		public static final int CALL_BACK = 13;

		/**
		 * 仅显示，不做任何业务
		 */
		public static final int ONLY_SHOW = 14;

	}

	/**
	 * 信用等级
	 */
	public static class ACC_LEVEL {

		/** 招商车 */
		public static final String JOIN_INVESTMENT_CARD = "5";

	};

	/**
	 * 是否可以预支
	 */
	public static class IS_ADVANCE {

		public static final String YES = "1";

		public static final String NO = "0";

	}

	/**
	 * todo 用户大类类型（定义） 1、司机 2、货主3运营
	 */
	public static class SYS_USER_TYPE_BIG {

		/** 司机 **/
		public static final int BIG_DRIVER_USER = 1;

		/** 货主 **/
		public static final int BIG_GOODS_USER = 2;

		/** 运营 */
		public static final int BIG_OPER_USER = 3;

	}

	/**
	 * 评价类别（定义） 1：评价2：信用
	 */
	public static class COMMENT_CLASS {

		/** 评价 **/
		public static final int COMMENT_1 = 1;

		/** 信用 **/
		public static final int COMMENT_2 = 2;

	}

	/**
	 * 订单是否已评价 (车主评价货主、货主评价车) 1：未评价2：已评价
	 */
	public static class COMMENT_Y_OR_N {

		/** 未评价 **/
		public static final int COMMENT_NO = 1;

		/** 已评价 **/
		public static final int COMMENT_YES = 2;

	}

	/**
	 * 用户类型
	 *
	 * @author liyiye
	 *
	 */
	public static class SYS_USER_TYPE {

		/** 业务员 **/
		public static final int SALESMAN = 1;

		/** 调度员 **/
		public static final int DISPATCH = 2;

		/** 系统管理员 **/
		public static final int ADMIN = 5;

		/** 系统用户 **/
		public static final int SYSTEM_USER = 1;

		/** 客户经理 **/
		public static final int CUSTOMER_MANAGER = 2;

		/** 个体车主 **/
		public static final int INDIVIDUAL_CAR = 3;

		/** 发货企业 **/
		public static final int GOOD_COMPANY = 4;

		/** 物流公司 **/
		public static final int LOGISTICS_COMPANY = 5;

		/** 物流园区 **/
		public static final int LOGISTICS_GARDEN = 6;

		/** 物流车队 **/
		public static final int LOGISTICS_MOTORCADE = 7;

		/** 车辆挂靠单位 **/
		public static final int CAR_ORGANIZATION = 8;

		/** 发货个人 **/
		public static final int GOOD_PERSONAGE = 9;

		/** 资料是否完善－否 */
		public static final int IS_PERFECT_INFO_0 = 0;

		/** 资料是否完善－是 */
		public static final int IS_PERFECT_INFO_1 = 1;

	}

	/**
	 * 是否绑定成功（0:车主未绑定司机，1:司机未确认绑定，2：司机确认绑定）** /
	 *
	 * @author user
	 *
	 */
	public static class IS_BIND_TRUE {

		/** 0:车主未绑定司机 */
		public static final int BIND_0 = 0;

		/** 1:司机未确认绑定 */
		public static final int BIND_1 = 1;

		/** 2：司机已确认绑定 */
		public static final int BIND_2 = 2;

	}

	public static class VehicleState {

		/** 空闲 */
		public static final Long FREE = 0L;

		/** 运输中 */
		public static final Long BUSY = 1L;

		/** 下班 */
		public static final Long OFF_DUTY = 2L;

		/** 待发车 */
		public static final Long WAIT_CAR = 3L;

	}

	/**
	 * 用户是否接单（上班、下班签到）
	 *
	 */
	public static class USER_WORKING {

		/** 上班（在线） **/
		public static final int WORKING_YES = 2;

		/** 下班（不接单） **/
		public static final int WORKING_NO = 1;

	}

	/**
	 * 折扣类型
	 */
	public static class OIL_FLOW_DISCOUNT_TYPE {

		/** 1:折扣费用；2非折扣费用;3扩展类型 */
		public static final int DISCOUNT_TYPE = 1;

		public static final int NON_DISCOUNT_TYPE = 2;

	};

	/** 数据来源 **/
	public static class SOURCE_FLAG {

		/** 平台 **/
		public static final int PLATFORM = 0;

		/** 爬虫 **/
		public static final int SPIDER = 1;

		/** 百度 **/
		public static final int BAIDU = 2;

		/** 自动生成 **/
		public static final int AUTOGEN = 3;

		/** 购买的数据 **/
		public static final int BUY = 4;

		/** 常跑城市生成车源 **/
		public static final int OFTEN_BACKSTAGE = 5;

		/** 下单生成车源 **/
		public static final int ORDER_BACKSTAGE = 6;

		/** 微信 **/
		public static final int WEIXIN = 7;

		/** 导入 */
		public static final int IMPORT = 8;

		/** 新订单录入 **/
		public static final int NEW_INPUT = 9;

		/** 百世录入 **/
		public static final int BAISHI = 10;

		/** 推送接口 **/
		public static final int PUSH = 11;

		/** 开票订单导入 */
		public static final int BILL_IMPORT = 12;

	}

	/** 推送类型 */
	public static class TASK_TYPE {

		/** 异常信息 */
		public static final int EXCEPTION = 1;

	}

	/**
	 * 当前登录用户是、1:车主 2：司机 3：车主司机同一人（对于这辆车来说{前台用于控制操作按钮}）
	 *
	 * @author user
	 *
	 */
	public static class ROLE_TYPE {

		/** 1:车主 */
		public static final int ROLE_1 = 1;

		/** 2:司机 */
		public static final int ROLE_2 = 2;

		/** 3：车主司机同一人 */
		public static final int ROLE_3 = 3;

	}

	/**
	 * 货源在订单的状态
	 *
	 * @author Administrator
	 */
	public static class GOODS_CONFIRM {

		/** 订单未确认 **/
		public static final int GOODS_ORDER_UNCONFIRMED = 0;

		/** 订单已确认 **/
		public static final int GOODS_ORDER_CONFIRMED = 1;

		/** 货源未收货 **/
		public static final int NOT_RECEIVING = 2;

		/** 货源已收货 **/
		public static final int HAVE_RECEIVING = 3;

		/** 货源装货中 **/
		public static final int GOODS_LOADING = 4;

		/** 待取消 **/
		public static final int WAIT_CANCER = 5;

		/** 已取消 **/
		public static final int CANCELLED = 6;

	}

	/**
	 * 增值服务 1:保险 2、代收货款3、回单
	 */
	public static class ORDER_SERVICE {

		/** 保险 */
		public static final int ORDER_SERVICE_1 = 1;

		/** 代收货款 */
		public static final int ORDER_SERVICE_2 = 2;

		/** 回单 */
		public static final int ORDER_SERVICE_3 = 3;

	}

	/**
	 * 增值服务是否需要回单 1、需要带回单 2、不需要带回单
	 */
	public static class ORDER_SERVICE_3 {

		/** 需要带回单 */
		public static final int ORDER_SERVICE_3_1 = 1;

		/** 不需要带回单 */
		public static final int ORDER_SERVICE_3_2 = 2;

	}

	/**
	 * 订单统计归类（定义） 统计类型 //queryType : DEAL 待处理、PROCESS 进行中 、HIS 历史 、COMP完成交易
	 */
	public static class ORDER_QUERY_TYPE {

		/** 待处理 **/
		public static final String DEAL = "DEAL";

		/** 进行中 **/
		public static final String PROCESS = "PROCESS";

		/** 已完成（历史） */
		public static final String HIS = "HIS";

		/** 完成 */
		public static final String COMP = "COMP";

	}

	/**
	 * APP的应用类型
	 */
	public static class APP_TYPE {

		/** 司机版 **/
		public static final int DRIVER_APP = 1;

		/** 货主版 **/
		public static final int GOODER_APP = 2;

	}

	/**
	 * 用户状态
	 */
	public static class SYS_USER_CLASS {

		/** 车主 **/
		public static final int CAR_USER = 1;

		/** 货主 **/
		public static final int GOOD_USER = 2;

	}

	/** 车源类型 **/
	public static class CAR_TYPE {

		/** 返程车 **/
		public static final int RETURN_THE_CAR = 1;

		/** 本地车 **/
		public static final int THE_LOCAL_CAR = 2;

	}

	/**
	 * 订单状态
	 */
	public static class ORDER_STATE {

		/** 待确认 **/
		public static final int TO_BE_CONFIRMED = 0;

		/** 洽谈中 **/
		public static final int COMING_NEGOTIATION = 1;

		/** 已确认 **/
		public static final int HAVE_BEEN_CONFIRMED = 2;

		/** 运输中 **/
		public static final int TRANSPORT = 3;

		/** 已完成 **/
		public static final int COMPLETE = 4;

		/** 已取消 **/
		public static final int CANCELLED = 5;

		/** 装货中 **/
		public static final int LOADING = 6;

		/** 待支付 **/
		public static final int WAID_PAID = 7;

		/** 仲裁中 **/
		public static final int ARBITRAMENT = 8;

		/*** 取消中 */
		public static final int CANCELLED_ING = 9;

	}

	/**
	 * 车源在订单的状态
	 *
	 * @author Administrator
	 */
	public static class CAR_CONFIRM {

		/** 订单未确认 **/
		public static final int CAR_ORDER_UNCONFIRMED = 0;

		/** 订单已确认 **/
		public static final int CAR_ORDER_CONFIRMED = 1;

		/** 未到达 **/
		public static final int HAVE_NOT_REACHED = 2;

		/** 已到达 **/
		public static final int HAVE_REACHED = 3;

		/** 装货中 **/
		public static final int CAR_LOADING = 4;

		/** 待取消 **/
		public static final int WAIT_CANCER = 5;

		/** 已取消 **/
		public static final int CANCELLED = 6;

	}

	/**
	 * 支付流程
	 *
	 * @author Administrator
	 */
	public static class PAY_STATE {

		/** 未支付 **/
		public static final int DID_NOT_PAY = 1;

		/** 已申请 **/
		public static final int HAS_APPLIED_FOR = 2;

		/** 待确认 **/
		public static final int TO_BE_CONFIRMED = 3;

		/** 已完成 **/
		public static final int COMPLETE = 4;

		/** 申述中 **/
		public static final int IN_THE_COMPLAINT = 5;

		/** 已赔偿 **/
		public static final int HAVE_COMPENSATE = 6;

		/** 已退款 **/
		public static final int HAVE_REFUND = 7;

		/** 申述失败 **/
		public static final int APPEAL_FAILED = 8;

		/** 全部 **/
		public static final int ALL = 0;

	}

	/**
	 * 支付类型
	 *
	 * @author Administrator
	 *
	 */
	public static class BUSINESS_TYPES {

		/** 订单支付 **/
		public static final int ORDER_PAY = 1;

		/** 保险支付 **/
		public static final int INSURANCE_PAY = 2;

		/** 担保金支付 **/
		public static final int GUARANTEE_PAY = 3;

		/** 年费支付 **/
		public static final int ANNUAL_FEE_PAY = 4;

		/** 月租支付 **/
		public static final int AVERAGE_RENTAL_PAY = 5;

		/** 信息费支付 **/
		public static final int SMS_SEND_PAY = 6;

		/** 预付费支付 **/
		public static final int PREPAID_PAYMENT = 7;

	}

	public static class SUBJECTS_TYPE {

		/** 固定费用 **/
		public static final int FIXED_FEE = 1;

		/** 非固定费用 **/
		public static final int NOT_FIXED_FEE = 2;

	}

	/**
	 * 投保流程
	 *
	 * @author Administrator
	 */
	public static class PREMIUM_STATUS {

		/** 申请中 **/
		public static final int APPLICATION = 0;

		/** 审批中 **/
		public static final int EXAMINATION_APPROVAL = 61;

		/** 审批通过 **/
		public static final int EXAMINATION_APPROVAL_PASS = 62;

		/** 审批不通过 **/
		public static final int EXAMINATION_APPROVA_NOTL_PASS = 63;

		/** 已撤销 **/
		public static final int HAD_WITHDRAWN = 64;

		/** 投保失败 **/
		public static final int INSURE_AGAINST_FAILURE = 65;

		/** 已退保 **/
		public static final int HAVE_RETREAT = 66;

		/** 待退保 **/
		public static final int WAIT_RETREAT = 216;

		/** 报批中 **/
		public static final int APPROVAL_OF_THE = 901;

		/** 报批通过 **/
		public static final int APPROVAL_PASS = 902;

		/** 报备 **/
		public static final int REPORTED_TO_THE = 903;

	}

	/**
	 * 支付项目
	 *
	 * @author Administrator
	 *
	 */
	public static class SUBJECT_COST {

		/** 预付费 **/
		public static final int PREPAID = 1;

		/** 担保金 **/
		public static final int INSURANCE_GOLD = 2;

		/** 年费 **/
		public static final int ANNUAL_FEE = 3;

		/** 月租 **/
		public static final int AVERAGE_RENTAL = 4;

		/** 信息费 **/
		public static final int SMS_SEND_FEE = 5;

		/** 保险金 **/
		public static final int INSURANCE = 6;

		/** 运费 **/
		public static final int THE_FREIGHT = 7;

	}

	/**
	 * 保险类型
	 *
	 * @author Administrator
	 */
	public static class PREMIUM_TYPE {

		/** 基本险 **/
		public static final int BASIC_RISKS = 1;

		/** 综合险 **/
		public static final int AGAINST_ALL_RISKS = 2;

	}

	/**
	 * 保险分类
	 *
	 * @author Administrator
	 */
	public static class MAIN_GLAUSES_CODE {

	}

	/**
	 * 保险货物分类
	 */
	public static class GOOD_TYPE {

	}

	/**
	 * @author Administrator
	 */
	public static class WEBMANAGER_USER_TYPE {

	}

	/**
	 * 查询时间段
	 *
	 * @author Administrator
	 */
	public static class QUERY_DATA {

	}

	/**
	 * 银行类型
	 *
	 * @author Administrator
	 */
	public static class BANK_TYPE {

		public static final int PRI_0 = 0;// 对私

		public static final int PUB_1 = 1;// 对公

	}

	/**
	 * 保险支付状态
	 *
	 * @author Administrator
	 */
	public static class P_PAY_WAY {

		/** 未支付 **/
		public static final int WAIT_PAY = 1;

		/** 已支付 **/
		public static final int HAVE_TO_PAY = 2;

	}

	/**
	 * 附加险
	 *
	 * @author Administrator
	 */
	public static class ADDITIVE_NO_GL {

	}

	/** 保险条款 **/
	public static class ADDITIVE_NO_XH {

	}

	/** 货源是否竞价 **/
	public static class HY_GOODS_PUBLISH_IS_BIDD {

		/** 货源没有竞价 **/
		public static final int NOT_BIDD = 0;

		/** 货源有竞价 **/
		public static final int IS_BIDD = 1;

	}

	/**
	 * 专线推荐
	 *
	 * @author Administrator
	 */
	public static class ZX_LINE_RECOMMEND {

		/** 不推荐 **/
		public static final int RECOMMENDED = 0;

		/** 推荐 **/
		public static final int NOT_RECOMMENDED = 1;

	}

	/** 投保药物小类 **/
	public static class GOOD_TYPE2 {

	}

	public static class GOOD_TYPE3 {

	}

	public static class GOOD_TYPE4 {

	}

	public static class GOOD_TYPE5 {

	}

	/** 投保提醒 (没有配置在静态数据表) **/
	public static class PREMIUM {

		/** 不提醒 **/
		public static final int NOT_PREMIUM = 0;

		/** 要提醒 **/
		public static final int IS_PREMIUM = 1;

	}

	/** 常用地址用户类型(没有配置在静态数据表) **/
	public static class VEHICLE_OFTEN_TYPE {

		/** 车主 **/
		public static final int CAR_PERSON = 21;

		/** 货主 **/
		public static final int GOODS_PERSON = 22;

	}

	/** 来源类型 **/
	public static class SOURCE_TYPE {

		/** 车源 **/
		public static final int CAR_SOURCE = 1;

		/** 货源 **/
		public static final int GOODS_SOURCE = 2;

	}

	/** 渠道类型 **/
	public static class CHANEL_TYPE {

		/** WEB **/
		public static final int CHANEL_WEB = 1;

		/** APP **/
		public static final int CHANEL_APP = 2;

		/** 小程序 **/
		public static final int CHANEL_MINI_PROGREM = 3;

	}

	/****/
	public static class SYS_ENTITY_TYPE {

		/** 菜单 **/
		public static final int THE_MENU = 1;

		/** 按钮 **/
		public static final int BUTTON = 2;

	}

	/** 消息发布状态 **/
	public static class PUBLISH_STATUS_TYPE {

		/** 未读 **/
		public static final int UNREAD = 0;

		/** 只读 **/
		public static final int READ_ONLY = 1;

		/** 消息发布状态 **/
		public static final int PUBLISH_STATUS = 2;

	}

	/** 匹配结果 **/
	public static class PUBLISH_STS {

	}

	/** 车辆类型 **/
	public static class VEHICLE_STATUS {

	}

	/** 运输方式 **/
	public static class CON_TYPE {

	}

	/** 评价类别 **/
	public static class COMMENT_LEVEL {

	}

	/** 吨位 **/
	public static class VEHICLE_LOAD {

	}

	/** 车长 **/
	public static class VEHICLE_LENGTH {

	}

	/** 消费类型 **/
	public static class COST_TYPE {

		/** 消费 **/
		public static final int CONSUMPTION = 1;

		/** 充值 **/
		public static final int TOP_UP = 2;

	}

	/** 密码类型 **/
	public static class PASSWORD_TYPE {

		/** 支付密码错误类型 :1 */
		public static final int PAY_ERR_TYPE_PASS = 1;

		/** 修改密码错误类型:2 */
		public static final int MODIFY_ERR_TYPE_PASS = 2;

		/** 修改密码成功类型:3 */
		public static final int MODIFY_SUCESS_TYPE_PASS = 3;

	}

	/** 线路类型 **/
	public static class LINE_TYPE {

		/** 车源线路 :1 */
		public static final int CARS_LINE_TYPE = 1;

		/** 货源线路:2 */
		public static final int GOODS_LINE_TYPE = 2;

	}

	/** 投诉类型（其他类） **/
	public static class COMPLAINT_OTHER_TYPE {

		/** 充值 **/
		public static final int TOP_UP = 1;

		/** 提现 **/
		public static final int WITHDRAWAL = 2;

		/** 认证 **/
		public static final int AUTHORIZED = 3;

		/** 数据丢失 **/
		public static final int DATA_LOSS = 4;

		/** 红包未到帐 **/
		public static final int ENVETOPE_ACCOUNT = 5;

		/** 积分未到帐 **/
		public static final int INTEGRAL_ACCOUNT = 6;

		/** 其他 **/
		public static final int OTHERS = 7;

	}

	/** 投诉类型（订单类：货主） **/
	public static class COMPLAINT_ORDER_GOOD_TYPE {

	}

	/** 投诉类型（订单类：车主） **/
	public static class COMPLAINT_ORDER_CAR_TYPE {

	}

	/** 被投诉方用户类型 **/
	public static class COMPLAINT_USER_TYPE {

		/** 车主 **/
		public static final int COMPLAINT_CAR_PERSON = 1;

		/** 货主 **/
		public static final int COMPLAINT_GOOD_PERSON = 2;

	}

	/** 建议类型 **/
	public static class ADVISE_TYPE {

	}

	/** 建议状态 **/
	public static class ADVISE_STATUS {

	}

	/** 投诉对象类型 **/
	public static class COMPLAINT_OBJECT_TYPE {

		/** 订单类 **/
		public static final int ORDER = 1;

		/** 其他类 **/
		public static final int OTHERS = 2;

	}

	/** 判断货源是否需要模糊化 **/
	public static class IS_DISPLAY {

		/** 不需要模糊化 **/
		public static final int DISPLAY_NONE = 0;

		/** 需要模糊化 **/
		public static final int DISPLAY_BLOCK = 1;

	}

	/** 积分兑换处理状态 **/
	public static class EXCHANGE_STS {

		/** 待处理 **/
		public static final int PENDING = 0;

		/** 处理中 **/
		public static final int BEING_PRO = 1;

		/** 已完成 **/
		public static final int FINISH = 2;

		/** 处理失败 **/
		public static final int DEAL_FALI = 3;

	}

	/** 费用信息处理状态 **/
	public static class INFO_FEES_STATE {

		/** 预付 **/
		public static final int PREPAY = 1;

		/** 申请退回 **/
		public static final int APPLY_FOR_RETURN = 2;

		/** 申请收款 **/
		public static final int APPLY_FOR_CREDIT = 3;

		/** 支付完成 **/
		public static final int PAY_TO_COMPLETE = 4;

		/** 支付完成（仲裁） **/
		public static final int PAY_TO_COMPLETE_ARBITRAMENT = 5;

		/** 退回（仲裁） **/
		public static final int APPLY_FOR_RETURN_ARBITRAMENT = 6;

		/** 仲裁中 **/
		public static final int ARBITRAMENTING = 7;

		/** 已收款 **/
		public static final int MONEY_RECEIPT = 8;

		/** 已退款 **/
		public static final int HAVE_REFUND = 9;

		/** 待支付 **/
		public static final int WAIT_PAY = 10;

	}

	/** 订单交易类型 **/
	public static class ORDER_TRADE_TYPE {

		/*** 普通交易 */
		public static final int NORMAL_TRADE = 1;

		/*** 担保交易 */
		public static final int ENSURE_TRADE = 2;

	}

	/** 信息费类型 **/
	public static class PAY_INFO_TYPE {

		/*** 信息费 */
		public static final int INFO_FEES = 1;

		/*** 放空费 */
		public static final int EMPTY_FEES = 2;

	}

	/** 信息费和放空费状态 **/
	public static class FEES_STATE {

		/** 待支付 **/
		public static final int DID_NOT_PAY = 1;

		/** 预付 **/
		public static final int PREPAY = 2;

		/** 已支付 **/
		public static final int PAY_COMPLETE = 3;

		/** 已退款 **/
		public static final int HAVE_REFUND = 4;

	}

	public static class AUTH_IMPORTANT {

		/** 非常紧急 **/
		public static final int VERY_URGENT = 1;

		/** 普通 **/
		public static final int GENERAL = 2;

	}

	public static class PAY_RECEIVE {

		/** 付款方 **/
		public static final int PAY = 1;

		/** 收款方 **/
		public static final int RECEIVE = 2;

	}

	/***
	 * ########################################上面安力未用#################################
	 */

	/**
	 * 系统数据状态
	 *
	 */
	public static class SYS_DATA_STATE {

		/** 无效 **/
		public static final int EFFECTIVE_NO = 0;

		/** 有效 **/
		public static final int EFFECTIVE_YES = 1;

	}

	/**
	 * APP登录类型 1：密码登录2验证码登录
	 *
	 */
	public static class APP_LOGIN_TYPE {

		/** 密码 **/
		public static final int PASSWORD_LOGIN = 1;

		/** 验证码 **/
		public static final int CODE_LOGIN = 2;

	}

	/**
	 * 验证码类型
	 *
	 */
	public static class SYS_CODE_TYPE {

		/** 注册验证码 **/
		public static final int REGISTER_CODE = 1;

		/** 登录密码重置验证码 **/
		public static final int LOGIN_RESET_CODE = 2;

		/** 支付密码 **/
		public static final int PAY_CODE = 3;

		/** 登录手机修改验证码 **/
		public static final int REGISTER_CODE_PHONE = 11;

		/** 登录验证码 **/
		public static final int LOGIN_CODE = 17;

		/** 现金打款支付密码失败超过3次清空失败次数 */
		public static final int PAY_CASH_RESET_CODE = 30;

	}

	/**
	 * 地理位置上传类型 （1、普通上传 2、订单上传 3、车辆上传、其他待定扩充）
	 */
	public static class SYS_LOCATION_UPLOAD_TYPE {

		/** 普通上传 **/
		public static final int CUSTOM_UPLOAD_TYPE = 1;

		/** 订单上传 **/
		public static final int ORDER_UPLOAD_TYPE = 2;

		/** 车辆上传 **/
		public static final int CAR_UPLOAD_TYPE = 3;

	}

	/** ####################################system 开始############################## */

	/**
	 * 短信发送标志 0：发送失败，1：成功、2：已读,3、已删除
	 */
	public static class SMS_SEND_FLAG {

		/** 发送失败 **/
		public static final int SEND_FLAG_0 = 0;

		/** 成功 **/
		public static final int SEND_FLAG_1 = 1;

		/** 已读 **/
		public static final int SEND_FLAG_2 = 2;

		/** 已删除 **/
		public static final int SEND_FLAG_3 = 3;

	}

	/**
	 * 短信修改标志 1：修改消息为已读状态2：删除短信
	 */
	public static class SMS_OBJECT_TYPE {

		/** 修改消息为已读状态 **/
		public static final int OBJECT_TYPE_1 = 1;

		/** 删除短信 **/
		public static final int OBJECT_TYPE_2 = 2;

	}

	/**
	 * 密码记录次数类型 密码类型1:支付密码错误 2:修改密码错误 次数类型 3：密码修改成功类型
	 */
	public static class PWD_TYPE {

		/** 支付密码错误 **/
		public static final int PWD_PAY_ERR = 1;

		/** 修改密码错误 **/
		public static final int PWD_MODIFY_ERR = 2;

		/** 密码修改成功类型 **/
		public static final int PWD_SUC = 3;

	}

	/**
	 * 评价用户类型（定义） 1评价的是车主（货主是评价人）2：评价的是货主(车主是评价人)
	 */
	public static class COMMENT_TYPE {

		/** 评价的是车主 （货主是评价人） **/
		public static final int COMMENT_CAR_USER = 1;

		/** 评价的是货主 (车主是评价人) **/
		public static final int COMMENT_GOOD_USER = 2;

	}

	/**
	 * 投诉类型 1：订单投诉2：其他投诉
	 */
	public static class COMPLAIN_TYPE {

		/** 订单投诉 **/
		public static final int COMPLAIN_1 = 1;

		/** 其他投诉 **/
		public static final int COMPLAIN_2 = 2;

	}

	/**
	 * 投诉用户类型主题 1：投诉车主2：投诉货主
	 */
	public static class COMPLAIN_ZHU_TI {

		/** 投诉车主 **/
		public static final int COMPLAIN_1 = 1;

		/** 投诉货主 **/
		public static final int COMPLAIN_2 = 2;

	}

	/**
	 *
	 * 投诉处理状态：
	 *
	 * 0:未处理状态、1:处理中状态、2:处理完成状态、3:关闭状态、4:已取消状态
	 **/
	public static class COMPLAINT_STATE {

		/** 未处理状态 **/
		public static final int NOT_DEAL = 0;

		/** 处理中状态 **/
		public static final int DEAL = 1;

		/** 处理完成状态 **/
		public static final int DEAL_SUCCESS = 2;

		/** 关闭状态 **/
		public static final int CLOSS = 3;

		/** 已取消状态 **/
		public static final int CANCEL = 4;

	}

	/**
	 *
	 * 建议处理状态：
	 *
	 * 0:未处理1：处理中2：已拒绝3：已采纳
	 **/
	public static class ADVICE_STATE {

		/** 未处理状态 **/
		public static final int NOT_DEAL = 0;

		/** 处理中状态 **/
		public static final int DEAL = 1;

		/** 已拒接 **/
		public static final int DEAL_REF = 2;

		/** 已采纳 **/
		public static final int DEAL_REC = 3;

	}

	/**
	 * 评价项目级别描述（定义） 每个级别总共5分
	 *
	 * 0-1 差 2-3 一般 4-5好
	 *
	 *
	 */
	public static class COMMENT_LEVEl_DESC {

		/** 好 **/
		public static final String LEVEl_DESC_1 = "好";

		/** 一般 **/
		public static final String LEVEl_DESC_2 = "一般";

		/** 差 **/
		public static final String LEVEl_DESC_3 = "差";

	}

	/**
	 * 被邀请用户状态 0:未注册、1；已注册 、2；已送完红包
	 *
	 *
	 */
	public static class INVITE_RESULT {

		/** 未注册 **/
		public static final int RESULT_0 = 0;

		/** 已注册 **/
		public static final int RESULT_1 = 1;

		/** 已送完红包 **/
		public static final int RESULT_2 = 2;

	}

	/**
	 * 邀请模式 1:链接2:分享3:发短信
	 *
	 *
	 */
	public static class INVITE_MODE {

		/** 链接 **/
		public static final int MODE_1 = 1;

		/** 分享 **/
		public static final int MODE_2 = 2;

		/** 发短信 **/
		public static final int MODE_3 = 3;

	}

	/**
	 * 装货还是卸货 1、装货 2、卸货
	 */
	public static class GOODS_Z_OR_X {

		/** 装货 */
		public static final int GOODS_Z = 1;

		/** 卸货 */
		public static final int GOODS_X = 2;

	}

	/**
	 * 支付状态
	 *
	 */
	public static class PAY_STATE_AL {

		/** 已支付 **/
		public static final int PAY_YES = 1;

		/** 未支付 **/
		public static final int PAY_NO = 0;

	}

	/**
	 * 订单统计归类（定义） 统计类型 1：待处理2，进行中，3已完成（历史）
	 */
	public static class SYS_ORDER_TYPE {

		/** 待处理 **/
		public static final int DEAL_NOT = 1;

		/** 进行中 **/
		public static final int DEAL_DOINFG = 2;

		/** 已完成（历史） */
		public static final int DEAL_END = 3;

	}

	public static class ORDER_TIME {

		/** 一个小时内 **/
		public static final int ONE_HOURS = 1;

		/** 12个小时内 **/
		public static final int TWELVE_HOURS = 2;

		/** 24小时内 **/
		public static final int TWENTY_FOUR_HOURS = 3;

	}

	public static class LOAD_TIME {

		/** 今天 **/
		public static final int TODAY = 1;

		/** 明天 **/
		public static final int TOMORROW = 2;

		/** 后天 **/
		public static final int AFTER_TOMORROW = 3;

	}

	/**
	 * 租户有效状态
	 *
	 * @author zhouchao
	 *
	 */
	public static class SYS_TENANT_DEF_STATUS {

		public static final int IS_VALID = 1;

		public static final int NO_VALID = 0;

	}

	/**
	 * 定时订单状态
	 */
	public static class ORDER_PLAN_STATE {

		/** 1:有效；2失效 */
		public static final int AVAILABILITY = 1;

		public static final int INVALID = 2;

	}

	/**
	 * 审核基本流程状态
	 */
	public static class EXPENSE_STATE {

		/**
		 * 待审核
		 */
		public static final int CHECK_PENDING = 0;

		/**
		 * 审核中
		 */
		public static final int AUDIT = 1;

		/**
		 * 审核不通过
		 */
		public static final int TURN_DOWN = 2;

		/**
		 * 已完成
		 */
		public static final int END = 3;

		/**
		 * 取消
		 */
		public static final int CANCEL = 8;

	}

	/**
	 * 是否认证1、未认证 2、已认证 3、认证失败
	 *
	 */
	public static class AUTH_STATE {

		/** 未认证 **/
		public static final int AUTH_STATE1 = 1;

		/** 已认证 **/
		public static final int AUTH_STATE2 = 2;

		/** 认证失败 **/
		public static final int AUTH_STATE3 = 3;

	}

	public static class USER_OIL_flow {

		/**
		 * 未到期
		 */
		public static final int STATS0 = 0;

		/**
		 * 已到期
		 */
		public static final int STATS1 = 1;

		/**
		 * 未到期转已到期失败
		 */
		public static final int STATS2 = 2;

	}

	/**
	 * 自动审核/人工审核状态
	 */
	public static class AUTO_AUDIT {

		public static final int AUTO_AUDIT0 = 0;// 需要人工审核

		public static final int AUTO_AUDIT1 = 1;// 自动审核通过

	}

	public static class IS_AUTH {

		public static final int IS_AUTH0 = 0;// 否

		public static final int IS_AUTH1 = 1;// 是

	}

	public static class SALARY_PATTERN {

		public static final int NORMAL = 1;// 普通

		public static final int MILEAGE = 2;// 里程

		public static final int TIMES = 3;// 按趟

	}

	public static class APPLY_STATE {

		public static final int APPLY_STATE0 = 0;// 0 : 处理中

		public static final int APPLY_STATE1 = 1;// 1 : 已通过

		public static final int APPLY_STATE2 = 2;// 2：被驳回

		public static final int APPLY_STATE3 = 3;// 3：平台仲裁中

		public static final int APPLY_STATE4 = 4;// 4：平台仲裁通过

		public static final int APPLY_STATE5 = 5;// 5：平台仲裁未通过

		public static final int APPLY_STATE6 = 6;// 6：失效

	}

	public static class VER_STATE {

		public static final int VER_STATE_N = 0;// 0 : 不可审核数据，只用做历史作用

		public static final int VER_STATE_Y = 1;// 1 : 可以审核数据

		public static final int VER_STATE_HIS = 9;// 9:历史档案状态。

	}

	/**
	 * 服务商类型
	 */
	public static class SERVICE_BUSI_TYPE {

		/** 1.油站 **/
		public static final int OIL = 1;

		/** 2.维修保养 **/
		public static final int REPAIR = 2;

		/** 3.ETC **/
		public static final int ETC = 3;

		/** 违章代缴服务商 */
		public static final int VR = 4;

		/** 保险服务商 **/
		public static final int INS = 5;

		/** 油卡供应商 **/
		public static final int OIL_CARD = 100;

	}

	/**
	 * 异常来源
	 */
	public static class SOURCE_PROBLEM {

		/**
		 * 内部登记
		 */
		public static final int INSIDE = 0;

		/**
		 * 外部推送
		 */
		public static final int PUSH = 1;

	}

	/**
	 * 确认差异枚举
	 *
	 * @author EditTheLife
	 */
	public static class DIFF_TYPE {

		/** 对账差异 */
		public static final int BILL_DIFF = 1;

		/** KPL差异 */
		public static final int KPL_DIFF = 2;

		/** 油价差异 */
		public static final int OIL_FEE_DIFF = 3;

		/** 开单差异 */
		public static final int BILLING_DIFF = 4;

		/** 其他差异 */
		public static final int OTHER_DOFF = 5;

	}

	/**
	 * 服务商审核状态
	 */
	public static class CUSTOMER_AUTH_STATE {

		/** 1.未认证 **/
		public static final int WAIT_AUTH = 1;

		/** 2.已认证 **/
		public static final int AUTH_PASS = 2;

		/** 3.认证失败 **/
		public static final int AUTH_FAIL = 3;

	}

	/**
	 * 车队注册 审核状态
	 */
	public static class REGISTER_AUDIT_STATE {

		public static final Integer WAIT_AUDIT = 1;

		public static final Integer AUDIT_PASS = 2;

		public static final Integer AUDIT_FAIL = 3;

	}

	/**
	 * 车队创建状态
	 */
	public static class TENANT_BUILD_STATE {

		/**
		 * 未创建
		 */
		public static final Integer UNBUILT = 1;

		/**
		 * 创建中
		 */
		public static final Integer BUILDING = 2;

		/**
		 * 已创建
		 */
		public static final Integer BUILT = 3;

	}

	public static class STAFF_STATE {

		/** 0：已删除 **/
		public static final int DELETE = 0;

		/** 1：未删除 **/
		public static final int NO_DELETE = 1;

	}

	/**
	 * 账单核销状态
	 *
	 * @author zhouJ
	 */
	public static class FINANCE_STS {

		/** 未核销 */
		public static final int NOT_CHECK = 0;

		/** 已核销 */
		public static final int YET_CHECK = 1;

		/** 部分核销 */
		public static final int PART_CHECK = 2;

		/** 已开票 */
		public static final int WRIT = 3;

	}

	/**
	 * 资金风控一级类别
	 *
	 * @author wangxinliang
	 *
	 */
	public static class PAY_FEE_LIMIT_TYPE {

		/** 1：订单支出 **/
		public static final int ORDER_PAYMENT_1 = 1;

		/** 2：司机工资 **/
		public static final int DRIVER_SALARY_2 = 2;

		/** 3：会员消费 **/
		public static final int MEMBER_CONSUME_3 = 3;

		/** 4：自动打款 **/
		public static final int AUTO_PAYMENT_4 = 4;

	}

	/**
	 * 资金风控二级类别
	 *
	 * @author wangxinliang
	 */
	public static class PAY_FEE_LIMIT_SUB_TYPE {

		/** 1：中标价上限 **/
		public static final int MAX_BID_AMOUNT_101 = 1;

		/** 2：成本异常上限 **/
		public static final int MAX_COST_EXCEPT_102 = 2;

		/** 3：收入异常上限 **/
		public static final int MAX_INMOME_EXCEPT_103 = 3;

		/** 4：司机借支上限 **/
		public static final int MAX_DRIVER_LOAN_104 = 4;

		/** 5：招商车费用补款上限 **/
		public static final int MAX_RENTED_CAR_SUBSIDY_105 = 5;

		/** 6：司机基本工资上限 **/
		public static final int MAX_DRIVER_SALAR_201 = 6;

		/** 7：基本模式补贴上限 **/
		public static final int MAX_BASIC_MODE_SUBSIDY_202 = 7;

		/** 8：自有车司机总工资上限 **/
		public static final int MAX_OWN_DRIVER_TOTAL_SALARY_203 = 8;

		/** 9：合作个体户总工资上限 **/
		public static final int MAX_PERSON_PARTNER_TOTAL_SALARY_204 = 9;

		/** 10：里程模式补贴上限 **/
		public static final int MAX_MILE_MODEL_SUBSIDY_205 = 10;

		/** 11：扫码加油金额上限 **/
		public static final int MAX_SCAN_CODE_ADD_OIL_AMT_301 = 11;

		/** 12：扫码加油次数上限 **/
		public static final int MAX_SCAN_CODE_ADD_OIL_TIMES_302 = 12;

		/** 13：自动打款 **/
		public static final int AUTO_TRANSFER_401 = 13;// 是否自动打款

		public static final int OWN_CAR_TRANSFER_SALARY_402 = 14;// 自有车运费

		public static final int DRIVER_SARALY_403 = 15;// 司机工资

		public static final int OIL_FEE_IS_BILL_404 = 16;// 自有车油费是否开票 1-是，0-否

		public static final int OIL_FEE_IS_BILL_405 = 405;// 自动支付虚拟油加油费用

		public static final int OIL_FEE_IS_BILL_406 = 406;// 自动支付维修资金维修费用 1-是，0-否

		public static final int OIL_FEE_IS_BILL_407 = 407;// 是否需要支付密码 1-是，2-否

		public static final int AUTO_PAY_BEIDOU_FEE_408 = 408;// 自动支付北斗费用 1-是，其他-否

		public static final int OIL_CARD_DEPOSIT_303 = 80;// 油卡押金

	}

	/**
	 * 服务商站点邀请审核
	 */
	public static class INVITE_AUTH_STATE {

		/** 1：待处理 **/
		public static final int WAIT = 1;

		/** 2：已通过 **/
		public static final int PASS = 2;

		/** 3： 被驳回 **/
		public static final int FAIL = 3;

	}

	public static class APPLY_TYPE {

		// 类型，1 会员 2 车辆
		public static final int USER = 1;

		public static final int VEHICLE = 2;

	}

	/**
	 * 登录渠道 1-PC 2- APP,3-微信，4-WAP
	 *
	 * @author zhihong
	 *
	 */
	public static class LOGIN_CHANNEL {

		public static final int PC_1 = 1;

		public static final int APP_2 = 2;

		public static final int WX_SERVICE_3 = 3;

		public static final int WX_TENANT_4 = 4;

		public static final int PC_OBMS_5 = 5;

		public static final int PC_SERVICE_6 = 6;

		public static final int WX_RECEIVER_7 = 7;

		public static final int XZH_956 = 956;

	}

	/**
	 * 维修审核状态
	 */
	public static class REPAIR_AUTH {

		/** 待审核 */
		public static final int WAIT_AUTH = 1;

		/** 审核中 */
		public static final int AUTH = 2;

		/** 已通过 */
		public static final int PASS = 3;

		/** 已驳回 */
		public static final int FAIL = 4;

	}

	/***
	 * 是否共享
	 */
	public static class SHARE_FLG {

		/** 否 */
		public static final int NO = 0;

		/** 是 */
		public static final int YES = 1;

	}

	/**
	 * 待办事项枚举
	 *
	 * @author EditTheLife
	 */
	public static class WEB_AUFGABE {

		/** 待付款 */
		public static final int WAIT_PAY = 1;

		/** 待转成本订单 */
		public static final int WAIT_TRANSFER_ORDER = 2;

		/** 回单审核 */
		public static final int RECEIPT_AUDIT = 3;

		/** 订单异常 */
		public static final int ORDER_ABNORMAL = 4;

		/** 收入异常 */
		public static final int INCOME_PROBLEM = 5;

		/** 成本异常 */
		public static final int COST_PROBLEM = 6;

		/** 车辆审核 */
		public static final int VEHICLE_AUDIT = 7;

		/** 司机审核 */
		public static final int DRIVER_AUDIT = 8;

		/** 服务商审核 */
		public static final int SERVICE_AUDIT = 9;

	}

	/***
	 * 组织状态
	 */
	public static class ORG_STATE {

		/** 有效 */
		public static final int ORG_STATE1 = 1;

		/***
		 * 无效
		 */
		public static final int ORG_STATE0 = 0;

	}

	/**
	 * 车辆绑定线路状态
	 */
	public static class VEHICLE_LINE_REL_STATE {

		/**
		 * 有效
		 */
		public static final int LINE_STATE1 = 1;

		/***
		 * 无效
		 */
		public static final int LINE_STATE0 = 0;

	}

	/**
	 * 开票状态
	 */
	public static class INVOICING_STATUS {

		/**
		 * 可开票
		 */
		public static final int INVOICING_STATUS1 = 1;

		/**
		 * 已开票
		 */
		public static final int INVOICING_STATUS2 = 2;

		/**
		 * 不可申请开票
		 */
		public static final int INVOICING_STATUS3 = 3;

		/**
		 * 不可申请开票
		 */
		public static final int INVOICING_STATUS4 = 4;

	}

	/**
	 * 票据状态
	 */
	public static class BILL_STATUS {

		/**
		 * 开票中
		 */
		public static final int BILL_STATUS0 = 0;

		/**
		 * 已开票
		 */
		public static final int BILL_STATUS1 = 1;

		/**
		 * 已收票
		 */
		public static final int BILL_STATUS2 = 2;

	}

	/**
	 * 油卡类型
	 */
	public static class OIL_CARD_TYPE {

		/**
		 * 供应商油卡
		 */
		public static final int SERVICE = 1;

		/**
		 * 自购卡
		 */
		public static final int OWN = 2;

		/**
		 * 客户卡
		 */
		public static final int CUSTOMER = 3;

	}

	/**
	 * 结算方式
	 * <p>
	 * Title: BALANCE_TYPE
	 * </p >
	 * <p>
	 * Description:
	 * </p >
	 *
	 * @author hp
	 * @date 2018-08-23
	 */
	public static class BALANCE_TYPE {

		/**
		 * 预付全款
		 */
		public static final int PRE_ALL = 1;

		/**
		 * 预付+尾款账期
		 */
		public static final int PRE_AFTER = 2;

		/**
		 * 预付+尾款月结
		 */
		public static final int PRE_AFTER_MONTH = 3;

	}

	/**
	 * 违章因公因私类型
	 */
	public static class VIOLATION_TYPE {

		/**
		 * 因公违章
		 */
		public static final Integer FOR_PUBLIC = 1;

		/**
		 * 因私违章
		 */
		public static final Integer FOR_PRIVATE = 2;

	}

	/**
	 * 违章分类
	 */
	public static class VIOLATION_CATEGORY {

		/**
		 * 电子眼
		 */
		public static final Integer CATEGORY_1 = 1;

		/**
		 * 现场单（文书）
		 */
		public static final Integer CATEGORY_2 = 2;

		/**
		 * 已处理未缴费
		 */
		public static final Integer CATEGORY_3 = 3;

	}

	/**
	 * 违章代办
	 */
	public static class VIOLATION_PROCESS_STATUS {

		/**
		 * 可代码
		 */
		public static final Integer CAN = 1;

		/**
		 * 不可代办
		 */
		public static final Integer CAN_NOT = 0;

	}

	/**
	 * 违章订单是否需要驾驶证处理（0、不需要(默认null也是)；1、需要）
	 */
	public static class VIOLATION_DRIVE_NO {

		public static final String DRIVE_NO_0 = "0";

		public static final String DRIVE_NO_1 = "1";

	}

	/**
	 * 违章记录状态（0、未处理；1、处理中；2、已完成）
	 */
	public static class VIOLATION_RECORD_STATE {

		public static final Integer NOT_HAND = 0;

		public static final Integer IN_HAND = 1;

		public static final Integer FINISH = 2;

	}

	/**
	 * 自有车违章订单状态 1待审核->2待付款->3处理中->4已完成/5已撤销/6已退单/7失效/8审核不通过 外调车违章订单状态
	 * 2待付款->3处理中->4已完成/5已撤销/6已退单/7失效
	 */
	public static class VIOLATION_ORDER_STATE {

		public static final int VIOLATION_ORDER_STATE1 = 1;

		public static final int VIOLATION_ORDER_STATE2 = 2;

		public static final int VIOLATION_ORDER_STATE3 = 3;

		public static final int VIOLATION_ORDER_STATE4 = 4;

		public static final int VIOLATION_ORDER_STATE5 = 5;

		public static final int VIOLATION_ORDER_STATE6 = 6;

		public static final int VIOLATION_ORDER_STATE7 = 7;

		public static final int VIOLATION_ORDER_STATE8 = 8;

	}

	/**
	 * 违章订单审核状态 1审核通过 2驳回
	 */
	public static class VIOLATION_AUDIT_STATE {

		public static final int VIOLATION_PASS = 1;

		public static final int VIOLATION_REJECT = 2;

	}

	/**
	 * 发送请求 1未发送 2已经发送
	 */
	public static class VIOLATION_SEND_STATE {

		public static final int SEND_NO = 1;

		public static final int SEND_YES = 2;

	}

	/**
	 * 违章订单支付结果 1成功 2失败
	 */
	public static class VIOLATION_RESCODE {

		public static final int RESCODE_YES = 1;

		public static final int RESCODE_NO = 2;

	}

	/**
	 * 违章支付类型 1车队支付;2司机支付
	 */
	public static class VIOLATION_PAYMENT_TYPE {

		public static final int TENANT_TYPE = 1;

		public static final int DRIVER_TYPE = 2;

	}

	/**
	 * 违章订单退款情况 0初始状态;1未退款;2已退款
	 */
	public static class VIOLATION_ORDER_MONEY_BACK {

		public static final int MONEY_BACK0 = 0;

		public static final int MONEY_BACK1 = 1;

		public static final int MONEY_BACK2 = 2;

	}

	/**
	 * 违章订单是否属于公司车 0不是;1是
	 */
	public static class VIOLATION_IS_COMPANY {

		public static final String COMPANY_NO = "0";

		public static final String COMPANY_YES = "1";

	}

	/**
	 * 违章订单取消类型 1违章已经完成 2违章价格报价变动 3订单已经已经超过有效期 4服务商订单失效
	 */
	public static class VIOLATION_ORDER_CANCEL_TYPE {

		public static final String CANCEL_TYPE_1 = "1";

		public static final String CANCEL_TYPE_2 = "2";

		public static final String CANCEL_TYPE_3 = "3";

		public static final String CANCEL_TYPE_4 = "4";

	}

	/**
	 * 油卡充值类型 1：预付款 2：充值
	 */
	public static class OIL_TYPE {

		public static final int OIL_TYPE1 = 1;

		public static final int OIL_TYPE2 = 2;

	}

	/**
	 * 资金票据类型 0：不开票 1：承运商开票 2 ：平台票 3：服务费票据
	 */
	public static class FUNDS_IS_NEED_BILL {

		public static final int FUNDS_IS_NEED_BILL0 = 0;

		public static final int FUNDS_IS_NEED_BILL1 = 1;

		public static final int FUNDS_IS_NEED_BILL2 = 2;

		public static final int FUNDS_IS_NEED_BILL3 = 3;

	}

	/**
	 * 售前与售后部门KEY
	 */
	public static class SALE_TYPE_NAME {

		public static final String SALE_BEFORE_ORG = "SALE_BEFORE_ORG";// 售前

		public static final String SALE_END_ORG = "SALE_END_ORG";// 售后

		public static final String SALE_BEF = "BEF";// 售前

		public static final String SALE_END = "END";// 售后

	}

	/**
	 * 页面上展示的导入明细状态
	 *
	 * @author tengfy
	 */
	public static class TAB_RESULT_DETAIL_STATE {

		/**
		 * 导入中
		 */
		public static final int TAB_IMPORT_DETAIL_PROCESSING = 1;

		/**
		 * 导入成功
		 */
		public static final int TAB_IMPORT_DETAIL_SUCCEESS = 2;

		/**
		 * 导入失败
		 */
		public static final int TAB_IMPORT_DETAIL_FAIL = 3;

		/** 对应数据库明细状态（导入中）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> TAB_IMPORT_DETAIL_PROCESSING_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_DETAIL_STATE.CHECK_HOURSE_SUCCEESS);
			}
		};

		/** 对应数据库明细状态（导入成功）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> TAB_IMPORT_DETAIL_SUCCEESS_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_DETAIL_STATE.IMPORT_SUCCEESS);
			}
		};

		/** 对应数据库明细状态（导入失败）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> TAB_IMPORT_DETAIL_FAIL_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_DETAIL_STATE.CHECK_HOURSE_FAIL);
				add(IMPORT_RESULT_DETAIL_STATE.IMPORT_FAIL);
			}
		};

		/**
		 * 页面上展示的导入明细状态
		 */
		@SuppressWarnings("serial")
		public static final Map<Integer, List<Integer>> RESULT_DETAIL_PAGE_REL_DATABASE = new HashMap<Integer, List<Integer>>() {
			{
				put(TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_PROCESSING,
						TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_PROCESSING_LIST);
				put(TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_SUCCEESS,
						TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_SUCCEESS_LIST);
				put(TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_FAIL,
						TAB_RESULT_DETAIL_STATE.TAB_IMPORT_DETAIL_FAIL_LIST);
			}
		};

	}

	/**
	 * 导入配置表的状态 状态，0待处理，1入库处理中，2入库处理成功，3入库处理失败，4导入处理中，5导入处理成功，6导入处理失败
	 *
	 * @author tengfy
	 */
	public static class IMPORT_RESULT_DETAIL_STATE {

		/**
		 * 校验成功
		 */
		public static final int CHECK_HOURSE_SUCCEESS = 1;

		/**
		 * 校验失败
		 */
		public static final int CHECK_HOURSE_FAIL = 2;

		/**
		 * 导入处理成功
		 */
		public static final int IMPORT_SUCCEESS = 3;

		/**
		 * 导入处理失败
		 */
		public static final int IMPORT_FAIL = 4;

	}

	/**
	 * @author tengfy 导入页面的显示的状态 导入中 （待处理、校验处理中、校验处理成功、导入处理中、校验部分成功） 导入成功 （导入处理成功、）
	 * 导入失败（校验处理失败、导入处理失败、导入数据异常） 导入部分成功 （导入部分成功）
	 */
	public static class TAB_RESULT_STATE {

		/**
		 * 导入中
		 */
		public static final int TAb_IMPORT_PROCESSING = 1;

		/**
		 * 导入部分成功
		 */
		public static final int TAB_SUCCEESS_PART = 2;

		/**
		 * 导入成功
		 */
		public static final int TAB_IMPORT_SUCCEESS = 3;

		/**
		 * 导入失败
		 */
		public static final int TAb_IMPORT_FAIL = 4;

		/** 对应数据库状态（导入中）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> RESULT_STATE_PROCESSING_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_STATE.pending);
				add(IMPORT_RESULT_STATE.IN_HOURSE_PROCESSING);
				add(IMPORT_RESULT_STATE.IN_HOURSE_PART);
				add(IMPORT_RESULT_STATE.IN_HOURSE_SUCCEESS);
				add(IMPORT_RESULT_STATE.IMPORT_PROCESSING);
				add(IMPORT_RESULT_STATE.IN_HOURSE_SUCCEESS);
			}
		};

		/** 对应数据库状态（导入部分成功）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> RESULT_STATE_PART_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_STATE.IMPORT_SUCCEESS_PART);
			}
		};

		/** 对应数据库状态（导入成功）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> RESULT_STATE_SUCCEESS_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_STATE.IMPORT_SUCCEESS);
			}
		};

		/** 对应数据库状态（导入失败）的集合 */
		@SuppressWarnings("serial")
		public static final List<Integer> RESULT_STATE_FAIL_LIST = new ArrayList<Integer>() {
			{
				add(IMPORT_RESULT_STATE.IN_HOURSE_FAIL);
				add(IMPORT_RESULT_STATE.IMPORT_FAIL);
				add(IMPORT_RESULT_STATE.IMPORT_MES_EXP);
			}
		};

		/**
		 * Page background relationship 导入页面的显示的状态 导入中 （待处理、校验处理中、校验处理成功、导入处理中、校验部分成功）
		 * 导入成功 （导入处理成功、） 导入失败（校验处理失败、导入处理失败、导入数据异常） 导入部分成功 （导入部分成功）
		 */
		@SuppressWarnings("serial")
		public static final Map<Integer, List<Integer>> RESULT_PAGE_REL_DATABASE = new HashMap<Integer, List<Integer>>() {
			{
				put(TAB_RESULT_STATE.TAb_IMPORT_PROCESSING, TAB_RESULT_STATE.RESULT_STATE_PROCESSING_LIST);
				put(TAB_RESULT_STATE.TAB_SUCCEESS_PART, RESULT_STATE_PART_LIST);
				put(TAB_RESULT_STATE.TAB_IMPORT_SUCCEESS, RESULT_STATE_SUCCEESS_LIST);
				put(TAB_RESULT_STATE.TAb_IMPORT_FAIL, RESULT_STATE_FAIL_LIST);
			}
		};

	}

	/**
	 * 导入配置表的状态 状态，0待处理，1入库处理中，2入库处理成功，3入库处理失败，4导入处理中，5导入处理成功，6导入处理失败
	 *
	 * @author tengfy
	 */
	public static class IMPORT_RESULT_STATE {

		/**
		 * 待处理
		 */
		public static final int pending = 0;

		/**
		 * 校验处理中
		 */
		public static final int IN_HOURSE_PROCESSING = 1;

		/**
		 * 校验处理成功
		 */
		public static final int IN_HOURSE_SUCCEESS = 2;

		/**
		 * 校验处理失败
		 */
		public static final int IN_HOURSE_FAIL = 3;

		/**
		 * 导入处理中
		 */
		public static final int IMPORT_PROCESSING = 4;

		/**
		 * 导入处理成功
		 */
		public static final int IMPORT_SUCCEESS = 5;

		/**
		 * 导入处理失败
		 */
		public static final int IMPORT_FAIL = 6;

		/**
		 * 校验部分成功
		 */
		public static final int IN_HOURSE_PART = 7;

		/**
		 * 导入部分成功
		 */
		public static final int IMPORT_SUCCEESS_PART = 8;

		/**
		 * 导入数据异常
		 */
		public static final int IMPORT_MES_EXP = 9;

	}

	public static class PAY_TYPE_TXM {

		/**
		 * 批量付款
		 */
		public static final int PAY_TYPE_TXM1 = 1;

	}

	public static class PAYMANAGER_STATE {

		// 状态 （0待审核、1审核不通过、2付款中、3已付款,4审核中,5取消）
		public static final int PAYMANAGER_STATE0 = 0;

		public static final int PAYMANAGER_STATE1 = 1;

		public static final int PAYMANAGER_STATE2 = 2;

		public static final int PAYMANAGER_STATE3 = 3;

		public static final int PAYMANAGER_STATE4 = 4;

		public static final int PAYMANAGER_STATE5 = 5;

	}

	public static class PAYMANAGER_BILL {

		// 是否开票 0否 1是
		public static final int PAYMANAGER_BILL0 = 0;

		public static final int PAYMANAGER_BILL1 = 1;

	}

	public static class PASSWORD_TIPS {

		public static final String LOGIN_PASSWORD_RULE = "LOGIN_PASSWORD_RULE";

		public static final String PAY_PASSWORD_RULE = "PAY_PASSWORD_RULE";

	}

	public static class JUDEGE_AMOUNT {

		public static final String AMOUNT_YUAN = "AMOUNT_YUAN";

		public static final String AMOUNT_FEN = "AMOUNT_FEN";

		public static final String COMMISSION_AMOUNT = "COMMISSION_AMOUNT";

	}

	public static class RECORDING_TYPE {

		public static final String RECORDING_TYPE_NAME1 = "圈存";

		public static final String RECORDING_TYPE_NAME2 = "加油";

		public static final String RECORDING_TYPE_NAME3 = "IC卡消费";

		public static final int RECORDING_TYP1 = 1;// 充值

		public static final int RECORDING_TYP2 = 2;// 消费

	}

	public static class SERVIER_TYPE {

		public static final int SERVIER_TYPE1 = 1;// 中石油

		public static final int SERVIER_TYPE2 = 2;// 中石化

	}

	public static class TITIE_NAME {

		public static final String TITIE_NAME = "客户名称,开户省份,卡号,小计,总计";

		public static final String SERVICE_ETC_BILL_TITLE_NAME = "公司名称,卡类型,站点名称,账单编号,账单开始日期,账单结束日期,最后缴费日,通行次数,通行费金额,服务费金额,滞纳金";

		public static final String SERVICE_ETC_INTF_TITLE_NAME = "etc卡号,车牌号,消费金额,扣款金额,优惠,交易地点,入站名,出站名,入站时间,出站时间,账单编号,账单开始日期,账单结束日期";

		public static final String FLEET_ETC_BILL_TITLE_NAME = "车牌,消费时间,消费金额,折后金额,利润,ETC供应商,支付卡卡号,交易地点,与ETC供应商结算月份,扣取车主费用日期,是否扣取成功";

		public static final String ACCOUNT_STATEMENT_DETAILS_TITLE_NAME = "车牌号,司机姓名,司机手机,账单接收人,接收人手机,总费用";

	}

	// 查询银行账户获取余额或者是读取缓存（如果open是读取缓存）
	public static class BALANCE_CACHE_SWITCH {

		public static final String open = "1";

		public static final String close = "0";

	}

	/**
	 * 票据平台静态配置
	 *
	 * @author EditTheLife
	 */
	public static class BILL_FORM_STATIC {

		/** HA票据平台 **/
		public static final int BILL_HA = 1;

		/** 56K票据平台 **/
		public static final int BILL_56K = 2;

		/** luge票据平台 **/
		public static final int BILL_LUGE = 3;

	}

	/**
	 * 密码记录是否 1:今天不在输入2:今天还需要输入
	 */
	public static class PWD_STATUS {

		/** 今天不在输入 **/
		public static final int PWD_STATUS1 = 1;

		/** 今天还需要输入 **/
		public static final int PWD_STATUS2 = 2;

	}

	/**
	 * 付款 1:审核通过后调用保存2:审核前置校验
	 */
	public static class RULE_STATUS {

		/** 审核通过后调用保存2 **/
		public static final int RULE_STATUS1 = 1;

		/** 审核前置校验 **/
		public static final int RULE_STATUS2 = 2;

	}

	/**
	 * 审核 1:审核通过2:审核不通过
	 */
	public static class CHOOSE_RESULT {

		/** 审核通过 **/
		public static final int CHOOSE_RESULT1 = 1;

		/** 审核不通过 **/
		public static final int CHOOSE_RESULT2 = 2;

	}

	/**
	 * 货物类型
	 *
	 */
	public static class GOODS_TYPE {

		/** 普货 **/
		public static final int COMMON_GOODS = 1;

		/** 危险品 **/
		public static final int DANGER_GOODS = 2;

	}

	/**
	 * 是否快速创建
	 */
	public static class IS_AUTH_SUCC {

		/** 人工审核通过 **/
		public static final Integer YES_1 = 1;

		/** 审核不通过 **/
		public static final Integer NOT_2 = 2;

		/** 自动审核通过 **/
		public static final Integer YES_3 = 3;

	}

	public static class MATURITY {

		/** 确认收款延期时间5天 **/
		public static final String MATURITY1 = "MATURITY1";

		/** 确认收款延期时间7天 **/
		public static final String MATURITY2 = "MATURITY2";

	}

	/**
	 * 现金付款返回状态
	 */
	public static class PAY_CASH_TYPE {

		/** 司机信息不完整 **/
		public static final String PAY_TYPE1 = "1";

		/** 车辆信息不完整 **/
		public static final String PAY_TYPE2 = "2";

		/** 可以付款 **/
		public static final String PAY_TYPE3 = "3";

	}

	/**
	 * 油卡充值账户资金来源
	 */
	public static class OIL_RECHARGE_SOURCE_TYPE {

		/** 返利充值 */
		public static final int SOURCE_TYPE1 = 1;

		/** 现金充值 */
		public static final int SOURCE_TYPE2 = 2;

		/** 授信充值 */
		public static final int SOURCE_TYPE3 = 3;

		/** 继承 */
		public static final int SOURCE_TYPE4 = 4;

		/** 抵扣票现金充值 */
		public static final int SOURCE_TYPE5 = 5;

		/** 转移账户充值 */
		public static final int SOURCE_TYPE6 = 6;

	}

	public static class OIL_RECHARGE_CLASS {

		/** 充值 */
		public static final int CLASS1 = 1;

		/** 提现 */
		public static final int CLASS2 = 2;

		/** 分配 */
		public static final int CLASS3 = 3;

		/** 撤回 */
		public static final int CLASS4 = 4;

	}

	/**
	 * 油卡充值账户操作类型
	 */
	public static class OIL_RECHARGE_BUSI_TYPE {

		/** 充值 */
		public static final int BUSI_TYPE1 = 1;

		/** 提现 */
		public static final int BUSI_TYPE2 = 2;

		/** 分配 */
		public static final int BUSI_TYPE3 = 3;

		/** 撤回 */
		public static final int BUSI_TYPE4 = 4;

		/** 加油 */
		public static final int BUSI_TYPE5 = 5;

		/** 返利 */
		public static final int BUSI_TYPE6 = 6;

		/** 还款 */
		public static final int BUSI_TYPE7 = 7;

		/** 移除-专票账户 */
		public static final int BUSI_TYPE8 = 8;

	}

	/**
	 * 母卡充值状态
	 *
	 */
	public static class OIL_RECHARGE_VERIFY_STATE {

		/** 待确认 */
		public static final int STATE0 = 0;

		/** 已完成 */
		public static final int STATE1 = 1;

		/** 已核销 */
		public static final int STATE2 = 2;

	}

	/**
	 * 油卡充值账户票据类型
	 *
	 */
	public static class OIL_RECHARGE_BILL_TYPE {

		/** 油票 */
		public static final int STATE1 = 1;

		/** 运输专票 */
		public static final int STATE2 = 2;

	}

	/**
	 * 票据平台静态配置
	 */
	public static class BILL_FORM {

		/** 56K票据平台 **/
		public static final String BILL_56K = "56K";

		/** 路歌票据平台 **/
		public static final String BILL_Luge = "luge";

	}

	/**
	 * @date 19:13 2019/8/2
	 * @description 预算结算表头
	 **/
	public static class BUDGET_SETT {

		// 运营管理部(唯一标识给前端)
		public static final String PARTNER_NAME = "partner";

		// 总部合计
		public static final Long HEADQUARTERS = -2L;

		public static final String HEADQUARTERS_NAME = "总部合计";

		// 自营运营区合计
		public static final Long OWN_OPERATE_SUM = -3L;

		public static final String OWN_OPERATE_SUM_NAME = "自营运营区合计";

	}

	/**
	 * @date 18:46 2019/8/14
	 * @description xzh预算明细业务类型
	 **/
	public static class XZH_BUSINESS_TYPE {

		// 散货业务
		public static final Long BULKLOAD = 1L;

		// 非散货业务
		public static final Long NO_BULKLOAD = 2L;

	}

	/**
	 * @date 19:13 2019/8/2
	 * @description xzh部门类型
	 **/
	public enum DEPARTMENT_TYPE {

		SPECIAL_DEPARTMENT(1, "公共关系及运力部"), FUNCTION_DEPARTMENT(2, "职能部门"), OWN_OPERATE(3, "自营区"), PARTNER_OPERATE(4,
				"合伙人运营区"),

		HEADQUARTERS(-1, "总部合计"), OWN_OPERATE_SUM(-2, "自营运营区合计");

		private Integer departmentType;

		private String departmentTypeDesc;

		DEPARTMENT_TYPE(Integer departmentType, String departmentTypeDesc) {
			this.departmentType = departmentType;
			this.departmentTypeDesc = departmentTypeDesc;
		}

		public Integer getDepartmentType() {
			return departmentType;
		}

		public void setDepartmentType(Integer departmentType) {
			this.departmentType = departmentType;
		}

		public String getDepartmentTypeDesc() {
			return departmentTypeDesc;
		}

		public void setDepartmentTypeDesc(String departmentTypeDesc) {
			this.departmentTypeDesc = departmentTypeDesc;
		}

	}

}
