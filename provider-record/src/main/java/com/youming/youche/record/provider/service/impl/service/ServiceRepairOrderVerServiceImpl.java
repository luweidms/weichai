package com.youming.youche.record.provider.service.impl.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.record.api.oa.IOaFilesService;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.service.IServiceRepairOrderVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.oa.OaFiles;
import com.youming.youche.record.domain.service.ServiceRepairItemsVer;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.domain.service.ServiceRepairOrderVer;
import com.youming.youche.record.domain.service.ServiceRepairPartsVer;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.RepairOrderDetailVerDto;
import com.youming.youche.record.dto.RepairOrderDto;
import com.youming.youche.record.dto.service.GetRepairOrderPartsVerDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailVerDto;
import com.youming.youche.record.provider.mapper.service.ServiceRepairItemsVerMapper;
import com.youming.youche.record.provider.mapper.service.ServiceRepairOrderMapper;
import com.youming.youche.record.provider.mapper.service.ServiceRepairOrderVerMapper;
import com.youming.youche.record.provider.mapper.service.ServiceRepairPartsVerMapper;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDataInfoMapper;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p>
 * 维修保养订单 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-04-08
 */
@DubboService(version = "1.0.0")
public class ServiceRepairOrderVerServiceImpl extends BaseServiceImpl<ServiceRepairOrderVerMapper, ServiceRepairOrderVer> implements IServiceRepairOrderVerService {

    @Resource
    ServiceRepairItemsVerMapper serviceRepairItemsVerMapper;

    @Resource
    ServiceRepairPartsVerMapper serviceRepairPartsVerMapper;

    @Resource
    VehicleDataInfoMapper vehicleDataInfoMapper;

    @Lazy
    @Resource
    IServiceRepairOrderService serviceRepairOrderService;

