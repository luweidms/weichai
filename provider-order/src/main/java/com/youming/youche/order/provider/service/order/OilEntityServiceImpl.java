package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.order.api.order.IOilEntityService;
import com.youming.youche.order.api.order.IOrderGoodsService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.order.OilEntity;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.provider.mapper.order.OilEntityMapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * <p>
 * 油充值核销表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
@Service
public class OilEntityServiceImpl extends BaseServiceImpl<OilEntityMapper, OilEntity> implements IOilEntityService {
    @Autowired
    private IOrderGoodsService orderGoodsService;
    @DubboReference(version = "1.0.0")
    private IServiceInfoService serviceInfoService;

    @Override
    public void saveOilCardLog(String oilcardNum, Long tenantId, Long userId, OrderScheduler scheduler, OrderInfo orderInfo, Long oilFee, OilCardManagement management, String oilAffiliation) {
        if (management != null ) {
            OrderGoods orderGoods = orderGoodsService.getOrderGoods(scheduler.getOrderId());
            OilEntity oilEntity = new OilEntity();
            oilEntity.setDependTime(scheduler.getDependTime());
            oilEntity.setDesRegion(orderInfo.getDesRegion());
            oilEntity.setNoVerificateEntityFee(oilFee);
            oilEntity.setOilCarNum(oilcardNum);
            oilEntity.setOrderId(scheduler.getOrderId());
            oilEntity.setPlateNumber(scheduler.getPlateNumber());
            oilEntity.setPreOilFee(oilFee);
            oilEntity.setSourceRegion(orderInfo.getSourceRegion());
            oilEntity.setTenantId(orderInfo.getTenantId());
            oilEntity.setUserId(userId);
            oilEntity.setVehicleClass(scheduler.getVehicleClass());
            oilEntity.setVehicleLengh(scheduler.getCarLengh());
            oilEntity.setVehicleStatus(scheduler.getCarStatus());
            oilEntity.setCreationTime(LocalDateTime.now());
            oilEntity.setVehicleCode(scheduler.getVehicleCode());
            oilEntity.setOilAffiliation(oilAffiliation);
            oilEntity.setOilType(SysStaticDataEnum.OIL_TYPE.OIL_TYPE1);
            oilEntity.setLineState(OrderAccountConst.OIL_ENTITY.LINE_STATE0);
            oilEntity.setCarDriverMan(scheduler.getCarDriverMan());
            oilEntity.setCarDriverPhone(scheduler.getCarDriverPhone());
            oilEntity.setCustomName(orderGoods.getCustomName());
            oilEntity.setSourceName(scheduler.getSourceName());
            if (management.getCardType() != null
                    && management.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                ServiceInfo serviceInfo = serviceInfoService.getServiceInfoByServiceUserId(management.getUserId());
                if (serviceInfo != null) {
                    oilEntity.setServiceName(serviceInfo.getServiceName());
                    if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().intValue() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD) {
                        oilEntity.setLineState(OrderAccountConst.OIL_ENTITY.LINE_STATE1);
                    }
                }
                oilEntity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE1);//默认未充值
                oilEntity.setVerificationState(OrderConsts.PayOutVerificationState.INIT);
            }else{//客户卡/自购卡 需自动核销
                oilEntity.setVerificationDate(LocalDateTime.now());
                oilEntity.setVerificationState(OrderConsts.PayOutVerificationState.VERIFICATION_STATE);
                oilEntity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE5);
            }
            this.saveOrUpdate(oilEntity);
        } else {
            throw new BusinessException("未找到油卡号[" + oilcardNum + "]");
        }
    }
}
