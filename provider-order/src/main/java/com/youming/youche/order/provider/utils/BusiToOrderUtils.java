package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilEntity;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BusiToOrderUtils {

	/**
	 * 业务反写订单数据
	 * @param inParam 入参集合
	 *
	 * 必传：用户ID：userId、手机号码：billId、业务ID：businessId、业务总金额：amount、资金渠道：vehicleAffiliation
	 *
	 * 业务：预付
	 * 需传：订单ID：orderId、
	 *
	 *
	 * @param rels 业务费用科目
	 * @throws Exception
	 */
	public List busiToOrder(Map<String,String> inParam,List<BusiSubjectsRel> rels){
    	long userId = DataFormat.getLongKey(inParam,"userId");
    	List results = new ArrayList();
    	if(userId==0L){
			throw new BusinessException("用户ID不能为空!");
    	}
    	long businessId = DataFormat.getLongKey(inParam,"businessId");
    	if(businessId==0L){
			throw new BusinessException("业务ID不能为空!");
    	}
    	String billId = DataFormat.getStringKey(inParam,"billId");
    	if(StringUtils.isEmpty(billId)){
    		throw new BusinessException("司机手机不能为空!");
    	}
    	String vehicleAffiliation = DataFormat.getStringKey(inParam,"vehicleAffiliation");
    	if(StringUtils.isEmpty(vehicleAffiliation)){
    		throw new BusinessException("资金渠道不能为空!");
    	}
    	long amount = DataFormat.getLongKey(inParam,"amount");

    	//数据锁定逻辑，避免不同方法操作同样数据
		// TODO: 2022/3/24 锁
		/*		boolean isLock = SysContexts.getLock(this.getClass().getName() + userId + vehicleAffiliation + businessId, 3, 5);
		if (!isLock)	//未或得资源锁，不做任何操作
		throw new BusinessException("账户资金操作中，请稍后重试!");*/
    	/*社会车、招商车对应订单逻辑-开始*/
    	//预付
   /* 	if(businessId== EnumConsts.PayInter.BEFORE_PAY_CODE){
    		BusiToOrderAbstract bto = new BeforePay();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//消费油
    	if(businessId==EnumConsts.PayInter.PAY_FOR_OIL_CODE){
    		BusiToOrderAbstract bto = new OilConsume();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//支付维修单
    	if(businessId==EnumConsts.PayInter.PAY_FOR_REPAIR){
    		BusiToOrderAbstract bto = new PayForRepair();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//尾款
    	if(businessId==EnumConsts.PayInter.PAY_FINAL){
    		BusiToOrderAbstract bto = new FinalPay();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//预支
    	if(businessId==EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE){
    		BusiToOrderAbstract bto = new PrePay();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//油卡结余和ETC结余
    	if(businessId==EnumConsts.PayInter.OIL_AND_ETC_SURPLUS){
    		BusiToOrderAbstract bto = new OilEtcTurnCash();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//油和ETC转现(功能化)
    	if(businessId==EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH){
    		BusiToOrderAbstract bto = new OilAndEtcTurnCash();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//维修基金转现
    	if(businessId==EnumConsts.PayInter.REPAIR_FUND_TURN_CASH){
    		BusiToOrderAbstract bto = new RepairFundTurnCash();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//强制平账
    	if (businessId == EnumConsts.PayInter.FORCE_ZHANG_PING) {
    		BusiToOrderAbstract bto = new ForceZhangPing();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//ETC消费
    	if(businessId==EnumConsts.PayInter.CONSUME_ETC_CODE){
    		BusiToOrderAbstract bto = new EtcConsume();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//招商车扣费
    	if(businessId==EnumConsts.PayInter.ZHAOSHANG_FEE){
    		BusiToOrderAbstract bto = new MerchantCar();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//充值
    	if(businessId==EnumConsts.PayInter.RECHARGE_CODE){
    		Recharge bto = new Recharge();
    		bto.recharge(inParam, rels);
    	}
    	//提现
    	if(businessId==EnumConsts.PayInter.WITHDRAWALS_CODE){
    		BusiToOrderAbstract bto = new Withdraw();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//异常补偿(20170802新增)
    	if(businessId == EnumConsts.PayInter.EXCEPTION_FEE){
    		BusiToOrderAbstract bto = new ExceptionCompensation();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//回退(司机路歌渠道 /20170806新增)
    	if(businessId == EnumConsts.PayInter.BACK_RECHARGE){
    		BusiToOrderAbstract bto = new BackRecharge();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//未到期转已到期
//    	if(businessId == EnumConsts.PayInter.PAYFOR_CODE){
//    		BusiToOrderAbstract bto = new PayForCharge();
//    		results = bto.dealToOrder(inParam, rels);
//    	}

    	//ETC自动充值反写
    	if (businessId == EnumConsts.PayInter.ETC_PAY_CODE) {
    		BusiToOrderAbstract bto = new EtcPayRecord();
    		results = bto.dealToOrder(inParam, rels);
		}




    	*//*自有车对应订单逻辑-开始*//*
    	//实体油卡核销
    	if(businessId == EnumConsts.PayInter.OIL_ENTITY_VERIFICATION){
    		BusiToOrderAbstract bto = new OilEntity();
    		results = bto.dealToOrder(inParam, rels);
    	}

    	//借支核销
    	if(businessId == EnumConsts.PayInter.OA_LOAN_VERIFICATION){
    		BusiToOrderAbstract bto = new OaLoanVerification();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//反写固定成本
    	if(businessId == EnumConsts.PayInter.FIXED_FEE_BACKWRITE){
    		BusiToOrderAbstract bto = new PayFixedFee();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//发放工资 (待发补贴 - 订单欠款)
    	if(businessId == EnumConsts.PayInter.CAR_DRIVER_SALARY){
    		BusiToOrderAbstract bto = new PaySalary();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	//修改订单
    	if(businessId == EnumConsts.PayInter.UPDATE_ORDER_PRICE){
    		BusiToOrderAbstract bto = new UpdateOrderPrice();
    		results = bto.dealToOrder(inParam, rels);
    	}
		//异常扣减
    	if(businessId == EnumConsts.PayInter.EXCEPTION_FEE_OUT){
    		BusiToOrderAbstract bto = new ExceptionFeeOut();
    		results = bto.dealToOrder(inParam, rels);
    	}
    	*//*自有车对应订单逻辑-结束*//*
    	if (isLock) {
    		SysContexts.releaseLockByKey(this.getClass().getName() + userId + vehicleAffiliation + businessId );
    	}*/
    	return results;
	}

	@Lazy
	@Resource
	private ConsumeOilNew consumeOilNew;

	@Resource
	private PrePayNew prePayNew;
	@Resource
	private OilAndEtcTurnCashNew oilAndEtcTurnCashNew;

	@Resource
	private WithdrawNew withdrawNew;

	@Resource
	private ForceZhangPingNew forceZhangPingNew;
	@Resource
	private  CancelTheOrder cancelTheOrder;

	@Resource
	private PledgeReleaseOilcard pledgeReleaseOilcard;
	@Resource
	private AccountOilAllot accountOilAllot;
	@Resource
	private PayOaLoan payOaLoan;
	@Resource
	private PayExpenseInfo payExpenseInfo;
	@Resource
	@Lazy
	private PayVehicleExpenseInfo payVehicleExpenseInfo;
	@Resource
	private GrantSalary grantSalary;
	@Resource
	private IOrderFundFlowService orderFundFlowService;
	@Resource
	private PayForRepairNew payForRepairNew;
	@Resource
	private UpdateOrder updateOrder;
	@Resource
	private RechargeAccountOilAllot rechargeAccountOilAllot;

	@Resource
	private ClearAccountOil clearAccountOil;

	public List busiToOrderNew(ParametersNewDto inParam,List<BusiSubjectsRel> rels,LoginInfo user) {
		long userId = inParam.getUserId();
		List results = new ArrayList();
		if (userId == 0L) {
			throw new BusinessException("用户ID不能为空!");
		}
		long businessId = inParam.getBusinessId();
		if (businessId == 0L) {
			throw new BusinessException("业务ID不能为空!");
		}
		String billId = inParam.getBillId();
		if (StringUtils.isEmpty(billId)) {
			throw new BusinessException("司机手机不能为空!");
		}
		String vehicleAffiliation = inParam.getVehicleAffiliation();
		if (StringUtils.isEmpty(vehicleAffiliation)) {
			throw new BusinessException("资金渠道不能为空!");
		}
		//todo 加锁
		//数据锁定逻辑，避免不同方法操作同样数据
		//     boolean isLock = SysContexts.getLock(this.getClass().getName() + userId + vehicleAffiliation + businessId, 3, 5);
//        if (!isLock)	//未或得资源锁，不做任何操作
//            throw new BusinessException("账户资金操作中，请稍后重试!");
		//消费油
		if(businessId==EnumConsts.PayInter.PAY_FOR_OIL_CODE){
			results = consumeOilNew.dealToOrderNew(inParam ,rels,user);
		}

		//预支
		if(businessId==EnumConsts.PayInter.ADVANCE_PAY_MARGIN_CODE){
			results = prePayNew.dealToOrderNew(inParam, rels,user);
		}
		//油和ETC转现(功能化)
		if(businessId==EnumConsts.PayInter.OIL_AND_ETC_TURN_CASH){
			results = oilAndEtcTurnCashNew.dealToOrderNew(inParam, rels,user);
		}
		//提现
		if(businessId==EnumConsts.PayInter.WITHDRAWALS_CODE){
			results = withdrawNew.dealToOrderNew(inParam, rels,user);
		}
		//强制平账
		if (businessId == EnumConsts.PayInter.FORCE_ZHANG_PING) {
			results = forceZhangPingNew.dealToOrderNew(inParam, rels,user);
		}
		//撤单
		if (businessId == EnumConsts.PayInter.CANCEL_THE_ORDER) {
			results = cancelTheOrder.dealToOrderNew(inParam, rels,user);
		}
		//油卡抵押释放
		if (businessId == EnumConsts.PayInter.PLEDGE_RELEASE_OILCARD) {
			results = pledgeReleaseOilcard.dealToOrderNew(inParam, rels,user);
		}
		//支付预付款、修改订单油账户分配
		if (businessId == EnumConsts.PayInter.BEFORE_PAY_CODE || businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
			results = accountOilAllot.dealToOrderNew(inParam, rels,user);
		}
		//借支审核通过
		if(businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE ||businessId == EnumConsts.PayInter.OA_LOAN_AVAILABLE_TUBE ){
			results = payOaLoan.dealToOrderNew(inParam, rels,user);
		}
		//报销反写-司机
		if (businessId == EnumConsts.PayInter.DRIVER_EXPENSE_ABLE) {
			results = payExpenseInfo.dealToOrderNew(inParam, rels,user);
		}
		//报销反写-车管
		if (businessId == EnumConsts.PayInter.TUBE_EXPENSE_ABLE) {
			results = payVehicleExpenseInfo.dealToOrderNew(inParam, rels,user);
		}
		//发放工资
		if (businessId == EnumConsts.PayInter.CAR_DRIVER_SALARY) {
			results = grantSalary.dealToOrderNew(inParam, rels,user);
		}
		//支付维修保养(新)
		if (businessId == EnumConsts.PayInter.PAY_FOR_REPAIR) {
			results = payForRepairNew.dealToOrderNew(inParam, rels,user);
		}
		//修改订单
		if (businessId == EnumConsts.PayInter.UPDATE_THE_ORDER) {
			results = updateOrder.dealToOrderNew(inParam, rels,user);
		}
		//充值油订单油账户分配
		if (businessId == EnumConsts.PayInter.RECHARGE_ACCOUNT_OIL) {
			results = rechargeAccountOilAllot.dealToOrderNew(inParam, rels,user);
		}
		//清零油账户
		if (businessId == EnumConsts.PayInter.CLEAR_ACCOUNT_OIL) {
			results = clearAccountOil.dealToOrderNew(inParam, rels,user);
		}
		//未到期转已到期
		if(businessId == EnumConsts.PayInter.PAYFOR_CODE){
			results = orderFundFlowService.dealToOrderNewCode(inParam, rels,user);
		}
		//到付款
		if (businessId == EnumConsts.PayInter.PAY_ARRIVE_CHARGE) {
			results = orderFundFlowService.dealToOrderNewCharge(inParam, rels,user);
		}
		//招商车挂靠车对账单
		if (businessId == EnumConsts.PayInter.ACCOUNT_STATEMENT) {
			results = orderFundFlowService.dealToOrderNew(inParam, rels,user);
		}
		//todo 解锁
//        if (isLock) {
//            SysContexts.releaseLockByKey(this.getClass().getName() + userId + vehicleAffiliation + businessId );
//        }
    	return results;
	}
	/**
	 * 根据提现人，金额匹配订单信息（路歌）
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
/*	public List<OrderLimit> matchOrderInfoForWithdraw(Map<String,String> inParam) throws Exception{
		Withdraw bto = new Withdraw();
		return bto.matchOrderInfoForWithdraw(inParam);
	}*/

/*	public List<OrderLimit> matchOrderInfoForPaySalary(Map<String, String> inParam) throws Exception {
		PaySalary bto = new PaySalary();
		return bto.matchOrderInfoForPaySalary(inParam);
	}*/

	/**
	 * 查询订单限制表
	 * @param orderId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
/*	public OrderLimit queryOrderLimitByOrderId(long orderId,long userId) throws Exception{
		if(orderId <= 0){
			throw new BusinessException("输入订单号不正确");
		}
		Session session = SysContexts.getEntityManager(true);
		Criteria ca = session.createCriteria(OrderLimit.class);
		ca.add(Restrictions.eq("orderId", orderId));
		ca.add(Restrictions.eq("userId", userId));
		OrderLimit ol = (OrderLimit) ca.uniqueResult();
		return ol;
	}*/

	/**
	 * @param userId 用户id
	 * @param billId 用户手机号
	 * @param businessId 业务id
	 * @param orderId 订单id
	 * @param amount 费用
	 * @param vehicleAffiliation 资金渠道
	 * @param finalPlanDate 尾款到账日期
	 */
	public Map<String, String> setParameters(long userId,String billId,long businessId,long orderId,long amount,String vehicleAffiliation,String finalPlanDate) throws Exception{
		Map<String,String> map = new HashMap<String, String>();
		map.put("userId", String.valueOf(userId));
		map.put("billId", String.valueOf(billId));
		map.put("businessId", String.valueOf(businessId));
		map.put("orderId", orderId<=0?null:String.valueOf(orderId));
		map.put("amount", String.valueOf(amount));
		map.put("vehicleAffiliation", String.valueOf(vehicleAffiliation));
		map.put("finalPlanDate",String.valueOf(finalPlanDate));
		return map;
	}

	/**
	 * @param userId 用户id
	 * @param billId 用户手机号
	 * @param businessId 业务id
	 * @param orderId 订单id
	 * @param amount 费用
	 * @param vehicleAffiliation 资金渠道
	 * @param finalPlanDate 尾款到账日期
	 */
	public ParametersNewDto setParametersNew(Long userId,String billId,Long businessId,Long orderId,Long amount,String vehicleAffiliation,String finalPlanDate) {
		ParametersNewDto map = new ParametersNewDto();
		map.setUserId(userId);
		map.setBillId(billId);
		map.setBusinessId(businessId);
		map.setOrderId(orderId);
		map.setAmount(amount);
		map.setVehicleAffiliation(vehicleAffiliation);
		map.setFinalPlanDate(finalPlanDate);
		return map;
	}
	/**
	 * 抵扣
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
/*	public List payForDebt(Map<String,String> inParam) throws Exception{
		MerchantCar merchantCar = new MerchantCar();
		List results = merchantCar.queryDebtToDeal(inParam);
		return results;
	}*/
	/**
	 * 实际返现金额
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
/*	public void getRealBackIncome(long userId,String vehicleAffiliation,List<BusiSubjectsRel> rels) throws Exception{
		OilConsume oilConsume = new OilConsume();
		oilConsume.getRealBackIncome(userId,vehicleAffiliation,rels);
	}*/

}
