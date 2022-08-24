package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.UserRepairMarginDto;
import com.youming.youche.order.dto.order.AccountPayRepairOutDto;
import com.youming.youche.order.dto.order.RepairAmountOutDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 维修保养记录表 服务类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
public interface IUserRepairMarginService extends IBaseService<UserRepairMargin> {
    /**
     * 查询到期列表
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<UserRepairMargin> queryUserRepairMargins(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize);

    /**
     * 根据流水号查询司机加油记录
     * @param flowId 用户id
     * @throws Exception
     * @return list
     */
    UserRepairMargin getUserRepairMargin(Long flowId);

    /**
     * 通过单号查维修保养记录
     * @param repairCode
     * @return
     */
    UserRepairMargin getUserRepairMarginByRepairCode(String repairCode);

    /**
     * 查找所有维修商未到期
     * @param getDate
     * @param state 0未到期，1已到期，2未到期转已到期失败
     * @return list
     * @throws Exception
     */
    List<UserRepairMargin> getUserRepairMargin(Date getDate, Integer[] states);

    /**
     *
     * 自付
     * 可以消费的金额，包含维修基金+可提现+即将到期 （目前没有维修基金）
     * 1、若该维修站是共享维修站，支持开票，则能使用所有资金
     * 2、若该维修站是共享维修站，不支持开票，则只能使用所有资金中不需要开票的金额（通过订单判断）
     * 3、若该维修站只和某车队合作，支持开票，则能使用所有资金（通过订单判断）但消费用户必须是归属此车队
     * 4、若该维修站只和某车队合作，不支持开票，则只能使用所有资金中不需要开票的金额 但消费用户必须是归属此车队
     * 5、若该维修站即合作车队的维修站，又是共享维修站，只是优先使用合作车队的金额
     * 维修单司机账户自付
     * @param userRepairInfo  维修记录
     * @param payWay 支付方式 支付方式：1为公司付 2为自付-账户 3为自付微信 4为自付现金
     * @param selectType 是否勾选了维修基金  0勾选了维修基金，1未勾选维修基金
     * @return AccountPayRepairOut
     * @throws Exception
     */
    AccountPayRepairOutDto payForRepairFee(UserRepairInfo userRepairInfo, String payWay, String selectType,String accessToken);

    /**
     * 查询司机在维修站可以维修金额
     * @param userId 消费用户id
     * @param serviceUserId 维修商id
     * @param productId 产品id
     * @param amountFee 消费金额
     * @param tenantId
     * @return
     * @throws Exception
     */
    RepairAmountOutDto queryAdvanceFeeByRepair(Long userId, Long serviceUserId, Long productId, Long amountFee, Long tenantId);

    /**
     * 操作维修基金
     * @param userRepairInfo
     * @param serviceProduct
     * @param sysOperator
     * @param sysOtherOperator
     * @param accountList
     * @param repairFlowList
     * @param sourceTenantIdList
     * @param inParm
     * @param serviceCharge
     * @param canUseRepair
     * @param isSharePorduct
     * @param isBill
     * @param isBelongToTenant
     * @param isNeedBill
     * @return
     * @throws Exception
     */
    Map<String, Object> operationOrderAccountRepair(UserRepairInfo userRepairInfo, ServiceProduct serviceProduct,
                                                    SysUser sysOperator , SysUser sysOtherOperator,
                                                    List<OrderAccount> accountList, List<UserRepairMargin> repairFlowList,
                                                    List<Long> sourceTenantIdList, Map<String,Object> inParm, String serviceCharge,
                                                    long canUseRepair, boolean isSharePorduct,
                                                    boolean isBill, boolean isBelongToTenant,
                                                    int isNeedBill, long soNbr,String accessToken);
    /**
     * 保存维修记录
     * @param userId
     * @param userBill
     * @param userName
     * @param costType
     * @param orderId
     * @param amount
     * @param oilPrice
     * @param oilRise
     * @param otherUserId
     * @param otherUserBill
     * @param otherName
     * @param vehicleAffiliation
     * @param tenantId
     * @param productId
     * @param isNeedBill
     * @return
     * @throws Exception
     */
    UserRepairMargin createUserRepairMargin(Long userId, String userBill, String userName, Integer costType,
                                            String orderId, Long amount,  Long otherUserId,
                                            String otherUserBill, String otherName, String vehicleAffiliation,
                                            Long tenantId, Long productId,
                                            Integer isNeedBill ,String accessToken);

    /**
     * niejiewei
     * 司机小程序
     * APP-维修保养交易-支付详情
     * 40027
     * @param repairId
     * @return
     */
    UserRepairMarginDto queryRepairMarginDetail (Long repairId);



}
