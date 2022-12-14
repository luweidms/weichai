package com.youming.youche.order.provider.service.order;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.order.dto.OrderLimitDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.provider.utils.BusiToOrderUtils;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.MatchAmountUtil;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.order.vo.QueryOrderProblemInfoQueryVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * ????????????????????? ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OrderLimitServiceImpl extends BaseServiceImpl<OrderLimitMapper, OrderLimit> implements IOrderLimitService {

    private static final Logger log = LoggerFactory.getLogger(OrderLimitServiceImpl.class);
    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    IOrderAccountService orderAccountService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @Lazy
    @Resource
    IOrderInfoService iOrderInfoService;

    @Resource
    IOrderInfoExtService iOrderInfoExtService;

    @Resource
    IOrderFeeService iOrderFeeService;

    @Resource
    IOrderSchedulerService iOrderSchedulerService;

    @Resource
    IOrderFeeExtService iOrderFeeExtService;

    @Resource
    IOrderProblemInfoService orderProblemInfoService;

    @Resource
    IOrderLimitService iOrderLimitService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysOperatorService;
    @Resource
    private IOrderLimitService orderLimitService;
    @Resource
    private IBusiSubjectsRelService busiSubjectsRelService;
    @Resource
    private IAccountDetailsService accountDetailsService;
    @Resource
    private IPayFeeLimitService payFeeLimitService;
    @Resource
    private IPayoutIntfService payoutIntfService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private BusiToOrderUtils busiToOrderUtils;
    @Resource
    private ISysOperLogService sysOperLogService;
    @Resource
    private OrderLimitMapper orderLimitMapper;
    @Resource
    private IOrderFundFlowService orderFundFlowService;
    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;
    @Resource
    private IPayoutOrderService payoutOrderService;
    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    private LoginUtils loginUtils;
    @Resource
    RedisUtil redisUtil;
    @Resource
    IAccountDetailsService iAccountDetailsService;
    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IOperationOilService iOperationOilService;
    @Resource
    IOrderOilSourceService iOrderOilSourceService;
    @Resource
    IServiceMatchOrderService iServiceMatchOrderService;
    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService1;
    @Resource
    IConsumeOilFlowExtService consumeOilFlowExtService;
    @Resource
    IBillPlatformService billPlatformService;
    @Resource
    IBillAgreementService billAgreementService;
    @Resource
    IUserRepairMarginService userRepairMarginService;
    @DubboReference(version = "1.0.0")
    IUserRepairInfoService userRepairInfoService;
    @Resource
    IConsumeOilFlowService consumeOilFlowService;

    @Override
    public OrderLimit getOrderLimitByUserIdAndOrderId(Long userId, Long orderId, Integer userType) {
        LambdaQueryWrapper<OrderLimit> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderLimit::getUserId, userId)
                .eq(OrderLimit::getOrderId, orderId);
        if (userType > 0) {
            lambda.eq(OrderLimit::getUserType, userType);
        }
        return this.getOne(lambda);
    }

    @Override
    public List<OrderLimit> getOrderLimit(Long orderId, Long tenantId, Integer userType) {
        LambdaQueryWrapper<OrderLimit> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderLimit::getTenantId, tenantId)
                .eq(OrderLimit::getOrderId, orderId);
        if (userType > 0) {
            lambda.eq(OrderLimit::getUserType, userType);
        }
        lambda.orderByAsc(OrderLimit::getOrderDate);
        return this.list(lambda);
    }


    /**
     * ???????????????????????????
     * //TODO  ?????????
     *
     * @param orderIds ??????id
     * @param userType
     * @return
     */
    @Override
    public List<OrderLimitDto> hasFinalOrderLimit(List<Long> orderIds, Integer userType) {
        List<OrderLimitDto> orderLimitDtos = new ArrayList<>();
        if (orderIds == null || orderIds.size() <= 0) {
            throw new BusinessException("??????????????????");
        }
        QueryWrapper<OrderLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fianl_sts", 1)
                .gt("no_pay_final", 0);
        queryWrapper.in("order_id", orderIds);
        queryWrapper.eq("user_type", userType);
        List<OrderLimit> orderLimits = baseMapper.selectList(queryWrapper);
        for (Long orderId : orderIds) {
            OrderLimitDto orderLimitDto = new OrderLimitDto();
            orderLimitDto.setOrderId(orderId);
            orderLimitDto.setIsAddProblem(false);
            orderLimitDtos.add(orderLimitDto);
            for (int i = 0; i < orderLimits.size(); i++) {
                OrderLimit orderLimit = orderLimits.get(i);
                if (String.valueOf(orderId + "").equals(String.valueOf(orderLimit.getOrderId() + ""))) {
                    OrderLimitDto orderLimitDto1 = new OrderLimitDto();
                    orderLimitDto1.setOrderId(orderId);
                    orderLimitDto1.setIsAddProblem(true);
                    orderLimitDtos.add(orderLimitDto1);
                }
            }
        }
        return orderLimitDtos;
    }

    @Override
    public OrderLimitDto hasFinalOrderLimit(Long orderId, Integer userType) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("????????????????????????");
        }

        LambdaQueryWrapper<OrderLimit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.notIn(OrderLimit::getFianlSts, 1); // ????????????
        queryWrapper.gt(OrderLimit::getNoPayFinal, 0); // ?????????
        queryWrapper.eq(OrderLimit::getOrderId, orderId);
        if (userType != null && userType > 0) {
            queryWrapper.eq(OrderLimit::getUserType, userType);
        }

        // ????????????????????????
        OrderLimit orderLimit = baseMapper.selectOne(queryWrapper);
        OrderLimitDto orderLimitDto = new OrderLimitDto();
        orderLimitDto.setOrderId(orderId);
        if (null != orderLimit) {
            orderLimitDto.setIsAddProblem(true);
        } else {
            orderLimitDto.setIsAddProblem(false);
        }

        return orderLimitDto;
    }

    @Override
    public List<OrderLimit> getOrderLimit(Long userId, String capitalChannel, String noPayType, Long tenantId, Integer userType) {
        LambdaQueryWrapper<OrderLimit> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderLimit::getUserId, userId);
        if (userType > 0) {
            lambda.eq(OrderLimit::getUserType, userType);
        }
        lambda.eq(OrderLimit::getVehicleAffiliation, capitalChannel);
        if (OrderAccountConst.NO_PAY.NO_PAY_CASH.equals(noPayType)) {
            lambda.gt(OrderLimit::getNoPayCash, 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_OIL.equals(noPayType)) {
            lambda.gt(OrderLimit::getNoPayOil, 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_ETC.equals(noPayType)) {
            lambda.gt(OrderLimit::getNoPayEtc, 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_FINAL.equals(noPayType)) {
            lambda.gt(OrderLimit::getNoPayFinal, 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_DEBT.equals(noPayType)) {
            lambda.gt(OrderLimit::getNoPayDebt, 0L);
        } else if (OrderAccountConst.NO_PAY.ACCOUNT_BALANCE.equals(noPayType)) {
            lambda.gt(OrderLimit::getAccountBalance, 0L);
        }
        if (tenantId != null && tenantId > 0L) {
            lambda.eq(OrderLimit::getTenantId, tenantId);
        }
        lambda.orderByAsc(OrderLimit::getOrderDate);
        return this.list(lambda);
    }

    @Override
    public List<OrderLimit> matchAmountToOrderLimit(Long amount, Long income, Long backIncome, List<OrderLimit> orderLimits) {
        double incomeRatio = ((double) income) / ((double) amount);
        double backIncomeRatio = ((double) backIncome) / ((double) amount);
        for (OrderLimit ol : orderLimits) {
            Long orderAmount = ol.getNoPayCash();
            if (orderAmount == null || orderAmount == 0L) {
                continue;
            }
            if (amount > orderAmount) {
                ol.setMatchAmount(orderAmount);
                ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                //????????????=?????????-???????????????
                amount = amount - orderAmount;
                income -= ol.getMatchIncome();
                backIncome -= ol.getMatchBackIncome();
            } else if (amount <= orderAmount) {
                ol.setMatchAmount(amount);
                ol.setMatchIncome(income);
                ol.setMatchBackIncome(backIncome);
                break;
            }
        }
        return orderLimits;
    }

    @Override
    public List<OrderLimit> getOrderLimitUserId(Long userId, Long orderId, Long tenantId, Integer userType, LoginInfo user) {
        LambdaQueryWrapper<OrderLimit> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderLimit::getUserId, userId)
                .eq(OrderLimit::getOrderId, orderId);
        if (userType > 0) {
            lambda.eq(OrderLimit::getUserType, userType);
        }
        if (tenantId > -1) {
            lambda.eq(OrderLimit::getTenantId, tenantId);
        } else {
            lambda.eq(OrderLimit::getTenantId, user.getTenantId());
        }
        return this.list(lambda);
    }

    @Override
    public List<OrderLimit> matchAmountToOrderLimit(Long amount, Long income, Long backIncome,
                                                    Long userId, String vehicleAffiliation, Long tenantId,
                                                    Integer isNeedBill, List<OrderLimit> orderLimits) {
        double incomeRatio = ((double) income) / ((double) amount);
        double backIncomeRatio = ((double) backIncome) / ((double) amount);
        for (OrderLimit ol : orderLimits) {
            Long orderAmount = ol.getNoPayCash();
            //????????????????????????????????????????????????
            if (isNeedBill == OrderAccountConst.ORDER_IS_NEED_BILL.YES) {
                if (orderAmount != null && orderAmount != 0L && ol.getUserId().equals(userId) && ol.getVehicleAffiliation().equals(vehicleAffiliation) && tenantId.equals(ol.getTenantId())) {
                    if (amount > orderAmount) {
                        ol.setMatchAmount(orderAmount);
                        ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                        //????????????=?????????-???????????????
                        amount = amount - orderAmount;
                    } else if (amount <= orderAmount) {
                        ol.setMatchAmount(amount);
                        ol.setMatchIncome(new Double(amount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(amount * backIncomeRatio).longValue());
                        break;
                    }
                }
            } else {
                if (orderAmount != null && orderAmount != 0L && ol.getUserId().equals(userId) && ol.getVehicleAffiliation().equals(vehicleAffiliation) && tenantId.equals(ol.getTenantId()) && ol.getIsNeedBill() == OrderAccountConst.ORDER_BILL_TYPE.notNeedBill) {
                    if (amount > orderAmount) {
                        ol.setMatchAmount(orderAmount);
                        ol.setMatchIncome(new Double(orderAmount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(orderAmount * backIncomeRatio).longValue());
                        //????????????=?????????-???????????????
                        amount = amount - orderAmount;
                    } else if (amount <= orderAmount) {
                        ol.setMatchAmount(amount);
                        ol.setMatchIncome(new Double(amount * incomeRatio).longValue());
                        ol.setMatchBackIncome(new Double(amount * backIncomeRatio).longValue());
                        break;
                    }
                }
            }
        }
        return orderLimits;
    }

    @Override
    public void createOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderScheduler scheduler, OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt, Long tenantId) {
        if (orderFee == null) {
            throw new BusinessException("??????????????????!");
        } else if (orderFee.getOrderId() == null || orderFee.getOrderId() <= 0L) {
            throw new BusinessException("???????????????");
        }
        List<OrderLimit> olList = this.getOrderLimit(orderFee.getOrderId(), tenantId, -1);
        OrderLimit ol = null;
        if (olList.isEmpty()) {// ??????
            ol = new OrderLimit();
            //????????????
            if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    throw new BusinessException("?????????????????????????????????????????????id");
                }
                Long toTenantId = orderInfo.getToTenantId();
                Long toTenantUserId = sysTenantDefService.getSysTenantDef(toTenantId).getAdminUser();
                if (toTenantUserId == null || toTenantUserId <= 0L) {
                    throw new BusinessException("???????????????????????????id!");
                }
                UserDataInfo toTenantUser = userDataInfoService.get(toTenantUserId);
                if (toTenantUser == null) {
                    throw new BusinessException("????????????????????????");
                }
                ol.setUserId(toTenantUserId);
                //????????????????????????
                ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                //????????????????????????
                ol.setUserName(toTenantUser.getLinkman());
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
                //usertype?????????????????????????????????????????????????????????????????????
                ol.setUserType(SysStaticDataEnum.USER_TYPE.CUSTOMER_USER);

            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
                //???????????????
                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    //?????????
                    if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                        ol.setUserId(scheduler.getCarDriverId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCarDriverMan());
                    } else {
                        Long toTenantId = orderInfo.getToTenantId();
                        Long toTenantUserId = sysTenantDefService.getSysTenantDef(toTenantId).getAdminUser();
                        if (toTenantUserId == null || toTenantUserId <= 0L) {
                            throw new BusinessException("???????????????????????????id!");
                        }
                        UserDataInfo toTenantUser = userDataInfoService.get(toTenantUserId);
                        if (toTenantUser == null) {
                            throw new BusinessException("????????????????????????");
                        }
                        ol.setUserId(toTenantUserId);
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        //????????????????????????
                        ol.setUserName(toTenantUser.getLinkman());
                    }
                } else {
                    if (scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        ol.setUserId(scheduler.getCollectionUserId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCollectionUserName());
                        //??????????????????????????????????????????
                        if (scheduler.getCarDriverId() != null && scheduler.getCarDriverId().longValue() != scheduler.getCollectionUserId().longValue()) {
                            OrderLimit collectionOrderLimit = this.createCollectionOrderLimit(orderInfo, orderInfoExt, orderFee, scheduler, orderGoodsInfo, orderFeeExt, tenantId);
                            this.saveOrUpdate(collectionOrderLimit);
                        }
                    } else {
                        ol.setUserId(scheduler.getCarDriverId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCarDriverMan());
                    }
                }
            }
            ol.setOrderId(orderFee.getOrderId());
            if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                ol.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            } else {
                ol.setVehicleAffiliation(orderFee.getVehicleAffiliation());
            }
            ol.setSourceOrderId(orderInfo.getFromOrderId());
            ol.setTenantId(tenantId);
            ol.setOrderCash(0L);
            ol.setPaidCash(0L);
            ol.setNoPayCash(0L);
            ol.setWithdrawCash(0L);
            ol.setNoWithdrawCash(0L);
            ol.setOrderOil(0L);
            ol.setCostEntityOil(0L);
            ol.setOrderEntityOil(0L);
            ol.setPaidOil(0L);
            ol.setNoPayOil(0L);
            ol.setOilIncome(0L);
            ol.setNoWithdrawOil(0L);
            ol.setWithdrawOil(0L);
            ol.setOrderEtc(0L);
            ol.setPaidEtc(0L);
            ol.setNoPayEtc(0L);
            ol.setEtcIncome(0L);
            ol.setDebtMoney(0L);
            ol.setPaidDebt(0L);
            ol.setNoPayDebt(0L);
            ol.setOrderFinal(0L);
            ol.setPaidFinalPay(0L);
            ol.setNoPayFinal(0L);
            ol.setFinalIncome(0L);
            ol.setMatchAmount(0L);
            ol.setMatchBackIncome(0L);
            ol.setMatchIncome(0L);
            ol.setLoanAmount(0L);
            ol.setVerificationLoan(0L);
            ol.setNoVerificationLoan(0L);
            ol.setPledgeOilcardFee(0L);
            ol.setOrderDate(scheduler.getDependTime());
            ol.setCreateDate(LocalDateTime.now());
            ol.setTotalFee(0L);
            ol.setAccountBalance(0L);
            ol.setMarginTurn(0L);
            ol.setMarginAdvance(0L);
            ol.setOilTurn(0L);
            ol.setEtcTurn(0L);
            ol.setMarginSettlement(0L);
            ol.setMarginDeduction(0L);
            ol.setReleaseOilcardFee(0L);
            ol.setArriveFee(0L);
            ol.setIsNeedBill(orderInfo.getIsNeedBill());
            ol.setOilAffiliation(orderInfoExt.getOilAffiliation());
            ol.setOilConsumer(orderFeeExt.getOilConsumer());
            ol.setOilAccountType(orderFeeExt.getOilAccountType());
            ol.setOilBillType(orderFeeExt.getOilBillType());
            ol.setServiceFee(0L);
            if (ol.getVehicleAffiliation() != null && ol.getOilAffiliation() != null && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getOilAffiliation()) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getOilAffiliation())) {
                if (!ol.getVehicleAffiliation().equals(ol.getOilAffiliation())) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
            }
            if (ol.getVehicleAffiliation() != null && (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) ||
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()))) {
                if (ol.getOilAffiliation() != null && Long.parseLong(ol.getOilAffiliation()) > Long.parseLong(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)) {
                    throw new BusinessException("???????????????????????????????????????");
                }
            }
            if (scheduler.getCarDriverId() != null && scheduler.getCarDriverId() > 0 && scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                // ????????????????????????????????????
                this.save(createOrderLimitCopilot(orderInfo, orderInfoExt, orderFee, scheduler, orderGoodsInfo, orderFeeExt, tenantId));
            }
        } else {
            //????????????
            if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    throw new BusinessException("?????????????????????????????????????????????id");
                }
                Long toTenantId = orderInfo.getToTenantId();
                Long toTenantUserId = sysTenantDefService.getSysTenantDef(toTenantId).getAdminUser();
                if (toTenantUserId == null || toTenantUserId <= 0L) {
                    throw new BusinessException("???????????????????????????id!");
                }
                UserDataInfo toTenantUser = userDataInfoService.get(toTenantUserId);
                if (toTenantUser == null) {
                    throw new BusinessException("????????????????????????");
                }
                boolean hasUserId = false;
                for (OrderLimit limit : olList) {
                    if (limit.getUserId() == null) {
                        ol = limit;
                        hasUserId = true;
                        break;
                    }
                    if (limit.getUserId().longValue() == toTenantUserId.longValue()) {
                        ol = limit;
                        hasUserId = true;
                        break;
                    }
                }
                if (!hasUserId) {
                    ol = new OrderLimit();
                    BeanUtils.copyProperties(olList.get(0), ol);
                    this.resetOrderLimit(ol);
                }
                ol.setUserId(toTenantUserId);
                //????????????????????????
                ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                //????????????????????????
                ol.setUserName(toTenantUser.getLinkman());
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
                ol = olList.get(0);
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
                //???????????????
                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    //?????????
                    if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                        boolean hasUserId = false;
                        for (OrderLimit limit : olList) {
                            if (limit.getUserId() == null) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                            if (scheduler.getCarDriverId() == null) {
                                hasUserId = false;
                                break;
                            }
                            if (limit.getUserId().longValue() == scheduler.getCarDriverId().longValue()) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                        }
                        if (!hasUserId) {
                            ol = new OrderLimit();
                            BeanUtils.copyProperties(olList.get(0), ol);
                            this.resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCarDriverId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCarDriverMan());
                    } else {
                        Long toTenantId = orderInfo.getToTenantId();
                        Long toTenantUserId = sysTenantDefService.getSysTenantDef(toTenantId).getAdminUser();
                        if (toTenantUserId == null || toTenantUserId <= 0L) {
                            throw new BusinessException("???????????????????????????id!");
                        }
                        UserDataInfo toTenantUser = userDataInfoService.get(toTenantUserId);
                        if (toTenantUser == null) {
                            throw new BusinessException("????????????????????????");
                        }
                        boolean hasUserId = false;
                        for (OrderLimit limit : olList) {
                            if (limit.getUserId() == null) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                            if (limit.getUserId().longValue() == toTenantUserId.longValue()) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                        }
                        if (!hasUserId) {
                            ol = new OrderLimit();
                            BeanUtils.copyProperties(olList.get(0), ol);
                            this.resetOrderLimit(ol);
                        }
                        ol.setUserId(toTenantUserId);
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        //????????????????????????
                        ol.setUserName(toTenantUser.getLinkman());
                    }
                } else {
                    if (scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        boolean hasUserId = false;
                        for (OrderLimit limit : olList) {
                            if (limit.getUserId() == null) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                            if (scheduler.getCollectionUserId() == null) {
                                hasUserId = false;
                                break;
                            }
                            if (limit.getUserId().longValue() == scheduler.getCollectionUserId().longValue()) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                        }
                        if (!hasUserId) {
                            ol = new OrderLimit();
                            BeanUtils.copyProperties(olList.get(0), ol);
                            this.resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCollectionUserId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCollectionUserName());
                        //??????
                        boolean hasCollectionDriverUserId = false;
                        OrderLimit collectionDriverLimit = null;
                        for (OrderLimit limit : olList) {
                            if (limit.getUserId() == null) {
                                collectionDriverLimit = limit;
                                hasCollectionDriverUserId = true;
                                break;
                            }
                            if (scheduler.getCarDriverId() == null) {
                                hasCollectionDriverUserId = false;
                                break;
                            }
                            if (limit.getUserId().longValue() == scheduler.getCarDriverId().longValue()) {
                                collectionDriverLimit = limit;
                                hasCollectionDriverUserId = true;
                                break;
                            }
                        }
                        if (!hasCollectionDriverUserId) {
                            collectionDriverLimit = new OrderLimit();
                            BeanUtils.copyProperties(olList.get(0), collectionDriverLimit);
                            this.resetOrderLimit(collectionDriverLimit);
                        }
                        collectionDriverLimit.setUserId(scheduler.getCarDriverId());
                        //????????????????????????
                        collectionDriverLimit.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        collectionDriverLimit.setUserName(scheduler.getCarDriverMan());
                        collectionDriverLimit.setUserPhone(scheduler.getCarDriverPhone());
                        //?????????????????????????????????
                        if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                            collectionDriverLimit.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                        } else {
                            collectionDriverLimit.setVehicleAffiliation(orderFee.getVehicleAffiliation());
                        }
                        collectionDriverLimit.setOilAffiliation(orderInfoExt.getOilAffiliation());
                        collectionDriverLimit.setIsNeedBill(orderInfo.getIsNeedBill());
                        collectionDriverLimit.setOilConsumer(orderFeeExt.getOilConsumer());
                        collectionDriverLimit.setOilAccountType(orderFeeExt.getOilAccountType());
                        collectionDriverLimit.setOilBillType(orderFeeExt.getOilBillType());
                        if (collectionDriverLimit.getIsNeedBill() != null && collectionDriverLimit.getIsNeedBill() <= 0) {
                            collectionDriverLimit.setVerificationState(0);//?????????????????????
                        } else {
                            collectionDriverLimit.setVerificationState(-1);
                        }
                        this.saveOrUpdate(collectionDriverLimit);
                    } else {
                        boolean hasUserId = false;
                        for (OrderLimit limit : olList) {
                            if (limit.getUserId() == null) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                            if (scheduler.getCarDriverId() == null) {
                                hasUserId = false;
                                break;
                            }
                            if (limit.getUserId().longValue() == scheduler.getCarDriverId().longValue()) {
                                ol = limit;
                                hasUserId = true;
                                break;
                            }
                        }
                        if (!hasUserId) {
                            ol = new OrderLimit();
                            BeanUtils.copyProperties(olList.get(0), ol);
                            this.resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCarDriverId());
                        //????????????????????????
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        ol.setUserName(scheduler.getCarDriverMan());
                    }
                }
            }
