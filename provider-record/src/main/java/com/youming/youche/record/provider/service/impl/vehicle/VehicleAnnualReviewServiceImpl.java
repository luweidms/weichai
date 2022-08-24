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
 * @description 车辆年审
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
        double annualreviewCost = Double.parseDouble(vehicleAnnualReview.getAnnualreviewCost()); // 年审费用
        vehicleAnnualReview.setAnnualreviewCost(df.format(annualreviewCost));
        if (vehicleAnnualReview.getId() != null) {
            // 修改
            vehicleAnnualReview.setUpdateTime(LocalDateTime.now());

            Integer count = baseMapper.judgeDataExist(vehicleAnnualReview.getVehicleId(), vehicleAnnualReview.getEffectiveDate(), vehicleAnnualReview.getAnnualreviewType());
            if (count == 0) {
                state = vehicleAnnualReviewMapper.customUpdate(vehicleAnnualReview);
                saveSysOperLog(SysOperLogConst.BusiCode.annualReview, SysOperLogConst.OperType.Update, "修改", accessToken, vehicleAnnualReview.getId());
            } else { // 不等于0 说明年审信息存在 返回自定义的一个编码 666666
                state = 666666;
            }

        } else {
            // 新增
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

            // 判断申请单号是否重复 requestNo
            Integer requestNoExist = baseMapper.judgeRequestNoExist(vehicleAnnualReview.getRequestNo(), loginInfo.getTenantId());
            if (requestNoExist != null && requestNoExist != 0) {
                return 777777;
            }

            // 判断本次插入的记录是否存在
            Integer count = baseMapper.judgeDataExist(vehicleAnnualReview.getVehicleId(), vehicleAnnualReview.getEffectiveDate(), vehicleAnnualReview.getAnnualreviewType());
            if (count == 0) {
                state = vehicleAnnualReviewMapper.customInsert(vehicleAnnualReview);
                saveSysOperLog(SysOperLogConst.BusiCode.annualReview, SysOperLogConst.OperType.Add, "新增", accessToken, vehicleAnnualReview.getId());
            } else { // 不等于0 说明年审信息存在 返回自定义的一个编码 666666
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
                showName = new String[]{"年审类型", "年审开始日期", "年审截止日期", "年审费用（元）", "牌照类型", "车牌号码",
                        "车辆归属部门", "申请单号", "服务商"};
                resourceFild = new String[]{"getAnnualreviewTypeName", "getAnnualreviewData", "getEffectiveDate", "getAnnualreviewCost", "getLicenceTypeName",
                        "getVehicleCode", "getVehicleAscriptionDep", "getRequestNo", "getServiceProvider"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleAnnualReviewList, showName, resourceFild, VehicleAnnualReview.class,
                        null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "车辆年审信息.xlsx", inputStream.available());
                os.close();
                inputStream.close();
                importOrExportRecords.setMediaUrl(path);
                importOrExportRecords.setState(2);
                importOrExportRecordsService.update(importOrExportRecords);
            } else {
                importOrExportRecords.setState(4);
                importOrExportRecords.setFailureReason("导出失败，没有数据！");
                importOrExportRecords.setRemarks("导出失败，没有数据！");
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
            int maxNum = 300; // 导入上限记录
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);

            if (rows == 1) {
                throw new BusinessException("导入的数量为0，导入失败");
            }
            if (rows > maxNum) {
                throw new BusinessException("导入不支持超过" + maxNum + "条数据，您当前条数[" + rows + "]");
            }

            List<VehicleAnnualReview> failureList = new ArrayList<VehicleAnnualReview>();
            List<VehicleAnnualReviewDto> failureListDto = new ArrayList<VehicleAnnualReviewDto>();
            int success = 0;
            for (int i = 2; i <= rows; i++) {
                StringBuffer reasonFailure = new StringBuffer();
                VehicleAnnualReview vehicleAnnualReview = new VehicleAnnualReview();
                // 车牌号码
                String vehicleCode = parse.readExcelByRowAndCell(sheetNo, i, 1);
                // 申请单号
                String requestNo = parse.readExcelByRowAndCell(sheetNo, i, 2);
                // 年审类型
                String annualreviewTypeName = parse.readExcelByRowAndCell(sheetNo, i, 3);
                // 开始
                String annualreviewData = parse.readExcelByRowAndCell(sheetNo, i, 4);
                // 截止有效期
                String effectiveDate = parse.readExcelByRowAndCell(sheetNo, i, 5);
                // 年审费用
                String annualreviewCost = parse.readExcelByRowAndCell(sheetNo, i, 6);
                // 代办服务商
                String serviceProvider = parse.readExcelByRowAndCell(sheetNo, i, 7);
                if (StringUtil.isEmpty(requestNo)) {
                    reasonFailure.append("申请单号为空");
                }
                if (StringUtil.isEmpty(vehicleCode)) {
                    reasonFailure.append("车牌号为空");
                }
                if (StringUtil.isEmpty(annualreviewTypeName)) {
                    reasonFailure.append("年审类型为空");
                }
                if (StringUtil.isEmpty(annualreviewData)) {
                    reasonFailure.append("车辆年审日期为空");
                }
                if (StringUtil.isEmpty(annualreviewCost)) {
                    reasonFailure.append("车辆年审费用为空");
                }
                if (StringUtil.isEmpty(effectiveDate)) {
                    reasonFailure.append("车辆年审的有效期截止日期为空");
                }
                if (StringUtil.isEmpty(serviceProvider)) {
                    reasonFailure.append("代办年审的服务商为空");
                }

                if ("行驶证年审".equals(annualreviewTypeName.trim())) {
                    vehicleAnnualReview.setAnnualreviewType("1");
                } else if ("营运证年审".equals(annualreviewTypeName.trim())) {
                    vehicleAnnualReview.setAnnualreviewType("2");
                } else {
                    reasonFailure.append("年审类型不正确！（行驶证年审/营运证年审）");
                }

                //查询车辆id
                VehicleDataInfo vehicleDataInfo = vehicleDataInfoService.getVehicle(vehicleCode, loginInfo.getTenantId());
                if (vehicleDataInfo == null) {
                    reasonFailure.append("所属车队没有车辆信息");
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
                //失败原因为空数据可入库
                if (StringUtils.isEmpty(reasonFailure)) {
                    Integer integer = insertOrUpdate(vehicleAnnualReview, accessToken);
//                    boolean status = BooleanUtils.toBoolean(integer);
                    //返回成功数量
                    if (integer == 1) {
                        success++;
                    } else if (integer == 0) {
                        reasonFailure.append("车俩年审保存失败！");
                        failureList.add(vehicleAnnualReview);

                        VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                        BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                        vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                        failureListDto.add(vehicleAnnualReviewDto);
                    } else if (integer == 666666) {
                        reasonFailure.append("当前车辆年审信息存在！");
                        failureList.add(vehicleAnnualReview);

                        VehicleAnnualReviewDto vehicleAnnualReviewDto = new VehicleAnnualReviewDto();
                        BeanUtil.copyProperties(vehicleAnnualReview, vehicleAnnualReviewDto);
                        vehicleAnnualReviewDto.setReasonFailure(reasonFailure.toString());
                        failureListDto.add(vehicleAnnualReviewDto);
                    } else if (integer == 777777) {
                        reasonFailure.append("当前年审申请单号存在！");
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
                showName = new String[]{"车牌号码", "申请单号", "年审类型", "年审开始日期",
                        "年审截止日期", "年审费用（元）", "服务商", "失败原因"};
                resourceFild = new String[]{"getVehicleCode", "getRequestNo", "getAnnualreviewTypeName", "getAnnualreviewData",
                        "getEffectiveDate", "getAnnualreviewCost", "getServiceProvider", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureListDto, showName, resourceFild, VehicleAnnualReviewDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "车辆年审导入失败.xlsx", inputStream.available());
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(success + "条成功" + failureList.size() + "条失败");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "条失败");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "条成功");
                record.setState(3);
            }
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            record.setState(5);
            record.setFailureReason("导入中断,请确认模版或者必填字段是否正确？");
            record.setRemarks("导入中断,请确认模版或者必填字段是否正确？");
            String[] showName = new String[]{"导入文件编号", "导入文件名称", "失败原因"};
            String[] resourceFild = new String[]{"getId", "getMediaName", "getFailureReason"};
            try {
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(Lists.newArrayList(record), showName, resourceFild, ImportOrExportRecords.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "车辆年审导入失败.xlsx", inputStream.available());
                record.setFailureUrl(failureExcelPath);
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e1) {
                record.setState(5);
                record.setRemarks("导入失败，请确认模版数据是否正确！");
                record.setFailureReason("导入失败，请确认模版数据是否正确！");
                e1.printStackTrace();
                throw new BusinessException("网络异常，请重试！");
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

}
