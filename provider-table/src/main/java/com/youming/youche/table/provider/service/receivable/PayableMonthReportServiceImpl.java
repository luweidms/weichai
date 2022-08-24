package com.youming.youche.table.provider.service.receivable;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.dto.PayableMonthReportFinanceDto;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.table.api.receivable.IPayableMonthReportService;
import com.youming.youche.table.domain.receivable.PayableMonthReport;
import com.youming.youche.table.dto.receivable.PayableDayReportDto;
import com.youming.youche.table.dto.receivable.PayableMonthReportDto;
import com.youming.youche.table.provider.mapper.receivable.PayableMonthReportMapper;
import com.youming.youche.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
* <p>
    * 应付月报表 服务实现类
    * </p>
* @author zengwen
* @since 2022-05-11
*/
@DubboService(version = "1.0.0")
public class PayableMonthReportServiceImpl extends BaseServiceImpl<PayableMonthReportMapper, PayableMonthReport> implements IPayableMonthReportService {

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService iPayoutIntfThreeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    LoginUtils loginUtils;
    
    @Override
    public void initPayableMonthReport() {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<PayableMonthReportFinanceDto> payableMonthReport = iPayoutIntfThreeService.getPayableMonthReport();
        List<PayableMonthReport> payableMonthReportList = new ArrayList<>();
        for (PayableMonthReportFinanceDto payableMonthReportFinanceDto : payableMonthReport) {
            PayableMonthReport report = new PayableMonthReport();
            BeanUtil.copyProperties(payableMonthReportFinanceDto, report);

            report.setCreateTime(localDateTime);
            report.setUpdateTime(localDateTime);
            payableMonthReportList.add(report);
        }

        if (CollectionUtils.isNotEmpty(payableMonthReportList)) {
            this.saveBatch(payableMonthReportList);
        }
    }

    @Override
    public Page<PayableMonthReportDto> getPayableMonthReport(String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        Page<PayableMonthReportDto> page = new Page<>(pageNum, pageSize);
        return baseMapper.getPayableMonthReportPage(tenantId, page);
    }

    @Async
    @Override
    public void downloadExcelFile(String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        List<PayableMonthReportDto> list = baseMapper.getPayableMonthReportList(tenantId);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            Date now = new Date();

            // 标题处理
            showName = new String[]{
                    "收款人",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 6), DateUtil.YEAR_MONTH_FORMAT3) + "以前未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 6), DateUtil.YEAR_MONTH_FORMAT3) + "以前已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 6), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 6), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 5), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 5), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 4), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 4), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 3), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 3), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 2), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 2), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 1), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 1), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
//                    DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 0), DateUtil.YEAR_MONTH_FORMAT3) + "未付", DateUtil.formatDateByFormat(DateUtil.diffMonth(now, 0), DateUtil.YEAR_MONTH_FORMAT3) + "已付",
                    "合计"};


            resourceFild = new String[]{
                    "getUserName",
                    "getEightNoPaidAmountDouble", "getEightPaidAmountDouble",
                    "getServenNoPaidAmountDouble", "getServenPaidAmountDouble",
                    "getSixNoPaidAmountDouble", "getSixPaidAmountDouble",
                    "getFiveNoPaidAmountDouble", "getFivePaidAmountDouble",
                    "getForeNoPaidAmountDouble", "getForePaidAmountDouble",
                    "getThreeNoPaidAmountDouble", "getThreePaidAmountDouble",
                    "getTwoNoPaidAmountDouble", "getTwoPaidAmountDouble",
//                    "getFirstNoPaidAmountDouble", "getFirstPaidAmountDouble",
                    "getSumAmountDouble"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, PayableMonthReportDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "应付月报.xlsx", inputStream.available());
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
