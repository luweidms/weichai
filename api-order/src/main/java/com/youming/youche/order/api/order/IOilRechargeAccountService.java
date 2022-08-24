package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.domain.TenantServiceRel;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.OilRechargeAccount;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import com.youming.youche.order.dto.PingAnBalanceDto;
import com.youming.youche.order.dto.order.AccountBankRelDto;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsOutDto;
import com.youming.youche.order.vo.OilRechargeAccountVo;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
public interface IOilRechargeAccountService extends IBaseService<OilRechargeAccount> {
    /**
     * @Description: 该函数的功能描述: 查询车队油账户余额（查充值账户的非抵扣油票余额、客户油、返利油、转移油余额、某收票主体充值账户抵扣油票余额）
     * @param userId 用户ID
     * @param tenantId 租户
     * @param userType 用户类型
     * @param acctNo 开票主体
     *
     * return
     * nonDeductOilBalance//充值账户的非抵扣油票余额
     * rebateOilBalance, //返利油
     * invoiceOilBalance, //抵扣油票余额
     * custOilBalance, //客户油
     * transferOilBalance //转移油余额
     * billLookUpBalance, //某开票主体抵扣票余额
     * unBillLookUpBalance, //某开票主体非抵扣票余额
     */
    OilRechargeAccountDto getOilRechargeAccount(Long userId, Long tenantId, Integer userType, String billLookUp, LoginInfo user);


    /**
     *
     * @Function: com.business.pt.ac.service.IOilRechargeAccountSV.java::getOilRechargeAccount
     * @Description: 该函数的功能描述:查询子母卡
     * @param userId
     * @return
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2019年6月17日 上午10:41:56
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     *
     */
     OilRechargeAccount getOilRechargeAccount(Long userId);

    /**
     * 获取各油来源账户中可使用的油
     * 油账户减去未支付的油
     * @return
     * @throws Exception
     */
    OilRechargeAccountDto querOilBalanceForOilAccountType(Long excludeOrderId,LoginInfo user);


    /**
     *
     * @Function: com.business.pt.ac.intf.IOpOilRechargeAccountTF.java::insertOilAccount
     * @Description: 该函数的功能描述:操作油账户明细流水
     * @param userId 用户编号
     * @param accountMap 账户明细主键+分配金额（如果交易是需要扣减的，金额是负数）
     * @param turnAmount 交易金额
     * @param orderNum 订单编号
     * @param busiType 业务类型：
     * @param batchId 批次号
     * @param subjectsId 业务科目编号
     * @param busiCode 业务编码
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2019年6月25日 下午1:57:17
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     * 2019年6月25日        huangqb           v1.1.0               修改原因
     */
     void insertOilAccount(Long userId, Map accountMap, Long turnAmount, String orderNum,
                           Integer busiType, Long batchId, Long subjectsId, String busiCode,
                           Map detailsFlows,LoginInfo user);



    /**
     * 分配油(开单、修改订单、司机账户充值)
     * @param userId
     * @param sourceUserId
     * @param amount
     * @param orderNum
     * @param subjectsId
     * @param tenantId
     * @param sourceRecordType
     * @param distributionType 分配类型 1获取油票（充值_非抵扣运输专票、授信充值），2获取运输专票（充值_抵扣运输专票），3已开票账户(返利、转移)
     * @throws Exception
     */
    Map<String, Object> distributionOil(Long userId, Long sourceUserId, Long amount,
                                        String orderNum, Long subjectsId, Long tenantId,
                                        Integer sourceRecordType, Integer distributionType,LoginInfo baseUser);


    /**
     * niejeiwei
     * 司机小程序
     * 客户油-小程序接口
     * 50050
     * @return
     */
    Page<OilRechargeAccountDto> queryOilAccountDetails(Long userId,
                                                       String billId,
                                                       String tenantName,
                                                       Integer userType,
                                                       Integer pageNum,
                                                       Integer pageSize,
                                                       String accessToken);


    /**
     * niejeiwei
     * 司机小程序
     * 授信列表-小程序接口
     * 50051
     * @return
     */
    Page<TenantServiceRel> queryCreditLineDetails(String accessToken,
                                                  String serviceName,
                                                  Integer pageNum,
                                                  Integer pageSize);

    /**
     * niejeiwei
     * 司机小程序
     * 预存资金-小程序接口
     * 50052
     * @return
     */
    Page<AccountBankRelDto> queryLockBalanceDetails(Long userId,
                                                    String accessToken,
                                                    Integer pageNum,
                                                    Integer pageSize);


    /**
     * niejiewei
     * 司机小程序
     * 充值账户-小程序接口
     * 50053
     * @param type
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<OilRechargeAccountDetailsOutDto> getOilRechargeAccountDetails(String accessToken, Integer type, Integer pageNum, Integer pageSize);

    /**
     *移除账户-小程序接口
     * niejiewei
     * 司机小程序
     * 50054
     * @param id
     * @return
     */
    PingAnBalanceDto removeRechargeAccount (Long id , String accessToken);

    /**
     * 移除充值油账户
     * @param accountId
     */
    void removeRechargeAccount(Long accountId);


    /**
     * 获取充值信息-小程序接口
     * niejiewei
     * 司机小程序
     * 50055
     * @return
     */
    OilRechargeAccountDto  getRechargeInfo(Long userId,String accessToken );

    /**
     * 司机小程序
     * niejeiwei
     * 50056
     * 确认充值-小程序接口
     * @param vo
     * @return
     */
    void  confirmRecharge(OilRechargeAccountVo vo ,String accessToken );

}
