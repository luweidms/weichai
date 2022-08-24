package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.dto.AccountBankRelDto;
import com.youming.youche.finance.dto.OaLoanOutDto;
import com.youming.youche.finance.dto.order.OrderSchedulerDto;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.system.domain.mycenter.AccountBankRel;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 借支信息表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-02-07
 */
public interface OaLoanMapper extends BaseMapper<OaLoan> {


    Page<OaLoanOutDto> selectAllByMore(Page<OaLoanOutDto> page, @Param("waitDeal") Boolean waitDeal, @Param("queryType") Integer queryType,
                                        @Param("oaLoanId") String oaLoanId, @Param("state") Integer state,
                                        @Param("loanSubject") Integer loanSubject, @Param("userName") String userName,
                                        @Param("orderId") String orderId, @Param("plateNumber") String plateNumber,
                                        @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                        @Param("flowId") String flowId, @Param("dataPermissionIds") List<Long> dataPermissionIds,
                                        @Param("subOrgList") List<Long> subOrgList, @Param("subjects") List<String> subjects,
                                        @Param("tenantId")Long tenantId,
                                        @Param("acctName")String acctName,
                                        @Param("mobilePhone")String mobilePhone,
                                        @Param("busiIdByUserId") List<Long> busiIdByUserId,
                                       @Param("aBoolean") Boolean aBoolean);

    /**
     *
     * @param oaLoanId 借支号
     * @return 借支详情
     */
    OaLoanOutDto statisticsMenuTab (@Param("oaLoanId") String oaLoanId );

    /** 方法说明  核销查询
     *
     * @param page
     * @param waitDeal
     * @param queryType
     * @param oaLoanId
     * @param state
     * @param loanSubject
     * @param userName
     * @param orderId
     * @param plateNumber
     * @param startTime
     * @param endTime
     * @param flowId
     * @param dataPermissionIds
     * @param subOrgList
     * @param subjects
     * @param tenantId
     * @param acctName
     * @param mobilePhone
     * @return
     */
    IPage<OaLoanOutDto> selectCancelAll(Page<OaLoanOutDto> page, @Param("waitDeal") Boolean waitDeal, @Param("queryType") Integer queryType,
                                          @Param("oaLoanId") String oaLoanId, @Param("state") Integer state,
                                          @Param("loanSubject") Integer loanSubject, @Param("userName") String userName,
                                          @Param("orderId") String orderId, @Param("plateNumber") String plateNumber,
                                          @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                          @Param("flowId") String flowId, @Param("dataPermissionIds") List<Long> dataPermissionIds,
                                          @Param("subOrgList") List<Long> subOrgList, @Param("subjects") List<String> subjects,
                                          @Param("tenantId")Long tenantId,
                                          @Param("acctName")String acctName,
                                          @Param("mobilePhone")String mobilePhone,
                                          @Param("busiIdByUserId") List<Long> busiIdByUserId);

    /**
     *  接口说明 核销报表
     * @param page
     * @param startTime
     * @param endTime
     * @param loanType
     * @param loanSubject
     * @param loanClassify
     * @param orderId
     * @param oaLoanId
     * @param userName
     * @param mobilePhone
     * @param acctName
     * @param amountStar
     * @param amountEnd
     * @param plateNumber
     * @param queryType
     * @param state
     * @param flowId
     * @param noPayedStar
     * @param noPayedEnd
     * @param subOrgList//获取当前部门以及所有子部门
     * @param subjects
     * @param tenantId
     * @return
     */
    IPage<OaLoanOutDto> queryOaLoanTable(Page<OaLoanOutDto> page,
                                         @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime,
                                         @Param("loanType") Integer loanType, @Param("loanSubject") Integer loanSubject,
                                         @Param("loanClassify") Integer loanClassify, @Param("orderId") String orderId,
                                         @Param("oaLoanId") String oaLoanId, @Param("userName") String userName,
                                         @Param("mobilePhone") String mobilePhone, @Param("acctName") String acctName,
                                         @Param("amountStar") Long amountStar, @Param("amountEnd") Long amountEnd,
                                         @Param("plateNumber") String plateNumber, @Param("queryType") Integer queryType,
                                         @Param("state") Integer state, @Param("flowId") String flowId,
                                         @Param("noPayedStar") Long noPayedStar,
                                         @Param("noPayedEnd") Long noPayedEnd,
                                         @Param("subOrgList") List<Long> subOrgList,@Param("subjects")List<String> subjects,
                                         @Param("tenantId") Long tenantId);

