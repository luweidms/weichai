package com.youming.youche.table.provider.service.receivable;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.order.*;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;
import com.youming.youche.finance.dto.order.OrderCheckInfoDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.dto.receivable.DailyAccountsReceivableExecuteDto;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.table.api.receivable.IDailyAccountsReceivableService;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.dto.receivable.ReceivableDetailsDto;
import com.youming.youche.table.provider.mapper.receivable.DailyAccountsReceivableMapper;
import com.youming.youche.table.vo.receivable.ReceivableDetailsVo;
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
 * ???????????? ???????????????
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@DubboService(version = "1.0.0")
public class DailyAccountsReceivableServiceImpl extends BaseServiceImpl<DailyAccountsReceivableMapper, DailyAccountsReceivable> implements IDailyAccountsReceivableService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IOrderBillCheckInfoService iOrderBillCheckInfoService;

    @DubboReference(version = "1.0.0")
    IOrderBillCheckInfoHService iOrderBillCheckInfoHService;

    @DubboReference(version = "1.0.0")
    IOrderFeeStatementService iOrderFeeStatementService;

    @DubboReference(version = "1.0.0")
    IOrderFeeStatementHService iOrderFeeStatementHService;

    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService iCmCustomerInfoService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @DubboReference(version = "1.0.0")
    IOrderReceiptService iOrderReceiptService;

    @Override
    public Page<DailyAccountsReceivable> queryDay(String name, String month, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String ids = "";
        if (StringUtils.isNotBlank(name)) {
            String nameOld = iCmCustomerInfoService.queryCustomerIdByName(name);
            if (StringUtils.isNotBlank(nameOld)) {
                ids = nameOld;
            } else {
                ids = "-1";
            }
        }
        if (name.contains("???") || name.contains("???")) {
            ids = "0";
        }
        if (StringUtils.isBlank(month)) {
            month = getYearMonth();
        }
        Page<DailyAccountsReceivable> dailyAccountsReceivablePage = baseMapper.queryInfo(new Page<>(pageNum, pageSize), loginInfo.getTenantId(), ids, month);

        List<DailyAccountsReceivable> records = new ArrayList<DailyAccountsReceivable>();

        long amountReceivable = 0L;
        long receivedNormal = 0L;
        long receivedOverdue = 0L;
        long uncollectedNormal = 0L;
        long uncollectedOverdue = 0L;

        if (dailyAccountsReceivablePage.getTotal() != 0) {
            for (DailyAccountsReceivable record : dailyAccountsReceivablePage.getRecords()) {
                records.add(record);

                amountReceivable += record.getAmountReceivable();
                receivedNormal += record.getReceivedNormal();
                receivedOverdue += record.getReceivedOverdue();
                uncollectedNormal += record.getUncollectedNormal();
                uncollectedOverdue += record.getUncollectedOverdue();
            }
        }

        DailyAccountsReceivable dto = new DailyAccountsReceivable();
        dto.setAmountReceivable(amountReceivable);
        dto.setReceivedNormal(receivedNormal);
        dto.setReceivedOverdue(receivedOverdue);
        dto.setUncollectedNormal(uncollectedNormal);
        dto.setUncollectedOverdue(uncollectedOverdue);

        records.add(dto);
        dailyAccountsReceivablePage.setRecords(records);

        return dailyAccountsReceivablePage;
    }

    @Override
    @Async
    public void queryDayExport(String name, String month, ImportOrExportRecords importOrExportRecords, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String ids = "";
        if (StringUtils.isNotBlank(name)) {
            ids = iCmCustomerInfoService.queryCustomerIdByName(name);
        }
        if (StringUtils.isBlank(month)) {
            month = getYearMonth();
        }
        List<DailyAccountsReceivable> list = baseMapper.queryInfoExport(loginInfo.getTenantId(), ids, month);

        List<DailyAccountsReceivable> records = new ArrayList<DailyAccountsReceivable>();

        long amountReceivable = 0L;
        long receivedNormal = 0L;
        long receivedOverdue = 0L;
        long uncollectedNormal = 0L;
        long uncollectedOverdue = 0L;

        if (list.size() != 0) {
            for (DailyAccountsReceivable record : list) {
                records.add(record);
                amountReceivable += record.getAmountReceivable();
                receivedNormal += record.getReceivedNormal();
                receivedOverdue += record.getReceivedOverdue();
                uncollectedNormal += record.getUncollectedNormal();
                uncollectedOverdue += record.getUncollectedOverdue();
            }
        }

        DailyAccountsReceivable dto = new DailyAccountsReceivable();
        dto.setAmountReceivable(amountReceivable);
        dto.setReceivedNormal(receivedNormal);
        dto.setReceivedOverdue(receivedOverdue);
        dto.setUncollectedNormal(uncollectedNormal);
        dto.setUncollectedOverdue(uncollectedOverdue);

        records.add(dto);

        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"????????????", "????????????", "????????????", "??????????????????",
                    "??????????????????", "??????????????????", "??????????????????"};
            resourceFild = new String[]{"getCustomerFullName", "getReceivableDate", "getAmountReceivableDouble", "getReceivedNormalDouble",
                    "getReceivedOverdueDouble", "getUncollectedNormalDouble", "getUncollectedOverdueDouble"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, DailyAccountsReceivable.class, null);
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
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public void execute() {
        List<DailyAccountsReceivableExecuteDto> executeDtos = baseMapper.executeQuery();
        for (DailyAccountsReceivableExecuteDto executeDto : executeDtos) {
            // ??????????????????
            if (executeDto.getBalanceType() == 1) { // ????????????
                if (StringUtils.isBlank(executeDto.getCreateTime())) {
                    continue;
                }
                executeDto.setReceivableDate(executeDto.getCreateTime());
            } else if (executeDto.getBalanceType() == 2) { // ?????? ????????????
                if (executeDto.getOrderState() == 14) { // ???????????????
                    if (StringUtils.isNotBlank(executeDto.getUpdateTime())) {
                        executeDto.setReceivableDate(getAccountPeriodDateStr(executeDto.getUpdateTime(), executeDto.getCollectionTime()));
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            } else if (executeDto.getBalanceType() == 3) { // ?????? ????????????
                if (executeDto.getOrderState() > 7) { // ????????????
                    if (StringUtils.isNotBlank(executeDto.getCarDependDate())) {
                        executeDto.setReceivableDate(getMonthlyDateStr(executeDto.getCarDependDate(), executeDto.getCollectionMonth(), executeDto.getCollectionDay()));
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            // ???????????? == ???????????? ???????????????????????????

            Long getAmount = executeDto.getGetAmount(); // ?????????????????????

            // ??????????????????
            Long receivedNormal = 0L;
            // ??????????????????
            Long receivedOverdue = 0L;
            // ??????????????????
            Long uncollectedNormal = 0L;
            // ??????????????????
            Long uncollectedOverdue = 0L;

            // ????????????????????????????????????????????????????????????
            if (StringUtils.isBlank(executeDto.getBillNumber())) {
                receivedNormal = 0L; // ??????????????????
                receivedOverdue = 0L; // ??????????????????
                if (equalTwoTimeStr(executeDto.getReceivableDate(), getYesterdayDataStr())) {
                    uncollectedNormal = executeDto.getCostPrice(); // ??????????????????
                } else {
                    uncollectedOverdue = executeDto.getCostPrice(); // ??????????????????
                }
            } else {
                if (getAmount == 0) { // ?????????
                    receivedNormal = 0L; // ??????????????????
                    receivedOverdue = 0L; // ??????????????????
                    if (equalTwoTimeStr(executeDto.getReceivableDate(), getYesterdayDataStr())) {
                        uncollectedNormal = executeDto.getCostPrice(); // ??????????????????
                    } else {
                        uncollectedOverdue = executeDto.getCostPrice(); // ??????????????????
                    }
                } else {
                    // 1??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    List<OrderCheckInfoDto> orderBillCheckInfo = getOrderBillCheckInfo(executeDto.getBillNumber());
                    // 2??????????????????????????????????????????????????????????????????
                    List<OrderFeeStatementH> orderFeeStatementHS = iOrderFeeStatementHService.queryOrderFeeStatementHByBillNumber(executeDto.getBillNumber());
                    List<OrderFeeStatement> orderFeeStatements = iOrderFeeStatementService.queryOrderFeeStatementByBillNumber(executeDto.getBillNumber());

                    for (OrderFeeStatement orderFeeStatement : orderFeeStatements) {
                        OrderFeeStatementH h = new OrderFeeStatementH();
                        BeanUtil.copyProperties(orderFeeStatement, h);
                        orderFeeStatementHS.add(h);
                    }


                    // 3????????????????????????????????????????????????????????????
                    List<OrderCheckInfoDto> currectOrderIdResult = new ArrayList<OrderCheckInfoDto>();

                    Long otherAmountTotal = 0L; // ???????????????????????????????????????????????????

                    for (OrderFeeStatementH orderFeeStatementH : orderFeeStatementHS) {
                        if (!executeDto.getOrderId().equals(orderFeeStatementH.getOrderId())) {
                            otherAmountTotal += orderFeeStatementH.getGetAmount();
                        } else { // ????????????????????????????????????????????????????????????
                            if (otherAmountTotal != 0L) {
                                for (OrderCheckInfoDto orderCheckInfoDto : orderBillCheckInfo) {
                                    long i = otherAmountTotal - orderCheckInfoDto.getCheckFee();
                                    if (i < 0) {
                                        OrderCheckInfoDto orderCheckInfoDto1 = new OrderCheckInfoDto();
                                        // ?????????otherAmountTotal?????????0????????????0???????????????????????????????????????????????????????????????????????????????????????????????????0??????????????????????????????????????????
                                        if (otherAmountTotal == 0) {
                                            if (getAmount == 0) {
                                                break;
                                            }
                                            long l = getAmount - orderCheckInfoDto.getCheckFee();
                                            if (l <= 0) {
                                                orderCheckInfoDto1.setCheckFee(getAmount);
                                                orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                                break;
                                            } else {
                                                orderCheckInfoDto1.setCheckFee(orderCheckInfoDto.getCheckFee());
                                                orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                                getAmount -= orderCheckInfoDto.getCheckFee();
                                            }
                                        } else {
                                            long amount = getAmount - Math.abs(i);
                                            if (amount <= 0) {
                                                orderCheckInfoDto1.setCheckFee(getAmount);
                                                orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                                break;
                                            } else {
                                                orderCheckInfoDto1.setCheckFee(Math.abs(i));
                                                orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                                getAmount = amount;
                                            }
                                            otherAmountTotal = 0L;
                                        }
                                        currectOrderIdResult.add(orderCheckInfoDto1);
                                    } else {
                                        if (otherAmountTotal != 0) {
                                            otherAmountTotal -= orderCheckInfoDto.getCheckFee();
                                        }
                                    }
                                }
                            } else { // ?????? 0 ?????????????????????????????????????????????????????????????????????
                                for (OrderCheckInfoDto orderCheckInfoDto : orderBillCheckInfo) {
                                    if (getAmount == 0) {
                                        break;
                                    }
                                    if (getAmount <= orderCheckInfoDto.getCheckFee()) {
                                        OrderCheckInfoDto orderCheckInfoDto1 = new OrderCheckInfoDto();
                                        orderCheckInfoDto1.setCheckFee(getAmount);
                                        orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                        currectOrderIdResult.add(orderCheckInfoDto1);
                                        getAmount = 0L;
                                    } else if (getAmount > orderCheckInfoDto.getCheckFee()) {
                                        OrderCheckInfoDto orderCheckInfoDto1 = new OrderCheckInfoDto();
                                        orderCheckInfoDto1.setCheckFee(orderCheckInfoDto.getCheckFee());
                                        orderCheckInfoDto1.setCreateTime(orderCheckInfoDto.getCreateTime());
                                        currectOrderIdResult.add(orderCheckInfoDto1);
                                        getAmount -= orderCheckInfoDto.getCheckFee();
                                    }
                                }
                            }
                        }
                    }

                    // 4?????????
                    for (OrderCheckInfoDto orderCheckInfoDto : currectOrderIdResult) {
                        if (equalTwoTimeStr(executeDto.getReceivableDate(), orderCheckInfoDto.getCreateTime())) {
                            receivedNormal += orderCheckInfoDto.getCheckFee();
                        } else {
                            receivedOverdue += orderCheckInfoDto.getCheckFee();
                        }
                    }

                    if (equalTwoTimeStr(executeDto.getReceivableDate(), getYesterdayDataStr())) {
                        uncollectedNormal = executeDto.getCostPrice() - executeDto.getGetAmount(); // ??????????????????
                    } else {
                        uncollectedOverdue = executeDto.getCostPrice() - executeDto.getGetAmount(); // ??????????????????
                    }
                }
            }
            DailyAccountsReceivable result = new DailyAccountsReceivable();
            result.setCustomerId(executeDto.getCustomUserId());
            result.setReceivableDate(executeDto.getReceivableDate());
            result.setAmountReceivable(executeDto.getCostPrice());
            result.setReceivedNormal(receivedNormal);
            result.setReceivedOverdue(receivedOverdue);
            result.setUncollectedNormal(uncollectedNormal);
            result.setUncollectedOverdue(uncollectedOverdue);
            result.setTenantId(executeDto.getTenantId());
            result.setCreateTime(LocalDateTime.now());

            this.save(result);
        }

    }

    @Override
    public Page<ReceivableDetailsDto> receivableDetails(ReceivableDetailsVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isNotEmpty(vo.getBeginDependTime())) {
            vo.setBeginDependTime(vo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vo.getEndDependTime())) {
            vo.setEndDependTime(vo.getEndDependTime() + " 23:59:59");
        }

        vo.setTenantId(loginInfo.getTenantId());

        String type = "";
        StringBuffer sb = new StringBuffer();
        if (vo.getStateType() != null && vo.getStateType() != -1) {
            sb.append("'").append(vo.getStateType()).append("',");
        }
        if (vo.getOrderType() != null && vo.getOrderType() != -1) {
            sb.append("'").append(vo.getOrderType()).append("',");
        }
        if (sb.length() != 0) {
            type = sb.substring(0, sb.length() - 1);
        }

        if (StringUtils.isNotBlank(vo.getOrgName())) {
            vo.setOrgId(vo.getOrgName());
        } else {
            vo.setOrgId("");
        }

        // ????????????????????????
        if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getCustomNumber())) {
            String[] cutomNumbers = vo.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sb1 = new StringBuilder();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (org.apache.commons.lang.StringUtils.isNotBlank(cutomNumber)) {
                        sb1.append(" g.CUSTOM_NUMBER like '%").append(cutomNumber).append("%' ");
                        if (index < cutomNumbers.length && org.apache.commons.lang.StringUtils.isNotBlank(cutomNumbers[index])) {
                            sb1.append(" or ");
                        }
                    }
                }
                if (org.apache.commons.lang.StringUtils.isNotBlank(sb1.toString())) {
                    vo.setCustomNumber(sb1.toString());
                }
            }
        }

        vo.setCustomerIds(vo.getCustomerIds().trim());

        if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getCustomerIds())) {
            StringBuffer buf = new StringBuffer();
            String[] split = vo.getCustomerIds().split(",");
            for (String customerId : split) {
                buf.append("'").append(customerId).append("',");
            }
            vo.setCustomerIds(buf.substring(0, buf.length() - 1));
        }

        List<ReceivableDetailsDto> list = baseMapper.receivableDetails(vo, type);
        List<ReceivableDetailsDto> listView = new ArrayList<ReceivableDetailsDto>();

        for (ReceivableDetailsDto dto : list) {

            if (dto.getBalanceType() == 1) { // ????????????
                if (StringUtils.isNotBlank(dto.getCreateTime())) {
                    dto.setReceivableDate(dto.getCreateTime());
                }
            } else if (dto.getBalanceType() == 2) { // ?????? ????????????
                if (StringUtils.isNotBlank(dto.getUpdateTime()) && dto.getOrderState() == 14) { // ???????????????
                    dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                }
            } else if (dto.getBalanceType() == 3) { // ?????? ????????????
                if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                    if (dto.getOrderState() > 7) { // ????????????
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    }
                }
            }
            listView.add(dto);
        }
        list.clear();
        list = listView;

        // ?????????????????? ????????????????????????????????????
        if (StringUtils.isNotBlank(vo.getMonth())) {
            list = list.stream().filter(bean -> StringUtils.isNotBlank(bean.getReceivableDate()))
                    .filter(bean -> getYearMonth(bean.getReceivableDate()).equals(vo.getMonth())).collect(Collectors.toList());
        }

        // ???????????? ????????????
        if (StringUtils.isNotBlank(vo.getReceivableDate())) {
            list = list.stream().filter(bean -> StringUtils.isNotBlank(bean.getReceivableDate()))
                    .filter(bean -> bean.getReceivableDate().equals(vo.getReceivableDate())).collect(Collectors.toList());
        }

        int size = list.size();
        Page<ReceivableDetailsDto> receivableDetailsDtoPage = new Page<ReceivableDetailsDto>();
        receivableDetailsDtoPage.setTotal(size);
        receivableDetailsDtoPage.setCurrent(pageNum);
        receivableDetailsDtoPage.setSize(pageSize);
        receivableDetailsDtoPage.setPages((size % pageSize == 0) ? (size / pageSize) : (size / pageSize + 1));
        // ??????
        int end = pageSize * pageNum;
        if (end > size) {
            end = size;
        }
        list = list.subList(pageSize * (pageNum - 1), end);
        receivableDetailsDtoPage.setRecords(list);

        if (receivableDetailsDtoPage.getTotal() != 0) {

            List<ReceivableDetailsDto> result = new ArrayList<>();
            StringBuffer orderIds = new StringBuffer();

            for (ReceivableDetailsDto record : receivableDetailsDtoPage.getRecords()) {
                record.setStartPointStr(record.getStartPoint() == null ? ""
                        : getSysStaticData("SYS_CITY", record.getStartPoint() + "").getCodeName());

                record.setEndPointStr(record.getEndPoint() == null ? ""
                        : getSysStaticData("SYS_CITY", record.getEndPoint() + "").getCodeName());

                record.setOrderTypeStr(record.getOrderType() == null ? ""
                        : getSysStaticData("ORDER_TYPE", record.getOrderType() + "").getCodeName());

                record.setOrderStateStr(record.getOrderState() == null ? ""
                        : getSysStaticData("ORDER_STATE", record.getOrderState() + "").getCodeName());

                record.setFinanceStsName(record.getFinanceSts() == null ? ""
                        : getSysStaticData("FINANCE_STS", record.getFinanceSts() + "").getCodeName());

                record.setIsCreateBillStr(StringUtils.isNotBlank(record.getBillNumber()) ? "?????????" : "?????????");
                record.setOrgName(record.getOrgId() == null ? loginInfo.getName() : iSysOrganizeService.getById(record.getOrgId()).getOrgName());

                orderIds.append("'" + record.getOrderId() + "',");
                result.add(record);

            }

            receivableDetailsDtoPage.setRecords(result);

            //????????????????????????????????????
            if (orderIds.length() > 0) {
                String subs = orderIds.substring(0, orderIds.length() - 1);
                List<Map<String, Object>> customerIdMap = iOrderReceiptService.getCustomerIdByOrderIdStrs(subs);
                if (customerIdMap != null) {
                    for (Map<String, Object> map : customerIdMap) {
                        for (ReceivableDetailsDto out : result) {
                            if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                                out.setCustomerId(DataFormat.getStringKey(map, "customerId"));
                                break;
                            }
                        }
                    }
                }
            }

        }

        return receivableDetailsDtoPage;
    }

    @Override
    @Async
    public void receivableDetailsExport(ReceivableDetailsVo vo, ImportOrExportRecords importOrExportRecords, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isNotEmpty(vo.getBeginDependTime())) {
            vo.setBeginDependTime(vo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vo.getEndDependTime())) {
            vo.setEndDependTime(vo.getEndDependTime() + " 23:59:59");
        }

        vo.setTenantId(loginInfo.getTenantId());

        String type = "";
        StringBuffer sb = new StringBuffer();
        if (vo.getStateType() != null && vo.getStateType() != -1) {
            sb.append("'").append(vo.getStateType()).append("',");
        }
        if (vo.getOrderType() != null && vo.getOrderType() != -1) {
            sb.append("'").append(vo.getOrderType()).append("',");
        }
        if (sb.length() != 0) {
            type = sb.substring(0, sb.length() - 1);
        }

        if (StringUtils.isNotBlank(vo.getOrgName())) {
//            vo.setOrgId(iSysOrganizeService.queryOrgIdByName(vo.getOrgName(), accessToken));
            vo.setOrgId(vo.getOrgName());
        } else {
            vo.setOrgId("");
        }

        // ????????????????????????
        if (org.apache.commons.lang.StringUtils.isNotBlank(vo.getCustomNumber())) {
            String[] cutomNumbers = vo.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sb1 = new StringBuilder();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (org.apache.commons.lang.StringUtils.isNotBlank(cutomNumber)) {
                        sb1.append(" g.CUSTOM_NUMBER like '%").append(cutomNumber).append("%' ");
                        if (index < cutomNumbers.length && org.apache.commons.lang.StringUtils.isNotBlank(cutomNumbers[index])) {
                            sb1.append(" or ");
                        }
                    }
                }
                if (org.apache.commons.lang.StringUtils.isNotBlank(sb1.toString())) {
                    vo.setCustomNumber(sb1.toString());
                }
            }
        }

        List<ReceivableDetailsDto> list = baseMapper.receivableDetails(vo, type);
        List<ReceivableDetailsDto> listView = new ArrayList<ReceivableDetailsDto>();

        for (ReceivableDetailsDto dto : list) {
            if (dto.getBalanceType() == 1) { // ????????????
                if (StringUtils.isNotBlank(dto.getCreateTime())) {
                    dto.setReceivableDate(dto.getCreateTime());
                }
            } else if (dto.getBalanceType() == 2) { // ?????? ????????????
                if (StringUtils.isNotBlank(dto.getUpdateTime()) && dto.getOrderState() == 14) { // ???????????????
                    dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                }
            } else if (dto.getBalanceType() == 3) { // ?????? ????????????
                if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                    if (dto.getOrderState() > 7) { // ????????????
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    }
                }
            }
            listView.add(dto);
        }
        list.clear();
        list = listView;

        // ?????????????????? ????????????????????????
        if (StringUtils.isNotBlank(vo.getMonth())) {
            list = list.stream().filter(bean -> StringUtils.isNotBlank(bean.getReceivableDate()))
                    .filter(bean -> getYearMonth(bean.getReceivableDate()).equals(vo.getMonth())).collect(Collectors.toList());
        }

        // ???????????? ????????????
        if (StringUtils.isNotBlank(vo.getReceivableDate())) {
            list = list.stream().filter(bean -> StringUtils.isNotBlank(bean.getReceivableDate()))
                    .filter(bean -> bean.getReceivableDate().equals(vo.getReceivableDate())).collect(Collectors.toList());
        }

        List<ReceivableDetailsDto> result = new ArrayList<>();
        StringBuffer orderIds = new StringBuffer();

        if (list.size() != 0) {
            for (ReceivableDetailsDto record : list) {
                record.setStartPointStr(record.getStartPoint() == null ? ""
                        : getSysStaticData("SYS_CITY", record.getStartPoint() + "").getCodeName());

                record.setEndPointStr(record.getEndPoint() == null ? ""
                        : getSysStaticData("SYS_CITY", record.getEndPoint() + "").getCodeName());

                record.setOrderTypeStr(record.getOrderType() == null ? ""
                        : getSysStaticData("ORDER_TYPE", record.getOrderType() + "").getCodeName());

                record.setOrderStateStr(record.getOrderState() == null ? ""
                        : getSysStaticData("ORDER_STATE", record.getOrderState() + "").getCodeName());

                record.setFinanceStsName(record.getFinanceSts() == null ? ""
                        : getSysStaticData("FINANCE_STS", record.getFinanceSts() + "").getCodeName());

                record.setIsCreateBillStr(StringUtils.isNotBlank(record.getBillNumber()) ? "?????????" : "?????????");
                record.setOrgName(record.getOrgId() == null ? loginInfo.getName() : iSysOrganizeService.getById(record.getOrgId()).getOrgName());

                orderIds.append("'" + record.getOrderId() + "',");
                result.add(record);
            }

            //????????????????????????????????????
            if (orderIds.length() > 0) {
                String subs = orderIds.substring(0, orderIds.length() - 1);
                List<Map<String, Object>> customerIdMap = iOrderReceiptService.getCustomerIdByOrderIdStrs(subs);
                if (customerIdMap != null) {
                    for (Map<String, Object> map : customerIdMap) {
                        for (ReceivableDetailsDto out : result) {
                            if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                                out.setCustomerId(DataFormat.getStringKey(map, "customerId"));
                                break;
                            }
                        }
                    }
                }
            }

        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            showName = new String[]{"?????????", "????????????", "????????????", "????????????", "????????????", "???????????????",
                    "?????????", "?????????", "????????????", "????????????", "????????????",
                    "????????????", "?????????", "??????", "????????????", "????????????",
                    "????????????", "????????????", "????????????", "????????????", "????????????",
                    "????????????"};
            resourceFild = new String[]{"getOrderId", "getDependDate", "getReceivableDate", "getCompanyName", "getCustomNumber", "getCustomerId",
                    "getStartPointStr", "getEndPointStr", "getSourceName", "getLinePropStr", "getOrgName",
                    "getGoodsInfo", "getPlateNumber", "getCarType", "getTrailerPlate", "getOrderTypeStr",
                    "getOrderStateStr", "getReceiptNumber", "getFinanceStsName", "getGetAmountDouble", "getIsCreateBillStr",
                    "getBillNumber"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(result, showName, resourceFild, ReceivableDetailsDto.class, null);
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
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }

    }

    /**
     * ?????? ??????-???????????? ????????????
     *
     * @param data ?????????????????? yyyy-mm-dd
     * @param num  ????????????
     * @return ?????????????????????????????? yyyy-mm-dd
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

        //??????Calendar?????????add()??????????????????
        cld.add(Calendar.DATE, null == num ? 0 : num);

        String m = cld.get(Calendar.MONTH) + "";
        String d = cld.get(Calendar.DATE) + "";

        if (m.length() == 1) {
            m = "0" + m;
        }
        if (d.length() == 1) {
            d = "0" + d;
        }

        return cld.get(Calendar.YEAR) + "-" + m + "-" + d;
    }

    /**
     * ?????? ?????? ???????????? ????????????
     *
     * @param data            ?????????????????? yyyy-mm-dd
     * @param collectionMonth ??????????????????????????????
     * @param collectionDay   ????????????????????????????????????
     * @return ?????????????????????????????? yyyy-mm-dd
     */
    private String getMonthlyDateStr(String data, Integer collectionMonth, Integer collectionDay) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month - 1);

        //??????Calendar?????????add()??????????????????
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

    // ?????????????????? yyyy-mm-dd
    private String getYesterdayDataStr() {
        Calendar now = Calendar.getInstance();

        String month = String.valueOf(now.get(Calendar.MONTH) + 1);
        if (month.length() != 2) {
            month = "0" + month;
        }

        String day = String.valueOf(now.get(Calendar.DAY_OF_MONTH));
        if (day.length() != 2) {
            day = "0" + day;
        }

        return now.get(Calendar.YEAR) + "-" + month + "-" + day;
    }

    /**
     * @param receivableDate ????????????
     * @param yesDate        ???????????? / ????????????
     * @return true ???????????????????????????????????????false ????????????????????????
     */
    private Boolean equalTwoTimeStr(String receivableDate, String yesDate) {
        int dateFlag = receivableDate.compareTo(yesDate);
        return dateFlag >= 0 ? true : false;
    }

    /**
     * @param billNumber ????????? ?????? ?????????????????????????????????
     */
    List<OrderCheckInfoDto> getOrderBillCheckInfo(String billNumber) {
        List<OrderCheckInfoDto> listH = iOrderBillCheckInfoHService.queryRecentRecordByBillNumber(billNumber);
        List<OrderBillCheckInfo> list = iOrderBillCheckInfoService.queryInfoByBillNumberAndIdAsc(billNumber);

        int size = listH.size();

        if (size == 0) {
            List<OrderCheckInfoDto> out = new ArrayList<OrderCheckInfoDto>();
            for (OrderBillCheckInfo orderBillCheckInfo : list) {
                OrderCheckInfoDto dto = new OrderCheckInfoDto();
                BeanUtil.copyProperties(orderBillCheckInfo, dto);
                out.add(dto);
            }
            return out;
        } else {
//            list.subList(0, size).clear();
            for (OrderBillCheckInfo orderBillCheckInfo : list) {
                OrderCheckInfoDto dto = new OrderCheckInfoDto();
                BeanUtil.copyProperties(orderBillCheckInfo, dto);
                listH.add(dto);
            }
            return listH;
        }

    }

    private String getYearMonth() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        return simpleDateFormat.format(new Date());
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    private static String getYearMonth(String date) {
        String[] split = date.split("-");
        return split[0] + "-" + split[1];
    }

}
