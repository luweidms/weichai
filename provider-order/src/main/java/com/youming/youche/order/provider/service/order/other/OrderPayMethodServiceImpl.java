package com.youming.youche.order.provider.service.order.other;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.other.IOrderPayMethodService;
import com.youming.youche.order.api.order.other.IUpdateOrderService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderFeeExt;
import com.youming.youche.order.domain.order.OrderFeeExtVer;
import com.youming.youche.order.domain.order.OrderFeeVer;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerVer;
import com.youming.youche.order.dto.CancelTheOrderInDto;
import com.youming.youche.order.dto.UpdateTheOrderInDto;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@DubboService(version = "1.0.0")
@Service
public class OrderPayMethodServiceImpl implements IOrderPayMethodService {

    @Resource
    IUpdateOrderService updateOrderService;

    @Override
    public void cancelTheOrderTransit(OrderFee orderFee, OrderScheduler scheduler, OrderInfo orderInfo,LoginInfo loginInfo,String token){

        String vehicleAffiliation = orderFee.getVehicleAffiliation();
        if (scheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            vehicleAffiliation = "0";
        }
        CancelTheOrderInDto cancelTheOrderIn = new CancelTheOrderInDto();
        cancelTheOrderIn.setAmountFee( orderFee.getPreCashFee() == null ? 0:orderFee.getPreCashFee());
        cancelTheOrderIn.setArriveFee(orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee());
        cancelTheOrderIn.setEtcFee(orderFee.getPreEtcFee() == null ? 0 : orderFee.getPreEtcFee());
        cancelTheOrderIn.setIsNeedBill(orderInfo.getIsNeedBill());
        cancelTheOrderIn.setIsPayArriveFee(orderFee.getArrivePaymentState());
        cancelTheOrderIn.setOrderId(orderInfo.getOrderId());
        cancelTheOrderIn.setTenantId(orderFee.getTenantId());
        if (scheduler.getIsCollection() != null
                && scheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES
        ) {
            cancelTheOrderIn.setUserId(scheduler.getCollectionUserId());
            cancelTheOrderIn.setEntityOilFee(0L);
            cancelTheOrderIn.setVirtualOilFee(0L);
            //移除司机油费
            CancelTheOrderInDto cancelTheOrderInDriver = new CancelTheOrderInDto();
            cancelTheOrderInDriver.setAmountFee(0L);
            cancelTheOrderInDriver.setArriveFee(0L);
            cancelTheOrderInDriver.setEtcFee(0L);
            cancelTheOrderInDriver.setIsNeedBill(orderInfo.getIsNeedBill());
            cancelTheOrderInDriver.setIsPayArriveFee(orderFee.getArrivePaymentState());
            cancelTheOrderInDriver.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            cancelTheOrderInDriver.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
            cancelTheOrderInDriver.setOrderId(orderInfo.getOrderId());
            cancelTheOrderInDriver.setTenantId(orderFee.getTenantId());
            cancelTheOrderInDriver.setUserId(scheduler.getCarDriverId());
            cancelTheOrderInDriver.setVehicleAffiliation(vehicleAffiliation);
            updateOrderService.cancelTheOrder(cancelTheOrderInDriver,loginInfo,token);
        }else{
            cancelTheOrderIn.setUserId(scheduler.getCarDriverId());
            cancelTheOrderIn.setEntityOilFee(orderFee.getPreOilFee() == null ? 0 : orderFee.getPreOilFee());
            cancelTheOrderIn.setVirtualOilFee(orderFee.getPreOilVirtualFee() == null ? 0 : orderFee.getPreOilVirtualFee());
        }
        cancelTheOrderIn.setVehicleAffiliation(vehicleAffiliation);
        updateOrderService.cancelTheOrder(cancelTheOrderIn,loginInfo,token);

    }

