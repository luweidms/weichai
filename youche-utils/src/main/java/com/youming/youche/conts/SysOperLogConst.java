package com.youming.youche.conts;

@Deprecated
public class SysOperLogConst {

	/**
	 * 操作的类型
	 *
	 * @author liyiye
	 */
	public enum OperType {
		Add(1, "新增"),
		Update(2, "更新"),
		Del(3, "删除"),
		Audit(4, "审核"),
		Remove(5, "移除"),
		Grant(6, "授权"), //员工授权
		WriteOff(7, "核销"),
		Enable(8,"启用"),
		Disable(9,"禁用"),
//		AuditMsg(10, "审核提醒"),
		BindCard(11,"绑定银行卡"),
		UnBindCard(12,"解绑银行卡"),
		PayOnline(13,"线上打款"),
		SetDefaultCard(14,"设定默认账户"),
		modiBranch(15,"修改支行"),
		quote(16,"报价"),
		AuditUser(17, ""),//审核提醒
		Idle(18, "闲置"),
		;

		// 定义私有变量
		private int code;
		private String name;

		// 构造函数，枚举类型只能为私有
		private OperType(int code, String name) {

			this.code = code;
			this.name = name;

		}

		public static OperType getOperType(int code) {
			for (OperType operType : OperType.values()) {
				if (operType.getCode() == code) {
					return operType;
				}
			}
			return null;
		}

		public int getCode() {
			return this.code;
		}

		public String getName() {
			return this.name;
		}
	}

	/**
	 * 定义日志操作的业务编码
	 * 100000 到199999 是基础数据
	 * 200000 到299999 是资金模块
	 * 300000 到399999 是订单模块
	 * 400000 到499999 是服务商模块
	 * 500000 到599999 是运营平台模块
	 * 600000 到699999 是服务商系统（违章服务商、乱七八糟的其他服务商）
	 *
	 * @author liyiye
	 */
	public enum BusiCode {

		/**
		 * 客户档案
		 */
		Customer(100000, "客户档案"),
		/**
		 * 客户线路
		 */
		CustomerLine(100001, "客户线路"),
		Address(1000011,"地址库"),
		/**
		 * 司机档案
		 */
		Driver(100002, "司机档案"),
		UserReceivcer(600010,"收款人管理"),
		/**
		 * 司机档案
		 */
		DriverOBMS(1000021, "司机档案"),
		/**
		 * 车辆档案
		 */
		Vehicle(100003, "车辆档案"),
		VehicleReg(1000031, "车辆档案已注册"),
		/**
		 * 挂车档案
		 */
		Trailer(100004, "挂车档案"),
		/**
		 * 员工档案
		 */
		Staff(100005, "员工档案"),
		/**
		 * 员工档案
		 */
		Entity(100006, "系统权限"),
		/**
		 * 组织架构
		 */
		Org(100007, "组织架构"),
		/**
		 * 微信管理
		 */
		Wechat(100008, "微信管理"),
		/**
		 * 车队信息
		 */
		Tenant(100009, "车队信息"),
		/**
		 * 会员权益
		 */
		UserCredit(100010, "会员权益"),
		/**
		 * 资金风控
		 */
		CapitalControl(100011, "资金风控"),
		/**
		 * 车队注册(运营后台)
		 */
		TenantRegister(100012, "车队注册"),

		/**
		 * 票据档案(运营后台)
		 */
		billPlatFrom(100013, "票据档案"),

		/**
		 * 平台账户(运营后台)
		 */
		platFromAcct(100014, "平台账户"),

		billRate(100015,"费率设置"),

		tenantRecommed(100016,"车队推荐"),

		/**
		 * 订单信息
		 */
		OrderInfo(300000, "订单信息"),
		/**
		 * 异常信息
		 */
		ProblemInfo(300001, "异常信息"),
		/**
		 * 时效罚款
		 */
		AgingInfo(300002, "时效罚款"),
		/**
		 * 时效罚款申诉
		 */
		AgingAppeal(300003, "时效罚款申诉"),

