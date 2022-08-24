package com.youming.youche.finance.provider.mapper.munual;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.munual.MunualPaymentInfo;
import com.youming.youche.finance.domain.munual.MunualPaymentSumInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.DueDateDetailsCDDto;
import com.youming.youche.finance.dto.OverdueReceivableDto;
import com.youming.youche.finance.dto.PayableDayReportFinanceDto;
import com.youming.youche.finance.dto.PayableMonthReportFinanceDto;
import com.youming.youche.finance.dto.order.PayoutInfoDto;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.finance.vo.order.QueryDouOverdueVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zengwen
 * @date 2022/4/7 9:39
 */
@Mapper
public interface PayoutIntfMapper extends BaseMapper<PayoutIntf> {

    List<MunualPaymentInfo> getPayOutIntfsList(@Param("queryPayoutIntfsVo") QueryPayoutIntfsVo queryPayoutIntfsVo);

    Integer getPayOutIntfsCount(@Param("queryPayoutIntfsVo") QueryPayoutIntfsVo queryPayoutIntfsVo);

    List<MunualPaymentSumInfo> getPayOutIntfsSum(@Param("queryPayoutIntfsVo") QueryPayoutIntfsVo queryPayoutIntfsVo);

    /**
     * 应收逾期（尾款到期,到付款,预付）
     */
    List<OverdueReceivableDto> doQueryDouOverdue(@Param("vo") QueryDouOverdueVo queryDouOverdueVo, @Param("tenantId") Long tenantId);

    /**
     * 应收逾期（尾款到期,到付款,预付）重构
     */
    List<OverdueReceivableDto> doQueryDouOverdues(@Param("vo") QueryDouOverdueVo queryDouOverdueVo, @Param("tenantId") Long tenantId);


    Page<DueDateDetailsCDDto> getDueDateDetailsCD(Page<DueDateDetailsCDDto> page,
                                                  @Param("busiCode") String busiCode,
                                                  @Param("name") String name,
                                                  @Param("isTenant") Boolean isTenant,
                                                  @Param("tenantId") Long tenantId,
                                                  @Param("userType") Integer userType);

    Integer getPayoutInfoBusiCount(@Param("txnType") String txnType,@Param("createDate") String createDate);

    List<PayoutIntf> getPayoutInfoBusiList(@Param("txnType") String txnType, @Param("createDate") String createDate, @Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    Integer getPayoutInfoCount(@Param("txnType") String txnType,@Param("createDate") String createDate);

    List<PayoutIntf> getPayoutInfoList(@Param("txnType") String txnType, @Param("createDate") String createDate, @Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    Integer updatePayManagerState(@Param("flowId") Long flowId);

    Integer doQueryConfirmMoney(@Param("completeTimeStart") String completeTimeStart);

    List<PayoutIntf> doQueryConfirmMoneyList(@Param("completeTimeStart") String completeTimeStart, @Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    Integer getOnlineToOfflineCount(@Param("txnType") String txnType);

    List<PayoutIntf> getOnlineToOfflineList(@Param("txnType") String txnType, @Param("currentPage") int currentPage, @Param("pageSize") int pageSize);

    Page<PayoutInfoDto> getOverdueCD(Page<PayoutInfoDto> page,
                                     @Param("payObjId") Long payObjId,
                                     @Param("userId") Long userId,
                                     @Param("name") String name,
                                     @Param("orderId") String orderId,
                                     @Param("businessNumbers") String businessNumbers,
                                     @Param("busiIds") List<String> busiIds,
                                     @Param("isTenant") Boolean isTenant,
                                     @Param("payTenantId") Long payTenantId,
                                     @Param("payUserType") Integer payUserType,
                                     @Param("state") String state,
                                     @Param("states") List<String> states);

    Long getOverdueCDSum(@Param("payObjId") Long payObjId,
                         @Param("userId") Long userId,
                         @Param("name") String name,
                         @Param("orderId") Long orderId,
                         @Param("businessNumbers") String businessNumbers,
                         @Param("busiIds") List<String> busiIds,
                         @Param("isTenant") Boolean isTenant,
                         @Param("payTenantId") Long payTenantId,
                         @Param("payUserType") Integer payUserType,
                         @Param("state") String state,
                         @Param("states") List<String> states);

    /**
     * 获取指定车队指定收款人应付逾期逾期总金额
     */
    Long getOverdueSum(@Param("userId") Long userId,
                       @Param("isTenant") Boolean isTenant,
                       @Param("payTenantId") Long payTenantId,
                       @Param("payUserType") Integer payUserType);

    /**
     * 财务工作台 应付账户 已付金额
     */
    List<WorkbenchDto> getTableFinancialPayablePaidAmount();

    /**
     * 财务工作台  应付账户  剩余金额
     */
    List<WorkbenchDto> getTableFinancialPayableSurplusAmount();

    /**
     * 财务工作台  应收逾期金额
     */
    List<WorkbenchDto> getTableFinancialOverdueReceivablesAmount();

    /**
     * 财务工作台  应付逾期金额
     */
    List<WorkbenchDto> getTableFinancialOverduePayableAmount();

    /**
     * 财务工作台  代付款金额
     */
    List<WorkbenchDto> getTableFinancialPendingPaymentAmount();

    /**
     * 老板工作台  付款审核数量
     */
    List<WorkbenchDto> getTableBossPaymentReviewAmount();

    /**
     * 获取应付日报数据
     */
    List<PayableDayReportFinanceDto> getPayableDayReport();

    /**
     * 获取应付月报
     */
    List<PayableMonthReportFinanceDto> getPayableMonthReport();

    Page<DueDateDetailsCDDto> selectOr(@Param("orderId") Long orderId, @Param("name")String name,@Param("isTenant") Boolean isTenant,
                                       @Param("businessNumbers")String businessNumbers,@Param("tenantId") Long tenantId,
                                       @Param("busiId")List<String> busiId,@Param("state")String state,@Param("states")List<String> states,
                                       @Param("userId") Long userId, Page<Object> objectPage);

    Page<DueDateDetailsCDDto> selectOrS(@Param("userId")Long userId, @Param("orderId")Long orderId, @Param("name")String name,
                                        @Param("businessNumbers")String businessNumbers, @Param("state") String state,@Param("states")List<String> states,
                                        @Param("sourceTenantId")Long sourceTenantId, @Param("userType")String userType,
                                        @Param("busiId")List<String> busiId,
                                        Page<Object> objectPage);

    Long selectOrSsum(@Param("userId")Long userId, @Param("orderId")Long orderId, @Param("name")String name,
                      @Param("businessNumbers")String businessNumbers, @Param("state") String state,@Param("states")List<String> states,
                      @Param("sourceTenantId")Long sourceTenantId, @Param("userType")String userType,
                      @Param("busiId")List<String> busiId);

    Page<PayoutInfoDto> getOverdueCDs(Page<PayoutInfoDto> page,
                                      @Param("payObjId") Long payObjId,
                                      @Param("userId") Long userId,
                                      @Param("name") String name,
                                      @Param("orderId") Long orderId,
                                      @Param("businessNumbers") String businessNumbers,
                                      @Param("busiIds") List<String> busiIds,
                                      @Param("isTenant") Boolean isTenant,
                                      @Param("payTenantId") Long payTenantId,
                                      @Param("payUserType") Integer payUserType,
                                      @Param("state") String state,
                                      @Param("states") List<String> states);

}
