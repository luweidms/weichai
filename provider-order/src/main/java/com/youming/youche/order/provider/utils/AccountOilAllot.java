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
public class AccountOilAllot {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final ReadisUtil readisUtil;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {

        long userId = inParam.getUserId() == null ? 0 : inParam.getUserId();
        long businessId = inParam.getBusinessId() == null ? 0 : inParam.getBusinessId();
        long accountDatailsId = inParam.getAccountDatailsId() == null ? 0 : inParam.getAccountDatailsId();
        long orderId = inParam.getOrderId() == null ? 0 : inParam.getOrderId();
        OrderOilSource ol = inParam.getOilSource();
        if (rels == null) {
            throw new BusinessException("???????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        // ????????????????????????
        for (BusiSubjectsRel rel : rels) {
            // ?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW) {
                if (ol == null) {
                    throw new BusinessException("???????????????????????????!");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// ???????????????????????????
                off.setOrderId(ol.getSourceOrderId());// ??????ID
                off.setBusinessId(rel.getBusinessId());// ????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(off.getBusinessId())).getCodeName());// ????????????
                off.setBookType(Long.parseLong(rel.getBookType()));// ????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE, String.valueOf(off.getBookType())).getCodeName());// ????????????
                off.setSubjectsId(rel.getSubjectsId());// ??????ID
                off.setSubjectsName(rel.getSubjectsName());// ????????????
                off.setBusiTable("ACCOUNT_DETAILS");// ???????????????
                off.setBusiKey(accountDatailsId);// ????????????ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// ????????????:???in???out???io
                off.setTenantId(ol.getSourceTenantId());
                off.setToOrderId(orderId);
                this.createOrderFundFlowNew(inParam, off, user);
                orderFundFlowService.saveOrUpdate(off);
                long noPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                long noRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                long noCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                ol.setNoPayOil(ol.getNoPayOil() - noPayOil);
                ol.setPaidOil(ol.getPaidOil() + noPayOil);
                ol.setNoRebateOil(ol.getNoRebateOil() - noRebateOil);
                ol.setPaidRebateOil(ol.getPaidRebateOil() + noRebateOil);
                ol.setNoCreditOil(ol.getNoCreditOil() - noCreditOil);
                ol.setPaidCreditOil(ol.getPaidCreditOil() + noCreditOil);
                orderOilSourceService.saveOrUpdate(ol);
                //?????????????????????????????????(??????????????????????????????)
                OrderLimit limit = orderLimitService.getOrderLimitByUserIdAndOrderId(ol.getUserId(), ol.getOrderId(), ol.getUserType());
                if (limit != null) {
                    limit.setNoPayOil(limit.getNoPayOil() - ol.getMatchAmount());
                    limit.setPaidOil(limit.getPaidOil() + ol.getMatchAmount());
                    if (String.valueOf(ol.getOrderId()).equals(String.valueOf(ol.getSourceOrderId()))) {
                        limit.setNoWithdrawOil(limit.getNoWithdrawOil() + ol.getMatchAmount());
                    } else {
                        limit.setWithdrawOil(limit.getWithdrawOil() + ol.getMatchAmount());
                    }
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
        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        UserDataInfo userDataInfo = null;
        userDataInfo = userDataInfoService.getById(userId);
        Long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
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
