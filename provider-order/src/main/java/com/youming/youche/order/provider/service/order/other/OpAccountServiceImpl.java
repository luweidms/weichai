package com.youming.youche.order.provider.service.order.other;

import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.ICreditRatingRuleService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.CreditRatingRule;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.vo.SysTenantVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资金账户相关操作接口实现类
 * */
@Service
public class OpAccountServiceImpl implements IOpAccountService {

    @Resource
    IOrderLimitService orderLimitService;

    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    ICreditRatingRuleService iCreditRatingRuleService;


    @Override
    public void createOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderScheduler scheduler,
                                 OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt, long tenantId) {
        if (orderFee == null) {
            throw new BusinessException("订单信息有误!");
        } else if (orderFee.getOrderId() == null || orderFee.getOrderId() <= 0L) {
            throw new BusinessException("订单号有误");
        }
        List<OrderLimit> olList = orderLimitService.getOrderLimit(orderFee.getOrderId(), tenantId,-1);
        OrderLimit ol = new OrderLimit();
        if (olList.isEmpty()) {// 建单
//            ol = new OrderLimit();
            //指派车队
            if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    throw new BusinessException("指派车队订单，未找到车队的租户id");
                }
                Long toTenantId = orderInfo.getToTenantId();
                SysTenantVo tenant = sysTenantDefService.getTenantById(toTenantId);
                Long toTenantUserId = null;
                if(tenant != null && tenant.getAdminUser() != null){
                    toTenantUserId= tenant.getAdminUser();
                }
                if (toTenantUserId == null || toTenantUserId <= 0L) {
                    throw new BusinessException("没有找到租户的用户id!");
                }
                UserDataInfo toTenantUser = userDataInfoService.getById(toTenantUserId);
                if (toTenantUser == null) {
                    throw new BusinessException("没有找到租户信息");
                }
                ol.setUserId(toTenantUserId);
                //会员体系改造开始
                ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                //会员体系改造结束
                ol.setUserName(toTenantUser.getLinkman());
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
                //usertype不能为空，给个默认值先，后面再指定具体人再设置
                ol.setUserType(SysStaticDataEnum.USER_TYPE.CUSTOMER_USER);

            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR  || scheduler.getAppointWay() == 6) {
                //有租户车辆
                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    //自有车
                    if (SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                        ol.setUserId(scheduler.getCarDriverId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCarDriverMan());
                    } else {
                        Long toTenantId = orderInfo.getToTenantId();
                        Long toTenantUserId = sysTenantDefService.getTenantById(toTenantId).getAdminUser();
                        if (toTenantUserId == null || toTenantUserId <= 0L) {
                            throw new BusinessException("没有找到租户的用户id!");
                        }
                        UserDataInfo toTenantUser = userDataInfoService.getById(toTenantUserId);
                        if (toTenantUser == null) {
                            throw new BusinessException("没有找到租户信息");
                        }
                        ol.setUserId(toTenantUserId);
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        //会员体系改造结束
                        ol.setUserName(toTenantUser.getLinkman());
                    }
                } else {
                    if (scheduler.getIsCollection() != null && scheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        ol.setUserId(scheduler.getCollectionUserId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCollectionUserName());
                        if(scheduler.getCollectionUserId() == null){
                            scheduler.setCollectionUserId(-1L);
                        }
                        //新增一条代收订单，司机限制表
                        if (scheduler.getCarDriverId() != null && scheduler.getCarDriverId().longValue() != scheduler.getCollectionUserId().longValue()) {
                            OrderLimit collectionOrderLimit = this.createCollectionOrderLimit(orderInfo, orderInfoExt, orderFee, scheduler, orderGoodsInfo, orderFeeExt, tenantId);
                            orderLimitService.saveOrUpdate(collectionOrderLimit);
                        }
                    } else {
                        ol.setUserId(scheduler.getCarDriverId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCarDriverMan());
                    }
                }
            }
            ol.setOrderId(orderFee.getOrderId());
            if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                ol.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            }
            else{
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
                    throw new BusinessException("开平台票资金渠道和油资金渠道不一致");
                }
            }
            if (ol.getVehicleAffiliation() != null  && (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) ||
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()))) {
                if (ol.getOilAffiliation() != null && Long.parseLong(ol.getOilAffiliation()) > Long.parseLong(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)) {
                    throw new BusinessException("资金渠道和油资金渠道不一致");
                }
            }
            if (scheduler.getCarDriverId() != null && scheduler.getCarDriverId() > 0 && scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                // 需要新增一条副驾的限制表
                orderLimitService.save(createOrderLimitCopilot(orderInfo,orderInfoExt,orderFee,scheduler, orderGoodsInfo,orderFeeExt, tenantId));
            }
        } else {
            //指派车队
            if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
                if (orderInfo.getToTenantId() == null || orderInfo.getToTenantId() <= 0) {
                    throw new BusinessException("指派车队订单，未找到车队的租户id");
                }
                Long toTenantId = orderInfo.getToTenantId();
                Long toTenantUserId = sysTenantDefService.getTenantById(toTenantId).getAdminUser();
                if (toTenantUserId == null || toTenantUserId <= 0L) {
                    throw new BusinessException("没有找到租户的用户id!");
                }
                UserDataInfo toTenantUser = userDataInfoService.getById(toTenantUserId);
                if (toTenantUser == null) {
                    throw new BusinessException("没有找到租户信息");
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
                    BeanUtils.copyProperties(olList.get(0),ol);
                    resetOrderLimit(ol);
                }
                ol.setUserId(toTenantUserId);
                //会员体系改造开始
                ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                //会员体系改造结束
                ol.setUserName(toTenantUser.getLinkman());
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
                ol = olList.get(0);
                //指派部门和指派车辆都走这
            } else if (scheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR || scheduler.getAppointWay() == 6) {
                //有租户车辆
                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    //自有车
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
                            BeanUtils.copyProperties( olList.get(0),ol);
                            resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCarDriverId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCarDriverMan());
                    } else {
                        Long toTenantId = orderInfo.getToTenantId();
                        Long toTenantUserId = sysTenantDefService.getTenantById(toTenantId).getAdminUser();
                        if (toTenantUserId == null || toTenantUserId <= 0L) {
                            throw new BusinessException("没有找到租户的用户id!");
                        }
                        UserDataInfo toTenantUser = userDataInfoService.get(toTenantUserId);
                        if (toTenantUser == null) {
                            throw new BusinessException("没有找到租户信息");
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
                            BeanUtils.copyProperties( olList.get(0),ol);
                            resetOrderLimit(ol);
                        }
                        ol.setUserId(toTenantUserId);
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                        //会员体系改造结束
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
                            BeanUtils.copyProperties( olList.get(0),ol);
                            resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCollectionUserId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCollectionUserName());
                        //司机
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
                            BeanUtils.copyProperties(olList.get(0),collectionDriverLimit);
                            resetOrderLimit(collectionDriverLimit);
                        }
                        collectionDriverLimit.setUserId(scheduler.getCarDriverId());
                        //会员体系改造开始
                        collectionDriverLimit.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        collectionDriverLimit.setUserName(scheduler.getCarDriverMan());
                        collectionDriverLimit.setUserPhone(scheduler.getCarDriverPhone());
                        //保持和代收人限制表一致
                        if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                            collectionDriverLimit.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                        }
                        else{
                            collectionDriverLimit.setVehicleAffiliation(orderFee.getVehicleAffiliation());
                        }
                        collectionDriverLimit.setOilAffiliation(orderInfoExt.getOilAffiliation());
                        collectionDriverLimit.setIsNeedBill(orderInfo.getIsNeedBill());
                        collectionDriverLimit.setOilConsumer(orderFeeExt.getOilConsumer());
                        collectionDriverLimit.setOilAccountType(orderFeeExt.getOilAccountType());
                        collectionDriverLimit.setOilBillType(orderFeeExt.getOilBillType());
                        if (collectionDriverLimit.getIsNeedBill() != null && collectionDriverLimit.getIsNeedBill() <= 0) {
                            collectionDriverLimit.setVerificationState(0);//票据进程不扫描
                        } else {
                            collectionDriverLimit.setVerificationState(-1);
                        }
                        orderLimitService.saveOrUpdate(collectionDriverLimit);
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
                            BeanUtils.copyProperties(olList.get(0),ol);
                            this.resetOrderLimit(ol);
                        }
                        ol.setUserId(scheduler.getCarDriverId());
                        //会员体系改造开始
                        ol.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        ol.setUserName(scheduler.getCarDriverMan());
                    }
                }
            }
