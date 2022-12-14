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
            throw new BusinessException("??????????????????ID????????????!");
        }
        //????????????????????????
        OrderLimit ol =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        if (ol == null) {
            throw new BusinessException("?????????????????????!");
        }
        if (rels == null) {
            throw new BusinessException("????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId( CommonUtil.createSoNbr() + "");
        for (BusiSubjectsRel rel : rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L || businessId != EnumConsts.PayInter.CANCEL_THE_ORDER) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//???????????????????????????
            off.setOrderId(orderId);//??????ID
            off.setBusinessId(businessId);//????????????
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//????????????
            off.setBookType(Long.parseLong(rel.getBookType()));//????????????
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
            off.setSubjectsId(rel.getSubjectsId());//??????ID
            off.setSubjectsName(rel.getSubjectsName());//????????????
            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
            off.setBusiKey(accountDatailsId);//????????????ID
            off.setTenantId(ol.getTenantId());
            off.setInoutSts("out");
            this.createOrderFundFlowNew(inParam,off,user);
            //?????????????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_RECEIVABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_RECEIVABLE_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_RECEIVABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_RECEIVABLE_OUT) {
                ol.setNoPayCash(ol.getNoPayCash() - off.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - off.getAmount());
            }
            //?????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1690) {
                ol.setServiceFee(ol.getServiceFee() - off.getAmount());
            }
            //?????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_CASH_PAYABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_MASTERCASH_PAYABLE_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_SALVECASH_PAYABLE_OUT || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ARRIVE_PAYABLE_OUT) {
                off.setUserId(tenantUserId);
                off.setBillId(tenantUserBill);
                off.setUserName(tenantUserName);
            }
            //???????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ENTITY_OIL || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_OWN_ENTITY_OIL) {
                ol.setOrderEntityOil(ol.getOrderEntityOil() - off.getAmount());
            }
            //???????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_VIRTUAL_OIL || rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_FICTITIOUS_OIL) {
                ol.setNoPayOil(ol.getNoPayOil() - off.getAmount());
                List<OrderOilSource> orderOilSources =inParam.getSourceList();
                if (orderOilSources == null || orderOilSources.size() <= 0) {
                    throw new BusinessException("???????????????????????????");
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
                                if (orderOil.getPaidOil() > 0) {//?????????????????????????????????????????????????????????????????????????????????
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
                                    throw new BusinessException("??????????????????:" + orderOil.getOrderId() +"??????????????????");
                                }
                                if (String.valueOf(orderOil.getOrderId()).equals(String.valueOf(orderOil.getSourceOrderId()))) {
                                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                } else {
                                    tempLimit.setWithdrawOil(tempLimit.getWithdrawOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                }
                                tempLimit.setPaidOil(tempLimit.getPaidOil() - (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                tempLimit.setNoPayOil(tempLimit.getNoPayOil() + (source.getNoPayOil() + source.getNoCreditOil() + source.getNoRebateOil()));
                                orderLimitService.saveOrUpdate(tempLimit);
                                //??????????????????
                                OrderFundFlow offOil = new OrderFundFlow();
                                offOil.setAmount(source.getNoPayOil());//???????????????????????????
                                offOil.setOrderId(tempLimit.getOrderId());//??????ID
                                offOil.setBusinessId(businessId);//????????????
                                offOil.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(businessId)).getCodeName());//????????????
                                offOil.setBookType(Long.parseLong(rel.getBookType()));//????????????
                                offOil.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
                                offOil.setSubjectsId(EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_CANCEL_ORDER);//??????ID
                                offOil.setSubjectsName(rel.getSubjectsName());//????????????
                                offOil.setBusiTable("ACCOUNT_DETAILS");//???????????????
                                offOil.setBusiKey(accountDatailsId);//????????????ID
                                offOil.setTenantId(tempLimit.getTenantId());
                                offOil.setInoutSts("in");
                                this.createOrderFundFlowNew(inParam,offOil,user);
                                offOil.setUserId(tenantUserId);
                                offOil.setUserName(tenantUserName);
                                offOil.setBillId(tenantUserBill);
                                orderFundFlowService.saveOrUpdate(offOil);
                            } else {
                                throw new BusinessException("????????????id" + tenantUserId + "????????????" + source.getSourceOrderId() + " ??????????????????????????????");
                            }
                        } else {
                            throw new BusinessException("????????????id" + tenantUserId + "????????????" + source.getSourceOrderId() + " ??????????????????????????????");
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
            //??????etc
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CANCEL_ORDER_ETC) {
                ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
            }
            //?????????????????????
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
            //????????????
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


    /*??????????????????????????????*/
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
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        off.setUserName(userDataInfo.getLinkman());//?????????
        off.setBatchId(batchId);//??????
        off.setBatchAmount(amount);//???????????????
        if (currOper != null) {
            off.setOpId(currOper.getId());//?????????ID
            off.setOpName(currOper.getName());//?????????
        }
        off.setOpDate(LocalDateTime.now());//????????????
        off.setVehicleAffiliation(vehicleAffiliation);//????????????
        if (off.getIncome() == null) {
            off.setIncome(0L);
        }
        if (off.getBackIncome() == null) {
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//?????????????????????????????????
        return off;
    }

}
