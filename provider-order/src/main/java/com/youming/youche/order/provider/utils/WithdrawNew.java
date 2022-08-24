package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.api.order.ISubjectsInfoService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutOrder;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;

@Component
@RequiredArgsConstructor
public class WithdrawNew {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    IServiceInfoService serviceInfoService;
    private final IOrderFundFlowService orderFundFlowService;
    private final IPayoutOrderService payoutOrderService;
    private final IServiceMatchOrderService serviceMatchOrderService;
    private final ReadisUtil readisUtil;
    private final ISubjectsInfoService subjectsInfoService;
    private final IOrderLimitService orderLimitService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user) {


        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long tenantId = inParam.getTenantId();
        Long amount = inParam.getAmount();
        Long userId = inParam.getUserId();
        // 资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        try {
            // 通过手机获取用户信息
            //是否服务商
            boolean isService = false;
            int serviceType = 0;
            ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(userId);
            // 服务商
            if (serviceInfo != null) {
                isService = true;
                serviceType = serviceInfo.getServiceType();
            }
            //服务商
            if (isService) {
                this.payCashForService(inParam, userId, vehicleAffiliation,
                        amount, businessId, accountDatailsId, serviceType,user);
            } else {
                PayoutIntf pay = inParam.getPayoutIntf();
                if (pay == null) {
                    throw new BusinessException("未找到提现记录");
                }
                this.payCashForDriver(inParam, userId, vehicleAffiliation,
                        amount, businessId, accountDatailsId, pay,user);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {

        }
        return null;
    }


    public void payCashForService(ParametersNewDto inParam, Long userId, String vehicleAffiliation,
                                  Long amount, Long businessId, Long accountDatailsId,
                                  Integer serviceType,LoginInfo user) {

        Long tenantId = inParam.getTenantId();
        Long flowId = inParam.getFlowId();
        ServiceMatchOrder smo =serviceMatchOrderService.getById (flowId);
        //平台票
        if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
            //服务商提现
            PayoutIntf pay =inParam.getPayoutIntf();
            if (pay == null) {
                throw new BusinessException("未找到提现记录");
            }

            //资金流水操作
            OrderFundFlow offnew = new OrderFundFlow();
            offnew.setAmount(amount);//交易金额（单位分）
            offnew.setOrderId(smo.getOrderId());//订单ID
            offnew.setBusinessId(businessId);//业务类型
            offnew.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
            offnew.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
            offnew.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE, String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
            offnew.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);//科目ID
            offnew.setSubjectsName(subjectsInfoService.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_HA));//科目名称
            offnew.setBusiTable("payout_intf");//业务对象表
            offnew.setBusiKey(pay.getId());//业务流水ID
            offnew.setBackIncome(0L);
            offnew.setIncome(0L);
            offnew.setInoutSts("out");//收支状态:收in支out转io
            offnew.setPayState(1);//真正提现成功
            this.createOrderFundFlowNew(inParam, offnew,user);
            orderFundFlowService.saveOrUpdate(offnew);
            //订单限制表操作
            //开票平台虚拟提现到开票平台实体的时候操作
//	    		smo.setNoWithdrawAmount(smo.getNoWithdrawAmount() - offnew.getAmount());
//	    		smo.setWithdrawAmount(smo.getWithdrawAmount() + offnew.getAmount());
            smo.setAccountBalance(smo.getAccountBalance() - offnew.getAmount());
            serviceMatchOrderService.saveOrUpdate(smo);
