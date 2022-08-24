package com.youming.youche.table.provider.service.vehicleReport;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.IVehicleExpenseDetailedService;
import com.youming.youche.finance.api.order.IOrderCostReportService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.dto.user.VehicleAfterServingDto;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.dto.VehicleFeeFromOrderDataDto;
import com.youming.youche.record.api.vehicle.IVehicleAccidentService;
import com.youming.youche.record.api.vehicle.IVehicleAnnualReviewService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.api.violation.IViolationRecordService;
import com.youming.youche.record.dto.TenantVehicleRelInfoDto;
import com.youming.youche.record.dto.vehicle.VehicleReportVehicleDataDto;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.table.api.vehicleReport.IVehicleReportService;
import com.youming.youche.table.domain.vehicleReport.VehicleReport;
import com.youming.youche.table.domain.workbench.BossWorkbenchInfo;
import com.youming.youche.table.provider.mapper.vehicleReport.VehicleReportMapper;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wuhao
 * @since 2022-05-06
 */
@DubboService(version = "1.0.0")
public class VehicleReportServiceImpl extends BaseServiceImpl<VehicleReportMapper, VehicleReport> implements IVehicleReportService {

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService vehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    IVehicleAccidentService iVehicleAccidentService;

    @DubboReference(version = "1.0.0")
    IVehicleAnnualReviewService iVehicleAnnualReviewService;

    @DubboReference(version = "1.0.0")
    IViolationRecordService iViolationRecordService;

    @DubboReference(version = "1.0.0")
    IOrderInfoThreeService iOrderInfoThreeService;

    @DubboReference(version = "1.0.0")
    IVehicleExpenseDetailedService iVehicleExpenseDetailedService;

    @DubboReference(version = "1.0.0")
    IOrderCostReportService iOrderCostReportService;

    @DubboReference(version = "1.0.0")
    IUserRepairInfoService iUserRepairInfoService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService iOrderInfoService;

    @Lazy
    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @Resource
    LoginUtils loginUtils;

