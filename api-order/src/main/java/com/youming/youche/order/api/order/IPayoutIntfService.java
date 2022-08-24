package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.AcBusiOrderLimitRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.BankDto;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.PayoutInfoDto;
import com.youming.youche.order.dto.PayoutInfosOutDto;
import com.youming.youche.order.dto.PinganBankInfoOutDto;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.dto.WXShopDto;
import com.youming.youche.order.vo.QueryPayManagerVo;
import com.youming.youche.system.dto.AuditOutDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 支出接口表(提现接口表) 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface IPayoutIntfService extends IBaseService<PayoutIntf> {

    /**
     * 生成平安虚拟到虚拟划拨转账记录
     * @param userId 收款对象id  必填
     * @param isDriver 0司机 1服务商  2租户 3HA虚拟  4HA实体 必填
     * @param txnType  OrderAccountConst.TXN_TYPE
     * @param txnAmt 交易金额，单位分  必填
     * @param tenantId 交易金额，单位分  必填
     * @param vehicleAffiliation 必填         0不开票  1承运方开票  非0和1为平台票
     * @param orderId 订单号 必填
     * @param payTenantId 打款车队id  非必填
     * @param isAutomatic 是否系统自动打款,0手动核销，1系统自动打款  必填
     * @param isTurnAutomatic 是否为手动转为系统自动打款 0:不是 1:是   非必填 默认1
     * @param payObjId  打款对象id  必填(userId)
     * @param payType 打款对象类型 0司机 1服务商  2租户 3HA虚拟  4HA实体   必填   OrderAccountConst.PAY_TYPE
     * @param busiId 业务ID 对应EnumConsts.PayInter  必填
     * @param subjectsId 科目ID subjects_info表  必填
     * @param billServiceFee 开票服务费
     * @return
     * @throws Exception
     */
    PayoutIntf createPayoutIntf(Long userId, Integer isDriver, String txnType, Long txnAmt, Long tenantId, String vehicleAffiliation, Long orderId, Long payTenantId, Integer isAutomatic,
                                Integer isTurnAutomatic, Long payObjId, Integer payType, Long busiId, Long subjectsId,String oilAffiliation,Integer payUserType,Integer receUserType,Long billServiceFee,String token);

    /***
     * // userId  收款对象id  必填
     * isDriver   0司机 1服务商  2租户 3HA虚拟  4HA实体 必填
     //txnAmt  交易金额，单位分  必填
     //tenantId 租户ID  非必填 默认取当前租户ID
     // vehicleAffiliation         必填         0不开票  1承运方开票  10平台开票    枚举(OrderAccountConst.VEHICLE_AFFILIATION)
     //orderId 订单号 必填
     //payTenantId    打款车队id  非必填
     //isAutomatic   是否系统自动打款,0手动核销，1系统自动打款  必填
     //isTurnAutomatic 是否为手动转为系统自动打款 0:不是 1:是   非必填 默认1
     //payObjId 打款对象id  必填
     //payType  打款对象类型 0司机 1服务商  2租户 3HA虚拟  4HA实体   必填   OrderAccountConst.PAY_TYPE
     //busiId 业务ID 对应EnumConsts.PayInter  必填
     //subjectsId //科目ID subjects_info表  必填

     * 保存需要从虚拟账户转账到虚拟账户的打款记录
     * TXN_TYPE 200
     * @param payoutIntf
     * @throws Exception
     */
    void doSavePayOutIntfVirToVir(PayoutIntf payoutIntf,String accessToken);

    /**
     * 收款人信息
     * @param payoutIntf
     * @param accessToken
     */
    void doSavePayoutInfoExpansion(PayoutIntf payoutIntf,String accessToken);

    /**
     * 获取支出信息
     * @param businessId
     * @param subjectsId
     * @param orderId
     * @return
     */
    List<PayoutIntf> queryPayoutIntf(Long businessId, Long subjectsId, Long orderId);


    /**
     * 获取支出信息
     * @param subjectsId
     * @param orderId
     * @return
     */
    List<PayoutIntf> queryPayoutIntf(List<Long> subjectsId, Long orderId);
    /***
     * 改单支付增加的补贴给司机
     * @param acOrderSubsidyIn
     * @throws Exception
     */
    void payAddSubsidy(AcOrderSubsidyInDto acOrderSubsidyIn, LoginInfo user,String token);

    /***
     * 支付补贴给司机
     */
    void paySubsidy(AcOrderSubsidyInDto acOrderSubsidyIn, LoginInfo loginInfo,String token);

    /**
     * 判断资金是否走过资金
     *
     * @param orderId
     * @return true 走过资金，false 未走过资金
     */
    boolean judgeOrderIsPaid(Long orderId);

    /**
     * 根据流水号和租户id查询提现记录
     * @param flowId  流水号
     * @param tenantId	租户id
     * @return PayoutIntf
     * @throws Exception
     */
     PayoutIntf getPayoutIntfPay(Long flowId, Long tenantId,String accessToken);

    /**
     * 计算服务费
     * @param cash
     * @param flowId
     * @param accessToken
     * @return
     */
     Long queryOrdServiceFee(Long cash,Long flowId,String accessToken);

    /**
     * 查询服务费
     * @param cash
     * @param flowId
     * @return
     */
    Map doQueryServiceFee(Long cash, Long flowId,String accessToken) ;

    /**
     * 已支付运费
     * @param orderId
     * @param accessToken
     * @return
     */
    long getPayoutIntfBySubFee(Long orderId,String accessToken);

    /**
     * 查询支出信息
     * @param flowIds
     * @return
     */
    List<PayoutIntf> getPayoutIntf(List<Long> flowIds);

    /**
     * 获取支出信息
     * @param orderId
     * @return
     */
    PayoutIntf getPayoutIntf(Long orderId);

    /**
     * 会员体系改造结束
     * @param userId
     * @param accountNo
     * @param userType
     * @return
     */
    PinganBankInfoOutDto getPinganBankInfoOutRemoteCall(Long userId, String accountNo, Integer userType);

    /**
     * 根据Xid查询交易记录
     */
    PayoutIntf getPayOUtIntfByXid(long xid);

    /***
     * // userId  收款对象id  必填
     * isDriver   0司机 1服务商  2租户 3HA虚拟  4HA实体 必填
     //bankCode 银行名称  必填
     //province 省份  必填
     //city 地市  必填
     //accNo  账号 必填
     //accName 账户名称  必填
     //txnAmt  交易金额，单位分  必填
     //tenantId 租户ID  非必填 默认取当前租户ID
     // vehicleAffiliation        必填          0不开票  1承运方开票  10平台开票    枚举(OrderAccountConst.VEHICLE_AFFILIATION)
     //orderId 订单号 必填
     //payTenantId    打款车队id  必填
     //isAutomatic   是否系统自动打款,0手动核销，1系统自动打款  必填
     //isTurnAutomatic 是否为手动转为系统自动打款 0:不是 1:是   非必填 默认1
     //payObjId 打款对象id  必填
     //payType  打款对象类型 0司机 1服务商  2租户 3HA虚拟  4HA实体   必填   OrderAccountConst.PAY_TYPE
     //busiId 业务ID 对应EnumConsts.PayInter  必填
     //subjectsId //科目ID subjects_info表  必填
     * 保存需要从HA转账到实体卡的打款记录
     * TXN_TYPE 100
     * @param payoutIntf
     * @throws Exception
     */
    void doSavePayOutIntfHAToHAEn(PayoutIntf payoutIntf);

    /***
     * // userId  收款对象id  必填
     * isDriver   0司机 1服务商  2租户 3HA虚拟  4HA实体 必填
     //bankCode 银行名称  必填
     //province 省份  必填
     //city 地市  必填
     //accNo  账号 必填
     //accName 账户名称  必填
     //txnAmt  交易金额，单位分  必填
     //tenantId 租户ID  非必填 默认取当前租户ID
     // vehicleAffiliation        必填          0不开票  1承运方开票  10平台开票    枚举(OrderAccountConst.VEHICLE_AFFILIATION)
     //orderId 订单号 必填
     //payTenantId    打款车队id  必填
     //isAutomatic   是否系统自动打款,0手动核销，1系统自动打款  必填
     //isTurnAutomatic 是否为手动转为系统自动打款 0:不是 1:是   非必填 默认1
     //payObjId 打款对象id  非必填
     //payType  打款对象类型 0司机 1服务商  2租户 3HA虚拟  4HA实体   必填   OrderAccountConst.PAY_TYPE
     //busiId 业务ID 对应EnumConsts.PayInter  非必填
     //subjectsId //科目ID subjects_info表  非必填
     * 保存需要从HA转账到实体卡的打款记录
     * TXN_TYPE 100
     * @param payoutIntf
     * @throws Exception
     */
    void doSavePayOutIntfEnToEn(PayoutIntf payoutIntf);

    /***
     *
     * // userId  收款对象id  必填
     * isDriver   0司机 1服务商  2租户 3HA虚拟  4HA实体 必填
     //bankCode 银行名称  必填
     //province 省份  必填
     //city 地市  必填
     //accNo  账号 必填
     //accName 账户名称  必填
     //txnAmt  交易金额，单位分  必填
     //tenantId 租户ID  非必填 默认取当前租户ID
     // vehicleAffiliation       必填           0不开票  1承运方开票  10平台开票    枚举(OrderAccountConst.VEHICLE_AFFILIATION)
     //orderId 订单号 必填
     //payTenantId    打款车队id  非必填
     //isAutomatic   是否系统自动打款,0手动核销，1系统自动打款  必填
     //isTurnAutomatic 是否为手动转为系统自动打款 0:不是 1:是   非必填 默认1
     //payObjId 打款对象id  必填
     //payType  打款对象类型 0司机 1服务商  2租户 3HA虚拟  4HA实体   必填   OrderAccountConst.PAY_TYPE
     //busiId 业务ID 对应EnumConsts.PayInter  必填
     //subjectsId //科目ID subjects_info表  必填
     * 保存需要从虚拟账户转账到实体卡的打款记录
     * （提现类，工资类，报销等）
     * TXN_TYPE 300
     * @param payoutIntf
     * @throws Exception
     */
    void doSavePayOutIntfVirToEn(PayoutIntf payoutIntf);

    /***
     * 处理200支付业务
     * @param payoutIntf
     * @throws Exception
     */
    boolean doPay200(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, List<BusiSubjectsRel> busiSubjectsRels, boolean isOnline, LoginInfo loginInfo,String token);

    /**
     * 查询该运单已打款金额
     * @param orderId
     * @return
     */
    long getPayoutIntfBySubFee(Long orderId);

    /**
     * 运单总服务费
     * @param cash
     * @param flowId
     * @param user
     * @return
     */
    Map doQueryServiceFee(Long cash, Long flowId,LoginInfo user);

    /**
     * 已支付服务费
     * @param orderId
     * @return
     */
    long getPayoutIntfServiceFee(Long orderId);

    /* 保存需要从虚拟账户转账到虚拟账户的打款记录
     * TXN_TYPE 200
     * @param payoutIntf
     * @throws Exception
     */
    void doSavePayOutIntfVirToVirNew(PayoutIntf payoutIntf,PayoutIntf payParentIntf,String token);

    /**
     * 会员体系结束
     * @param payoutIntf
     * @param token
     */
    void doSavePayOutIntfForOA(PayoutIntf payoutIntf,String token);

    /**
     * 查询流水号
     * @param busiCode
     * @return
     */
    PayoutIntf  getPayoutIntfId(String busiCode );

    PinganBankInfoOutDto getBindBankAc(long userId, String accountNo, Integer userType);

    /**
     * 这个方法当启动缓存机制开关的时候调用
     * 扣减掉在途金额，包含发起状态
     */
    Long getPayoutIntfBalanceForRedis(Long userId, Integer bankType, String[] txnTypes,
                                      String accountNo, Integer userType, Integer payUserType);

    /**
     * 查询账户锁定业务的金额
     *
     * @param custAcctId   平安虚拟账户
     * @param businessType 业务类型 1电子油 2ETC  -1查询全部
     */
    Long getAccountLockSum(String custAcctId, int businessType);

    /**
     * @Description: 该函数的功能描述:该函数的功能描述:支付的时候收款方可以没有绑卡，付款方支付的金额记录待支付状态（锁定），查询支付的虚拟卡号锁定的金额
     */
    Long getPayUnDoPayAccount(String pinganAccId);

    /**
     * @param userId   资金转出方userId
     * @param bankType 资金转出账户类型 1对公收款 11对公付款 2对私收款 22对私付款
     * @Description: 该函数的功能描述:用于统计业务操作并且为发起银行资金转移的金额
     * <p>
     * 扣减掉在途金额
     * 其中包含：已操作提现未发起金额，操作提现发起失败金额，操作提现发起网络超时金额（包含请求超时、返回超时两种情况）
     * 发起成功后，平安银行实时扣减掉付款账户金额，失效的金额也是实时返回付款账户
     */
    Long getPayoutIntfBalance(Long userId, int bankType, String accountNo, Integer userType, Integer payUserType);

    /***
     * 保存平台服务费
     */
    void doSavePayPlatformServiceFee(PayoutIntf payoutIntf, String accessToken);


    /**
     * 现金打款判断收款方和付款方是否是同一个主体
     *  @param payoutIntf
     *  @param payoutIntfExpansion
     * true 是  false 不是
     */
    boolean judge(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion);

    /**
     * 平台票需要判断收付款账号是否是同一个主体
     * @param payoutIntf
     * @param payoutIntfExpansion
     * @return
     */
    Map judgeName(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion);

    /**
     *
     * @Function: com.business.pt.ac.intf.IPayOutIntfTF.java::startAuditProcess
     * @Description: 该函数的功能描述:启动审核流程
     * @param payoutIntf
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2019年4月10日 上午11:38:36
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     * 2019年4月10日        huangqb           v1.1.0               修改原因
     */
    public void startAuditProcess(PayoutIntf payoutIntf,String accessToken);

    /**
     * 根据业务单号获取支出信息
     * @param busiCode
     * @param txnType
     * @return
     */
    List<PayoutIntf> getPayoutIntfByBusiCode(String busiCode,String txnType);
    /**
     *
     * 接口编码:21119
     * @Description: 微信应收逾期明细
     * 接口入参
     * param userId    用户ID
     * param flowId    流水号
     * param name      车队名称
     * param businessNumber   业务类型
     * param state   状态
     * @return
     * @throws Exception
     */
    IPage getDueDateDetailsWX(Long userId, Long flowId, String name, String state, Long sourceTenantId, Integer userType,Long pageSize,Long pageNum,String accessToken);

    /**
     * 微信接口-商家-首页
     * @param accessToken
     * @return
     */
    WXShopDto getServiceOverView(String accessToken);

    /**
     * 获取打款账号
     * @param userId
     * @param accountNo
     * @param userType
     * @return
     */
    PinganBankInfoOutDto getPinganBankInfoOut(Long userId,String accountNo,Integer userType);

    /**
     * 应付逾期-列表查询 21120
     */
    Page<PayoutInfosOutDto> getOverdue(Long userId, Long orderId, String userType, String name, String businessNumbers, String state, Long sourceTenantId, Integer pageSize, Integer pageNum, String accessToken);
    /**
     * 应付逾期-同意线上退款21122
     */
    String confirmPaymentLine(Long flowId, String payAcctId, String receAcctId, String accessToken);
    /**
     * 接口编码21156
     * 应付逾期 付款详情
     */
    PayoutInfoDto paymentDetails(Long flowId, String accessToken);
    /**已线下退款
     * 接口编码 21123
     * param flowId  流水号
     * @return
     * @throws Exception
     */
    String confirmPaymentOffline(Long flowId, String accessToken);
    /**
     * 查询服务商某个月份开票金额
     * @param userId
     * @param month
     * @param fleetName
     * @param userType收款方用户类型
     * @param payUserType 付款方用户类型
     * @return
     * @throws Exception
     */
    Page<PayoutInfosOutDto>  billingDetailsByWx(Long userId, String month, String fleetName,
                                                Integer userType,Integer payUserType,
                                                Integer pageNum, Integer pageSize);
    /**接口编码21157
     * 判断金额是否足够
     * @param
     * @return
     * @throws Exception
     */
    BankDto balanceJudgmentCD(Long balance, Integer type, Integer userType, String payAcctId, String accessToken);
    /**
     * 接口编码：21167
     * 判断现金付款业务当前操作员是否是最后一个审核人
     */
    List<AuditOutDto> isLastPayCashAuditer(String busiIdString, String accessToken);

    /**
     * 微信付款申请审核通过接口编码21183
     */
    String updatePayManagerState(String desc, Integer chooseResult, Long payId,Integer state,String accessToken);

    /**
     * 根据userId 统计租户自己的应付应收逾期
     * @param userId
     * @param userType
     * @param payUserType
     * @return
     */
    Long getPayoutIntfOverdueBalance(Long userId, Integer userType,Integer payUserType);

    /**
     * 付款申请
     * @param queryPayManagerVo
     * @param pageSize
     * @param pageNum
     * @param accessToken
     * @return
     */
    Page<QueryPayManagerDto> doQueryAllPayManager(QueryPayManagerVo queryPayManagerVo,Integer pageSize, Integer pageNum, String accessToken);

    /**
     * 21201
     * @Description: 账户明细-即将到期明细
     * @param userId
     * @param orderId
     * @param startTime
     * @param endTime
     * @param sourceRegion
     * @param desRegion
     * @param userType
     * @param PageSize
     * @param PageNum
     * @param accessToken
     * @return
     */
    Page<OrderLimitOutDto> getAccountDetailsNoPay(String userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion, Integer userType, Integer PageSize, Integer PageNum, String accessToken);

    /**
     * 支付的时候收款方可以没有绑卡，付款方支付的金额记录待支付状态（锁定），查询收款方锁定的金额
     * @param userId
     * @param userType
     * @return
     */
    Long getPayUnDoReceiveAccount(Long userId, Integer userType);

    /**
     * 流程结束，审核通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    public void sucess(Long busiId,String desc,Map paramsMap,String accessToken);
    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId    业务的主键
     * @param desc      结果的描述
     * @return
     */
    public void fail(Long busiId,String desc,Map paramsMap,String accessToken);
}
