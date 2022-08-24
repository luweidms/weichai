package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.order.api.order.IOrderReceiptService;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.domain.order.OrderReport;
import com.youming.youche.order.domain.tenant.TenantVehicleRel;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.VehiclesDto;
import com.youming.youche.order.dto.VehiclesListDto;
import com.youming.youche.order.provider.mapper.order.OrderReportMapper;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuhao
 * @since 2022-03-29
 */
@DubboService(version = "1.0.0")
@Service
public class OrderReportServiceImpl extends BaseServiceImpl<OrderReportMapper, OrderReport> implements IOrderReportService {

    @Resource
    private LoginUtils loginUtils;

    @Resource
    OrderReportMapper orderReportMapper;

    @Resource
    private IOrderReceiptService iOrderReceiptService;

    @Resource
    private ReadisUtil readisUtil;

    /**
     * 查询订单报备
     * 聂杰伟
     */
    @Override
    public List<OrderReportDto> queryOrderReportList(Long orderId, Long tenantId, Long userId, String accessToken) {
        List<OrderReportDto> reportDtoList = new ArrayList<>();
        QueryWrapper<OrderReport> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
                if(tenantId != null && tenantId > 0){
                    wrapper.eq("tenant_id",tenantId);
                }
                if(userId != null && userId > 0){
                    wrapper.eq("report_user_id",userId);
                }
        wrapper.orderByDesc("create_time");

       List<OrderReport> list = baseMapper.selectList(wrapper);
       try {
           FastDFSHelper client = FastDFSHelper.getInstance();
           for (OrderReport orderReport:list) {
               OrderReportDto orderReportDto = new OrderReportDto();
               orderReportDto.setOrderId(orderReport.getOrderId());
               orderReportDto.setReportType(orderReport.getReportType());
               orderReportDto.setReportDesc(orderReport.getReportDesc());
               orderReportDto.setImgIds(orderReport.getImgIds());
               orderReportDto.setImgUrls(orderReport.getImgUrls());
               orderReportDto.setId(orderReport.getId());
               orderReportDto.setReportUserId(orderReport.getReportUserId());
               orderReportDto.setReportUserName(orderReport.getReportUserName());
               orderReportDto.setReportUserPhone(orderReport.getReportUserPhone());
               orderReportDto.setCreateDate(orderReport.getCreateTime());
               orderReportDto.setReportTypeName(readisUtil.getSysStaticData("ORDER_REPORT", orderReport.getReportType()+"").getCodeName());
               if (StringUtils.isNotEmpty(orderReport.getImgIds())) {
                   String[] imgUrlArr = orderReport.getImgUrls().split(",");
                   for (int i = 0; i < imgUrlArr.length; i++) {
                       imgUrlArr[i] = client.getHttpURL(imgUrlArr[i]).split("\\?")[0];
                   }
                   orderReportDto.setImgUrlArr(imgUrlArr);
               }
               reportDtoList.add(orderReportDto);
           }
       }catch (Exception e){
           log.error("查询异常"+e);
       }
        return reportDtoList;
    }

    @Override
    public List<Long> queryOrderOilEnRouteUse(Long userId, Long oilId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID为空!");
        }
        if (oilId == null || oilId <= 0) {
            throw new BusinessException("油站编号为空!");
        }
        Integer  vehicleClass = SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1;
        List<Integer> vehicleClassOther = new ArrayList<>();
        vehicleClassOther.add(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5);
        vehicleClassOther.add(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2);
        vehicleClassOther.add(SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4);
        Integer  orderState = OrderConsts.ORDER_STATE.TO_BE_LOAD;
        Integer  orderStateOther  = OrderConsts.ORDER_STATE.RECIVE_TO_BE_AUDIT;
        List<Long> longList = baseMapper.queryOrderOilEnRouteUse(vehicleClass, vehicleClassOther, orderState, orderStateOther, userId, oilId);
        return longList;
    }

    @Override
    public List<OrderReportDto> queryOrderReport(Long orderId, Long userId, String accessToken) {
        List<OrderReportDto> orderReportDtos = this.queryOrderReportList(orderId, null, userId, accessToken);
        for (OrderReportDto dto : orderReportDtos){
            dto.setReportTypeName(readisUtil.getSysStaticData("ORDER_REPORT", dto.getReportType()+"").getCodeName());
        }
        return orderReportDtos;
    }

    @Override
    public VehiclesDto getVehicle(String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long carDriverId = user.getUserInfoId();
        Long tenantId = user.getTenantId();

        Set<String> vehicles=new HashSet<String>();
        //查询司机名下的车辆
        List<VehiclesListDto> list=this.getVehicle(carDriverId, tenantId,false);
        if(list!=null) {
            for (VehiclesListDto map : list) {
                VehicleDataInfo vehicleDataInfo = map.getVehicleDataInfo();
                TenantVehicleRel tenantVehicleRel = map.getTenantVehicleRel();
                if(!vehicles.contains(vehicleDataInfo.getPlateNumber())
                        &&tenantVehicleRel.getAuthState()==SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
                    vehicles.add(vehicleDataInfo.getPlateNumber());
                }
            }
        }

        // TODO 2022-6-29 小程序 和绑定车辆同步
//        //查询司机在途行驶的车辆
//        List<QueryDriverOrderDto> list1 =iOrderReceiptService.queryDriverOrderPlateNumber(carDriverId);
//        if(list1!=null) {
//            for (QueryDriverOrderDto map : list1) {
//                String plateNumber= map.getPlateNumber();
//                if(StringUtils.isNotBlank(plateNumber)&&!vehicles.contains(plateNumber)) {
//                    vehicles.add(plateNumber);
//                }
//            }
//        }


        VehiclesDto vehiclesDto = new VehiclesDto();
        vehiclesDto.setVehicles(vehicles);
        return vehiclesDto;
    }

    private List<VehiclesListDto> getVehicle(Long userId, Long tenantId,Boolean all) {
        List<VehiclesListDto> vehiclesListDtos = baseMapper.selectOr(userId,tenantId,all);
        return vehiclesListDtos;
    }

}
