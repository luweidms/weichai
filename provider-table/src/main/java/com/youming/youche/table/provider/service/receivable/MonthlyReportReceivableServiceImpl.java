package com.youming.youche.table.provider.service.receivable;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.dto.receivable.DailyAccountsReceivableExecuteDto;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.table.api.receivable.IMonthlyReportReceivableService;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.domain.receivable.MonthlyReportReceivable;
import com.youming.youche.table.provider.mapper.receivable.DailyAccountsReceivableMapper;
import com.youming.youche.table.provider.mapper.receivable.MonthlyReportReceivableMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@DubboService(version = "1.0.0")
public class MonthlyReportReceivableServiceImpl extends BaseServiceImpl<MonthlyReportReceivableMapper, MonthlyReportReceivable> implements IMonthlyReportReceivableService {

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    DailyAccountsReceivableMapper dailyAccountsReceivableMapper;

    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService iCmCustomerInfoService;

    @Override
    public Page<MonthlyReportReceivable> queryMonth(String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<MonthlyReportReceivable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonthlyReportReceivable::getTenantId, loginInfo.getTenantId());
        queryWrapper.eq(MonthlyReportReceivable::getMonthlyReportTime, getCurrentYearMonth());
        Page<MonthlyReportReceivable> page = this.page(new Page<MonthlyReportReceivable>(pageNum, pageSize), queryWrapper);

        for (MonthlyReportReceivable monthlyReportReceivable : page.getRecords()) {
            if (monthlyReportReceivable.getCustomerId() != 0) {
                monthlyReportReceivable.setCustomerFullName(iCmCustomerInfoService.getById(monthlyReportReceivable.getCustomerId()).getCompanyName());
            }
        }

