package com.youming.youche.order.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.ITransferInfoService;
import com.youming.youche.order.api.order.ICmCustomerInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtHService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderGoodsHService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoHService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoHService;
import com.youming.youche.order.api.order.IOrderTransitLineInfoService;
import com.youming.youche.order.api.order.other.ILugeOrderBusinessService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.TransferInfo;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtH;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderGoodsH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderOilDepotScheme;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import com.youming.youche.order.domain.order.OrderTransitLineInfoH;
import com.youming.youche.order.dto.CmCustomerInfoOutDto;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.order.dto.OrderDetailsTransferDto;
import com.youming.youche.order.dto.OrderDispatchInDto;
import com.youming.youche.order.dto.OrderIncomeInDto;
import com.youming.youche.order.dto.OrderTransferInfoDetailDto;
import com.youming.youche.order.dto.ReceiveOrderDto;
import com.youming.youche.order.dto.TransferInfoDto;
import com.youming.youche.order.provider.mapper.TransferInfoMapper;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.VehicleStaticDataUtil;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.order.vo.AcceptOrderVo;
import com.youming.youche.order.vo.OrderDetailsTransferVo;
import com.youming.youche.order.vo.OrderOilCardInfoVo;
import com.youming.youche.order.vo.SaveOrderVo;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.youming.youche.order.util.OrderUtil.mul;
import static com.youming.youche.order.util.OrderUtil.objToFloatMul100;
import static com.youming.youche.order.util.OrderUtil.objToInteger0;
import static com.youming.youche.order.util.OrderUtil.objToLong0;
import static com.youming.youche.order.util.OrderUtil.objToLongMul100;
import static com.youming.youche.order.util.OrderUtil.objToLongNull;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-14
 */
@DubboService(version = "1.0.0")
@Slf4j
public class TransferInfoServiceImpl extends BaseServiceImpl<TransferInfoMapper, TransferInfo> implements ITransferInfoService {

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private TransferInfoMapper transferInfoMapper;
    @Lazy
    @Resource
    private IOrderInfoService orderInfoService;
    @Resource
    private IOrderInfoHService orderInfoHService;
    @Resource
    private IOrderInfoExtService orderInfoExtService;
    @Resource
    private IOrderInfoExtHService orderInfoExtHService;
    @Resource
    private IOrderSchedulerService orderSchedulerService;
    @Resource
    private IOrderSchedulerHService orderSchedulerHService;
    @Resource
    private IOrderGoodsService orderGoodsService;
    @Resource
    private IOrderGoodsHService orderGoodsHService;
    @Resource
    private IOrderFeeService orderFeeService;
    @Resource
    private IOrderFeeHService orderFeeHService;
    @Resource
    private IOrderFeeExtService orderFeeExtService;
    @Resource
    private IOrderFeeExtHService orderFeeExtHService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Resource
    private IOrderPaymentDaysInfoService orderPaymentDaysInfoService;
    @Resource
    private IOrderPaymentDaysInfoHService orderPaymentDaysInfoHService;
    @Resource
    private IOrderTransitLineInfoHService orderTransitLineInfoHService;
    @Resource
    private IOrderTransitLineInfoService orderTransitLineInfoService;
    @Resource
    private LoginUtils loginUtils;

    @Resource
    private IOrderTransferInfoService orderTransferInfoService;


    @Resource
    private IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    private ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService tenantVehicleRelService;

    @Resource
    private IOrderProblemInfoService iOrderProblemInfoService;

    @Resource
    private IOrderFeeService iOrderFeeService;
    @Resource
    private ReadisUtil readisUtil;
    @Resource
    private VehicleStaticDataUtil vehicleStaticDataUtil;
    @DubboReference(version = "1.0.0")
    ISysRoleService iSysRoleService;
    @Autowired
    ILugeOrderBusinessService lugeOrderBusinessService;
    @Resource
    private OrderDateUtil orderDateUtil;
    @DubboReference(version = "1.0.0")
    ICmCustomerLineService cmCustomerLineService;
    @Resource
    ICmCustomerInfoService icmCustomerInfoService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @Override
    public Page<TransferInfo> getTransferInfoList(Page<TransferInfo> page, TransferInfoDto transferInfoDto, String accessToken) throws Exception {
//        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (tenantId == null) {
            return null;
        }
        QueryWrapper<TransferInfo> transferInfoQueryWrapper = new QueryWrapper<>();

        String transferTenantName = transferInfoDto.getTransferTenantName();
        if (StringUtils.isNoneBlank(transferTenantName)) {
            transferInfoQueryWrapper.like("TRANSFER_TENANT_NAME", transferTenantName);
        }
        Long orderId = transferInfoDto.getOrderId();
        if (orderId != null) {
            transferInfoQueryWrapper.like("order_id", orderId);
        }
        String plateNumber = transferInfoDto.getPlateNumber();
        if (StringUtils.isNoneBlank(plateNumber)) {
            transferInfoQueryWrapper.like("PLATE_NUMBER", plateNumber);
        }
        Long transferOrderId = transferInfoDto.getTransferOrderId();
        if (transferOrderId != null) {
            transferInfoQueryWrapper.like("TRANSFER_ORDER_ID", transferOrderId);
        }
        Integer transferOrderState = transferInfoDto.getTransferOrderState();
        if (transferOrderState != null) {
            transferInfoQueryWrapper.eq("TRANSFER_ORDER_STATE", transferOrderState);
        }

        String beginTranDate = transferInfoDto.getBeginTranDate();
        if (StringUtils.isNoneBlank(beginTranDate)) {
            String beginTranDateStr = beginTranDate + " 00:00:00";
            Date beginTran = DataFormat.stringToDateTime(beginTranDateStr);
            transferInfoQueryWrapper.ge("TRANSFER_DATE", beginTran);

        }
        String endTranDate = transferInfoDto.getEndTranDate();
        if (StringUtils.isNoneBlank(endTranDate)) {
            String endTranDateStr = endTranDate + " 23:59:59";
            Date endTran = DataFormat.stringToDateTime(endTranDateStr);
            transferInfoQueryWrapper.le("TRANSFER_DATE", endTran);
        }
        String beginAcceptDate = transferInfoDto.getBeginAcceptDate();
        if (StringUtils.isNoneBlank(beginAcceptDate)) {
            String beginAcceptDateStr = beginAcceptDate + " 00:00:00";
            Date beginAccept = DataFormat.stringToDateTime(beginAcceptDateStr);
            transferInfoQueryWrapper.ge("ACCEPT_DATE", beginAccept);
        }
        String endAcceptDate = transferInfoDto.getEndAcceptDate();
        if (StringUtils.isNoneBlank(endAcceptDate)) {
            String endAcceptDateStr = endAcceptDate + " 23:59:59";
            Date endAccept = DataFormat.stringToDateTime(endAcceptDateStr);
            transferInfoQueryWrapper.le("ACCEPT_DATE", endAccept);
        }

        transferInfoQueryWrapper.eq("ACCEPT_TENANT_ID", tenantId);
        transferInfoQueryWrapper.orderByDesc("create_time");
        Page<TransferInfo> transferInfoPage = transferInfoMapper.selectPage(page, transferInfoQueryWrapper);
        List<TransferInfo> records = transferInfoPage.getRecords();
        for (TransferInfo transferInfo : records) {
            VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(transferInfo.getPlateNumber());
            if (vehicleDataInfo != null) {
                TenantVehicleRel tenantVehicleRel = tenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
                if (tenantVehicleRel != null && tenantVehicleRel.getVehicleClass() != null) {
                    transferInfo.setVehicleCode(vehicleDataInfo.getId());
                    transferInfo.setVehicleClass(tenantVehicleRel.getVehicleClass());
                }
            }
        }

