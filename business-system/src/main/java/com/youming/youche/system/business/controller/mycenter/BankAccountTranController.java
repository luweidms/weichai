package com.youming.youche.system.business.controller.mycenter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.AesEncryptUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import com.youming.youche.system.domain.mycenter.CmbAccountTransactionRecord;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppDto;
import com.youming.youche.system.vo.mycenter.AccountFlowListVo;
import com.youming.youche.system.vo.mycenter.AccountFlowQueryVo;
import com.youming.youche.system.vo.mycenter.AccountTransferVo;
import com.youming.youche.system.vo.mycenter.AccountWithdrawVo;
import com.youming.youche.system.vo.mycenter.BankFlowDetailsAppVo;
import com.youming.youche.system.vo.mycenter.BankFlowDownVo;
import com.youming.youche.system.vo.mycenter.BankFlowDownloadUrlVo;
import com.youming.youche.system.vo.mycenter.BankReceiptVo;
import com.youming.youche.system.vo.mycenter.RechargeAccountVo;
import com.youming.youche.system.vo.mycenter.SetPayPwdVo;
import com.youming.youche.system.vo.mycenter.TenantAccountFlowQueryVo;
import com.youming.youche.system.vo.mycenter.TenantAccountTranFlowQueryVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName BankAccountTranController
 * @Description 银行账户交易
 * @Author zag
 * @Date 2022/2/15 18:34
 */
@RestController
@RequestMapping("/bankaccount/transaction")
public class BankAccountTranController extends BaseController<CmbAccountTransactionRecord, IBankAccountTranService> {

    @DubboReference(version = "1.0.0")
    IBankAccountTranService bankAccountTranService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    LoginUtils loginUtils;

    @Override
    public IBankAccountTranService getService() {
        return bankAccountTranService;
    }

