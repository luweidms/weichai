package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyVerService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.provider.mapper.order.OrderFeeExtMapper;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.util.OrderUtil;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.domain.tenant.TenantUserSalaryRel;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 订单费用扩展表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
@Service
public class OrderFeeExtServiceImpl extends BaseServiceImpl<OrderFeeExtMapper, OrderFeeExt> implements IOrderFeeExtService {

    private static final Logger log = LoggerFactory.getLogger(OrderFeeExtServiceImpl.class);

    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @Autowired
    private IOrderDriverSubsidyService orderDriverSubsidyService;
    @Autowired
    private IOrderFeeService orderFeeService;
    @Autowired
    private IOrderInfoExtService orderInfoExtService;
    @Autowired
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Autowired
    private IOrderDriverSubsidyVerService orderDriverSubsidyVerService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    private ITenantUserSalaryRelService tenantUserSalaryRelService;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private OrderDateUtil orderDateUtil;


    @Override
    public OrderFeeExt getOrderFeeExt(Long orderId) {
        LambdaQueryWrapper<OrderFeeExt> lambda=new QueryWrapper<OrderFeeExt>().lambda();
        lambda.eq(OrderFeeExt::getOrderId,orderId);
        List<OrderFeeExt> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else{
            return new OrderFeeExt();
        }
    }

    @Override
    public OrderFeeExt selectByOrderId(Long orderId) {
        LambdaQueryWrapper<OrderFeeExt> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(OrderFeeExt::getOrderId,orderId).last("LIMIT 1");
        return getOne(wrapper,false);
    }

