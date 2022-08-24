package com.youming.youche.record.provider.service.impl.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.*;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.oa.IOaFilesService;
import com.youming.youche.record.api.service.IServiceRepairItemsService;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.service.IServiceRepairOrderVerService;
import com.youming.youche.record.api.service.IServiceRepairPartsService;
import com.youming.youche.record.api.sys.ISysUserService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.oa.OaFiles;
import com.youming.youche.record.domain.service.*;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.DoSureDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailVerDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderUpdateEchoDto;
import com.youming.youche.record.provider.mapper.oa.OaFilesMapper;
import com.youming.youche.record.provider.mapper.service.*;
import com.youming.youche.record.provider.mapper.sys.SysAttachMapper;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelMapper;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDataInfoMapper;
import com.youming.youche.record.vo.service.ServiceRepairOrderIUVo;
import com.youming.youche.record.vo.service.ServiceRepairOrderVo;
import com.youming.youche.system.api.*;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditNodeInstVerService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.constant.AuditConsts;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeInstVer;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.youming.youche.conts.ServiceConsts.REPAIR_ORDER_STATUS.*;
import static com.youming.youche.conts.ServiceConsts.REPAIR_WORK_STATUS.YWC;
import static com.youming.youche.util.BeanUtils.convertBean2Map;

/**
 * hzx
 * 车辆维保
 *
 * @date 2022/1/8 10:56
 */
@DubboService(version = "1.0.0")
public class ServiceRepairOrderServiceImpl extends BaseServiceImpl<ServiceRepairOrderMapper, ServiceRepairOrder> implements IServiceRepairOrderService {

    @Resource
    ServiceRepairOrderMapper serviceRepairOrderMapper;

    @Resource
    ServiceRepairOrderVerMapper serviceRepairOrderVerMapper;

    @Lazy
    @Resource
    IServiceRepairOrderVerService serviceRepairOrderVerService;

    @Resource
    ServiceRepairItemsMapper serviceRepairItemsMapper;

    @Resource
    IServiceRepairItemsService serviceRepairItemsService;

    @Resource
    ServiceRepairPartsMapper serviceRepairPartsMapper;

    @Resource
    IServiceRepairPartsService serviceRepairPartsService;

    @Resource
    ServiceRepairPartsVerMapper serviceRepairPartsVerMapper;
    @Resource
    ServiceInfoMapper serviceInfoMapper;

    @Resource
    VehicleDataInfoMapper vehicleDataInfoMapper;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;


    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    OaFilesMapper oaFilesMapper;

    @Resource
    TenantVehicleRelMapper tenantVehicleRelMapper;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstVerService auditNodeInstVerService;

    @DubboReference(version = "1.0.0")
    IAuditService iAuditService;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;
    @Lazy
    @Autowired
    ISysUserService iSysUserService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @Resource
    SysAttachMapper sysAttachMapper;

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @DubboReference(version = "1.0.0")
    IBasicConfigurationService basicConfigurationService;

    @DubboReference(version = "1.0.0")
    IAuditNodeInstService iAuditNodeInstService;

    @Resource
    IOaFilesService iOaFilesService;

