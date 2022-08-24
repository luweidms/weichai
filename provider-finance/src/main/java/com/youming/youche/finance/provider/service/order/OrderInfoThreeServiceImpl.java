package com.youming.youche.finance.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.net.multipart.UploadFile;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.api.order.IOrderFeeStatementHService;
import com.youming.youche.finance.api.order.IOrderFeeStatementService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.commons.util.DateUtil;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.domain.ClaimExpenseInfo;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.domain.ac.CmSalaryOrderExt;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.order.OrderBillInfo;
import com.youming.youche.finance.domain.order.OrderCostOtherReport;
import com.youming.youche.finance.domain.order.OrderDiffInfo;
import com.youming.youche.finance.domain.order.OrderFeeH;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.dto.LineInfoDto;
import com.youming.youche.finance.dto.OilCardRechargeDto;
import com.youming.youche.finance.dto.order.OrderDiffInfoDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.dto.order.VehicleOrderDto;
import com.youming.youche.finance.provider.mapper.ClaimExpenseInfoMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryInfoNewMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryOrderExtMapper;
import com.youming.youche.finance.provider.mapper.order.OrderBillInfoMapper;
import com.youming.youche.finance.provider.mapper.order.OrderBillInvoiceMapper;
import com.youming.youche.finance.provider.mapper.order.OrderDiffInfoMapper;
import com.youming.youche.finance.provider.mapper.order.OrderFeeHMapper;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementHMapper;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementMapper;
import com.youming.youche.finance.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.finance.provider.mapper.order.OrderReceiptMapper;
import com.youming.youche.finance.provider.utils.ReadisUtil;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.VehicleOrderVo;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderCostOtherReportService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.order.OrderDetailsDto;
import com.youming.youche.record.dto.trailer.DateCostDto;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Size;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:45
 */
@DubboService(version = "1.0.0")
@Service
public class OrderInfoThreeServiceImpl extends BaseServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoThreeService {

    @Resource
    OrderInfoMapper orderInfoMapper;

    @Resource
    OrderBillInfoMapper orderBillInfoMapper;

    @Resource
    OrderDiffInfoMapper orderDiffInfoMapper;

    @Resource
    OrderReceiptMapper orderReceiptMapper;

    @Resource
    OrderBillInvoiceMapper orderBillInvoiceMapper;

    @Resource
    OrderFeeStatementMapper orderFeeStatementMapper;

    @Resource
    OrderFeeStatementHMapper orderFeeStatementHMapper;

    @Resource
    OrderFeeHMapper orderFeeHMapper;

    @DubboReference(version = "1.0.0")
    IOrderFeeService iOrderFeeService;

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    IOrderAgingInfoService orderAgingInfoService;

    @Resource
    IOrderFeeStatementHService iOrderFeeStatementHService;

    @Autowired
    IOrderFeeStatementService iOrderFeeStatementService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService; // 日志

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @DubboReference(version = "1.0.0")
    IOrderInfoHService orderInfoHService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;
    @Resource
    ReadisUtil readisUtil;

    @Resource
    ClaimExpenseInfoMapper claimExpenseInfoMapper;

    @Resource
    ICmSalaryInfoNewService cmSalaryInfoNewService;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @DubboReference(version = "1.0.0")
    ISysRoleService iSysRoleService;

