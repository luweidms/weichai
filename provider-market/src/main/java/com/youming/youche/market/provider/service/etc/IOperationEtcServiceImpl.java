package com.youming.youche.market.provider.service.etc;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.api.etc.etcutil.IOperationEtcService;
import com.youming.youche.market.commons.OrderConsts;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.dto.etc.EtcOrderOutDto;
import com.youming.youche.market.provider.mapper.etc.CmEtcInfoMapper;
import com.youming.youche.market.provider.mapper.etc.EtcMaintainMapper;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsDto;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.dto.order.OrderSchedulerDto;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;


@DubboService(version = "1.0.0")
public class IOperationEtcServiceImpl extends BaseServiceImpl<CmEtcInfoMapper, CmEtcInfo> implements IOperationEtcService {

    private static final Logger log = LoggerFactory.getLogger(IOperationEtcServiceImpl.class);
    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    IOrderFeeExtService iOrderFeeExtService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtService  iOrderInfoExtService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtHService iOrderInfoExtHService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService iOrderSchedulerHService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @DubboReference(version = "1.0.0")
    IOrderFeeExtHService iOrderFeeExtHService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsService iOilRechargeAccountDetailsService;
    @Resource
    CmEtcInfoMapper cmEtcInfoMapper;
    @Resource
    EtcMaintainMapper etcMaintainMapper;
    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;
    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;
    @Resource
    IEtcMaintainService iEtcMaintainService;
    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;
    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
    IAccountDetailsService iAccountDetailsService;
    @DubboReference(version = "1.0.0")
    IPayoutIntfService iPayoutIntfService;
    @DubboReference(version = "1.0.0")
    com.youming.youche.record.api.order.IOrderSchedulerService iOrderSchedulerService1;
    @Override
    public void consumeETC(CmEtcInfo cmEtcInfo, Long tenantId, long soNbr,String accessToken)  {

    }

