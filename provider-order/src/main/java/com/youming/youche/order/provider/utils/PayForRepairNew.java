package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.domain.order.UserRepairMargin;
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
public class PayForRepairNew {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    private final  ReadisUtil readisUtil;
    private final IOrderLimitService orderLimitService;
    private final IOrderFundFlowService orderFundFlowService;
    private final IServiceMatchOrderService serviceMatchOrderService;


    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user) {
        Long userId =inParam.getUserId();
        Long businessId =inParam.getBusinessId();
        Long accountDatailsId =inParam.getAccountDatailsId();
        String vehicleAffiliation =inParam.getVehicleAffiliation();
        String oilAffiliation =inParam.getOilAffiliation();
        Long tenantId =inParam.getTenantId();
        Long serviceUserId = inParam.getServiceUserId();
        Long productId = inParam.getProductId();
        Integer isNeedBill =inParam.getIsNeedBill();
        List<OrderLimit> orderLimits =inParam.getOrderLimit();
        List<UserRepairMargin> tempFlow = inParam.getUserRepairMarginlist();
        Long repairId =inParam.getRepairId();

        if (rels == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr()+"");
        Long repairFlowId = 0L;
        Long cashFlowId = 0L;
        for (UserRepairMargin flow : tempFlow) {
            if (flow.getSubjectsId() != null && (flow.getSubjectsId() == EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR || flow.getSubjectsId() == EnumConsts.SubjectIds.COMPANY_PAY_REPAIR)) {
                repairFlowId = flow.getId();
            } else if (flow.getSubjectsId() != null && flow.getSubjectsId() == EnumConsts.SubjectIds.BALANCE_PAY_REPAIR) {
                cashFlowId = flow.getId();
            }
        }
        for (BusiSubjectsRel rel : rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //?????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BALANCE_PAY_REPAIR) {
                //????????????????????????????????????
                orderLimitService.matchAmountToOrderLimit(rel.getAmountFee(), 0L, 0L, userId, vehicleAffiliation, tenantId, isNeedBill,  orderLimits);
                //??????????????????????????????
                for (OrderLimit olTemp : orderLimits) {
                    if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                        //??????????????????
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(olTemp.getMatchAmount());//???????????????????????????
                        off.setOrderId(olTemp.getOrderId());//??????ID
                        off.setBusinessId(businessId);//????????????
                        off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());//????????????
                        off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                        off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());//????????????
                        off.setSubjectsId(rel.getSubjectsId());//??????ID
                        off.setSubjectsName(rel.getSubjectsName());//????????????
                        off.setBusiTable("USER_REPAIR_MARGIN");//???????????????
                        off.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                        off.setBusiKey(repairFlowId);//????????????ID
                        off.setTenantId(tenantId);
                        this.createOrderFundFlowNew(inParam,off,user);
                        off.setInoutSts("out");//????????????:???in???out???io
                        orderFundFlowService.saveOrUpdate(off);
                        //?????????????????????
                        olTemp.setNoPayCash(olTemp.getNoPayCash() - off.getCost());
                        olTemp.setPaidCash(olTemp.getPaidCash() + off.getCost());
                        olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() - off.getCost());
                        olTemp.setNoWithdrawRepair((olTemp.getNoWithdrawRepair() == null ? 0L : olTemp.getNoWithdrawRepair()) + off.getCost());
                        orderLimitService.saveOrUpdate(olTemp);
                        //???????????????????????????
                        ServiceMatchOrder orderOil = new ServiceMatchOrder();
                        orderOil.setUserId(userId);
                        orderOil.setAmount(off.getCost());
                        orderOil.setOrderId(olTemp.getOrderId());
                        orderOil.setProductId(productId);
                        orderOil.setServiceUserId(serviceUserId);
                        orderOil.setAccountBalance(0L);
                        orderOil.setNoWithdrawAmount(off.getCost());
                        orderOil.setWithdrawAmount(0L);
                        orderOil.setAdvanceFee(0L);
                        orderOil.setTenantId(olTemp.getTenantId());
                        orderOil.setCreateDate(LocalDateTime.now());
                        orderOil.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//?????????
                        orderOil.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                        orderOil.setOilAffiliation(olTemp.getOilAffiliation());
                        orderOil.setIsNeedBill(isNeedBill);
                        orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_CASH);//??????????????????
                        orderOil.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR);//?????????
                        orderOil.setOtherFlowId(cashFlowId);
                        serviceMatchOrderService.save(orderOil);
                        //??????????????????
                        olTemp.setMatchAmount(0L);
                        olTemp.setMatchBackIncome(0L);
                        olTemp.setMatchIncome(0L);
                    }
                }
            }
            //????????????(?????????????????????????????????????????????)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR || rel.getSubjectsId() == EnumConsts.SubjectIds.COMPANY_PAY_REPAIR) {
                //??????????????????
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//???????????????????????????
                off.setOrderId(repairId);//??????ID
                off.setBusinessId(rel.getBusinessId());//????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());//????????????
                off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());//????????????
                off.setSubjectsId(rel.getSubjectsId());//??????ID
                off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("USER_REPAIR_MARGIN");//???????????????
                off.setBusiKey(cashFlowId);//????????????ID
                off.setFaceType(5);//????????????:1?????????2?????????3?????????4??????5?????????
                off.setFaceUserId(rel.getOtherId());//????????????ID
                off.setFaceUserName(rel.getOtherName());//???????????????
                off.setIncome(rel.getIncome());
                off.setBackIncome(rel.getBackIncome());
                this.createOrderFundFlowNew(inParam,off,user);
                off.setInoutSts("out");//????????????:???in???out???io
                orderFundFlowService.saveOrUpdate(off);
                //???????????????????????????
                ServiceMatchOrder orderOil = new ServiceMatchOrder();
                orderOil.setUserId(userId);
                orderOil.setAmount(off.getCost());
                orderOil.setOrderId(0L);
                orderOil.setProductId(productId);
                orderOil.setServiceUserId(serviceUserId);
                orderOil.setAccountBalance(0L);
                orderOil.setNoWithdrawAmount(off.getCost());
                orderOil.setWithdrawAmount(0L);
                orderOil.setAdvanceFee(0L);
                orderOil.setTenantId(tenantId);
                orderOil.setCreateDate(LocalDateTime.now());
                orderOil.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//?????????
                orderOil.setVehicleAffiliation(vehicleAffiliation);
                orderOil.setOilAffiliation(oilAffiliation);
                orderOil.setIsNeedBill(isNeedBill);
                orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_CASH);//??????????????????
                orderOil.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR);//???????????????
                orderOil.setOtherFlowId(repairFlowId);
                serviceMatchOrderService.save(orderOil);
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
