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
            throw new BusinessException("??????????????????ID????????????!");
        }
        //????????????????????????
        OrderLimit ol = inParam.getOrderLimitBase();
        if (rels == null) {
            throw new BusinessException("????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        for (BusiSubjectsRel rel : rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP_OUT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW) {
                continue;
            }
            if (ol == null) {
                throw new BusinessException("?????????????????????!");
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//???????????????????????????
            off.setOrderId(orderId);//??????ID
            off.setBusinessId(businessId);//????????????
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//????????????
            off.setBookType(Long.parseLong(rel.getBookType()));//????????????
            off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
            off.setSubjectsId(rel.getSubjectsId());//??????ID
            off.setSubjectsName(rel.getSubjectsName());//????????????
            off.setBusiTable("ACCOUNT_DETAILS");//???????????????
            off.setBusiKey(accountDatailsId);//????????????ID
            off.setTenantId(ol.getTenantId());
            this.createOrderFundFlowNew(inParam,off,user);
            //?????????????????????????????????(????????????)????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_CASH_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_MASTERSUBSIDY_UPP
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_SLAVESUBSIDY_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_RECEIVABLE_ARRIVE_UPP) {
                off.setInoutSts("in");
                ol.setOrderCash(ol.getOrderCash() + off.getAmount());
                ol.setNoPayCash(ol.getNoPayCash() + off.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getAmount());
            }
            //???????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1689) {
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
            }
            //?????????????????????????????????(????????????)??????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_CASH_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_MASTERSUBSIDY_LOW
                    || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_SLAVESUBSIDY_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_PAYABLE_ARRIVE_LOW) {
                off.setInoutSts("in");
                ol.setDebtMoney(ol.getDebtMoney() + off.getAmount());
                ol.setNoPayDebt(ol.getNoPayDebt() + off.getAmount());
            }
            //????????????????????????
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
            //???????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_LOW) {
                off.setInoutSts("out");
                ol.setOrderOil(ol.getOrderOil() - off.getAmount());
                ol.setOrderEntityOil(ol.getOrderEntityOil() - off.getAmount());
            }
            //???????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ENTITYOIL_CARD_UPP) {
                off.setInoutSts("in");
                ol.setOrderOil(ol.getOrderOil() + off.getAmount());
                ol.setOrderEntityOil(ol.getOrderEntityOil() + off.getAmount());
                ol.setCostEntityOil((ol.getCostEntityOil() == null ? 0L : ol.getCostEntityOil()) + off.getCost());
            }
            //???????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_UPP || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_UPP) {
                off.setInoutSts("in");
                ol.setOrderOil(ol.getOrderOil() + off.getAmount());
                ol.setNoPayOil(ol.getNoPayOil() + off.getAmount());
            }
            //?????????????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_LOW || rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUALOIL_CARD_LOW) {
                off.setInoutSts("out");
                ol.setOrderOil(ol.getOrderOil() - off.getAmount());
                ol.setNoPayOil(ol.getNoPayOil() - off.getAmount());
                List<OrderOilSource> orderOilSources =inParam.getSourceList();
                if (orderOilSources == null || orderOilSources.size() <= 0) {
                    throw new BusinessException("???????????????????????????");
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
                                    throw new BusinessException("??????????????????:" + orderOil.getOrderId() +"??????????????????");
                                }
                                if (String.valueOf(orderOil.getOrderId()).equals(String.valueOf(orderOil.getSourceOrderId()))) {
                                    tempLimit.setNoWithdrawOil(tempLimit.getNoWithdrawOil() - source.getMatchAmount());
                                } else {
                                    tempLimit.setWithdrawOil(tempLimit.getWithdrawOil() - source.getMatchAmount());
                                }
                                tempLimit.setPaidOil(tempLimit.getPaidOil() - source.getMatchAmount());
                                tempLimit.setNoPayOil(tempLimit.getNoPayOil() + source.getMatchAmount());
                                orderLimitService.saveOrUpdate(tempLimit);
                                //??????????????????
                                OrderFundFlow offOil = new OrderFundFlow();
                                offOil.setAmount(source.getMatchAmount());//???????????????????????????
                                offOil.setOrderId(tempLimit.getOrderId());//??????ID
                                offOil.setBusinessId(businessId);//????????????
                                offOil.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//????????????
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
                                throw new BusinessException("??????????????????????????????");
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
            //????????????etc??????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ETC_LOW ) {
                off.setInoutSts("out");
                ol.setOrderEtc(ol.getOrderEtc() - off.getAmount());
                ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
            }
            //???????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_LOW) {
                off.setInoutSts("out");
            }
            //????????????etc
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_ETC_UPP ) {
                off.setInoutSts("in");
                ol.setOrderEtc(ol.getOrderEtc() + off.getAmount());
                ol.setNoPayEtc(ol.getNoPayEtc() + off.getAmount());
            }
            //???????????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.UPDATE_ORDER_LUQIAO_UPP) {
                off.setInoutSts("in");
            }
            //????????????etc??????????????????(????????????)
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



    /*??????????????????????????????*/
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
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        if(userDataInfo == null){
            off.setUserName(null);//?????????
        }else {
            off.setUserName(userDataInfo.getLinkman());//?????????
        }
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
