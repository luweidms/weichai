package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.BusiSubjectsDtlMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.vo.QueryOrderLimitByCndVo;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/6/13 16:49
 */
@Component
public class MerchantCar {

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    IOrderLimitService orderLimitService;

    @Resource
    BusiSubjectsDtlMapper busiSubjectsDtlMapper;


    public List dealToOrder(ParametersNewDto inParam,
                            List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        if (rels == null) {
            throw new BusinessException("招商车费用明细不能为空!");
        }
        // 资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //查询司机订单
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitByOrderId(orderId,-1);
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            if(rel.getAmountFee() > 0){//导入招商车费用为正数
                // 查询司机对应资金渠道订单
                OrderLimit olTemp = orderLimits.get(0);
                //尾款与招商车费用判断，是否需要借款
                long noPayFinal = olTemp.getNoPayFinal();
                if(noPayFinal>=rel.getAmountFee()){
                    // 尾款扣费
                    this.payFinal(olTemp, inParam,rel.getAmountFee(), rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                }
                else
                {
                    long amount = rel.getAmountFee();
                    // 尾款扣费
                    this.payFinal(olTemp, inParam,noPayFinal , rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                    // 欠款扣费
                    this.payDebt(olTemp, inParam,rel.getSubjectsId(),amount - noPayFinal ,businessId, accountDatailsId, user);
                    //抵扣欠款
                    QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                    queryOrderLimitByCndVo.setUserId(userId);
                    queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                    queryOrderLimitByCndVo.setNoPayFinal("1");
                    queryOrderLimitByCndVo.setOrderId(orderId);
                    List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                    //需要其他单抵扣的金额
                    long deductibleAmount = 0L;
                    for(OrderLimit ol : orderLimitsFinal){
                        deductibleAmount += ol.getNoPayFinal();
                    }
                    if(deductibleAmount >= (amount - noPayFinal)){//其他单足够抵扣本单产生的欠款
                        //抵扣金额
                        this.matchAmountToOrder((amount - noPayFinal),0L,0L, "NoPayFinal",orderLimitsFinal);
                        // 将提现金额分摊给订单
                        for (OrderLimit olTempFinal : orderLimitsFinal) {
                            if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                            }
                        }
                    }else{
                        if(deductibleAmount > 0){
                            //抵扣金额
                            this.matchAmountToOrder(deductibleAmount,0L,0L, "NoPayFinal",orderLimitsFinal);
                            // 将提现金额分摊给订单
                            for (OrderLimit olTempFinal : orderLimitsFinal) {
                                if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                    this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                                }
                            }
                        }
                        //其他单不扣抵扣导致本单上产生欠款
                    }
                }
            }else{
                // 查询司机对应资金渠道订单
                OrderLimit olTemp = orderLimits.get(0);
                //尾款与招商车费用判断，是否需要借款
                long noPayFinal = olTemp.getNoPayFinal();
                // 尾款扣费
                this.payFinal(olTemp, inParam,rel.getAmountFee(), rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                // 抵扣
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setHasDebt("1");
                List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                //欠款金额
                this.matchAmountToOrder(Math.abs(rel.getAmountFee()),0L,0L, "NoPayDebt",orderLimitsFinal);
                // 将提现金额分摊给订单
                for (OrderLimit olTempFinal : orderLimitsFinal) {
                    if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                        this.payForDebt(orderId, olTempFinal.getOrderId(), olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                    }
                }
            }
        }
        return null;
    }

    public void payFinal(OrderLimit olTemp, ParametersNewDto inParam, Long amount, Long subjectId, String subjectName, Long businessId, Long accountDatailsId, LoginInfo user){
        OrderFundFlow off = new OrderFundFlow();
        off.setAmount(amount);// 交易金额（单位分）
        off.setOrderId(olTemp.getOrderId());// 订单ID
        off.setBusinessId(businessId);// 业务类型
        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
        off.setBookType(EnumConsts.PayInter.CHANGE_CODE);// 账户类型
        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", EnumConsts.PayInter.CHANGE_CODE+"").getCodeName());// 账户类型
        off.setSubjectsId(subjectId);// 科目ID
        off.setSubjectsName(subjectName);// 科目名称
        off.setBusiTable("ACCOUNT_DETAILS");// 业务对象表
        off.setBusiKey(accountDatailsId);// 业务流水ID
        off.setInoutSts("out");// 收支状态:收in支out转io
        this.createOrderFundFlow(inParam, off, user);
        // 订单限制表操作
		/*List<OperDataParam> odps = new ArrayList();
		odps.add(new OperDataParam("NO_PAY_FINAL", String.valueOf(off
				.getAmount()), "-"));
		odps.add(new OperDataParam("FINAL_INCOME", String.valueOf(off
				.getAmount()), "+"));
		if(amount > 0){
			odps.add(new OperDataParam("PAID_FINAL_PAY", String.valueOf(off
					.getAmount()), "+"));
		}
		session.saveOrUpdate(off);
		this.updateOrderLimit(olTemp.getOrderId(), odps);*/
        orderFundFlowService.saveOrUpdate(off);
        olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
        olTemp.setFinalIncome(olTemp.getFinalIncome() + off.getAmount());
        if(amount > 0){
            olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
        }
        orderLimitService.saveOrUpdate(olTemp);
    }

