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
 * @date 2022/6/13 16:11
 */
@Component
public class ForceZhangPing {

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
        Long orderId = inParam.getOrderId();
        Long flowId = inParam.getFlowId();
        String batchId = inParam.getBatchId();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        String zhangPingType =inParam.getZhangPingType();
        String accountType = inParam.getAccountType();
        String isDriverOrderType = inParam.getIsDriverOrderType();
        if (rels == null) {
            throw new BusinessException("结余明细不能为空!");
        }
        List<OrderLimit> orderLimits = null;
        if (String.valueOf(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE1).equals(isDriverOrderType)) {//司机
            orderLimits = orderLimitMapper.getOrderLimitUserId(orderId,userId, user.getTenantId(), -1);
        }
        if (String.valueOf(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE2).equals(isDriverOrderType)) {//经济人
            orderLimits = orderLimitMapper.getFaceOrderLimit(inParam.getOrderId(), inParam.getUserId(), inParam.getUserType());
        }
        OrderLimit olTemp = orderLimits.get(0);
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if (EnumConsts.ZHANG_PING.ZHANH_PING_TYPE1.equals(zhangPingType)) {//单账户平账
                //未付现金
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_BALANCE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_BALANCE_OUT) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(rel.getBusinessId());// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("zhang_ping_order_record");// 业务对象表
                    off.setBusiKey(flowId);// 业务流水ID
                    off.setIncome(rel.getIncome());
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_BALANCE_IN ) {
                        off.setInoutSts("in");// 收支状态:收in支out转io
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_BALANCE_OUT ) {
                        off.setInoutSts("out");// 收支状态:收in支out转io
                    }
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    if (String.valueOf(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE1).equals(isDriverOrderType)) {
                        olTemp.setNoPayCash(olTemp.getNoPayCash() - off.getAmount());
                    }
                    olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() - off.getAmount());
                    orderLimitService.saveOrUpdate(olTemp);
                }
                //未付油
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(rel.getBusinessId());// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("zhang_ping_order_record");// 业务对象表
                    off.setBusiKey(flowId);// 业务流水ID
                    off.setIncome(rel.getIncome());
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_IN ) {
                        off.setInoutSts("in");// 收支状态:收in支out转io
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT ) {
                        off.setInoutSts("out");// 收支状态:收in支out转io
                    }
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    olTemp.setNoPayOil(olTemp.getNoPayOil() - off.getAmount());
                    orderLimitService.saveOrUpdate(olTemp);
                }
                //未付ETC
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(rel.getBusinessId());// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("zhang_ping_order_record");// 业务对象表
                    off.setBusiKey(flowId);// 业务流水ID
                    off.setIncome(rel.getIncome());
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_IN ) {
                        off.setInoutSts("in");// 收支状态:收in支out转io
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT ) {
                        off.setInoutSts("out");// 收支状态:收in支out转io
                    }
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    olTemp.setNoPayEtc(olTemp.getNoPayEtc() - off.getAmount());
                    orderLimitService.saveOrUpdate(olTemp);
                }
                //未付未到期或欠款
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT) {
                    // 资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());// 订单ID
                    off.setBusinessId(rel.getBusinessId());// 业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName());// 账户类型
                    off.setSubjectsId(rel.getSubjectsId());// 科目ID
                    off.setSubjectsName(rel.getSubjectsName());// 科目名称
                    off.setBusiTable("zhang_ping_order_record");// 业务对象表
                    off.setBusiKey(flowId);// 业务流水ID
                    off.setIncome(rel.getIncome());
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_IN ) {
                        off.setInoutSts("in");// 收支状态:收in支out转io
                    } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT ) {
                        off.setInoutSts("out");// 收支状态:收in支out转io
                    }
                    this.createOrderFundFlow(inParam, off, user);
                    orderFundFlowService.saveOrUpdate(off);
                    // 订单限制表操作
                    if (String.valueOf(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE1).equals(isDriverOrderType)) {
                        if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_IN ) {
                            olTemp.setNoPayDebt(olTemp.getNoPayDebt() - off.getAmount());
                        } else if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT ) {
                            olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                        }
                    }
                    orderLimitService.saveOrUpdate(olTemp);
                }

            } else if (EnumConsts.ZHANG_PING.ZHANH_PING_TYPE2.equals(zhangPingType)) {//选择账户平账

                if (rel.getSubjectsId() == EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.SELECT_ZHANG_PING_MARGIN_OUT) {
                    //强制账户
                    if (String.valueOf(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE1).equals(accountType)) {
                        // 资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());// 订单ID
                        off.setBusinessId(rel.getBusinessId());// 业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName()+"欠款");// 账户类型
                        off.setSubjectsId(rel.getSubjectsId());// 科目ID
                        off.setSubjectsName(rel.getSubjectsName());// 科目名称
                        off.setBusiTable("zhang_ping_order_record");// 业务对象表
                        off.setBusiKey(flowId);// 业务流水ID
                        off.setIncome(rel.getIncome());
                        off.setInoutSts("in");// 收支状态:收in支out转io
                        this.createOrderFundFlow(inParam, off, user);
                        orderFundFlowService.saveOrUpdate(off);
                        // 订单限制表操作
                        olTemp.setNoPayDebt(olTemp.getNoPayDebt() - off.getAmount());
                        olTemp.setPaidDebt(olTemp.getPaidDebt() + off.getAmount());
                        orderLimitService.saveOrUpdate(olTemp);
                    }
                    //勾选的账户
                    if (String.valueOf(EnumConsts.ZHANG_PING.ZHANH_PING_ACCOUNT_TYPE2).equals(accountType)) {
                        // 资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());// 订单ID
                        off.setBusinessId(rel.getBusinessId());// 业务类型
                        off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER",String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                        off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE",String.valueOf(off.getBookType())).getCodeName()+"冲抵欠款");// 账户类型
                        off.setSubjectsId(rel.getSubjectsId());// 科目ID
                        off.setSubjectsName(rel.getSubjectsName());// 科目名称
                        off.setBusiTable("zhang_ping_order_record");// 业务对象表
                        off.setBusiKey(flowId);// 业务流水ID
                        off.setIncome(rel.getIncome());
                        off.setInoutSts("out");// 收支状态:收in支out转io
                        this.createOrderFundFlow(inParam, off, user);
                        orderFundFlowService.saveOrUpdate(off);
                        // 订单限制表操作
                        if (String.valueOf(EnumConsts.ZHANG_PING.IS_DRIVER_ORDER_TYPE1).equals(isDriverOrderType)) {
                            olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                            olTemp.setPaidFinalPay(olTemp.getPaidFinalPay() + off.getAmount());
                        }
                        orderLimitService.saveOrUpdate(olTemp);
                    }
                }
            }
        }
        return null;
    }

    private OrderFundFlow createOrderFundFlow(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user){
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
