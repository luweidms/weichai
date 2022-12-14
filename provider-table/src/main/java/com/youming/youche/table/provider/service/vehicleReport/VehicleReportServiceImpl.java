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
 * ???????????????
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
            showName = new String[]{"??????", "????????????", "?????????",
                    "??????", "????????????", "???????????????", "?????????",
                    "?????????", "??????",
                    "????????????",
                    "????????????", "?????????", "????????????",
                    "????????????", "??????",
                    "????????????", "????????????", "????????????",
                    "????????????", "???????????????"
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
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
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

                // ????????????
                /*
                    tenant_vehicle_rel.org_id
                    vehicle_data_info
                    LEFT JOIN tenant_vehicle_rel ON vehicle_data_info.id = tenant_vehicle_rel.vehicle_Code
                 */

//                // ????????????
//                String vehicleLineFromOrderDataByMonth = iOrderInfoService.getVehicleLineFromOrderDataByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                vehicleReport.setCurrentLine("");

                // ??????????????????????????????????????????
                vehicleReport.setEquipmentFuelConsumption(0L);

                // ??????????????????????????????????????????
                vehicleReport.setEquipmentMileage(0L);

                // ????????????
                Long vehicleClaimAmountByMonth = iVehicleAccidentService.getVehicleClaimAmountByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setVehicleAccident(null == vehicleClaimAmountByMonth ? 0 : vehicleClaimAmountByMonth);

                // ????????????
                Long vehicleViolationAmountByMonth = iViolationRecordService.getVehicleViolationAmountByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setPenaltyForViolation(null == vehicleViolationAmountByMonth ? 0 : vehicleViolationAmountByMonth);

                // ????????????
                Long vehicleAnnualreviewCostByMonth = iVehicleAnnualReviewService.getVehicleAnnualreviewCostByMonth(vehicleDataInfo.getVehicleCode(), tenantId, lastDateStr);
                vehicleReport.setAnnualVehicleReview(null == vehicleAnnualreviewCostByMonth ? 0 : vehicleAnnualreviewCostByMonth);

                // ????????????
                Long vehicleInsurance = 0L;
                // ???????????????
                Long vehicleDepreciation = 0L;
                if (vehicleDataInfo.getVehicleClass() == 1) {
                    TenantVehicleRelInfoDto tenantVehicleRelInfoDto = vehicleDataInfoService.getVehicleInsuranceFee(vehicleDataInfo.getVehicleClass(), vehicleDataInfo.getVehicleCode(), tenantId);
                    if (tenantVehicleRelInfoDto != null) {
                        // ????????????
                        vehicleInsurance = tenantVehicleRelInfoDto.getCollectionInsurance() != null ? tenantVehicleRelInfoDto.getCollectionInsurance() : 0;
                        if (tenantVehicleRelInfoDto.getDepreciatedMonth() != null && tenantVehicleRelInfoDto.getPurchaseDate() != null) {
                            Integer diffMonth = getDiffMonth(tenantVehicleRelInfoDto.getPurchaseDate());// ???????????????????????????????????????????????????
                            if (tenantVehicleRelInfoDto.getDepreciatedMonth() != 0) {
                                // ???????????????
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

                // ????????????
                Long vehicleAffirmIncomeFeeByMonth = iOrderInfoThreeService.getVehicleAffirmIncomeFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, beginDate, endDate);
                vehicleReport.setVehicleRevenue(null == vehicleAffirmIncomeFeeByMonth ? 0 : vehicleAffirmIncomeFeeByMonth);

                // ?????????
                Long maintenanceCost = 0L;
                // ?????????
                Long maintenanceCosts = 0L;
                // ??????
                Long oilCost = 0L;
                // ?????????
                Long roadAndBridgeFee = 0L;
                // ??????
                Long incidental = 0L;

                /**
                 * ??????????????????
                 */
                List<VehicleMiscellaneouFeeDto> vehicleFeeByMonth = iVehicleExpenseDetailedService.getVehicleFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleFeeByMonth != null && vehicleFeeByMonth.size() > 0) {
                    for (VehicleMiscellaneouFeeDto bean : vehicleFeeByMonth) {
                        if (bean.getType() == 64) { // ?????????
                            maintenanceCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 65) { // ?????????
                            maintenanceCosts += bean.getApplyAmountSum();
                        } else if (bean.getType() == 62) { // ??????
                            oilCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 63) { // ?????????
                            roadAndBridgeFee += bean.getApplyAmountSum();
                        } else if (bean.getType() == 67) { // ??????
                            incidental += bean.getApplyAmountSum();
                        }
                    }
                }
                /**
                 * ??????????????????
                 */
                List<VehicleMiscellaneouFeeDto> vehicleOrderFeeByMonth = iOrderCostReportService.getVehicleOrderFeeByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleOrderFeeByMonth != null && vehicleOrderFeeByMonth.size() > 0) {
                    for (VehicleMiscellaneouFeeDto bean : vehicleOrderFeeByMonth) {
                        if (bean.getType() == 64) { // ?????????
                            maintenanceCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 65) { // ?????????
                            maintenanceCosts += bean.getApplyAmountSum();
                        } else if (bean.getType() == 62) { // ??????
                            oilCost += bean.getApplyAmountSum();
                        } else if (bean.getType() == 63) { // ?????????
                            roadAndBridgeFee += bean.getApplyAmountSum();
                        } else if (bean.getType() == 67) { // ??????
                            incidental += bean.getApplyAmountSum();
                        }
                    }
                }

                vehicleReport.setIncidental(incidental);

                /**
                 * ???????????????-????????????
                 */
                List<VehicleAfterServingDto> vehicleAfterServingByMonth = iUserRepairInfoService.getVehicleAfterServingByMonth(vehicleDataInfo.getPlateNumber(), tenantId, lastDateStr);
                if (vehicleAfterServingByMonth != null && vehicleAfterServingByMonth.size() > 0) {
                    for (VehicleAfterServingDto bean : vehicleAfterServingByMonth) {
                        if (null != bean.getState()) {
                            if (bean.getState() == 1) { // 1?????????
                                maintenanceCosts += bean.getTotalFee();
                            } else if (bean.getState() == 2) { // 2?????????
                                maintenanceCost += bean.getTotalFee();
                            }
                        }
                    }
                }

                vehicleReport.setMaintenanceCost(maintenanceCost);
                vehicleReport.setMaintenanceCosts(maintenanceCosts);

                /**
                 * ?????????????????????????????????
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
