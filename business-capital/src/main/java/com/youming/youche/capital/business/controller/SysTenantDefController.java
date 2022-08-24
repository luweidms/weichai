package com.youming.youche.capital.business.controller;

import cn.hutool.core.util.StrUtil;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.SysTenantInfoVo;
import com.youming.youche.system.vo.SysTenantVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys/tenant/def")
public class SysTenantDefController extends BaseController {
    @Override
    public IBaseService getService() {
        return sysTenantDefService;
    }

    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    /**
     * 查询车队信息
     * @param tenantId
     * @return
     */
    @RequestMapping("/queryTenantById")
    private ResponseResult queryTenantById(@RequestParam Long tenantId){

        SysTenantVo sysTenantVo=sysTenantDefService.getTenantById(tenantId);
        if(sysTenantVo.getBusinessState().getAnnualTurnover()==null){
            sysTenantVo.getBusinessState().setAnnualTurnover(Double.valueOf(0));
        }
        if(sysTenantVo.getBusinessState().getStaffNumber()==null){
            sysTenantVo.getBusinessState().setStaffNumber(0);
        }
        return ResponseResult.success(sysTenantVo);
    }

    /**
     * 修改车队信息
     * @param sysTenantVo
     * @return
     */
    @PutMapping("/doUpdate")
    public ResponseResult doUpdate(@RequestBody SysTenantInfoVo sysTenantVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        //修改区分于新增的信息设置
        if (sysTenantVo == null || sysTenantVo.getBusinessState().getTenantId() <= 0) {
            throw new BusinessException("车队主键ID不存在!");
        }
        vaildateParam(sysTenantVo);
        sysTenantDefService.updateTenantInfo(sysTenantVo, accessToken);
        return ResponseResult.success("修改成功");
    }
    /***
     * 修改车队参数校验
     **/
    private void vaildateParam(SysTenantInfoVo sysTenantVo) {
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

//        if (serviceFee == null || serviceFee < 0) {
//            throw new BusinessException("请填写平台服务费!");
//        }

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
//        if (payServiceFeeDate == null || "".equals(payServiceFeeDate)) {
//            throw new BusinessException("系统服务费的收款日期不能为空!");
//        }
        //设置数据
//        BillSetting billSetting = sysTenantVo.getBillSetting();
//        if (billSetting == null || billSetting.getBillAbility() == null) {
//            throw new BusinessException("请选择开票能力!");
//        }
    }
}