		/***
		 * 百世工单转订单
		 */
		workOrderInfo(300004, "百世工单"),

		/***
		 * 神汽维修保养
		 */
		serviceRepair(300005, "维修保养"),

		/***
		 * 轮胎
		 */
		tyreSettlementBill(300006, "轮胎"),

		/***
		 * 生成开票订单
		 */
		quickInvoiceOrder(300007, "生成开票订单"),

		/**
		 * 后服档案
		 */
		ServiceInfo(400000, "服务商档案"),
		AgentService(4000000, "代收服务商"),
		TenantAgentService(4000001, "代收服务商与车队关系"),
		/**
		 * 合作站点
		 */
		TenantProductRel(400001, "合作站点"),
		/**
		 * 油卡
		 */
		OilCard(400002, "油卡"),

		/**
		 * etc
		 */
		EtcCard(400003, "ETC"),
		/**
		 * etc
		 */
		RepairInfo(400004, "维修记录"),

		/**
		 * 合作站点
		 */
		ServiceProduct(400005, "站点管理"),

		/**
		 * 违章管理
		 */
		violationManage(400007, "违章管理"),
		/**
		 * 服务商订单
		 */
		ServiceOrderInfo(400008, "服务商订单"),

		/**
		 * 油卡充值
		 */
		OilEntity(230001, "油卡充值"),

		/**
		 * 确认收款
		 */
		DueOverdue(299999, "油卡充值"),

		/**
		 * 车主账户
		 */
		AccountQuery(21000026, "车主账户"),


		/**
		 * 油卡充值
		 */
		OilTurnOilEntity(230002, "油转油卡充值"),

		/**
		 * 现金打款
		 */
		Payoutchunying(230003, "现金打款"),

		/**
		 * 应收管理--订单
		 */
		IncomeManageOrder(230004, "应收管理--订单"),

		/**
		 * 应收管理--账单
		 */
		IncomeManageBill(230005, "应收管理--账单"),


		/**
		 * "服务商关系
		 */
		//TenantServiceRel(400009, "服务商关系"),
		/**
		 * 支付预付款
		 */
		PayAdvanceCharge(21000002, "支付预付款"),
		/**
		 * 撤单
		 */
		CancelTheOrder(220000011, "撤单"),
		/**
		 * 支付尾款
		 */
		PayForFinalCharge(21000014, "支付尾款"),
		/**
		 * 未到期转可用
		 */
		MarginTurnCash(21000003, "未到期转可用"),
		/**
		 *
		 */
		AdvanceExpire(21000006, "提前到期"),
		/**
		 * 异常补偿
		 */
		PayForException(21000015, "异常补偿"),
		/**
		 * 异常扣减
		 */
		PayForExceptionOut(21000018, "异常扣减"),
		/**
		 * 预支
		 */
		AdvancePayMarginBalance(21000011, "预支"),
		/**
		 * 转现
		 */
		SaveOilAndEtcTurnCash(21000025, "油和ETC转现"),
		/**
		 * 强制平账
		 */
		SaveForceZhangPing(21000026, "强制平账"),
		/**
		 * 支付到付款
		 */
		PayArriveCharge(22000028, "支付到付款"),
		/**
		 * 发放工资
		 */
		PaySalary(220000001, "发放工资"),
		/**
		 * 借支 21000021--21000029 预留借支
		 */
		StaffBorrow(21000021, "员工借支"),
		TubeBorrow(21000022, "车管借支"),
		DriverBorrow(21000023, "司机借支"),

		/**
		 * 报销
		 */
		TubeExpense(21000100, "车管报销"),
		DriverExpense(21000101, "司机报销"),

		/**
		 * 工资
		 */
		CmSalaryInfo(21001100, "工资"),

