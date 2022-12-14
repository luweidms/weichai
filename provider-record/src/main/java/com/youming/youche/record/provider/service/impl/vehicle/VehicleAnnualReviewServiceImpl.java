package com.youming.youche.record.provider.service.impl.vehicle;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.util.StringUtil;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.api.vehicle.IVehicleAnnualReviewService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleAnnualReview;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.VehicleAnnualReviewDto;
import com.youming.youche.record.provider.mapper.vehicle.VehicleAnnualReviewMapper;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExcelParse;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.collections4.CollectionUtils;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @description ????????????
 * @date 2022/1/14 16:45
 */
@DubboService(version = "1.0.0")
public class VehicleAnnualReviewServiceImpl extends BaseServiceImpl<VehicleAnnualReviewMapper, VehicleAnnualReview> implements IVehicleAnnualReviewService {

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    VehicleAnnualReviewMapper vehicleAnnualReviewMapper;

    @Resource
    IVehicleDataInfoService vehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    IUserService iUserService;

    @Resource
    LoginUtils loginUtils;

    @Transactional
    @Override
    public Integer insertOrUpdate(VehicleAnnualReview vehicleAnnualReview, String accessToken) {
        Integer state = 0;
        LoginInfo loginInfo = loginUtils.get(accessToken);
        DecimalFormat df = new DecimalFormat("###.##");
        double annualreviewCost = Double.parseDouble(vehicleAnnualReview.getAnnualreviewCost()); // ????????????
        vehicleAnnualReview.setAnnualreviewCost(df.format(annualreviewCost));
        if (vehicleAnnualReview.getId() != null) {
            // ??????
            vehicleAnnualReview.setUpdateTime(LocalDateTime.now());

            Integer count = baseMapper.judgeDataExist(vehicleAnnualReview.getVehicleId(), vehicleAnnualReview.getEffectiveDate(), vehicleAnnualReview.getAnnualreviewType());
            if (count == 0) {
                state = vehicleAnnualReviewMapper.customUpdate(vehicleAnnualReview);
                saveSysOperLog(SysOperLogConst.BusiCode.annualReview, SysOperLogConst.OperType.Update, "??????", accessToken, vehicleAnnualReview.getId());
            } else { // ?????????0 ???????????????????????? ?????????????????????????????? 666666
                state = 666666;
            }

        } else {
            // ??????
            vehicleAnnualReview.setTenantId(loginInfo.getTenantId());
            vehicleAnnualReview.setCreateTime(LocalDateTime.now());
            vehicleAnnualReview.setUserDataId(loginInfo.getUserInfoId());
            if (loginInfo.getOrgIds() != null && loginInfo.getOrgIds().size() > 0) {
                vehicleAnnualReview.setOrgId(loginInfo.getOrgIds().get(0));
            } else {
                vehicleAnnualReview.setOrgId(-1L);
            }
            Page page = null;
            try {
                page = iUserService.doQueryVehicleForQuick(vehicleAnnualReview.getVehicleCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (page != null && page.getRecords() != null && page.getRecords().size() > 0) {
                VehicleDataInfo records = ((List<VehicleDataInfo>) page.getRecords()).get(0);
                if (records != null) {
                    vehicleAnnualReview.setVehicleId(records.getId());
                }

            }

            // ?????????????????????????????? requestNo
            Integer requestNoExist = baseMapper.judgeRequestNoExist(vehicleAnnualReview.getRequestNo(), loginInfo.getTenantId());
            if (requestNoExist != null && requestNoExist != 0) {
                return 777777;
            }

            // ???????????????????????????????????????
            Integer count = baseMapper.judgeDataExist(vehicleAnnualReview.getVehicleId(), vehicleAnnualReview.getEffectiveDate(), vehicleAnnualReview.getAnnualreviewType());
            if (count == 0) {
                state = vehicleAnnualReviewMapper.customInsert(vehicleAnnualReview);
                saveSysOperLog(SysOperLogConst.BusiCode.annualReview, SysOperLogConst.OperType.Add, "??????", accessToken, vehicleAnnualReview.getId());
            } else { // ?????????0 ???????????????????????? ?????????????????????????????? 666666
                state = 666666;
            }

        }
        return state;
    }

    @Override
    public Integer exitsVehicleCode(String vehicleCode) {
        return vehicleAnnualReviewMapper.exitsVehicleCode(vehicleCode);
    }

    @Override
    public Page<VehicleAnnualReview> getVehicleAnnualReviewList(Page<VehicleAnnualReview> objectPage, VehicleAnnualReview vehicleAnnualReview, String accessToken) {
//        SysUser sysUser = getSysUser(accessToken);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return vehicleAnnualReviewMapper.getVehicleAnnualReviewList(objectPage, vehicleAnnualReview, loginInfo.getTenantId());
    }

    @Override
    @Async
    public void getVehicleAnnualReviewListExport(VehicleAnnualReview vehicleAnnualReview, String accessToken, ImportOrExportRecords importOrExportRecords) {
//        SysUser sysUser = getSysUser(accessToken);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        try {
            List<VehicleAnnualReview> vehicleAnnualReviewList = vehicleAnnualReviewMapper.getVehicleAnnualReviewListExport(vehicleAnnualReview, loginInfo.getTenantId());
            if (vehicleAnnualReviewList != null && vehicleAnnualReviewList.size() > 0) {
                for (VehicleAnnualReview annualReview : vehicleAnnualReviewList) {
                    annualReview.setAnnualreviewData(annualReview.getAnnualreviewData().split(" ")[0]);
                    annualReview.setEffectiveDate(annualReview.getEffectiveDate().split(" ")[0]);
                }
                String[] showName = null;
                String[] resourceFild = null;
                String fileName = null;
                showName = new String[]{"????????????", "??????????????????", "??????????????????", "?????????????????????", "????????????", "????????????",
                        "??????????????????", "????????????", "?????????"};
                resourceFild = new String[]{"getAnnualreviewTypeName", "getAnnualreviewData", "getEffectiveDate", "getAnnualreviewCost", "getLicenceTypeName",
                        "getVehicleCode", "getVehicleAscriptionDep", "getRequestNo", "getServiceProvider"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleAnnualReviewList, showName, resourceFild, VehicleAnnualReview.class,
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
            } else {
                importOrExportRecords.setState(4);
                importOrExportRecords.setFailureReason("??????????????????????????????");
                importOrExportRecords.setRemarks("??????????????????????????????");
                importOrExportRecordsService.update(importOrExportRecords);
            }
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public VehicleAnnualReview queryVehicleAnnualReviewDetails(Long id) {
        return vehicleAnnualReviewMapper.selectAnnualReviewById(id);
    }

    @Transactional
    @Override
    @Async
    public void batchImport(byte[] bytes, String fileName, ImportOrExportRecords record, String accessToken) {
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(fileName, is);
            LoginInfo loginInfo = loginUtils.get(accessToken);
            int maxNum = 300; // ??????????????????
            // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);

            if (rows == 1) {
                throw new BusinessException("??????????????????0???????????????");
            }
            if (rows > maxNum) {
                throw new BusinessException("?????????????????????" + maxNum + "???????????????????????????[" + rows + "]");
            }

            List<VehicleAnnualReview> failureList = new ArrayList<VehicleAnnualReview>();
            List<VehicleAnnualReviewDto> failureListDto = new ArrayList<VehicleAnnualReviewDto>();
            int success = 0;
            for (int i = 2; i <= rows; i++) {
                StringBuffer reasonFailure = new StringBuffer();
                VehicleAnnualReview vehicleAnnualReview = new VehicleAnnualReview();
                // ????????????
                String vehicleCode = parse.readExcelByRowAndCell(sheetNo, i, 1);
                // ????????????
                String requestNo = parse.readExcelByRowAndCell(sheetNo, i, 2);
                // ????????????
                String annualreviewTypeName = parse.readExcelByRowAndCell(sheetNo, i, 3);
                // ??????
                String annualreviewData = parse.readExcelByRowAndCell(sheetNo, i, 4);
                // ???????????????
                String effectiveDate = parse.readExcelByRowAndCell(sheetNo, i, 5);
                // ????????????
                String annualreviewCost = parse.readExcelByRowAndCell(sheetNo, i, 6);
                // ???????????????
                String serviceProvider = parse.readExcelByRowAndCell(sheetNo, i, 7);
                if (StringUtil.isEmpty(requestNo)) {
                    reasonFailure.append("??????????????????");
                }
                if (StringUtil.isEmpty(vehicleCode)) {
                    reasonFailure.append("???????????????");
                }
                if (StringUtil.isEmpty(annualreviewTypeName)) {
                    reasonFailure.append("??????????????????");
                }
                if (StringUtil.isEmpty(annualreviewData)) {
                    reasonFailure.append("????????????????????????");
                }
                if (StringUtil.isEmpty(annualreviewCost)) {
                    reasonFailure.append("????????????????????????");
                }
                if (StringUtil.isEmpty(effectiveDate)) {
                    reasonFailure.append("??????????????????????????????????????????");
                }
                if (StringUtil.isEmpty(serviceProvider)) {
                    reasonFailure.append("??????????????????????????????");
                }

                if ("???????????????".equals(annualreviewTypeName.trim())) {
                    vehicleAnnualReview.setAnnualreviewType("1");
                } else if ("???????????????".equals(annualreviewTypeName.trim())) {
                    vehicleAnnualReview.setAnnualreviewType("2");
                } else {
                    reasonFailure.append("??????????????????????????????????????????/??????????????????");
                }

                //????????????id
                VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getVehicle(vehicleCode, loginInfo.getTenantId());
                if (vehicleDataInfo == null) {
                    reasonFailure.append("??????????????????????????????");
                } else {
                    vehicleAnnualReview.setVehicleId(vehicleDataInfo.getId());
                }
                vehicleAnnualReview.setVehicleCode(vehicleCode);
                vehicleAnnualReview.setRequestNo(requestNo);
                vehicleAnnualReview.setAnnualreviewTypeName(annualreviewTypeName);
                vehicleAnnualReview.setEffectiveDate(effectiveDate);
                vehicleAnnualReview.setAnnualreviewData(annualreviewData);
                vehicleAnnualReview.setAnnualreviewCost(annualreviewCost);
                vehicleAnnualReview.setServiceProvider(serviceProvider);
                //?????????????????????????????????
                if (StringUtils.isEmpty(reasonFailure)) {
                    Integer integer = insertOrUpdate(vehicleAnnualReview, accessToken);
//                    boolean status = BooleanUtils.toBoolean(integer);
                    //??????????????????
                    if (integer == 1) {
                        success++;
                    } else if (integer == 0) {
                        reasonFailure.append("???????????????????????????");
                        failureList.add(vehicleAnnualReview);

                        VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                        BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                        vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                        failureListDto.add(vehicleAnnualReviewDto);
                    } else if (integer == 666666) {
                        reasonFailure.append("?????????????????????????????????");
                        failureList.add(vehicleAnnualReview);

                        VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                        BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                        vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                        failureListDto.add(vehicleAnnualReviewDto);
                    } else if (integer == 777777) {
                        reasonFailure.append("?????????????????????????????????");
                        failureList.add(vehicleAnnualReview);

                        VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                        BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                        vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                        failureListDto.add(vehicleAnnualReviewDto);
                    }
                } else {
                    failureList.add(vehicleAnnualReview);
                    VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                    BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                    vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                    failureListDto.add(vehicleAnnualReviewDto);
                }
            }
            if (CollectionUtils.isNotEmpty(failureListDto)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"????????????", "????????????", "????????????", "??????????????????",
                        "??????????????????", "?????????????????????", "?????????", "????????????"};
                resourceFild = new String[]{"getVehicleCode", "getRequestNo", "getAnnualreviewTypeName", "getAnnualreviewData",
                        "getEffectiveDate", "getAnnualreviewCost", "getServiceProvider", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureListDto, showName, resourceFild, VehicleAnnualReviewDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "????????????????????????.xlsx", inputStream.available());
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "?????????");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "?????????");
                record.setState(3);
            }
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            record.setState(5);
            record.setFailureReason("????????????,????????????????????????????????????????????????");
            record.setRemarks("????????????,????????????????????????????????????????????????");
            String[] showName = new String[]{"??????????????????", "??????????????????", "????????????"};
            String[] resourceFild = new String[]{"getId", "getMediaName", "getFailureReason"};
            try {
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(Lists.newArrayList(record), showName, resourceFild, ImportOrExportRecords.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "????????????????????????.xlsx", inputStream.available());
                record.setFailureUrl(failureExcelPath);
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e1) {
                record.setState(5);
                record.setRemarks("???????????????????????????????????????????????????");
                record.setFailureReason("???????????????????????????????????????????????????");
                e1.printStackTrace();
                throw new BusinessException("???????????????????????????");
            }
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    public List<WorkbenchDto> getTableVehicleCount() {
        LocalDateTime localDateTime = LocalDate.now().plusMonths(1).atStartOfDay();

        return baseMapper.getTableVehicleCount(localDateTime);
    }

    @Override
    public Page<VehicleDataInfo> queryVehicleFromAnnualReview(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        plateNumber = "'" + plateNumber + "'";
        return baseMapper.queryVehicleFromAnnualReview(new Page<VehicleDataInfo>(1, 10), plateNumber, loginInfo.getTenantId(), 1);
    }

    @Override
    public Long getVehicleAnnualreviewCostByMonth(Long vehicleCode, Long tenantId, String month) {
        return baseMapper.getVehicleAnnualreviewCostByMonth(vehicleCode, tenantId, month);
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

}
