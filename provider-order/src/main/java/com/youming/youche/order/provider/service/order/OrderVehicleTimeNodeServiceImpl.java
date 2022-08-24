package com.youming.youche.order.provider.service.order;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderRetrographyCostInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderVehicleTimeNodeService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderRetrographyCostInfo;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderVehicleTimeNode;
import com.youming.youche.order.dto.OrderConsumeOilDto;
import com.youming.youche.order.dto.OrderCostInfoDto;
import com.youming.youche.order.dto.OrderCostRetrographyDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.provider.mapper.order.OrderCostReportMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.mapper.order.OrderVehicleTimeNodeMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-25
 */
@DubboService(version = "1.0.0")
@Service
public class OrderVehicleTimeNodeServiceImpl extends BaseServiceImpl<OrderVehicleTimeNodeMapper, OrderVehicleTimeNode> implements IOrderVehicleTimeNodeService {
    @Autowired
    private IOrderSchedulerService orderSchedulerService;
    @Resource
    private  OrderVehicleTimeNodeMapper orderVehicleTimeNodeMapper;

    @Resource
    IOrderSchedulerHService iOrderSchedulerHService;

    @Resource
    IOrderCostReportService iOrderCostReportService;
    @Lazy
    @Resource
    IOrderInfoService iOrderInfoService;

    @Resource
    IOrderInfoHService iOrderInfoHService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    IOrderInfoExtHService iOrderInfoExtHService;

    @Resource
    IOrderFeeExtHService iOrderFeeExtHService;

    @Resource
    IOrderInfoExtService iOrderInfoExtService;

    @Resource
    IOrderFeeExtService iOrderFeeExtService;

    @Resource
    IOrderRetrographyCostInfoService iOrderRetrographyCostInfoService;

    @Resource
    OrderSchedulerMapper orderSchedulerMapper;

    @Resource
    OrderCostReportMapper orderCostReportMapper;

    @Resource
    IOperationOilService iOperationOilService;


    @Override
    public void addVehicleTimeNode(Long startOrderId, LocalDateTime startDate, String plateNumber, Long tenantId) {
        if (startDate == null) {
            throw new BusinessException("开始时间为空！");
        }
        String month = LocalDateTimeUtil.convertDateToStringYm(startDate);
        List<OrderVehicleTimeNode> vehicleTimeNodes = this.queryOrderVehicleTimeNodeByMonth(plateNumber, null,
                month,true);
        if (vehicleTimeNodes != null && vehicleTimeNodes.size() > 0) {
            OrderVehicleTimeNode orderVehicleTimeNode = vehicleTimeNodes.get(0);
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(startOrderId);
            if (orderScheduler != null) {
                if (orderVehicleTimeNode.getStartDate().isBefore(orderScheduler.getDependTime())) {
                    List<OrderVehicleTimeNode>  nodes = this.queryOrderVehicleTimeNode(plateNumber, null, null,
                            month, orderVehicleTimeNode.getStartOrderId(), null,
                            orderVehicleTimeNode.getId());
                    if (nodes != null && nodes.size() > 0) {//有作为上一开始时间
                        OrderVehicleTimeNode lastNode = nodes.get(0);
                        lastNode.setStartDate(orderScheduler.getDependTime());
                        lastNode.setStartOrderId(startOrderId);
                        this.saveOrUpdate(lastNode);
                    }else{//没有作为上一开始时间
                        List<OrderVehicleTimeNode>  nodes2 = this.queryOrderVehicleTimeNode(plateNumber, null,
                                null, month, null, orderVehicleTimeNode.getStartOrderId(),
                                orderVehicleTimeNode.getId());
                        if (nodes2 == null || nodes2.size() == 0) {//订单没有为截断点 更新本节点订单
                            orderVehicleTimeNode.setStartDate(orderScheduler.getDependTime());
                            orderVehicleTimeNode.setStartOrderId(startOrderId);
                            this.saveOrUpdate(orderVehicleTimeNode);
                        }//订单为截断点不做处理  已包含本节点订单
                    }
                }
            }
        }else{
            //无开始数据添加
            this.setVehicleTimeNode(startOrderId, startDate, plateNumber, tenantId);
        }
    }


