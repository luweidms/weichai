package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.dto.PayoutInfoOutDto;

import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.PayoutInfosOutDto;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.vo.QueryPayManagerVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 支出接口表(提现接口表)Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
public interface PayoutIntfMapper extends BaseMapper<PayoutIntf> {
    /**
     * 这个方法当启动缓存机制开关的时候调用
     * 扣减掉在途金额，包含发起状态
     */
    Long getPayoutIntfBalanceForRedis(@Param("userId") Long userId, @Param("bankType") Integer bankType, @Param("txnTypes") String txnTypes,
                                      @Param("accountNo") String accountNo, @Param("userType") Integer userType, @Param("payUserType") Integer payUserType);

    /**
     * 查询账户锁定业务的金额
     *
     * @param custAcctId   平安虚拟账户
     * @param businessType 业务类型 1电子油 2ETC  -1查询全部
     */
    Long getAccountLockSum(@Param("custAcctId") String custAcctId, @Param("businessType") int businessType);

    /**
     * @Description: 该函数的功能描述:该函数的功能描述:支付的时候收款方可以没有绑卡，付款方支付的金额记录待支付状态（锁定），查询支付的虚拟卡号锁定的金额
     */
    Long getPayUnDoPayAccount(@Param("pinganAccId") String pinganAccId);

    Long getPayoutIntfBalance(@Param("userId") Long userId, @Param("bankType") Integer bankType,
                              @Param("accountNo") String accountNo, @Param("userType") Integer userType,
                              @Param("payUserType") Integer payUserType);

    Page<PayoutInfoOutDto> selectOrList(@Param("page") Page<PayoutInfoOutDto> page, @Param("userId")Long userId,
                      @Param("flowId")Long flowId, @Param("name")String name, @Param("sourceTenantId")Long sourceTenantId,
                      @Param("userType")Integer userType, @Param("states")List<String> states,@Param("state") String state );

    Page<PayoutInfosOutDto> selectBeOverdue(@Param("userId")Long userId, @Param("orderId")Long orderId, @Param("userType")String userType, @Param("name")String name,
                                            @Param("businessNumbers")String businessNumbers, @Param("busiIds")List<String> busiIds, @Param("state")String state,
                                            @Param("states")List<String> states,
                                            @Param("sourceTenantId")Long sourceTenantId, Page<PayoutInfoOutDto> page);

    Page<PayoutInfosOutDto> billingDetailsByWx( Page<PayoutInfosOutDto> page ,
                                                @Param("userId") Long userId,
                                                @Param("months")String months,
                                                @Param("userType")Integer userType,
                                                @Param("payUserType")  Integer payUserType,
                                                @Param("tenantId") List<Long> tenantId );

    Page<QueryPayManagerDto> select(@Param("queryPayManagerVo") QueryPayManagerVo queryPayManagerVo,
                                    Page<Object> objectPage);


    // 1查询作为收款 2 查询作为付款
    Long countOnFlowPayoutInfo( @Param("accNo")String accNo,
                                @Param("txnTypes")String[] txnTypes,
                                @Param("respCodes")String[] respCodes,
                                @Param("subjectsId")Long subjectsId,
                                @Param("type")Integer type);

    Long selectSum(@Param("userId")Long userId, @Param("userType")Integer userType, @Param("payUserType")Integer payUserType);

    Long selectSums(@Param("userId")Long userId, @Param("userType")Integer userType, @Param("payUserType")Integer payUserType);

    Long selectOr(Long userId, Integer userType);
}
