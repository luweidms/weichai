package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OrderFundFlowMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFundFlowServiceImpl extends BaseServiceImpl<OrderFundFlowMapper, OrderFundFlow> implements IOrderFundFlowService {

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @Lazy
    @Resource
    private IOrderFundFlowService orderFundFlowService;
    @Lazy
    @Resource
    private IOrderLimitService orderLimitService;
    @Resource
    private IPayoutOrderService payoutOrderService;
    @Resource
    private IServiceMatchOrderService serviceMatchOrderService;
    @Lazy
    @Resource
    private IConsumeOilFlowExtService consumeOilFlowExtService;
    @Resource
    private IOrderOilSourceService orderOilSourceService;
    @Resource
    private ReadisUtil readisUtil;

    @Override
    public List dealToOrderNew(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        long orderId = inParam.getOrderId();
        long businessId = inParam.getBusinessId();
        OrderLimit ol = inParam.getOrderLimitBase();
        if (ol == null) {
            throw new BusinessException("未找到订单限制表信息");
        }
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(0L);//业务流水ID
            off.setTenantId(ol.getTenantId());
            this.createOrderFundFlowNew(inParam, off, user);
            //未到期抵扣车辆费用
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION) {
                ol.setNoPayFinal(ol.getNoPayFinal() - off.getCost());
                ol.setMarginDeduction(ol.getMarginDeduction() + off.getCost());
                off.setInoutSts("out");
            }
            //未到期结算账单金额
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN) {
                ol.setNoPayFinal(ol.getNoPayFinal() - off.getCost());
                ol.setMarginSettlement(ol.getMarginSettlement() + off.getCost());
                off.setInoutSts("out");
            }
            //未到期结算账单金额  应收
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE) {
                ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
                ol.setOrderCash(ol.getOrderCash() + off.getAmount());
                off.setInoutSts("in");
            }
            //票据服务费
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1698) {
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
                off.setInoutSts("in");
            }
            //未到期结算账单金额  应付
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_PAYABLE) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("out");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            //账单车主应付账单车辆费用
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE) {
                off.setInoutSts("out");
            }
            //车队应收车辆费用
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_RECEIVABLE) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(ol);
        }
        return null;
    }

    @Override
    public OrderFundFlow createOrderFundFlowNew(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user) {
        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if (user != null) {
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
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

    @Override
    public List dealToOrderNewCharge(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
        long orderId = inParam.getOrderId();
        long businessId = inParam.getBusinessId();
        OrderLimit ol = inParam.getOrderLimitBase();
        if (ol == null) {
            throw new BusinessException("未找到订单限制表信息");
        }
        for (BusiSubjectsRel rel : rels) {
            //金额为0不进行处理
            if (rel.getAmountFee() == 0L) {
                continue;
            }
            OrderFundFlow off = new OrderFundFlow();
            off.setAmount(rel.getAmountFee());//交易金额（单位分）
            off.setOrderId(orderId);//订单ID
            off.setBusinessId(businessId);//业务类型
            off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
            off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
            off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
            off.setSubjectsId(rel.getSubjectsId());//科目ID
            off.setSubjectsName(rel.getSubjectsName());//科目名称
            off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
            off.setBusiKey(0L);//业务流水ID
            off.setTenantId(ol.getTenantId());
            this.createOrderFundFlowNewCharge(inParam, off, user);
            //到付款应收
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_RECEIVABLE_OVERDUE_SUB) {
                ol.setOrderCash(ol.getOrderCash() + off.getCost());
                ol.setNoPayCash(ol.getNoPayCash() + off.getCost());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getCost());
                off.setInoutSts("in");
            }
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1694) {
                ol.setServiceFee(ol.getServiceFee() + off.getCost());
                off.setInoutSts("in");
            }
            //到付款应付
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_PAYABLE_OVERDUE_SUB) {
                long tenantUserId = inParam.getTenantUserId();
                String tenantBillId = inParam.getTenantBillId();
                String tenantUserName = inParam.getTenantUserName();
                off.setInoutSts("in");
                off.setUserId(tenantUserId);
                off.setBillId(tenantBillId);
                off.setUserName(tenantUserName);
                ol.setArriveFee((ol.getArriveFee() == null ? 0L : ol.getArriveFee()) + off.getCost());
            }
            //抵扣异常罚款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_EXCEPTION) {
                off.setInoutSts("out");
                ol.setOrderCash(ol.getOrderCash() - Math.abs(off.getCost()));
                ol.setNoPayCash(ol.getNoPayCash() - Math.abs(off.getCost()));
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - Math.abs(off.getCost()));
            }
            //抵扣时效罚款
            if (rel.getSubjectsId() == EnumConsts.SubjectIds.ARRIVE_CHARGE_DEDUCTION_PRESCRIPTION) {
                off.setInoutSts("out");
                ol.setOrderCash(ol.getOrderCash() - Math.abs(off.getCost()));
                ol.setNoPayCash(ol.getNoPayCash() - Math.abs(off.getCost()));
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - Math.abs(off.getCost()));
            }
            orderFundFlowService.saveOrUpdate(off);
            orderLimitService.saveOrUpdate(ol);
        }
        return null;
    }

    @Override
    public OrderFundFlow createOrderFundFlowNewCharge(ParametersNewDto inParam, OrderFundFlow off, LoginInfo user) {

        if (off == null) {
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if (user != null) {
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
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

    @Override
    public List dealToOrderNewCode(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {

        long userId = inParam.getUserId() == null ? -1 : inParam.getUserId();
        long businessId = inParam.getBusinessId() == null ? -1 : inParam.getBusinessId();
        long orderId = inParam.getOrderId() == null ? -1 : inParam.getOrderId();
        long accountDatailsId = inParam.getAccountDatailsId() == null ? -1 : inParam.getAccountDatailsId();
        long tenantId = inParam.getTenantId() == null ? -1 : inParam.getTenantId();
        long otherFlowId = inParam.getOtherFlowId() == null ? -1 : inParam.getOtherFlowId();
        long amount = inParam.getAmount() == null ? -1 : inParam.getAmount();
        long payFlowId = inParam.getPayFlowId() == null ? -1:inParam.getPayFlowId();
        String vehicleAffiliation = inParam.getVehicleAffiliation() == null ? "":inParam.getVehicleAffiliation();
        String oilAffiliation = inParam.getOilAffiliation() == null ? "" :inParam.getOilAffiliation() ;
        String sign = inParam.getSign();
        Object obj = inParam.getObj();
        if (rels == null) {
            throw new BusinessException("异常补偿费用明细不能为空!");
        }
        if (obj == null) {
            throw new BusinessException("入参对象不能为空!");
        }
        //资金流向批次
        inParam.setBatchId(CommonUtil.createSoNbr() + "");
        //查询司机订单
        OrderLimit olTemp = null;
        if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {
            olTemp = inParam.getOrderLimitBase();
            if (olTemp == null) {
                throw new BusinessException("订单信息不存在!");
            }
            //处理业务费用明细
            for (BusiSubjectsRel rel : rels) {
                //金额为0不进行处理
                if (rel.getAmountFee() == 0L) {
                    continue;
                }
                //未到期扣减
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_REDUCE) {
                    //资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());//交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());//订单ID
                    off.setBusinessId(businessId);//业务类型
                    off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                    off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                    off.setSubjectsId(rel.getSubjectsId());//科目ID
                    off.setSubjectsName(rel.getSubjectsName());//科目名称
                    off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                    off.setBusiKey(accountDatailsId);//业务流水ID
                    off.setInoutSts("out");//收支状态:收in支out转io
                    off.setTenantId(olTemp.getTenantId());
                    this.createOrderFundFlowNew(inParam, off, user);
                    //订单限制表操作
                    if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {//司机未到期转可用
                        olTemp.setNoPayFinal(olTemp.getNoPayFinal() - off.getAmount());
                        olTemp.setOrderFinal(olTemp.getOrderFinal() - off.getAmount());
                        olTemp.setMarginTurn((olTemp.getMarginTurn() == null ? 0L : olTemp.getMarginTurn()) + off.getAmount());

                    } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
                        olTemp.setNoWithdrawOil(olTemp.getNoWithdrawOil() - off.getAmount());
                    }
                    orderFundFlowService.saveOrUpdate(off);
                    orderLimitService.saveOrUpdate(olTemp);
                }
                //可用增加
                if (rel.getSubjectsId() == EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD) {
                    //资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(rel.getAmountFee());//交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());//订单ID
                    off.setBusinessId(businessId);//业务类型
                    off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                    off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                    off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                    off.setSubjectsId(rel.getSubjectsId());//科目ID
                    off.setSubjectsName(rel.getSubjectsName());//科目名称
                    off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                    off.setBusiKey(accountDatailsId);//业务流水ID
                    off.setInoutSts("in");//收支状态:收in支out转io
                    off.setTenantId(olTemp.getTenantId());
                    this.createOrderFundFlowNew(inParam, off, user);
                    //订单限制表操作
                    if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {//司机未到期转可用
                        olTemp.setNoPayCash(olTemp.getNoPayCash() + off.getAmount());
                        olTemp.setNoWithdrawCash(olTemp.getNoWithdrawCash() + off.getAmount());
                        olTemp.setOrderCash(olTemp.getOrderCash() + off.getAmount());
                    } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
                        olTemp.setWithdrawOil(olTemp.getWithdrawOil() + off.getAmount());
                    }
                    orderFundFlowService.saveOrUpdate(off);
                    orderLimitService.saveOrUpdate(olTemp);
                }
            }
            PayoutOrder payoutOrder = new PayoutOrder();
            payoutOrder.setAmount(amount);
            payoutOrder.setUserId(userId);
            payoutOrder.setTenantId(olTemp.getTenantId());
            payoutOrder.setVehicleAffiliation(vehicleAffiliation);
            payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.CASH_TYPE);//FICTITIOUS_OIL_TYPE
            payoutOrder.setBatchId(payFlowId);
            payoutOrder.setOrderId(olTemp.getOrderId());
            payoutOrder.setCreateTime(LocalDateTime.now());
            payoutOrderService.save(payoutOrder);
        } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
            ConsumeOilFlow consumeOilFlow = (ConsumeOilFlow) obj;
            List<ServiceMatchOrder> serviceMatchOrders = serviceMatchOrderService.getServiceMatchOrderByOtherFlowId(otherFlowId, consumeOilFlow.getUserId(), vehicleAffiliation, oilAffiliation, tenantId, SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL);
            //处理业务费用明细
            Long sumAmount = amount;//总的到期金额
            for (ServiceMatchOrder serviceMatchOrder : serviceMatchOrders) {
                if (sumAmount == 0) {
                    continue;
                }
                //20181012 修改
                if (serviceMatchOrder.getOrderId() == null || serviceMatchOrder.getOrderId().longValue() <= 0) {
                    Long trunAmount = serviceMatchOrder.getNoWithdrawAmount();
                    sumAmount -= trunAmount;
                    serviceMatchOrder.setNoWithdrawAmount(serviceMatchOrder.getNoWithdrawAmount() - trunAmount);
                    if (serviceMatchOrder.getNoWithdrawAmount() == 0) {
                        serviceMatchOrder.setState(1);//已经全部转现
                    }
                    serviceMatchOrder.setWithdrawAmount(serviceMatchOrder.getWithdrawAmount() + trunAmount);
                    serviceMatchOrderService.saveOrUpdate(serviceMatchOrder);
                    continue;
                }
                olTemp = orderLimitService.getOrderLimitByUserIdAndOrderId(serviceMatchOrder.getUserId(), serviceMatchOrder.getOrderId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER);//修改为获取自己的订单，修复之前的方法核销了别人的订单金额
                if (olTemp == null) {
                    ConsumeOilFlowExt ext = consumeOilFlowExtService.queryConsumeOilFlowExtByFlowId(inParam.getFlowId());
                    if (ext != null) {
                        if (ext.getSourceRecordType() == OrderAccountConst.SERVICE_MATCH_ORDER.FROM_STATE_OIL) {
                            OrderOilSource source = orderOilSourceService.getById(ext.getOtherFlowId());
                            if (source == null) {
                                throw new BusinessException("未找到订单油来源记录");
                            }
                            olTemp = orderLimitService.getOrderLimitByUserIdAndOrderId(serviceMatchOrder.getUserId(), source.getOrderId(), SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        }
                    }
                }

                Long trunAmount = serviceMatchOrder.getNoWithdrawAmount();
                if (trunAmount > olTemp.getNoWithdrawOil()) {
                    throw new BusinessException("订单金额不符");
                }
                sumAmount -= trunAmount;
                for (BusiSubjectsRel rel : rels) {
                    //金额为0不进行处理
                    if (rel.getAmountFee() == 0L) {
                        continue;
                    }
                    //未到期扣减
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_REDUCE) {
                        //资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(trunAmount);//交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());//订单ID
                        off.setBusinessId(businessId);//业务类型
                        off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                        off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                        off.setSubjectsId(rel.getSubjectsId());//科目ID
                        off.setSubjectsName(rel.getSubjectsName());//科目名称
                        off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                        off.setBusiKey(accountDatailsId);//业务流水ID
                        off.setInoutSts("out");//收支状态:收in支out转io
                        off.setTenantId(olTemp.getTenantId());
                        this.createOrderFundFlowNew(inParam, off, user);
                        olTemp.setNoWithdrawOil(olTemp.getNoWithdrawOil() - off.getAmount());
                        //订单限制表操作
                        orderFundFlowService.saveOrUpdate(off);
                        orderLimitService.saveOrUpdate(olTemp);
                    }
                    //可用增加
                    if (rel.getSubjectsId() == EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_ADD) {
                        //资金流水操作
                        OrderFundFlow off = new OrderFundFlow();
                        off.setAmount(trunAmount);//交易金额（单位分）
                        off.setOrderId(olTemp.getOrderId());//订单ID
                        off.setBusinessId(businessId);//业务类型
                        off.setBusinessName(readisUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                        off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
                        off.setBookTypeName(readisUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
                        off.setSubjectsId(rel.getSubjectsId());//科目ID
                        off.setSubjectsName(rel.getSubjectsName());//科目名称
                        off.setBusiTable("ACCOUNT_DETAILS");//业务对象表
                        off.setBusiKey(accountDatailsId);//业务流水ID
                        off.setInoutSts("in");//收支状态:收in支out转io
                        off.setTenantId(olTemp.getTenantId());
                        this.createOrderFundFlowNew(inParam, off, user);
                        olTemp.setWithdrawOil(olTemp.getWithdrawOil() + off.getAmount());
                        //订单限制表操作
                        orderFundFlowService.saveOrUpdate(off);
                        orderLimitService.saveOrUpdate(olTemp);
                    }
                }
                serviceMatchOrder.setNoWithdrawAmount(serviceMatchOrder.getNoWithdrawAmount() - trunAmount);
                if (serviceMatchOrder.getNoWithdrawAmount() == 0) {
                    serviceMatchOrder.setState(1);//已经全部转现
                }
                serviceMatchOrder.setWithdrawAmount(serviceMatchOrder.getWithdrawAmount() + trunAmount);
                serviceMatchOrderService.saveOrUpdate(serviceMatchOrder);
                PayoutOrder payoutOrder = new PayoutOrder();
                payoutOrder.setAmount(trunAmount);
                payoutOrder.setUserId(userId);
                payoutOrder.setTenantId(olTemp.getTenantId());
                payoutOrder.setVehicleAffiliation(vehicleAffiliation);
                payoutOrder.setAmountType(OrderAccountConst.FEE_TYPE.FICTITIOUS_OIL_TYPE);//虚拟油
                payoutOrder.setBatchId(payFlowId);
                payoutOrder.setOrderId(olTemp.getOrderId());
                payoutOrder.setCreateTime(LocalDateTime.now());
                payoutOrderService.save(payoutOrder);
            }
            if (sumAmount != 0) {
                throw new BusinessException("未到期金额和订单明细金额不符合");
            }
        } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(sign)) {
            List<ServiceMatchOrder> serviceMatchOrders = serviceMatchOrderService.getServiceMatchOrderByOtherFlowId(otherFlowId, userId, vehicleAffiliation, oilAffiliation, tenantId, SysStaticDataEnum.SERVICE_BUSI_TYPE.REPAIR);
            //处理业务费用明细
            for (ServiceMatchOrder serviceMatchOrder : serviceMatchOrders) {
                if (amount == 0) {
                    continue;
                }
                serviceMatchOrder.setNoWithdrawAmount(serviceMatchOrder.getNoWithdrawAmount() - amount);
                serviceMatchOrder.setState(1);//已经全部转现
                serviceMatchOrder.setWithdrawAmount(serviceMatchOrder.getWithdrawAmount() + amount);
                serviceMatchOrderService.save(serviceMatchOrder);
            }
        }
        return null;
    }


}