//			ol.setVehicleAffiliation(orderFee.getVehicleAffiliation());
            if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                ol.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            } else {
                ol.setVehicleAffiliation(orderFee.getVehicleAffiliation());
            }
            ol.setOilAffiliation(orderInfoExt.getOilAffiliation());
            ol.setIsNeedBill(orderInfo.getIsNeedBill());
            ol.setOilConsumer(orderFeeExt.getOilConsumer());
            ol.setOilAccountType(orderFeeExt.getOilAccountType());
            ol.setOilBillType(orderFeeExt.getOilBillType());
            if (ol.getVehicleAffiliation() != null && ol.getOilAffiliation() != null && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getOilAffiliation()) &&
                    !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getOilAffiliation())) {
                if (!ol.getVehicleAffiliation().equals(ol.getOilAffiliation())) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
            }
            if (ol.getVehicleAffiliation() != null && (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) ||
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()))) {
                if (ol.getOilAffiliation() != null && Long.parseLong(ol.getOilAffiliation()) > Long.parseLong(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)) {
                    throw new BusinessException("???????????????????????????????????????");
                }
            }
            // ?????????????????????????????????????????????????????????????????????
            if (scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                boolean hasCopilotUserId = false;
                for (OrderLimit limit : olList) {
                    if (limit.getUserId().longValue() == scheduler.getCopilotUserId().longValue()) {
                        limit.setUserId(scheduler.getCopilotUserId());
                        //????????????????????????
                        limit.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //????????????????????????
                        limit.setUserName(scheduler.getCopilotMan());
                        limit.setUserPhone(scheduler.getCopilotPhone());
                        limit.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                        limit.setOilAffiliation(orderInfoExt.getOilAffiliation());
                        limit.setIsNeedBill(orderInfo.getIsNeedBill());
                        limit.setOilConsumer(orderFeeExt.getOilConsumer());
                        limit.setOilAccountType(orderFeeExt.getOilAccountType());
                        limit.setOilBillType(orderFeeExt.getOilBillType());
                        if (limit.getIsNeedBill() != null && ol.getIsNeedBill() <= 0) {
                            limit.setVerificationState(0);//?????????????????????
                        } else {
                            limit.setVerificationState(-1);
                        }
                        this.saveOrUpdate(limit);
                        hasCopilotUserId = true;
                        break;
                    }
                }
                if (!hasCopilotUserId) {
                    OrderLimit olCopilot = new OrderLimit();
                    BeanUtils.copyProperties(olList.get(0), olCopilot);
                    this.resetOrderLimit(olCopilot);
                    olCopilot.setUserId(scheduler.getCopilotUserId());
                    //????????????????????????
                    olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                    //????????????????????????
                    olCopilot.setUserName(scheduler.getCopilotMan());
                    olCopilot.setUserPhone(scheduler.getCopilotPhone());
                    olCopilot.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                    olCopilot.setOilAffiliation(orderInfoExt.getOilAffiliation());
                    olCopilot.setIsNeedBill(orderInfo.getIsNeedBill());
                    olCopilot.setOilConsumer(orderFeeExt.getOilConsumer());
                    olCopilot.setOilAccountType(orderFeeExt.getOilAccountType());
                    olCopilot.setOilBillType(orderFeeExt.getOilBillType());
                    if (olCopilot.getIsNeedBill() != null && olCopilot.getIsNeedBill() <= 0) {
                        olCopilot.setVerificationState(0);//?????????????????????
                    } else {
                        olCopilot.setVerificationState(-1);
                    }
                    this.saveOrUpdate(olCopilot);
                }
            }
        }
        if (ol.getUserId() != null) {

            SysUser operator = sysUserService.getByUserInfoId(ol.getUserId());
            if (operator != null) {
                ol.setUserPhone(operator.getBillId());
            }
        }
        if (ol.getIsNeedBill() != null && ol.getIsNeedBill() <= 0) {
            ol.setVerificationState(0);//?????????????????????
        } else {
            ol.setVerificationState(-1);
        }
        this.saveOrUpdate(ol);
    }

    @Override
    public OrderLimit createCollectionOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt,
                                                 OrderFee orderFee, OrderScheduler scheduler,
                                                 OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt,
                                                 Long tenantId) {
        OrderLimit olCopilot = new OrderLimit();
        olCopilot.setOrderId(scheduler.getOrderId());
        olCopilot.setUserId(scheduler.getCarDriverId());
        //????????????????????????
        olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //????????????????????????
        olCopilot.setUserName(scheduler.getCarDriverMan());
        olCopilot.setUserPhone(scheduler.getCarDriverPhone());
        olCopilot.setVehicleAffiliation(orderFee.getVehicleAffiliation());
        olCopilot.setSourceOrderId(orderInfo.getFromOrderId());
        olCopilot.setTenantId(tenantId);
        olCopilot.setOrderCash(0L);
        olCopilot.setPaidCash(0L);
        olCopilot.setNoPayCash(0L);
        olCopilot.setWithdrawCash(0L);
        olCopilot.setNoWithdrawCash(0L);
        olCopilot.setOrderOil(0L);
        olCopilot.setOrderEntityOil(0L);
        olCopilot.setPaidOil(0L);
        olCopilot.setNoPayOil(0L);
        olCopilot.setOilIncome(0L);
        olCopilot.setNoWithdrawOil(0L);
        olCopilot.setWithdrawOil(0L);
        olCopilot.setOrderEtc(0L);
        olCopilot.setPaidEtc(0L);
        olCopilot.setNoPayEtc(0L);
        olCopilot.setEtcIncome(0L);
        olCopilot.setDebtMoney(0L);
        olCopilot.setPaidDebt(0L);
        olCopilot.setNoPayDebt(0L);
        olCopilot.setOrderFinal(0L);
        olCopilot.setPaidFinalPay(0L);
        olCopilot.setNoPayFinal(0L);
        olCopilot.setFinalIncome(0L);
        olCopilot.setMatchAmount(0L);
        olCopilot.setMatchBackIncome(0L);
        olCopilot.setMatchIncome(0L);
        olCopilot.setLoanAmount(0L);
        olCopilot.setVerificationLoan(0L);
        olCopilot.setNoVerificationLoan(0L);
        olCopilot.setPledgeOilcardFee(0L);
        olCopilot.setOrderDate(scheduler.getDependTime());
        olCopilot.setCreateDate(LocalDateTime.now());
        olCopilot.setTotalFee(0L);
        olCopilot.setAccountBalance(0L);
        olCopilot.setIsNeedBill(orderInfo.getIsNeedBill());
        olCopilot.setOilAffiliation(orderInfoExt.getOilAffiliation());
        olCopilot.setOilConsumer(orderFeeExt.getOilConsumer());
        olCopilot.setOilAccountType(orderFeeExt.getOilAccountType());
        olCopilot.setOilBillType(orderFeeExt.getOilBillType());
        olCopilot.setMarginTurn(0L);
        olCopilot.setMarginAdvance(0L);
        olCopilot.setOilTurn(0L);
        olCopilot.setEtcTurn(0L);
        olCopilot.setMarginSettlement(0L);
        olCopilot.setMarginDeduction(0L);
        olCopilot.setReleaseOilcardFee(0L);
        olCopilot.setArriveFee(0L);
        olCopilot.setServiceFee(0L);
        if (olCopilot.getIsNeedBill() != null && olCopilot.getIsNeedBill() <= 0) {
            olCopilot.setVerificationState(0);//?????????????????????
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }


    @Override
    public OrderLimit createOrderLimitCopilot(OrderInfo orderInfo, OrderInfoExt orderInfoExt,
                                              OrderFee orderFee, OrderScheduler scheduler,
                                              OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt,
                                              Long tenantId) {
        OrderLimit olCopilot = new OrderLimit();
        olCopilot.setOrderId(scheduler.getOrderId());
        olCopilot.setUserId(scheduler.getCopilotUserId());
        //????????????????????????
        olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //????????????????????????
        olCopilot.setUserName(scheduler.getCopilotMan());
        olCopilot.setUserPhone(scheduler.getCopilotPhone());
        olCopilot.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        olCopilot.setSourceOrderId(orderInfo.getFromOrderId());
        olCopilot.setTenantId(tenantId);
        olCopilot.setOrderCash(0L);
        olCopilot.setPaidCash(0L);
        olCopilot.setNoPayCash(0L);
        olCopilot.setWithdrawCash(0L);
        olCopilot.setNoWithdrawCash(0L);
        olCopilot.setOrderOil(0L);
        olCopilot.setOrderEntityOil(0L);
        olCopilot.setPaidOil(0L);
        olCopilot.setNoPayOil(0L);
        olCopilot.setOilIncome(0L);
        olCopilot.setNoWithdrawOil(0L);
        olCopilot.setWithdrawOil(0L);
        olCopilot.setOrderEtc(0L);
        olCopilot.setPaidEtc(0L);
        olCopilot.setNoPayEtc(0L);
        olCopilot.setEtcIncome(0L);
        olCopilot.setDebtMoney(0L);
        olCopilot.setPaidDebt(0L);
        olCopilot.setNoPayDebt(0L);
        olCopilot.setOrderFinal(0L);
        olCopilot.setPaidFinalPay(0L);
        olCopilot.setNoPayFinal(0L);
        olCopilot.setFinalIncome(0L);
        olCopilot.setMatchAmount(0L);
        olCopilot.setMatchBackIncome(0L);
        olCopilot.setMatchIncome(0L);
        olCopilot.setLoanAmount(0L);
        olCopilot.setVerificationLoan(0L);
        olCopilot.setNoVerificationLoan(0L);
        olCopilot.setPledgeOilcardFee(0L);
        olCopilot.setOrderDate(scheduler.getDependTime());
        olCopilot.setCreateDate(LocalDateTime.now());
        olCopilot.setTotalFee(0L);
        olCopilot.setAccountBalance(0L);
        olCopilot.setIsNeedBill(orderInfo.getIsNeedBill());
        olCopilot.setOilAffiliation(orderInfoExt.getOilAffiliation());
        olCopilot.setOilConsumer(orderFeeExt.getOilConsumer());
        olCopilot.setOilAccountType(orderFeeExt.getOilAccountType());
        olCopilot.setOilBillType(orderFeeExt.getOilBillType());
        olCopilot.setUserPhone(scheduler.getCopilotPhone());
        olCopilot.setMarginTurn(0L);
        olCopilot.setMarginAdvance(0L);
        olCopilot.setOilTurn(0L);
        olCopilot.setEtcTurn(0L);
        olCopilot.setMarginSettlement(0L);
        olCopilot.setMarginDeduction(0L);
        olCopilot.setReleaseOilcardFee(0L);
        olCopilot.setArriveFee(0L);
        olCopilot.setServiceFee(0L);
        if (olCopilot.getIsNeedBill() != null && olCopilot.getIsNeedBill() <= 0) {
            olCopilot.setVerificationState(0);//?????????????????????
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }

    @Override
    public OrderLimit createOrderLimit(AcOrderSubsidyInDto acOrderSubsidyIn) {
        OrderLimit olTemp = this.getOrderLimitByUserIdAndOrderId(acOrderSubsidyIn.getDriverUserId(), acOrderSubsidyIn.getOrderId(), -1);
        if (null != olTemp) {
            return olTemp;
        }
        OrderInfo orderInfo = iOrderInfoService.getOrder(acOrderSubsidyIn.getOrderId());
        if (null == orderInfo) {
            throw new BusinessException("???????????????");
        }
        OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(acOrderSubsidyIn.getOrderId());
        OrderFee orderFee = iOrderFeeService.getOrderFee(acOrderSubsidyIn.getOrderId());
        OrderScheduler scheduler = iOrderSchedulerService.getOrderScheduler(acOrderSubsidyIn.getOrderId());
        OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(acOrderSubsidyIn.getOrderId());
        OrderScheduler schedulerNew = new OrderScheduler();
        schedulerNew.setCopilotUserId(acOrderSubsidyIn.getDriverUserId());
        schedulerNew.setCopilotMan(acOrderSubsidyIn.getDriverUserName());
        schedulerNew.setOrderId(orderInfo.getOrderId());
        schedulerNew.setDependTime(scheduler.getDependTime());
        OrderLimit ol = iOrderLimitService.createOrderLimitCopilot(orderInfo, orderInfoExt, orderFee, schedulerNew, null, orderFeeExt, orderInfo.getTenantId());
        iOrderLimitService.saveOrUpdate(ol);
        return ol;
    }

    @Override
    public OrderLimit getOrderLimit(long userId, long orderId, long tenantId, Integer userType, LoginInfo loginInfo) {

        LambdaQueryWrapper<OrderLimit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLimit::getUserId, userId);
        queryWrapper.eq(OrderLimit::getOrderId, orderId);
        queryWrapper.eq(userType > 0, OrderLimit::getUserType, userType);

        if (tenantId > -1) {
            queryWrapper.eq(OrderLimit::getTenantId, tenantId);
        } else {
            queryWrapper.eq(OrderLimit::getTenantId, loginInfo.getTenantId());
        }

        return this.getOne(queryWrapper);
    }

    @Override
    public void payForExceptionOut(Long userId, String vehicleAffiliation, Long amountFee, Long objId, Long tenantId, Long orderId, LoginInfo loginInfo, String token) {

        log.info("???????????????:userId=" + userId + "amountFee=" + amountFee + "objId=" + objId);
        if (userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("?????????????????????");
        }
        if (orderId <= 0) {
            throw new BusinessException("??????????????????");
        }
        if (tenantId == null || tenantId <= -1) {
            throw new BusinessException("???????????????id");
        }
        // todo
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "payForExceptionOut" + orderId + objId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("????????????????????????????????????!");
//        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
        // ????????????id?????????????????????id
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        SysUser tenantUser = sysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);

//        UserDataInfo tenantUser = userSV.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ??????????????????
        SysUser user = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
//        UserDataInfo user = userSV.getUserDataInfo(userId);
        if (user == null) {
            throw new BusinessException("????????????????????????");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        //????????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        // ????????????ID???????????????????????????????????????
        OrderLimit ol = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, orderId, -1);
        if (ol == null) {
            throw new BusinessException("??????????????????" + orderId + " ??????ID???" + userId + " ???????????????????????????");
        }
        String oilAffiliation = ol.getOilAffiliation();
        if (StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("??????????????????????????????");
        }
        //????????????????????????
        OrderLimit olTemp = this.getOrderLimitByUserIdAndOrderId(userId, orderId, -1);
        if (olTemp == null) {
            throw new BusinessException("?????????????????????!");
        }
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, ol.getUserType());
        //?????????
        Long marginBalance = olTemp.getNoPayFinal();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        Long soNbr = CommonUtil.createSoNbr();
        Long overdueAmount = 0L;
        if (amountFee < 0) {
            //?????????
            BusiSubjectsRel amountFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_FEE_OUT, amountFee);
            busiList.add(amountFeeSubjectsRel);
            //?????????????????????
            if (marginBalance > 0) {
                if (marginBalance >= Math.abs(amountFee)) {
                    BusiSubjectsRel marginBalanceRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.MARGINBALANCE_EXCEPTION_OUT_SUB, Math.abs(amountFee));
                    busiList.add(marginBalanceRel);
                } else {
                    BusiSubjectsRel marginBalanceRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.MARGINBALANCE_EXCEPTION_OUT_SUB, marginBalance);
                    busiList.add(marginBalanceRel);
                    //??????????????????
                    overdueAmount = Math.abs(amountFee) - marginBalance;
                    BusiSubjectsRel payableOverdue = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB, overdueAmount);
                    busiList.add(payableOverdue);
                }
            } else {
                //??????????????????
                overdueAmount = Math.abs(amountFee);
                BusiSubjectsRel payableOverdue = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB, overdueAmount);
                busiList.add(payableOverdue);
            }

            // ??????????????????
            List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE_OUT, busiList);
            // ????????????????????????????????????????????????
            accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE_OUT,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), account, busiSubjectsRelList, soNbr, orderId, sysOperator.getName(),
                    null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            //??????????????????
            if (overdueAmount > 0L) {
                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, ol.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel receivableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.EXCEPTION_OUT_RECEIVABLE_OVERDUE_SUB, overdueAmount);
                fleetBusiList.add(receivableOverdueRel);

                // ??????????????????
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.EXCEPTION_FEE_OUT, fleetBusiList);
                accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.EXCEPTION_FEE_OUT,
                        sysOperator.getUserInfoId(), sysOperator.getName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId,
                        tenantSysOperator.getName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
                busiSubjectsRelList.addAll(fleetSubjectsRelList);

                //??????????????????
                boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
                Integer isAutomatic = null;
                if (isAutoTransfer) {
                    isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
                } else {
                    isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
                }
                PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantUserId, OrderAccountConst.PAY_TYPE.TENANT,
                        OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, overdueAmount, tenantId, vehicleAffiliation, orderId,
                        -1L, isAutomatic, isAutomatic, userId, OrderAccountConst.PAY_TYPE.USER,
                        EnumConsts.PayInter.EXCEPTION_FEE_OUT, EnumConsts.SubjectIds.EXCEPTION_OUT_PAYABLE_OVERDUE_SUB,
                        oilAffiliation, ol.getUserType(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, 0L, token);
                payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
                payoutIntf.setRemark("????????????");
                if (isTenant) {
                    payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                    payoutIntf.setPayTenantId(sysTenantDef.getId());
                }
                //??????????????????????????????????????????????????????
				/*if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
						!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
					payoutIntf.setIsDriver(PAY_TYPE.HAVIR);
				}*/
                OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
                payoutIntf.setBusiCode(String.valueOf(orderId));
                if (orderScheduler != null) {
                    payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                } else {
                    OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                    if (orderSchedulerH != null) {
                        payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                    }
                }
                payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf, token);
            }
            // ?????????????????????????????????????????????
            Map<String, String> inParam = orderAccountService.setParameters(sysOperator.getUserInfoId(),
                    sysOperator.getBillId(), EnumConsts.PayInter.EXCEPTION_FEE_OUT,
                    orderId, Math.abs(amountFee), vehicleAffiliation, "");
            inParam.put("tenantUserId", String.valueOf(tenantUserId));
            inParam.put("tenantBillId", tenantSysOperator.getBillId());
            inParam.put("tenantUserName", tenantUser.getLinkMan());
            busiToOrderUtils.busiToOrder(inParam, busiSubjectsRelList);
        }
        // ????????????
        String remark = "???????????????" + "????????????" + orderId + " ?????????????????????" + amountFee;
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.PayForExceptionOut, soNbr, com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, loginInfo.getName() + remark, loginInfo.getTenantId());
    }

    @Override
    public List<OrderLimit> queryOilAndEtcBalance(OilExcDto oilExcDto) {
        LambdaQueryWrapper<OrderLimit> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderLimit::getUserId, oilExcDto.getUserId());
        if (StringUtils.isNotEmpty(oilExcDto.getVehicleAffiliation())) {
            queryWrapper.eq(OrderLimit::getVehicleAffiliation, oilExcDto.getVehicleAffiliation());
        }
        if (StringUtils.isNotBlank(oilExcDto.getOilAffiliation())) {
            queryWrapper.eq(OrderLimit::getOilAffiliation, oilExcDto.getOilAffiliation());
        }
        if (oilExcDto.getUserType() > 0) {
            queryWrapper.eq(OrderLimit::getUserType, oilExcDto.getUserType());
        }
        queryWrapper.ge(OrderLimit::getOrderDate, oilExcDto.getCreateTime());
        queryWrapper.le(OrderLimit::getOrderDate, oilExcDto.getUpdateTime());
        queryWrapper.eq(OrderLimit::getTenantId, oilExcDto.getTenantId());
        queryWrapper.orderByAsc(OrderLimit::getOrderDate);
        List<OrderLimit> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public List<OrderLimit> getOrderLimitZhangPing(Long userId, Long tenantId, Integer userType) {
//        Session session = SysContexts.getEntityManager(true);
//        Criteria ca = session.createCriteria(OrderLimit.class);
//        ca.add(Restrictions.eq("userId", userId));
//        ca.add(Restrictions.eq("tenantId", tenantId));
//        Disjunction dis = Restrictions.disjunction();
//        dis.add(Restrictions.gt("noPayCash", 0L));
//        dis.add(Restrictions.gt("noPayOil", 0L));
//        dis.add(Restrictions.gt("noPayEtc", 0L));
//        dis.add(Restrictions.gt("noPayFinal", 0L));
//        dis.add(Restrictions.gt("pledgeOilcardFee", 0L));
//        if(userType > 0){
//            ca.add(Restrictions.eq("userType",userType));
//        }
//        ca.add(dis);

        QueryWrapper<OrderLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.and(wq -> wq.gt("no_pay_cash", 0L).or().gt("no_pay_oil", 0L).or().gt("no_pay_etc", 0L).or().gt("no_pay_final", 0L).or().gt("pledge_oilcard_fee", 0L));
        if (userType > 0) {
            queryWrapper.eq("user_type", userType);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderLimit> getOrderLimitsByOrderId(long orderId, Integer userType) {
        QueryWrapper<OrderLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        if (userType > 0) {
            queryWrapper.eq("user_type", userType);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<OrderLimit> getOrderLimit(long userId, String noPayType, Long tenantId, Integer userType) {
        QueryWrapper<OrderLimit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        if (OrderAccountConst.NO_PAY.NO_PAY_CASH.equals(noPayType)) {
            queryWrapper.gt("no_pay_cash", 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_OIL.equals(noPayType)) {
            queryWrapper.gt("no_pay_oil", 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_ETC.equals(noPayType)) {
            queryWrapper.gt("no_pay_etc", 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_FINAL.equals(noPayType)) {
            queryWrapper.gt("no_pay_final", 0L);
        } else if (OrderAccountConst.NO_PAY.NO_PAY_DEBT.equals(noPayType)) {
            queryWrapper.gt("no_pay_debt", 0L);
        }
        if (userType > 0) {
            queryWrapper.eq("user_type", userType);
        }

        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.orderByAsc("order_date");

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<OrderLimit> queryOrderLimit(Integer[] fianlStss, Date finalPlanDate, Integer userType) {
        LambdaQueryWrapper<OrderLimit> orderLimitLambdaQueryWrapper = new LambdaQueryWrapper<>();
        orderLimitLambdaQueryWrapper.le(OrderLimit::getFinalPlanDate, finalPlanDate);
        orderLimitLambdaQueryWrapper.in(OrderLimit::getFianlSts, fianlStss);
        if (userType > 0) {
            orderLimitLambdaQueryWrapper.eq(OrderLimit::getUserType, userType);
        }
        List<OrderLimit> orderLimitList = super.list(orderLimitLambdaQueryWrapper);
        return orderLimitList;
    }


    public OrderLimit resetOrderLimit(OrderLimit olCopilot) {
        if (olCopilot == null) {
            throw new BusinessException("?????????????????????????????????");
        }
        olCopilot.setId(null);
        olCopilot.setOrderCash(0L);
        olCopilot.setPaidCash(0L);
        olCopilot.setNoPayCash(0L);
        olCopilot.setWithdrawCash(0L);
        olCopilot.setNoWithdrawCash(0L);
        olCopilot.setOrderOil(0L);
        olCopilot.setOrderEntityOil(0L);
        olCopilot.setPaidOil(0L);
        olCopilot.setNoPayOil(0L);
        olCopilot.setOilIncome(0L);
        olCopilot.setNoWithdrawOil(0L);
        olCopilot.setWithdrawOil(0L);
        olCopilot.setOrderEtc(0L);
        olCopilot.setPaidEtc(0L);
        olCopilot.setNoPayEtc(0L);
        olCopilot.setEtcIncome(0L);
        olCopilot.setDebtMoney(0L);
        olCopilot.setPaidDebt(0L);
        olCopilot.setNoPayDebt(0L);
        olCopilot.setOrderFinal(0L);
        olCopilot.setPaidFinalPay(0L);
        olCopilot.setNoPayFinal(0L);
        olCopilot.setFinalIncome(0L);
        olCopilot.setMatchAmount(0L);
        olCopilot.setMatchBackIncome(0L);
        olCopilot.setMatchIncome(0L);
        olCopilot.setLoanAmount(0L);
        olCopilot.setVerificationLoan(0L);
        olCopilot.setNoVerificationLoan(0L);
        olCopilot.setPledgeOilcardFee(0L);
        olCopilot.setCreateDate(LocalDateTime.now());
        olCopilot.setTotalFee(0L);
        olCopilot.setAccountBalance(0L);
        olCopilot.setMarginTurn(0L);
        olCopilot.setMarginAdvance(0L);
        olCopilot.setOilTurn(0L);
        olCopilot.setEtcTurn(0L);
        olCopilot.setMarginSettlement(0L);
        olCopilot.setMarginDeduction(0L);
        olCopilot.setReleaseOilcardFee(0L);
        olCopilot.setArriveFee(0L);
        olCopilot.setServiceFee(0L);
        if (olCopilot.getIsNeedBill() != null && olCopilot.getIsNeedBill() <= 0) {
            olCopilot.setVerificationState(0);//?????????????????????
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }


    @Override
    public Page<OrderLimit> queryOrderLimits(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize) {
        Page<OrderLimit> orderLimitPage = orderLimitMapper.queryOrderLimits(new Page<>(pageNum, pageSize), advanceExpireOutVo);
        return orderLimitPage;
    }

    @Override
    public void doOrderLimtByFlowId(PayoutIntf payoutIntf) {
        if (payoutIntf.getUserType() != SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
            List<Long> batchIds = new ArrayList<Long>();
            batchIds.add(payoutIntf.getId());
            List<PayoutOrder> orderList = payoutOrderService.getBaseMapper().selectBatchIds(batchIds);

            for (PayoutOrder payoutOrder : orderList) {
                OrderLimit ol = getOrderLimitByUserIdAndOrderId(payoutIntf.getUserId(), payoutOrder.getOrderId(), -1);
                if (ol == null) {
                    throw new BusinessException("??????????????????");
                }
                OrderFundFlow off = new OrderFundFlow();
                off.setAmount(payoutOrder.getAmount());//???????????????????????????
                off.setOrderId(payoutOrder.getOrderId());//??????ID
                off.setUserId(payoutOrder.getUserId());

                off.setBusinessId(payoutIntf.getBusiId());//????????????
                off.setBusinessName(sysStaticDataService.getSysStaticDatas("ACCOUNT_DETAILS_BUSINESS_NUMBER", (int) ((long) off.getBusinessId())));//????????????
                off.setBookTypeName(sysStaticDataService.getSysStaticDatas("ACCOUNT_BOOK_TYPE", (int) ((long) off.getBookType())));//????????????
                off.setSubjectsId(payoutIntf.getSubjectsId());//??????ID
                //			off.setSubjectsName(rel.getSubjectsName());//????????????
                off.setBusiTable("ACCOUNT_DETAILS");//???????????????
                off.setBusiKey(payoutOrder.getBatchId());//????????????ID
                off.setTenantId(payoutIntf.getTenantId());
                off.setInoutSts("out");//????????????:???in???out???io
                //???????????????
                orderFundFlowService.saveOrUpdate(off);

                ol.setNoPayCash(ol.getNoPayCash() - payoutOrder.getAmount());
                ol.setPaidCash(ol.getPaidCash() + payoutOrder.getAmount());
                ol.setNoWithdrawCash(ol.getNoWithdrawCash() - payoutOrder.getAmount());
                ol.setWithdrawCash(ol.getWithdrawCash() + payoutOrder.getAmount());
                this.save(ol);
            }
        }
    }

    @Override
    public List<RechargeOilSource> matchOrderAccountOilToRechargeOil(long amount, Long tenantUserId, String rechargeOrderId, long tenantId, long driverUserId, Long businessId, Long subjectsId, int userType, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Date date = new Date();
        // ????????????ID???????????????????????????????????????
        List<RechargeOilSource> sourceList = new ArrayList<RechargeOilSource>();
        List<OrderAccount> tenantAccount = iOrderAccountService.getOrderAccount(tenantUserId, OrderAccountConst.ORDER_BY.OIL_BALANCE, -1L, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        long oilAmount = amount;
        SysUser sysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        for (OrderAccount ac : tenantAccount) {
            long oilBalance = ac.getOilBalance();
            if (oilBalance <= 0 || oilAmount == 0) {
                continue;
            }
            //??????????????????
            List<OrderOilSource> orderOilSourceList = iOrderOilSourceService.getOrderOilSource(ac.getUserId(), Long.valueOf(ac.getVehicleAffiliation()), String.valueOf(ac.getSourceTenantId()), Long.valueOf(ac.getOilAffiliation()), ac.getUserType());
            long shouleMatchAmount = 0L;
            List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
            // ????????????
            if (oilBalance >= oilAmount) {
                shouleMatchAmount = oilAmount;
                oilAmount = 0;
            } else {
                oilAmount -= ac.getOilBalance();
                shouleMatchAmount = ac.getOilBalance();
            }
            BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(subjectsId, shouleMatchAmount);
            busiList.add(amountFeeSubjectsRel);
            // ??????????????????
            // ????????????id?????????????????????id
            Long otherTenantUserId = iSysTenantDefService.getTenantAdminUser(ac.getSourceTenantId());
            if (otherTenantUserId == null || otherTenantUserId <= 0L) {
                throw new BusinessException("???????????????????????????id!");
            }
            SysUser tenantSysOperator = sysOperatorService.getSysOperatorByUserIdOrPhone(otherTenantUserId, null, 0L);
            if (tenantSysOperator == null) {
                throw new BusinessException("????????????????????????!");
            }
            List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(businessId, busiList);
            long soNbr = CommonUtil.createSoNbr();
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, businessId,
                    tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), ac, busiSubjectsRelList, soNbr, 0L, "", null, tenantId, null, "", null, ac.getVehicleAffiliation(), user);

            MatchAmountUtil.matchAmounts(shouleMatchAmount, 0, 0, "noPayOil", "noRebateOil", "noCreditOil", orderOilSourceList);
            long matchToatalAmount = 0L;

            for (OrderOilSource ol : orderOilSourceList) {
                if (ol.getMatchAmount() == null || ol.getMatchAmount() <= 0L) {
                    continue;
                }
                long matchNoPayOil = ol.getMatchNoPayOil() == null ? 0 : ol.getMatchNoPayOil();
                long matchNoRebateOil = ol.getMatchNoRebateOil() == null ? 0 : ol.getMatchNoRebateOil();
                long matchNoCreditOil = ol.getMatchNoCreditOil() == null ? 0 : ol.getMatchNoCreditOil();
                RechargeOilSource source = iOperationOilService.saveRechargeOilSource(driverUserId, ol.getId(), rechargeOrderId, String.valueOf(ol.getSourceOrderId()), matchNoPayOil, matchNoPayOil, 0, ol.getSourceTenantId(), date, user.getId(), ol.getIsNeedBill(), ol.getVehicleAffiliation(), getLocalDateTimeToDate(ol.getOrderDate()), ol.getOilAffiliation(), OrderAccountConst.RECHARGE_ORDER_ACCOUNT_OIL.CUSTOMER_OIL,
                        ol.getOilConsumer(), matchNoRebateOil, matchNoRebateOil, 0, matchNoCreditOil, matchNoCreditOil, 0, userType, ol.getOilAccountType(), ol.getOilBillType(), accessToken);
                source.setMatchAmount(ol.getMatchAmount());
                sourceList.add(source);
                matchToatalAmount += ol.getMatchAmount();
                amountFeeSubjectsRel.setAmountFee(ol.getMatchAmount());
                busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(businessId, busiList);
                ParametersNewDto parametersNewDto = busiToOrderUtils.setParametersNew(ol.getUserId(), sysOperator.getBillId(), businessId, ol.getOrderId(), ol.getMatchAmount(), ol.getVehicleAffiliation(), "");
                parametersNewDto.setOilSource(ol);
                busiToOrderUtils.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);

            }
            if (matchToatalAmount != shouleMatchAmount) {
                throw new BusinessException("???????????????" + ac.getVehicleAffiliation() + "????????????????????????????????????????????????????????????");
            }
        }
        return sourceList;
    }

    @Override
    public List<OrderAccount> matchHaOrderAccount(Long userId) {
        //???????????????
        boolean isService = false;
        ServiceInfo serviceInfo = iServiceInfoService1.getServiceInfoById(userId);
        // ?????????
        if (serviceInfo != null) {
            isService = true;
        }
        List<OrderAccount> list = new ArrayList<OrderAccount>();
        if (isService) {
            List<ServiceMatchOrder> serviceMatchOrderList = iServiceMatchOrderService.getHaServiceMatchOrder(userId);
            Map tmpMap = new HashMap();
            for (ServiceMatchOrder smo : serviceMatchOrderList) {
                if (null == tmpMap.get(smo.getOilAffiliation() + smo.getTenantId())) {
                    tmpMap.put(smo.getOilAffiliation() + smo.getTenantId(), smo);
                    OrderAccount orderAccount = orderAccountService.queryOrderAccount(userId, smo.getVehicleAffiliation(), smo.getTenantId(), smo.getOilAffiliation(), SysStaticDataEnum.USER_TYPE.SERVICE_USER);
                    if (orderAccount != null) {
                        list.add(orderAccount);
                    }
                }
            }
        } else {
            List<OrderLimit> orderLimits = this.getHaOrderLimit(userId, -1L, -1);
            Map tmpMap = new HashMap();
            for (OrderLimit ol : orderLimits) {
                if (null == tmpMap.get(ol.getVehicleAffiliation() + ol.getTenantId())) {
                    tmpMap.put(ol.getVehicleAffiliation() + ol.getTenantId(), ol);
                    OrderAccount orderAccount = orderAccountService.queryOrderAccount(userId, ol.getVehicleAffiliation(),
                            ol.getTenantId(), ol.getOilAffiliation(), ol.getUserType());
                    if (orderAccount != null) {
                        list.add(orderAccount);
                    }
                }
            }
        }
        return list;
    }

    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    @Override
    public List<OrderLimit> getHaOrderLimit(long userId, Long tenantId, Integer userType) {
        QueryWrapper<OrderLimit> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .ne("vehicle_affiliation", OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0)
                .ne("vehicle_affiliation", OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)
                .gt("account_balance", 0L);
        if (userType > 0) {
            wrapper.eq("user_type", userType);
        }

        wrapper.orderByAsc("order_date");
        List<OrderLimit> orderLimits = baseMapper.selectList(wrapper);
        return orderLimits;
    }

    @Override
    public List<OrderLimit> matchOrderInfoWithdraw(Map<String, Object> inParam) {
        long userId = DataFormat.getLongKey(inParam, "userId");
        String vehicleAffiliation = DataFormat.getStringKey(inParam, "vehicleAffiliation");
        long amount = DataFormat.getLongKey(inParam, "amountFee");
        long tenantId = DataFormat.getLongKey(inParam, "tenantId");
        int userType = DataFormat.getIntKey(inParam, "userType");
        if (userId <= 0) {
            throw new BusinessException("??????ID????????????!");
        }
        if (StringUtils.isEmpty(vehicleAffiliation)) {
            throw new BusinessException("????????????????????????!");
        }
        if (amount == 0) {
            throw new BusinessException("????????????????????????!");
        }
        //??????????????????????????????????????????
        List<OrderLimit> orderLimits = this.getOrderLimit(userId, vehicleAffiliation, "accountBalance", tenantId, userType);
        try {
            MatchAmountUtil.matchAmount(amount, 0, 0, "accountBalance", orderLimits);
        } catch (Exception e) {
            throw new BusinessException("??????!" + e);
        }

        return orderLimits;
    }

    @Override
    public OrderLimit queryOrderLimitByOrderId(Long orderId, Long userId) {
        if (orderId == null) {
            throw new BusinessException("????????????????????????");
        }
        LambdaQueryWrapper<OrderLimit> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderLimit::getOrderId, orderId)
                .eq(OrderLimit::getUserId, userId);
        OrderLimit orderLimit = baseMapper.selectOne(wrapper);
        return orderLimit;
    }

    @Override
    public void marginTurnCash(long userId, String vehicleAffiliation, long amountFee, long orderId, long tenantId, String sign, String oilAffiliation, Long payFlowId, Object obj, String accessToken) {
        OrderLimit orderLimits = null;
        long otherFlowId = 0L;
        if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign) || OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(sign)) {
            otherFlowId = orderId;
            orderId = 0L;
        }
        if (orderId == 1656498499303L) {
            System.out.println("ssssss");
        }
        log.info("????????????????????????:userId=" + userId + "amountFee=" + amountFee + "orderId=" + orderId + "vehicleAffiliation" + vehicleAffiliation);
        if (userId < 1) {
            throw new BusinessException("?????????????????????");
        }
        if (tenantId < 0) {
            throw new BusinessException("?????????????????????");
        }
        Integer userType = null;
        Integer payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        //?????????????????????????????????????????????(????????????)
        if (obj instanceof ConsumeOilFlow) {
            ConsumeOilFlow flow = (ConsumeOilFlow) obj;
            userType = flow.getUserType();
            ConsumeOilFlowExt ext = consumeOilFlowExtService.getConsumeOilFlowExtByFlowId(flow.getFlowId());
        }
        if (obj instanceof UserRepairMargin) {
            UserRepairMargin flow = (UserRepairMargin) obj;
            userType = SysStaticDataEnum.USER_TYPE.SERVICE_USER;
        }
        if (obj instanceof OrderLimit) {
            OrderLimit flow = (OrderLimit) obj;
            userType = flow.getUserType();
            orderLimits = flow;
        }
        //????????????id?????????????????????id
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(tenantId);
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("???????????????????????????id!");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("yyyyMM", new String[]{DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2)});
        // ??????????????????????????????
        // ????????????id??????????????????
        UserDataInfo user = userDataInfoService.getUserDataInfo(userId);
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(userId, null);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        LoginInfo loginInfo = new LoginInfo();
        BeanUtil.copyProperties(sysOperator, loginInfo);
        //?????????????????????
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        List<BusiSubjectsRel> busiSubjectsRelList = null;
        // ????????????ID???????????????????????????????????????
        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId, oilAffiliation, userType);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        long soNbr = CommonUtil.createSoNbr();
        long paysubjectId = EnumConsts.SubjectIds.FINAL_PAYFOR_PAYABLE_IN;
        long receSubjectId = EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN;
        long turnReceSubjectId = EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_REDUCE;//?????????????????????
        long turnPaySubjectId = EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD;//?????????????????????
        // ???????????????????????????
        if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {//????????????
            turnReceSubjectId = EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_REDUCE;
            turnPaySubjectId = EnumConsts.SubjectIds.NOTDUE_TRUN_AVAILABLE_ADD;
            receSubjectId = EnumConsts.SubjectIds.FINAL_PAYFOR_RECEIVABLE_IN;
            paysubjectId = EnumConsts.SubjectIds.FINAL_PAYFOR_PAYABLE_IN;

        } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
            turnReceSubjectId = EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_REDUCE;
            turnPaySubjectId = EnumConsts.SubjectIds.OIL_TRUN_AVAILABLE_ADD;
            receSubjectId = EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN;//?????????
            paysubjectId = EnumConsts.SubjectIds.Oil_PAYFOR_PAYABLE_IN;
        } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(sign)) {
            turnReceSubjectId = EnumConsts.SubjectIds.REPAIR_TRUN_AVAILABLE_REDUCE;
            turnPaySubjectId = EnumConsts.SubjectIds.REPAIR_TRUN_AVAILABLE_ADD;
            receSubjectId = EnumConsts.SubjectIds.REPAIR_PAYFOR_RECEIVABLE_IN;//?????????????????????
            paysubjectId = EnumConsts.SubjectIds.REPAIR_PAYFOR_PAYABLE_IN;
        }
        long serviceFee = 0;
        if (amountFee > 0) {
            if (account.getMarginBalance() >= amountFee) {
                // ?????????????????????
                BusiSubjectsRel reduce = new BusiSubjectsRel();
                reduce.setSubjectsId(turnReceSubjectId);
                reduce.setAmountFee(amountFee);
                // ???????????????????????????
                BusiSubjectsRel add = new BusiSubjectsRel();
                add.setSubjectsId(receSubjectId);
                add.setAmountFee(amountFee);
                //???????????? ????????? 20190717 ?????????????????????????????????
                if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {//????????????
                    boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                    if (isLuge) {
                        Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), amountFee, 0L, 0L, amountFee, tenantId, null);
                        serviceFee = (Long) result.get("lugeBillServiceFee");
                    }
                }
                // ???????????????????????????
                BusiSubjectsRel addPay = new BusiSubjectsRel();
                addPay.setSubjectsId(paysubjectId);
                addPay.setAmountFee(amountFee);

                busiList.add(reduce);
                busiList.add(add);

                busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, busiList);
                // ????????????????????????????????????????????????
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE, sysOperator.getUserInfoId(),
                        sysOperator.getOpName(), account, busiSubjectsRelList, soNbr, orderId, sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
                //??????????????????
                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();

                BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(paysubjectId, amountFee);
                fleetBusiList.add(payableOverdueRel);
                //???????????? ????????? 20190717
                if (serviceFee > 0) {
                    BusiSubjectsRel payableServiceFeeSubjectsRel = busiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4007, serviceFee);
                    fleetBusiList.add(payableServiceFeeSubjectsRel);
                }
                // ??????????????????
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, fleetBusiList);
                // ????????????????????????????????????????????????
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE, sysOperator.getUserInfoId(),
                        sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId, sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);


            } else if (account.getMarginBalance() > 0) {
                BusiSubjectsRel reduce = new BusiSubjectsRel();
                reduce.setSubjectsId(turnReceSubjectId);
                reduce.setAmountFee(account.getMarginBalance());
                // ???????????????????????????
                BusiSubjectsRel add = new BusiSubjectsRel();
                add.setSubjectsId(receSubjectId);
                add.setAmountFee(account.getMarginBalance());
                // ???????????????????????????
                BusiSubjectsRel addPay = new BusiSubjectsRel();
                addPay.setSubjectsId(paysubjectId);
                addPay.setAmountFee(account.getMarginBalance());
                busiList.add(reduce);
                busiList.add(add);

                busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, busiList);
                // ????????????????????????????????????????????????
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE, sysOperator.getUserInfoId(),
                        sysOperator.getOpName(), account, busiSubjectsRelList, soNbr, orderId, sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);


                //??????????????????
                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
                BusiSubjectsRel payableOverdueRel = busiSubjectsRelService.createBusiSubjectsRel(paysubjectId, account.getMarginBalance());
                fleetBusiList.add(payableOverdueRel);

                // ??????????????????
                List<BusiSubjectsRel> fleetSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, fleetBusiList);
                // ????????????????????????????????????????????????
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.PAYFOR_CODE, sysOperator.getUserInfoId(),
                        sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, orderId, sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

            }
        } else {
            return;
        }
        //???????????????????????????????????????
        if (!OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
            //??????????????????
            //		boolean isAutoTransfer = payFeeLimitTF.isMemberAutoTransfer(account.getSourceTenantId());
            long isAutoTransfer = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;//???????????????

            Long adminUser = sysTenantDefService.getTenantAdminUser(tenantId);
            OrderAccount payAccount = orderAccountService.getOrderAccount(adminUser, vehicleAffiliation, tenantId, oilAffiliation, payUserType);
            PayoutIntf payOutIntfVirToVir = new PayoutIntf();
            payOutIntfVirToVir.setVehicleAffiliation(vehicleAffiliation);
            payOutIntfVirToVir.setOrderId(orderId);
            payOutIntfVirToVir.setUserId(userId);
            //????????????????????????
            payOutIntfVirToVir.setUserType(userType);
            payOutIntfVirToVir.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            //????????????????????????
            payOutIntfVirToVir.setTxnAmt(amountFee);
            payOutIntfVirToVir.setBusiId(EnumConsts.PayInter.PAYFOR_CODE);//???????????????
            payOutIntfVirToVir.setObjId(StringUtils.isNotBlank(user.getMobilePhone()) ? Long.valueOf(user.getMobilePhone()) : 0L);
            payOutIntfVirToVir.setObjType(SysStaticDataEnum.OBJ_TYPE.TURN_CASH + "");
            payOutIntfVirToVir.setPayObjId(payAccount.getUserId());
            payOutIntfVirToVir.setCreateDate(LocalDateTime.now());
            payOutIntfVirToVir.setWaitBillingAmount(0L);//?????????0
            if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) && !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);//?????????????????????
                payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);

            } else {
                if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {
                    payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.USER);//??????????????????
                    payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {
                    payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);//?????????????????????
                    payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                    if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(oilAffiliation)) {//?????????????????????????????????
                        payOutIntfVirToVir.setWaitBillingAmount(amountFee);
                    }
                } else if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(sign)) {
                    payOutIntfVirToVir.setIsDriver(OrderAccountConst.PAY_TYPE.SERVICE);//?????????????????????
                    payOutIntfVirToVir.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
                    UserRepairMargin userRepairMargin = userRepairMarginService.getUserRepairMargin(orderId);
                    if (userRepairMargin != null && userRepairMargin.getIsNeedBill() != 0) {//?????????????????????????????????
                        payOutIntfVirToVir.setWaitBillingAmount(amountFee);
                    }
                }
            }

            payOutIntfVirToVir.setBillServiceFee(serviceFee);
            payOutIntfVirToVir.setSubjectsId(receSubjectId);
            payOutIntfVirToVir.setFeeType(OrderAccountConst.FEE_TYPE.CASH_TYPE);
            payOutIntfVirToVir.setPayTenantId(tenantId);
            if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(sign)) {//????????????????????????????????????????????????
                OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
                isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401);
                if (orderScheduler == null || orderScheduler.getOrderId() == null) {
                    orderScheduler = new OrderScheduler();
                    OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                    BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
                }
                if (orderScheduler != null && orderScheduler.getOrderId() != null) {
                    payOutIntfVirToVir.setPlateNumber(orderScheduler.getPlateNumber());
                    payOutIntfVirToVir.setBusiCode(orderId + "");
                }
            }
            if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(sign)) {//?????????????????????????????????????????????
                ConsumeOilFlow consumeOilFlow = (ConsumeOilFlow) obj;
                if (consumeOilFlow != null) {
                    LocalDateTime startDate1 = consumeOilFlow.getCreateTime();
                    Calendar calendar1 = GregorianCalendar.from(startDate1.atZone(ZoneId.systemDefault()));
                    calendar1.add(Calendar.HOUR_OF_DAY, 2);
                    Date endDate1 = calendar1.getTime();
                    LocalDateTime startDate2 = consumeOilFlow.getGetDate();
                    Calendar calendar2 = GregorianCalendar.from(startDate2.atZone(ZoneId.systemDefault()));
                    calendar2.add(Calendar.HOUR_OF_DAY, 2);
                    Date endDate2 = calendar2.getTime();
                    if ((this.isEffectiveDate(consumeOilFlow.getGetDate(), startDate1, endDate1) || this.isEffectiveDate(consumeOilFlow.getCreateTime(), startDate2, endDate2))
                            && (consumeOilFlow.getExpireType() == null || consumeOilFlow.getExpireType() == 0)) {//???????????????????????????????????????????????????????????????????????????????????????0???????????????????????????????????????
                        isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_FEE_IS_BILL_405);
                    } else {
                        isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401);
                        if (consumeOilFlow.getFromType() == OrderAccountConst.PAY_ORDER_OIL.FROM_TYPE2) {
                            isAutoTransfer = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;//???????????????????????????????????????????????????????????????
                        }
                    }
                    //????????????????????????????????????
                    ConsumeOilFlowExt ext = consumeOilFlowExtService.getConsumeOilFlowExtByFlowId(consumeOilFlow.getFlowId());
                    payOutIntfVirToVir.setPlateNumber(consumeOilFlow.getPlateNumber());
                    payOutIntfVirToVir.setBusiCode(consumeOilFlow.getOrderId());

                } else {
                    throw new BusinessException("?????????????????????");
                }
            }

            if (OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(sign)) {//????????????????????????????????????????????????
                UserRepairMargin userRepairMargin = (UserRepairMargin) obj;
                if (userRepairMargin != null) {
                    LocalDateTime startDate1 = userRepairMargin.getCreateTime();
                    Calendar calendar1 = GregorianCalendar.from(startDate1.atZone(ZoneId.systemDefault()));
                    calendar1.add(Calendar.HOUR_OF_DAY, 2);
                    Date endDate1 = calendar1.getTime();

                    LocalDateTime startDate2 = userRepairMargin.getGetDate();
                    Calendar calendar2 = GregorianCalendar.from(startDate2.atZone(ZoneId.systemDefault()));
                    calendar2.add(Calendar.HOUR_OF_DAY, 2);
                    Date endDate2 = calendar2.getTime();

                    if ((this.isEffectiveDate(userRepairMargin.getGetDate(), startDate1, endDate1) || this.isEffectiveDate(userRepairMargin.getCreateTime(), startDate2, endDate2))
                            && (userRepairMargin.getExpireType() == null || userRepairMargin.getExpireType() == 0)) {//???????????????????????????????????????????????????????????????????????????????????????0???????????????????????????????????????
                        isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.OIL_FEE_IS_BILL_406);
                    } else {
                        isAutoTransfer = payFeeLimitService.getAmountLimitCfgVal(account.getSourceTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.AUTO_TRANSFER_401);
                    }
                    Long repairId = userRepairMargin.getRepairId();
                    if (repairId != null && repairId > 0) {
                        UserRepairInfo userRepairInfo = userRepairInfoService.getUserRepairInfo(repairId);
                        if (userRepairInfo != null) {
                            payOutIntfVirToVir.setPlateNumber(userRepairInfo.getPlateNumber());
                            payOutIntfVirToVir.setBusiCode(userRepairInfo.getRepairCode());
                        }
                    }
                } else {
                    throw new BusinessException("?????????????????????");
                }
            }
            if (isTenant) {
                payOutIntfVirToVir.setTenantId(sysTenantDef.getId());
            } else {
                payOutIntfVirToVir.setTenantId(-1L);
            }
            payOutIntfVirToVir.setRemark("????????????????????????");
            //????????????????????????????????????
            if (isAutoTransfer == OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1) {
                payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);
            } else {
                payOutIntfVirToVir.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);
            }

            payOutIntfVirToVir.setOilAffiliation(oilAffiliation);
            payoutIntfService.doSavePayOutIntfVirToVir(payOutIntfVirToVir, accessToken);

            // ????????????
            String remark = "?????????????????????" + "????????????" + orderId + " ???????????????" + amountFee;
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.MarginTurnCash, EnumConsts.PayInter.PAYFOR_CODE, SysOperLogConst.OperType.Add, remark);
            // ?????????????????????????????????????????????
            ParametersNewDto inParam = busiToOrderUtils.setParametersNew(userId, sysOperator.getBillId(), EnumConsts.PayInter.PAYFOR_CODE, orderId, amountFee, vehicleAffiliation, "");
            inParam.setSign(sign);
            inParam.setTenantId(tenantId);
            inParam.setOtherFlowId(otherFlowId);
            inParam.setPayFlowId(payOutIntfVirToVir.getId());
            inParam.setOilAffiliation(oilAffiliation);
            inParam.setObj(obj);
            if (orderLimits != null) {
                inParam.setOrderLimitBase(orderLimits);
            }
            // ?????????????????????
            BusiSubjectsRel reduce = new BusiSubjectsRel();
            reduce.setSubjectsId(turnReceSubjectId);
            reduce.setAmountFee(amountFee);
            // ??????????????????
            BusiSubjectsRel add = new BusiSubjectsRel();
            add.setSubjectsId(turnPaySubjectId);
            add.setAmountFee(amountFee);
            busiList.add(reduce);
            busiList.add(add);
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, busiList);
            busiToOrderUtils.busiToOrderNew(inParam, busiSubjectsRelList, loginInfo);
        } else {
            // ????????????
            String remark = "?????????????????????" + "????????????" + orderId + " ???????????????" + amountFee;
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.MarginTurnCash, EnumConsts.PayInter.PAYFOR_CODE, SysOperLogConst.OperType.Add, remark);
            // ?????????????????????????????????????????????
            ParametersNewDto inParam = busiToOrderUtils.setParametersNew(userId, sysOperator.getBillId(), EnumConsts.PayInter.PAYFOR_CODE, orderId, amountFee, vehicleAffiliation, "");
            inParam.setSign(sign);
            inParam.setTenantId(tenantId);
            inParam.setOtherFlowId(otherFlowId);
            inParam.setPayFlowId(payFlowId);
            inParam.setOilAffiliation(oilAffiliation);
            inParam.setObj(obj);
            if (orderLimits != null) {
                inParam.setOrderLimitBase(orderLimits);
            }
            // ?????????????????????
            BusiSubjectsRel reduce = new BusiSubjectsRel();
            reduce.setSubjectsId(turnReceSubjectId);
            reduce.setAmountFee(amountFee);
            // ??????????????????
            BusiSubjectsRel add = new BusiSubjectsRel();
            add.setSubjectsId(turnPaySubjectId);
            add.setAmountFee(amountFee);
            busiList.add(reduce);
            busiList.add(add);
            busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.PAYFOR_CODE, busiList);
            busiToOrderUtils.busiToOrderNew(inParam, busiSubjectsRelList, loginInfo);
        }
    }

    @Override
    @Transactional
    public String execution() {
        LoginInfo baseUser = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (baseUser == null) {
            baseUser = new LoginInfo();
            baseUser.setName(((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            baseUser.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            baseUser.setTenantId(-1L);
            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), baseUser);
        }
        String accessToken = ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();
        Integer[] dealStates = new Integer[]{OrderAccountConst.STATE.INIT};
        //????????????????????????????????????
        List<OrderLimit> orderLimitList = queryOrderLimit(dealStates, DateUtil.getTimeStamp(DateUtil.getCurrDateTime()), -1);
        log.info("--------??????????????????????????????--------");
        Long orderId = 0L;
        Long userId = 0L;
        Long sourceTenantId = 0L;
        String vehicleAffiliation = null;
        String oilAffiliation = null;
        QueryOrderProblemInfoQueryVo queryOrderProblemInfoQueryVo = new QueryOrderProblemInfoQueryVo();
        queryOrderProblemInfoQueryVo.setState(SysStaticDataEnum.EXPENSE_STATE.CHECK_PENDING + "," + SysStaticDataEnum.EXPENSE_STATE.AUDIT);
        queryOrderProblemInfoQueryVo.setCodeId("1");
        for (int i = 0; i < orderLimitList.size(); i++) {
            OrderLimit ol = orderLimitList.get(i);
            orderId = ol.getOrderId();
            userId = ol.getUserId();
            vehicleAffiliation = ol.getVehicleAffiliation();
            oilAffiliation = ol.getOilAffiliation();
            sourceTenantId = ol.getTenantId();
            queryOrderProblemInfoQueryVo.setOrderId(orderId);
            try {
                //20190710 ???ID1006068???
                List<OrderProblemInfo> problemList = orderProblemInfoService.queryOrderProblemInfoQueryList(queryOrderProblemInfoQueryVo);
                if (problemList != null && problemList.size() > 0) {
                    throw new BusinessException("???????????????????????????????????????????????????");
                }
                //??????????????????????????????
                baseUser.setTenantId(sourceTenantId);
                this.marginTurnCash(userId, vehicleAffiliation, ol.getNoPayFinal(), orderId, sourceTenantId, OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1, oilAffiliation, -1L, ol, accessToken);
                ol.setFianlSts(OrderAccountConst.STATE.SUCCESS);//??????
                ol.setStsDate(LocalDateTime.now());
                ol.setStsNote("??????????????????????????????");
                orderLimitService.saveOrUpdate(ol);
            } catch (Exception ex) {
                log.error("???????????????????????????" + ex.getMessage());
                ex.printStackTrace();
                ol.setFianlSts(OrderAccountConst.STATE.FAIL);//??????
                ol.setStsDate(LocalDateTime.now());
                ol.setStsNote("????????????????????????");
                orderLimitService.saveOrUpdate(ol);
                continue;
            }
        }
        //?????????????????????????????????
        List<ConsumeOilFlow> cList = consumeOilFlowService.getConsumeOilFlowNew(OrderAccountConst.CONSUME_COST_TYPE.TYPE2, dealStates, DateUtil.getTimeStamp(DateUtil.getCurrDateTime()), -1, -1, null);
        for (ConsumeOilFlow map : cList) {
            String flowIds = map.getFlowIds();
            Long tenantId = map.getTenantId();
            Long flowId = map.getFlowId();
            Long undueAmount = map.getUndueAmount();
            baseUser.setTenantId(tenantId);
            try {
                ConsumeOilFlow c = consumeOilFlowService.get(flowId);
                Long payFlowId = iOrderOilSourceService.payTurnCash(c, undueAmount, accessToken);
                if (payFlowId == null) {
                    continue;
                }
                List<ConsumeOilFlow> consumeOilFlowList = consumeOilFlowService.doQueryConsumeOilFlow(flowIds);
                for (ConsumeOilFlow u : consumeOilFlowList) {
                    //?????????????????????
                    marginTurnCash(u.getUserId(), u.getVehicleAffiliation(), u.getUndueAmount(), u.getId(), u.getTenantId(), OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2, u.getOilAffiliation(), payFlowId, u, accessToken);
                    u.setExpiredAmount((u.getExpiredAmount() == null ? 0L : u.getExpiredAmount()) + u.getUndueAmount());
                    u.setUndueAmount(0L);
                    u.setGetResult("????????????");
                    u.setState(OrderAccountConst.STATE.SUCCESS);
                    consumeOilFlowService.saveOrUpdate(u);
                }
            } catch (Exception ex) {
                //????????????????????????????????????????????????
                log.error("???????????????????????????FLOW_ID: " + flowIds + " ????????????:" + ex.getMessage());
                ex.printStackTrace();
                consumeOilFlowService.updateConsumeOilFlow(flowIds, OrderAccountConst.STATE.FAIL, "????????????");
                continue;
            }
        }
        //?????????????????????????????????

        //?????????????????????????????????
        //?????????????????????????????????
        List<UserRepairMargin> userRepairMarginList = userRepairMarginService.getUserRepairMargin(DateUtil.getTimeStamp(DateUtil.getCurrDateTime()), dealStates);
        for (UserRepairMargin u : userRepairMarginList) {
            long flowId = u.getId();
            try {
                baseUser.setTenantId(u.getTenantId());
                marginTurnCash(u.getUserId(), u.getVehicleAffiliation(), u.getUndueAmount(), u.getId(), u.getTenantId(), OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3, u.getOilAffiliation(), -1L, u, accessToken);
                u.setExpiredAmount((u.getExpiredAmount() == null ? 0L : u.getExpiredAmount()) + u.getUndueAmount());
                u.setUndueAmount(0L);
                u.setGetResult("????????????");
                u.setState(OrderAccountConst.STATE.SUCCESS);
                userRepairMarginService.saveOrUpdate(u);
                if (u.getRepairId() != null && u.getRepairId() > 0) {
                    UserRepairInfo uri = userRepairInfoService.getById(u.getRepairId());
                    uri.setRepairState(SysStaticDataEnum.REPAIR_STATE.DUE);
                    userRepairInfoService.saveOrUpdate(uri);
                }
            } catch (Exception ex) {
                log.error("???????????????????????????" + ex.getMessage());
                ex.printStackTrace();
                u = userRepairMarginService.getUserRepairMargin(flowId);
                u.setGetResult("????????????:" + ex.getMessage());
                u.setState(OrderAccountConst.STATE.FAIL);
                userRepairMarginService.saveOrUpdate(u);
                continue;
            }
        }
        log.info("--------??????????????????????????????--------");
        //?????????????????????????????????
        return "????????????????????????????????????";
    }

    /**
     * ???????????????????????????[startTime, endTime]????????????????????????????????????
     *
     * @param nowTime   ????????????
     * @param startTime ????????????
     * @param endTime   ????????????
     * @return
     * @author jqlin
     */
    public boolean isEffectiveDate(LocalDateTime nowTime, LocalDateTime startTime, Date endTime) {
        Date nowTimeDate = Date.from(nowTime.atZone(ZoneId.systemDefault()).toInstant());
        Date startDate = Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant());
        if (nowTimeDate.getTime() == startDate.getTime()
                || nowTimeDate.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTimeDate);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


}
