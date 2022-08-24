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
public class UpdateOrder {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final  ReadisUtil readisUtil;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;
    private final IOrderFundFlowService orderFundFlowService;


    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user){
        Long orderId =inParam.getOrderId();
        Long businessId =inParam.getBusinessId();
        Long accountDatailsId =inParam.getAccountDatailsId();
        Long tenantUserId =inParam.getTenantUserId();
        String tenantUserBill =inParam.getTenantUserBill();
        String tenantUserName =inParam.getTenantUserName();
        if (orderId <= 0L) {
            throw new BusinessException("预付业务订单ID不能为空!");
        }
        //查询订单限制数据
        OrderLimit ol = inParam.getOrderLimitBase();
        if (rels == null) {
            throw new BusinessException("预付明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW) {
                continue;
            }
            if (ol == null) {
                throw new BusinessException("订单信息不存在!");
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(ol.getTenantId());
            this.createOrderFundFlowNew(inParam,off,user);
            //修改订单现金、补贴增加(应收逾期)、到付款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                off.setInoutSts("in");
                ol.setOrderCash(ol.getOrderCash() + off.getAmount());
                ol.setNoPayCash(ol.getNoPayCash() + off.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getAmount());
            }
            //票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1689) {
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
            }
            //修改订单现金、补贴减少(应付逾期)、到付款减少
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW) {
                off.setInoutSts("in");
                ol.setDebtMoney(ol.getDebtMoney() + off.getAmount());
                ol.setNoPayDebt(ol.getNoPayDebt() + off.getAmount());
            }
            //车队应收应付逾期
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_UPP
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ETC_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_VIRTUALOIL_CARD_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_UPP
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_UPP) {
                off.setUserId(tenantUserId);
                off.setBillId(tenantUserBill);
                off.setUserName(tenantUserName);
            }
            //修改订单实体油减少
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_LOW) {
                off.setInoutSts("out");
                ol.setOrderOil(ol.getOrderOil() - off.getAmount());
                ol.setOrderEntityOil(ol.getOrderEntityOil() - off.getAmount());
            }
            //修改订单实体油增加
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP) {
                off.setInoutSts("in");
                ol.setOrderOil(ol.getOrderOil() + off.getAmount());
                ol.setOrderEntityOil(ol.getOrderEntityOil() + off.getAmount());
                ol.setCostEntityOil((ol.getCostEntityOil() == null ? 0L : ol.getCostEntityOil()) + off.getCost());
            }
            //修改订单虚拟油增加
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP) {
                off.setInoutSts("in");
                ol.setOrderOil(ol.getOrderOil() + off.getAmount());
                ol.setNoPayOil(ol.getNoPayOil() + off.getAmount());
            }
            //修改订单未付虚拟油减少
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW) {
                off.setInoutSts("out");
                ol.setOrderOil(ol.getOrderOil() - off.getAmount());
                ol.setNoPayOil(ol.getNoPayOil() - off.getAmount());
                List<OrderOilSource> orderOilSources =inParam.getSourceList();
                if (orderOilSources == null || orderOilSources.size() <= 0) {
                    throw new BusinessException("未找到订单来源记录");
                }
                List<OrderOilSource> tenantOrderOil = null;
                for (OrderOilSource source : orderOilSources) {
                    if (source.getMatchAmount() == null || source.getMatchAmount() <= 0L) {
                        continue;
                    }
                    long matchAmount = source.getMatchAmount();
                    long matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
                    long matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
                    long matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
                    if (String.valueOf(source.getOrderId()).equals(String.valueOf(source.getSourceOrderId()))) {

                    } else {
                        source.setNoPayOil(source.getNoPayOil() - matchNoPayOil);
                        source.setSourceAmount(source.getSourceAmount() - matchNoPayOil);
                        source.setNoCreditOil(source.getNoCreditOil() - matchNoCreditOil);
                        source.setCreditOil(source.getCreditOil() - matchNoCreditOil);
                        source.setNoRebateOil(source.getNoRebateOil() - matchNoRebateOil);
                        source.setRebateOil(source.getRebateOil() - matchNoRebateOil);
                        tenantOrderOil = orderOilSourceService.getOrderOilSource(tenantUserId, source.getSourceOrderId(), source.getVehicleAffiliation(), source.getSourceTenantId(),-1);
                        if (tenantOrderOil != null && tenantOrderOil.size() >0) {
                            OrderOilSource orderOil = null;
                            for (OrderOilSource oil : tenantOrderOil) {
                                long tempPaidOil = (oil.getPaidOil() == null ? 0L : oil.getPaidOil());
                                if (tempPaidOil >= source.getMatchAmount()) {
                                    orderOil = oil;
                                    break;
                                }
                            }
                            if (orderOil != null) {
                                orderOil.setNoPayOil(orderOil.getNoPayOil() + matchNoPayOil);
                                orderOil.setNoCreditOil(orderOil.getNoCreditOil() + matchNoCreditOil);
                                orderOil.setNoRebateOil(orderOil.getNoRebateOil() + matchNoRebateOil);
                                orderOil.setPaidOil(orderOil.getPaidOil() - matchNoPayOil);
                                orderOil.setPaidCreditOil(orderOil.getPaidCreditOil() - matchNoCreditOil);
                                orderOil.setPaidRebateOil(orderOil.getPaidRebateOil() - matchNoRebateOil);
                                orderOilSourceService.saveOrUpdate(orderOil);
                                OrderLimit tempLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, orderOil.getOrderId(),-1);
                                if (tempLimit == null) {
                                    throw new BusinessException("未找到单号为:" + orderOil.getOrderId() +"的订单限制表");
                                }
                                if (String.valueOf(orderOil.getOrderId()).equals(String.valueOf(orderOil.getSourceOrderId()))) {
                                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - source.getMatchAmount());
                                } else {
                                    tempLimit.setWithdrawOil(tempLimit.getWithdrawOil() - source.getMatchAmount());
                                }
                                tempLimit.setPaidOil(tempLimit.getPaidOil() - source.getMatchAmount());
                                tempLimit.setNoPayOil(tempLimit.getNoPayOil() + source.getMatchAmount());
                                orderLimitService.saveOrUpdate(tempLimit);
                                //生成资金流水
                                OrderFundFlow offOil = new OrderFundFlow();
                                offOil.setAmount(source.getMatchAmount());//交易金额（单位分）
                                offOil.setOrderId(tempLimit.getOrderId());//订单ID
                                offOil.setBusinessId(businessId);//业务类型
                                offOil.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
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
                                throw new BusinessException("未找到租户订单油来源");
                            }
                        }
                    }
					/*source.setNoPayOil(source.getNoPayOil() - matchNoPayOil);
					source.setSourceAmount(source.getSourceAmount() - matchNoPayOil);
					source.setNoCreditOil(source.getNoCreditOil() - matchNoCreditOil);
					source.setCreditOil(source.getCreditOil() - matchNoCreditOil);
					source.setNoRebateOil(source.getNoRebateOil() - matchNoRebateOil);
					source.setRebateOil(source.getRebateOil() - matchNoRebateOil);*/
                    orderOilSourceService.saveOrUpdate(source);
                }
            }
            //修改订单etc、路桥费减少
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW ) {
                off.setInoutSts("out");
                ol.setOrderEtc(ol.getOrderEtc() - off.getAmount());
                ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
            }
            //修改订单路桥费减少
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_LOW) {
                off.setInoutSts("out");
            }
            //修改订单etc
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP ) {
                off.setInoutSts("in");
                ol.setOrderEtc(ol.getOrderEtc() + off.getAmount());
                ol.setNoPayEtc(ol.getNoPayEtc() + off.getAmount());
            }
            //修改订单路桥费增加
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_UPP) {
                off.setInoutSts("in");
            }
            //修改订单etc、虚拟油减少(应付逾期)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ETC_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_CARD_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_VIRTUALOIL_LOW) {
                off.setInoutSts("in");
                ol.setDebtMoney(ol.getDebtMoney() + off.getAmount());
                ol.setNoPayDebt(ol.getNoPayDebt() + off.getAmount());
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
        userDataInfo = userDataInfoService.getById(userId);
        Long amount =inParam.getAmount();
        String vehicleAffiliation =inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        if(userDataInfo == null){
            off.setUserName(null);//用户名
        }else {
            off.setUserName(userDataInfo.getLinkman());//用户名
        }
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
