package com.youming.youche.record.provider.service.impl.violation;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.record.api.violation.IViolationRecordService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.sys.SysOperLog;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.violation.ViolationQuotation;
import com.youming.youche.record.domain.violation.ViolationRecord;
import com.youming.youche.record.dto.violation.ViolationDto;
import com.youming.youche.record.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.record.provider.mapper.service.ServiceInfoMapper;
import com.youming.youche.record.provider.mapper.sys.SysOperLogMapper;
import com.youming.youche.record.provider.mapper.tenant.TenantVehicleRelMapper;
import com.youming.youche.record.provider.mapper.user.UserDataInfoRecordMapper;
import com.youming.youche.record.provider.mapper.vehicle.VehicleDataInfoMapper;
import com.youming.youche.record.provider.mapper.violation.ViolationQuotationMapper;
import com.youming.youche.record.provider.mapper.violation.ViolationRecordMapper;
import com.youming.youche.record.vo.violation.ViolationRecordVo;
import com.youming.youche.record.vo.violation.ViolationVo;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.BeanUtils;
import com.youming.youche.util.TableShardingRule;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @date 2022/1/7 15:30
 */
@DubboService(version = "1.0.0")
public class ViolationRecordServiceImpl extends BaseServiceImpl<ViolationRecordMapper, ViolationRecord> implements IViolationRecordService {

    @Resource
    private ServiceInfoMapper serviceInfoMapper;

    @Resource
    private ViolationRecordMapper violationRecordMapper;

    @Resource
    private ViolationQuotationMapper violationQuotationMapper;

    @Resource
    private VehicleDataInfoMapper vehicleDataInfoMapper;

    @Resource
    private TenantVehicleRelMapper tenantVehicleRelMapper;

    @Resource
    private UserDataInfoRecordMapper userDataInfoRecordMapper;

    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;

