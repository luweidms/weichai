package com.youming.youche.order.provider.service.order;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IEtcMaintainService;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderMainReportService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.IOrderVehicleTimeNodeService;
import com.youming.youche.order.api.order.other.IOrderSync56KService;
import com.youming.youche.order.common.GeoCoordinate;
import com.youming.youche.order.common.GpsUtil;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.EtcMaintain;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderMainReport;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.domain.order.OrderVehicleTimeNode;
import com.youming.youche.order.dto.CheckConflictDto;
import com.youming.youche.order.dto.OrderCostRetrographyDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.monitor.OrderLineDto;
import com.youming.youche.order.dto.orderSchedulerDto;
import com.youming.youche.order.provider.mapper.OrderDriverSubsidyMapper;
import com.youming.youche.order.provider.mapper.order.OrderGoodsHMapper;
import com.youming.youche.order.provider.mapper.order.OrderGoodsMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerHMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.mapper.order.OrderTransitLineInfoHMapper;
import com.youming.youche.order.provider.mapper.order.OrderTransitLineInfoMapper;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.vo.CheckConflictVo;
import com.youming.youche.order.vo.CheckLineIsOkByPlateNumberVo;
import com.youming.youche.order.vo.OrderVerifyInfoOut;
import com.youming.youche.order.vo.QueryOrderLineNodeListVo;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.youming.youche.order.constant.BaseConstant.ESTIMATE_DEPARTURE_TIME;


/**
 * <p>
 * 订单调度表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
@Service
public class OrderSchedulerServiceImpl extends BaseServiceImpl<OrderSchedulerMapper, OrderScheduler> implements IOrderSchedulerService {
    @Resource
    private IOrderInfoHService orderInfoHService;
    @Resource
    IEtcMaintainService iEtcMaintainService;
    @Lazy
    @Autowired
    private IOrderInfoService orderInfoService;
    @Autowired
    private IOrderPaymentDaysInfoService orderPaymentDaysInfoService;
    @Autowired
    private IOrderInfoExtService orderInfoExtService;
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @Autowired
    private IOrderSchedulerHService orderSchedulerHService;
    @Autowired
    private IOrderTransitLineInfoHService orderTransitLineInfoHService;
    @Autowired
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Resource
    private IOrderInfoExtHService orderInfoExtHService;
    @Resource
    private IOrderCostReportService orderCostReportService;
    @Resource
    private IOrderMainReportService orderMainReportService;
    @Resource
    private IBillPlatformService billPlatformService;
    @Resource
    private OrderDateUtil orderDateUtil;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;
    @Resource
    private IOrderSchedulerService orderSchedulerService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderInfoHMapper orderInfoHMapper;
    @Resource
    private OrderLimitMapper orderLimitMapper;


    @Resource
    private OrderTransitLineInfoMapper orderTransitLineInfoMapper;

    @Resource
    private OrderTransitLineInfoHMapper orderTransitLineInfoHMapper;

    @Resource
    OrderDriverSubsidyMapper orderDriverSubsidyMapper;

    @Resource
    IOrderFeeService iOrderFeeService;

    @Resource
    IOrderSync56KService iOrderSync56KService;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IOrderDriverSwitchInfoService orderDriverSwitchInfoService;

    @Resource
    IOrderLimitService iOrderLimitService;

    @Resource
    private IOrderGoodsHService orderGoodsHService;

    @Resource
    OrderSchedulerHMapper orderSchedulerHMapper;

    @Resource
    OrderGoodsHMapper orderGoodsHMapper;

    @Resource
    OrderGoodsMapper orderGoodsMapper;

    @Resource
    IOrderVehicleTimeNodeService iOrderVehicleTimeNodeService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @Override
    public OrderScheduler getOrderScheduler(Long orderId) {
        LambdaQueryWrapper<OrderScheduler> lambda = new QueryWrapper<OrderScheduler>().lambda();
        lambda.eq(OrderScheduler::getOrderId, orderId);
        List<OrderScheduler> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return new OrderScheduler();
        }
    }

    @Override
    public void sendOrderSms(String tenantName, Long orderId, String plateNumber, String carDriverPhone, String carDriverName, String sourceCityName, String desCityName, LocalDateTime dependTime,String token) throws Exception{
        long template = EnumConsts.SmsTemplate.ORDER_REASSIGN;
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("tenantName", tenantName);
        paraMap.put("orderId", orderId);
        paraMap.put("plateNumber", plateNumber);
        //判断是否是该司机的首单
        Map<String, Object> list = redisUtil.hmget("FIRST_ORDER_CARDRIVER", "DRIVER_" + carDriverPhone);
        if (list == null || list.get("DRIVER_" + carDriverPhone) == null) {
            template = EnumConsts.SmsTemplate.FIRST_ORDER_REASSIGN;
            paraMap.put("carDriverName", carDriverName);
            paraMap.put("sourceCityName", sourceCityName);
            paraMap.put("desCityName", desCityName);
            paraMap.put("dependTime", DateUtil.formatDate(DateUtil.asDate(dependTime), "MM-dd HH:mm"));
        } else if (list.get("DRIVER_" + carDriverPhone).equals(String.valueOf(orderId))) {
            return;
        }

        sysSmsSendService.sendSms(carDriverPhone, template, SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                SysStaticDataEnum.OBJ_TYPE.ORDER_REASSIGN, String.valueOf(orderId), paraMap,token);
        redisUtil.hset("FIRST_ORDER_CARDRIVER", "DRIVER_" + carDriverPhone, orderId.toString());
    }

    @Override
    public OrderScheduler selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderScheduler> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderScheduler::getOrderId, orderId);
        return getOne(wrapper, false);
    }

    @Override
    public OrderScheduler getPreOrderSchedulerByUserId(Long userId, LocalDateTime dependTime,
                                                       Long tenandId, Long orderId,
                                                       Boolean isLastOrder) {
        if (dependTime == null) {
            throw new BusinessException("传入的靠台时间为空");
        }

        if (userId == null) {
            throw new BusinessException("传入的用户为空");
        }
        if (tenandId == null) {
            throw new BusinessException("传入的车队为空");
        }

        OrderScheduler orderScheduler = null;

        //主驾
        List returnList = this.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder,
                dependTime, tenandId, 1, false);
//        if(returnList!=null && returnList.size()==1){
//            orderScheduler= (OrderScheduler)returnList.get(0);
//            session.evict(orderScheduler);
//        }

        //副驾
        returnList = this.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder, dependTime, tenandId,
                2, false);
        if (returnList != null && returnList.size() == 1) {
            OrderSchedulerH orderScheduler1 = (OrderSchedulerH) returnList.get(0);
            //     session.evict(orderScheduler1);
            if (orderScheduler == null) {
                orderScheduler = new OrderScheduler();
                org.springframework.beans.BeanUtils.copyProperties(orderScheduler1, orderScheduler);
            } else {
                if (orderScheduler.getDependTime().isBefore(orderScheduler1.getDependTime())) {
                    org.springframework.beans.BeanUtils.copyProperties(orderScheduler1, orderScheduler);
                }
            }
        }

        //切换司机
        returnList = this.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder, dependTime, tenandId, 3, false);
        if (returnList != null && returnList.size() == 1) {
            OrderScheduler orderScheduler1 = (OrderScheduler) returnList.get(0);
            //     session.evict(orderScheduler1);
            if (orderScheduler == null) {
                orderScheduler = orderScheduler1;
            } else {
                if (orderScheduler.getDependTime().isBefore(orderScheduler1.getDependTime())) {
                    orderScheduler = orderScheduler1;
                }
            }
        }

        return orderScheduler;
    }

    /**
     * 查询司机id最近的订单的调度信息
     *
     * @param userId      司机Id
     * @param orderId     过滤订单号
     * @param isLastOrder 是否上一单
     * @param dependTime  靠台时间
     * @param tenantId    车队Id
     * @param selectType  查询类型 1:主驾 2:副驾 3:切换司机
     * @param isHis       是否历史
     * @return
     * @throws Exception
     */
    public List getPreOrderSchedulerByUserId(Long userId, Long orderId,
                                             Boolean isLastOrder, LocalDateTime dependTime,
                                             Long tenantId, Integer selectType, Boolean isHis) {
        List<OrderSchedulerH> preOrderSchedulerByUserId = orderSchedulerMapper.getPreOrderSchedulerByUserId(userId, orderId, isLastOrder,
                dependTime, tenantId, selectType, isHis);
        return preOrderSchedulerByUserId;
    }

    @Override
    public LocalDateTime getOrderReceivableDate(OrderScheduler orderScheduler, OrderInfo orderInfo, OrderPaymentDaysInfo info, LocalDateTime reciveDate, LoginInfo user) {
        if (orderScheduler == null || orderScheduler.getOrderId() == null || orderScheduler.getOrderId() <= 0) {
            throw new BusinessException("未找到调度信息！");
        }
        LocalDateTime receivableDate = null;
        if (info.getBalanceType() != null) {
            if (info.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                receivableDate = orderScheduler.getDependTime();//月结使用指定靠台时间
            } else {
                //其他模式使用回单时间
                if (reciveDate == null) {
                    //离场时间+回单期限+尾款期限
                    if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                        //在线接单 发单方回单审核时间+尾款期限
                        OrderInfoH infoH = orderInfoHService.getOrderH(orderInfo.getFromOrderId());
                        if (infoH != null) {
                            if (infoH.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
                                //撤销单使用本单时间
                                receivableDate = this.queryOrderTrackDate(orderScheduler.getOrderId(), OrderConsts.TRACK_TYPE.TYPE4, user);
                            } else {
                                //发单方回单时间
                                receivableDate = infoH.getEndDate();
                            }
                        } else {
                            receivableDate = this.queryOrderTrackDate(orderInfo.getFromOrderId(), OrderConsts.TRACK_TYPE.TYPE4, user);
                            if (receivableDate == null) {
                                throw new BusinessException("来源订单号[" + orderInfo.getFromOrderId() + "]计算离场时间错误！");
                            }
                        }
                    } else {
                        receivableDate = this.queryOrderTrackDate(orderScheduler.getOrderId(), OrderConsts.TRACK_TYPE.TYPE4, user);
                    }
                    if (receivableDate == null) {
                        throw new BusinessException("订单号[" + orderScheduler.getOrderId() + "]计算离场时间错误！");
                    }
                    Integer reciveTime = orderPaymentDaysInfoService.calculatePaymentDays(info, orderScheduler.getDependTime(), OrderConsts.CALCULATE_TYPE.RECIVE);
                    if (reciveTime != null) {
                        receivableDate = receivableDate.plusDays(reciveTime);
                    }
                } else {
                    if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                        //在线接单 发单方回单审核时间+尾款期限
                        OrderInfoH infoH = orderInfoHService.getOrderH(orderInfo.getFromOrderId());
                        if (infoH != null) {
                            if (infoH.getOrderState() == OrderConsts.ORDER_STATE.CANCELLED) {
                                //撤销单使用本单时间
                                receivableDate = reciveDate;
                            } else {
                                //发单方回单时间
                                receivableDate = infoH.getEndDate();
                            }
                        } else {
                            receivableDate = this.queryOrderTrackDate(orderInfo.getFromOrderId(), OrderConsts.TRACK_TYPE.TYPE4, user);
                            if (receivableDate == null) {
                                throw new BusinessException("来源订单号[" + orderInfo.getFromOrderId() + "]计算离场时间错误！");
                            }
                        }
                    } else {
                        receivableDate = reciveDate;//本单时间回单时间
                    }
                }
            }
        } else {
            return null;
        }
        Integer collectionTime = orderPaymentDaysInfoService.calculatePaymentDays(info, orderScheduler.getDependTime(), OrderConsts.CALCULATE_TYPE.COLLECTION);
        if (collectionTime != null) {
            receivableDate = receivableDate.plusDays(collectionTime);
            if (info.getBalanceType() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
                Date date = null;
                try {
                    date = DateUtil.formatStringToDate(DateUtil.formatDate(DateUtil.asDate(receivableDate), "yyyy-MM-" + (info.getCollectionDay() > 9 ? info.getCollectionDay() : "0" + info.getCollectionDay()) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                receivableDate = LocalDateTimeUtil.of(date);
            }
        }
        return receivableDate;
    }


    @Override
    public OrderInfoDto queryOrderTrackType(Long orderId, List<SysStaticData> sysStaticDataList) {
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);

        if (orderInfo != null) {
            if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {//待装货
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                map.put("msg", "确认车辆已到达装货点（" + orderInfo.getSourceRegion() + "）？");
//
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                if (orderInfo.getSourceRegion() != null) {
                    orderInfoDto.setTrackMas("确认车辆已到达装货点（" + readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getSourceRegion().toString()) + "）？");
                }
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.LOADING) {//装货中
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
//                map.put("msg", "确认车辆已完成装货 ？");
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE2);
                orderInfoDto.setTrackMas("确认车辆已完成装货");
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TRANSPORT_ING) {//运输中
                QueryWrapper<OrderTransitLineInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id", orderId);
                List<OrderTransitLineInfo> list = orderTransitLineInfoMapper.selectList(queryWrapper);
                if (list != null && list.size() > 0) {
                    boolean isArrive = true;
                    for (int i = list.size() - 1; i >= 0; i--) {
                        OrderTransitLineInfo transitLineInfo = list.get(i);
                        if (transitLineInfo.getCarDependDate() != null && transitLineInfo.getCarStartDate() == null) {
//                            map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
//                            map.put("msg", "确认车辆已完成装货 ？");
                            orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE2);
                            orderInfoDto.setTrackMas("确认车辆已完成装货");
                            isArrive = false;
                            break;
                        } else if (transitLineInfo.getCarDependDate() == null) {
                            if (i == 0) {
//                                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                                map.put("msg", "确认车辆已到达经停点" + transitLineInfo.getSortId() + "（" + transitLineInfo.getRegion() + ")");
                                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                                if (transitLineInfo.getRegion() != null) {
                                    orderInfoDto.setTrackMas("确认车辆已完成装货" + transitLineInfo.getSortId() + "(" + readisUtil.getCodeNameStr(sysStaticDataList, transitLineInfo.getRegion().toString()) + ")");
                                }
                                isArrive = false;
                                break;
                            } else {
                                OrderTransitLineInfo lastTransitLineInfo = list.get(i - 1);
                                if (lastTransitLineInfo.getCarDependDate() != null && lastTransitLineInfo.getCarStartDate() != null) {
//                                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                                    map.put("msg", "确认车辆已到达经停点" + transitLineInfo.getSortId() + "（" + transitLineInfo.getRegion() + ")");
                                    orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                                    if (transitLineInfo.getRegion() != null) {
                                        orderInfoDto.setTrackMas("确认车辆已到达经停点" + transitLineInfo.getSortId() + "(" + readisUtil.getCodeNameStr(sysStaticDataList, transitLineInfo.getRegion().toString()) + ")");
                                    }
                                    isArrive = false;
                                    break;
                                }
                            }
                        } else if (i == list.size() - 1) {
                            break;
                        }
                    }
                    if (isArrive) {//经停点已完成
//                        map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
//                        map.put("msg", "确认车辆已到达卸货点（" + orderInfo.getDesRegion() + "）？");

                        orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE3);
                        if (orderInfo.getDesRegion() != null) {
                            orderInfoDto.setTrackMas("确认车辆已到达卸货点" + "(" + readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getDesRegion().toString()) + ")");
                        }

                    }
                } else {
//                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
//                    map.put("msg", "确认车辆已到达卸货点（" + orderInfo.getDesRegion() + "）？");

                    orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE3);
                    if (orderInfo.getDesRegion() != null) {
                        orderInfoDto.setTrackMas("确认车辆已到达卸货点" + "(" + readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getDesRegion().toString()) + ")");
                    }
                }
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.ARRIVED) {//已到达
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE4);
//                map.put("msg", "确认车辆已完成卸货 ？");
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE4);
                orderInfoDto.setTrackMas("确认车辆已完成卸货？");
            }

            return orderInfoDto;
        } else {//历史单不需轨迹操作
            return new OrderInfoDto();
        }
    }

    /**
     * 获取订单轨迹状态
     * 聂杰伟
     *
     * @param orderId 订单号
     * @return
     */
    @Override
    public OrderInfoDto queryOrderTrackType(Long orderId) {
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);

        if (orderInfo != null) {
            if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {//待装货
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                map.put("msg", "确认车辆已到达装货点（" + orderInfo.getSourceRegion() + "）？");
//
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                if (orderInfo.getSourceRegion() != null) {
                    orderInfoDto.setTrackMas("确认车辆已到达装货点（" + readisUtil.getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName() + "）？");
                }
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.LOADING) {//装货中
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
//                map.put("msg", "确认车辆已完成装货 ？");
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE2);
                orderInfoDto.setTrackMas("确认车辆已完成装货");
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TRANSPORT_ING) {//运输中
                QueryWrapper<OrderTransitLineInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("order_id", orderId);
                List<OrderTransitLineInfo> list = orderTransitLineInfoMapper.selectList(queryWrapper);
                if (list != null && list.size() > 0) {
                    boolean isArrive = true;
                    for (int i = list.size() - 1; i >= 0; i--) {
                        OrderTransitLineInfo transitLineInfo = list.get(i);
                        if (transitLineInfo.getCarDependDate() != null && transitLineInfo.getCarStartDate() == null) {
//                            map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
//                            map.put("msg", "确认车辆已完成装货 ？");
                            orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE2);
                            orderInfoDto.setTrackMas("确认车辆已完成装货");
                            isArrive = false;
                            break;
                        } else if (transitLineInfo.getCarDependDate() == null) {
                            if (i == 0) {
//                                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                                map.put("msg", "确认车辆已到达经停点" + transitLineInfo.getSortId() + "（" + transitLineInfo.getRegion() + ")");
                                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                                if (transitLineInfo.getRegion() != null) {
                                    orderInfoDto.setTrackMas("确认车辆已完成装货" + transitLineInfo.getSortId() + "(" + readisUtil.getSysStaticData("SYS_CITY", transitLineInfo.getRegion().toString()).getCodeName() + ")");
                                }
                                isArrive = false;
                                break;
                            } else {
                                OrderTransitLineInfo lastTransitLineInfo = list.get(i - 1);
                                if (lastTransitLineInfo.getCarDependDate() != null && lastTransitLineInfo.getCarStartDate() != null) {
//                                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
//                                    map.put("msg", "确认车辆已到达经停点" + transitLineInfo.getSortId() + "（" + transitLineInfo.getRegion() + ")");
                                    orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE1);
                                    if (transitLineInfo.getRegion() != null) {
                                        orderInfoDto.setTrackMas("确认车辆已到达经停点" + transitLineInfo.getSortId() + "(" + readisUtil.getSysStaticData("SYS_CITY", transitLineInfo.getRegion().toString()).getCodeName() + ")");
                                    }
                                    isArrive = false;
                                    break;
                                }
                            }
                        } else if (i == list.size() - 1) {
                            break;
                        }
                    }
                    if (isArrive) {//经停点已完成
//                        map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
//                        map.put("msg", "确认车辆已到达卸货点（" + orderInfo.getDesRegion() + "）？");

                        orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE3);
                        if (orderInfo.getDesRegion() != null) {
                            orderInfoDto.setTrackMas("确认车辆已到达卸货点" + "(" + readisUtil.getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName() + ")");
                        }

                    }
                } else {
//                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
//                    map.put("msg", "确认车辆已到达卸货点（" + orderInfo.getDesRegion() + "）？");

                    orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE3);
                    if (orderInfo.getDesRegion() != null) {
                        orderInfoDto.setTrackMas("确认车辆已到达卸货点" + "(" + readisUtil.getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName() + ")");
                    }
                }
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.ARRIVED) {//已到达
//                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE4);
//                map.put("msg", "确认车辆已完成卸货 ？");
                orderInfoDto.setTrackType(OrderConsts.TRACK_TYPE.TYPE4);
                orderInfoDto.setTrackMas("确认车辆已完成卸货？");
            }

            return orderInfoDto;
        } else {//历史单不需轨迹操作
            return new OrderInfoDto();
        }
    }

    @Override
    public OrderInfoDto queryOrderLineString(Long orderId) {
        List<SysStaticData> sysStaticDataList = readisUtil.getSysStaticDataList("SYS_CITY");
        return queryOrderLineString(orderId, sysStaticDataList);
    }

    /**
     * 查询订单线路详情
     *
     * @param orderId
     * @param sysStaticDataList
     * @return orderLine 总线路
     * isTransitLine 是否有经停线路
     */
    @Override
    public OrderInfoDto queryOrderLineString(Long orderId, List<SysStaticData> sysStaticDataList) {
        OrderInfoDto orderInfoDto = new OrderInfoDto();
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);

        String sourceRegionName = "";
        String desRegionName = "";
        List<OrderTransitLineInfo> transitLineInfos = null;
        if (orderInfo == null) {
            QueryWrapper<OrderInfoH> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId);
            OrderInfoH orderInfoH = orderInfoHMapper.selectOne(queryWrapper);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            if (orderInfoH.getSourceRegion() != null) {
                sourceRegionName = readisUtil.getCodeNameStr(sysStaticDataList, orderInfoH.getSourceRegion().toString());
            }
            if (orderInfoH.getDesRegion() != null) {
                desRegionName = readisUtil.getCodeNameStr(sysStaticDataList, orderInfoH.getDesRegion().toString());
            }
            QueryWrapper<OrderTransitLineInfoH> infoHQueryWrapper = new QueryWrapper<>();
            infoHQueryWrapper.eq("order_id", orderId);
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHMapper.selectList(infoHQueryWrapper);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                transitLineInfos = new ArrayList<>();
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    org.springframework.beans.BeanUtils.copyProperties(orderTransitLineInfoH, transitLineInfo);
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            if (orderInfo.getSourceRegion() != null) {
                sourceRegionName = readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getSourceRegion().toString());
            }
            if (orderInfo.getDesRegion() != null) {
                desRegionName = readisUtil.getCodeNameStr(sysStaticDataList, orderInfo.getDesRegion().toString());
            }
            QueryWrapper<OrderTransitLineInfo> lineInfoQueryWrapper = new QueryWrapper<>();
            lineInfoQueryWrapper.eq("order_id", orderId);
            transitLineInfos = orderTransitLineInfoMapper.selectList(lineInfoQueryWrapper);

        }
        String orderLine = sourceRegionName + " -> ";
        Map<String, Object> mapOut = new ConcurrentHashMap<String, Object>();
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (int i = 0; i < transitLineInfos.size(); i++) {
                OrderTransitLineInfo transitLineInfo = transitLineInfos.get(i);
                //TODO 到达市
                String codeName = "";
                if (transitLineInfo != null) {
                    if (transitLineInfo.getRegion() != null) {
                        codeName = readisUtil.getCodeNameStr(sysStaticDataList, transitLineInfo.getRegion().toString());
                    }
                }
                orderLine += codeName + " -> ";
            }
            orderLine += desRegionName;