    @Override
    public void updateOrderTransit(Long userId, OrderFee orderFee, OrderFeeVer orderFeeVer, OrderInfo orderInfo,
                                   OrderInfoExt orderInfoExt, OrderScheduler orderScheduler,
                                   OrderSchedulerVer orderSchedulerVer, OrderFeeExt orderFeeExt,
                                   OrderFeeExtVer orderFeeExtVer, boolean isUpdateDriver, LoginInfo loginInfo,String token) {
        Long originalAmountFee = orderFee.getPreCashFee();
        Long updateAmountFee = orderFeeVer.getPreCashFee();
        Long originalEntityOilFee = orderFee.getPreOilFee();
        Long updateEntityOilFee = orderFeeVer.getPreOilFee();
        Long originalVirtualOilFee = orderFee.getPreOilVirtualFee();
        Long updatelongVirtualOilFee = orderFeeVer.getPreOilVirtualFee();
        Long originalEtcFee = orderFee.getPreEtcFee();
        Long updateEtcFee = orderFeeVer.getPreEtcFee();
        Long originalArriveFee = orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee();
        Long updateArriveFee = orderFeeVer.getArrivePaymentFee() == null ? 0 : orderFeeVer.getArrivePaymentFee();
        if (isUpdateDriver) {
            if (orderInfo.getToTenantId() != null && orderInfo.getToTenantId() > 0) {//有归属租户
            }else if(orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES){
                //代收
                UpdateTheOrderInDto in = new UpdateTheOrderInDto();
                in.setUserId(orderScheduler.getCarDriverId());
                in.setVehicleAffiliation(orderFee.getVehicleAffiliation());
                if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    in.setVehicleAffiliation("0");
                }
                in.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
                in.setOriginalAmountFee(0L);
                in.setUpdateAmountFee(0L);
                in.setOriginalEntityOilFee(orderFee.getPreOilFee());
                in.setUpdateEntityOilFee(0L);
                in.setOriginalVirtualOilFee(orderFee.getPreOilVirtualFee());
                in.setUpdatelongVirtualOilFee(0L);
                in.setOriginalEtcFee(0L);
                in.setUpdateEtcFee(0L);
                in.setIsPayArriveFee(orderFee.getArrivePaymentState() == null ? 0: orderFee.getArrivePaymentState());
                in.setOriginalArriveFee(0L);
                in.setUpdateArriveFee(0L);
                in.setOrderId(orderInfo.getOrderId());
                in.setTenantId(orderInfo.getTenantId());
                in.setIsNeedBill(orderInfo.getIsNeedBill());
                in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
                in.setUpdateOilConsumer(orderFeeExt.getOilConsumer());
                in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                updateOrderService.updateTheOrder(in,loginInfo,token);

                originalEntityOilFee = 0L;
                originalVirtualOilFee = 0L;
            }
            else{
                //司机
                UpdateTheOrderInDto in = new UpdateTheOrderInDto();
                in.setUserId(orderScheduler.getCarDriverId());
                in.setVehicleAffiliation(orderFee.getVehicleAffiliation());
                if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
                    in.setVehicleAffiliation("0");
                }
                in.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
                in.setOriginalAmountFee(orderFee.getPreCashFee());
                in.setUpdateAmountFee(0L);
                in.setOriginalEntityOilFee(orderFee.getPreOilFee());
                in.setUpdateEntityOilFee(0L);
                in.setOriginalVirtualOilFee(orderFee.getPreOilVirtualFee());
                in.setUpdatelongVirtualOilFee(0L);
                in.setOriginalEtcFee(orderFee.getPreEtcFee());
                in.setUpdateEtcFee(0L);
                in.setIsPayArriveFee(orderFee.getArrivePaymentState() == null ? 0: orderFee.getArrivePaymentState());
                in.setOriginalArriveFee(orderFee.getArrivePaymentFee() == null ? 0 : orderFee.getArrivePaymentFee());
                in.setUpdateArriveFee(0L);
                in.setOrderId(orderInfo.getOrderId());
                in.setTenantId(orderInfo.getTenantId());
                in.setIsNeedBill(orderInfo.getIsNeedBill());
                in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
                in.setUpdateOilConsumer(orderFeeExt.getOilConsumer());
                in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
                in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
                in.setOriginalOilBillType(orderFeeExt.getOilBillType());
                in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
                updateOrderService.updateTheOrder(in,loginInfo,token);

                originalAmountFee = 0L;
                originalEntityOilFee = 0L;
                originalVirtualOilFee = 0L;
                originalEtcFee = 0L;
                originalArriveFee = 0L;
            }
        }

        UpdateTheOrderInDto in = new UpdateTheOrderInDto();
        in.setUserId(userId);
        in.setVehicleAffiliation(orderFee.getVehicleAffiliation());
        if (orderScheduler.getVehicleClass() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            in.setVehicleAffiliation("0");
        }
        in.setOilUserType(orderInfoExt.getOilUseType()!=null?orderInfoExt.getOilUseType():-1);
        in.setOriginalAmountFee(originalAmountFee);
        in.setUpdateAmountFee(updateAmountFee);
        if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            in.setOriginalEntityOilFee(0L);
            in.setUpdateEntityOilFee(0L);
            in.setOriginalVirtualOilFee(0L);
            in.setUpdatelongVirtualOilFee(0L);
        }else{
            in.setOriginalEntityOilFee(originalEntityOilFee);
            in.setUpdateEntityOilFee(updateEntityOilFee);
            in.setOriginalVirtualOilFee(originalVirtualOilFee);
            in.setUpdatelongVirtualOilFee(updatelongVirtualOilFee);
        }
        in.setOriginalEtcFee(originalEtcFee);
        in.setUpdateEtcFee(updateEtcFee);
        in.setIsPayArriveFee(orderFee.getArrivePaymentState() == null ? 0: orderFee.getArrivePaymentState());
        in.setOriginalArriveFee(originalArriveFee);
        in.setUpdateArriveFee(updateArriveFee);
        in.setOrderId(orderInfo.getOrderId());
        in.setTenantId(orderInfo.getTenantId());
        in.setIsNeedBill(orderInfo.getIsNeedBill());
        in.setOriginalOilConsumer(orderFeeExt.getOilConsumer());
        in.setUpdateOilConsumer(orderFeeExtVer.getOilConsumer());
        in.setOriginalOilAccountType(orderFeeExt.getOilAccountType());
        in.setUpdateOilAccountType(orderFeeExtVer.getOilAccountType());
        in.setOriginalOilBillType(orderFeeExt.getOilBillType());
        in.setUpdateOilBillType(orderFeeExtVer.getOilBillType());
        updateOrderService.updateTheOrder(in,loginInfo,token);

        //代收调整司机油费
        if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection().intValue() == OrderConsts.IS_COLLECTION.YES) {
            in.setOriginalAmountFee(0L);
            in.setUpdateAmountFee(0L);
            in.setOriginalEtcFee(0L);
            in.setUpdateEtcFee(0L);
            in.setOriginalArriveFee(0L);
            in.setUpdateArriveFee(0L);
            in.setUserId(orderSchedulerVer.getCarDriverId());

            in.setOriginalEntityOilFee(originalEntityOilFee);
            in.setUpdateEntityOilFee(updateEntityOilFee);
            in.setOriginalVirtualOilFee(originalVirtualOilFee);
            in.setUpdatelongVirtualOilFee(updatelongVirtualOilFee);
            updateOrderService.updateTheOrder(in,loginInfo,token);
        }
    }
}
