package com.youming.youche.record.provider.service.impl.vehicle;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.api.vehicle.IVehicleAccidentService;
import com.youming.youche.record.domain.vehicle.VehicleAccident;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.provider.mapper.vehicle.VehicleAccidentMapper;
import com.youming.youche.record.vo.VehicledentAccidentVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.ISysUserOrgRelService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author hzx
 * @description ????????????
 * @date 2022/1/15 16:15
 */
@DubboService(version = "1.0.0")
public class VehicleAccidentServiceImpl extends BaseServiceImpl<VehicleAccidentMapper, VehicleAccident> implements IVehicleAccidentService {

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    private VehicleAccidentMapper vehicleAccidentMapper;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysUserOrgRelService iSysUserOrgRelService;

    @Resource
    private IUserService iUserService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysStaticDataService iSysStaticDataService;

    @Transactional
    @Override
    public Integer insertAccidentRecord(VehicleAccident vehicleAccident, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        Page page = null;
        try {
            page = iUserService.doQueryVehicleForQuick(vehicleAccident.getVehicleCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        VehicleDataInfo records = ((List<VehicleDataInfo>) page.getRecords()).get(0);
        if (records != null) {
            vehicleAccident.setVehicleId(records.getId());
        }

        vehicleAccident.setTenantId(loginInfo.getTenantId());
        vehicleAccident.setAccidentStatus("1");
        vehicleAccident.setCreateTime(LocalDateTime.now());
        if (loginInfo.getOrgIds() != null && loginInfo.getOrgIds().size() > 0) {
            vehicleAccident.setOrgId(loginInfo.getOrgIds().get(0));
        } else {
            vehicleAccident.setOrgId(-1L);
        }
        vehicleAccident.setUserDataId(loginInfo.getUserInfoId());
        Integer status = vehicleAccidentMapper.insertAccidentRecord(vehicleAccident);
        saveSysOperLog(SysOperLogConst.BusiCode.accident, SysOperLogConst.OperType.Add, "??????", accessToken, vehicleAccident.getId()); // ????????????
        return status;
    }

    @Transactional
    @Override
    public Integer insertTheDamageRecord(VehicleAccident vehicleAccident, String accessToken) {
        vehicleAccident.setAccidentStatus("2");
        DecimalFormat df = new DecimalFormat("###.##");
        double vehicleDamage = Double.parseDouble(vehicleAccident.getVehicleDamage());// ????????????
        double otherDamage = Double.parseDouble(vehicleAccident.getOtherDamage());// ????????????
        double roadLoss = Double.parseDouble(vehicleAccident.getRoadLoss());// ????????????
        vehicleAccident.setVehicleDamage(df.format(vehicleDamage));
        vehicleAccident.setOtherDamage(df.format(otherDamage));
        vehicleAccident.setRoadLoss(df.format(roadLoss));
        Integer status = vehicleAccidentMapper.updateAccidentById(vehicleAccident);
        saveSysOperLog(SysOperLogConst.BusiCode.accident, SysOperLogConst.OperType.Add, "??????", accessToken, vehicleAccident.getId()); // ????????????
        return status;
    }

    @Transactional
    @Override
    public Integer insertNuclearDamageRecord(VehicleAccident vehicleAccident, String accessToken) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vehicleAccident.setAccidentStatus("3");
        DecimalFormat df = new DecimalFormat("###.##");
        double claimAmount = Double.parseDouble(vehicleAccident.getClaimAmount());// ????????????
        vehicleAccident.setClaimAmount(df.format(claimAmount));
        vehicleAccident.setClaimDate(sdf.format(new Date()));
        Integer status = vehicleAccidentMapper.updateAccidentById(vehicleAccident);
        saveSysOperLog(SysOperLogConst.BusiCode.accident, SysOperLogConst.OperType.Add, "??????", accessToken, vehicleAccident.getId()); // ????????????
        return status;
    }

    @Override
    public VehicleAccident selectAccidentById(Long id) {
        return vehicleAccidentMapper.selectAccidentById(id);
    }

    @Transactional
    @Override
    public Integer updateAccidentRecord(VehicleAccident vehicleAccident, String accessToken) {
        DecimalFormat df = new DecimalFormat("###.##");
        if (StringUtils.isNotBlank(vehicleAccident.getVehicleDamage())) {
            double vehicleDamage = Double.parseDouble(vehicleAccident.getVehicleDamage());// ????????????
            vehicleAccident.setVehicleDamage(df.format(vehicleDamage));
        }
        if (StringUtils.isNotBlank(vehicleAccident.getOtherDamage())) {
            double otherDamage = Double.parseDouble(vehicleAccident.getOtherDamage());// ????????????
            vehicleAccident.setOtherDamage(df.format(otherDamage));
        }
        if (StringUtils.isNotBlank(vehicleAccident.getRoadLoss())) {
            double roadLoss = Double.parseDouble(vehicleAccident.getRoadLoss());// ????????????
            vehicleAccident.setRoadLoss(df.format(roadLoss));
        }
        if (StringUtils.isNotBlank(vehicleAccident.getClaimAmount())) {
            double claimAmount = Double.parseDouble(vehicleAccident.getClaimAmount());// ????????????
            vehicleAccident.setClaimAmount(df.format(claimAmount));
        }
        vehicleAccident.setUpdateTime(LocalDateTime.now());
        Integer status = vehicleAccidentMapper.updateAccidentById(vehicleAccident);
        saveSysOperLog(SysOperLogConst.BusiCode.accident, SysOperLogConst.OperType.Update, "??????", accessToken, vehicleAccident.getId()); // ????????????
        return status;
    }

    @Override
    public Page<VehicleAccident> queryAllRecord(Page<VehicleAccident> objectPage,
                                                String vehicleCode, String accidentStatus,
                                                String reportDateStart, String reportDateEnd,
                                                String accidentDateStart, String accidentDateEnd,
                                                String createDateStart, String createDateEnd, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<VehicleAccident> vehicleAccidentPage = vehicleAccidentMapper.queryAllRecord(objectPage, vehicleCode, accidentStatus,
                reportDateStart, reportDateEnd, accidentDateStart, accidentDateEnd, createDateStart, createDateEnd, loginInfo.getTenantId());
        return vehicleAccidentPage;
    }

    @Override
    @Async
    public void queryAllRecordExport(String vehicleCode, String accidentStatus, String reportDateStart, String reportDateEnd,
                                     String accidentDateStart, String accidentDateEnd, String createDateStart,
                                     String createDateEnd, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<VehicleAccident> vehicleAccidents = vehicleAccidentMapper.queryAllRecordExport(vehicleCode, accidentStatus,
                reportDateStart, reportDateEnd, accidentDateStart, accidentDateEnd, createDateStart, createDateEnd, loginInfo.getTenantId());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (VehicleAccident vehicleAccident : vehicleAccidents) {
            vehicleAccident.setCreateDate(df.format(vehicleAccident.getCreateTime()));
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"????????????", "????????????", "????????????", "????????????", "????????????", "????????????",
                    "????????????", "????????????", "????????????", "????????????", "????????????"};
            resourceFild = new String[]{"getVehicleCode", "getAccidentStatusName", "getOrgName", "getBrandName", "getBrandType", "getInsuranceCompany",
                    "getInsured", "getReportDate", "getAccidentDate", "getClaimAmount", "getCreateDate"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleAccidents, showName, resourceFild, VehicleAccident.class,
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
    public Page<VehicledentAccidentVo> getVehicledentAccident(Page<VehicledentAccidentVo> page, String monthTime, Integer licenceType, Integer accidentStatus, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String monthTimeStart = null;
        String monthTimeEnd = null;
        if (StringUtils.isNotEmpty(monthTime)) {
            monthTimeStart = monthTime + "-01";
            monthTimeEnd = getLastDayOfMonth(monthTime);
        }
        Page<VehicledentAccidentVo> vehicledentAccidentVoPage = baseMapper.getVehicledentAccident(page, monthTimeStart,
                monthTimeEnd, licenceType, accidentStatus, loginInfo.getTenantId());
        if (vehicledentAccidentVoPage.getRecords() != null && vehicledentAccidentVoPage.getRecords().size() > 0) {
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
            for (VehicledentAccidentVo record : vehicledentAccidentVoPage.getRecords()) {

                if (record.getVehicleStatus() != null && record.getVehicleStatus() > 0) {
                    Long vehicleStatus1 = Long.valueOf(record.getVehicleStatus());
                    for (SysStaticData sysStaticData : vehicleStatusList) {
                        if (sysStaticData.getId().equals(vehicleStatus1)) {
                            record.setVehicleStatusName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }

                if (record.getLicenceType() != null) {
                    if (record.getLicenceType().equals(1)) {
                        record.setLicenceTypeName("??????");
                    } else if (record.getLicenceType().equals(2)) {
                        record.setLicenceTypeName("??????");
                    }
                }

                if (record.getMaterialAmage() != null) {
                    if (record.getMaterialAmage().equals(0)) {
                        record.setMaterialAmageName("???");
                    } else if (record.getMaterialAmage().equals(1)) {
                        record.setMaterialAmageName("???");
                    }
                }
                if (record.getWounding() != null) {
                    if (record.getWounding().equals(0)) {
                        record.setWoundingName("???");
                    } else if (record.getWounding().equals(1)) {
                        record.setWoundingName("???");
                    }
                }
                if (record.getAccidentType() != null) {
                    if (record.getAccidentType().equals(1)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(2)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(3)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(4)) {
                        record.setAccidentTypeName("???????????????");
                    } else if (record.getAccidentType().equals(5)) {
                        record.setAccidentTypeName("???????????????");
                    } else if (record.getAccidentType().equals(6)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(7)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(8)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(9)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(10)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(11)) {
                        record.setAccidentTypeName("????????????");
                    }
                }
                if (record.getVehicleClass() != null) {
                    record.setVehicleClassName(iSysStaticDataService.getSysStaticDatas("VEHICLE_CLASS", record.getVehicleClass()));
                }
            }
        }
        return vehicledentAccidentVoPage;
    }

    @Override
    @Async
    public void getVehicledentAccidentExport(String monthTime, Integer licenceType, Integer accidentStatus, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String monthTimeStart = null;
        String monthTimeEnd = null;
        if (StringUtils.isNotEmpty(monthTime)) {
            monthTimeStart = monthTimeStart + "-01";
            monthTimeEnd = monthTimeEnd + "-31";
        }

        List<VehicledentAccidentVo> list = baseMapper.getVehicledentAccidentExport(monthTimeStart, monthTimeEnd,
                licenceType, accidentStatus, loginInfo.getTenantId());

        if (list != null && list.size() > 0) {
            // ????????????????????????
            List<SysStaticData> vehicleStatusList = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("VEHICLE_STATUS"));
            for (VehicledentAccidentVo record : list) {
                if (record.getVehicleStatus() != null && record.getVehicleStatus() > 0) {
                    Long vehicleStatus1 = Long.valueOf(record.getVehicleStatus());
                    for (SysStaticData sysStaticData : vehicleStatusList) {
                        if (sysStaticData.getId().equals(vehicleStatus1)) {
                            record.setVehicleStatusName(sysStaticData.getCodeName());
                            break;
                        }
                    }
                }
                if (record.getLicenceType() != null) {
                    if (record.getLicenceType().equals(1)) {
                        record.setLicenceTypeName("??????");
                    } else if (record.getLicenceType().equals(2)) {
                        record.setLicenceTypeName("??????");
                    }
                }

                if (record.getMaterialAmage() != null) {
                    if (record.getMaterialAmage().equals(0)) {
                        record.setMaterialAmageName("???");
                    } else if (record.getMaterialAmage().equals(1)) {
                        record.setMaterialAmageName("???");
                    }
                }
                if (record.getWounding() != null) {
                    if (record.getWounding().equals(0)) {
                        record.setWoundingName("???");
                    } else if (record.getWounding().equals(1)) {
                        record.setWoundingName("???");
                    }
                }
                if (record.getAccidentType() != null) {
                    if (record.getAccidentType().equals(1)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(2)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(3)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(4)) {
                        record.setAccidentTypeName("???????????????");
                    } else if (record.getAccidentType().equals(5)) {
                        record.setAccidentTypeName("???????????????");
                    } else if (record.getAccidentType().equals(6)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(7)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(8)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(9)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(10)) {
                        record.setAccidentTypeName("????????????");
                    } else if (record.getAccidentType().equals(11)) {
                        record.setAccidentTypeName("????????????");
                    }
                }
                if (record.getUserId() != null) {
                    List<String> orgNameByUserId = iSysUserOrgRelService.getOrgNameByUserId(record.getUserId());
                    if (orgNameByUserId != null && orgNameByUserId.size() > 0) {
                        if (orgNameByUserId.size() == 1) {
                            record.setDepartmentName(orgNameByUserId.get(0));
                        } else {
                            String orgName = "";
                            int a = orgNameByUserId.size() - 1;
                            for (int i = 0; i < orgNameByUserId.size(); i++) {
                                if (a == i) {
                                    orgName = orgName + orgNameByUserId.get(i);
                                } else {
                                    orgName = orgName + orgNameByUserId.get(i) + ",";
                                }
                            }
                            record.setDepartmentName(orgName);
                        }
                    }
                }
            }
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"?????????", "????????????", "??????", "????????????", "????????????",
                    "????????????", "????????????", "????????????", "????????????", "????????????",
                    "????????????", "????????????", "????????????", "????????????", "????????????",
                    "????????????", "??????", "????????????", "????????????", "????????????",
                    "????????????"};
            resourceFild = new String[]{"getPlateNumber", "getLicenceTypeName", "getVehicleStatusName", "getDepartmentName", "getBrandModel",
                    "getVehicleModel", "getAccidentDate", "getAccidentPlace", "getAccidentCause", "getAccidentTypeName",
                    "getVehicleDamage", "getOtherDamage", "getRoadLoss", "getMaterialAmageName", "getWoundingName",
                    "getAccidentDriver", "getMainDriver", "getInsuranceCompany", "getInsured", "getReportDate",
                    "getClaimAmount"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, VehicledentAccidentVo.class,
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

    /**
     * ????????????
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

    @Override
    public Long getVehicleClaimAmountByMonth(Long vehicleCode, Long tenantId, String month) {
        return baseMapper.getVehicleClaimAmountByMonth(vehicleCode, tenantId, month);
    }

}
