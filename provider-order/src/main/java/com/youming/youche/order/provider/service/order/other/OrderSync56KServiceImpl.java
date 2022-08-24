package com.youming.youche.order.provider.service.order.other;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.other.IOrderSync56KService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BillPlatform;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.FeesDto;
import com.youming.youche.order.dto.OrderDetailsOutDto;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.OrderDateUtil;
import com.youming.youche.order.provider.utils.SysCfgRedisUtils;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoVerService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderSync56KServiceImpl implements IOrderSync56KService {

    @DubboReference(version = "1.0.0")
    ISysUserService  userService;

    @Resource
    IBillPlatformService billPlatformService;

//    @Resource
//    ILugeOrderBusinessService lugeOrderBusinessService;

    @Resource
    IOrderFeeService orderFeeService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;
    @Resource
    SysCfgRedisUtils sysCfgRedisUtils;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoVerService vehicleDataInfoVerService;

    @Override
    public String getTag56K(Long orderId) throws Exception {
        return null;
    }

    @Override
    public void syncOrderInfoTo56K(Long orderId, boolean isUpdate, boolean isDirectSync)  {

        OrderDetailsOutDto orderDetailsOut = null;
//        OrderDetailsOutDto orderDetailsOut = lugeOrderBusinessService.getOrderAll(orderId);
        Map<String, Object> map = new HashMap<String, Object>();
        OrderInfo orderInfo = orderDetailsOut.getOrderInfo();
        OrderScheduler orderScheduler = orderDetailsOut.getOrderScheduler();
        OrderGoods orderGoods = orderDetailsOut.getOrderGoods();
        OrderInfoExt orderInfoExt = orderDetailsOut.getOrderInfoExt();
        OrderFeeExt orderFeeExt = orderDetailsOut.getOrderFeeExt();
        OrderFee orderFee = orderDetailsOut.getOrderFee();
        boolean isHis = orderDetailsOut.getIsHis() == OrderConsts.TableType.HIS;
        FeesDto feeMap = orderFeeService.calculateSyncFee(orderFee, orderInfo, orderInfoExt, orderScheduler);

        Long payWaybill = feeMap.getCashFee() + feeMap.getEntityOilFee() + feeMap.getVirtualFee() + feeMap.getEtcFee()
                + (orderFeeExt.getAppendFreight() == null ? 0 : orderFeeExt.getAppendFreight());//增加附加运费 10-12
        String plateNumber = orderScheduler.getPlateNumber();
        Long carDriverId = orderScheduler.getCarDriverId();
        map.put("carDriverId", carDriverId);
        map.put("tenantId", orderScheduler.getTenantId());
        ;
        map.put("routeStartRegionId", sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("SYS_CITY", String.valueOf(orderInfo.getSourceRegion())));
        map.put("routeEndRegionId", sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("SYS_CITY", String.valueOf(orderInfo.getDesRegion())));
        map.put("totalWeight", orderGoods.getWeight());
        VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getVehicleDataInfo(plateNumber);
        if (vehicleDataInfo == null) {
            VehicleDataInfoVer vehicleDataInfoVer =  vehicleDataInfoVerService.getVehicleDataInfoVer(plateNumber,null);
            if (vehicleDataInfoVer != null) {
                vehicleDataInfo = new VehicleDataInfo();
//                BeanUtils.copyProperties(vehicleDataInfo, vehicleDataInfoVer);
                BeanUtil.copyProperties(vehicleDataInfoVer,vehicleDataInfo);
            }else{
                throw new BusinessException("车辆信息不存在");
            }
        }
        String defaultWeight = sysCfgRedisUtils.getSysCfgByCfgNameAndCfgSystem("MOTOR_TRACTOR_DEFAULT_WEIGHT", 0).getCfgValue();
        String sendWeight = StringUtils.isBlank(vehicleDataInfo.getVehicleLoad()) ? "0" : vehicleDataInfo.getVehicleLoad();
        if (vehicleDataInfo.getLicenceType() != null
                && vehicleDataInfo.getLicenceType().intValue() == SysStaticDataEnum.LICENCE_TYPE.TT) {
            sendWeight = defaultWeight;
        }
        map.put("sendWeight", sendWeight);
        map.put("goodsType", orderGoods.getGoodsName());
        int isDanger = 0;
        if (orderGoods.getGoodsType() !=null && orderGoods.getGoodsType().intValue() == SysStaticDataEnum.GOODS_TYPE.DANGER_GOODS) {
            isDanger = 1;//危险品为1
        }
        int waybillType = 0;
        BillPlatform bpf = billPlatformService.queryBillPlatformByUserId(Long.valueOf(orderFee.getVehicleAffiliation()));

        String identifying = sysCfgRedisUtils.getSysCfgByCfgNameAndCfgSystem("FILIALE_IDENTIFYING_56K",0).getCfgValue();
        if (bpf != null) {
            if (bpf.getPlatName().indexOf(identifying) >= 0) {
                waybillType = 1;//江西分公司
            }
        }
        map.put("waybillType", waybillType);
        map.put("isDanger", isDanger);
        map.put("orderId", orderId);
        String driverPhone =  orderScheduler.getCarDriverPhone();
        if (orderScheduler.getCarDriverId() != null && orderScheduler.getCarDriverId() > 0) {
            UserDataInfo userDataInfo = userDataInfoService.get(orderScheduler.getCarDriverId());
            if (userDataInfo != null) {
                driverPhone = userDataInfo.getMobilePhone();
            }
        }
        map.put("driverPhone", driverPhone);
        map.put("vehicleAffiliation", orderFee.getVehicleAffiliation());
        map.put("payWaybill", CommonUtil.getDoubleFormatLongMoney((payWaybill == null ? 0 : payWaybill), 2));
        if (isUpdate) {
            if (!isDirectSync) {
                if (orderScheduler.getVehicleClass() != null && orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    if (orderInfoExt.getPreAmountFlag() !=null &&
                            orderInfoExt.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                        //支付预付款的自有车不需同步
                        return;
                    }
                }
            }
            // TODO: 2022/3/25
/*            Map<String, Date> dateMap =	this.queryOrderTrackDate(orderInfo, orderScheduler, orderInfoExt, isHis);
            Date dependTime = dateMap.get("dependTime");
            Date arriveDate = dateMap.get("arriveDate");
            if (dependTime != null) {
                map.put("loadTime", dependTime);
            }
            if (arriveDate != null) {
                map.put("arriveTime", arriveDate);
            }*/
        }else{
            map.put("agentReceive", 0);
            map.put("driverReceive", (payWaybill == null ? 0 : payWaybill)/100);
            map.put("customerWaybillCode", orderScheduler.getOrderId());
            SysUser sysOperator = userService.get(orderInfo.getOpId());
            if (sysOperator == null) {
                throw new BusinessException("开单人信息不存在！");
            }
            map.put("createUser", sysOperator.getName());
            map.put("createUserPhone", sysOperator.getBillId());
            LocalDateTime estimatedLoadingTime = orderScheduler.getDependTime();
            Random random = new Random();
            Double num = CommonUtils.getDoubleFormat(random.nextDouble(), 3);
            LocalDateTime receiveTime = OrderDateUtil.subHourAndMins(estimatedLoadingTime, num.floatValue()*10);
            LocalDateTime createTime = orderInfo.getCreateTime();
            if (orderScheduler.getDependTime().getSecond() < orderScheduler.getCreateTime().getSecond()) {
                String timeCompensation = sysCfgRedisUtils.getSysCfgByCfgNameAndCfgSystem("ORDER_SYNC_56K_DEPEND_TIME_COMPENSATION", 0).getCfgValue();
                Double dependCompensation = Double.parseDouble(timeCompensation);
                estimatedLoadingTime =DateUtil.asLocalDateTime( DateUtil.addHour(DateUtil.asDate(orderScheduler.getCreateTime()), dependCompensation.intValue()));
                createTime = OrderDateUtil.subHourAndMins(receiveTime, num.floatValue());
                map.put("receiveTime", receiveTime);
            }else{
                map.put("receiveTime",  orderInfo.getCreateTime());
            }
            map.put("createTime", createTime);
            map.put("estimatedLoadingTime", estimatedLoadingTime);
        }
		/*if (StringUtils.isBlank(this.getTag56K(orderId))) {
			isUpdate = false;
		}*/
        // TODO: 2022/3/25 同步订单到开票平台
/*        if (isDirectSync) {
            I56KTF i56ktf = (I56KTF) SysContexts.getBean("i56KTF");
            if (isUpdate) {
                Map inParam = i56ktf.syncOrderInfoUpdate(JsonHelper.json(map));
                inParam.put(InterFacesCodeConsts.RESP.MAIN_ORDER_ID, orderId);
                this.syncCallBack(inParam);
            }else{
                Map inParam = i56ktf.syncOrderInfo(JsonHelper.json(map));
                inParam.put(InterFacesCodeConsts.RESP.MAIN_ORDER_ID, orderId);
                this.syncOrderInfoCallBack(inParam);
            }
        }else{
            IIntfRetryInfoTF intfRetryInfoTF = (IIntfRetryInfoTF) SysContexts.getBean("intfRetryInfoTF");
            String beanMethod = isUpdate ? "syncOrderInfoUpdate" : "syncOrderInfo";
            String postRemark = isUpdate ? "修改订单同步56K" : "新增订单同步56K";
            String callBackBeanMethod = isUpdate ? "syncCallBack" : "syncOrderInfoCallBack";
            intfRetryInfoTF.saveIntfRetryInfo("i56KTF", beanMethod, callBackBeanMethod, "orderSync56KTF", orderId.toString(), map, postRemark);
        }*/
    }

    @Override
    public void orderDelTo56K(Long orderId, boolean isDirectSync) throws Exception {

    }

    @Override
    public void syncOrderTrackTo56K(Long orderId, boolean isDirectSync) throws Exception {

    }

    @Override
    public void syncOrderTrackDateTo56K(Long orderId, boolean isDirectSync) throws Exception {

    }

    @Override
    public void syncOrderInfoCallBack(Map<String, Object> inParam) throws Exception {

    }

    @Override
    public void syncCallBack(Map<String, Object> inParam) throws Exception {

    }

    @Override
    public Map<Long, String> getTag56KList(List<Long> orderIds) throws Exception {
        return null;
    }

    @Override
    public List<Map> querySupplementOrderTrack(OrderScheduler orderScheduler, OrderGoods orderGoods, Date dependTime, Date arriveDate, Boolean isHis) throws Exception {
        return null;
    }

    @Override
    public Map<String, Date> queryOrderTrackDate(OrderInfo orderInfo, OrderScheduler orderScheduler, OrderInfoExt orderInfoExt, boolean isHis) throws Exception {
        return null;
    }
}