    @Override
    public PageInfo<OrderInfoDto> queryReceviceManageOrder(OrderInfoVo orderInfoVo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(orderInfoVo.getBeginDependTime())) {
            orderInfoVo.setBeginDependTime(orderInfoVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderInfoVo.getEndDependTime())) {
            orderInfoVo.setEndDependTime(orderInfoVo.getEndDependTime() + " 23:59:59");
        }
        orderInfoVo.setTenantId(loginInfo.getTenantId());
        // 添加客户单号过滤
        if (StringUtils.isNotBlank(orderInfoVo.getCustomNumber())) {
            String[] cutomNumbers = orderInfoVo.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sb = new StringBuilder();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (StringUtils.isNotBlank(cutomNumber)) {
                        sb.append(" g.CUSTOM_NUMBER like '%").append(cutomNumber).append("%' ");
                        if (index < cutomNumbers.length && StringUtils.isNotBlank(cutomNumbers[index])) {
                            sb.append(" or ");
                        }
                    }
                }
                if (StringUtils.isNotBlank(sb.toString())) {
                    orderInfoVo.setCustomNumber(sb.toString());
                }
            }
        }
        if(orderInfoVo.getCustomerIds() != null) {
            orderInfoVo.setCustomerIds(orderInfoVo.getCustomerIds().trim());
        }
        if (StringUtils.isNotBlank(orderInfoVo.getCustomerIds())) {
            StringBuffer buf = new StringBuffer();
            String[] split = orderInfoVo.getCustomerIds().split(",");
            for (String customerId : split) {
                buf.append("'").append(customerId).append("',");
            }
            orderInfoVo.setCustomerIds(buf.substring(0, buf.length() - 1));
        }

        List<OrderInfoDto> list = orderInfoMapper.queryReceviceManageOrderExport(orderInfoVo);
        // 处理应收日期
        for (OrderInfoDto orderInfoDto : list) {
            if (orderInfoDto.getBalanceType() == 1) { // 预付全款
                if (StringUtils.isNotBlank(orderInfoDto.getCreateTime())) {
                    orderInfoDto.setReceivableDate(orderInfoDto.getCreateTime());
                }
            } else if (orderInfoDto.getBalanceType() == 2) { // 预付 尾款账期
                if (StringUtils.isNotBlank(orderInfoDto.getUpdateTime())) {
                    orderInfoDto.setReceivableDate(getAccountPeriodDateStr(orderInfoDto.getUpdateTime(), orderInfoDto.getCollectionTime()));
                }
            } else if (orderInfoDto.getBalanceType() == 3) { // 预付 尾款月结
                if (StringUtils.isNotBlank(orderInfoDto.getCarDependDateT())) {
                    orderInfoDto.setReceivableDate(getMonthlyDateStr(orderInfoDto.getCarDependDateT(), orderInfoDto.getCollectionMonth(), orderInfoDto.getCollectionDay()));
                }
            }
        }

        List<OrderInfoDto> collect = null;
        if (StringUtils.isNotBlank(orderInfoVo.getBeginTime()) || StringUtils.isNotBlank(orderInfoVo.getEndTime())) {
            collect = list.stream().filter(bean -> StringUtils.isNotBlank(bean.getReceivableDate())
                            && hourMinuteBetween(bean.getReceivableDate(), orderInfoVo.getBeginTime(), orderInfoVo.getEndTime()))
                    .collect(Collectors.toList());
        }

        if (collect != null) {
            list.clear();
            list.addAll(collect);
        }

        // 分页
        PageInfo<OrderInfoDto> infoDtoPage = listToPageInfo(list, pageNum, pageSize);
        List<OrderInfoDto> records = infoDtoPage.getList();

        List<OrderInfoDto> listOuts = new ArrayList<OrderInfoDto>();
        StringBuffer orderIds = new StringBuffer();
        StringBuffer billNumberIds = new StringBuffer();
        for (OrderInfoDto out : records) {
            if (out.getFromOrderId() != null && out.getFromOrderId() > 0) {
                List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getFromOrderId());
                if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
                    Long finePrice = 0L;
                    for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                        if (orderAgingInfo != null && orderAgingInfo.getAuditSts() != null
                                && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                            finePrice += (orderAgingInfo.getFinePrice() == null ? 0 : orderAgingInfo.getFinePrice());
                        }
                    }
                    out.setIncomeExceptionFee((out.getIncomeExceptionFee() == null ? 0 : out.getIncomeExceptionFee())
                            + finePrice);
                }
            }

            out.setDesRegionName(out.getDesRegion() == null ? ""
                    : getSysStaticData("SYS_CITY", out.getDesRegion() + "").getCodeName());
            out.setOrderTypeName(out.getOrderType() == null ? ""
                    : getSysStaticData("ORDER_TYPE", out.getOrderType() + "").getCodeName());
            out.setSourceRegionName(out.getSourceRegion() == null ? ""
                    : getSysStaticData("SYS_CITY", out.getSourceRegion() + "").getCodeName());
            out.setOrderStateName(out.getOrderState() == null ? ""
                    : getSysStaticData("ORDER_STATE", out.getOrderState() + "").getCodeName());
            out.setFinanceStsName(out.getFinanceSts() == null ? ""
                    : getSysStaticData("FINANCE_STS", out.getFinanceSts() + "").getCodeName());
            out.setCarStatusName(out.getCarLengh() == null ? ""
                    : out.getCarLengh() + " " + out.getCarStatus() == null ? ""
                    : getSysStaticData("VEHICLE_STATUS@ORD", out.getCarStatus() + "").getCodeName());
            out.setOrgName(out.getOrgId() == null ? loginInfo.getName() : iSysOrganizeService.getById(out.getOrgId()).getOrgName());
            if (out.getBillNumber() != null) {// 有账单信息
                OrderBillInfo billInfo = orderBillInfoMapper.getOrderBillInfo(out.getBillNumber());
                if (billInfo != null) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    out.setOpenBillDate(billInfo.getCreateReceiptDate() == null ? ""
                            : DateUtil.formatDate(billInfo.getCreateReceiptDate(), DateUtil.DATETIME_FORMAT));
                    out.setOpenBillName(billInfo.getCreateReceiptName());
                    out.setBillCreatorName(billInfo.getCreatorName());
                    out.setBillCreatorDate(billInfo.getCreateTime() == null ? ""
                            : df.format(billInfo.getCreateTime()));
                    out.setCheckBillDate(billInfo.getCheckBillDate() == null ? ""
                            : DateUtil.formatDate(billInfo.getCheckBillDate(), DateUtil.DATETIME_FORMAT));
                    out.setCheckBillName(billInfo.getCheckBillName());
                }
                billNumberIds.append("'" + out.getBillNumber() + "',");
            }

            if (out.getOrderId() != null) {
                Long billDiff = 0L;
                Long kplDiff = 0L;
                Long oilFeeDiff = 0L;
                Long billIngDiff = 0L;
                Long otherDiff = 0L;
                List<OrderDiffInfo> diffInfos = orderDiffInfoMapper.getOrderDiffList(String.valueOf(out.getOrderId()));
                for (OrderDiffInfo diff : diffInfos) {
                    if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.BILL_DIFF) {
                        billDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.KPL_DIFF) {
                        kplDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.OIL_FEE_DIFF) {
                        oilFeeDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.BILLING_DIFF) {
                        billIngDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.OTHER_DOFF) {
                        otherDiff += diff.getDiffFee();
                    }
                }
                out.setBillDiff(billDiff);
                out.setKplDiff(kplDiff);
                out.setOilFeeDiff(oilFeeDiff);
                out.setBillIngDiff(billIngDiff);
                out.setOtherDiff(otherDiff);

                orderIds.append("'" + out.getOrderId() + "',");
            }
            listOuts.add(out);
        }
        //查询客户回单号和对账金额
        if (orderIds.length() > 0) {
            String subs = orderIds.substring(0, orderIds.length() - 1);
            List<Map<String, Object>> customerIdMap = orderReceiptMapper.getCustomerIdByOrderIds(subs);
            if (customerIdMap != null) {
                for (Map<String, Object> map : customerIdMap) {
                    for (OrderInfoDto out : listOuts) {
                        if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                            out.setCustomerId(DataFormat.getStringKey(map, "customerId"));
                            out.setCutomerNum(DataFormat.getStringKey(map, "customerNum"));
                            break;
                        }
                    }
                }
            }
            List<Map<String, Object>> diffFeeMap = orderDiffInfoMapper.getDiffFeeByOrderIds(subs);
            if (diffFeeMap != null) {
                for (Map<String, Object> map : diffFeeMap) {
                    for (OrderInfoDto out : listOuts) {
                        if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                            out.setDiffFee(DataFormat.getLongKey(map, "diffFee"));
                            break;
                        }
                    }
                }
            }
        }
        //查询账单号
        if (billNumberIds.length() > 0) {
            String subs = billNumberIds.substring(0, billNumberIds.length() - 1);
            List<Map<String, Object>> receiptNumbersMap = orderBillInvoiceMapper.getReceiptNumbersByBillNumbers(subs);
            if (receiptNumbersMap != null) {
                for (OrderInfoDto out : listOuts) {
                    for (Map<String, Object> map : receiptNumbersMap) {
                        if (out.getBillNumber() != null && out.getBillNumber().equals(DataFormat.getStringKey(map, "billNumber"))) {
                            out.setReceiptNumbers(DataFormat.getStringKey(map, "receiptNumbers"));
                            out.setReceiptNumber(DataFormat.getStringKey(map, "receiptNumber"));
                            break;
                        }
                    }
                }
            }
        }

        infoDtoPage.setList(listOuts);
        return infoDtoPage;
    }

    @Override
    public List<OrderDiffInfoDto> getOrderDiffList(String orderId, String accessToken) {
        List<OrderDiffInfoDto> outs = new ArrayList<OrderDiffInfoDto>();
        List<OrderDiffInfo> orderDiffList = orderDiffInfoMapper.getOrderDiffList(orderId);

        Map<String, OrderDiffInfo> ms = new HashMap<String, OrderDiffInfo>();
        for (OrderDiffInfo o : orderDiffList) {
            ms.put(o.getDiffType() + "", o);
        }

        // 处理对账类型
        List<SysStaticData> sysStaticDataList = new ArrayList<>();
        sysStaticDataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("DIFF_TYPE"));

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (SysStaticData staticData : sysStaticDataList) {
            OrderDiffInfo info = ms.get(staticData.getCodeValue());
            OrderDiffInfoDto out = new OrderDiffInfoDto();
            if (info != null) {

                BeanUtil.copyProperties(info, out);

                // 以下俩个字段不一致，需要手动copy
                out.setCreateDate(dtf.format(info.getCreateTime()));
                out.setDiffId(info.getId());

                out.setDiffFeeDouble(Double.parseDouble(info.getDiffFee() + "") / 100);
                out.setDiffTypeName(staticData.getCodeName());
            } else {
                out.setDiffTypeName(staticData.getCodeName());
                out.setDiffType(Integer.parseInt(staticData.getCodeValue()));
                out.setDiffFeeDouble(0.00);
                out.setDiffFee(0L);
                out.setDiffDesc("");
                out.setOrderId(Long.parseLong(orderId));
            }
            outs.add(out);
        }
        for (OrderDiffInfoDto out : outs) {
            out.setDiffFee(out.getDiffFee() / 100);
        }
        return outs;
    }

    @Override
    public void saveOrderDiff(Long orderId, List<OrderDiffInfo> orderDiffInfos, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long totalDiffFee = 0L;
        long diffFeeSum = 0;


        // 保存订单差异
        for (OrderDiffInfo info : orderDiffInfos) {
            // 必要的参数判断
            // 判断差异类型是是否传入，如果没有，则抛出异常
            if (info.getDiffType() == null || info.getDiffType() <= 0) {
                continue;
            }

            // 2、判断差异金额是否为空，若为空，则置为0
            if (org.apache.commons.lang3.StringUtils.isEmpty(info.getDiffFee() + "")) {
                info.setDiffFee(0L);
            }

            diffFeeSum += info.getDiffFee();
            totalDiffFee += info.getDiffFee();
        }

        // 1、填冲确认收入金额
        /**
         * 先通过orderId查询order_fee_statement表，
         * 若查询不到，则查询order_fee_statement_h表，
         * 若还查询不到，则抛出异常
         */
        List<OrderFeeStatement> orderFeeStatements = orderFeeStatementMapper.getFeeStatementByOrderIds("'" + orderId + "'");
        if (orderFeeStatements == null || orderFeeStatements.size() < 1) {

            List<OrderFeeStatementH> orderFeeStatemenths = orderFeeStatementHMapper.getFeeStatementHByOrderIds("'" + orderId + "'");

            if (orderFeeStatemenths == null || orderFeeStatemenths.size() < 1) {
                throw new BusinessException("订单[" + orderId + "]信息未找到。");
            } else {
                OrderFeeStatementH orderFeeStatementh = orderFeeStatemenths.get(0);
                if (orderFeeStatementh.getFinanceSts() != null &&
                        Integer.parseInt(orderFeeStatementh.getFinanceSts() + "") != EnumConsts.FINANCE_STS.FINANCE_STS_0) {
                    throw new BusinessException("订单[" + orderId + "]非已开票之前的状态不可修改差异信息。");
                }

                // 通过orderId查询order_fee_h表，获取预估应收和收入异常
                List<OrderFeeH> orderFeeHList = orderFeeHMapper.getOrderFeeHByOrderId(orderId);

                if (orderFeeHList == null || orderFeeHList.size() < 1) {
                    throw new BusinessException("订单[" + orderId + "]信息未找到。");
                }

                OrderFeeH orderFeeh = orderFeeHList.get(0);
                // 确认收入 = 预估应收+异常款（收入异常） + 确认差异
                long confirmAmount = (orderFeeh.getCostPrice() == null ? 0 : orderFeeh.getCostPrice())
                        + (orderFeeh.getIncomeExceptionFee() == null ? 0 : orderFeeh.getIncomeExceptionFee())
                        + diffFeeSum;
                orderFeeStatementHMapper.updateOrderDiffByOrderId(confirmAmount > 0 ? confirmAmount : 0, diffFeeSum, orderId, loginInfo.getId(), LocalDateTime.now());

            }
        } else {

            OrderFeeStatement orderFeeStatement = orderFeeStatements.get(0);
            if (orderFeeStatement.getFinanceSts() != null &&
                    Integer.parseInt(orderFeeStatement.getFinanceSts() + "") != EnumConsts.FINANCE_STS.FINANCE_STS_0) {
                throw new BusinessException("订单[" + orderId + "]非已开票之前的状态不可修改差异信息。");
            }

            // 通过orderId查询order_fee表，获取预估应收和收入异常
            OrderFee orderFee = iOrderFeeService.getOrderFeeByOrderId(orderId);

            if (orderFee == null) {
                throw new BusinessException("订单[" + orderId + "]信息未找到。");
            }

            // 确认收入 = 预估应收+异常款（收入异常） + 确认差异
            long confirmAmount = (orderFee.getCostPrice() == null ? 0 : orderFee.getCostPrice())
                    + (orderFee.getIncomeExceptionFee() == null ? 0 : orderFee.getIncomeExceptionFee())
                    + diffFeeSum;

            orderFeeStatementMapper.updateOrderDiffByOrderId(confirmAmount > 0 ? confirmAmount : 0, diffFeeSum, orderId, loginInfo.getId(), LocalDateTime.now());

        }

        List<OrderDiffInfo> list = orderDiffInfoMapper.getOrderDiffList(orderId.toString());
        if (list != null && list.size() > 0) {
            // 2、删除原来的订单差异
            orderDiffInfoMapper.deleteDiffInfoByOrderId(orderId);
        }

        // 3、保存订单差异
        for (OrderDiffInfo info : orderDiffInfos) {
            // 必要的参数判断
            // 1、判断差异类型是是否传入，如果没有，则抛出异常
            if (info.getDiffType() == null || info.getDiffType() <= 0) {
                continue;
            }

            // 2、判断差异金额是否为空，若为空，则置为0
            if (org.apache.commons.lang3.StringUtils.isEmpty(info.getDiffFee() + "")) {
                info.setDiffFee(0L);
            }

            if (org.apache.commons.lang3.StringUtils.isBlank(info.getCreatorName())) {
                info.setCreatorName(loginInfo.getName());
            }
            if (info.getCreatorId() == null) {
                info.setCreatorId(loginInfo.getId());
            }
            // 若创建时间为空，则创建时间为当前时间
            if (info.getCreateTime() == null
                    || org.apache.commons.lang3.StringUtils.equals("0000-00-00 00:00:00", info.getCreateTime() + "")) {
                info.setCreateTime(LocalDateTime.now());
            }
            info.setTenantId(loginInfo.getTenantId());
            info.setOperDate(LocalDateTime.now());
            info.setOperId(loginInfo.getId());

            orderDiffInfoMapper.insert(info);
        }

        // 记录操作日志
        String msg = "更新差异：" + (totalDiffFee / 100);
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageOrder, SysOperLogConst.OperType.Update, msg, accessToken, Long.valueOf(orderId));
    }

    @Override
    public Integer batchUpdateCheckName(List<Long> orderId, String checkName) {
        List<OrderFeeStatement> statementList = iOrderFeeStatementService.getOrderFreeStatementListByOrderIdList(orderId);
        List<OrderFeeStatementH> statementHList = iOrderFeeStatementHService.getOrderFreeStatementHListByOrderIdList(orderId);
        int count = 0;
        for (OrderFeeStatement orderFeeStatement : statementList) {
            if (StringUtils.isBlank(orderFeeStatement.getBillNumber())) {
                orderFeeStatement.setCheckName(checkName);
                iOrderFeeStatementService.update(orderFeeStatement);
                count++;
            }
        }

        for (OrderFeeStatementH orderFeeStatementH : statementHList) {
            if (StringUtils.isBlank(orderFeeStatementH.getBillNumber())) {
                orderFeeStatementH.setCheckName(checkName);
                iOrderFeeStatementHService.update(orderFeeStatementH);
                count++;
            }
        }

        return count;
    }

    @Override
    @Async
    public void export(OrderInfoVo orderInfoVo, Boolean isOrder, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(orderInfoVo.getBeginDependTime())) {
            orderInfoVo.setBeginDependTime(orderInfoVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(orderInfoVo.getEndDependTime())) {
            orderInfoVo.setEndDependTime(orderInfoVo.getEndDependTime() + " 23:59:59");
        }
        orderInfoVo.setTenantId(loginInfo.getTenantId());
        // 添加客户单号过滤
        if (StringUtils.isNotBlank(orderInfoVo.getCustomNumber())) {
            String[] cutomNumbers = orderInfoVo.getCustomNumber().split("\n");
            if (cutomNumbers.length > 0) {
                int index = 0;
                StringBuilder sb = new StringBuilder();
                for (String cutomNumber : cutomNumbers) {
                    index++;
                    if (StringUtils.isNotBlank(cutomNumber)) {
                        sb.append(" g.CUSTOM_NUMBER like '%").append(cutomNumber).append("%' ");
                        if (index < cutomNumbers.length && StringUtils.isNotBlank(cutomNumbers[index])) {
                            sb.append(" or ");
                        }
                    }
                }
                if (StringUtils.isNotBlank(sb.toString())) {
                    orderInfoVo.setCustomNumber(sb.toString());
                }
            }
        }
        List<OrderInfoDto> records = orderInfoMapper.queryReceviceManageOrderExport(orderInfoVo);
        List<OrderInfoDto> listOuts = new ArrayList<OrderInfoDto>();
        StringBuffer orderIds = new StringBuffer();
        StringBuffer billNumberIds = new StringBuffer();
        for (OrderInfoDto out : records) {
            if (out.getFromOrderId() != null && out.getFromOrderId() > 0) {
                List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getFromOrderId());
                if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
                    Long finePrice = 0L;
                    for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                        if (orderAgingInfo != null && orderAgingInfo.getAuditSts() != null
                                && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                            finePrice += (orderAgingInfo.getFinePrice() == null ? 0 : orderAgingInfo.getFinePrice());
                        }
                    }
                    out.setIncomeExceptionFee((out.getIncomeExceptionFee() == null ? 0 : out.getIncomeExceptionFee())
                            + finePrice);
                }
            }

            out.setDesRegionName(out.getDesRegion() == null ? ""
                    : getSysStaticData("SYS_CITY", out.getDesRegion() + "").getCodeName());
            out.setOrderTypeName(out.getOrderType() == null ? ""
                    : getSysStaticData("ORDER_TYPE", out.getOrderType() + "").getCodeName());
            out.setSourceRegionName(out.getSourceRegion() == null ? ""
                    : getSysStaticData("SYS_CITY", out.getSourceRegion() + "").getCodeName());
            out.setOrderStateName(out.getOrderState() == null ? ""
                    : getSysStaticData("ORDER_STATE", out.getOrderState() + "").getCodeName());
            out.setFinanceStsName(out.getFinanceSts() == null ? ""
                    : getSysStaticData("FINANCE_STS", out.getFinanceSts() + "").getCodeName());
            out.setCarStatusName(out.getCarLengh() == null ? ""
                    : out.getCarLengh() + " " + out.getCarStatus() == null ? ""
                    : getSysStaticData("VEHICLE_STATUS@ORD", out.getCarStatus() + "").getCodeName());
            out.setOrgName(out.getOrgId() == null ? loginInfo.getName() : iSysOrganizeService.getById(out.getOrgId()).getOrgName());
            if (out.getBillNumber() != null) {// 有账单信息
                OrderBillInfo billInfo = orderBillInfoMapper.getOrderBillInfo(out.getBillNumber());
                if (billInfo != null) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    out.setOpenBillDate(billInfo.getCreateReceiptDate() == null ? ""
                            : DateUtil.formatDate(billInfo.getCreateReceiptDate(), DateUtil.DATETIME_FORMAT));
                    out.setOpenBillName(billInfo.getCreateReceiptName());
                    out.setBillCreatorName(billInfo.getCreatorName());
                    out.setBillCreatorDate(billInfo.getCreateTime() == null ? ""
                            : df.format(billInfo.getCreateTime()));
                    out.setCheckBillDate(billInfo.getCheckBillDate() == null ? ""
                            : DateUtil.formatDate(billInfo.getCheckBillDate(), DateUtil.DATETIME_FORMAT));
                    out.setCheckBillName(billInfo.getCheckBillName());
                }
                billNumberIds.append("'" + out.getBillNumber() + "',");
            }

            if (out.getOrderId() != null) {
                Long billDiff = 0L;
                Long kplDiff = 0L;
                Long oilFeeDiff = 0L;
                Long billIngDiff = 0L;
                Long otherDiff = 0L;
                List<OrderDiffInfo> diffInfos = orderDiffInfoMapper.getOrderDiffList(String.valueOf(out.getOrderId()));
                for (OrderDiffInfo diff : diffInfos) {
                    if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.BILL_DIFF) {
                        billDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.KPL_DIFF) {
                        kplDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.OIL_FEE_DIFF) {
                        oilFeeDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.BILLING_DIFF) {
                        billIngDiff += diff.getDiffFee();
                    } else if (diff.getDiffType() == SysStaticDataEnum.DIFF_TYPE.OTHER_DOFF) {
                        otherDiff += diff.getDiffFee();
                    }
                }
                out.setBillDiff(billDiff);
                out.setKplDiff(kplDiff);
                out.setOilFeeDiff(oilFeeDiff);
                out.setBillIngDiff(billIngDiff);
                out.setOtherDiff(otherDiff);

                orderIds.append("'" + out.getOrderId() + "',");
            }
            listOuts.add(out);
        }
        //查询客户回单号和对账金额
        if (orderIds.length() > 0) {
            String subs = orderIds.substring(0, orderIds.length() - 1);
            List<Map<String, Object>> customerIdMap = orderReceiptMapper.getCustomerIdByOrderIds(subs);
            if (customerIdMap != null) {
                for (Map<String, Object> map : customerIdMap) {
                    for (OrderInfoDto out : listOuts) {
                        if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                            out.setCustomerId(DataFormat.getStringKey(map, "customerId"));
                            out.setCutomerNum(DataFormat.getStringKey(map, "customerNum"));
                            break;
                        }
                    }
                }
            }
            List<Map<String, Object>> diffFeeMap = orderDiffInfoMapper.getDiffFeeByOrderIds(subs);
            if (diffFeeMap != null) {
                for (Map<String, Object> map : diffFeeMap) {
                    for (OrderInfoDto out : listOuts) {
                        if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                            out.setDiffFee(DataFormat.getLongKey(map, "diffFee"));
                            break;
                        }
                    }
                }
            }
        }
        //查询账单号
        if (billNumberIds.length() > 0) {
            String subs = billNumberIds.substring(0, billNumberIds.length() - 1);
            List<Map<String, Object>> receiptNumbersMap = orderBillInvoiceMapper.getReceiptNumbersByBillNumbers(subs);
            if (receiptNumbersMap != null) {
                for (OrderInfoDto out : listOuts) {
                    for (Map<String, Object> map : receiptNumbersMap) {
                        if (out.getBillNumber() != null && out.getBillNumber().equals(DataFormat.getStringKey(map, "billNumber"))) {
                            out.setReceiptNumbers(DataFormat.getStringKey(map, "receiptNumbers"));
                            out.setReceiptNumber(DataFormat.getStringKey(map, "receiptNumber"));
                            break;
                        }
                    }
                }
            }
        }

        // 合计
        Long costPrice = 0L; // 订单收入 // 订单导出字段
        Long incomeExceptionFee = 0L; // 异常款 // 订单导出字段
        Long affirmIncomeFee = 0L; // 确认应收 // 账单详情导出字段
        Long estimateIncomeFee = 0L; // 订单收入合计
        Long billDiff = 0L; // 对账差异
        Long kplDiff = 0L; // 品质罚款
        Long oilFeeDiff = 0L; // 油价差异
        Long billIngDiff = 0L; // 开单差异
        Long otherDiff = 0L; // 其它差异
        Long getAmount = 0L; // 核销金额
        Long handleFee = 0L; // 应付运费
        Long marginFee = 0L; // 毛利

        for (OrderInfoDto listOut : listOuts) {

            if (null != listOut.getPaymentWay()) {
                if (listOut.getPaymentWay() == 1) {
                    OrderDetailsDto orderDetailsDto = orderInfoHService.queryOrderDetails(listOut.getOrderId(), OrderConsts.orderDetailsType.SELECT, accessToken);
                    listOut.setIntelligent(orderDetailsDto.getOrderFeeExt().getEstFee() != null ? orderDetailsDto.getOrderFeeExt().getEstFee() : 0);
                } else if (listOut.getPaymentWay() == 2) {
                    OrderDetailsDto orderDetailsDto = orderInfoHService.queryOrderDetails(listOut.getOrderId(), OrderConsts.orderDetailsType.SELECT, accessToken);
                    listOut.setReimbursement(orderDetailsDto.getTotalCost() != null ? orderDetailsDto.getTotalCost() : 0);
                }
            }

            costPrice += null == listOut.getCostPrice() ? 0 : listOut.getCostPrice();
            incomeExceptionFee += null == listOut.getIncomeExceptionFee() ? 0 : listOut.getIncomeExceptionFee();
            affirmIncomeFee += null == listOut.getAffirmIncomeFee() ? 0 : listOut.getAffirmIncomeFee();
            estimateIncomeFee += null == listOut.getEstimateIncomeFee() ? 0 : listOut.getEstimateIncomeFee();
            billDiff += null == listOut.getBillDiff() ? 0 : listOut.getBillDiff();
            kplDiff += null == listOut.getKplDiff() ? 0 : listOut.getKplDiff();
            oilFeeDiff += null == listOut.getOilFeeDiff() ? 0 : listOut.getOilFeeDiff();
            billIngDiff += null == listOut.getBillIngDiff() ? 0 : listOut.getBillIngDiff();
            otherDiff += null == listOut.getOtherDiff() ? 0 : listOut.getOtherDiff();
            getAmount += null == listOut.getGetAmount() ? 0 : listOut.getGetAmount();
            handleFee += null == listOut.getHandleFee() ? 0 : listOut.getHandleFee();
            marginFee += null == listOut.getMarginFee() ? 0 : listOut.getMarginFee();
        }

        OrderInfoDto additionDto = new OrderInfoDto();
        additionDto.setCostPrice(costPrice);
        additionDto.setIncomeExceptionFee(incomeExceptionFee);
        additionDto.setAffirmIncomeFee(affirmIncomeFee);
        additionDto.setEstimateIncomeFee(estimateIncomeFee);
        additionDto.setBillDiff(billDiff);
        additionDto.setKplDiff(kplDiff);
        additionDto.setOilFeeDiff(oilFeeDiff);
        additionDto.setBillIngDiff(billIngDiff);
        additionDto.setOtherDiff(otherDiff);
        additionDto.setGetAmount(getAmount);
        additionDto.setHandleFee(handleFee);
        additionDto.setMarginFee(marginFee);

        listOuts.add(additionDto);

        try {
            String[] showName = null;
            String[] resourceFild = null;
            if (isOrder) { // 订单导出
                showName = new String[]{"订单号", "靠台时间", "客户名称", "客户简称", "对账名称",
                        "客户单号", "客户回单号", "起始地", "到达地", "线路名称",
                        "线路属性", "归属部门", "货物信息", "车牌号", "车型",
                        "箱挂号", "实际靠台时间", "实际离台时间", "实际到达时间",
                        "订单收入", "异常款", "订单收入合计", "确认应收", "对账差异",
                        "品质罚款", "油价差异", "开单差异", "其它差异", "核销金额",
                        "应付运费", "毛利", "毛利率", "账单号"/*, "发票申请号"*/, "核销状态",
                        "业务员", "账单创建时间", "账单创建人", "开票时间", "开票人",
                        "订单核销时间", "订单核销人"};
                resourceFild = new String[]{"getOrderId", "getDependDate", "getCompanyName", "getCustomName", "getCheckName",
                        "getCustomNumber", "getCustomerId", "getSourceRegionName", "getDesRegionName", "getSourceName",
                        "getOrderTypeName", "getOrgName", "getGoodsInfo", "getPlateNumber", "getCarType",
                        "getTrailerPlate", "getCarDependDate", "getCarStartDate", "getCarArriveDate",
                        "getCostPriceDouble", "getIncomeExceptionFeeDouble", "getEstimateIncomeFeeDouble", "getAffirmIncomeFeeDouble", "getBillDiffDouble",
                        "getKplDiffDouble", "getOilFeeDiffDouble", "getBillIngDiffDouble", "getOtherDiffDouble", "getGetAmountDouble",
                        "getHandleFeeDouble", "getMarginFeeDouble", "getMarginFeeString", "getBillNumber"/*, "getReceiptNumber"*/, "getFinanceStsName",
                        "getOpName", "getBillCreatorDate", "getBillCreatorName", "getOpenBillDate", "getOpenBillName",
                        "getCheckBillDate", "getCheckBillName"};
            } else { // 账单详情导出
                showName = new String[]{"订单号", "靠台时间", "客户归类", "客户简称",
                        "客户名称", "客户单号", "起始地", "到达地",
                        "线路名称", "货物信息", "车牌号", "车型",
                        "箱挂号", "实际靠台时间", "实际离台时间",
                        "实际到达时间", "订单收入合计", "确认应收",
                        "对账差异", "品质罚款", "油价差异", "开单差异",
                        "其它差异", "核销金额", "应付运费", "毛利",
                        "毛利率", "账单号"/*, "发票申请号"*/, "核销状态",
                        "业务员", "账单创建时间", "账单创建人",
                        "开票时间", "开票人", "订单核销时间", "订单核销人"};
                resourceFild = new String[]{"getOrderId", "getDependDate", "getCustomerCategory", "getCustomName",
                        "getCompanyName", "getCustomNumber", "getSourceRegionName", "getDesRegionName",
                        "getSourceName", "getGoodsInfo", "getPlateNumber", "getCarType",
                        "getTrailerPlate", "getCarDependDate", "getCarStartDate",
                        "getCarArriveDate", "getEstimateIncomeFeeDouble", "getAffirmIncomeFeeDouble",
                        "getBillDiffDouble", "getKplDiffDouble", "getOilFeeDiffDouble", "getBillIngDiffDouble",
                        "getOtherDiffDouble", "getGetAmountDouble", "getHandleFeeDouble", "getMarginFeeDouble",
                        "getMarginFeeString", "getBillNumber"/*, "getReceiptNumber"*/, "getFinanceStsName",
                        "getOpName", "getBillCreatorDate", "getBillCreatorName",
                        "getOpenBillDate", "getOpenBillName", "getCheckBillDate", "getCheckBillName"};
            }

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(listOuts, showName, resourceFild, OrderInfoDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "应收管理订单信息.xlsx", inputStream.available());
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


    @Override
    public Page<VehicleOrderDto> merchantsVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        Page<VehicleOrderDto> vehicleOrderDto = orderInfoMapper.merchantsVehicleOrder(new Page<>(pageNum, pageSize), vehicleOrderVo, loginInfo.getTenantId());
        List<VehicleOrderDto> records = vehicleOrderDto.getRecords();
        List<VehicleOrderDto> list = new ArrayList<>();
        for (VehicleOrderDto dto : records) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }
