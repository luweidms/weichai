package com.youming.youche.table.provider.service.statistic;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.statistic.IStatementCustomerDayService;
import com.youming.youche.table.api.statistic.IStatementOwnCostDayService;
import com.youming.youche.table.domain.statistic.StatementCustomerDay;
import com.youming.youche.table.domain.statistic.StatementOwnCostDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchCustomerInfo;
import com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import com.youming.youche.table.provider.mapper.statistic.StatementCustomerDayMapper;
import com.youming.youche.util.CommonUtils;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
@DubboService(version = "1.0.0")
public class StatementCustomerDayServiceImpl extends BaseServiceImpl<StatementCustomerDayMapper, StatementCustomerDay> implements IStatementCustomerDayService {

    @Resource
    IStatementOwnCostDayService statementOwnCostDayService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    LoginUtils loginUtils;


    @Override
    public StatementDepartmentDto report(String startDate, String endDate, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        StatementDepartmentDto statementDepartmentDto = baseMapper.dailySummary(startDate, endDate, loginInfo.getTenantId());
        if (statementDepartmentDto != null) {
            List<StatementDepartmentDetailDto> list = baseMapper.dailyDetail(startDate, endDate, loginInfo.getTenantId());
            if (list != null && list.size() > 0) {
                statementDepartmentDto.setStatementDepartmentDays(list);
            }
        }
        return statementDepartmentDto;
    }

