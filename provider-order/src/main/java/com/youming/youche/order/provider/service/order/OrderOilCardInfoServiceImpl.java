package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.vo.facilitator.ServiceInfoBasisVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.oil.ICarLastOilService;
import com.youming.youche.order.api.order.IOrderOilCardInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.oil.CarLastOil;
import com.youming.youche.order.domain.order.OrderOilCardInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.order.QueryOrderOilCardInfoDto;
import com.youming.youche.order.provider.mapper.order.OrderOilCardInfoMapper;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


/**
 * <p>
 * 订单油卡表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class OrderOilCardInfoServiceImpl extends BaseServiceImpl<OrderOilCardInfoMapper, OrderOilCardInfo> implements IOrderOilCardInfoService {

    @Resource
    private OrderOilCardInfoMapper orderOilCardInfoMapper;
    @Resource
    private IOilCardManagementService iOilCardManagementService;
    @Resource
    IServiceInfoService iServiceInfoService;
    @Resource
    IOrderOilCardInfoService orderOilCardInfoSV;
    @Resource
    IOrderSchedulerService orderSchedulerTF;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ICarLastOilService iCarLastOilService;
    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Override
    public String deleteOrderOilCardInfo(Long orderId) {
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单ID不存在!");
        }
        LambdaQueryWrapper<OrderOilCardInfo> lambda = new QueryWrapper<OrderOilCardInfo>().lambda();
        lambda.eq(OrderOilCardInfo::getOrderId, orderId);
        this.remove(lambda);
        return "Y";
    }

    @Override
    public List<OrderOilCardInfo> queryOrderOilCardInfoByOrderId(long orderId, String oilCardNum) {

        QueryWrapper<OrderOilCardInfo> orderOilCardInfoQueryWrapper = new QueryWrapper<>();
        if(orderId != 0 ){
            orderOilCardInfoQueryWrapper.eq("order_id", orderId);
        }
       if(oilCardNum != null){
           orderOilCardInfoQueryWrapper.eq("oil_card_num", oilCardNum);
       }

        List<OrderOilCardInfo> oilCardInfos = orderOilCardInfoMapper.selectList(orderOilCardInfoQueryWrapper);
        if (oilCardInfos != null) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
                boolean isNeedWarn = false;

                List<OilCardManagement> list = iOilCardManagementService.getOilCardManagementByCard(orderOilCardInfo.getOilCardNum(), orderOilCardInfo.getTenantId());
                if (list != null && list.size() > 0) {
                    OilCardManagement oilCard = list.get(0);
                    if (oilCard.getCardType() != null && oilCard.getCardType().intValue() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                        ServiceInfoBasisVo serviceInfo = iServiceInfoService.getServiceInfo(oilCard.getUserId());
                        if (serviceInfo != null) {
                            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().intValue() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD) {
                                isNeedWarn = true;
                            }
                        }
                    }
                    orderOilCardInfo.setCardBalance(oilCard.getCardBalance());
                }
                orderOilCardInfo.setIsNeedWarn(isNeedWarn);
            }
        }

        return oilCardInfos;
    }

    @Override
    public List<QueryOrderOilCardInfoDto> queryOrderOilCardInfoByOrderIdWx(long orderId, String oilCardNum) {
        QueryWrapper<OrderOilCardInfo> orderOilCardInfoQueryWrapper = new QueryWrapper<>();
        if(orderId != 0 ){
            orderOilCardInfoQueryWrapper.eq("order_id", orderId);
        }
        if(oilCardNum != null){
            orderOilCardInfoQueryWrapper.eq("oil_card_num", oilCardNum);
        }

        List<QueryOrderOilCardInfoDto> dtos = new ArrayList<>();
        List<OrderOilCardInfo> oilCardInfos = orderOilCardInfoMapper.selectList(orderOilCardInfoQueryWrapper);
        if (oilCardInfos != null) {
            for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
                boolean isNeedWarn = false;

                List<OilCardManagement> list = iOilCardManagementService.findByOilCardNum(orderOilCardInfo.getOilCardNum(), orderOilCardInfo.getTenantId());
                if (list != null && list.size() > 0) {
                    OilCardManagement oilCard = list.get(0);
                    if (oilCard.getCardType() != null && oilCard.getCardType().intValue() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                        ServiceInfoBasisVo serviceInfo = iServiceInfoService.getServiceInfo(oilCard.getUserId());
                        if (serviceInfo != null) {
                            if (serviceInfo.getServiceType() != null && serviceInfo.getServiceType().intValue() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD) {
                                isNeedWarn = true;
                            }
                        }
                    }
                    orderOilCardInfo.setCardBalance(oilCard.getCardBalance());
                }
                orderOilCardInfo.setIsNeedWarn(isNeedWarn);

                QueryOrderOilCardInfoDto dto = new QueryOrderOilCardInfoDto();
                BeanUtil.copyProperties(orderOilCardInfo, dto);
                dto.setOilCarNum(dto.getOilCardNum()==null?"":dto.getOilCardNum());
                if (dto.getCardType() != null) {
                    dto.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", dto.getCardType() + ""));
                }
                dtos.add(dto);
            }
        }

        return dtos;
    }

    @Override
    public CarLastOil getOilCarNumberByPlateNumber(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isEmpty(plateNumber)) {
            return new CarLastOil();
        }
        CarLastOil carLastOil = iCarLastOilService.getCarLastOilByPlateNumber(plateNumber, loginInfo.getTenantId());
        if (carLastOil == null) {
            List<OilCardManagement> list = iOilCardManagementService.getOilCardsByPlateNumber(plateNumber, accessToken);
            carLastOil = new CarLastOil();
            if (list != null && list.size() > 0) {
                carLastOil.setPlateNumber(plateNumber);
                carLastOil.setOilCarNum(list.get(list.size() - 1).getOilCarNum());
            }
        }
        return carLastOil;
    }

    @Override
    public List<QueryOrderOilCardInfoDto> getOilCarNumberByPlateNumberWX(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        CarLastOil carLastOil = iCarLastOilService.getCarLastOilByPlateNumber(plateNumber, loginInfo.getTenantId());
        if (carLastOil == null) {
            List<OilCardManagement> list = iOilCardManagementService.getOilCardsByPlateNumber(plateNumber, accessToken);
            carLastOil = new CarLastOil();
            if (list != null && list.size() > 0) {
                carLastOil.setPlateNumber(plateNumber);
                carLastOil.setOilCarNum(list.get(list.size() - 1).getOilCarNum());
            }
        }

        List<QueryOrderOilCardInfoDto> dtos = new ArrayList<>();
        QueryOrderOilCardInfoDto dto = new QueryOrderOilCardInfoDto();
        BeanUtil.copyProperties(carLastOil, dto);

        if (dto.getCardType() != null) {
            dto.setCardTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue2String("OIL_CARD_TYPE", dto.getCardType() + ""));
        }
        dtos.add(dto);
        return dtos;
    }

    @Override
    public List<String> queryOrdercConsumeCard(Long orderId,String accessToken) {
        if (orderId <= 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        List<String> list = new ArrayList<String>();
        List<OrderOilCardInfo> oilCardInfos = orderOilCardInfoSV.queryOrderOilCardInfoByOrderId(orderId, null);
        for (OrderOilCardInfo orderOilCardInfo : oilCardInfos) {
            list.add(orderOilCardInfo.getOilCardNum());
        }
        //TODO 还需查询车辆绑定卡号
        OrderScheduler orderScheduler = orderSchedulerTF.getOrderScheduler(orderId);
        String bindVehicle = "";
        if (orderScheduler == null) {
            OrderSchedulerH schedulerH = orderSchedulerTF.getOrderSchedulerH(orderId);
            if (schedulerH != null) {
                bindVehicle = schedulerH.getPlateNumber();
            }else{
                throw new BusinessException("未找到订单号["+orderId+"]的订单信息");
            }
        }else{
            bindVehicle = orderScheduler.getPlateNumber();
        }
        if (StringUtils.isNotBlank(bindVehicle)) {
            List<OilCardManagement> managements = iOilCardManagementService.getOilCardsByPlateNumbers(bindVehicle,accessToken);
            for (OilCardManagement oilCardManagement : managements) {
                list.add(oilCardManagement.getOilCarNum());
            }
        }
        LinkedHashSet<String> set = new LinkedHashSet<String>(list.size());
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    @Override
    public List<OrderOilCardInfo> queryOrderOilCardInfoByOrderIds(long orderId, String oilCardNum,String accessToken ){
        QueryWrapper<OrderOilCardInfo> wrapper = new QueryWrapper<>();
        if (orderId>0){
            wrapper.eq("order_id",orderId);
        }
        if (StringUtils.isNotBlank(oilCardNum)){
            wrapper.eq("oil_car_num",oilCardNum);
        }
        List<OrderOilCardInfo> orderOilCardInfos = baseMapper.selectList(wrapper);
        return orderOilCardInfos;
    }

}