//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            dto.setCostPrice(dto.getCostPrice() == null ? 0 : dto.getCostPrice());
            dto.setProblemPrice(dto.getProblemPrice() == null ? 0 : dto.getProblemPrice());
            dto.setConfirmDiffAmount(dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount());
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue(dto.getCostPrice() + (dto.getProblemPrice()));
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable(dto.getCostPrice() + dto.getProblemPrice() + dto.getConfirmDiffAmount());
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());

            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }
        }
        vehicleOrderDto.setRecords(records);
        return vehicleOrderDto;
    }

    @Override
    @Async
    public void merchantsExport(VehicleOrderVo vehicleOrderVo, ImportOrExportRecords importOrExportRecords, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        List<VehicleOrderDto> list = orderInfoMapper.merchantsExport(vehicleOrderVo, loginInfo.getTenantId());
        for (VehicleOrderDto dto : list) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }
//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            dto.setCostPrice(dto.getCostPrice() == null ? 0 : dto.getCostPrice()/100);
            dto.setProblemPrice(dto.getProblemPrice() == null ? 0 : dto.getProblemPrice()/100);
            dto.setConfirmDiffAmount(dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount()/100);
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue(dto.getCostPrice() + (dto.getProblemPrice()));
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable(dto.getCostPrice() + dto.getProblemPrice() + dto.getConfirmDiffAmount());
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());
            //中标价
            dto.setTotalFee(dto.getTotalFee()==null ?0:dto.getTotalFee()/100);
            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "靠台时间", "录入时间", "客户全称", "客户单号", "线路编码", "运输线路", "经停信息", "线路名称", "车牌号码", "挂车车牌", "主驾姓名", "副驾姓名", "跟单人员",
                    "录入人员", "归属部门", "货物信息", "货物类型", "订单收入", "异常款", "订单收入合计", "对账调整", "确认应收", "中标价", "订单状态"};
            resourceFild = new String[]{
                    "getOrderId", "getDependTime", "getCreateTime", "getCompanyName", "getCustomNumber", "getSourceCode", "getOrderLine", "getLineNode", "getSourceName", "getPlateNumber", "getTrailerPlate", "getCarDriverMan", "getCopilotMan", "getLocalUserName",
                    "getOpName", "getTenantName", "getGoodsInfo", "getGoodsTypeName", "getCostPrice", "getProblemPrice", "getTotalOrderRevenue", "getConfirmDiffAmount", "getNotarizeReceivable", "getTotalFee", "getOrderStateName"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, VehicleOrderDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "merchants.xlsx", inputStream.available());
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
    public Page<VehicleOrderDto> transferVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        Page<VehicleOrderDto> vehicleOrderDto = orderInfoMapper.transferVehicleOrder(new Page<>(pageNum, pageSize), vehicleOrderVo, loginInfo.getTenantId());
        List<VehicleOrderDto> records = vehicleOrderDto.getRecords();
        List<VehicleOrderDto> list = new ArrayList<>();
        for (VehicleOrderDto dto : records) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }


