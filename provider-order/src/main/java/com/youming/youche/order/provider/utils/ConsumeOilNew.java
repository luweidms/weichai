package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IConsumeOilFlowExtService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOilRecord56kService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderStateTrackOperService;
import com.youming.youche.order.api.order.IRechargeOilSourceService;
import com.youming.youche.order.api.order.IServiceBlanceConfigService;
import com.youming.youche.order.api.order.IServiceMatchOrderService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.ConsumeOilFlowExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.RechargeOilSource;
import com.youming.youche.order.domain.order.ServiceMatchOrder;
import com.youming.youche.order.dto.ParametersNewDto;

import com.youming.youche.system.api.ISysTenantDefService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsumeOilNew {
    private final IOrderOilSourceService orderOilSourceService;
    private final IOrderLimitService orderLimitService;
    private final IRechargeOilSourceService rechargeOilSourceService;
    private final IServiceMatchOrderService serviceMatchOrderService;
    private final IConsumeOilFlowExtService consumeOilFlowExtService;
    private final IServiceBlanceConfigService serviceBlanceConfigService;
    final IOilRechargeAccountService oilRechargeAccountService;
    private final IOilRecord56kService oilRecord56kService;
    private final IOrderStateTrackOperService orderStateTrackOperService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;


    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {

        Long userId = inParam.getUserId();
        long serviceUserId = inParam.getServiceUserId();
        long productId = inParam.getProductId();
        OrderOilSource oilSource = inParam.getOilSource();
        ConsumeOilFlow cof = inParam.getConsumeOilFlow();
        ConsumeOilFlowExt ext = inParam.getConsumeOilFlowExt();
        RechargeOilSource rechargeOilSource = inParam.getRechargeOilSource();
        if (rels == null) {
            throw new BusinessException("购油费用明细不能为空!");
        }
        if (cof == null) {
            throw new BusinessException("油消费记录不能为空");
        }
        if (ext == null) {
            throw new BusinessException("油消费记录不能为空");
        }
        if (oilSource == null) {
            throw new BusinessException("油来源记录不能为空");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //处理业务费用明细
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            //消费预付油卡
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_OIL_SUB || rel.getSubjectsId() == EnumConsts.SubjectIds.CONSUME_RECHARGE_OIL_SUB) {
                String vehicleAffiliation = "";
                String oilAffiliation = "";
                Integer oilConsumer = -1;
                Long sourceTenantId = 0l;
                Long orderId = 0L;
                String orderNum = "";
                Integer oilAccountType = -1;
                Integer oilBillType = -1;
                // 可能传入的这个对象 不为null 但是里面的变量都为null  具体业务 司机在司机账户充值 oilSource 表应该是空的 改之前给你传的是null  但是这个方法里 69 有不允许这个对象为null
                if (oilSource != null &&oilSource.getId()!=null) {
                    OrderOilSource source = oilSource;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    oilConsumer = source.getOilConsumer();
                    orderId = source.getSourceOrderId();
                    orderNum = String.valueOf(source.getSourceOrderId());
                    oilAccountType = source.getOilAccountType();
                    oilBillType = source.getOilBillType();
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
                    OrderLimit limit = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, source.getOrderId(), -1);
                    if (limit == null) {
                        throw new BusinessException("根据用户id：" + userId + "未找到订单号为：" + source.getOrderId() + "的订单来源记录");
                    }
                    limit.setNoPayOil(limit.getNoPayOil() - source.getMatchAmount());
                    limit.setPaidOil(limit.getPaidOil() + source.getMatchAmount());
                    if (source.getOilConsumer().intValue() == OrderConsts.OIL_CONSUMER.SHARE) {
                        limit.setWithdrawOil(limit.getWithdrawOil() + source.getMatchAmount());
                    } else {
                        limit.setNoWithdrawOil(limit.getNoWithdrawOil() + source.getMatchAmount());
                    }
                    orderLimitService.saveOrUpdate(limit);
                    if (source.getOrderId().longValue() != source.getSourceOrderId().longValue()) {
                        Long tenantUserId = sysTenantDefService.getSysTenantDef(source.getTenantId()).getAdminUser();
                        if (tenantUserId == null || tenantUserId <= 0L) {
                            throw new BusinessException("根据租户id" + source.getTenantId() + "没有找到租户的用户id!");
                        }
                        OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, source.getSourceOrderId(), -1);
                        if (orderLimit == null) {
                            throw new BusinessException("未找到订单号为：" + source.getSourceOrderId() + "的订单限制记录");
                        }
                        orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - source.getMatchAmount());
                        orderLimit.setWithdrawOil(orderLimit.getWithdrawOil() == null ? 0L : orderLimit.getWithdrawOil() + source.getMatchAmount());
                        orderLimitService.saveOrUpdate(orderLimit);
                    }

                } else if (rechargeOilSource != null) {
                    RechargeOilSource source = rechargeOilSource;
                    vehicleAffiliation = source.getVehicleAffiliation();
                    oilAffiliation = source.getOilAffiliation();
                    sourceTenantId = source.getSourceTenantId();
                    oilConsumer = source.getOilConsumer();
                    orderNum = source.getSourceOrderId();
                    oilAccountType = source.getOilAccountType();
                    oilBillType = source.getOilBillType();
                    long matchNoPayOil = source.getMatchNoPayOil() == null ? 0 : source.getMatchNoPayOil();
                    long matchNoRebateOil = source.getMatchNoRebateOil() == null ? 0 : source.getMatchNoRebateOil();
                    long matchNoCreditOil = source.getMatchNoCreditOil() == null ? 0 : source.getMatchNoCreditOil();
                    source.setNoPayOil(source.getNoPayOil() - matchNoPayOil);
                    source.setNoCreditOil(source.getNoCreditOil() - matchNoCreditOil);
                    source.setNoRebateOil(source.getNoRebateOil() - matchNoRebateOil);
                    source.setPaidOil(source.getPaidOil() + matchNoPayOil);
                    source.setPaidCreditOil(source.getPaidCreditOil() + matchNoCreditOil);
                    source.setPaidRebateOil(source.getPaidRebateOil() + matchNoRebateOil);
                    rechargeOilSourceService.saveOrUpdate(source);
                    if (!source.getRechargeOrderId().equals(source.getSourceOrderId())) {
                        orderId = Long.valueOf(source.getSourceOrderId());
                        Long tenantUserId = sysTenantDefService.getSysTenantDef(source.getTenantId()).getAdminUser();
                        if (tenantUserId == null || tenantUserId <= 0L) {
                            throw new BusinessException("根据租户id" + source.getTenantId() + "没有找到租户的用户id!");
                        }
                        OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(tenantUserId, Long.valueOf(source.getSourceOrderId()), -1);
                        if (orderLimit == null) {
                            throw new BusinessException("未找到订单号为：" + source.getSourceOrderId() + "的订单限制记录");
                        }
                        orderLimit.setNoWithdrawOil(orderLimit.getNoWithdrawOil() - source.getMatchAmount());
                        orderLimit.setWithdrawOil(orderLimit.getWithdrawOil() == null ? 0L : orderLimit.getWithdrawOil() - source.getMatchAmount());
                        orderLimitService.saveOrUpdate(orderLimit);
                    }
                } else {
                    throw new BusinessException("订单油记录转换出错");
                }
                //司机加油订单记录表
                ServiceMatchOrder orderOil = new ServiceMatchOrder();
                orderOil.setUserId(userId);
                orderOil.setAmount(rel.getAmountFee());
                orderOil.setOrderId(orderId);
                orderOil.setProductId(productId);
                orderOil.setServiceUserId(serviceUserId);
                orderOil.setAccountBalance(0L);
                orderOil.setNoWithdrawAmount(rel.getAmountFee());
                orderOil.setWithdrawAmount(0L);
                if (cof.getState() == OrderAccountConst.ORDER_LIMIT_STS.FIANL_STS1) {
                    orderOil.setNoWithdrawAmount(0l);
                    orderOil.setWithdrawAmount(rel.getAmountFee());
                }
                orderOil.setAdvanceFee(0L);
                orderOil.setTenantId(sourceTenantId);
                orderOil.setCreateDate(LocalDateTime.now());
                orderOil.setState(cof.getState());//未提现
                orderOil.setVehicleAffiliation(vehicleAffiliation);
                orderOil.setOilAffiliation(oilAffiliation);
                orderOil.setIsNeedBill(-1);
                if (oilSource != null) {
                    orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_OIL);//来源订单油
                } else if (rechargeOilSource != null) {
                    orderOil.setFromState(OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_ACCOUNT);//来源司机账户充值油

                }
                orderOil.setServiceType(SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL);//油老板
                orderOil.setOtherFlowId(cof.getId());
                serviceMatchOrderService.saveOrUpdate(orderOil);
                ext.setOilAccountType(oilAccountType);
                ext.setOilBillType(oilBillType);
                consumeOilFlowExtService.saveOrUpdate(ext);
                if (oilConsumer == OrderConsts.OIL_CONSUMER.SHARE) {
                    Long amount = ((ext.getRechargeOil() == null ? 0l : ext.getRechargeOil()) + (ext.getCreditOil() == null ? 0L : ext.getCreditOil()));
                    serviceBlanceConfigService.doUpdServiceBlanceConfig(ext.getOilComUserId(), cof.getUserId(), productId, amount);
                }
                if (oilConsumer == OrderConsts.OIL_CONSUMER.SHARE) {
                    oilRecord56kService.consumeOil(orderNum, rel.getAmountFee(), sourceTenantId, inParam, user);
                }
                if (orderId > 0) {

                    orderStateTrackOperService.saveOrUpdate(orderId, -1L, OrderConsts.OrderOpType.SCAN_QR_CODE_OIL);
                }
            }
        }
        return null;
    }
}
