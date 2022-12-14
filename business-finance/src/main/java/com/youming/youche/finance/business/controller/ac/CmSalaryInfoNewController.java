package com.youming.youche.finance.business.controller.ac;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.api.ac.ICmSalaryTemplateService;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.domain.ac.CmTemplateField;
import com.youming.youche.finance.dto.CmSalaryInfoDto;
import com.youming.youche.finance.dto.CmSalaryOrderInfoDto;
import com.youming.youche.finance.dto.CompSalaryTemplateDto;
import com.youming.youche.finance.dto.SaveCmSalaryTemplateDto;
import com.youming.youche.finance.dto.SubsidyInfoDto;
import com.youming.youche.finance.dto.ac.CmSalaryComplainNewDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoNewQueryDto;
import com.youming.youche.finance.dto.order.OrderListOutDto;
import com.youming.youche.finance.vo.ModifySalaryInfoVo;
import com.youming.youche.finance.vo.ac.CmSalaryInfoNewQueryVo;
import com.youming.youche.finance.vo.ac.OrderListInVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
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

import java.util.List;

/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author zengwen
 * @since 2022-04-18
 */
@RestController
@RequestMapping("cm/salary/info/new")
public class CmSalaryInfoNewController extends BaseController<CmSalaryInfoNew, ICmSalaryInfoNewService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmSalaryInfoNewController.class);

    @DubboReference(version = "1.0.0")
    ICmSalaryInfoNewService cmSalaryInfoNewService;

    @DubboReference(version = "1.0.0")
    ICmSalaryTemplateService cmSalaryTemplateService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public ICmSalaryInfoNewService getService() {
        return cmSalaryInfoNewService;
    }

    /**
     * ???????????????????????????
     */
    @PostMapping("/query")
    public ResponseResult queryCmSalaryInfoNew(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewQueryVo.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        Page<CmSalaryInfoNewQueryDto> page = cmSalaryInfoNewService.queryCmSalaryInfoNew(cmSalaryInfoNewQueryVo, accessToken, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * ??????????????????????????????
     */
    @GetMapping("/getMaxTemplateFiel")
    public ResponseResult getMaxTemplateFiel() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<CmTemplateField> list = cmSalaryTemplateService.getMaxTemplateFiel(accessToken);
        return ResponseResult.success(list);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("/getTemplateAllField")
    public ResponseResult getTemplateAllField(String settleMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<CmTemplateField> list = cmSalaryTemplateService.getTemplateAllField(accessToken, settleMonth);
        return ResponseResult.success(list);
    }

    /**
     * ???????????????
     *
     * @return
     * @throws Exception
     */
    @PostMapping("sendBillBySalary")
    public ResponseResult sendBillBySalary(String salaryIds ) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.sendBillBySalary(salaryIds, accessToken);
        return ResponseResult.success();
    }


    /**
     * ????????????????????????
     *
     * @param id
     * @param tenantId
     * @return
     */
    @GetMapping("/getSubsidyInfo")
    public ResponseResult getSubsidyInfo(Long id, Long tenantId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        SubsidyInfoDto subsidyInfo = cmSalaryInfoNewService.getSubsidyInfo(id, tenantId, accessToken);
        return ResponseResult.success(subsidyInfo);
    }

    /**
     * ????????????
     *
     * @param orderId
     * @param salary
     * @param salaryId
     * @param paidSalaryFee
     * @return
     */
    @PostMapping("/balanceSubsidy")
    public ResponseResult balanceSubsidy(Long orderId, double salary, Long salaryId, Double paidSalaryFee) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = cmSalaryInfoNewService.balanceSubsidy(orderId,salary,salaryId,paidSalaryFee,accessToken);
        return i > 0 ? ResponseResult.success("????????????") : ResponseResult.failure("????????????");
    }

    /**
     * ??????????????????
     *
     * @param templateMonth
     * @return
     */
    @PostMapping("/compSalaryTemplate")
    public ResponseResult compSalaryTemplate(String templateMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<CompSalaryTemplateDto> compSalaryTemplateDto = cmSalaryInfoNewService.compSalaryTemplate(templateMonth, accessToken);
        return ResponseResult.success(compSalaryTemplateDto);
    }

    /**
     * ??????????????????
     * @return
     */
