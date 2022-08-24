package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.ITransferInfoService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.order.dto.OrderTransferInfoDto;
import com.youming.youche.order.provider.mapper.TransferInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderTransferInfoMapper;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.util.OrderUtil;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.OrderConsts;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
@Slf4j
public class OrderTransferInfoServiceImpl extends BaseServiceImpl<OrderTransferInfoMapper, OrderTransferInfo> implements IOrderTransferInfoService {
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Lazy
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderInfoHService orderInfoHService;

    @Resource
    private IOrderInfoExtHService orderInfoExtHService;

    @Resource
    private IAccountBankRelService accountBankRelService;

    @Resource
    private IOrderInfoExtService orderInfoExtService;

    @DubboReference(version = "1.0.0")
    private ISysSmsSendService sysSmsSendService;

    @Resource
    private IOrderGoodsHService orderGoodsHService;

    @Resource
    private IOrderGoodsService orderGoodsService;

    @Resource
    private IOrderFeeExtHService orderFeeExtHService;

    @Resource
    private IOrderFeeExtService orderFeeExtService;

    @Resource
    private IOrderFeeHService orderFeeHService;
    @Resource
    private IOrderFeeService orderFeeService;

    @Resource
    private IOrderSchedulerHService orderSchedulerHService;

    @Resource
    private IOrderSchedulerService orderSchedulerService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;


    @Resource
    private IOrderTransferInfoService orderTransferInfoService;

    @Resource
    private TransferInfoMapper transferInfoMapper;

    @Autowired
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Resource
    private ITransferInfoService transferInfoService;

    @Resource
    private LoginUtils loginUtils;

    @Resource
    OrderTransferInfoMapper orderTransferInfoMapper;

    @Resource
    private ReadisUtil readisUtil;

    @Override
    public List<OrderTransferInfo> queryTransferInfoList(Integer state, Long tenantId, Long orderId) {
        LambdaQueryWrapper<OrderTransferInfo> lambda = new QueryWrapper<OrderTransferInfo>().lambda();
        if (state != null && state != -1) {
            lambda.eq(OrderTransferInfo::getTransferOrderState, state);
        }
        if (orderId != null && orderId > 0) {
            lambda.eq(OrderTransferInfo::getOrderId, orderId);
        }
        if (tenantId != null && tenantId > 0) {
            lambda.eq(OrderTransferInfo::getAcceptTenantId, tenantId);
        }

        List<OrderTransferInfo> p = this.list(lambda);
        return p;
    }