        return transferInfoPage;
    }

    @Override
    public List<TransferInfo> getOrderTransferInfoList(Long orderId, Long tenantId, Integer transferOrderState) {

        QueryWrapper<TransferInfo> transferInfoQueryWrapper = new QueryWrapper<>();
        transferInfoQueryWrapper.eq("order_id", orderId)
                .eq("accept_tenant_id", tenantId)
                .eq("transfer_order_state", transferOrderState);
        List<TransferInfo> transferInfos = transferInfoMapper.selectList(transferInfoQueryWrapper);
        return transferInfos;
    }

    @Override
    public int updateOrderTransferState(Long orderId, Long acceptTenantId, Integer transferOrderState, Date opDate, String remark) {

        QueryWrapper<TransferInfo> transferInfoQueryWrapper = new QueryWrapper<>();
        transferInfoQueryWrapper.eq("order_id", orderId)
                .eq("accept_tenant_id", acceptTenantId);
        TransferInfo transferInfo = new TransferInfo();

        transferInfo.setAcceptDate(localDateTime(opDate))
                .setTransferOrderState(transferOrderState)
                .setRemark(remark);
        int update = transferInfoMapper.update(transferInfo, transferInfoQueryWrapper);
        return update;
    }

    @Override
    public TransferInfo getOrderTransferInfo(Long orderId, Long tenantId) {

        QueryWrapper<TransferInfo> transferInfoQueryWrapper = new QueryWrapper<>();
        transferInfoQueryWrapper.eq("order_id", orderId)
                .eq("accept_tenant_id", tenantId)
                .eq("transfer_order_state", OrderConsts.TransferOrderState.TO_BE_RECIVE)
                .or()
                .eq("transfer_order_state", OrderConsts.TransferOrderState.BILL_YES);
        List<TransferInfo> transferInfos = transferInfoMapper.selectList(transferInfoQueryWrapper);
        if (transferInfos != null && transferInfos.size() > 0) {
            return transferInfos.get(0);
        }
        return null;
    }

    @Override
    public OrderDetailsTransferVo queryReceiveOrderDetail(String orderId, String authorization) throws Exception {
        LoginInfo loginInfo = (LoginInfo) Optional.ofNullable(redisUtil.get("loginInfo:" + authorization)).orElseThrow(() -> new IllegalArgumentException("请输入订单orderId !"));
        /**租户只能查看订单状态是待接单，已接单的数据*/
        TransferInfo transferInfo = transferInfoMapper.selectOne(
                new QueryWrapper<TransferInfo>()
                        .eq("order_id", Long.valueOf(orderId))
                        .eq("accept_tenant_id", loginInfo.getTenantId())
                        .eq("transfer_order_state", OrderConsts.TransferOrderState.TO_BE_RECIVE).or()
                        .eq("transfer_order_state", OrderConsts.TransferOrderState.BILL_YES)
                        .orderByDesc("id").last("limit 0,1")
        );
        Optional.ofNullable(transferInfo).orElseThrow(() -> new BusinessException("该数据不能查看"));
        /**根据order_id 查询orderInfo，orderInfo 如果为空去查历史表*/
        OrderInfo orderInfo = orderInfoService.getOrder(Long.parseLong(orderId));
        /**定义变量*/
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderGoods orderGoods = new OrderGoods();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        boolean isHis = false;
        /**
         * orderInfo 为空去查历史表，不空 查正现在的表
         * */
        if (orderInfo == null) {
            orderInfo = new OrderInfo();
            /**去查历史表*/
            OrderInfoH orderInfoH = orderInfoHService.getOne(new QueryWrapper<OrderInfoH>().eq("order_id", Long.valueOf(orderId)));
            Optional.ofNullable(orderInfoH).orElseThrow(() -> new BusinessException("未找到订单号[" + orderId + "]信息！"));
            //order_info_ext_h
            OrderInfoExtH orderInfoExtH = orderInfoExtHService.getOne(new QueryWrapper<OrderInfoExtH>().eq("order_id", Long.valueOf(orderId)));
            //order_scheduler_h
            OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOne(new QueryWrapper<OrderSchedulerH>().eq("order_id", Long.valueOf(orderId)));
            //order_goods_h
            OrderGoodsH orderGoodsH = orderGoodsHService.getOne(new QueryWrapper<OrderGoodsH>().eq("order_id", Long.valueOf(orderId)));
            //order_fee_h
            OrderFeeH orderFeeH = orderFeeHService.getOne(new QueryWrapper<OrderFeeH>().eq("order_id", Long.valueOf(orderId)));
            //order_fee_ext_h
            OrderFeeExtH orderFeeExtH = orderFeeExtHService.getOne(new QueryWrapper<OrderFeeExtH>().eq("order_id", Long.valueOf(orderId)));
            BeanUtils.copyProperties(orderInfo, orderInfoH);
            BeanUtils.copyProperties(orderFee, orderFeeH);
            BeanUtils.copyProperties(orderScheduler, orderSchedulerH);
            BeanUtils.copyProperties(orderGoods, orderGoodsH);
            BeanUtils.copyProperties(orderInfoExt, orderInfoExtH);
            BeanUtils.copyProperties(orderFeeExt, orderFeeExtH);
            isHis = true;
        } else {
            /**去查现在的表*/
            orderScheduler = orderSchedulerService.getOne(new QueryWrapper<OrderScheduler>().eq("order_id", Long.valueOf(orderId)));
            orderGoods = orderGoodsService.getOne(new QueryWrapper<OrderGoods>().eq("order_id", Long.valueOf(orderId)));
            orderInfoExt = orderInfoExtService.getOne(new QueryWrapper<OrderInfoExt>().eq("order_id", Long.valueOf(orderId)));
            orderFee = orderFeeService.getOne(new QueryWrapper<OrderFee>().eq("order_id", Long.valueOf(orderId)));
            orderFeeExt = orderFeeExtService.getOne(new QueryWrapper<OrderFeeExt>().eq("order_id", Long.valueOf(orderId)));
        }
        //一会儿有用
        //out.setIsHis(isHis ? OrderConsts.TableType.HIS : OrderConsts.TableType.ORI);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(transferInfo.getTransferTenantTenantId());
        setCustomerInfo(sysTenantDef, orderGoods);
        orderGoods.setLineName(orderGoods.getLocalUserName());
        orderGoods.setLinePhone(orderGoods.getLocalPhone());
        orderGoods.setReciveAddr(orderScheduler.getReciveAddr());
        if (orderGoods.getLineName() != null && !orderGoods.getLineName().equals("")) {
            orderGoods.setLinkName(orderGoods.getLineName());
        }
        if (orderGoods.getLinePhone() != null && !orderGoods.getLinePhone().equals("")) {
            orderGoods.setLinkPhone(orderGoods.getLinePhone());
        }


        // 查 costPaymentDaysInfo  下面塞进去
        OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoService.getOne(new QueryWrapper<OrderPaymentDaysInfo>().eq("order_id", Long.valueOf(orderId)).eq("payment_days_type", OrderConsts.PAYMENT_DAYS_TYPE.COST));
        if (costPaymentDaysInfo == null) {
            OrderPaymentDaysInfoH preCostPaymentDaysInfoH = orderPaymentDaysInfoHService.getOne(new QueryWrapper<OrderPaymentDaysInfoH>().eq("order_id", Long.valueOf(orderId)).eq("payment_days_type", OrderConsts.PAYMENT_DAYS_TYPE.COST));
            if (preCostPaymentDaysInfoH != null) {
                costPaymentDaysInfo = new OrderPaymentDaysInfo();
                BeanUtils.copyProperties(costPaymentDaysInfo, preCostPaymentDaysInfoH);
            }
        }
        //查 transitLineInfos 下面塞进去
        List<OrderTransitLineInfo> transitLineInfos = new ArrayList<OrderTransitLineInfo>();
        if (isHis) {
            List<OrderTransitLineInfoH> transitLineInfosH = orderTransitLineInfoHService.list(new QueryWrapper<OrderTransitLineInfoH>().eq("order_id", Long.valueOf(orderId)));
            if (transitLineInfosH != null && transitLineInfosH.size() > 0) {
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfosH) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    BeanUtils.copyProperties(transitLineInfo, orderTransitLineInfoH);
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            transitLineInfos = orderTransitLineInfoService.list(new QueryWrapper<OrderTransitLineInfo>().eq("order_id", Long.valueOf(orderId)));
        }
        //成本转收入
        if (costPaymentDaysInfo == null) {
            costPaymentDaysInfo = new OrderPaymentDaysInfo();
        }
        costPaymentDaysInfo.setId(null);
        costPaymentDaysInfo.setOrderId(null);
        costPaymentDaysInfo.setTenantId(null);
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        if (null != orderGoods && orderGoods.getVehicleLengh() != null && orderGoods.getVehicleStatus() != null && orderGoods.getVehicleStatus() > 0) {
            String name = "";

            SysStaticData staticData = readisUtil.getSysStaticData("VEHICLE_STATUS", orderGoods.getVehicleStatus().toString());
            List<SysStaticData> lengthList = readisUtil.getSysStaticDataList("VEHICLE_LENGTH");
            if (null != staticData && CollectionUtils.isNotEmpty(lengthList)) {
                for (SysStaticData lengthData : lengthList) {
                    if (Objects.equals(staticData.getCodeId(), lengthData.getCodeId()) && lengthData.getCodeValue().equals(orderGoods.getVehicleLengh())) {
                        name = lengthData.getCodeName();
                        break;
                    }
                }

            }
            orderGoods.setVehicleLenghName(name);
        }
        if (null != orderInfo) {
            if (orderInfo.getOrgId() != null) {
                SysUser sysUser = sysUserService.getSysOperatorByUserId(orderInfo.getOrgId().longValue());
                if (sysUser != null && sysUser.getOrgName() != null) {
                    orderInfo.setOrgName(sysUser.getOrgName());
                }
            }
        }
        if (orderScheduler.getCarLengh() != null && orderScheduler.getCarStatus() != null && orderScheduler.getCarStatus() > 0) {
            orderScheduler.setCarLengh(vehicleStaticDataUtil.getVehicleLengthName(orderScheduler.getCarStatus().toString(), orderScheduler.getCarLengh()));
        }
        OrderDetailsTransferVo result = OrderDetailsTransferVo.builder().orderFee(orderFee).orderInfo(orderInfo).orderGoods(orderGoods).
                orderInfoExt(orderInfoExt).orderScheduler(orderScheduler).orderFeeExt(orderFeeExt).incomePaymentDaysInfo(costPaymentDaysInfo).
                orderIncomePermission(true).orderCostPermission(true).transitLineInfos(transitLineInfos).build();
        return result;
    }

    @Override
    public Boolean receiveOrder(ReceiveOrderDto receiveOrderDto, String authorization) throws Exception {
        LoginInfo loginInfo = loginUtils.get(authorization);
        OrderDispatchInDto dispatchIn = Optional.ofNullable(receiveOrderDto.getDispatchInfo()).orElseThrow(() -> {
            log.error("调度信息为空{}", receiveOrderDto);
            return new BusinessException("调度信息为空");
        });
        OrderIncomeInDto incomeIn = Optional.ofNullable(receiveOrderDto.getIncomeIn()).orElseThrow(() -> {
            log.error("调度信息为空{}", receiveOrderDto);
            return new BusinessException("费用信息为空");
        });
        OrderPaymentDaysInfo dispatchPaymentDaysInfo = Optional.ofNullable(receiveOrderDto.getDispatchBalanceData()).orElseThrow(() -> {
            log.error("调度信息为空{}", receiveOrderDto);
            return new BusinessException("费用信息为空");
        });
        /**校验是否指派车辆  指派员工   */
        if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getAppointWay())
                || !String.valueOf(OrderConsts.AppointWay.APPOINT_CAR).equals(dispatchIn.getAppointWay())
                && !String.valueOf(OrderConsts.AppointWay.APPOINT_LOCAL).equals(dispatchIn.getAppointWay())) {
            log.error("指派方式错误{}", dispatchIn);
            throw new BusinessException("指派方式错误,请重新选择！");
        }
        //初始化一些变量
        OrderInfo orderInfo = new OrderInfo();
        OrderScheduler orderScheduler = new OrderScheduler();
        OrderFee orderFee = new OrderFee();
        OrderFeeExt orderFeeExt = new OrderFeeExt();
        OrderInfoExt orderInfoExt = new OrderInfoExt();
        OrderGoods orderGoods = new OrderGoods();
        //把前端传进来的值 赋值过去
        List<OrderOilDepotScheme> depotSchemes = new ArrayList<>();
        //设置订单信息
        setOrderInfo(orderInfo, dispatchIn);
        //设置费用信息
        setOrderFee(orderFee, incomeIn, dispatchIn);
        //设置调度信息
        setDispatchInfo(orderScheduler, dispatchIn, depotSchemes);
        //设置费用拓展表信息
        setOrderFeeExt(orderFeeExt, dispatchIn);
        //接单的时候,补贴金额需要除以100放进数据库
        if (orderFeeExt.getSalary() != null) {
            orderFeeExt.setSalary(orderFeeExt.getSalary() * 100);
            if (dispatchIn.getCarDriverSubsidyDate() != null) {
                orderFeeExt.setSubsidyDay(Integer.valueOf(dispatchIn.getCarDriverSubsidyDate()));
            }
        }
        //设置订单拓展表信息
        setOrderInfoExt(orderInfoExt, dispatchIn);
        //如果是指派车辆并且是承包模式中标价为0
        if (orderScheduler.getAppointWay() != null && orderScheduler.getAppointWay() == 3 && orderInfoExt.getPaymentWay() != null && orderInfoExt.getPaymentWay() == 3) {
            orderFee.setGuidePrice(0L);
        }
        //设置货物信息
        setOrderGoods(orderGoods, dispatchIn);
        //备注
        String remark = receiveOrderDto.getRemark();
        //设置订单油卡
        List<OrderOilCardInfo> orderOilCardInfos = new ArrayList<>();
        if (orderInfoExt.getPaymentWay() == null || orderInfoExt.getPaymentWay() != OrderConsts.PAYMENT_WAY.EXPENSE) {
            orderOilCardInfos = null;
        } else {
            List<OrderOilCardInfoVo> oilCardStr = receiveOrderDto.getDispatchInfo().getOilCardStr();
            for (OrderOilCardInfoVo orderOilCardInfoVo : oilCardStr) {
                String oilFee = orderOilCardInfoVo.getOilFee();
                if (oilFee != null) {
                    OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
                    org.springframework.beans.BeanUtils.copyProperties(orderOilCardInfoVo, orderOilCardInfo);
                    orderOilCardInfo.setOilFee(objToLongMul100(oilFee));
                    orderOilCardInfos.add(orderOilCardInfo);
                }
            }
        }
        if (orderInfoExt.getPaymentWay() != null && (orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.COST || orderInfoExt.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE)) {
            orderFeeExt.setCopilotSalary(orderFeeExt.getCopilotSalary() == null ? 0 : orderFeeExt.getCopilotSalary());
            orderFeeExt.setSalary(orderFeeExt.getSalary() == null ? 0 : orderFeeExt.getSalary());
            orderFeeExt.setPontage(orderFeeExt.getPontage() == null ? 0 : orderFeeExt.getPontage());
            orderFee.setPreOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            orderFee.setPreOilVirtualFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
            orderFee.setPreTotalFee(orderFeeExt.getCopilotSalary() + orderFeeExt.getSalary() + orderFeeExt.getPontage()
                    + orderFee.getPreOilFee() + orderFee.getPreOilVirtualFee());
        }
        //数据封装完成   插入数据  调用接口
        log.info("在线接单数据封装完成，调用acceptOrder{}", orderInfo.getOrderId());
        acceptOrder(orderInfo, orderGoods, orderInfoExt, orderScheduler, orderFee, orderFeeExt
                , depotSchemes, dispatchPaymentDaysInfo, orderOilCardInfos, remark, authorization, loginInfo);
        return true;
    }

    @Override
