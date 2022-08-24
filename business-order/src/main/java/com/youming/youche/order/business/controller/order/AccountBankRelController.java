package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.exception.BusinessException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.BankReceiptOutDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.BillingDetailsOut;
import com.youming.youche.order.vo.AccountBankRelVo;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppOutDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import com.youming.youche.commons.base.BaseController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("account/bank/rel")
public class AccountBankRelController extends BaseController<AccountBankRel, IAccountBankRelService> {
    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;
    @Override
    public IAccountBankRelService getService() {
        return accountBankRelService;
    }

    /**
     * 5104
     * 司机小程序
     * WX接口-查询可转移金额
     * niejiewei
     *
     * @return
     */
    @GetMapping("/turnVirAmountUIByWx")
    public ResponseResult turnVirAmountUIByWx() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto pingAnBalanceDto = accountBankRelService.turnVirAmountUIByWx(accessToken);
        return ResponseResult.success(pingAnBalanceDto);
    }

    /**
     * 5106
     * 司机小程序
     * WX接口-资金转移
     * niejiewei
     *
     * @param accountBankRelVo
     * @return
     */
    @PostMapping("/turnVirAmountWx")
    public ResponseResult turnVirAmountWx(@RequestBody AccountBankRelVo accountBankRelVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = accountBankRelService.turnVirAmount(null, accountBankRelVo.getAmount(),
                accountBankRelVo.getAccountType(), accountBankRelVo.getMesCode(),
                accountBankRelVo.getSerialNo(), accountBankRelVo.getAccountNo(),
                accountBankRelVo.getAccountType(), accessToken);
        return ResponseResult.success(s);
    }

    /**
     * niejiewei
     * 司机小程序
     * 50014
     * 微信接口-提现界面
     *
     * @return
     */
    @GetMapping("/withdrawCashUIByWx")
    public ResponseResult withdrawCashUIByWx() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto dto = accountBankRelService.withdrawCashUIByWx(accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * 根据用户编号查询账号信息-21153
     *
     * @param userId
     * @param type
     * @param userType
     * @return
     */
    @GetMapping("getBankInfo")
    public List<AccountBankRel> getBankInfo(Long userId, Integer type, Integer userType) {
        List<AccountBankRel> accountBankRels = null;
        accountBankRels = accountBankRelService.queryAccountBankRel(userId, userType, type);
        return accountBankRels;
    }

    /**
     * niejiewei
     * 小程序 微信接口-提现
     * 50015
     *
     * @param accountBankRelVo
     * @return
     */
    @PostMapping("/withdrawCashByWx")
    public ResponseResult withdrawCashByWx(@RequestBody AccountBankRelVo accountBankRelVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto dto = accountBankRelService.withdrawCashByWx(accessToken, accountBankRelVo);
        return ResponseResult.success(dto);
    }

    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支界面
     * 50016
     *
     * @return
     */
    @GetMapping("/advanceUIByWx")
    public ResponseResult advanceUIByWx() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto dto = accountBankRelService.advanceUIByWx(accessToken);
        return ResponseResult.success(dto);
    }


    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支-查询预支手续费
     * 50017
     *
     * @return
     */
    @GetMapping("/getAdvanceFeeByWx")
    public ResponseResult getAdvanceFeeByWx(@RequestParam(value = "amountFee") Long amountFee) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto advanceFeeByWx = accountBankRelService.getAdvanceFeeByWx(accessToken, amountFee);
        return ResponseResult.success(advanceFeeByWx);
    }


    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支-预支
     * 50018
     *
     * @return
     */
    @PostMapping("/confirmAdvanceByWx")
    public ResponseResult confirmAdvanceByWx(@RequestBody AccountBankRelVo accountBankRelVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PingAnBalanceDto dto = accountBankRelService.confirmAdvanceByWx(accessToken, accountBankRelVo);
        return ResponseResult.success(dto);
    }

    /**
     * niejeiwei
     * 司机小程序
     * 微信接口-预支-可预支金额详情
     * 50019
     *
     * @param userId
     * @return
     */
    @GetMapping("/getAdvanceDetailsByWx")
    public ResponseResult getAdvanceDetailsByWx(@RequestParam(value = "userId") Long userId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<MarginBalanceDetailsOut> marginBalance = accountBankRelService.getMarginBalance(accessToken, userId);
        return ResponseResult.success(marginBalance);
    }

    /**
     * 20001
     * 查询账户列表
     *
     * @param bankType1 //0对私 1对公
     */
    @GetMapping("queryBankBalanceInfoListAPP")
    public ResponseResult queryBankBalanceInfoListAPP(Integer userType, Long userId, Integer bankType,
                                                      String acctName, String acctNo, Integer bankType1, Integer isNeedHa) {
        if (userId == null || userId < 0) {
            throw new BusinessException("用户主键错误");
        }
        if (bankType == null || bankType < 0) {
            throw new BusinessException("账户类型参数错误");
        }

        List<BankBalanceInfo> bankBalanceInfos = accountBankRelService.queryBankBalanceInfoList(userType, userId, bankType, acctName, acctNo, bankType1, isNeedHa);
        return ResponseResult.success(bankBalanceInfos);
    }

    /**
     * niejeiwei
     * 微信接口-开票明细
     * 司机小程序
     * 50020
     * @param userId
     * @param month
     * @param fleetName
     * @param userType
     * @param payUserType
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/billingDetailsByWx")
    public ResponseResult billingDetailsByWx(@RequestParam(value = "userId") Long userId,
                                             @RequestParam(value = "month") String month,
                                             @RequestParam(value = "fleetName") String fleetName,
                                             @RequestParam(value = "userType") Integer userType,
                                             @RequestParam(value = "payUserType") Integer payUserType,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<BillingDetailsOut> outPage = accountBankRelService.billingDetailsByWx(userId, month, fleetName, userType,
                payUserType, pageNum, pageSize, accessToken);
        return ResponseResult.success(outPage);
    }

    /**
     * niejiewei
     * 司机小程序
     * APP接口-我的钱包-收支明细-银行流水
     * 50026
     * @param userId
     * @param month
     * @param queryType
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getBankDetailsToApp")
    public ResponseResult getBankDetailsToApp(@RequestParam(value = "userId") Long userId,
                                             @RequestParam(value = "month") String month,
                                             @RequestParam(value = "queryType") String queryType,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<BankFlowDetailsAppOutDto> bankDetailsToApp = accountBankRelService.getBankDetailsToApp(userId, month,
                queryType, pageNum, pageSize, accessToken);
        return ResponseResult.success(bankDetailsToApp);
    }

    /**
     * 20002
     * 微信绑定账户列表
     */
    @PostMapping("queryBankBalanceInfoListWX")
    public ResponseResult queryBankBalanceInfoListWX() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                accountBankRelService.queryBankBalanceInfoListWX(accessToken)
        );
    }


    /**
     * niejeiwei
     * 司机小程序
     * APP接口-我的钱包-收支明细-银行流水-银行回单
     * 50028
     * @param bankPreFlowNumber
     * @return
     */
    @GetMapping("/getBankReceiptToApp")
    public ResponseResult getBankReceiptToApp(@RequestParam(value = "bankPreFlowNumber")  String bankPreFlowNumber
                                             ) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BankReceiptOutDto bankReceiptToApp = accountBankRelService.getBankReceiptToApp(bankPreFlowNumber, accessToken);
        return ResponseResult.success(bankReceiptToApp);
    }

    /**
     * 接口编码:21010
     * <p>
     * 接口出参：
     * depositBalance	押金	number
     * totalBalance	可提现	number
     * totalDebtAmount	欠款	number
     * totalEtcBalance	ETC账户	number
     * totalMarginBalance	即将到期	number
     * totalOilBalance	油账户	number
     * totalRepairFund	维修资金	number
     */
    @GetMapping("getAccountSumWX")
    public ResponseResult getAccountSumWX() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAccountOutVo accountSumWX = accountBankRelService.getAccountSumWX(accessToken);
        return ResponseResult.success(accountSumWX);
    }


    /**
     * 获取银行流水下载地址接口 50060
     * niejeiwei
     *
     */
    @GetMapping("/getBankFlowDownloadUrl")
    public  ResponseResult getBankFlowDownloadUrl(AccountBankRelVo vo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String bankFlowDownloadUrl = accountBankRelService.getBankFlowDownloadUrl(vo, accessToken);
        return ResponseResult.success(bankFlowDownloadUrl);
    }
}