    @Override
    @Async
    public void CarDownload(String accessToken, ImportOrExportRecords record, String carNumber, String startMouth, String endMonth, String currentLine, String department) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<VehicleReport> records = baseMapper.queryVehicleReportData(loginInfo.getTenantId(), carNumber, startMouth, endMonth, currentLine, department);
        for (VehicleReport vehicleReport : records) {
            String vehicleLineFromOrderDataByMonth = iOrderInfoService.getVehicleLineFromOrderDataByMonth(vehicleReport.getCarNumber(), loginInfo.getTenantId(), "");
            vehicleReport.setCurrentLine(vehicleLineFromOrderDataByMonth);
            vehicleReport.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), vehicleReport.getOrgId()));
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"部门", "当前线路", "车牌号",
                    "月份", "费用合计", "车辆折旧费", "维修费",
                    "保养费", "油费",
                    "设备油耗",
                    "设备里程", "路桥费", "车辆事故",
                    "违章罚款", "杂费",
                    "车辆年审", "车辆保险", "车辆收入",
                    "车辆毛利", "车辆毛利率"
            };
            resourceFild = new String[]{
                    "getOrgName", "getCurrentLine", "getCarNumber",
                    "getMouth", "getTotalExpenses", "getVehicleDepreciationDouble", "getMaintenanceCostDouble",
                    "getMaintenanceCostsDouble", "getOilCostDouble",
                    "getEquipmentFuelConsumptionDouble",
                    "getEquipmentMileageDouble", "getRoadAndBridgeFeeDouble", "getVehicleAccident",
                    "getPenaltyForViolationDouble", "getIncidentalDouble",
                    "getAnnualVehicleReview", "getVehicleInsurance", "getVehicleRevenueDouble",
                    "getVehicleGrossProfit", "getGrossProfitMarginOfVehicle"
            };
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, VehicleReport.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "车辆报表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    public List<BossWorkbenchInfo> getTableVehicleList() {
        return baseMapper.getTableVehicleList();
    }

    @Override
    public void execute(String lastDateStr) {

        Map<String, String> dateStr = DateUtil.getLastOneMonthDay(null);
        String beginDate = dateStr.get("beginDate");
        String endDate = dateStr.get("endDate");

        List<VehicleReportVehicleDataDto> vehicleData = vehicleDataInfoService.getVehicleDataAll();
        Map<Long, List<VehicleReportVehicleDataDto>> kTenantVVehicleData = vehicleData.stream().collect(Collectors.groupingBy(VehicleReportVehicleDataDto::getTenantId));

        for (Long tenantId : kTenantVVehicleData.keySet()) {

            List<VehicleReportVehicleDataDto> vehicleDataInfos = kTenantVVehicleData.get(tenantId);

            for (VehicleReportVehicleDataDto vehicleDataInfo : vehicleDataInfos) {

                VehicleReport vehicleReport = new VehicleReport();

                vehicleReport.setVehicleCode(vehicleDataInfo.getVehicleCode());
                vehicleReport.setCarNumber(vehicleDataInfo.getPlateNumber());
                vehicleReport.setTenantId(tenantId);
                vehicleReport.setCreateTime(LocalDateTime.now());
                vehicleReport.setMouth(lastDateStr);

                // 归属部门
                /*
                    tenant_vehicle_rel.org_id
                    vehicle_data_info
                    LEFT JOIN tenant_vehicle_rel ON vehicle_data_info.id = tenant_vehicle_rel.vehicle_Code
                 */

//                // 线路名称
//                String vehicleLineFromOrderDataByMonth = iOrderInfoService.getVehicleLineFromOrderDataByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                vehicleReport.setCurrentLine("");

                // 设备油耗，对接车联网设备获取
                vehicleReport.setEquipmentFuelConsumption(0L);

                // 设备里程，对接车联网设备获取
                vehicleReport.setEquipmentMileage(0L);

                // 车辆事故
                Long vehicleClaimAmountByMonth = iVehicleAccidentService.getVehicleClaimAmountByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setVehicleAccident(null == vehicleClaimAmountByMonth ? 0 : vehicleClaimAmountByMonth);

                // 违章罚款
                Long vehicleViolationAmountByMonth = iViolationRecordService.getVehicleViolationAmountByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setPenaltyForViolation(null == vehicleViolationAmountByMonth ? 0 : vehicleViolationAmountByMonth);

                // 车辆年审
                Long vehicleAnnualreviewCostByMonth = iVehicleAnnualReviewService.getVehicleAnnualreviewCostByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setAnnualVehicleReview(null == vehicleAnnualreviewCostByMonth ? 0 : vehicleAnnualreviewCostByMonth);

                // 车辆保险
                Long vehicleInsurance = 0L;
                // 车辆折旧费
                Long vehicleDepreciation = 0L;
                if (vehicleDataInfo.getVehicleClass() == 1) {
                    TenantVehicleRelInfoDto tenantVehicleRelInfoDto = vehicleDataInfoService.getVehicleInsuranceFee(vehicleDataInfo.getVehicleClass(), vehicleDataInfo.getVehicleCode(), tenantId);
                    if (tenantVehicleRelInfoDto != null) {
                        // 车辆保险
                        vehicleInsurance = tenantVehicleRelInfoDto.getCollectionInsurance() != null ? tenantVehicleRelInfoDto.getCollectionInsurance() : 0;
                        if (tenantVehicleRelInfoDto.getDepreciatedMonth() != null && tenantVehicleRelInfoDto.getPurchaseDate() != null) {
                            Integer diffMonth = getDiffMonth(tenantVehicleRelInfoDto.getPurchaseDate());// 购买时间（已过折旧月计算所需字段）
                            if (tenantVehicleRelInfoDto.getDepreciatedMonth() != 0) {
                                // 车辆折旧费
                                double vehicleDepreciationDouble =
                                        ((null == tenantVehicleRelInfoDto.getPrice() ? 0 : tenantVehicleRelInfoDto.getPrice()) - (null == tenantVehicleRelInfoDto.getResidual() ? 0 : tenantVehicleRelInfoDto.getResidual()))
                                                / (double) tenantVehicleRelInfoDto.getDepreciatedMonth() * diffMonth;
                                String format = new DecimalFormat("#").format(vehicleDepreciationDouble);
                                vehicleDepreciation = Long.parseLong(format);

                            }
                        }
                    }
                }
                vehicleReport.setVehicleInsurance(vehicleInsurance);
                vehicleReport.setVehicleDepreciation(vehicleDepreciation);

                // 车辆收入
                Long vehicleAffirmIncomeFeeByMonth = iOrderInfoThreeService.getVehicleAffirmIncomeFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, beginDate, endDate);
                vehicleReport.setVehicleRevenue(null == vehicleAffirmIncomeFeeByMonth ? 0 : vehicleAffirmIncomeFeeByMonth);

                // 维修费
                Long maintenanceCost = 0L;
                // 保养费
                Long maintenanceCosts = 0L;
                // 油费
                Long oilCost = 0L;
                // 路桥费
                Long roadAndBridgeFee = 0L;
                // 杂费
                Long incidental = 0L;

                /**
                 * 车辆费用上报
                 */
                List<VehicleMiscellaneouFeeDto> vehicleFeeByMonth = iVehicleExpenseDetailedService.getVehicleFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleFeeByMonth != null && vehicleFeeByMonth.size() > 0) {
                    for (VehicleMiscellaneouFeeDto bean : vehicleFeeByMonth) {
                        if (bean.getType() == 64) { // 维修费
                            maintenanceCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 65) { // 保养费
                            maintenanceCosts += bean.getApplyAmountSum();
                        } else if (bean.getType() == 62) { // 油费
                            oilCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 63) { // 路桥费
                            roadAndBridgeFee += bean.getApplyAmountSum();
                        } else if (bean.getType() == 67) { // 杂费
                            incidental += bean.getApplyAmountSum();
                        }
                    }
                }
                /**
                 * 订单上报费用
                 */
                List<VehicleMiscellaneouFeeDto> vehicleOrderFeeByMonth = iOrderCostReportService.getVehicleOrderFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleOrderFeeByMonth != null && vehicleOrderFeeByMonth.size() > 0) {
                    for (VehicleMiscellaneouFeeDto bean : vehicleOrderFeeByMonth) {
                        if (bean.getType() == 64) { // 维修费
                            maintenanceCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 65) { // 保养费
                            maintenanceCosts += bean.getApplyAmountSum();
                        } else if (bean.getType() == 62) { // 油费
                            oilCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 63) { // 路桥费
                            roadAndBridgeFee += bean.getApplyAmountSum();
                        } else if (bean.getType() == 67) { // 杂费
                            incidental += bean.getApplyAmountSum();
                        }
                    }
                }

                vehicleReport.setIncidental(incidental);

                /**
                 * 维修保养费-后服模块
                 */
                List<VehicleAfterServingDto> vehicleAfterServingByMonth = iUserRepairInfoService.getVehicleAfterServingByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleAfterServingByMonth != null && vehicleAfterServingByMonth.size() > 0) {
                    for (VehicleAfterServingDto bean : vehicleAfterServingByMonth) {
                        if (null != bean.getState()) {
                            if (bean.getState() == 1) { // 1保养费
                                maintenanceCosts += bean.getTotalFee();
                            } else if (bean.getState() == 2) { // 2维修费
                                maintenanceCost += bean.getTotalFee();
                            }
                        }
                    }
                }

                vehicleReport.setMaintenanceCost(maintenanceCost);
                vehicleReport.setMaintenanceCosts(maintenanceCosts);

                /**
                 * 订单分配的油费和路桥费
                 */
                        VehicleFeeFromOrderDataDto vehicleFeeFromOrderDataByMonth = iOrderInfoService.getVehicleFeeFromOrderDataByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleFeeFromOrderDataByMonth != null) {
                    oilCost += null == vehicleFeeFromOrderDataByMonth.getOilFee() ? 0 : vehicleFeeFromOrderDataByMonth.getOilFee();
                    roadAndBridgeFee += null == vehicleFeeFromOrderDataByMonth.getEtcFee() ? 0 : vehicleFeeFromOrderDataByMonth.getEtcFee();
                }

                vehicleReport.setOilCost(oilCost);
                vehicleReport.setRoadAndBridgeFee(roadAndBridgeFee);

                baseMapper.insert(vehicleReport);
            }
        }

    }

    @Override
    public PageInfo<VehicleReport> queryVehicleReportData(String carNumber, String startMonth, String endMonth, String currentLine, String department, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<VehicleReport> pageInfo = new PageInfo<VehicleReport>(
                baseMapper.queryVehicleReportData(loginInfo.getTenantId(), carNumber, startMonth, endMonth, currentLine, department)
        );

        List<VehicleReport> list = pageInfo.getList();
        for (VehicleReport vehicleReport : list) {
            String vehicleLineFromOrderDataByMonth = iOrderInfoService.getVehicleLineFromOrderDataByMonth(vehicleReport.getCarNumber(), loginInfo.getTenantId(), "");
            vehicleReport.setCurrentLine(vehicleLineFromOrderDataByMonth);
            vehicleReport.setOrgName(iSysOrganizeService.getCurrentTenantOrgNameById(loginInfo.getTenantId(), vehicleReport.getOrgId()));
        }

        return pageInfo;
    }

    private String getLocalDateTimeToStr(LocalDateTime time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM");
        return df.format(time);
    }

    private Integer getDiffMonth(LocalDate purchaseDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        String[] split = purchaseDate.format(fmt).split("-");
        String[] split1 = getLastDate().split("-");
        int year = Integer.valueOf(split1[0]) - Integer.valueOf(split[0]);
        int month = Integer.valueOf(split1[1]) - Integer.valueOf(split[1]);
        return year * 12 + Math.abs(month) - 1;
    }

    /**
     * @return yyyy-MM
     */
    private static String getLastDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return sdf.format(cal.getTime());
    }

}
