package com.youming.youche.table.provider.service.receivable;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.domain.munual.MunualPaymentInfo;
import com.youming.youche.finance.dto.PayableDayReportFinanceDto;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.table.api.receivable.IPayableDayReportService;
import com.youming.youche.table.domain.receivable.PayableDayReport;
import com.youming.youche.table.dto.receivable.PayableDayReportDto;
import com.youming.youche.table.dto.receivable.PayableDayReportSumDto;
import com.youming.youche.table.provider.mapper.receivable.PayableDayReportMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-05-10
*/
@DubboService(version = "1.0.0")
    public class PayableDayReportServiceImpl extends BaseServiceImpl<PayableDayReportMapper, PayableDayReport> implements IPayableDayReportService {

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService iPayoutIntfThreeService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public void initPayableDayReportData() {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<PayableDayReportFinanceDto> payableDayReport = iPayoutIntfThreeService.getPayableDayReport();
        List<PayableDayReport> payableDayReportList = new ArrayList<>();
        for (PayableDayReportFinanceDto dto : payableDayReport) {
            PayableDayReport report = new PayableDayReport();
            BeanUtil.copyProperties(dto, report);
            report.setCreateTime(localDateTime);
            report.setUpdateTime(localDateTime);

            payableDayReportList.add(report);
        }

        if (CollectionUtils.isNotEmpty(payableDayReportList)) {
            this.saveBatch(payableDayReportList);
        }
    }

    @Override
    public Page<PayableDayReportDto> getPayableDayReportList(String accessToken, String userName, String time, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        

        Page<PayableDayReportDto> page = new Page<>(pageNum, pageSize);
        Page<PayableDayReportDto> payableDayReportPage = baseMapper.getPayableDayReportPage(page, tenantId, time, userName);
        List<PayableDayReportDto> list = payableDayReportPage.getRecords();
        for (PayableDayReportDto payableDayReportDto : list) {
            if (payableDayReportDto.getBusiId() != null) {
                payableDayReportDto.setBusiIdName(getSysStaticDataCodeName(tenantId, "BUSINESS_NUMBER_TYPE", String.valueOf(payableDayReportDto.getBusiId())));
                if (payableDayReportDto.getBusiId() == 900000000L) {
                    payableDayReportDto.setBusiIdName("支付轮胎租赁费");
                }
            }
        }

        payableDayReportPage.setRecords(list);
        return payableDayReportPage;
    }

    @Override
    public PayableDayReportSumDto getPayableDayReportSum(String accessToken, String userName, String time) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        PayableDayReportSumDto payableDayReportSum = baseMapper.getPayableDayReportSum(tenantId, time, userName);

        if (payableDayReportSum == null) {
            payableDayReportSum = new PayableDayReportSumDto();
            payableDayReportSum.setTxnAmount(0L);
            payableDayReportSum.setPaidNormalAmount(0L);
            payableDayReportSum.setPaidOverdueAmount(0L);
            payableDayReportSum.setNopaidNormalAmount(0L);
            payableDayReportSum.setNopaidOverdueAmount(0L);
        }
        return payableDayReportSum;
    }

    @Async
    @Override
    public void downloadExcelFile(String accessToken, ImportOrExportRecords importOrExportRecords, String userName, String time) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        List<PayableDayReportDto> list = baseMapper.getPayableDayReportList(tenantId, time, userName);
        for (PayableDayReportDto payableDayReportDto : list) {
            if (payableDayReportDto.getBusiId() != null) {
                payableDayReportDto.setBusiIdName(getSysStaticDataCodeName(tenantId, "BUSINESS_NUMBER_TYPE", String.valueOf(payableDayReportDto.getBusiId())));
                if (payableDayReportDto.getBusiId() == 900000000L) {
                    payableDayReportDto.setBusiIdName("支付轮胎租赁费");
                }
            }
        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "收款人", "付款类型", "应付日期",
                    "应付金额", "已付(正常)", "已付(逾期)",
                    "未付(正常)", "未付(逾期)"};
            resourceFild = new String[]{
                    "getUserName", "getBusiIdName", "getCreateDate",
                    "getTxnAmountDouble", "getPaidNormalAmountDouble", "getPaidOverdueAmountDouble",
                    "getNopaidNormalAmountDouble", "getNopaidOverdueAmountDouble"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, PayableDayReportDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "应付日报.xlsx", inputStream.available());
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

    private String getSysStaticDataCodeName(Long tenantId, String codeType, String codeValue) {
        String codeName = "";
        SysStaticData staticData = getSysStaticData(tenantId, codeType, codeValue);
        if (staticData != null) {
            codeName = staticData.getCodeName();
        }

        return codeName;
    }

    private SysStaticData getSysStaticData(Long tenantId, String codeType, String codeValue) {
        List<SysStaticData> staticDataList = getSysStaticDataList(codeType, tenantId);
        if (staticDataList != null && staticDataList.size() > 0) {
            Iterator var3 = staticDataList.iterator();

            while(var3.hasNext()) {
                SysStaticData sysData = (SysStaticData)var3.next();
                if (sysData.getCodeValue().equals(codeValue)) {
                    return sysData;
                }
            }
        }

        return new SysStaticData();
    }

    private List<SysStaticData> getSysStaticDataList(String codeType, Long tenantId) {
        if (StringUtils.isNotEmpty(codeType)) {
            if (Objects.isNull(tenantId)) {
                tenantId = -1L;
            }

            return getSysStaticDataList(tenantId, codeType);
        } else {
            return null;
        }
    }

    private List<SysStaticData> getSysStaticDataList(long tenantId, String codeType) {
        List<SysStaticData> staticDataList = null;
        if (StringUtils.isNotEmpty(codeType)) {
            staticDataList = (List) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(tenantId + "_" + codeType));
        }

        if (staticDataList == null || staticDataList.isEmpty()) {
            staticDataList = (List) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        }

        return staticDataList;
    }
}