//            mapOut.put("orderLine", orderLine);
//            mapOut.put("isTransitLine", true);
            orderInfoDto.setOrderLine(orderLine);
            orderInfoDto.setIsTransitLine(true);
            return orderInfoDto;
        } else {
            orderLine += desRegionName;
//            mapOut.put("orderLine", orderLine);
            orderInfoDto.setOrderLine(orderLine);
            orderInfoDto.setIsTransitLine(false);
            return orderInfoDto;
        }
    }

    @Override
    public void checkBillDependTime(OrderInfo orderInfo, OrderScheduler orderScheduler, long billMethod, LoginInfo user) {

        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            Object filtrationTenantObj = readisUtil.getSysCfg("TERRACE_BILL_TIME_LIMIT_FILTRATION_TENANT", "0").getCfgValue();
            if (filtrationTenantObj != null) {
                String[] filtrationTenantArr = String.valueOf(filtrationTenantObj).split(",");
                for (String tenant : filtrationTenantArr) {
                    if (tenant.equals(orderInfo.getTenantId().toString())) {
                        return;
                    }
                }
            }
            BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(billMethod);
            if (billPlatform == null) {
                throw new BusinessException("未找到对应票票据平台！");
            }

            if (billPlatform.getMaxReinputOrderTime() != null && billPlatform.getMaxReinputOrderTime().doubleValue() > 0) {
                LocalDateTime dependTime = orderScheduler.getDependTime();
                LocalDateTime dependTimeMins = OrderDateUtil.subHourAndMins(orderScheduler.getCreateTime(), billPlatform.getMaxReinputOrderTime().floatValue());
                if (dependTime.isBefore(dependTimeMins)) {
                    throw new BusinessException("平台开票订单要求靠台时间不能早于" + billPlatform.getMaxReinputOrderTime().floatValue() + "小时前！");
                }
            }
        }

    }

    @Override
    public List<Map> queryOrderDriverSubsidyDay(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        boolean isHis = false;
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            isHis = true;
        }
        List<Map> driverSubsidyDay = orderDriverSubsidyMapper.queryOrderDriverSubsidyDay(orderId, isHis);
        return driverSubsidyDay;
    }

    @Override
    public void orderLoadingVerify(OrderInfo orderInfo, OrderScheduler orderScheduler, boolean isFromTenant) {

        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            OrderVerifyInfoOut transitCarOrder = null;
            OrderVerifyInfoOut waitCarOrder = null;
            String tenantMsg = "";
            if (isFromTenant) {
                tenantMsg = "由于来源车队需要开平台票，";
            }
            OrderInfoDto orderInfoDto = queryOrderTrackType(orderInfo.getOrderId());
            Integer trackType = orderInfoDto.getTrackType() < 0 ? 0 : orderInfoDto.getTrackType();
            //靠台到达限制 离台不做限制
            if (trackType.intValue() == OrderConsts.TRACK_TYPE.TYPE1 || trackType.intValue() == OrderConsts.TRACK_TYPE.TYPE3) {
                String des = trackType.intValue() == OrderConsts.TRACK_TYPE.TYPE1 ? "靠台" : "到达";

                if (StringUtils.isNotBlank(orderScheduler.getPlateNumber())) {
                    transitCarOrder = queryOrderVerifyInfoOut(orderScheduler.getPlateNumber(), orderInfo.getOrderId(), getDateByLocalDateTime(orderScheduler.getDependTime()), OrderConsts.ORDER_STATE.ARRIVED, null, true);
                    waitCarOrder = queryOrderVerifyInfoOut(orderScheduler.getPlateNumber(), orderInfo.getOrderId(), getDateByLocalDateTime(orderScheduler.getDependTime()), OrderConsts.ORDER_STATE.LOADING, null, true);
                }
                if (waitCarOrder != null) {
                    throw new BusinessException(tenantMsg + "该车辆还有更早靠台的订单没有靠台，不能操作本单" + des + "。");
                }
                if (transitCarOrder != null) {
                    throw new BusinessException(tenantMsg + "该车辆还有未到达的订单，不能操作本单" + des + "。");
                }

                OrderVerifyInfoOut transitDriverOrder = null;
                OrderVerifyInfoOut waitDriverOrder = null;
                if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
                    transitDriverOrder = queryOrderVerifyInfoOut(null, orderInfo.getOrderId(), getDateByLocalDateTime(orderScheduler.getDependTime()), OrderConsts.ORDER_STATE.ARRIVED, orderScheduler.getCarDriverId(), true);
                    waitDriverOrder = queryOrderVerifyInfoOut(null, orderInfo.getOrderId(), getDateByLocalDateTime(orderScheduler.getDependTime()), OrderConsts.ORDER_STATE.LOADING, orderScheduler.getCarDriverId(), true);
                }
                if (waitDriverOrder != null) {
                    throw new BusinessException(tenantMsg + "该主驾还有更早靠台的订单没有靠台，不能操作本单" + des + "。");
                }
                if (transitDriverOrder != null) {
                    throw new BusinessException(tenantMsg + "该主驾还有未到达的订单，不能操作本单" + des + "。");
                }
            }
        } else {
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                OrderInfo fromOrderInfo = orderInfoService.getOrder(orderInfo.getFromOrderId());
                if (fromOrderInfo != null && fromOrderInfo.getIsNeedBill() != null && fromOrderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    OrderScheduler fromOrderScheduler = getOrderScheduler(orderInfo.getFromOrderId());
                    this.orderLoadingVerify(fromOrderInfo, fromOrderScheduler, true);
                }
            }
        }
    }

    @Override
    public void verifyTrackNode(OrderScheduler orderScheduler, OrderGoods orderGoods, OrderInfo orderInfo, List<OrderTransitLineInfo> transitLineInfos, String nandDes, String eandDes, Date opDate, Integer transitSortId, boolean isFromTenant, String vehicleAffiliation, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(Long.valueOf(vehicleAffiliation));
            if (billPlatform == null) {
                throw new BusinessException("未找到对应票票据平台！");
            }
            if (billPlatform.getTravelSpeed() != null && billPlatform.getTravelSpeed().doubleValue() > 0) {
                double speedStandard = billPlatform.getTravelSpeed().doubleValue();
//                Double estStartTime = Double.parseDouble(String.valueOf(getCfgVal( "ESTIMATE_START_TIME", 0, String.class,loginInfo)));
                Double estStartTime = 0.0;
                String nand = orderGoods.getNand();
                String eand = orderGoods.getEand();
                String tenantMsg = "";
                if (isFromTenant) {
                    tenantMsg = "由于来源车队需要开平台票，";
                }
                Date carStartDate = getLocalDateTimeToDate(orderScheduler.getCarStartDate() == null ? orderDateUtil
                        .addHourAndMins(orderScheduler.getDependTime(), estStartTime.floatValue()) : orderScheduler.getCarStartDate());
                if (transitLineInfos != null && transitLineInfos.size() > 0) {
                    for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                        if (transitSortId != null && transitSortId.intValue() == orderTransitLineInfo.getSortId()) {
                            break;
                        }
                        if (orderTransitLineInfo.getCarDependDate() == null) {
                            carStartDate = getLocalDateTimeToDate(orderDateUtil.addHourAndMins(orderDateUtil.
                                            addHourAndMins(getDateToLocalDateTime(carStartDate), orderTransitLineInfo.getArriveTime()),
                                    estStartTime.floatValue()));
                        } else {
                            if (orderTransitLineInfo.getCarStartDate() == null) {
                                carStartDate = getLocalDateTimeToDate(orderDateUtil.addHourAndMins(orderTransitLineInfo.
                                        getCarDependDate(), estStartTime.floatValue()));
                            } else {
                                carStartDate = getLocalDateTimeToDate(orderTransitLineInfo.getCarStartDate());
                            }
                        }
                        nand = orderTransitLineInfo.getNand();
                        eand = orderTransitLineInfo.getEand();
                    }
                }
                Map directionMap = GpsUtil.getDirectionV2(Double.parseDouble(eand), Double.parseDouble(nand), Double.parseDouble(eandDes), Double.parseDouble(nandDes), null, 0);
                //上一到达点到该点距离
                Double distance = CommonUtils.getDoubleFormat(Double.parseDouble(DataFormat.getStringKey(directionMap, "distance")) / 1000.0, 3);
                Double arriveTime = (distance / speedStandard);
                if ((opDate.getTime() - carStartDate.getTime()) < (arriveTime * 3600000)) {
                    throw new BusinessException(tenantMsg + "该订单正在运输中（预估到达时间" + DateUtil.formatDate(getLocalDateTimeToDate(orderDateUtil.addHourAndMins(getDateToLocalDateTime(carStartDate), arriveTime.floatValue())), DateUtil.DATETIME_FORMAT) + "），不能触发到达。");
                }
            }
        } else {
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                OrderInfo fromOrderInfo = orderInfoService.getOrder(orderInfo.getFromOrderId());
                if (fromOrderInfo != null && fromOrderInfo.getIsNeedBill() != null && fromOrderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    OrderScheduler fromOrderScheduler = this.getOrderScheduler(orderInfo.getFromOrderId());
                    OrderGoods fromOrderGoods = orderGoodsService.getOrderGoods(orderInfo.getFromOrderId());
                    OrderFee fromOrderFee = iOrderFeeService.getOrderFee(orderInfo.getFromOrderId());
                    this.verifyTrackNode(fromOrderScheduler, fromOrderGoods, fromOrderInfo,
                            transitLineInfos, nandDes, eandDes, opDate, transitSortId, true, fromOrderFee.getVehicleAffiliation(), accessToken);
                }
            }
        }
    }

    @Override
    public Date queryOrderTrackDate(Long orderId, Integer trackType, Long tenantId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        Double estDeparturerTime = Double.parseDouble(String.valueOf(readisUtil.getSysCfg(ESTIMATE_DEPARTURE_TIME, "0").getCfgValue()));
        if (orderInfo != null) {
            OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
            OrderInfoExt OrderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            OrderGoods OrderGoods = orderGoodsService.getOrderGoods(orderId);
            if (orderScheduler == null) {
                throw new BusinessException("未找到订单[" + orderId + "]调度" + (OrderInfoExt == null ? ",订单扩展表" : "") + (OrderGoods == null ? ",订单货物表" : "") + "信息！[demo]" + tenantId);
            }
            Date dependTime = getLocalDateTimeToDate(orderScheduler.getCarDependDate() != null ? orderScheduler.getCarDependDate() : orderScheduler.getDependTime());
            if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                if (orderScheduler.getCarArriveDate() != null) {
                    return getLocalDateTimeToDate(orderScheduler.getCarArriveDate());
                } else {
                    return getLocalDateTimeToDate(this.getOrderArriveDate(orderId, getDateToLocalDateTime(dependTime),
                            orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false));
                }
            } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                if (orderScheduler.getCarDepartureDate() != null) {
                    return getLocalDateTimeToDate(orderScheduler.getCarDepartureDate());
                } else {
                    return getLocalDateTimeToDate(orderDateUtil.addHourAndMins(this.getOrderArriveDate(orderId,
                            getDateToLocalDateTime(dependTime), orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false), estDeparturerTime.floatValue()));
                }
            }
        } else {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderInfoH.getOrderState().intValue() == OrderConsts.ORDER_STATE.CANCELLED) {
                Date dependTime = getLocalDateTimeToDate(orderSchedulerH.getCarDependDate() != null ? orderSchedulerH.getCarDependDate() : orderSchedulerH.getDependTime());
                if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                    if (orderSchedulerH.getCarArriveDate() != null) {
                        return getLocalDateTimeToDate(orderSchedulerH.getCarArriveDate());
                    } else {
                        return getLocalDateTimeToDate(this.getOrderArriveDate(orderId, getDateToLocalDateTime(dependTime), orderSchedulerH.getCarStartDate(), orderSchedulerH.getArriveTime(), false));
                    }
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                    if (orderSchedulerH.getCarDepartureDate() != null) {
                        return getLocalDateTimeToDate(orderSchedulerH.getCarDepartureDate());
                    } else {
                        return getLocalDateTimeToDate(orderDateUtil.addHourAndMins(this.getOrderArriveDate(orderId, getDateToLocalDateTime(dependTime), orderSchedulerH.getCarStartDate(), orderSchedulerH.getArriveTime(), false), estDeparturerTime.floatValue()));
                    }
                }
            } else {
                if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                    return getLocalDateTimeToDate(orderSchedulerH.getCarArriveDate());
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                    return getLocalDateTimeToDate(orderSchedulerH.getCarDepartureDate());
                }
            }
        }
        return null;
    }

    @Override
    public void compensationTrackDate(Long orderId, String accessToken) {
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo != null) {
            OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
            OrderGoods orderGoods = orderGoodsService.getOrderGoods(orderId);
            Double compensationTrackTime = Double.parseDouble(readisUtil.getSysCfg("DELAYED_COMPENSATION_TIME", "0").getCfgValue());
            List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            if (transitLineInfos != null && transitLineInfos.size() > 0) {//有经停点
                List<Double> notEmptyDateList = new ArrayList<>();
                List<Double> emptyDateList = new ArrayList<>();
                double lastNumber = transitLineInfos.size() + 1.5;
                if (orderScheduler.getCarDependDate() == null) {
                    emptyDateList.add(0.0);
                } else {
                    notEmptyDateList.add(0.0);
                }
                if (orderScheduler.getCarStartDate() == null) {
                    emptyDateList.add(0.5);
                } else {
                    notEmptyDateList.add(0.5);
                }
                for (int i = 0; i < transitLineInfos.size(); i++) {
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(i);
                    if (orderTransitLineInfo.getCarDependDate() == null) {
                        emptyDateList.add(orderTransitLineInfo.getSortId().doubleValue());
                    } else {
                        notEmptyDateList.add(orderTransitLineInfo.getSortId().doubleValue());
                    }
                    if (orderTransitLineInfo.getCarStartDate() == null) {
                        emptyDateList.add(orderTransitLineInfo.getSortId() + 0.5);
                    } else {
                        notEmptyDateList.add(orderTransitLineInfo.getSortId() + 0.5);
                    }
                }
                if (orderScheduler.getCarArriveDate() == null) {
                    emptyDateList.add(transitLineInfos.size() + 1.0);
                } else {
                    notEmptyDateList.add(transitLineInfos.size() + 1.0);
                }
                if (orderScheduler.getCarDepartureDate() == null) {
                    emptyDateList.add(lastNumber);
                } else {
                    notEmptyDateList.add(lastNumber);
                }
                if (emptyDateList != null && emptyDateList.size() > 0) {
                    if (notEmptyDateList != null && notEmptyDateList.size() > 0) {
                        for (int i = 0; i < notEmptyDateList.size(); i++) {
                            double num = notEmptyDateList.get(i);
                            if (i == 0) {//初始节点
                                if (num == 0) {//起始点靠台
                                    if (notEmptyDateList.size() > 1) {
                                        double nextNum = notEmptyDateList.get(i + 1);
                                        this.calculateSectionDate(num, nextNum, lastNumber, orderInfo, orderScheduler, transitLineInfos, compensationTrackTime, accessToken);
                                    } else {//只有靠台时间
                                        orderScheduler.setCarStartDate(orderDateUtil.addHourAndMins(orderScheduler.getCarDependDate(), compensationTrackTime.floatValue()));
                                        for (int j = 0; j < transitLineInfos.size(); j++) {
                                            OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(j);
                                            if (j == 0) {
                                                orderTransitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(orderScheduler.getCarStartDate(), orderTransitLineInfo.getArriveTime()));
                                            } else {
                                                OrderTransitLineInfo lastOrderTransitLineInfo = transitLineInfos.get(j - 1);
                                                orderTransitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(lastOrderTransitLineInfo.getCarStartDate(), orderTransitLineInfo.getArriveTime()));
                                            }
                                            orderTransitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                                            String regionName = "";
                                            String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                            if (org.apache.commons.lang.StringUtils.isNotBlank(name)) regionName = name;
                                            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                    "[系统补偿机制]已靠台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")",
                                                    accessToken, orderInfo.getOrderId());
                                            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                    "[系统补偿机制]已离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")",
                                                    accessToken, orderInfo.getOrderId());
                                            orderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                                        }
                                        OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(transitLineInfos.size() - 1);
                                        orderScheduler.setCarArriveDate(orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarStartDate(), orderScheduler.getArriveTime()));
                                        orderScheduler.setCarDepartureDate(orderDateUtil.addHourAndMins(orderScheduler.getCarArriveDate(), compensationTrackTime.floatValue()));
                                        String sourceRegionName = "";
                                        String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                                        String desRegionName = "";
                                        String desName = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(desName)) desRegionName = desName;
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已到达目的地(" + desRegionName + ")", accessToken, orderInfo.getOrderId());
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已卸货", accessToken, orderInfo.getOrderId());
                                    }
                                } else if (num == lastNumber || num == lastNumber - 0.5) {
                                    if (num == lastNumber - 0.5) {
                                        orderScheduler.setCarDepartureDate(orderDateUtil.addHourAndMins(orderScheduler.getCarArriveDate(), compensationTrackTime.floatValue()));
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已卸货", accessToken, orderInfo.getOrderId());
                                    } else {
                                        orderScheduler.setCarArriveDate(OrderDateUtil.subHourAndMins(orderScheduler.getCarDepartureDate(), compensationTrackTime.floatValue()));
                                        String desRegionName = "";
                                        String desName = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(desName)) desRegionName = desName;
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已到达目的地(" + desRegionName + ")", accessToken, orderInfo.getOrderId());
                                    }
                                    for (int j = transitLineInfos.size() - 1; j >= 0; j--) {
                                        OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(j);
                                        if (j == transitLineInfos.size() - 1) {
                                            orderTransitLineInfo.setCarStartDate(OrderDateUtil.subHourAndMins(orderScheduler.getCarArriveDate(), orderScheduler.getArriveTime()));
                                        } else {
                                            OrderTransitLineInfo lastOrderTransitLineInfo = transitLineInfos.get(j + 1);
                                            orderTransitLineInfo.setCarStartDate(OrderDateUtil.subHourAndMins(lastOrderTransitLineInfo.getCarDependDate(), orderTransitLineInfo.getArriveTime()));
                                        }
                                        orderTransitLineInfo.setCarDependDate(OrderDateUtil.subHourAndMins(orderTransitLineInfo.getCarStartDate(), compensationTrackTime.floatValue()));
                                        String regionName = "";
                                        String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(name)) regionName = name;
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已靠台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                                        orderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                                    }
                                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(0);
                                    orderScheduler.setCarStartDate(OrderDateUtil.subHourAndMins(orderTransitLineInfo.getCarDependDate(), orderTransitLineInfo.getArriveTime()));
                                    orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(orderScheduler.getCarStartDate(), compensationTrackTime.floatValue()));
                                    String sourceRegionName = "";
                                    String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                                    if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                            "[系统补偿机制]已靠台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                            "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                } else {
                                    if (num == 0.5) {
                                        String sourceRegionName = "";
                                        String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                                        orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(orderScheduler.getCarStartDate(), compensationTrackTime.floatValue()));
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已靠台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                    } else if (num >= 1 && num < lastNumber - 0.5) {
                                        if (num > 1) {
                                            for (Double j = num - 0.5; j > 1; j -= 0.5) {
                                                OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(j.intValue() - 1);
                                                if (j - j.intValue() == 0) {
                                                    String regionName = "";
                                                    String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                                    if (StringUtils.isNotBlank(name)) regionName = name;
                                                    orderTransitLineInfo.setCarDependDate(OrderDateUtil.subHourAndMins(orderTransitLineInfo.getCarStartDate(), compensationTrackTime.floatValue()));
                                                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                            "[系统补偿机制]已靠台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                                                } else {
                                                    String regionName = "";
                                                    String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                                                    if (StringUtils.isNotBlank(name)) regionName = name;
                                                    OrderTransitLineInfo lastTransitLineInfo = transitLineInfos.get(j.intValue());
                                                    orderTransitLineInfo.setCarStartDate(OrderDateUtil.subHourAndMins(lastTransitLineInfo.getCarDependDate(), lastTransitLineInfo.getArriveTime()));
                                                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update
                                                            , "[系统补偿机制]已离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                                                }
                                            }
                                        }
                                        OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(0);
                                        orderScheduler.setCarStartDate(OrderDateUtil.subHourAndMins(orderTransitLineInfo.getCarDependDate(), orderTransitLineInfo.getArriveTime()));
                                        orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(orderScheduler.getCarStartDate(), compensationTrackTime.floatValue()));
                                        String sourceRegionName = "";
                                        String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                                        if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已靠台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                                "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                                    }
                                    if (notEmptyDateList.size() > 1) {
                                        double nextNum = notEmptyDateList.get(i + 1);
                                        this.calculateSectionDate(num, nextNum, lastNumber, orderInfo, orderScheduler, transitLineInfos, compensationTrackTime, accessToken);
                                    } else {
                                        this.calculateSectionDate(num, lastNumber + 0.5, lastNumber, orderInfo, orderScheduler, transitLineInfos, compensationTrackTime, accessToken);
                                    }
                                }
                            } else {
                                if (num == lastNumber) {//目的地离台
                                    break;
                                } else {
                                    if (i == notEmptyDateList.size() - 1) {//最后节点
                                        this.calculateSectionDate(num, lastNumber + 0.5, lastNumber, orderInfo, orderScheduler, transitLineInfos, compensationTrackTime, accessToken);
                                    } else {
                                        double nextNumber = notEmptyDateList.get(i + 1);
                                        this.calculateSectionDate(num, nextNumber, lastNumber, orderInfo, orderScheduler, transitLineInfos, compensationTrackTime, accessToken);
                                    }
                                }
                            }
                        }
                    } else {//全空
                        orderScheduler.setCarDependDate(orderScheduler.getDependTime());
                        orderScheduler.setCarStartDate(orderDateUtil.addHourAndMins(orderScheduler.getCarDependDate(), compensationTrackTime.floatValue()));
                        Date carStartDate = getLocalDateTimeToDate(orderScheduler.getCarStartDate());
                        for (int i = 0; i < transitLineInfos.size(); i++) {
                            OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(i);
                            if (i == 0) {
                                orderTransitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(orderScheduler.getCarStartDate(), orderTransitLineInfo.getArriveTime()));
                            } else {
                                OrderTransitLineInfo lstOrderTransitLineInfo = transitLineInfos.get(i - 1);
                                orderTransitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(lstOrderTransitLineInfo.getCarStartDate(), orderTransitLineInfo.getArriveTime()));
                            }
                            orderTransitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                            if (i == transitLineInfos.size() - 1) {
                                carStartDate = getLocalDateTimeToDate(orderTransitLineInfo.getCarStartDate());
                            }
                            orderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                            String regionName = "";
                            String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                            if (StringUtils.isNotBlank(name)) regionName = name;
                            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                    "[系统补偿机制]已靠台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                            saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                    "[系统补偿机制]已离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                        }
                        orderScheduler.setCarArriveDate(orderDateUtil.addHourAndMins(getDateToLocalDateTime(carStartDate), orderScheduler.getArriveTime()));
                        orderScheduler.setCarDepartureDate(orderDateUtil.addHourAndMins(orderScheduler.getCarArriveDate(), compensationTrackTime.floatValue()));
                        String sourceRegionName = "";
                        String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                        if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                        String desRegionName = "";
                        String desName = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                        if (StringUtils.isNotBlank(desName)) desRegionName = desName;
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已靠台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已到达目的地(" + desRegionName + ")", accessToken, orderInfo.getOrderId());
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已卸货", accessToken, orderInfo.getOrderId());
                    }
                }
            } else {//无经停点
                Date carDependDate = null;
                Date carStartDate = null;
                Date carArriveDate = null;
                Date carDepartureDate = null;
                boolean isDepend = false;
                boolean isStartDate = false;
                boolean isArrive = false;
                boolean isDeparture = false;
                if (orderScheduler.getCarDependDate() == null) {
                    if (orderScheduler.getCarStartDate() != null) {
                        orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(
                                orderScheduler.getCarStartDate(), compensationTrackTime.floatValue()));
                    } else if (orderScheduler.getCarArriveDate() != null) {
                        orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(
                                OrderDateUtil.subHourAndMins(orderScheduler.getCarArriveDate(), orderScheduler.getArriveTime()),
                                compensationTrackTime.floatValue()));
                    } else if (orderScheduler.getCarDepartureDate() != null) {
                        orderScheduler.setCarDependDate(OrderDateUtil.subHourAndMins(
                                OrderDateUtil.subHourAndMins(OrderDateUtil.subHourAndMins(
                                                orderScheduler.getCarDepartureDate(), compensationTrackTime.floatValue())
                                        , orderScheduler.getArriveTime()),
                                compensationTrackTime.floatValue()));
                    } else {
                        orderScheduler.setCarDependDate(orderScheduler.getDependTime());
                    }
                    isDepend = true;
                }
                if (orderScheduler.getCarStartDate() == null) {
                    orderScheduler.setCarStartDate(orderDateUtil.addHourAndMins(
                            orderScheduler.getCarDependDate(), compensationTrackTime.floatValue()));
                    isStartDate = true;
                }
                if (orderScheduler.getCarArriveDate() == null) {
                    orderScheduler.setCarArriveDate(orderDateUtil.addHourAndMins(
                            orderScheduler.getCarStartDate(), orderScheduler.getArriveTime()));
                    isArrive = true;
                }
                if (orderScheduler.getCarDepartureDate() == null) {
                    orderScheduler.setCarDepartureDate(orderDateUtil.addHourAndMins(
                            orderScheduler.getCarArriveDate(), compensationTrackTime.floatValue()));
                    isDeparture = true;
                }
                String sourceRegionName = "";
                String sourceName = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                if (StringUtils.isNotBlank(sourceName)) sourceRegionName = sourceName;
                String desRegionName = "";
                String desName = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                if (StringUtils.isNotBlank(desName)) desRegionName = desName;
                if (isDepend) {
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[系统补偿机制]已靠台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                }
                if (isStartDate) {
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                }
                if (isArrive) {
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[系统补偿机制]已到达目的地(" + desRegionName + ")", accessToken, orderInfo.getOrderId());
                }
                if (isDeparture) {
                    saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                            "[系统补偿机制]已卸货", accessToken, orderInfo.getOrderId());
                }
            }
            this.saveOrUpdate(orderScheduler);
            //离场同步56K
            OrderFee orderFee = iOrderFeeService.getOrderFee(orderId);
            if (orderInfo.getOrderState() <= OrderConsts.ORDER_STATE.ARRIVED) {
                if (orderInfo.getOrderState() != OrderConsts.ORDER_STATE.ARRIVED) {
//                    wayBillTF.addOrderIdToCache(orderInfo.getOrderId(),orderInfo.getTenantId());
                    //记录终结操作
                    iOrderFeeService.syncBillForm(orderInfo, orderFee, OrderConsts.SYNC_TYPE.FINALITY, accessToken);
                }
                orderInfo.setOrderState(OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT);
            }
            iOrderFeeService.syncOrderTrackTo56K(orderInfo, orderFee, accessToken);
            orderInfoService.saveOrUpdate(orderInfo);
            this.setOrderLoncationInfo(orderGoods, orderScheduler);
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                this.compensationTrackDate(orderInfo.getFromOrderId(), accessToken);
            }
        }
    }

    @Override
    public List<OrderLineDto> queryOrderLineList(Long orderId, String accessToken) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        List<OrderLineDto> listOut = new ArrayList<OrderLineDto>();
        String nand = "";
        String nandDes = "";
        String eand = "";
        String eandDes = "";
        List<OrderTransitLineInfo> transitLineInfos = null;
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            OrderGoodsH orderGoodsH = orderGoodsHService.getOrderGoodsH(orderId);
            nand = orderGoodsH.getNand();
            nandDes = orderGoodsH.getNandDes();
            eand = orderGoodsH.getEand();
            eandDes = orderGoodsH.getEandDes();
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                transitLineInfos = new ArrayList<>();
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    BeanUtil.copyProperties(orderTransitLineInfoH, transitLineInfo);
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            OrderGoods orderGoods = orderGoodsService.getOrderGoods(orderId);
            nand = orderGoods.getNand();
            nandDes = orderGoods.getNandDes();
            eand = orderGoods.getEand();
            eandDes = orderGoods.getEandDes();
            transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
        }
        OrderLineDto map = new OrderLineDto();
        map.setNand(nand);
        map.setEand(eand);
        map.setLineType(0);
        listOut.add(map);
        if (transitLineInfos != null && transitLineInfos.size() > 0) {//有经停点
            for (int i = 0; i < transitLineInfos.size(); i++) {
                OrderTransitLineInfo transitLineInfo = transitLineInfos.get(i);
                OrderLineDto transitMap = new OrderLineDto();
                transitMap.setNand(transitLineInfo.getNand());
                transitMap.setEand(transitLineInfo.getEand());
                transitMap.setLineType(transitLineInfo.getSortId());
                listOut.add(transitMap);
            }
        }
        OrderLineDto mapDes = new OrderLineDto();
        mapDes.setNand(nandDes);
        mapDes.setEand(eandDes);
        mapDes.setLineType(-1);
        listOut.add(mapDes);
        return listOut;
    }


    @Override
    public void setOrderLoncationInfo(OrderGoods orderGoods, OrderScheduler orderScheduler) {
        redisUtil.del(EnumConsts.RemoteCache.ORDER_DEPEND_LOCATION_INFO + orderGoods.getOrderId());
        redisUtil.del(EnumConsts.RemoteCache.ORDER_LEAVE_LOCATION_INFO + orderGoods.getOrderId());

        Map<String, GeoCoordinate> memberCoordinateDependMap = new ConcurrentHashMap<String, GeoCoordinate>();
        Map<String, GeoCoordinate> memberCoordinateLeaveMap = new ConcurrentHashMap<String, GeoCoordinate>();

        if (orderScheduler.getCarArriveDate() == null) {
            GeoCoordinate g1 = new GeoCoordinate(Double.parseDouble(orderGoods.getEandDes()), Double.parseDouble(orderGoods.getNandDes()));
            memberCoordinateDependMap.put("-1", g1);
            List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderGoods.getOrderId());
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (int i = transitLineInfos.size() - 1; i >= 0; i--) {
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(i);
                    if (orderTransitLineInfo.getCarDependDate() == null) {
                        GeoCoordinate g2 = new GeoCoordinate(Double.parseDouble(orderTransitLineInfo.getEand()), Double.parseDouble(orderTransitLineInfo.getNand()));
                        memberCoordinateDependMap.put(String.valueOf(orderTransitLineInfo.getSortId()), g2);
                        if (i == 0) {//经停点1未靠台
                            if (orderScheduler.getCarDependDate() == null) {
                                GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()));
                                memberCoordinateDependMap.put("0", g);
                            } else {
                                if (orderScheduler.getCarStartDate() == null) {
                                    GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()));
                                    memberCoordinateLeaveMap.put("0", g);
                                }
                            }
                        }
                    } else {
                        if (orderTransitLineInfo.getCarStartDate() == null) {
                            GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderTransitLineInfo.getEand()), Double.parseDouble(orderTransitLineInfo.getNand()));
                            memberCoordinateLeaveMap.put(String.valueOf(orderTransitLineInfo.getSortId()), g);
                        }
                        break;//有靠台操作
                    }
                }
            } else {
                if (orderScheduler.getCarDependDate() == null) {
                    GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()));
                    memberCoordinateDependMap.put("0", g);
                } else {
                    if (orderScheduler.getCarStartDate() == null) {
                        GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()));
                        memberCoordinateLeaveMap.put("0", g);
                    }
                }
            }
        } else {
            if (orderScheduler.getCarDepartureDate() == null) {
                GeoCoordinate g = new GeoCoordinate(Double.parseDouble(orderGoods.getEandDes()), Double.parseDouble(orderGoods.getNandDes()));
                memberCoordinateLeaveMap.put("-1", g);
            }
        }
        if (!memberCoordinateDependMap.isEmpty()) {
            redisUtil.set(EnumConsts.RemoteCache.ORDER_DEPEND_LOCATION_INFO + orderGoods.getOrderId(), memberCoordinateDependMap);
        }
        if (!memberCoordinateLeaveMap.isEmpty()) {
            redisUtil.set(EnumConsts.RemoteCache.ORDER_LEAVE_LOCATION_INFO + orderGoods.getOrderId(), memberCoordinateLeaveMap);
        }
    }

    /**
     * 获取订单校验信息
     *
     * @param plateNumber      车牌号
     * @param orderId          过滤订单号
     * @param dependTime       靠台时间
     * @param orderStateLT     小于此订单状态
     * @param userId           用户ID
     * @param isQueryLastOrder 是否查上一单(否则查下一单)
     * @return
     * @throws Exception
     */
    OrderVerifyInfoOut queryOrderVerifyInfoOut(String plateNumber, Long orderId, Date dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder) {
        List<OrderVerifyInfoOut> orderVerifyInfoOuts = orderSchedulerMapper.queryOrderVerifyInfoOut(plateNumber, orderId, dependTime, orderStateLT, userId, isQueryLastOrder);
        OrderVerifyInfoOut orderVerifyInfoOut = null;
        if (orderVerifyInfoOuts != null && orderVerifyInfoOuts.size() > 0) {
            orderVerifyInfoOut = new OrderVerifyInfoOut();
            try {
                BeanUtils.copyProperties(orderVerifyInfoOut, orderVerifyInfoOuts.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<OrderVerifyInfoOut> orderVerifyInfoOutsH = orderSchedulerMapper.queryOrderVerifyInfoOutH(plateNumber, orderId, dependTime, orderStateLT, userId, isQueryLastOrder);
        OrderVerifyInfoOut orderVerifyInfoOutH = null;
        if (orderVerifyInfoOutsH != null && orderVerifyInfoOutsH.size() > 0) {
            orderVerifyInfoOutH = new OrderVerifyInfoOut();
            try {
                BeanUtils.copyProperties(orderVerifyInfoOutH, orderVerifyInfoOutsH.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (orderVerifyInfoOutH != null && orderVerifyInfoOut != null) {
            if (isQueryLastOrder) {
                if (orderVerifyInfoOutH.getDependTime().isBefore(orderVerifyInfoOut.getDependTime())) {
                    return orderVerifyInfoOutH;
                } else {
                    return orderVerifyInfoOut;
                }
            } else {
                if (orderVerifyInfoOutH.getDependTime().isBefore(orderVerifyInfoOut.getDependTime())) {
                    return orderVerifyInfoOut;
                } else {
                    return orderVerifyInfoOutH;
                }
            }
        } else {
            return orderVerifyInfoOutH == null ? orderVerifyInfoOut : orderVerifyInfoOutH;
        }
    }

    private void calculateSectionDate(double startNum, double nextNum, double lastNumber, OrderInfo orderInfo, OrderScheduler orderScheduler, List<OrderTransitLineInfo> transitLineInfos, Double compensationTrackTime, String accessToken) {
        if (startNum != nextNum) {
            Double judgeTime = Double.parseDouble(readisUtil.getSysCfg("DELAYED_COMPENSATION_JUDGE_TIME", "0").getCfgValue());
            Double sationTime = Double.parseDouble(readisUtil.getSysCfg("DELAYED_COMPENSATION_SATION_TIME", "0").getCfgValue());
            Double firstTransitNode = 0.0;
            Double lastTransitNode = 0.0;
            for (Double j = startNum + 0.5; j < nextNum; j += 0.5) {
                if (j < 1) {//起始点
                    if (j == 0.5) {
                        orderScheduler.setCarStartDate(orderDateUtil.addHourAndMins(orderScheduler.getCarDependDate(), compensationTrackTime.floatValue()));
                        String sourceRegionName = "";
                        String name = getSysStaticData("SYS_CITY", orderInfo.getSourceRegion().toString()).getCodeName();
                        if (StringUtils.isNotBlank(name)) sourceRegionName = name;
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已离台起始地(" + sourceRegionName + ")", accessToken, orderInfo.getOrderId());
                    }
                } else if (j >= 1 && j < lastNumber - 0.5) {//经停点
                    OrderTransitLineInfo transitLineInfo = transitLineInfos.get(j.intValue() - 1);
                    if (j == 1 || j == 1.5) {
                        if (j == 1) {
                            transitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(orderScheduler.getCarStartDate(), transitLineInfo.getArriveTime()));
                        } else {
                            transitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                        }
                    } else {
                        if (j - j.intValue() == 0) {//靠台
                            OrderTransitLineInfo lastTransitLineInfo = transitLineInfos.get(j.intValue() - 2);
                            transitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(lastTransitLineInfo.getCarStartDate(), transitLineInfo.getArriveTime()));
                        } else {//离台
                            transitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                        }
                    }
                    if (firstTransitNode.doubleValue() == 0) {
                        firstTransitNode = j;
                    }
                    lastTransitNode = j;
                } else {//到达地
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(transitLineInfos.size() - 1);
                    if (j == lastNumber - 0.5) {
                        orderScheduler.setCarArriveDate(orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarStartDate(), orderScheduler.getArriveTime()));
                        String desRegionName = "";
                        String name = getSysStaticData("SYS_CITY", orderInfo.getDesRegion().toString()).getCodeName();
                        if (StringUtils.isNotBlank(name)) desRegionName = name;
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已到达目的地(" + desRegionName + ")", accessToken, orderInfo.getOrderId());
                    } else if (j == lastNumber) {
                        orderScheduler.setCarDepartureDate(orderDateUtil.addHourAndMins(orderScheduler.getCarArriveDate(), compensationTrackTime.floatValue()));
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已卸货", accessToken, orderInfo.getOrderId());
                    }
                }
            }
            if (lastTransitNode > 0 && lastTransitNode != firstTransitNode) {//无中间经停点
                OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(lastTransitNode.intValue() - 1);
                if (lastTransitNode.intValue() == firstTransitNode.intValue()) {
                    if (orderTransitLineInfo.getCarDependDate().isBefore(orderTransitLineInfo.getCarStartDate())) {
                        orderTransitLineInfo.setCarDependDate(OrderDateUtil.subHourAndMins(orderTransitLineInfo.getCarStartDate(), compensationTrackTime.floatValue()));
                    }
                } else {
                    boolean isBeyond = false;
                    if (lastTransitNode - lastTransitNode.intValue() == 0) {
                        if (orderTransitLineInfo.getCarDependDate().isBefore(orderTransitLineInfo.getCarStartDate())) {
                            isBeyond = true;
                        }
                    } else {
                        if (lastTransitNode.intValue() == transitLineInfos.size()) {//最后经停点
                            if (orderTransitLineInfo.getCarStartDate().isBefore(orderScheduler.getCarArriveDate())) {
                                isBeyond = true;
                            }
                        } else {
                            OrderTransitLineInfo lastTransitLineInfo = transitLineInfos.get(lastTransitNode.intValue());
                            if (orderTransitLineInfo.getCarStartDate().isBefore(lastTransitLineInfo.getCarDependDate())) {
                                isBeyond = true;
                            }
                        }
                    }
                    if (isBeyond) {//超出时间
                        Date stateDate = null;
                        Date endDate = null;
                        //Double j = startNum+0.5; j < nextNum
                        if (firstTransitNode.intValue() == 1) {
                            stateDate = getLocalDateTimeToDate(orderScheduler.getCarStartDate());
                        } else {
                            OrderTransitLineInfo priorOrderTransitLineInfo = transitLineInfos.get(lastTransitNode.intValue() - 2);
                            stateDate = getLocalDateTimeToDate(priorOrderTransitLineInfo.getCarStartDate());
                        }
                        if (lastTransitNode.intValue() == transitLineInfos.size() && orderScheduler.getCarArriveDate() != null) {
                            endDate = getLocalDateTimeToDate(orderScheduler.getCarArriveDate());
                        } else {
                            OrderTransitLineInfo priorOrderTransitLineInfo = transitLineInfos.get(lastTransitNode.intValue() - 1);
                            endDate = getLocalDateTimeToDate(priorOrderTransitLineInfo.getCarStartDate());
                        }
                        if (stateDate != null && endDate != null) {
                            Float totalArriveTime = 0f;
                            int sum = 0;
                            Double diffHour = CommonUtil.getDoubleFormat((endDate.getTime() - stateDate.getTime()) / (3600.0 * 1000.0), 2);
                            for (int i = firstTransitNode.intValue(); i <= lastTransitNode.intValue(); i++) {
                                OrderTransitLineInfo t = transitLineInfos.get(i - 1);
                                totalArriveTime += t.getArriveTime();
                                sum++;
                            }
                            //(后面有时间点的靠台时间/到达时间 - 前面有时间点的离场时间）的小时数
                            //< （中间经停点的到达时效总和 + 中间经停点个数 * 0.5小时 ）
                            //&&
                            //（后面有时间点的靠台时间/到达时间 - 前面有时间点的离场时间）的小时数 < 中间经停点个数 * 1小时（配置项）
                            if (isBeyond) {
                                if (diffHour < (totalArriveTime + (sum * compensationTrackTime))
                                        && diffHour < (sum * judgeTime)) {
                                    //重新计算时间
                                    //后面有时间点的靠台时间/到达时间 - 前面有时间点的离场时间）的小时数 / （中间经停点个数 * 2）
                                    Double agingTime = sum * sationTime != 0 ? diffHour / (sum * sationTime) : 0;
                                    for (Double j = startNum + 0.5; j < nextNum; j += 0.5) {
                                        if (j >= 1 && j < lastNumber - 0.5) {
                                            OrderTransitLineInfo transitLineInfo = transitLineInfos.get(j.intValue() - 1);
                                            if (j == 1 || j == 1.5) {
                                                if (j == 1) {
                                                    transitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(orderScheduler.getCarStartDate(), agingTime.floatValue()));
                                                } else {
                                                    transitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                                                }
                                            } else {
                                                if (j - j.intValue() == 0) {//靠台
                                                    OrderTransitLineInfo lastTransitLineInfo = transitLineInfos.get(j.intValue() - 2);
                                                    transitLineInfo.setCarDependDate(orderDateUtil.addHourAndMins(lastTransitLineInfo.getCarStartDate(), agingTime.floatValue()));
                                                } else {//离台
                                                    transitLineInfo.setCarStartDate(orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), compensationTrackTime.floatValue()));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //记录日志 并且保存
            for (Double j = startNum + 0.5; j < nextNum; j += 0.5) {
                if (j >= 1 && j < lastNumber - 0.5) {
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(j.intValue() - 1);
                    String regionName = "";
                    String name = getSysStaticData("SYS_CITY", orderTransitLineInfo.getRegion().toString()).getCodeName();
                    if (StringUtils.isNotBlank(name)) regionName = name;
                    if (j - j.intValue() == 0) {//靠台
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已靠台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                    } else {//离台
                        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                                "[系统补偿机制]已离台经停点" + orderTransitLineInfo.getSortId() + "(" + regionName + ")", accessToken, orderInfo.getOrderId());
                    }
                    orderTransitLineInfoService.saveOrUpdate(orderTransitLineInfo);
                }
            }
        }
    }

    @Override
    public Map<String, Object> getPreOrderIdByUserid(Long userId, LocalDateTime dependTime, Long tenantId, Long orderId) {
        OrderScheduler orderScheduler = this.getPreOrderSchedulerByUserId(userId, dependTime,
                tenantId, orderId, false);
        OrderSchedulerH orderSchedulerH = orderSchedulerHService.getPreOrderSchedulerHByUserId(userId, dependTime, tenantId,
                orderId, false);
        return compareScheduler(orderScheduler, orderSchedulerH, false);
    }

    @Override
    public OrderScheduler getOrderSchedulerByOrderId(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        OrderScheduler orderScheduler = getOrderScheduler(orderId);
        if (orderScheduler.getId() == null) {
            orderScheduler = new OrderScheduler();
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息！");
            }
            try {
                org.springframework.beans.BeanUtils.copyProperties(orderSchedulerH, orderScheduler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return orderScheduler;
    }

    @Override
    public OrderScheduler getOrderSchedulerByOrderIdWX(Long orderId) {
        OrderScheduler orderScheduler = this.getOrderSchedulerByOrderId(orderId);
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();

            orderScheduler.setVerifyDependFileUrl(orderScheduler.getVerifyDependFileUrl() == null ? ""
                    : client.getHttpURL(orderScheduler.getVerifyDependFileUrl()).split("\\?")[0]);
            orderScheduler.setVerifyStartFileUrl(orderScheduler.getVerifyStartFileUrl() == null ? ""
                    : client.getHttpURL(orderScheduler.getVerifyStartFileUrl()).split("\\?")[0]);
            orderScheduler.setVerifyArriveFileUrl(orderScheduler.getVerifyArriveFileUrl() == null ? ""
                    : client.getHttpURL(orderScheduler.getVerifyArriveFileUrl()).split("\\?")[0]);
            orderScheduler.setVerifyDepartureFileUrl(orderScheduler.getVerifyDepartureFileUrl() == null ? ""
                    : client.getHttpURL(orderScheduler.getVerifyDepartureFileUrl()).split("\\?")[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderScheduler;
    }

    @Override
    public void currectOrderTrackDate(Long orderId, int trackType, Date verifyDate, Long fileId, String fileUrl, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        if (verifyDate == null) {//无时间 初始化为null
            fileId = null;
            fileUrl = null;
        }
        if (trackType <= 0 || trackType > OrderConsts.TRACK_TYPE.TYPE4) {
            throw new BusinessException("校准时间操作类型异常！");
        }
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        boolean isHis = false; // 从订单历史表中查询的订单数据
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息！");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            try {
                org.springframework.beans.BeanUtils.copyProperties(orderInfoH, orderInfo);
                org.springframework.beans.BeanUtils.copyProperties(orderSchedulerH, orderScheduler);
            } catch (Exception e) {
                e.printStackTrace();
            }
            isHis = true;
        } else {
            orderScheduler = getOrderScheduler(orderId);
        }
        String trackTypeName = "";
        if (trackType == OrderConsts.TRACK_TYPE.TYPE1) {//靠台
            if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.LOADING) {
                throw new BusinessException("订单还未靠台，不能校准靠台时间！");
            }
            trackTypeName = "靠台";
            if (verifyDate != null) {
                if (orderScheduler.getVerifyStartDate() != null && DateUtil.asDate(orderScheduler.getVerifyStartDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准靠台时间不能超过校准离台时间！");
                } else if (orderScheduler.getVerifyArriveDate() != null && DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准靠台时间不能超过校准到达时间！");
                } else if (orderScheduler.getVerifyDepartureDate() != null && DateUtil.asDate(orderScheduler.getVerifyDepartureDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准靠台时间不能超过校准离场时间！");
                }
            }
            if (isHis) {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                orderSchedulerH.setVerifyDependDate(getDateToLocalDateTime(verifyDate));
                orderSchedulerH.setVerifyDependFileId(fileId);
                orderSchedulerH.setVerifyDependFileUrl(fileUrl);
                if (orderSchedulerH.getId() != null) {
                    orderSchedulerHService.updateById(orderSchedulerH);
                }
//                orderSchedulerHService.saveOrUpdate(orderSchedulerH);
            } else {
                orderScheduler.setVerifyDependDate(getDateToLocalDateTime(verifyDate));
                orderScheduler.setVerifyDependFileId(fileId);
                orderScheduler.setVerifyDependFileUrl(fileUrl);
                if (orderScheduler.getId() != null) {
                    this.updateById(orderScheduler);
                }
//                this.saveOrUpdate(orderScheduler);
            }
        } else if (trackType == OrderConsts.TRACK_TYPE.TYPE2) {//离台
            if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.TRANSPORT_ING) {
                throw new BusinessException("订单还未离台，不能校准离台时间！");
            }
            trackTypeName = "离台";
            if (verifyDate != null) {
                if (orderScheduler.getVerifyDependDate() != null && DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准离台时间不能小于校准靠台时间！");
                } else if (orderScheduler.getVerifyArriveDate() != null && DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准离台时间不能超过校准到达时间！");
                } else if (orderScheduler.getVerifyDepartureDate() != null && DateUtil.asDate(orderScheduler.getVerifyDepartureDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准离台时间不能超过校准离场时间！");
                }
            }
            if (isHis) {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                orderSchedulerH.setVerifyStartDate(getDateToLocalDateTime(verifyDate));
                orderSchedulerH.setVerifyStartFileId(fileId);
                orderSchedulerH.setVerifyStartFileUrl(fileUrl);
                if (orderSchedulerH.getId() != null) {
                    orderSchedulerHService.updateById(orderSchedulerH);
                }
//                orderSchedulerHService.saveOrUpdate(orderSchedulerH);
            } else {
                orderScheduler.setVerifyStartDate(getDateToLocalDateTime(verifyDate));
                orderScheduler.setVerifyStartFileId(fileId);
                orderScheduler.setVerifyStartFileUrl(fileUrl);
                if (orderScheduler.getId() != null) {
                    this.updateById(orderScheduler);
                }
//                this.saveOrUpdate(orderScheduler);
            }
        } else if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {//到达
            if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.ARRIVED) {
                throw new BusinessException("订单还未到达，不能校准到达时间！");
            }
            trackTypeName = "到达";
            if (verifyDate != null) {
                if (orderScheduler.getVerifyDependDate() != null && DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准到达时间不能小于校准靠台时间！");
                } else if (orderScheduler.getVerifyStartDate() != null && DateUtil.asDate(orderScheduler.getVerifyStartDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准到达台时间不能小于校准离台时间！");
                } else if (orderScheduler.getVerifyDepartureDate() != null && DateUtil.asDate(orderScheduler.getVerifyDepartureDate()).getTime() < verifyDate.getTime()) {
                    throw new BusinessException("校准到达时间不能超过校准离场时间！");
                }
            }
            if (isHis) {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                orderSchedulerH.setVerifyArriveDate(getDateToLocalDateTime(verifyDate));
                orderSchedulerH.setVerifyArriveFileId(fileId);
                orderSchedulerH.setVerifyArriveFileUrl(fileUrl);
                if (orderSchedulerH.getId() != null) {
                    orderSchedulerHService.updateById(orderSchedulerH);
                }
//                orderSchedulerHService.saveOrUpdate(orderSchedulerH);
            } else {
                orderScheduler.setVerifyArriveDate(getDateToLocalDateTime(verifyDate));
                orderScheduler.setVerifyArriveFileId(fileId);
                orderScheduler.setVerifyArriveFileUrl(fileUrl);
                if (orderScheduler.getId() != null) {
                    this.updateById(orderScheduler);
                }
//                this.saveOrUpdate(orderScheduler);
            }
        } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {//离场
            if (orderInfo.getOrderState() < OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT) {
                throw new BusinessException("订单还未离场，不能校准离场时间！");
            }
            trackTypeName = "离场";
            if (verifyDate != null) {
                if (orderScheduler.getVerifyDependDate() != null && DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准离场时间不能小于校准靠台时间！");
                } else if (orderScheduler.getVerifyStartDate() != null && DateUtil.asDate(orderScheduler.getVerifyStartDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准离场时间不能小于校准离台时间！");
                } else if (orderScheduler.getVerifyArriveDate() != null && DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime() > verifyDate.getTime()) {
                    throw new BusinessException("校准离场时间不能小于校准到达时间！");
                }
            }
            if (isHis) {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                orderSchedulerH.setVerifyDepartureDate(getDateToLocalDateTime(verifyDate));
                orderSchedulerH.setVerifyDepartureFileId(fileId);
                orderSchedulerH.setVerifyDepartureFileUrl(fileUrl);
                if (orderSchedulerH.getId() != null) {
                    orderSchedulerHService.updateById(orderSchedulerH);
                }
//                orderSchedulerHService.saveOrUpdate(orderSchedulerH);
            } else {
                orderScheduler.setVerifyDepartureDate(getDateToLocalDateTime(verifyDate));
                orderScheduler.setVerifyDepartureFileId(fileId);
                orderScheduler.setVerifyDepartureFileUrl(fileUrl);
                if (orderScheduler.getId() != null) {
                    this.updateById(orderScheduler);
                }
//                this.saveOrUpdate(orderScheduler);
            }
        }

        // 查询订单付款回显记录
        List<OrderLimit> limitList = iOrderLimitService.getOrderLimit(orderId, loginInfo.getTenantId(), -1);
        if (limitList != null && limitList.size() > 0) {
            OrderLimit limit = limitList.get(0);

            // 车辆归属(资金渠道)不为空
            if (org.apache.commons.lang.StringUtils.isNotBlank(limit.getVehicleAffiliation())) {
                // 根据用户id和票据平台，判断此用户id是否是对应的票据平台
                boolean is56K = billPlatformService.judgeBillPlatform(Long.valueOf(limit.getVehicleAffiliation()), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_56K));
                if (is56K) {
                    // 同步订单56K
                    iOrderSync56KService.syncOrderInfoTo56K(orderId, true, true);
                }
            }
        }
        saveSysOperLog(SysOperLogConst.BusiCode.OrderInfo, SysOperLogConst.OperType.Update,
                loginInfo.getName() + "校准了" + trackTypeName + "时间[" + (verifyDate == null ? "" : DateUtil.formatDateByFormat(verifyDate, DateUtil.DATETIME_FORMAT)) + "]", accessToken, orderInfo.getOrderId());
    }

    public static Date getDateByLocalDateTime(LocalDateTime localDateTime) {

        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return date;
    }

    /**
     * 计算订单预估轨迹时间
     *
     * @param orderId
     * @param trackType
     * @return
     * @throws Exception
     */
    public LocalDateTime queryOrderTrackDate(Long orderId, Integer trackType, LoginInfo user) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        Double estDeparturerTime = Double.parseDouble(String.valueOf(readisUtil.getSysCfg(ESTIMATE_DEPARTURE_TIME, "0").getCfgValue()));
        if (orderInfo != null) {
            //  SysContexts.getEntityManager().evict(orderInfo);
            OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
            OrderInfoExt OrderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            OrderGoods OrderGoods = orderGoodsService.getOrderGoods(orderId);
            if (orderScheduler == null) {
                //  logger.error(orderInfo.getRemark());
                throw new BusinessException("未找到订单[" + orderId + "]调度" + (OrderInfoExt == null ? ",订单扩展表" : "") + (OrderGoods == null ? ",订单货物表" : "") + "信息！[demo]" + user.getTenantId());
            }
            LocalDateTime dependTime = orderScheduler.getCarDependDate() != null ? orderScheduler.getCarDependDate() : orderScheduler.getDependTime();
            if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                if (orderScheduler.getCarArriveDate() != null) {
                    return orderScheduler.getCarArriveDate();
                } else {
                    return this.getOrderArriveDate(orderId, dependTime, orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false);
                }
            } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                if (orderScheduler.getCarDepartureDate() != null) {
                    return orderScheduler.getCarDepartureDate();
                } else {
                    return orderDateUtil.addHourAndMins(this.getOrderArriveDate(orderId, dependTime, orderScheduler.getCarStartDate(), orderScheduler.getArriveTime(), false), estDeparturerTime.floatValue());
                }
            }
        } else {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.selectByOrderId(orderId);
            if (orderInfoH.getOrderState().intValue() == OrderConsts.ORDER_STATE.CANCELLED) {
                LocalDateTime dependTime = orderSchedulerH.getCarDependDate() != null ? orderSchedulerH.getCarDependDate() : orderSchedulerH.getDependTime();
                if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                    if (orderSchedulerH.getCarArriveDate() != null) {
                        return orderSchedulerH.getCarArriveDate();
                    } else {
                        return this.getOrderArriveDate(orderId, dependTime, orderSchedulerH.getCarStartDate(), orderSchedulerH.getArriveTime(), false);
                    }
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                    if (orderSchedulerH.getCarDepartureDate() != null) {
                        return orderSchedulerH.getCarDepartureDate();
                    } else {
                        return orderDateUtil.addHourAndMins(this.getOrderArriveDate(orderId, dependTime, orderSchedulerH.getCarStartDate(), orderSchedulerH.getArriveTime(), false), estDeparturerTime.floatValue());
                    }
                }
            } else {
                if (trackType == OrderConsts.TRACK_TYPE.TYPE3) {
                    return orderSchedulerH.getCarArriveDate();
                } else if (trackType == OrderConsts.TRACK_TYPE.TYPE4) {
                    return orderSchedulerH.getCarDepartureDate();
                }
            }
        }
        return null;
    }


    /**
     * 获取订单预估到达时间
     *
     * @param orderId
     * @param dependTime
     * @param carStartDate
     * @param arriveTime
     * @param isHis
     * @return
     * @throws Exception
     */
    public LocalDateTime getOrderArriveDate(Long orderId, LocalDateTime dependTime, LocalDateTime carStartDate, Float arriveTime, Boolean isHis) {
        Double estStartTime = Double.parseDouble(String.valueOf(readisUtil.getSysCfg("ESTIMATE_START_TIME", "0").getId()));
        LocalDateTime orderArriveDate = carStartDate == null ? orderDateUtil.addHourAndMins(dependTime, estStartTime.floatValue()) : carStartDate;
        if (isHis) {
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    if (orderTransitLineInfoH.getCarDependDate() == null) {
                        orderArriveDate = orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfoH.getArriveTime()), estStartTime.floatValue());
                    } else {
                        if (orderTransitLineInfoH.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfoH.getCarDependDate(), estStartTime.floatValue());
                        } else {
                            orderArriveDate = orderTransitLineInfoH.getCarStartDate();
                        }
                    }
                }
            }
        } else {
            List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                    if (orderTransitLineInfo.getCarDependDate() == null) {
                        orderArriveDate = orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfo.getArriveTime()), estStartTime.floatValue());
                    } else {
                        if (orderTransitLineInfo.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarDependDate(), estStartTime.floatValue());
                        } else {
                            orderArriveDate = orderTransitLineInfo.getCarStartDate();
                        }
                    }
                }
            }
        }
        orderArriveDate = orderDateUtil.addHourAndMins(orderArriveDate, arriveTime);
        return orderArriveDate;
    }

    @Override
    public Boolean checkPayMentWaySwitchover(String plateNumber, LocalDateTime dependTime, Long tenantId, Long orderId) {

        // 获取订单类型状态
        Map map = this.getPreOrderIdByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        if (map != null) {
            Long lastOrderId = DataFormat.getLongKey(map, "orderId");
            Integer type = DataFormat.getIntKey(map, "type");
            if (type == OrderConsts.TableType.ORI) {// 原表

                // 获取订单扩展信息
                OrderInfoExt orderInfoExt = orderInfoExtService.selectByOrderId(lastOrderId);
                // 付款方式  实时报销
                if (orderInfoExt != null && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    // 通过订单号，获取上报费用记录
                    List<OrderCostReport> list = orderCostReportService.getOrderCostReportByOrderId(lastOrderId);
                    // 获取订单成本上报信息
                    OrderMainReport orderMainReport = orderMainReportService.getObjectByOrderId(lastOrderId);
                    if (list != null && list.size() > 0) {
                        OrderCostReport orderCostReport = list.get(0);
                        if (orderCostReport == null) {
                            return false; // 无上班费用记录
                        }
                        // 无成本上报记录  没有审核  没有加满油
                        if ((orderMainReport == null || orderMainReport.getIsAuditPass() == null || orderMainReport.getIsAuditPass().intValue() != SysStaticDataEnum.IS_AUTH.IS_AUTH1)
                                || (orderCostReport.getIsFullOil() == null || orderCostReport.getIsFullOil().intValue() == 0)) {
                            return false;
                        }
                    } else {
                        return false; // 无上班费用记录
                    }
                }
            } else if (type == OrderConsts.TableType.HIS) {// 历史表
                OrderInfoExtH orderInfoExtH = orderInfoExtHService.selectByOrderId(lastOrderId);
                if (orderInfoExtH != null && orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                    List<OrderCostReport> list = orderCostReportService.getOrderCostReportByOrderId(lastOrderId);
                    OrderMainReport orderMainReport = orderMainReportService.getObjectByOrderId(lastOrderId);
                    if (list != null && list.size() > 0) {
                        OrderCostReport orderCostReport = list.get(0);
                        if (orderCostReport == null) {
                            return false;
                        }
                        if ((orderMainReport == null || orderMainReport.getIsAuditPass() == null || orderMainReport.getIsAuditPass().intValue() != SysStaticDataEnum.IS_AUTH.IS_AUTH1)
                                || (orderCostReport.getIsFullOil() == null || orderCostReport.getIsFullOil().intValue() == 0)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> getPreOrderIdByPlateNumber(String plateNumber, LocalDateTime dependTime, Long tenantId, Long orderId) {
        // 根据车牌号查询靠台时间上一单订单的调度信息
        OrderScheduler orderScheduler = this.getPreOrderSchedulerByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        // 根据车牌号查询最近的已完成订单的调度信息
        OrderSchedulerH preOrderSchedulerHByPlateNumber = orderSchedulerHService.getPreOrderSchedulerHByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        return compareScheduler(orderScheduler, preOrderSchedulerHByPlateNumber, false);

    }

    @Override
    public OrderScheduler getPreOrderSchedulerByPlateNumber(String plateNumber, LocalDateTime dependTime, Long tenandId, Long orderId) {
        LambdaQueryWrapper<OrderScheduler> lambda = new QueryWrapper<OrderScheduler>().lambda();
        if (StringUtils.isNotBlank(plateNumber)) {
            lambda.eq(OrderScheduler::getPlateNumber, plateNumber);
        } else {
            throw new BusinessException("传入的车牌号为空");
        }
        if (dependTime != null) {
            lambda.le(OrderScheduler::getDependTime, dependTime);
        }
        if (orderId != null && orderId > 0) {
            lambda.ne(OrderScheduler::getOrderId, orderId);
        }
        if (tenandId != null) {
            lambda.eq(OrderScheduler::getTenantId, tenandId);
        } else {
            throw new BusinessException("传入的车队为空");
        }
        lambda.orderByDesc(OrderScheduler::getDependTime)
                .last("limit 1");
        List<OrderScheduler> returnList = this.list(lambda);
        if (returnList != null && returnList.size() == 1) {
            OrderScheduler scheduler = returnList.get(0);
            //  session.evict(scheduler);
            return scheduler;
        }
        return null;
    }

    private Map<String, Object> compareScheduler(OrderScheduler orderScheduler, OrderSchedulerH orderSchedulerH, Boolean isGt) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (orderScheduler != null && orderSchedulerH != null) {
            if (!isGt) {
                if (orderScheduler.getDependTime().isBefore(orderSchedulerH.getDependTime())) {
                    retMap.put("orderId", orderSchedulerH.getOrderId());
                    retMap.put("type", OrderConsts.TableType.HIS);
                    return retMap;
                } else {
                    retMap.put("orderId", orderScheduler.getOrderId());
                    retMap.put("type", OrderConsts.TableType.ORI);
                    return retMap;
                }
            } else {
                if (orderScheduler.getDependTime().isAfter(orderSchedulerH.getDependTime())) {
                    retMap.put("orderId", orderSchedulerH.getOrderId());
                    retMap.put("type", OrderConsts.TableType.HIS);
                    return retMap;
                } else {
                    retMap.put("orderId", orderScheduler.getOrderId());
                    retMap.put("type", OrderConsts.TableType.ORI);
                    return retMap;
                }
            }
        }

        if (orderScheduler != null && orderSchedulerH == null) {
            retMap.put("orderId", orderScheduler.getOrderId());
            retMap.put("type", OrderConsts.TableType.ORI);
            return retMap;
        }

        if (orderScheduler == null && orderSchedulerH != null) {
            retMap.put("orderId", orderSchedulerH.getOrderId());
            retMap.put("type", OrderConsts.TableType.HIS);
            return retMap;
        }

        return null;
    }

    public static SysCfg getSysCfg(String cfgName, LoginInfo user) {
        long tenantId = -1L;
        SysCfg sysCfg = null;
        if (user != null && user.getTenantId() != null) {
            tenantId = user.getTenantId();
        }
        if (tenantId > 0L) {
            sysCfg.setCfgName(cfgName);
            sysCfg.setTenantId(tenantId);
        }
        if (sysCfg == null) {
            if (sysCfg == null) {
                throw new BusinessException("没有找到对应的系统参数！");
            }
        }

        return sysCfg;
    }

    public Object getCfgVal(String cfgName, int system, Class type, LoginInfo user) {
        SysCfg cfg = getSysCfg(cfgName, user);
        if (cfg == null) {
            cfg.setCfgName(cfgName);
        }

        if (cfg != null && (system == -1 || system == cfg.getCfgSystem())) {
            if (type.equals(Integer.class)) {
                return Integer.valueOf(cfg.getCfgValue());
            } else if (type.equals(Double.class)) {
                return Double.parseDouble(cfg.getCfgValue());
            } else if (type.equals(Float.class)) {
                return Float.parseFloat(cfg.getCfgValue());
            } else {
                return !type.equals(Boolean.class) ? String.valueOf(cfg.getCfgValue()) : "1".equals(cfg.getCfgValue()) || "true".equals(cfg.getCfgValue().toLowerCase());
            }
        } else {
            return null;
        }
    }

    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 记录日志
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }


    @Override
    public Map<String, Object> getPreOrderIdByTrailerPlate(String trailerPlate, Long tenantId) {
        OrderScheduler orderScheduler = getPreOrderSchedulerByTrailerPlate(trailerPlate, tenantId);
//        try {
//            if (orderScheduler == null) {
//                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getPreOrderSchedulerHByTrailerPlate(trailerPlate, tenantId);
//                return orderSchedulerH == null ? null : JSON.parseObject(JSON.toJSONString(orderSchedulerH), Map.class);
//            } else {
//                return JSON.parseObject(JSON.toJSONString(orderScheduler), Map.class);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        return null;
    }


    @Override
    public OrderScheduler getPreOrderSchedulerByTrailerPlate(String trailerPlate, Long tenandId) {
        LambdaQueryWrapper<OrderScheduler> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(org.apache.commons.lang.StringUtils.isNotBlank(trailerPlate), OrderScheduler::getTrailerPlate, trailerPlate);
        queryWrapper.eq(tenandId != null, OrderScheduler::getTenantId, tenandId);
        queryWrapper.orderByDesc(OrderScheduler::getDependTime);

        List<OrderScheduler> orderSchedulers = getBaseMapper().selectList(queryWrapper);
        if (orderSchedulers != null && orderSchedulers.size() == 1) {
            OrderScheduler orderScheduler = orderSchedulers.get(0);
            return orderScheduler;
        }

        return null;
    }

    @Override
    public OrderSchedulerH getOrderSchedulerH(Long orderId) {
        List<OrderSchedulerH> list = baseMapper.getOrderId(orderId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    @Override
    public String queryCarEtcCardInfo(Long orderId, String accessToken) {
        OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
        String bindVehicle = "";
        Long tenantId = null;
        if (orderScheduler == null) {
            OrderSchedulerH schedulerH = this.getOrderSchedulerH(orderId);
            if (schedulerH != null) {
                bindVehicle = schedulerH.getPlateNumber();
                tenantId = schedulerH.getTenantId();
            } else {
                throw new BusinessException("未找到订单号[" + orderId + "]的订单信息");
            }
        } else {
            bindVehicle = orderScheduler.getPlateNumber();
            tenantId = orderScheduler.getTenantId();
        }
        EtcMaintain etcMaintain = iEtcMaintainService.getETCardByNumber(bindVehicle, tenantId, accessToken);
        return etcMaintain == null ? "" : etcMaintain.getEtcId();
    }

  /*  @Override
    public Map<String, Object> getPreOrderIdByPlateNumber(String plateNumber, Date dependTime, Long tenantId, Long orderId) {
        OrderScheduler orderScheduler = getPreOrderSchedulerByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        OrderSchedulerH orderSchedulerH = orderSchedulerHService.getPreOrderSchedulerHByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        return compareScheduler(orderScheduler, orderSchedulerH, false);
    }*/

    @Override
    public OrderScheduler getPreOrderSchedulerByPlateNumber(String plateNumber, Date dependTime, Long tenandId, Long orderId) {

        LambdaQueryWrapper<OrderScheduler> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(org.apache.commons.lang.StringUtils.isNotBlank(plateNumber), OrderScheduler::getPlateNumber, plateNumber);
        queryWrapper.le(dependTime != null, OrderScheduler::getDependTime, dependTime);
        queryWrapper.ne(orderId != null && orderId.intValue() > 0, OrderScheduler::getOrderId, orderId);
        queryWrapper.eq(tenandId != null, OrderScheduler::getTenantId, tenandId);
        queryWrapper.orderByDesc(OrderScheduler::getDependTime);

        List<OrderScheduler> orderSchedulers = getBaseMapper().selectList(queryWrapper);
        if (orderSchedulers != null && orderSchedulers.size() == 1) {
            OrderScheduler orderScheduler = orderSchedulers.get(0);
            return orderScheduler;
        }

        return null;
    }

//    @Override
//    public Map<Long, Boolean> hasFinalOrderLimit(List<Long> orderIds) {
//        Map<Long, Boolean> longBooleanMap = orderLimitMapper.hasFinalOrderLimit(orderIds, -1);
//        return longBooleanMap;
//    }

    @Override
    public CheckConflictVo checkDependTimeConflict(CheckConflictDto checkConflictDto) {

        CheckConflictVo checkConflictVo = new CheckConflictVo();
        String dependTime = checkConflictDto.getDependTime();
        if (StringUtils.isBlank(dependTime)) {
            throw new BusinessException("缺少靠台时间参数！");
        }
        DateTimeFormatter dft = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(dependTime, dft);

        if (org.apache.commons.lang.StringUtils.isNotBlank(checkConflictDto.getPlateNumber())) {
            Map plateNumberMap = this.checkLineIsOkByPlateNumber(checkConflictDto.getPlateNumber(),
                    dateTime, checkConflictDto.getFromOrderId(), checkConflictDto.getOrderId());
            boolean plateNumberCheck = DataFormat.getBooleanKey(plateNumberMap, "checkResult");
            if (!plateNumberCheck) {
                checkConflictVo.setPlateNumberConflict(true);
            } else {
                checkConflictVo.setPlateNumberConflict(false);
            }
        }
        if (checkConflictDto.getCarDriverId() != null && checkConflictDto.getCarDriverId() > 0L) {
            Map driverMap = this.checkLineIsOkByDriverId(checkConflictDto.getCarDriverId(), dateTime,
                    checkConflictDto.getFromOrderId(), checkConflictDto.getOrderId());
            boolean driverCheck = DataFormat.getBooleanKey(driverMap, "checkResult");
            if (!driverCheck) {
                checkConflictVo.setDriverConflict(true);
            } else {
                checkConflictVo.setDriverConflict(false);
            }
        }
        if (checkConflictDto.getCopilotDriverId() != null && checkConflictDto.getCopilotDriverId() > 0L) {
            Map copilotDriverMap = this.checkLineIsOkByDriverId(checkConflictDto.getCopilotDriverId(),
                    dateTime, checkConflictDto.getFromOrderId(), checkConflictDto.getOrderId());
            boolean copilotDriverCheck = DataFormat.getBooleanKey(copilotDriverMap, "checkResult");
            if (!copilotDriverCheck) {
                checkConflictVo.setCopilotDriverConflict(true);
            } else {
                checkConflictVo.setCopilotDriverConflict(false);
            }
        }


        return checkConflictVo;
    }

    @Override
    public Map checkLineIsOkByPlateNumber(String plateNumber, LocalDateTime dependTime, Long fromOrderId, Long orderId) {
        LocalDateTime arriveDate = null;
        LocalDateTime arriveDateHis = null;
        List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumberVos = orderSchedulerMapper.checkLineIsOkByPlateNumber(plateNumber, fromOrderId, dependTime, orderId);
        if (checkLineIsOkByPlateNumberVos != null && checkLineIsOkByPlateNumberVos.size() > 0) {
            CheckLineIsOkByPlateNumberVo checkLineIsOkByPlateNumberVo = checkLineIsOkByPlateNumberVos.get(0);
            arriveDate = checkLineIsOkByPlateNumberVo.getArriveDate();
        }
        //查询历史表，查询已完成，撤单的不需要查询
        List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumberVosH = orderSchedulerMapper.checkLineIsOkByPlateNumberH(plateNumber, fromOrderId, dependTime, orderId);
        if (checkLineIsOkByPlateNumberVosH != null && checkLineIsOkByPlateNumberVosH.size() > 0) {
            CheckLineIsOkByPlateNumberVo checkLineIsOkByPlateNumberVoH = checkLineIsOkByPlateNumberVosH.get(0);
            arriveDateHis = checkLineIsOkByPlateNumberVoH.getArriveDate();
        }
        Map out = new ConcurrentHashMap();
        if (arriveDate != null || arriveDateHis != null) {
            out.put("checkResult", false);
            if (arriveDate != null && arriveDateHis != null) {
                out.put("arriveDate", arriveDate.isAfter(arriveDateHis) ? arriveDate : arriveDateHis);
            } else if (arriveDate != null) {
                out.put("arriveDate", arriveDate);
            } else if (arriveDateHis != null) {
                out.put("arriveDate", arriveDateHis);
            }
        } else {
            out.put("checkResult", true);
            out.put("arriveDate", new Date());
        }
        return out;
    }

    @Override
    public Map checkLineIsOkByDriverId(Long userId, LocalDateTime dependTime, Long fromOrderId, Long orderId) {
        LocalDateTime arriveDate = null;
        LocalDateTime arriveDateHis = null;
        List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumberVos = orderSchedulerMapper.checkLineIsOkByDriverId(userId, fromOrderId, dependTime, orderId);
        if (checkLineIsOkByPlateNumberVos != null && checkLineIsOkByPlateNumberVos.size() > 0) {
            CheckLineIsOkByPlateNumberVo checkLineIsOkByPlateNumberVo = checkLineIsOkByPlateNumberVos.get(0);
            arriveDate = checkLineIsOkByPlateNumberVo.getArriveDate();
        }
//查询历史表查询已完成，撤单的不需要查询
        List<CheckLineIsOkByPlateNumberVo> checkLineIsOkByPlateNumberVosH = orderSchedulerMapper.checkLineIsOkByDriverIdH(userId, fromOrderId, dependTime, orderId);
        if (checkLineIsOkByPlateNumberVosH != null && checkLineIsOkByPlateNumberVosH.size() > 0) {
            CheckLineIsOkByPlateNumberVo checkLineIsOkByPlateNumberVoH = checkLineIsOkByPlateNumberVosH.get(0);
            arriveDateHis = checkLineIsOkByPlateNumberVoH.getArriveDate();
        }

        Map out = new ConcurrentHashMap();
        if (arriveDate != null || arriveDateHis != null) {
            out.put("checkResult", false);
            if (arriveDate != null && arriveDateHis != null) {
                out.put("arriveDate", arriveDate.isAfter(arriveDateHis) ? arriveDate : arriveDateHis);
            } else if (arriveDate != null) {
                out.put("arriveDate", arriveDate);
            } else if (arriveDateHis != null) {
                out.put("arriveDate", arriveDateHis);
            }
        } else {
            out.put("checkResult", true);
            out.put("arriveDate", new Date());
        }
        return out;
    }

    @Override
    public Map<Long, Boolean> hasFinalOrderLimit(List<Long> orderIds) {
        Map<Long, Boolean> longBooleanMap = orderLimitMapper.hasFinalOrderLimit(orderIds, -1);
        return longBooleanMap;
    }

    @Override
    public List<orderSchedulerDto> queryOrderDriverList(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        Long carDriverId = 0L;
        String carDriverMan = "";
        String carDriverPhone = "";
        Long copilotUserId = 0L;
        String copilotMan = "";
        String copilotPhone = "";
        Boolean isNeedCopilot = true;
        Boolean isTenant = false;
        Long toTenantId = null;
        // 获取订单调度信息
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            // 订单调度信息不存在  从订单调度日志信息表中查询
            OrderSchedulerH orderSchedulerH = orderSchedulerService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            // 获取订单历史信息
            OrderInfoH orderInfo = orderInfoHService.getOrderH(orderId);
            if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                isTenant = true;
                toTenantId = orderInfo.getToTenantId();
            }
            // 获取订单历史扩展信息
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            if (orderSchedulerH.getVehicleClass() != null
                    && ((orderSchedulerH.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderSchedulerH.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderSchedulerH.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )
                    || (orderSchedulerH.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT))) {
                isNeedCopilot = false;
            }
            carDriverId = orderSchedulerH.getCarDriverId();
            carDriverMan = orderSchedulerH.getCarDriverMan();
            carDriverPhone = orderSchedulerH.getCarDriverPhone();
            copilotUserId = orderSchedulerH.getCopilotUserId();
            copilotMan = orderSchedulerH.getCopilotMan();
            copilotPhone = orderSchedulerH.getCopilotPhone() == null ? "" : orderSchedulerH.getCopilotPhone();
        } else {
            OrderInfo orderInfo = orderInfoService.getOrder(orderId);
            // 订单转租
            if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
                isTenant = true;
                toTenantId = orderInfo.getToTenantId();
            }
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
            if (orderScheduler.getVehicleClass() != null
                    && ((orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
            )
                    || (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.CONTRACT))) {
                isNeedCopilot = false;
            }
            carDriverId = orderScheduler.getCarDriverId();
            carDriverMan = orderScheduler.getCarDriverMan();
            carDriverPhone = orderScheduler.getCarDriverPhone();
            copilotUserId = orderScheduler.getCopilotUserId();
            copilotMan = orderScheduler.getCopilotMan();
            copilotPhone = orderScheduler.getCopilotPhone() == null ? "" : orderScheduler.getCopilotPhone();
        }
        List<orderSchedulerDto> listOut = new ArrayList<orderSchedulerDto>();
        // 订单是否转租
        if (isTenant) {
            SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(toTenantId, true);
            if (sysTenantDef != null) {
                orderSchedulerDto orderSchedulerDto = new orderSchedulerDto();
                orderSchedulerDto.setUserId(sysTenantDef.getAdminUser());
                orderSchedulerDto.setUserName(sysTenantDef.getName());
                orderSchedulerDto.setUserPhone(sysTenantDef.getLinkPhone());
                orderSchedulerDto.setUserType(4);
                orderSchedulerDto.setUserTypeName("车队");
                listOut.add(orderSchedulerDto);
                if (listOut == null) {
                    listOut = new ArrayList();
                }
                return listOut;
            }
            return null;
        } else if (carDriverId != null && carDriverId > 0) {
            orderSchedulerDto orderSchedulerDto = new orderSchedulerDto();
            orderSchedulerDto.setUserId(carDriverId);
            orderSchedulerDto.setUserName(carDriverMan);
            orderSchedulerDto.setUserPhone(carDriverPhone);
            orderSchedulerDto.setUserType(1);
            orderSchedulerDto.setUserTypeName("主驾驶");
            listOut.add(orderSchedulerDto);
            // 副驾驶存在
            if (copilotUserId != null && copilotUserId > 0 && isNeedCopilot) {
                orderSchedulerDto dto = new orderSchedulerDto();
                dto.setUserId(copilotUserId);
                dto.setUserName(copilotMan);
                dto.setUserPhone(copilotPhone);
                dto.setUserType(2);
                dto.setUserTypeName("副驾驶");
                listOut.add(dto);
            }
            if (isNeedCopilot) {
                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrder(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                for (OrderDriverSwitchInfo orderDriverSwitchInfo : switchInfos) {
                    if (orderDriverSwitchInfo.getReceiveUserId() != null &&
                            !orderDriverSwitchInfo.getReceiveUserId().equals(carDriverId)
                            && !orderDriverSwitchInfo.getReceiveUserId().equals(copilotUserId)) {
                        orderSchedulerDto schedulerDto = new orderSchedulerDto();
                        schedulerDto.setUserId(orderDriverSwitchInfo.getReceiveUserId());
                        schedulerDto.setUserName(orderDriverSwitchInfo.getReceiveUserName());
                        schedulerDto.setUserPhone(orderDriverSwitchInfo.getReceiveUserPhone());
                        schedulerDto.setUserType(3);
                        schedulerDto.setUserTypeName("经停驾驶");
                        listOut.add(schedulerDto);
                    }
                }
            }
            if (listOut == null) {
                listOut = new ArrayList();
            }
            return listOut;
        } else {
            if (listOut == null) {
                listOut = new ArrayList();
            }
            return null;
        }
    }

    @Override
    public List<BigInteger> getMonthFirstOrderId(String plateNumber, String month, Long excludeOrderId) {
        List<BigInteger> monthFirstOrderId = getBaseMapper().getMonthFirstOrderId(plateNumber, month, excludeOrderId);
        return monthFirstOrderId;
    }


    /**
     * 获取订单线路节点集合
     * 聂杰伟
     */
    @Override
    public List<QueryOrderLineNodeListVo> queryOrderLineNodeList(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId)
                .eq("tenant_id", loginInfo.getTenantId());

        // 获取订单信息
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);
        List<QueryOrderLineNodeListVo> listOut = new ArrayList<QueryOrderLineNodeListVo>();
        Integer sourceRegion = 0;
        Integer desRegion = 0;
        String sourceRegionName = "";
        String desRegionName = "";
        Float arrive = 0f;
        String nand = "";
        String nandDes = "";
        String eand = "";
        LocalDateTime carStartDate = null;
        LocalDateTime carArriveDate = null;
        String eandDes = "";
        List<OrderTransitLineInfo> transitLineInfos = null;
        SysStaticData sysStaticData = null;
        if (orderInfo == null) {
            QueryWrapper<OrderInfoH> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            OrderInfoH orderInfoH = orderInfoHMapper.selectOne(queryWrapper); // 订单历史
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            QueryWrapper<OrderSchedulerH> schedulerQueryWrapper = new QueryWrapper<>();
            schedulerQueryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            OrderSchedulerH orderSchedulerH = orderSchedulerHMapper.selectOne(schedulerQueryWrapper);//订单调度表 历史

            QueryWrapper<OrderGoodsH> goodsHQueryWrapper = new QueryWrapper<>();
            goodsHQueryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            OrderGoodsH orderGoodsH = orderGoodsHMapper.selectOne(goodsHQueryWrapper);//订单货物历史表

            carStartDate = orderSchedulerH.getCarStartDate();
            carArriveDate = orderSchedulerH.getCarArriveDate();
            arrive = orderSchedulerH.getArriveTime();
            sourceRegion = orderInfoH.getSourceRegion();
            desRegion = orderInfoH.getDesRegion();
            sysStaticData = getSysStaticData("SYS_CITY", sourceRegion + "");
            sourceRegionName = sysStaticData.getCodeName() + "(起始地)";
            sysStaticData = getSysStaticData("SYS_CITY", desRegion + "");
            desRegionName = sysStaticData.getCodeName() + "(目的地)";
            nand = orderGoodsH.getNand();
            nandDes = orderGoodsH.getNandDes();
            eand = orderGoodsH.getEand();
            eandDes = orderGoodsH.getEandDes();
            QueryWrapper<OrderTransitLineInfoH> infoHQueryWrapper = new QueryWrapper<>();
            infoHQueryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHMapper.selectList(infoHQueryWrapper);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                transitLineInfos = new ArrayList<>();
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    try {
                        BeanUtils.copyProperties(transitLineInfo, orderTransitLineInfoH);
                    } catch (Exception e) {
                        throw new BusinessException("数据");
                    }
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            QueryWrapper<OrderScheduler> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            OrderScheduler orderScheduler = orderSchedulerMapper.selectOne(queryWrapper);

            QueryWrapper<OrderGoods> goodsQueryWrapper = new QueryWrapper<>();
            goodsQueryWrapper.eq("order_id", orderId)
                    .eq("tenant_id", loginInfo.getTenantId());
            OrderGoods orderGoods = orderGoodsMapper.selectOne(goodsQueryWrapper);

            carStartDate = orderScheduler.getCarStartDate();
            carArriveDate = orderScheduler.getCarArriveDate();
            arrive = orderScheduler.getArriveTime();
            sourceRegion = orderInfo.getSourceRegion();
            desRegion = orderInfo.getDesRegion();
            sysStaticData = getSysStaticData("SYS_CITY", sourceRegion + "");
            if (sysStaticData != null && sysStaticData.getCodeName() != null) {
                sourceRegionName = sysStaticData.getCodeName() + "(起始地)";
            }
            sysStaticData = getSysStaticData("SYS_CITY", desRegion + "");
            desRegionName = sysStaticData.getCodeName() + "(目的地)";
            nand = orderGoods.getNand();
            nandDes = orderGoods.getNandDes();
            eand = orderGoods.getEand();
            eandDes = orderGoods.getEandDes();
            QueryWrapper<OrderTransitLineInfo> infoQueryWrapper = new QueryWrapper<>();
            infoQueryWrapper.eq("order_id", orderId);
            transitLineInfos = orderTransitLineInfoMapper.selectList(infoQueryWrapper);
        }
        if (transitLineInfos != null && transitLineInfos.size() > 0) {//有经停点
            for (int i = 0; i < transitLineInfos.size(); i++) {
                OrderTransitLineInfo transitLineInfo = transitLineInfos.get(i);
                if (i == 0) {//起始地
                    QueryOrderLineNodeListVo queryOrderLineNodeListVo = new QueryOrderLineNodeListVo();
                    queryOrderLineNodeListVo.setSourceRegion(sourceRegion);
                    queryOrderLineNodeListVo.setDesRegion(transitLineInfo.getRegion());
                    queryOrderLineNodeListVo.setArrive(transitLineInfo.getArriveTime());
                    queryOrderLineNodeListVo.setNand(nand);
                    queryOrderLineNodeListVo.setNandDes(transitLineInfo.getNand());
                    queryOrderLineNodeListVo.setEand(eand);
                    queryOrderLineNodeListVo.setEandDes(transitLineInfo.getEand());
                    SysStaticData city = readisUtil.getSysStaticData("SYS_CITY", transitLineInfo.getRegion() != null ? transitLineInfo.getRegion() + "" : "-1");
                    String cityCodeName = city != null ? city.getCodeName() : "";
                    queryOrderLineNodeListVo.setDetailLine(sourceRegionName + " -> " + cityCodeName + "(经停点" + (i + 1) + ")");

                    queryOrderLineNodeListVo.setCarStartDate(carStartDate == null ? null : carStartDate);
                    queryOrderLineNodeListVo.setCarArriveDate(transitLineInfo.getCarDependDate() == null ? null : transitLineInfo.getCarDependDate());
//                    Map map = new ConcurrentHashMap();
//                    map.put("sourceRegion", sourceRegion);
//                    map.put("desRegion", transitLineInfo.getRegion());
//                    map.put("arrive", transitLineInfo.getArriveTime());
//                    map.put("nand", nand);
//                    map.put("nandDes", transitLineInfo.getNand());
//                    map.put("eand", eand);
//                    map.put("eandDes", transitLineInfo.getEand());
//                    map.put("detailLine", sourceRegionName +" -> "+transitLineInfo.getRegion()+"(经停点"+(i+1)+")");
//                    map.put("carStartDate", carStartDate == null ? "" : carStartDate);
//                    map.put("carArriveDate", transitLineInfo.getCarDependDate() == null ? "": transitLineInfo.getCarDependDate());
                    listOut.add(queryOrderLineNodeListVo);
                }
                if (i != 0 && (i <= transitLineInfos.size() - 1)) {//经停点
                    OrderTransitLineInfo sourceTransitLineInfo = transitLineInfos.get(i - 1);
                    QueryOrderLineNodeListVo queryOrderLineNodeListVo = new QueryOrderLineNodeListVo();
                    queryOrderLineNodeListVo.setSourceRegion(sourceTransitLineInfo.getRegion());
                    queryOrderLineNodeListVo.setDesRegion(transitLineInfo.getRegion());
                    queryOrderLineNodeListVo.setArrive(transitLineInfo.getArriveTime());
                    queryOrderLineNodeListVo.setNand(sourceTransitLineInfo.getNand());
                    queryOrderLineNodeListVo.setNandDes(transitLineInfo.getNand());
                    queryOrderLineNodeListVo.setEand(sourceTransitLineInfo.getEand());
                    queryOrderLineNodeListVo.setEandDes(transitLineInfo.getEand());

                    SysStaticData sysCity = readisUtil.getSysStaticData("SYS_CITY", sourceTransitLineInfo.getRegion() != null ? sourceTransitLineInfo.getRegion() + "" : "-1");
                    SysStaticData city = readisUtil.getSysStaticData("SYS_CITY", transitLineInfo.getRegion() != null ? transitLineInfo.getRegion() + "" : "-1");
                    String cityData = sysCity != null ? sysCity.getCodeName() : "";
                    String cityCode = city != null ? city.getCodeName() : "";
                    queryOrderLineNodeListVo.setDetailLine(cityData + "(经停点" + i + ")" + " -> " + cityCode + "(经停点" + (i + 1) + ")");

                    queryOrderLineNodeListVo.setCarStartDate(sourceTransitLineInfo.getCarStartDate() == null ? null : sourceTransitLineInfo.getCarStartDate());
                    queryOrderLineNodeListVo.setCarArriveDate(transitLineInfo.getCarDependDate() == null ? null : transitLineInfo.getCarDependDate());

//                    Map map = new ConcurrentHashMap();
//                    map.put("sourceRegion", sourceTransitLineInfo.getRegion());
//                    map.put("desRegion", transitLineInfo.getRegion());
//                    map.put("arrive", transitLineInfo.getArriveTime());
//                    map.put("nand", sourceTransitLineInfo.getNand());
//                    map.put("nandDes", transitLineInfo.getNand());
//                    map.put("eand", sourceTransitLineInfo.getEand());
//                    map.put("eandDes", transitLineInfo.getEand());
//                    map.put("detailLine",sourceTransitLineInfo.getRegion()+"(经停点"+i+")" +" -> "+transitLineInfo.getRegion()+"(经停点"+(i+1)+")");
//                    map.put("carStartDate", sourceTransitLineInfo.getCarStartDate() == null ? "" : sourceTransitLineInfo.getCarStartDate());
//                    map.put("carArriveDate", transitLineInfo.getCarDependDate() == null ? "" :  transitLineInfo.getCarDependDate());
                    listOut.add(queryOrderLineNodeListVo);
                }
                if (i == transitLineInfos.size() - 1) {//目的地
                    QueryOrderLineNodeListVo queryOrderLineNodeListVo = new QueryOrderLineNodeListVo();
                    queryOrderLineNodeListVo.setSourceRegion(transitLineInfo.getRegion());
                    queryOrderLineNodeListVo.setDesRegion(desRegion);
                    queryOrderLineNodeListVo.setArrive(arrive);
                    queryOrderLineNodeListVo.setNand(transitLineInfo.getNand());
                    queryOrderLineNodeListVo.setNandDes(nandDes);
                    queryOrderLineNodeListVo.setEand(transitLineInfo.getEand());
                    queryOrderLineNodeListVo.setEandDes(eandDes);
                    SysStaticData city = readisUtil.getSysStaticData("SYS_CITY", transitLineInfo.getRegion() != null ? transitLineInfo.getRegion() + "" : "-1");
                    String cityCodeName = city != null ? city.getCodeName() : "";
                    queryOrderLineNodeListVo.setDetailLine(cityCodeName + "(经停点" + (i + 1) + ")" + " -> " + desRegionName);

                    queryOrderLineNodeListVo.setCarStartDate(transitLineInfo.getCarStartDate() == null ? null : transitLineInfo.getCarStartDate());
                    queryOrderLineNodeListVo.setCarArriveDate(carArriveDate == null ? null : carArriveDate);


//                    Map map = new ConcurrentHashMap();
//                    map.put("sourceRegion", transitLineInfo.getRegion());
//                    map.put("desRegion", desRegion);
//                    map.put("arrive", arrive);
//                    map.put("nand", transitLineInfo.getNand());
//                    map.put("nandDes", nandDes);
//                    map.put("eand", transitLineInfo.getEand());
//                    map.put("eandDes", eandDes);
//                    map.put("carStartDate", transitLineInfo.getCarStartDate() == null ? "" : transitLineInfo.getCarStartDate());
//                    map.put("carArriveDate", carArriveDate == null ? "" : carArriveDate);
//                    map.put("detailLine",transitLineInfo.getRegion()+"(经停点"+(i+1)+")" +" -> "+desRegionName);
                    listOut.add(queryOrderLineNodeListVo);
                }
            }
        } else {
            QueryOrderLineNodeListVo queryOrderLineNodeListVo = new QueryOrderLineNodeListVo();
            queryOrderLineNodeListVo.setSourceRegion(sourceRegion);
            queryOrderLineNodeListVo.setDesRegion(desRegion);
            queryOrderLineNodeListVo.setArrive(arrive);
            queryOrderLineNodeListVo.setNand(nand);
            queryOrderLineNodeListVo.setNandDes(nandDes);
            queryOrderLineNodeListVo.setEand(eand);
            queryOrderLineNodeListVo.setEandDes(eandDes);
            queryOrderLineNodeListVo.setDetailLine(sourceRegionName + " -> " + desRegionName);
            queryOrderLineNodeListVo.setCarStartDate(carStartDate == null ? null : carStartDate);
            queryOrderLineNodeListVo.setCarArriveDate(carArriveDate == null ? null : carArriveDate);


//            Map map = new ConcurrentHashMap();
//            map.put("sourceRegion", sourceRegion);
//            map.put("desRegion", desRegion);
//            map.put("arrive", arrive);
//            map.put("nand", nand);
//            map.put("nandDes", nandDes);
//            map.put("eand", eand);
//            map.put("eandDes", eandDes);
//            map.put("detailLine", sourceRegionName +" -> "+desRegionName);
//            map.put("carStartDate", carStartDate == null ? "" : carStartDate);
//            map.put("carArriveDate", carArriveDate == null ? "" : carArriveDate);
            listOut.add(queryOrderLineNodeListVo);
        }
        return listOut;
    }

    @Override
    public OrderCostRetrographyDto orderCostRetrography(long orderId, boolean isSelect, String accessToken) {
        OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        Date dependTime = null;
        String plateNumber = null;
        boolean isCalculate = false;//自有车实报实销才反写
        if (orderScheduler == null) {
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOrderInfoExtH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]订单信息！");
            }
            dependTime = getLocalDateTimeToDate(orderSchedulerH.getDependTime());
            plateNumber = orderSchedulerH.getPlateNumber();
            if (orderSchedulerH.getVehicleClass() != null && orderSchedulerH.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExtH.getPaymentWay() != null && orderInfoExtH.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                isCalculate = true;
            }
        } else {
            dependTime = getLocalDateTimeToDate(orderScheduler.getDependTime());
            plateNumber = orderScheduler.getPlateNumber();
            if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.EXPENSE) {
                isCalculate = true;
            }
        }
        if (isCalculate) {
            List<OrderVehicleTimeNode> vehicleTimeNodes = null;
            if (isSelect) {
                vehicleTimeNodes = new ArrayList<>();
                OrderVehicleTimeNode orderVehicleTimeNode = iOrderVehicleTimeNodeService.queryOrderVehicleTimeNode(plateNumber, dependTime, DateUtil.formatDate(dependTime, DateUtil.YEAR_MONTH_FORMAT), orderId);
                if (orderVehicleTimeNode != null) {
                    vehicleTimeNodes.add(orderVehicleTimeNode);
                }
            } else {
                vehicleTimeNodes = iOrderVehicleTimeNodeService.queryOrderVehicleTimeNodeByMonth(plateNumber, null, DateUtil.formatDate(dependTime, DateUtil.YEAR_MONTH_FORMAT), false);
            }
            return iOrderVehicleTimeNodeService.setOrderCostFeeByTimeNode(vehicleTimeNodes, isSelect, accessToken);//反写成本
        }
        return new OrderCostRetrographyDto();
    }

    @Override
    public Map<String, Object> getNextOrderIdByPlateNumber(String plateNumber, Date dependTime, Long tenantId, Long orderId) {
        OrderScheduler orderScheduler = this.getNextOrderSchedulerByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("传入的车牌号为空");
        }
        if (tenantId == null) {
            throw new BusinessException("传入的车队为空");
        }
        OrderSchedulerH orderSchedulerH = orderSchedulerHMapper.getNextOrderSchedulerHByPlateNumber(plateNumber, dependTime, tenantId, orderId);
        return compareScheduler(orderScheduler, orderSchedulerH, true);
    }

    @Override
    public OrderScheduler getNextOrderSchedulerByPlateNumber(String plateNumber, Date dependTime, Long tenantId, Long orderId) {
        LambdaQueryWrapper<OrderScheduler> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(plateNumber)) {
            queryWrapper.eq(OrderScheduler::getPlateNumber, plateNumber);
        } else {
            throw new BusinessException("传入的车牌号为空");
        }
        if (dependTime != null) {
            queryWrapper.ge(OrderScheduler::getDependTime, dependTime);
        }
        if (orderId != null && orderId > 0) {
            queryWrapper.ne(OrderScheduler::getOrderId, orderId);
        }
        if (tenantId != null) {
            queryWrapper.eq(OrderScheduler::getTenantId, tenantId);
        } else {
            throw new BusinessException("传入的车队为空");
        }

        queryWrapper.orderByAsc(OrderScheduler::getDependTime);
        List<OrderScheduler> list = this.list();
        if (list != null && list.size() == 1) {
            OrderScheduler scheduler = list.get(0);
            return scheduler;
        }
        return new OrderScheduler();
    }

    @Override
    public Boolean checkPayMentWaySwitchover(String plateNumber, String dependTime, String accessToken, Long orderId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(dependTime)) {
            throw new BusinessException("请填写靠台时间！");
        }
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("车牌号不能为空！");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime parsedDate = LocalDateTime.parse(dependTime, formatter);
        Boolean checkBoolean = this.checkPayMentWaySwitchover(plateNumber, parsedDate, loginInfo.getTenantId(), orderId);

        return checkBoolean;
    }

    @Override
    public OrderScheduler getOrderSchedulerByPlateNumber(String plateNumber) {
        OrderScheduler orderScheduler = new OrderScheduler();
        QueryWrapper<OrderScheduler> orderSchedulerQueryWrapper = new QueryWrapper<>();
        orderSchedulerQueryWrapper.eq("plate_number", plateNumber)
                .orderByDesc("create_time");
        List<OrderScheduler> list = this.list(orderSchedulerQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return orderScheduler;
    }

    @Override
    public void judgeVehicleOrder(String plateNumbers, Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotBlank(plateNumbers)) {
            OrderScheduler orderSchedulerByPlateNumber = orderSchedulerService.getOrderSchedulerByPlateNumber(plateNumbers);
            if (orderSchedulerByPlateNumber != null && orderSchedulerByPlateNumber.getOrderId() != null) {
                OrderInfo order = orderInfoService.getOrder(orderSchedulerByPlateNumber.getOrderId());
                if (order != null && order.getOrderState() != 4 || order.getOrderState() != 7) {
                    throw new BusinessException("所选车辆有未完成的订单");
                }
            }
        }
        if (userId != null) {
            QueryWrapper<OrderScheduler> orderSchedulerQueryWrapper = new QueryWrapper<>();
            orderSchedulerQueryWrapper.eq("car_driver_id", userId).
                    or().eq("copilot_user_id", userId).
                    eq("tenant_id", loginInfo.getTenantId())
                    .orderByDesc("create_time");

            List<OrderScheduler> list = this.list(orderSchedulerQueryWrapper);
            if (list != null && list.size() > 0) {
                OrderScheduler orderScheduler = list.get(0);
                if (orderScheduler != null) {
                    OrderInfo order = orderInfoService.getOrder(orderScheduler.getOrderId());
                    if (order != null && order.getOrderState() != 4 || order.getOrderState() != 7) {
                        throw new BusinessException("所选司机有未完成的订单");
                    }
                }

            }

        }

    }

    @Override
    public Integer queryVehicleGpsType(Long vehicleCode) {
        int gpsType = OrderConsts.GPS_TYPE.APP;
        if (vehicleCode != null) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(vehicleCode);
            if (vehicleDataInfo != null) {
                if (vehicleDataInfo.getLocationServ() != null) {
                    if (vehicleDataInfo.getLocationServ() == OrderConsts.GPS_TYPE.G7) {
                        gpsType = OrderConsts.GPS_TYPE.G7;
                    } else if (vehicleDataInfo.getLocationServ() == OrderConsts.GPS_TYPE.YL) {
                        gpsType = OrderConsts.GPS_TYPE.YL;
                    } else if (vehicleDataInfo.getLocationServ() == OrderConsts.GPS_TYPE.BD) {
                        gpsType = OrderConsts.GPS_TYPE.BD;
                    }
                }
            }
        }
        return gpsType;
    }

    @Override
    public Map<String, Object> queryOrderTrackLocation(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        Map<String, Object> map = new ConcurrentHashMap<String, Object>();
        Double estStartTime = Double.parseDouble(String.valueOf(sysCfgRedisUtils.getCfgVal(-1L, "ESTIMATE_START_TIME", 0, String.class, null)));
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if (orderInfo != null) {
            OrderGoods orderGoods = orderGoodsService.getOrderGoods(orderId);
            OrderScheduler orderScheduler = this.getOrderScheduler(orderId);
            if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_LOAD) {//待装货
                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
                map.put("eand", orderGoods.getEand());
                map.put("nand", orderGoods.getNand());
                map.put("lineType", 0);
                map.put("lineDetail", orderGoods.getAddrDtl());
                map.put("estDate", orderScheduler.getDependTime());
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.LOADING) {//装货中
                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
                map.put("eand", orderGoods.getEand());
                map.put("nand", orderGoods.getNand());
                map.put("lineType", 0);
                map.put("lineDetail", orderGoods.getAddrDtl());
                map.put("estDate", orderScheduler.getCarDependDate() == null
                        ? orderScheduler.getDependTime()
                        : orderScheduler.getCarDependDate());
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.TRANSPORT_ING) {//运输中
                List<OrderTransitLineInfo> list = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
                if (list != null && list.size() > 0) {
                    boolean isArrive = true;
                    for (int i = list.size() - 1; i >= 0; i--) {
                        OrderTransitLineInfo transitLineInfo = list.get(i);
                        if (transitLineInfo.getCarDependDate() != null && transitLineInfo.getCarStartDate() == null) {
                            map.put("trackType", OrderConsts.TRACK_TYPE.TYPE2);
                            map.put("eand", transitLineInfo.getEand());
                            map.put("nand", transitLineInfo.getNand());
                            map.put("lineType", transitLineInfo.getSortId());
                            map.put("lineDetail", transitLineInfo.getAddress());
                            map.put("realDependDate", transitLineInfo.getCarDependDate());
                            map.put("estDate", orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), estStartTime.floatValue()));
                            isArrive = false;
                            break;
                        } else if (transitLineInfo.getCarDependDate() == null) {
                            if (i == 0) {
                                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
                                map.put("eand", transitLineInfo.getEand());
                                map.put("nand", transitLineInfo.getNand());
                                map.put("lineType", transitLineInfo.getSortId());
                                map.put("lineDetail", transitLineInfo.getAddress());
                                map.put("estDate", orderDateUtil.addHourAndMins((orderScheduler.getCarDependDate() == null
                                        ? orderScheduler.getDependTime()
                                        : orderScheduler.getCarDependDate()), transitLineInfo.getArriveTime()));
                                isArrive = false;
                                break;
                            } else {
                                OrderTransitLineInfo lastTransitLineInfo = list.get(i - 1);
                                if (lastTransitLineInfo.getCarDependDate() != null && lastTransitLineInfo.getCarStartDate() != null) {
                                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE1);
                                    map.put("eand", transitLineInfo.getEand());
                                    map.put("nand", transitLineInfo.getNand());
                                    map.put("lineType", transitLineInfo.getSortId());
                                    map.put("lineDetail", transitLineInfo.getAddress());
                                    map.put("estDate", orderDateUtil.addHourAndMins(lastTransitLineInfo.getCarStartDate(), transitLineInfo.getArriveTime()));
                                    isArrive = false;
                                    break;
                                }
                            }
                        } else if (i == list.size() - 1) {
                            break;
                        }
                    }
                    if (isArrive) {//经停点已完成
                        OrderTransitLineInfo transitLineInfo = list.get(list.size() - 1);
                        map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
                        map.put("eand", orderGoods.getEandDes());
                        map.put("nand", orderGoods.getNandDes());
                        map.put("lineDetail", orderGoods.getDesDtl());
                        map.put("lineType", 0);
                        map.put("estDate", orderDateUtil.addHourAndMins(transitLineInfo.getCarStartDate(), orderScheduler.getArriveTime()));
                    }
                } else {
                    map.put("trackType", OrderConsts.TRACK_TYPE.TYPE3);
                    map.put("eand", orderGoods.getEandDes());
                    map.put("nand", orderGoods.getNandDes());
                    map.put("lineDetail", orderGoods.getDesDtl());
                    map.put("lineType", 0);
                    map.put("estDate", orderDateUtil.addHourAndMins(orderScheduler.getCarStartDate(), orderScheduler.getArriveTime()));
                }
            } else if (orderInfo.getOrderState() == OrderConsts.ORDER_STATE.ARRIVED) {//已到达
                map.put("trackType", OrderConsts.TRACK_TYPE.TYPE4);
                map.put("eand", orderGoods.getEandDes());
                map.put("nand", orderGoods.getNandDes());
                map.put("lineDetail", orderGoods.getDesDtl());
                map.put("lineType", 0);
                map.put("estDate", orderScheduler.getCarArriveDate());
            }
            return map;
        } else {//历史单不需轨迹操作
            return null;
        }
    }

    @Override
    public Map queryLastStartInfo(Long orderId, int lineType) {
        Double estStartTime = Double.parseDouble(String.valueOf(sysCfgRedisUtils.getCfgVal(-1L, "ESTIMATE_START_TIME", 0, String.class, null)));
//        Date lastDate = null;
        LocalDateTime lastDate = null;
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不能为空！");
        }
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        List<OrderTransitLineInfo> transitLineInfos = null;
//        Date carStartDate = null;
        LocalDateTime carStartDate = null;
        String eand = null;
        String nand = null;
        String billId = null;
        Long vehicleCode = null;
        if (orderInfo == null) {
            OrderInfoH orderInfoH = orderInfoHService.getOrderH(orderId);
            if (orderInfoH == null) {
                throw new BusinessException("未找到订单[" + orderId + "]信息");
            }
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                transitLineInfos = new ArrayList<>();
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    BeanUtil.copyProperties(orderTransitLineInfoH, transitLineInfo);
                    transitLineInfos.add(transitLineInfo);
                }
            }
            billId = orderSchedulerH.getCarDriverPhone();
            vehicleCode = orderSchedulerH.getVehicleCode();
            if (orderSchedulerH.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                billId = orderSchedulerH.getOnDutyDriverPhone();//自有车取当值司机
            }
            carStartDate = orderSchedulerH.getCarStartDate() == null ?
                    orderDateUtil.addHourAndMins((orderSchedulerH.getCarDependDate() == null ? orderSchedulerH.getDependTime() : orderSchedulerH.getCarDependDate()), estStartTime.floatValue())
                    : orderSchedulerH.getCarStartDate();
        } else {
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            billId = orderScheduler.getCarDriverPhone();
            if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                billId = orderScheduler.getOnDutyDriverPhone();//自有车取当值司机
            }
            vehicleCode = orderScheduler.getVehicleCode();
            carStartDate = orderScheduler.getCarStartDate() == null ?
                    orderDateUtil.addHourAndMins((orderScheduler.getCarDependDate() == null ? orderScheduler.getDependTime() : orderScheduler.getCarDependDate()), estStartTime.floatValue())
                    : orderScheduler.getCarStartDate();
            transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
        }
        String equipmentNumber = null;
        if (vehicleCode != null && vehicleCode > 0) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(vehicleCode);
            if (vehicleDataInfo != null) {
                if (vehicleDataInfo.getLicenceType() != null && vehicleDataInfo.getLicenceType().intValue() != OrderConsts.GPS_TYPE.APP) {
                    equipmentNumber = vehicleDataInfo.getPlateNumber();
                } else {
                    equipmentNumber = billId;
                }
            }
        }
        int num = 0;
        if (lineType > 0) {
            num = lineType - 1;
        } else if (lineType == -1) {
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                num = transitLineInfos.size() - 1;
            }
        }
        if (transitLineInfos != null && transitLineInfos.size() > 0) {
            for (int i = num; i >= 0; i--) {
                OrderTransitLineInfo transitLineInfo = transitLineInfos.get(i);
                eand = transitLineInfo.getEand();
                nand = transitLineInfo.getNand();
                if (transitLineInfo.getCarStartDate() != null) {
                    lastDate = transitLineInfo.getCarStartDate();
                    break;
                } else if (transitLineInfo.getCarDependDate() != null) {
                    lastDate = orderDateUtil.addHourAndMins(transitLineInfo.getCarDependDate(), estStartTime.floatValue());
                    break;
                }
                if (i == 0) {
                    lastDate = carStartDate;
                }
            }
        } else {
            lastDate = carStartDate;
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(equipmentNumber) && carStartDate != null) {
            Map perMap = null/*dataSV.getLocation(equipmentNumber, DateUtil.formatDate(carStartDate, DateUtil.YEAR_MONTH_FORMAT2), carStartDate)*/;
            // 两公里最大点
            if (perMap != null && org.apache.commons.lang.StringUtils.isNotBlank(DataFormat.getStringKey(perMap, "sourceNand")) && org.apache.commons.lang.StringUtils.isNotBlank(DataFormat.getStringKey(perMap, "sourceEand"))) {
                nand = DataFormat.getStringKey(perMap, "sourceNand");
                eand = DataFormat.getStringKey(perMap, "sourceEand");
            }
        }
        Map map = new ConcurrentHashMap();
        if (lastDate != null) {
            map.put("lastDate", lastDate);
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(eand)) {
            map.put("eand", eand);
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(nand)) {
            map.put("nand", nand);
        }
        return map;
    }

    @Override
    public LocalDateTime getOrderEstimateDate(Long orderId, LocalDateTime dependTime, LocalDateTime carStartDate, Float arriveTime, Boolean isHis, int nodeId) {
        Double estStartTime = Double.parseDouble(String.valueOf(sysCfgRedisUtils.getCfgVal(-1L, "ESTIMATE_START_TIME", 0, String.class, null)));
        if (nodeId == 0) {
            return dependTime;
        }
        LocalDateTime orderArriveDate = carStartDate == null ? orderDateUtil.addHourAndMins(dependTime, estStartTime.floatValue()) : carStartDate;
        if (isHis) {
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                for (int i = 0; i < transitLineInfoHs.size(); i++) {
                    OrderTransitLineInfoH orderTransitLineInfoH = transitLineInfoHs.get(i);
                    if (orderTransitLineInfoH.getCarDependDate() == null) {
                        orderArriveDate = orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfoH.getArriveTime()), estStartTime.floatValue());
                    } else {
                        if (orderTransitLineInfoH.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfoH.getCarDependDate(), estStartTime.floatValue());
                        } else {
                            orderArriveDate = orderTransitLineInfoH.getCarStartDate();
                        }
                    }
                    if (orderTransitLineInfoH.getSortId() == nodeId) {
                        break;
                    }
                }
            }
        } else {
            List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (int i = 0; i < transitLineInfos.size(); i++) {
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(i);
                    if (orderTransitLineInfo.getCarDependDate() == null) {
                        orderArriveDate = orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfo.getArriveTime()), estStartTime.floatValue());
                    } else {
                        if (orderTransitLineInfo.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarDependDate(), estStartTime.floatValue());
                        } else {
                            orderArriveDate = orderTransitLineInfo.getCarStartDate();
                        }
                    }
                    if (orderTransitLineInfo.getSortId() == nodeId) {
                        break;
                    }
                }
            }
        }
        if (nodeId == -1) {
            orderArriveDate = orderDateUtil.addHourAndMins(orderArriveDate, arriveTime);
        }
        return orderArriveDate;
    }

}
