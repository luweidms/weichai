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

        //违章罚款因私的会在发工资的时候抵扣工资
        if (loanSubjects == OaLoanData.LOANSUBJECT5 && loanTransReason == 2) {
            return null;
        }
        //查询订单限制数据
        OrderLimit ol =  new OrderLimit();
        OaLoan oaLoan = oaLoanMapper.queryOaLoanById(oaLoanId, Lists.newArrayList(), null);
        if(oaLoan!=null&&oaLoan.getLoanSubject()!=5&&orderId>0){
            ol = orderLimitMapper.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
            if (ol == null) {
                throw new BusinessException("订单信息不存在!");
            }
        }

        if (rels == null) {
            throw new BusinessException("预付明细不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel:rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("oa_loan");//业务对象表
            off.setBusiKey(oaLoanId);//业务流水ID
            off.setTenantId(tenantId);
            inParam.setBillId(oaLoan.getBorrowPhone());
            this.createOrderFundFlow(inParam,off, user);
            List<OperDataParam> odps = new ArrayList();
            //司机借支金额核销
            if (loanSubjects != OaLoanData.LOANSUBJECT5&&rel.getSubjectsId() == EnumConsts.SubjectIds.OA_LOAN_VERIFICATION) {
                off.setInoutSts("out");//收支状态:收in支out转io
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

    /*产生订单资金流水数据*/
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
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if(user != null){
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
            off.setUpdateOpId(user.getId());//修改人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }
}
