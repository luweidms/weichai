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
public class PledgeReleaseOilcard {

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    private  final IOrderLimitService orderLimitService;

    private final ReadisUtil readisUtil;

    private final IOrderFundFlowService orderFundFlowService;

    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user)  {

        long orderId =inParam.getOrderId();
        long businessId =inParam.getBusinessId();
        long accountDatailsId =inParam.getAccountDatailsId() == null ? -1:inParam.getAccountDatailsId();//
        long userId =inParam.getUserId();
        long tenantId = inParam.getTenantId();
        if (orderId <= 0) {
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
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        for (BusiSubjectsRel rel : rels) {
            if (rel.getAmountFee() == 0L) {
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
            this.createOrderFundFlowNew(inParam,off,user);
            //油卡抵押
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_PAYABLE_IN) {
                off.setInoutSts("out");
                ol.setPledgeOilcardFee(ol.getPledgeOilcardFee() + off.getCost());
                ol.setNoPayDebt(ol.getNoPayDebt() + off.getCost());
                ol.setDebtMoney(ol.getDebtMoney() +  off.getCost());
            }
            //油卡押金调整--油卡抵押(未到期)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_MARGIN) {
                off.setInoutSts("out");
                ol.setPledgeOilcardFee(ol.getPledgeOilcardFee() + off.getCost());
                ol.setNoPayFinal(ol.getNoPayFinal() - off.getCost());
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_PLEDGE_RECEIVABLE_IN || rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_IN) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName =inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            //油卡释放
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_PAYABLE_OUT) {
                off.setInoutSts("in");
                ol.setPledgeOilcardFee(ol.getPledgeOilcardFee() - off.getCost());
                ol.setNoPayDebt(ol.getNoPayDebt() - off.getCost());
                ol.setDebtMoney(ol.getDebtMoney() -  off.getCost());
            }
            //油卡押金调整--油卡抵押(未到期)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_MARGIN) {
                off.setInoutSts("in");
                ol.setPledgeOilcardFee(ol.getPledgeOilcardFee() - off.getCost());
                ol.setNoPayFinal(ol.getNoPayFinal() + off.getCost());
                ol.setReleaseOilcardFee((ol.getReleaseOilcardFee() == null ? 0l : ol.getReleaseOilcardFee()) + off.getCost());
            }
            //油卡押金调整--应收逾期(油卡释放)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_IN) {
                off.setInoutSts("in");
                ol.setPledgeOilcardFee(ol.getPledgeOilcardFee() - off.getCost());
                ol.setOrderFinal(ol.getOrderFinal() - off.getCost());
                ol.setOrderCash(ol.getOrderCash() + off.getCost());
                ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
                ol.setReleaseOilcardFee((ol.getReleaseOilcardFee() == null ? 0l : ol.getReleaseOilcardFee()) + off.getCost());
                ol.setMarginTurn((ol.getMarginTurn() == null ? 0L : ol.getMarginTurn()) + off.getCost());
            }
            //票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1697) {
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
                off.setInoutSts("in");
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_CARD_RELEASE_RECEIVABLE_OUT ) {
                long tenantUserId =inParam.getTenantUserId();
                String tenantBillId =inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("out");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
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
