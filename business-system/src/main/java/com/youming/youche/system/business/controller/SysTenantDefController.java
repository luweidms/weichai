package com.youming.youche.system.business.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.tenant.ISysTenantRegisterService;
import com.youming.youche.system.business.dto.SysTenantDefSimDto;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.tenant.BillSetting;
import com.youming.youche.system.dto.TenantInfoDto;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.system.vo.SysTenantVo;
import com.youming.youche.system.vo.TenantQueryVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-04
 */
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantDefController extends BaseController<SysTenantDef, ISysTenantDefService> {

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ITenantStaffRelService tenantStaffRelService;

    @DubboReference(version = "1.0.0")
    ISysTenantRegisterService sysTenantRegisterService;


    @Override
    public ISysTenantDefService getService() {
        return sysTenantDefService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SysTenantDefController.class);

    /**
     * 获取精简版车队信息
     */
    @GetMapping({"get"})
    public ResponseResult getOwn() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.selectByUserId(accessToken);
        List<SysTenantDefSimDto> simDtoList = Lists.newArrayList();
        for (SysTenantDef sysTenantDef : sysTenantDefList) {
            SysTenantDefSimDto simDto = SysTenantDefSimDto.of();
            BeanUtil.copyProperties(sysTenantDef, simDto);
            simDtoList.add(simDto);
        }
        return ResponseResult.success(simDtoList);
    }

    /**
     * 获取车队详情
     */
    @GetMapping({"getDetails"})
    public ResponseResult getDetails() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.selectByUserId(accessToken);
        return ResponseResult.success(sysTenantDefList);
    }

    @PostMapping({"choose/{tenantId}"})
    public ResponseResult choose(@PathVariable("tenantId") Long tenantId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean choose = sysTenantDefService.choose(accessToken, tenantId);

        return choose ? ResponseResult.success("选择成功") : ResponseResult.failure();
    }

    /***
     * @Description: 查询车队列表（运营端）
     * @Author: luwei
     * @Date: 2022/1/6 5:17 下午
     * @Param inParam:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    @GetMapping({"queryTenant"})
    public ResponseResult queryTenant(TenantQueryVo tenantQueryVo,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String startTime = tenantQueryVo.getStartTime();
        String endTime = tenantQueryVo.getEndTime();
        if (StrUtil.isNotEmpty(startTime)) {
            startTime = startTime + " 00:00:00";
            tenantQueryVo.setStartTime(startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            endTime = endTime + " 23:59:59";
            tenantQueryVo.setEndTime(endTime);
        }
        PageInfo pageInfo = sysTenantDefService.queryTenant(tenantQueryVo, pageNum, pageSize);
        return ResponseResult.success(pageInfo);
    }

//    导出结果页
//    @GetMapping(value = "export")
//    public ResponseResult export(TenantQueryVo tenantQueryVo, HttpServletResponse response) {
//        try {
//            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            String startTime = tenantQueryVo.getStartTime();
//            String endTime = tenantQueryVo.getEndTime();
//            if (StrUtil.isNotEmpty(startTime)) {
//                startTime = startTime + " 00:00:00";
//                tenantQueryVo.setStartTime(startTime);
//            }
//            if (StrUtil.isNotEmpty(endTime)) {
//                endTime = endTime + " 23:59:59";
//                tenantQueryVo.setEndTime(endTime);
//            }
//            ImportOrExportRecords record = new ImportOrExportRecords();
//            record.setName("车队信息导出");
//            record.setBussinessType(2);
//            record.setState(1);
//            record = importOrExportRecordsService.saveRecords(record,accessToken);
//            sysTenantDefService.queryTenantExport(tenantQueryVo,record);
//            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
//        } catch (Exception e) {
//            LOGGER.error("导出失败车队列表异常" + e);
//            return ResponseResult.failure("导出成功车队列表异常");
//        }
//    }
    /** 车队信息导出
     * 方法实现说明
     * @author      terry
     * @param tenantQueryVo
     * @param response
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 14:36
     */
    @GetMapping(value = "export")
    public ResponseResult export(TenantQueryVo tenantQueryVo, HttpServletResponse response) {
        String startTime = tenantQueryVo.getStartTime();
        String endTime = tenantQueryVo.getEndTime();
        if (StrUtil.isNotEmpty(startTime)) {
            startTime = startTime + " 00:00:00";
            tenantQueryVo.setStartTime(startTime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            endTime = endTime + " 23:59:59";
            tenantQueryVo.setEndTime(endTime);
        }
        List<SysTenantDef> queryTenantList = sysTenantDefService.queryTenantList(tenantQueryVo);
        String[] showName = null;
        String[] resourceFild = null;
        String fileName = null;
        showName = new String[]{"车队名称", "手机号码", "注册时间", "实际控制人", "控制人联系方式", "员工数", "年营业额（万）", "自有车数量", "外调车数量", "类型", "账号状态"};
        resourceFild = new String[]{"getName", "getLinkPhone", "getCreateTimeSter", "getActualController", "getActualControllerPhone", "getStaffNumber", "getAnnualTurnover", "getVehicleNumber", "getOtherCar", "getVirtualStateName", "getStateName"};
        try {
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(queryTenantList, showName, resourceFild, SysTenantDef.class,
                    null);
            // 准备将Excel的输出流通过response输出到页面下载
            // 八进制输出流
            fileName = new String("车队信息".getBytes("utf-8"), "iso8859-1");
            // 这后面可以设置导出Excel的名称，
            response.setHeader("Content-disposition",
                    "attachment;filename=" + fileName + Calendar.getInstance().getTimeInMillis() + ".xlsx");
            // 刷新缓冲
            response.flushBuffer();
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.success("导出成功");
    }


    /**
     * 保存车队信息（运营端）
     *
     * @return
     * @throws Exception
     */
    @PostMapping({"doSaveTenant"})
    public ResponseResult doSaveTenant(@RequestBody SysTenantVo sysTenantVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        this.vaildateParam(sysTenantVo);
        if (null == sysTenantVo.getAvgScore()) {
            sysTenantVo.setAvgScore(100.00);
        }
        Long registerId = sysTenantVo.getRegisterId();
        if (registerId != null) {
            sysTenantVo.setRegisterId(registerId);
        }
        int flag = sysTenantDefService.saveAndBuild(sysTenantVo, accessToken);
        if (flag == 1) {
            return ResponseResult.success("保存成功");
        } else {
            return ResponseResult.success("保存失败");
        }
    }

    /***
     * @Description: 手机号验证（运营端）
     * @Author: luwei
     * @Date: 2022/1/18 4:36 下午
     * @Param phone:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping({"checkPhone"})
    public ResponseResult checkPhone(@RequestParam("phone") String phone) {
        return ResponseResult.success(sysTenantDefService.checkPhone(phone));
    }


    /***
     * @Description: 获取车队信息（运营端-获取已注册的）
     * @Author: luwei
     * @Date: 2022/1/19 4:49 下午
     * @Param tenantId:
     * @return: java.lang.String
     * @Version: 1.0
     **/
    @GetMapping({"getTenantById"})
    public ResponseResult getTenantById(@RequestParam("tenantId") Long tenantId) {
        if (tenantId <= 0) {
            throw new BusinessException("输入车队ID错误");
        }
        return ResponseResult.success(sysTenantDefService.getTenantById(tenantId));
    }

    /**
     * 重置登录密码
     *
     * @return
     * @throws Exception
     */
    @PutMapping({"resetPasswords"})
    public ResponseResult resetPasswords(@RequestParam("tenantId") Long tenantId) {
        if (tenantId <= 0) {
            throw new BusinessException("输入车队ID错误");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        sysTenantDefService.resetPassword(tenantId,accessToken);
        return ResponseResult.success("重置成功");
    }

    /**
     * 启用停用车队
     *
     * @return
     * @throws Exception
     */
    @PutMapping("updateState")
    public ResponseResult updateState(@RequestParam("tenantId") Long tenantId, @RequestParam("reason") String
            reason, @RequestParam(value = "state", defaultValue = "0") Integer state) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (tenantId <= 0) {
            throw new BusinessException("输入车队ID错误!");
        }
        if (StringUtils.isEmpty(reason)) {
            throw new BusinessException("输入操作原因!");
        }
        if (state < 0) {
            throw new BusinessException("该车队状态有误!");
        }

        sysTenantDefService.updateState(tenantId, state == 1 ? 0 : 1, reason, accessToken);
        return ResponseResult.success("操作成功");
    }

    /***
     * @Description: 修改车队信息（运营端）
     * @Author: luwei
     * @Date: 2022/1/20 3:19 下午
     * @Param sysTenantVo:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @PutMapping({"doUpdate"})
    public ResponseResult doUpdate(@RequestBody SysTenantVo sysTenantVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        //修改区分于新增的信息设置
        if (sysTenantVo == null || sysTenantVo.getTenantId() <= 0) {
            throw new BusinessException("车队主键ID不存在!");
        }
        vaildateParam(sysTenantVo);
        sysTenantDefService.updateTenant(sysTenantVo, accessToken);
        return ResponseResult.success("修改成功");
    }


    /**
     * 修改车队开票设置
     *
     * @return
     * @throws Exception
     */
    @PutMapping("doUpdateBillSetting")
    public ResponseResult doUpdateBillSetting(@RequestBody BillSetting billSetting) throws Exception {
        if (billSetting.getAttachFree() != null) {
            if (Double.valueOf("40.0").compareTo(billSetting.getAttachFree()) < 0) {
                throw new BusinessException("附加运费最大可设置40%");
            }
        }
        if (billSetting.getBillMethod() < 0) {
            throw new BusinessException("请选择开票平台!");
        }
        if (billSetting.getRateId() < 0) {
            throw new BusinessException("请选择费率!");
        }
        sysTenantDefService.doUpdateBillSetting(billSetting);
        return ResponseResult.success("修改车队开票成功");
    }

    /***
     * @Description: 修改入场日期
     * @Author: luwei
     * @Date: 2022/1/21 2:32 下午

     * @return: java.lang.String
     * @Version: 1.0
     **/
    @PutMapping("updatePayDateAndLeaveDate")
    public ResponseResult updatePayDateAndLeaveDate(@RequestParam("tenantId") Long tenantId,
                                                    @RequestParam(value = "payDate", required = false) String payDate,
                                                    @RequestParam(value = "leaveDate", required = false) String leaveDate,
                                                    @RequestParam(value = "entranceDate", required = false) String entranceDate) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (tenantId <= 0) {
            throw new BusinessException("车队主键ID不存在!");
        }
        try {
            sysTenantDefService.updatePayDateAndLeaveDate(tenantId, payDate, leaveDate, entranceDate, accessToken);
        } catch (Exception e) {
            LOGGER.error("查询车队列表异常" + e);
            return ResponseResult.failure("修改入场日期异常");
        }
        return ResponseResult.success("修改入场日期成功");
    }

    /**
     * 审核车队信息
     *
     * @return
     * @throws Exception
     */
    @PutMapping("audit")
    public ResponseResult audit(@RequestParam("auditState") Integer auditState, @RequestParam("id") Long
            id, @RequestParam("auditContent") String auditContent) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (id <= 0) {
            throw new BusinessException("找不到审核车队ID");
        }
        if (SysStaticDataEnum.REGISTER_AUDIT_STATE.AUDIT_FAIL == auditState && StringUtils.isEmpty(auditContent)) {
            throw new BusinessException("审核不通过,需要填写原因!");
        }
        sysTenantDefService.audit(id, auditState, auditContent, accessToken);
        return ResponseResult.success();
    }


    /***
     * @Description: todo
     * @Author: 保存车队参数校验（运营端）
     * @Date: 2022/1/18 5:16 下午
     * @Param sysTenantVo:
     * @return: void
     * @Version: 1.0
     **/
    private void vaildateParam(SysTenantVo sysTenantVo) {
        //车队信息
        String linkPhone = sysTenantVo.getLinkPhone();
        String linkMan = sysTenantVo.getLinkMan();
        String linkManIdentification = sysTenantVo.getLinkManIdentification();
        String businessLicense = sysTenantVo.getBusinessLicense();
        String companyQualifications = sysTenantVo.getCompanyQualifications();
        String companyName = sysTenantVo.getCompanyName();
        String shortName = sysTenantVo.getShortName();
        String businessLicenseNo = sysTenantVo.getBusinessLicenseNo();
        String companyAddress = sysTenantVo.getCompanyAddress();
        Integer provinceId = sysTenantVo.getProvinceId();
        Integer cityId = sysTenantVo.getCityId();
        String address = sysTenantVo.getAddress();
        String logo = sysTenantVo.getLogo();
        String identificationPicture = sysTenantVo.getIdentificationPicture();
        String actualController = sysTenantVo.getActualController();
        String identification = sysTenantVo.getIdentification();
        String actualControllerPhone = sysTenantVo.getActualControllerPhone();
        Long serviceFee = sysTenantVo.getServiceFee();//系统服务费
        String payServiceFeeDate = sysTenantVo.getPayServiceFeeDate();//服务费支付日期
        //数据校验
        if (!CommonUtils.isCheckPhone(linkPhone)) {
            throw new BusinessException("输入车队信息手机号码格式错误!");
        }

        if (StrUtil.isEmpty(linkMan)) {
            throw new BusinessException("请填写车队超管姓名!");
        }

        if (StrUtil.isEmpty(linkManIdentification)) {
            throw new BusinessException("请填写超管身份证号码!");
        }

        if (serviceFee == null || serviceFee < 0) {
            throw new BusinessException("请填写平台服务费!");
        }

        if (!CommonUtils.isIDCard(linkManIdentification)) {
            throw new BusinessException("请填写正确的超管身份证号码!");
        }

        if (StrUtil.isEmpty(businessLicense)) {
            throw new BusinessException("请上传车队信息营业执照!");
        }

        if (StrUtil.isEmpty(companyQualifications)) {
            throw new BusinessException("请上传企业资质图片!");
        }

        if (StrUtil.isEmpty(companyName)) {
            throw new BusinessException("请输入企业全称!");
        }
        if (StrUtil.isEmpty(shortName)) {
            throw new BusinessException("请输入企业简称!");
        }
        if (shortName.length() > 6) {
            throw new BusinessException("企业简称不能大于6个中文字符!");
        }
        if (StrUtil.isEmpty(businessLicenseNo)) {
            throw new BusinessException("请输入企业社会信用代码!");
        }
        if (StrUtil.isEmpty(companyAddress)) {
            throw new BusinessException("请选择企业注册住所详细地址!");
        }
        if (provinceId <= 0 || cityId <= 0) {
            throw new BusinessException("请选择常用办公地点省市!");
        }
        if (StrUtil.isEmpty(address)) {
            throw new BusinessException("请填写常用办公地点详细地址!");
        }
        if (StrUtil.isEmpty(logo)) {
            throw new BusinessException("请上传车队LOGO!");
        }
        if (StrUtil.isEmpty(identificationPicture)) {
            throw new BusinessException("请上传实际控制人身份证!");
        }
        if (StringUtils.isEmpty(actualController)) {
            throw new BusinessException("请输入实际控制人姓名!");
        }
        if (!CommonUtils.isIDCard(identification)) {
            throw new BusinessException("请输入正确的实际控制人身份证号!");
        }
        if (!CommonUtils.isCheckMobiPhoneNew(actualControllerPhone)) {
            throw new BusinessException("请输入正确的实际控制人电话号码!");
        }
        if (payServiceFeeDate == null || "".equals(payServiceFeeDate)) {
            throw new BusinessException("系统服务费的收款日期不能为空!");
        }
        //设置数据
        BillSetting billSetting = sysTenantVo.getBillSetting();
        if (billSetting == null || billSetting.getBillAbility() == null) {
            throw new BusinessException("请选择开票能力!");
        }
    }

    /**
     * 查询当前租户的信息
     * @author zag
     * @date 2022/3/29 18:01
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("sysTenantDef")
    public ResponseResult sysTenantDef(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        SysTenantDef sysTenantDef=sysTenantDefService.getSysTenantDef(accessToken);
        return ResponseResult.success(sysTenantDef);
    }

    /**
     * 是否设置支付密码，编号 10021
     *
     * @param userId   用户编号
     * @param userType 用户类型 1-员工，2-其他 （如果是员工，车队超管设置了支付密码则为设置了支付密码）
     * @return result Y-已设置，N-未设置
     */
    @PostMapping("isSetPayPasswd")
    public ResponseResult isSetPayPasswd(Long userId, Integer userType) {
        if (userId == null) {
            throw new BusinessException("用户编号不能为空!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean setPayPasswd = sysTenantDefService.isSetPayPasswd(userId, userType, accessToken);
        return ResponseResult.success(setPayPasswd);
    }

    /**
     * 10027 APP接口-查询租户办公地址
     * 查询租户信息
     *
     * @param tenantId 租户编号
     * @return tenantName 租户名称
     * tenantLogo 租户loggo
     * detailAddr 办公地址
     */
    @GetMapping("queryTenantInfo")
    public ResponseResult queryTenantInfo(Long tenantId) {
        if (tenantId == null || tenantId < 0) {
            throw new BusinessException("租户编号不能为空！");
        }
        Map tenantInfo = sysTenantDefService.getTenantInfo(tenantId);
        TenantInfoDto dto = new TenantInfoDto();
        BeanUtil.copyProperties(tenantInfo, dto);
        return ResponseResult.success(dto);
    }

    /**
     * 接口编号 10030
     * <p>
     * 判断租户或用户是否已绑定银行卡
     *
     * @param tenantId 租户编号
     */
    @GetMapping("isTenantOrUserBindCard")
    public ResponseResult isTenantOrUserBindCard(Long tenantId, Long userId, Integer type) {

        return ResponseResult.success(
                sysTenantDefService.isTenantOrUserBindCard(tenantId, userId, type)
        );

    }

}
