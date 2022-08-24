package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
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
public class CancelTheOrder {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final IOrderLimitService orderLimitService;
    private final ReadisUtil readisUtil;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderFundFlowService orderFundFlowService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user) {
        Long orderId = inParam.getOrderId();
        Long businessId =inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();//
        Long userId = inParam.getUserId();
        Long tenantUserId = inParam.getTenantUserId();
        String tenantUserBill = inParam.getTenantUserBill();
        String tenantUserName =inParam.getTenantUserName();
        if (orderId == 0L) {
            throw new BusinessException("预付业务订单ID不能为空!");
        }
        //查询订单限制数据
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("订单信息不存在!");
        }
        if (rels == null) {
            throw new BusinessException("预付明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId( CommonUtil.createSoNbr() + "");
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L || businessId != EnumConsts.PayInter.CANCEL_THE_ORDER) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(ol.getTenantId());
            off.setInoutSts("out");
            this.createOrderFundFlowNew(inParam,off,user);
            //撤单现金、补贴、到付款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_RECEIVABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_RECEIVABLE_OUT) {
                ol.setNoPayCash(ol.getNoPayCash() - off.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - off.getAmount());
            }
            //撤单票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1690) {
                ol.setServiceFee(ol.getServiceFee() - off.getAmount());
            }
            //车队应付逾期减
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_OUT) {
                off.setUserId(tenantUserId);
                off.setBillId(tenantUserBill);
                off.setUserName(tenantUserName);
            }
            //撤单实体油
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ENTITY_OIL || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_ENTITY_OIL) {
                ol.setOrderEntityOil(ol.getOrderEntityOil() - off.getAmount());
            }
            //撤单虚拟油
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL) {
                ol.setNoPayOil(ol.getNoPayOil() - off.getAmount());
                List<OrderOilSource> orderOilSources =inParam.getSourceList();
                if (orderOilSources == null || orderOilSources.size() <= 0) {
                    throw new BusinessException("未找到订单来源记录");
                }
                List<OrderOilSource> tenantOrderOil = null;
                for (OrderOilSource source : orderOilSources) {
                    if (source.getOrderId().longValue() != source.getSourceOrderId().longValue()) {
                        tenantOrderOil = orderOilSourceService.getOrderOilSource(tenantUserId, source.getSourceOrderId(), source.getVehicleAffiliation(), source.getSourceTenantId(),-1);
                        if (tenantOrderOil != null && tenantOrderOil.size() >0) {
                            OrderOilSource orderOil = null;
                            for (OrderOilSource oil : tenantOrderOil) {
                                if (String.valueOf(oil.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {
                                    orderOil = oil;
                                    break;
                                }
                            }
                            if (orderOil != null) {
                                orderOil.setNoPayOil(orderOil.getNoPayOil() + source.getNoPayOil());
                                orderOil.setNoCreditOil(orderOil.getNoCreditOil() + source.getNoCreditOil());
                                orderOil.setNoRebateOil(orderOil.getNoRebateOil() + source.getNoRebateOil());
                                if (orderOil.getPaidOil() > 0) {//判断来源订单是否提前撤单了，如果已经撤单了，就不用减了
                                    orderOil.setPaidOil(orderOil.getPaidOil() - source.getNoPayOil());
                                }
                                if (orderOil.getPaidCreditOil() > 0) {
                                    orderOil.setPaidCreditOil(orderOil.getPaidCreditOil() - source.getNoCreditOil());
                                }
                                if (orderOil.getPaidRebateOil() > 0) {
                                    orderOil.setPaidRebateOil(orderOil.getPaidRebateOil() - source.getNoRebateOil());
                                }
                                orderOilSourceService.saveOrUpdate(orderOil);
                                OrderLimit tempLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, orderOil.getOrderId(),-1);
                                if (tempLimit == null) {
                                    throw new BusinessException("未找到单号为:" + orderOil.getOrderId() +"的订单限制表");
                                }
                                if (String.valueOf(orderOil.getOrderId()).equals(String.valueOf(orderOil.getSourceOrderId()))) {
                                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                } else {
                                    tempLimit.setWithdrawOil(tempLimit.getWithdrawOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                }
                                tempLimit.setPaidOil(tempLimit.getPaidOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                tempLimit.setNoPayOil(tempLimit.getNoPayOil() + (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                orderLimitService.saveOrUpdate(tempLimit);
                                //生成资金流水
                                OrderFundFlow offOil = new OrderFundFlow();
                                offOil.setAmount(source.getNoPayOil());//交易金额（单位分）
                                offOil.setOrderId(tempLimit.getOrderId());//订单ID
                                offOil.setBusinessId(businessId);//业务类型
                                offOil.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//业务名称
                                offOil.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                                offOil.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
                                offOil.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_CANCEL_ORDER);//科目ID
                                offOil.setSubjectsName(rel.getSubjectsName());//科目名称
                                offOil.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                                offOil.setBusiKey(accountDatailsId);//业务流水ID
                                offOil.setTenantId(tempLimit.getTenantId());
                                offOil.setInoutSts("in");
                                this.createOrderFundFlowNew(inParam,offOil,user);
                                offOil.setUserId(tenantUserId);
                                offOil.setUserName(tenantUserName);
                                offOil.setBillId(tenantUserBill);
                                orderFundFlowService.saveOrUpdate(offOil);
                            } else {
                                throw new BusinessException("根据用户id" + tenantUserId + "订单号：" + source.getSourceOrderId() + " 未找到订单油来源记录");
                            }
                        } else {
                            throw new BusinessException("根据用户id" + tenantUserId + "订单号：" + source.getSourceOrderId() + " 未找到订单油来源记录");
                        }
                    }
                    source.setNoPayOil(0L);
                    source.setPaidOil(0L);
                    source.setSourceAmount(0L);
                    source.setNoCreditOil(0L);
                    source.setPaidCreditOil(0L);
                    source.setCreditOil(0L);
                    source.setNoRebateOil(0L);
                    source.setPaidRebateOil(0L);
                    source.setRebateOil(0L);
                    orderOilSourceService.saveOrUpdate(source);
                }
            }
            //撤单etc
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC) {
                ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
            }
            //未到期抵扣欠款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_DEDUCTION
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_DEDUCTION ||  rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_DEDUCTION
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_DEDUCTION  || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_DEDUCTION
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_IN  || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_PAYABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_PAYABLE_IN  || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_PAYABLE_IN  || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_IN) {
                ol.setDebtMoney(ol.getDebtMoney() + off.getAmount());
                ol.setNoPayDebt(ol.getNoPayDebt() + off.getAmount());
                off.setInoutSts("in");
            }
            //车队记录
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_RECEIVABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL_RECEIVABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC_RECEIVABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_VIRTUAL_OIL_RECEIVABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_IN
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_RECEIVABLE_IN) {
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantUserBill);
                off.setUserName(tenantUserName);
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(ol);
        }

        return null;
    }


    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {
        if(off==null){
            off = new OrderFundFlow();
        }
        long userId =inParam.getUserId();
        String billId =inParam.getBillId();
        String batchId =inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.get(userId);
        Long amount =inParam.getAmount();
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

}
