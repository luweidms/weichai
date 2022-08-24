package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.TimeUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/13 14:32
 */
@Component
public class FinalPay {

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

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long userId = inParam.getUserId();
        Long businessId = inParam.getBusinessId();
        Long orderId = inParam.getOrderId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        Long tenantId = inParam.getTenantId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        String paymentDay = inParam.getFinalPlanDate();
        if (rels == null) {
            throw new BusinessException("尾款费用明细不能为空!");
        }
        Date finalPlanDate = null;
        if (paymentDay == null || Integer.valueOf(paymentDay) <= 0) {
            finalPlanDate = new Date();
        } else {
            // 计划处理时间(尾款到期时间):签收时间+帐期
            Calendar cal = Calendar.getInstance();
            cal.setTime(TimeUtil.getDataTime());
            cal.add(Calendar.DATE, Integer.valueOf(paymentDay));
            finalPlanDate = cal.getTime();
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //查询司机订单
        //查询订单限制数据
        OrderLimit olTemp =  orderLimitMapper.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (olTemp == null) {
            throw new BusinessException("订单信息不存在!");
        }
        //处理业务费用明细
        for (BusiSubjectsRel rel:rels) {
            //金额为0不进行处理
            if (rel.getAmountFee( ) == 0L) {
                continue;
            }
            // TODO 2022-6-29 回单审核 bug 处理
            if (rel.getSubjectsId()==null){
                continue;
            }
            //尾款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.FINAL_CHARGE) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                orderFundFlowService.saveOrUpdate(off);

                olTemp.setOrderFinal(olTemp.getOrderFinal() + off.getAmount());
                olTemp.setNoPayFinal(olTemp.getNoPayFinal() + off.getAmount());
                olTemp.setFianlSts(0);
                olTemp.setStsDate(LocalDateTime.now());
                olTemp.setFinalPlanDate(DateUtil.asLocalDateTime(finalPlanDate));
                olTemp.setStsNote("未到期尾款");
                orderLimitService.saveOrUpdate(olTemp);
            }
            //应付逾期(订单尾款到期转可用)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.FINAL_PAYFOR_PAYABLE_IN) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                orderFundFlowService.saveOrUpdate(off);
            }
            //应收逾期(订单尾款到期转可用)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                orderFundFlowService.saveOrUpdate(off);
            }
            //应付逾期(订单尾款到期转可用)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.Oil_PAYFOR_PAYABLE_IN) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                orderFundFlowService.saveOrUpdate(off);
            }
            //应收逾期(订单油到期转可用)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("in");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                orderFundFlowService.saveOrUpdate(off);
            }
            //保费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.INSURANCE_SUB) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作
                orderFundFlowService.saveOrUpdate(off);
                if (Math.abs(off.getAmount()) <= olTemp.getNoPayFinal()) {
                    olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                    olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
                } else {
                    olTemp.setDebtMoney(olTemp.getDebtMoney() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
                    olTemp.setNoPayDebt(olTemp.getNoPayDebt() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
                    olTemp.setNoPayFinal(0L);
                    olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + olTemp.getNoPayFinal());
                }
                orderLimitService.saveOrUpdate(olTemp);
            }
            //时效罚款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.PRESCRIPTION_FINE) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作
                orderFundFlowService.saveOrUpdate(off);
                if (Math.abs(off.getAmount()) <= olTemp.getNoPayFinal()) {
                    olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                    olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
                } else {
                    olTemp.setDebtMoney(olTemp.getDebtMoney() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
                    olTemp.setNoPayDebt(olTemp.getNoPayDebt() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
                    olTemp.setNoPayFinal(0L);
                    olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + olTemp.getNoPayFinal());
                }
                orderLimitService.saveOrUpdate(olTemp);
            }
            //异常扣减(异常扣减传过来是个负数)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_FINE_FEE) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(olTemp.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setTenantId(olTemp.getTenantId());
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作
				/*if (Math.abs(off.getAmount()) <= olTemp.getNoPayFinal()) {
					olTemp.setNoPayFinal(olTemp.getNoPayFinal() - Math.abs(off.getAmount()));
					olTemp.setOrderFinal(olTemp.getOrderFinal() - Math.abs(off.getAmount()));
				} else {
					olTemp.setDebtMoney(olTemp.getDebtMoney() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
					olTemp.setNoPayDebt(olTemp.getNoPayDebt() + Math.abs(off.getAmount()) - olTemp.getNoPayFinal());
					olTemp.setNoPayFinal(0L);
					olTemp.setOrderFinal(olTemp.getPaidFinalPay());
				}*/
                olTemp.setNoPayFinal(olTemp.getNoPayFinal() - Math.abs(off.getAmount()));
                olTemp.setOrderFinal(olTemp.getOrderFinal() - Math.abs(off.getAmount()));
                orderFundFlowService.saveOrUpdate(off);
                orderLimitService.saveOrUpdate(olTemp);
            }
        }
        //异常扣减导致本单欠款，需要其他单抵扣
    	/*if (olTemp.getNoPayDebt() > 0) {
    		// 抵扣
			List<OrderLimit> orderLimitsFinal = orderLimitSV.getOrderLimit(userId,vehicleAffiliation, OrderAccountConst.NO_PAY.NO_PAY_FINAL,olTemp.getTenantId());
			//需要其他单抵扣的金额
			long deductibleAmount = 0L;
			for (OrderLimit ol : orderLimitsFinal) {
				deductibleAmount += ol.getNoPayFinal();
			}
			//其他单足够抵扣本单产生的欠款
			if (deductibleAmount > olTemp.getNoPayDebt()) {
				//抵扣金额
				opOrderLimitTF.matchAmountToOrderLimit(olTemp.getNoPayDebt(),0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
				// 将金额分摊给订单
				for (OrderLimit olTempFinal : orderLimitsFinal) {
					if (olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0) {
						this.payForDebt(session, olTempFinal, olTemp, olTempFinal.getMatchAmount(), accountDatailsId, inParam);
					}
				}
			} else {
				if (deductibleAmount > 0) {
					//抵扣金额
					opOrderLimitTF.matchAmountToOrderLimit(deductibleAmount,0,0,OrderAccountConst.NO_PAY.NO_PAY_FINAL,orderLimitsFinal);
					// 将提现金额分摊给订单
					for (OrderLimit olTempFinal : orderLimitsFinal) {
						if (olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0) {
							this.payForDebt(session, olTempFinal, olTemp, olTempFinal.getMatchAmount(), accountDatailsId, inParam);
						}
					}
				}
			}
    	}*/
        List results = null;
        //查询欠款处理逻辑
    	/*if (olTemp.getNoPayFinal() != null && olTemp.getNoPayFinal() > 0L) {
    		//查询是否有欠款
    		List<OrderLimit> debtOrders = orderLimitSV.getOrderLimit(userId,vehicleAffiliation,OrderAccountConst.NO_PAY.NO_PAY_DEBT,olTemp.getTenantId());
    		//尾款金额
    		long noPayFinal = olTemp.getNoPayFinal();
    		for (OrderLimit old : debtOrders) {
    			if (results == null) {
    				results = new ArrayList();
    			}
    			//欠款金额
    			long noPayDebt = old.getNoPayDebt();
    			ReturnResult rr  = new ReturnResult();
    			if (noPayDebt <= noPayFinal) {
    				//尾款足够支付欠款
    				this.payForDebt(session, olTemp, old, noPayDebt, accountDatailsId, inParam);
    				rr.setOrderId(orderId);
    				rr.setDebtOrderId(olTemp.getOrderId());
    				rr.setAmount(noPayDebt);
    				results.add(rr);
    				noPayFinal -= noPayDebt;
    			} else {
    				//尾款不足够支付欠款
    				this.payForDebt(session, olTemp, old, noPayFinal, accountDatailsId, inParam);
    				rr.setOrderId(orderId);
    				rr.setDebtOrderId(olTemp.getOrderId());
    				rr.setAmount(noPayFinal);
    				results.add(rr);
    				break;
    			}
    		}
    	}*/
        return results;
    }

    private OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
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