//    @PostMapping("/statisticsMenuTab")
//    public ResponseResult statisticsMenuTab(String name){
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        cmSalaryInfoNewService.statisticsMenuTab(name,accessToken);
//        return ResponseResult.success();
//    }

    /**
     * ??????-??????????????????
     *
     * @param saveCmSalaryTemplateDto
     * @return
     */
    @PostMapping("/doSaveCmSalaryTemplate")
    public ResponseResult doSaveCmSalaryTemplate(@RequestBody List<SaveCmSalaryTemplateDto> saveCmSalaryTemplateDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        Map<String,Object> inParam=new HashMap<>();
//        inParam.put("cmTemplateFieldList",saveCmSalaryTemplateDto);
        cmSalaryInfoNewService.doSaveCmSalaryTemplate(saveCmSalaryTemplateDto, accessToken);
        return ResponseResult.success();
    }

    /**
     * ??????????????????
     */
    //@PostMapping("opBillByExcel")
    public ResponseResult opBillByExcel(Integer operType, MultipartFile file, String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ExcelParse parse = new ExcelParse();
        try {
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            //        // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("?????????????????????300?????????");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "????????????.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("??????????????????");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.batchImport(file.getBytes(), record, operType, accessToken, orderId);
            return ResponseResult.success("???????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            throw new BusinessException("????????????????????????");
        }
    }


    //@PostMapping("checkSalaryInfoByExcel")
    public ResponseResult checkSalaryInfoByExcel(Integer operType, MultipartFile file, String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ExcelParse parse = new ExcelParse();
        try {
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            //        // ??????????????????
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("?????????????????????300?????????");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "????????????.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("??????????????????");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.batchImports(file.getBytes(), record, operType, accessToken, orderId);
            return ResponseResult.success("???????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            throw new BusinessException("????????????????????????");
        }
    }

    /**
     * ??????????????????
     *
     * @param id
     * @param subsidy
     * @return
     */
    @PostMapping("/amendSubsidy")
    public ResponseResult amendSubsidy(Long id,Double subsidy) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = cmSalaryInfoNewService.changeSubsidy(id, subsidy, accessToken);
        return i > 0 ? ResponseResult.success("????????????") : ResponseResult.failure("????????????");
    }

    /**
     * ??????????????????
     *
     * @param modifySalaryInfoVo
     * @return
     */
    @PostMapping("/modifySalaryInfo")
    public ResponseResult modifySalaryInfo(ModifySalaryInfoVo modifySalaryInfoVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.modifySalaryInfo(modifySalaryInfoVo, accessToken);
        return ResponseResult.success();
    }

    /**
     * ???????????????
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("??????????????????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.downloadExcelFile(cmSalaryInfoNewQueryVo, accessToken, record);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????" + e);
            return ResponseResult.failure("??????????????????????????????");
        }
    }

    /**
     * ???????????????????????????
     */
    @PostMapping("/getSalaryOrders")
    public ResponseResult getSalaryOrders(OrderListInVo orderListInVo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<OrderListOutDto> page = cmSalaryInfoNewService.getSalaryOrder(orderListInVo, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * ??????-??????
     */
    /**
     * ????????????
     *
     * @return
     * @throws Exception
     */
    @PostMapping("checkedSalaryBill")
    public String checkedSalaryBill(String salaryId, String verificationSalary, String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return cmSalaryInfoNewService.checkedSalaryBill(salaryId, Math.round(Double.parseDouble(verificationSalary) * 100), accessToken, orderId);
    }

    /**
     * ??????????????????(70075)
     *
     * @return
     */
    @PostMapping("salaryAffirm")
    public ResponseResult salaryAffirm(Long salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (salaryId == null || salaryId <= 0L) {
            throw new BusinessException("?????????????????????");
        }
        cmSalaryInfoNewService.salaryAffirm(salaryId, accessToken);
        return ResponseResult.success("Y");
    }

    /**
     * ??????????????????
     */
    @PostMapping("salaryGoTo")
    public ResponseResult SalaryGoTo(String orderId, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryGoTo(orderId, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ????????????????????????
     */
    @PostMapping("salaryGoTos")
    public ResponseResult SalaryGoTos(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryGoTos(orderIds, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????????????????
     */
    @PostMapping("salaryConfirm")
    public ResponseResult SalaryConfirm(String orderId, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryConfirm(orderId, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ????????????????????????
     */
    @PostMapping("salaryConfirms")
    public ResponseResult SalaryConfirms(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryConfirms(orderIds, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????????????????
     */
    @PostMapping("salarySettlement")
    public ResponseResult SalarySettlement(String orderId, Long salaryFee, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalarySettlement(orderId, salaryFee, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ????????????????????????
     */
    @PostMapping("salarySettlements")
    public ResponseResult SalarySettlements(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalarySettlements(orderIds, salaryId, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ???????????????
     */
    @PostMapping("sendBillToBySalary")
    public ResponseResult sendBillToBySalary(String salaryIds, Long verificationSalary,String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.sendBillToBySalary(salaryIds, verificationSalary, accessToken,orderId);
        return ResponseResult.success("????????????");
    }

    /**
     * ?????????????????????
     */
    @PostMapping("sendBillToBySalarys")
    public ResponseResult sendBillToBySalarys(String salaryIds) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.sendBillToBySalarys(salaryIds, accessToken);
        return ResponseResult.success("????????????");
    }

    /**
     * ??????????????????
     *
     * @param templateMonth
     * @return
     */
    @PostMapping("updateTemplate")
    public ResponseResult updateTemplate(String templateMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.updateCmSalaryTemplate(templateMonth, accessToken);
        return ResponseResult.success("????????????????????????");
    }

    /**
     * ????????????????????????
     */
    @GetMapping("doQuerySalaryComplain")
    public ResponseResult doQuerySalaryComplain(Long salaryId,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<CmSalaryComplainNewDto> cmSalaryComplainNewDtoPage = cmSalaryInfoNewService.doQuerySalaryComplain(salaryId, pageNum, pageSize, accessToken);
        return ResponseResult.success(cmSalaryComplainNewDtoPage);
    }

    /**
     * ??????????????????
     */
    @PostMapping("/downloadQuerySalaryComplainExcelFile")
    public ResponseResult downloadQuerySalaryComplainExcelFile(Long salaryId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("?????????????????????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.downloadQuerySalaryComplainExcelFile(salaryId, accessToken, record);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????" + e);
            return ResponseResult.failure("??????????????????????????????");
        }
    }

    /**
     * ??????????????????
     */
    @PostMapping("/checkSalaryComplain")
    public ResponseResult checkSalaryComplain(Long id, Integer checkState, String checkResult) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.checkSalaryComplain(id, checkState, checkResult, accessToken);
        return ResponseResult.success();
    }

    /**
     * 22030 APP-????????????????????????
     *
     * @param cmSalaryInfoDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("queryCmSalaryInfo")
    public ResponseResult queryCmSalaryInfo(CmSalaryInfoDto cmSalaryInfoDto,
                                            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                cmSalaryInfoNewService.queryCmSalaryInfo(cmSalaryInfoDto, pageNum, pageSize, accessToken)
        );
    }

    /**
     * ?????????????????????????????????
     */
    @GetMapping("queryCmSalaryInfoNew")
    public ResponseResult queryCmSalaryInfoNew(Long salaryId,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                cmSalaryInfoNewService.queryCmSalaryInfoNew(accessToken, pageNum, pageSize, salaryId)
        );
    }

    /**
     * ????????? ???????????????????????????????????????
     * @param sendId
     * @return
     */
    @PostMapping("confirmSalarySendOrder")
    public ResponseResult confirmSalarySendOrder(Long sendId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.confirmSalarySendOrder(sendId, accessToken);
        return ResponseResult.success();
    }



    /**
     * 22031 ?????????-??????????????????
     *
     * @param salaryId
     * @return
     */
    @GetMapping("queryCmSalaryDetail")
    public ResponseResult queryCmSalaryDetail(Long salaryId) {
        if (salaryId < 0) {
            throw new BusinessException("??????????????????");
        }
        return ResponseResult.success(
                cmSalaryInfoNewService.queryCmSalaryDetail(salaryId)
        );
    }

    /**
     * 22033   ?????????-??????????????????
     *
     * @param salaryId
     * @param complainType
     * @param complainReason
     * @return
     */
    @GetMapping("doCmSalaryComplain")
    public ResponseResult doCmSalaryComplain(Long salaryId, String complainType, String complainReason) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                cmSalaryInfoNewService.oCmSalaryComplain(salaryId, complainType, complainReason, accessToken)
        );
    }

    /**
     * 22035 APP-????????????????????????
     *
     * @param cmSalaryOrderInfoDto
     * @return
     */
    @GetMapping("getCmSalaryOrderInfo")
    public ResponseResult getCmSalaryOrderInfo(CmSalaryOrderInfoDto cmSalaryOrderInfoDto) {
        return ResponseResult.success(
                cmSalaryInfoNewService.getCmSalaryOrderInfo(cmSalaryOrderInfoDto)
        );
    }

    /**
     * ???????????????????????????
     */
    @GetMapping("getCmSalaryOrderInfoNew")
    public ResponseResult getCmSalaryOrderInfoNew(Long sendId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderListOutDto> list = cmSalaryInfoNewService.getCmSalaryOrderInfoNew(sendId, accessToken);
        return ResponseResult.success(list);
    }

    /**
     *22036  APP-??????????????????
     * @param salaryId
     * @param channelType
     * @return
     */
    @GetMapping("getDriverSubsidyInfo")
    public ResponseResult getDriverSubsidyInfo(Long salaryId, String channelType) {
        return ResponseResult.success(
                cmSalaryInfoNewService.getDriverSubsidyInfo(salaryId, channelType)
        );
    }

}
