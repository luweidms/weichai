package com.youming.youche.finance.provider.service.order;

import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.order.IOrderBillCheckInfoService;
import com.youming.youche.finance.api.order.IOrderBillInfoService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.domain.order.OrderBillInfo;
import com.youming.youche.finance.domain.order.OrderBillInvoice;
import com.youming.youche.finance.domain.order.OrderDiffInfo;
import com.youming.youche.finance.domain.order.OrderFeeStatement;
import com.youming.youche.finance.domain.order.OrderFeeStatementH;
import com.youming.youche.finance.dto.order.ExportQueryBillInfoDto;
import com.youming.youche.finance.dto.order.ExportQueryOrderInfoDto;
import com.youming.youche.finance.dto.order.OrderBillCheckInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInvoiceDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.provider.mapper.order.OrderBillCheckInfoHMapper;
import com.youming.youche.finance.provider.mapper.order.OrderBillCheckInfoMapper;
import com.youming.youche.finance.provider.mapper.order.OrderBillInfoMapper;
import com.youming.youche.finance.provider.mapper.order.OrderBillInvoiceMapper;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementHMapper;
import com.youming.youche.finance.provider.mapper.order.OrderFeeStatementMapper;
import com.youming.youche.finance.provider.mapper.order.OrderProblemInfoMapper;
import com.youming.youche.finance.vo.order.IncomeOrderImportVo;
import com.youming.youche.finance.vo.order.OrderBillInfoVo;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.OrederExportBillVo;
import com.youming.youche.order.api.order.IOrderOpRecordService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExcelParse;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ????????????
 *
 * @author hzx
 * @date 2022/2/8 16:00
 */
@DubboService(version = "1.0.0")
public class OrderBillInfoServiceImpl extends BaseServiceImpl<OrderBillInfoMapper, OrderBillInfo> implements IOrderBillInfoService {

    @Resource
    OrderBillInfoMapper orderBillInfoMapper;

    @Resource
    OrderBillCheckInfoMapper orderBillCheckInfoMapper; // ????????????

    @Resource
    OrderBillCheckInfoHMapper orderBillCheckInfoHMapper; // ???????????? ?????????

    @Resource
    OrderFeeStatementMapper orderFeeStatementMapper;

    @Resource
    OrderFeeStatementHMapper orderFeeStatementHMapper;

    @Resource
    IOrderInfoThreeService iOrderInfoService; // ??????

    @DubboReference(version = "1.0.0")
    IOrderOpRecordService iOrderOpRecordService;

    @DubboReference(version = "1.0.0")
    IOverdueReceivableService overdueReceivableService;

    @Resource
    OrderBillInvoiceMapper orderBillInvoiceMapper; // ??????

    @Resource
    OrderProblemInfoMapper orderProblemInfoMapper;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService; // ??????

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysAttachService iSysAttachService;

    @Resource
    IOrderBillCheckInfoService iOrderBillCheckInfoService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOrganizeService iSysOrganizeService;

    @Override
    public Page<OrderBillInfoDto> doQuery(Page<OrderBillInfoDto> objectPage, OrderBillInfoVo orderBillInfoVO, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OrderBillInfoDto> infoDtoPage = orderBillInfoMapper.doQuery(objectPage, orderBillInfoVO, loginInfo.getTenantId());
        if (infoDtoPage.getTotal() != 0) {
            for (OrderBillInfoDto record : infoDtoPage.getRecords()) {
                record.setBillStsName(getSysStaticData("BILL_STS", String.valueOf(record.getBillSts())).getCodeName());
                record.setOrgIdName(iSysOrganizeService.getOrgNameByOrgId(record.getOrgId(), loginInfo.getTenantId()));
                record.setRootOrgIdName(iSysOrganizeService.getOrgNameByOrgId(record.getRootOrgId(), loginInfo.getTenantId()));
            }
        }
        return infoDtoPage;
    }

    @Override
    public List<OrderBillCheckInfoDto> queryChecksByBillNumber(String billNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderBillCheckInfo> lists =
                orderBillCheckInfoMapper.queryAllRecordByBillNumberAndTenantId(billNumber, loginInfo.getTenantId());

        List<Long> flowIds = new ArrayList<Long>();
        for (OrderBillCheckInfo o : lists) {
            if (StringUtils.isNotEmpty(o.getFileIds())) {
                String[] ss = o.getFileIds().split(",");
                for (String s : ss) {
                    if (CommonUtil.isLong(s)) {
                        flowIds.add(Long.valueOf(s));
                    }
                }
            }
        }

        List<SysAttach> sysAttaches = null;
        // ??????????????????
        if (flowIds.size() > 0) {
            sysAttaches = iSysAttachService.selectAllInfoByIds(flowIds);
        }

        Map<String, SysAttach> mQuery = new HashMap<String, SysAttach>();
        if (sysAttaches != null) {
            for (SysAttach sa : sysAttaches) {
                mQuery.put(sa.getId() + "", sa);
            }
        }

        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<OrderBillCheckInfoDto> listOut = new ArrayList<OrderBillCheckInfoDto>();
        for (OrderBillCheckInfo o : lists) {
            OrderBillCheckInfoDto bean = new OrderBillCheckInfoDto();
            String fileIds = o.getFileIds();
            bean.setCheckId(o.getId());
            bean.setCheckFee(o.getCheckFee());
            bean.setCheckDesc(o.getCheckDesc());
            bean.setFileIds(fileIds);
            bean.setFileUrls(o.getFileUrls());
            bean.setCheckType(o.getCheckType());
            bean.setCheckFeeDouble(CommonUtil.getDoubleFormatLongMoney(o.getCheckFee(), 2));
            bean.setCheckTypeName(getSysStaticData("CHECK_TYPE", o.getCheckType() + "").getCodeName());

            //??????URL??? ??????
            String fileNames = "";
            String filefullUrls = "";
            String fileMinfullUrls = "";
            if (StringUtils.isNotEmpty(fileIds)) {
                String[] ss = fileIds.split(",");
                for (String s : ss) {
                    SysAttach attach = mQuery.get(s);
                    if (attach != null) {
                        if (StringUtils.isEmpty(fileNames)) {
                            fileNames = attach.getFileName() + "";
                        } else {
                            fileNames += "," + attach.getFileName() + "";
                        }
                        String[] as = (attach.getStorePath() + "").split("\\.");
                        String url = null;//?????????
                        String bigUrl = null; //?????????
                        try {
                            url = client.getHttpURL(attach.getStorePath() + "", attach.getFileName() + "");
                            bigUrl = client.getHttpURL(as[0] + "_big." + as[1], attach.getFileName() + "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (StringUtils.isNotEmpty(url)) {
                            if (StringUtils.isEmpty(filefullUrls)) {
                                filefullUrls = bigUrl;
                                fileMinfullUrls = url;
                            } else {
                                filefullUrls += "," + bigUrl;
                                fileMinfullUrls += url;
                            }
                        }
                    }
                }
            }

            bean.setFlowNames(fileNames);
            bean.setFullUrls(filefullUrls);
            bean.setFileMinfullUrls(fileMinfullUrls);
            listOut.add(bean);
        }

        return listOut;
    }

    @Override
    public void saveChecks(String billNumber, List<OrderBillCheckInfo> orderBillCheckInfos, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        OrderBillInfo orderBillInfo = orderBillInfoMapper.getOrderBillInfo(billNumber);
        if (orderBillInfo == null) {
            throw new BusinessException("???????????????[" + billNumber + "]??????");
        }
        if (orderBillInfo.getBillSts() == SysStaticDataEnum.BILL_STS.CHECK_ALL) {
            throw new BusinessException("??????[" + billNumber + "]???????????????????????????????????????");
        }

        long badDebt = 0;//????????????
        long checkAmount = 0; //?????????????????????
        // ?????????????????????????????????
        for (OrderBillCheckInfo c : orderBillCheckInfos) {
            checkAmount += c.getCheckFee();
            if (c.getCheckType() == 6) {
                badDebt += c.getCheckFee();
            }
        }

        long oldCheckAmount = orderBillInfo.getCheckAmount(); // ????????????
        OrderBillInfoVo orderBillInfoVO = new OrderBillInfoVo();
        orderBillInfoVO.setBillNumber(billNumber);
        orderBillInfoVO.setAllBill(true);
        orderBillInfoVO.setBillSts("");
        Page<OrderBillInfoDto> orderBillInfoDtoPage = doQuery(new Page<>(), orderBillInfoVO, accessToken);
        List<OrderBillInfoDto> out = orderBillInfoDtoPage.getRecords();

        //????????????????????? ????????????????????????
        long orderConfirmAmountSum = (out.get(0).getCostPrice() + out.get(0).getConfirmDiffAmount() + out.get(0).getIncomeExceptionFee());//????????????
        if (checkAmount > orderConfirmAmountSum) {
            throw new BusinessException("??????[" + billNumber + "]???????????????????????????????????????????????????");
        }

        //??????????????????(???????????????????????? ???????????????????????????????????? ?????????)
        int h = orderBillCheckInfoHMapper.insertCheckInfoH(billNumber, loginInfo.getTenantId(), loginInfo.getUserInfoId());
        if (h > 0) {
            //??????????????????
            orderBillCheckInfoMapper.deleteCheckInfoByBillNumberAndTenantId(billNumber, loginInfo.getTenantId());
        }

        Date createDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // ??????????????????
        for (OrderBillCheckInfo c : orderBillCheckInfos) {
            c.setCreateTime(LocalDateTime.now());
            c.setCreatorId(loginInfo.getUserInfoId());
            c.setOperId(loginInfo.getUserInfoId());
            c.setOperDate(sdf.format(createDate));
            c.setTenantId(loginInfo.getTenantId());
            orderBillCheckInfoMapper.insertChechInfo(c);
        }

        int billSts = SysStaticDataEnum.BILL_STS.CHECK_ALL;
        String operName = "";
        if (checkAmount < orderConfirmAmountSum) {
            billSts = SysStaticDataEnum.BILL_STS.CHECK_PART;
            if (oldCheckAmount <= 0) {
                operName = "[" + loginInfo.getName() + "]??????[" + CommonUtil.getDoubleFormatLongMoney(checkAmount, 2) + "]???";
            } else {
                operName = "[" + loginInfo.getName() + "]???????????????"
                        + "[" + CommonUtil.getDoubleFormatLongMoney(oldCheckAmount, 2) + "]??????["
                        + +CommonUtil.getDoubleFormatLongMoney(checkAmount, 2) + "]???";
            }
            orderBillInfo.setRealIncome(0L);
        } else {
            orderBillInfo.setRealIncome(checkAmount - badDebt);
            operName = "[" + loginInfo.getName() + "]??????[" + CommonUtil.getDoubleFormatLongMoney(checkAmount, 2) + "]???";
        }

        //??????????????????L
        orderBillInfo.setCheckAmount(checkAmount);
        orderBillInfo.setOperDate(sdf.format(createDate));
        orderBillInfo.setOperId(loginInfo.getUserInfoId());
        orderBillInfo.setCheckBillDate(new Date());
        orderBillInfo.setCheckBillName(loginInfo.getName());
        orderBillInfo.setBillSts(billSts);
        orderBillInfoMapper.updateRecordByBillNumber(orderBillInfo);
        // ???????????????????????????
        apportionCheckAmountToOrder(billNumber, checkAmount, billSts, badDebt, accessToken);
        // ???????????????????????????
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, operName, accessToken, getLongBillNumber(billNumber));
    }

