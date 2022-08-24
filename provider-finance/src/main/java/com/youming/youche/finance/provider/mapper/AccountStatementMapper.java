package com.youming.youche.finance.provider.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.domain.AccountStatement;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.finance.dto.AccountStatementUserDto;
import com.youming.youche.finance.dto.DoQueryBillReceiverPageListDto;
import com.youming.youche.finance.dto.OrderFeeDto;
import com.youming.youche.finance.dto.QueryAccountStatementOrdersDto;
import com.youming.youche.finance.vo.DoQueryBillReceiverPageListVo;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
* <p>
* Mapper接口
* </p>
* @author luona
* @since 2022-04-11
*/
public interface AccountStatementMapper extends BaseMapper<AccountStatement> {
    /**
     * 获取对账单列表
     * @param page
     * @param accountStatement
     * @param opType
     * @return
     */
    Page<AccountStatement> getAccountStatements(@Param("page") Page<AccountStatement> page, @Param("accountStatement") AccountStatement accountStatement, @Param("opType") Integer opType);

    /**
     * 获取对账单列表
     *
     * @param accountStatement
     * @param opType
     * @return
     */
    List<AccountStatement> getAccountStatementList(@Param("accountStatement") AccountStatement accountStatement, @Param("opType") Integer opType);

    /**
     * 获取指定月份 - 接收人 的对账单
     * @param billMonth
     * @param receiverPhone
     * @param tenantId
     * @return
     */
    List<AccountStatement> queryAccountStatement(@Param("billMonth") String billMonth, @Param("receiverPhone") Set<String> receiverPhone, @Param("tenantId") Long tenantId);


    /**
     * 获取当前车辆 租户 接收人在某个月份的最早账单
     *
     * @param flowId
     * @param tenantId
     * @param plateNumber
     * @param receiverPhone
     * @param receiverUserId
     * @return
     */
    Long queryLastBillForMonth(@Param("flowId") Long flowId, @Param("tenantId") Long tenantId, @Param("plateNumber") String plateNumber, @Param("receiverPhone") String receiverPhone, @Param("receiverUserId") Long receiverUserId, @Param("month") String month);

    //获取当前车辆、租户、接收人在某个月份最早的账单
    List<Long> selectLastBillForMonth(@Param("plateNumber") String plateNumber,
                                      @Param("tenantId") Long tenantId,
                                      @Param("flowId") Long flowId,
                                      @Param("receiverPhone") String receiverPhone,
                                      @Param("receiverUserId") Long receiverUserId,
                                      @Param("month") String month);

    Page<DoQueryBillReceiverPageListDto> doQueryBillReceiverPageList(@Param("vo") DoQueryBillReceiverPageListVo vo, Page<DoQueryBillReceiverPageListDto> page);

    /**
     * 分页获取招商对账单代收人信息
     */
    Page<AccountStatementUserDto> selectAccountStatementUserPage(@Param("collectionUserName") String collectionUserName,
                                                                 @Param("collectionUserPhone") String collectionUserPhone,
                                                                 @Param("tenantId") Long tenantId,
                                                                 @Param("monList") List<String> monList,
                                                                 Page<AccountStatementUserDto> page);


    AccountStatement selectAccountStatementByMonAndUserId(@Param("receiverUserId") Long receiverUserId, @Param("billMonth") String billMonth);

    List<Long> getAccountStatementOrderList(Long collectionUserId, List<String> monList, Long tenantId);

    Long getAccountStatementOrderListAgingFee(@Param("orderIds") List<Long> orderIds);

    List<OrderFeeDto> getAccountStatementOrderListAgingFeeGroup(@Param("orderIds") List<Long> orderIds);

    Long getAccountStatementOrderListTotalFee(@Param("orderIds") List<Long> orderIds);

    Long getAccountStatementOrderListExceptionIn(@Param("orderIds") List<Long> orderIds);

    List<OrderFeeDto> getAccountStatementOrderListExceptionInGroup(@Param("orderIds") List<Long> orderIds);

    Long getAccountStatementOrderListExceptionOut(@Param("orderIds") List<Long> orderIds);

    List<OrderFeeDto> getAccountStatementOrderListExceptionOutGroup(@Param("orderIds") List<Long> orderIds);

    Long getAccountStatementOrderListPaidFee(@Param("orderIds") List<Long> orderIds, @Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId);

    List<OrderFeeDto> getAccountStatementOrderListPaidFeeGroup(@Param("orderIds") List<Long> orderIds, @Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId);

    Long getAccountStatementOrderListNoPaidFee(@Param("orderIds") List<Long> orderIds, @Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId);

    List<OrderFeeDto> getAccountStatementOrderListNoPaidFeeGroup(@Param("orderIds") List<Long> orderIds, @Param("tenantId") Long tenantId, @Param("userInfoId") Long userInfoId);

    Page<QueryAccountStatementOrdersDto> queryAccountStatementOrders(Page<QueryAccountStatementOrdersDto> page,
                                                                     @Param("vo") QueryAccountStatementOrdersVo vo,
                                                                     @Param("orderIds") List<Long> orderIds);

    List<QueryAccountStatementOrdersDto> queryAccountStatementOrderList(@Param("vo") QueryAccountStatementOrdersVo vo);
}
