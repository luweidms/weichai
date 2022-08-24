package com.youming.youche.order.provider.service.order;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.common.GpsUtil;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.provider.mapper.order.OrderInfoExtMapper;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.order.vo.OrderVerifyInfoOut;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 订单扩展表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
@Service
public class OrderInfoExtServiceImpl extends BaseServiceImpl<OrderInfoExtMapper, OrderInfoExt> implements IOrderInfoExtService {
    @Lazy
    @Resource
    private IBillPlatformService billPlatformService;
    @Lazy
    @Resource
    private IOrderSchedulerService orderSchedulerService;
    @Lazy
    @Resource
    private ReadisUtil readisUtil;
    @Lazy
    @Resource
    private OrderDateUtil orderDateUtil;
    @Lazy
    @Resource
    private IOrderTransitLineInfoHService orderTransitLineInfoHService;
    @Lazy
    @Resource
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Lazy
    @Resource
    private OrderInfoExtMapper orderInfoExtMapper;
    @Lazy
    @Resource
    private IOrderInfoService orderInfoService;
    @Lazy
    @Resource
    private IOrderInfoExtService orderInfoExtService;
    @Lazy
    @Resource
    private IOrderFeeService orderFeeService;
    @Lazy
    @Resource
    private IOrderGoodsService orderGoodsService;

    @Override
    public OrderInfoExt getOrderInfoExt(Long orderId) {
        LambdaQueryWrapper<OrderInfoExt> lambda=new QueryWrapper<OrderInfoExt>().lambda();
        lambda.eq(OrderInfoExt::getOrderId,orderId);
        List<OrderInfoExt> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public OrderInfoExt getOrderInfoPaymentWay(Long orderId, Long tenantId) {
        LambdaQueryWrapper<OrderInfoExt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderInfoExt::getOrderId,orderId)
                .eq(OrderInfoExt::getTenantId,tenantId);
        List<OrderInfoExt> list = this.list(wrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public OrderInfoExt selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderInfoExt> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderInfoExt::getOrderId,orderId);
        return getOne(wrapper,false);
    }

