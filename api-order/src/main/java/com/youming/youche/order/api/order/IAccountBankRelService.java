package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.BankReceiptOutDto;
import com.youming.youche.order.dto.DirverxDto;
import com.youming.youche.order.dto.MarginBalanceDetailsOut;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.BillingDetailsOut;
import com.youming.youche.order.vo.AccountBankRelVo;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.mycenter.BankFlowDetailsAppOutDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IAccountBankRelService extends IBaseService<AccountBankRel> {

    /**
     * 根据车队id获取银行卡版本信息
     * @param tenantId
     * @return
     */
    List<AccountBankRel> getCollectAmount(Long tenantId);

    /**
     * 查询用户绑卡信息
     * @param userId
     * @param accountName
     * @param acctNo
     * @param bankType
     * @param userType
     * @return
     */
     List<AccountBankRel> getBankInfo(Long userId, String accountName, String acctNo, Integer bankType, Integer userType);

    /**
     * 获取车队默认的收款私户
     * @param userId
     * @param bankType
     * @param userType
     * @return
     */
    AccountBankRel getDefaultAccountBankRel(Long userId, Integer bankType,Integer userType);

    /**
     * 根据租户id和发票id查询用户银行卡信息
     * @param tenantId 租户id
     * @param billLookUp 发票抬头
     * @return
     */
    List<AccountBankRel> getAccountBankList(Long tenantId,String billLookUp);


    /**
     * 根据用户编号查询账号信息
     * @param userId 用户编号-必填
     * @param userType 用户类型-选填
     * @param bankType 账户类型-选填
     * @return
     */
    List<AccountBankRel> queryAccountBankRel(Long userId, Integer userType, Integer bankType);

    /**
     * 不需要修改
     * @param pinganAcctNo
     * @return
     * @throws Exception
     */
     AccountBankRel getAcctNo(String pinganAcctNo);
    /**
     * 是否有支行,true 有  false 无
     * @param accNo
     * @return
     */
     Boolean JudgeAmount(String accNo);

     /**
     * 用户绑定了多卡的情况下，判断所有的卡
     * @param userId
     * @return
     * @throws Exception
     */
     Boolean isUserBindCardAll(Long userId,String accessToken);
    /**
     * 查询用户下的银行列表
     * @param userId
     * @return
     */
    List<AccountBankRel> getBankListByUserId(Long userId);


    /**
     * WX接口-查询可转移金额
     * niejiewei
     *
     * @return
     */
    PingAnBalanceDto turnVirAmountUIByWx(String accessToken);

    /**
     * 司机小程序
     * WX接口-资金转移
     * niejiewei
     *
     * @param
     * @return
     */
    String turnVirAmount(Long userId, Long tranAmount, Integer accountType, String mesCode, String serialNo, String accountNo,
                         Integer userType, String accessToken);

    /**
     * niejiewei
     * 司机小程序
     * 50014
     * 微信接口-提现界面
     *
     * @return
     */
    PingAnBalanceDto withdrawCashUIByWx(String accessToken);

    /**
     * niejiewei
     * 小程序 微信接口-提现
     * 50015
     *
     * @param accountBankRelVo
     * @return
     */
    PingAnBalanceDto withdrawCashByWx(String accessToken, AccountBankRelVo accountBankRelVo);


    //用户针对哪个账户进行提现要传入accountType
    PingAnBalanceDto doWithdrawal(Long userId, Long amount, Integer accountType, String withdrawalsChannel,
                                  String accountNo, Integer userType, String accessToken);

    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支界面
     * 50016
     *
     * @return
     */
    PingAnBalanceDto advanceUIByWx(String accessToken);

    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支-查询预支手续费
     * 50017
     *
     * @return
     */
    PingAnBalanceDto getAdvanceFeeByWx(String accessToken, Long amountFee);

    /**
     * niejiewei
     * 司机小程序
     * 微信接口-预支-预支
     * 50018
     *
     * @return
     */
    PingAnBalanceDto confirmAdvanceByWx(String accessToken, AccountBankRelVo accountBankRelVo);


    /**
     * niejeiwei
     * 司机小程序
     * 微信接口-预支-可预支金额详情
     * 50019
     *
     * @param userId
     * @return
     */
    List<MarginBalanceDetailsOut> getMarginBalance(String accessToken, Long userId);

    /**
     * 20001
     * 查询账户列表
     *
     * @param bankType1 //0对私 1对公
     */
    List<BankBalanceInfo> queryBankBalanceInfoList(Integer userType, Long userId, Integer bankType,
                                                   String acctName, String acctNo,Integer bankType1, Integer isNeedHa);

    /**
     * niejeiwei
     * 微信接口-开票明细
     * 司机小程序
     * 50020
     *
     * @param userId
     * @param month
     * @param fleetName
     * @param userType
     * @param payUserType
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<BillingDetailsOut> billingDetailsByWx(Long userId, String month,
                                               String fleetName, Integer userType, Integer payUserType,
                                               Integer pageNum, Integer pageSize, String accessToken);



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
    Page<BankFlowDetailsAppOutDto> getBankDetailsToApp(Long userId, String month,
                                                       String queryType, Integer pageNum,
                                                       Integer pageSize, String accessToken);

    /**
     * 20002
     * 微信绑定账户列表
     */
    List<BankBalanceInfo> queryBankBalanceInfoListWX(String accessToken );



    /**
     * niejeiwei
     * 司机小程序
     * APP接口-我的钱包-收支明细-银行流水-银行回单
     * 50028
     * @param bankPreFlowNumber
     * @return
     */
    BankReceiptOutDto getBankReceiptToApp(	String bankPreFlowNumber,String accessToken);

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
    OrderAccountOutVo getAccountSumWX(String accessToken);




    /**
     * 获取银行流水下载地址接口 50060
     * niejeiwei
     *
     */
    String getBankFlowDownloadUrl (AccountBankRelVo vo ,String accessToken);


    boolean isUserBindCard(Long userId, UserDataInfo userDataInfo, Integer bankType);

    /**
     * 根据卡号查询引用该卡的用户类型
     * @param bankAcctNo - 银行实体卡号
     * @param pinganAcctNo - 平安虚拟账户
     * @param userId 用户编号-可选
     * @return
     * @throws Exception
     */
    List<DirverxDto> getAcctRelUserTypeList(String bankAcctNo, String pinganAcctNo, Long userId) ;
}