    @Override
    public List<OrderVehicleTimeNode> queryOrderVehicleTimeNode(String plateNumber, String startDate,
                                                                String endDate, String month, Long startOrderId,
                                                                Long endOrderId, Long id) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("参数车牌号不能为空！");
        }
        if (StringUtils.isBlank(month)) {
            throw new BusinessException("参数月份不能为空！");
        }
      return   orderVehicleTimeNodeMapper.queryOrderVehicleTimeNode(plateNumber, startDate, endDate,
              month, startOrderId, endOrderId, id);
    }

    @Override
    public List<OrderVehicleTimeNode> queryOrderVehicleTimeNodeByMonth(String plateNumber, String endDate,
                                                                       String month,
                                                                       Boolean isSelelctNullEndDate) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("参数车牌号不能为空！");
        }
        if (StringUtils.isBlank(month)) {
            throw new BusinessException("参数月份不能为空！");
        }
        return   orderVehicleTimeNodeMapper.queryOrderVehicleTimeNodeByMonth(plateNumber,
                endDate, month, isSelelctNullEndDate);
    }

    @Override
    public void delVehicleTimeNode(Long orderId, Date startDate, String plateNumber, Long tenantId) {
        if (startDate == null) {
            throw new BusinessException("开始时间为空！");
        }
        String month = DateUtil.formatDate(startDate, DateUtil.YEAR_MONTH_FORMAT);
        List<BigInteger> firstOrder = orderSchedulerService.getMonthFirstOrderId(plateNumber, month,null);
        boolean isFirstOrder = false;
        Long standbyOrderId = null;
        if (firstOrder != null && firstOrder.size() > 0) {//获取第一单
            //是否月初第一单
            if (firstOrder.get(0).longValue() == orderId.longValue()) {
                isFirstOrder = true;
            }
            if (firstOrder.size() > 1) {
                standbyOrderId = firstOrder.get(1).longValue();
            }
        }
        List<OrderVehicleTimeNode>  startVehicleTimeNodes = queryOrderVehicleTimeNode(plateNumber, null, null, month, orderId, null, null);
        List<Long> endOrderIds = new ArrayList<>();
        //是否存在起始节点
        if (startVehicleTimeNodes != null && startVehicleTimeNodes.size() > 0) {
            for (OrderVehicleTimeNode orderVehicleTimeNode : startVehicleTimeNodes) {
                //起始节点是否存在截止订单
                if (orderVehicleTimeNode.getEndOrderId() != null && orderVehicleTimeNode.getEndOrderId() > 0) {
                    //截止订单是否是当前删除订单 不是则记录该截止订单
                    if (orderVehicleTimeNode.getEndOrderId().longValue() != orderId.longValue()) {
                        endOrderIds.add(orderVehicleTimeNode.getEndOrderId());
                    }
                    this.removeById(orderVehicleTimeNode.getId());
                }else{
                    //是否月初第一单
                    if (isFirstOrder && standbyOrderId != null && standbyOrderId.longValue() != orderId.longValue()) {
                        //是 替换订单号
                        orderVehicleTimeNode.setStartOrderId(standbyOrderId);
                        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(standbyOrderId);
                        if (orderScheduler == null) {
                            OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(standbyOrderId);
                            if (orderSchedulerH != null) {
                                orderVehicleTimeNode.setStartDate(orderSchedulerH.getDependTime());
                            }else{
                                throw new BusinessException("未找到订单["+standbyOrderId+"]信息！");
                            }
                        }else{
                            orderVehicleTimeNode.setStartDate(orderScheduler.getDependTime());
                        }
                        this.saveOrUpdate(orderVehicleTimeNode);
                    }else{
                        //不是 删除
                        this.removeById(orderVehicleTimeNode.getId());
                    }
                }
            }
        }
        List<OrderVehicleTimeNode>  endVehicleTimeNodes =
                queryOrderVehicleTimeNode(plateNumber, null, null, month, null, orderId, null);
        //是否存在截止节点
        if (endVehicleTimeNodes != null && endVehicleTimeNodes.size() > 0) {
            //是否有记录截止订单
            OrderVehicleTimeNode orderVehicleTimeNode = endVehicleTimeNodes.get(0);
            if (endOrderIds != null && endOrderIds.size() > 0) {//有则替换截止订单号 以及时间
                for (Long endOrderId : endOrderIds) {
                    if (endOrderId != orderId.longValue()) {
                        orderVehicleTimeNode.setEndOrderId(endOrderId);
                        OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(endOrderId);
                        if (orderScheduler == null) {
                            OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(endOrderId);
                            if (orderSchedulerH != null) {
                                orderVehicleTimeNode.setEndDate(orderSchedulerH.getDependTime());
                            }else{
                                throw new BusinessException("未找到订单["+endOrderId+"]信息！");
                            }
                        }else{
                            orderVehicleTimeNode.setEndDate(orderScheduler.getDependTime());
                        }
                    }
                }
            }else{//无则清空截止订单
                orderVehicleTimeNode.setEndOrderId(null);
                orderVehicleTimeNode.setEndDate(null);
            }
            this.saveOrUpdate(orderVehicleTimeNode);
        }else{//不存在
            if (endOrderIds != null && endOrderIds.size() > 0) {//是否有记录截止订单
                for (Long endOrderId : endOrderIds) {
                    if (endOrderId != orderId.longValue()) {
                        //存在 则记录一条以截止订单开始截止数据
                        List<OrderVehicleTimeNode>  vehicleTimeNodes =
                                queryOrderVehicleTimeNode(plateNumber, null, null, month, endOrderId, null, null);
                        List<OrderCostReport> orderCostReports = iOrderCostReportService.getOrderCostReportByOrderId(endOrderId);
                        Boolean isFullOil = false;
                        if (orderCostReports != null && orderCostReports.size() > 0) {
                            OrderCostReport orderCostReport = orderCostReports.get(0);
                            //满油
                            if (orderCostReport.getIsFullOil() != null && orderCostReport.getIsFullOil().intValue() == 1) {
                                isFullOil = true;
                            }
                        }
                        if (vehicleTimeNodes == null || vehicleTimeNodes.size() == 0 || isFullOil) {
                            OrderVehicleTimeNode orderVehicleTimeNode = new OrderVehicleTimeNode();
                            orderVehicleTimeNode.setPlateNumber(plateNumber);
                            orderVehicleTimeNode.setCreateTime(LocalDateTime.now());
                            orderVehicleTimeNode.setUpdateTime(LocalDateTime.now());
                            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(endOrderId);
                            if (orderScheduler == null) {
                                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(endOrderId);
                                if (orderSchedulerH != null) {
                                    orderVehicleTimeNode.setStartDate(orderSchedulerH.getDependTime());
                                    orderVehicleTimeNode.setEndDate(orderSchedulerH.getDependTime());
                                }else{
                                    throw new BusinessException("未找到订单["+endOrderId+"]信息！");
                                }
                            }else{
                                orderVehicleTimeNode.setStartDate(orderScheduler.getDependTime());
                                orderVehicleTimeNode.setEndDate(orderScheduler.getDependTime());
                            }
                            orderVehicleTimeNode.setStartOrderId(endOrderId);
                            orderVehicleTimeNode.setEndOrderId(endOrderId);
                            this.saveOrUpdate(orderVehicleTimeNode);
                        }
                    }
                }
            }
        }
    }

    @Override
    public OrderVehicleTimeNode queryOrderVehicleTimeNode(String plateNumber, Date dependDate, String month, Long orderId) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("参数车牌号不能为空！");
        }
        if (StringUtils.isBlank(month)) {
            throw new BusinessException("参数月份不能为空！");
        }
        if (dependDate == null) {
            throw new BusinessException("参数靠台时间不能为空！");
        }
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("参数订单号不能为空！");
        }

        List<OrderVehicleTimeNode> orderVehicleTimeNodes = getBaseMapper().queryOrderVehicleTimeNodeNew(plateNumber, dependDate, month, orderId);
        if (orderVehicleTimeNodes != null && orderVehicleTimeNodes.size() > 0) {
            return orderVehicleTimeNodes.get(0);
        }

        return null;
    }

    @Override
    public OrderCostRetrographyDto setOrderCostFeeByTimeNode(List<OrderVehicleTimeNode> vehicleTimeNodes, boolean isSelect,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderCostRetrographyDto out = null;
        if (vehicleTimeNodes != null && vehicleTimeNodes.size() > 0) {
            Long num = 0L;
            out = new OrderCostRetrographyDto();
            for (OrderVehicleTimeNode orderVehicleTimeNode : vehicleTimeNodes) {
                Date startDate = getLocalDateTimeToDate(orderVehicleTimeNode.getStartDate());
                Date endDate = getLocalDateTimeToDate(orderVehicleTimeNode.getEndDate());
                if (endDate == null) {
                    Calendar cale = Calendar.getInstance();
                    cale.setTime(startDate);
                    cale.add(Calendar.MONTH, 1);
                    cale.set(Calendar.DAY_OF_MONTH, 0);
                    endDate = cale.getTime();
                    try {
                        endDate = DateUtil.formatStringToDate(DateUtil.formatDateByFormat(endDate, DateUtil.DATE_FULLTIME_FORMAT), DateUtil.DATETIME_FORMAT);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                String month = orderVehicleTimeNode.getStartDate() == null ? null : DateUtil.formatDate(getLocalDateTimeToDate(orderVehicleTimeNode.getStartDate()), DateUtil.YEAR_MONTH_FORMAT);
                List<BigInteger> list = orderSchedulerMapper.queryExpenseOrderIdByTimeNode(orderVehicleTimeNode.getPlateNumber(), startDate,
                        endDate, month);
                List<Long> orderIds = new ArrayList<Long>();
                List<BigInteger> firstOrder = null;
                if (num == 0) {//第一时间节点
                    firstOrder = orderSchedulerService.getMonthFirstOrderId(orderVehicleTimeNode.getPlateNumber(), month,null);
                }
                Long firstOrderId = null;
                num ++;
                if ((list != null && list.size() > 0) || (firstOrder != null && firstOrder.size() > 0)) {
                    for (BigInteger bigInteger : list) {
                        orderIds.add(bigInteger.longValue());
                    }
                    if (firstOrder != null && firstOrder.size() > 0) {//获取第一单
                        if (isSelect) {
                            List<OrderVehicleTimeNode> nodes = this.queryOrderVehicleTimeNodeByMonth(orderVehicleTimeNode.getPlateNumber(),null, DateUtil.formatDate(getLocalDateTimeToDate(orderVehicleTimeNode.getStartDate()), DateUtil.YEAR_MONTH_FORMAT),false);
                            if (nodes != null && nodes.size() > 0) {
                                OrderVehicleTimeNode vehicleTimeNode = nodes.get(0);
                                if (vehicleTimeNode.getId().intValue() == orderVehicleTimeNode.getId().intValue()) {//第一单
                                    orderIds.add(firstOrder.get(0).longValue());
                                }
                            }
                        }else{
                            orderIds.add(firstOrder.get(0).longValue());
                        }
                        firstOrderId = firstOrder.get(0).longValue();
                    }
                    //集合去重
                    orderIds=new ArrayList<>(new HashSet(orderIds));
                    //费用上报总油费 总油耗
                    Map oilMap = new HashMap();
                    if (orderIds != null && orderIds.size() > 0) {
                        StringBuffer str = new StringBuffer();
                        for (Long orderId : orderIds) {
                            str.append("'").append(orderId).append("',");
                        }
                        oilMap = orderCostReportMapper.getTotalAmtByOrderIds(str.substring(0,str.length()-1));
                    }
                    Double oilRise = org.apache.commons.lang3.StringUtils.isBlank(DataFormat.getStringKey(oilMap, "totalOil"))  ? 0.0 : Double.parseDouble(DataFormat.getStringKey(oilMap, "totalOil")) * 100;
                    Long oilAmt = (DataFormat.getLongKey(oilMap, "totalAmt") < 0 ? 0 : DataFormat.getLongKey(oilMap, "totalAmt"));
                    Long consumeOilAmt = 0L;
                    Double consumeOilRiseDouble = 0.0;
                    //总距离
                    Map<Long, long[]> rtnMap = new HashMap();
                    if (orderIds != null && orderIds.size() > 0) {
                        rtnMap =  iOrderCostReportService.getKilometreByOrderIds(orderIds);
                    }
                    Long totalMileage = 0L;
                    for (Long orderId : orderIds) {
                        Long tenantId = 0l;
                        Long userId = 0L;
                        Date dependTime = null;
                        OrderInfo orderInfo = iOrderInfoService.getOrder(orderId);
                        if (orderInfo == null) {
                            OrderInfoH orderInfoH = iOrderInfoHService.getOrderH(orderId);
                            if (orderInfoH == null) {
                                throw new BusinessException("未找到订单["+orderId+"]订单信息！");
                            }else{
                                tenantId = orderInfoH.getTenantId();
                                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                                dependTime = getLocalDateTimeToDate(orderSchedulerH.getDependTime());
                                userId = orderSchedulerH.getCarDriverId();
                                if (orderInfoH.getOrderState() != OrderConsts.ORDER_STATE.CANCELLED) {
                                    Long mileageNumber = (long) (orderSchedulerH.getMileageNumber() == null ? 0 : orderSchedulerH.getMileageNumber());
                                    totalMileage += rtnMap.get(orderId) != null ? rtnMap.get(orderId)[0] + rtnMap.get(orderId)[1] : mileageNumber;
                                }
                            }
                        }else{
                            tenantId = orderInfo.getTenantId();
                            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                            userId = orderScheduler.getCarDriverId();
                            dependTime = getLocalDateTimeToDate(orderScheduler.getDependTime());
                            Long mileageNumber = (long) (orderScheduler.getMileageNumber() == null ? 0 : orderScheduler.getMileageNumber());
                            totalMileage += rtnMap.get(orderId) != null ? rtnMap.get(orderId)[0] + rtnMap.get(orderId)[1] : mileageNumber;
                        }
                        Date nextDependDate = null;
                        Map<String, Object> nextOrderMap =	orderSchedulerService.getNextOrderIdByPlateNumber(orderVehicleTimeNode.getPlateNumber(), dependTime, tenantId, orderId);
                        Map<String, Object> map =	orderSchedulerService.getPreOrderIdByPlateNumber(orderVehicleTimeNode.getPlateNumber(), getDateToLocalDateTime(dependTime), tenantId, orderId);
                        if (nextOrderMap != null) {//有下一单
                            Long nextOrderId = DataFormat.getLongKey(nextOrderMap, "orderId");
                            Integer type = DataFormat.getIntKey(nextOrderMap, "type");
                            if (type == OrderConsts.TableType.ORI) {
                                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(nextOrderId);
                                nextDependDate = getLocalDateTimeToDate(orderScheduler.getDependTime());
                            }else if(type == OrderConsts.TableType.HIS){
                                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(nextOrderId);
                                nextDependDate = getLocalDateTimeToDate(orderSchedulerH.getDependTime());
                            }
                        }else{
                            nextDependDate = null;//没有下一单 时间无上限
                        }
                        if (firstOrderId != null && firstOrderId.longValue() == orderId.longValue()) {
                            try {
                                dependTime = DateUtil.formatStringToDate(DateUtil.formatDate(dependTime, "yyyy-MM")+"-01 00:00:00", DateUtil.DATETIME_FORMAT);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        if (map == null) {//无上一单 时间无下限
                            dependTime = null;
                        }
                        //司机指定时间加油
                        OrderConsumeOilDto oilOut = iOperationOilService.getOrderCousumeOil(null, dependTime, nextDependDate,userId,tenantId);
                        consumeOilAmt += oilOut.getTotalConsumeAmount() == null ? 0 : oilOut.getTotalConsumeAmount();
                        consumeOilRiseDouble += oilOut.getTotalConsumeRise() == null ? 0 : oilOut.getTotalConsumeRise();
                    }
                    Long consumeOilRise =  (consumeOilRiseDouble == null ? 0 : CommonUtil.getLongByString(String.valueOf(consumeOilRiseDouble)));
                    //扫码加油总油费 总油耗
                    Long totalAmt = oilAmt + consumeOilAmt ;
                    Long totalOil = oilRise.longValue() + consumeOilRise;
                    Integer count = 1;
                    BigDecimal entityOilFeeSum = BigDecimal.ZERO;
                    BigDecimal entityOilLitreSum = BigDecimal.ZERO;
                    BigDecimal virtualOilFeeSum = BigDecimal.ZERO;
                    BigDecimal virtualOilLitreSum = BigDecimal.ZERO;
                    BigDecimal oilWearTotalSum = BigDecimal.ZERO;
                    BigDecimal oilFeeTotalSum = BigDecimal.ZERO;

                    BigDecimal totalMileageBig = new BigDecimal(String.valueOf(totalMileage));
                    BigDecimal consumeOilRiseBig = new BigDecimal(String.valueOf(consumeOilRise));
                    BigDecimal consumeOilAmtBig = new BigDecimal(String.valueOf(consumeOilAmt));
                    BigDecimal oilRiseBig = new BigDecimal(String.valueOf(oilRise));
                    BigDecimal oilAmtBig = new BigDecimal(String.valueOf(oilAmt));
                    BigDecimal totalAmtBig = new BigDecimal(String.valueOf(totalAmt));
                    BigDecimal totalOilBig = new BigDecimal(String.valueOf(totalOil));
                    out.setTotalMileage(totalMileage);
                    out.setTotalOil(totalOil);
                    List<OrderCostInfoDto> costInfoOuts = new ArrayList<>();
                    for (Long orderId : orderIds) {
                        Long tenantId = 0l;
                        Integer orderState = 0;
                        Date dependTime = null;
                        Date assignDependTime = null;
                        Long runWay = 0L;
                        Long oilRebate = 0L;
                        Long mileageNumber = 0L;
                        Date preArriveDate = null;
                        OrderInfo orderInfo = iOrderInfoService.getOrder(orderId);
                        Date arriveDate = orderSchedulerService.queryOrderTrackDate(orderId,OrderConsts.TRACK_TYPE.TYPE3,loginInfo.getTenantId());
                        if (orderInfo == null) {
                            OrderInfoH orderInfoH = iOrderInfoHService.getOrderH(orderId);
                            if (orderInfoH == null) {
                                throw new BusinessException("未找到订单["+orderId+"]订单信息！");
                            }else{
                                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
                                OrderInfoExtH orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
                                OrderFeeExtH orderFeeExtH = iOrderFeeExtHService.getOrderFeeExtH(orderId);
                                runWay = orderInfoExtH.getRunWay() == null ? 0 : orderInfoExtH.getRunWay();
                                mileageNumber = (long)(orderSchedulerH.getMileageNumber()  == null ? 0 : orderSchedulerH.getMileageNumber());
                                tenantId = orderInfoH.getTenantId();
                                oilRebate = (orderFeeExtH.getOilRebate() == null ? 0 : orderFeeExtH.getOilRebate())
                                        +( orderFeeExtH.getOilRebateVirtual() == null ? 0 : orderFeeExtH.getOilRebateVirtual())
                                ;
                                assignDependTime = getLocalDateTimeToDate(orderSchedulerH.getDependTime());
                                orderState = orderInfoH.getOrderState();
                                dependTime = getLocalDateTimeToDate(orderSchedulerH.getCarDependDate() != null ? orderSchedulerH.getCarDependDate() : orderSchedulerH.getDependTime());
                            }
                        }else{
                            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                            OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
                            OrderFeeExt orderFeeExt = iOrderFeeExtService.getOrderFeeExt(orderId);
                            runWay = orderInfoExt.getRunWay() == null ? 0 : orderInfoExt.getRunWay();
                            mileageNumber = (long) (orderScheduler.getMileageNumber() == null ? 0 : orderScheduler.getMileageNumber());
                            tenantId = orderInfo.getTenantId();
                            assignDependTime = getLocalDateTimeToDate(orderScheduler.getDependTime());
                            oilRebate = (orderFeeExt.getOilRebate() == null ? 0 : orderFeeExt.getOilRebate())
                                    +( orderFeeExt.getOilRebateVirtual() == null ? 0 : orderFeeExt.getOilRebateVirtual());
                            orderState = orderInfo.getOrderState();
                            dependTime = getLocalDateTimeToDate(orderScheduler.getCarDependDate() != null ? orderScheduler.getCarDependDate() : orderScheduler.getDependTime());
                        }
                        Long emptyRunDistance = 0L;
                        Long runDistance = 0L;
                        Long orderTotalTime = 0L;
                        if (orderState != null && orderState != OrderConsts.ORDER_STATE.CANCELLED) {
                            emptyRunDistance = rtnMap.get(orderId) != null ? rtnMap.get(orderId)[0] : 0;
                            runDistance = rtnMap.get(orderId) != null ? rtnMap.get(orderId)[1] : mileageNumber;
                            Map<String, Object> map =	orderSchedulerService.getPreOrderIdByPlateNumber(orderVehicleTimeNode.getPlateNumber(), getDateToLocalDateTime(assignDependTime), tenantId, orderId);
                            if (map != null) {
                                Long preOrderId = DataFormat.getLongKey(map, "orderId");
                                Integer type = DataFormat.getIntKey(map, "type");
                                if (type != null && type == OrderConsts.TableType.HIS) {
                                    OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(preOrderId);
                                    preArriveDate = getLocalDateTimeToDate(orderSchedulerH.getCarArriveDate());
                                }else if(type != null && type == OrderConsts.TableType.ORI){
                                    preArriveDate = orderSchedulerService.queryOrderTrackDate(preOrderId,OrderConsts.TRACK_TYPE.TYPE3,loginInfo.getTenantId());
                                }
                            }
                            Double time = (preArriveDate == null ? (arriveDate.getTime() - dependTime.getTime())  : (arriveDate.getTime() - preArriveDate.getTime()))/(60.0*60.0*1000.0)*100;
                            orderTotalTime = time.longValue();
                        }
                        BigDecimal totalDistance =  new BigDecimal(String.valueOf(emptyRunDistance + runDistance));

                        BigDecimal entityOilFee = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? oilAmtBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        BigDecimal entityOilLitre = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? oilRiseBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        BigDecimal virtualOilFee = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? consumeOilAmtBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        BigDecimal virtualOilLitre = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? consumeOilRiseBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        BigDecimal oilWearTotal = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? totalOilBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        BigDecimal oilFeeTotal = (totalMileageBig.compareTo(BigDecimal.ZERO) != 0 ? totalAmtBig.divide(totalMileageBig, 10, BigDecimal.ROUND_HALF_UP).multiply(totalDistance) : BigDecimal.ZERO);
                        if (count == orderIds.size()) {//最后一单
                            entityOilFee = oilAmtBig.subtract(entityOilFeeSum);
                            entityOilLitre = oilRiseBig.subtract(entityOilLitreSum);
                            virtualOilFee = consumeOilAmtBig.subtract(virtualOilFeeSum);
                            virtualOilLitre = consumeOilRiseBig.subtract(virtualOilLitreSum);
                            oilWearTotal = totalOilBig.subtract(oilWearTotalSum);
                            oilFeeTotal = totalAmtBig.subtract(oilFeeTotalSum);
                        }else{
                            entityOilFeeSum =entityOilFeeSum.add(new BigDecimal(String.valueOf(entityOilFee.longValue())));
                            entityOilLitreSum = entityOilLitreSum.add(new BigDecimal(String.valueOf(entityOilLitre.longValue())));
                            virtualOilFeeSum = virtualOilFeeSum.add(new BigDecimal(String.valueOf(virtualOilFee.longValue())));
                            virtualOilLitreSum = virtualOilLitreSum.add(new BigDecimal(String.valueOf(virtualOilLitre.longValue())));
                            oilWearTotalSum = oilWearTotalSum.add(new BigDecimal(String.valueOf(oilWearTotal.longValue())));
                            oilFeeTotalSum = oilFeeTotalSum.add(new BigDecimal(String.valueOf(oilFeeTotal.longValue())));
                        }
                        BigDecimal orderOilWear = (totalDistance.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : oilWearTotal.divide(totalDistance, 10, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(String.valueOf(100000))));
                        if (!isSelect) {//不是查询
                            OrderRetrographyCostInfo costInfo = iOrderRetrographyCostInfoService.queryOrderRetrographyCostInfo(orderId);
                            if (costInfo == null) {
                                costInfo = new OrderRetrographyCostInfo();
                                costInfo.setOrderId(orderId);
                                costInfo.setCreateTime(LocalDateTime.now());
                                costInfo.setTenantId(tenantId);
                            }
                            costInfo.setEntityOilFee(entityOilFee.longValue());
                            costInfo.setEntityOilLitre(entityOilLitre.longValue());
                            costInfo.setVirtualOilFee(virtualOilFee.longValue());
                            costInfo.setVirtualOilLitre(virtualOilLitre.longValue());
                            //总耗油量 = （总油量/总的公里数 ） *（本订单空载公里数+本订单载重公里数）
                            costInfo.setOilWearTotal(oilWearTotal.longValue());
                            //油费金额 = （总油费/总的公里数 ） *（本订单空载公里数+本订单载重公里数）
                            costInfo.setOilFeeTotal(oilFeeTotal.longValue());
                            //空驶距离
                            costInfo.setEmptyRunDistance(emptyRunDistance);
                            //行驶距离
                            costInfo.setRunDistance(runDistance);
                            //订单油耗 = 总耗油量/(空载公里数+载重公里数)
                            costInfo.setOrderOilWear(orderOilWear.longValue());
                            //订单总耗时 = 上单到达时间-本单到达时间
                            costInfo.setOrderTotalTime(orderTotalTime);
                            iOrderRetrographyCostInfoService.saveOrUpdate(costInfo);
                        }
                        count ++;
                        if (isSelect) {
                            OrderCostInfoDto orderCostInfoOut = new OrderCostInfoDto();
                            orderCostInfoOut.setOrderId(orderId);
                            orderCostInfoOut.setDependTime(assignDependTime);
                            orderCostInfoOut.setEntityOilFee(entityOilFee.longValue());
                            orderCostInfoOut.setEntityOilLitre(entityOilLitre.longValue());
                            orderCostInfoOut.setOilFeeTotal(oilFeeTotal.longValue());
                            orderCostInfoOut.setOilWearTotal(oilWearTotal.longValue());
                            Map mapOut = orderCostReportMapper.getTotalAmtByOrderId(orderId, OrderConsts.TABLE_TYPE.TABLE_TYPE2);
                            orderCostInfoOut.setPontageFee(mapOut == null ? 0 : (DataFormat.getLongKey(mapOut, "totalAmt") < 0 ? 0 : DataFormat.getLongKey(mapOut, "totalAmt")));
                            orderCostInfoOut.setVirtualOilFee(virtualOilFee.longValue());
                            orderCostInfoOut.setVirtualOilLitre(virtualOilLitre.longValue());
                            orderCostInfoOut.setEmptyRunDistance(emptyRunDistance);
                            orderCostInfoOut.setRunDistance(runDistance);
                            orderCostInfoOut.setOilRebate(oilRebate);
                            OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(orderId);
                            orderCostInfoOut.setOrderLine(orderInfoDto.getOrderLine());
                            orderCostInfoOut.setIsTransitLine(orderInfoDto.getIsTransitLine());
                            costInfoOuts.add(orderCostInfoOut);
                        }
                    }
                    if (costInfoOuts != null && costInfoOuts.size() > 0) {
                        Collections.sort(costInfoOuts, new Comparator<OrderCostInfoDto>(){
                            public int compare(OrderCostInfoDto a, OrderCostInfoDto b) {
                                if(a.getDependTime().getTime() > b.getDependTime().getTime()){
                                    return 1;
                                }
                                if(a.getDependTime().getTime() == b.getDependTime().getTime()){
                                    return 0;
                                }
                                return -1;
                            }
                        });
                    }
                    out.setCostInfoOuts(costInfoOuts);
                    out.setOrderCount(costInfoOuts != null ? costInfoOuts.size() : 0);
                }else{//无订单处理
                    continue;
                }
            }
        }
        return out;
    }

    /**
     * 截断时间节点
     * @param orderId
     * @param plateNumber
     * @throws Exception
     */
    @Override
    public void truncationVehicleTimeNode(Long orderId, String plateNumber) {
        OrderScheduler orderScheduler = new OrderScheduler();
        QueryWrapper<OrderScheduler> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        List<OrderScheduler> list = orderSchedulerMapper.selectList(wrapper);
        if (list != null && list.size() > 0) {
            orderScheduler=list.get(0);
        }else{
            orderScheduler=null;
        }
        LocalDateTime dependTime = null;
        Long tenantId = null;
        if (orderScheduler == null) {
//            OrderSchedulerH orderSchedulerH = orderSchedulerSV.getOrderSchedulerH(orderId);
            OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
            if (orderSchedulerH == null) {
                throw new BusinessException("未找到订单["+orderId+"]订单信息！");
            }else{
                dependTime = orderSchedulerH.getDependTime();
                tenantId = orderSchedulerH.getTenantId();
                plateNumber = orderSchedulerH.getPlateNumber();
            }
        }else{
            dependTime = orderScheduler.getDependTime();
            tenantId = orderScheduler.getTenantId();
            plateNumber = orderScheduler.getPlateNumber();
        }
        String month = DateUtil.formatDate(DateUtil.asDate(dependTime), DateUtil.YEAR_MONTH_FORMAT);
//        List<OrderVehicleTimeNode>  timeNodes = orderVehicleTimeNodeSV.queryOrderVehicleTimeNodeByMonth(plateNumber, dependTime, month,false);
        List<OrderVehicleTimeNode> timeNodes = this.queryOrderVehicleTimeNodeByMonth(plateNumber, null, month, false);

        if (timeNodes != null && timeNodes.size() > 0) {//当前截至时间小于已有截至时间
            OrderVehicleTimeNode  orderVehicleTimeNodeOld = timeNodes.get(0);
//            Date lastEndDate = orderVehicleTimeNodeOld.getEndDate();
            LocalDateTime lastEndDate = orderVehicleTimeNodeOld.getEndDate();

            Long lastOrderId = orderVehicleTimeNodeOld.getEndOrderId();
            //更换结束时间
            orderVehicleTimeNodeOld.setEndDate(dependTime);
            orderVehicleTimeNodeOld.setEndOrderId(orderId);
//            orderVehicleTimeNodeSV.saveOrUpdate(orderVehicleTimeNodeOld);
            this.saveOrUpdate(orderVehicleTimeNodeOld);
            //新增  新靠台时间 --> 截止时间
            OrderVehicleTimeNode orderVehicleTimeNodeNew = new OrderVehicleTimeNode();
            orderVehicleTimeNodeNew.setPlateNumber(plateNumber);
            orderVehicleTimeNodeNew.setStartOrderId(orderId);
            orderVehicleTimeNodeNew.setStartDate(dependTime);
//            orderVehicleTimeNodeNew.setTenantId(tenantId);
            orderVehicleTimeNodeNew.setEndDate(lastEndDate);
            orderVehicleTimeNodeNew.setEndOrderId(lastOrderId);
//            orderVehicleTimeNodeSV.saveOrUpdate(orderVehicleTimeNodeNew);
            this.saveOrUpdate(orderVehicleTimeNodeNew);

        }else{
//            List<OrderVehicleTimeNode>  vehicleTimeNodes = orderVehicleTimeNodeSV.queryOrderVehicleTimeNodeByMonth(plateNumber, null, month,true);
            List<OrderVehicleTimeNode>  vehicleTimeNodes =this.queryOrderVehicleTimeNodeByMonth(plateNumber,null,month,true);
            if (vehicleTimeNodes != null && vehicleTimeNodes.size() > 0) {//有开始时间无结束时间
                OrderVehicleTimeNode  orderVehicleTimeNode = vehicleTimeNodes.get(0);
                //填写结束时间
                orderVehicleTimeNode.setEndDate(dependTime);
                orderVehicleTimeNode.setEndOrderId(orderId);
//                orderVehicleTimeNodeSV.saveOrUpdate(orderVehicleTimeNode);
                this.saveOrUpdate(orderVehicleTimeNode);
            }else{//新增一条
                OrderVehicleTimeNode orderVehicleTimeNode = new OrderVehicleTimeNode();
                orderVehicleTimeNode.setPlateNumber(plateNumber);
                orderVehicleTimeNode.setStartOrderId(orderId);
//                orderVehicleTimeNode.setTenantId(tenantId);
                //填写结束时间
                orderVehicleTimeNode.setEndDate(dependTime);
                orderVehicleTimeNode.setEndOrderId(orderId);
//                orderVehicleTimeNodeSV.saveOrUpdate(orderVehicleTimeNode);
                this.saveOrUpdate(orderVehicleTimeNode);
            }
            //记录新开始时间
            this.setVehicleTimeNode(orderId,dependTime, plateNumber, tenantId);
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
        if(date == null){
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
//    /**
//     * 查询指定月份车辆成本时间
//     * @param plateNumber 车牌号
//     * @param endDate 结束时间
//     * @param month 月份:yyyy-mm
//     * @param isSelelctNullEndDate 是否查询空值EndDate
//     * @return
//     * @throws Exception
//     */
//    public List<OrderVehicleTimeNode> queryOrderVehicleTimeNodeByMonth(String plateNumber, Date endDate, String month,
//                                                                       Boolean isSelelctNullEndDate) {
//        StringBuffer sb = new StringBuffer();
//        if (StringUtils.isBlank(plateNumber)) {
//            throw new BusinessException("参数车牌号不能为空！");
//        }
//        if (StringUtils.isBlank(month)) {
//            throw new BusinessException("参数月份不能为空！");
//        }
//        LambdaQueryWrapper<OrderVehicleTimeNode> wrapper = Wrappers.lambdaQuery();
//        try {
//            Date months = DateUtil.formatStringToDate(month, DateUtil.YEAR_MONTH_FORMAT);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        wrapper.eq(OrderVehicleTimeNode::getStartDate, month)
//                .eq(OrderVehicleTimeNode::getPlateNumber, plateNumber);
//
//        if (isSelelctNullEndDate) {
//            wrapper.isNull(OrderVehicleTimeNode::getEndDate);
//        }
//
//        if (endDate != null) {
//            wrapper.ge(OrderVehicleTimeNode::getEndDate, endDate).last("ORDER BY end_date");
//        } else {
//            wrapper.last("ORDER BY start_date");
//        }
//
//        List<OrderVehicleTimeNode> orderVehicleTimeNodes = baseMapper.selectList(wrapper);
//        return orderVehicleTimeNodes;
//    }

    /**
     * 车辆时间节点查询
     *
     * @param plateNumber
     * @param startDate
     * @param endDate
     * @param month
     * @param startOrderId
     * @param endOrderId
     * @param id           排除主键
     * @return
     * @throws Exception
     */
//    public List<OrderVehicleTimeNode> queryOrderVehicleTimeNode(String plateNumber, Date startDate, Date endDate,
//                                                                String month, Long startOrderId, Long endOrderId, Long id)  {
//
//
//        if (StringUtils.isBlank(plateNumber)) {
//            throw new BusinessException("参数车牌号不能为空！");
//        }
//        if (StringUtils.isBlank(month)) {
//            throw new BusinessException("参数月份不能为空！");
//        }
//
//        LambdaQueryWrapper<OrderVehicleTimeNode> wrapper = Wrappers.lambdaQuery();
//        try {
//            Date months = DateUtil.formatStringToDate(month, DateUtil.YEAR_MONTH_FORMAT);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
////        sb.append(" AND DATE_FORMAT(START_DATE, '%Y-%m') =:month  ");
//        wrapper.eq(OrderVehicleTimeNode::getStartDate, month)
//                .eq(OrderVehicleTimeNode::getPlateNumber, plateNumber);
//
//        if (endDate != null) {
//            wrapper.eq(OrderVehicleTimeNode::getEndDate, endDate);
//        }
//
//        if (null != startDate) {
//            wrapper.eq(OrderVehicleTimeNode::getStartDate, startDate);
//        }
//        if (id != null && id > 0) {
//            wrapper.ne(OrderVehicleTimeNode::getId, id);
//        }
//        if (startOrderId != null && startOrderId > 0) {
//            wrapper.eq(OrderVehicleTimeNode::getStartOrderId, startOrderId);
//        }
//        if (endOrderId != null && endOrderId > 0) {
//            wrapper.eq(OrderVehicleTimeNode::getEndOrderId, endOrderId);
//        }
//        return baseMapper.selectList(wrapper);
//    }

    /**
     * 设置车辆时间节点
     * @param startOrderId
     * @param startDate
     * @param plateNumber
     * @param tenantId
     */
    private void setVehicleTimeNode(Long startOrderId,LocalDateTime startDate,String plateNumber,Long tenantId){
        OrderVehicleTimeNode orderVehicleTimeNode = new OrderVehicleTimeNode();
        orderVehicleTimeNode.setPlateNumber(plateNumber);
        orderVehicleTimeNode.setStartDate(startDate);
        orderVehicleTimeNode.setStartOrderId(startOrderId);
     //   orderVehicleTimeNode.setTenantId(tenantId);
        saveOrUpdate(orderVehicleTimeNode);
    }
}