//			ol.setVehicleAffiliation(orderFee.getVehicleAffiliation());
            if (null != scheduler.getVehicleClass() && SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 == scheduler.getVehicleClass()) {
                ol.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
            }
            else{
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
                    throw new BusinessException("开平台票资金渠道和油资金渠道不一致");
                }
            }
            if (ol.getVehicleAffiliation() != null  && (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(ol.getVehicleAffiliation()) ||
                    OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(ol.getVehicleAffiliation()))) {
                if (ol.getOilAffiliation() != null && Long.parseLong(ol.getOilAffiliation()) > Long.parseLong(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1)) {
                    throw new BusinessException("资金渠道和油资金渠道不一致");
                }
            }
            // 调车完成，要是有副驾信息，需要新增一条副驾数据
            if (scheduler.getCopilotUserId() != null && scheduler.getCopilotUserId() > 0) {
                boolean hasCopilotUserId = false;
                for (OrderLimit limit : olList) {
                    if (limit.getUserId().longValue() == scheduler.getCopilotUserId().longValue()) {
                        limit.setUserId(scheduler.getCopilotUserId());
                        //会员体系改造开始
                        limit.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                        //会员体系改造结束
                        limit.setUserName(scheduler.getCopilotMan());
                        limit.setUserPhone(scheduler.getCopilotPhone());
                        limit.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                        limit.setOilAffiliation(orderInfoExt.getOilAffiliation());
                        limit.setIsNeedBill(orderInfo.getIsNeedBill());
                        limit.setOilConsumer(orderFeeExt.getOilConsumer());
                        limit.setOilAccountType(orderFeeExt.getOilAccountType());
                        limit.setOilBillType(orderFeeExt.getOilBillType());
                        if (limit.getIsNeedBill() != null && ol.getIsNeedBill() <= 0) {
                            limit.setVerificationState(0);//票据进程不扫描
                        } else {
                            limit.setVerificationState(-1);
                        }
                        try {
                            orderLimitService.saveOrUpdate(limit);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        hasCopilotUserId = true;
                        break;
                    }
                }
                if (!hasCopilotUserId) {
                    OrderLimit olCopilot = new OrderLimit();
                    BeanUtils.copyProperties( olList.get(0),olCopilot);
                    this.resetOrderLimit(olCopilot);
                    olCopilot.setUserId(scheduler.getCopilotUserId());
                    //会员体系改造开始
                    olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
                    //会员体系改造结束
                    olCopilot.setUserName(scheduler.getCopilotMan());
                    olCopilot.setUserPhone(scheduler.getCopilotPhone());
                    olCopilot.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
                    olCopilot.setOilAffiliation(orderInfoExt.getOilAffiliation());
                    olCopilot.setIsNeedBill(orderInfo.getIsNeedBill());
                    olCopilot.setOilConsumer(orderFeeExt.getOilConsumer());
                    olCopilot.setOilAccountType(orderFeeExt.getOilAccountType());
                    olCopilot.setOilBillType(orderFeeExt.getOilBillType());
                    if (olCopilot.getIsNeedBill() != null && olCopilot.getIsNeedBill() <= 0) {
                        olCopilot.setVerificationState(0);//票据进程不扫描
                    } else {
                        olCopilot.setVerificationState(-1);
                    }
                    orderLimitService.saveOrUpdate(olCopilot);
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
            ol.setVerificationState(0);//票据进程不扫描
        } else {
            ol.setVerificationState(-1);
        }
        orderLimitService.saveOrUpdate(ol);
    }

    @Override
    public OrderLimit createOrderLimitCopilot(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee,
                                              OrderScheduler scheduler, OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt,
                                              long tenantId)  {
        OrderLimit olCopilot = new OrderLimit();
        olCopilot.setOrderId(scheduler.getOrderId());
        olCopilot.setUserId(scheduler.getCopilotUserId());
        //会员体系改造开始
        olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //会员体系改造结束
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
            olCopilot.setVerificationState(0);//票据进程不扫描
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }

    @Override
    public void handleRepairFundAccount(long userId, String vehicleAffiliation, long amountFee, long businessId, long subjectsId, long soNbr, Long orderId, String objId, long tenantId, Integer userType) throws Exception {

    }

    @Override
    public void recharge(long userId, String vehicleAffiliation, long amountFee, long counterFee, Long tenantId, Integer userType) throws Exception {

    }

    @Override
    public Map<String, Object> getMarginBalanceUI(Long userId, Integer userType)  {
        if (userId == null || userId <= 1) {
            throw new BusinessException("用户id不合法");
        }
        // 查询司机所有资金账户
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccountQuey(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1L, userType);
        List<MarginBalanceDetailsOut> list = new ArrayList<MarginBalanceDetailsOut>();
        Map<String, MarginBalanceDetailsOut> map = new HashMap<String, MarginBalanceDetailsOut>();
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0) {
                String tenantId = String.valueOf(ac.getSourceTenantId());
                MarginBalanceDetailsOut out = map.get(tenantId);
                if (out == null) {
                    out = new  MarginBalanceDetailsOut();
                    map.put(tenantId, out);
                }
            }
        }
        //可以预支金额
        long canAdvance = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0 && ac.getMarginBalance() > 0) {
                CreditRatingRule rating = iCreditRatingRuleService.getCreditRatingRule(userId, ac.getSourceTenantId());
                if (userType!= SysStaticDataEnum.USER_TYPE.SERVICE_USER&&rating == null) {
                    throw new BusinessException("未找到租户的会员权益规则信息");
                }
                if (userType== SysStaticDataEnum.USER_TYPE.SERVICE_USER||(rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES)) {
                    canAdvance += ac.getMarginBalance();
                    SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(ac.getSourceTenantId());
                    if (sysTenantDef == null) {
                        throw new BusinessException("根据租户id：" + ac.getSourceTenantId() + "未找到车队信息");
                    }
                    String tenantId = String.valueOf(ac.getSourceTenantId());
                    MarginBalanceDetailsOut out = map.get(tenantId);
                    long tempAdvanceAmount = out.getAdvanceAmount() == null ? 0L : out.getAdvanceAmount();
                    out.setUserId(userId);
                    out.setAdvanceAmount(tempAdvanceAmount + ac.getMarginBalance());
                    out.setFleetName(sysTenantDef.getName());
                    Float advanceCharge = rating.getCounterFee();
                    if (advanceCharge == null) {
                        advanceCharge = 0F;
                    }
                    Double temp = CommonUtil.getDoubleFormat(new Double(100 * rating.getCounterFee()),2);
                    String proportion = String.valueOf(temp) + "%";
                    out.setProportion(proportion);
                    out.setTenantId(ac.getSourceTenantId());
                    if (!list.contains(out)) {
                        list.add(out);
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put(OrderAccountConst.ACCOUNT_KEY.canAdvance, canAdvance);
        result.put(OrderAccountConst.COMMON_KEY.list, list);
        return result;
    }

    @Override
    public  List<MarginBalanceDetailsOut> getMarginBalance(Long userId, Integer userType) {
        if (userId == null || userId <= 1) {
            throw new BusinessException("用户id不合法");
        }
        // 查询司机所有资金账户
        List<OrderAccount> accountList = iOrderAccountService.getOrderAccountQuey(userId, OrderAccountConst.ORDER_BY.VEHICLE_AFFILIATION, -1L, userType);
        List<MarginBalanceDetailsOut> list = new ArrayList<MarginBalanceDetailsOut>();
        Map<String, MarginBalanceDetailsOut> map = new HashMap<String, MarginBalanceDetailsOut>();
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0) {
                String tenantId = String.valueOf(ac.getSourceTenantId());
                MarginBalanceDetailsOut out = map.get(tenantId);
                if (out == null) {
                    out = new  MarginBalanceDetailsOut();
                    map.put(tenantId, out);
                }
            }
        }
        //可以预支金额
        long canAdvance = 0;
        for (OrderAccount ac : accountList) {
            if (ac.getAccState() == OrderAccountConst.ORDER_ACCOUNT_STATE.STATE1 && ac.getSourceTenantId() > 0 && ac.getMarginBalance() > 0) {
                CreditRatingRule rating = iCreditRatingRuleService.getCreditRatingRule(userId, ac.getSourceTenantId());
                if (userType!= SysStaticDataEnum.USER_TYPE.SERVICE_USER&&rating == null) {
                    throw new BusinessException("未找到租户的会员权益规则信息");
                }
                if (userType== SysStaticDataEnum.USER_TYPE.SERVICE_USER||(rating.getIsAdvance() != null && rating.getIsAdvance() == OrderAccountConst.IS_ADVANCE.YES)) {
                    canAdvance += ac.getMarginBalance();
                    SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(ac.getSourceTenantId());
                    if (sysTenantDef == null) {
                        throw new BusinessException("根据租户id：" + ac.getSourceTenantId() + "未找到车队信息");
                    }
                    String tenantId = String.valueOf(ac.getSourceTenantId());
                    MarginBalanceDetailsOut out = map.get(tenantId);
                    long tempAdvanceAmount = out.getAdvanceAmount() == null ? 0L : out.getAdvanceAmount();
                    out.setUserId(userId);
                    out.setAdvanceAmount(tempAdvanceAmount + ac.getMarginBalance());
                    out.setFleetName(sysTenantDef.getName());
                    Float advanceCharge = rating.getCounterFee();
                    if (advanceCharge == null) {
                        advanceCharge = 0F;
                    }
                    Double temp = CommonUtil.getDoubleFormat(new Double(100 * rating.getCounterFee()),2);
                    String proportion = String.valueOf(temp) + "%";
                    out.setProportion(proportion);
                    out.setTenantId(ac.getSourceTenantId());
                    if (!list.contains(out)) {
                        list.add(out);
                    }
                }
            }
        }
        return list;
    }