    /**
     * 同步车辆的信息 1 上一单的指派类型为指派车队，并 当前车队指派为车辆类型并且是自有车,C端车的情况，需要同步上一单的订单车辆信息
     * 2上一单的指派为车辆类型，并且车辆类型是外调车，需要把车辆信息同步到上上一单的车辆信息
     *
     * @param orderId
     * @param orderScheduler
     * @throws Exception
     */
    @Override
    public void syncOrderVehicleInfo(Long orderId, OrderInfo orderInfo, OrderScheduler orderScheduler) {

        Long syncOrderId = null;
        boolean isSyncFrom = false;
//        OrderDetailsOutDto

//        ILugeOrderBusinessTF lugeOrderBusinessTF = (ILugeOrderBusinessTF) SysContexts.getBean("lugeOrderBusinessTF");
//        OrderDetailsOut orderDetailsOut = lugeOrderBusinessTF.getOrderAll(orderId);
        OrderDetailsOutDto orderDetailsOut = getOrderAll(orderId);

        OrderInfo preOrderInfo = orderDetailsOut.getOrderInfo();
        OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
        if (preOrderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            // 上一单的指派类型为指派车队，并 当前车队指派为车辆类型并且是自有车的情况
            syncOrderId = preOrderInfo.getOrderId();
        } else if (preOrderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && (preOrderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || preOrderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || preOrderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )) {
            syncOrderId = preOrderInfo.getOrderId();
            isSyncFrom = true;
        }
        log.info("同步订单车辆信息：同步的订单[" + syncOrderId + "],同步车辆[" + orderScheduler.getPlateNumber() + "],车队车辆类型[" + orderScheduler.getVehicleClass() + "]");
        if (syncOrderId != null) {
            // 需要同步车辆信息

            OrderScheduler syncOrderScheduler = orderSchedulerService.getOrderScheduler(syncOrderId);
            if (syncOrderScheduler != null) {
                if (StringUtils.isNotBlank(orderScheduler.getPlateNumber())) {
                    OrderInfoExt syncOrderInfoExt = orderInfoExtService.getOrderInfoExt(syncOrderId);
                    syncOrderScheduler.setPlateNumber(orderScheduler.getPlateNumber());
                    syncOrderScheduler.setCarDriverPhone(orderScheduler.getCarDriverPhone());
                    syncOrderScheduler.setCarDriverMan(orderScheduler.getCarDriverMan());
                    syncOrderScheduler.setCarDriverId(orderScheduler.getCarDriverId());
                    syncOrderScheduler.setVehicleCode(orderScheduler.getVehicleCode());
                    syncOrderScheduler.setCarLengh(orderScheduler.getCarLengh());

                    // 同步的车辆统一设置为外调车
                    if (syncOrderScheduler.getVehicleClass() == null) {
                        syncOrderScheduler.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
                    }
                    syncOrderInfoExt.setPaymentWay(OrderConsts.PAYMENT_WAY.CONTRACT);
                    orderSchedulerService.updateById(syncOrderScheduler);
                    orderInfoExtService.updateById(syncOrderInfoExt);
                }
            } else {
                OrderSchedulerH syncOrderSchedulerH = orderSchedulerHService.selectByOrderId(syncOrderId);
                if (syncOrderSchedulerH != null) {
                    OrderInfoExtH syncOrderInfoExtH = orderInfoExtHService.selectByOrderId(syncOrderId);
                    syncOrderSchedulerH.setPlateNumber(orderScheduler.getPlateNumber());
                    syncOrderSchedulerH.setCarDriverPhone(orderScheduler.getCarDriverPhone());
                    syncOrderSchedulerH.setCarDriverMan(orderScheduler.getCarDriverMan());
                    syncOrderSchedulerH.setCarDriverId(orderScheduler.getCarDriverId());
                    syncOrderSchedulerH.setVehicleCode(orderScheduler.getVehicleCode());
                    syncOrderSchedulerH.setCarLengh(orderScheduler.getCarLengh());

                    // 同步的车辆统一设置为外调车
                    if (syncOrderSchedulerH.getVehicleClass() == null) {
                        syncOrderSchedulerH.setVehicleClass(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
                    }
                    syncOrderInfoExtH.setPaymentWay(OrderConsts.PAYMENT_WAY.CONTRACT);
                    orderSchedulerHService.updateById(syncOrderSchedulerH);
                    orderInfoExtHService.updateById(syncOrderInfoExtH);
                } else {
                    log.error("不需要同步车辆信息：同步的订单[" + syncOrderId + "] Order_Scheduler 数据为空");
                }
            }
            if (isSyncFrom) {
                if (preOrderInfo.getFromOrderId() != null && preOrderInfo.getFromOrderId() > 0) {
                    syncOrderVehicleInfo(preOrderInfo.getFromOrderId(), preOrderInfo, preOrderScheduler);
                }
            }
        }
    }

    @Override
    public OrderDetailsOutDto getOrderAll(Long orderId) {
        OrderDetailsOutDto out = new OrderDetailsOutDto();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.selectByOrderId(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单号[" + orderId + "]信息！");
            }

            OrderInfoExtH orderInfoExtH = orderInfoExtHService.selectByOrderId(orderId);
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            OrderGoodsH orderGoodsH = orderGoodsHService.selectByOrderId(orderId);
            OrderFeeH orderFeeH = orderFeeHService.selectByOrderId(orderId);
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.selectByOrderId(orderId);
            BeanUtil.copyProperties(orderInfoH, orderInfo);
            BeanUtil.copyProperties(orderFeeH, orderFee);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            BeanUtil.copyProperties(orderGoodsH, orderGoods);
            BeanUtil.copyProperties(orderInfoExtH, orderInfoExt);
            BeanUtil.copyProperties(orderFeeExtH, orderFeeExt);
            isHis = true;
        } else {
            orderScheduler = orderSchedulerService.selectByOrderId(orderId);
            orderGoods = orderGoodsService.selectByOrderId(orderId);
            orderInfoExt = orderInfoExtService.selectByOrderId(orderId);
            orderFee = orderFeeService.selectByOrderId(orderId);
            orderFeeExt = orderFeeExtService.selectByOrderId(orderId);
        }
//        orderInfoSV.evict(orderInfo);
//        orderInfoSV.evict(orderInfoExt);
//        orderGoodsSV.evict(orderGoods);
//        orderFeeSV.evict(orderFeeExt);
//        orderFeeSV.evict(orderFee);
//        orderSchedulerSV.evict(orderScheduler);
        out.setIsHis(isHis ? OrderConsts.TableType.HIS : OrderConsts.TableType.ORI);
        out.setOrderFee(orderFee);
        out.setOrderInfo(orderInfo);
        out.setOrderGoods(orderGoods);
        out.setOrderInfoExt(orderInfoExt);
        out.setOrderScheduler(orderScheduler);
        out.setOrderFeeExt(orderFeeExt);
        return out;
    }

    @Override
    public void updateOrderTransferPlateNumber(Long fromOrderId, String plateNumber, LoginInfo loginInfo) {
        if (StringUtils.isNotBlank(plateNumber)) {
            OrderTransferInfo orderTransferInfo = getOrderTransferInfo(fromOrderId, loginInfo.getTenantId(), OrderConsts.TransferOrderState.BILL_YES);
            if (orderTransferInfo != null) {
                orderTransferInfo.setPlateNumber(plateNumber);
                updateById(orderTransferInfo);
            }
        }
    }