    @Override
    public void calculateDriverSwitchSubsidy(Long orderId, Boolean isVer) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("未找到在途订单["+orderId+"]信息");
        }

        Long subSidyFee = orderDriverSubsidyService.findOrderDriverSubSidyFee(orderId, null, orderScheduler.getCarDriverId(), orderScheduler.getCopilotUserId(), isVer,null);
        OrderFeeExt orderFeeExt = this.getOrderFeeExt(orderId);
        OrderFee orderFee = orderFeeService.getOrderFee(orderId);
        orderFeeExt.setDriverSwitchSubsidy(subSidyFee);
        orderFeeExt.setEstFee((orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee())
                + (orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee())
                +(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage())
                +(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary())
                +(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary())
                +(orderFeeExt.getDriverSwitchSubsidy() == null ? 0 : orderFeeExt.getDriverSwitchSubsidy()));
        this.saveOrUpdate(orderFeeExt);
    }

    @Override
    public Map<String, Object> culateSubsidy(Long userId, Long tenantId, LocalDateTime dependTime, Float arriveTime, Long orderId, Boolean isCopilot, Integer transitLineSize) {
        Map<String, Object> retMap = new HashMap<String, Object>();
        Long subsidy = 0L;// 单位分
        Long salary = 0L;
        Integer subsidyDay = 0;
        String dateString = "";
        if(userId<=0||tenantId<=0){
            throw  new BusinessException("参数错误");
        }

        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        if (userDataInfo == null) {//非自有车司机不计算补贴
            retMap.put("fee", "0");
            retMap.put("num", subsidyDay);
            retMap.put("date", dateString);
            retMap.put("subsidy", subsidy);
            retMap.put("salary", salary);
            retMap.put("salaryPattern", null);
            return retMap;
        }
        TenantUserSalaryRel salaryRel = tenantUserSalaryRelService.getTenantUserRalaryRelByUserId(userId, tenantId);

        if (salaryRel == null) {
            throw new BusinessException("未找到"+(isCopilot ? "副驾驶":"主驾驶或切换司机")+"工资信息！");
        }
        subsidy = salaryRel.getSubsidy();// 单位分
        salary = salaryRel.getSalary();
        subsidyDay = 0;
        dateString = "";
        Double estStartTime = Double.parseDouble(String.valueOf(readisUtil.getSysCfg("ESTIMATE_START_TIME","0").getCfgValue()));
        Map<String, Object> orderIdMap = orderSchedulerService.getPreOrderIdByUserid(userId, dependTime, tenantId,orderId);
        //预估订单时效
        arriveTime = arriveTime +((transitLineSize < 0 ? 0 : transitLineSize)+1 * estStartTime.floatValue());
        LocalDateTime preArriveDate = null;
        StringBuffer print = new StringBuffer("计算补贴打印信息：");
        if (orderIdMap != null) {
            if(OrderConsts.TableType.ORI==(Integer) orderIdMap.get("type")){
                //原表
                OrderScheduler scheduler=  orderSchedulerService.getOrderScheduler(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(scheduler.getOrderId(), scheduler.getDependTime()
                        , scheduler.getCarStartDate(), scheduler.getArriveTime(), false);

            }else if(OrderConsts.TableType.HIS==(Integer) orderIdMap.get("type")){
                //历史表
                OrderSchedulerH schedulerH=  orderSchedulerHService.getOrderSchedulerH(DataFormat.getLongKey(orderIdMap, "orderId"));
                preArriveDate = orderInfoExtService.getOrderArriveDate(schedulerH.getOrderId(), schedulerH.getDependTime()
                        , schedulerH.getCarStartDate(), schedulerH.getArriveTime(), true);
            }
            print.append("上一单订单号:[").append(orderIdMap.get("orderId")).append("] ");
            print.append(" 上一单达到时间:[").append(LocalDateTimeUtil.convertDateToString(preArriveDate))
                    .append("]");
        }
        print.append("本单靠台时间:[").append(LocalDateTimeUtil.convertDateToString(dependTime)).append("]");
        print.append("本单达到时限:[").append(arriveTime).append("]小时");
// 补贴天数，补贴金额不看之前历史订单，重先计算，并且只计算主驾，副驾不考虑
        Map<String, Object> subsidyDayMap = culateSubsidy(dependTime, arriveTime, null);
        subsidyDay = Integer.valueOf(subsidyDayMap.get("subsidyDay").toString());
        dateString = subsidyDayMap.get("dateString").toString();

        print.append("补贴天数：[").append(subsidyDay).append("]");
        print.append("补贴日期：[").append(dateString).append("]");
        log.info(print.toString());

        if (subsidy != null) {
            Long subsidyFee = subsidyDay.longValue() * subsidy.longValue();
            retMap.put("fee", subsidyFee);
        } else {
            retMap.put("fee", "0");
        }

        retMap.put("num", subsidyDay);

        retMap.put("date", dateString);
        retMap.put("subsidy", subsidy);
        retMap.put("salary", salary);
        retMap.put("salaryPattern", salaryRel.getSalaryPattern());

        return retMap;
    }
    public Map<String, Object> culateSubsidy(LocalDateTime dependTime, Float arriveTime, LocalDateTime preArriveDate)
            {

        int subsidyDay = 0;

        if (preArriveDate == null) {
            // 没有上一单
            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);
//            subsidyDay = CommonUtil.getDifferDay(dependTime, arriveDate) + 1;

            Date date2 = Date.from(dependTime.atZone(ZoneId.systemDefault()).toInstant());
            Date date1 = Date.from(arriveDate.atZone(ZoneId.systemDefault()).toInstant());
            subsidyDay = OrderDateUtil.getDifferDay(date1, date2) + 1;
        } else {
            // 有上一单
            // 获取上一单的到达时间
            // 本单的预计到达时间
            LocalDateTime arriveDate = orderDateUtil.addHourAndMins(dependTime, arriveTime);

            // 本单到达时间-（上一单到达时间+1），如果为负数 ，就取0
            if (arriveDate.isBefore(preArriveDate)) {
                subsidyDay = 0;
            } else {
                if (arriveDate.getYear() != preArriveDate.getYear()
                        || arriveDate.getMonth() != preArriveDate.getMonth()) {
                    // 如果上一单的时间跟本单的是不同年月的，按本单的单月1号计算
                    preArriveDate = LocalDateTimeUtil.convertStringToDate(LocalDateTimeUtil.convertDateToStringYm(arriveDate)
                            +"-01 00:00:00");
                }
                Date date2 = Date.from(preArriveDate.atZone(ZoneId.systemDefault()).toInstant());
                Date date1 = Date.from(arriveDate.atZone(ZoneId.systemDefault()).toInstant());
                subsidyDay = OrderDateUtil.getDifferDay(date1, date2) + 1;
//                subsidyDay = CommonUtil.getDifferDay(preArriveDate, arriveDate)+ 1;
            }

        }
        String dateString = "";
        if (preArriveDate == null) {
            preArriveDate = dependTime;
            for (int j = 0; j < subsidyDay; j++) {
                dateString += " " +LocalDateTimeUtil.convertDateToStringMd(preArriveDate.plusDays(j));
            }
        } else {
            for (int j = 0; j < subsidyDay; j++) {
                dateString += " " + LocalDateTimeUtil.convertDateToStringMd(preArriveDate.plusDays(j));
            }
        }

        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("subsidyDay", subsidyDay);
        retMap.put("dateString", dateString);
        return retMap;
    }
    @Override
    public void setDriverSubsidy(Long orderId, Long userId, Boolean isVer,
                                 Integer isPayed, LoginInfo user) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号不能为空！");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException("切换司机ID不能为空！");
        }
        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
        if (orderScheduler == null) {
            throw new BusinessException("未找到在途订单["+orderId+"]信息");
        }
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);

        if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1
                && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay().intValue() == OrderConsts.PAYMENT_WAY.COST
                && userId.longValue() != orderScheduler.getCarDriverId().longValue()
                && (orderScheduler.getCopilotUserId() == null || userId.longValue() != orderScheduler.getCopilotUserId().longValue())) {
            List<OrderTransitLineInfo>  orderTransitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderId);
            Float arriveTime =  orderScheduler.getArriveTime();
            if (orderTransitLineInfos != null && orderTransitLineInfos.size() > 0) {
                for (OrderTransitLineInfo orderTransitLineInfo : orderTransitLineInfos) {
                    arriveTime += orderTransitLineInfo.getArriveTime();
                }
            }
            Map<String, Object> carDriver = this.culateSubsidy(userId,
                    orderScheduler.getTenantId(), orderScheduler.getDependTime(), arriveTime,
                    orderScheduler.getOrderId(),false,orderTransitLineInfos == null ? 0 : orderTransitLineInfos.size());
            if(StringUtils.isNotBlank(OrderUtil.objToStringEmpty(carDriver.get("date")))) {
                String[] subsidyTimeArr=OrderUtil.objToStringEmpty(carDriver.get("date")).split(" ");
                Long subsidy= DataFormat.getLongKey(carDriver, "subsidy");
                for (String subsidyTime : subsidyTimeArr) {
                    if(StringUtils.isBlank(subsidyTime)) {
                        continue;
                    }
//                    LocalDateTime subsidyDate=LocalDateTimeUtil.convertStringToDateYMD(LocalDateTime.now().getYear()+
//                            "-"+subsidyTime);
                    LocalDateTime subsidyDate=LocalDateTimeUtil.convertStringToDate(LocalDateTime.now().getYear()+
                            "-"+subsidyTime+" 00:00:00");
                    if (isVer) {
                        orderDriverSubsidyVerService.saveDriverSubsidyVer(orderId, userId, subsidyTime, subsidy,
                                3,isPayed,user);
                    }else{
                        orderDriverSubsidyVerService.saveDriverSubsidy(orderId,userId, subsidyDate, subsidy,3,user);
                    }
                    this.calculateDriverSwitchSubsidy(orderId,false);
                }
            }
        }
    }
}
