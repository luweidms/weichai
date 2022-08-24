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
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.SysStaticDataEnum.SysStaticData.ACCOUNT_DETAILS_BUSINESS_NUMBER;
import static com.youming.youche.order.constant.BaseConstant.ACCOUNT_BOOK_TYPE;

@Component
@RequiredArgsConstructor
public class OilAndEtcTurnCashNew {

    private final ReadisUtil readisUtil;
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderFundFlowService orderFundFlowService;
    private final IOrderLimitService orderLimitService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;


    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels,LoginInfo user) {

        Long userId = inParam.getUserId();
        Long orderId =inParam.getOrderId();
        Long flowId =inParam.getFlowId();
        Long tenantId = inParam.getTenantId();
        Long tenantUserId = inParam.getTenantUserId();
        String trunType = inParam.getTurnType();
        if (StringUtils.isEmpty(trunType)) {
            throw new BusinessException("没有找到转现类型");
        }
        OrderOilSource source = null;
        OrderLimit limit = null;
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(trunType)) {
            source = inParam.getOilSource();
            if (source == null) {
                throw new BusinessException("未找到订单号为：" + orderId + "的订单来源记录");
            }
            limit =  orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId,-1);
        } else {
            limit = inParam.getOrderLimitBase();
        }
        if (rels == null) {
            throw new BusinessException("结余明细不能为空!");
        }
        // 处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            // 金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            // 资金流水操作
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());// 交易金额（单位分）
            off.setOrderId(orderId);// 订单ID
            if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(trunType)) {
                off.setToOrderId(source.getSourceOrderId());
            } {
                off.setToOrderId(limit.getOrderId());
            }
            off.setBusinessId(rel.getBusinessId());// 业务类型
            off.setBusinessName(readisUtil.getSysStaticData(ACCOUNT_DETAILS_BUSINESS_NUMBER,String.valueOf(off.getBusinessId())).getCodeName());// 业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));// 账户类型
            off.setBookTypeName(readisUtil .getSysStaticData(ACCOUNT_BOOK_TYPE,String.valueOf(off.getBookType())).getCodeName());// 账户类型
            off.setSubjectsId(rel.getSubjectsId());// 科目ID
            off.setSubjectsName(rel.getSubjectsName());// 科目名称
            off.setBusiTable("turn_cash_log");// 业务对象表
            off.setBusiKey(flowId);// 业务流水ID
            off.setIncome(rel.getIncome());
            off.setInoutSts("in");// 收支状态:收in支out转io
            off.setTenantId(tenantId);
            this.createOrderFundFlowNew(inParam, off,user);
            //油转现(油支出) 、//油转实体油卡(油支出)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_OIL || rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL) {
                // 订单油来源和订单限制表操作
                long matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
                long matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
                long matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
                source.setNoPayOil(source.getNoPayOil() - matchNoPayOil);
                source.setNoCreditOil(source.getNoCreditOil() - matchNoCreditOil);
                source.setNoRebateOil(source.getNoRebateOil() - matchNoRebateOil);
                source.setPaidOil(source.getPaidOil() + matchNoPayOil);
                source.setPaidCreditOil(source.getPaidCreditOil() + matchNoCreditOil);
                source.setPaidRebateOil(source.getPaidRebateOil() + matchNoRebateOil);
                orderOilSourceService.saveOrUpdate(source);
                limit.setOrderOil(limit.getOrderOil() - off.getAmount());
                limit.setOilIncome(limit.getOilIncome() + off.getIncome());
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_ENTITY_OIL) {
                    off.setInoutSts("out");// 收支状态:收in支out转io
                    orderFundFlowService.saveOrUpdate(off);
                    limit.setOrderEntityOil(limit.getOrderEntityOil() + off.getCost());
                }
                if (source.getOrderId().longValue() != source.getSourceOrderId().longValue()) {
                    OrderOilSource oos = orderOilSourceService.getOrderOilSource(tenantUserId, source.getSourceOrderId(), source.getSourceOrderId(), source.getVehicleAffiliation(), source.getSourceTenantId(),-1,source.getOilAccountType(),source.getOilConsumer(),source.getOilBillType(),source.getOilAffiliation());
                    if (oos == null) {
                        throw new BusinessException("根据用户id：" + tenantUserId + " 订单号：" + source.getSourceOrderId() + "资金渠道：" + source.getVehicleAffiliation() + "油资金渠道：" + source.getVehicleAffiliation() + "租户id：" + source.getSourceTenantId() + " 油账户类型：" + source.getOilAccountType() + " 油消费类型：" + source.getOilConsumer() + " 油开票类型：" + source.getOilBillType() + "未找到油资金来源记录");
                    }
                    oos.setNoPayOil(oos.getNoPayOil() + matchNoPayOil);
                    oos.setNoCreditOil(oos.getNoCreditOil() + matchNoCreditOil);
                    oos.setNoRebateOil(oos.getNoRebateOil() + matchNoRebateOil);
                    oos.setPaidOil(oos.getPaidOil() - matchNoPayOil);
                    oos.setPaidCreditOil(oos.getPaidCreditOil() - matchNoCreditOil);
                    oos.setPaidRebateOil(oos.getPaidRebateOil() - matchNoRebateOil);
                    orderOilSourceService.saveOrUpdate(oos);
                    OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, oos.getOrderId(),-1);
                    if (orderLimit == null) {
                        throw new BusinessException("未找到订单号为：" + oos.getOrderId() + "的订单限制记录");
                    }
                    orderLimit.setNoPayOil(orderLimit.getNoPayOil() + source.getMatchAmount());
                    orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - source.getMatchAmount());
                    orderLimit.setPaidOil(orderLimit.getPaidOil() - source.getMatchAmount());
                    orderLimitService.saveOrUpdate(orderLimit);
                }
                limit.setNoPayOil(limit.getNoPayOil() - off.getAmount());
                limit.setOilTurn((limit.getOilTurn() == null ? 0L : limit.getOilTurn()) + off.getAmount());
                orderLimitService.saveOrUpdate(limit);

            }
            //油转现(现金收入)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_RECEIVABLE_IN) {
                off.setInoutSts("in");// 收支状态:收in支out转io
                limit.setPaidCash(limit.getPaidCash() + off.getAmount());
                limit.setWithdrawCash(limit.getWithdrawCash() +off.getAmount() );
                limit.setOrderCash(limit.getOrderCash() + off.getAmount());
            }
            //票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1691 || rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1692) {
                limit.setServiceFee(limit.getServiceFee() + off.getCost());
            }
            //车队
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TURN_CASH_PAYABLE_IN) {
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            //ETC转现(ETC支出)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_ETC) {
                off.setInoutSts("out");// 收支状态:收in支out转io
                // 订单限制表操作
                limit.setNoPayEtc(limit.getNoPayEtc() - off.getAmount());
                limit.setOrderEtc(limit.getOrderEtc() - off.getAmount());
                limit.setEtcIncome(limit.getEtcIncome() + off.getIncome());
                limit.setEtcTurn((limit.getEtcTurn() == null ? 0L : limit.getEtcTurn()) + off.getAmount());
            }
            //ETC转现(现金收入)
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_RECEIVABLE_IN) {
                off.setInoutSts("in");// 收支状态:收in支out转io
                limit.setPaidCash(limit.getPaidCash() + off.getCost());
                limit.setWithdrawCash(limit.getWithdrawCash() + off.getCost());
                limit.setOrderCash(limit.getOrderCash() + off.getCost());
            }
            //车队
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ETC_TURN_CASH_PAYABLE_IN) {
                String tenantBillId =inParam.getTenantBillId();
                String tenantUserName =inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(limit);

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
