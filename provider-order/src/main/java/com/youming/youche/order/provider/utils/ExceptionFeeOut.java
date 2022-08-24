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
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/14 13:20
 */
@Component
public class ExceptionFeeOut {

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long userId = inParam.getUserId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        if (orderId <= 0L) {
            throw new BusinessException("预付业务订单ID不能为空!");
        }
        //查询订单限制数据
        OrderLimit olTemp =  orderLimitMapper.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (olTemp == null) {
            throw new BusinessException("订单信息不存在!");
        }
        if (rels == null) {
            throw new BusinessException("预付明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(Math.abs(rel.getAmountFee()));//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(olTemp.getTenantId());
            this.createOrderFundFlow(inParam,off, user);
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_FEE_OUT) {
                off.setInoutSts("out");//收支状态:收in支out转io
                if (olTemp.getNoPayFinal() > off.getAmount()) {
                    olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                    olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
                } else {
                    if (olTemp.getNoPayFinal() > 0) {
                        olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + olTemp.getNoPayFinal());
                        olTemp.setNoPayDebt(olTemp.getNoPayDebt() + (off.getAmount() - olTemp.getNoPayFinal()));
                        olTemp.setDebtMoney(olTemp.getDebtMoney() + (off.getAmount() - olTemp.getNoPayFinal()));
                        olTemp.setNoPayFinal(0L);
                    } else {
                        olTemp.setNoPayDebt(olTemp.getNoPayDebt() + off.getAmount());
                        olTemp.setDebtMoney(olTemp.getDebtMoney() + off.getAmount());
                    }
                }
    	    	/*if (olTemp.getNoPayDebt() > 0) {
    	    		// 抵扣
    				List<OrderLimit> orderLimitsFinal = orderLimitSV.getOrderLimit(userId,olTemp.getVehicleAffiliation(), OrderAccountConst.NO_PAY.NO_PAY_FINAL,olTemp.getTenantId(),olTemp.getOilAffiliation(),olTemp.getUserType());
    				//需要其他单抵扣的金额
    				long deductibleAmount = 0L;
    				for (OrderLimit ol : orderLimitsFinal) {
    					deductibleAmount += ol.getNoPayFinal();
    				}
    				//其他单足够抵扣本单产生的欠款
    				if (deductibleAmount > olTemp.getNoPayDebt()) {
    					//抵扣金额
    					MatchAmountUtil.matchAmount(olTemp.getNoPayDebt(), 0, 0, "noPayFinal", orderLimitsFinal);
    					// 将金额分摊给订单
    					for (OrderLimit olTempFinal : orderLimitsFinal) {
    						if (olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0) {
    							this.payForDebt(session, olTempFinal, olTemp, olTempFinal.getMatchAmount(), accountDatailsId, inParam);
    						}
    					}
    				} else {
    					if (deductibleAmount > 0) {
    						//抵扣金额
    						MatchAmountUtil.matchAmount(deductibleAmount, 0, 0, "noPayFinal", orderLimitsFinal);
    						// 将提现金额分摊给订单
    						for (OrderLimit olTempFinal : orderLimitsFinal) {
    							if (olTempFinal.getMatchAmount() != null && olTempFinal.getMatchAmount() > 0) {
    								this.payForDebt(session, olTempFinal, olTemp, olTempFinal.getMatchAmount(), accountDatailsId, inParam);
    							}
    						}
    					}
    				}
    	    	}*/
            }
            //司机应付逾期
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB) {
                off.setInoutSts("out");//收支状态:收in支out转io
            }
            //尾款抵扣异常扣减
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB) {
                off.setInoutSts("in");//收支状态:收in支out转io
            }
            //车队应收逾期
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.EXCEPTION_OUT_RECEIVABLE_OVERDUE_SUB) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(olTemp);
        }
        return null;
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