    @Resource
    private SysOperLogMapper sysOperLogMapper;

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @SneakyThrows
    @Override
    public Page<ViolationDto> doQuery(Page<ViolationDto> objectPage, ViolationVo violationVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Map> ret = serviceInfoMapper.getServiceInfoList(SysStaticDataEnum.SERVICE_BUSI_TYPE.VR, null);

        Page<ViolationDto> violationDtoPage = violationRecordMapper.doQuery(objectPage, violationVo, loginInfo.getTenantId());

        //????????????
        if (null == violationDtoPage) {
            return null;
        }

        List<SysStaticData> staticData = null;
        try {
            staticData = iSysStaticDataService.getSysStaticDataList("VIOLATION_SERVICE_USER_ID_CMP", loginInfo.getUserInfoId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serviceIdStr = null;
        Long serviceId=null;
        if(staticData != null && staticData.size() > 0){
            serviceIdStr = staticData.get(0).getCodeValue();
             serviceId = Long.parseLong(serviceIdStr); // ?????????????????????ID
        }else{
            serviceId = -1L;
        }


        for (ViolationDto violationDto : violationDtoPage.getRecords()) {
            //????????????
            Long recordId = violationDto.getRecordId();
            long offerCount = violationQuotationMapper.getViolationQuotationCount(recordId, serviceId);
            violationDto.setOfferCount(String.valueOf(offerCount));
            violationDto.setIsOffer(offerCount > 0 && ret.size() > 1);
            //??????????????????
            List<String> fenString = new ArrayList<>();
            fenString.add("violationFine");
            fenString.add("overdueFine");
            HashMap<String, Object> iMap = new HashMap<>();
            iMap.put("violationFine", violationDto.getViolationFine());
            iMap.put("overdueFine", violationDto.getOverdueFine());
            CommonUtil.transFeeFenToYuan(fenString, iMap);
            iMap.putAll(iMap);
            if (iMap.get("violationFine") != null) {
                violationDto.setViolationFine(iMap.get("violationFine").toString());
            }
            if (iMap.get("overdueFine") != null) {
                violationDto.setOverdueFine(iMap.get("overdueFine").toString());
            }
            //????????????
            Integer type = violationDto.getType();
            if (null != type && type >= 0) {
                if ("1".equals(type)) { // 1:???????????? 2:????????????
                    violationDto.setTypeName("??????");
                } else {
                    violationDto.setTypeName("??????");
                }
            }
            //???????????????????????????????????????
            Integer orderState = violationDto.getOrderState();
            Integer recordState = violationDto.getRecordState();
            if (null != orderState &&
                    (SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE1 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE2 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE3 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE4 == orderState)
            ) {
                violationDto.setOrderStateName(iSysStaticDataService.getSysStaticData("VIOLATION_ORDER_STATE", orderState + "").getCodeName());
            } else {
                if (orderState != null && orderState > 0) {//?????????????????????
                    if (offerCount > 0 && !SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    } else {
                        if (SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                            violationDto.setOrderStateName("?????????");
                        }
                        //????????????
                        if (SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                            violationDto.setOrderStateName("?????????");
                        }
                    }
                } else {//?????????????????????
                    if (offerCount <= 0 && SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    if (offerCount > 0 && SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    if (SysStaticDataEnum.VIOLATION_RECORD_STATE.IN_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    //????????????
                    if (SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                }
            }
        }
        return violationDtoPage;
    }

    @Override
    @Async
    public void doQueryExport(ViolationVo violationVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Map> ret = serviceInfoMapper.getServiceInfoList(SysStaticDataEnum.SERVICE_BUSI_TYPE.VR, null);

        List<ViolationDto> violationDtoPage = violationRecordMapper.doQueryExport(violationVo, loginInfo.getTenantId());

        List<SysStaticData> staticData = null;
        try {
            staticData = iSysStaticDataService.getSysStaticDataList("VIOLATION_SERVICE_USER_ID_CMP", loginInfo.getUserInfoId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String serviceIdStr = staticData.get(0).getCodeValue();
        long serviceId = Long.parseLong(serviceIdStr); // ?????????????????????ID

        for (ViolationDto violationDto : violationDtoPage) {
            //????????????
            Long recordId = violationDto.getRecordId();
            long offerCount = violationQuotationMapper.getViolationQuotationCount(recordId, serviceId);
            violationDto.setOfferCount(String.valueOf(offerCount));
            violationDto.setIsOffer(offerCount > 0 && ret.size() > 1);
            //??????????????????
            List<String> fenString = new ArrayList<>();
            fenString.add("violationFine");
            fenString.add("overdueFine");
            HashMap<String, Object> iMap = new HashMap<>();
            iMap.put("violationFine", violationDto.getViolationFine());
            iMap.put("overdueFine", violationDto.getOverdueFine());
            CommonUtil.transFeeFenToYuan(fenString, iMap);
            iMap.putAll(iMap);
            if (iMap.get("violationFine") != null) {
                violationDto.setViolationFine(iMap.get("violationFine").toString());
            }
            if (iMap.get("overdueFine") != null) {
                violationDto.setOverdueFine(iMap.get("overdueFine").toString());
            }
            //????????????
            Integer type = violationDto.getType();
            if (null != type && type >= 0) {
                if ("1".equals(type)) { // 1:???????????? 2:????????????
                    violationDto.setTypeName("??????");
                } else {
                    violationDto.setTypeName("??????");
                }
            }
            //???????????????????????????????????????
            Integer orderState = violationDto.getOrderState();
            Integer recordState = violationDto.getRecordState();
            if (null != orderState &&
                    (SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE1 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE2 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE3 == orderState ||
                            SysStaticDataEnum.VIOLATION_ORDER_STATE.VIOLATION_ORDER_STATE4 == orderState)
            ) {
                violationDto.setOrderStateName(iSysStaticDataService.getSysStaticData("VIOLATION_ORDER_STATE", orderState + "").getCodeName());
            } else {
                if (orderState != null && orderState > 0) {//?????????????????????
                    if (offerCount > 0 && !SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    } else {
                        if (SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                            violationDto.setOrderStateName("?????????");
                        }
                        //????????????
                        if (SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                            violationDto.setOrderStateName("?????????");
                        }
                    }
                } else {//?????????????????????
                    if (offerCount <= 0 && SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    if (offerCount > 0 && SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    if (SysStaticDataEnum.VIOLATION_RECORD_STATE.IN_HAND.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                    //????????????
                    if (SysStaticDataEnum.VIOLATION_RECORD_STATE.FINISH.equals(recordState)) {
                        violationDto.setOrderStateName("?????????");
                    }
                }
            }
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"????????????", "????????????", "????????????", "????????????", "????????????", "??????",
                    "????????????", "?????????", "????????????", "?????????", "????????????", "????????????", "???????????????", "????????????"};
            resourceFild = new String[]{"getViolationTime", "getViolationCode", "getLocationName", "getViolationAddress", "getViolationFine",
                    "getViolationPoint", "getCategory", "getViolationWritNo", "getTypeName", "getPlateNumber",
                    "getLinkMan", "getMobilePhone", "getViolationOrderNum", "getOrderStateName"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(violationDtoPage, showName, resourceFild, ViolationDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
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
    public ViolationRecord queryById(Long recordId) {
        ViolationRecord violationRecord = violationRecordMapper.selectById(recordId);
        Map<String, Object> outMap = null;
        if (violationRecord != null) {
            try {
                outMap = BeanUtils.convertBean2Map(violationRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (null != outMap) {
                //??????????????????
                List<String> fenString = new ArrayList<>();
                fenString.add("violationFine"); // ????????????
                fenString.add("overdueFine"); // ?????????
                CommonUtil.transFeeFenToYuan(fenString, outMap);
                violationRecord.setViolationFine(Long.parseLong(outMap.get("violationFine").toString()));
                violationRecord.setOverdueFine(Long.parseLong(outMap.get("overdueFine").toString()));
            }
        }

        return violationRecord;
    }

    @Override
    public Long saveOrUpdateViolationRecord(ViolationRecord record, SysUser sysUser, String accessToken) throws Exception {
        //????????????????????????
        if (record.getAccessType() != null && record.getAccessType() == EnumConsts.ACCESS_TYPE.ACCESS_TYPE01) {
            //????????????
            if (StringUtils.isEmpty(record.getPlateNumber())) {
                throw new BusinessException("?????????????????????!");
            }
            VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(record.getPlateNumber());
            if (vehicleDataInfo == null) {
                throw new BusinessException("??????????????????[" + record.getPlateNumber() + "]??????????????????");
            }

            long count = 0L;
            if (StringUtils.isNotEmpty(record.getViolationWritNo())) {
                count = violationRecordMapper.queryCountByViolationWritNo(record.getViolationWritNo());
            }

            if (count > 0) {
                throw new BusinessException("????????????" + record.getViolationWritNo() + " ???????????????????????????????????????");
            }

            List<TenantVehicleRel> vehicleRelList = tenantVehicleRelMapper.queryTenantVehicleRel(sysUser.getTenantId(), vehicleDataInfo.getPlateNumber());
            if (CollectionUtils.isEmpty(vehicleRelList)) {
                throw new BusinessException("??????????????????[" + record.getPlateNumber() + "]????????????????????????,????????????!");
            }
            TenantVehicleRel vehicleRel = vehicleRelList.get(0);

            //??????????????????
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(vehicleRel.getTenantId());

            //??????????????????????????????
            if (null == vehicleRel.getDriverUserId() && !vehicleRel.getVehicleClass().equals(1)) {
                throw new BusinessException("????????????");
            }
            //?????????????????????????????????????????????
            Long driverUserId = vehicleRel.getDriverUserId();
            if (null == driverUserId) {
                driverUserId = sysTenantDef.getAdminUser();
            }

            UserDataInfo userDataInfo = userDataInfoRecordMapper.selectById(driverUserId);

            record.setViolationTime(record.getRenfaTime());//?????????????????????????????????
            record.setCategory("?????????");
            record.setCategoryId(SysStaticDataEnum.VIOLATION_CATEGORY.CATEGORY_2);
            record.setVehicleCode(vehicleDataInfo.getId());
            record.setEngineNo(vehicleDataInfo.getEngineNo());
            record.setVinNo(vehicleDataInfo.getVinNo());
            record.setTenantId(sysTenantDef.getId());
            record.setTenantName(sysTenantDef.getName());
            record.setLinkMan(userDataInfo.getLinkman());
            record.setUserId(userDataInfo.getId());
            record.setMobilePhone(userDataInfo.getMobilePhone());
        }
        return saveOrUpdateViolationRecord(sysUser, record, accessToken);
    }

    @Override
    public Page<ViolationRecordVo> getViolationDetails(Page<ViolationRecordVo> page, String monthTime, Integer licenceType, Integer recordState, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String monthTimeStart = null;
        String monthTimeEnd = null;
        if (StringUtils.isNotBlank(monthTime)) {
            monthTimeStart = monthTime + "-01";
            monthTimeEnd = getLastDayOfMonth(monthTime);
        }
        Page<ViolationRecordVo> recordVoPage = baseMapper.getViolationDetails(page, monthTimeStart, monthTimeEnd,
                licenceType, recordState, loginInfo.getTenantId());
        // ????????????
        if (recordVoPage.getRecords() != null && recordVoPage.getRecords().size() > 0) {

            for (ViolationRecordVo record : recordVoPage.getRecords()) {

                if (record.getLicenceType() != null) {
                    record.setLicenceTypeName(iSysStaticDataService.getSysStaticDatas("LICENCE_TYPE", record.getLicenceType()));
                }

                String vehicleStatus = null;
                if (record.getVehicleStatus() != null) {
                    vehicleStatus = iSysStaticDataService.getSysStaticDataId("VEHICLE_STATUS", record.getVehicleStatus().toString());
                }

                String vehicleLength = null;
                if (record.getVehicleLength() != null && record.getVehicleStatus() != null) {
                    String vehicle_length = iSysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", record.getVehicleStatus().toString());
                    if (StringUtils.isNotBlank(vehicle_length)) {
                        vehicleLength = vehicle_length;
                    }
                }
                if (record.getVehicleClass() != null) {
                    record.setVehicleClassName(iSysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", record.getVehicleClass()));
                }

                if (StringUtils.isNotBlank(vehicleStatus) && StringUtils.isNotBlank(vehicleLength)) {
                    record.setVehicleStatusName(vehicleStatus + "/" + vehicleLength);
                } else if (StringUtils.isNotBlank(vehicleStatus) && StringUtils.isBlank(vehicleLength)) {
                    record.setVehicleStatusName(vehicleStatus);
                } else {
                    record.setVehicleStatusName(null);
                }

                if (record.getRecordState() != null) {
                    if (record.getRecordState() == 0) {
                        record.setRecordStateName("?????????");
                    } else if (record.getRecordState() == 1) {
                        record.setRecordStateName("?????????");
                    } else if (record.getRecordState() == 2) {
                        record.setRecordStateName("?????????");
                    } else {
                        record.setRecordStateName(null);
                    }
                }

                if (record.getCategoryId() != null) {
                    if (record.getCategoryId() == 1) {
                        record.setCategoryName("?????????");
                    } else if (record.getCategoryId() == 2) {
                        record.setCategoryName("?????????");
                    } else if (record.getCategoryId() == 3) {
                        record.setCategoryName("??????????????????");
                    }
                }
            }
        }
        return recordVoPage;
    }

    @Override
    @Async
    public void getViolationDetailsExport(String monthTime, Integer licenceType, Integer recordState, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String monthTimeStart = null;
        String monthTimeEnd = null;
        if (StringUtils.isNotBlank(monthTime)) {
            monthTimeStart = monthTime + "-01";
            monthTimeEnd = monthTime + "-31";
        }
        List<ViolationRecordVo> violationRecordVos = baseMapper.getViolationDetailsExport(monthTimeStart, monthTimeEnd,
                licenceType, recordState, loginInfo.getTenantId());
        // ????????????
        if (violationRecordVos != null && violationRecordVos.size() > 0) {
            for (ViolationRecordVo record : violationRecordVos) {
                if (record.getLicenceType() != null) {
                    record.setLicenceTypeName(iSysStaticDataService.getSysStaticDatas("LICENCE_TYPE", record.getLicenceType()));
                }

                String vehicleStatus = null;
                if (record.getVehicleStatus() != null) {
                    vehicleStatus = iSysStaticDataService.getSysStaticDatas("VEHICLE_STATUS", record.getVehicleStatus());
                }

                String vehicleLength = null;
                if (record.getVehicleLength() != null && record.getVehicleStatus() != null) {
                    String vehicle_length = iSysStaticDataService.getSysStaticDataId("VEHICLE_LENGTH", record.getVehicleStatus().toString());
                    if (StringUtils.isNotBlank(vehicle_length)) {
                        vehicleLength = vehicle_length;
                    }
                }

                if (StringUtils.isNotBlank(vehicleStatus) && StringUtils.isNotBlank(vehicleLength)) {
                    record.setVehicleStatusName(vehicleStatus + "/" + vehicleLength);
                } else if (StringUtils.isNotBlank(vehicleStatus) && StringUtils.isBlank(vehicleLength)) {
                    record.setVehicleStatusName(vehicleStatus);
                } else {
                    record.setVehicleStatusName(null);
                }

                if (record.getRecordState() != null) {
                    if (record.getRecordState() == 0) {
                        record.setRecordStateName("?????????");
                    } else if (record.getRecordState() == 1) {
                        record.setRecordStateName("?????????");
                    } else if (record.getRecordState() == 2) {
                        record.setRecordStateName("?????????");
                    } else {
                        record.setRecordStateName(null);
                    }
                }

                if (record.getCategoryId() != null) {
                    if (record.getCategoryId() == 1) {
                        record.setCategoryName("?????????");
                    } else if (record.getCategoryId() == 2) {
                        record.setCategoryName("?????????");
                    } else if (record.getCategoryId() == 3) {
                        record.setCategoryName("??????????????????");
                    }
                }
            }
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"?????????", "????????????", "??????", "????????????", "???????????????",
                    "????????????", "????????????", "????????????", "??????", "????????????",
                    "????????????", "????????????"};
            resourceFild = new String[]{"getPlateNumber", "getLicenceTypeName", "getVehicleStatusName", "getViolationTime", "getRecordStateName",
                    "getRenfaTime", "getCategoryName", "getViolationFine", "getViolationPoint", "getLocationName",
                    "getViolationAddress", "getViolationReason"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(violationRecordVos, showName, resourceFild, ViolationRecordVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "??????????????????.xlsx", inputStream.available());
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
    public Long getVehicleViolationAmountByMonth(Long vehicleCode, Long tenantId, String month) {
        return baseMapper.getVehicleViolationAmountByMonth(vehicleCode, tenantId, month);
    }

    /**
     * ?????????????????????????????????
     *
     * @param record ViolationRecord
     * @return
     * @throws Exception
     */
    public long saveOrUpdateViolationRecord(SysUser sysUser, ViolationRecord record, String accessToken) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        ViolationRecord violationRecord = null;
        String remark = "";
        SysOperLogConst.OperType operType = SysOperLogConst.OperType.Add;
        boolean create = false;
        if (record.getId() > 0) {
            violationRecord = violationRecordMapper.selectById(record.getId());
            remark = "??????????????????";
            operType = SysOperLogConst.OperType.Update;
        } else {
            violationRecord = new ViolationRecord();
            violationRecord.setCreateTime(LocalDateTime.now());
            violationRecord.setUpdateTime(LocalDateTime.now());
            violationRecord.setAppNotifyDate(formatter.format(new Date()));
            violationRecord.setTenantId(sysUser.getTenantId());
            violationRecord.setRecordState(SysStaticDataEnum.VIOLATION_RECORD_STATE.NOT_HAND);
            violationRecord.setOpId(sysUser.getId());
            remark = "??????????????????";

            VehicleDataInfo vehicleDataInfo = vehicleDataInfoMapper.getVehicleDataInfo(record.getPlateNumber());
            violationRecord.setPlateType(getPlateNumberType(vehicleDataInfo.getVehicleLength(), vehicleDataInfo.getLicenceType(), accessToken));
            create = true;
        }

        if (record.getAccessType() != null) {
            violationRecord.setAccessType(record.getAccessType());
        }
        if (record.getFineImageId() != null) {
            violationRecord.setFineImageId(record.getFineImageId());
        }
        if (StringUtils.isNotBlank(record.getFineImageUrl())) {
            violationRecord.setFineImageUrl(record.getFineImageUrl());
        }

        if (StringUtils.isNotBlank(record.getCategory())) {
            violationRecord.setCategory(record.getCategory());
        }
        if (record.getCategoryId() != null) {
            violationRecord.setCategoryId(record.getCategoryId());
        }

        if (StringUtils.isNotBlank(record.getEngineNo())) {
            violationRecord.setEngineNo(record.getEngineNo());
        }
        if (StringUtils.isNotBlank(record.getLinkMan())) {
            violationRecord.setLinkMan(record.getLinkMan());
        }
        if (record.getLocationid() != null) {
            violationRecord.setLocationid(record.getLocationid());
        }
        if (StringUtils.isNotBlank(record.getLocationName())) {
            violationRecord.setLocationName(record.getLocationName());
        }
        if (StringUtils.isNotBlank(record.getMobilePhone())) {
            violationRecord.setMobilePhone(record.getMobilePhone());
        }
        if (record.getOpId() != null) {
            violationRecord.setOpId(record.getOpId());
        }
        if (record.getOrderId() != null) {
            violationRecord.setOrderId(record.getOrderId());
        }
        if (record.getOverdueFine() != null) {
            violationRecord.setOverdueFine(record.getOverdueFine());
        }
        if (StringUtils.isNotBlank(record.getPlateNumber())) {
            violationRecord.setPlateNumber(record.getPlateNumber());
        }
        if (StringUtils.isNotBlank(record.getPlateType())) {
            violationRecord.setPlateType(record.getPlateType());
        }
        if (null != record.getVehicleCode()) {
            violationRecord.setVehicleCode(record.getVehicleCode());
        }
        if (record.getRecordState() != null) {
            violationRecord.setRecordState(record.getRecordState());
        }
        if (record.getTenantId() != null) {
            violationRecord.setTenantId(record.getTenantId());
        }
        if (StringUtils.isNotBlank(record.getTenantName())) {
            violationRecord.setTenantName(record.getTenantName());
        }
        if (record.getType() != null) {
            violationRecord.setType(record.getType());
        }
        if (record.getUserId() != null) {
            violationRecord.setUserId(record.getUserId());
        }
        if (StringUtils.isNotBlank(record.getVinNo())) {
            violationRecord.setVinNo(record.getVinNo());
        }
        if (StringUtils.isNotBlank(record.getViolationAddress())) {
            violationRecord.setViolationAddress(record.getViolationAddress());
        }
        if (StringUtils.isNotBlank(record.getViolationCode())) {
            violationRecord.setViolationCode(record.getViolationCode());
        }
        if (record.getViolationFine() != null) {
            violationRecord.setViolationFine(record.getViolationFine());
        }
        if (record.getViolationPoint() != null) {
            violationRecord.setViolationPoint(record.getViolationPoint());
        }
        if (StringUtils.isNotBlank(record.getViolationReason())) {
            violationRecord.setViolationReason(record.getViolationReason());
        }
        if (record.getViolationTime() != null) {
            violationRecord.setViolationTime(record.getViolationTime());
        }
        if (record.getRenfaTime() != null) {
            violationRecord.setRenfaTime(record.getRenfaTime());
        }
        if (StringUtils.isNotBlank(record.getViolationWritNo())) {
            violationRecord.setViolationWritNo(record.getViolationWritNo());
        }
        if (StringUtils.isNotBlank(record.getViolationId())) {
            violationRecord.setViolationId(record.getViolationId());
        }

        Map quoteMap = null;
        if (StringUtils.isNoneBlank(violationRecord.getViolationWritNo()) && violationRecord.getRenfaTime() != null && violationRecord.getViolationFine() != null && (violationRecord.getCategoryId() == 2 || create)) {
            Long fine = violationRecord.getViolationFine();//????????????
            fine = null == fine ? 0 : fine / 100;
//            quoteMap = CheMaPaoUtil.queryQuotedPrice(violationRecord.getViolationWritNo(), formatter.format(violationRecord.getRenfaTime()), fine + "");
            Map orderMap = queryDriverViolationInfo(violationRecord.getPlateNumber(), violationRecord.getViolationTime());
            if (orderMap != null) {
                violationRecord.setOrderId(DataFormat.getLongKey(orderMap, "orderId"));
                violationRecord.setUserId(DataFormat.getLongKey(orderMap, "carDriverId"));
                violationRecord.setLinkMan(DataFormat.getStringKey(orderMap, "carDriverMan"));
                violationRecord.setMobilePhone(DataFormat.getStringKey(orderMap, "carDriverPhone"));
            }
            if (quoteMap != null) {
                String waitMoneyStr = DataFormat.getStringKey(quoteMap, "wait_money");
                Long waitMoney = 0L;
                if (StringUtils.isNotBlank(waitMoneyStr)) {
                    waitMoney = Math.round(Double.parseDouble(waitMoneyStr) * 100);
                }
                violationRecord.setOverdueFine(waitMoney);
            }
        }
        if (record.getId() > 0) {
            violationRecordMapper.updateById(violationRecord);
        } else {
            violationRecordMapper.insert(violationRecord);
        }
        if (violationRecord.getCategoryId() == 2 || create) {
            if (quoteMap != null) {
                List<SysStaticData> sysStaticDataList = iSysStaticDataService.getSysStaticDataList("VIOLATION_SERVICE_RATE_BY_AMOUNT", sysUser.getUserInfoId());//??????????????????????????????
                long service_rate = 500L;
                if (sysStaticDataList != null && sysStaticDataList.size() > 0) {
                    service_rate = Long.parseLong(sysStaticDataList.get(0).getCodeValue());
                }
                List<SysStaticData> staticData = iSysStaticDataService.getSysStaticDataList("VIOLATION_SERVICE_USER_ID_CMP", sysUser.getUserInfoId());
                String serviceId = "";
                if (staticData != null && staticData.size() > 0) {
                    serviceId = staticData.get(0).getCodeValue();
                } else {
                    throw new BusinessException("ViolationRecordTask ??????????????????????????????ID??????????????????");
                }
                String cooperFreeStr = DataFormat.getStringKey(quoteMap, "fee");
                Long cooperFree = 0L;
                if (StringUtils.isNotBlank(cooperFreeStr)) {
                    cooperFree = Math.round(Double.parseDouble(cooperFreeStr) * 100);
                }
                Boolean flag = true; // ??????
                ViolationQuotation violationQuotation = null;
                List<ViolationQuotation> violationQuotations = violationQuotationMapper.selectOneByRecordId(record.getId());
                if (violationQuotations != null && violationQuotations.size() > 0) {
                    for (ViolationQuotation quotation : violationQuotations) {
                        if (StringUtils.equals(quotation.getServiceUserId() + "", serviceId)) {
                            violationQuotation = quotation;
                        }
                    }
                }
                if (violationQuotation == null) {
                    violationQuotation = new ViolationQuotation();
                    violationQuotation.setCreateDate(formatter.format(new Date()));
                    violationQuotation.setServiceUserId(Long.parseLong(serviceId));
                    violationQuotation.setRecordId(violationRecord.getId());
                    flag = false; // ??????
                }
                violationQuotation.setUpdateDate(formatter.format(new Date()));
                violationQuotation.setOpId(sysUser.getId());
                violationQuotation.setCooperFree(cooperFree);
                violationQuotation.setPtCooperFree(service_rate);
                violationQuotation.setTotalCooperFree(violationQuotation.getCooperFree() + violationQuotation.getPtCooperFree());
                violationQuotation.setTotalQuotation(violationQuotation.getTotalCooperFree() + (violationRecord.getViolationFine() == null ? 0 : violationRecord.getViolationFine()) + (violationRecord.getOverdueFine() == null ? 0 : violationRecord.getOverdueFine()));

                if (cooperFree > 0) {
                    violationQuotation.setProcessStatus(SysStaticDataEnum.VIOLATION_PROCESS_STATUS.CAN);
                }
                violationQuotation.setQuotationState(SysStaticDataEnum.QUOTATION_STATE.PROCESSED);//????????????

                if (flag) {
                    violationQuotationMapper.updateById(violationQuotation);
                } else {
                    violationQuotationMapper.insert(violationQuotation);
                }
            }
        }

        //??????????????????
        String table = "sys_oper_log_" + TableShardingRule.getShardingIdx(violationRecord.getId());
        SysOperLog operLog = new SysOperLog();
        operLog.setOpName(sysUser.getName());
        operLog.setBusiCode(SysOperLogConst.BusiCode.violationManage.getCode());
        operLog.setBusiName(SysOperLogConst.BusiCode.violationManage.getName());
        operLog.setBusiId(violationRecord.getId());
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(remark);
        sysOperLogMapper.insertSysOperLog(table, operLog);

        return violationRecord.getId();
    }

    /**
     * ??????????????????????????????
     *
     * @param plateNumber
     * @param violationDate
     * @return carDriverPhone ???????????????
     * carDriverId ??????ID
     * orderId ?????????
     * arriveDate ????????????
     * @throws Exception
     */
    public Map queryDriverViolationInfo(String plateNumber, String violationDate) {
        Double estStartTime = sysCfgService.getCfgVal(-1L, "ESTIMATE_START_TIME", 0);
        List<Map> list = orderSchedulerMapper.queryDriverViolationInfo(plateNumber, violationDate, estStartTime, "true");
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            List<Map> listOut = orderSchedulerMapper.queryDriverViolationInfo(plateNumber, violationDate, estStartTime, "false");
            if (listOut != null && listOut.size() > 0) {
                return listOut.get(0);
            } else {
                return null;
            }
        }
    }

    /**
     * ????????????????????????????????????????????????????????????????????????
     *
     * @param vehicleLength
     * @return
     * @throws Exception
     */
    public String getPlateNumberType(String vehicleLength, int licenceType, String accessToken) throws Exception {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (SysStaticDataEnum.LICENCE_TYPE.TT == licenceType) {
            return EnumConsts.PLATE_NUMBER_TYPE.PLATE_NUMBER_TYPE_01;
        }
        String plateNumberType = EnumConsts.PLATE_NUMBER_TYPE.PLATE_NUMBER_TYPE_01;
        if (org.apache.commons.lang.StringUtils.isEmpty(vehicleLength) || !CommonUtil.isNumber(vehicleLength)) {
            return plateNumberType;
        }

        double vehicleLengthDouble = Double.parseDouble(vehicleLength);
        List<SysStaticData> sysStaticDataList = iSysStaticDataService.getSysStaticDataList("VIOLATION_PLATE_TYPE", loginInfo.getUserInfoId());
        double violationPlateTypeSys = 6.8;
        if (sysStaticDataList != null && sysStaticDataList.size() > 0) {
            violationPlateTypeSys = Double.parseDouble(sysStaticDataList.get(0).getCodeValue());
        }
        if (vehicleLengthDouble < violationPlateTypeSys) {
            plateNumberType = EnumConsts.PLATE_NUMBER_TYPE.PLATE_NUMBER_TYPE_02;
        }
        return plateNumberType;
    }


//    private SysUser getSysUser(String accessToken) {
//        LoginInfo loginInfo = loginUtils.get(accessToken);
//        BaseMapper<SysUser> baseMapper = iSysUserService.getBaseMapper();
//        SysUser sysUser = baseMapper.selectById(loginInfo.getId());
//        return sysUser;
//    }

    public static String getLastDayOfMonth(String yearMonth) {
        int year = Integer.parseInt(yearMonth.split("-")[0]);  //???
        int month = Integer.parseInt(yearMonth.split("-")[1]); //???
        Calendar cal = Calendar.getInstance();
        // ????????????
        cal.set(Calendar.YEAR, year);
        // ????????????
        // cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.MONTH, month); //??????????????????????????????
        // ????????????????????????
        //int lastDay = cal.getActualMaximum(Calendar.DATE);
        int lastDay = cal.getMinimum(Calendar.DATE); //??????????????????????????????????????????
        // ????????????????????????????????????
        //cal.set(Calendar.DAY_OF_MONTH, lastDay);
        cal.set(Calendar.DAY_OF_MONTH, lastDay - 1); //????????????????????????1???????????????????????????
        // ???????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime());
    }

}
