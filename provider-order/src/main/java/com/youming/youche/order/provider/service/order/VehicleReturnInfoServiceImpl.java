package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.order.IVehicleReturnInfoService;
import com.youming.youche.order.domain.order.VehicleReturnInfo;
import com.youming.youche.order.provider.mapper.order.VehicleReturnInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


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
public class VehicleReturnInfoServiceImpl extends BaseServiceImpl<VehicleReturnInfoMapper, VehicleReturnInfo> implements IVehicleReturnInfoService {
    @Resource
    private VehicleReturnInfoMapper  vehicleReturnInfoMapper;

    @Override
    public void disableVehicleReturns(String plateNumber, String inValidDate, Boolean isReturn) {
        LambdaUpdateWrapper<VehicleReturnInfo> updateWrapper= Wrappers.lambdaUpdate();
        if(isReturn){
            updateWrapper.set(VehicleReturnInfo::getState,0)
                          .eq(VehicleReturnInfo::getPlateNumber,plateNumber)
                          .le(VehicleReturnInfo::getBeginDate,inValidDate);
        } else{
            updateWrapper.set(VehicleReturnInfo::getState,0)
                          .eq(VehicleReturnInfo::getPlateNumber,plateNumber);
        }
       this.update(updateWrapper);

    }

    @Override
    public void addVehicleReturn(VehicleReturnInfo returnInfo, LoginInfo user) {
//        if (returnInfo.getBeginDate() == null) {
//            throw new BusinessException("回货出发时间不能为空");
//        }
        String relDateMap = this.queryRecentArriveDate(returnInfo.getPlateNumber(), returnInfo.getOrderId());
//        if (relDateMap != null && !relDateMap.equals("")){
//            LocalDateTime dateTime = LocalDateTimeUtil.convertStringToDate(relDateMap);
//            LocalDateTime localDateTime = dateTime.plusHours(2);
//            if (localDateTime.compareTo(returnInfo.getBeginDate())> 0){
//                throw new BusinessException("到达目的地2小时后才能发布回货信息");
//            }
//        }

//        if (LocalDateTime.now().plusHours(72).compareTo(returnInfo.getBeginDate()) < 0){
//            throw new BusinessException("出发时间必须在3天以内");
//        }
//        if (LocalDateTime.now().compareTo(returnInfo.getBeginDate()) > 0){
//            throw new BusinessException("出发时间必须大于当前时间");
//        }
        returnInfo.setExpiryDate(returnInfo.getBeginDate().plusHours(72));
        returnInfo.setOpId(user.getId());
        returnInfo.setCreateDate(LocalDateTime.now());
        returnInfo.setState(1);
        this.addVehicleReturn(returnInfo);
    }

    @Override
    public String queryRecentArriveDate(String plateNumber, Long orderId) {
        List<String> list = vehicleReturnInfoMapper.queryRecentArriveDate(plateNumber, orderId);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }else {
            return null;
        }
    }

    @Override
    public void addVehicleReturn(VehicleReturnInfo reutrnInfo) {
        LambdaQueryWrapper<VehicleReturnInfo> lambda=Wrappers.lambdaQuery();
        lambda.eq(VehicleReturnInfo::getPlateNumber,reutrnInfo.getPlateNumber())
               .eq(VehicleReturnInfo::getState,1)
               .orderByDesc(VehicleReturnInfo::getBeginDate)
                .last("LIMIT 1");
        List<VehicleReturnInfo> qryResultList = this.list(lambda);
        if (qryResultList != null && qryResultList.size() > 0){
            LocalDateTime beginDate = qryResultList.get(0).getBeginDate();
            if(reutrnInfo.getBeginDate().compareTo(beginDate.plusHours(2)) < 0){
                throw new BusinessException("出发时间必须在上个回货出发时间2小时以后");
            }
        }
        this.save(reutrnInfo);
    }
}
