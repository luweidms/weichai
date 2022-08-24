package com.youming.youche.finance.provider.utils;

/**
 * ETC业务相关枚举
 */
public class EtcConsts {

	/**
	 * 账单状态
	 */
	public static class ETC_BILL_STATE {
		public static final int STS1 = 1;// 未支付
		public static final int STS2 = 2;// 已支付
		public static final int STS3 = 3;// 部分支付
		public static final int STS4 = 4;// 部分支付中
		public static final int STS5 = 5;// 支付中
		public static final int STS6 = 6;// 审核通过
		public static final int STS7 = 7;// 审核不通过
		public static final int STS8 = 8;// 待审核
	}

	/**
	 * 账单来源
	 */
	public static class ETC_BILL_SOURCE_TYPE {
		public static final int TYPE1 = 1;// 平台下发
		public static final int TYPE2 = 2;// 手工新增
	}
	
	/**
	 * 消费明细来源
	 */
	public static class ETC_SOURCE_TYPE {
		public static final int TYPE1 = 1;// 平台下发
		public static final int TYPE2 = 2;// 导入
	}
	
	/**
	 * 消费明细来源
	 */
	public static class UPDATE_BILL_STATE {
		public static final int STATE0 = 0;// 默认为更新
		public static final int STATE1 = 1;// 有更新
	}
	/**
	 * 消费明细扣费状态
	 */
	public static class ETC_CHARGING_STATE {
		public static final int STATE0 = 0;// 未扣费
		public static final int STATE1 = 1;// 扣费成功
		public static final int STATE2 = 2;// 扣费失败
		public static final int STATE3 = 3;// 反写成功
		public static final int STATE4 = 4;// 反写失败
	}
	
	/**
	 * 账单审核状态
	 */
	public static class ETC_AUDIT_STATE {
		public static final int STATE1 = 1;// 审核通过
		public static final int STATE2 = 2;// 审核不通过
	}
	/**
	 * 消费明细付款类型
	 */
	public static class ETC_PAYMENT_TYPE {
		public static final int TYPE1 = 1;// 预付费
		public static final int TYPE2 = 2;// 后付费
	}
	/**
	 * 是否可以审核 1可以 0不可以
	 */
	public static class IS_ALLOW_AUDIT {
		public static final int STATE1 = 1;// 可以
		public static final int STATE0 = 0;// 不可以
	}
}