    @Autowired
    IVehicleDataInfoService vehicleDataInfoService;

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRepairOrderServiceImpl.class);

    private final String PUSH_REPAIR_ORDER_TENANT_IDS = "2066";

    @Override
    public Page<ServiceRepairOrderDto> doQueryOrderList(ServiceRepairOrderVo serviceRepairOrderVo, Page<ServiceRepairOrderDto> objectPage, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        serviceRepairOrderVo.setTenantId(loginInfo.getTenantId());
        serviceRepairOrderVo.setTenantName(iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getName());
        String scrStartTime = serviceRepairOrderVo.getScrStartTime();
        if (StringUtils.isNotBlank(scrStartTime)) {
            scrStartTime = scrStartTime + " 00:00:00";
            serviceRepairOrderVo.setScrStartTime(scrStartTime);
        }

        String scrEndTime = serviceRepairOrderVo.getScrEndTime();
        if (StringUtils.isNotBlank(scrEndTime)) {
            scrEndTime = scrEndTime + " 23:59:59";
            serviceRepairOrderVo.setScrEndTime(scrEndTime);
        }

        String repairStartTime = serviceRepairOrderVo.getRepairStartTime();
        if (StringUtils.isNotBlank(repairStartTime)) {
            repairStartTime = repairStartTime + " 00:00:00";
            serviceRepairOrderVo.setRepairStartTime(repairStartTime);
        }

        String repairEndTime = serviceRepairOrderVo.getRepairEndTime();
        if (StringUtils.isNotBlank(repairEndTime)) {
            repairEndTime = repairEndTime + " 23:59:59";
            serviceRepairOrderVo.setRepairEndTime(repairEndTime);
        }

        Page<ServiceRepairOrderDto> serviceRepairOrderDtoPage = serviceRepairOrderMapper.doQueryOrderList(serviceRepairOrderVo, objectPage);
        List busiIds = new ArrayList();
        for (ServiceRepairOrderDto record : serviceRepairOrderDtoPage.getRecords()) {

            VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(record.getCarNo());
            if (null != vehicleDataInfo) {
                String brandModel = (null == vehicleDataInfo.getBrandModel() ? "" : vehicleDataInfo.getBrandModel()) + "" + (null == vehicleDataInfo.getVehicleModel() ? "" : vehicleDataInfo.getVehicleModel());
                record.setBrandModel(StringUtils.isNotBlank(brandModel) ? brandModel : "");
            }

            int orderStatus = record.getOrderStatus();
            orderStatus = orderStatus == 0 ? 2 : orderStatus;
            record.setOrderStatusName(iSysStaticDataService.getSysStaticData("ORDER_STATUS", orderStatus + "").getCodeName());
            String workType = record.getWorkType();
            record.setWorkTypeName(iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", workType + "").getCodeName());
            Long totalAmount = record.getTotalAmount();
            if (totalAmount != null && totalAmount.intValue() > 0) {
                record.setTotalAmount(Long.parseLong(CommonUtil.divide(totalAmount)));
            }
            String lastOrderMileage = record.getLastOrderMileage();
            record.setUpdValue(0);
            /**
             * 订单状态 1申请待审核 2申请不通过 3待接单 4工单待确认 5工单待审核 6工单不通过 7维保中  8完成待确认   11待支付 12已完成 13废弃
             *  14申请审核中 15工单审核中 16工单支付中。
             */
            if ("GHCWX".equals(workType) || orderStatus < 4 || orderStatus == 13) {
                record.setUpdValue(1);
            } else if (StringUtils.isBlank(lastOrderMileage)) {
                record.setUpdValue(2);
            }
            Long eid = record.getEid();
            busiIds.add(eid);
        }
        dealPermission(AuditConsts.AUDIT_CODE.SERVICE_REPAIR_INFO,busiIds,serviceRepairOrderDtoPage.getRecords(),accessToken);
        return serviceRepairOrderDtoPage;
    }

    public void dealPermission(String busiCode, List busiIds, List<ServiceRepairOrderDto> items, String accessToken){
        Map<Long, Boolean> hasPermissionMap = null;
        if(!busiIds.isEmpty()) {
            hasPermissionMap = iAuditNodeInstService.isHasPermission(busiCode, busiIds, accessToken);
            if(null == hasPermissionMap){
                return;
            }
            for (int i = 0; i < items.size(); i++) {
                ServiceRepairOrderDto rtnMap = items.get(i);
                long eid = rtnMap.getEid();
                Boolean flg = hasPermissionMap.get(rtnMap.getEid());
                rtnMap.setIsAuth(flg?1:0);
            }
        }
    }

    @Override
    @Async
    public void doQueryOrderListExport(ServiceRepairOrderVo serviceRepairOrderVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        serviceRepairOrderVo.setTenantId(loginInfo.getTenantId());
        serviceRepairOrderVo.setTenantName(iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getName());
        String scrStartTime = serviceRepairOrderVo.getScrStartTime();
        if (StringUtils.isNotBlank(scrStartTime)) {
            scrStartTime = scrStartTime + " 00:00:00";
            serviceRepairOrderVo.setScrStartTime(scrStartTime);
        }

        String scrEndTime = serviceRepairOrderVo.getScrEndTime();
        if (StringUtils.isNotBlank(scrEndTime)) {
            scrEndTime = scrEndTime + " 23:59:59";
            serviceRepairOrderVo.setScrEndTime(scrEndTime);
        }

        String repairStartTime = serviceRepairOrderVo.getRepairStartTime();
        if (StringUtils.isNotBlank(repairStartTime)) {
            repairStartTime = repairStartTime + " 00:00:00";
            serviceRepairOrderVo.setRepairStartTime(repairStartTime);
        }

        String repairEndTime = serviceRepairOrderVo.getRepairEndTime();
        if (StringUtils.isNotBlank(repairEndTime)) {
            repairEndTime = repairEndTime + " 23:59:59";
            serviceRepairOrderVo.setRepairEndTime(repairEndTime);
        }

        List<ServiceRepairOrderDto> serviceRepairOrderDtos = serviceRepairOrderMapper.doQueryOrderListExport(serviceRepairOrderVo);
        List busiIds = new ArrayList();
        for (ServiceRepairOrderDto record : serviceRepairOrderDtos) {

            VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(record.getCarNo());
            if (null != vehicleDataInfo) {
                String brandModel = vehicleDataInfo.getBrandModel() + "" + vehicleDataInfo.getVehicleModel();
                record.setBrandModel(StringUtils.isNotBlank(brandModel) ? brandModel : "");
            }

            int orderStatus = record.getOrderStatus();
            record.setOrderStatusName(iSysStaticDataService.getSysStaticData("ORDER_STATUS", orderStatus + "").getCodeName());
            String workType = record.getWorkType();
            record.setWorkTypeName(iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", workType + "").getCodeName());
            Long totalAmount = record.getTotalAmount();
            if (totalAmount != null && totalAmount.intValue() > 0) {
                record.setTotalAmount(Long.parseLong(CommonUtil.divide(totalAmount)));
            }
            String lastOrderMileage = record.getLastOrderMileage();
            record.setUpdValue(0);
            /**
             * 订单状态 1申请待审核 2申请不通过 3待接单 4工单待确认 5工单待审核 6工单不通过 7维保中  8完成待确认   11待支付 12已完成 13废弃
             *  14申请审核中 15工单审核中 16工单支付中。
             */
            if ("GHCWX".equals(workType) || orderStatus < 4 || orderStatus == 13) {
                record.setUpdValue(1);
            } else if (StringUtils.isBlank(lastOrderMileage)) {
                record.setUpdValue(2);
            }
            Long eid = record.getEid();
            busiIds.add(eid);
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"申请单号", "申请时间", "维保类型", "车牌号", "品牌型号",
                    "上单保养里程数", "司机姓名", "司机手机号", "申请人", "状态"};
            resourceFild = new String[]{"getOrderCode", "getCreateDate", "getWorkTypeName", "getCarNo", "getBrandModel",
                    "getLastOrderMileage", "getContractName", "getContractMobile", "getOpName", "getOrderStatusName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(serviceRepairOrderDtos, showName, resourceFild, ServiceRepairOrderDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "车辆维保信息.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public ServiceRepairOrderDetailDto getRepairOrderDetail(long flowId) {
        ServiceRepairOrderDetailDto dto = new ServiceRepairOrderDetailDto();

        if (flowId <= 0) {
            throw new BusinessException("缺少工单ID！");
        }
        ServiceRepairOrder repairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
        if (repairOrder == null) {
            throw new BusinessException("未找到工单信息！");
        }

        // 图片
        List<OaFiles> picList = oaFilesMapper.getRepairOrderPicList2(flowId, "6");
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (OaFiles file : picList) {
            if (StringUtils.isNotBlank(file.getFileUrl())) {
                try {
                    String tag = file.getFileUrl().substring(file.getFileUrl().lastIndexOf(".") + 1);
                    String url =  client.getHttpURL(file.getFileUrl()).split("\\?")[0];
                    file.setFileUrl(url.substring(0,url.lastIndexOf(".")) + "_big." + tag);
                    LOGGER.info("打印参数:"+(client.getHttpURL(file.getFileUrl()).split("\\?")[0]).substring(0,file.getFileUrl().indexOf("."))+ "_big." + tag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 维修项目
        List<ServiceRepairItems> items = serviceRepairItemsMapper.getRepairOrderItems(flowId);
        ServiceRepairItems serviceRepairItems = new ServiceRepairItems();
        serviceRepairItems.setItemName("自定义项目");
        serviceRepairItems.setItemId(0L);
        items.add(serviceRepairItems);
        long totalPartsPrice = 0; // 总的配件费
        long totalItemPrice = 0; // 总的人工费
        List<ServiceRepairParts> serviceRepairPartsList = serviceRepairPartsMapper.getRepairOrderParts(flowId);
        for (ServiceRepairParts serviceRepairParts : serviceRepairPartsList) {
            totalPartsPrice += (serviceRepairParts.getTotalPartsPrice() == null ? 0 : serviceRepairParts.getTotalPartsPrice());
        }
        for (ServiceRepairItems item : items) {
            //查询维修配件
            long itemPartsPirce = 0;
            double count = 0;

            for (ServiceRepairParts serviceRepairParts : serviceRepairPartsList) {
                if (null != serviceRepairParts.getOrderItemId() && serviceRepairParts.getOrderItemId().longValue() == item.getItemId().longValue()) {
                    itemPartsPirce += (serviceRepairParts.getTotalPartsPrice() == null ? 0 : serviceRepairParts.getTotalPartsPrice());
                    if (null != serviceRepairParts.getPartsNumber()) {
                        count += serviceRepairParts.getPartsNumber();
                    }
                }
            }
            totalItemPrice += item.getTotalItemPrice() == null ? 0 : item.getTotalItemPrice();
            item.setItemPartsPirce(itemPartsPirce);
            item.setItemPartsCount(count + "");
        }
        ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerMapper.getServiceRepairOrderVer(flowId);
        List<ServiceRepairItemsVer> itemsVer = serviceRepairItemsMapper.getRepairOrderItemsVer(flowId, serviceRepairOrderVer.getId());
        ServiceRepairItemsVer serviceRepairItemsVer = new ServiceRepairItemsVer();
        serviceRepairItemsVer.setItemName("自定义项目");
        serviceRepairItemsVer.setItemId(0L);
        itemsVer.add(serviceRepairItemsVer);
        long totalPartsPriceVer = 0;//总的配件费
        long totalItemPriceVer = 0;//总的人工费
        List<ServiceRepairPartsVer> serviceRepairPartsListVer = serviceRepairPartsVerMapper.getRepairOrderPartsVer(flowId, serviceRepairOrderVer.getId(), null);
        for (ServiceRepairPartsVer serviceRepairPartsVer : serviceRepairPartsListVer) {
            totalPartsPriceVer += (serviceRepairPartsVer.getTotalPartsPrice() == null ? 0 : serviceRepairPartsVer.getTotalPartsPrice());
        }
        for (ServiceRepairItemsVer item : itemsVer) {
            //查询维修配件
            long itemPartsPirce = 0;
            double count = 0;

            for (ServiceRepairPartsVer serviceRepairPartsVer : serviceRepairPartsListVer) {
                if (null != serviceRepairPartsVer.getOrderItemId() && serviceRepairPartsVer.getOrderItemId().longValue() == item.getItemId().longValue()) {
                    itemPartsPirce += (serviceRepairPartsVer.getTotalPartsPrice() == null ? 0 : serviceRepairPartsVer.getTotalPartsPrice());
                    if (null != serviceRepairPartsVer.getPartsNumber()) {
                        count += serviceRepairPartsVer.getPartsNumber();
                    }
                }
            }
            totalItemPriceVer += item.getTotalItemPrice() == null ? 0 : item.getTotalItemPrice();
            item.setItemPartsPirce(itemPartsPirce);
            item.setItemPartsCount(count + "");
        }

        List<Map> itemsList = disffServiceRepairItems(items, itemsVer);
        Long shopId = -1L;
        if (repairOrder.getShopId() != null) {
            shopId = repairOrder.getShopId();
        }
        Map map = getServicePorductInfo(shopId);
        if (map.get("serviceName") != null) {
            dto.setServiceName(map.get("serviceName").toString());
        }
        if (map.get("address") != null) {
            dto.setAddress(map.get("address").toString());
        }
        if (map.get("serviceCall") != null) {
            dto.setServiceCall(map.get("serviceCall").toString());
        }
        dto.setItemStr(repairOrder.getItems());
        repairOrder.setOrderStatusName(iSysStaticDataService.getSysStaticData("ORDER_STATUS", repairOrder.getOrderStatus() + "").getCodeName());
        repairOrder.setWorkTypeName(iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", repairOrder.getWorkType() + "").getCodeName());

        Long opId = repairOrder.getOpId();
        if (opId > 0) {
            SysUser sysOperator = iSysUserService.getById(opId);
            if (null != sysOperator) {
                repairOrder.setOpName(sysOperator.getName());
                repairOrder.setOpPhone(sysOperator.getBillId());
            }
        }
        dto.setTotalPartsPrice(CommonUtil.divide(totalPartsPrice));
        dto.setTotalItemPrice(CommonUtil.divide(totalItemPrice));
        dto.setTotalPartsPriceVer(CommonUtil.divide(totalPartsPriceVer));
        dto.setTotalItemPriceVer(CommonUtil.divide(totalItemPriceVer));
        dto.setTotalAmountVer(serviceRepairOrderVer.getTotalAmount());
        dto.setPicList(picList); //上传图片
        dto.setItems(itemsList); //维修项目

        //审核信息
//        dto.setSysOperLogList(querySysOperLogAll(300005, flowId, false));

        VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(serviceRepairOrderVer.getCarNo());
        String brandModel = "";
        if (null != vehicleDataInfo) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dto.setVehicleCode(vehicleDataInfo.getPlateNumber());
            dto.setMaintenanceDis(vehicleDataInfo.getMaintenanceDis());
            if (vehicleDataInfo.getLastMaintenanceDate() != null) {
                dto.setLastMaintenanceDate(df.format(vehicleDataInfo.getLastMaintenanceDate()));
            }
            brandModel = vehicleDataInfo.getBrandModel() + "" + vehicleDataInfo.getVehicleModel();
        }
        //核算金额
        Double checkAmount = null;
        if (repairOrder.getWorkType() != null) {
            /***
             * 工单类型，’GHCWX’-维修，’GHCBY’-保养
             */
            if ("GHCBY".equals(repairOrder.getWorkType())) {
                String desc = "";
                if (repairOrder.getOrderStatus() != null
                        && repairOrder.getOrderStatus().intValue() == 5
                        && StringUtils.isBlank(repairOrder.getCarMileage()) && serviceRepairOrderVer != null &&
                        StringUtils.isBlank(serviceRepairOrderVer.getCarMileage())
                ) {
                    desc = "该订单的本单里程数不能为空， 请门店修改后操作";
                }
                dto.setDesc(desc);
                //车辆品牌
                if (StringUtils.isNotBlank(repairOrder.getBrandModel())) {
                    brandModel = repairOrder.getBrandModel() + "" + repairOrder.getVehicleModel();
                }
                //品牌单价
                Double brandPrice = null;
                if (repairOrder.getBrandPrice() != null && repairOrder.getBrandPrice() >= 0) {
                    brandPrice = repairOrder.getBrandPrice();
                }
                dto.setBrandPrice(brandPrice);
                if (repairOrder.getCheckAmount() != null && repairOrder.getCheckAmount() >= 0) {
                    checkAmount = repairOrder.getCheckAmount();
                } else {
                    if (brandPrice != null
                            && StringUtils.isNotBlank(repairOrder.getCarMileage())
                            && StringUtils.isNotBlank(repairOrder.getLastOrderMileage())) {
                        // 配置上的单价 *（本单里程数-上单保养里程数）
                        checkAmount = brandPrice *
                                (Double.parseDouble(repairOrder.getCarMileage())
                                        - Double.parseDouble(repairOrder.getLastOrderMileage()));
                        checkAmount = checkAmount < 0 ? 0 : CommonUtil.getDoubleFormat(checkAmount, 2);
                        ;
                    }
                }
            } else if ("GHCWX".equals(repairOrder.getWorkType())) {
                checkAmount = repairOrder.getTotalAmount() != null ?
                        CommonUtil.getDoubleFormatLongMoney(repairOrder.getTotalAmount(), 2) : null;
            }
        }
        dto.setBrandModel(brandModel);
        dto.setCheckAmount(checkAmount);
        dto.setServiceRepairOrder(repairOrder);
        return dto;
    }

    @Override
    public ServiceRepairOrderDetailVerDto getRepairOrderDetailVer(long flowId) {
        ServiceRepairOrderDetailVerDto dto = null;
        try {
            dto = new ServiceRepairOrderDetailVerDto();
            ServiceRepairOrder serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
            if (null != serviceRepairOrder) {
                serviceRepairOrder.setOrderStatusName(iSysStaticDataService.getSysStaticData("ORDER_STATUS", serviceRepairOrder.getOrderStatus() + "").getCodeName());
                ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerMapper.getServiceRepairOrderVer(flowId);
                List<ServiceRepairItemsVer> serviceRepairItemsVerList = serviceRepairItemsMapper.getRepairOrderItemsVer(flowId, serviceRepairOrderVer.getId());
                List<ServiceRepairItemsVer> rtnServiceRepairItemsList = new ArrayList<>();
                List<ServiceRepairPartsVer> serviceRepairPartsListVer = serviceRepairPartsVerMapper.getRepairOrderPartsVer(flowId, serviceRepairOrderVer.getId(), null);

                ServiceRepairItemsVer serviceRepairItemsVer = new ServiceRepairItemsVer();
                serviceRepairItemsVer.setItemName("自定义项目");
                serviceRepairItemsVer.setItemId(0L);
                serviceRepairItemsVer.setItemPartsCount("0");
                serviceRepairItemsVer.setItemManHour(0.0);
                serviceRepairItemsVer.setRepairOrderId(flowId);
                serviceRepairItemsVerList.add(serviceRepairItemsVer);

                for (int i = 0; i < serviceRepairItemsVerList.size(); i++) {
                    ServiceRepairItemsVer itVer = serviceRepairItemsVerList.get(i);
                    double count = 0;
                    Long totalItemPirce = itVer.getTotalItemPrice() == null ? 0 : itVer.getTotalItemPrice();
                    for (ServiceRepairPartsVer serviceRepairPartsVer : serviceRepairPartsListVer) {
                        if (null != serviceRepairPartsVer.getOrderItemId() && serviceRepairPartsVer.getOrderItemId().longValue() == itVer.getItemId().longValue()) {
                            if (null != serviceRepairPartsVer.getPartsNumber()) {
                                count += serviceRepairPartsVer.getPartsNumber();
                                totalItemPirce += serviceRepairPartsVer.getTotalPartsPrice();
                            }
                        }
                    }
                    itVer.setItemPartsCount(count + "");
                    ServiceRepairItemsVer temp = new ServiceRepairItemsVer();
                    BeanUtils.copyProperties(temp, itVer);
                    temp.setTotalItemPrice(totalItemPirce);
                    rtnServiceRepairItemsList.add(temp);
                }

                if ("0".equals(serviceRepairItemsVer.getItemPartsCount())) {
                    rtnServiceRepairItemsList.remove(serviceRepairItemsVer);
                }

                dto.setServiceRepairItemsVerList(rtnServiceRepairItemsList);
                dto.setTenantId(serviceRepairOrder.getTenantId());
                Map<String, Object> map = convertBean2Map(serviceRepairOrder);
                map.put("totalAmount", serviceRepairOrderVer.getTotalAmount());
                map.put("workTypeName", iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", serviceRepairOrder.getWorkType() + "").getCodeName());
                map.put("itemCount", serviceRepairItemsVerList.size());
                map.put("tenantId", serviceRepairOrder.getTenantId());
                dto.setServiceRepairOrderVer(map);
                long shopId = serviceRepairOrderVer.getShopId() == null ? -1L : serviceRepairOrderVer.getShopId();
                Map serviceMap = getServicePorductInfo(shopId);
                serviceMap.put("shopName", DataFormat.getStringKey(map, "shopName"));
                dto.setShopInfo(serviceMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Override
    public ServiceRepairOrderUpdateEchoDto getServiceRepairOrder(Long flowId) {
        ServiceRepairOrderUpdateEchoDto dto = null;
        try {
            dto = new ServiceRepairOrderUpdateEchoDto();
            if (flowId == null || flowId <= 0) {
                throw new BusinessException("缺少工单ID！");
            }

            // 查询维修保养订单
            ServiceRepairOrder serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
            org.apache.commons.beanutils.BeanUtils.copyProperties(dto, serviceRepairOrder);
            dto.setWorkTypeName(iSysStaticDataService.getSysStaticData("REPAIR_WORK_TYPE", dto.getWorkType() + "").getCodeName());
//            SysUser sysOperator = iSysUserService.getBaseMapper().selectById(serviceRepairOrder.getOpId());
//            if (null != sysOperator) {
//                dto.setOpName(sysOperator.getName());
//                dto.setOpPhone(sysOperator.getBillId());
//            }

            // 通过车牌号查询司机信息
            VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(serviceRepairOrder.getCarNo());
            if (null != vehicleDataInfo) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dto.setVehicleCode(vehicleDataInfo.getId());
                dto.setMaintenanceDis(vehicleDataInfo.getMaintenanceDis());
                if (vehicleDataInfo.getLastMaintenanceDate() != null) {
                    dto.setLastMaintenanceDate(df.format(vehicleDataInfo.getLastMaintenanceDate()));
                }
            }

            // 通过订单号查询关联文件
            List<OaFiles> repairOrderPicList = oaFilesMapper.getRepairOrderPicList(flowId);
            List<Map> repairOrderPicMapList = new ArrayList<>();
            FastDFSHelper client = FastDFSHelper.getInstance();
            for (int i = 0; i < repairOrderPicList.size(); i++) {
                OaFiles oa = repairOrderPicList.get(i);
                Map map = new HashMap();
                map.put("fileUrl", client.getHttpURL(oa.getFileUrl()).split("\\?")[0]);
                map.put("fileId", oa.getFileId());
                repairOrderPicMapList.add(map);
            }
            dto.setRepairOrderPicList(repairOrderPicMapList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dto;
    }

    @Transactional
    @Override
    public void doSaveOrUpdateServiceRepairOrder(ServiceRepairOrderIUVo serviceRepairOrderIUVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String contractName = serviceRepairOrderIUVo.getContractName();
        if (StringUtils.isBlank(contractName)) {
            throw new BusinessException("司机姓名不能为空");
        }

        String contractMobile = serviceRepairOrderIUVo.getContractMobile();
        if (StringUtils.isBlank(contractMobile)) {
            throw new BusinessException("司机手机号不能为空");
        }

        String carNo = serviceRepairOrderIUVo.getCarNo();
        Long vehicleCode = serviceRepairOrderIUVo.getVehicleCode();
        if (vehicleCode == null && vehicleCode < 0) {
            throw new BusinessException("车牌号不能为空");
        }

        String workType = serviceRepairOrderIUVo.getWorkType().trim();

        if (StringUtils.isBlank(workType)) {
            throw new BusinessException("工单类型不能为空");
        }
        if ("GHCBY".equals(workType)) {
            String scrStartTime = serviceRepairOrderIUVo.getScrStartTime();
            String scrEndTime = serviceRepairOrderIUVo.getScrEndTime();
            if (null == scrStartTime || null == scrEndTime) {
                throw new BusinessException("请选择计划起始时间");
            }
            String items = serviceRepairOrderIUVo.getItems();
            if (StringUtils.isBlank(items)) {
                throw new BusinessException("至少选择一个保养项目");
            }
        }

        // 自有公司车 1 *招商挂靠车 2 *临时外调车 3 *外来挂靠车 4 *外调合同车 5
        TenantVehicleRel tenantVehicleRel = new TenantVehicleRel();
        List<TenantVehicleRel> list = tenantVehicleRelMapper.getZYVehicleByVehicleCode(vehicleCode, SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1);
        if (list.size() == 0) {
            throw new BusinessException("该功能暂时只对自有车开放");
        }
        tenantVehicleRel = list.get(0);
        if (StringUtils.isBlank(carNo)) {
            serviceRepairOrderIUVo.setCarNo(vehicleDataInfoMapper.selectPlateNumberById(vehicleCode));
        }

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Long flowId = serviceRepairOrderIUVo.getId();
        ServiceRepairOrder serviceRepairOrder = new ServiceRepairOrder();
        ServiceRepairOrderVer serviceRepairOrderVer = new ServiceRepairOrderVer();
        if (flowId != null) { // 修改
            serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
            // 订单状态 1申请待审核 2申请不通过 3待接单 4工单待确认 5工单待审核 6工单不通过 7维保中  8完成待确认
            // 11待支付 12已完成 13废弃 14申请审核中 15工单审核中 16工单支付中
            if (serviceRepairOrder.getOrderStatus() != 1 && serviceRepairOrder.getOrderStatus() != 2) {
                throw new BusinessException("工单信息不允许修改");
            }
            Map map = JSON.parseObject(JSON.toJSONString(serviceRepairOrderIUVo), Map.class);
            BeanUtil.copyProperties(map, serviceRepairOrder);
            serviceRepairOrder.setTenantId(loginInfo.getTenantId());
            serviceRepairOrder.setUserId(tenantVehicleRel.getDriverUserId());
            serviceRepairOrder.setOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateDate(df.format(date));
            serviceRepairOrder.setOrderStatus(1);
            serviceRepairOrderMapper.updateById(serviceRepairOrder); // 修改

            BeanUtil.copyProperties(serviceRepairOrder, serviceRepairOrderVer);
            serviceRepairOrderVer.setFlowId(serviceRepairOrder.getId());
            serviceRepairOrderVerMapper.updateServiceRepairOrderVerByFlowId(serviceRepairOrderVer);

            saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Update, "修改工单信息", accessToken, flowId);
        } else {
            serviceRepairOrder = new ServiceRepairOrder();
            Map map = JSON.parseObject(JSON.toJSONString(serviceRepairOrderIUVo), Map.class);
            BeanUtil.copyProperties(map, serviceRepairOrder);

            serviceRepairOrder.setCreateDate(df.format(date));
            serviceRepairOrder.setTenantId(loginInfo.getTenantId());
            serviceRepairOrder.setUserId(tenantVehicleRel.getDriverUserId());
            serviceRepairOrder.setOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateOpId(loginInfo.getId());
            serviceRepairOrder.setUpdateDate(df.format(date));
            serviceRepairOrder.setOrderStatus(1);
            serviceRepairOrderMapper.insertServiceRepairOrder(serviceRepairOrder); // 新增
            // 工单类型，’GHCWX’-维修，’GHCBY’-保养
            if ("GHCWX".equals(workType)) {
                serviceRepairOrder.setOrderCode("WX" + String.format("%08d", serviceRepairOrder.getId()));
            } else if ("GHCBY".equals(workType)) {
                serviceRepairOrder.setOrderCode("BY" + String.format("%08d", serviceRepairOrder.getId()));
            }
            // 添加工单号（内）code
            serviceRepairOrderMapper.updateServiceRepairOrderById(serviceRepairOrder);

            BeanUtil.copyProperties(serviceRepairOrder, serviceRepairOrderVer);
            serviceRepairOrderVer.setFlowId(serviceRepairOrder.getId());
            serviceRepairOrderVerMapper.insertServiceRepairOrderVer(serviceRepairOrderVer);

            saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Add, "发起工单", accessToken, serviceRepairOrder.getId());
        }

        String picFileIds = serviceRepairOrderIUVo.getPicFileIds();
        //处理图片
        try {
            doSavePicFiles(serviceRepairOrderVer.getFlowId(), picFileIds, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map inMap = new HashMap();
        inMap.put("svName", "serviceRepairOrderTF");

        boolean bool = iAuditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_REPAIR_INFO, serviceRepairOrder.getId(), SysOperLogConst.BusiCode.serviceRepair, inMap, accessToken);
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }

    }

    @Transactional
    @Override
    public void doCancel(long flowId, String accessToken) {
        if (flowId <= 0) {
            throw new BusinessException("缺少工单ID！");
        }
        ServiceRepairOrder serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
        if (null == serviceRepairOrder) {
            throw new BusinessException("未找到工单信息");
        }
        if (serviceRepairOrder.getOrderStatus() > 3) { // 3 待接单
            throw new BusinessException("工单不允许取消");
        }
        // 1申请待审核   5工单待审核
        if (null != serviceRepairOrder.getOrderStatus() && (serviceRepairOrder.getOrderStatus() == 1 || serviceRepairOrder.getOrderStatus() == 5)) {
            //取消审核流程
            try {
                cancelProcess("400008", serviceRepairOrder.getId(), serviceRepairOrder.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        serviceRepairOrder.setOrderStatus(13); // 13废弃
        if (null != serviceRepairOrder.getId() && serviceRepairOrder.getId() > 0) {
            serviceRepairOrderMapper.updateOrderStatusById(serviceRepairOrder);
        } else {
            serviceRepairOrderMapper.insert(serviceRepairOrder);
        }

        saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Update, "工单废弃", accessToken, flowId);
    }

    @Transactional
    @Override
    public void doCancelWX(long flowId, String accessToken) {
        if (flowId <= 0) {
            throw new BusinessException("缺少工单ID！");
        }

        // 查询维修保养工单
        ServiceRepairOrder serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
        if (null == serviceRepairOrder) {
            throw new BusinessException("未找到工单信息");
        }
        if (serviceRepairOrder.getOrderStatus() > 3) { // 3 待接单
            throw new BusinessException("工单不允许取消");
        }
        // 1申请待审核   5工单待审核
        if (null != serviceRepairOrder.getOrderStatus() && (serviceRepairOrder.getOrderStatus() == 1 || serviceRepairOrder.getOrderStatus() == 5)) {
            //取消审核流程
            try {
                cancelProcess("400008", serviceRepairOrder.getId(), serviceRepairOrder.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        serviceRepairOrder.setOrderStatus(13); // 13废弃
        if (null != serviceRepairOrder.getId() && serviceRepairOrder.getId() > 0) {
            serviceRepairOrderMapper.updateOrderStatusById(serviceRepairOrder);
        } else {
            serviceRepairOrderMapper.insert(serviceRepairOrder);
        }

        saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Update, "工单废弃", accessToken, flowId);
        execPushRepairOrder(flowId);
    }

    private void execPushRepairOrder(Long flowId) {
        List<Long> tenantList = CommonUtil.strToList(PUSH_REPAIR_ORDER_TENANT_IDS,"long");
        LambdaQueryWrapper<ServiceRepairOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceRepairOrder::getId, flowId);
        queryWrapper.in(ServiceRepairOrder::getTenantId, tenantList);
        ServiceRepairOrder serviceRepairOrder = baseMapper.selectOne(queryWrapper);

        if (null == serviceRepairOrder) {
            return ;
        }

        List<Long> flowIdList = Lists.newArrayList(flowId);
        List<ServiceRepairItems>  serviceRepairItemsList = serviceRepairItemsService.getRepairOrderItems(flowIdList);
        List<ServiceRepairParts> serviceRepairPartsList = serviceRepairPartsService.getRepairOrderParts(flowIdList);
        List<OaFiles> oaFilesList = iOaFilesService.getRepairOrderPicList(flowIdList);
        Map repairOrderDetail = getRepairOrderDetail(flowIdList, serviceRepairItemsList, serviceRepairPartsList, oaFilesList);
        doPush(serviceRepairOrder,repairOrderDetail);
    }

    @Override
    public String getTotalFee(ServiceRepairOrderVo serviceRepairOrderVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        serviceRepairOrderVo.setTenantId(loginInfo.getTenantId());
        serviceRepairOrderVo.setTenantName(iSysTenantDefService.getSysTenantDef(loginInfo.getTenantId()).getName());
        String scrStartTime = serviceRepairOrderVo.getScrStartTime();
        if (StringUtils.isNotBlank(scrStartTime)) {
            scrStartTime = scrStartTime + " 00:00:00";
            serviceRepairOrderVo.setScrStartTime(scrStartTime);
        }

        String scrEndTime = serviceRepairOrderVo.getScrEndTime();
        if (StringUtils.isNotBlank(scrEndTime)) {
            scrEndTime = scrEndTime + " 23:59:59";
            serviceRepairOrderVo.setScrEndTime(scrEndTime);
        }

        String repairStartTime = serviceRepairOrderVo.getRepairStartTime();
        if (StringUtils.isNotBlank(repairStartTime)) {
            repairStartTime = repairStartTime + " 00:00:00";
            serviceRepairOrderVo.setRepairStartTime(repairStartTime);
        }

        String repairEndTime = serviceRepairOrderVo.getRepairEndTime();
        if (StringUtils.isNotBlank(repairEndTime)) {
            repairEndTime = repairEndTime + " 23:59:59";
            serviceRepairOrderVo.setRepairEndTime(repairEndTime);
        }
        String totalFee = "0.00";
        Double res = serviceRepairOrderMapper.getTotalFee(serviceRepairOrderVo);
        if (res != null) {
            DecimalFormat df = new DecimalFormat("#.##");

            totalFee = df.format(res);
        }
        return totalFee;
    }

    @Transactional
    @Override
    public void doSaveLastOrderMileage(Long flowId, String lastOrderMileage) {
        ServiceRepairOrder serviceRepairOrder = serviceRepairOrderMapper.selectRecordById(flowId);
        if (null == serviceRepairOrder) {
            throw new BusinessException("工单不存在");
        }
        serviceRepairOrder.setLastOrderMileage(lastOrderMileage);

        // 修改上单里程数
        serviceRepairOrderMapper.updateLastOrderMileageById(serviceRepairOrder);
    }


    public static List<Map> disffServiceRepairItems(List<ServiceRepairItems> items, List<ServiceRepairItemsVer> itemsVer) {
        Map itemsMap = new HashMap();
        for (int i = 0; i < items.size(); i++) {
            ServiceRepairItems item = items.get(i);
            itemsMap.put(item.getItemId(), item);
        }

        Map itemsVerMap = new HashMap();

        List<Map> rtnList = new ArrayList<>();
        for (int i = 0; i < itemsVer.size(); i++) {
            Map rtnMap = new HashMap();
            ServiceRepairItemsVer itemVer = itemsVer.get(i);
            itemsVerMap.put(itemVer.getItemId(), itemVer);
            if (itemsMap.containsKey(itemVer.getItemId())) {
                rtnMap.put("item", itemsMap.get(itemVer.getItemId()));
                rtnMap.put("itemVer", itemVer);
                rtnMap.put("oper", "update");
                rtnMap.put("operName", "");
            } else {
                rtnMap.put("item", itemVer);
                rtnMap.put("oper", "add");
                rtnMap.put("operName", "增");
            }
            rtnList.add(rtnMap);
        }

        for (int i = 0; i < items.size(); i++) {
            Map rtnMap = new HashMap();
            ServiceRepairItems item = items.get(i);
            if (!itemsVerMap.containsKey(item.getItemId())) {
                rtnMap.put("item", item);
                rtnMap.put("oper", "delete");
                rtnMap.put("operName", "删");
                rtnList.add(rtnMap);
            }
        }

        return rtnList;

    }

    private Map getServicePorductInfo(long shopId) {
        if (shopId < 0) {
            return new HashMap();
        }
        Map rtnMap = new HashMap();
        ServiceProduct serviceProduct = serviceRepairItemsMapper.getServiceProductByStationId(shopId + "", 3);
        if (null != serviceProduct) {
            ServiceInfo serviceInfo = serviceInfoMapper.getObjectById(serviceProduct.getServiceUserId());
            if (null != serviceInfo) {
                rtnMap.put("serviceName", serviceInfo.getServiceName());
            }
            rtnMap.put("address", serviceProduct.getAddress());
            rtnMap.put("serviceCall", serviceProduct.getServiceCall());
        }
        return rtnMap;
    }
    @Override
    public void doSavePicFiles(long flowId, String picFileIds, String file) {
        if (StringUtils.isBlank(picFileIds)) {
            return;
        }
        if (flowId > 0) {
            oaFilesMapper.deletePicFileIds(flowId, 6, file);
        }
        List<Long> fileList = CommonUtil.strToList(picFileIds, "long");
        if (null != picFileIds && picFileIds.length() > 0) {
            List<SysAttach> sysAttachList = sysAttachMapper.selectBatchIds(fileList);
            for (SysAttach sys : sysAttachList) {
                OaFiles oa = new OaFiles();
                oa.setRelId(flowId);
                oa.setRelType(6);
                oa.setFileName(sys.getFileName());
                oa.setFileId(sys.getId());
                oa.setFileUrl(sys.getStorePath());
                oa.setOpId(sys.getOpId());
                oa.setOpDate(sys.getCreateTime().toString());
                oa.setTenantId(-1L);
                oaFilesMapper.insertRecord(oa);
            }
        }
    }

    @Override
    public void cancelProcess(String auditCode, Long busiId, Long tenantId) throws InvocationTargetException, IllegalAccessException {
        AuditNodeInst preAuditNodeInst = auditNodeInstService.queryAuditIng(auditCode, busiId, tenantId);
        if (preAuditNodeInst != null) {
            //审核流程-已完成
            preAuditNodeInst.setStatus(AuditConsts.Status.FINISH);
            //审核流程-取消，发起了新的审核
            preAuditNodeInst.setAuditResult(AuditConsts.RESULT.CANCEL);
            auditNodeInstService.save(preAuditNodeInst);
//            auditNodeInstMapper.insert(preAuditNodeInst);

            //把该流程的状态都改为流程完成
            auditNodeInstService.updateAuditInstToFinish(preAuditNodeInst.getAuditId(), busiId);
            //移除到历史表
            List<AuditNodeInst> auditNodeInsts = auditNodeInstService.selectByBusiCodeAndBusiIdAndTenantIdAntStatus(
                    preAuditNodeInst.getAuditCode(), preAuditNodeInst.getBusiId(), preAuditNodeInst.getTenantId(),
                    AuditConsts.Status.FINISH);
            if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
                for (AuditNodeInst auditNodeInst : auditNodeInsts) {
                    AuditNodeInstVer bean = new AuditNodeInstVer();
                    BeanUtils.copyProperties(bean, auditNodeInst);
                    auditNodeInstVerService.save(bean);
                    auditNodeInstService.removeById(auditNodeInst.getId());
//                    auditNodeInstService.getBaseMapper().deleteByMap(JSONObject.parseObject(JSONObject.toJSONString(auditNodeInst), Map.class));
                }
            }
        }
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String accessToken) {
        ServiceRepairOrder serviceRepairOrder = super.getById(busiId);
        ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerMapper.getServiceRepairOrderVer(busiId);
        serviceRepairOrder.setAuditRemark(desc);
        // ShenQiTF shenQiTF = (ShenQiTF) SysContexts.getBean("shenQiTF");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        int state = getOrderStatus(SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, serviceRepairOrder.getOrderStatus());
        //将工单报价确认信息推送给神汽
        if (state == REPAIR_ORDER_STATUS7) {
            // int isJumpSQ = Integer.parseInt((String)SysCfgUtil.getCfgVal("IS_JUMP_SQ", 0, String.class));
            //确认之前先查一下工单是否已经确认过
            //   if(isJumpSQ != 1){
//                Map<String,Object> shRtnOrderMap = shenQiTF.queryGhcWorkOrder(serviceRepairOrder.getOrderSn());
//                String workStatus = DataFormat.getStringKey(shRtnOrderMap,"workStatus");
//                if(DSH.equals(workStatus)){
//                    Map<String,Object> shRtnMap = shenQiTF.workOrderDoAction(serviceRepairOrder.getOrderSn(), QUOTE,desc);
//                    serviceRepairOrderVer.setWorkStatus(DataFormat.getStringKey(shRtnMap,"workStatus"));
//                }
//        }
        //生成维修保养开始时间
        if (null == serviceRepairOrder.getRepairStartTime()) {
            serviceRepairOrder.setRepairStartTime(df.format(date));
            serviceRepairOrderVer.setRepairStartTime(df.format(date));
        }

        //该订单的{本单里程数,上单保养里程数,品牌类型, 品牌单价}不能为空，只能做驳回操作，请对应修改后操作
        boolean isCheckPass = true;
        StringBuffer msg = new StringBuffer("该订单的{");
        if (StringUtils.isBlank(serviceRepairOrder.getCarMileage()) && StringUtils.isBlank(serviceRepairOrderVer.getCarMileage())) {
            msg.append("本单里程 ");
            isCheckPass = false;
        }
        if (StringUtils.isBlank(serviceRepairOrder.getLastOrderMileage())) {
            msg.append("上单保养里程 ");
            isCheckPass = false;
        }
        VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(serviceRepairOrderVer.getCarNo());
        String brandModel = "";
        if (null != vehicleDataInfo) {
            brandModel = vehicleDataInfo.getBrandModel();
        }
        if (StringUtils.isNotBlank(serviceRepairOrder.getBrandModel())) {
            brandModel = serviceRepairOrder.getBrandModel();
        }
        if (StringUtils.isBlank(brandModel)) {
            msg.append("车辆品牌类型 ");
            isCheckPass = false;
        } else {
            Double brandPrice = null;
            if (serviceRepairOrder.getBrandPrice() != null && serviceRepairOrder.getBrandPrice() >= 0) {
                brandPrice = serviceRepairOrder.getBrandPrice();
            } else {
                String brandPriceStr = basicConfigurationService.getBasicCodeName("REPAIR_BRAND_PRICE", brandModel,accessToken);
                brandPrice = StringUtils.isNotBlank(brandPriceStr) ? Double.parseDouble(brandPriceStr) : null;
            }
            if (brandPrice == null) {
                msg.append("品牌单价 ");
                isCheckPass = false;
            }
        }
        if (!ServiceConsts.REPAIR_WORK_TYPE.GHCBY.equals(serviceRepairOrder.getWorkType())) {
            isCheckPass = true;
        }
        if (!isCheckPass) {
            throw new BusinessException(msg + "}不能为空，只能做驳回操作，请对应修改后操作");
        }
        serviceRepairOrder.setBrandModel(brandModel);
        //品牌单价
        String brandPriceStr = basicConfigurationService.getBasicCodeName("REPAIR_BRAND_PRICE", brandModel,accessToken);
        Double brandPrice = StringUtils.isNotBlank(brandPriceStr) ? Double.parseDouble(brandPriceStr) : null;
        serviceRepairOrder.setBrandPrice(brandPrice);


        //核算金额
        Double checkAmount = null;
        if (ServiceConsts.REPAIR_WORK_TYPE.GHCBY.equals(serviceRepairOrder.getWorkType())) {
            if (brandPrice != null
                    && StringUtils.isNotBlank(serviceRepairOrderVer.getCarMileage())
                    && StringUtils.isNotBlank(serviceRepairOrder.getLastOrderMileage())) {
                // 配置上的单价 *（本单里程数-上单保养里程数）
                checkAmount = brandPrice *
                        (Double.parseDouble(serviceRepairOrderVer.getCarMileage())
                                - Double.parseDouble(serviceRepairOrder.getLastOrderMileage()));
                checkAmount = checkAmount < 0 ? 0 : CommonUtil.getDoubleFormat(checkAmount, 2);
            }
        } else if (ServiceConsts.REPAIR_WORK_TYPE.GHCWX.equals(serviceRepairOrder.getWorkType())) {
            checkAmount = serviceRepairOrderVer.getTotalAmount() != null ?
                    CommonUtil.getDoubleFormatLongMoney(serviceRepairOrderVer.getTotalAmount(), 2) : null;
        }
        serviceRepairOrder.setCheckAmount(checkAmount);
        //SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid
        saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Update, "通知维保人员开始施工", accessToken, serviceRepairOrder.getId());
        if (null == serviceRepairOrder.getPushState() || serviceRepairOrder.getPushState() != REPAIR_ORDER_STATUS7) {
            //设置推送状态 需要将工单信息推送给跨越
            serviceRepairOrderVer.setDiffFlg(1);
            redisUtil.lpush("SERVICE_REPAIR_ORDER", serviceRepairOrder.getId() + "");
        }
    }else if(state ==ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS3)

    {
        redisUtil.lpush("SERVICE_REPAIR_ORDER", serviceRepairOrder.getId() + "");
    }

   // IServiceRepairOrderTF serviceRepairOrderTF = (IServiceRepairOrderTF) SysContexts.getBean("serviceRepairOrderTF");
        flushVerData(serviceRepairOrder,serviceRepairOrderVer,state);
        serviceRepairOrder.setOrderStatus(state);
    //设置审核时间
        serviceRepairOrder.setUpdateDate(df.format(date));
        serviceRepairOrderVerService.saveOrUpdate(serviceRepairOrderVer);
        super.saveOrUpdate(serviceRepairOrder);

}

    @Override
    public void fail(Long busiId, String desc, Map paramsMap) {
        ServiceRepairOrder serviceRepairOrder = super.getById(busiId);
        serviceRepairOrder.setAuditRemark(desc);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        serviceRepairOrder.setOrderStatus(getOrderStatus(SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, serviceRepairOrder.getOrderStatus()));
        //将工单报价拒绝信息推送给神汽
        if (serviceRepairOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS6) {
            int isJumpSQ = Integer.parseInt((String) sysCfgService.getCfgVal("IS_JUMP_SQ", 0, String.class));
            if (isJumpSQ != 1) {
                //神汽验证
                //Map<String,Object> shRtnMap =  shenQiTF.workOrderDoAction(serviceRepairOrder.getOrderSn(), REFUSE,desc);
            }
        }

        //设置审核时间
        serviceRepairOrder.setUpdateDate(df.format(date));
        serviceRepairOrder.setUpdateTime(LocalDateTime.now());
        super.saveOrUpdate(serviceRepairOrder);
    }

    @Override
    public void auditingCallBack(Long busiId) {
        ServiceRepairOrder serviceRepairOrder = super.getById(busiId);
        if (ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1 == serviceRepairOrder.getOrderStatus()) {
            serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS14);
        } else if (ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS5 == serviceRepairOrder.getOrderStatus()) {
            serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS15);
        }
        super.saveOrUpdate(serviceRepairOrder);
    }

    private int getOrderStatus(int authState, int orderStatus) {
        if (authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3) {
            if (orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1) {
                return ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS2;
            } else if (orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS5) {
                return ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS6;
            }
        } else if (authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2) {
            if (orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS1 || orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS14) {
                //产品要求审核通过状态改为已完成
                return ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS12;
            } else if (orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS5 || orderStatus == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS15) {
                return REPAIR_ORDER_STATUS7;
            }
        }
        return 0;
    }

    @Override
    public void flushVerData(ServiceRepairOrder serviceRepairOrder,ServiceRepairOrderVer serviceRepairOrderVer,int state) {
        BeanUtil.copyProperties(serviceRepairOrderVer,serviceRepairOrder);
        serviceRepairOrder.setId(serviceRepairOrderVer.getFlowId());
        serviceRepairOrder.setOrderStatus(state);
        //刷新项目工时和配置项
        deleteServiceRepairOrderItems(serviceRepairOrder.getId());
        List<ServiceRepairItemsVer> serviceRepairItemsVerList = serviceRepairItemsMapper.getRepairOrderItemsVer(serviceRepairOrderVer.getFlowId(),serviceRepairOrderVer.getId());
        for (int i = 0; i < serviceRepairItemsVerList.size(); i++) {
            ServiceRepairItemsVer serviceRepairItemsVer = serviceRepairItemsVerList.get(i);
            serviceRepairItemsVer.setId(null);
            ServiceRepairItems serviceRepairItems = new ServiceRepairItems();
            BeanUtil.copyProperties(serviceRepairItemsVer,serviceRepairItems);
            serviceRepairItemsService.saveOrUpdate(serviceRepairItems);
            serviceRepairItemsVer.setId(serviceRepairItems.getId());
            serviceRepairItemsMapper.updataRepairItemsVer(serviceRepairItemsVer.getHisId(),serviceRepairItems.getId());
        }
        List<ServiceRepairPartsVer> serviceRepairPartsVerList = serviceRepairPartsVerMapper.getRepairOrderPartsVer(serviceRepairOrderVer.getFlowId(),serviceRepairOrderVer.getId(),null);
        for (int i = 0; i < serviceRepairPartsVerList.size(); i++) {
            ServiceRepairPartsVer serviceRepairPartsVer = serviceRepairPartsVerList.get(i);
            serviceRepairPartsVer.setId(null);
            ServiceRepairParts serviceRepairParts = new ServiceRepairParts();
            BeanUtil.copyProperties(serviceRepairPartsVer,serviceRepairParts);
            serviceRepairPartsService.saveOrUpdate(serviceRepairParts);
            serviceRepairPartsVer.setId(serviceRepairParts.getId());
            serviceRepairPartsVerMapper.updateRepairPartsVer(serviceRepairPartsVer.getHisId(),serviceRepairParts.getId());
        }
    }

    @Override
    public void deleteServiceRepairOrderItems(long flowId){
        serviceRepairItemsMapper.delRepairItems(flowId);
        serviceRepairItemsMapper.delRepairParts(flowId);
    }

    @Override
    public void payRepairFeeCallBack(String orderCode, String accessToken) {
        if (StringUtils.isEmpty(orderCode)) {
            throw new BusinessException("维修单号不能为空");
        }
        this.payOrderCall(orderCode, accessToken);
    }

    @Override
    public List<SysStaticData> getStaticDataOption(String codeType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return iSysStaticDataService.getSysStaticDataList(codeType,loginInfo.getUserInfoId());
    }

    @Override
    public void payOrderCall(String orderCode, String accessToken) {
        if (StringUtils.isBlank(orderCode)) {
            return;
        }
        ServiceRepairOrder serviceRepairOrder = this.getOrderByOrderCode(orderCode);
        serviceRepairOrder.setOrderStatus(REPAIR_ORDER_STATUS12);
        serviceRepairOrder.setWorkStatus(YWC);
        serviceRepairOrder.setOrderEndTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        this.saveOrUpdate(serviceRepairOrder);
        saveSysOperLog(SysOperLogConst.BusiCode.serviceRepair, SysOperLogConst.OperType.Update,
                "支付成功,工单结束", accessToken, serviceRepairOrder.getId());
        this.execution(serviceRepairOrder.getId() + "",accessToken);
    }

    @Override
    public ServiceRepairOrder getOrderByOrderCode(String orderSn) {
        LambdaQueryWrapper<ServiceRepairOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceRepairOrder::getOrderSn, orderSn);
        List<ServiceRepairOrder> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new ServiceRepairOrder();
    }

    @Override
    public Integer doQueryServiceRepairOrderCount(List<Long> flowIds, List<Long> tenantList) {
        LambdaQueryWrapper<ServiceRepairOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ServiceRepairOrder::getId, flowIds);
        queryWrapper.in(ServiceRepairOrder::getTenantId, tenantList);
        return this.count(queryWrapper);
    }

    @Override
    public List<ServiceRepairOrder> doQueryServiceRepairOrder(int currentPage, int pageSize, List<Long> flowIds, List<Long> tenantList) {
        LambdaQueryWrapper<ServiceRepairOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ServiceRepairOrder::getId, flowIds);
        queryWrapper.in(ServiceRepairOrder::getTenantId, tenantList);
        Page<ServiceRepairOrder> page = this.page(new Page<>(currentPage, pageSize), queryWrapper);
        return page.getRecords();
    }

    @Override
    public List<WorkbenchDto> getTableMaintenanceCount() {
        return baseMapper.getTableMaintenanceCount();
    }

    @Override
    public List<WorkbenchDto> getTableMaintenanceMeCount() {
        return baseMapper.getTableMaintenanceMeCount();
    }

    public String execution(String id, String accessToken){
        LoginInfo loginInfo = loginUtils.get(accessToken);
        loginInfo.setName(getSysCfg(EnumConsts.TASK_USER.TASK_USER_NAME,"0").getCfgValue());
        loginInfo.setId(Long.valueOf(getSysCfg(EnumConsts.TASK_USER.TASK_USER_ID,"0").getCfgValue()));
        List flowIds = getFlowIdFromRedis(id);
        if(flowIds.isEmpty()){
            return "";
        }

        List<Long> tenantList = CommonUtil.strToList(loginInfo.getTenantId().toString(),"long");
        int allRow= this.doQueryServiceRepairOrderCount(flowIds,tenantList);
        int pageSize = 50;
        int totalPage = allRow % pageSize== 0 ? allRow / pageSize : allRow / pageSize+1;
        int currentPage = 0;
        for(int i=0;i<totalPage;i++){
            List<ServiceRepairOrder> serviceRepairOrderList = this.doQueryServiceRepairOrder(currentPage, pageSize,flowIds,tenantList);
            execution(serviceRepairOrderList);
            currentPage+=pageSize;
        }
        return "PushRepairOrderTask";
    }

    private List<Long> getFlowIdFromRedis(String id){
        String flowIdStr = "";
        List<Long> rtnList = new ArrayList<>();
        do {
            flowIdStr = id;
            if(StringUtils.isNotBlank(flowIdStr)){
                rtnList.add(Long.parseLong(flowIdStr));
            }
        }while (StringUtils.isNotEmpty(flowIdStr));
        return rtnList;
    }

    public void execution(List<ServiceRepairOrder> serviceRepairOrderList){
        List<Long> flowIdList = this.getFlowIdList(serviceRepairOrderList);
        List<ServiceRepairItems> serviceRepairItemsList = serviceRepairItemsService.getRepairOrderItems(flowIdList);
        List<ServiceRepairParts> serviceRepairPartsList = serviceRepairPartsService.getRepairOrderParts(flowIdList);
        List<OaFiles> oaFilesList = iOaFilesService.getRepairOrderPicList(flowIdList);
        Map repairOrderDetail = this.getRepairOrderDetail(flowIdList, serviceRepairItemsList, serviceRepairPartsList, oaFilesList);
        for (int i = 0; i < serviceRepairOrderList.size(); i++) {
            ServiceRepairOrder serviceOrder = serviceRepairOrderList.get(i);
            boolean isSuccess = true;
            int count = 0;
            do {
                isSuccess = doPush(serviceOrder, repairOrderDetail);
                count++;
            } while (!isSuccess && serviceOrder.getPushFailCount() < 4 && count < 5);
        }
    }

    private List<Long> getFlowIdList(List<ServiceRepairOrder> serviceRepairOrderList) {
        List<Long> flowIdList = new ArrayList<>();
        for (int i = 0; i < serviceRepairOrderList.size(); i++) {
            ServiceRepairOrder serviceOrder = serviceRepairOrderList.get(i);
            flowIdList.add(serviceOrder.getId());
        }
        return flowIdList;
    }

    private Map getRepairOrderDetail(List<Long> flowIdList, List<ServiceRepairItems> serviceRepairItemsList, List<ServiceRepairParts> serviceRepairPartsList, List<OaFiles> oaFilesList){
        Map rtnMap = new HashMap();
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < flowIdList.size(); i++) {
            Long flowId = flowIdList.get(i);
            List<Map> serviceRepairItemsTempList = new ArrayList<>();
            List<Map> serviceRepairPartsTempList = new ArrayList<>();
            List<String> oaFilesTempList = new ArrayList<>();
            List list = new ArrayList();
            for (int j = 0; j < serviceRepairItemsList.size(); j++) {
                ServiceRepairItems items = serviceRepairItemsList.get(j);
                if(flowId.longValue() == items.getRepairOrderId().longValue()){
                    Map map = new HashMap();
                    map.put("itemId",items.getItemId());
                    map.put("itemName",items.getItemName());
                    String projectCode = getSysStaticData("SERVICE_REPAIR_ITEMS",items.getItemName()).getCodeValue();
                    if(StringUtils.isBlank(projectCode)){
                        projectCode = "50";
                    }
                    map.put("projectCode", projectCode);
                    map.put("manHour",items.getItemManHour());
                    if(null != items.getItemPrice()){
                        map.put("price",Double.parseDouble(CommonUtil.divide(items.getItemPrice())));
                    }
                    if(null != items.getTotalItemPrice()){
                        map.put("totalPrice",Double.parseDouble(CommonUtil.divide(items.getTotalItemPrice())));
                    }
                    serviceRepairItemsTempList.add(map);
                }
            }

            for (int j = 0; j < serviceRepairPartsList.size(); j++) {
                ServiceRepairParts parts = serviceRepairPartsList.get(j);
                if(flowId.longValue() == parts.getRepairOrderId().longValue()){
                    Map map = new HashMap();
                    map.put("partsId",parts.getPartsId());
                    map.put("orderItemId",parts.getOrderItemId());
                    map.put("partsName",parts.getPartsName());
                    map.put("partsNumber",parts.getPartsNumber());
                    if(null != parts.getPartsPrice()){
                        map.put("partsPrice",Double.parseDouble(CommonUtil.divide(parts.getPartsPrice())));
                    }
                    if(null != parts.getTotalPartsPrice()){
                        map.put("totalpartsprice",Double.parseDouble(CommonUtil.divide(parts.getTotalPartsPrice())));
                    }
                    serviceRepairPartsTempList.add(map);
                }
            }

            for (int j = 0; j < oaFilesList.size(); j++) {
                OaFiles pic = oaFilesList.get(j);
                if(flowId.longValue() == pic.getRelId().longValue()){
                    Map map = new HashMap();
                    if(StringUtils.isNotBlank(pic.getFileUrl())){
                        String url = null;
                        try {
                            url = client.getHttpURL(pic.getFileUrl()).split("\\?")[0];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String prefixPath = "";
                        String fileExtName = "";
                        if(url.contains(".")) {
                            int idx1 = url.lastIndexOf(".");
                            prefixPath = url.substring(0, idx1);
                            fileExtName = url.substring(idx1 + 1);
                            url = prefixPath + "_big" + "." + fileExtName;
                        }
                        url = url.replaceAll(":90",":94");
                        oaFilesTempList.add(url);
                    }
                }
            }

            list.add(serviceRepairItemsTempList);
            list.add(serviceRepairPartsTempList);
            list.add(oaFilesTempList);
            rtnMap.put(flowId,list);
        }
        return  rtnMap ;
    }

    private boolean doPush(ServiceRepairOrder serviceOrder,Map repairOrderDetail){
        int count = serviceOrder.getPushFailCount()== null ? 0 :serviceOrder.getPushFailCount();
        try {
            Map pushMap = null;
            if(serviceOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS3){
                //新增接口
                pushMap = getPushDataForNoOrder(serviceOrder,repairOrderDetail);
            }else if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS7){
                //// TODO: 2019/9/23 推送增量数据
                pushMap = getPushDataForOrder(serviceOrder,repairOrderDetail,null);
            }else if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS8 ){
                pushMap = getPushDataForOrder(serviceOrder,repairOrderDetail,null);
            }else if(serviceOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS16){
                //修改接口 推送状态
                pushMap = new HashMap();
                //工单编码
                pushMap.put("orderCode", StringUtils.isNotBlank(serviceOrder.getOrderCode())?serviceOrder.getOrderCode():serviceOrder.getOrderSn());
                //工单状态
                pushMap.put("status",serviceOrder.getOrderStatus());

                //确认支付时间
                pushMap.put("confirmPaymentTime", serviceOrder.getRepairEndTime());
            }else if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS12){
                //修改接口 推送状态
                pushMap = new HashMap();
                //工单编码
                pushMap.put("orderCode", StringUtils.isNotBlank(serviceOrder.getOrderCode())?serviceOrder.getOrderCode():serviceOrder.getOrderSn());
                //工单状态
                pushMap.put("status",serviceOrder.getOrderStatus());
                //支付完成时间
                pushMap.put("finishPaymentTime", serviceOrder.getOrderEndTime());
            }else if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS13 && null != serviceOrder.getPushState()){
                //修改接口 推送状态
                pushMap = new HashMap();
                //工单编码
                pushMap.put("orderCode", StringUtils.isNotBlank(serviceOrder.getOrderCode())?serviceOrder.getOrderCode():serviceOrder.getOrderSn());
                //工单状态
                pushMap.put("status",serviceOrder.getOrderStatus());
            }else if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS11 && null != serviceOrder.getPushState() && serviceOrder.getPushState() < REPAIR_ORDER_STATUS8){
                pushMap = getPushDataForOrder(serviceOrder,repairOrderDetail,REPAIR_ORDER_STATUS8);
            }
            if(null != pushMap){
                pushData(pushMap);
                serviceOrder.setPushState(serviceOrder.getOrderStatus());
                serviceOrder.setPushFailCount(0);
                this.saveOrUpdate(serviceOrder);
            }
            return true;
        } catch (Exception e) {
            log.error("["+serviceOrder.getId()+"推送失败]"+e.getMessage());
            count ++ ;
            serviceOrder.setPushFailCount(count);
            serviceOrder.setPushFailTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
            serviceOrder.setPushFailRemark(e.getMessage());
            this.saveOrUpdate(serviceOrder);
            e.printStackTrace();
            return false;
        }finally {
//            platformTransactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        }

    }

    private Map getPushDataForNoOrder(ServiceRepairOrder serviceOrder,Map repairOrderDetail){
        Map map = new HashMap();
        //工单编码
        map.put("orderCode", serviceOrder.getOrderCode());
        //开单时间
        map.put("submitDate", serviceOrder.getCreateDate()==null?serviceOrder.getCreateTime():serviceOrder.getCreateDate());
        //司机姓名
        map.put("submitter", serviceOrder.getContractName());
        //联系号码
        map.put("submitterMobile", serviceOrder.getContractMobile());
        //申请原因
        map.put("maintenaceContent", serviceOrder.getRemark());
        //车牌号
        map.put("plateNumber", serviceOrder.getCarNo());
        //工单类型
        map.put("orderType", serviceOrder.getWorkType());
        //审核信息
        SysOperLog sysOperLog = getSysOperLog(serviceOrder.getId());
        if(null != sysOperLog){
            //审核意见
            map.put("auditSuggestion",sysOperLog.getOperComment());
            //审核时间
            map.put("auditTime",sysOperLog.getCreateTime());
            //审核人
            map.put("auditor",sysOperLog.getOpName());
        }
        //工单状态
        map.put("status",serviceOrder.getOrderStatus());
        //图片信息
        List list = (List) repairOrderDetail.get(serviceOrder.getId());
        List<Map> pics = (List) list.get(2);

        return map;
    }

    private SysOperLog getSysOperLog(long flowId){
        List<SysOperLog> sysOperLogList =  sysOperLogService.querySysOperLogAll(300005,flowId,true);
        SysOperLog sysOperLog = null;
        if(!sysOperLogList.isEmpty()){
            sysOperLog = sysOperLogList.get(sysOperLogList.size()-1);
            if(null != sysOperLog.getOperType() && sysOperLog.getOperType() == 4){
                return sysOperLog;
            }else{
                return null;
            }
        }
        return sysOperLog;
    }

    private Map getPushDataForOrder(ServiceRepairOrder serviceOrder,Map repairOrderDetail,Integer status)throws Exception{
        Map map = new HashMap();
        ////工单编码
        map.put("orderCode", StringUtils.isNotBlank(serviceOrder.getOrderCode())?serviceOrder.getOrderCode():serviceOrder.getOrderSn());
        //状态
        if(null != status){
            map.put("status", status);
        }else{
            map.put("status", serviceOrder.getOrderStatus());
        }

        if(serviceOrder.getOrderStatus() == REPAIR_ORDER_STATUS7){

            //兼容不申请直接开单
            if(StringUtils.isBlank(serviceOrder.getOrderCode())){
                //车牌号
                map.put("plateNumber", serviceOrder.getCarNo());
                //工单类型
                map.put("orderType", serviceOrder.getWorkType());
                //司机姓名
                map.put("submitter", serviceOrder.getContractName());
                //联系号码
                map.put("submitterMobile", serviceOrder.getContractMobile());
                //开单时间
                map.put("submitDate", serviceOrder.getCreateDate()==null?serviceOrder.getCreateTime():serviceOrder.getCreateDate());
            }

            //审核信息
            SysOperLog sysOperLog = getSysOperLog(serviceOrder.getId());
            if(null != sysOperLog){
                //审核意见
                if(StringUtils.isNotBlank(sysOperLog.getOperComment())){
                    map.put("auditSuggestion",sysOperLog.getOperComment());
                }
                //审核时间
                map.put("auditTime",sysOperLog.getCreateTime());
                //审核人
                map.put("auditor",sysOperLog.getOpName());
            }

            //维修站点编码
            if(null != serviceOrder.getShopId()){
                map.put("maintenaceCompanyId", serviceOrder.getShopId());
            }
            //维修站点名称
            if(StringUtils.isNotBlank(serviceOrder.getShopName())){
                map.put("maintenaceCompany", serviceOrder.getShopName());
            }
            //进厂公里数
            if(StringUtils.isNotBlank(serviceOrder.getCarMileage())){
                map.put("maintenaceKm", serviceOrder.getCarMileage());
            }
            //维保开始时间
            if(null != serviceOrder.getRepairStartTime()){
                map.put("repairstarttime", serviceOrder.getRepairStartTime());
                map.put("entranceTime", serviceOrder.getInFactoryTime());
            }

            //总费用
            if (null != serviceOrder.getTotalAmount()) {
                map.put("totalamount",  Double.parseDouble(CommonUtil.divide(serviceOrder.getTotalAmount())));
            }
            List list = (List) repairOrderDetail.get(serviceOrder.getId());
            //项目
            List<Map> items = (List) list.get(0);
            if(null != items && !items.isEmpty()){
                map.put("maintenaceProjectList", items);
            }
            //配件
            List<Map> parts = (List) list.get(1);
            if(null != parts && !items.isEmpty()){
                map.put("partsList", parts);
            }
        }else{
            ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerService.getServiceRepairOrderVer(serviceOrder.getId(),1);
            //维保结束时间
            map.put("repairendtime", serviceOrder.getOutFactoryTime());
            map.put("leaveTime", serviceOrder.getOutFactoryTime());
            if(null != serviceRepairOrderVer){
                if(null != serviceOrder.getTotalAmount() && !serviceOrder.getTotalAmount().equals(serviceRepairOrderVer.getTotalAmount())){
                    map.put("totalamount", Double.parseDouble(CommonUtil.divide(serviceOrder.getTotalAmount())));
                }else if(null == serviceOrder.getTotalAmount() && null != serviceRepairOrderVer.getTotalAmount()){
                    map.put("totalamount",0);
                }

                if(StringUtils.isNotBlank(serviceOrder.getCarMileage()) && !serviceOrder.getCarMileage().equals(serviceRepairOrderVer.getCarMileage())){
                    map.put("maintenaceKm", Double.parseDouble(serviceOrder.getCarMileage()));
                }else if(StringUtils.isNotBlank(serviceRepairOrderVer.getCarMileage())  && !serviceRepairOrderVer.getCarMileage().equals(serviceOrder.getCarMileage())){
                    map.put("maintenaceKm", Double.parseDouble(serviceOrder.getCarMileage()));
                }

                List<Map> itemList = getChangeItemList(serviceRepairOrderVer);
                if(null != itemList && !itemList.isEmpty()){
                    map.put("maintenaceProjectList",itemList);
                }

                List<Map> partList = getChangePartsList(serviceRepairOrderVer);
                if(null != partList && !partList.isEmpty()){
                    map.put("partsList",partList);
                }
            }
            //新增图片推送
            //图片信息
            List list = (List) repairOrderDetail.get(serviceOrder.getId());
            if(null != list){

                List<Map> pics = (List) list.get(2);

                if(null != pics && !pics.isEmpty()){
                    map.put("picUrlList", pics);
                }
            }
        }
        return map;
    }

    private List getChangeItemList(ServiceRepairOrderVer serviceRepairOrderVer){
        List<Map> itemList = new ArrayList<>();
        List<ServiceRepairItemsVer> itemsVer = serviceRepairItemsMapper.getRepairOrderItemsVer(serviceRepairOrderVer.getFlowId(),serviceRepairOrderVer.getId());
        List<ServiceRepairItems> items = serviceRepairItemsMapper.getRepairOrderItems(serviceRepairOrderVer.getFlowId());
        List<Map> itemMap = disffServiceRepairItems(items,itemsVer);
        for (int i = 0; i < itemMap.size(); i++) {
            Map tempMap = itemMap.get(i);
            String oper = DataFormat.getStringKey(tempMap,"oper");
            Object obj  = tempMap.get("item");
            Map objMap = null;
            try {
                objMap = BeanUtils.convertBean2Map(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long itemId = DataFormat.getLongKey(objMap,"itemId");
            if("delete".equals(oper)){
                Map diffMap = new HashMap();
                diffMap.put("itemId",itemId);
                String itemName = DataFormat.getStringKey(objMap,"itemName");
                diffMap.put("itemName",itemName);
                String projectCode = getSysStaticData("SERVICE_REPAIR_ITEMS",itemName).getCodeValue();
                if(StringUtils.isBlank(projectCode)){
                    projectCode = "50";
                }
                diffMap.put("projectCode", projectCode);
                long itemPrice = DataFormat.getLongKey(objMap,"itemPrice");
                if(itemPrice > 0){
                    diffMap.put("price",Double.parseDouble(CommonUtil.divide(itemPrice)));
                }
                long totalItemPrice = DataFormat.getLongKey(objMap,"totalItemPrice");
                if(totalItemPrice > 0){
                    diffMap.put("totalPrice",Double.parseDouble(CommonUtil.divide(totalItemPrice)));
                }
                String itemManHour = DataFormat.getStringKey(objMap,"itemManHour");
                if(StringUtils.isNotBlank(itemManHour)){
                    diffMap.put("manHour", Double.parseDouble(itemManHour));
                }
                itemList.add(diffMap);

            }else if("add".equals(oper)){
                objMap = new HashMap();
                objMap.put("itemId",itemId);
                objMap.put("isDelete",1);
                itemList.add(objMap);
            }else{
                Map diffMap = new HashMap();
                Object objVer  = tempMap.get("itemVer");
                Map objMapVer = null;
                try {
                    objMapVer = BeanUtils.convertBean2Map(objVer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String itemName = DataFormat.getStringKey(objMap,"itemName");
                String itemNameVer = DataFormat.getStringKey(objMapVer,"itemName");
                if(!itemName.equals(itemNameVer)){
                    diffMap.put("itemName",itemName);
                }
                long itemPrice = DataFormat.getLongKey(objMap,"itemPrice");
                long itemPriceVer = DataFormat.getLongKey(objMapVer,"itemPrice");
                if(itemPrice != itemPriceVer){
                    diffMap.put("price",Double.parseDouble(CommonUtil.divide(itemPrice)));
                }

                long totalItemPrice = DataFormat.getLongKey(objMap,"totalItemPrice");
                long totalItemPriceVer = DataFormat.getLongKey(objMapVer,"totalItemPrice");
                if(totalItemPrice != totalItemPriceVer){
                    diffMap.put("totalPrice",Double.parseDouble(CommonUtil.divide(totalItemPrice)));
                }

                String itemManHour = DataFormat.getStringKey(objMap,"itemManHour");
                String itemManHourVer = DataFormat.getStringKey(objMapVer,"itemManHour");
                if(!itemManHour.equals(itemManHourVer)){
                    diffMap.put("manHour",Double.parseDouble(itemManHour));
                }
                if(!diffMap.isEmpty()){
                    diffMap.put("itemId",itemId);
                    itemList.add(diffMap);
                }
            }
        }

        return itemList;
    }

    private List getChangePartsList(ServiceRepairOrderVer serviceRepairOrderVer){
        List<Map> itemList = new ArrayList<>();
        List<ServiceRepairPartsVer> partsVer = serviceRepairPartsVerMapper.getRepairOrderPartsVer(serviceRepairOrderVer.getFlowId(),serviceRepairOrderVer.getId(),null);
        List<ServiceRepairParts> parts = serviceRepairPartsMapper.getRepairOrderParts(serviceRepairOrderVer.getFlowId());
        List<Map> itemMap = this.disffServiceRepairParts(parts,partsVer);
        for (int i = 0; i < itemMap.size(); i++) {
            Map tempMap = itemMap.get(i);
            String oper = DataFormat.getStringKey(tempMap,"oper");
            Object obj  = tempMap.get("item");
            Map objMap = null;
            try {
                objMap = BeanUtils.convertBean2Map(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long partsId = DataFormat.getLongKey(objMap,"partsId");
            if("delete".equals(oper)){
                long partsPrice = DataFormat.getLongKey(objMap,"partsPrice");
                if(partsPrice > 0){
                    objMap.put("partsPrice",Double.parseDouble(CommonUtil.divide(partsPrice)));
                }
                long totalPartsPrice = DataFormat.getLongKey(objMap,"totalPartsPrice");
                if(totalPartsPrice > 0){
                    objMap.put("totalpartsprice",Double.parseDouble(CommonUtil.divide(totalPartsPrice)));
                }
                objMap.remove("totalPartsPrice");
                objMap.remove("id");
                objMap.remove("tenantId");
                objMap.remove("repairOrderId");
                objMap.remove("createTime");
                itemList.add(objMap);
            }else if("add".equals(oper)){
                objMap = new HashMap();
                objMap.put("partsId",partsId);
                objMap.put("isDelete",1);
                itemList.add(objMap);
            }else{
                Map diffMap = new HashMap();
                Object objVer  = tempMap.get("itemVer");
                Map objMapVer = null;
                try {
                    objMapVer = BeanUtils.convertBean2Map(objVer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String partsName = DataFormat.getStringKey(objMap,"partsName");
                String partsNameVer = DataFormat.getStringKey(objMapVer,"partsName");
                if(!partsName.equals(partsNameVer)){
                    diffMap.put("partsName",partsName);
                }
                long partsPrice = DataFormat.getLongKey(objMap,"partsPrice");
                long partsPriceVer = DataFormat.getLongKey(objMapVer,"partsPrice");
                if(partsPrice != partsPriceVer){
                    diffMap.put("partsPrice",Double.parseDouble(CommonUtil.divide(partsPrice)));
                }

                long totalPartsPrice = DataFormat.getLongKey(objMap,"totalPartsPrice");
                long totalPartsPriceVer = DataFormat.getLongKey(objMapVer,"totalPartsPrice");
                if(totalPartsPrice != totalPartsPriceVer){
                    diffMap.put("totalpartsprice",Double.parseDouble(CommonUtil.divide(totalPartsPrice)));
                }

                String partsNumber = DataFormat.getStringKey(objMap,"partsNumber");
                String partsNumberVer = DataFormat.getStringKey(objMapVer,"partsNumber");
                if(!partsNumber.equals(partsNumberVer)){
                    diffMap.put("partsNumber",Double.parseDouble(partsNumber));
                }
                if(!diffMap.isEmpty()){
                    diffMap.put("partsId",partsId);
                    itemList.add(diffMap);
                }
            }
        }
        return itemList;
    }

    public List<Map> disffServiceRepairParts(List<ServiceRepairParts> items,List<ServiceRepairPartsVer> itemsVer){
        Map itemsMap = new HashMap();
        for (int i = 0; i < items.size(); i++) {
            ServiceRepairParts item = items.get(i);
            itemsMap.put(item.getPartsId(),item);
        }

        Map itemsVerMap = new HashMap();

        List<Map> rtnList = new ArrayList<>();
        for (int i = 0; i < itemsVer.size(); i++) {
            Map rtnMap = new HashMap();
            ServiceRepairPartsVer itemVer = itemsVer.get(i);
            itemsVerMap.put(itemVer.getPartsId(),itemVer);
            if(itemsMap.containsKey(itemVer.getPartsId())){
                rtnMap.put("item",itemsMap.get(itemVer.getPartsId()));
                rtnMap.put("itemVer",itemVer);
                rtnMap.put("oper","update");
                rtnMap.put("operName","");
            }else {
                rtnMap.put("item",itemVer);
                rtnMap.put("oper","add");
                rtnMap.put("operName","增");
            }
            rtnList.add(rtnMap);
        }

        for (int i = 0; i < items.size(); i++) {
            Map rtnMap = new HashMap();
            ServiceRepairParts item = items.get(i);
            if(!itemsVerMap.containsKey(item.getPartsId())){
                rtnMap.put("item",item);
                rtnMap.put("oper","delete");
                rtnMap.put("operName","删");
                rtnList.add(rtnMap);
            }
        }

        return rtnList;
    }

    private void pushData(Map pushDataMap){
//        String method = "open.api.inside.guanhaoche.vms.maintenaceOrder.save";
//        String bizBody  = JSON.toJSONString(pushDataMap);
//        log.error("请求报文：" + bizBody);
//        String resp = retryableApplyBiz(bizBody,method);
//        log.error("返回报文：" + resp);
//        JSONObject jsonObject = JSONObject.fromObject(resp);
//        Map rtnMap = (Map)jsonObject;
//        boolean success = DataFormat.getBooleanKey(rtnMap,"success");
//        if(!success){
//            String msg = DataFormat.getStringKey(rtnMap,"msg");
//            throw new BusinessException(msg);
//        }
    }

//    private SysUser getSysUser(String accessToken) {
//        LoginInfo loginInfo = loginUtils.get(accessToken);
//        BaseMapper<SysUser> baseMapper = iSysUserService.getBaseMapper();
//        SysUser sysUser = baseMapper.selectById(loginInfo.getId());
//        return sysUser;
//    }

    /**
     * 记录日志
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    public SysCfg getSysCfg(String cfgName, String cfgSystem){
        SysCfg sysCfg = (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));

        if (null != sysCfg && (Integer.parseInt(cfgSystem) == -1 || Integer.parseInt(cfgSystem)==(sysCfg.getCfgSystem()))) {
            return sysCfg;
        }

        return new SysCfg();
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }



    @Override
    public boolean doSure(DoSureDto dto, String accessToken) {
        LoginInfo loginInfo= loginUtils.get(accessToken);
        if(dto.getFlowId() < 0){
            throw new BusinessException("缺少参数");
        }
        ServiceRepairOrder serviceRepairOrder = this.getById(dto.getFlowId());
        if(null == serviceRepairOrder){
            throw new BusinessException("未找到该工单信息");
        }
        if(dto.getActionState() == 0){
            if(serviceRepairOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS4){
                serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS6);
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(),SysOperLogConst.OperType.Update , "司机驳回"+ dto.getAuditContent());

                //将工单报价拒绝信息推送给神汽
                int isJumpSQ = Integer.parseInt((String)sysCfgService.getCfgVal("IS_JUMP_SQ", 0, String.class));
                if(isJumpSQ != 1){
//                    shenQiTF.workOrderDoAction(serviceRepairOrder.getOrderSn(), REFUSE,auditContent);
                }

            }else if(serviceRepairOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS8){
                serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS10);
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(),SysOperLogConst.OperType.Update , "司机驳回"+ dto.getAuditContent());
            }

        }else if(dto.getActionState() == 1) {
            if (serviceRepairOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS4) {
                serviceRepairOrder.setOrderStatus(ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS5);
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(), SysOperLogConst.OperType.Update, "司机确认通过" + dto.getAuditContent());

                Map inMap = new HashMap();
                inMap.put("svName", "ServiceRepairOrderTF");
                boolean bool = iAuditService.startProcess(com.youming.youche.order.commons.AuditConsts.AUDIT_CODE.SERVICE_REPAIR_INFO, serviceRepairOrder.getId(), SysOperLogConst.BusiCode.serviceRepair, inMap, String.valueOf(serviceRepairOrder.getTenantId()));
                if (!bool) {
                    throw new BusinessException("启动审核流程失败！");
                }

            } else if (serviceRepairOrder.getOrderStatus() == ServiceConsts.REPAIR_ORDER_STATUS.REPAIR_ORDER_STATUS8) {
                //需求变更 司机确认之后直接付款
                //付款之前确认一下价格是否有变化
                ServiceRepairOrderVer serviceRepairOrderVer = serviceRepairOrderVerMapper.getServiceRepairOrderVer(serviceRepairOrder.getId());
                if (null == serviceRepairOrderVer) {
                    throw new BusinessException("未找到报价单确认时的价格,无法确认价格是否被修改,业务异常");
                }
                if(serviceRepairOrderVer.getTotalAmount().longValue() != serviceRepairOrder.getTotalAmount().longValue()){
                    throw new BusinessException("工单价格与报价单确认时价格不一致,业务异常");
                }
                //确认之前先查一下工单是否已经确认过
//                int isJumpSQ = Integer.parseInt((String) SysCfgUtil.getCfgVal("IS_JUMP_SQ", 0, String.class));
//                if(isJumpSQ != 1){
//                    Map<String,Object> shRtnOrderMap = shenQiTF.queryGhcWorkOrder(serviceRepairOrder.getOrderSn());
//                    String workStatus = DataFormat.getStringKey(shRtnOrderMap,"workStatus");
//                    String workStatusNew = workStatus;
//                    if(SGWC.equals(workStatus)){
//                        Map<String,Object> shRtnMap = shenQiTF.workOrderDoAction(serviceRepairOrder.getOrderSn(), MAINTAIN,auditContent);
//                        workStatusNew = DataFormat.getStringKey(shRtnMap,"workStatus");
//                    }
//                    serviceRepairOrder.setWorkStatus(workStatusNew);
//                }


                //刷新ver数据
                if(null != serviceRepairOrderVer){
                    flushVerData(serviceRepairOrder,serviceRepairOrderVer,REPAIR_ORDER_STATUS11);
                }
                //生成离厂时间
                if(null == serviceRepairOrder.getOutFactoryTime()){
                    serviceRepairOrder.setOutFactoryTime(String.valueOf(new Date()));
                }

                //维护车辆的维保里程和最近维保时间
                int carMil = 0;
                if(StringUtils.isNotBlank(serviceRepairOrder.getCarMileage()) && CommonUtil.isNumber(serviceRepairOrder.getCarMileage())){
                    carMil = Integer.parseInt(serviceRepairOrder.getCarMileage());
                }
                vehicleDataInfoService.updateMaintenance(serviceRepairOrder.getCarNo(),carMil,LocalDateTime.now());
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.serviceRepair, serviceRepairOrder.getId(),SysOperLogConst.OperType.Update , "司机完成确认通过"+dto.getAuditContent());
            }
        }else{
            throw new BusinessException("操作错误");
        }
        this.saveOrUpdate(serviceRepairOrder);
        return true;
    }


}