//		    	OrderAccount orderAccount=orderAccountSV.queryOrderAccount(userId, vehicleAffiliation, tenantId);
//		    	orderAccount.setBalance(orderAccount.getBalance()-offnew.getAmount());//资金渠道开票平台的订单账户未提现金额要相应扣减
//		    	orderAccountSV.saveOrUpdate(orderAccount);
            //记录开票平台实体到提现方实 操作的订单，用于传参给支付平台
            PayoutOrder payoutOrder = new PayoutOrder();
            payoutOrder.setAmount(amount);
            payoutOrder.setUserId(userId);
            payoutOrder.setTenantId(smo.getTenantId());
            payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);//FICTITIOUS_OIL_TYPE
            if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.FICTITIOUS_OIL_TYPE);
            }
            payoutOrder.setVehicleAffiliation(vehicleAffiliation);
            payoutOrder.setBatchId(pay.getId());
            payoutOrder.setOrderId(smo.getOrderId());
            payoutOrder.setCreateTime(LocalDateTime.now());
            payoutOrderService.save(payoutOrder);
        } else {
            PayoutIntf pay = inParam.getPayoutIntf();
            if (pay == null) {
                throw new BusinessException("未找到提现记录");
            }
            //资金流水操作
            OrderFundFlow offnew = new OrderFundFlow();
            offnew.setAmount(amount);//交易金额（单位分）
            offnew.setOrderId(smo.getOrderId());//订单ID
            offnew.setBusinessId(businessId);//业务类型
            offnew.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
            offnew.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
            offnew.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
            offnew.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);//科目ID
            offnew.setSubjectsName(subjectsInfoService.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_HA));//科目名称
            offnew.setBusiTable("payout_intf");//业务对象表
            offnew.setBusiKey(pay.getId());//业务流水ID
            offnew.setBackIncome(0L);
            offnew.setIncome(0L);
            offnew.setToOrderId(smo.getId());
            offnew.setPayState(1);//真正提现成功
            this.createOrderFundFlowNew(inParam, offnew,user);
            orderFundFlowService.saveOrUpdate(offnew);
            //订单限制表操作
