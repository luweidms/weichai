package com.youming.youche.market.business.controller.facilitator;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.market.api.facilitator.IConsumeOilFlowService;
import com.youming.youche.market.api.youka.IVoucherInfoService;
import com.youming.youche.market.domain.facilitator.OilTransaction;
import com.youming.youche.market.dto.facilitator.ServiceUnexpiredDetailDto;
import com.youming.youche.market.dto.youca.ConsumeOilFlowDto;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordOut;
import com.youming.youche.market.vo.youca.ConsumeOilFlowVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
@RestController
@RequestMapping("/facilitator/consume/oil/flow")
public class ConsumeOilFlowController  {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumeOilFlowController.class);
    @DubboReference(version = "1.0.0")
    IConsumeOilFlowService consumeOilFlowService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    HttpServletRequest request;
    public IConsumeOilFlowService getService(){
        return consumeOilFlowService;
    }

    /**
     * 查询油站校验记录
     * @return
     */
    @GetMapping("queryOilTransaction")
    public ResponseResult queryOilTransaction(OilTransaction oilTransaction,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

       return ResponseResult.success(consumeOilFlowService.queryConsumeOilFlow(pageSize, pageNum, oilTransaction, accessToken));
    }

    /**
     *统计交易合计
     * @return
     */
    @GetMapping("totalOilTransaction")
    public ResponseResult totalOilTransaction(OilTransaction oilTransaction,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(consumeOilFlowService.totalOilTransaction(pageSize, pageNum,oilTransaction, accessToken));
    }

    /**
     * 交易记录导出
     * @param oilTransaction
     * @return
     */
    @GetMapping("export")
    public ResponseResult export(OilTransaction oilTransaction) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("交易记录信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            consumeOilFlowService.queryConsumeOilFlowExport(record,oilTransaction.getOrderId(),
                    oilTransaction.getTradeTimeStart(), oilTransaction.getTradeTimeEnd(),
                    oilTransaction.getConsumerName(), oilTransaction.getConsumerBill(),
                    oilTransaction.getSettlTimeStart(), oilTransaction.getSettlTimeEnd(), oilTransaction.getState(), oilTransaction.getProductId(), accessToken,oilTransaction.getServiceType());
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            return ResponseResult.failure("导出成功交易记录异常");
        }
    }


    @DubboReference(version = "1.0.0")
    IVoucherInfoService iVoucherInfoService;

    /**
     * 油卡消费记录列表
     *
     * @param pageNum          分页参数
     * @param pageSize         分页参数
     * @param consumeOilFlowVo 油卡消费记录查询传入参数
     */
    @GetMapping("getOilConsumeRecords")
    public ResponseResult getOilConsumeRecords(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                               ConsumeOilFlowVo consumeOilFlowVo){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            if(consumeOilFlowVo.getFromType()==4){//扫码加油
                Page<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> page = new Page<>(pageNum, pageSize);
                IPage<ConsumeOilFlowDto> page1=  consumeOilFlowService.queryConsumeOilFlowList(page,consumeOilFlowVo,accessToken);
                return page == null ? null : ResponseResult.success(page1);
            }else{
                IPage<RechargeConsumeRecordOut> rechargeConsumeRecords = iVoucherInfoService.getRechargeConsumeRecords(consumeOilFlowVo.getOrderId(),
                        "", consumeOilFlowVo.getRecordStartTime(), consumeOilFlowVo.getRecordEndTime(),
                        "2", consumeOilFlowVo.getCardType(), "", consumeOilFlowVo.getCardNum(),
                        consumeOilFlowVo.getVoucherId(), consumeOilFlowVo.getPlateNumber(), consumeOilFlowVo.getFromType(),
                        consumeOilFlowVo.getServiceName(), consumeOilFlowVo.getRebate(), accessToken, consumeOilFlowVo.getAddress(),
                        consumeOilFlowVo.getDealRemark(), pageNum, pageSize);
                return ResponseResult.success(rechargeConsumeRecords);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }


    /**
     * 油卡充值/消费记录 统计 (服务商)
     *  startTime 记录开始 yyyy-MM-dd
     *  endTime 记录结束 yyyy-MM-dd
     *  recordType 记录类型：1充值，2油卡消费
     *  cardType 卡类型：1中石油，2中石化
     *  tenantName 车队名称
     *  cardNum 油卡卡号
     */
    @GetMapping("sumOilConsumeRecords")
    public ResponseResult sumOilConsumeRecords( ConsumeOilFlowVo consumeOilFlowVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(consumeOilFlowVo.getFromType()==4){//扫码加油
            return ResponseResult.success(consumeOilFlowService.sumConsumeOilFlow(consumeOilFlowVo,accessToken));
        }else{
            return ResponseResult.success(iVoucherInfoService.sumRechargeConsumeRecords(consumeOilFlowVo.getOrderId(),
                    "", consumeOilFlowVo.getRecordStartTime(), consumeOilFlowVo.getRecordEndTime(),
                    "2" , consumeOilFlowVo.getCardType(), "", consumeOilFlowVo.getCardNum(),
                    consumeOilFlowVo.getVoucherId(), consumeOilFlowVo.getPlateNumber(),
                    consumeOilFlowVo.getFromType(), consumeOilFlowVo.getServiceName(),
                    consumeOilFlowVo.getRebate(), accessToken, consumeOilFlowVo.getAddress(),
                    consumeOilFlowVo.getDealRemark()));
        }
    }

    /**
     * 油卡消费记录导出
     * @param consumeOilFlowVo
     * @return
     */
    @GetMapping("exportConsume")
    public ResponseResult exportConsume(ConsumeOilFlowVo consumeOilFlowVo,
                                        @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("油卡消费记录导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            consumeOilFlowService.queryAllListExport(accessToken, record,consumeOilFlowVo,pageNum,pageSize);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            return ResponseResult.failure("导出油卡消费记录异常");
        }
    }

    /***
     * 中石化记录导入
     * @return
     * @throws Exception
     */
    @PostMapping("import")
    public ResponseResult driverImport(@RequestParam("file") MultipartFile file) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "中石化记录列表.xls", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("中石化记录导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            consumeOilFlowService.batchImport(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导入中石化记录异常" + e);
            return ResponseResult.failure("文件导入失败，请重试！");
        }
    }
    /***
     * 中石油记录导入
     * @return
     * @throws Exception
     */
    @PostMapping("importYou")
    public ResponseResult importYou(@RequestParam("file") MultipartFile file) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数

            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "中石油记录.xls", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("中石油记录导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            consumeOilFlowService.importYou(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导入中石化记录异常" + e);
            return ResponseResult.failure("文件导入失败，请重试！");
        }
    }
    //获取流文件
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
     * 微信接口-服务商-即将到期明细
     *
     * @param orderId        交易流水号
     * @param name           来源车队
     * @param sourceTenantId 来源车队ID
     * @param pageNum        分页参数
     * @param pageSize       分页参数
     */
    @GetMapping("/getServiceUnexpiredDetail")
    public ResponseResult getServiceUnexpiredDetail(String orderId, String name, String sourceTenantId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<ServiceUnexpiredDetailDto> page = consumeOilFlowService.getServiceUnexpiredDetail(orderId, name, sourceTenantId, accessToken, pageNum, pageSize);
        return ResponseResult.success(page);
    }
    /**
     *
     * 聂杰伟
     * 统计支付次数
     * @return
     */
    @GetMapping("paymentTimes")
    public ResponseResult scanCodePayment() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Integer aLong = consumeOilFlowService.paymentTimes(accessToken);
        return ResponseResult.success(aLong);
    }
}