    @Override
    public List<OrderBillInvoiceDto> queryBillReceipt(String billNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OrderBillInvoiceDto> orderBillInvoiceDtos = orderBillInvoiceMapper.queryBillReceipt(billNumber, loginInfo.getTenantId());
        return orderBillInvoiceDtos;
    }

    @Override
    public void saveBillSts(String billNumberStr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        String[] bills = billNumberStr.split(",");
        StringBuffer billNumbers = new StringBuffer();
        for (String b : bills) {
            billNumbers.append("'").append(b).append("',");
        }
        String billNumbersSub = billNumbers.substring(0, billNumbers.length() - 1);
        //???????????? ???????????????
        orderBillInfoMapper.updateRecordByBillNumbers(billNumbersSub,
                SysStaticDataEnum.BILL_STS.CHECK_ALL,
                SysStaticDataEnum.BILL_STS.CHECK_PART,
                SysStaticDataEnum.BILL_STS.MAKE_RECEPIT,
                loginInfo.getId(), loginInfo.getName());

        //????????????
        int resultNumM = orderFeeStatementMapper.updateFeeByFields(billNumbersSub);
        //???????????????
        int resultNumH = orderFeeStatementHMapper.updateFeeHByFields(billNumbersSub);
        if (resultNumM + resultNumH <= 0) {
            throw new BusinessException("???????????????????????????");
        }
        List<Long> orderIds = orderFeeStatementMapper.getOrderIdsByBill(billNumbersSub);
        if (orderIds != null && orderIds.size() > 0) {
            for (Long orderId : orderIds) {
                iOrderOpRecordService.saveOrUpdate(Long.valueOf(orderId + ""), 212, accessToken); // ??????
            }
        }

        // ???????????????????????????
        for (String billNumber : bills) {
            saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, "????????????", accessToken, getLongBillNumber(billNumber));
        }

    }

    @Override
    public void saveOrderBillReceipt(String billNumber, String invoiceList, String amountList, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        List<OrderBillInvoice> orderBillInvoiceArrayList = new ArrayList<>();
        if (StringUtils.isNotBlank(invoiceList)) {
            String[] invoiceArr = invoiceList.split(",");
            // ???????????????invoiceList?????????????????????????????????amountList????????????invoiceList
            String[] amountArr = amountList.split(",");
            String[] amountArrNew = Arrays.copyOf(amountArr, invoiceArr.length);
            for (int i = 0; i < invoiceArr.length; i++) {
                OrderBillInvoice orderBillInvoice = new OrderBillInvoice();
                if (StringUtils.isNotBlank(amountArrNew[i])) {
                    orderBillInvoice.setAmount(Math.round(Double.parseDouble(amountArrNew[i]) * 100));
                } else {
                    orderBillInvoice.setAmount(0L);
                }
                orderBillInvoice.setReceiptNumber(invoiceArr[i]);
                orderBillInvoice.setBillNumber(billNumber);
                orderBillInvoiceArrayList.add(orderBillInvoice);
            }
        }

        String receiptNumber = "";

        OrderBillInfo orderBillInfo = orderBillInfoMapper.getOrderBillInfo(billNumber);
        if (orderBillInfo == null) {
            throw new BusinessException("?????????[" + billNumber + "]??????????????????");
        }

        //1?????????????????????????????????????????????????????????????????????????????????
        orderBillInvoiceMapper.deleteInvoiceRecordByBillNumber(billNumber, loginInfo.getTenantId());
        //2?????????????????????
        if (orderBillInvoiceArrayList != null && orderBillInvoiceArrayList.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (OrderBillInvoice invoice : orderBillInvoiceArrayList) {
                invoice.setBillNumber(billNumber);
                invoice.setCreateReceiptDate(sdf.format(new Date()));
                invoice.setOperDate(sdf.format(new Date()));
                invoice.setCreateReceiptName(loginInfo.getName());
                invoice.setTenantId(loginInfo.getTenantId());
                invoice.setOperId(loginInfo.getId());
                try {
                    // ??????
                    orderBillInvoiceMapper.insertInvoiceRecord(invoice);
                } catch (Exception exception) {
                    throw new BusinessException("??????[" + invoice.getReceiptNumber() + "]????????????????????????????????????????????????!");
                }

                receiptNumber += (invoice.getReceiptNumber() + ",");
            }
        }
        StringBuffer billNumbers = new StringBuffer().append("'").append(billNumber).append("'");
        List<Long> orderIds = orderFeeStatementMapper.getOrderIdsByBill(billNumbers.toString());

        if (orderIds != null && orderIds.size() > 0) {
            for (Long orderId : orderIds) {
                iOrderOpRecordService.saveOrUpdate(Long.valueOf(orderId + ""), 213, accessToken);//??????
            }
        }

        // ??????????????????
        String msg = "???????????????" + receiptNumber;
        if (StringUtils.isBlank(receiptNumber)) {
            msg = "???????????????";
        }
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, msg, accessToken, getLongBillNumber(billNumber));

    }

    @Override
    public void undoBill(String billNumberStr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        String[] bills = billNumberStr.split(",");
        StringBuffer billNumbers = new StringBuffer();
        for (String b : bills) {
            billNumbers.append("'").append(b).append("',");
        }
        String billNumbersSub = billNumbers.substring(0, billNumbers.length() - 1);

        List<OrderBillInfo> orderBillInfoByBillNumbers = orderBillInfoMapper.getOrderBillInfoByBillNumbers(billNumbersSub);

        Date date = new Date();
        int billOperType = 3;

        for (OrderBillInfo b : orderBillInfoByBillNumbers) {
            String operName = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int z = 0;
            int h = 0;
            if (b.getBillSts() == SysStaticDataEnum.BILL_STS.NEW) {
                b.setBillSts(SysStaticDataEnum.BILL_STS.DEL_STS);
                b.setReceiptNumber("");
                operName = "????????????[??????]???[??????]??????";
                // ????????????
                z = orderFeeStatementMapper.updateGoBackStatusByBillNumber0(b.getBillNumber());
                // ???????????????
                h = orderFeeStatementHMapper.updateGoBackStatusHByBillNumber0(b.getBillNumber());
            } else if (b.getBillSts() == SysStaticDataEnum.BILL_STS.MAKE_RECEPIT) {
                b.setBillSts(SysStaticDataEnum.BILL_STS.NEW);
                b.setReceiptNumber("");
                operName = "????????????[?????????]???[??????]??????";
                // ????????????
                z = orderFeeStatementMapper.updateGoBackStatusByBillNumber1(b.getBillNumber());
                // ???????????????
                h = orderFeeStatementHMapper.updateGoBackStatusHByBillNumber1(b.getBillNumber());
            } else {
                //????????????
                if (b.getBillSts() == SysStaticDataEnum.BILL_STS.CHECK_ALL) {
                    operName = "????????????[?????????]???[?????????]??????";
                } else {
                    operName = "????????????[????????????]????????????]??????";
                }
                b.setBillSts(SysStaticDataEnum.BILL_STS.MAKE_RECEPIT);
                b.setCheckAmount(0L);
                // ????????????
                z = orderFeeStatementMapper.updateGoBackStatusByBillNumber2(b.getBillNumber());
                // ???????????????
                h = orderFeeStatementHMapper.updateGoBackStatusHByBillNumber2(b.getBillNumber());

                //??????????????????(???????????????????????? ??????????????? ?????????)
                //???????????????
                int num = orderBillCheckInfoHMapper.insertCheckInfoH(b.getBillNumber(), loginInfo.getTenantId(), loginInfo.getUserInfoId());
                if (num > 0) {
                    //??????????????????
                    orderBillCheckInfoMapper.deleteCheckInfoByBillNumberAndTenantId(b.getBillNumber(), loginInfo.getTenantId());
                }

                // ??????-???????????????
                // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                Long checkAmount = 0L;
                if (orderFeeStatementMapper.getOrderFeeStatementByBillNumber(b.getBillNumber()) != null) {
                    checkAmount += orderFeeStatementMapper.getOrderFeeStatementByBillNumber(b.getBillNumber());
                }

                //??????????????????????????????????????????
                if (checkAmount > 0) {
                    OrderBillCheckInfo c = new OrderBillCheckInfo();
                    c.setCheckFee(checkAmount);
                    c.setBillNumber(b.getBillNumber());
                    c.setCheckType(EnumConsts.OrderBillCHECK_TYPE.CASH);
                    c.setCreateTime(LocalDateTime.now());
                    c.setCreatorId(loginInfo.getId());
                    c.setOperId(loginInfo.getId());
                    c.setOperDate(sdf.format(date));
                    c.setTenantId(loginInfo.getTenantId());
                    orderBillCheckInfoMapper.insertChechInfo(c);
                }

            }
            b.setRealIncome(0L);
            b.setOperDate(sdf.format(date));
            b.setOperId(loginInfo.getId());
            orderBillInfoMapper.updateRecordByBillNumber(b);

            if (z + h <= 0) {
                throw new BusinessException("????????????[" + b.getBillNumber() + "]???????????????????????????");
            }

            //?????????????????? add by huangqb
            List<Long> orderIds = orderFeeStatementMapper.getOrderIdsByBill(billNumbersSub);
            if (orderIds != null && orderIds.size() > 0) {
                for (Long orderId : orderIds) {
                    iOrderOpRecordService.saveOrUpdate(orderId, 213, accessToken);//??????
                }
            }
            // ???????????????????????????
            saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, operName, accessToken, getLongBillNumber(b.getBillNumber()));
        }

    }

    @Override
    public void billAddOrders(String billNumber, String orderIdsStr, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        String[] orderIds = orderIdsStr.split(",");
        StringBuffer sb = new StringBuffer();
        int size = 0;
        for (String orderId : orderIds) {
            sb.append("'").append(orderId).append("',");
            size++;
        }
        String orderIdsSub = sb.substring(0, sb.length() - 1);

        OrderBillInfo orderBillInfo = orderBillInfoMapper.getOrderBillInfo(billNumber);
        if (orderBillInfo == null) {
            throw new BusinessException("???????????????[" + billNumber + "],??????????????????");
        }
        if (orderBillInfo.getBillSts() != SysStaticDataEnum.BILL_STS.NEW) {
            throw new BusinessException("???????????????????????????????????????????????????");
        }

        // ???????????????????????????????????????
        List<OrderFeeStatement> orderFeeStatements = getOrderFeeStatementByIds(orderIdsSub);
        for (OrderFeeStatement orderFeeStatement : orderFeeStatements) {
            if (StringUtils.isNotBlank(orderFeeStatement.getBillNumber())) {
                throw new BusinessException("??????[" + orderFeeStatement.getOrderId() + "]???????????????[" + orderFeeStatement.getBillNumber() + "],??????????????????????????????????????????");
            }
        }

        // ??????billNumber ?????? ????????????????????????
        checkOrderCreateBill(orderIdsSub, billNumber, loginInfo.getTenantId());
        // ???????????????????????????????????? ??????????????????BL??????
        checkOrderExceptionCreateBill(orderIdsSub, billNumber);

        //???????????????
        int resultNumH = orderFeeStatementHMapper.updateBillNumberByOrderIds(billNumber, orderIdsSub);
        //????????????
        int resultNumM = orderFeeStatementMapper.updateBillNumberByOrderIds(billNumber, orderIdsSub);
        if (resultNumH + resultNumM != size) {
            log.error("???????????????????????????????????????[" + (resultNumH + resultNumM) + "???]?????????[" + size + "]????????????????????????" + orderIdsStr);
            throw new BusinessException("????????????BL????????????????????????????????????????????????????????????????????????");
        }

        // ??????????????????
        for (String orderId : orderIds) {
            iOrderOpRecordService.saveOrUpdate(Long.valueOf(orderId), 214, accessToken); // ??????????????????
        }

        String msg = "??????[" + orderIds.length + "]??????????????????";
        // ??????????????????
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, msg, accessToken, getLongBillNumber(billNumber));
    }

    @Override
    public void billReduceOrders(String billNumber, String orderIdsStr, String accessToken) {
        String[] orderIds = orderIdsStr.split(",");
        StringBuffer sb = new StringBuffer();
        int size = 0;
        for (String orderId : orderIds) {
            sb.append("'").append(orderId).append("',");
            size++;
        }
        String orderIdsSub = sb.substring(0, sb.length() - 1);

        OrderBillInfo orderBillInfo = orderBillInfoMapper.getOrderBillInfo(billNumber);
        if (orderBillInfo == null) {
            throw new BusinessException("?????????????????????");
        }
        if (orderBillInfo.getBillSts() != SysStaticDataEnum.BILL_STS.NEW) {
            throw new BusinessException("???????????????????????????????????????????????????");
        }

        //??????????????????????????????
        int orderNum = orderFeeStatementMapper.getOrderNumBuBillNumber(billNumber);

        // ??????????????????????????????
        int resultNumH = orderFeeStatementHMapper.queryBillNumberByOrderIdsAndBillNumber(billNumber, orderIdsSub);
        int resultNumM = orderFeeStatementMapper.queryBillNumberByOrderIdsAndBillNumber(billNumber, orderIdsSub);

        if (resultNumH + resultNumM != size) {
            log.error("???????????????????????????????????????[" + (resultNumH + resultNumM) + "???]?????????[" + size + "]????????????????????????" + orderIdsStr);
            throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????");
        }
        if (orderNum == resultNumH + resultNumM) {
            throw new BusinessException("??????????????????????????????????????????????????????????????????[??????]???????????????");

        }

        //???????????????
        orderFeeStatementHMapper.updateBillNumberByOrderIdsAndBillNumber(billNumber, orderIdsSub);
        //????????????
        orderFeeStatementMapper.updateBillNumberByOrderIdsAndBillNumber(billNumber, orderIdsSub);

        //?????????????????? add by huangqb
        for (String orderId : orderIds) {
            iOrderOpRecordService.saveOrUpdate(Long.valueOf(orderId), 215, accessToken); // ??????????????????
        }

        String msg = "??????[" + orderIds.length + "]?????????";
        // ???????????????????????????
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, msg, accessToken, getLongBillNumber(billNumber));

    }

    @Override
    public String createBill(String orderIdStrs, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String[] orderIds = orderIdStrs.split(",");
        StringBuffer sb = new StringBuffer();
        for (String o : orderIds) {
            // ???????????????????????? add by huangqb
            iOrderOpRecordService.saveOrUpdate(Long.valueOf(o), 211, accessToken);
            sb.append("'").append(o).append("',");
        }
        String orderIdSub = sb.substring(0, sb.length() - 1);

        // ???????????????
        String billNumber = CommonUtil.createBillNo();

        // ??????????????????????????????
        List<OrderFeeStatement> orderFeeStatementList = getOrderFeeStatementByIds(orderIdSub);
        if (orderFeeStatementList != null && orderFeeStatementList.size() > 0) {
            for (OrderFeeStatement orderFeeStatement : orderFeeStatementList) {
                if (StringUtils.isNotBlank(orderFeeStatement.getBillNumber())) {
                    throw new BusinessException("??????[" + orderFeeStatement.getOrderId() + "]???????????????[" + orderFeeStatement.getBillNumber() + "],??????????????????????????????????????????");
                }
            }
        }

        // ?????? ??????????????????????????????
        checkOrderCreateBill(orderIdSub, null, loginInfo.getTenantId()); //????????????????????????????????????
        // ??????????????????????????????????????????????????????BL??????
        checkOrderExceptionCreateBill(orderIdSub, null);

        // ???????????????????????????
        int resultNumH = orderFeeStatementHMapper.updateBillNumberByOrderIds(billNumber, orderIdSub);
        // ????????????????????????
        int resultNumM = orderFeeStatementMapper.updateBillNumberByOrderIds(billNumber, orderIdSub);
        if (resultNumH + resultNumM != orderIds.length) {
            log.error("?????????????????????????????????[" + (resultNumH + resultNumM) + "???]?????????[" + orderIds.length + "]????????????????????????" + orderIdStrs);
            throw new BusinessException("????????????BL????????????????????????????????????????????????????????????????????????");
        }

        // ???????????????
        // ??????-???????????????
        // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
        Long checkAmount = 0L;
        List<OrderFeeStatement> orderFeeStatements = getOrderFeeStatementByIds(orderIdSub);
        if (orderFeeStatements != null && orderFeeStatements.size() > 0) {
            for (OrderFeeStatement ofs : orderFeeStatements) {
                checkAmount += (ofs.getSourceCheckAmount() == null ? 0 : ofs.getSourceCheckAmount());
            }
        }

        // ??????????????????????????????????????????
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (checkAmount > 0) {
            OrderBillCheckInfo c = new OrderBillCheckInfo();
            c.setCheckFee(checkAmount);
            c.setBillNumber(billNumber);
            c.setCheckType(EnumConsts.OrderBillCHECK_TYPE.CASH);
            c.setCreateTime(LocalDateTime.now());
            c.setCreatorId(loginInfo.getUserInfoId());
            c.setOperId(loginInfo.getUserInfoId());
            c.setOperDate(sdf.format(new Date()));
            c.setTenantId(loginInfo.getTenantId());
            orderBillCheckInfoMapper.insertChechInfo(c);
        }

        OrderBillInfo bill = new OrderBillInfo();
        bill.setRealIncome(0L);
        bill.setBillNumber(billNumber);
        bill.setCheckAmount(checkAmount);
        bill.setCreateTime(LocalDateTime.now());
        bill.setCreatorId(loginInfo.getUserInfoId());
        bill.setOperId(loginInfo.getUserInfoId());
        bill.setOperDate(sdf.format(new Date()));
        bill.setBillSts(SysStaticDataEnum.BILL_STS.NEW);
        bill.setTenantId(loginInfo.getTenantId());
        bill.setCreatorName(loginInfo.getName());
        orderBillInfoMapper.insertOrderBillInfo(bill);

        // ??????????????????
        String msg = "???????????????" + billNumber;
        saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageBill, SysOperLogConst.OperType.Update, msg, accessToken, getLongBillNumber(billNumber));
        for (String orderId : orderIds) {
            saveSysOperLog(SysOperLogConst.BusiCode.IncomeManageOrder, SysOperLogConst.OperType.Update, msg, accessToken, Long.parseLong(orderId));
        }

        return billNumber;
    }

    @Override
    @Async
    public void createOrderBillByExcel(byte[] bytes, String fileName, ImportOrExportRecords record, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(fileName, is);

            int sheetNo = 1;
            // ??????????????????
            int rows = parse.getRealRowCount(sheetNo);

            List<String> orderIdsDistinct = new ArrayList<String>(); // ??????????????????????????????????????????

            List<OrederExportBillVo> orederExportBillVos = new ArrayList<OrederExportBillVo>(); // ????????????????????????????????????????????????

            for (int i = 2; i <= rows; i++) {
                StringBuffer reasonFailure = new StringBuffer(); // ????????????
                OrederExportBillVo orederExportBillVo = new OrederExportBillVo();
                String billNumber = parse.readExcelByRowAndCell(sheetNo, i, 1); //?????????
                orederExportBillVo.setBillNumber(billNumber);
                String orderId = parse.readExcelByRowAndCell(sheetNo, i, 2); //?????????
                orederExportBillVo.setOrderId(orderId);
                String billDiff = parse.readExcelByRowAndCell(sheetNo, i, 3); //????????????
                orederExportBillVo.setBillDiff(billDiff);
                String billDiffRemark = parse.readExcelByRowAndCell(sheetNo, i, 4); //??????
                orederExportBillVo.setBillDiffRemark(billDiffRemark);
                String kpiDiff = parse.readExcelByRowAndCell(sheetNo, i, 5); //KPI??????
                orederExportBillVo.setKpiDiff(kpiDiff);
                String kpiDiffRemark = parse.readExcelByRowAndCell(sheetNo, i, 6); //??????
                orederExportBillVo.setKpiDiffRemark(kpiDiffRemark);
                String oilFeeDiff = parse.readExcelByRowAndCell(sheetNo, i, 7); //????????????
                orederExportBillVo.setOilFeeDiff(oilFeeDiff);
                String oilFeeDiffRemark = parse.readExcelByRowAndCell(sheetNo, i, 8); //??????
                orederExportBillVo.setOilFeeDiffRemark(oilFeeDiffRemark);
                String billIngDiff = parse.readExcelByRowAndCell(sheetNo, i, 9); //????????????
                orederExportBillVo.setBillIngDiff(billIngDiff);
                String billIngDiffRemark = parse.readExcelByRowAndCell(sheetNo, i, 10); //??????
                orederExportBillVo.setBillIngDiffRemark(billIngDiffRemark);
                String otherDiff = parse.readExcelByRowAndCell(sheetNo, i, 11); //????????????
                orederExportBillVo.setOtherDiff(otherDiff);
                String otherDiffRemark = parse.readExcelByRowAndCell(sheetNo, i, 12); //??????
                orederExportBillVo.setOtherDiffRemark(otherDiffRemark);

                Boolean flag = true; // ??????????????????orderId????????????\??????

                if (org.apache.commons.lang.StringUtils.isEmpty(orderId)) {
                    reasonFailure.append("??????????????????");
                    flag = false;
                }
                if (!CommonUtil.isLong(orderId.trim())) {
                    reasonFailure.append("??????????????????");
                    flag = false;
                }

                if (flag) { // ??????/??????
                    // ??????????????????
                    if (orderIdsDistinct.contains(orderId)) { // ??????
                        reasonFailure.append("???????????????");
                    } else {
                        orderIdsDistinct.add(orderId);
                        // ????????????
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(billDiff) && org.apache.commons.lang.StringUtils.isNotEmpty(billDiff.trim())) {
                            if (!CommonUtil.isNumber(billDiff)) {
                                reasonFailure.append("??????????????????????????????");
                            }
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(kpiDiff) && org.apache.commons.lang.StringUtils.isNotEmpty(kpiDiff.trim())) {
                            if (!CommonUtil.isNumber(kpiDiff)) {
                                reasonFailure.append("KPI????????????????????????");
                            }
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(oilFeeDiff) && org.apache.commons.lang.StringUtils.isNotEmpty(oilFeeDiff.trim())) {
                            if (!CommonUtil.isNumber(oilFeeDiff)) {
                                reasonFailure.append("??????????????????????????????");
                            }
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(billIngDiff) && org.apache.commons.lang.StringUtils.isNotEmpty(billIngDiff.trim())) {
                            if (!CommonUtil.isNumber(billIngDiff)) {
                                reasonFailure.append("??????????????????????????????");
                            }
                        }
                        if (org.apache.commons.lang.StringUtils.isNotEmpty(otherDiff) && org.apache.commons.lang.StringUtils.isNotEmpty(otherDiff.trim())) {
                            if (!CommonUtil.isNumber(otherDiff)) {
                                reasonFailure.append("??????????????????????????????");
                            }
                        }
                    }
                }

                List<OrderFeeStatement> orderFeeStatements = getOrderFeeStatementByIds("'" + orderId + "'");
                // ????????????????????????
                if (orderFeeStatements == null || orderFeeStatements.size() < 1) {
                    reasonFailure.append("??????[" + orderId + "]??????????????????");
                }
                // ????????????????????????
                if (!"".equals(billNumber)) {
                    LambdaQueryWrapper<OrderBillInfo> billNumberExist = new LambdaQueryWrapper<>();
                    billNumberExist.eq(OrderBillInfo::getBillNumber, billNumber);
                    billNumberExist.eq(OrderBillInfo::getTenantId, loginInfo.getTenantId());
                    List<OrderBillInfo> list = this.list(billNumberExist);
                    if (list.size() == 0) {
                        reasonFailure.append("??????[" + billNumber + "]??????????????????");
                    }
                    // ????????????????????????????????????????????????
                    String billNumberCheckName = baseMapper.getBillNumberCheckName(billNumber, loginInfo.getTenantId());
                    billNumberCheckName = null == billNumberCheckName ? "bill" : billNumberCheckName;
                    String orderIdCheckName = baseMapper.getOrderIdCheckName(orderId, loginInfo.getTenantId());
                    orderIdCheckName = null == orderIdCheckName ? "order" : orderIdCheckName;
                    if (orderFeeStatements.size() != 0 && list.size() != 0) {
                        if (!(billNumberCheckName.equals(orderIdCheckName))) {
                            reasonFailure.append("??????[" + orderId + "]?????????[" + billNumber + "]??????????????????????????????BL??????????????????");
                        }
                    }
                }

                // ??????????????????????????????
                for (OrderFeeStatement orderFeeStatement : orderFeeStatements) {
                    if (StringUtils.isNotBlank(orderFeeStatement.getBillNumber())) {
                        reasonFailure.append("??????[" + orderFeeStatement.getOrderId() + "]???????????????[" + orderFeeStatement.getBillNumber() + "],??????????????????????????????????????????");
                    }
                }

                orederExportBillVo.setReasonFailure(reasonFailure.toString());
                orederExportBillVos.add(orederExportBillVo);
            }

            Map<String, String> inBillParams = new HashMap<String, String>(); // ???????????????s
            List<IncomeOrderImportVo> importIns = new ArrayList<IncomeOrderImportVo>(); // ?????????????????????
            List<Long> orderIdsList = new ArrayList<Long>(); // ??????

            List<OrederExportBillVo> failureList = new ArrayList<OrederExportBillVo>(); // ????????????

            int success = 0;
            for (OrederExportBillVo orederExportBillVo : orederExportBillVos) {
                //?????????????????????????????????
                if (StringUtils.isEmpty(orederExportBillVo.getReasonFailure())) {
                    String orderId = orederExportBillVo.getOrderId().trim();
                    String billNumber = orederExportBillVo.getBillNumber().trim();

                    // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (StringUtils.isNotBlank(billNumber)) {
                        String orderIds = inBillParams.get(billNumber);
                        if (StringUtils.isNotEmpty(orderIds)) {
                            inBillParams.put(billNumber, orderIds + "," + orderId);
                        } else {
                            inBillParams.put(billNumber, orderId);
                        }
                    } else {
                        // ??????bill??????????????? ?????? ????????????????????????
                        orderIdsList.add(Long.valueOf(orderId));
                    }

                    //???????????????????????? add by huangqb
                    iOrderOpRecordService.saveOrUpdate(Long.valueOf(orderId), 211, accessToken);

                    IncomeOrderImportVo importIn = new IncomeOrderImportVo();
                    /**
                     * ?????????|?????????|????????????|??????|????????????|??????|????????????|??????|????????????|??????|????????????|??????
                     * 1????????????2????????????3????????????4????????????5??????
                     */
                    long orderIdLong = Long.parseLong(orderId);
                    List<OrderDiffInfo> orderDiffInfos = getDiffInfo(orderIdLong, orederExportBillVo);

                    importIn.setOrderDiffInfos(orderDiffInfos);
                    importIn.setOrderId(orderId);
                    importIn.setBillNumber(billNumber);

                    // ????????????
                    importIns.add(importIn);

                    success++;

                } else {
                    failureList.add(orederExportBillVo);
                }
            }

            for (Map.Entry<String, String> entry : inBillParams.entrySet()) {

                // ?????????????????????
                billAddOrders(entry.getKey(), entry.getValue(), accessToken);
            }

            //??????BL??????
            if (orderIdsList.size() > 0) {
                //????????????????????????????????????
                StringBuffer sb = new StringBuffer();
                for (Long id : orderIdsList) {
                    sb.append("'").append(id).append("',");
                }
                List<OrderFeeStatement> orderFeeStatementList = getOrderFeeStatementByIds(sb.substring(0, sb.length() - 1));
                String osList = "";
                if (orderFeeStatementList != null && orderFeeStatementList.size() > 0) {
                    for (OrderFeeStatement orderFeeStatement : orderFeeStatementList) {
                        if (StringUtils.isBlank(orderFeeStatement.getBillNumber())) {
                            if (StringUtils.isEmpty(osList)) {
                                osList = orderFeeStatement.getOrderId() + "";
                            } else {
                                osList = osList + "," + orderFeeStatement.getOrderId();
                            }
                        } else {
                            throw new BusinessException("??????[" + orderFeeStatement.getOrderId() + "]????????????????????????????????????????????????");
                        }
                    }
                }

                if (StringUtils.isNotEmpty(osList)) {
                    createBill(osList, accessToken); // ?????????????????????????????????
                }

            }

            // ????????????
            if (importIns != null && importIns.size() > 0) {
                for (IncomeOrderImportVo in : importIns) {
                    iOrderInfoService.saveOrderDiff(Long.parseLong(in.getOrderId()), in.getOrderDiffInfos(), accessToken);
                }
            }

            if (CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"?????????", "?????????", "????????????", "??????",
                        "KPI??????", "??????", "????????????", "??????",
                        "????????????", "??????", "????????????", "??????", "????????????"};
                resourceFild = new String[]{"getBillNumber", "getOrderId", "getBillDiff", "getBillDiffRemark",
                        "getKpiDiff", "getKpiDiffRemark", "getOilFeeDiff", "getOilFeeDiffRemark"
                        , "getBillIngDiff", "getBillIngDiffRemark", "getOtherDiff", "getOtherDiffRemark", "getReasonFailure"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, OrederExportBillVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "????????????????????????.xlsx", inputStream.available());
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(failureList.size() + "?????????");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "?????????");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "?????????");
                record.setState(3);
            }
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            record.setState(4);
            record.setFailureReason("?????????????????????????????????");
            record.setRemarks("?????????????????????????????????");
            String[] showName = new String[]{"??????????????????", "??????????????????", "????????????"};
            String[] resourceFild = new String[]{"getId", "getMediaName", "getFailureReason"};
            try {
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(Lists.newArrayList(record), showName, resourceFild, ImportOrExportRecords.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream, "????????????????????????.xlsx", inputStream.available());
                record.setFailureUrl(failureExcelPath);
                if (null != os) {
                    os.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
            } catch (Exception e1) {
                throw new BusinessException("???????????????????????????");
            }
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void exportQuery(String billNumbers, String customNames, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        String[] billNumberArr = billNumbers.split(",");
        String[] customNameArr = customNames.split(",");
        int num = 0;
        Workbook workbook = new XSSFWorkbook();
        for (String billNumber : billNumberArr) {
            List<List<?>> content = new ArrayList<List<?>>();
            List<String> head = new ArrayList<String>();
            head.add("????????????");
            head.add("?????????");
            head.add("?????????");
            head.add("????????????");
            head.add("?????????");
            head.add("?????????");
            head.add("???????????? ");
            head.add("????????????");
            head.add("????????????");
            head.add("????????????");
            content.add(head);

            String customName = customNameArr[num];
            Sheet sheet = workbook.createSheet(customName + "-??????????????????(" + billNumber + ")");

            List<ExportQueryOrderInfoDto> list = baseMapper.exportQueryOrderInfo(billNumber, loginInfo.getTenantId());
            Double costPriceSum = 0.0;
            Double confirmAmountSum = 0.0;
            Double getAmountSum = 0.0;
            Double confirmDiffAmountSum = 0.0;
            int count = 0;
            if (list != null && list.size() > 0) {
                for (ExportQueryOrderInfoDto ob : list) {
                    List<String> rowData = new ArrayList<String>();
                    String orderId = ob.getOrderId() == null ? "" : ob.getOrderId();
                    String dependTime = ob.getDependTime() == null ? "" : ob.getDependTime();
                    Double costPrice = ob.getCostPrice() == null ? 0.0 : Double.parseDouble(ob.getCostPrice());
                    Double incomeExceptionFee = ob.getIncomeExceptionFee() == null ? 0.0 : Double.parseDouble(ob.getIncomeExceptionFee());
                    Double getAmount = ob.getGetAmount() == null ? 0.0 : Double.parseDouble(ob.getGetAmount());
                    Double confirmDiffAmount = ob.getConfirmDiffAmount() == null ? 0.0 : Double.parseDouble(ob.getConfirmDiffAmount());
                    String orgName = ob.getOrgName() == null ? "" : ob.getOrgName();
                    String customerId = ob.getCustomerId() == null ? "" : ob.getCustomerId();
                    String source = ob.getSourceRegionName() == null ? "" : ob.getSourceRegionName();
                    String des = ob.getDesRegionName() == null ? "" : ob.getDesRegionName();
                    Double confirmAmount = costPrice + confirmDiffAmount + incomeExceptionFee;

                    costPriceSum += costPrice;
                    confirmAmountSum += confirmAmount;
                    getAmountSum += getAmount;
                    confirmDiffAmountSum += confirmDiffAmount;
                    rowData.add(dependTime);
                    rowData.add(orgName);
                    rowData.add(orderId);
                    rowData.add(customerId);
                    rowData.add(source);
                    rowData.add(des);
                    rowData.add(costPrice.toString());
                    rowData.add(confirmAmount.toString());
                    rowData.add(confirmDiffAmount.toString());
                    rowData.add(getAmount.toString());
                    content.add(rowData);
                    count++;
                }
            }
            List<String> rowData = new ArrayList<String>();
            rowData.add("?????????" + count);
            rowData.add("");
            rowData.add("");
            rowData.add("");
            rowData.add("");
            rowData.add("");
            rowData.add(String.format("%.2f", costPriceSum));
            rowData.add(String.format("%.2f", confirmAmountSum));
            rowData.add(String.format("%.2f", confirmDiffAmountSum));
            rowData.add(String.format("%.2f", getAmountSum));
            content.add(rowData);
            for (int i = 0; i < 2; i++) {
                List<String> rowData1 = new ArrayList<String>();
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                rowData1.add("");
                content.add(rowData1);
            }
            List<String> rowData2 = new ArrayList<String>();
            rowData2.add("????????????");
            rowData2.add(billNumber);
            rowData2.add("???????????????");
            rowData2.add(customName);
            rowData2.add("");
            rowData2.add("");
            rowData2.add("");
            rowData2.add("");
            rowData2.add("");
            rowData2.add("");
            content.add(rowData2);
            num++;
            List<String> head2 = new ArrayList<String>();
            head2.add("?????????");
            head2.add("????????????");
            head2.add("????????????");
            head2.add("????????????");
            head2.add("????????????");
            head2.add("????????????");
            head2.add("KPI?????? ");
            head2.add("????????????");
            head2.add("????????????");
            head2.add("????????????");
            content.add(head2);

            List<ExportQueryBillInfoDto> listSb2 = baseMapper.exportQueryBillInfo(billNumber, loginInfo.getTenantId());
            if (listSb2 != null && listSb2.size() > 0) {
                for (ExportQueryBillInfoDto ob : listSb2) {
                    List<String> rowData3 = new ArrayList<String>();
                    String orgName = ob.getOrgName() == null ? "" : ob.getOrgName();
                    String dependMonth = ob.getDependTime() == null ? "" : ob.getDependTime();
                    String orderNum = ob.getCountTotal() == null ? "0.0" : ob.getCountTotal();
                    String costPrice = ob.getCostPrice() == null ? "0.0" : ob.getCostPrice();
                    String confirmAmount = ob.getConfirMamount() == null ? "0.0" : ob.getConfirMamount();
                    String diff1 = ob.getDiff1() == null ? "0.0" : ob.getDiff1();
                    String diff2 = ob.getDiff2() == null ? "0.0" : ob.getDiff2();
                    String diff3 = ob.getDiff3() == null ? "0.0" : ob.getDiff3();
                    String diff4 = ob.getDiff4() == null ? "0.0" : ob.getDiff4();
                    String diff5 = ob.getDiff5() == null ? "0.0" : ob.getDiff5();
                    rowData3.add(orgName);
                    rowData3.add(dependMonth);
                    rowData3.add(orderNum);
                    rowData3.add(costPrice);
                    rowData3.add(confirmAmount);
                    rowData3.add(diff1);
                    rowData3.add(diff2);
                    rowData3.add(diff3);
                    rowData3.add(diff4);
                    rowData3.add(diff5);
                    content.add(rowData3);
                }
            }
            //????????????
            createExcel(content, false, workbook, sheet);
        }

        try {
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
    public OrderBillInfo getOrderBillInfo(String billNumber) {
        OrderBillInfo byId = this.getById(billNumber);
        if (byId != null) {
            return byId;
        }
        return new OrderBillInfo();
    }

    @Override
    public void saveChecksByProcess(Long orderId, Long getAmount, String billNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderBillInfo orderBillInfo = this.getOrderBillInfo(billNumber);
        orderBillInfo.setCheckBillDate(new Date());

        //??????????????????
        OrderBillInfoVo vo = new OrderBillInfoVo();
        vo.setBillNumber(billNumber);
        vo.setAllBill(true);
        List<OrderBillInfoDto> out = baseMapper.doQueryExport(vo, loginInfo.getTenantId());
        if (out == null || out.size() <= 0) {
            throw new BusinessException("??????????????????" + billNumber + "?????????????????????");
        }
        long orderConfirmAmountSum = (out.get(0).getCostPrice() + out.get(0).getConfirmDiffAmount() + out.get(0).getIncomeExceptionFee());//????????????
        long syCheck = Math.round(out.get(0).getSyCheckDouble() * 100);//???????????????

        long checkAmount = getAmount;
        if (syCheck >= 0 && syCheck <= getAmount) {
            checkAmount = syCheck;
            orderBillInfo.setCheckAmount(orderConfirmAmountSum);
            orderBillInfo.setBillSts(SysStaticDataEnum.BILL_STS.CHECK_ALL);

        } else if (syCheck > getAmount) {
            orderBillInfo.setCheckAmount(orderConfirmAmountSum - (syCheck - getAmount));
            orderBillInfo.setBillSts(SysStaticDataEnum.BILL_STS.CHECK_PART);
        }
        long badDebt = 0;
        boolean existCash = false;
        //???????????????????????????????????????????????????
        List<OrderBillCheckInfo> lists = iOrderBillCheckInfoService.queryInfoByBillNumber(billNumber);
        if (lists != null && lists.size() > 0) {
            for (OrderBillCheckInfo checkInfo : lists) {
                if (checkInfo.getCheckType() == EnumConsts.OrderBillCHECK_TYPE.CASH) {
                    OrderBillCheckInfo c = lists.get(0);
                    if (orderBillInfo.getBillSts() == SysStaticDataEnum.BILL_STS.CHECK_ALL) {
                        c.setCheckFee(c.getCheckFee() + syCheck);
                    } else {
                        c.setCheckFee(c.getCheckFee() + getAmount);
                    }
                    c.setOperId(0L);
                    c.setOperDate(LocalDateTime.now().toString());
                    iOrderBillCheckInfoService.saveOrUpdate(c);
                    existCash = true;
                } else if (checkInfo.getCheckType() == EnumConsts.OrderBillCHECK_TYPE.BAD_DEBT) {
                    badDebt += checkInfo.getCheckFee();
                }
            }
        }

        if (!existCash) {
            OrderBillCheckInfo c = new OrderBillCheckInfo();
            c.setCheckFee(checkAmount);
            c.setBillNumber(billNumber);
            c.setCheckType(EnumConsts.OrderBillCHECK_TYPE.CASH);
            c.setCreateTime(LocalDateTime.now());
            c.setCreatorId(0L);
            c.setOperId(0L);
            c.setOperDate(LocalDateTime.now().toString());
            c.setTenantId(-1L);
            iOrderBillCheckInfoService.saveOrUpdate(c);
        }

        this.saveOrUpdate(orderBillInfo);
        /**
         * ???????????????????????????
         */
        apportionCheckAmountToOrder(billNumber, orderBillInfo.getCheckAmount(), orderBillInfo.getBillSts(), badDebt, accessToken);
    }

    public void createExcel(List<List<?>> content, boolean hasSummary, Workbook wb, Sheet s) {
        Font font = wb.createFont();
        font.setBoldweight((short) 700);

        CellStyle headstyle = wb.createCellStyle();
        headstyle.setFont(font);
        headstyle.setBorderTop((short) 1);
        headstyle.setBorderBottom((short) 1);
        headstyle.setBorderLeft((short) 1);
        headstyle.setBorderRight((short) 1);
        headstyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headstyle.setFillPattern((short) 1);

        CellStyle rowstyle1 = wb.createCellStyle();
        rowstyle1.setBorderTop((short) 1);
        rowstyle1.setBorderBottom((short) 1);
        rowstyle1.setBorderLeft((short) 1);
        rowstyle1.setBorderRight((short) 1);
        rowstyle1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        rowstyle1.setFillPattern((short) 1);

        Font summaryFont = wb.createFont();
        summaryFont.setColor(IndexedColors.RED.getIndex());
        CellStyle tailstyle = wb.createCellStyle();
        tailstyle.setFont(summaryFont);
        tailstyle.setBorderTop((short) 1);
        tailstyle.setBorderBottom((short) 1);
        tailstyle.setBorderLeft((short) 1);
        tailstyle.setBorderRight((short) 1);

        Row r = null;
        Cell c = null;
        List rowContent = null;
        for (int i = 0; i < content.size(); ++i) {
            r = s.createRow(i);
            rowContent = (List) content.get(i);
            if (rowContent != null) {
                for (int j = 0; j < rowContent.size(); ++j) {
                    c = r.createCell(j);

                    if (i == 0) {
                        c.setCellStyle(headstyle);
                    } else if ((hasSummary) && (i == content.size() - 1)) {
                        c.setCellStyle(tailstyle);
                    } else {
                        c.setCellStyle(rowstyle1);
                    }
                    if (rowContent.get(j) != null) {
                        if (rowContent.get(j) instanceof Integer) {
                            c.setCellValue(((Integer) rowContent.get(j)).intValue());

                        } else if (rowContent.get(j) instanceof Long) {
                            c.setCellValue(((Long) rowContent.get(j)).longValue());

                        } else if (rowContent.get(j) instanceof Double) {
                            c.setCellValue(((Double) rowContent.get(j)).doubleValue());
                        } else if (rowContent.get(j) instanceof Float) {
                            c.setCellValue(((Float) rowContent.get(j)).floatValue());
                        } else if (rowContent.get(j) instanceof BigDecimal) {
                            c.setCellValue(((BigDecimal) rowContent.get(j)).doubleValue());
                        } else {
                            c.setCellValue(rowContent.get(j).toString());
                        }
                    } else {
                        c.setCellValue("");
                    }
                }
            }
        }
    }

    /**
     * ???????????????????????????
     */
    public void apportionCheckAmountToOrder(String billNumber, long checkAmount, int billSts, long badDebt, String
            accessToken) {

        //1?????????????????????????????? SOURCE_CHECK_AMOUNT????????????????????????????????????????????????????????????
        long sourceAmount = 0;
        //???????????????????????????????????????????????????
        List<OrderFeeStatementH> orderFeeStatementHs = orderFeeStatementHMapper.queryFeeHByBillNumber(billNumber);

        if (orderFeeStatementHs != null && orderFeeStatementHs.size() > 0) {
            for (OrderFeeStatementH ofh : orderFeeStatementHs) {
                sourceAmount += ofh.getSourceCheckAmount();
                ofh.setGetAmount(ofh.getSourceCheckAmount());
            }
        }
        List<OrderFeeStatement> orderFeeStatements = orderFeeStatementMapper.queryFeeByBillNumber(billNumber);
        if (orderFeeStatements != null && orderFeeStatements.size() > 0) {
            for (OrderFeeStatement of : orderFeeStatements) {
                sourceAmount += of.getSourceCheckAmount();
                of.setGetAmount(of.getSourceCheckAmount());
            }
        }
        //2???????????????????????????????????? sourceAmount
        long amount = checkAmount - sourceAmount;
        long subBadDebt = badDebt;
        //3????????????????????????:?????????????????????????????????,????????????????????????????????????????????????
        if (amount > 0) {
            if (orderFeeStatementHs != null && orderFeeStatementHs.size() > 0) {
                for (OrderFeeStatementH ofh : orderFeeStatementHs) {
                    OrderInfoVo orderReceivableIn = new OrderInfoVo();
                    orderReceivableIn.setTenantId(ofh.getTenantId());
                    orderReceivableIn.setOrderId(ofh.getOrderId() + "");
                    PageInfo<OrderInfoDto> p = iOrderInfoService.queryReceviceManageOrder(orderReceivableIn, accessToken, 1, 10);
                    long confirmAmount = p.getList().get(0).getAffirmIncomeFee();
                    long canCheckAmount = confirmAmount - ofh.getSourceCheckAmount();
                    OverdueReceivable overdueReceivable = overdueReceivableService.getBusinessCode(String.valueOf(ofh.getOrderId()));
                    if (amount >= canCheckAmount) {
                        /**
                         * ?????????
                         */
                        ofh.setFinanceSts(billSts);
                        ofh.setGetAmount(confirmAmount);
                        overdueReceivable.setPaid(confirmAmount);
                        overdueReceivable.setPayConfirm(1);
                        amount = amount - canCheckAmount;
                        if (SysStaticDataEnum.BILL_STS.CHECK_ALL == billSts) {
                            //????????????????????????????????????????????????6?????????????????????????????????
                            if (badDebt > 0) {
                                if (amount > 0) {
                                    long curBadAmount = Math.round((badDebt / 100.0) * (confirmAmount / 100.0) / (checkAmount / 100.0) * 100);
                                    ofh.setRealIncome(confirmAmount - curBadAmount);
                                    subBadDebt = subBadDebt - curBadAmount;
                                } else {
                                    ofh.setRealIncome(confirmAmount - subBadDebt);
                                }
                            } else {
                                ofh.setRealIncome(confirmAmount);
                            }

                        }
                    } else {
                        /**
                         * ????????????
                         */
                        ofh.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_2);
                        ofh.setGetAmount(amount + ofh.getSourceCheckAmount());
                        overdueReceivable.setPaid(amount + ofh.getSourceCheckAmount());
                        amount = 0;
                    }
                    orderFeeStatementHMapper.updateFeeHByHId(ofh);
                    iOrderOpRecordService.saveOrUpdate(ofh.getOrderId(), 216, accessToken); // ??????
                    try {
                        //??????????????????????????????
                        if (overdueReceivable != null && overdueReceivable.getId() != null) {
                            overdueReceivableService.update(overdueReceivable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (amount > 0) {
            if (orderFeeStatements != null && orderFeeStatements.size() > 0) {
                for (OrderFeeStatement of : orderFeeStatements) {
                    OrderInfoVo orderReceivableIn = new OrderInfoVo();
                    orderReceivableIn.setTenantId(of.getTenantId());
                    orderReceivableIn.setOrderId(of.getOrderId() + "");
                    PageInfo<OrderInfoDto> p = iOrderInfoService.queryReceviceManageOrder(orderReceivableIn, accessToken, 1, 10);
                    long confirmAmount = p.getList().get(0).getAffirmIncomeFee();
                    long canCheckAmount = confirmAmount - of.getSourceCheckAmount();
                    OverdueReceivable overdueReceivable = overdueReceivableService.getBusinessCode(String.valueOf(of.getOrderId()));
                    if (amount >= canCheckAmount) {
                        /**
                         * ?????????
                         */
                        of.setFinanceSts(billSts);
                        of.setGetAmount(confirmAmount);
                        overdueReceivable.setPaid(confirmAmount);
                        overdueReceivable.setPayConfirm(1);
                        amount = amount - canCheckAmount;
                        if (SysStaticDataEnum.BILL_STS.CHECK_ALL == billSts) {
                            //????????????????????????????????????????????????6?????????????????????????????????
                            if (badDebt > 0) {
                                if (amount > 0) {
                                    long curBadAmount = Math.round((badDebt / 100.0) * (confirmAmount / 100.0) / (checkAmount / 100.0) * 100);
                                    of.setRealIncome(confirmAmount - curBadAmount);
                                    subBadDebt = subBadDebt - curBadAmount;
                                } else {
                                    of.setRealIncome(confirmAmount - subBadDebt);
                                }
                            } else {
                                of.setRealIncome(confirmAmount);
                            }
                        }
                    } else {
                        /**
                         * ????????????
                         */
                        of.setFinanceSts(EnumConsts.FINANCE_STS.FINANCE_STS_2);
                        of.setGetAmount(amount + of.getSourceCheckAmount());
                        overdueReceivable.setPaid(amount + of.getSourceCheckAmount());
                        amount = 0;
                    }
                    orderFeeStatementMapper.updateFeeByHId(of);
                    iOrderOpRecordService.saveOrUpdate(of.getOrderId(), 216, accessToken); // ??????
                    try {
                        //??????????????????????????????
                        if (overdueReceivable != null && overdueReceivable.getId() != null) {
                            overdueReceivableService.update(overdueReceivable);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private List<OrderFeeStatement> getOrderFeeStatementByIds(String orderIdsSub) {
        List<OrderFeeStatement> feeStatementByOrderIds = new ArrayList<>();
        feeStatementByOrderIds = orderFeeStatementMapper.getFeeStatementByOrderIds(orderIdsSub);
        List<OrderFeeStatementH> feeStatementHByOrderIds = orderFeeStatementHMapper.getFeeStatementHByOrderIds(orderIdsSub);
        if (feeStatementHByOrderIds != null && feeStatementHByOrderIds.size() > 0) {
            for (OrderFeeStatementH orderFeeStatementH : feeStatementHByOrderIds) {
                OrderFeeStatement orderFeeStatement = new OrderFeeStatement();
                BeanUtils.copyProperties(orderFeeStatementH, orderFeeStatement);
                feeStatementByOrderIds.add(orderFeeStatement);
            }
        }
        return feeStatementByOrderIds;
    }

    // ?????? ?????????????????? ??????BILL ??????
    private void checkOrderCreateBill(String orderIds, String billNumber, Long tenantId) {
        List<Map<String, Object>> lists = new ArrayList<>();
        if (StringUtils.isNotBlank(billNumber)) {
            lists = orderFeeStatementMapper.checkOrderCreateBill(orderIds, tenantId, billNumber);
        } else {
            lists = orderFeeStatementHMapper.checkOrderCreateBill(orderIds, tenantId);
        }

        Map<String, Long> map = new HashMap<String, Long>();
        String oldOrderId = "";
        for (Map<String, Object> os : lists) {
            if (os.get("orderId") != null && os.get("checkName") != null) {
                map.put(os.get("checkName") + "", Long.valueOf(os.get("orderId") + ""));
                if (map.entrySet().size() > 1) {
                    throw new BusinessException("??????[" + os.get("orderId") + "]??????[" + oldOrderId + "]??????????????????????????????BL??????????????????");
                }
                oldOrderId = os.get("orderId") + "";
            }
        }
    }

    // ???????????????????????????????????? ??????????????????BL??????
    private void checkOrderExceptionCreateBill(String orderIdsSub, String billNumber) {
        List<Map<String, Object>> lists = orderProblemInfoMapper.checkOrderExceptionCreateBill(orderIdsSub);
        if (lists != null && lists.size() > 0) {
            String orderIds = "";
            for (Map<String, Object> os : lists) {
                orderIds += "[" + os.get("orderId") + "]";
            }
            if (StringUtils.isEmpty(billNumber)) {
                throw new BusinessException("??????" + orderIds + "???????????????????????????????????????BL????????????");
            } else {
                throw new BusinessException("??????" + orderIds + "????????????????????????????????????????????????[" + billNumber + "]??????");
            }
        }
    }

    /**
     * ??????????????????Long???????????????????????????
     *
     * @param billNumber
     * @return
     */
    public Long getLongBillNumber(String billNumber) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(billNumber)) {
            String number = org.apache.commons.lang3.StringUtils.substringAfter(billNumber, "BL");
            return Long.parseLong(number);
        } else {
            return 0L;
        }
    }

    /**
     * ????????????
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

    private Boolean createOrderBillByExcel(OrederExportBillVo orederExportBillVo) {

        Map<String, String> inBillParams = new HashMap<String, String>(); // ???????????????s
        List<IncomeOrderImportVo> importIns = new ArrayList<IncomeOrderImportVo>(); // ?????????????????????
        List<Long> orderIdsList = new ArrayList<Long>(); // ??????


        return true;
    }

    /**
     * ?????????????????????
     *
     * @return OrderDiffInfo
     */
    private List<OrderDiffInfo> getDiffInfo(long orderIdLong, OrederExportBillVo orederExportBillVo) {
        List<OrderDiffInfo> result = new ArrayList<>();
        OrderDiffInfo billDiffInfo = new OrderDiffInfo();
        billDiffInfo.setOrderId(orderIdLong);
        OrderDiffInfo kpiDiffInfo = new OrderDiffInfo();
        kpiDiffInfo.setOrderId(orderIdLong);
        OrderDiffInfo oilFeeInfo = new OrderDiffInfo();
        oilFeeInfo.setOrderId(orderIdLong);
        OrderDiffInfo billIngDiffInfo = new OrderDiffInfo();
        billIngDiffInfo.setOrderId(orderIdLong);
        OrderDiffInfo otherDiffInfo = new OrderDiffInfo();
        otherDiffInfo.setOrderId(orderIdLong);

        String billDiff = orederExportBillVo.getBillDiff();
        if (billDiff.trim() != null
                && !StringUtils.equals("null", billDiff.trim())) {
            billDiffInfo.setDiffFee(Math.round(Double.parseDouble(billDiff.trim()) * 100));
        } else {
            billDiffInfo.setDiffFee(0L);
        }
        billDiffInfo.setDiffDesc(orederExportBillVo.getBillDiffRemark());
        billDiffInfo.setDiffType(1);
        result.add(billDiffInfo);

        String kpiDiff = orederExportBillVo.getKpiDiff();
        if (kpiDiff.trim() != null
                && !StringUtils.equals("null", kpiDiff.trim())) {
            kpiDiffInfo.setDiffFee(Math.round(Double.parseDouble(kpiDiff.trim()) * 100));
        } else {
            kpiDiffInfo.setDiffFee(0L);
        }
        kpiDiffInfo.setDiffDesc(orederExportBillVo.getKpiDiffRemark());
        kpiDiffInfo.setDiffType(2);
        result.add(kpiDiffInfo);

        String oilFeeDiff = orederExportBillVo.getOilFeeDiff();
        if (oilFeeDiff.trim() != null
                && !StringUtils.equals("null", oilFeeDiff.trim())) {
            oilFeeInfo.setDiffFee(Math.round(Double.parseDouble(oilFeeDiff.trim()) * 100));
        } else {
            oilFeeInfo.setDiffFee(0L);
        }
        oilFeeInfo.setDiffDesc(orederExportBillVo.getOilFeeDiffRemark());
        oilFeeInfo.setDiffType(3);
        result.add(oilFeeInfo);

        String billIngDiff = orederExportBillVo.getBillIngDiff();
        if (billIngDiff.trim() != null
                && !StringUtils.equals("null", billIngDiff.trim())) {
            billIngDiffInfo.setDiffFee(Math.round(Double.parseDouble(billIngDiff.trim()) * 100));
        } else {
            billIngDiffInfo.setDiffFee(0L);
        }
        billIngDiffInfo.setDiffDesc(orederExportBillVo.getBillIngDiffRemark());
        billIngDiffInfo.setDiffType(4);
        result.add(billIngDiffInfo);

        String otherDiff = orederExportBillVo.getOtherDiff();
        if (otherDiff.trim() != null
                && !StringUtils.equals("null", otherDiff.trim())) {
            otherDiffInfo.setDiffFee(Math.round(Double.parseDouble(otherDiff.trim()) * 100));
        } else {
            otherDiffInfo.setDiffFee(0L);
        }
        otherDiffInfo.setDiffDesc(orederExportBillVo.getOtherDiffRemark());
        otherDiffInfo.setDiffType(5);
        result.add(otherDiffInfo);

        return result;
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

}