    public void payDebt(OrderLimit olTemp, ParametersNewDto inParam, Long subjectId, Long amount, Long businessId, Long accountDatailsId, LoginInfo user){
        OrderFundFlow off = new OrderFundFlow();
        off.setAmount(amount);// 交易金额（单位分）
        off.setOrderId(olTemp.getOrderId());// 订单ID
        off.setBusinessId(businessId);// 业务类型
        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// 业务名称
        off.setBookType(EnumConsts.PayInter.BEFOREPAY_CODE);// 账户类型
        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", EnumConsts.PayInter.BEFOREPAY_CODE+"").getCodeName());// 账户类型
        off.setSubjectsId(10086L);// 科目ID
        off.setSubjectsName("欠款");// 科目名称
        off.setBusiTable("ACCOUNT_DETAILS");// 业务对象表
        off.setBusiKey(accountDatailsId);// 业务流水ID
        off.setInoutSts("out");// 收支状态:收in支out转io
        this.createOrderFundFlow(inParam, off, user);
        // 订单限制表操作
		/*List<OperDataParam> odps = new ArrayList();
		odps.add(new OperDataParam("DEBT_MONEY", String.valueOf(off.getAmount()), "+"));
		odps.add(new OperDataParam("NO_PAY_DEBT", String.valueOf(off.getAmount()), "+"));
		odps.add(new OperDataParam("FINAL_INCOME", String.valueOf(off.getAmount()), "+"));
		session.saveOrUpdate(off);
		this.updateOrderLimit(olTemp.getOrderId(), odps);*/
        orderFundFlowService.saveOrUpdate(off);
        olTemp.setDebtMoney(olTemp.getDebtMoney() + off.getAmount());
        olTemp.setNoPayDebt(olTemp.getNoPayDebt() + off.getAmount());
        olTemp.setFinalIncome(olTemp.getFinalIncome() + off.getAmount());
        orderLimitService.saveOrUpdate(olTemp);
    }