//    @Override
//    public Pagination getMarginBalance(Long userId, String fleetName, Integer userType) throws Exception {
//        return null;
//    }

    @Override
    public OrderAccount createOrderAccount(Long userId, String vehicleAffiliation, Long tenantId, Integer userType) throws Exception {
        return null;
    }

//    @Override
//    public Pagination getAccountDetails(String name, String accState, Integer userType) throws Exception {
//        return null;
//    }
//
//    @Override
//    public Pagination getAccountDetailsNoPay(String userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion) throws Exception {
//        return null;
//    }
//
//    @Override
//    public Pagination getAccountDetailsPledge(String userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion, String name, String carDriverPhone, String plateNumber) throws Exception {
//        return null;
//    }

    @Override
    public void lockPinganAmount(Long userId, String pinganPayAcctId, String orderId, String vehicleAffiliation, long soNbr, Long tenantId, List<BusiSubjectsRel> busiSubjectsRelList, long subjectsId, String oilAffiliation) throws Exception {

    }

    @Override
    public OrderLimit createCollectionOrderLimit(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderFee orderFee, OrderScheduler scheduler, OrderGoods orderGoodsInfo, OrderFeeExt orderFeeExt, long tenantId) {
        OrderLimit olCopilot = new OrderLimit();
        olCopilot.setOrderId(scheduler.getOrderId());
        olCopilot.setUserId(scheduler.getCarDriverId());
        //会员体系改造开始
        olCopilot.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        //会员体系改造结束
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
            olCopilot.setVerificationState(0);//票据进程不扫描
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }



    public OrderLimit resetOrderLimit(OrderLimit olCopilot)  {
        if (olCopilot == null) {
            throw new BusinessException("订单限制表对象不能为空");
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
            olCopilot.setVerificationState(0);//票据进程不扫描
        } else {
            olCopilot.setVerificationState(-1);
        }
        return olCopilot;
    }
}