    @Override
    public void consumeETCHave(CmEtcInfo cmEtcInfo, Long tenantId, long soNbr,String accessToken)  {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String logRemak="????????????";
        if(cmEtcInfo.getChargingState()==2||cmEtcInfo.getChargingState()==4){
            //?????????????????????????????????
            logRemak = "??????????????????";
        }
        EtcOrderOutDto etcOrderOut= getOrderEtc(cmEtcInfo,tenantId);
        long carDriverId = etcOrderOut.getCarDriverId();
        long orderId = etcOrderOut.getOrderId();
        String vehicleAffiliation = etcOrderOut.getVehicleAffiliation();
        String oilAffiliation = etcOrderOut.getOilAffiliation();
        String etcCardNumber = etcOrderOut.getEtcCardNumber();
        String plateNumber = etcOrderOut.getPlateNumber();
        String billId = etcOrderOut.getCarPhone();
        long consumeMoney = (cmEtcInfo.getConsumeMoney() == null ? 0L : cmEtcInfo.getConsumeMoney());
        long consumeProfit = (cmEtcInfo.getConsumeProfit() == null ? 0L : cmEtcInfo.getConsumeProfit());
        cmEtcInfo.setOrderId(orderId);

        OrderFeeExt orderFeeExt  = iOrderFeeExtService.getOrderFeeExt(orderId);

        OrderInfoExt orderInfoExt= iOrderInfoExtService.getOrderInfoExt(orderId);
        int paymentWay = OrderConsts.PAYMENT_WAY.COST;
        int preAmountFlag = 0;
        if(orderInfoExt==null){
            OrderInfoExtH orderInfoExtH = iOrderInfoExtHService.getOrderInfoExtH(orderId);
            if(orderInfoExtH==null){
                throw new BusinessException("??????????????????????????????!");
            }
            preAmountFlag = orderInfoExtH.getPreAmountFlag();
            if(orderInfoExtH.getPaymentWay()==null){
                paymentWay = OrderConsts.PAYMENT_WAY.COST;
            }else{
                paymentWay = orderInfoExtH.getPaymentWay();
            }
        }else{
            preAmountFlag = orderInfoExt.getPreAmountFlag();
            if(orderInfoExt.getPaymentWay()==null){
                paymentWay = OrderConsts.PAYMENT_WAY.COST;
            }else{
                paymentWay = orderInfoExt.getPaymentWay();
            }
        }
        if(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1==cmEtcInfo.getEtcUserType()
                &&paymentWay == OrderConsts.PAYMENT_WAY.CONTRACT
                &&preAmountFlag!=1){//???????????????????????????????????????????????????????????????
            throw new BusinessException("?????????????????????????????????????????????!");
        }
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
        if(null == orderScheduler){
            OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orderId);
            if(orderSchedulerH==null){
                throw new BusinessException("??????????????????????????????!");
            }
            orderScheduler = new OrderScheduler();
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
        }
        if(etcOrderOut.getToTenantId()>0 || paymentWay==OrderConsts.PAYMENT_WAY.CONTRACT){
            if(etcOrderOut.getToTenantId()>0){

                Long tenantUserId = sysTenantDefService.getTenantAdminUser(etcOrderOut.getToTenantId());
                if(tenantUserId==null){
                    throw new BusinessException("?????????????????????????????????!");
                }

                UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
                if(tenantUser==null){
                    throw new BusinessException("?????????????????????????????????!");
                }
                //???????????????????????????????????????ETC????????????????????????????????????????????????????????????A??????B????????????B??????
                consumeETCHandle(cmEtcInfo, tenantId, tenantUserId, orderId, vehicleAffiliation, etcCardNumber, plateNumber,
                        tenantUser.getMobilePhone(),soNbr,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER,accessToken);
            }else{
                consumeETCHandle(cmEtcInfo, tenantId, carDriverId, orderId, vehicleAffiliation, etcCardNumber,
                        plateNumber, billId,soNbr,oilAffiliation,SysStaticDataEnum.USER_TYPE.DRIVER_USER,accessToken);
            }
            cmEtcInfo.setCollectUserId(orderScheduler.getCarDriverId());
            cmEtcInfo.setCollectName(orderScheduler.getCarDriverMan());
            cmEtcInfo.setCollectMobile(orderScheduler.getCarDriverPhone());
            cmEtcInfo.setChargingState(1);
        }else if((paymentWay==OrderConsts.PAYMENT_WAY.COST ||paymentWay==OrderConsts.PAYMENT_WAY.EXPENSE)
                && orderScheduler.getVehicleClass().intValue()== SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1){
            //???????????????????????????????????????ETC?????????????????????????????????????????????????????????????????????
            //??????????????????????????????
            if(orderFeeExt != null){
                consumeMoney += (orderFeeExt.getPontageActual() == null ? 0L : orderFeeExt.getPontageActual());
                consumeProfit += (orderFeeExt.getPontageIncome() == null ? 0L : orderFeeExt.getPontageIncome());
                orderFeeExt.setPontageActual(consumeMoney);
                orderFeeExt.setPontageIncome(consumeProfit);
                iOrderFeeExtService.saveOrUpdate(orderFeeExt);
            }else{
                OrderFeeExtH  orderFeeExtH = iOrderFeeExtHService.getOrderFeeExtH(orderId);
                if(orderFeeExtH==null){
                    throw new BusinessException("??????????????????????????????!");
                }
                consumeMoney += (orderFeeExtH.getPontageActual() == null ? 0L : orderFeeExtH.getPontageActual());
                consumeProfit += (orderFeeExtH.getPontageIncome() == null ? 0L : orderFeeExtH.getPontageIncome());
                orderFeeExtH.setPontageActual(consumeMoney);
                orderFeeExtH.setPontageIncome(consumeProfit);
                iOrderFeeExtHService.saveOrUpdate(orderFeeExtH);
            }
            cmEtcInfo.setCollectUserId(0L);
            cmEtcInfo.setCollectName("--");
            cmEtcInfo.setCollectMobile("--");
            cmEtcInfo.setChargingState(3);
        }
        cmEtcInfo.setPaymentWay(paymentWay);
        // ETC??????????????????
        iSysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                cmEtcInfo.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, logRemak);
    }


    /***
     * ??????????????????ETC??????
     */
    @Override
    public void consumeETCInvestment(CmEtcInfo cmEtcInfo, Long tenantId, long soNbr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        //ETC???????????????
        EtcOrderOutDto etcOrderOut= getOrderEtcInvestment(cmEtcInfo,tenantId);
        String plateNumber = cmEtcInfo.getPlateNumber();//?????????
        LocalDateTime etcConsumeTime = cmEtcInfo.getEtcConsumeTime();//????????????
        String month = DateUtil.formatDate(DateUtil.asDate(etcConsumeTime), DateUtil.YEAR_MONTH_FORMAT);
        if(etcOrderOut!=null){
            Long orderId=etcOrderOut.getOrderId();
            long carDriverId = etcOrderOut.getCarDriverId();

            List<OilRechargeAccountDetailsDto> dtos = iOilRechargeAccountDetailsService.queryOrderPayee(orderId.toString(), null);
            cmEtcInfo.setOrderId(orderId);
            if(dtos==null){
                throw new BusinessException("?????????ETC??????????????????????????????");
            }else{
                cmEtcInfo.setCollectUserId(dtos.get(0).getPayeeUserid());
                if (dtos.get(0).getVehicleClass()!=null && StringUtils.isNotEmpty(dtos.get(0).getVehicleClass())){
                    cmEtcInfo.setEtcUserType(Integer.valueOf(dtos.get(0).getVehicleClass()));
                }

            }
            cmEtcInfo.setUserId(carDriverId);
        }else{//??????????????????
             // TODO ?????????
//            IVehicleSV vehicleSV = (IVehicleSV)SysContexts.getBean("vehicleSV");
//            Map<String, Object> billReceiver = vehicleSV.getMonthLastBillReceiver(plateNumber, tenantId, month, null);
//            if(billReceiver==null){
//                throw new BusinessException("?????????ETC??????????????????????????????");
//            }
//            Long billReceiverUserId = Long.valueOf(billReceiver.get("billReceiverUserId")+"");
//            cmEtcInfo.setCollectUserId(billReceiverUserId);
//            cmEtcInfo.setEtcUserType(Integer.valueOf(billReceiver.get("vehicleClass")+""));
        }
        String logRemak="????????????";
        iSysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                cmEtcInfo.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, logRemak);
    }

    /**
     * ETC???????????????
     * @param cmEtcInfo
     * @throws Exception
     */
    private EtcOrderOutDto getOrderEtc(CmEtcInfo cmEtcInfo, Long tenantId) {
        String etcCardNumber = cmEtcInfo.getEtcCardNo();
        long amountFee = cmEtcInfo.getConsumeMoney();
        long objId = cmEtcInfo.getId();
        log.info("ETC????????????:etcCardNumber=" + etcCardNumber + "amountFee=" + amountFee + "objId=" + objId);
        if (etcCardNumber == null || "".equals(etcCardNumber)) {
            throw new BusinessException("?????????ETC??????!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("ETC??????????????????????????????0!");
        }
        if (cmEtcInfo.getState() != 1) {
            throw new BusinessException("ETC???????????????");
        }
        LambdaQueryWrapper<com.youming.youche.market.domain.etc.EtcMaintain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.youming.youche.market.domain.etc.EtcMaintain::getTenantId,tenantId)
                .eq(com.youming.youche.market.domain.etc.EtcMaintain::getEtcId,etcCardNumber);
        com.youming.youche.market.domain.etc.EtcMaintain em = etcMaintainMapper.selectOne(wrapper);
        if(em==null){
            throw new BusinessException("ETC????????????????????????");
        }
        // ?????????????????????????????????ETC????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // A-B?????????B??????????????????Etc??????????????????????????????????????????????????????????????????????????????ETC???
        // B-D??????????????????????????????????????????????????????????????????????????????ETC
        Long vehicleCode = em.getVehicleCode();
        if(vehicleCode==null||vehicleCode<=0){
            throw new BusinessException("ETC???????????????????????????!");
        }
        List<OrderSchedulerDto> list = iOrderSchedulerService1.queryOrderInfoByCar(null, tenantId, -1, -1,cmEtcInfo.getOrderId(),em.getBindVehicle());
        if (list == null || list.size() <= 0) {
            throw new BusinessException("??????????????????????????????");
        }
        Date etcConsumeTime = DateUtil.asDate(cmEtcInfo.getEtcConsumeTime());// etc????????????
        if(etcConsumeTime==null){
            throw new BusinessException("etc??????????????????!");
        }
        long carDriverId = 0l;
        long orderId = 0l;
        long toTenantId = 0l;
        String vehicleAffiliation = null;
        Map<String,Object> objTmp = null;
//        Collections.sort(list, new Comparator<Map<String, Object>>() {
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                long map1value = ((Date)o1.get("carDependDate")).getTime();
//                long map2value = ((Date)o2.get("carDependDate")).getTime();
////                    return map1value - map2value;
//                return String.valueOf(map1value).compareTo(String.valueOf(map2value));
//            }
//        });
        OrderSchedulerDto  tmp = null;
        for (int i=0;i<list.size();i++) {
//            Map<String,Object> obj = list.get(i);
            OrderSchedulerDto dto = list.get(i);
//            if((dto.getCarDependDate() != null && ((((Date)obj.get("carDependDate")).getTime()) <= etcConsumeTime.getTime()))
//                    && (obj.get("carArriveDate") != null && ((((Date)obj.get("carArriveDate")).getTime()) >= etcConsumeTime.getTime()))){
//                //??????????????????<=ETC????????????<=????????????????????????
//                if (obj.get("carDriverId") != null) {
//                    carDriverId = ((Number) obj.get("carDriverId")).longValue();
//                }
//                if (obj.get("orderId") != null) {
//                    orderId = ((Number) obj.get("orderId")).longValue();
//                }
//                if(obj.get("toTenantId")!=null){
//                    toTenantId =  ((Number) obj.get("toTenantId")).longValue();
//                }
//                vehicleAffiliation = String.valueOf(obj.get("vehicleAffiliation"));
//                log.info("??????id???" + orderId);

                if (dto.getCarDependDate()!=null && (dto.getCarDependDate().getTime()<= etcConsumeTime.getTime())
                    && dto.getCarArriveDate()!=null && dto.getCarArriveDate().getTime()>= etcConsumeTime.getTime()){
                    //??????????????????<=ETC????????????<=????????????????????????
                    if (dto.getCarDriverId()!=null){
                        carDriverId = dto.getCarDriverId();
                    }
                    if (dto.getOrderId() != null) {
                        orderId = dto.getOrderId();
                    }
                    if(dto.getToTenantId()!=null){
                        toTenantId =  dto.getToTenantId();
                    }
                    vehicleAffiliation = dto.getVehicleAffiliation();
                    log.info("??????id???" + orderId);
                    break;

            }else{
                //??????????????????<=ETC????????????<=????????????????????????
                if(dto.getCarDependDate()!=null &&  (etcConsumeTime.getTime()<dto.getCarDependDate().getTime())){
                    if (objTmp == null) {
                        // ????????????????????????????????????ETC????????????<????????????????????????
                        if (dto.getCarDriverId() != null) {
                            carDriverId = dto.getCarDriverId();
                        }
                        if (dto.getOrderId() != null) {
                            orderId = dto.getOrderId();
                        }
                        if(dto.getToTenantId()!=null){
                            toTenantId =  dto.getToTenantId();
                        }
                        vehicleAffiliation =dto.getVehicleAffiliation();
                        log.info("??????id???" + orderId);
                        break;


                    }else if (objTmp != null && (objTmp.get("carArriveDate") != null
                            && (etcConsumeTime.getTime() > ((Date) objTmp.get("carArriveDate")).getTime()))) {
                        // ETC????????????<???????????????????????? ??? ????????????ETC????????????>??????????????????
                        if (objTmp.get("carDriverId") != null) {
                            carDriverId = ((Number) objTmp.get("carDriverId")).longValue();
                        }
                        if (objTmp.get("orderId") != null) {
                            orderId = ((Number) objTmp.get("orderId")).longValue();
                        }
                        if(dto.getToTenantId()!=null){
                            toTenantId = dto.getToTenantId();
                        }
                        vehicleAffiliation = String.valueOf(objTmp.get("vehicleAffiliation"));
                        log.info("??????id???" + orderId);
                        break;
                    }
                }
            }
            tmp = dto;
            if(i==(list.size()-1)){
                if(dto!=null && (dto.getCarArriveDate()!=null && (etcConsumeTime.getTime()>dto.getCarArriveDate().getTime()))){
                    //????????????????????????????????????????????????????????????????????????
                    if (tmp.getCarDriverId() != null) {
                        carDriverId = tmp.getCarDriverId();
                    }
                    if (objTmp.get("orderId") != null) {
                        orderId = ((Number) objTmp.get("orderId")).longValue();
                    }
                    vehicleAffiliation = String.valueOf(objTmp.get("vehicleAffiliation"));
                    log.info("??????id???" + orderId);
                    break;
                }
            }
        }
        if (orderId == 0l) {
            throw new BusinessException("??????ETC??????????????????????????????");
        }
        OrderLimit ol = iOrderLimitService.getOrderLimitByUserIdAndOrderId(carDriverId,orderId,-1);
        if (ol == null) {
            throw new BusinessException("??????????????????" + orderId + "??????ID???" + carDriverId + "????????????????????????");
        }
        EtcOrderOutDto etcOrderOut =  new EtcOrderOutDto();
        etcOrderOut.setCarDriverId(carDriverId);
        etcOrderOut.setOrderId(orderId);
        etcOrderOut.setVehicleAffiliation(vehicleAffiliation);
        etcOrderOut.setEtcCardNumber(etcCardNumber);
        etcOrderOut.setPlateNumber(em.getBindVehicle());
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(carDriverId, null, -1L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        etcOrderOut.setCarPhone(sysOperator.getBillId());
        etcOrderOut.setOilAffiliation(ol.getOilAffiliation());
        etcOrderOut.setToTenantId(toTenantId);
        return etcOrderOut;
    }
    //??????????????????????????????
    EtcOrderOutDto getOrderEtcInvestment(CmEtcInfo cmEtcInfo, Long tenantId) {
        String etcCardNumber = cmEtcInfo.getEtcCardNo();
        long amountFee = cmEtcInfo.getConsumeMoney();
        long objId = cmEtcInfo.getId();
        log.info("ETC????????????:etcCardNumber=" + etcCardNumber + "amountFee=" + amountFee + "objId=" + objId);
        if (etcCardNumber == null || "".equals(etcCardNumber)) {
            throw new BusinessException("?????????ETC??????!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("ETC??????????????????????????????0!");
        }
        if (cmEtcInfo.getState() != 1) {
            throw new BusinessException("ETC???????????????");
        }
        com.youming.youche.market.domain.etc.EtcMaintain em = iEtcMaintainService.etcmaintain(etcCardNumber);
        if(em==null){
            throw new BusinessException("ETC????????????????????????");
        }

        // ?????????????????????????????????ETC????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // A-B?????????B??????????????????Etc??????????????????????????????????????????????????????????????????????????????ETC???
        // B-D??????????????????????????????????????????????????????????????????????????????ETC
        Long vehicleCode = em.getVehicleCode();
        if(vehicleCode==null||vehicleCode<=0){
            throw new BusinessException("ETC???????????????????????????!");
        }
        List<OrderSchedulerDto> list = iOrderSchedulerService1.queryOrderInfoByCar(null, tenantId, -1, -1,cmEtcInfo.getOrderId(),em.getBindVehicle());
        if (list == null || list.size() <= 0) {
            return null;
        }
        Date etcConsumeTime = DateUtil.asDate(cmEtcInfo.getEtcConsumeTime());// etc????????????
        if(etcConsumeTime==null){
            throw new BusinessException("etc??????????????????!");
        }
        long carDriverId = 0l;
        long orderId = 0l;
        long toTenantId = 0l;
        String vehicleAffiliation = null;
        Map<String,Object> objTmp = null;
        // TODO ????????????????????????????????????
//        Collections.sort(list, new Comparator<Map<String, Object>>() {
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                long map1value = ((Date)o1.get("carDependDate")).getTime();
//                long map2value = ((Date)o2.get("carDependDate")).getTime();
////                    return map1value - map2value;
//                return String.valueOf(map1value).compareTo(String.valueOf(map2value));
//            }
//        });
        OrderSchedulerDto tpm = null;
        for (int i=0;i<list.size();i++) {
            OrderSchedulerDto dto = list.get(i);
            if((dto.getCarDependDate() != null && ((dto.getCarDependDate().getTime()) <= etcConsumeTime.getTime()))
                    && (dto.getCarArriveDate() != null && (dto.getCarArriveDate().getTime()) >= etcConsumeTime.getTime())){
                //??????????????????<=ETC????????????<=????????????????????????
                if (dto.getCarDriverId() != null) {
                    carDriverId = dto.getCarDriverId();
                }
                if (dto.getOrderId() != null) {
                    orderId = dto.getOrderId();
                }
                if(dto.getToTenantId()!=null){
                    toTenantId =  dto.getToTenantId();
                }
                vehicleAffiliation = dto.getVehicleAffiliation();
                log.info("??????id???" + orderId);
                break;
            }else{
                //??????????????????<=ETC????????????<=????????????????????????
                if(dto.getCarDependDate()!=null && (etcConsumeTime.getTime()<dto.getCarDependDate().getTime())){
                    if (objTmp == null) {
                        // ????????????????????????????????????ETC????????????<????????????????????????
                        if (dto.getCarDriverId() != null) {
                            carDriverId =dto.getCarDriverId();
                        }
                        if (dto.getOrderId() != null) {
                            orderId = dto.getOrderId();
                        }
                        if(dto.getToTenantId()!=null){
                            toTenantId =  dto.getToTenantId();
                        }
                        vehicleAffiliation = dto.getVehicleAffiliation();
                        log.info("??????id???" + orderId);
                        break;
                    }else if (objTmp != null && (objTmp.get("carArriveDate") != null
                            && (etcConsumeTime.getTime() > ((Date) objTmp.get("carArriveDate")).getTime()))) {
                        // ETC????????????<???????????????????????? ??? ????????????ETC????????????>??????????????????
                        if (objTmp.get("carDriverId") != null) {
                            carDriverId = ((Number) objTmp.get("carDriverId")).longValue();
                        }
                        if (objTmp.get("orderId") != null) {
                            orderId = ((Number) objTmp.get("orderId")).longValue();
                        }
                        if(dto.getToTenantId()!=null){
                            toTenantId =  dto.getToTenantId();
                        }
                        vehicleAffiliation = String.valueOf(objTmp.get("vehicleAffiliation"));
                        log.info("??????id???" + orderId);
                        break;
                    }
                }
            }
            tpm = dto;
            if(i==(list.size()-1)){
                if(dto!=null && (dto.getCarArriveDate()!=null && (etcConsumeTime.getTime()>(dto.getCarArriveDate().getTime())))){
                    //????????????????????????????????????????????????????????????????????????
                    if (tpm.getCarDriverId() != null) {
                        carDriverId = tpm.getCarDriverId() ;
                    }
                    if (tpm.getOrderId() != null) {
                        orderId = tpm.getOrderId();
                    }
                    vehicleAffiliation = tpm.getVehicleAffiliation();
                    log.info("??????id???" + orderId);
                    break;
                }
            }
        }
        if (orderId == 0l) {
            return null;
        }
        EtcOrderOutDto etcOrderOut =  new EtcOrderOutDto();
        etcOrderOut.setCarDriverId(carDriverId);
        etcOrderOut.setOrderId(orderId);
        etcOrderOut.setVehicleAffiliation(vehicleAffiliation);
        etcOrderOut.setEtcCardNumber(etcCardNumber);
        etcOrderOut.setPlateNumber(em.getBindVehicle());
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(carDriverId, null, -1L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        etcOrderOut.setCarPhone(sysOperator.getBillId());
        etcOrderOut.setToTenantId(toTenantId);
        return etcOrderOut;
    }
    /**
     * ETC???????????????????????????????????????????????????
     * @param cmEtcInfo
     * @param tenantId
     * @param carDriverId
     * @param orderId
     * @param vehicleAffiliation
     * @param etcCardNumber
     * @param plateNumber
     * @param billId
     * @param oilAffiliation
     * @throws Exception
     */
    private void consumeETCHandle(CmEtcInfo cmEtcInfo,Long tenantId,long carDriverId,long orderId,
                                  String vehicleAffiliation,String etcCardNumber,String plateNumber,
                                  String billId,long soNbr,String oilAffiliation,int userType , String accessToken ) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long amountFee = cmEtcInfo.getConsumeMoney();
        // ??????userid??????????????????
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(carDriverId, null, -1L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        // ????????????ID???????????????????????????????????????
        OrderAccount account = iOrderAccountService.queryOrderAccount(sysOperator.getUserInfoId(),
                vehicleAffiliation,0L, tenantId,oilAffiliation,userType);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        long balance = account.getBalance();
        long etcAmount = account.getEtcBalance();
        long marginBalance = account.getMarginBalance();
        log.info("??????????????????" + balance);
        log.info("????????????ETC??????" + etcAmount);
        log.info("???????????????????????????" + marginBalance);
        cmEtcInfo.setEtcAmount(etcAmount);
        cmEtcInfo.setMarginBalance(marginBalance);
        cmEtcInfo.setUserId(sysOperator.getUserInfoId());
        cmEtcInfo.setOrderId(orderId);
        // ETC???????????? (??????????????????etc?????????????????????????????????,???????????????)
        OrderLimit ol = iOrderLimitService.queryOrderLimitByOrderId(orderId, carDriverId);
        if (ol == null) {
            throw new BusinessException("???????????????????????????");
        }
        //??????????????????
        this.consumeETCHandleFee(cmEtcInfo,ol,busiList,tenantId);
        if(busiList.size()<=0){
            throw new BusinessException("?????????????????????");
        }
        // ??????????????????

        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CONSUME_ETC_CODE, busiList);
        // ???????????????????????????????????????
        Map<String, String> inParam = iOrderAccountService.setParameters(carDriverId, billId,EnumConsts.PayInter.CONSUME_ETC_CODE, orderId, amountFee,vehicleAffiliation, "");
        inParam.put("etcId", String.valueOf(cmEtcInfo.getId()));
        inParam.put("tenantId",String.valueOf(ol.getTenantId()));
        // TODO ?????????
//        busiToOrder.busiToOrder(inParam, busiSubjectsRelList);
        // ????????????????????????????????????????????????

        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,
                EnumConsts.PayInter.CONSUME_ETC_CODE, sysOperator.getUserInfoId(),
                sysOperator.getOpName(), account, busiSubjectsRelList, soNbr, orderId,
                sysOperator.getOpName(), cmEtcInfo.getEtcConsumeTime(), tenantId, etcCardNumber, plateNumber, null,
                vehicleAffiliation,loginInfo);
        //??????????????????
        long etcArrears= 0L;//??????
        long etcAvailable= 0L;//????????????
        List<BusiSubjectsRel> busiCoList = new ArrayList<BusiSubjectsRel>();
        for(BusiSubjectsRel busiSubjectsRel : busiList){
            if(busiSubjectsRel.getSubjectsId()==EnumConsts.SubjectIds.ETC_ARREARS_FEE && busiSubjectsRel.getAmountFee()>0){
                BusiSubjectsRel arrearsSubjectsRel = new BusiSubjectsRel();
                arrearsSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ETC_ARREARS_CO_FEE);
                arrearsSubjectsRel.setAmountFee(busiSubjectsRel.getAmountFee());
                busiCoList.add(arrearsSubjectsRel);
                etcArrears = busiSubjectsRel.getAmountFee();
            }
            if(busiSubjectsRel.getSubjectsId()==EnumConsts.SubjectIds.ETC_AVAILABLE_FEE && busiSubjectsRel.getAmountFee()>0){
                etcAvailable = busiSubjectsRel.getAmountFee();
            }
        }
        if(busiCoList.size()>0){
            Long toTenantUserId = sysTenantDefService.getTenantAdminUser(ol.getTenantId());//?????????????????????userId
            SysUser tenantUser = iSysUserService.getSysOperatorByUserIdOrPhone(toTenantUserId, null, -1L);
            if (tenantUser == null) {
                throw new BusinessException("?????????????????????????????????!");
            }
            List<BusiSubjectsRel> busiSubjectsRelCOList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CONSUME_ETC_CODE, busiCoList);
            Map<String, String> inParamCo = iOrderAccountService.setParameters(tenantUser.getUserInfoId(), tenantUser.getBillId(),
                    EnumConsts.PayInter.CONSUME_ETC_CODE, orderId, etcArrears,vehicleAffiliation, "");
            inParamCo.put("etcId", String.valueOf(cmEtcInfo.getId()));
            inParamCo.put("tenantId",String.valueOf(ol.getTenantId()));
            //  TODO ?????????
//            busiToOrder.busiToOrder(inParamCo, busiSubjectsRelCOList);
            OrderAccount accountCO = iOrderAccountService.queryOrderAccount(tenantUser.getUserInfoId(), vehicleAffiliation,
                    0L, tenantId,oilAffiliation,SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            iAccountDetailsService.insetAccDet(EnumConsts.BusiType.CONSUME_CODE,
                    EnumConsts.PayInter.CONSUME_ETC_CODE, tenantUser.getUserInfoId(),
                    tenantUser.getOpName(), accountCO, busiSubjectsRelCOList, soNbr, orderId,
                    tenantUser.getOpName(), cmEtcInfo.getEtcConsumeTime(), tenantId, etcCardNumber, plateNumber, null,
                    vehicleAffiliation,loginInfo);
            //???????????????payoutinfo ??????????????????
            PayoutIntf payoutIntfArrears = new PayoutIntf();
            payoutIntfArrears.setUserId(tenantUser.getUserInfoId());
            //????????????????????????
            payoutIntfArrears.setUserType(com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.ADMIN_USER);
            payoutIntfArrears.setPayUserType(com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            //????????????????????????
            payoutIntfArrears.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
            payoutIntfArrears.setTxnAmt(etcArrears);
            payoutIntfArrears.setTenantId(tenantId);
            payoutIntfArrears.setPlateNumber(plateNumber);
            payoutIntfArrears.setVehicleAffiliation(vehicleAffiliation);
            payoutIntfArrears.setOrderId(orderId);
            payoutIntfArrears.setPayTenantId(0L);
            payoutIntfArrears.setIsAutomatic(1);
            payoutIntfArrears.setIsTurnAutomatic(0);
            payoutIntfArrears.setPayObjId(sysOperator.getUserInfoId());
            payoutIntfArrears.setObjId(Long.parseLong(tenantUser.getBillId()));
            payoutIntfArrears.setPayType(OrderAccountConst.PAY_TYPE.USER);
            payoutIntfArrears.setBusiId(EnumConsts.PayInter.CONSUME_ETC_CODE);
            payoutIntfArrears.setSubjectsId(EnumConsts.SubjectIds.ETC_ARREARS_FEE);
            payoutIntfArrears.setOilAffiliation(account.getOilAffiliation());
            iPayoutIntfService.doSavePayOutIntfVirToVir(payoutIntfArrears,accessToken);
        }
    }

    private void consumeETCHandleFee(CmEtcInfo cmEtcInfo,OrderLimit ol,List<BusiSubjectsRel> busiList,Long tenantId) {
        //ETC????????????
        long amountFee = cmEtcInfo.getConsumeMoney();
        //?????????????????????
        long consumeAfterMoney = cmEtcInfo.getConsumeAfterMoney();
        // ????????????ETC
        long noPayEtc = (ol.getNoPayEtc() == null ? 0L : ol.getNoPayEtc());
        // ???????????????????????????
        long noPayCash = (ol.getNoPayCash() == null ? 0L : ol.getNoPayCash());
        // ??????????????????
        long noPayFinal = (ol.getNoPayFinal() == null ? 0L : ol.getNoPayFinal());
        // ETC??????????????????
        double rate = (double) (amountFee - consumeAfterMoney) / (double) amountFee;
        //??????????????????
        long rateFee = amountFee - consumeAfterMoney;
        if (rateFee < 0) {
            rateFee = 0;
        }
        long alreadyETC = 0L;
        long alreadyCash = 0L;
        long alreadyFinal = 0L;
        long alreadyServiceCharge = 0L;
        long arrearsFee = 0L;
        long availablcFee = 0L;

        if (noPayEtc >= amountFee) {// ????????????ETC??????ETC??????
            //ETC??????
            BusiSubjectsRel noPayEtcSubjectsRel = new BusiSubjectsRel();
            noPayEtcSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE);
            noPayEtcSubjectsRel.setAmountFee(amountFee);
            noPayEtcSubjectsRel.setIncome(rateFee);
            busiList.add(noPayEtcSubjectsRel);
            alreadyETC = amountFee;
        } else {//????????????ETC??????ETC??????
            //ETC??????
            BusiSubjectsRel noPayEtcSubjectsRel = new BusiSubjectsRel();
            noPayEtcSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ORDER_ETC_CONSUME_FEE);
            noPayEtcSubjectsRel.setAmountFee(noPayEtc);
            long noPayEtcIncome = Math.round(noPayEtc * rate);
            noPayEtcSubjectsRel.setIncome(noPayEtcIncome);
            busiList.add(noPayEtcSubjectsRel);
            alreadyETC = noPayEtc;
            // ??????(ETC??????????????????)
            BusiSubjectsRel arrearsSubjectsRel = new BusiSubjectsRel();
            arrearsSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.ETC_ARREARS_FEE);
            arrearsSubjectsRel.setAmountFee(amountFee - noPayEtc);
            long arrearsIncome = rateFee - noPayEtcIncome;//????????????
            arrearsSubjectsRel.setIncome(arrearsIncome);
            busiList.add(arrearsSubjectsRel);
            arrearsFee = amountFee - noPayEtc;
        }
        cmEtcInfo.setEtcAmountDeduct(alreadyETC);
        cmEtcInfo.setWithdrawalAmountDeduct(availablcFee);
        cmEtcInfo.setMarginBalanceDeduct(alreadyFinal);
        cmEtcInfo.setAdvanceFee(alreadyServiceCharge);
        cmEtcInfo.setArrearsFee(arrearsFee);
    }
}