    @Override
    public void saveOrderTransferInfo(OrderScheduler orderScheduler, OrderInfo orderInfo,
                                      OrderGoods orderGoods, Long tenantId,
                                      OrderFee orderfee) {
        if (OrderConsts.AppointWay.APPOINT_TENANT == orderScheduler.getAppointWay()) {
            // 指派车队
            // 1 写入转单的中间表
            List<OrderTransferInfo> transferInfos = this
                    .queryTransferInfoList(OrderConsts.TransferOrderState.TO_BE_RECIVE, null, orderInfo.getOrderId());
            OrderTransferInfo orderTransferInfo = new OrderTransferInfo();
            for (OrderTransferInfo Info : transferInfos) {
                if (Info.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                    orderTransferInfo = Info;
                }
            }
            String acceptTenantName = sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId()).getName();
            orderTransferInfo.setOrderId(orderInfo.getOrderId());
            orderTransferInfo.setTransferTenantTenantId(tenantId);
            orderTransferInfo.setTransferTenantName(sysTenantDefService.getSysTenantDef(tenantId).getName());
            orderTransferInfo.setAcceptTenantId(orderInfo.getToTenantId().toString());
            orderTransferInfo.setAcceptTenantName(acceptTenantName);
            orderTransferInfo.setTransferOrderState(OrderConsts.TransferOrderState.TO_BE_RECIVE);
            orderTransferInfo.setDesRegion(orderGoods.getDes());
            orderTransferInfo.setSourceRegion(orderGoods.getSource());
            orderTransferInfo.setGoodsName(orderGoods.getGoodsName());
            orderTransferInfo.setWeight(orderGoods.getWeight());
            orderTransferInfo.setSquare(orderGoods.getSquare());
            orderTransferInfo.setTransferDate(LocalDateTime.now());
            orderTransferInfo.setTotalFee(orderfee.getTotalFee());

            this.saveOrUpdate(orderTransferInfo);

        } else if (OrderConsts.AppointWay.APPOINT_CAR == orderScheduler.getAppointWay()) {
            // 指派车辆
            // 1 如果是外调车，需要写入转单中间表
            // 2 如果是自有车，需要保存油站分配的表

            if ( ( orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )) {
                // 外调车

                if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                    // 有归属车队的外调车
                    String acceptTenantName =sysTenantDefService.getSysTenantDef(orderInfo.getToTenantId()).getName();
                    OrderTransferInfo orderTransferInfo = new OrderTransferInfo();
                    List<OrderTransferInfo> transferInfos = this.queryTransferInfoList(
                            OrderConsts.TransferOrderState.TO_BE_RECIVE, null, orderInfo.getOrderId());
                    for (OrderTransferInfo Info : transferInfos) {
                        if (Info.getTransferOrderState() == OrderConsts.TransferOrderState.TO_BE_RECIVE) {
                            orderTransferInfo = Info;
                        }
                    }
                    orderTransferInfo.setOrderId(orderInfo.getOrderId());
                    orderTransferInfo.setTransferTenantTenantId(tenantId);
                    orderTransferInfo.setTransferTenantName(sysTenantDefService.getSysTenantDef(tenantId).getName());
                    orderTransferInfo.setAcceptTenantId(orderInfo.getToTenantId().toString());
                    orderTransferInfo.setAcceptTenantName(acceptTenantName);
                    orderTransferInfo.setTransferOrderState(OrderConsts.TransferOrderState.TO_BE_RECIVE);
                    orderTransferInfo.setDesRegion(orderGoods.getDes());
                    orderTransferInfo.setSourceRegion(orderGoods.getSource());
                    orderTransferInfo.setGoodsName(orderGoods.getGoodsName());
                    orderTransferInfo.setWeight(orderGoods.getWeight());
                    orderTransferInfo.setSquare(orderGoods.getSquare());
                    orderTransferInfo.setTransferDate(LocalDateTime.now());
                    orderTransferInfo.setPlateNumber(orderScheduler.getPlateNumber());
                    orderTransferInfo.setTotalFee(orderfee.getTotalFee());

                    this.saveOrUpdate(orderTransferInfo);

                }
                /***
                 * 这个逻辑不知道是谁加的，目前不清楚，先注释 else{ //接单列表失效 List
                 * <OrderTransferInfo> transferInfos =
                 * orderTransferinfoSV.queryTransferInfoList(OrderConsts.
                 * TransferOrderState.TO_BE_RECIVE, null,
                 * orderInfo.getOrderId()); for (OrderTransferInfo Info :
                 * transferInfos) { if(Info.getTransferOrderState() ==
                 * OrderConsts.TransferOrderState.TO_BE_RECIVE){
                 * Info.setTransferOrderState(OrderConsts.TransferOrderState.
                 * BILL_TIME_OUT); orderTransferinfoSV.saveOrUpdate(Info); } }
                 * //自由车需要校验线路冲突的问题
                 * orderSchedulerTF.checkLineIsOk(orderScheduler.getPlateNumber(
                 * ), orderScheduler.getCarDriverId(),
                 * orderScheduler.getCopilotUserId(),
                 * orderScheduler.getDependTime(),null);
                 *
                 * }
                 **/
            }
        }
    }

    @Override
    public Long acceptOrderTemp(OrderInfo orderInfo, OrderGoods orderGoods,
                                OrderInfoExt orderInfoExt, OrderScheduler orderScheduler,
                                OrderFee orderFee, OrderFeeExt orderFeeExt,
                                OrderPaymentDaysInfo costPaymentDaysInfo,LoginInfo user,String accessToken) {
        // 把原有的订单数据组装一些，调用原来的订单录入
        //小车队成本收入账期一致
        Long toOrderId = saveNewOrderInfoTemp(orderInfo, orderGoods, orderInfoExt, orderScheduler,
                orderFee, orderFeeExt,costPaymentDaysInfo,user,accessToken);
        //重新赋值
        orderScheduler = orderSchedulerService.getOrderScheduler(toOrderId);
        // 回写原来的订单的数据
        //updatePreOrderInfo(orderInfo.getOrderId(), toOrderId);
        orderInfo.setToOrderId(toOrderId);
        if(orderInfo.getOrderState()== OrderConsts.ORDER_STATE.TO_BE_RECIVE) {
            orderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
        }
        // 回写原来的订单表的数据
        transferInfoMapper.updateOrderTransferState(orderInfo.getOrderId(), orderInfo.getToTenantId(),
                OrderConsts.TransferOrderState.BILL_YES, new Date(), orderScheduler.getPlateNumber(), "系统自动接单", toOrderId);

        // 同步支付中心
        OrderInfo newOrderInfo = new OrderInfo();
        newOrderInfo.setOrderId(orderInfo.getOrderId());
        newOrderInfo.setIsNeedBill(OrderConsts.IS_NEED_BILL.NOT_NEED_BILL);

        orderFeeService.synPayCenterAcceptOrder(newOrderInfo, orderScheduler);
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo,
                orderInfo.getOrderId(), SysOperLogConst.OperType.Update,
                "接单");
        return toOrderId;
    }

    @Override
    public OrderTransferInfo reminder(Long orderId, String accessToken) {
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        LoginInfo user = loginUtils.get(accessToken);
        if(user == null || user.getTenantId() == null){
            throw new BusinessException("用户未登录！");
        }
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH != null) {
                BeanUtils.copyProperties(orderInfo, orderInfoH);
            }else{
                throw new BusinessException("未找到订单["+orderId+"]信息");
            }
        }
        if (orderInfo.getOrderState().intValue() != com.youming.youche.order.commons.OrderConsts.ORDER_STATE.TO_BE_RECIVE) {
            throw new BusinessException("订单状态不是待接单，不能进行催单操作");
        }

        if (orderInfo.getTenantId().longValue() != user.getTenantId()) {
            throw new BusinessException("该订单不属于该车队，不能进行催单操作，请联系客服！");
        }

        SysCfg countSysCfg = sysCfgRedisUtils.getSysCfgByCfgNameAndCfgSystem(EnumConsts.SysCfg.REMINDER_COUNT,-1);
        SysCfg timeSysCfg = sysCfgRedisUtils.getSysCfgByCfgNameAndCfgSystem(EnumConsts.SysCfg.REMINDER_INTERVAL_TIME,-1);

        if (countSysCfg == null || timeSysCfg == null) {
            log.error("缺少催单配置，请联系客服！配置["+EnumConsts.SysCfg.REMINDER_COUNT+","+ EnumConsts.SysCfg.REMINDER_INTERVAL_TIME+"]");
            throw new BusinessException("缺少催单配置，请联系客服！");
        }

        OrderTransferInfo transferInfo = orderTransferInfoService.getOrderTransferInfoBytransferTenant(orderId, null,
                com.youming.youche.order.commons.OrderConsts.TransferOrderState.TO_BE_RECIVE);

        if (transferInfo == null) {
            throw new BusinessException("转单状态不是待接单，不能进行催单操作");
        }

        if (transferInfo.getReminderCount() != null) {
            if (transferInfo.getReminderCount().intValue() >= Integer.parseInt(countSysCfg.getCfgValue().trim())) {
                throw new BusinessException("本单的催单次数已用完，不能进行催单操作");
            }
        }
        if (transferInfo.getReminderDate() != null) {
            Date checkDate = DateUtil.addMinis(getLocalDateTimeToDate(transferInfo.getReminderDate()), Integer.parseInt(timeSysCfg.getCfgValue()));
            if (checkDate.after(new Date())) {
                throw new BusinessException("催单太频繁，需要间隔" + timeSysCfg.getCfgValue() + "分钟");
            }
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(Long.valueOf(transferInfo.getAcceptTenantId()));
        String bill = sysTenantDef.getLinkPhone();
        if (StringUtils.isEmpty(bill)) {
            throw new BusinessException("对方车队的手机号码为空，不能进行催单操作，请联系客服！");
        }
        // EnumConsts.SmsTemplate.REMINDER
        Map paraMap = new HashMap();
        paraMap.put("acceptTenantName", transferInfo.getAcceptTenantName());
        paraMap.put("transferTenantName", transferInfo.getTransferTenantName());
        // 给接单车队的超管发送短信。内容：尊敬的{acceptTenantName}车队，{transferTenantName}车队给您推送了一个订单，请尽快处理。
        sysSmsSendService.sendSms(bill, EnumConsts.SmsTemplate.REMINDER,
                SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                com.youming.youche.record.common.SysStaticDataEnum.OBJ_TYPE.ORDER,
                String.valueOf(orderId), paraMap,accessToken);


        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.OrderInfo,
                transferInfo.getOrderId(), SysOperLogConst.OperType.Update,
                DateUtil.formatDate(new Date(),
                        DateUtil.DATETIME12_FORMAT) + user.getName() + "催单");
        int count = 0;
        if (transferInfo.getReminderCount() != null) {
            count = transferInfo.getReminderCount();
        }
        count = count + 1;

        transferInfo.setReminderCount(count);
        transferInfo.setReminderDate(LocalDateTime.now());
        orderTransferInfoService.saveOrUpdate(transferInfo);
        return transferInfo;
    }

    @Override
    public OrderTransferInfo getOrderTransferInfoBytransferTenant(Long orderId, Long acceptTenantId, Integer... transferOrderState) {
        QueryWrapper<OrderTransferInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        if(acceptTenantId == null){

        }else {
            queryWrapper.eq("accept_tenant_id",acceptTenantId);
        }
        List<OrderTransferInfo> orderTransferInfoList = orderTransferInfoService.list(queryWrapper);
        if (orderTransferInfoList.size()>0 && orderTransferInfoList !=null){
            return orderTransferInfoList.get(0);
        }
        return null;
    }

    @Override
    public List<OrderTransferInfo> queryTransferInfo(LocalDateTime tranDate, Integer state, Integer count) {

        if(tranDate==null){
            throw new BusinessException("转单的时间不能为空！");
        }
        if(state==null){
            throw new BusinessException("转单的状态不能为空！");
        }
        if(count==null){
            count=200;
        }

        QueryWrapper<OrderTransferInfo> orderTransferInfoQueryWrapper = new QueryWrapper<>();
        orderTransferInfoQueryWrapper.eq("transfer_order_state",state)
                .lt("transfer_date",tranDate);
        List<OrderTransferInfo> list = this.list(orderTransferInfoQueryWrapper);
        return list;
    }

    @Override
    public void timeOutOrder(LoginInfo loginInfo,Long orderId, Long tenantId, String remark) {
        try {
            updateTransferInfoOrder(loginInfo,orderId, tenantId, remark, com.youming.youche.order.commons.OrderConsts.TransferOrderState.BILL_TIME_OUT);
        } catch (Exception e) {
            log.error("接单超时处理：订单号[" + orderId + "]", e);
        }

    }
    /**
     * 更新订单，转单信息
     *
     * @param orderId
     * @param tenantId
     * @param remark
     * @param transferOrderState
     * @throws Exception
     */
    private void updateTransferInfoOrder(LoginInfo loginInfo,Long orderId, Long tenantId, String remark, Integer transferOrderState){
        if (orderId == null) {
            throw new BusinessException("订单号为空，请联系客服！");
        }
        if (tenantId == null) {
            throw new BusinessException("未找到车队信息，请联系客服！");
        }
        if (StringUtils.isEmpty(remark)) {
            throw new BusinessException("拒单原因为空");
        }

        // 更新原来的订单的状态为已拒单
        String logRemark = "拒单的原因：" + remark;
        // 判断一下原来的订单的状态为待接单
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        boolean isHis = false;
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH != null) {
                BeanUtils.copyProperties(orderInfo, orderInfoH);
                isHis = true;
            }else{
                throw new BusinessException("未找到订单["+orderId+"]信息");
            }
        }

        if (orderInfo.getToTenantId() == null
                || (orderInfo.getToTenantId() != null && orderInfo.getToTenantId().longValue() != tenantId)) {
            throw new BusinessException("该订单本车队不能进行操作，不是转给本车队，请联系客服！");
        }

        if (orderInfo.getOrderState() == com.youming.youche.order.commons.OrderConsts.ORDER_STATE.TO_BE_RECIVE) {//待接单进行状态同步
            if (isHis) {
                orderInfoService.updateOrderStateH(orderId, tenantId, com.youming.youche.order.commons.OrderConsts.ORDER_STATE.BILL_NOT, logRemark);
            }else{
                orderInfoService.updateOrderState(orderId, tenantId, com.youming.youche.order.commons.OrderConsts.ORDER_STATE.BILL_NOT, logRemark);
            }
        }
        // 判断一下原来的转单的状态为待接单
        OrderTransferInfo orderTransferInfo = this.getOrderTransferInfo(orderId, tenantId,
                com.youming.youche.order.commons.OrderConsts.TransferOrderState.TO_BE_RECIVE);
        if (orderTransferInfo == null) {
            throw new BusinessException("数据有误，不能进行操作");
        }
        // 源租户的日志
        sysOperLogService.saveSysOperLogTime(SysOperLogConst.BusiCode.masterCardDetails,orderId,SysOperLogConst.OperType.Update ,
                logRemark);

        transferInfoService.updateOrderTransferState(orderId, tenantId, transferOrderState, new Date(),  remark);
    }

    /**
     * 保存新的订单数据
     *
     * 根据页面传入的数据跟原来的订单表的数据，组装订单信息，进行保存
     *
     * @param orderInfo
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param
     * @return
     * @throws Exception
     */
    private Long saveNewOrderInfoTemp(OrderInfo orderInfo, OrderGoods orderGoods, OrderInfoExt orderInfoExt,
                                      OrderScheduler orderScheduler, OrderFee orderFee,
                                      OrderFeeExt orderFeeExt,OrderPaymentDaysInfo costPaymentDaysInfo
                                     ,LoginInfo user,String accessToken)  {
        OrderInfo newOrderInfo = new OrderInfo();
        OrderFee newOrderFee = new OrderFee();
        OrderGoods newOrderGoods = new OrderGoods();
        OrderInfoExt newOrderInfoExt = new OrderInfoExt();
        OrderFeeExt newOrderFeeExt = new OrderFeeExt();
        OrderScheduler newOrderScheduler = new OrderScheduler();

        // 订单表的数据
        newOrderInfo.setFromTenantId(orderInfo.getTenantId());
        newOrderInfo.setFromOrderId(orderInfo.getOrderId());
        newOrderInfo.setTenantId(orderInfo.getToTenantId());
        newOrderInfo.setTenantName(orderInfo.getToTenantName());
        newOrderInfo.setOpOrgId(-1L);
        newOrderInfo.setOpName("系统自动");
        newOrderInfo.setOrderType(OrderConsts.OrderType.ONLINE_RECIVE);
        newOrderInfo.setSourceRegion(orderInfo.getSourceRegion());
        newOrderInfo.setDesRegion(orderInfo.getDesRegion());
        newOrderInfo.setSourceProvince(orderInfo.getSourceProvince());
        newOrderInfo.setDesProvince(orderInfo.getDesProvince());
        newOrderInfo.setSourceCounty(orderInfo.getSourceCounty());
        newOrderInfo.setDesCounty(orderInfo.getDesCounty());
        newOrderInfo.setRemark(orderInfo.getRemark());
        newOrderInfo.setOrgId(orderInfo.getOrgId());
        newOrderInfo.setSourceFlag(orderInfo.getSourceFlag());
        //不需要发票
        newOrderInfo.setIsNeedBill(0);

        newOrderGoods.setSource(orderGoods.getSource());
        newOrderGoods.setDes(orderGoods.getDes());
        newOrderGoods.setGoodsName(orderGoods.getGoodsName());
        newOrderGoods.setGoodsType(orderGoods.getGoodsType());
        newOrderGoods.setSquare(orderGoods.getSquare());
        newOrderGoods.setWeight(orderGoods.getWeight());
        newOrderGoods.setFromTenantId(orderInfo.getTenantId());
        newOrderGoods.setRecivePhone(orderGoods.getRecivePhone());
        newOrderGoods.setReciveName(orderGoods.getReciveName());
        newOrderGoods.setContactPhone(orderGoods.getContactPhone());
        newOrderGoods.setReciveType(orderGoods.getReciveType());
        newOrderGoods.setContactName(orderGoods.getContactName());

        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getTenantId());
        setCustomerInfo(sysTenantDef, newOrderGoods);
        newOrderGoods.setLinkName(orderGoods.getLocalUserName());
        newOrderGoods.setLinkPhone(orderGoods.getLocalPhone());

        newOrderGoods.setNand(orderGoods.getNand());
        newOrderGoods.setEand(orderGoods.getEand());
        newOrderGoods.setNandDes(orderGoods.getNandDes());
        newOrderGoods.setEandDes(orderGoods.getEandDes());
        newOrderGoods.setVehicleLengh(orderGoods.getVehicleLengh());
        newOrderGoods.setVehicleStatus(orderGoods.getVehicleStatus());
        newOrderGoods.setLocalUser(orderGoods.getLocalUser());
        newOrderGoods.setLocalPhone(orderGoods.getLocalPhone());
        newOrderGoods.setLocalUserName(orderGoods.getLocalUserName());
        newOrderGoods.setDesDtl(orderGoods.getDesDtl());
        newOrderGoods.setNavigatDesLocation(orderGoods.getNavigatDesLocation());
        newOrderGoods.setNavigatSourceLocation(orderGoods.getNavigatSourceLocation());
        newOrderGoods.setTenantId(orderInfo.getToTenantId());
        newOrderGoods.setReciveAddr(orderScheduler.getReciveAddr());
        newOrderGoods.setAddrDtl(orderGoods.getAddrDtl());
        newOrderGoods.setNand(orderGoods.getNand());
        newOrderGoods.setEand(orderGoods.getEand());
        newOrderGoods.setDesDtl(orderGoods.getDesDtl());
        newOrderGoods.setNandDes(orderGoods.getNandDes());
        newOrderGoods.setEandDes(orderGoods.getEandDes());
        newOrderGoods.setNavigatDesLocation(orderGoods.getNavigatDesLocation());
        newOrderGoods.setNavigatSourceLocation(orderGoods.getNavigatSourceLocation());
        newOrderGoods.setCustomNumber(orderInfo.getOrderId()+"");

        // 订单扩展表的数据
        if (orderInfoExt!=null) {
            newOrderInfoExt.setPaymentWay(orderInfoExt.getPaymentWay());
            newOrderInfoExt.setIsBackhaul(orderInfoExt.getIsBackhaul());
            newOrderInfoExt.setBackhaulPrice(orderInfoExt.getBackhaulPrice());
            newOrderInfoExt.setBackhaulLinkMan(orderInfoExt.getBackhaulLinkMan());
            newOrderInfoExt.setBackhaulLinkManId(orderInfoExt.getBackhaulLinkManId());
            newOrderInfoExt.setBackhaulLinkManBill(orderInfoExt.getBackhaulLinkManBill());
            newOrderInfoExt.setCapacityOil(orderInfoExt.getCapacityOil());
            newOrderInfoExt.setRunOil(orderInfoExt.getRunOil());
            newOrderInfoExt.setIsUseCarOilCost(orderInfoExt.getIsUseCarOilCost());
            newOrderInfoExt.setRunWay(orderInfoExt.getRunWay());
        }
        newOrderInfoExt.setTenantId(orderInfo.getToTenantId());
        //设置临时车队标识
        newOrderInfoExt.setIsTempTenant(OrderConsts.IS_TEMP_TENANT.YES);

        // 订单费用 原来的订单的成本的费用做为订单的收入费用
        newOrderFee.setCostPrice(orderFee.getTotalFee());

        //小车队除了实体虚拟油 其他都为0
        newOrderFee.setInsuranceFee(0L);
        newOrderFee.setPrePayCash(0L);
        newOrderFee.setPreEtcFee(0L);
        newOrderFee.setFinalFee(0L);
        newOrderFee.setAfterPayCash(0L);
        newOrderFee.setArrivePaymentFee(0L);
        //总费用就为实体油+虚拟油
        newOrderFee.setPreOilVirtualFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
        newOrderFee.setPreOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
        newOrderFee.setTotalFee(newOrderFee.getPreOilVirtualFee() + newOrderFee.getPreOilFee());
        newOrderFee.setPreTotalFee(newOrderFee.getTotalFee());
        newOrderFee.setTenantId(orderInfo.getToTenantId());
        //比例初始化
        newOrderFee.setPreTotalScale(1 * 10000L);
        newOrderFee.setPreCashScale(0L);
        newOrderFee.setPreEtcScale(0L);
        newOrderFee.setFinalScale(0L);
        newOrderFee.setPreOilVirtualScale(newOrderFee.getTotalFee() == 0 ? 0L
                : OrderUtil.mul(String.valueOf(CommonUtil.getDoubleFormat((orderFee.getPreOilVirtualFee() * 1.0 / newOrderFee.getTotalFee() * 1.0),3)), 10000+""));
        newOrderFee.setPreOilScale(newOrderFee.getTotalFee() == 0 ? 0L
                : OrderUtil.mul(String.valueOf(CommonUtil.getDoubleFormat((orderFee.getPreOilFee()  * 1.0/ newOrderFee.getTotalFee() * 1.0),3)), 10000+""));


        // 订单费用扩展表 ,原来的订单的成本的费用做为订单的收入费用
        newOrderFeeExt.setPontagePer(orderFeeExt.getPontagePer());
        newOrderFeeExt.setOilPrice(orderFeeExt.getOilPrice());
        newOrderFeeExt.setPreOilAuditSts(0);
        newOrderFeeExt.setPreEtcAuditSts(0);
        newOrderFeeExt.setPreTotalAuditSts(0);
        newOrderFeeExt.setTotalAuditSts(0);

        newOrderFeeExt.setIncomeCashFee(orderFee.getPreCashFee());
        newOrderFeeExt.setIncomeOilVirtualFee(orderFee.getPreOilVirtualFee());
        newOrderFeeExt.setIncomeOilFee(orderFee.getPreOilFee());
        newOrderFeeExt.setIncomeEtcFee(orderFee.getPreEtcFee());
        newOrderFeeExt.setIncomeArrivePaymentFee(orderFee.getArrivePaymentFee());
        newOrderFeeExt.setIncomeFinalFee(orderFee.getFinalFee());
        newOrderFeeExt.setIncomeInsuranceFee(orderFee.getInsuranceFee());
        newOrderFeeExt.setTenantId(orderInfo.getToTenantId());

        //调度信息就设置原始值  代收人属性剔除
        newOrderScheduler.setPlateNumber(orderScheduler.getPlateNumber());
        newOrderScheduler.setCarDriverPhone(orderScheduler.getCarDriverPhone());
        newOrderScheduler.setCarDriverMan(orderScheduler.getCarDriverMan());
        newOrderScheduler.setCarDriverId(orderScheduler.getCarDriverId());
        newOrderScheduler.setVehicleCode(orderScheduler.getVehicleCode());
        newOrderScheduler.setCarLengh(orderScheduler.getCarLengh());
        newOrderScheduler.setVehicleClass(orderScheduler.getVehicleClass());
        newOrderScheduler.setCarStatus(orderScheduler.getCarStatus());
        newOrderScheduler.setAppointWay(orderScheduler.getAppointWay());
        newOrderScheduler.setCopilotMan(orderScheduler.getCopilotMan());
        newOrderScheduler.setCopilotPhone(orderScheduler.getCopilotPhone());
        newOrderScheduler.setCopilotUserId(orderScheduler.getCopilotUserId());
        newOrderScheduler.setDispatcherName(orderScheduler.getDispatcherName());
        newOrderScheduler.setDispatcherId(orderScheduler.getDispatcherId());
        newOrderScheduler.setTrailerPlate(orderScheduler.getTrailerPlate());
        newOrderScheduler.setTrailerId(orderScheduler.getTrailerId());
        newOrderScheduler.setDispatcherBill(orderScheduler.getDispatcherBill());
        newOrderScheduler.setDistance(orderScheduler.getDistance());
        newOrderScheduler.setIsUrgent(orderScheduler.getIsUrgent());
        newOrderScheduler.setDependTime(orderScheduler.getDependTime());
        newOrderScheduler.setArriveTime(orderScheduler.getArriveTime());
        newOrderScheduler.setIsCollection(OrderConsts.IS_COLLECTION.NO);
        newOrderScheduler.setTenantId(orderInfo.getToTenantId());
        newOrderScheduler.setLicenceType(orderScheduler.getLicenceType());
        newOrderScheduler.setBillReceiverMobile(orderScheduler.getBillReceiverMobile());
        newOrderScheduler.setBillReceiverName(orderScheduler.getBillReceiverName());
        newOrderScheduler.setBillReceiverUserId(orderScheduler.getBillReceiverUserId());

        newOrderScheduler.setReciveAddr(orderGoods.getReciveAddr());
        //成本由上层订单的成本期限覆盖
        OrderPaymentDaysInfo costPayment =  new OrderPaymentDaysInfo();
        OrderPaymentDaysInfo incomePayment =  new OrderPaymentDaysInfo();
        BeanUtils.copyProperties( costPaymentDaysInfo,costPayment);
        costPayment.setTenantId(orderInfo.getToTenantId());
        costPayment.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.COST);
        costPayment.setId(null);
        costPayment.setCreateTime(LocalDateTime.now());
        costPayment.setOpDate(LocalDateTime.now());
        costPayment.setOpId(-1L);
        BeanUtils.copyProperties(incomePayment, costPaymentDaysInfo);
        incomePayment.setTenantId(orderInfo.getToTenantId());
        incomePayment.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        incomePayment.setId(null);
        incomePayment.setCreateTime(LocalDateTime.now());
        incomePayment.setOpDate(LocalDateTime.now());
        incomePayment.setOpId(-1L);

        //继承来源订单的经停点

        List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());

        Long orderId = orderInfoService.saveOrUpdateOrderInfo(newOrderInfo, newOrderFee, newOrderGoods, newOrderInfoExt,
                newOrderFeeExt, newOrderScheduler, null,costPayment,incomePayment,
                null,transitLineInfos, false,accessToken,user);
        return orderId;
    }

    public OrderTransferInfo getOrderTransferInfo(Long orderId, Long tenantId, Integer... transferOrderState) {
        LambdaQueryWrapper<OrderTransferInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderTransferInfo::getOrderId, orderId).eq(OrderTransferInfo::getAcceptTenantId, tenantId);
        if (transferOrderState.length > 0) {
            wrapper.in(OrderTransferInfo::getTransferOrderState, Arrays.asList(transferOrderState));
        }
        wrapper.last("LIMIT 1");
        return getOne(wrapper);
    }



    /**
     * 设置订单里面的客户信息
     *
     * @param sysTenantDef
     * @param orderGoods
     */
    private void setCustomerInfo(SysTenantDef sysTenantDef, OrderGoods orderGoods) {
        if (sysTenantDef == null) {
            throw new BusinessException("订单的车队信息为空");
        }

        if (orderGoods == null) {
            throw new BusinessException("订单的货品信息为空");
        }

        orderGoods.setCompanyName(sysTenantDef.getName());
        orderGoods.setCustomName(sysTenantDef.getShortName());

        // orderGoods.setLinkName(orderGoods.getLocalUserName());
        // orderGoods.setLinkPhone(orderGoods.getLocalPhone());

        String address = StringUtils.isNotEmpty(sysTenantDef.getProvinceName()) ? sysTenantDef.getProvinceName() : "";
        if (StringUtils.isNotEmpty(sysTenantDef.getCityName())) {
            address = address + sysTenantDef.getCityName();
        }
        if (StringUtils.isNotEmpty(sysTenantDef.getDistrictName())) {
            address = address + sysTenantDef.getDistrictName();
        }
        if (StringUtils.isNotEmpty(sysTenantDef.getAddress())) {
            address = address + sysTenantDef.getAddress();
        }

        orderGoods.setAddress(address);
    }
    //日期类型转换
    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }
    //日期类型转换
    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    @Override
    public Page<OrderTransferInfoDto> queryOrderTransferInfoList(OrderTransferInfoDto orderTransferInfoDto, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        orderTransferInfoDto.setTransferOrderStates(Arrays.asList(orderTransferInfoDto.getTransferOrderState().split(",")));
        Page<OrderTransferInfoDto> retPage=null;
        try {
            retPage = orderTransferInfoMapper.queryOrderTransferInfoList(new Page<>(pageNum, pageSize), orderTransferInfoDto, loginInfo.getTenantId());
        }catch (Exception e){
            e.printStackTrace();
        }
        List<OrderTransferInfoDto> retList = new ArrayList<OrderTransferInfoDto>();
        List<OrderTransferInfoDto> list = retPage.getRecords();
        for (OrderTransferInfoDto elm : list) {
            Long orderId = elm.getOrderId();
            List<OrderTransitLineInfo> transitLineInfos = null;
            if (orderId != null && orderId > 0) {
                transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            }
            elm.setTransitLineInfos(transitLineInfos);
            if (elm.getSourceRegion() != null) {
                elm.setSourceRegionName(readisUtil.getSysStaticData("SYS_CITY", String.valueOf(elm.getSourceRegion())).getCodeName());
            }
            if (elm.getDesRegion() != null) {
                elm.setDesRegionName(readisUtil.getSysStaticData("SYS_CITY", String.valueOf(elm.getDesRegion())).getCodeName());
            }
            SysStaticData staticData = readisUtil.getSysStaticData("TRANSFER_ORDER_STATE", elm.getTransferOrderState() + "");
            if (staticData != null) {
                elm.setTransferTenantName(staticData.getCodeName());
            } else {
                elm.setTransferTenantName("");
            }
            //订单来源
            elm.setTenantName(sysTenantDefService.getSysTenantDef(elm.getTenantId()).getName());
            retList.add(elm);
        }
        retPage.setRecords(retList);
        return retPage;
    }
}
