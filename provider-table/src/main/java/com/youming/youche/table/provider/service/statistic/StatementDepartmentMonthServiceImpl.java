package com.youming.youche.table.provider.service.statistic;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.statistic.IStatementDepartmentMonthService;
import com.youming.youche.table.api.statistic.IStatementOwnCostMonthService;
import com.youming.youche.table.domain.statistic.StatementDepartmentMonth;
import com.youming.youche.table.domain.statistic.StatementOwnCostMonth;
import com.youming.youche.table.domain.workbench.BossWorkbenchMonthInfo;
import com.youming.youche.table.domain.workbench.WechatOperationWorkbenchInfo;
import com.youming.youche.table.dto.statistic.StatementDepartmentDetailDto;
import com.youming.youche.table.dto.statistic.StatementDepartmentDto;
import com.youming.youche.table.provider.mapper.statistic.StatementDepartmentDayMapper;
import com.youming.youche.table.provider.mapper.statistic.StatementDepartmentMonthMapper;
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
import java.util.List;


/**
 * <p>
 * 部门月报 服务实现类
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@DubboService(version = "1.0.0")
public class StatementDepartmentMonthServiceImpl extends BaseServiceImpl<StatementDepartmentMonthMapper, StatementDepartmentMonth> implements IStatementDepartmentMonthService {

    @Resource
    StatementDepartmentDayMapper statementDepartmentDayMapper;

    @Resource
    IStatementOwnCostMonthService statementOwnCostMonthService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;


    @Resource
    LoginUtils loginUtils;

    @Override
    public StatementDepartmentDto report(String startMonth, String endMonth, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        StatementDepartmentDto statementDepartmentDto = baseMapper.dailySummary(startMonth, endMonth, loginInfo.getTenantId());
        if(statementDepartmentDto != null){
            List<StatementDepartmentDetailDto> list = baseMapper.monthDetail(startMonth,endMonth,loginInfo.getTenantId());
            if(list != null && list.size()>0){
                statementDepartmentDto.setStatementDepartmentDays(list);
            }
        }
        return statementDepartmentDto;
    }

    @Async
    @Override
    public void export(String startMonth, String endMonth, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        StatementDepartmentDto statementDepartmentDto = baseMapper.dailySummary(startMonth, endMonth, loginInfo.getTenantId());
        if (statementDepartmentDto != null) {
            List<StatementDepartmentDetailDto> list = baseMapper.monthDetail(startMonth, endMonth, loginInfo.getTenantId());
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
                                cell.setCellValue("工资");
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
                                cell.setCellValue(statementDepartmentDto.getWageFee());
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
                                cell.setCellValue(list.get(j - 3).getWageFee());
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
                String path = client.upload(inputStream, "部门月报.xlsx", inputStream.available());
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
    public void departmentStatisticsMonthReport(String startDate, String endDate, Long tenantId) {
        List<StatementDepartmentDetailDto> statementDepartmentDetailDtos = statementDepartmentDayMapper.dailyDetail(startDate,endDate,tenantId);
        if(statementDepartmentDetailDtos != null){
            for(StatementDepartmentDetailDto statementDepartmentDetailDto : statementDepartmentDetailDtos) {
                StatementDepartmentMonth statementDepartmentMonth = new StatementDepartmentMonth();
                StatementOwnCostMonth statementOwnCostMonth = new StatementOwnCostMonth();
                BeanUtil.copyProperties(statementDepartmentDetailDto, statementDepartmentMonth);
                BeanUtil.copyProperties(statementDepartmentDetailDto, statementOwnCostMonth);
                statementDepartmentMonth.setDepartmentId(statementDepartmentDetailDto.getId());
                statementDepartmentMonth.setDepartmentName(statementDepartmentDetailDto.getName());
                statementDepartmentMonth.setId(null);
                statementOwnCostMonth.setId(null);
                //根据部门计算司机工资
                if(statementDepartmentDetailDto.getId() != null) {
                   Long wageFee = baseMapper.sumSalary(startDate, endDate, statementDepartmentDetailDto.getId());
                    statementOwnCostMonth.setWageFee(CommonUtils.objToLongDiv100(wageFee));
                    BigDecimal subsidy = new BigDecimal(statementDepartmentDetailDto.getSubsidyFee());
                    BigDecimal ownCost = statementDepartmentMonth.getOwnCost();
                    //自有成本减去补贴金额加上司机工资subsidyFee
                    ownCost = ownCost.subtract(subsidy).add(statementOwnCostMonth.getWageFee());
                    statementDepartmentMonth.setOwnCost(ownCost);
                    try {
                        statementDepartmentMonth.setCreateMonth(DateUtil.formatDate(DateUtil.formatStringToDate(startDate), DateUtil.YEAR_MONTH_FORMAT));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    statementDepartmentMonth.setCreateTime(LocalDateTime.now());
                    statementDepartmentMonth.setUpdateTime(LocalDateTime.now());
                    statementDepartmentMonth.setTenantId(tenantId);
                    super.save(statementDepartmentMonth);
                    statementOwnCostMonth.setStatementId(statementDepartmentMonth.getId());
                    statementOwnCostMonth.setCreateTime(LocalDateTime.now());
                    statementOwnCostMonth.setUpdateTime(LocalDateTime.now());
                    statementOwnCostMonthService.save(statementOwnCostMonth);
                }
            }
        }
    }

    @Override
    public List<BossWorkbenchMonthInfo> getTableBossBusinessMonthInfo() {
        return baseMapper.getTableBossBusinessMonthInfo();
    }

    @Override
    public List<WechatOperationWorkbenchInfo> getTableWechatOperationWorkbenchInfo() {
        return baseMapper.getTableWechatOperationWorkbenchInfo();
    }
}