//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            dto.setCostPrice(dto.getCostPrice() == null ? 0 : dto.getCostPrice());
            dto.setProblemPrice(dto.getProblemPrice() == null ? 0 : dto.getProblemPrice());
            dto.setConfirmDiffAmount(dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount());
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue(dto.getCostPrice() + (dto.getProblemPrice()));
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable(dto.getCostPrice() + dto.getProblemPrice() + dto.getConfirmDiffAmount());
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());

            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }
        }
        vehicleOrderDto.setRecords(records);
        return vehicleOrderDto;
    }

    @Override
    @Async
    public void transferExport(VehicleOrderVo vehicleOrderVo, ImportOrExportRecords importOrExportRecords, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        List<VehicleOrderDto> list = orderInfoMapper.transferExport(vehicleOrderVo, loginInfo.getTenantId());
        for (VehicleOrderDto dto : list) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }
//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            dto.setCostPrice(dto.getCostPrice() == null ? 0 : dto.getCostPrice()/100);
            dto.setProblemPrice(dto.getProblemPrice() == null ? 0 : dto.getProblemPrice()/100);
            dto.setConfirmDiffAmount(dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount()/100);
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue(dto.getCostPrice() + (dto.getProblemPrice()));
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable(dto.getCostPrice() + dto.getProblemPrice() + dto.getConfirmDiffAmount());
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());
            //中标价
            dto.setTotalFee(dto.getTotalFee()==null ?0:dto.getTotalFee()/100);
            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "靠台时间", "录入时间", "客户全称", "客户单号", "线路编码", "运输线路", "经停信息", "线路名称", "车牌号码", "挂车车牌", "主驾姓名", "副驾姓名", "跟单人员",
                    "录入人员", "归属部门", "货物信息", "货物类型", "订单收入", "异常款", "订单收入合计", "对账调整", "确认应收", "中标价", "订单状态"};
            resourceFild = new String[]{
                    "getOrderId", "getDependTime", "getCreateTime", "getCompanyName", "getCustomNumber", "getSourceCode", "getOrderLine", "getLineNode", "getSourceName", "getPlateNumber", "getTrailerPlate", "getCarDriverMan", "getCopilotMan", "getLocalUserName",
                    "getOpName", "getTenantName", "getGoodsInfo", "getGoodsTypeName", "getCostPrice", "getProblemPrice", "getTotalOrderRevenue", "getConfirmDiffAmount", "getNotarizeReceivable", "getTotalFee", "getOrderStateName"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, VehicleOrderDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "merchants.xlsx", inputStream.available());
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
    public Page<VehicleOrderDto> ownVehicleOrder(VehicleOrderVo vehicleOrderVo, Integer pageNum, Integer pageSize, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        Page<VehicleOrderDto> vehicleOrderDto = orderInfoMapper.ownVehicleOrder(new Page<>(pageNum, pageSize), vehicleOrderVo, loginInfo.getTenantId());
        List<VehicleOrderDto> records = vehicleOrderDto.getRecords();
        List<VehicleOrderDto> list = new ArrayList<>();
        for (VehicleOrderDto dto : records) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }
