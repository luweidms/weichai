package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.SysTenantDefDto;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
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
public class GrantSalary {
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    private final ReadisUtil readisUtil;

    private final IOrderFundFlowService orderFundFlowService;

    private final IOrderLimitService orderLimitService;


    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user) {

        Long userId =inParam.getUserId();
        Long tenantId =inParam.getTenantId();
        Long businessId =inParam.getBusinessId();
        Long accountDatailsId =inParam.getAccountDatailsId();
        String vehicleAffiliation =inParam.getVehicleAffiliation();
        Long salaryId =inParam.getSalaryId();
        OrderLimit ol =inParam.getOrderLimitBase();
        if (rels == null) {
            throw new BusinessException("??????????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //????????????????????????
        for (BusiSubjectsRel rel : rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_AWARD16) {
                if (ol == null) {
                    throw new BusinessException("???????????????????????????");
                }
                //??????????????????
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//???????????????????????????
                off.setOrderId(ol.getOrderId());//??????ID
                off.setBusinessId(businessId);//????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//????????????
                off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
                off.setSubjectsId(rel.getSubjectsId());//??????ID
                off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                off.setBusiKey(accountDatailsId);//????????????ID
                off.setFaceUserName(rel.getOtherName());//???????????????
                off.setVehicleAffiliation(ol.getVehicleAffiliation());
                off.setInoutSts("out");//????????????:???in???out???io
                off.setTenantId(ol.getTenantId());
                this.createOrderFundFlowNew(inParam,off,user);
                orderFundFlowService.saveOrUpdate(off);
                ol.setNoPayDebt((ol.getNoPayDebt() == null ? 0L : ol.getNoPayDebt()) - off.getAmount());
                ol.setPaidDebt((ol.getPaidDebt() == null ? 0L : ol.getPaidDebt()) + off.getAmount());
                orderLimitService.saveOrUpdate(ol);
            }
            //?????????????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION) {
                if (ol == null) {
                    throw new BusinessException("???????????????????????????");
                }
                //??????????????????
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//???????????????????????????
                off.setOrderId(ol.getOrderId());//??????ID
                off.setBusinessId(businessId);//????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//????????????
                off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
                off.setSubjectsId(rel.getSubjectsId());//??????ID
                off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                off.setBusiKey(accountDatailsId);//????????????ID
                off.setVehicleAffiliation(ol.getVehicleAffiliation());
                off.setFaceUserName(rel.getOtherName());//???????????????
                off.setInoutSts("out");//????????????:???in???out???io
                off.setTenantId(ol.getTenantId());
                this.createOrderFundFlowNew(inParam,off,user);
                orderFundFlowService.saveOrUpdate(off);
                ol.setNoVerificationLoan((ol.getNoVerificationLoan() == null ? 0L : ol.getNoVerificationLoan()) - off.getAmount());
                ol.setVerificationLoan((ol.getVerificationLoan() == null ? 0L : ol.getVerificationLoan()) + off.getAmount());
                orderLimitService.saveOrUpdate(ol);
            }
            //????????????
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_RECEIVABLE || rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_HANDLE) {
                //??????????????????
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//???????????????????????????
                off.setBusinessId(businessId);//????????????
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//????????????
                off.setBookType(Long.parseLong(rel.getBookType()));//????????????
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//????????????
                off.setSubjectsId(rel.getSubjectsId());//??????ID
                off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("payout_intf");//???????????????
                off.setBusiKey(accountDatailsId);//????????????ID
                off.setVehicleAffiliation(vehicleAffiliation);
                off.setFaceUserName(rel.getOtherName());//???????????????
                off.setTenantId(tenantId);
                off.setToOrderId(salaryId);
                this.createOrderFundFlowNew(inParam,off,user);
                if(rel.getSubjectsId()== EnumConsts.SubjectIds.SALARY_RECEIVABLE){
                    off.setInoutSts("in");//????????????:???in???out???io
                }else if(rel.getSubjectsId()==EnumConsts.SubjectIds.SALARY_HANDLE){
                    SysTenantDefDto sysTenantDef =inParam.getSysTenantDef();
                    off.setUserId(sysTenantDef.getAdminUser());//??????ID
                    off.setBillId(sysTenantDef.getLinkPhone());//??????
                    off.setUserName(sysTenantDef.getName());//?????????
                    off.setInoutSts("out");//????????????:???in???out???io
                }
                orderFundFlowService.saveOrUpdate(off);
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
