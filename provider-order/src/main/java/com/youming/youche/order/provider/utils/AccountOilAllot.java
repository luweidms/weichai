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
            throw new BusinessException("油账户明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT || rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_OIL_ALLOT_UPDATE_ORDER_LOW) {
                if (ol == null) {
                    throw new BusinessException("订单油来源不能为空!");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());// 交易金额（单位分）
                off.setOrderId(ol.getSourceOrderId());// 订单ID
                off.setBusinessId(rel.getBusinessId());// 业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE, String.valueOf(off.getBookType())).getCodeName());// 账户类型
                off.setSubjectsId(rel.getSubjectsId());// 科目ID
                off.setSubjectsName(rel.getSubjectsName());// 科目名称
                off.setBusiTable("ACCOUNT_DETAILS");// 业务对象表
                off.setBusiKey(accountDatailsId);// 业务流水ID
                off.setIncome(rel.getIncome());
                off.setInoutSts("out");// 收支状态:收in支out转io
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
                //订单限制表未付油置已付(订单号不等于来源单号)
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
                    throw new BusinessException("根据用户id：" + ol.getUserId() + "订单号：" + ol.getOrderId() + "未找到订单限制记录");
                }
            }
        }
        return null;
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
        Long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
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
