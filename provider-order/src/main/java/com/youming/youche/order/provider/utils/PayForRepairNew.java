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
            throw new BusinessException("维修费用明细不能为空!");
        }
        //资金流向批次
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
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //可用支付维修费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.BALANCE_PAY_REPAIR) {
                //查询司机对应资金渠道订单
                orderLimitService.matchAmountToOrderLimit(rel.getAmountFee(), 0L, 0L, userId, vehicleAffiliation, tenantId, isNeedBill,  orderLimits);
                //将油费金额分摊给订单
                for (OrderLimit olTemp : orderLimits) {
                    if (olTemp.getMatchAmount() != null && olTemp.getMatchAmount() > 0L) {
                        //资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());//订单ID
                        off.setBusinessId(businessId);//业务类型
                        off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                        off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());//账户类型
                        off.setSubjectsId(rel.getSubjectsId());//科目ID
                        off.setSubjectsName(rel.getSubjectsName());//科目名称
                        off.setBusiTable("USER_REPAIR_MARGIN");//业务对象表
                        off.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                        off.setBusiKey(repairFlowId);//业务流水ID
                        off.setTenantId(tenantId);
                        this.createOrderFundFlowNew(inParam,off,user);
                        off.setInoutSts("out");//收支状态:收in支out转io
                        orderFundFlowService.saveOrUpdate(off);
                        //订单限制表操作
                        olTemp.setNoPayCash(olTemp.getNoPayCash() - off.getCost());
                        olTemp.setPaidCash(olTemp.getPaidCash() + off.getCost());
                        olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() - off.getCost());
                        olTemp.setNoWithdrawRepair((olTemp.getNoWithdrawRepair() == null ? 0L : olTemp.getNoWithdrawRepair()) + off.getCost());
                        orderLimitService.saveOrUpdate(olTemp);
                        //司机维修订单记录表
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
                        orderOil.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//未提现
                        orderOil.setVehicleAffiliation(olTemp.getVehicleAffiliation());
                        orderOil.setOilAffiliation(olTemp.getOilAffiliation());
                        orderOil.setIsNeedBill(isNeedBill);
                        orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_CASH);//来源订单现金
                        orderOil.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR);//油老板
                        orderOil.setOtherFlowId(cashFlowId);
                        serviceMatchOrderService.save(orderOil);
                        //清空匹配金额
                        olTemp.setMatchAmount(0L);
                        olTemp.setMatchBackIncome(0L);
                        olTemp.setMatchIncome(0L);
                    }
                }
            }
            //维修基金(不操作订单，只生成资金流向记录)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.REPAIR_FUND_PAY_REPAIR || rel.getSubjectsId() == EnumConsts.SubjectIds.COMPANY_PAY_REPAIR) {
                //资金流水操作
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(rel.getAmountFee());//交易金额（单位分）
                off.setOrderId(repairId);//订单ID
                off.setBusinessId(rel.getBusinessId());//业务类型
                off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());//业务名称
                off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                off.setBookTypeName(readisUtil.getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());//账户类型
                off.setSubjectsId(rel.getSubjectsId());//科目ID
                off.setSubjectsName(rel.getSubjectsName());//科目名称
                off.setBusiTable("USER_REPAIR_MARGIN");//业务对象表
                off.setBusiKey(cashFlowId);//业务流水ID
                off.setFaceType(5);//对方类型:1油老板2车老板3经纪人4司机5维修商
                off.setFaceUserId(rel.getOtherId());//对方用户ID
                off.setFaceUserName(rel.getOtherName());//对方用户名
                off.setIncome(rel.getIncome());
                off.setBackIncome(rel.getBackIncome());
                this.createOrderFundFlowNew(inParam,off,user);
                off.setInoutSts("out");//收支状态:收in支out转io
                orderFundFlowService.saveOrUpdate(off);
                //司机维修订单记录表
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
                orderOil.setState(OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS0);//未提现
                orderOil.setVehicleAffiliation(vehicleAffiliation);
                orderOil.setOilAffiliation(oilAffiliation);
                orderOil.setIsNeedBill(isNeedBill);
                orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_CASH);//来源订单现金
                orderOil.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR);//维修商老板
                orderOil.setOtherFlowId(repairFlowId);
                serviceMatchOrderService.save(orderOil);
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