//    @Scheduled(cron = "0 */1 * * * ? ")
    public Boolean execution() {
        SysCfg sysCfg = readisUtil.getSysCfg(EnumConsts.SysCfg.TIME_OUT_TRANSFER_ORDER, "-1");
        if (sysCfg == null || org.apache.commons.lang.StringUtils.isEmpty(sysCfg.getCfgValue())) {
            throw new BusinessException("sys_cfg表的[" + EnumConsts.SysCfg.TIME_OUT_TRANSFER_ORDER + "]枚举值没有配置");
        }
        String timeOutHour = sysCfg.getCfgValue();//小时
        LocalDateTime tranDate = OrderDateUtil.subHourAndMins(LocalDateTime.now(), Float.valueOf(timeOutHour));

        List<OrderTransferInfo> orderTransferInfos = orderTransferInfoService.queryTransferInfo(tranDate, OrderConsts.TransferOrderState.TO_BE_RECIVE, 200);

        if (orderTransferInfos != null && orderTransferInfos.size() > 0) {
            for (OrderTransferInfo orderTransferInfo : orderTransferInfos) {
                String acceptTenantId = orderTransferInfo.getAcceptTenantId();
                Long aLong = Long.valueOf(acceptTenantId);
                orderTransferInfoService.timeOutOrder(null, orderTransferInfo.getOrderId(), aLong, "订单超过时间，系统进行撤单");
            }
        }
        return true;
    }

    public static LocalDateTime localDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    private void setCustomerInfo(SysTenantDef sysTenantDef, OrderGoods orderGoods) {
        Optional.ofNullable(sysTenantDef).orElseThrow(() -> new BusinessException("订单的车队信息为空"));
        Optional.ofNullable(orderGoods).orElseThrow(() -> new BusinessException("订单的货品信息为空"));
        orderGoods.setCompanyName(sysTenantDef.getName());
        orderGoods.setCustomName(sysTenantDef.getShortName());
        // orderGoods.setLinkName(orderGoods.getLocalUserName());
        // orderGoods.setLinkPhone(orderGoods.getLocalPhone());
        String address = org.apache.commons.lang.StringUtils.isNotEmpty(sysTenantDef.getProvinceName()) ? sysTenantDef.getProvinceName() : "";
        if (org.apache.commons.lang.StringUtils.isNotEmpty(sysTenantDef.getCityName())) {
            address = address + sysTenantDef.getCityName();
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(sysTenantDef.getDistrictName())) {
            address = address + sysTenantDef.getDistrictName();
        }
        if (org.apache.commons.lang.StringUtils.isNotEmpty(sysTenantDef.getAddress())) {
            address = address + sysTenantDef.getAddress();
        }
        orderGoods.setAddress(address);
    }


    /**
     * 设置订单信息
     *
     * @param orderInfo
     * @param dispatchIn
     */
    private void setOrderInfo(OrderInfo orderInfo, OrderDispatchInDto dispatchIn) {
        if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getOrgId())) {
            throw new BusinessException("请选择订单归属部门");
        }
        orderInfo.setOrderId(Long.parseLong(dispatchIn.getOrderId()));
        orderInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.PLATFORM);
        orderInfo.setOrderType(OrderConsts.OrderType.ONLINE_RECIVE);
        //如果不是选择自选车则需要赋予车辆归属车队
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getVehicleClass())
                && !dispatchIn.getVehicleClass().equals(String.valueOf(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1))) {
            //objToLongNull(dispatchIn.getTenantId())
            orderInfo.setToTenantId(objToLongNull(dispatchIn.getTenantId()));
            orderInfo.setToTenantName(dispatchIn.getTenantName());
        }

        orderInfo.setOrgId(Integer.parseInt(dispatchIn.getOrgId()));
        orderInfo.setIsNeedBill(DataFormat.getIntKey(dispatchIn.getIsNeedBill()));
        orderInfo.setRemark(dispatchIn.getRemark());

    }

    /**
     * 设置订单费用信息
     *
     * @param orderFee
     * @param incomeIn
     * @param dispatchIn
     */
    private void setOrderFee(OrderFee orderFee, OrderIncomeInDto incomeIn, OrderDispatchInDto dispatchIn) {

        //收款信息
        orderFee.setAcctName(dispatchIn.getAcctName());
        orderFee.setAcctNo(dispatchIn.getAcctNo());
        orderFee.setGuidePrice(objToLongMul100(dispatchIn.getGuidePrice()));
        orderFee.setInsuranceFee(objToLongMul100(dispatchIn.getInsuranceFee()));
        orderFee.setTotalFee(objToLongMul100(dispatchIn.getTotalFeeY()));
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getVehicleClass()) && dispatchIn.getVehicleClass().equals(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 + "")) {
            if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getPaymentWay()) && String.valueOf(OrderConsts.PAYMENT_WAY.CONTRACT).equals(dispatchIn.getPaymentWay())) {
                orderFee.setGuidePrice(objToLongMul100(dispatchIn.getTotalFeeY()));
            } else {
                //不是承包价时，不要设置中标价
                orderFee.setTotalFee(null);
            }
        }
        //指定驻场需要填写拦标价和中标价
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getAppointWay()) && String.valueOf(OrderConsts.AppointWay.APPOINT_LOCAL).equals(dispatchIn.getAppointWay())) {
            if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getGuidePriceY())) {
                throw new BusinessException("请填写拦标价！");
            }
            if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getTotalFeeY())) {
                throw new BusinessException("请填写承包价！");
            }
            orderFee.setGuidePrice(objToLongMul100(dispatchIn.getGuidePriceY()));
        }
        // orderFee.setPaymentDays(DataFormat.getIntKey(dispatchIn.getPaymentDays()));
        //如果是自有车，并且是承包价是不用设置费用的
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getVehicleClass()) && org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getPaymentWay())
                && dispatchIn.getVehicleClass().equals(String.valueOf(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1))
                && dispatchIn.getPaymentWay().equals(OrderConsts.PAYMENT_WAY.COST + "")) {
            orderFee.setPreOilFee(objToLongMul100(dispatchIn.getOilSelfEntity()));
            orderFee.setPreOilVirtualFee(objToLongMul100(dispatchIn.getOilSelfVirtual()));
            return;
        }

        //设置比例
        orderFee.setPreTotalFee(objToLongMul100(dispatchIn.getPreTotalFeeY()));
        orderFee.setPreCashFee(objToLongMul100(dispatchIn.getPreCashFeeY()));
        orderFee.setPreOilVirtualFee(objToLongMul100(dispatchIn.getPreOilVirtualFeeY()));
        orderFee.setPreOilFee(objToLongMul100(dispatchIn.getPreOilFeeY()));
        orderFee.setPreEtcFee(objToLongMul100(dispatchIn.getPreEtcFeeY()));
        orderFee.setFinalFee(objToLongMul100(dispatchIn.getFinalFeeY()));
        orderFee.setFinalScale(objToLongMul100(dispatchIn.getFinalScaleShow()));
        orderFee.setArrivePaymentFee(objToLongMul100(dispatchIn.getArrivePaymentFeeY()));
        orderFee.setArrivePaymentFeeScale(objToLongMul100(dispatchIn.getArrivePaymentFeeScaleShow()));

