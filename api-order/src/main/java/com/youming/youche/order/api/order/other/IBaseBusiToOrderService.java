package com.youming.youche.order.api.order.other;

import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.UserDataInfoDto;
import com.youming.youche.order.dto.order.BankBalanceInfo;
import com.youming.youche.order.dto.order.PayReturnDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;

import java.util.List;
import java.util.Map;

/**
 * @author hzx
 * @date 2022/4/14 15:02
 */
public interface IBaseBusiToOrderService {

    void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, Boolean isOnline, String accessToken);

    void dealBusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, Boolean isOnline, String accessToken);

    /**
     * 自动发起提现
     *
     * @param payoutIntf
     * @param level
     */
    void doSavePayoutIntf(PayoutIntfDto payoutIntfDto, int level);

    void dealOABusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, boolean isOnline, String accessToken);

    void dealOABusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int level, boolean isOnline, String accessToken);

    void dealBusiOaLoanAvailable(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken);

    void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken);

    void dealBusiTwo(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, String accessToken);

    List busiToOrder(Map<String, String> inParam, List<BusiSubjectsRel> rels);

    void dealBusi(PayoutIntfDto payoutIntfDto, UserDataInfoDto[] userDataInfoDtos, int flg, String accessToken);

    void doSavePayOutIntfForOA(PayoutIntfDto payoutIntfDto,String accessToken);

    /**
     * 会员交易
     *
     * @param outUserId   付款用户ID
     * @param inUserId    收款用户ID
     * @param tranAmount  交易金额 (单位:分)
     * @param outbankType 付款方银行类型 1:对公收款账户余额，11:对公付款账户余额，2：对私收款账户余额，22对私付款账户余额
     * @param inbankType  收款方银行类型 1:对公收款账户余额，11:对公付款账户余额，2：对私收款账户余额，22对私付款账户余额
     * @return *        reqCode 0、成功；1、网络超时；2、余额不足；3、未绑定银行卡；4、其他异常 EnumConsts.PAY_RESULT_STS 2、余额不足
     * reqMess 失败错误信息；
     * thirdLogNo  交易流水
     * *        isException 是否需要抛异常(true是 false否)
     * accountNos accountNos[0]付款账户  accountNos[1]收款账户
     */
    PayReturnDto payMemberTransaction(long outUserId, long inUserId, long tranAmount, int outbankType,
                                      int inbankType, String thirdLogNo, boolean isException,
                                      String accountNoOut, String accountNoIn, Integer outUserType,
                                      Integer inUserType);

    /**
     * 根据用户ID查询银行可提现余额  带支付中心余额
     *
     * @param userId   用户ID
     * @param bankType 1:对公收款账户余额，11:对公付款账户余额，2：对私收款账户余额+支付中心金额，22对私付款账户余额
     * @return 金额 (单位：元)
     */
    BankBalanceInfo getBankBalanceToUserId(long userId, int bankType, String accountNo, Integer userType);

    void doSavePayoutInfoExpansion(PayoutIntfDto payoutIntfDto,String token);

    /***
     * 保存平台服务费
     */
    void doSavePayPlatformServiceFee(PayoutIntfDto payoutIntfDto, String accessToken);


    Double getPcBalance(long userId, Integer userType);

    /**
     * @param userId      用户ID
     * @param bankType    账户类型 1对公收款 11 对公付款 2对私收款 22对私付款
     * @param accountNo   银行账户
     * @param accountName 银行账户名称
     * @param acctNo      银行实体卡账户
     * @param bankType1   账户类型 1对公 0对私
     */
    BankBalanceInfo getBankBalanceToUserIdNo(long userId, int bankType, String accountNo,
                                             String accountName, String acctNo, int bankType1, Integer userType);

    /**
     * 根据用户ID查询银行可提现余额
     * @param userId 用户ID
     * @param bankType 1:对公收款账户余额，11:对公付款账户余额，2：对私收款账户余额(不包含支付中心金额)，22对私付款账户余额
     * @return 金额 (单位：元)
     * @throws Exception
     */
     BankBalanceInfo getBankBalanceToUserIdNo(long userId,int bankType,String accountNo,Integer userType);
}