//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            //路桥费
            Double pontage = dto.getPontage() == null ? 0 : dto.getPontage();
            Double preEtcFee = dto.getPreEtcFee() == null ? 0 : dto.getPreEtcFee();
            dto.setPontage(pontage + preEtcFee);

            dto.setCostPrice(dto.getCostPrice() == null ? 0 : dto.getCostPrice());
            dto.setProblemPrice(dto.getProblemPrice() == null ? 0 : dto.getProblemPrice());
            dto.setConfirmDiffAmount(dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount());
            dto.setPreOilFee(dto.getPreOilFee() == null ? 0 : dto.getPreOilFee());
            dto.setPreOilVirtualFee(dto.getPreOilVirtualFee() == null ? 0 : dto.getPreOilVirtualFee());
            if (dto.getPaymentWay()!=null && (dto.getPaymentWay() == 1 || dto.getPaymentWay() == 2)) {//智能模式、报账模式中标价为0
                dto.setTotalFee(0.0);
            } else {
                dto.setTotalFee(dto.getTotalFee() == null ? 0 : dto.getTotalFee());
            }
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue(dto.getCostPrice() + (dto.getProblemPrice()));
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable(dto.getCostPrice() + dto.getProblemPrice() + dto.getConfirmDiffAmount());
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());
            //成本模式
            dto.setPaymentWayName(dto.getPaymentWay() == null ? "" : getSysStaticData("PAYMENT_WAY", String.valueOf(dto.getPaymentWay())).getCodeName());
            //加油費用
            dto.setOilFeeTotal(dto.getPreOilFee() + dto.getPreOilVirtualFee());
            List<OrderCostOtherReport> reportList = orderInfoMapper.getConsumeFee(dto.getOrderId());
            //上报费用合计
            Double consumeFee = 0.0;
            for (OrderCostOtherReport report : reportList) {
                report.setConsumeFee(report.getConsumeFee() == null ? 0 : report.getConsumeFee());
                consumeFee = consumeFee + report.getConsumeFee();
            }
            dto.setConsumeFee(consumeFee);
            //订单成本--直接成本
            if (dto.getPaymentWay() != null) {
                if (dto.getPaymentWay() == 1) {
                    dto.setDriverDaySalary(dto.getDriverDaySalary()==null?0:dto.getDriverDaySalary());
                    //智能模式=订单预估成本(补贴+油费+路桥费)
                    dto.setOrderFeeExt(dto.getDriverDaySalary()+dto.getOilFeeTotal()+dto.getPontage());
                    dto.setOrderCost(dto.getOrderFeeExt() == null ? 0 : dto.getOrderFeeExt());
                } else if (dto.getPaymentWay() == 2) {
                    //报账模式(油费+上报费用)
                    dto.setOilFeeTotal(dto.getOilFeeTotal() == null ? 0 : dto.getOilFeeTotal());
                    dto.setConsumeFee(dto.getConsumeFee() == null ? 0 : dto.getConsumeFee());
                    dto.setOrderCost(dto.getOilFeeTotal() + dto.getConsumeFee());
                } else if (dto.getPaymentWay() == 3) {
                    //承包模式=中标价
                    dto.setOrderCost(dto.getTotalFee() == null ? 0 : dto.getTotalFee());
                }
            }else {
                dto.setOrderCost(dto.getOrderFeeExt() == null ? 0 : dto.getOrderFeeExt());
            }
            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }

           //返回资金归属司机的借支类型
            List<String> loanBelongDriverSubjectList = this.getLoanBelongDriverSubjectList();
            List<String> loanBelongAdminSubjectList = this.getLoanBelongAdminSubjectList();


            //司机借支
            Double driverOaLoanAmount = this.getOaLoanAmount(3, dto.getOrderId(), accessToken, loanBelongDriverSubjectList,subOrgList,aBoolean);
            if (driverOaLoanAmount == null) {
                driverOaLoanAmount = 0.0;
            }
            dto.setDriverOaLoanAmount(driverOaLoanAmount);
            //员工借支
            Double userOaLoanAmount = this.getOaLoanAmount(2, dto.getOrderId(), accessToken, loanBelongAdminSubjectList,subOrgList,aBoolean);
            if (userOaLoanAmount == null) {
                userOaLoanAmount = 0.0;
            }
            dto.setUserOaLoanAmount(userOaLoanAmount);
            //司机报销