//        if (orderFee.getInsuranceFee() != null && orderFee.getInsuranceFee() > 0 && orderFee.getInsuranceFee() > orderFee.getFinalFee()) {
//            throw new BusinessException("保费不能大于尾款金额");
//        }
        //存4位小数 eg:32% =  3200
        orderFee.setPreTotalScale(objToLongMul100(dispatchIn.getPreTotalScaleShow()));
        orderFee.setPreCashScale(objToLongMul100(dispatchIn.getPreCashScaleShow()));
        orderFee.setPreOilVirtualScale(objToLongMul100(dispatchIn.getPreOilVirtualScaleShow()));
        orderFee.setPreOilScale(objToLongMul100(dispatchIn.getPreOilScaleShow()));
        orderFee.setPreEtcScale(objToLongMul100(dispatchIn.getPreEtcScaleShow()));

        orderFee.setPreTotalScaleStandard(mul(dispatchIn.getPreTotalScaleStandard(), 10000 + ""));
        orderFee.setPreCashScaleStandard(mul(dispatchIn.getPreCashScaleStandard(), 10000 + ""));
        orderFee.setPreOilVirtualScaleStandard(mul(dispatchIn.getPreOilVirtualScaleStandard(), 10000 + ""));
        orderFee.setPreOilScaleStandard(mul(dispatchIn.getPreOilScaleStandard(), 10000 + ""));
        orderFee.setPreEtcScaleStandard(mul(dispatchIn.getPreEtcScaleStandard(), 10000 + ""));
    }

    /**
     * 设置调派信息
     *
     * @param orderScheduler
     * @param dispatchIn
     * @param orderOilDepotSchemeList
     */
    private void setDispatchInfo(OrderScheduler orderScheduler, OrderDispatchInDto dispatchIn, List<OrderOilDepotScheme> orderOilDepotSchemeList) {

    	/*if(StringUtils.isBlank(dispatchIn.getReciveTime())){
            throw new BusinessException("请填写回单期限！");
        }
    	//承运商开票必须填写开票期限
        if(StringUtils.isBlank(dispatchIn.getInvoiceTime())&&"1".equals(dispatchIn.getIsNeedBill())){
            throw new BusinessException("请填写开票期限！");
        }*/

        if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getReciveAddr())) {
            throw new BusinessException("请填写回单地址！");
        }
        /*orderScheduler.setReciveTime(DataFormat.getIntKey(dispatchIn.getReciveTime()));
        orderScheduler.setInvoiceTime(DataFormat.getIntKey(dispatchIn.getInvoiceTime()));
        orderScheduler.setCollectionTime(DataFormat.getIntKey(dispatchIn.getCollectionTime()));*/
        orderScheduler.setReciveAddr(dispatchIn.getReciveAddr());
        orderScheduler.setAppointWay(DataFormat.getIntKey(dispatchIn.getAppointWay()));

        //指派员工
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
            orderScheduler.setDispatcherName(dispatchIn.getLocalUserName());
            orderScheduler.setDispatcherId(objToLong0(dispatchIn.getLocalUser()));
            orderScheduler.setDispatcherBill(dispatchIn.getLocalPhone());
        }
        //指派车辆
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            setAppointCarInfo(orderScheduler, orderOilDepotSchemeList, dispatchIn);
            if ((orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5)
                    && org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getIsAgent()) && "true".equals(dispatchIn.getIsAgent())) {
                if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getAgentPhone())) {
                    throw new BusinessException("收款人手机号为空或格式不正确！");
                }
                orderScheduler.setCollectionUserName(dispatchIn.getAgentAccountName());
                orderScheduler.setCollectionUserPhone(dispatchIn.getAgentPhone());
                orderScheduler.setCollectionUserName(dispatchIn.getAgentAccountName());
                orderScheduler.setIsCollection(1);
                orderScheduler.setCollectionName(dispatchIn.getAgentAccountName());
                orderScheduler.setCollectionUserId(org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getAgentId()) ? null : Long.parseLong(dispatchIn.getAgentId()));
            } else {
                orderScheduler.setIsCollection(null);
            }

            //账单接收人
            if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                    || orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                orderScheduler.setBillReceiverMobile(dispatchIn.getBillReceiverMobile());
                orderScheduler.setBillReceiverName(dispatchIn.getBillReceiverName());
                orderScheduler.setBillReceiverUserId(org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getBillReceiverUserId()) ? Long.valueOf(dispatchIn.getBillReceiverUserId()) : null);
            } else {
                orderScheduler.setBillReceiverMobile(null);
                orderScheduler.setBillReceiverName(null);
                orderScheduler.setBillReceiverUserId(null);
            }
        }
    }

    /**
     * 设置订单拓展表信息
     *
     * @param orderInfoExt
     * @param dispatchIn
     */
    private void setOrderInfoExt(OrderInfoExt orderInfoExt, OrderDispatchInDto dispatchIn) {
        //自有车
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getVehicleClass())
                && dispatchIn.getVehicleClass().equals(String.valueOf(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1))) {
            orderInfoExt.setCapacityOil(objToFloatMul100(dispatchIn.getEmptyOilCostPerY()));
            orderInfoExt.setRunOil(objToFloatMul100(dispatchIn.getOilCostPerY()));
            orderInfoExt.setIsUseCarOilCost(DataFormat.getIntKey(dispatchIn.getIsUseCarOilCost()));
            //空驶距离
            orderInfoExt.setRunWay(mul(dispatchIn.getRunWay(), 1000 + ""));
            //是否为回程货
            if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getIsBackhaul())
                    && "true".equals(dispatchIn.getIsBackhaul())) {
                if (org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getBackhaulLinkManId())) {
                    throw new BusinessException("请输入回程货联系人！");
                }
                orderInfoExt.setIsBackhaul(1);
                orderInfoExt.setBackhaulPrice(objToLongMul100(dispatchIn.getBackhaulPriceY()));
                orderInfoExt.setBackhaulLinkMan(dispatchIn.getBackhaulLinkMan());
                orderInfoExt.setBackhaulLinkManBill(dispatchIn.getBackhaulLinkManBill());
                orderInfoExt.setBackhaulLinkManId(objToLongNull(dispatchIn.getBackhaulLinkManId()));
            } else {
                orderInfoExt.setIsBackhaul(0);
            }

            if (objToInteger0(dispatchIn.getPaymentWay()) >= OrderConsts.PAYMENT_WAY.COST && objToInteger0(dispatchIn.getPaymentWay()) <= OrderConsts.PAYMENT_WAY.CONTRACT) {
                orderInfoExt.setPaymentWay(objToInteger0(dispatchIn.getPaymentWay()));
            } else {
                orderInfoExt.setPaymentWay(null);
            }

            //设置油是否需要发票，使用客户客户油还是本车队油
            orderInfoExt.setOilUseType(org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getOilUseType()) ? Integer.parseInt(dispatchIn.getOilUseType()) : null);
            if (orderInfoExt.getOilUseType() != null && orderInfoExt.getOilUseType() == OrderConsts.OIL_USE_TYPE.TENANTOIL) {
                orderInfoExt.setOilIsNeedBill("true".equals(dispatchIn.getOilIsNeedBill()) ? 1 : 0);
            } else {
                orderInfoExt.setOilIsNeedBill(0);
            }
        }
    }

    /**
     * 设置订单费用拓展表
     *
     * @param orderFeeExt
     * @param dispatchIn
     */
    private void setOrderFeeExt(OrderFeeExt orderFeeExt, OrderDispatchInDto dispatchIn) {
        orderFeeExt.setOilAccountType(org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getOilAccountType())
                ? OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1 : Integer.parseInt(dispatchIn.getOilAccountType()));
        orderFeeExt.setOilConsumer(org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getOilConsumer())
                ? OrderConsts.OIL_CONSUMER.SELF : Integer.parseInt(dispatchIn.getOilConsumer()));
        if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getVehicleClass()) && org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getPaymentWay())
                && dispatchIn.getVehicleClass().equals(String.valueOf(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1))) {
            if (!String.valueOf(OrderConsts.PAYMENT_WAY.EXPENSE).equals(dispatchIn.getPaymentWay())) {
                orderFeeExt.setPontagePer(objToLongMul100(dispatchIn.getPontagePerY()));
                orderFeeExt.setOilPrice(objToLongMul100(dispatchIn.getOilPrice()));

                orderFeeExt.setPontage(objToLongMul100(dispatchIn.getPontage()));
                //TODO 总油费 没有字段保存
                orderFeeExt.setEstFee(objToLongMul100(dispatchIn.getEstFee()));
                orderFeeExt.setOilLitreTotal(objToLongMul100(dispatchIn.getOilLitreTotal()));
                orderFeeExt.setOilLitreVirtual(objToLongMul100(dispatchIn.getOilLitreVirtual()));
                orderFeeExt.setOilLitreEntity(objToLongMul100(dispatchIn.getOilLitreEntity()));
            } else {
                orderFeeExt.setEstFee(objToLongMul100(dispatchIn.getPreOilFeeY()) + objToLongMul100(dispatchIn.getPreOilVirtualFeeY()));
            }
            if (String.valueOf(OrderConsts.PAYMENT_WAY.COST).equals(dispatchIn.getPaymentWay())) {
                orderFeeExt.setSalary(objToLongMul100(dispatchIn.getUserSubsidy()));
                orderFeeExt.setCopilotSalary(objToLongMul100(dispatchIn.getCopilotSubsidy()));
            }

            if (org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getOilUseType()) && OrderConsts.OIL_USE_TYPE.TENANTOIL == Integer.valueOf(dispatchIn.getOilUseType())) {
                orderFeeExt.setOilConsumer(org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getOilConsumer()) ? OrderConsts.OIL_CONSUMER.SELF : Integer.parseInt(dispatchIn.getOilConsumer()));
            } else {
                orderFeeExt.setOilConsumer(OrderConsts.OIL_CONSUMER.SELF);
            }
        } else {
            if (StringUtils.isNotBlank(dispatchIn.getIsNeedBill()) && Integer.parseInt(dispatchIn.getIsNeedBill()) == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                double preOilVirtualFee = org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getPreOilVirtualFeeY()) ? 0 : Double.valueOf(dispatchIn.getPreOilVirtualFeeY());
                if (preOilVirtualFee > 0) {
                    orderFeeExt.setOilAccountType(OrderAccountConst.OIL_ACCOUNT_TYPE.OIL_ACCOUNT_TYPE1);
                    orderFeeExt.setOilConsumer(OrderConsts.OIL_CONSUMER.SHARE);
                }
            } else {
                orderFeeExt.setOilConsumer(org.apache.commons.lang.StringUtils.isBlank(dispatchIn.getOilConsumer()) ? OrderConsts.OIL_CONSUMER.SELF : Integer.parseInt(dispatchIn.getOilConsumer()));
            }
        }
    }

    /**
     * 设置指派车辆的调度信息
     *
     * @param orderScheduler
     * @param orderOilDepotSchemeList
     * @param dispatchIn
     */
    private void setAppointCarInfo(OrderScheduler orderScheduler, List<OrderOilDepotScheme> orderOilDepotSchemeList, OrderDispatchInDto dispatchIn) {
        orderScheduler.setPlateNumber(dispatchIn.getPlateNumber());
        orderScheduler.setVehicleCode(objToLong0(dispatchIn.getVehicleCode()));
        orderScheduler.setCarLengh(dispatchIn.getVehicleLength());
        orderScheduler.setCarStatus(DataFormat.getIntKey(dispatchIn.getVehicleStatus()));

        orderScheduler.setCarDriverMan(dispatchIn.getCarDriverMan());
        orderScheduler.setCarDriverId(objToLong0(dispatchIn.getCarDriverId()));
        orderScheduler.setCarDriverPhone(dispatchIn.getCarDriverPhone());
        orderScheduler.setVehicleClass(DataFormat.getIntKey(dispatchIn.getVehicleClass()));
        orderScheduler.setLicenceType(DataFormat.getIntKey(dispatchIn.getLicenceType()));

        //自有车
        if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && org.apache.commons.lang.StringUtils.isNotBlank(dispatchIn.getPaymentWay())) {
            orderScheduler.setTrailerId(objToLong0(dispatchIn.getTrailerId()));
            orderScheduler.setTrailerPlate(dispatchIn.getTrailerPlate());
            orderScheduler.setCopilotMan(dispatchIn.getCopilotMan());
            orderScheduler.setCopilotPhone(dispatchIn.getCopilotPhone());
            orderScheduler.setCopilotUserId(objToLong0(dispatchIn.getCopilotUserId()));

            if (String.valueOf(OrderConsts.PAYMENT_WAY.COST).equals(dispatchIn.getPaymentWay())) {
                OrderOilDepotScheme oilDepotSchemeOne = new OrderOilDepotScheme();
                oilDepotSchemeOne.setOilDepotId(objToLong0(dispatchIn.getOilDepotIdOne()));
                oilDepotSchemeOne.setOilDepotName(dispatchIn.getOilDepotNameOne());
                oilDepotSchemeOne.setDependDistance(mul(dispatchIn.getDependDistanceOne(), 1000 + ""));
                oilDepotSchemeOne.setOilDepotPrice(objToLongMul100(dispatchIn.getOilDepotPriceOne()));
                oilDepotSchemeOne.setOilDepotLitre(objToLongMul100(dispatchIn.getOilDepotLitreOne()));
                oilDepotSchemeOne.setOilDepotFee(objToLongMul100(dispatchIn.getOilDepotFeeOne()));

                OrderOilDepotScheme oilDepotSchemeTwo = new OrderOilDepotScheme();
                oilDepotSchemeTwo.setOilDepotId(objToLong0(dispatchIn.getOilDepotIdTwo()));
                oilDepotSchemeTwo.setOilDepotName(dispatchIn.getOilDepotNameTwo());
                oilDepotSchemeTwo.setDependDistance(mul(dispatchIn.getDependDistanceTwo(), 1000 + ""));
                oilDepotSchemeTwo.setOilDepotPrice(objToLongMul100(dispatchIn.getOilDepotPriceTwo()));
                oilDepotSchemeTwo.setOilDepotLitre(objToLongMul100(dispatchIn.getOilDepotLitreTwo()));
                oilDepotSchemeTwo.setOilDepotFee(objToLongMul100(dispatchIn.getOilDepotFeeTwo()));

                OrderOilDepotScheme oilDepotSchemeThree = new OrderOilDepotScheme();
                oilDepotSchemeThree.setOilDepotId(objToLong0(dispatchIn.getOilDepotIdThree()));
                oilDepotSchemeThree.setOilDepotName(dispatchIn.getOilDepotNameThree());
                oilDepotSchemeThree.setDependDistance(mul(dispatchIn.getDependDistanceThree(), 1000 + ""));
                oilDepotSchemeThree.setOilDepotPrice(objToLongMul100(dispatchIn.getOilDepotPriceThree()));
                oilDepotSchemeThree.setOilDepotLitre(objToLongMul100(dispatchIn.getOilDepotLitreThree()));
                oilDepotSchemeThree.setOilDepotFee(objToLongMul100(dispatchIn.getOilDepotFeeThree()));

                orderOilDepotSchemeList.add(oilDepotSchemeOne);
                orderOilDepotSchemeList.add(oilDepotSchemeTwo);
                orderOilDepotSchemeList.add(oilDepotSchemeThree);
            }
        }
    }

    /**
     * 设置货物信息
     *
     * @param orderGoods
     * @param dispatchIn
     */
    private void setOrderGoods(OrderGoods orderGoods, OrderDispatchInDto dispatchIn) {
        if (org.apache.commons.lang.StringUtils.isEmpty(dispatchIn.getLocalUser())
                || org.apache.commons.lang.StringUtils.isEmpty(dispatchIn.getLocalUserName())
                || org.apache.commons.lang.StringUtils.isEmpty(dispatchIn.getLocalPhone())) {
            throw new BusinessException("请输入跟单人！");
        }
        orderGoods.setLocalUserName(dispatchIn.getLocalUserName());
        orderGoods.setLocalPhone(dispatchIn.getLocalPhone());
        orderGoods.setLocalUser(Long.parseLong(dispatchIn.getLocalUser()));
    }

    private List<OrderOilCardInfo> getOrderOilCards(String oilCardStr, Integer paymentWay) {
        if (paymentWay == null || paymentWay != OrderConsts.PAYMENT_WAY.EXPENSE) {
            return null;
        }
        List<OrderOilCardInfo> orderOilCardInfos = null;
        if (org.apache.commons.lang.StringUtils.isNotBlank(oilCardStr)) {
            orderOilCardInfos = new ArrayList<OrderOilCardInfo>();
            String[] arr = oilCardStr.split(";");
            for (String str : arr) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(str)) {
                    String[] arr2 = str.split(",");
                    OrderOilCardInfo orderOilCardInfo = new OrderOilCardInfo();
                    orderOilCardInfo.setOilCardNum(arr2[0]);
                    orderOilCardInfo.setOilFee(objToLongMul100(arr2[1]));
                    orderOilCardInfo.setCardType(Integer.parseInt(arr2[2]));
                    orderOilCardInfo.setCardChannel("true".equals(arr2[3]) ? OrderConsts.CARD_CHANNEL.ADD : OrderConsts.CARD_CHANNEL.VEHICLE_BIND);
                    orderOilCardInfos.add(orderOilCardInfo);
                }
            }
        }

        return orderOilCardInfos;
    }


    /**
     * 接单的逻辑
     * <p>
     * 中间表的数据的更改 新增订单数据 同步原来的订单表的状态
     *
     * @param orderInfo
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param depotSchemes
     * @param costPaymentDaysInfo
     * @param orderOilCardInfos
     * @param remark              接单的备注信息
     */
    private Long acceptOrder(OrderInfo orderInfo, OrderGoods orderGoods, OrderInfoExt orderInfoExt,
                             OrderScheduler orderScheduler, OrderFee orderFee, OrderFeeExt orderFeeExt,
                             List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo,
                             List<OrderOilCardInfo> orderOilCardInfos, String remark, String accessToken, LoginInfo loginInfo) throws Exception {
        // 校验
        // 根据原来的订单号，校验订单的接单租户，原来的订单的转单信息
        checkAcceptOrderInfo(orderInfo, loginInfo.getTenantId(), orderScheduler);
        // 把原有的订单数据组装一些，调用原来的订单录入
        Long toOrderId = saveNewOrderInfo(orderInfo, orderGoods, orderInfoExt, orderScheduler, orderFee, orderFeeExt,
                depotSchemes, costPaymentDaysInfo, orderOilCardInfos, accessToken, loginInfo);
        //重新赋值
        orderScheduler = iOrderSchedulerService.getOrderScheduler(toOrderId);
        // 回写原来的订单的数据
        updatePreOrderInfo(orderInfo.getOrderId(), toOrderId);
        // 回写原来的订单表的数据
        transferInfoMapper.updateOrderTransferState(orderInfo.getOrderId(), loginInfo.getTenantId(),
                OrderConsts.TransferOrderState.BILL_YES, new Date(), orderScheduler.getPlateNumber(), remark, toOrderId);
        //同步转单方异常
        iOrderProblemInfoService.saveTransferInfoProblem(loginInfo.getTenantId(), toOrderId, orderInfo.getOrderId());
        // 同步车辆信息
        orderTransferInfoService.syncOrderVehicleInfo(orderInfo.getOrderId(), orderInfo, orderScheduler);
        // 同步支付中心
        iOrderFeeService.synPayCenterAcceptOrder(orderInfo, orderScheduler);
        /*    orderFeeTF.synPayCenterAcceptOrder(orderInfo, orderScheduler);
         */
        //保存操作日志
        sysOperLogService.save(SysOperLogConst.BusiCode.OrderInfo, orderInfo.getOrderId(), SysOperLogConst.OperType.Update, "接单", loginInfo);

        return toOrderId;
    }

    /**
     * 回写转出去订单id,订单状态
     *
     * @param orderId
     * @param toOrderId
     * @throws Exception
     */
    private void updatePreOrderInfo(Long orderId, Long toOrderId) throws Exception {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId);
        OrderInfo preOrderInfo = orderInfoService.getOne(wrapper);

        if (preOrderInfo != null) {
            preOrderInfo.setToOrderId(toOrderId);
            if (preOrderInfo.getOrderState() == OrderConsts.ORDER_STATE.TO_BE_RECIVE) {
                preOrderInfo.setOrderState(OrderConsts.ORDER_STATE.TO_BE_LOAD);
            }
            orderInfoService.updateById(preOrderInfo);
        } else {
            QueryWrapper<OrderInfoH> wrapperH = new QueryWrapper<>();
            wrapperH.eq("order_id", orderId);
            OrderInfoH preOrderInfoH = orderInfoHService.getOne(wrapperH);
            if (preOrderInfoH != null) {
                preOrderInfoH.setToOrderId(toOrderId);
                orderInfoHService.updateById(preOrderInfoH);
            }
        }
    }

    /**
     * 校验原来的订单的状态，并校验接单的车队况，不能再进行跟原来的订单的信息是否一致 校验，如果是转租户的情转租户的操作
     * 校验，如果是指派车辆的信息，那传入的车辆信息要跟原来的订单信息一致
     * 校验：如果当前订单是开平台票，需要坚持一下来源的订单是否已经选择了开平台票，如果开了，不能勾选，不管 多少转了多少层，只有一个订单能开平台票
     *
     * @param inputOrderInfo 前端输入的订单信息
     * @param acceptTenantId
     * @param orderScheduler 当前订单是否勾选了开票
     * @throws Exception
     */
    private void checkAcceptOrderInfo(OrderInfo inputOrderInfo, Long acceptTenantId, OrderScheduler orderScheduler)
            throws Exception {
        Integer isNeedBill = inputOrderInfo.getIsNeedBill();
        Long orderId = inputOrderInfo.getOrderId();
        OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderId);
        OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
        OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
		/*if (OrderConsts.ORDER_STATE.TO_BE_RECIVE != orderInfo.getOrderState()) {
			throw new BusinessException("订单[" + orderId + "]不是处于待接单的状态，请刷新页面！");
		}*/
        if (orderInfo.getToTenantId() == null) {
            log.error("订单[" + orderId + "]没有转单出去，请联系客服");
            throw new BusinessException("订单[" + orderId + "]没有转单出去，请联系客服");
        }
        if (orderInfo.getToTenantId().longValue() != acceptTenantId.longValue()) {
            log.error("订单[\" + orderId + \"]的接单的车队不是本车队，请联系客服！");
            throw new BusinessException("订单[" + orderId + "]的接单的车队不是本车队，请联系客服！");
        }

        // 校验转单的中间表的状态
        TransferInfo orderTransferInfo = transferInfoMapper.selectOne(
                new QueryWrapper<TransferInfo>()
                        .eq("order_id", orderId)
                        .eq("accept_tenant_id", acceptTenantId)
                        .eq("transfer_order_state", OrderConsts.TransferOrderState.TO_BE_RECIVE)
        );
        Optional.ofNullable(orderTransferInfo).orElseThrow(() -> new BusinessException("接单数据有误！"));
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_TENANT) {
            // 指派车队
            log.error("在线接单不能再进行指派车队的操作！" + orderId);
            throw new BusinessException("在线接单不能再进行指派车队的操作！");
        }

        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && preOrderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR) {
            if (orderScheduler.getVehicleCode().longValue() != preOrderScheduler.getVehicleCode().longValue()) {
                log.error("接单车队不能更改车辆信息！" + orderId);
                throw new BusinessException("接单车队不能更改车辆信息");
            }
        }
        OrderInfo preOrderInfo = orderInfoService.getOne(new QueryWrapper<OrderInfo>().eq("order_id", orderInfo.getFromOrderId()));
        OrderInfoH preOrderInfoH = orderInfoHService.getOne(new QueryWrapper<OrderInfoH>().eq("order_id", orderInfo.getFromOrderId()));
        if (isNeedBill == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
            // 如果当前用户选择了需要平台开票，需要校验一下源订单的是否已经选择了开平台票，如果已经选择了，不能进行平台开票
            if (orderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                log.error("抱歉！开单方已要求平台开票，因此平台不再提供票据给您！" + orderId);
                throw new BusinessException("抱歉！开单方已要求平台开票，因此平台不再提供票据给您。");

            }
            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {
                // 有再上一层的订单，需要校验一下
                if (preOrderInfo != null) {
                    if (preOrderInfo.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                        log.error("抱歉！开单方已要求平台开票，因此平台不再提供票据给您！" + orderId);
                        throw new BusinessException("抱歉！开单方已要求平台开票，因此平台不再提供票据给您。");
                    }
                } else {
                    if (preOrderInfoH != null) {
                        if (preOrderInfoH.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                            log.error("抱歉！开单方已要求平台开票，因此平台不再提供票据给您！" + orderId);
                            throw new BusinessException("抱歉！开单方已要求平台开票，因此平台不再提供票据给您。");
                        }
                    }
                }
            }

        }

        // A——》B，B不能给A的
        if (orderScheduler.getAppointWay() == OrderConsts.AppointWay.APPOINT_CAR
                && (orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2
                || orderScheduler.getVehicleClass().intValue() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4
        )
                && inputOrderInfo.getToTenantId() != null && inputOrderInfo.getToTenantId() > 0) {
            // 指派车辆，并且是有归属车队的外调车，需要校验一下该车辆的车队，是不是来源订单的车队的，如果是，要抛出异常
            if (orderInfo.getTenantId().longValue() == inputOrderInfo.getToTenantId().longValue()) {
                log.error("该车辆归属订单来源车队，不能选择！" + orderId);
                throw new BusinessException("该车辆归属订单来源车队，不能选择！");
            }

            if (orderInfo.getFromOrderId() != null && orderInfo.getFromOrderId() > 0) {

                // 有再上一层的订单，需要校验一下
                if (preOrderInfo != null) {
                    if (preOrderInfo.getTenantId().longValue() == inputOrderInfo.getToTenantId().longValue()) {
                        log.error("该车辆归属订单来源车队，不能选择！" + orderId);
                        throw new BusinessException("该车辆归属订单来源车队，不能选择！");
                    }
                } else {
                    if (preOrderInfoH != null) {
                        if (preOrderInfoH.getTenantId().longValue() == inputOrderInfo.getToTenantId().longValue()) {
                            log.error("该车辆归属订单来源车队，不能选择！" + orderId);
                            throw new BusinessException("该车辆归属订单来源车队，不能选择！");
                        }
                    }
                }
            }

        }

    }

    /**
     * 保存新的订单数据
     * <p>
     * 根据页面传入的数据跟原来的订单表的数据，组装订单信息，进行保存
     *
     * @param orderInfo
     * @param orderScheduler
     * @param orderFee
     * @param orderFeeExt
     * @param depotSchemes
     * @param costPaymentDaysInfo
     * @param orderOilCardInfos
     * @return
     * @throws Exception
     */
    private Long saveNewOrderInfo(OrderInfo orderInfo, OrderGoods orderGoods, OrderInfoExt orderInfoExt,
                                  OrderScheduler orderScheduler, OrderFee orderFee, OrderFeeExt orderFeeExt,
                                  List<OrderOilDepotScheme> depotSchemes, OrderPaymentDaysInfo costPaymentDaysInfo
            , List<OrderOilCardInfo> orderOilCardInfos, String accessToken, LoginInfo loginInfo) throws Exception {
        OrderInfo newOrderInfo = new OrderInfo();
        OrderFee newOrderFee = new OrderFee();
        OrderGoods newOrderGoods = new OrderGoods();
        OrderInfoExt newOrderInfoExt = new OrderInfoExt();
        OrderFeeExt newOrderFeeExt = new OrderFeeExt();
        OrderScheduler newOrderScheduler = new OrderScheduler();
        OrderDetailsOutDto orderDetailsOut = orderTransferInfoService.getOrderAll(orderInfo.getOrderId());
        OrderInfo preOrderInfo = orderDetailsOut.getOrderInfo();
        OrderGoods preOrderGoods = orderDetailsOut.getOrderGoods();
        OrderFee preOrderFee = orderDetailsOut.getOrderFee();
        OrderScheduler preOrderScheduler = orderDetailsOut.getOrderScheduler();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(preOrderInfo.getTenantId());
        // 订单表的数据
        newOrderInfo.setFromTenantId(preOrderInfo.getTenantId());
        newOrderInfo.setTenantId(loginInfo.getTenantId());
        newOrderInfo.setOpOrgId(loginInfo.getOrgIds().get(0));
        newOrderInfo.setOpName(loginInfo.getName());
        newOrderInfo.setOrderType(OrderConsts.OrderType.ONLINE_RECIVE);
        newOrderInfo.setSourceRegion(preOrderInfo.getSourceRegion());
        newOrderInfo.setDesRegion(preOrderInfo.getDesRegion());
        newOrderInfo.setSourceProvince(preOrderInfo.getSourceProvince());
        newOrderInfo.setDesProvince(preOrderInfo.getDesProvince());
        newOrderInfo.setSourceCounty(preOrderInfo.getSourceCounty());
        newOrderInfo.setDesCounty(preOrderInfo.getDesCounty());
        newOrderInfo.setRemark(preOrderInfo.getRemark());
        if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {
            newOrderInfo.setToTenantId(orderInfo.getToTenantId());
            newOrderInfo.setToTenantName(orderInfo.getToTenantName());
        }
        newOrderInfo.setOrgId(orderInfo.getOrgId());
        newOrderInfo.setFromOrderId(preOrderInfo.getOrderId());
        newOrderInfo.setSourceFlag(preOrderInfo.getSourceFlag());
        newOrderInfo.setIsNeedBill(orderInfo.getIsNeedBill());
        // 订单货物表
        newOrderGoods.setSource(preOrderGoods.getSource());
        newOrderGoods.setDes(preOrderGoods.getDes());
        newOrderGoods.setGoodsName(preOrderGoods.getGoodsName());
        newOrderGoods.setGoodsType(preOrderGoods.getGoodsType());
        newOrderGoods.setSquare(preOrderGoods.getSquare());
        newOrderGoods.setWeight(preOrderGoods.getWeight());
        newOrderGoods.setFromTenantId(loginInfo.getTenantId());
        newOrderGoods.setRecivePhone(preOrderGoods.getRecivePhone());
        newOrderGoods.setReciveName(preOrderGoods.getReciveName());
        newOrderGoods.setContactPhone(preOrderGoods.getContactPhone());
        newOrderGoods.setReciveType(preOrderGoods.getReciveType());
        newOrderGoods.setContactName(preOrderGoods.getContactName());

        setCustomerInfo(sysTenantDef, newOrderGoods);
        newOrderGoods.setLineName(preOrderGoods.getLocalUserName());
        newOrderGoods.setLinePhone(preOrderGoods.getLocalPhone());

        newOrderGoods.setCustomNumber(orderInfo.getOrderId() + "");

        newOrderGoods.setNand(preOrderGoods.getNand());
        newOrderGoods.setEand(preOrderGoods.getEand());
        newOrderGoods.setNandDes(preOrderGoods.getNandDes());
        newOrderGoods.setEandDes(preOrderGoods.getEandDes());
        newOrderGoods.setVehicleLengh(preOrderGoods.getVehicleLengh());
        newOrderGoods.setVehicleStatus(preOrderGoods.getVehicleStatus());
        newOrderGoods.setLocalUser(orderGoods.getLocalUser());
        newOrderGoods.setLocalPhone(orderGoods.getLocalPhone());
        newOrderGoods.setLocalUserName(orderGoods.getLocalUserName());
        newOrderGoods.setDesDtl(preOrderGoods.getDesDtl());
        newOrderGoods.setNavigatDesLocation(preOrderGoods.getNavigatDesLocation());
        newOrderGoods.setNavigatSourceLocation(preOrderGoods.getNavigatSourceLocation());
        newOrderGoods.setTenantId(loginInfo.getTenantId());
        newOrderGoods.setReciveAddr(preOrderScheduler.getReciveAddr());
        newOrderGoods.setAddrDtl(preOrderGoods.getAddrDtl());
        newOrderGoods.setNand(preOrderGoods.getNand());
        newOrderGoods.setEand(preOrderGoods.getEand());
        newOrderGoods.setDesDtl(preOrderGoods.getDesDtl());
        newOrderGoods.setNandDes(preOrderGoods.getNandDes());
        newOrderGoods.setEandDes(preOrderGoods.getEandDes());
        newOrderGoods.setNavigatDesLocation(preOrderGoods.getNavigatDesLocation());
        newOrderGoods.setNavigatSourceLocation(preOrderGoods.getNavigatSourceLocation());
        newOrderGoods.setContractUrl(preOrderGoods.getContractUrl());
        newOrderGoods.setContractId(preOrderGoods.getContractId());
        newOrderGoods.setReciveProvinceId(preOrderGoods.getReciveProvinceId());
        newOrderGoods.setReciveCityId(preOrderGoods.getReciveCityId());

        // 订单扩展表的数据
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
        newOrderInfoExt.setOilUseType(orderInfoExt.getOilUseType());
        newOrderInfoExt.setOilIsNeedBill(orderInfoExt.getOilIsNeedBill());

        // 订单费用 原来的订单的成本的费用做为订单的收入费用
        newOrderFee = orderFee;
        newOrderFee.setCostPrice(preOrderFee.getTotalFee());
        newOrderFee.setPriceEnum(orderFee.getPriceEnum());
        newOrderFee.setPriceUnit(orderFee.getPriceUnit());
        newOrderFee.setPreEtcScale(orderFee.getPreEtcScale());
        newOrderFee.setPreEtcScaleStandard(orderFee.getPreEtcScaleStandard());

        newOrderFee.setPreOilScale(orderFee.getPreOilScale());
        newOrderFee.setPreOilScaleStandard(orderFee.getPreOilScaleStandard());

        newOrderFee.setPreTotalScale(orderFee.getPreTotalScale());
        newOrderFee.setPreTotalScaleStandard(orderFee.getPreTotalScaleStandard());

        newOrderFee.setPreCashScale(orderFee.getPreCashScale());
        newOrderFee.setPreCashScaleStandard(orderFee.getPreCashScaleStandard());

        newOrderFee.setPreOilVirtualScale(orderFee.getPreOilVirtualScale());
        newOrderFee.setPreOilVirtualScaleStandard(orderFee.getPreOilVirtualScaleStandard());

        newOrderFee.setArrivePaymentFeeScale(orderFee.getArrivePaymentFeeScale());
        newOrderFee.setArrivePaymentFee(orderFee.getArrivePaymentFee());

        //  newOrderFee.setFinalFee(preOrderFee.getFinalFee());
        newOrderFee.setFinalScale(orderFee.getFinalScale());
        newOrderFeeExt = orderFeeExt == null ? newOrderFeeExt : orderFeeExt;

        // 订单费用扩展表 ,原来的订单的成本的费用做为订单的收入费用
        newOrderFeeExt.setIncomeCashFee(preOrderFee.getPreCashFee());
        newOrderFeeExt.setIncomeOilVirtualFee(preOrderFee.getPreOilVirtualFee());
        newOrderFeeExt.setIncomeOilFee(preOrderFee.getPreOilFee());
        newOrderFeeExt.setIncomeEtcFee(preOrderFee.getPreEtcFee());
        newOrderFeeExt.setIncomeArrivePaymentFee(preOrderFee.getArrivePaymentFee());
        newOrderFeeExt.setIncomeFinalFee(preOrderFee.getFinalFee());
        newOrderFeeExt.setIncomeInsuranceFee(preOrderFee.getInsuranceFee());

        newOrderScheduler.setPlateNumber(orderScheduler.getPlateNumber());
        newOrderScheduler.setCarDriverPhone(orderScheduler.getCarDriverPhone());
        newOrderScheduler.setCarDriverMan(orderScheduler.getCarDriverMan());
        newOrderScheduler.setCarDriverId(orderScheduler.getCarDriverId());
        newOrderScheduler.setVehicleCode(orderScheduler.getVehicleCode());
        newOrderScheduler.setCarLengh(orderScheduler.getCarLengh());
        newOrderScheduler.setVehicleClass(orderScheduler.getVehicleClass());
        newOrderScheduler.setCarStatus(orderScheduler.getCarStatus());
        newOrderScheduler.setAppointWay(orderScheduler.getAppointWay());
        newOrderScheduler.setLicenceType(orderScheduler.getLicenceType());
        newOrderScheduler.setClientContractId(preOrderScheduler.getClientContractId());
        newOrderScheduler.setClientContractUrl(preOrderScheduler.getClientContractUrl());
        newOrderScheduler.setCollectionName(orderScheduler.getCollectionName());
		/*if (preOrderScheduler.getAppointWay() == AppointWay.APPOINT_TENANT) {
			// 指派车队，调度的全部信息从前端传入
			newOrderScheduler.setPlateNumber(orderScheduler.getPlateNumber());
			newOrderScheduler.setCarDriverPhone(orderScheduler.getCarDriverPhone());
			newOrderScheduler.setCarDriverMan(orderScheduler.getCarDriverMan());
			newOrderScheduler.setCarDriverId(orderScheduler.getCarDriverId());
			newOrderScheduler.setVehicleCode(orderScheduler.getVehicleCode());
			newOrderScheduler.setCarLengh(orderScheduler.getCarLengh());
			newOrderScheduler.setVehicleClass(orderScheduler.getVehicleClass());
			newOrderScheduler.setCarStatus(orderScheduler.getCarStatus());
			newOrderScheduler.setAppointWay(orderScheduler.getAppointWay());
		} else if (preOrderScheduler.getAppointWay() == AppointWay.APPOINT_CAR) {
			// 指派车辆，车辆信息是从原来的订单的数据获取，并且车辆类型应该是自有车
			newOrderScheduler.setPlateNumber(preOrderScheduler.getPlateNumber());
			newOrderScheduler.setCarDriverPhone(preOrderScheduler.getCarDriverPhone());
			newOrderScheduler.setCarDriverMan(preOrderScheduler.getCarDriverMan());
			newOrderScheduler.setCarDriverId(preOrderScheduler.getCarDriverId());
			newOrderScheduler.setVehicleCode(preOrderScheduler.getVehicleCode());
			newOrderScheduler.setCarLengh(preOrderScheduler.getCarLengh());
			newOrderScheduler.setCarStatus(preOrderScheduler.getCarStatus());
			// 设置为自有车
			newOrderScheduler.setVehicleClass(VEHICLE_CLASS.VEHICLE_CLASS1);
			newOrderScheduler.setAppointWay(AppointWay.APPOINT_CAR);
		}*/

        newOrderScheduler.setCopilotMan(orderScheduler.getCopilotMan());
        newOrderScheduler.setCopilotPhone(orderScheduler.getCopilotPhone());
        newOrderScheduler.setCopilotUserId(orderScheduler.getCopilotUserId());
        newOrderScheduler.setDispatcherName(orderScheduler.getDispatcherName());
        newOrderScheduler.setDispatcherId(orderScheduler.getDispatcherId());
        newOrderScheduler.setTrailerPlate(orderScheduler.getTrailerPlate());
        newOrderScheduler.setTrailerId(orderScheduler.getTrailerId());
        newOrderScheduler.setDispatcherBill(orderScheduler.getDispatcherBill());
        newOrderScheduler.setIsCollection(orderScheduler.getIsCollection());
        newOrderScheduler.setCollectionUserId(orderScheduler.getCollectionUserId());
        newOrderScheduler.setCollectionUserName(orderScheduler.getCollectionUserName());
        newOrderScheduler.setCollectionUserName(orderScheduler.getCollectionUserName());
        newOrderScheduler.setCollectionUserPhone(orderScheduler.getCollectionUserPhone());
        newOrderScheduler.setBillReceiverMobile(orderScheduler.getBillReceiverMobile());
        newOrderScheduler.setBillReceiverName(orderScheduler.getBillReceiverName());
        newOrderScheduler.setBillReceiverUserId(orderScheduler.getBillReceiverUserId());


        newOrderScheduler.setDistance(preOrderScheduler.getDistance());
        newOrderScheduler.setMileageNumber(preOrderScheduler.getMileageNumber());

        newOrderScheduler.setIsUrgent(preOrderScheduler.getIsUrgent());
        newOrderScheduler.setDependTime(preOrderScheduler.getDependTime());
        newOrderScheduler.setArriveTime(preOrderScheduler.getArriveTime());

        newOrderScheduler.setReciveAddr(orderScheduler.getReciveAddr());

        OrderPaymentDaysInfo preCostPaymentDaysInfo = orderPaymentDaysInfoService.getOne(new QueryWrapper<OrderPaymentDaysInfo>().eq("order_id", orderInfo.getOrderId()).eq("payment_days_type", OrderConsts.PAYMENT_DAYS_TYPE.COST));
        if (preCostPaymentDaysInfo != null) {
            OrderPaymentDaysInfoH preCostPaymentDaysInfoH = orderPaymentDaysInfoHService.getOne(new QueryWrapper<OrderPaymentDaysInfoH>().eq("order_id", orderInfo.getOrderId()).eq("payment_days_type", OrderConsts.PAYMENT_DAYS_TYPE.INCOME));
            if (preCostPaymentDaysInfoH != null) {
                preCostPaymentDaysInfo = new OrderPaymentDaysInfo();
                BeanUtils.copyProperties(preCostPaymentDaysInfo, preCostPaymentDaysInfoH);
            }
        }
        //成本转收入
        if (preCostPaymentDaysInfo == null) {
            preCostPaymentDaysInfo = new OrderPaymentDaysInfo();
        }
        preCostPaymentDaysInfo.setId(null);
        preCostPaymentDaysInfo.setOrderId(null);
        preCostPaymentDaysInfo.setTenantId(null);
        preCostPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);
        //继承来源订单的经停点
        List<OrderTransitLineInfo> transitLineInfos = new ArrayList<OrderTransitLineInfo>();
        if (orderDetailsOut.getIsHis().intValue() == OrderConsts.TableType.HIS) {
            List<OrderTransitLineInfoH> transitLineInfosH = orderTransitLineInfoHService.list(new QueryWrapper<OrderTransitLineInfoH>().eq("order_id", orderInfo.getOrderId()));
            if (transitLineInfosH != null && transitLineInfosH.size() > 0) {
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfosH) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    BeanUtils.copyProperties(transitLineInfo, orderTransitLineInfoH);
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            transitLineInfos = orderTransitLineInfoService.list(new QueryWrapper<OrderTransitLineInfo>().eq("order_id", orderInfo.getOrderId()));
        }
        Long orderId = orderInfoService.saveOrUpdateOrderInfo(newOrderInfo, newOrderFee, newOrderGoods, newOrderInfoExt,
                newOrderFeeExt, newOrderScheduler, depotSchemes, costPaymentDaysInfo, preCostPaymentDaysInfo, orderOilCardInfos,
                transitLineInfos, false, accessToken, loginInfo);
        return orderId;
    }


    @Override
    public Long acceptOrderWx(AcceptOrderVo acceptOrderVo, String accessToken) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (acceptOrderVo.getOrderScheduler().getAppointWay() == OrderConsts.AppointWay.APPOINT_LOCAL) {
            // 指派员工
            acceptOrderVo.getOrderScheduler().setDispatcherName(acceptOrderVo.getOrderGoods().getLocalUserName());
            acceptOrderVo.getOrderScheduler().setDispatcherId(acceptOrderVo.getOrderGoods().getLocalUser());
            acceptOrderVo.getOrderScheduler().setDispatcherBill(acceptOrderVo.getOrderGoods().getLocalPhone());
        }
        Long orderId = this.acceptOrder(acceptOrderVo.getOrderInfo(), acceptOrderVo.getOrderGoods(), acceptOrderVo.getOrderInfoExt(), acceptOrderVo.getOrderScheduler(), acceptOrderVo.getOrderFee(),
                acceptOrderVo.getOrderFeeExt(), acceptOrderVo.getDepotSchemes(), acceptOrderVo.getCostPaymentDaysInfo(), acceptOrderVo.getOrderOilCardInfos(), "", accessToken, loginInfo);
        return orderId;
    }

    @Override
    public OrderTransferInfoDetailDto OrderTransferInfoDetail(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderDetailsTransferDto transferOut = getOrderTransferInfoDetail(orderId, loginInfo.getTenantId(), accessToken);
        OrderInfo orderInfo = transferOut.getOrderInfo();
        OrderGoods orderGoods = transferOut.getOrderGoods();
        OrderScheduler orderScheduler = transferOut.getOrderScheduler();
        OrderFee orderFee = transferOut.getOrderFee();

        OrderTransferInfoDetailDto dto = new OrderTransferInfoDetailDto();
        dto.setSourceRegion(orderInfo.getSourceRegionName());// "线路起始城市"
        dto.setDesRegion(orderInfo.getDesRegionName());// "线路目的城市"
        dto.setOrderId(orderInfo.getOrderId());// "订单号"

        dto.setTenantName(orderInfo.getTenantName());// "订单来源"
        dto.setLinkPhone(orderGoods.getLinkPhone());// "联系电话"
        dto.setDependTime(orderScheduler.getDependTime());// "装车时间"
        dto.setArriveTime(orderScheduler.getArriveTime());// "交货时间"
        dto.setSource(orderGoods.getSource());// "起始地点"
        dto.setDes(orderGoods.getDes());// "交货地点"
        dto.setDistance(orderScheduler.getDistance());// "运输距离"
        dto.setGoodsName(orderGoods.getGoodsName());// "货物名称"

        dto.setWeight(orderGoods.getWeight());// "重量"
        dto.setSquare(orderGoods.getSquare());// "体积"
        dto.setVehicleLengh(orderGoods.getVehicleLengh());// "车长"
        dto.setVehicleStatus(orderGoods.getVehicleStatus());// "车辆类型"
        dto.setLocalUserName(orderGoods.getLocalUserName());// "跟单人员"
        dto.setLocalPhone(orderGoods.getLocalPhone());// "跟单人电话"

        dto.setInsuranceFee(orderFee.getInsuranceFee());// "运费保险"
        dto.setPreCashFee(orderFee.getPreCashFee());// "现金"
        dto.setPreOilVirtualFee(orderFee.getPreOilVirtualFee());// "油账户"
        dto.setPreOilFee(orderFee.getPreOilFee());// "油卡"
        dto.setPreEtcFee(orderFee.getPreEtcFee());// "ETC"
        dto.setFinalFee(orderFee.getFinalFee());// "尾款"
        dto.setTotalFee(orderFee.getTotalFee());// "尾款"

        dto.setPlateNumber(orderScheduler.getPlateNumber());// "车牌号"
        dto.setCarDriverMan(orderScheduler.getCarDriverMan());// "司机名称"
        dto.setCarDriverPhone(orderScheduler.getCarDriverPhone());// "司机号码"
        dto.setSourceId(orderScheduler.getSourceId());// 线路ID
        dto.setMileageNumber(orderScheduler.getMileageNumber());// "供应商距离"
        List<OrderTransitLineInfo> transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());
        dto.setTransitLineInfos(transitLineInfos);
        Map tenantMap = sysTenantDefService.getTenantInfo(loginInfo.getTenantId());
        //新增字段：
        dto.setReciveAddr(String.valueOf(tenantMap.get("detailAddr")));// "当前车队的办公地址就是回单地址"
        Integer provinceId = null;
        if (tenantMap.get("provinceId") != null) {
            provinceId = Integer.parseInt(tenantMap.get("provinceId").toString());
        }

        Integer cityId = null;
        if (tenantMap.get("cityId") != null) {
            cityId = Integer.parseInt(tenantMap.get("cityId").toString());
        }
        dto.setProvinceId(provinceId);
        dto.setCityId(cityId);
        if (provinceId != null) {
            dto.setProvinceName(readisUtil.getSysStaticData("SYS_PROVINCE", provinceId.toString()).getCodeName());
        }
        if (cityId != null) {
            dto.setCityName(readisUtil.getSysStaticData("SYS_CITY", cityId.toString()).getCodeName());
        }
        dto.setEand(orderGoods.getEand());// "起始地经纬度"
        dto.setNand(orderGoods.getNand());// "起始地经纬度"

        dto.setEandDes(orderGoods.getEandDes());// "目的地经纬度"
        dto.setNandDes(orderGoods.getNandDes());// "目的地经纬度"
        dto.setSourceProvince(orderInfo.getSourceProvince());// "靠台地的省份id"

        dto.setArriveTimeStr(orderDateUtil.addHourAndMins(orderScheduler.getDependTime(), orderScheduler.getArriveTime()));// "交货时间"

        dto.setSourceRegionId(orderInfo.getSourceRegion());// "线路起始城市id"

        if (orderInfo.getSourceRegion() != null) {
            dto.setSourceRegion(readisUtil.getSysStaticData("SYS_CITY", String.valueOf(orderInfo.getSourceRegion())).getCodeName());
        }
        if (orderInfo.getDesRegion() != null) {
            dto.setDesRegion(readisUtil.getSysStaticData("SYS_CITY", String.valueOf(orderInfo.getDesRegion())).getCodeName());
        }
        return dto;
    }

    @Override
    public OrderDetailsTransferDto getOrderTransferInfoDetail(Long orderId, Long tenantId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        // 租户只能查看订单状态是待接单，已接单的数据
        OrderTransferInfo orderTransferInfo = orderTransferInfoService.getOrderTransferInfo(orderId, tenantId,
                OrderConsts.TransferOrderState.TO_BE_RECIVE, OrderConsts.TransferOrderState.BILL_YES);
        if (orderTransferInfo == null) {
            throw new BusinessException("该数据不能查看");
        }
        //权限控制
        boolean orderIncomePermission = iSysRoleService.hasOrderIncomePermission(loginInfo);
        boolean orderCostPermission = iSysRoleService.hasOrderCostPermission(loginInfo);
        OrderDetailsOutDto orderDetailsOut = null;
        try {
            orderDetailsOut = lugeOrderBusinessService.getOrderById(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        OrderDetailsOutDto orderDetailsOut = lugeOrderBusinessService.getOrderAll(orderId);
        OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
        OrderInfoExt orderInfoExt = orderDetailsOut.getOrderInfoExt();
        OrderGoods orderGoods = orderDetailsOut.getOrderGoods();
        OrderFee orderFee = orderDetailsOut.getOrderFee();
        OrderScheduler orderScheduler = orderDetailsOut.getOrderScheduler();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(orderInfo.getTenantId());
        OrderFeeExt orderFeeExt = orderDetailsOut.getOrderFeeExt();
        setCustomerInfo(sysTenantDef, orderGoods);
        orderGoods.setLinkName(orderGoods.getLocalUserName());
        orderGoods.setLinkPhone(orderGoods.getLocalPhone());

        OrderDetailsTransferDto detailsTransferOut = new OrderDetailsTransferDto();
        orderGoods.setReciveAddr(orderScheduler.getReciveAddr());
        OrderPaymentDaysInfo costPaymentDaysInfo = orderPaymentDaysInfoService.queryOrderPaymentDaysInfo(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
        if (costPaymentDaysInfo != null) {
//            SysContexts.getEntityManager().evict(costPaymentDaysInfo);
        } else {
            OrderPaymentDaysInfoH preCostPaymentDaysInfoH = orderPaymentDaysInfoHService.queryOrderPaymentDaysInfoH(orderInfo.getOrderId(), OrderConsts.PAYMENT_DAYS_TYPE.COST);
            if (preCostPaymentDaysInfoH != null) {
                costPaymentDaysInfo = new OrderPaymentDaysInfo();
//                SysContexts.getEntityManager().evict(preCostPaymentDaysInfoH);
                BeanUtil.copyProperties(preCostPaymentDaysInfoH, costPaymentDaysInfo);
            }
        }
        List<OrderTransitLineInfo> transitLineInfos = new ArrayList<OrderTransitLineInfo>();
        if (orderDetailsOut.getIsHis().intValue() == OrderConsts.TableType.HIS) {
            List<OrderTransitLineInfoH> transitLineInfosH = orderTransitLineInfoHService.queryOrderTransitLineInfoHByOrderId(orderInfo.getOrderId());
            if (transitLineInfosH != null && transitLineInfosH.size() > 0) {
                for (OrderTransitLineInfoH orderTransitLineInfoH : transitLineInfosH) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    BeanUtil.copyProperties(orderTransitLineInfoH, transitLineInfo);
                    transitLineInfos.add(transitLineInfo);
                }
            }
        } else {
            transitLineInfos = orderTransitLineInfoService.queryOrderTransitLineInfoByOrderId(orderInfo.getOrderId());
        }
        //成本转收入
        costPaymentDaysInfo.setId(null);
        costPaymentDaysInfo.setOrderId(null);
        costPaymentDaysInfo.setTenantId(null);
        costPaymentDaysInfo.setPaymentDaysType(OrderConsts.PAYMENT_DAYS_TYPE.INCOME);

        detailsTransferOut.setIncomePaymentDaysInfo(costPaymentDaysInfo);
        detailsTransferOut.setOrderGoods(orderGoods);
        detailsTransferOut.setOrderFee(orderFee);
        detailsTransferOut.setOrderScheduler(orderScheduler);
        detailsTransferOut.setOrderInfo(orderInfo);
        detailsTransferOut.setOrderInfoExt(orderInfoExt);
        detailsTransferOut.setOrderFeeExt(orderFeeExt);
        detailsTransferOut.setOrderIncomePermission(orderIncomePermission);
        detailsTransferOut.setOrderCostPermission(orderCostPermission);
        detailsTransferOut.setTransitLineInfos(transitLineInfos);
        return detailsTransferOut;
    }

    @Override
    public String saveOrder(SaveOrderVo saveOrderVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        int numInt = 0;
        Object num = saveOrderVo.getNum();
        if (num != null) {
            numInt = Integer.parseInt(num.toString());
        }
        if (numInt <= 0) {
            throw new BusinessException("需求数量错误，请重新填写！");
        }

        List<Long> retList = new ArrayList<Long>();
        for (int i = 0; i < numInt; i++) {
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setIsNeedBill(saveOrderVo.getIsNeedBill());
            orderInfo.setOrgId(saveOrderVo.getOrgId());
//            BeanUtil.copyProperties(saveOrderVo.getOrderInfo(),orderInfo);
            OrderFee orderfee = new OrderFee();
            orderfee.setCostPrice(saveOrderVo.getCostPrice());
            orderfee.setPrePayCash(saveOrderVo.getPrePayCash());
            orderfee.setPrePayEquivalenceCardType(saveOrderVo.getPrePayEquivalenceCardType());
            orderfee.setPrePayEquivalenceCardNumber(saveOrderVo.getPrePayEquivalenceCardNumber());
            orderfee.setPrePayEquivalenceCardAmount(saveOrderVo.getPrePayEquivalenceCardAmount());
            orderfee.setAfterPayCash(saveOrderVo.getAfterPayCash());
            orderfee.setAfterPayEquivalenceCardType(saveOrderVo.getAfterPayEquivalenceCardType());
            orderfee.setAfterPayEquivalenceCardAmount(saveOrderVo.getAfterPayEquivalenceCardAmount());
            orderfee.setAfterPayEquivalenceCardNumber(saveOrderVo.getAfterPayEquivalenceCardNumber());
//            BeanUtil.copyProperties(saveOrderVo.getOrderfee(),orderfee);
            OrderGoods orderGoods = new OrderGoods();
            orderGoods.setGoodsName(saveOrderVo.getGoodsName());
            orderGoods.setCustomName(saveOrderVo.getCustomName());
            orderGoods.setCompanyName(saveOrderVo.getCompanyName());
            orderGoods.setCustomUserId(saveOrderVo.getCustomUserId());
            orderGoods.setGoodsType(saveOrderVo.getGoodsType());
//            BeanUtil.copyProperties(saveOrderVo.getOrderGoods(),orderGoods);
            OrderInfoExt orderInfoExt = new OrderInfoExt();
//            BeanUtil.copyProperties(saveOrderVo.getOrderInfoExt(),orderInfoExt);
            OrderFeeExt orderFeeExt = new OrderFeeExt();
//            BeanUtil.copyProperties(saveOrderVo.getOrderFeeExt(),orderFeeExt);
            OrderScheduler orderScheduler = new OrderScheduler();
            orderScheduler.setSourceId(saveOrderVo.getSourceId());
            orderScheduler.setIsUrgent(saveOrderVo.getIsUrgent());
            orderScheduler.setDispatcherBill(saveOrderVo.getDispatcherBill());
            orderScheduler.setDispatcherId(saveOrderVo.getDispatcherId());
            orderScheduler.setDispatcherName(saveOrderVo.getDispatcherName());
//            BeanUtil.copyProperties(saveOrderVo.getOrderScheduler(),orderScheduler);
            //设置收入信息
            OrderPaymentDaysInfo incomePaymentDaysInfo = new OrderPaymentDaysInfo();
            incomePaymentDaysInfo.setBalanceType(saveOrderVo.getBalanceType());
            incomePaymentDaysInfo.setReciveTime(saveOrderVo.getReciveTime());
            incomePaymentDaysInfo.setReconciliationTime(saveOrderVo.getReconciliationTime());
            incomePaymentDaysInfo.setInvoiceTime(saveOrderVo.getInvoiceTime());
            incomePaymentDaysInfo.setPaymentDaysType(saveOrderVo.getPaymentDaysType());
            incomePaymentDaysInfo.setCollectionTime(saveOrderVo.getCollectionTime());
            incomePaymentDaysInfo.setReciveMonth(saveOrderVo.getReciveMonth());
            incomePaymentDaysInfo.setReciveDay(saveOrderVo.getReciveDay());
            incomePaymentDaysInfo.setInvoiceMonth(saveOrderVo.getInvoiceMonth());
            incomePaymentDaysInfo.setInvoiceDay(saveOrderVo.getInvoiceDay());
            incomePaymentDaysInfo.setCollectionMonth(saveOrderVo.getCollectionMonth());
            incomePaymentDaysInfo.setCollectionDay(saveOrderVo.getCollectionDay());
            incomePaymentDaysInfo.setReconciliationMonth(saveOrderVo.getReconciliationMonth());
            incomePaymentDaysInfo.setReconciliationDay(saveOrderVo.getReconciliationDay());
//            BeanUtil.copyProperties(saveOrderVo.getIncomePaymentDaysInfo(),incomePaymentDaysInfo);
            // 线路信息
            CmCustomerLine line = null;
            try {
                line = cmCustomerLineService.getById(orderScheduler.getSourceId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            orderfee.setGuidePrice(line.getGuidePrice());
            orderfee.setPriceEnum(line.getPriceEnum());
            orderfee.setPriceUnit(line.getPriceUnit() == null ? 0 : line.getPriceUnit().doubleValue());

            orderInfo.setSourceProvince(line.getSourceProvince());
            orderInfo.setSourceRegion(line.getSourceCity());
            orderInfo.setSourceCounty(line.getSourceCounty());
            orderInfo.setDesProvince(line.getDesProvince());
            orderInfo.setDesRegion(line.getDesCity());
            orderInfo.setDesCounty(line.getDesCounty());
            orderInfo.setOrderType(OrderConsts.OrderType.FIXED_LINE);
            orderInfo.setSourceFlag(SysStaticDataEnum.SOURCE_FLAG.WEIXIN);
            orderInfoExt.setRunOil((line.getOilCostPer() == null ? 0 : line.getOilCostPer().floatValue()));
            orderInfoExt.setCapacityOil((line.getEmptyOilCostPer() == null ? 0 : line.getEmptyOilCostPer().floatValue()));
            orderGoods.setNand(line.getSourceNand());
            orderGoods.setEand(line.getSourceEand());
            orderGoods.setNandDes(line.getDesNand());
            orderGoods.setEandDes(line.getDesEand());
            orderGoods.setAddrDtl(line.getSourceAddress());
            orderGoods.setDesDtl(line.getDesAddress());
            orderGoods.setNavigatDesLocation(line.getNavigatDesLocation());
            orderGoods.setNavigatSourceLocation(line.getNavigatSourceLocation());
            orderGoods.setVehicleLengh(line.getVehicleLength());
            orderGoods.setVehicleStatus(line.getVehicleStatus());
            orderGoods.setWeight(line.getGoodsWeight());
            orderGoods.setSquare(line.getGoodsVolume());
            orderGoods.setVehicleLengh(line.getVehicleLength());
            orderGoods.setVehicleStatus(line.getVehicleStatus());
            orderGoods.setLinkPhone(line.getLineTel());
            orderGoods.setLinkName(line.getLineName());
            orderGoods.setContactName(line.getLineName());
            orderGoods.setContactPhone(line.getLineTel());
            orderGoods.setLocalUser(orderScheduler.getDispatcherId());
            orderGoods.setLocalUserName(orderScheduler.getDispatcherName());
            orderGoods.setLocalPhone(orderScheduler.getDispatcherBill());

            CmCustomerInfoOutDto customerInfoOut = (CmCustomerInfoOutDto) icmCustomerInfoService.getCustomerInfo(orderGoods.getCustomUserId(), 1, accessToken);

            orderGoods.setReciveType(customerInfoOut.getOddWay() == null ? null : Integer.valueOf(customerInfoOut.getOddWay()));
            orderGoods.setReciveCityId(line.getReciveCityId() == null ? null : line.getReciveCityId());
            orderGoods.setReciveProvinceId(line.getReciveProvinceId() == null ? null : line.getReciveProvinceId());
            orderGoods.setReciveAddr(line.getReciveAddress() == null ? null : line.getReciveAddress());
            orderGoods.setAddress(customerInfoOut.getAddress() == null ? null : customerInfoOut.getAddress());

            orderGoods.setSource(orderInfo.getSourceProvinceName() + orderInfo.getSourceRegionName()
                    + orderInfo.getSourceCountyName());
            orderGoods.setDes(
                    orderInfo.getDesProvinceName() + orderInfo.getDesRegionName() + orderInfo.getDesCountyName());

            orderScheduler.setDistance((long) (line.getCmMileageNumber() * 1000));
            orderScheduler.setMileageNumber((int) (line.getMileageNumber() * 1000));
            orderScheduler.setArriveTime(line.getArriveTime() == null ? null : line.getArriveTime());
            orderScheduler.setAppointWay(OrderConsts.AppointWay.APPOINT_LOCAL);
            orderScheduler.setSourceName(line.getLineCodeName() == null ? null : line.getLineCodeName());
            DateTimeFormatter sf = DateTimeFormatter.ofPattern(DateUtil.DATETIME_FORMAT1);
            try {
                orderScheduler.setDependTime(LocalDateTime.parse(saveOrderVo.getDependTime(), sf));
            } catch (Exception e) {
                e.printStackTrace();
            }
            orderScheduler.setClientContractId(line.getContractId());
            orderScheduler.setClientContractUrl(line.getContractUrl());
            orderScheduler.setSourceCode(line.getLineCodeRule() == null ? null : line.getLineCodeRule());
            OrderPaymentDaysInfo costPaymentDaysInfo = new OrderPaymentDaysInfo();
            List<CmCustomerLineSubway> outs = null;
            if (line.getLineId() != null) {
                outs = cmCustomerLineService.getCustomerLineSubwayList(line.getLineId());
            }
            List<OrderTransitLineInfo> transitLineInfos = new ArrayList<>();
            if (outs != null && outs.size() > 0) {
                for (CmCustomerLineSubway cmCustomerLineSubwayOut : outs) {
                    OrderTransitLineInfo transitLineInfo = new OrderTransitLineInfo();
                    transitLineInfo.setAddress(cmCustomerLineSubwayOut.getDesAddress());
                    transitLineInfo.setArriveTime(cmCustomerLineSubwayOut.getArriveTime());
                    transitLineInfo.setEand(cmCustomerLineSubwayOut.getDesEnd());
                    transitLineInfo.setNand(cmCustomerLineSubwayOut.getDesNand());
                    transitLineInfo.setNavigatLocation(cmCustomerLineSubwayOut.getNavigatDesLocation());
                    transitLineInfo.setProvince(cmCustomerLineSubwayOut.getDesProvince());
                    transitLineInfo.setRegion(cmCustomerLineSubwayOut.getDesCity());
                    transitLineInfo.setCounty(cmCustomerLineSubwayOut.getDesCounty());
                    transitLineInfos.add(transitLineInfo);
                }
            }
            Long orderId = orderInfoService.saveOrUpdateOrderInfo(orderInfo, orderfee, orderGoods, orderInfoExt, orderFeeExt,
                    orderScheduler, null, costPaymentDaysInfo, incomePaymentDaysInfo, null, transitLineInfos, false, accessToken, loginInfo);
            retList.add(orderId);
        }
        String orderIds = org.apache.commons.lang.StringUtils.join(retList, ",");
        return orderIds;
    }

}
