package com.youming.youche.system.business.controller.mycenter;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.domain.mycenter.CmbBankAccountInfo;
import com.youming.youche.system.dto.mycenter.BankAccountListDto;
import com.youming.youche.system.vo.mycenter.AccountBalanceVo;
import com.youming.youche.system.vo.mycenter.CreatePrivateAccountVo;
import com.youming.youche.system.vo.mycenter.CreatePublicAccountVo;
import com.youming.youche.system.vo.mycenter.PrivateAccountVo;
import com.youming.youche.system.vo.mycenter.PublicAccountVo;
import com.youming.youche.system.vo.mycenter.UpdateBankAccountVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName BankAccountController
 * @Description 银行账户
 * @Author zag
 * @Date 2022/2/15 9:43
 */
@RestController
@RequestMapping("/bankaccount")
public class BankAccountController extends BaseController<CmbBankAccountInfo, IBankAccountService> {

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @Override
    public IBankAccountService getService() {
        return bankAccountService;
    }


    /**
     * @description 获取个人用户账户列表
     * @author zag
     * @date 2022/2/23 15:00
     * @param userId 
     * @param accType 
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getUserAccList")
    public ResponseResult getUserAccList(@RequestParam(value = "userId") Long userId,
                                         @RequestParam(value = "accType", defaultValue = "", required = false) String accType){
        List<BankAccountListDto> list= bankAccountService.getUserAccList(userId,accType);
        return ResponseResult.success(list);
    }

    /**
     * @description 获取车队账户列表
     * @author zag
     * @date 2022/2/23 15:00
     * @param tenantId 
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getTenantAccList")
    public ResponseResult getTenantAccList(@RequestParam(value = "tenantId") Long tenantId,
                                           @RequestParam(value = "accType", defaultValue = "", required = false) String accType){
        List<BankAccountListDto> list= bankAccountService.getTenantAccList(tenantId, accType);
        return ResponseResult.success(list);
    }

    /**
     * @param id
     * @return com.youming.youche.commons.response.ResponseResult
     * @description 获取账户详情
     * @author zag
     * @date 2022/2/19 19:26
     */
    @GetMapping({"get/{id}"})
    @Override
    public ResponseResult get(@PathVariable Long id) {
        return ResponseResult.success(bankAccountService.getById(id));
    }

    /**
     * @description 获取公户详情
     * @author zag
     * @date 2022/3/1 18:30
     * @param id
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getPublicAccount/{id}")
    public ResponseResult getPublicAccount(@PathVariable Long id) {
        CmbBankAccountInfo cmbBankAccountInfo = bankAccountService.getById(id);
        if (cmbBankAccountInfo == null) {
            return ResponseResult.failure("获取账户详情失败");
        } else {
            PublicAccountVo publicAccountVo = new PublicAccountVo();
            BeanUtils.copyProperties(cmbBankAccountInfo, publicAccountVo);
            return ResponseResult.success(publicAccountVo);
        }
    }

    /**
     * @description 获取私户详情
     * @author zag
     * @date 2022/3/1 18:29
     * @param id 
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getPrivateAccount/{id}")
    public ResponseResult getPrivateAccount(@PathVariable Long id) {
        CmbBankAccountInfo cmbBankAccountInfo = bankAccountService.getById(id);
        if (cmbBankAccountInfo == null) {
            return ResponseResult.failure("获取账户详情失败");
        } else {
            PrivateAccountVo privateAccountVo = new PrivateAccountVo();
            BeanUtils.copyProperties(cmbBankAccountInfo, privateAccountVo);
            return ResponseResult.success(privateAccountVo);
        }
    }

    /**
     * @description 公户注册
     * @author zag
     * @date 2022/2/23 20:38
     * @param createPublicAccountVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("publciAccReg")
    public ResponseResult publicAccReg(@Valid @RequestBody CreatePublicAccountVo createPublicAccountVo){
        bankAccountService.publicAccReg(createPublicAccountVo);
        return ResponseResult.success("账户注册申请提交成功");
    }

    /**
     * @description 私户注册
     * @author zag
     * @date 2022/2/23 20:39
     * @param createPrivateAccountVo
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("privceAccReg")
    public ResponseResult privceAccReg(@Valid @RequestBody CreatePrivateAccountVo createPrivateAccountVo){
        bankAccountService.privceAccReg(createPrivateAccountVo);
        return ResponseResult.success("账户注册申请提交成功");
    }

    /**
     * @param updateBankAccountVo
     * @return com.youming.youche.commons.response.ResponseResult
     * @description 账户管理员信息变更
     * @author zag
     * @date 2022/2/19 19:32
     */
    @PutMapping({"mgrInfoChg"})
    public ResponseResult mgrInfoChg(@RequestBody UpdateBankAccountVo updateBankAccountVo) {
        bankAccountService.mgrInfoChg(updateBankAccountVo);
        return ResponseResult.success();
    }


    /**
     * @param id
     * @return com.youming.youche.commons.response.ResponseResult
     * @description 删除账户
     * @author zag
     * @date 2022/2/19 19:33
     */
    @DeleteMapping({"remove/{id}"})
    @Override
    public ResponseResult remove(@PathVariable Long id) {
        Boolean deleted = bankAccountService.remove(id);
        return deleted ? ResponseResult.success("账户删除成功") : ResponseResult.failure("账户删除失败");
    }

    /**
     * @param mbrNo
     * @return com.youming.youche.commons.response.ResponseResult
     * @description 获取账户余额（单个）
     * @author zag
     * @date 2022/2/19 19:35
     */
    @GetMapping("getAccBalance/{mbrNo}")
    public ResponseResult getAccBalance(@PathVariable String mbrNo) {
        String balance = bankAccountService.getBalance(mbrNo);
        return ResponseResult.success((Object) balance);
    }

    /**
     * @description 获取用户账户总余额
     * @author zag
     * @date 2022/2/23 9:53
     * @param userId
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getUserAccBalance/{userId}")
    public ResponseResult getUserAccBalance(@PathVariable Long userId) {
        AccountBalanceVo accountBalanceVo = bankAccountService.getUserAccBalance(userId);
        return ResponseResult.success(accountBalanceVo);
    }

    /**
     * @description 获取车队账户总余额
     * @author zag
     * @date 2022/2/23 9:53
     * @param tenantId
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("getTenantAccBalance/{tenantId}")
    public ResponseResult getTenantAccBalance(@PathVariable Long tenantId) {
        AccountBalanceVo accountBalanceVo = bankAccountService.getTenantAccBalance(tenantId);
        return ResponseResult.success(accountBalanceVo);
    }

}
