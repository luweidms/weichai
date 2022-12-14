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
            throw new BusinessException("?????????????????????????????????!");
        }
        // ??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //??????????????????
        List<OrderLimit> orderLimits = orderLimitMapper.getOrderLimitByOrderId(orderId,-1);
        // ????????????????????????
        for (BusiSubjectsRel rel : rels) {
            if(rel.getAmountFee() > 0){//??????????????????????????????
                // ????????????????????????????????????
                OrderLimit olTemp = orderLimits.get(0);
                //???????????????????????????????????????????????????
                long noPayFinal = olTemp.getNoPayFinal();
                if(noPayFinal>=rel.getAmountFee()){
                    // ????????????
                    this.payFinal(olTemp, inParam,rel.getAmountFee(), rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                }
                else
                {
                    long amount = rel.getAmountFee();
                    // ????????????
                    this.payFinal(olTemp, inParam,noPayFinal , rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                    // ????????????
                    this.payDebt(olTemp, inParam,rel.getSubjectsId(),amount - noPayFinal ,businessId, accountDatailsId, user);
                    //????????????
                    QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                    queryOrderLimitByCndVo.setUserId(userId);
                    queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                    queryOrderLimitByCndVo.setNoPayFinal("1");
                    queryOrderLimitByCndVo.setOrderId(orderId);
                    List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                    //??????????????????????????????
                    long deductibleAmount = 0L;
                    for(OrderLimit ol : orderLimitsFinal){
                        deductibleAmount += ol.getNoPayFinal();
                    }
                    if(deductibleAmount >= (amount - noPayFinal)){//??????????????????????????????????????????
                        //????????????
                        this.matchAmountToOrder((amount - noPayFinal),0L,0L, "NoPayFinal",orderLimitsFinal);
                        // ??????????????????????????????
                        for (OrderLimit olTempFinal : orderLimitsFinal) {
                            if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                            }
                        }
                    }else{
                        if(deductibleAmount > 0){
                            //????????????
                            this.matchAmountToOrder(deductibleAmount,0L,0L, "NoPayFinal",orderLimitsFinal);
                            // ??????????????????????????????
                            for (OrderLimit olTempFinal : orderLimitsFinal) {
                                if(olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0){
                                    this.payForDebt(olTempFinal.getOrderId(), orderId, olTempFinal.getMatchAmount(), accountDatailsId, inParam, user);
                                }
                            }
                        }
                        //????????????????????????????????????????????????
                    }
                }
            }else{
                // ????????????????????????????????????
                OrderLimit olTemp = orderLimits.get(0);
                //???????????????????????????????????????????????????
                long noPayFinal = olTemp.getNoPayFinal();
                // ????????????
                this.payFinal(olTemp, inParam,rel.getAmountFee(), rel.getSubjectsId(),rel.getSubjectsName(),businessId, accountDatailsId, user);
                // ??????
                QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
                queryOrderLimitByCndVo.setUserId(userId);
                queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
                queryOrderLimitByCndVo.setHasDebt("1");
                List<OrderLimit> orderLimitsFinal = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                //????????????
                this.matchAmountToOrder(Math.abs(rel.getAmountFee()),0L,0L, "NoPayDebt",orderLimitsFinal);
                // ??????????????????????????????
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
        off.setAmount(amount);// ???????????????????????????
        off.setOrderId(olTemp.getOrderId());// ??????ID
        off.setBusinessId(businessId);// ????????????
        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
        off.setBookType(EnumConsts.PayInter.CHANGE_CODE);// ????????????
        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", EnumConsts.PayInter.CHANGE_CODE+"").getCodeName());// ????????????
        off.setSubjectsId(subjectId);// ??????ID
        off.setSubjectsName(subjectName);// ????????????
        off.setBusiTable("ACCOUNT_DETAILS");// ???????????????
        off.setBusiKey(accountDatailsId);// ????????????ID
        off.setInoutSts("out");// ????????????:???in???out???io
        this.createOrderFundFlow(inParam, off, user);
        // ?????????????????????
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
        off.setAmount(amount);// ???????????????????????????
        off.setOrderId(olTemp.getOrderId());// ??????ID
        off.setBusinessId(businessId);// ????????????
        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(businessId)).getCodeName());// ????????????
        off.setBookType(EnumConsts.PayInter.BEFOREPAY_CODE);// ????????????
        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", EnumConsts.PayInter.BEFOREPAY_CODE+"").getCodeName());// ????????????
        off.setSubjectsId(10086L);// ??????ID
        off.setSubjectsName("??????");// ????????????
        off.setBusiTable("ACCOUNT_DETAILS");// ???????????????
        off.setBusiKey(accountDatailsId);// ????????????ID
        off.setInoutSts("out");// ????????????:???in???out???io
        this.createOrderFundFlow(inParam, off, user);
        // ?????????????????????
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
     * ???????????????????????????
     */
    public void payForDebt(Long fianlOrderId, Long debtOrderId, Long amount, Long accountDatailsId, ParametersNewDto inParam, LoginInfo user){
        List<BusiSubjectsDtl> bsds = busiSubjectsDtlMapper.queryBusiSubjectsDtl(EnumConsts.PayInter.PAY_FINAL, EnumConsts.SubjectIds.DEDUCTION_ARREARS);
        for(BusiSubjectsDtl bsd : bsds){
            //??????????????????
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(amount);//???????????????????????????
            off.setBusinessId(EnumConsts.PayInter.PAY_FINAL);//????????????
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(EnumConsts.PayInter.PAY_FINAL)).getCodeName());//????????????
            off.setBookType(bsd.getBooType());//????????????
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(bsd.getBooType())).getCodeName());//????????????
            off.setSubjectsId(bsd.getSubjectsId());//??????ID
            off.setSubjectsName("");//????????????
            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
            off.setBusiKey(accountDatailsId);//????????????ID
            off.setInoutSts(bsd.getInoutSts());//????????????:???in???out???io
            this.createOrderFundFlow(inParam,off, user);
            if(bsd.getDtlBusinessId()==11000010L){
                off.setSubjectsName("????????????");//????????????
                off.setOrderId(fianlOrderId);//??????ID
                //?????????????????????
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
                off.setSubjectsName("??????");//????????????
                off.setOrderId(debtOrderId);//??????ID
                //?????????????????????
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
                off.setSubjectsName("??????");//????????????
                off.setOrderId(debtOrderId);//??????ID
                //?????????????????????
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
    //????????????????????????
    public List queryDebtToDeal(Map inParam) throws Exception{
//		Session session = SysContexts.getEntityManager();
//		long userId = DataFormat.getLongKey(inParam, "userId");
//		String vehicleAffiliation = DataFormat.getStringKey(inParam, "vehicleAffiliation");
//		long vehicleCode = DataFormat.getLongKey(inParam, "vehicleCode");
//		List<Object[]> orderInfoList = orderImpl.queryOrderInfoByCar(vehicleCode,inParam);
//		long orderId = 0L;
//		List results = null;
//		//?????????????????????????????????????????????????????????
//		for(Object[] obj : orderInfoList){
//			orderId = ((Number)obj[0]).longValue();
//			List<OrderLimit> olList = this.getOrderLimitByOrderId(orderId);
//			OrderLimit olTemp = olList.get(0);
//			//????????????
//			if(olTemp.getNoPayDebt()!=null && olTemp.getNoPayDebt()!=0L){
//				Long debtAmount = olTemp.getNoPayDebt();
//				Map<String, String> queryCond = new HashMap();
//				queryCond.put("userId", String.valueOf(userId));
//				queryCond.put("vehicleAffiliation", vehicleAffiliation);
//				List<OrderLimit> orderLimitsFinal = this.queryOrderLimitByCnd(queryCond);
//				//????????????
//				this.matchAmountToOrder(debtAmount,0L,0L, "NoPayFinal",orderLimitsFinal);
//				// ??????????????????????????????
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
        //??????????????????
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
                    //????????????=?????????-???????????????
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
            throw new BusinessException("????????????");
        }
        return allAmount;
    }

    /*??????????????????????????????*/
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
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        off.setUserName(userDataInfo.getLinkman());//?????????
        off.setBatchId(batchId);//??????
        off.setBatchAmount(amount);//???????????????
        if(user != null){
            off.setOpId(user.getId());//?????????ID
            off.setOpName(user.getName());//?????????
            off.setUpdateOpId(user.getId());//?????????
        }
        off.setOpDate(LocalDateTime.now());//????????????
        off.setVehicleAffiliation(vehicleAffiliation);//????????????
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//?????????????????????????????????
        return off;
    }
}
