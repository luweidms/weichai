package com.youming.youche.order.provider.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.AccountDto;
import com.youming.youche.order.dto.BaseNameDto;
import com.youming.youche.order.dto.LoanDetail;
import com.youming.youche.order.dto.OaloanAndUserDateInfoDto;
import com.youming.youche.order.dto.OrderDebtDetail;
import com.youming.youche.order.dto.PeccancDetailDto;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;
import com.youming.youche.order.vo.UserAccountOutVo;
import com.youming.youche.order.vo.UserAccountVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 订单账户表Mapper接口
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-18
 */
public interface OrderAccountMapper extends BaseMapper<OrderAccount> {
    /**
     * 司机账户查询
     *
     * @param out
     * @return
     */
    List<UserAccountVo> doAccountQuery(@Param("out") UserAccountOutVo out);


    /**
     * 司机历史账户查询
     *
     * @param out
     * @return
     */
    Page<UserAccountVo> doAccountQueryHis(@Param("page") Page<UserAccountVo> page, @Param("out") UserAccountOutVo out);



    List<UserAccountVo> doAccountQueryS(@Param("out") UserAccountOutVo out);

    /**
     * 司机账户查询汇总
     *
     * @param out
     * @return
     */
    AccountDto doAccountQuerySum(@Param("out") UserAccountOutVo out);

    List<OrderAccount> findOrderAccount(@Param("userId") Long userId, @Param("sourceTenantId")
            Long sourceTenantId, @Param("userType") Integer userType);

    Page<BaseNameDto> selectFee(@Param("name") String name, @Param("accState") String accState, @Param("userType") Integer userType, @Param("tenantId")Long tenantId,@Param("userId")Long userId, Page<BaseNameDto> page);

    List<ReceivableOverdueBalanceDto> selectOr(@Param("sysTenantUserId") Long sysTenantUserId, @Param("tenantUserId") String tenantUserId, @Param("userType") Integer userType);

    Page<OaloanAndUserDateInfoDto> selectOrs(@Param("userId") Long userId, @Param("queryMonth") String queryMonth, @Param("subjectList")List<String> subjectList,@Param("loanSubjectList") String loanSubjectList,
                                             @Param("loanSubjectLists")List<String> loanSubjectLists,
                                             @Param("stateList") String stateList,@Param("stateLists")List<String> stateLists,
                                             @Param("orderId") Long orderId, Page<OaloanAndUserDateInfoDto> objectPage);

    Page<OrderLimit> selectOrsTwo(@Param("userId") Long userId, @Param("userType") Integer userType, @Param("tenantId") String tenantId, Page<OrderLimit> objectPage);

    /**
     * APP-借支扣款详情
     *
     * @param tenantId
     * @param userId
     * @param settleMonth
     * @return
     */
    Page<LoanDetail> queryLoanDetail(Page<LoanDetail> page,
                                     @Param("tenantId") Long tenantId,
                                     @Param("userId") Long userId,
                                     @Param("settleMonth") String settleMonth);


    /**
     * 22024  APP-订单欠款
     * @param userId
     * @param settleMonthData
     * @return
     */
    List<OrderDebtDetail> queryOrderDebtDetail(@Param("userId") Long userId,
                                         @Param("settleMonthData") String settleMonthData);


    /**
     * 违章罚款
     * @param userId
     * @param settleMonthData
     * @return
     */
    List<PeccancDetailDto>   queryPeccancDetail(@Param("userId") Long userId,
                                                @Param("settleMonthData") String settleMonthData);
}
