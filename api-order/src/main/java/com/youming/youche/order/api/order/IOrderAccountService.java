package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.dto.AccountDto;
import com.youming.youche.order.dto.LoanDetail;
import com.youming.youche.order.dto.OaLoanOutDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderAccountsDto;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.PeccancDetailDto;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.OrderAccountVO;
import com.youming.youche.order.vo.UserAccountOutVo;
import com.youming.youche.order.vo.UserAccountVo;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单账户表 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-18
 */
public interface IOrderAccountService extends IBaseService<OrderAccount> {
    /**
     * 查询司机油卡余额
     * @param userId 用户id
     * @param tenantId 用户归属租户id，没有就null或-1
     * @param userType 用户类型
     * @return 油卡余额
     * @throws Exception
     */
    Long queryOilBalance(Long userId,Long tenantId,Integer userType);


    /**
     * 查询用户所有账户金额
     * @param userId  用户编号
     * @param tenantId 租户ID
     * @param userType 用户类型
     * @return OrderAccountOut
     */
    OrderAccountBalanceDto getOrderAccountBalance(Long userId, String orderByType, Long tenantId, Integer userType);

    /**
     * 查询OrderAccount 只提供查询时用
     * @param userId
     * @param orderByType
     * @param sourceTenantId
     * @param userType
     * @return
     * @throws Exception
     */
     List<OrderAccount> getOrderAccountQuey(Long userId, String orderByType, Long sourceTenantId, Integer userType);


    /**
     * 资金帐户创建公共接口
     * @param userId  用户编号
     * @param vehicleAffiliation 资金渠道类型
     * @param tenantId 账户归属租户id
     * @param sourceTenantId 资金来源租户id
     * @param oilAffiliation 油资金渠道
     * @param userType 用户类型
     * @throws Exception
     * @return OrderAccount
     */
    OrderAccount queryOrderAccount(Long userId, String vehicleAffiliation, Long tenantId,
                                   Long sourceTenantId, String oilAffiliation,Integer userType);


    /**
     * 查询OrderAccount 只提供修改时用
     * @param userId
     * @param vehicleAffiliation
     * @param sourceTenantId
     * @param oilAffiliation
     * @param userType
     * @return
     * @throws Exception
     */
     OrderAccount queryOrderAccount(Long userId,String vehicleAffiliation,Long sourceTenantId,
                                          String oilAffiliation,Integer userType);

    /**
     * 查询OrderAccount 只提供查询时用
     * @param userId
     * @param vehicleAffiliation
     * @param sourceTenantId
     * @param oilAffiliation
     * @param userType
     * @return
     * @throws Exception
     */
     OrderAccount getOrderAccount(Long userId, String vehicleAffiliation,
                                        Long sourceTenantId,String oilAffiliation,
                                        Integer userType);



    /**
     * 查询OrderAccount 只提供修改时用
     * @param userId
     * @param orderByType
     * @param sourceTenantId
     * @param userType
     * @return
     * @throws Exception
     */
    List<OrderAccount> getOrderAccount(Long userId, String orderByType, Long sourceTenantId,Integer userType);

    /**
     *  amount  匹配金额
     *  userId 付款人用户id
     * @param vehicleAffiliation 资金渠道
     * @param orderId  订单号
     *  tenantId 租户id
     * @param driverUserId 收款人driverUserId
     * @param driverUserId businessId 科目大类
     * @param driverUserId subjectsId 科目小类
     */
    List<OrderOilSource> dealTemporaryFleetOil(OrderAccount fleetAccount, OrderOilSource oilSource, Long fleetUserId, long orderId, long fleetTenantId, int isNeedBill, String vehicleAffiliation, long driverUserId, Long businessId, Long subjectsId,String accessToken);

    /**
     * 异常补偿费用接口
     * @param userId
     * @param amountFee 金额单位(分)正数司机可用金额增加
     * @param orderId 订单编号
     * @param vehicleAffiliation 资金渠道
     * @param tenantId 租户ID
     * @throws Exception
     * @return void
     */
    void payForException(Long userId, String vehicleAffiliation, Long amountFee, Long orderId, Long tenantId, LoginInfo loginInfo,String token);

    /**
     * @param userId 用户id
     * @param billId 用户手机号
     * @param businessId 业务id
     * @param orderId 订单id
     * @param amount 费用
     * @param vehicleAffiliation 资金渠道
     * @param finalPlanDate 尾款到账日期
     */
    Map<String, String> setParameters(Long userId, String billId, Long businessId, Long orderId, Long amount, String vehicleAffiliation, String finalPlanDate);

