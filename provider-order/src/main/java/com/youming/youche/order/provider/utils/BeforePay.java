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
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/13 11:02
 */
@Component
public class BeforePay {

    @Resource
    IOrderLimitService orderLimitService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        Long orderId = inParam.getOrderId();
        Long businessId = inParam.getBusinessId();
        Long accountDatailsId = inParam.getAccountDatailsId();
        String totalFee = inParam.getTotalFee();
        Long userId = inParam.getUserId();
        if (orderId == 0L) {
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
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel:rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L && businessId != EnumConsts.PayInter.BEFORE_PAY_CODE || rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_BEFORE_Entity_SUB || rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_BEFORE_ENTIY_OIL_FEE) {
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
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(accountDatailsId);//业务流水ID
            off.setTenantId(ol.getTenantId());
            this.createOrderFundFlow(inParam, off, user);
            //预付现金
    		/*if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_PAY_SUB) {
    	    	off.setInoutSts("in");//收支状态:收in支out转io
    	    	ol.setOrderCash(ol.getOrderCash() + off.getCost());
    	    	ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
    	    	ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
    	    	ol.setTotalFee(ol.getTotalFee() + totalFee);
    		}*/
            //应收逾期
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_RECEIVABLE_OVERDUE_SUB) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderCash(ol.getOrderCash() + off.getCost());
                ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
                ol.setTotalFee(ol.getTotalFee() + Long.valueOf(totalFee));
            }
            //票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1688) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
            }
            //应付逾期、自有车平台总运费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_PAYABLE_OVERDUE_SUB || rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_HA_TOTAL_FEE) {
                    off.setInoutSts("out");
                } else {
                    off.setInoutSts("in");
                }
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            //混合油卡实体卡
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_Entity_SUB) {
                //实体卡
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderOil(ol.getOrderOil() + off.getCost());
                ol.setOrderEntityOil(ol.getOrderEntityOil() + off.getCost());
                ol.setCostEntityOil((ol.getCostEntityOil() == null ? 0L : ol.getCostEntityOil()) + off.getCost());
            }
            //混合油卡虚拟卡
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_Virtual_SUB) {
                //虚拟卡
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderOil(ol.getOrderOil() + off.getCost());
                ol.setNoPayOil(ol.getNoPayOil() + off.getCost());
            }
            //预付ETC
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_ETC_SUB) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderEtc(ol.getOrderEtc() + off.getCost());
                ol.setNoPayEtc(ol.getNoPayEtc() + off.getCost());
            }

            //自有车科目
            //路桥费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_BRIDGE) {
                off.setInoutSts("in");//收支状态:收in支out转io
            }
            //预付补贴
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_MASTER_SUBSIDY || rel.getSubjectsId() == EnumConsts.SubjectIds.BEFOREPAY_SLAVE_SUBSIDY) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderCash(ol.getOrderCash() + off.getCost());
                ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
                ol.setTotalFee(ol.getTotalFee() + Long.valueOf(totalFee));
            }
            //实体卡（自有车）
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_ENTIY_OIL_FEE) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderOil(ol.getOrderOil() + off.getCost());
                ol.setOrderEntityOil(ol.getOrderEntityOil() + off.getCost());
                ol.setCostEntityOil((ol.getCostEntityOil() == null ? 0L : ol.getCostEntityOil()) + off.getCost());
            }
            //虚拟卡（自有车）
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BEFORE_FICTITIOUS_OIL_FEE) {
                off.setInoutSts("in");//收支状态:收in支out转io
                ol.setOrderOil(ol.getOrderOil() + off.getCost());
                ol.setNoPayOil(ol.getNoPayOil() + off.getCost());
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(ol);
        }

        return null;
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlow(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user){
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