		/**
		 * 消费油
		 */
		PayForOilOut(21000012, "消费油"),
		/**
		 * 收入油费
		 */
		PayForOilIn(21000013, "收入油费"),
		/**
		 * 提现
		 */
		Withdrawals(21000004, "提现"),
		/**
		 * 回退
		 */
		BackRecharge(21000019, "回退"),

		EtcConsume(210010, "ETC消费"),

		payRepairComp(21100, "公司支付维修单"),

		attractCarFeeOp(21000020, "抵扣释放油卡押金"),

		repairFundOp(21101, "操作维修基金账户"),

		rechargeOp(21000000, "充值"),

		etcPayOp(220000012, "ETC自动充值"),

		deductibleOilFee(21102, "抵扣释放油卡押金"),

		entrustOp(21000001, "托管入账"),

		transferTrustOp(21000009, "托管出账"),

		payFinal(21000014, "支付经纪人"),

		incomeOilOp(21000013, "收入油费"),

		oilAndEtcSurplus(21103, "油卡结余和ETC结余"),

		repairFundTurnCash(22000015, "维修基金转现"),

		updateOrderPrice(220000010, "修改订单"),

		updateOilPrice(400006, "修改全国油价"),

	   reportTargetConfig(1000088, "运营报表月目标设置"),

	   serviceFeeVerification(21000030,"服务费核销审核"),

		serviceRefundVerification(21000031, "开票服务费核销"),//返现核销

		orderCostReport(22000022, "费用上报"),
		payManager(22000023, "付款管理"),
		applyOilCard(22000024, "申请油卡"),
		applyEtcCard(22000030, "申请ETC卡"),
		oilCardManager(22000026, "油卡管理"),
		myOilCardProduct(22000028, "油卡管理"),
		serviceInvitation(22000029, "油卡站点邀请"),
		rebateInfoLog(22000031, "发卡方返利"),

		etcBillManager(22000040, "ETC账单管理"),

		etcDeductView(22000050, "ETC消费记录管理"),

		payable(1000089, "应付逾期银行打款"),

		staffRefund(500001, "会员退款"),

		dealBankReconcilia(500003, "处理平安对账"),

		baseOpenBill(22000027, "票据订单管理"),
		applyOpenBill(22000025, "票据申请管理"),
		beidouPaymentRecord(22000030, "北斗缴费"),
		additionalFeeRePay(40001200, "附加运费"),

		quotation(600000, "报价"),

		voucherInfo(9000000, "代金券"),
		rechargeInfoRecord(9000001, "充值记录"),
		rechargeConsumeRecord(9000002, "油卡充值/消费(返利)记录"),
		serviceMonthBill(9000003, "后服消费月账单"),
		billApplyDetails(9000004, "账单申请开票明细表"),
		servicePorduct(9000006, "油卡站点操作"),
		OPENBILLDATE(66666666, "开票时间设置"),
		accountStatement(9000007, "招商车挂靠车对账单"),
		serviceBlanceConfig(4000010, "服务商预存"),
		masterCard(4000011, "母卡操作"),
		masterCardDetails(4000012, "母卡明细"),
		masterCardFlow(4000013, "母卡充值操作")
		;
	   // 定义私有变量
	    private int code;
	    private String name;

	    // 构造函数，枚举类型只能为私有
	    private BusiCode(int code,String name) {

	        this.code =code;
	        this.name=name;

	    }

	   public static BusiCode getBusiCode(int code) {
		   for (BusiCode busiCode : BusiCode.values()) {
			   if (busiCode.getCode() == code) {
				   return busiCode;
			   }
		   }
		   return null;
	   }

	   public int getCode(){
	    	return this.code;
	    }
	    public String getName(){
	    	return this.name;
	    }

	}

	public static void main(String[] args) {
		System.out.println(BusiCode.CustomerLine.getCode());
		System.out.println(BusiCode.CustomerLine.getName());
	}
}