    @Override
    public void verifyOrderNeedBill(OrderInfo orderInfo, OrderInfoExt orderInfoExt, OrderScheduler orderScheduler, OrderFee orderFee, OrderGoods orderGoods, List<OrderTransitLineInfo> transitLineInfos, LoginInfo user) {
        if (orderInfo.getIsNeedBill() != null && orderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {

            BillPlatform billPlatform = billPlatformService.queryAllBillPlatformByUserId(Long.valueOf(orderFee.getVehicleAffiliation()));
            if (billPlatform == null) {
                throw new BusinessException("未找到对应票票据平台！");
            }
            Long totalFee = orderFee.getTotalFee();//订单运费
            if (orderScheduler.getVehicleClass() != null
                    && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                    && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() != OrderConsts.PAYMENT_WAY.CONTRACT
            ) {
                totalFee = orderFee.getCostPrice();
            }
            if (totalFee == null) {
                totalFee = 0L;
            }
            String waypoints ="";//经停点经纬度
            Float arriveTime  = orderScheduler.getArriveTime();//到达时效
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (int i = 0; i < transitLineInfos.size(); i++) {
                    OrderTransitLineInfo orderTransitLineInfo = transitLineInfos.get(i);
                    waypoints += orderTransitLineInfo.getNand()+","+orderTransitLineInfo.getEand() + (i !=  transitLineInfos.size()-1 ? "|" : "");
                    arriveTime += orderTransitLineInfo.getArriveTime();
                }
            }
                Map directionMap = GpsUtil.getDirectionV2(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()), Double.parseDouble(orderGoods.getEandDes()), Double.parseDouble(orderGoods.getNandDes()), waypoints, 0);
                Double distance = CommonUtils.getDoubleFormat(Double.parseDouble(DataFormat.getStringKey(directionMap, "distance"))/1000.0,3);//本单距离

            //运输时效校验
            if (billPlatform.getTravelSpeed() != null && billPlatform.getTravelSpeed().doubleValue() > 0){
                double speedStandard = billPlatform.getTravelSpeed().doubleValue();
                OrderVerifyInfoOut lastCarOrder = null;
                OrderVerifyInfoOut nextCarOrder = null;
                if (StringUtils.isNotBlank(orderScheduler.getPlateNumber())) {


                    lastCarOrder = this.queryOrderVerifyInfoOut(orderScheduler.getPlateNumber(), orderInfo.getOrderId(), orderScheduler.getDependTime(),null, null, true);
                    nextCarOrder = this.queryOrderVerifyInfoOut(orderScheduler.getPlateNumber(), orderInfo.getOrderId(), orderScheduler.getDependTime(),null, null, false);
                }

                OrderVerifyInfoOut lastDriverOrder = null;
                OrderVerifyInfoOut nextDriverOrder = null;
                if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
                    lastDriverOrder = this.queryOrderVerifyInfoOut(null, orderInfo.getOrderId(), orderScheduler.getDependTime(),null, orderScheduler.getCarDriverId(), true);
                    nextDriverOrder = this.queryOrderVerifyInfoOut(null, orderInfo.getOrderId(), orderScheduler.getDependTime(),null, orderScheduler.getCarDriverId(), false);
                }

                //线路冲突校验
                this.lineClashVerify(lastCarOrder, nextCarOrder, orderScheduler,orderGoods,transitLineInfos, false,speedStandard,user);
                this.lineClashVerify(lastDriverOrder, nextDriverOrder, orderScheduler,orderGoods,transitLineInfos, true,speedStandard,user);

                double transitTime = CommonUtils.getDoubleFormat(distance / speedStandard, 2);//运输时效
                if (transitTime > arriveTime) {
                    throw new BusinessException("订单的到达时效太短（不能小于"+transitTime+"小时），请重新设置。");
                }
            }
            double minFeeStandard = 0.0;
            double maxFeeStandard = 0.0;
            if (billPlatform.getMinTravelFee() != null && billPlatform.getMinTravelFee().doubleValue() > 0) {
                minFeeStandard = billPlatform.getMinTravelFee().doubleValue();
            }
            if (billPlatform.getMaxTravelFee() != null && billPlatform.getMaxTravelFee().doubleValue() > 0) {
                maxFeeStandard = billPlatform.getMaxTravelFee().doubleValue();
            }
            if (StringUtils.isNotBlank(orderScheduler.getPlateNumber()) || (orderScheduler.getAppointWay() != null &&
                    orderScheduler.getAppointWay().intValue() == OrderConsts.AppointWay.APPOINT_TENANT
            )) {
                double minTotalFee = CommonUtils.getDoubleFormat(distance * minFeeStandard,2);
                double maxTotalFee = CommonUtils.getDoubleFormat(distance * maxFeeStandard,2);
                if (maxTotalFee == 0) {
                    if (minTotalFee >  CommonUtils.getDoubleFormatLongMoney(totalFee, 2)) {
                        throw new BusinessException("运输费用太低(建议运费大于等于"+minTotalFee+"元)，请重新调整。");
                    }
                }else{
                    if (minTotalFee >  CommonUtils.getDoubleFormatLongMoney(totalFee, 2)
                            ||   CommonUtils.getDoubleFormatLongMoney(totalFee, 2) > maxTotalFee) {
                        throw new BusinessException("运输费用"+(minTotalFee >  CommonUtils.getDoubleFormatLongMoney(totalFee, 2) ? "太低" :"太高")+"(建议运费区间"+minTotalFee+"-"+maxTotalFee+"元)，请重新调整。");
                    }
                }
            }
        }else{
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                OrderInfo fromOrderInfo = orderInfoService.getOrder(orderInfo.getFromOrderId());
                if (fromOrderInfo != null &&  fromOrderInfo.getIsNeedBill() != null && fromOrderInfo.getIsNeedBill().intValue() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    OrderInfoExt fromOrderInfoExt = orderInfoExtService.getOrderInfoExt(orderInfo.getFromOrderId());
                    OrderScheduler fromOrderScheduler = orderSchedulerService.getOrderScheduler(orderInfo.getFromOrderId());
                    OrderFee fromOrderFee = orderFeeService.getOrderFee(orderInfo.getFromOrderId());
                    OrderGoods fromOrderGoods = orderGoodsService.getOrderGoods(orderInfo.getFromOrderId());
                    List<OrderTransitLineInfo> transitLineInfos2 = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getFromOrderId());
                    this.verifyOrderNeedBill(fromOrderInfo, fromOrderInfoExt, fromOrderScheduler, fromOrderFee, fromOrderGoods, transitLineInfos2,user);
                }
            }
        }
    }

    @Override
    public OrderVerifyInfoOut queryOrderVerifyInfoOut(String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder) {

        OrderVerifyInfoOut orderVerifyInfoOut = this.getOrderVerifyInfoOut(plateNumber, orderId, dependTime, orderStateLT, userId, isQueryLastOrder);
        OrderVerifyInfoOut orderVerifyInfoOutH = this.getOrderVerifyInfoOutH(plateNumber, orderId, dependTime, orderStateLT, userId, isQueryLastOrder);

        if (orderVerifyInfoOutH != null && orderVerifyInfoOut != null) {
            if (isQueryLastOrder) {
                if (orderVerifyInfoOutH.getDependTime().isAfter(orderVerifyInfoOut.getDependTime())) {
                    return orderVerifyInfoOutH;
                }else{
                    return orderVerifyInfoOut;
                }
            }else{
                if (orderVerifyInfoOutH.getDependTime().isAfter(orderVerifyInfoOut.getDependTime())) {
                    return orderVerifyInfoOut;
                }else{
                    return orderVerifyInfoOutH;
                }
            }
        }else{
            return orderVerifyInfoOutH == null ? orderVerifyInfoOut : orderVerifyInfoOutH;
        }
    }

    @Override
    public OrderVerifyInfoOut getOrderVerifyInfoOut(String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder) {
        List<OrderVerifyInfoOut> list = orderInfoExtMapper.getOrderVerifyInfoOut(plateNumber, orderId, dependTime, orderStateLT, userId, isQueryLastOrder);
        OrderVerifyInfoOut orderVerifyInfoOut = null;
        if (list != null && list.size() > 0) {
            orderVerifyInfoOut = new OrderVerifyInfoOut();
            try{
                BeanUtils.copyProperties(orderVerifyInfoOut, list.get(0));
                return orderVerifyInfoOut;
            } catch (Exception e) {
                log.error("queryOrderVerifyInfoOut数据有误");
            }
        }
        return orderVerifyInfoOut;
    }

    @Override
    public OrderVerifyInfoOut getOrderVerifyInfoOutH(String plateNumber, Long orderId, LocalDateTime dependTime, Integer orderStateLT, Long userId, boolean isQueryLastOrder) {

        List<OrderVerifyInfoOut> list = orderInfoExtMapper.getOrderVerifyInfoOutH(orderId, orderStateLT);
        OrderVerifyInfoOut orderVerifyInfoOut = null;
        if (list != null && list.size() > 0) {
            orderVerifyInfoOut = new OrderVerifyInfoOut();
            try{
                BeanUtils.copyProperties(orderVerifyInfoOut, list.get(0));
                return orderVerifyInfoOut;
            } catch (Exception e) {
                log.error("queryOrderVerifyInfoOutH数据有误");
            }
        }
        return orderVerifyInfoOut;
    }

    @Override
    public LocalDateTime getOrderArriveDate(Long orderId, LocalDateTime dependTime, LocalDateTime carStartDate, Float arriveTime, Boolean isHis) {
        Double estStartTime =  Double.parseDouble(String.valueOf(readisUtil.getSysCfg("ESTIMATE_START_TIME","0").getCfgValue()));
        LocalDateTime orderArriveDate = carStartDate ==null ? orderDateUtil.addHourAndMins(dependTime, estStartTime.floatValue()) : carStartDate;
        if (isHis) {
            List<OrderTransitLineInfoH> transitLineInfoHs = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderId);
            if (transitLineInfoHs != null && transitLineInfoHs.size() > 0) {
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfoHs) {
                    if (orderTransitLineInfoH.getCarDependDate() == null) {
                        orderArriveDate =  orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfoH.getArriveTime()), estStartTime.floatValue());
                    }else{
                        if (orderTransitLineInfoH.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfoH.getCarDependDate(), estStartTime.floatValue());
                        }else{
                            orderArriveDate = orderTransitLineInfoH.getCarStartDate();
                        }
                    }
                }
            }
        }else{
            List<OrderTransitLineInfo> transitLineInfos =orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            if (transitLineInfos != null && transitLineInfos.size() > 0) {
                for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                    if (orderTransitLineInfo.getCarDependDate() == null) {
                        orderArriveDate =  orderDateUtil.addHourAndMins(orderDateUtil.addHourAndMins(orderArriveDate, orderTransitLineInfo.getArriveTime()), estStartTime.floatValue());
                    }else{
                        if (orderTransitLineInfo.getCarStartDate() == null) {
                            orderArriveDate = orderDateUtil.addHourAndMins(orderTransitLineInfo.getCarDependDate(), estStartTime.floatValue());
                        }else{
                            orderArriveDate = orderTransitLineInfo.getCarStartDate();
                        }
                    }
                }
            }
        }
        orderArriveDate = orderDateUtil.addHourAndMins(orderArriveDate, arriveTime);
        return orderArriveDate;
    }


    /**
     * 线路冲突校验
     * @param lastOrder 上一单
     * @param nextOrder 下一单
     * @param orderScheduler
     * @param orderGoods
     * @param transitLineInfos
     * @param isDriver 是否司机
     * @throws Exception
     */
    public void lineClashVerify(OrderVerifyInfoOut lastOrder,OrderVerifyInfoOut nextOrder,
                                 OrderScheduler orderScheduler,OrderGoods orderGoods,
                                 List<OrderTransitLineInfo> transitLineInfos,Boolean isDriver,double speedStandard,LoginInfo user){
        String des = isDriver ? "司机" : "车辆";
        //上一单
        if (lastOrder != null && lastOrder.getOrderId() != null && lastOrder.getOrderId() > 0) {
            LocalDateTime arriveDate = lastOrder.getCarArriveDate();
            if (arriveDate == null) {

                arriveDate = this.getOrderArriveDate(lastOrder.getOrderId(),
                        lastOrder.getCarDependDate() == null ? lastOrder.getDependTime() : lastOrder.getCarDependDate(),
                        lastOrder.getCarStartDate(), lastOrder.getArriveTime(), lastOrder.getIsHis());
            }
            if (arriveDate.isBefore(orderScheduler.getDependTime())) {
                throw new BusinessException("承运"+des+"在"+DateUtil.formatDate(this.getDateByLocalDateTime(lastOrder.getDependTime()), DateUtil.DATETIME_FORMAT)+"-"+DateUtil.formatDate(this.getDateByLocalDateTime(arriveDate), DateUtil.DATETIME_FORMAT)+" 已承运上一订单("+lastOrder.getOrderId()+")，无法承运该订单。");
            }
                Map directionMap = GpsUtil.getDirectionV2(Double.parseDouble(orderGoods.getEand()), Double.parseDouble(orderGoods.getNand()), Double.parseDouble(lastOrder.getEandDes()), Double.parseDouble(lastOrder.getNandDes()), null, 0);
                //上单目的地到本单空驶距离
                Double distance = CommonUtils.getDoubleFormat(Double.parseDouble(DataFormat.getStringKey(directionMap, "distance"))/1000.0,3);
                if (((distance / speedStandard) * 3600000) > (orderScheduler.getDependTime().toInstant(ZoneOffset.of("+8")).toEpochMilli() - arriveDate.toInstant(ZoneOffset.of("+8")).toEpochMilli())) {
                    throw new BusinessException("承运"+des+"的上单("+lastOrder.getOrderId()+")目的地("+lastOrder.getDesRegionName()+")距离本单的靠台地太远，间隔时间太短，"+des+"无法返回承运本单。");
                }

        }
        //下一单
        if (nextOrder != null && nextOrder.getOrderId() != null && nextOrder.getOrderId() > 0) {
            LocalDateTime dependDate = nextOrder.getDependTime();
            LocalDateTime nextArriveDate = nextOrder.getCarArriveDate();
            if (nextArriveDate == null) {
                this.getOrderArriveDate(nextOrder.getOrderId(),
                        nextOrder.getCarDependDate() == null ? nextOrder.getDependTime() : nextOrder.getCarDependDate(),
                        nextOrder.getCarStartDate(), nextOrder.getArriveTime(), nextOrder.getIsHis());
            }
            LocalDateTime arriveDate = orderScheduler.getCarArriveDate();
            if (arriveDate == null) {
                Double estStartTime = Double.parseDouble(String.valueOf(readisUtil.getSysCfg( "ESTIMATE_START_TIME", "0")));
                arriveDate = orderScheduler.getCarStartDate() ==null ? this.addHourAndMins(orderScheduler.getDependTime(), estStartTime.floatValue()) : orderScheduler.getCarStartDate();
                if (transitLineInfos != null ) {
                    if (transitLineInfos != null && transitLineInfos.size() > 0) {
                        for (OrderTransitLineInfo orderTransitLineInfo : transitLineInfos) {
                            if (orderTransitLineInfo.getCarDependDate() == null) {
                                this.addHourAndMins(arriveDate, orderTransitLineInfo.getArriveTime());
                                arriveDate =  this.addHourAndMins(this.addHourAndMins(arriveDate, orderTransitLineInfo.getArriveTime()), estStartTime.floatValue());
                            }else{
                                if (orderTransitLineInfo.getCarStartDate() == null) {
                                    arriveDate = this.addHourAndMins(orderTransitLineInfo.getCarDependDate(), estStartTime.floatValue());
                                }else{
                                    arriveDate = orderTransitLineInfo.getCarStartDate();
                                }
                            }
                        }
                    }
                }
                arriveDate = this.addHourAndMins(arriveDate, orderScheduler.getArriveTime());
            }
            if (arriveDate.toInstant(ZoneOffset.of("+8")).toEpochMilli() > dependDate.toInstant(ZoneOffset.of("+8")).toEpochMilli()) {
                throw new BusinessException("承运"+des+"在"+ DateUtil.formatDate(this.getDateByLocalDateTime(nextOrder.getDependTime()), DateUtil.DATETIME_FORMAT)+"-"+DateUtil.formatDate(this.getDateByLocalDateTime(nextArriveDate), DateUtil.DATETIME_FORMAT)+" 已承运下一订单("+nextOrder.getOrderId()+")，无法承运该订单。");
            }
            Map directionMap = GpsUtil.getDirectionV2(Double.parseDouble(nextOrder.getEand()), Double.parseDouble(nextOrder.getNand()), Double.parseDouble(orderGoods.getEandDes()), Double.parseDouble(orderGoods.getNandDes()), null, 0);
                //本单目的地到下单靠台地空驶距离
                Double distance = CommonUtils.getDoubleFormat(Double.parseDouble(DataFormat.getStringKey(directionMap, "distance"))/1000.0,3);
                if (((distance / speedStandard) * 3600000) > (dependDate.toInstant(ZoneOffset.of("+8")).toEpochMilli() - arriveDate.toInstant(ZoneOffset.of("+8")).toEpochMilli())) {
                    throw new BusinessException("承运"+des+"的下一单("+nextOrder.getOrderId()+")靠台地("+nextOrder.getSourceRegionName()+")距离本单的目的地太远，间隔时间太短，"+des+"无法返回承运下一单。");
                }
        }
    }


    /**
     * 开始时间+多少个小时 的时间
     * @param orig  开始时间
     * @param incrFloat 单位小时
     * @return
     *    返回添加后的时间
     */
    public LocalDateTime addHourAndMins(LocalDateTime orig, Float incrFloat){
        float incr = incrFloat == null ? 0f : incrFloat;
        int incrHour = (int)incr;
        int incrMins = new BigDecimal(incr).subtract(new BigDecimal(incrHour)).multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        orig.plusMinutes(incrMins);
        return orig;
    }

    public static Date getDateByLocalDateTime(LocalDateTime localDateTime){

        Date date = Date.from(localDateTime.atZone( ZoneId.systemDefault()).toInstant());
        return date;
    }


}
