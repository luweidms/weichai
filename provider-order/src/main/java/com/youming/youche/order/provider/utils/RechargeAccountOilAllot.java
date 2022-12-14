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
public class RechargeAccountOilAllot {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final ReadisUtil readisUtil;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user)  {
        Long userId = inParam.getUserId();
        Long businessId =inParam.getBusinessId();
        Long tenantId =inParam.getTenantId();
        Long accountDatailsId =inParam.getAccountDatailsId();
        Long orderId = inParam.getOrderId();
        OrderOilSource ol = inParam.getOilSource();
        if (rels == null) {
            throw new BusinessException("???????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr()+"");

        // ????????????????????????
        for (BusiSubjectsRel rel : rels) {
            // ?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //?????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_FEE) {
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// ???????????????????????????
                off.setOrderId(0l);// ??????ID
                off.setBusinessId(rel.getBusinessId());// ????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());// ????????????
                off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());// ????????????
                off.setSubjectsId(rel.getSubjectsId());// ??????ID
                off.setSubjectsName(rel.getSubjectsName());// ????????????
                off.setBusiTable("ACCOUNT_DETAILS");// ???????????????
                off.setBusiKey(accountDatailsId);// ????????????ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("in");// ????????????:???in???out???io
                off.setTenantId(tenantId);
                off.setToOrderId(orderId);
                this.createOrderFundFlowNew(inParam, off,user);
                orderFundFlowService.saveOrUpdate(off);
            }
            //?????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECHARGE_ACCOUNT_OIL_ALLOT ) {
                if (ol == null) {
                    throw new BusinessException("???????????????????????????!");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// ???????????????????????????
                off.setOrderId(ol.getSourceOrderId());// ??????ID
                off.setBusinessId(rel.getBusinessId());// ????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());// ????????????
                off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());// ????????????
                off.setSubjectsId(rel.getSubjectsId());// ??????ID
                off.setSubjectsName(rel.getSubjectsName());// ????????????
                off.setBusiTable("ACCOUNT_DETAILS");// ???????????????
                off.setBusiKey(accountDatailsId);// ????????????ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// ????????????:???in???out???io
                off.setTenantId(ol.getSourceTenantId());
                off.setToOrderId(orderId);
                this.createOrderFundFlowNew(inParam, off,user);
                orderFundFlowService.saveOrUpdate(off);

                long matchAmount = ol.getMatchAmount();
                long matchNoPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                long matchNoRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                long matchNoCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                ol.setNoPayOil(ol.getNoPayOil() - matchNoPayOil);
                ol.setNoCreditOil(ol.getNoCreditOil() - matchNoCreditOil);
                ol.setNoRebateOil(ol.getNoRebateOil() - matchNoRebateOil);
                ol.setPaidOil(ol.getPaidOil() + matchNoPayOil);
                ol.setPaidCreditOil(ol.getPaidCreditOil() + matchNoCreditOil);
                ol.setPaidRebateOil(ol.getPaidRebateOil() + matchNoRebateOil);

                orderOilSourceService.saveOrUpdate(ol);
                //?????????????????????????????????(??????????????????????????????)
                OrderLimit limit = orderLimitService.getOrderLimitByUserIdAndOrderId(ol.getUserId(),ol.getOrderId(),-1);
                if (limit != null) {
                    limit.setNoPayOil(limit.getNoPayOil() - ol.getMatchAmount());
                    limit.setPaidOil(limit.getPaidOil() + ol.getMatchAmount());
                    limit.setNoWithdrawOil(limit.getNoWithdrawOil() + ol.getMatchAmount());
                    orderLimitService.saveOrUpdate(limit);
                } else {
                    throw new BusinessException("????????????id???" + ol.getUserId() + "????????????" + ol.getOrderId() + "???????????????????????????");
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
        long userId =inParam.getUserId();
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
