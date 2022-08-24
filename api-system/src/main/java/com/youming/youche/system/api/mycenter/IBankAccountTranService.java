package com.youming.youche.system.api.mycenter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
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

import java.util.List;
import java.util.Map;

/**
 * @InterfaceName IBankAccountTranService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/19 20:15
 */
public interface IBankAccountTranService extends IBaseService<CmbAccountTransactionRecord> {

    /** 获取平台充值账户
     * */
    RechargeAccountVo getRechargeAccount();

    /** 虚拟账户间转帐
     * */
    void transfer(AccountTransferVo accountTransferVo);

    /** 账户提现
     * */
    void withdraw(AccountWithdrawVo accountWithdrawVo);

    /** 发送支付手机验证码
     * */
    void sendPayVerifyCode(String phone);

    /** 检查支付手机验证码
     * */
    boolean checkPayVerifyCode(String phone,String verifyCode);

    /** 设置支付密码
     * */
    boolean setPayPwd(SetPayPwdVo setPayPwdVo);

    /** 检查支付密码是否存在
     * */
    boolean existsPayPwd(Long userId);

    /** 分页获取个人账户银行流水
     * */
    IPage<AccountFlowListVo> getUserAccountFlow(Integer pageNum, Integer pageSize, AccountFlowQueryVo accountFlowQueryVo);

    /** 分页获取车队账户银行流水
     * */
    IPage<AccountFlowListVo> getTenantAccountFlow(Integer pageNum, Integer pageSize, TenantAccountFlowQueryVo tenantAccountFlowQueryVo);

    /** 分页获取车队账户交易（转帐）银行流水
     * */
    IPage<AccountFlowListVo> getTenantAccountTranFlow(Integer pageNum, Integer pageSize, TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo);

    /** 分页获取个人账户银行流水（Wx）
     * */
    List<AccountFlowListVo> getUserAccountFlowByWx(Long userId, String yearAndMonth, String businessType);

    /** 分页获取车队账户银行流水（Wx）
     * */
    List<AccountFlowListVo> getTenantAccountFlowByWx(Long tenantId,String yearAndMonth,String businessType);

    /** 获取账户交易电子回单
     * */
    BankReceiptVo getElectronicReceipt(String respNo, String tranType);

    /** 导出个人账户银行流水
     * */
    void userAccFlowExport(AccountFlowQueryVo accountFlowQueryVo, ImportOrExportRecords record);

    /** 导出车队账户银行流水
     * */
    void tenantAccFlowExport(TenantAccountFlowQueryVo tenantAccountFlowQueryVo,ImportOrExportRecords record);

    /** 导出平台账户转帐银行流水(对应平台支付)
     * */
    void tenantAccTranFlowExport(TenantAccountTranFlowQueryVo tenantAccountTranFlowQueryVo, ImportOrExportRecords record);

    /** 同步账户退票记录
     * */
    void syncRefundRecord(String clearDate);

    /** 同步账户充值记录（包含系统自动识别和手工调整）
     * */
    void syncRechargeRecord(String tranDate);

    /** 同步账户交易历史记录（包含：提现、转帐、调帐）
     * */
    void syncTranRecord(String tranDate);

    /** 同步账户提现最终交易状态
     * */
    void syncWithdrawTranStatus();

    /** 子商户注资回调接口
     * */
    void mbrChargeFundCallBack(Map<String, Object> param);

    /** 匿名资金回调接口
     * */
    void itaFundCallBack(Map<String, Object> param);

    /** 提现回调接口
     * */
    void withdrawCallBack(Map<String, Object> param);

    BankFlowDetailsAppDto getBankDetailsToApp(BankFlowDetailsAppVo bankFlowDetailsAppVo, String accessToken);

    BankFlowDownloadUrlVo getBankFlowDownloadUrl(BankFlowDownVo bankFlowDownVo);

}