    @Async
    @Override
    public void exportReport(String startDate, String endDate, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        StatementDepartmentDto statementDepartmentDto = baseMapper.dailySummary(startDate, endDate, loginInfo.getTenantId());
        if (statementDepartmentDto != null) {
            List<StatementDepartmentDetailDto> list = baseMapper.dailyDetail(startDate, endDate, loginInfo.getTenantId());
            //??????
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("sheet1");
            XSSFCellStyle style = workbook.createCellStyle();// ?????????????????????
            XSSFRow row1 = sheet.createRow(0);
            XSSFCell cell1 = row1.createCell(0);
            cell1.setCellValue("??????");
            cell1.setCellStyle(style);
            XSSFCell cell2 = row1.createCell(1);
            cell2.setCellValue("??????");
            cell2.setCellStyle(style);
            XSSFCell cell3 = row1.createCell(2);
            cell3.setCellValue("????????????");
            cell3.setCellStyle(style);
            int cellSum = 3;
            //???????????????
            if (list != null && list.size() > 0) {
                cellSum += list.size();
                for (int i = 0; i < list.size(); i++) {
                    XSSFCell cell = row1.createCell(3 + i);
                    cell.setCellValue(list.get(i).getName());
                    cell.setCellStyle(style);
                }
            }
            //??????25???????????????
            for (int i = 1; i <= 25; i++) {
                XSSFRow row = sheet.createRow(i);
                for (int j = 0; j < cellSum; j++
                ) {
                    XSSFCell cell = row.createCell(j);
                    if (j == 0) {
                        if (i <= 15) {
                            cell.setCellValue("????????????");
                            cell.setCellStyle(style);
                        } else if (i >= 16 && i < 19) {
                            cell.setCellValue("????????????");
                            cell.setCellStyle(style);
                        } else if (i >= 19 && i < 22) {
                            cell.setCellValue("????????????");
                            cell.setCellStyle(style);
                        } else {
                            cell.setCellValue("????????????");
                            cell.setCellStyle(style);
                        }
                    } else if (j == 1) {
                        switch (i) {
                            case 1:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 2:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 3:
                                cell.setCellValue("??????");
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue("??????");
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue("?????????");
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue("?????????");
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue("??????");
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue("??????");
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue("??????");
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue("?????????");
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue("?????????");
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue("????????????");
                                cell.setCellStyle(style);
                                break;
                            default:
                                break;
                        }
                    } else if (j == 2) {
                        switch (i) {
                            case 1:
                                cell.setCellValue(statementDepartmentDto.getOwnIncome());
                                cell.setCellStyle(style);
                                break;
                            case 2:
                                cell.setCellValue(statementDepartmentDto.getOwnCost());
                                cell.setCellStyle(style);
                                break;
                            case 3:
                                cell.setCellValue(statementDepartmentDto.getCash());
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue(statementDepartmentDto.getOilFee());
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue(statementDepartmentDto.getTollFee());
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue(statementDepartmentDto.getParkingFee());
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue(statementDepartmentDto.getSubsidyFee());
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue(statementDepartmentDto.getMiscellaneousFee());
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue(statementDepartmentDto.getInsuranceFee());
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue(statementDepartmentDto.getDriverCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue(statementDepartmentDto.getDriverExpenseFee());
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue(statementDepartmentDto.getMaintenanceFee());
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue(statementDepartmentDto.getMaintainFee());
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue(statementDepartmentDto.getEmployeesCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue(statementDepartmentDto.getOwnGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue(statementDepartmentDto.getDiversionIncome());
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue(statementDepartmentDto.getDiversionCost());
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue(statementDepartmentDto.getDiversionGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue(statementDepartmentDto.getMerchantsIncome());
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue(statementDepartmentDto.getMerchantsCost());
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue(statementDepartmentDto.getMerchantsGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue(statementDepartmentDto.getSumIncome());
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue(statementDepartmentDto.getSumCost());
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue(statementDepartmentDto.getSumMargin());
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue(statementDepartmentDto.getSumMarginRate());
                                cell.setCellStyle(style);
                                break;
                            default:
                                break;
                        }
                    } else {
                        switch (i) {
                            case 1:
                                cell.setCellValue(list.get(j - 3).getOwnIncome());
                                cell.setCellStyle(style);
                                break;
                            case 2:
                                cell.setCellValue(list.get(j - 3).getOwnCost());
                                cell.setCellStyle(style);
                                break;
                            case 3:
                                cell.setCellValue(list.get(j - 3).getCash());
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue(list.get(j - 3).getOilFee());
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue(list.get(j - 3).getTollFee());
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue(list.get(j - 3).getParkingFee());
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue(list.get(j - 3).getSubsidyFee());
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue(list.get(j - 3).getMiscellaneousFee());
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue(list.get(j - 3).getInsuranceFee());
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue(list.get(j - 3).getDriverCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue(list.get(j - 3).getDriverExpenseFee());
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue(list.get(j - 3).getMaintenanceFee());
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue(list.get(j - 3).getMaintainFee());
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue(list.get(j - 3).getEmployeesCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue(list.get(j - 3).getOwnGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue(list.get(j - 3).getDiversionIncome());
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue(list.get(j - 3).getDiversionCost());
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue(list.get(j - 3).getDiversionGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue(list.get(j - 3).getMerchantsIncome());
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue(list.get(j - 3).getMerchantsCost());
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue(list.get(j - 3).getMerchantsGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue(list.get(j - 3).getSumIncome());
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue(list.get(j - 3).getSumCost());
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue(list.get(j - 3).getSumMargin());
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue(list.get(j - 3).getSumMarginRate());
                                cell.setCellStyle(style);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
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
    }

    @Override
    @Transactional
    public void customerStatisticsDayReport(String startDate, String endDate, Long tenantId){
        //????????????????????????
        Date start = DateUtil.parseDate(startDate, DateUtil.DATE_FORMAT);
        Date end = DateUtil.parseDate(endDate, DateUtil.DATE_FORMAT);
        List<Date> dateList = DateUtil.findDates(start,end);
        for(Date date:dateList) {
            LocalDateTime startTime = DateUtil.asLocalDateTime(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            LocalDateTime endTime = DateUtil.asLocalDateTime(calendar.getTime());
            //????????????????????????
            List<CmCustomerInfo> cmCustomerInfoList = baseMapper.queryCustomerId(tenantId);
            if (cmCustomerInfoList != null && cmCustomerInfoList.size() > 0) {
                for (CmCustomerInfo cmCustomerInfo : cmCustomerInfoList) {
                    if (cmCustomerInfo.getState() == null || cmCustomerInfo.getState() == 0) {
                        continue;
                    }
                    StatementCustomerDay statementCustomerDay = new StatementCustomerDay();
                    //??????????????????????????????????????????????????????
                    List<OrderInfoDto> orderInfoList = baseMapper.queryOrderReceivable(startTime, endTime, tenantId, cmCustomerInfo.getId());
                    //????????????
                    Long ownIncome = 0L;
                    //????????????
                    Long ownCost = 0L;
                    //???????????????
                    Long merchantsFee = 0L;
                    //???????????????
                    Long merchantsCost = 0L;
                    //???????????????
                    Long temporaryFee = 0L;
                    //???????????????
                    Long diversionCost = 0L;
                    //??????????????????
                    //??????
                    Long oilFee = 0L;
                    //????????????
                    Long cash = 0L;
                    //?????????
                    Long tollFee = 0L;
                    //??????
                    Long subsidyFee = 0L;
                    //??????
                    Long insuranceFee = 0L;
                    //?????????
                    Long maintenanceFee = 0L;
                    //?????????
                    Long maintainFee = 0L;
                    //????????????
                    Long driverCreditFee = 0L;
                    //????????????
                    Long driverExpenseFee = 0L;
                    //?????????
                    Long parkingFee = 0L;
                    //??????
                    Long miscellaneousFee = 0L;
                    //????????????
                    Long employeesCreditFee = 0L;
                    //??????????????????
                    if (orderInfoList != null && orderInfoList.size() > 0) {
                        for (OrderInfoDto orderInfoDto : orderInfoList
                        ) {
                            if (orderInfoDto.getAffirmIncomeFee() != null) {
                                if (orderInfoDto.getVehicleClass() != null) {
                                    switch (orderInfoDto.getVehicleClass()) {
                                        case 1:
                                            ownIncome += orderInfoDto.getAffirmIncomeFee();
                                            //???????????????????????????
                                           // if (orderInfoDto.getPaymentWay() == 1) {
                                                //?????????????????????????????????
//                                                if (orderInfoDto.getPreTotalFee() != null) {
//                                                    oilFee += orderInfoDto.getPreTotalFee();
//                                                }
                                         ///   } else {
                                                if (orderInfoDto.getPreCashFee() != null) {
                                                    //??????
                                                    cash += orderInfoDto.getPreCashFee();
                                                }
                                                if (orderInfoDto.getFinalFee() != null) {
                                                    //??????
                                                    cash += orderInfoDto.getFinalFee();
                                                }
                                                if (orderInfoDto.getPreOilVirtualFee() != null) {
                                                    //??????
                                                    oilFee += orderInfoDto.getPreOilVirtualFee();
                                                }
                                                if (orderInfoDto.getPreOilFee() != null) {
                                                    //??????
                                                    oilFee += orderInfoDto.getPreOilFee();
                                                }
                                                if (orderInfoDto.getPontagePer() != null) {
                                                    if(orderInfoDto.getPaymentWay() == 1) {
                                                        //?????????
                                                        tollFee += orderInfoDto.getPontagePer();
                                                    }else{
                                                        tollFee += orderInfoDto.getPreEtcFee();
                                                    }
                                                }
                                                if (orderInfoDto.getSalary() != null) {
                                                    //??????
                                                    subsidyFee += orderInfoDto.getSalary();
                                                }
                                                if (orderInfoDto.getInsuranceFee() != null) {
                                                    //??????
                                                    insuranceFee += orderInfoDto.getInsuranceFee();
                                                }
                                          //  }
                                            break;
                                        case 2:
                                            //?????????
                                            merchantsFee += orderInfoDto.getAffirmIncomeFee();
                                            if (orderInfoDto.getTotalFee() != null) {
                                                merchantsCost += orderInfoDto.getTotalFee() -orderInfoDto.getInsuranceFee();;
                                            }
                                            break;
                                        case 3:
                                            //?????????
                                            temporaryFee += orderInfoDto.getAffirmIncomeFee();
                                            if (orderInfoDto.getTotalFee() != null) {
                                                diversionCost += orderInfoDto.getTotalFee() - orderInfoDto.getInsuranceFee();;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    //??????????????????
                    //?????????????????????
                    //????????????id??????????????????
                    StatementOwnCostDay statementOwnCostDay = new StatementOwnCostDay();
                    List<DepartmentReportCostFeeDto> departmentReportCostFeeDtoList = baseMapper.sumReportCostFee(cmCustomerInfo.getId(), startTime, endTime, null, tenantId);
                    if (departmentReportCostFeeDtoList != null) {
                        for (DepartmentReportCostFeeDto departmentReportCostFeeDto : departmentReportCostFeeDtoList) {
                            if (departmentReportCostFeeDto.getFeeName() != null && departmentReportCostFeeDto.getConsumeFee() != null && departmentReportCostFeeDto.getConsumeFee() > 0) {
                                switch (departmentReportCostFeeDto.getFeeName()) {
                                    case "??????":
                                        oilFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "?????????":
                                        tollFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "?????????":
                                        maintenanceFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "?????????":
                                        maintainFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "?????????":
                                        parkingFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "??????":
                                        miscellaneousFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
//                //????????????id???????????????????????????
//                List<DepartmentReportCostFeeDto> departmentZhaoCarFeeDtoList = statementDepartmentDayMapper.sumCarFee(s.getId(), startTime, endTime, 2);
//                if (departmentZhaoCarFeeDtoList != null && departmentZhaoCarFeeDtoList.size() > 0) {
//                    merchantsCost += departmentZhaoCarFeeDtoList.get(0).getConsumeFee() == null ? 0 : departmentZhaoCarFeeDtoList.get(0).getConsumeFee();
//                }
                    //????????????id???????????????????????????
//                List<DepartmentReportCostFeeDto> departmentWaiCarFeeDtoList = statementDepartmentDayMapper.sumCarFee(s.getId(), startTime, endTime, 3);
//                if (departmentWaiCarFeeDtoList != null && departmentWaiCarFeeDtoList.size() > 0) {
//                    diversionCost += departmentWaiCarFeeDtoList.get(0).getConsumeFee() == null ? 0 : departmentWaiCarFeeDtoList.get(0).getConsumeFee();
//                }
                    //????????????????????????
                    driverCreditFee = baseMapper.queryAdvanceCid(cmCustomerInfo.getId(), startTime, endTime, 2, tenantId);
                    //??????????????????
                    employeesCreditFee = baseMapper.queryAdvanceCid(cmCustomerInfo.getId(), startTime, endTime, 1, tenantId);
                    //????????????id?????????????????????????????????????????????
                    //  String vehicleInspectionFeeStr = statementDepartmentDayMapper.carAnnualFee(s.getId(), startTime, endTime, 1);
                    //  vehicleInspectionFee = CommonUtils.objToLongMul100(vehicleInspectionFeeStr);
                    //  String vehicleAccidentFeeStr = statementDepartmentDayMapper.carAccidentFee(s.getId(), startTime, endTime, 1);
                    //  vehicleAccidentFee = CommonUtils.objToLongMul100(vehicleAccidentFeeStr);
                    //????????????id??????????????????????????????????????????
//                if (statementDepartmentDayMapper.carAnnualFee(s.getId(), startTime, endTime, 2) != null) {
//                    Long annulFee = CommonUtils.objToLongMul100(statementDepartmentDayMapper.carAnnualFee(s.getId(), startTime, endTime, 2));
//                    merchantsCost += annulFee;
//                }
                    //????????????id??????????????????????????????????????????
//                if (statementDepartmentDayMapper.carAnnualFee(s.getId(), startTime, endTime, 2) != null) {
//                    Long annulFee = CommonUtils.objToLongMul100(statementDepartmentDayMapper.carAnnualFee(s.getId(), startTime, endTime, 2));
//                    diversionCost += annulFee;
//                }
                    //????????????????????????????????????

                    statementCustomerDay.setOwnIncome(CommonUtils.objToLongDiv100(ownIncome));
                    statementCustomerDay.setMerchantsIncome(CommonUtils.objToLongDiv100(merchantsFee));
                    statementCustomerDay.setDiversionIncome(CommonUtils.objToLongDiv100(temporaryFee));
                    statementCustomerDay.setCustomerId(cmCustomerInfo.getId());
                    statementCustomerDay.setCustomerName(cmCustomerInfo.getCompanyName());
                    statementCustomerDay.setTenantId(tenantId);
                    statementCustomerDay.setCreateDate(DateUtil.asLocalDate(date));
                    statementCustomerDay.setCreateTime(LocalDateTime.now());
                    //?????????????????????oilFee+cash+tollFee+subsidyFee+insuranceFee+maintenanceFee+maintainFee+driverCreditFee+driverExpenseFeeparkingFee+vehicleViolationFee+employeesCreditFee
                    ownCost = oilFee + cash + tollFee + subsidyFee + insuranceFee + maintenanceFee + maintainFee + driverCreditFee + driverExpenseFee + parkingFee + miscellaneousFee + employeesCreditFee;
                    statementCustomerDay.setOwnCost(CommonUtils.objToLongDiv100(ownCost));
                    statementCustomerDay.setMerchantsCost(CommonUtils.objToLongDiv100(merchantsCost));
                    statementCustomerDay.setDiversionCost(CommonUtils.objToLongDiv100(diversionCost));
                    statementCustomerDay.setOwnGrossMargin(statementCustomerDay.getOwnIncome().subtract(statementCustomerDay.getOwnCost()));
                    statementCustomerDay.setMerchantsGrossMargin(statementCustomerDay.getMerchantsIncome().subtract(statementCustomerDay.getMerchantsCost()));
                    statementCustomerDay.setDiversionGrossMargin(statementCustomerDay.getDiversionIncome().subtract(statementCustomerDay.getDiversionCost()));
                    //????????????????????????
                    super.save(statementCustomerDay);
                    //??????????????????????????????
                    statementOwnCostDay.setStatementId(statementCustomerDay.getId());
                    statementOwnCostDay.setOilFee(CommonUtils.objToLongDiv100(oilFee));
                    statementOwnCostDay.setTollFee(CommonUtils.objToLongDiv100(tollFee));
                    statementOwnCostDay.setSubsidyFee(CommonUtils.objToLongDiv100(subsidyFee));
                    statementOwnCostDay.setInsuranceFee(CommonUtils.objToLongDiv100(insuranceFee));
                    statementOwnCostDay.setDriverCreditFee(CommonUtils.objToLongDiv100(driverCreditFee));
                    statementOwnCostDay.setDriverExpenseFee(CommonUtils.objToLongDiv100(driverExpenseFee));
                    statementOwnCostDay.setMaintainFee(CommonUtils.objToLongDiv100(maintainFee));
                    statementOwnCostDay.setMaintenanceFee(CommonUtils.objToLongDiv100(maintenanceFee));
                    statementOwnCostDay.setParkingFee(CommonUtils.objToLongDiv100(parkingFee));
                    statementOwnCostDay.setMiscellaneousFee(CommonUtils.objToLongDiv100(miscellaneousFee));
//                statementOwnCostDay.setVehicleAccidentFee(CommonUtils.objToLongDiv100(vehicleAccidentFee));
//                statementOwnCostDay.setVehicleInspectionFee(CommonUtils.objToLongDiv100(vehicleInspectionFee));
//                statementOwnCostDay.setVehicleViolationFee(CommonUtils.objToLongDiv100(vehicleViolationFee));
                    statementOwnCostDay.setEmployeesCreditFee(CommonUtils.objToLongDiv100(employeesCreditFee));
                    statementOwnCostDay.setCreateTime(LocalDateTime.now());
                    statementOwnCostDay.setCash(CommonUtils.objToLongDiv100(cash));
                    statementOwnCostDay.setType(2);
                    statementOwnCostDayService.save(statementOwnCostDay);
                }
            }
        }
    }

    @Override
    public List<BossWorkbenchCustomerInfo> getTableBossCustomerInfo() {
        return baseMapper.getTableBossCustomerInfo();
    }


//    /**
//     * ??????????????????
//     */
//    public void costFeeAccount(List<DepartmentReportCostFeeDto> costFee, Long oilFee, Long tollFee, Long
//            maintenanceFee, Long maintainFee, Long parkingFee, Long miscellaneousFee) {
//        if (costFee != null) {
//            for (DepartmentReportCostFeeDto departmentReportCostFeeDto : costFee) {
//                if (departmentReportCostFeeDto.getFeeName() != null && departmentReportCostFeeDto.getConsumeFee() != null && departmentReportCostFeeDto.getConsumeFee() > 0) {
//                    switch (departmentReportCostFeeDto.getFeeName()) {
//                        case "??????":
//                            oilFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "?????????":
//                            tollFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "?????????":
//                            maintenanceFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "?????????":
//                            maintainFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "?????????":
//                            parkingFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "??????":
//                            miscellaneousFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        }
//    }

}