    /**
     * 司机账户查询
     * @param out
     * @return
     */
    IPage<UserAccountVo> doAccountQuery(UserAccountOutVo out, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 司机账户汇总查询
     * @param out
     * @return
     */
    AccountDto doAccountQuerySum(UserAccountOutVo out, String accessToken);


    /**
     * 获取司机油账户余额
     * @param userId  用户编号
     * @param
     * @param userType 用户类型
     * @return OrderAccountOut
     */
    OrderAccountBalanceDto getDriverOil(Long userId, String orderByType, String accessToken, Integer userType);


    void updateByA(OrderAccountVO orderAccountVO);

    void OilCardList1(String accessToken, ImportOrExportRecords record, UserAccountOutVo userAccountOutVo);

    /**
     * @param userId             用户编号
     * @param turnMonth          转现月份
     * @param turnType           转现类型(1油转现，2etc转现)
     * @param turnOilType        1油转移到现金账户 2油转成实体油卡
     * @param turnEntityOilCard
     * @param turnBalance
     * @param turnDiscountDouble
     * @param userType           用户类型
     * @param accessToken
     */
    String saveOilAndEtcTurnCashNew(Long userId,
                                    String turnMonth,
                                    String turnType, String turnOilType,
                                    String turnEntityOilCard, Long turnBalance,
                                    Long turnDiscountDouble, Integer userType, String accessToken);

    /**
     * 查询OrderAccount 只提供修改时用
     * @param userId
     * @return
     */
    OrderAccount getOrderAccount(Long userId);

    /**
     * 处理用户的账户应收逾期金额
     * @param id
     * @param account
     */
    void upOrderAccountReceivableOverdueBalance(Long id, Long account);
    /***
     *
     * 接口编码:21200
     * @Description: 微信接口-账户明细(驻场号)
     * 接口入参：
     * @param
     *
     */
    IPage<ReceivableOverdueBalanceDto> getAccountDetailsR(String name, String accState, Integer userType, Integer pageSize, Integer pageNum, String accessToken);

    /**
     * 我的钱包-首页
     * @param accessToken
     * @return
     */
    OrderAccountOutVo getAccountSum(String accessToken);
    /***
     *
     * 接口编码:21003
     * @Description: 我的钱包-账户明细
     * 接口入参：
     * @param
     *
     */
    OrderAccountsDto getAccountDetails(Long userId, String accessToken);
    /***
     *
     * 接口编码:21006
     * @Description: 我的钱包-借支列表
     * 接口入参：
     * @param userId 用户id
     *
     * 接口出参：
     *
     *  userId	用户id	string
     *	items	列表	array<object>
     *  createDate	订单创建时间	string
     *	desRegionName	目的地	string
     *	fianlSts	处理状态	string	0初始1完成2失败
     *	finalPlanDate	到期时间	number
     *	noPayFinal	应付尾款(未付).	number
     *	orderFinal	尾款	number
     *	orderId	订单Id	string
     *	orderPay	预付款	number
     *	paidFinalPay	已付尾款(已付).	number
     *	plateNumber	车牌号	string
     *	sourceRegionName	出发地	string
     *	totalFee	运费	number
     *
     *
     */
    Page<OaLoanOutDto> getOaLoanList(Long userId, String queryMonth, String loanSubjectList, String stateList,
                                     Long orderId, Integer pageSize, Integer pageNum, String accessToken);
    /***
     *
     * 接口编码:21008
     * @Description: 我的钱包-账户明细-押金明细
     * 接口入参：
     * @param
     *
     */
    Page<OrderLimitOutDto> getAccountDetailsPledge(Long userId, String tenantId, Integer userType, Integer pageSize, Integer pageNum, String accessToken);

    /**
     * 22023  APP-借支扣款详情
     * @param tenantId
     * @param userId
     * @param settleMonth
     * @return
     */
    Page<LoanDetail> queryLoanDetail(Long tenantId, Long userId, String settleMonth,
                                     Integer pageNum,Integer pageSize);

    /**
     * 22024  APP-订单欠款
     * @param userId
     * @param settleMonth
     * @return
     */
    Map<String,Object>   queryOrderDebtDetail(Long userId, String settleMonth) throws ParseException;

    /**
     * 22025 违章罚款
     * @param userId
     * @param settleMonth
     * @return
     */
    List<PeccancDetailDto>  queryPeccancDetail(Long userId, String settleMonth);

}
