package com.youming.youche.order.provider.utils;

import com.beust.jcommander.internal.Lists;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.finance.constant.OaLoanData;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OaLoanMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.vo.OperDataParam;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/14 10:48
 */
@Component
public class OaLoanVerification {

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    OaLoanMapper oaLoanMapper;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Integer loanSubjects = inParam.getLoanSubjects();
        Integer loanTransReason = inParam.getLoanTransReason();
        Long userId = inParam.getUserId();
        Long oaLoanId = inParam.getOaLoanId();
        Long tenantId = inParam.getTenantId();

        //?????????????????????????????????????????????????????????
        if (loanSubjects == OaLoanData.LOANSUBJECT5 && loanTransReason == 2) {
            return null;
        }
        //????????????????????????
        OrderLimit ol =  new OrderLimit();
        OaLoan oaLoan = oaLoanMapper.queryOaLoanById(oaLoanId, Lists.newArrayList(), null);
        if(oaLoan!=null&&oaLoan.getLoanSubject()!=5&&orderId>0){
            ol = orderLimitMapper.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
            if (ol == null) {
                throw new BusinessException("?????????????????????!");
            }
        }

        if (rels == null) {
            throw new BusinessException("????????????????????????!");
        }
        //??????????????????
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //????????????????????????
        for (BusiSubjectsRel rel:rels) {
            //?????????0???????????????
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//???????????????????????????
            off.setOrderId(orderId);//??????ID
            off.setBusinessId(businessId);//????????????
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//????????????
            off.setBookType(Long.parseLong(rel.getBookType()));//????????????
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//????????????
            off.setSubjectsId(rel.getSubjectsId());//??????ID
            off.setSubjectsName(rel.getSubjectsName());//????????????
            off.setBusiTable("oa_loan");//???????????????
            off.setBusiKey(oaLoanId);//????????????ID
            off.setTenantId(tenantId);
            inParam.setBillId(oaLoan.getBorrowPhone());
            this.createOrderFundFlow(inParam,off, user);
            List<OperDataParam> odps = new ArrayList();
            //????????????????????????
            if (loanSubjects != OaLoanData.LOANSUBJECT5&&rel.getSubjectsId() == EnumConsts.SubjectIds.OA_LOAN_VERIFICATION) {
                off.setInoutSts("out");//????????????:???in???out???io
                ol.setVerificationLoan(ol.getVerificationLoan() + off.getCost());
                ol.setNoVerificationLoan(ol.getNoVerificationLoan() - off.getCost());
            }
            orderFundFlowService.saveOrUpdate(off);
            if(loanSubjects != OaLoanData.LOANSUBJECT5){
                orderLimitService.saveOrUpdate(ol);
            }
        }
        return null;
    }

    /*??????????????????????????????*/
    public OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
        if(off==null){
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.getUserDataInfo(userId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//??????ID
        off.setBillId(billId);//??????
        off.setUserName(userDataInfo.getLinkman());//?????????
        off.setBatchId(batchId);//??????
        off.setBatchAmount(amount);//???????????????
        if(user != null){
            off.setOpId(user.getId());//?????????ID
            off.setOpName(user.getName());//?????????
            off.setUpdateOpId(user.getId());//?????????
        }
        off.setOpDate(LocalDateTime.now());//????????????
        off.setVehicleAffiliation(vehicleAffiliation);//????????????
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//?????????????????????????????????
        return off;
    }
}