    /**
     * 抵扣欠款流水和处理
     */
    public void payForDebt(Long fianlOrderId, Long debtOrderId, Long amount, Long accountDatailsId, ParametersNewDto inParam, LoginInfo user){
        List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(EnumConsts.PayInter.PAY_FINAL, EnumConsts.SubjectIds.DEDUCTION_ARREARS);
        for(BusiSubjectsDtl bsd : bsds){
            //资金流水操作
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(amount);//交易金额（单位分）
            off.setBusinessId(EnumConsts.PayInter.PAY_FINAL);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(EnumConsts.PayInter.PAY_FINAL)).getCodeName());//业务名称
            off.setBookType(bsd.getBooType());//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(bsd.getBooType())).getCodeName());//账户类型
            off.setSubjectsId(bsd.getSubjectsId());//科目ID
            off.setSubjectsName("");//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setInoutSts(bsd.getInoutSts());//收支状态:收in支out转io
            this.createOrderFundFlow(inParam,off, user);
            if(bsd.getDtlBusinessId()==11000010L){
                off.setSubjectsName("抵扣欠款");//科目名称
                off.setOrderId(fianlOrderId);//订单ID
                //订单限制表操作
				/*List<OperDataParam> odps = new ArrayList();
				odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"-"));
				odps.add(new OperDataParam("PAID_FINAL_PAY",String.valueOf(amount),"+"));
				session.saveOrUpdate(off);
				this.updateOrderLimit(fianlOrderId , odps);*/
                orderFundFlowService.saveOrUpdate(off);
                OrderLimit olTemp = orderLimitMapper.getOneOrderLimitByOrderId(fianlOrderId);
                olTemp.setNoPayFinal(olTemp.getNoPayFinal() - amount);
                olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + amount);
                orderLimitService.saveOrUpdate(olTemp);
            }
            if(bsd.getDtlBusinessId()==11000011L){
                off.setSubjectsName("欠款");//科目名称
                off.setOrderId(debtOrderId);//订单ID
                //订单限制表操作
				/*List<OperDataParam> odps = new ArrayList();
				odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"+"));
				session.saveOrUpdate(off);
				this.updateOrderLimit(debtOrderId , odps);*/
                orderFundFlowService.saveOrUpdate(off);
                OrderLimit olTemp = orderLimitMapper.getOneOrderLimitByOrderId(debtOrderId);
                olTemp.setNoPayFinal(olTemp.getNoPayFinal() + amount);
                orderLimitService.saveOrUpdate(olTemp);
            }
            if(bsd.getDtlBusinessId()==11000012L){
                off.setSubjectsName("欠款");//科目名称
                off.setOrderId(debtOrderId);//订单ID
                //订单限制表操作
				/*List<OperDataParam> odps = new ArrayList();
				odps.add(new OperDataParam("NO_PAY_FINAL",String.valueOf(amount),"-"));
				odps.add(new OperDataParam("PAID_DEBT",String.valueOf(amount),"+"));
				odps.add(new OperDataParam("NO_PAY_DEBT",String.valueOf(amount),"-"));
				session.saveOrUpdate(off);
				this.updateOrderLimit(debtOrderId , odps);*/
                orderFundFlowService.saveOrUpdate(off);
                OrderLimit olTemp = orderLimitMapper.getOneOrderLimitByOrderId(debtOrderId);
                olTemp.setNoPayFinal(olTemp.getNoPayFinal() - amount);
                olTemp.setPaidDebt(olTemp.getPaidDebt() + amount);
                olTemp.setNoPayDebt(olTemp.getNoPayDebt() - amount);
                orderLimitService.saveOrUpdate(olTemp);

            }
        }
    }
    //抵扣欠款对外方法
    public List queryDebtToDeal(Map inParam) throws Exception{
//		Session session = SysContexts.getEntityManager();
//		long userId = DataFormat.getLongKey(inParam, "userId");
//		String vehicleAffiliation = DataFormat.getStringKey(inParam, "vehicleAffiliation");
//		long vehicleCode = DataFormat.getLongKey(inParam, "vehicleCode");
//		List<Object[]> orderInfoList = orderImpl.queryOrderInfoByCar(vehicleCode,inParam);
//		long orderId = 0L;
//		List results = null;
//		//检查订单是否有欠费，存在欠费则查找抵扣
//		for(Object[] obj : orderInfoList){
//			orderId = ((Number)obj[0]).longValue();
//			List<OrderLimit> olList = this.getOrderLimitByOrderId(orderId);
//			OrderLimit olTemp = olList.get(0);
//			//存在欠款
//			if(olTemp.getNoPayDebt()!=null && olTemp.getNoPayDebt()!=0L){
//				Long debtAmount = olTemp.getNoPayDebt();
//				Map<String, String> queryCond = new HashMap();
//				queryCond.put("userId", String.valueOf(userId));
//				queryCond.put("vehicleAffiliation", vehicleAffiliation);
//				List<OrderLimit> orderLimitsFinal = this.queryOrderLimitByCnd(queryCond);
//				//欠款金额
//				this.matchAmountToOrder(debtAmount,0L,0L, "NoPayFinal",orderLimitsFinal);
//				// 将提现金额分摊给订单
//				for (OrderLimit olTempFinal : orderLimitsFinal) {
//					if(results==null){
//						results = new ArrayList();
//					}
//					this.payForDebt(session, olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), 0L, inParam);
//					ReturnResult rr = new ReturnResult();
//					rr.setOrderId(orderId);
//					rr.setDebtOrderId(olTempFinal.getOrderId());
//					rr.setAmount(olTempFinal.getMatchAmount());
//					results.add(rr);
//				}
//			}
//		}
        return null;
    }

    private Long matchAmountToOrder(Long amount, Long income, Long backIncome, String fieldName, List<OrderLimit> orderLimits) {
        //保存剩余金额
        long allAmount = amount;
        if(income==null){
            income = 0L;
        }
        if(backIncome==null){
            backIncome = 0L;
        }
        double incomeRatio = ((double)income)/((double)amount);
        double backIncomeRatio = ((double)backIncome)/((double)amount);
        try {
            for (OrderLimit ol : orderLimits) {
                Method method = ol.getClass().getDeclaredMethod("get" + fieldName);
                Long value = (Long) method.invoke(ol);
                if (value == 0L) {
                    continue;
                }
                if (allAmount > value) {
                    ol.setMatchAmount(value);
                    ol.setMatchIncome(new Double(value * incomeRatio).longValue());
                    ol.setMatchBackIncome(new Double(value * backIncomeRatio).longValue());
                    //剩余金额=总金额-分摊掉金额
                    allAmount = allAmount - value;
                    income -= ol.getMatchIncome();
                    backIncome -= ol.getMatchBackIncome();
                } else if (allAmount <= value) {
                    ol.setMatchAmount(allAmount);
                    ol.setMatchIncome(income);
                    ol.setMatchBackIncome(backIncome);
                    allAmount = 0L;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常");
        }
        return allAmount;
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
        if(off==null){
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.getUserDataInfo(userId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if(user != null){
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
            off.setUpdateOpId(user.getId());//修改人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }
}