//    		smo.setNoWithdrawAmount(smo.getNoWithdrawAmount() - offnew.getAmount());
//    		smo.setWithdrawAmount(smo.getWithdrawAmount() + offnew.getAmount());
            serviceMatchOrderService.saveOrUpdate(smo);
            //记录开票平台实体到提现方实体  操作的订单，用于传参给支付平台
            PayoutOrder payoutOrder = new PayoutOrder();
            payoutOrder.setAmount(amount);
            payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
            if (serviceType == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.FICTITIOUS_OIL_TYPE);
            }
            payoutOrder.setVehicleAffiliation(vehicleAffiliation);
            payoutOrder.setBatchId(pay.getId());
            payoutOrder.setOrderId(smo.getOrderId());
            payoutOrder.setCreateTime(LocalDateTime.now());
            payoutOrderService.save(payoutOrder);
        }
    }


    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {

        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.getById(userId);
        long amount = inParam.getAmount();
        String vehicleAffiliation =inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if (currOper != null) {
            off.setOpId(currOper.getId());//操作人ID
            off.setOpName(currOper.getName());//操作人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if (off.getIncome() == null) {
            off.setIncome(0L);
        }
        if (off.getBackIncome() == null) {
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }



    //司机提现
    public void payCashForDriver(ParametersNewDto inParam,Long userId,String vehicleAffiliation,
                                 Long txnAmt,Long businessId,Long accountDatailsId,PayoutIntf pay,LoginInfo user) {
        //查询司机对应资金渠道订单
        Integer carUserType =inParam.getCarUserType();
        Long tenantId = inParam.getTenantId();
        //平台和非平台渠道
        if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
            OrderFundFlow off = new OrderFundFlow();
            Long orderId =inParam.getOrderId();
            if (pay.getOrderId() != null && pay.getOrderId() > 0L) {
                orderId = pay.getOrderId();
            }
            OrderLimit orderTemp =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
            if (orderTemp == null) {
                throw new BusinessException("未找到订单号为" + orderId + "的限制表");
            }
            //真正支付成功
            if (OrderAccountConst.COMMON_KEY.success.equals(inParam.getSuccess())) {
                off.setAmount(txnAmt);//交易金额（单位分）
                off.setOrderId(pay.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);//科目ID
                off.setSubjectsName(subjectsInfoService.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_HA));//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(pay.getId());//业务流水ID
                off.setBackIncome(0L);
                off.setIncome(0L);
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setPayState(1);//真正提现成功
                this.createOrderFundFlowNew(inParam,off,user);
                //记录开票平台实体到提现方实体  操作的订单，用于传参给支付平台
                PayoutOrder payoutOrder = new PayoutOrder();
                payoutOrder.setVehicleAffiliation(vehicleAffiliation);
                payoutOrder.setAmount(txnAmt);
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                payoutOrder.setBatchId(pay.getId());
                payoutOrder.setOrderId(orderTemp.getOrderId());
                payoutOrder.setCreateTime(LocalDateTime.now());
                payoutOrderService.save(payoutOrder);
                orderFundFlowService.saveOrUpdate(off);
                //订单限制表操作
                orderTemp.setAccountBalance(orderTemp.getAccountBalance()- off.getAmount());//操作订单未提现金额要相应扣减
                orderLimitService.saveOrUpdate(orderTemp);
//		    	OrderAccount orderAccount=orderAccountSV.getAccountUserId(userId, vehicleAffiliation, tenantId);
//		    	orderAccount.setBalance(orderAccount.getBalance()-off.getAmount());//资金渠道开票平台的订单账户未提现金额要相应扣减
//		    	orderAccountSV.saveOrUpdate(orderAccount);

            } else {
                off.setAmount(txnAmt);//交易金额（单位分）
                off.setOrderId(orderId);
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);//科目ID
                off.setSubjectsName(subjectsInfoService.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_HA));
                off.setSubjectsName("提现");//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(pay.getId());//业务流水ID
                off.setBackIncome(0L);
                off.setIncome(0L);
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setPayState(0);//发起提现
                this.createOrderFundFlowNew(inParam,off,user);
                //记录开票平台实体到提现方实体  操作的订单，用于传参给支付平台
                PayoutOrder payoutOrder = new PayoutOrder();
                payoutOrder.setAmount(txnAmt);
                payoutOrder.setUserId(userId);
                payoutOrder.setTenantId(orderTemp.getTenantId());
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                payoutOrder.setBatchId(pay.getId());
                payoutOrder.setOrderId(orderTemp.getOrderId());
                payoutOrder.setCreateTime(LocalDateTime.now());
                payoutOrder.setVehicleAffiliation(vehicleAffiliation);
                payoutOrderService.save(payoutOrder);
                //订单限制表操作
                orderTemp.setAccountBalance(orderTemp.getAccountBalance()- off.getAmount());//操作订单未提现金额要相应扣减
                orderLimitService.saveOrUpdate(orderTemp);
                orderFundFlowService.saveOrUpdate(off);
            }
        } else {
            List<OrderLimit> orderLimits = orderLimitService.getOrderLimit(userId,vehicleAffiliation,OrderAccountConst.NO_PAY.NO_PAY_CASH,pay.getPayTenantId(),pay.getUserType());
            orderLimitService.matchAmountToOrderLimit(txnAmt, 0L, 0L, orderLimits);
            //将提现金额分摊给订单
            for (OrderLimit olTemp : orderLimits) {
                if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                    //资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(txnAmt);//交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());//订单ID
                    off.setBusinessId(businessId);//业务类型
                    off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
                    off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                    off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                    off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_HA);//科目ID
                    off.setSubjectsName(subjectsInfoService.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_HA));//科目名称
                    off.setBusiTable("payout_intf");//业务对象表
                    off.setBusiKey(pay.getId());//业务流水ID
                    off.setBackIncome(0L);

                    off.setIncome(0L);
                    off.setInoutSts("out");//收支状态:收in支out转io
                    off.setPayState(1);//真正提现成功
                    this.createOrderFundFlowNew(inParam,off,user);
                    //订单限制表操作
                    olTemp.setAccountBalance(olTemp.getAccountBalance()- off.getAmount());//操作订单未提现金额要相应扣减

                    //记录开票平台实体到提现方实体  操作的订单，用于传参给支付平台
                    PayoutOrder payoutOrder = new PayoutOrder();
                    payoutOrder.setAmount(txnAmt);
                    payoutOrder.setUserId(userId);
                    payoutOrder.setVehicleAffiliation(vehicleAffiliation);
                    payoutOrder.setTenantId(olTemp.getTenantId());
                    payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
                    payoutOrder.setBatchId(pay.getId());
                    payoutOrder.setOrderId(olTemp.getOrderId());
                    payoutOrder.setCreateTime(LocalDateTime.now());
                    payoutOrderService.save(payoutOrder);
                    orderLimitService.saveOrUpdate(olTemp);
                    orderFundFlowService.saveOrUpdate(off);

                }
            }
        }
    }
}
