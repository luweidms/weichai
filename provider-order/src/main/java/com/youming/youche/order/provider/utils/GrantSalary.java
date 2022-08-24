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
            throw new BusinessException("购油费用明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //订单欠款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_AWARD16) {
                if (ol == null) {
                    throw new BusinessException("未找到订单限制记录");
                }
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(ol.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setFaceUserName(rel.getOtherName());//对方用户名
                off.setVehicleAffiliation(ol.getVehicleAffiliation());
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setTenantId(ol.getTenantId());
                this.createOrderFundFlowNew(inParam,off,user);
                orderFundFlowService.saveOrUpdate(off);
                ol.setNoPayDebt((ol.getNoPayDebt() == null ? 0L : ol.getNoPayDebt()) - off.getAmount());
                ol.setPaidDebt((ol.getPaidDebt() == null ? 0L : ol.getPaidDebt()) + off.getAmount());
                orderLimitService.saveOrUpdate(ol);
            }
            //借支未核销记录
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_BORROW_DEDUCTION) {
                if (ol == null) {
                    throw new BusinessException("未找到订单限制记录");
                }
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(ol.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setVehicleAffiliation(ol.getVehicleAffiliation());
                off.setFaceUserName(rel.getOtherName());//对方用户名
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setTenantId(ol.getTenantId());
                this.createOrderFundFlowNew(inParam,off,user);
                orderFundFlowService.saveOrUpdate(off);
                ol.setNoVerificationLoan((ol.getNoVerificationLoan() == null ? 0L : ol.getNoVerificationLoan()) - off.getAmount());
                ol.setVerificationLoan((ol.getVerificationLoan() == null ? 0L : ol.getVerificationLoan()) + off.getAmount());
                orderLimitService.saveOrUpdate(ol);
            }
            //核销工资
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_RECEIVABLE || rel.getSubjectsId() == EnumConsts.SubjectIds.SALARY_HANDLE) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER, String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,rel.getBookType()).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(accountDatailsId);//业务流水ID
                off.setVehicleAffiliation(vehicleAffiliation);
                off.setFaceUserName(rel.getOtherName());//对方用户名
                off.setTenantId(tenantId);
                off.setToOrderId(salaryId);
                this.createOrderFundFlowNew(inParam,off,user);
                if(rel.getSubjectsId()== EnumConsts.SubjectIds.SALARY_RECEIVABLE){
                    off.setInoutSts("in");//收支状态:收in支out转io
                }else if(rel.getSubjectsId()==EnumConsts.SubjectIds.SALARY_HANDLE){
                    SysTenantDefDto sysTenantDef =inParam.getSysTenantDef();
                    off.setUserId(sysTenantDef.getAdminUser());//用户ID
                    off.setBillId(sysTenantDef.getLinkPhone());//手机
                    off.setUserName(sysTenantDef.getName());//用户名
                    off.setInoutSts("out");//收支状态:收in支out转io
                }
                orderFundFlowService.saveOrUpdate(off);
            }
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
