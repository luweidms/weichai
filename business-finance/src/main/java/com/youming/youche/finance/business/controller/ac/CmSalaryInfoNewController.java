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
 * 前端控制器
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
     * 分页查询司机工资单
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
     * 获取最大的自定义模板
     */
    @GetMapping("/getMaxTemplateFiel")
    public ResponseResult getMaxTemplateFiel() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<CmTemplateField> list = cmSalaryTemplateService.getMaxTemplateFiel(accessToken);
        return ResponseResult.success(list);
    }

    /**
     * 获取自定义模板
     */
    @GetMapping("/getTemplateAllField")
    public ResponseResult getTemplateAllField(String settleMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<CmTemplateField> list = cmSalaryTemplateService.getTemplateAllField(accessToken, settleMonth);
        return ResponseResult.success(list);
    }

    /**
     * 发送工资单
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
     * 获取结算补贴信息
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
     * 结算补贴
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
        return i > 0 ? ResponseResult.success("结算成功") : ResponseResult.failure("结算失败");
    }

    /**
     * 更新工资模板
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
     * 自定义工资单
     * @return
     */
//    @PostMapping("/statisticsMenuTab")
//    public ResponseResult statisticsMenuTab(String name){
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        cmSalaryInfoNewService.statisticsMenuTab(name,accessToken);
//        return ResponseResult.success();
//    }

    /**
     * 确定-自定义工资单
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
     * 导入发送账单
     */
    //@PostMapping("opBillByExcel")
    public ResponseResult opBillByExcel(Integer operType, MultipartFile file, String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ExcelParse parse = new ExcelParse();
        try {
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            //        // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "客户信息.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("导入发送账单");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.batchImport(file.getBytes(), record, operType, accessToken, orderId);
            return ResponseResult.success("正在导文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            throw new BusinessException("导入发送账单异常");
        }
    }


    //@PostMapping("checkSalaryInfoByExcel")
    public ResponseResult checkSalaryInfoByExcel(Integer operType, MultipartFile file, String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ExcelParse parse = new ExcelParse();
        try {
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            //        // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "客户信息.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("导入发送账单");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.batchImports(file.getBytes(), record, operType, accessToken, orderId);
            return ResponseResult.success("正在导文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            throw new BusinessException("导入发送账单异常");
        }
    }

    /**
     * 修改补贴金额
     *
     * @param id
     * @param subsidy
     * @return
     */
    @PostMapping("/amendSubsidy")
    public ResponseResult amendSubsidy(Long id,Double subsidy) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        int i = cmSalaryInfoNewService.changeSubsidy(id, subsidy, accessToken);
        return i > 0 ? ResponseResult.success("修改成功") : ResponseResult.failure("修改失败");
    }

    /**
     * 修改工资信息
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
     * 导出工资单
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("司机工资报表数据导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.downloadExcelFile(cmSalaryInfoNewQueryVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }

    /**
     * 获取工资单补贴明细
     */
    @PostMapping("/getSalaryOrders")
    public ResponseResult getSalaryOrders(OrderListInVo orderListInVo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<OrderListOutDto> page = cmSalaryInfoNewService.getSalaryOrder(orderListInVo, pageNum, pageSize);
        return ResponseResult.success(page);
    }

    /**
     * 结算-单个
     */
    /**
     * 勾选核销
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
     * 工资确认接口(70075)
     *
     * @return
     */
    @PostMapping("salaryAffirm")
    public ResponseResult salaryAffirm(Long salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (salaryId == null || salaryId <= 0L) {
            throw new BusinessException("工资编号不正确");
        }
        cmSalaryInfoNewService.salaryAffirm(salaryId, accessToken);
        return ResponseResult.success("Y");
    }

    /**
     * 补贴明细发送
     */
    @PostMapping("salaryGoTo")
    public ResponseResult SalaryGoTo(String orderId, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryGoTo(orderId, salaryId, accessToken);
        return ResponseResult.success("发送成功");
    }

    /**
     * 补贴明细批量发送
     */
    @PostMapping("salaryGoTos")
    public ResponseResult SalaryGoTos(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryGoTos(orderIds, salaryId, accessToken);
        return ResponseResult.success("发送成功");
    }

    /**
     * 补贴明细确认
     */
    @PostMapping("salaryConfirm")
    public ResponseResult SalaryConfirm(String orderId, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryConfirm(orderId, salaryId, accessToken);
        return ResponseResult.success("确认成功");
    }

    /**
     * 补贴明细批量确认
     */
    @PostMapping("salaryConfirms")
    public ResponseResult SalaryConfirms(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalaryConfirms(orderIds, salaryId, accessToken);
        return ResponseResult.success("确认成功");
    }

    /**
     * 补贴明细结算
     */
    @PostMapping("salarySettlement")
    public ResponseResult SalarySettlement(String orderId, Long salaryFee, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalarySettlement(orderId, salaryFee, salaryId, accessToken);
        return ResponseResult.success("结算成功");
    }

    /**
     * 补贴明细批量结算
     */
    @PostMapping("salarySettlements")
    public ResponseResult SalarySettlements(String orderIds, String salaryId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.SalarySettlements(orderIds, salaryId, accessToken);
        return ResponseResult.success("结算成功");
    }

    /**
     * 发送工资单
     */
    @PostMapping("sendBillToBySalary")
    public ResponseResult sendBillToBySalary(String salaryIds, Long verificationSalary,String orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.sendBillToBySalary(salaryIds, verificationSalary, accessToken,orderId);
        return ResponseResult.success("发放成功");
    }

    /**
     * 批量发送工资单
     */
    @PostMapping("sendBillToBySalarys")
    public ResponseResult sendBillToBySalarys(String salaryIds) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.sendBillToBySalarys(salaryIds, accessToken);
        return ResponseResult.success("发放成功");
    }

    /**
     * 更新模板时间
     *
     * @param templateMonth
     * @return
     */
    @PostMapping("updateTemplate")
    public ResponseResult updateTemplate(String templateMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.updateCmSalaryTemplate(templateMonth, accessToken);
        return ResponseResult.success("更新模板时间成功");
    }

    /**
     * 获取工资申诉详情
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
     * 导出工资申诉
     */
    @PostMapping("/downloadQuerySalaryComplainExcelFile")
    public ResponseResult downloadQuerySalaryComplainExcelFile(Long salaryId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("工资单申诉数据列表导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            cmSalaryInfoNewService.downloadQuerySalaryComplainExcelFile(salaryId, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }

    /**
     * 工资申诉审核
     */
    @PostMapping("/checkSalaryComplain")
    public ResponseResult checkSalaryComplain(Long id, Integer checkState, String checkResult) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        cmSalaryInfoNewService.checkSalaryComplain(id, checkState, checkResult, accessToken);
        return ResponseResult.success();
    }

    /**
     * 22030 APP-查询工资列表新版
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
     * 小程序获取工资列表数据
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
     * 小程序 确认司机工资单补贴明细发送
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
     * 22031 工资单-查看详情新版
     *
     * @param salaryId
     * @return
     */
    @GetMapping("queryCmSalaryDetail")
    public ResponseResult queryCmSalaryDetail(Long salaryId) {
        if (salaryId < 0) {
            throw new BusinessException("主键参数错误");
        }
        return ResponseResult.success(
                cmSalaryInfoNewService.queryCmSalaryDetail(salaryId)
        );
    }

    /**
     * 22033   工资单-申诉申请新版
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
     * 22035 APP-新版工资订单列表
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
     * 获取工资单订单信息
     */
    @GetMapping("getCmSalaryOrderInfoNew")
    public ResponseResult getCmSalaryOrderInfoNew(Long sendId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderListOutDto> list = cmSalaryInfoNewService.getCmSalaryOrderInfoNew(sendId, accessToken);
        return ResponseResult.success(list);
    }

    /**
     *22036  APP-新版工资补贴
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