    /**
     * @description 获取平台账户信息（充值）
     * @author zag
     * @date 2022/2/16 15:22
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getPlatformAccount")
    public ResponseResult getRechargeAccount() {
        RechargeAccountVo rechargeAccountVo = bankAccountTranService.getRechargeAccount();
        return ResponseResult.success(rechargeAccountVo);
    }

    /**
     * @description 账户转帐
     * @author zag
     * @date 2022/2/16 15:22
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("transfer")
    public ResponseResult transfer(@Valid @RequestBody AccountTransferVo accountTransferVo){
        bankAccountTranService.transfer(accountTransferVo);
        return ResponseResult.success();
    }

    /**
     * @description 账户提现
     * @author zag
     * @date 2022/2/16 15:22
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("withdraw")
    public ResponseResult withdraw(@Valid @RequestBody AccountWithdrawVo accountWithdrawVo){
        bankAccountTranService.withdraw(accountWithdrawVo);
        return ResponseResult.success();
    }

    /**
     * 发送支付验证码
     * @author zag
     * @date 2022/3/21 10:36
     * @param phone
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("sendPayVerifyCode/{phone}")
    public ResponseResult sendPayVerifyCode(@PathVariable String phone){
        bankAccountTranService.sendPayVerifyCode(phone);
        return ResponseResult.success();
    }

    /**
     * 验证支付验证码
     * @author zag
     * @date 2022/3/22 15:08
     * @param phone
     * @param verifyCode
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("checkPayVerifyCode")
    public ResponseResult checkPayVerifyCode(@RequestParam(value = "phone") String phone,
                                             @RequestParam(value = "verifyCode") String verifyCode){
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("手机号不能为空");
        }
        if (StringUtils.isBlank(verifyCode)) {
            throw new BusinessException("验证码不能为空");
        }
        Boolean result =bankAccountTranService.checkPayVerifyCode(phone,verifyCode);
        return result == true ? ResponseResult.success() : ResponseResult.failure("验证码错误");
    }

    /**
     * 设置支付密码
     * @author zag
     * @date 2022/3/21 10:45
     * @param setPayPwdVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("setPayPwd")
    public ResponseResult setPayPwd(@Valid @RequestBody SetPayPwdVo setPayPwdVo) {
        setPayPwdVo.setPwd(AesEncryptUtil.desEncrypt(setPayPwdVo.getPwd()));
        setPayPwdVo.setPwd2(AesEncryptUtil.desEncrypt(setPayPwdVo.getPwd2()));
        if (!setPayPwdVo.getPwd().equals(setPayPwdVo.getPwd2())) {
            throw new BusinessException("密码与确认密码不一致");
        }

        boolean result = bankAccountTranService.setPayPwd(setPayPwdVo);
        return result == true ? ResponseResult.success("密码设置成功") : ResponseResult.failure("密码设置失败");
    }

    /**
     * 是否已设置过支付密码
     * @author zag
     * @date 2022/3/21 13:38
     * @param userId
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("isExistsPayPwd/{userId}")
    public ResponseResult isExistsPayPwd(@PathVariable Long userId) {
        return bankAccountTranService.existsPayPwd(userId) ? ResponseResult.success(true) : ResponseResult.success(false);
    }

    /**
     * @description 获取个人账户流水
     * @author zag
     * @date 2022/2/16 14:52
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getUserAccountFlow")
    public ResponseResult getUserAccountFlow(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                         AccountFlowQueryVo accountFlowQueryVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = loginUtils.get(accessToken);
        accountFlowQueryVo.setTenantId(loginInfo.getTenantId());
        IPage<AccountFlowListVo> page=bankAccountTranService.getUserAccountFlow(pageNum,pageSize,accountFlowQueryVo);
        return ResponseResult.success(page);
    }

    /**
     * 获取车队账户流水
     * @author zag
     * @date 2022/3/24 19:05
     * @param pageNum
     * @param pageSize
     * @param tenantAccountFlowQueryVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getTenantAccountFlow")
    public ResponseResult getTenantAccountFlow(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                               TenantAccountFlowQueryVo tenantAccountFlowQueryVo) {
        if(tenantAccountFlowQueryVo==null || tenantAccountFlowQueryVo.getAccountId()==null){
            throw new BusinessException("账户Id不能为空");
        }
        IPage<AccountFlowListVo> page = bankAccountTranService.getTenantAccountFlow(pageNum, pageSize, tenantAccountFlowQueryVo);
        return ResponseResult.success(page);
    }

    /**
     * 获取车队账户交易流水
     * @author zag
     * @date 2022/4/21 15:56
     * @param pageNum 
     * @param pageSize 
     * @param tenantAccountTranFlowQueryVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getTenantAccountTranFlow")
    public ResponseResult getTenantAccountTranFlow(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                                   TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(tenantAccountTranFlowQueryVo==null) {
            tenantAccountTranFlowQueryVo = new TenantAccountTranFlowQueryVo();
        }
        tenantAccountTranFlowQueryVo.setTenantId(loginUtils.get(accessToken).getTenantId());
        IPage<AccountFlowListVo> page = bankAccountTranService.getTenantAccountTranFlow(pageNum, pageSize, tenantAccountTranFlowQueryVo);
        return ResponseResult.success(page);
    }

    /**
     * 获取个人账户流水（Wx）
     * @author zag
     * @date 2022/7/15 15:09
     * @param yearAndMonth
     * @param businessType
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getUserAccountFlowByWx")
    public ResponseResult getUserAccountFlowByWx(String yearAndMonth,String businessType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<AccountFlowListVo> page = bankAccountTranService.getUserAccountFlowByWx(loginInfo.getUserInfoId(), yearAndMonth, businessType);
        return ResponseResult.success(page);
    }

    /**
     * 获取车队账户流水（Wx）
     * @author zag
     * @date 2022/7/15 15:09
     * @param yearAndMonth
     * @param businessType
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getTenantAccountFlowByWx")
    public ResponseResult getTenantAccountFlowByWx(String yearAndMonth,String businessType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<AccountFlowListVo> page = bankAccountTranService.getTenantAccountFlowByWx(loginInfo.getTenantId(), yearAndMonth, businessType);
        return ResponseResult.success(page);
    }


    /**
     * @description 获取电子回单
     * @author zag
     * @date 2022/2/16 15:22
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getBankReceipt/{tranType}/{respNo}")
    public ResponseResult getElectronicReceipt(@PathVariable String respNo,@PathVariable String tranType) {
        BankReceiptVo bankReceiptVo = bankAccountTranService.getElectronicReceipt(respNo, tranType);
        return ResponseResult.success(bankReceiptVo);
    }

    /**
     * @description 导出账户流水
     * @author zag
     * @date 2022/2/16 15:22
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("userAccFlowExport")
    public ResponseResult userAccFlowExport(AccountFlowQueryVo accountFlowQueryVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("银行账户流水导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            bankAccountTranService.userAccFlowExport(accountFlowQueryVo,record);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("银行账户流水导出异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }

    /**
     * 车队账户流水导出
     * @author zag
     * @date 2022/3/24 19:43
     * @param tenantAccountFlowQueryVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("tenantAccFlowExport")
    public ResponseResult tenantAccFlowExport(TenantAccountFlowQueryVo tenantAccountFlowQueryVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("银行账户流水导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            bankAccountTranService.tenantAccFlowExport(tenantAccountFlowQueryVo,record);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("银行账户流水导出异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }

    /**
     * 车队账户转帐流水导出（对应平台支付）
     * @author zag
     * @date 2022/3/24 19:43
     * @param tenantAccountTranFlowQueryVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("tenantAccTranFlowExport")
    public ResponseResult tenantAccTranFlowExport(TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(tenantAccountTranFlowQueryVo==null) {
            tenantAccountTranFlowQueryVo = new TenantAccountTranFlowQueryVo();
        }
        tenantAccountTranFlowQueryVo.setTenantId(loginUtils.get(accessToken).getTenantId());
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("银行账户流水导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            bankAccountTranService.tenantAccTranFlowExport(tenantAccountTranFlowQueryVo,record);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("银行账户流水导出异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }

    /**
     * 查询银行流水
     * @param bankFlowDetailsAppVo
     * @return
     */
    @GetMapping("/getBankDetailsToApp")
    public ResponseResult getBankDetailsToApp(BankFlowDetailsAppVo bankFlowDetailsAppVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BankFlowDetailsAppDto bankDetailsToApp = bankAccountTranService.getBankDetailsToApp(bankFlowDetailsAppVo, accessToken);
        return ResponseResult.success(bankDetailsToApp);
    }

    @PostMapping("/getBankFlowDownloadUrl")
    public ResponseResult getBankFlowDownloadUrl(BankFlowDownVo bankFlowDownVo){
        BankFlowDownloadUrlVo bankFlowDownloadUrl = bankAccountTranService.getBankFlowDownloadUrl(bankFlowDownVo);
        return ResponseResult.success(bankFlowDownloadUrl);
    }
}
