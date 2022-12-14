package com.youming.youche.finance.business.controller.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.finance.api.order.IOrderBillInfoService;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.domain.order.OrderBillInfo;
import com.youming.youche.finance.dto.order.OrderBillCheckInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInvoiceDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.vo.order.CheckDatas;
import com.youming.youche.finance.vo.order.OrderBillInfoVo;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.SaveChecksVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ????????????
 *
 * @author hzx
 * @date 2022/2/8 15:50
 */
@RestController
@RequestMapping("order/bill/info")
public class OrderBillInfoController extends BaseController<OrderBillInfo, IOrderBillInfoService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInfoController.class);

    @DubboReference(version = "1.0.0")
    IOrderBillInfoService iOrderBillInfoService;

    @DubboReference(version = "1.0.0")
    IOrderInfoThreeService orderInfoThreeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IOrderBillInfoService getService() {
        return iOrderBillInfoService;
    }

    /**
     * ??????
     */
    @GetMapping("doQuery")
    public ResponseResult doQuery(OrderBillInfoVo orderBillInfoVO,
                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderBillInfoDto> objectPage = new Page<>(pageNum, pageSize);
        Page<OrderBillInfoDto> orderBillInfoDtoPage = iOrderBillInfoService.doQuery(objectPage, orderBillInfoVO, accessToken);
        return ResponseResult.success(orderBillInfoDtoPage);

    }

    /**
     * ??????????????????????????????
     *
     * @param billNumber ?????????
     */
    @PostMapping("queryChecksByBillNumber")
    public ResponseResult queryChecksByBillNumber(@RequestParam("billNumber") String billNumber) {

        if (StringUtils.isBlank(billNumber)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderBillCheckInfoDto> orderBillCheckInfos = iOrderBillInfoService.queryChecksByBillNumber(billNumber, accessToken);
        return ResponseResult.success(orderBillCheckInfos);

    }

    /**
     * ??????????????????
     */
    @PostMapping("saveChecks")
    public ResponseResult saveChecks(@RequestBody SaveChecksVo saveChecksVo) {

        if (StringUtils.isBlank(saveChecksVo.getBillNumber())) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (saveChecksVo.getLists().size() == 0) {
            throw new BusinessException("?????????????????????");
        }

        List<OrderBillCheckInfo> orderBillCheckInfos = new ArrayList<OrderBillCheckInfo>();
        //???????????????
        for (CheckDatas d : saveChecksVo.getLists()) {
            OrderBillCheckInfo c = new OrderBillCheckInfo();
            c.setCheckDesc(d.getCheckDesc());
            c.setCheckType(d.getCheckType());
            c.setFileIds(d.getFileIds());
            c.setFileUrls(d.getFileUrls());

            c.setId(null);
            if ("null".equals(c.getFileIds()) || "\"null\"".equals(c.getFileIds())) {
                c.setFileIds("");
                c.setFileUrls("");
            }
            if ("null".equals(c.getCheckDesc()) || "\"null\"".equals(c.getCheckDesc())) {
                c.setCheckDesc("");
            }

            String fee = d.getCheckFeeDouble();
            if (StringUtils.isEmpty(fee)) {
                fee = "0";
            }
            if (!CommonUtil.isNumber(fee)) {
                String checkTypeName = d.getCheckTypeName();
                throw new BusinessException("??????[" + saveChecksVo.getBillNumber() + "]??????[" + checkTypeName + "]?????????????????????");
            }
            long checkFee = Math.round(Double.valueOf(fee) * 100);
            c.setCheckFee(checkFee);
            c.setBillNumber(saveChecksVo.getBillNumber());
            orderBillCheckInfos.add(c);
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.saveChecks(saveChecksVo.getBillNumber(), orderBillCheckInfos, accessToken);
        return ResponseResult.success();

    }

    /**
     * ????????????????????????
     */
    @GetMapping("queryBillReceipt")
    public ResponseResult queryBillReceipt(@RequestParam("billNumber") String billNumber) {

        if (StringUtils.isBlank(billNumber)) {
            throw new BusinessException("?????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderBillInvoiceDto> orderBillInvoiceDtos = iOrderBillInfoService.queryBillReceipt(billNumber, accessToken);
        return ResponseResult.success(orderBillInvoiceDtos);

    }

    /**
     * ????????????
     */
    @PostMapping("saveBillSts")
    public ResponseResult saveBillSts(@RequestParam("billNumberStr") String billNumberStr) {

        if (StringUtils.isBlank(billNumberStr)) {
            throw new BusinessException("?????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.saveBillSts(billNumberStr, accessToken);
        return ResponseResult.success();

    }

    /**
     * ??????????????????
     */
    @PostMapping("saveOrderBillReceipt")
    public ResponseResult saveOrderBillReceipt(@RequestParam("billNumber") String billNumber,
                                               @RequestParam("invoiceList") String invoiceList,
                                               @RequestParam("amountList") String amountList) {

        if (StringUtils.isBlank(billNumber)) {
            throw new BusinessException("?????????????????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.saveOrderBillReceipt(billNumber, invoiceList, amountList, accessToken);
        return ResponseResult.success();

    }

    /**
     * ??????
     */
    @PostMapping("uodoBill")
    public ResponseResult uodoBill(@RequestParam("billNumberStr") String billNumberStr) {

        if (StringUtils.isBlank(billNumberStr)) {
            throw new BusinessException("??????????????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.undoBill(billNumberStr, accessToken);
        return ResponseResult.success();

    }

    /**
     * ??????????????????
     */
    @PostMapping("billAddOrders")
    public ResponseResult billAddOrders(@RequestParam("billNumber") String billNumber,
                                        @RequestParam("orderIds") String orderIds) {

        if (StringUtils.isEmpty(billNumber)) {
            throw new BusinessException("?????????????????????");
        }
        if (StringUtils.isEmpty(orderIds)) {
            throw new BusinessException("?????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.billAddOrders(billNumber, orderIds, accessToken);
        return ResponseResult.success();

    }

    /**
     * ??????????????????
     */
    @PostMapping("billReduceOrders")
    public ResponseResult billReduceOrders(@RequestParam("billNumber") String billNumber,
                                           @RequestParam("orderIds") String orderIds) {

        if (StringUtils.isBlank(billNumber)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }
        if (StringUtils.isBlank(orderIds)) {
            throw new BusinessException("????????????????????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderBillInfoService.billReduceOrders(billNumber, orderIds, accessToken);
        return ResponseResult.success();

    }

    /**
     * ????????????
     */
    @PostMapping("createOrderBill")
    public ResponseResult createOrderBill(@RequestParam("orderIds") String orderIds) {

        if (StringUtils.isBlank(orderIds)) {
            throw new BusinessException("??????????????????????????????????????????");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String billNumber = iOrderBillInfoService.createBill(orderIds, accessToken);
        return ResponseResult.success(billNumber);

    }

    /**
     * ??????????????????
     */
    @PostMapping("createBillQuickly")
    public ResponseResult createBillQuickly(OrderInfoVo orderInfoVo,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        Page<OrderInfoDto> objectPage = new Page<>(pageNum, pageSize);
        PageInfo<OrderInfoDto> p = orderInfoThreeService.queryReceviceManageOrder( orderInfoVo, accessToken,pageNum,pageSize);
        List<OrderInfoDto> list = p.getList();
        StringBuilder orderIds = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (OrderInfoDto out : list) {
                orderIds.append(out.getOrderId());
                orderIds.append(",");
            }
        }
        if (StringUtils.isBlank(orderIds.toString())) {
            throw new BusinessException("?????????????????????");
        }

        String billNumber = iOrderBillInfoService.createBill(orderIds.toString(), accessToken);
        return ResponseResult.success(billNumber);

    }

    /**
     * ??????????????????????????????
     */
    @PostMapping("createOrderBillByExcel")
    public ResponseResult createOrderBillByExcel(@RequestParam("file") MultipartFile file) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows == 1) {
                throw new BusinessException("??????????????????0???????????????");
            }
            if (rows > 300) {
                throw new BusinessException("?????????????????????300???????????????????????????[" + rows + "]");
            }
            if (!"?????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 1)))
                throw new BusinessException("??????????????????");
            if (!"?????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 2)))
                throw new BusinessException("??????????????????");
            if (!"????????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 3)))
                throw new BusinessException("??????????????????");
            if (!"??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 4)))
                throw new BusinessException("??????????????????");
            if (!"KPI??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 5)))
                throw new BusinessException("??????????????????");
            if (!"??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 6)))
                throw new BusinessException("??????????????????");
            if (!"????????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 7)))
                throw new BusinessException("??????????????????");
            if (!"??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 8)))
                throw new BusinessException("??????????????????");
            if (!"????????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 9)))
                throw new BusinessException("??????????????????");
            if (!"??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 10)))
                throw new BusinessException("??????????????????");
            if (!"????????????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 11)))
                throw new BusinessException("??????????????????");
            if (!"??????".equals(parse.readExcelByRowAndCell(sheetNo, 1, 12)))
                throw new BusinessException("??????????????????");

            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(file.getInputStream(), file.getOriginalFilename(), file.getSize());

            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("??????????????????????????????");
            record.setMediaName(file.getOriginalFilename());
            record.setMediaUrl(path);
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderBillInfoService.createOrderBillByExcel(file.getBytes(), file.getOriginalFilename(), record, accessToken);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????????????????????????????" + e);
            return ResponseResult.failure("?????????????????????????????????");
        }
    }

    //???????????????
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????
     */
    @GetMapping("exportQuery")
    public ResponseResult exportQuery(@RequestParam("billNumbers") String billNumbers, @RequestParam("customNames") String customNames) {
        if (StringUtils.isBlank(billNumbers)) {
            throw new BusinessException("??????????????????");
        }
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("??????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderBillInfoService.exportQuery(billNumbers, customNames, accessToken, record);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????" + e);
            return ResponseResult.failure("????????????????????????????????????");
        }
    }

}
