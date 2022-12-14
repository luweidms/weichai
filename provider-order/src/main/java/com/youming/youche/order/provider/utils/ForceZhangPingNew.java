package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;

@Component
@RequiredArgsConstructor
public class ForceZhangPingNew {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    private final ReadisUtil readisUtil;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;
    private final IOilCardManagementService oilCardManagementService;
    private final IRechargeOilSourceService rechargeOilSourceService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user){

        Long userId =inParam.getUserId();
        Long flowId =inParam.getFlowId();
        Long tenantId =inParam.getTenantId();
        String zhangPingType =inParam.getZhangPingType();
        OrderLimit olTemp = inParam.getOrderLimitBase();
        List<OrderOilSource> list =inParam.getSourceList();
        long tenantUserId = inParam.getTenantUserId();
        String type =inParam.getType();
        RechargeOilSource rechargeOilSource = inParam.getRechargeOilSource();

        if (rels == null) {
            throw new BusinessException("????????????????????????!");
        }
        if (userId <= 0) {
            throw new BusinessException("???????????????id");
        }
        if (StringUtils.isEmpty(zhangPingType)) {
            throw new BusinessException("?????????????????????");
        }
        for (BusiSubjectsRel rel : rels) {
            // ?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if ("1".equals(type)) {//?????????
                if (olTemp == null) {
                    throw new BusinessException("????????????????????????");
                }
                // ??????????????????
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// ???????????????????????????
                off.setOrderId(olTemp.getOrderId());// ??????ID
                off.setBusinessId(rel.getBusinessId());// ????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());// ????????????
                off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());// ????????????
                off.setSubjectsId(rel.getSubjectsId());// ??????ID
                off.setSubjectsName(rel.getSubjectsName());// ????????????
                off.setBusiTable("zhang_ping_order_record");// ???????????????
                off.setBusiKey(flowId);// ????????????ID
                off.setIncome(rel.getIncome());
                off.setTenantId(tenantId);
                off.setInoutSts("out");// ????????????:???in???out???io
                this.createOrderFundFlowNew(inParam, off,user);
                orderFundFlowService.saveOrUpdate(off);
                //???????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_OIL_OUT) {
                    // ?????????????????????
                    olTemp.setNoPayOil(olTemp.getNoPayOil() - off.getAmount());
                    if (list == null) {
                        throw new BusinessException("???????????????????????????????????????????????????");
                    }
                    for (OrderOilSource oos : list) {
                        if (oos.getOrderId().longValue() != oos.getSourceOrderId().longValue()) {
                            OrderOilSource orderOilSource = orderOilSourceService.getOrderOilSource(tenantUserId, oos.getSourceOrderId(), oos.getSourceOrderId(), oos.getVehicleAffiliation(), oos.getSourceTenantId(),-1,oos.getOilAccountType(),oos.getOilConsumer(),oos.getOilBillType(),oos.getOilAffiliation());
                            if (orderOilSource == null) {
                                throw new BusinessException("????????????id???" + tenantUserId + " ????????????" + oos.getSourceOrderId() + "???????????????" + oos.getVehicleAffiliation() + "??????????????????" + oos.getVehicleAffiliation() + "??????id???" + oos.getSourceTenantId() + " ??????????????????" + oos.getOilAccountType() + " ??????????????????" + oos.getOilConsumer() + " ??????????????????" + oos.getOilBillType() + "??????????????????????????????");
                            }
                            long oil = ( oos.getNoPayOil() + oos.getNoCreditOil() + oos.getNoRebateOil());
                            orderOilSource.setNoPayOil(orderOilSource.getNoPayOil() + oos.getNoPayOil());
                            orderOilSource.setNoCreditOil(orderOilSource.getNoCreditOil() + oos.getNoCreditOil());
                            orderOilSource.setNoRebateOil(orderOilSource.getNoRebateOil() + oos.getNoRebateOil());
                            orderOilSource.setPaidOil(orderOilSource.getPaidOil() - oos.getNoPayOil());
                            orderOilSource.setPaidCreditOil(orderOilSource.getPaidCreditOil() - oos.getNoCreditOil());
                            orderOilSource.setPaidRebateOil(orderOilSource.getPaidRebateOil() - oos.getNoRebateOil());
                            orderOilSourceService.saveOrUpdate(orderOilSource);
                            OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, oos.getSourceOrderId(),-1);
                            if (orderLimit == null) {
                                throw new BusinessException("????????????????????????" + oos.getOrderId() + "?????????????????????");
                            }
                            orderLimit.setNoPayOil(orderLimit.getNoPayOil() + oil);
                            orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - oil);
                            orderLimit.setPaidOil(orderLimit.getPaidOil() - oil);
                            orderLimitService.saveOrUpdate(orderLimit);
                        }
                        oos.setNoPayOil(0l);
                        oos.setNoCreditOil(0l);
                        oos.setNoRebateOil(0l);
                        orderOilSourceService.saveOrUpdate(oos);
                    }
                }
                //??????ETC
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_ETC_OUT) {
                    // ?????????????????????
                    olTemp.setNoPayEtc(olTemp.getNoPayEtc() - off.getAmount());
                }
                //????????????????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_MARGIN_OUT) {
                    // ?????????????????????
                    olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                }
                //???????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_OUT) {
                    // ?????????????????????
                    olTemp.setPledgeOilcardFee(olTemp.getPledgeOilcardFee() - off.getAmount());
                    //????????????????????????
                    List<OilCardManagement> oilCardByOrderId = oilCardManagementService.getOilCardByOrderId(olTemp.getOrderId(), olTemp.getTenantId());
                    if (oilCardByOrderId != null && oilCardByOrderId.size() > 0){
                        for (OilCardManagement cardNum : oilCardByOrderId) {
                            cardNum.setPledgeOrderId(-1L);
                            cardNum.setPledgeFee(-1L);
                            oilCardManagementService.saveOrUpdate(cardNum);
                        }
                    }
                }
                orderLimitService.saveOrUpdate(olTemp);
            }
            if ("2".equals(type)) {
                //?????????????????????
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.ZHANG_PING_RECHARGE_OIL_OUT) {
                    if (rechargeOilSource == null) {
                        throw new BusinessException("???????????????????????????????????????????????????");
                    }
                    if (!rechargeOilSource.getRechargeOrderId().equals(rechargeOilSource.getSourceOrderId())) {
                        OrderOilSource orderOilSource = orderOilSourceService.getOrderOilSource(tenantUserId, Long.parseLong(rechargeOilSource.getSourceOrderId()), Long.parseLong(rechargeOilSource.getSourceOrderId()), rechargeOilSource.getVehicleAffiliation(), rechargeOilSource.getSourceTenantId(),-1,rechargeOilSource.getOilAccountType(),rechargeOilSource.getOilConsumer(),rechargeOilSource.getOilBillType(),rechargeOilSource.getOilAffiliation());
                        if (orderOilSource == null) {
                            throw new BusinessException("????????????id???" + tenantUserId + " ????????????" + rechargeOilSource.getSourceOrderId() + "???????????????" + rechargeOilSource.getVehicleAffiliation() + "??????????????????" + rechargeOilSource.getVehicleAffiliation() + "??????id???" + rechargeOilSource.getSourceTenantId() + "??????????????????????????????");
                        }
                        long oil = ( rechargeOilSource.getNoPayOil() + rechargeOilSource.getNoCreditOil() + rechargeOilSource.getNoRebateOil());
                        orderOilSource.setNoPayOil(orderOilSource.getNoPayOil() + rechargeOilSource.getNoPayOil());
                        orderOilSource.setNoCreditOil(orderOilSource.getNoCreditOil() + rechargeOilSource.getNoCreditOil());
                        orderOilSource.setNoRebateOil(orderOilSource.getNoRebateOil() + rechargeOilSource.getNoRebateOil());
                        orderOilSource.setPaidOil(orderOilSource.getPaidOil() - rechargeOilSource.getNoPayOil());
                        orderOilSource.setPaidCreditOil(orderOilSource.getPaidCreditOil() - rechargeOilSource.getNoCreditOil());
                        orderOilSource.setPaidRebateOil(orderOilSource.getPaidRebateOil() - rechargeOilSource.getNoRebateOil());
                        orderOilSourceService.saveOrUpdate(orderOilSource);
                        OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, Long.parseLong(rechargeOilSource.getSourceOrderId()),-1);
                        if (orderLimit == null) {
                            throw new BusinessException("????????????????????????" + rechargeOilSource.getSourceOrderId() + "?????????????????????");
                        }
                        orderLimit.setNoPayOil(orderLimit.getNoPayOil() + oil);
                        orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - oil);
                        orderLimit.setPaidOil(orderLimit.getPaidOil() - oil);
                        orderLimitService.saveOrUpdate(orderLimit);
                    }
                    rechargeOilSource.setNoPayOil(0l);
                    rechargeOilSource.setNoCreditOil(0l);
                    rechargeOilSource.setNoRebateOil(0l);
                    rechargeOilSourceService.saveOrUpdate(rechargeOilSource);
                }
            }
        }
        return null;
    }



    /*??????????????????????????????*/
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo currOper) {
        if(off==null){
            off = new OrderFundFlow();
        }
        Long userId =inParam.getTenantUserId();
        String billId =inParam.getBillId();
        String batchId =inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.getById(userId);
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