    // 核销统计
    Page<OaLoanOutDto> queryOaLoanTableSum(Page<OaLoanOutDto> page,
                                           @Param("tenantId")Long tenantId,
                                           @Param("noPayedStar")Long noPayedStar,
                                           @Param("noPayedEnd") Long noPayedEnd,
                                           @Param("startTime") LocalDateTime startTime,
                                           @Param("userName") String userName,
                                           @Param("acctName") String acctName,
                                           @Param("mobilePhone") String mobilePhone,
                                           @Param("amountStar") Long amountStar,
                                           @Param("amountEnd") Long amountEnd,
                                           @Param("queryType") Integer queryType,
                                           @Param("orderId") String orderId,
                                           @Param("plateNumber") String plateNumber,
                                           @Param("endTime") LocalDateTime endTime,
                                           @Param("loanSubject") Integer loanSubject,
                                           @Param("oaLoanId") String oaLoanId,
                                           @Param("flowId") String flowId,
                                           @Param("state") Integer state,
                                           @Param("subOrgList") List<Long> subOrgList,
                                           @Param("subjects")List<String> subjects );


    Page<AccountBankRelDto> getSelfAndBusinessBank(Page<AccountBankRelDto> page,
                                                   @Param("userName")String userName,
                                                   @Param("mobilePhone")String mobilePhone,
                                                   @Param("tenantId")Long tenantId);

    Page<OrderSchedulerDto> queryOrderByCarDriver(Page<OrderSchedulerDto> page,
                                                  @Param("userName")String userName,
                                                  @Param("mobilePhone")String mobilePhone,
                                                  @Param("subOrgList")List<Long> subOrgList,
                                                  @Param("orderState")Integer orderState,
                                                  @Param("tenantId") Long tenantId
                                                );

    Page<OrderSchedulerDto> queryOrderByCarDriverHis(Page<OrderSchedulerDto> page,
                                                  @Param("userName")String userName,
                                                  @Param("mobilePhone")String mobilePhone,
                                                  @Param("subOrgList")List<Long> subOrgList,
                                                  @Param("orderState")Integer orderState,
                                                  @Param("tenantId") Long tenantId
                                                 );

    /**
     * 营运工作台  费用借支 待我审(司机借支)
     */
    List<WorkbenchDto> getTableCostBorrowDriverCount(@Param("subjects") List<String> subjects);

    /**
     * 营运工作台  费用借支  待我审(员工借支)
     */
    List<WorkbenchDto> getTableCostBorrowStaffCount(@Param("subjects") List<String> subjects);

    /**
     * 营运工作台  费用借支 我发起(司机借支)
     */
    List<WorkbenchDto> getTableCostBorrowMeDriverCount(@Param("subjects") List<String> subjects);

    /**
     * 营运工作台  费用借支 我发起(员工借支)
     */
    List<WorkbenchDto> getTableCostBorrowMeStaffCount(@Param("subjects") List<String> subjects);

    Integer doQueryAllPayManager(@Param("tenantId") Long tenantId, @Param("lidStr") String lidStr, @Param("orgStr") String orgStr);

    List<OaLoan> selectOr(@Param("oilExcDto") OilExcDto oilExcDto);

    List<OaLoan> queryOaLoan(@Param("userId") Long userId,
                             @Param("tenantId") Long tenantId,
                             @Param("loanType") List<Integer> loanType,
                             @Param("carPhone") String carPhone,
                             @Param("orderId") Long orderId);

}