//            LambdaQueryWrapper<ClaimExpenseInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
//            lambdaQueryWrapper.eq(ClaimExpenseInfo::getOrderId, dto.getOrderId());
//            lambdaQueryWrapper.eq(ClaimExpenseInfo::getTenantId, loginInfo.getTenantId());
//            List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoMapper.selectList(lambdaQueryWrapper);
//            long amount = 0;
//            for (ClaimExpenseInfo claim : claimExpenseInfos) {
//                amount = amount + claim.getAmount();
//            }
//            dto.setAmount(amount);
            Double amount = orderInfoMapper.getOrderReimburse(dto.getOrderId(), loginInfo.getTenantId());
            if (amount==null){
                amount=0.0;
            }
            dto.setAmount(amount);

            //车辆折旧=资产单月折旧/30*(到达时期-靠台时期)
            double depreciationCost = 0;
            DateCostDto assetDetails = orderInfoMapper.getAssetDetails(loginInfo.getTenantId(), dto.getPlateNumber());
            //资产单月折旧：（总价 - 残值）/折旧月数
            long zcdyzj = 0;
            if (assetDetails != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(assetDetails.getPrice()) && org.apache.commons.lang3.StringUtils.isNotBlank(assetDetails.getResidual()) && assetDetails.getDepreciatedMonth() != null) {
                    Long cz = Long.parseLong(assetDetails.getPrice()) - Long.parseLong(assetDetails.getResidual());//差值：（总价 - 残值）
                    if (cz > 0 && assetDetails.getDepreciatedMonth() > 0) {
                        zcdyzj = cz / assetDetails.getDepreciatedMonth();
                    }
                }
            }
            if (dto.getCarArriveDate()!=null && dto.getDependTime()!=null) {
                LocalDate carArriveDate = dto.getCarArriveDate().toLocalDate();
                LocalDate DependDate = dto.getCarArriveDate().toLocalDate();
                if (carArriveDate.isEqual(DependDate)){
                    depreciationCost = zcdyzj / 30 * 1 ;
                }else {
                    Duration duration = Duration.between(dto.getDependTime(), dto.getCarArriveDate());//获取时间间隔
                    long l = duration.toDays();
                    depreciationCost = zcdyzj / 30 * duration.toDays();
                }
            }
            dto.setDepreciationCost(depreciationCost);
            //司机工资
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(dto.getOrderId());
            String carDriverPhone = "";
            if (StringUtils.isNotBlank(orderScheduler.getCarDriverPhone())) {
                carDriverPhone = orderScheduler.getCarDriverPhone();
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(dto.getOrderId());
                carDriverPhone = orderSchedulerH.getCarDriverPhone();
            }

            LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(CmSalaryInfoNew::getCarDriverPhone, carDriverPhone);
            Date from = Date.from(dto.getCreateTime().toInstant(ZoneOffset.of("+8")));
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM");
            String format = simpleDateFormatDay.format(from);
            queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, format);

            List<CmSalaryInfoNew> cmSalaryInfoNewList = cmSalaryInfoNewService.list(queryWrapper);
            long basicSalaryFee = 0;
            for (CmSalaryInfoNew cm : cmSalaryInfoNewList) {
                cm.setBasicSalaryFee(cm.getBasicSalaryFee()==null?0:cm.getBasicSalaryFee());
                basicSalaryFee = basicSalaryFee + cm.getBasicSalaryFee();
            }
            dto.setBasicSalaryFee(basicSalaryFee);

            //变动成本=司机借支+员工借支+司机报销+车辆折旧+司机工资
            double variableCost = driverOaLoanAmount + userOaLoanAmount + amount + depreciationCost + basicSalaryFee;
            dto.setVariableCost(variableCost);
            //订单毛利=订单收入合计-直接成本-变动成本
            dto.setOrderMargin(dto.getTotalOrderRevenue() - dto.getOrderCost() - variableCost);

        }
        vehicleOrderDto.setRecords(records);
        return vehicleOrderDto;
    }

    @Override
    public void ownExport(VehicleOrderVo vehicleOrderVo, ImportOrExportRecords importOrExportRecords, String accessToken) {
        List<SysStaticData> sysStaticDataList5 = readisUtil.getSysStaticDataList("SYS_CITY");
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginDependTime())) {
            vehicleOrderVo.setBeginDependTime(vehicleOrderVo.getBeginDependTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndDependTime())) {
            vehicleOrderVo.setEndDependTime(vehicleOrderVo.getEndDependTime() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getBeginOrderTime())) {
            vehicleOrderVo.setBeginOrderTime(vehicleOrderVo.getBeginOrderTime() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(vehicleOrderVo.getEndOrderTime())) {
            vehicleOrderVo.setEndOrderTime(vehicleOrderVo.getEndOrderTime() + " 23:59:59");
        }
        List<VehicleOrderDto> vehicleOrderDto = orderInfoMapper.ownExport(vehicleOrderVo, loginInfo.getTenantId());
        for (VehicleOrderDto dto : vehicleOrderDto) {
            //  查询订单线路详情
            com.youming.youche.order.dto.OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(dto.getOrderId(), sysStaticDataList5);
            //获取运输线路
            String orderLine = orderInfoDto.getOrderLine();
            String lines="";
            String lineNodes="";
            String[] orderLineSplit = orderLine.split(" -> ");
            List<String> tmp = new ArrayList<String>();
            for(String str:orderLineSplit){
                if(!str.trim().equals("")){
                    tmp.add(str);
                }
            }
            orderLineSplit= tmp.toArray(new String[0]);
            if (orderLineSplit.length==2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode("");
            }else if (orderLineSplit.length>2) {
                for (int i = 0; i < orderLineSplit.length; i++) {
                    if (i == 0) {
                        lines = orderLineSplit[i];
                    } else if (i == orderLineSplit.length - 1) {
                        lines = lines + "->" + orderLineSplit[i];
                    }else{
                        if (i==orderLineSplit.length - 2){
                            lineNodes=lineNodes+orderLineSplit[i];
                        }else {
                            lineNodes=lineNodes+orderLineSplit[i]+" -> ";
                        }
                    }
                }
                dto.setOrderLine(lines);
                dto.setLineNode(lineNodes);
            }
//            if (StringUtils.isNotBlank(dto.getSourceRegion()) && StringUtils.isNotBlank(dto.getDesRegion())){
//                dto.setReginInfo(dto.getSourceRegion()+"->"+dto.getDesRegion());//运输路线
//            }
            //路桥费
            Double pontage = dto.getPontage() == null ? 0 : dto.getPontage();
            Double preEtcFee = dto.getPreEtcFee() == null ? 0 : dto.getPreEtcFee();
            dto.setPontage((pontage + preEtcFee)/100);

            Double costPrice = dto.getCostPrice() == null ? 0 : dto.getCostPrice();
            dto.setCostPrice(costPrice/100);
            Double problemPrice=dto.getProblemPrice() == null ? 0 : dto.getProblemPrice();
            dto.setProblemPrice(problemPrice/100);
            Double confirmDiffAmount=dto.getConfirmDiffAmount() == null ? 0 : dto.getConfirmDiffAmount();
            dto.setConfirmDiffAmount(confirmDiffAmount/100);
            dto.setPreOilFee(dto.getPreOilFee() == null ? 0 : dto.getPreOilFee());
            dto.setPreOilVirtualFee(dto.getPreOilVirtualFee() == null ? 0 : dto.getPreOilVirtualFee());
            if (dto.getPaymentWay()!=null && (dto.getPaymentWay() == 1 || dto.getPaymentWay() == 2)) {//智能模式、报账模式中标价为0
                dto.setTotalFee(0.0);
            } else {
                dto.setTotalFee(dto.getTotalFee() == null ? 0 : dto.getTotalFee()/100);
            }
            //订单收入合计=订单收入+(异常款)
            dto.setTotalOrderRevenue((costPrice + (problemPrice))/100);
            //确认应收=订单收入合计+对账调整
            dto.setNotarizeReceivable((costPrice + (problemPrice) + confirmDiffAmount)/100);
            dto.setOrderStateName(dto.getOrderState() == null ? "" : getSysStaticData("ORDER_STATE", String.valueOf(dto.getOrderState())).getCodeName());
            //成本模式
            dto.setPaymentWayName(dto.getPaymentWay() == null ? "" : getSysStaticData("PAYMENT_WAY", String.valueOf(dto.getPaymentWay())).getCodeName());
            //加油費用
            dto.setOilFeeTotal((dto.getPreOilFee() + dto.getPreOilVirtualFee())/100);
            List<OrderCostOtherReport> reportList = orderInfoMapper.getConsumeFee(dto.getOrderId());
            //上报费用合计
            Double consumeFee = 0.0;
            for (OrderCostOtherReport report : reportList) {
                report.setConsumeFee(report.getConsumeFee() == null ? 0 : report.getConsumeFee()/100);
                consumeFee = consumeFee + report.getConsumeFee();
            }
            dto.setConsumeFee(consumeFee);
            //订单成本--直接成本
            if (dto.getPaymentWay() != null) {
                if (dto.getPaymentWay() == 1) {
                    dto.setDriverDaySalary(dto.getDriverDaySalary()==null?0:dto.getDriverDaySalary());
                    dto.setOilFeeTotal(dto.getOilFeeTotal() == null ? 0 : dto.getOilFeeTotal());
                    //智能模式=订单预估成本(补贴+油费+路桥费)
                    dto.setOrderFeeExt((dto.getDriverDaySalary()+dto.getOilFeeTotal()+dto.getPontage()/100));
                    dto.setOrderCost(dto.getOrderFeeExt() == null ? 0 : dto.getOrderFeeExt());
                } else if (dto.getPaymentWay() == 2) {
                    //报账模式(油费+上报费用)
                    dto.setOilFeeTotal(dto.getOilFeeTotal() == null ? 0 : dto.getOilFeeTotal());
                    dto.setConsumeFee(dto.getConsumeFee() == null ? 0 : dto.getConsumeFee());
                    dto.setOrderCost((dto.getOilFeeTotal() + dto.getConsumeFee())/100);
                } else if (dto.getPaymentWay() == 3) {
                    //承包模式=中标价
                    dto.setOrderCost(dto.getTotalFee() == null ? 0 : dto.getTotalFee()/100);
                }
            }else {
                dto.setOrderCost(dto.getOrderFeeExt() == null ? 0 : dto.getOrderFeeExt()/100);
            }
            //货物类型名称
            if (dto.getGoodsType() != null) {
                dto.setGoodsTypeName(getSysStaticData("GOODS_TYPE", dto.getGoodsType() + "").getCodeName());
            }


            //返回资金归属司机的借支类型
            List<String> loanBelongDriverSubjectList = this.getLoanBelongDriverSubjectList();
            List<String> loanBelongAdminSubjectList = this.getLoanBelongAdminSubjectList();


            //司机借支
            Double driverOaLoanAmount = this.getOaLoanAmount(3, dto.getOrderId(), accessToken, loanBelongDriverSubjectList,subOrgList,aBoolean);
            if (driverOaLoanAmount == null) {
                driverOaLoanAmount = 0.0;
            }
            dto.setDriverOaLoanAmount(driverOaLoanAmount/100);
            //员工借支
            Double userOaLoanAmount = this.getOaLoanAmount(2, dto.getOrderId(), accessToken, loanBelongAdminSubjectList,subOrgList,aBoolean);
            if (userOaLoanAmount == null) {
                userOaLoanAmount = 0.0;
            }
            dto.setUserOaLoanAmount(userOaLoanAmount/100);
            //司机报销
//            LambdaQueryWrapper<ClaimExpenseInfo> lambdaQueryWrapper = new LambdaQueryWrapper();
//            lambdaQueryWrapper.eq(ClaimExpenseInfo::getOrderId, dto.getOrderId());
//            lambdaQueryWrapper.eq(ClaimExpenseInfo::getTenantId, loginInfo.getTenantId());
//            List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoMapper.selectList(lambdaQueryWrapper);
//            long amount = 0;
//            for (ClaimExpenseInfo claim : claimExpenseInfos) {
//                amount = amount + claim.getAmount();
//            }
//            dto.setAmount(amount/100);
            Double amount = orderInfoMapper.getOrderReimburse(dto.getOrderId(), loginInfo.getTenantId());
            if (amount==null){
                amount=0.0;
            }
            dto.setAmount(amount);

            //车辆折旧=资产单月折旧/30*(到达时期-靠台时期)
            double depreciationCost = 0;
            DateCostDto assetDetails = orderInfoMapper.getAssetDetails(loginInfo.getTenantId(), dto.getPlateNumber());
            //资产单月折旧：（总价 - 残值）/折旧月数
            long zcdyzj = 0;
            if (assetDetails != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(assetDetails.getPrice()) && org.apache.commons.lang3.StringUtils.isNotBlank(assetDetails.getResidual()) && assetDetails.getDepreciatedMonth() != null) {
                    Long cz = Long.parseLong(assetDetails.getPrice()) - Long.parseLong(assetDetails.getResidual());//差值：（总价 - 残值）
                    if (cz > 0 && assetDetails.getDepreciatedMonth() > 0) {
                        zcdyzj = cz / assetDetails.getDepreciatedMonth();
                    }
                }
            }
            if (dto.getCarArriveDate()!=null && dto.getDependTime()!=null) {
                LocalDate carArriveDate = dto.getCarArriveDate().toLocalDate();
                LocalDate DependDate = dto.getCarArriveDate().toLocalDate();
                if (carArriveDate.isEqual(DependDate)){
                    depreciationCost = zcdyzj / 30 * 1 ;
                }else {
                    Duration duration = Duration.between(dto.getDependTime(), dto.getCarArriveDate());//获取时间间隔
                    long l = duration.toDays();
                    depreciationCost = zcdyzj / 30 * duration.toDays();
                }
            }
            dto.setDepreciationCost(depreciationCost/100);
            //司机工资
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(dto.getOrderId());
            String carDriverPhone = "";
            if (StringUtils.isNotBlank(orderScheduler.getCarDriverPhone())) {
                carDriverPhone = orderScheduler.getCarDriverPhone();
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(dto.getOrderId());
                carDriverPhone = orderSchedulerH.getCarDriverPhone();
            }

            LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.eq(CmSalaryInfoNew::getCarDriverPhone, carDriverPhone);
            Date from = Date.from(dto.getCreateTime().toInstant(ZoneOffset.of("+8")));
            SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM");
            String format = simpleDateFormatDay.format(from);
            queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, format);

            List<CmSalaryInfoNew> cmSalaryInfoNewList = cmSalaryInfoNewService.list(queryWrapper);
            long basicSalaryFee = 0;
            for (CmSalaryInfoNew cm : cmSalaryInfoNewList) {
                cm.setBasicSalaryFee(cm.getBasicSalaryFee()==null?0:cm.getBasicSalaryFee());
                basicSalaryFee = basicSalaryFee + cm.getBasicSalaryFee();
            }
            dto.setBasicSalaryFee(basicSalaryFee/100);

            //变动成本=司机借支+员工借支+司机报销+车辆折旧+司机工资
            double variableCost = (driverOaLoanAmount + userOaLoanAmount + amount + depreciationCost + basicSalaryFee)/100;
            dto.setVariableCost(variableCost);
            //订单毛利=订单收入合计-直接成本-变动成本
            dto.setOrderMargin(dto.getTotalOrderRevenue() - dto.getOrderCost() - variableCost);

        }


        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "靠台时间", "录入时间", "客户全称", "客户单号", "线路编码", "运输线路", "经停信息", "线路名称", "车牌号码", "挂车车牌", "主驾姓名", "副驾姓名", "跟单人员",
                    "录入人员", "归属部门", "货物信息", "货物类型", "订单收入", "异常款", "订单收入合计", "对账调整", "确认应收", "成本模式", "中标价", "加油费用", "路桥费", "上报费用", "直接成本", "订单状态",
                    "司机借支", "员工借支", "司机报销", "司机工资", "车辆折旧", "变动成本", "订单毛利"};
            resourceFild = new String[]{
                    "getOrderId", "getDependTime", "getCreateTime", "getCompanyName", "getCustomNumber", "getSourceCode", "getOrderLine", "getLineNode", "getSourceName", "getPlateNumber", "getTrailerPlate", "getCarDriverMan", "getCopilotMan", "getLocalUserName",
                    "getOpName", "getTenantName", "getGoodsInfo", "getGoodsTypeName", "getCostPrice", "getProblemPrice", "getTotalOrderRevenue", "getConfirmDiffAmount", "getNotarizeReceivable", "getPaymentWayName", "getTotalFee", "getOilFeeTotal", "getPontage", "getConsumeFee", "getOrderCost", "getOrderStateName",
                    "getDriverOaLoanAmount", "getUserOaLoanAmount", "getAmount", "getBasicSalaryFee", "getDepreciationCost", "getVariableCost", "getOrderMargin"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(vehicleOrderDto, showName, resourceFild, VehicleOrderDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "ownVehicle.xlsx", inputStream.available());
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
     * 获取借支金额
     *
     * @param queryType
     * @param orderId
     * @return
     */
    private Double getOaLoanAmount(Integer queryType, Long orderId, String accessToken, List<String> subjects ,List<Long> subOrgList,Boolean aBoolean) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Double oaLoanAmount = orderInfoMapper.getOaLoanAmount(queryType, orderId, loginInfo.getTenantId(),subjects,subOrgList,aBoolean);
        return oaLoanAmount;
    }


    @Override
    public Page<LineInfoDto> lineStatements(String sourceName, String beginTime, String endTime, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
        LocalDate begins = null;
        LocalDate ends = null;
        if (StringUtils.isEmpty(beginTime) && StringUtils.isEmpty(endTime)) {
            //获取上一个月
            LocalDate time = LocalDate.now().minusMonths(1);
            LocalDate beginDate = time.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endDate = beginDate.plusMonths(1);
            beginTime = String.valueOf(beginDate);
            endTime = String.valueOf(endDate);
            begins = LocalDate.parse(beginTime);
            ends = LocalDate.parse(endTime);
        } else {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (StringUtils.isNotEmpty(beginTime) && StringUtils.isEmpty(endTime)) {
                beginTime = beginTime + "-01";
                begins = LocalDate.parse(beginTime, fmt);
            }
            if (StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(beginTime)) {
                endTime = endTime + "-01";
                ends = LocalDate.parse(endTime, fmt);
                endTime = String.valueOf(ends.plusMonths(1));
                ends = LocalDate.parse(endTime, fmt);
            }
            if (StringUtils.isNotEmpty(endTime) && StringUtils.isNotEmpty(beginTime)) {
                beginTime = beginTime + "-01";
                begins = LocalDate.parse(beginTime, fmt);
                endTime = endTime + "-01";
                ends = LocalDate.parse(endTime, fmt);
                endTime = String.valueOf(ends.plusMonths(1));
                ends = LocalDate.parse(endTime, fmt);
//                begins = LocalDate.parse(beginTime, fmt);
//                ends = LocalDate.parse(endTime, fmt);
            }
        }
        Page<LineInfoDto> lineInfoDtoPage = orderInfoMapper.lineStatements(new Page<>(pageNum, pageSize), sourceName, begins, ends, loginInfo.getTenantId());
        List<LineInfoDto> list = lineInfoDtoPage.getRecords();
        List<LineInfoDto> lineInfoDtoList = new ArrayList<>();
        int num = 0;
        for (LineInfoDto dto : list) {
            if (StringUtils.isNotBlank(dto.getSourceName())) {
                num++;
                //收入统计
                dto.setNotarizeReceivable(dto.getNotarizeReceivable() == null ? 0 : dto.getNotarizeReceivable());
                dto.setMileageNumber(dto.getMileageNumber() == null ? 0 : dto.getMileageNumber());
                dto.setPreOilFee(dto.getPreOilFee() == null ? 0 : dto.getPreOilFee());
                dto.setPreOilVirtualFee(dto.getPreOilVirtualFee() == null ? 0 : dto.getPreOilVirtualFee());
                //油费
                dto.setConsumeOilFee(dto.getConsumeOilFee()==null?0:dto.getConsumeOilFee());
                dto.setOilTotal(dto.getPreOilFee() + dto.getPreOilVirtualFee()+dto.getConsumeOilFee());
                //路桥费
                dto.setConsumePontageFee(dto.getConsumePontageFee()==null?0:dto.getConsumePontageFee());
//                if (dto.getPaymentWay() != null) {
//                    if (dto.getPaymentWay() == 1) {
//                        //智能模式=路桥费
//                        dto.setPontage(dto.getPontage() == null ? 0 : dto.getPontage());
//                    } else if (dto.getPaymentWay() == 3) {
//                        //承包模式=etc
//                        dto.setPreEtcFee(dto.getPreEtcFee() == null ? 0 : dto.getPreEtcFee());
//                        dto.setPontage(dto.getPreEtcFee()+dto.getConsumePontageFee());
//                    }
//                }
                dto.setPontage(dto.getPontage() == null ? 0 : dto.getPontage());
                dto.setPontage(dto.getPontage() == null ? 0 : dto.getPontage()+dto.getConsumePontageFee());
                //其他费用
                dto.setConsumeFee(dto.getConsumeFee()==null?0:dto.getConsumeFee());
//                //成本统计（油费+路桥费+其他费用）
//                dto.setCostStatistics(dto.getOilTotal() + dto.getPontage() + dto.getOtherFee());

                //线路月份
                if (begins != null && ends != null && begins.getMonth() != ends.minusMonths(1).getMonth()) {
                    dto.setCreateTime("-");
                }

                //返回资金归属司机的借支类型
                List<String> loanBelongDriverSubjectList = this.getLoanBelongDriverSubjectList();
                List<String> loanBelongAdminSubjectList = this.getLoanBelongAdminSubjectList();

                //司机借支
                Double driverOaLoanAmount = orderInfoMapper.getOaLoanAmountLine(3, dto.getSourceName(), loginInfo.getTenantId(), loanBelongDriverSubjectList,subOrgList,aBoolean);
                dto.setDriverOaLoanAmount(driverOaLoanAmount == null ? 0 : driverOaLoanAmount);
                //员工借支
                Double userOaLoanAmount = orderInfoMapper.getOaLoanAmountLine(2, dto.getSourceName(), loginInfo.getTenantId(), loanBelongAdminSubjectList,subOrgList,aBoolean);
                dto.setUserOaLoanAmount(userOaLoanAmount == null ? 0 : userOaLoanAmount);
                //司机报销
//                Double lineReimburse = orderInfoMapper.getLineReimburse(dto.getSourceName(), loginInfo.getTenantId());
                dto.setAmount(dto.getAmount() == null ? 0 : dto.getAmount());
                //司机工资
//                LambdaQueryWrapper<OrderSchedulerH> queryWrapper=new LambdaQueryWrapper<>();
//                queryWrapper.eq(OrderSchedulerH::getSourceName,dto.getSourceName());
//                List<OrderSchedulerH> orderSchedulerHList = orderSchedulerHService.list(queryWrapper);
                List<String> carDriverPhoneList = orderInfoMapper.getCarDriverPhone(dto.getSourceName());
                long basicSalaryFee = 0;
                for (String orderh : carDriverPhoneList) {
                    QueryWrapper<CmSalaryInfoNew> qw = new QueryWrapper<>();
                    qw.lambda().eq(CmSalaryInfoNew::getCarDriverPhone, orderh);
                    if (begins != null && ends != null && begins.getMonth() != ends.minusMonths(1).getMonth()) {
                        qw.lambda().ge(CmSalaryInfoNew::getSettleMonth, begins);
                        qw.lambda().lt(CmSalaryInfoNew::getSettleMonth, ends);
                    }else{
                        qw.lambda().eq(CmSalaryInfoNew::getSettleMonth,dto.getCreateTime());
                    }
                    List<CmSalaryInfoNew> cmSalaryInfoNewList = cmSalaryInfoNewService.list(qw);
                    long driveSalary = 0;
                    for (CmSalaryInfoNew cm : cmSalaryInfoNewList) {
                        driveSalary = driveSalary + cm.getBasicSalaryFee();
                    }
                    basicSalaryFee = basicSalaryFee + driveSalary;
                }
                dto.setBasicSalaryFee(basicSalaryFee);
                //成本统计（油费+路桥费+其他费用+司机借支+员工借支+司机报销+司机工资）
                dto.setCostStatistics(dto.getOilTotal() + dto.getPontage() + dto.getConsumeFee() + dto.getDriverOaLoanAmount() + dto.getUserOaLoanAmount() + dto.getAmount() + dto.getBasicSalaryFee());
                lineInfoDtoList.add(dto);
                //线路毛利（收入统计-成本统计）
                dto.setMargin(dto.getNotarizeReceivable() - dto.getCostStatistics());
                //线路毛利率（线路毛利/收入统计）
                java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
                double grossMargin = dto.getMargin() / dto.getNotarizeReceivable() * 100;
                dto.setGrossMargin(df.format(grossMargin) + "%");
            }
        }
        lineInfoDtoPage.setRecords(lineInfoDtoList);
        lineInfoDtoPage.setTotal(num);
        return lineInfoDtoPage;
    }

    @Override
    @Async
    public void lineStatementsExport(String sourceName, String beginTime, String endTime, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<Long> subOrgList = sysOrganizeService.getSubOrgList(accessToken);
        Boolean aBoolean = iSysRoleService.hasAllData(loginInfo);
        LocalDate begins = null;
        LocalDate ends = null;
        if (StringUtils.isEmpty(beginTime) && StringUtils.isEmpty(endTime)) {
            //获取上一个月
            LocalDate time = LocalDate.now().minusMonths(1);
            LocalDate beginDate = time.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endDate = beginDate.plusMonths(1);
            beginTime = String.valueOf(beginDate);
            endTime = String.valueOf(endDate);
            begins = LocalDate.parse(beginTime);
            ends = LocalDate.parse(endTime);
        } else {

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (StringUtils.isNotEmpty(beginTime) && StringUtils.isEmpty(endTime)) {
                beginTime = beginTime + "-01";
                begins = LocalDate.parse(beginTime, fmt);
            }
            if (StringUtils.isNotEmpty(endTime) && StringUtils.isEmpty(beginTime)) {
                endTime = endTime + "-01";
                ends = LocalDate.parse(endTime, fmt);
                endTime = String.valueOf(ends.plusMonths(1));
                ends = LocalDate.parse(endTime, fmt);
            }
            if (StringUtils.isNotEmpty(endTime) && StringUtils.isNotEmpty(beginTime)) {
                beginTime = beginTime + "-01";
                begins = LocalDate.parse(beginTime, fmt);
                endTime = endTime + "-01";
                ends = LocalDate.parse(endTime, fmt);
                endTime = String.valueOf(ends.plusMonths(1));
                ends = LocalDate.parse(endTime, fmt);
//                begins = LocalDate.parse(beginTime, fmt);
//                ends = LocalDate.parse(endTime, fmt);
            }
        }
        List<LineInfoDto> list = orderInfoMapper.lineStatementsExport(sourceName, begins, ends, loginInfo.getTenantId());
        int num = 0;
        for (LineInfoDto dto : list) {
            if (StringUtils.isNotBlank(dto.getSourceName())) {
                num++;
                //收入统计
                dto.setNotarizeReceivable(dto.getNotarizeReceivable() == null ? 0 : dto.getNotarizeReceivable()/100);
                dto.setMileageNumber(dto.getMileageNumber() == null ? 0 : dto.getMileageNumber()/1000);
                dto.setPreOilFee(dto.getPreOilFee() == null ? 0 : dto.getPreOilFee());
                dto.setPreOilVirtualFee(dto.getPreOilVirtualFee() == null ? 0 : dto.getPreOilVirtualFee());
                //油费
                dto.setConsumeOilFee(dto.getConsumeOilFee()==null?0:dto.getConsumeOilFee());
                Double oilTotal = dto.getPreOilFee() + dto.getPreOilVirtualFee()+dto.getConsumeOilFee();
                dto.setOilTotal(oilTotal/100);
                //路桥费
                dto.setConsumePontageFee(dto.getConsumePontageFee()==null?0:dto.getConsumePontageFee());
//                if (dto.getPaymentWay() != null) {
//                    if (dto.getPaymentWay() == 1) {
//                        //智能模式=路桥费
//                        dto.setPontage(dto.getPontage() == null ? 0 : dto.getPontage());
//                    } else if (dto.getPaymentWay() == 3) {
//                        //承包模式=etc
//                        dto.setPreEtcFee(dto.getPreEtcFee() == null ? 0 : dto.getPreEtcFee());
//                        dto.setPontage(dto.getPreEtcFee()+dto.getConsumePontageFee());
//                    }
//                }
                dto.setPontage(dto.getPontage() == null ? 0 : dto.getPontage());
                Double pontage = dto.getPontage()+dto.getConsumePontageFee();
                dto.setPontage(pontage/100);
                //其他费用
                Double consumeFee = dto.getConsumeFee()==null?0:dto.getConsumeFee();
                dto.setConsumeFee(consumeFee/100);
//                //成本统计（油费+路桥费+其他费用）
//                dto.setCostStatistics(dto.getOilTotal() + dto.getPontage() + dto.getOtherFee());

                //线路月份
                if (begins != null && ends != null && begins.getMonth() != ends.minusMonths(1).getMonth()) {
                    dto.setCreateTime("-");
                }

                //返回资金归属司机的借支类型
                List<String> loanBelongDriverSubjectList = this.getLoanBelongDriverSubjectList();
                List<String> loanBelongAdminSubjectList = this.getLoanBelongAdminSubjectList();

                //司机借支
                Double driverOaLoanAmount = orderInfoMapper.getOaLoanAmountLine(3, dto.getSourceName(), loginInfo.getTenantId(), loanBelongDriverSubjectList,subOrgList,aBoolean);
                driverOaLoanAmount=driverOaLoanAmount == null ? 0 : driverOaLoanAmount;
                dto.setDriverOaLoanAmount(driverOaLoanAmount/100);
                //员工借支
                Double userOaLoanAmount = orderInfoMapper.getOaLoanAmountLine(2, dto.getSourceName(), loginInfo.getTenantId(), loanBelongAdminSubjectList,subOrgList,aBoolean);
                userOaLoanAmount=userOaLoanAmount == null ? 0 : userOaLoanAmount;
                dto.setUserOaLoanAmount(userOaLoanAmount/100);
                //司机报销
//                Double lineReimburse = orderInfoMapper.getLineReimburse(dto.getSourceName(), loginInfo.getTenantId());
                Double amount = dto.getAmount()==null? 0:dto.getAmount();
                dto.setAmount(dto.getAmount() == null ? 0 : dto.getAmount()/100);
                //司机工资
//                LambdaQueryWrapper<OrderSchedulerH> queryWrapper=new LambdaQueryWrapper<>();
//                queryWrapper.eq(OrderSchedulerH::getSourceName,dto.getSourceName());
//                List<OrderSchedulerH> orderSchedulerHList = orderSchedulerHService.list(queryWrapper);
                List<String> carDriverPhoneList = orderInfoMapper.getCarDriverPhone(dto.getSourceName());
                long basicSalaryFee = 0;
                for (String orderh : carDriverPhoneList) {
                    QueryWrapper<CmSalaryInfoNew> qw = new QueryWrapper<>();
                    qw.lambda().eq(CmSalaryInfoNew::getCarDriverPhone, orderh);
                    if (begins != null && ends != null && begins.getMonth() != ends.minusMonths(1).getMonth()) {
                        qw.lambda().ge(CmSalaryInfoNew::getSettleMonth, begins);
                        qw.lambda().lt(CmSalaryInfoNew::getSettleMonth, ends);
                    }else{
                        qw.lambda().eq(CmSalaryInfoNew::getSettleMonth,dto.getCreateTime());
                    }
                    List<CmSalaryInfoNew> cmSalaryInfoNewList = cmSalaryInfoNewService.list(qw);
                    long driveSalary = 0;
                    for (CmSalaryInfoNew cm : cmSalaryInfoNewList) {
                        driveSalary = driveSalary + cm.getBasicSalaryFee();
                    }
                    basicSalaryFee = basicSalaryFee + driveSalary;
                }
                dto.setBasicSalaryFee(basicSalaryFee/100);
                //成本统计（油费+路桥费+其他费用+司机借支+员工借支+司机报销+司机工资）
                dto.setCostStatistics((oilTotal + pontage + consumeFee + driverOaLoanAmount + userOaLoanAmount + amount + basicSalaryFee)/100);
                //线路毛利（收入统计-成本统计）
                dto.setMargin(dto.getNotarizeReceivable() - dto.getCostStatistics());
                //线路毛利率（线路毛利/收入统计）
                java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
                double grossMargin = dto.getMargin() / dto.getNotarizeReceivable() * 100;
                dto.setGrossMargin(df.format(grossMargin) + "%");
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "线路名称", "线路毛利", "线路毛利率", "货物重量", "货物体积", "收入统计", "公里数", "油费", "路桥费", "其他费用", "成本统计", "线路月份",
                    "司机借支", "员工借支", "司机报销", "司机工资"};
            resourceFild = new String[]{
                    "getSourceName", "getMargin", "getGrossMargin", "getWeight", "getSquare", "getNotarizeReceivable", "getMileageNumber", "getOilTotal",
                    "getPontage", "getConsumeFee", "getCostStatistics", "getCreateTime","getDriverOaLoanAmount", "getUserOaLoanAmount", "getAmount", "getBasicSalaryFee"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, LineInfoDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "ownVehicle.xlsx", inputStream.available());
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
    public List<WorkbenchDto> getTableFinancialReceivableReceivedAmount() {
        return baseMapper.getTableFinancialReceivableReceivedAmount();
    }

    @Override
    public List<WorkbenchDto> getTableFinancialReceivableSurplusAmount() {
        return baseMapper.getTableFinancialReceivableSurplusAmount();
    }

    @Override
    public Integer queryOrderNumberByState(Integer selectType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        return baseMapper.queryOrderNumberByState(selectType, loginInfo.getTenantId());
    }

    @Override
    public Long getVehicleAffirmIncomeFeeByMonth(String plateNumber, Long tenantId, String beginDependTime, String endDependTime) {
        OrderInfoVo orderInfoVo = new OrderInfoVo();
        if (StringUtils.isNotEmpty(beginDependTime)) {
            orderInfoVo.setBeginDependTime(beginDependTime + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(endDependTime)) {
            orderInfoVo.setEndDependTime(endDependTime + " 23:59:59");
        }
        orderInfoVo.setTenantId(tenantId);
        orderInfoVo.setPlateNumber(plateNumber);

        PageHelper.startPage(1, 9999);
        List<OrderInfoDto> list = orderInfoMapper.queryReceviceManageOrderExport(orderInfoVo);
        PageInfo<OrderInfoDto> infoDtoPage = new PageInfo<OrderInfoDto>(list);

        List<OrderInfoDto> listOuts = new ArrayList<OrderInfoDto>();
        StringBuffer orderIds = new StringBuffer();

        for (OrderInfoDto out : infoDtoPage.getList()) {
            if (out.getFromOrderId() != null && out.getFromOrderId() > 0) {
                List<OrderAgingInfo> orderAgingInfos = orderAgingInfoService.queryAgingInfoByOrderId(out.getFromOrderId());
                if (orderAgingInfos != null && orderAgingInfos.size() > 0) {
                    Long finePrice = 0L;
                    for (OrderAgingInfo orderAgingInfo : orderAgingInfos) {
                        if (orderAgingInfo != null && orderAgingInfo.getAuditSts() != null
                                && orderAgingInfo.getAuditSts() == SysStaticDataEnum.EXPENSE_STATE.END) {
                            finePrice += (orderAgingInfo.getFinePrice() == null ? 0 : orderAgingInfo.getFinePrice());
                        }
                    }
                    out.setIncomeExceptionFee((out.getIncomeExceptionFee() == null ? 0 : out.getIncomeExceptionFee())
                            + finePrice);
                }
            }

            listOuts.add(out);
        }

        //查询客户回单号和对账金额
        if (orderIds.length() > 0) {
            String subs = orderIds.substring(0, orderIds.length() - 1);
            List<Map<String, Object>> diffFeeMap = orderDiffInfoMapper.getDiffFeeByOrderIds(subs);
            if (diffFeeMap != null) {
                for (Map<String, Object> map : diffFeeMap) {
                    for (OrderInfoDto out : listOuts) {
                        if (out.getOrderId().equals(DataFormat.getLongKey(map, "orderId"))) {
                            out.setDiffFee(DataFormat.getLongKey(map, "diffFee"));
                            break;
                        }
                    }
                }
            }
        }

        long sum = listOuts.stream().mapToLong(OrderInfoDto::getAffirmIncomeFee).sum();

        return sum;
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
     * @param nowDate   要比较的时间
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return true在时间段内，false不在时间段内
     */
    public static boolean hourMinuteBetween(String nowDate, String startDate, String endDate) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date now = null;
        Date start = null;
        Date end = null;
        try {
            now = format.parse(nowDate);
            if (startDate != null && startDate != "") {
                start = format.parse(startDate);
            }
            if (endDate != null && endDate != "") {
                end = format.parse(endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long nowTime = now.getTime();
        long startTime = 0;
        if (start != null) {
            startTime = start.getTime();
        }
        long endTime = 0;
        if (end != null) {
            endTime = end.getTime();
        }

        if (startTime != 0 && endTime == 0) {
            return nowTime >= startTime;
        } else if (startTime == 0 && endTime != 0) {
            return nowTime <= endTime;
        }

        return nowTime >= startTime && nowTime <= endTime;
    }

    private PageInfo<OrderInfoDto> listToPageInfo(List<OrderInfoDto> list, Integer pageNum, Integer pageSize) {
        int i = list.size() % pageSize != 0 ? list.size() / pageSize + 1 : list.size() / pageSize;
        PageInfo<OrderInfoDto> pageInfo = new PageInfo<>();
        pageInfo.setPageNum(pageNum);
        pageInfo.setPageSize(pageSize);
        pageInfo.setTotal(list.size());
        if (pageNum > i) {
            pageInfo.setSize(0);
            pageInfo.setList(new ArrayList<OrderInfoDto>());
            return pageInfo;
        }
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = pageNum * pageSize;
        endIndex = endIndex > list.size() ? list.size() : endIndex;
        List<OrderInfoDto> result = list.subList(startIndex, endIndex);
        pageInfo.setList(result);
        pageInfo.setSize(result.size());

        return pageInfo;
    }


    /**
     * 返回资金归属司机的借支类型
     *
     * @return
     */
    public List<String> getLoanBelongDriverSubjectList() {
        List<String> loanSubjects = Lists.newArrayList();
        List<SysStaticData> dataList = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));
//        List<SysStaticData> dataList = SysStaticDataUtil.getSysStaticDataList("LOAN_SUBJECT_APP");//司机借支
        for (SysStaticData data : dataList) {
            loanSubjects.add(data.getCodeValue());
        }
        return loanSubjects;
    }


    /**
     * 返回司机申请但资金归属车管的借支类型
     *
     * @return
     */
    public List<String> getLoanBelongAdminSubjectList() {
        List<String> result = new ArrayList();
        Set<String> extsubject = new HashSet();
        List<SysStaticData> ext = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT_APP"));
        List<SysStaticData> total = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("LOAN_SUBJECT"));
        for (SysStaticData data : ext) {
            extsubject.add(data.getCodeValue());
        }

        for (SysStaticData data : total) {
            if (!extsubject.contains(data.getCodeValue())) {
                result.add(data.getCodeValue());
            }
        }
        return result;
    }

}