        return page;
    }

    @Override
    public List<MonthlyReportReceivable> queryMonthExport(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<MonthlyReportReceivable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonthlyReportReceivable::getTenantId, loginInfo.getTenantId());
        queryWrapper.eq(MonthlyReportReceivable::getMonthlyReportTime, getCurrentYearMonth());
        List<MonthlyReportReceivable> list = this.list(queryWrapper);
        for (MonthlyReportReceivable monthlyReportReceivable : list) {
            if (monthlyReportReceivable.getCustomerId() != 0) {
                monthlyReportReceivable.setCustomerFullName(iCmCustomerInfoService.getById(monthlyReportReceivable.getCustomerId()).getCompanyName());
            }
        }

        return list;
    }

    @Override
    @Async
    public void queryMonthExport(ImportOrExportRecords importOrExportRecords, String accessToken) {
        List<MonthlyReportReceivable> monthlyReportReceivables = this.queryMonthExport(accessToken);
        Object[] objects = dataArray("yyyy年MM月", 8);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"客户全称", objects[5] + "以前未收", objects[5] + "以前已收", objects[5] + "未收",
                    objects[5] + "已收", objects[4] + "未收", objects[4] + "已收", objects[3] + "未收",
                    objects[3] + "已收", objects[2] + "未收", objects[2] + "已收", objects[1] + "未收",
                    objects[1] + "已收", objects[0] + "未收", objects[0] + "已收", "合计"};
            resourceFild = new String[]{"getCustomerFullName", "getUncollectedOther", "getReceivedOther", "getUncollected6",
                    "getReceived6", "getUncollected5", "getReceived5", "getUncollected4",
                    "getReceived4", "getUncollected3", "getReceived3", "getUncollected2",
                    "getReceived2", "getUncollected1", "getReceived1", "getTotal"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(monthlyReportReceivables, showName, resourceFild, MonthlyReportReceivable.class, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "应收月报信息.xlsx", inputStream.available());
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
    public void execute() {
        List<DailyAccountsReceivableExecuteDto> dtoList = dailyAccountsReceivableMapper.executeQuery();
        List<DailyAccountsReceivableExecuteDto> executeDtos = new ArrayList<DailyAccountsReceivableExecuteDto>();
        // 处理时间
        for (DailyAccountsReceivableExecuteDto executeDto : dtoList) {
            // 处理应收日期
            if (executeDto.getBalanceType() == 1) { // 预付全款
                if (StringUtils.isBlank(executeDto.getCreateTime())) {
                    continue;
                }
                executeDto.setReceivableDate(executeDto.getCreateTime());
            } else if (executeDto.getBalanceType() == 2) { // 预付 尾款账期
                if (executeDto.getOrderState() == 14) { // 订单已完成
                    if (StringUtils.isNotBlank(executeDto.getUpdateTime())) {
                        executeDto.setReceivableDate(getAccountPeriodDateStr(executeDto.getUpdateTime(), executeDto.getCollectionTime()));
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else if (executeDto.getBalanceType() == 3) { // 预付 尾款月结
                if (executeDto.getOrderState() > 7) { // 靠台完成
                    if (StringUtils.isNotBlank(executeDto.getCarDependDate())) {
                        executeDto.setReceivableDate(getMonthlyDateStr(executeDto.getCarDependDate(), executeDto.getCollectionMonth(), executeDto.getCollectionDay()));
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            executeDto.setReceivableDate(getSpecificationReceivableDate(executeDto.getReceivableDate()));
            executeDtos.add(executeDto);
        }

        Object[] objects = dataArray("yyyy-MM", 7);

        // 车队id分组
        Map<Long, List<DailyAccountsReceivableExecuteDto>> collectTeanantIdGroupByData = executeDtos.stream()
                .collect(Collectors.groupingBy(DailyAccountsReceivableExecuteDto::getTenantId));

        for (Long tenantId : collectTeanantIdGroupByData.keySet()) {
            List<DailyAccountsReceivableExecuteDto> list = collectTeanantIdGroupByData.get(tenantId); // 当前车队的所有客户名称数据

            // 客户id分组
            Map<Long, List<DailyAccountsReceivableExecuteDto>> collectCustomUserIdGroupByData = list.stream()
                    .collect(Collectors.groupingBy(DailyAccountsReceivableExecuteDto::getCustomUserId));

            for (Long customId : collectCustomUserIdGroupByData.keySet()) {

                MonthlyReportReceivable result = new MonthlyReportReceivable();
                result.setCustomerId(customId);
                result.setMonthlyReportTime(getCurrentYearMonth());
                result.setCreateTime(LocalDateTime.now());

                // 应收日期分组
                List<DailyAccountsReceivableExecuteDto> value = collectCustomUserIdGroupByData.get(customId);
                Map<String, List<DailyAccountsReceivableExecuteDto>> receivableDateCollect = value.stream()
                        .collect(Collectors.groupingBy(DailyAccountsReceivableExecuteDto::getReceivableDate));

                // 计算已收未收
                for (String receivableDate : receivableDateCollect.keySet()) {
                    List<DailyAccountsReceivableExecuteDto> dtos = receivableDateCollect.get(receivableDate);

                    long total = 0L; // 总金额
                    long receive = 0L; // 已收

                    for (DailyAccountsReceivableExecuteDto dto : dtos) {
                        total += dto.getCostPrice();
                        receive += dto.getGetAmount();
                    }
                    long unReceive = total - receive;

                    if (objects[0].equals(receivableDate)) {
                        result.setUncollected1(result.getUncollected1() != null ? result.getUncollected1() + unReceive : unReceive);
                        result.setReceived1(result.getReceived1() != null ? result.getReceived1() + receive : receive);
                    } else if (objects[1].equals(receivableDate)) {
                        result.setUncollected2(result.getUncollected2() != null ? result.getUncollected2() + unReceive : unReceive);
                        result.setReceived2(result.getReceived2() != null ? result.getReceived2() + receive : receive);
                    } else if (objects[2].equals(receivableDate)) {
                        result.setUncollected3(result.getUncollected3() != null ? result.getUncollected3() + unReceive : unReceive);
                        result.setReceived3(result.getReceived3() != null ? result.getReceived3() + receive : receive);
                    } else if (objects[3].equals(receivableDate)) {
                        result.setUncollected4(result.getUncollected4() != null ? result.getUncollected4() + unReceive : unReceive);
                        result.setReceived4(result.getReceived4() != null ? result.getReceived4() + receive : receive);
                    } else if (objects[4].equals(receivableDate)) {
                        result.setUncollected5(result.getUncollected5() != null ? result.getUncollected5() + unReceive : unReceive);
                        result.setReceived5(result.getReceived5() != null ? result.getReceived5() + receive : receive);
                    } else if (objects[5].equals(receivableDate)) {
                        result.setUncollected6(result.getUncollected6() != null ? result.getUncollected6() + unReceive : unReceive);
                        result.setReceived6(result.getReceived6() != null ? result.getReceived6() + receive : receive);
                    } else if (equalTwoTimeStr(objects[5].toString(), receivableDate)) { // 小与第六个月
                        result.setUncollectedOther(result.getUncollectedOther() != null ? result.getUncollectedOther() + unReceive : unReceive);
                        result.setReceivedOther(result.getReceivedOther() != null ? result.getReceivedOther() + receive : receive);
                    }

                    result.setTenantId(tenantId);

                }

                this.save(result);

            }

        }

    }

    private String getCurrentYearMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(new Date());
    }

    /**
     * @param format 时间格式
     * @return 过去六个月的年月
     */
    private Object[] dataArray(String format, Integer index) {
        String dateString;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        List<String> rqList = new ArrayList<String>();
        for (int i = 0; i < 6; i++) {
            dateString = sdf.format(cal.getTime());
            rqList.add(dateString.substring(0, index));
            cal.add(Calendar.MONTH, -1);
        }
        return rqList.toArray();
    }

    /**
     * 计算 预付-尾款账期 应收日期
     *
     * @param data 审核通过时间 yyyy-mm-dd
     * @param num  收款期限
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getAccountPeriodDateStr(String data, Integer num) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);
        Integer day = Integer.valueOf(split[2]);

        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month);
        cld.set(Calendar.DATE, day);

        //调用Calendar类中的add()，增加时间量
        if (num != null) {
            cld.add(Calendar.DATE, num);
        }

        return cld.get(Calendar.YEAR) + "-" + cld.get(Calendar.MONTH) + "-" + cld.get(Calendar.DATE);
    }

    /**
     * 计算 预付 尾款月结 应收日期
     *
     * @param data            订单可靠时间 yyyy-mm-dd
     * @param collectionMonth 收款月（几个月以后）
     * @param collectionDay   收款天（几个月后的几号）
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getMonthlyDateStr(String data, Integer collectionMonth, Integer collectionDay) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month - 1);

        //调用Calendar类中的add()，增加时间量
        if (collectionMonth != null) {
            cld.add(Calendar.MONDAY, collectionMonth);
        }
        cld.set(Calendar.DAY_OF_MONTH, collectionDay);

        String m = cld.get(Calendar.MONTH) + 1 + "";
        String d = cld.get(Calendar.DAY_OF_MONTH) + "";

        if (m.length() == 1) {
            m = "0" + m;
        }
        if (d.length() == 1) {
            d = "0" + d;
        }

        return cld.get(Calendar.YEAR) + "-" + m + "-" + d;
    }

    /**
     * @return true 前面的时间大于后面的，false 前面小于后面的
     */
    private Boolean equalTwoTimeStr(String brforDate, String afterDate) {
        int dateFlag = brforDate.compareTo(afterDate);
        return dateFlag > 0 ? true : false;
    }

    private String getSpecificationReceivableDate(String receivableDate) {
        String[] split = receivableDate.split("-");
        if (split[1].length() != 2) {
            return split[0] + "-0" + split[1];
        }
        return split[0] + "-" + split[1];
    }

}