    @Resource
    IOaFilesService iOaFilesService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @Resource
    LoginUtils loginUtils;
    @Resource
    ITenantVehicleRelService tenantVehicleRelService;
    @Resource
    IVehicleDataInfoService vehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;
    @Resource
    ServiceRepairOrderMapper serviceRepairOrderMapper;
    @Resource
    ServiceRepairOrderVerMapper serviceRepairOrderVerMapper;


    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRepairOrderServiceImpl.class);



    @Override
    public ServiceRepairOrderVer getServiceRepairOrderVer(long flowId, Integer diffFlg) {
        LambdaQueryWrapper<ServiceRepairOrderVer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceRepairOrderVer::getFlowId, flowId);
        queryWrapper.isNotNull(ServiceRepairOrderVer::getOrderSn);
        queryWrapper.eq(null != diffFlg, ServiceRepairOrderVer::getDiffFlg, diffFlg);
        queryWrapper.orderByDesc(ServiceRepairOrderVer::getId);
        List<ServiceRepairOrderVer> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new ServiceRepairOrderVer();
    }

    @Override
    public GetRepairOrderPartsVerDto getRepairOrderPartsVer(Long id, Long flowId, Long orderItemId) {
        if(null == orderItemId || orderItemId < 0){
            orderItemId = null;
        }
        GetRepairOrderPartsVerDto dto = new GetRepairOrderPartsVerDto();

        // 查询维修保养订单信息
        ServiceRepairOrderVer serviceRepairOrderVer = baseMapper.getServiceRepairOrderVer(flowId);

        // 服务商维修项
        ServiceRepairItemsVer serviceRepairItemsVer = null;
        if(null != serviceRepairOrderVer){
            if(id > 0){
                serviceRepairItemsVer = serviceRepairItemsVerMapper.getServiceRepairItemsVerById(id);
            } else{
                serviceRepairItemsVer = new ServiceRepairItemsVer();
                serviceRepairItemsVer.setItemName("自定义项目");
                serviceRepairItemsVer.setItemId(0l);
                serviceRepairItemsVer.setItemPartsCount("0");
                serviceRepairItemsVer.setItemManHour(0.0);
                serviceRepairItemsVer.setRepairOrderId(flowId);
            }
            dto.setItemName(serviceRepairItemsVer.getItemName());
            dto.setTotalItemPrice(serviceRepairItemsVer.getTotalItemPrice());
            dto.setItemManHour(serviceRepairItemsVer.getItemManHour());

            // 查询维修保养订单信息
            List<ServiceRepairPartsVer> serviceRepairPartsVerList = serviceRepairPartsVerMapper.getRepairOrderPartsVer(flowId, serviceRepairOrderVer.getId(), orderItemId);
            dto.setServiceRepairPartsVerList(serviceRepairPartsVerList);
            //配件费用
            Long totalPartsPrice = 0L;
            for (int i = 0; i < serviceRepairPartsVerList.size(); i++) {
                ServiceRepairPartsVer parts = serviceRepairPartsVerList.get(i);
                if(null != parts.getTotalPartsPrice() && parts.getTotalPartsPrice() > 0){
                    totalPartsPrice = totalPartsPrice + parts.getTotalPartsPrice();
                }
            }
            dto.setTotalPartsPrice(totalPartsPrice);
            dto.setTotalPrice((null == serviceRepairItemsVer.getTotalItemPrice() ? 0L : serviceRepairItemsVer.getTotalItemPrice()) + totalPartsPrice);
        }

        return dto;
    }

    @Override
    public Page<ServiceRepairOrder> doQueryOrderListApp(Integer orderStatus, Integer pageNum, Integer pageSize,String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        Page<ServiceRepairOrder> serviceRepairOrderPage = vehicleDataInfoMapper.doQueryOrderListApp(new Page<>(pageNum, pageSize), orderStatus, loginInfo.getTenantId(), Long.valueOf(loginInfo.getBillId()));
        List<ServiceRepairOrder> list=serviceRepairOrderPage.getRecords();
        for (ServiceRepairOrder dto : list) {
            dto.setOrderStatusName(dto.getOrderStatus()==null?"":iSysStaticDataService.getSysStaticData("ORDER_STATUS", dto.getOrderStatus() + "").getCodeName());
            dto.setWorkTypeName(dto.getWorkType()==null?"":iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", dto.getWorkType() + "").getCodeName());

            Long flowId = dto.getId();
            ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerMapper.getServiceRepairOrderVer(flowId);
            if (null != serviceRepairOrderVer) {
                dto.setTotalAmount(serviceRepairOrderVer.getTotalAmount());
            }
        }
        return serviceRepairOrderPage;
    }

    @Override
    public RepairOrderDetailVerDto getRepairOrderDetailVerApp(Long flowId) {
        if(flowId < 0){
            throw new BusinessException("缺少参数");
        }
        RepairOrderDetailVerDto dto=new RepairOrderDetailVerDto();
        ServiceRepairOrderDetailVerDto repairOrderDetailVer = serviceRepairOrderService.getRepairOrderDetailVer(flowId);
        dto.setServiceRepairItemsVerList(repairOrderDetailVer.getServiceRepairItemsVerList());
        dto.setTenantId(repairOrderDetailVer.getTenantId());
        dto.setShopInfo(repairOrderDetailVer.getShopInfo());
        dto.setServiceRepairOrderVer(repairOrderDetailVer.getServiceRepairOrderVer());
        List<OaFiles> repairOrderPicList = iOaFilesService.getRepairOrderPicListById(flowId);
        FastDFSHelper client =null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Long tenantId=null;
        for (OaFiles oa :repairOrderPicList){
//            tenantId=oa.getTenantId();
            try {
                String tag = oa.getFileUrl().substring(oa.getFileUrl().lastIndexOf(".") + 1);
                String url =  client.getHttpURL(oa.getFileUrl()).split("\\?")[0];
                oa.setFileUrl(url.substring(0,url.lastIndexOf("."))+ "_big." + tag);
                LOGGER.info("打印参数："+ (client.getHttpURL(oa.getFileUrl()).split("\\?")[0]).substring(0,oa.getFileUrl().indexOf("."))+ "_big." + tag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //图片信息
        dto.setRepairOrderPicMapList(repairOrderPicList);
        //审核信息
        List<SysOperLog> sysOperLogList = sysOperLogService.querySysOperLogAll(300005, flowId, false);
//        List<SysOperLog> sysOperLogListNew = new ArrayList<>();
//        for (SysOperLog sysOperLog : sysOperLogList){
//            sysOperLogListNew.add(sysOperLog);
//        }
        dto.setSysOperLogList(sysOperLogList);
        //车队信息
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(repairOrderDetailVer.getTenantId());
        dto.setTenant(sysTenantDef);
        return dto;
    }

    @Override
    public boolean doSaveOrUpdateServiceRepairOrder(RepairOrderDto repairOrderDto, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        if(StringUtils.isBlank(repairOrderDto.getContractName())){
            throw new BusinessException("司机姓名不能为空");
        }
        if(StringUtils.isBlank(repairOrderDto.getContractMobile())){
            throw new BusinessException("司机手机号不能为空");
        }
        if( repairOrderDto.getVehicleCode() < 0){
            throw new BusinessException("车牌号不能为空");
        }
        if(StringUtils.isBlank(repairOrderDto.getWorkType())){
            throw new BusinessException("工单类型不能为空");
        }
        if("GHCBY".equals(repairOrderDto.getWorkType())){
            if(null == repairOrderDto.getScrEndTime() || null == repairOrderDto.getScrEndTime()){
                throw new BusinessException("请选择计划起始时间");
            }
            if(StringUtils.isBlank(repairOrderDto.getItems())){
                throw new BusinessException("至少选择一个保养项目");
            }
        }
        TenantVehicleRel tenantVehicleRel = tenantVehicleRelService.getTenantVehiclesRel(repairOrderDto.getVehicleCode(), SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        if(null == tenantVehicleRel){
            throw new BusinessException("该功能暂时只对自有车开放");
        }
        if(StringUtils.isBlank(repairOrderDto.getCarNo())){
            VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getById(repairOrderDto.getVehicleCode());
            repairOrderDto.setCarNo(vehicleDataInfo.getPlateNumber());
        }
        ServiceRepairOrder serviceRepairOrder = null;
        ServiceRepairOrderVer serviceRepairOrderVer = new ServiceRepairOrderVer();
        String fmt = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fmt);
        String newTime = LocalDateTime.now().format(formatter);
        if(repairOrderDto.getFlowId()!=null && repairOrderDto.getFlowId() > 0){
            serviceRepairOrder = serviceRepairOrderService.getById(repairOrderDto.getFlowId());
            if(serviceRepairOrder.getOrderStatus() != ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1 && serviceRepairOrder.getOrderStatus() != ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS2){
                throw new BusinessException("工单信息不允许修改");
            }
            BeanUtils.copyProperties(repairOrderDto,serviceRepairOrder);
            serviceRepairOrder.setTenantId(tenantVehicleRel.getTenantId());
            serviceRepairOrder.setUserId(tenantVehicleRel.getDriverUserId());
            serviceRepairOrder.setOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateOpId(loginInfo.getUserInfoId());
            serviceRepairOrder.setUpdateDate(newTime);
            serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1);
            serviceRepairOrderService.saveOrUpdate(serviceRepairOrder);
            BeanUtils.copyProperties(serviceRepairOrder,serviceRepairOrderVer);
            serviceRepairOrderVer.setFlowId(repairOrderDto.getFlowId());
            serviceRepairOrderVerMapper.updateServiceRepairOrderVerByFlowId(serviceRepairOrderVer);
//			BeanUtils.populate(serviceRepairOrderVer,inParam);
            sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(),SysOperLogConst.OperType.Update , "修改工单信息");
        }else{
            serviceRepairOrder = new ServiceRepairOrder();
            BeanUtils.copyProperties(repairOrderDto,serviceRepairOrder);
            serviceRepairOrder.setCreateDate(newTime);
            serviceRepairOrder.setTenantId(tenantVehicleRel.getTenantId());
            serviceRepairOrder.setUserId(tenantVehicleRel.getDriverUserId());
            serviceRepairOrder.setOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateOpId(loginInfo.getUserInfoId());
            serviceRepairOrder.setUpdateDate(newTime);
            serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1);
            serviceRepairOrderService.saveOrUpdate(serviceRepairOrder);
            if("GHCWX".equals(repairOrderDto.getWorkType())){
                serviceRepairOrder.setOrderCode("WX"+String.format("%08d", serviceRepairOrder.getId()));
            }else if("GHCBY".equals(repairOrderDto.getWorkType())){
                serviceRepairOrder.setOrderCode("BY"+String.format("%08d", serviceRepairOrder.getId()));
            }
            serviceRepairOrderMapper.updateServiceRepairOrderById(serviceRepairOrder);

            BeanUtils.copyProperties(serviceRepairOrder,serviceRepairOrderVer);
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(),SysOperLogConst.OperType.Add , "发起工单");
            serviceRepairOrderVer.setFlowId(serviceRepairOrder.getId());
        }
        try {
            this.saveOrUpdate(serviceRepairOrderVer);
        }catch (Exception e){
            e.printStackTrace();
        }

        //处理图片
        serviceRepairOrderService.doSavePicFiles(serviceRepairOrderVer.getFlowId(),repairOrderDto.getPicFileIds(),"");
        Map<String, Object> params = new ConcurrentHashMap<String, Object>();
        params.put("svName","IServiceRepairOrderService");
        boolean bool=true;
        try {
            bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_REPAIR_INFO, serviceRepairOrderVer.getFlowId(), SysOperLogConst.BusiCode.serviceRepair, params, accessToken, serviceRepairOrder.getTenantId());
        }catch (Exception e){
            e.printStackTrace();
        }
//        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_REPAIR_INFO, repairOrderDto.getFlowId(), SysOperLogConst.BusiCode.serviceRepair, params, accessToken, serviceRepairOrder.getTenantId());
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
        return true;

    }

}
