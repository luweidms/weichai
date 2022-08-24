package com.youming.youche.table.provider.service.statistic;


import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOrganize;
import com.youming.youche.table.api.statistic.IStatementDepartmentDayService;
import com.youming.youche.table.api.statistic.IStatementOwnCostDayService;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.provider.mapper.statistic.StatementDepartmentDayMapper;
import com.youming.youche.table.domain.statistic.StatementDepartmentDay;
import com.youming.youche.table.domain.statistic.StatementOwnCostDay;
import com.youming.youche.table.domain.workbench.BossWorkbenchDayInfo;
import com.youming.youche.table.dto.statistic.DepartmentReportCostFeeDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 部门日报 服务实现类
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@DubboService(version = "1.0.0")
public class StatementDepartmentDayServiceImpl extends BaseServiceImpl<StatementDepartmentDayMapper, StatementDepartmentDay> implements IStatementDepartmentDayService {


    @DubboReference(version = "1.0.0")
    IOaLoanThreeService oaLoanThreeService;

    @Resource
    IStatementOwnCostDayService statementOwnCostDayService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService organizeService;

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
            //生成
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("sheet1");
            XSSFCellStyle style = workbook.createCellStyle();// 设置为水平居中
            XSSFRow row1 = sheet.createRow(0);
            XSSFCell cell1 = row1.createCell(0);
            cell1.setCellValue("分类");
            cell1.setCellStyle(style);
            XSSFCell cell2 = row1.createCell(1);
            cell2.setCellValue("科目");
            cell2.setCellStyle(style);
            XSSFCell cell3 = row1.createCell(2);
            cell3.setCellValue("部门汇总");
            cell3.setCellStyle(style);
            int cellSum = 3;
            //设置第表头
            if (list != null && list.size() > 0) {
                cellSum += list.size();
                for (int i = 0; i < list.size(); i++) {
                    XSSFCell cell = row1.createCell(3 + i);
                    cell.setCellValue(list.get(i).getName());
                    cell.setCellStyle(style);
                }
            }
            //一共28行固定数据
            for (int i = 1; i <= 28; i++) {
                XSSFRow row = sheet.createRow(i);
                for (int j = 0; j < cellSum; j++
                ) {
                    XSSFCell cell = row.createCell(j);
                    if (j == 0) {
                        if (i <= 18) {
                            cell.setCellValue("自有业务");
                            cell.setCellStyle(style);
                        } else if (i > 18 && i <= 21) {
                            cell.setCellValue("外调业务");
                            cell.setCellStyle(style);
                        } else if (i > 21 && i <= 24) {
                            cell.setCellValue("招商业务");
                            cell.setCellStyle(style);
                        } else {
                            cell.setCellValue("汇总信息");
                            cell.setCellStyle(style);
                        }
                    } else if (j == 1) {
                        switch (i) {
                            case 1:
                                cell.setCellValue("自有收入");
                                cell.setCellStyle(style);
                                break;
                            case 2:
                                cell.setCellValue("自有成本");
                                cell.setCellStyle(style);
                                break;
                            case 3:
                                cell.setCellValue("油费");
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue("路桥费");
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue("补贴");
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue("保费");
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue("司机借支");
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue("司机报销");
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue("维修费");
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue("保养费");
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue("停车费");
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue("杂费");
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue("车辆年审");
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue("车辆事故");
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue("车辆违章");
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue("员工借支");
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue("现金");
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue("自有毛利");
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue("外调收入");
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue("外调成本");
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue("外调毛利");
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue("招商收入");
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue("招商成本");
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue("招商毛利");
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue("收入汇总");
                                cell.setCellStyle(style);
                                break;
                            case 26:
                                cell.setCellValue("成本汇总");
                                cell.setCellStyle(style);
                                break;
                            case 27:
                                cell.setCellValue("总毛利润");
                                cell.setCellStyle(style);
                                break;
                            case 28:
                                cell.setCellValue("总毛利率");
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
                                cell.setCellValue(statementDepartmentDto.getOilFee());
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue(statementDepartmentDto.getTollFee());
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue(statementDepartmentDto.getSubsidyFee());
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue(statementDepartmentDto.getInsuranceFee());
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue(statementDepartmentDto.getDriverCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue(statementDepartmentDto.getDriverExpenseFee());
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue(statementDepartmentDto.getMaintenanceFee());
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue(statementDepartmentDto.getMaintainFee());
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue(statementDepartmentDto.getParkingFee());
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue(statementDepartmentDto.getMiscellaneousFee());
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue(statementDepartmentDto.getVehicleInspectionFee());
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue(statementDepartmentDto.getVehicleAccidentFee());
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue(statementDepartmentDto.getVehicleViolationFee());
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue(statementDepartmentDto.getEmployeesCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue(statementDepartmentDto.getCash());
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue(statementDepartmentDto.getOwnGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue(statementDepartmentDto.getDiversionIncome());
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue(statementDepartmentDto.getDiversionCost());
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue(statementDepartmentDto.getDiversionGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue(statementDepartmentDto.getMerchantsIncome());
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue(statementDepartmentDto.getMerchantsCost());
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue(statementDepartmentDto.getMerchantsGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue(statementDepartmentDto.getSumIncome());
                                cell.setCellStyle(style);
                                break;
                            case 26:
                                cell.setCellValue(statementDepartmentDto.getSumCost());
                                cell.setCellStyle(style);
                                break;
                            case 27:
                                cell.setCellValue(statementDepartmentDto.getSumMargin());
                                cell.setCellStyle(style);
                                break;
                            case 28:
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
                                cell.setCellValue(list.get(j - 3).getOilFee());
                                cell.setCellStyle(style);
                                break;
                            case 4:
                                cell.setCellValue(list.get(j - 3).getTollFee());
                                cell.setCellStyle(style);
                                break;
                            case 5:
                                cell.setCellValue(list.get(j - 3).getSubsidyFee());
                                cell.setCellStyle(style);
                                break;
                            case 6:
                                cell.setCellValue(list.get(j - 3).getInsuranceFee());
                                cell.setCellStyle(style);
                                break;
                            case 7:
                                cell.setCellValue(list.get(j - 3).getDriverCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 8:
                                cell.setCellValue(list.get(j - 3).getDriverExpenseFee());
                                cell.setCellStyle(style);
                                break;
                            case 9:
                                cell.setCellValue(list.get(j - 3).getMaintenanceFee());
                                cell.setCellStyle(style);
                                break;
                            case 10:
                                cell.setCellValue(list.get(j - 3).getMaintainFee());
                                cell.setCellStyle(style);
                                break;
                            case 11:
                                cell.setCellValue(list.get(j - 3).getParkingFee());
                                cell.setCellStyle(style);
                                break;
                            case 12:
                                cell.setCellValue(list.get(j - 3).getMiscellaneousFee());
                                cell.setCellStyle(style);
                                break;
                            case 13:
                                cell.setCellValue(list.get(j - 3).getVehicleInspectionFee());
                                cell.setCellStyle(style);
                                break;
                            case 14:
                                cell.setCellValue(list.get(j - 3).getVehicleAccidentFee());
                                cell.setCellStyle(style);
                                break;
                            case 15:
                                cell.setCellValue(list.get(j - 3).getVehicleViolationFee());
                                cell.setCellStyle(style);
                                break;
                            case 16:
                                cell.setCellValue(list.get(j - 3).getEmployeesCreditFee());
                                cell.setCellStyle(style);
                                break;
                            case 17:
                                cell.setCellValue(list.get(j - 3).getCash());
                                cell.setCellStyle(style);
                                break;
                            case 18:
                                cell.setCellValue(list.get(j - 3).getOwnGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 19:
                                cell.setCellValue(list.get(j - 3).getDiversionIncome());
                                cell.setCellStyle(style);
                                break;
                            case 20:
                                cell.setCellValue(list.get(j - 3).getDiversionCost());
                                cell.setCellStyle(style);
                                break;
                            case 21:
                                cell.setCellValue(list.get(j - 3).getDiversionGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 22:
                                cell.setCellValue(list.get(j - 3).getMerchantsIncome());
                                cell.setCellStyle(style);
                                break;
                            case 23:
                                cell.setCellValue(list.get(j - 3).getMerchantsCost());
                                cell.setCellStyle(style);
                                break;
                            case 24:
                                cell.setCellValue(list.get(j - 3).getMerchantsGrossMargin());
                                cell.setCellStyle(style);
                                break;
                            case 25:
                                cell.setCellValue(list.get(j - 3).getSumIncome());
                                cell.setCellStyle(style);
                                break;
                            case 26:
                                cell.setCellValue(list.get(j - 3).getSumCost());
                                cell.setCellStyle(style);
                                break;
                            case 27:
                                cell.setCellValue(list.get(j - 3).getSumMargin());
                                cell.setCellStyle(style);
                                break;
                            case 28:
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
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "部门日报.xlsx", inputStream.available());
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
    public void departmentStatisticsDayReport(String startDate, String endDate, Long tenantId){
        //根据部门分组统计
        Date start = DateUtil.parseDate(startDate, DateUtil.DATE_FORMAT);
        Date end = DateUtil.parseDate(endDate, DateUtil.DATE_FORMAT);
        List<Date> dateList = DateUtil.findDates(start, end);
        for (Date date : dateList) {
            LocalDateTime startTime = DateUtil.asLocalDateTime(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            LocalDateTime endTime = DateUtil.asLocalDateTime(calendar.getTime());
            //查询车队所有部门
            List<SysOrganize> organizeList = organizeService.getSysOrganizeBytenantId(tenantId);
            if (organizeList != null && organizeList.size() > 0) {
                for (SysOrganize s : organizeList) {
                    if (s.getState() == 0 || s.getParentOrgId() == -1) {
                        continue;
                    }
                    StatementDepartmentDay statementDepartmentDay = new StatementDepartmentDay();
                    //按日期查询自有车订单数据（计算收入）
                    List<OrderInfoDto> orderInfoList = baseMapper.queryOrderReceivable(startTime, endTime, tenantId, s.getId());
                    //自有收入
                    Long ownIncome = 0L;
                    //自有成本
                    Long ownCost = 0L;
                    //招商车收入
                    Long merchantsFee = 0L;
                    //招商车成本
                    Long merchantsCost = 0L;
                    //外调车收入
                    Long temporaryFee = 0L;
                    //外调车成本
                    Long diversionCost = 0L;
                    //自有成本数据
                    //油费
                    Long oilFee = 0L;
                    //现金
                    Long cash = 0L;
                    //路桥费
                    Long tollFee = 0L;
                    //补贴
                    Long subsidyFee = 0L;
                    //保费
                    Long insuranceFee = 0L;
                    //维修费
                    Long maintenanceFee = 0L;
                    //保养费
                    Long maintainFee = 0L;
                    //司机借支
                    Long driverCreditFee = 0L;
                    //司机报销
                    Long driverExpenseFee = 0L;
                    //停车费
                    Long parkingFee = 0L;
                    //杂费
                    Long miscellaneousFee = 0L;
                    //车辆年审
                    Long vehicleInspectionFee = 0L;
                    //车辆事故
                    Long vehicleAccidentFee = 0L;
                    //车辆违章
                    Long vehicleViolationFee = 0L;
                    //员工借支
                    Long employeesCreditFee = 0L;
                    //计算订单收入
                    if (orderInfoList != null && orderInfoList.size() > 0) {
                        for (OrderInfoDto orderInfoDto : orderInfoList
                        ) {
                            if (orderInfoDto.getAffirmIncomeFee() != null) {
                                if (orderInfoDto.getVehicleClass() != null) {
                                    switch (orderInfoDto.getVehicleClass()) {
                                        case 1:
                                            ownIncome += orderInfoDto.getAffirmIncomeFee();
                                            //自由车判断成本模式
                                            //   if (orderInfoDto.getPaymentWay() == 1) {
                                            //智能模式模式只计算油卡成本
//                                                if (orderInfoDto.getPreTotalFee() != null) {
//                                                    oilFee += orderInfoDto.getPreTotalFee();
//                                                }
                                            //   } else {
                                            if (orderInfoDto.getPreCashFee() != null) {
                                                //现金
                                                cash += orderInfoDto.getPreCashFee();
                                            }
                                            if (orderInfoDto.getFinalFee() != null) {
                                                //现金
                                                cash += orderInfoDto.getFinalFee();
                                            }
                                            if (orderInfoDto.getPreOilVirtualFee() != null) {
                                                //油费
                                                oilFee += orderInfoDto.getPreOilVirtualFee();
                                            }
                                            if (orderInfoDto.getPreOilFee() != null) {
                                                //油费
                                                oilFee += orderInfoDto.getPreOilFee();
                                            }
                                            if (orderInfoDto.getPontagePer() != null) {
                                                if (orderInfoDto.getPaymentWay() == 1) {
                                                    //路桥费
                                                    tollFee += orderInfoDto.getPontagePer();
                                                } else {
                                                    tollFee += orderInfoDto.getPreEtcFee();
                                                }
                                            }
                                            if (orderInfoDto.getSalary() != null) {
                                                //补贴
                                                subsidyFee += orderInfoDto.getSalary();
                                            }
                                            if (orderInfoDto.getInsuranceFee() != null) {
                                                //保费
                                                insuranceFee += orderInfoDto.getInsuranceFee();
                                            }
                                            //}
                                            break;
                                        case 2:
                                            //招商车
                                            merchantsFee += orderInfoDto.getAffirmIncomeFee();
                                            if (orderInfoDto.getTotalFee() != null) {
                                                merchantsCost += orderInfoDto.getTotalFee() - orderInfoDto.getInsuranceFee();
                                            }
                                            break;
                                        case 3:
                                            //外调车
                                            temporaryFee += orderInfoDto.getAffirmIncomeFee();
                                            if (orderInfoDto.getTotalFee() != null) {
                                                diversionCost += orderInfoDto.getTotalFee() - orderInfoDto.getInsuranceFee();
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    //计算成本费用
                    //自有车成本费用
                    //根据部门id统计上报费用
                    StatementOwnCostDay statementOwnCostDay = new StatementOwnCostDay();
                    List<DepartmentReportCostFeeDto> departmentReportCostFeeDtoList = baseMapper.sumReportCostFee(s.getId(), startTime, endTime, null, tenantId);
                    if (departmentReportCostFeeDtoList != null) {
                        for (DepartmentReportCostFeeDto departmentReportCostFeeDto : departmentReportCostFeeDtoList) {
                            if (departmentReportCostFeeDto.getFeeName() != null && departmentReportCostFeeDto.getConsumeFee() != null && departmentReportCostFeeDto.getConsumeFee() > 0) {
                                switch (departmentReportCostFeeDto.getFeeName()) {
                                    case "油费":
                                        oilFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "路桥费":
                                        tollFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "维修费":
                                        maintenanceFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "保养费":
                                        maintainFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "停车费":
                                        parkingFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "杂费":
                                        miscellaneousFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    //根据部门id统计自有车车辆费用
                    List<DepartmentReportCostFeeDto> departmentCarFeeDtoList = baseMapper.sumCarFee(s.getId(), startTime, endTime, 1, tenantId);
                    if (departmentCarFeeDtoList != null) {
                        for (DepartmentReportCostFeeDto departmentReportCostFeeDto : departmentCarFeeDtoList) {
                            if (departmentReportCostFeeDto.getFeeName() != null && departmentReportCostFeeDto.getConsumeFee() != null && departmentReportCostFeeDto.getConsumeFee() > 0) {
                                switch (departmentReportCostFeeDto.getFeeName()) {
                                    case "油费":
                                        oilFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "路桥费":
                                        tollFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "维修费":
                                        maintenanceFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "保养费":
                                        maintainFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "停车费":
                                        parkingFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    case "杂费":
                                        miscellaneousFee += departmentReportCostFeeDto.getConsumeFee();
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    //根据部门id统计自有司机报销费用
                    driverExpenseFee = baseMapper.driverFee(s.getId(),startTime,endTime,tenantId);
                    //根据部门id统计招商车车辆费用
                    List<DepartmentReportCostFeeDto> departmentZhaoCarFeeDtoList = baseMapper.sumCarFee(s.getId(), startTime, endTime, 2, tenantId);
                    if (departmentZhaoCarFeeDtoList != null && departmentZhaoCarFeeDtoList.size() > 0) {
                        merchantsCost += departmentZhaoCarFeeDtoList.get(0).getConsumeFee() == null ? 0 : departmentZhaoCarFeeDtoList.get(0).getConsumeFee();
                    }
                    //根据部门id统计外调车车辆费用
                    List<DepartmentReportCostFeeDto> departmentWaiCarFeeDtoList = baseMapper.sumCarFee(s.getId(), startTime, endTime, 3, tenantId);
                    if (departmentWaiCarFeeDtoList != null && departmentWaiCarFeeDtoList.size() > 0) {
                        diversionCost += departmentWaiCarFeeDtoList.get(0).getConsumeFee() == null ? 0 : departmentWaiCarFeeDtoList.get(0).getConsumeFee();
                    }
                    //计算订单司机借支
                    driverCreditFee = oaLoanThreeService.queryAdvanceUid(s.getId(), startTime, endTime, 2, tenantId);
                    //计算员工借支
                    employeesCreditFee = oaLoanThreeService.queryAdvanceUid(s.getId(), startTime, endTime, 1, tenantId);
                    //根据部门id统计自由车车辆年审、事故、违章、维保、维修
                    String vehicleInspectionFeeStr = baseMapper.carAnnualFee(s.getId(), startTime, endTime, 1, tenantId);
                    vehicleInspectionFee = CommonUtils.objToLongMul100(vehicleInspectionFeeStr);
                    String vehicleAccidentFeeStr = baseMapper.carAccidentFee(s.getId(), startTime, endTime, 1, tenantId);
                    vehicleAccidentFee = CommonUtils.objToLongMul100(vehicleAccidentFeeStr);
                    maintainFee += baseMapper.maintenanceSum(s.getId(),1,startTime,endTime,tenantId);
                    maintenanceFee += baseMapper.maintenanceSum(s.getId(),2,startTime,endTime,tenantId);
                    //根据部门id统计招商车辆年审、事故、违章
                    if (baseMapper.carAnnualFee(s.getId(), startTime, endTime, 2, tenantId) != null) {
                        Long annulFee = CommonUtils.objToLongMul100(baseMapper.carAnnualFee(s.getId(), startTime, endTime, 2, tenantId));
                        merchantsCost += annulFee;
                    }
                    //根据部门id统计外调车辆年审、事故、违章
                    if (baseMapper.carAnnualFee(s.getId(), startTime, endTime, 2, tenantId) != null) {
                        Long annulFee = CommonUtils.objToLongMul100(baseMapper.carAnnualFee(s.getId(), startTime, endTime, 2, tenantId));
                        diversionCost += annulFee;
                    }
                    //违章还未对接三方暂不开发

                    statementDepartmentDay.setOwnIncome(CommonUtils.objToLongDiv100(ownIncome));
                    statementDepartmentDay.setMerchantsIncome(CommonUtils.objToLongDiv100(merchantsFee));
                    statementDepartmentDay.setDiversionIncome(CommonUtils.objToLongDiv100(temporaryFee));
                    statementDepartmentDay.setDepartmentId(s.getId());
                    statementDepartmentDay.setDepartmentName(s.getOrgName());
                    statementDepartmentDay.setTenantId(tenantId);
                    statementDepartmentDay.setCreateDate(DateUtil.asLocalDate(date));
                    statementDepartmentDay.setCreateTime(LocalDateTime.now());
                    //计算自由车成本oilFee+cash+tollFee+subsidyFee+insuranceFee+maintenanceFee+maintainFee+driverCreditFee+driverExpenseFeeparkingFee+miscellaneousFee+vehicleInspectionFee+vehicleAccidentFee+vehicleViolationFee+employeesCreditFee
                    ownCost = oilFee + cash + tollFee + subsidyFee + insuranceFee + maintenanceFee + maintainFee + driverCreditFee + driverExpenseFee + parkingFee + miscellaneousFee + vehicleInspectionFee + vehicleAccidentFee + vehicleViolationFee + employeesCreditFee;
                    statementDepartmentDay.setOwnCost(CommonUtils.objToLongDiv100(ownCost));
                    statementDepartmentDay.setMerchantsCost(CommonUtils.objToLongDiv100(merchantsCost));
                    statementDepartmentDay.setDiversionCost(CommonUtils.objToLongDiv100(diversionCost));
                    statementDepartmentDay.setOwnGrossMargin(statementDepartmentDay.getOwnIncome().subtract(statementDepartmentDay.getOwnCost()));
                    statementDepartmentDay.setMerchantsGrossMargin(statementDepartmentDay.getMerchantsIncome().subtract(statementDepartmentDay.getMerchantsCost()));
                    statementDepartmentDay.setDiversionGrossMargin(statementDepartmentDay.getDiversionIncome().subtract(statementDepartmentDay.getDiversionCost()));
                    //添加部门日报记录
                    super.save(statementDepartmentDay);
                    //添加部门日报自有明细
                    statementOwnCostDay.setStatementId(statementDepartmentDay.getId());
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
                    statementOwnCostDay.setVehicleAccidentFee(CommonUtils.objToLongDiv100(vehicleAccidentFee));
                    statementOwnCostDay.setVehicleInspectionFee(CommonUtils.objToLongDiv100(vehicleInspectionFee));
                    statementOwnCostDay.setVehicleViolationFee(CommonUtils.objToLongDiv100(vehicleViolationFee));
                    statementOwnCostDay.setEmployeesCreditFee(CommonUtils.objToLongDiv100(employeesCreditFee));
                    statementOwnCostDay.setCreateTime(LocalDateTime.now());
                    statementOwnCostDay.setCash(CommonUtils.objToLongDiv100(cash));
                    statementOwnCostDay.setType(1);
                    statementOwnCostDayService.save(statementOwnCostDay);
                }
            }
        }
    }


//    /**
//     * 成本费用计算
//     */
//    public void costFeeAccount(List<DepartmentReportCostFeeDto> costFee, long oilFee, long tollFee, long
//            maintenanceFee, long maintainFee, long parkingFee, long miscellaneousFee) {
//        if (costFee != null) {
//            for (DepartmentReportCostFeeDto departmentReportCostFeeDto : costFee) {
//                if (departmentReportCostFeeDto.getFeeName() != null && departmentReportCostFeeDto.getConsumeFee() != null && departmentReportCostFeeDto.getConsumeFee() > 0) {
//                    switch (departmentReportCostFeeDto.getFeeName()) {
//                        case "油费":
//                            oilFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "路桥费":
//                            tollFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "维修费":
//                            maintenanceFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "保养费":
//                            maintainFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "停车费":
//                            parkingFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        case "杂费":
//                            miscellaneousFee += departmentReportCostFeeDto.getConsumeFee();
//                            break;
//                        default:
//                            break;
//                    }
//                }
//            }
//        }
//    }

    /**
     * 开始时间+多少个小时 的时间
     *
     * @param orig      开始时间
     * @param incrFloat 单位小时
     * @return 返回添加后的时间
     */
    public static Date addHourAndMins(Date orig, Float incrFloat) {
        float incr = incrFloat == null ? 0f : incrFloat;
        int incrHour = (int) incr;
        int incrMins = new BigDecimal(incr).subtract(new BigDecimal(incrHour)).multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        Date des = addHour(orig, incrHour);
        return addMinis(des, incrMins);
    }

    public static Date addHour(Date date, int hour) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long) hour * 3600L * 1000L);
            return c.getTime();
        }
    }

    public static Date addMinis(Date date, int minis) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long) minis * 60L * 1000L);
            return c.getTime();
        }
    }

    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
    }


    @Override
    public List<BossWorkbenchDayInfo> getTableBossBusinessDayInfo() {
        return baseMapper.getTableBossBusinessDayInfo();
    }

    @Override
    public List<WechatOperationWorkbenchInfo> getTableWechatOperationWorkbenchInfo() {
        return baseMapper.getTableWechatOperationWorkbenchInfo();
    }

}
