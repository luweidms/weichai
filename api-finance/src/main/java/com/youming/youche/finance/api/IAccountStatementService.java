package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.AccountStatementDetails;
import com.youming.youche.finance.dto.AccountStatementAppDto;
import com.youming.youche.finance.dto.AccountStatementUserDto;
import com.youming.youche.finance.dto.DoQueryBillReceiverPageListDto;
import com.youming.youche.finance.dto.QueryAccountStatementOrdersDto;
import com.youming.youche.finance.dto.order.AccountStatementMarginDto;
import com.youming.youche.finance.dto.order.OrderStatementListToDoubleOutDto;
import com.youming.youche.finance.vo.AccountStatementOutVO;
import com.youming.youche.finance.vo.CreateAccountStatementVo;
import com.youming.youche.finance.vo.DoQueryBillReceiverPageListVo;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;
import com.youming.youche.finance.vo.QueryAccountStatementUserVo;
import com.youming.youche.finance.vo.order.AccountStatementCreateVo;
import com.youming.youche.finance.vo.order.OrderStatementListInVo;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;

import java.util.List;

/**
* <p>
    *  服务类
    * </p>
* @author luona
* @since 2022-04-11
*/
public interface IAccountStatementService extends IBaseService<AccountStatement> {

    /**
     * 获取对账单列表
     * @param accountStatement
     * @param opType
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    Page<AccountStatementOutVO> getAccountStatements(AccountStatement accountStatement, Integer opType, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 导出对账单信息
     * @param accountStatement
     * @param opType
     * @param accessToken
     * @param importOrExportRecords
     */
    void downloadExcelFile(AccountStatement accountStatement, Integer opType, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 车辆费用打款回调核销对账单
     */
    void payForCarFeeWriteBack(String busiCode);

    /**
     * 根据对账单编号查找对账单
     */
    AccountStatement getAccountStatementByBillNumber(String billNumber);


    /**
     * 创建账单
     * param accountStatementCreateVo
     */
    void createAccountStatement(AccountStatementCreateVo accountStatementCreateVo, String accessToken);

    /**
     * 新增修改移除车辆 对账单重新计算相关费用
     * @param as
     * @param details
     * @param operType 操作类型 insert新增车辆，delete移除车辆，update修改车辆
     * @throws Exception
     */
    void saveOrUpdateAccountStatementDetail(AccountStatement as, List<AccountStatementDetails> details, String operType, boolean isWriteLog, LoginInfo loginInfo);

    /**
     * 发送对账单
     * @param flowId
     */
    void sendAccountStatement(Long flowId, String accessToken);

    /**
     * 删除对账单
     * 对账单添加删除功能，未发送和被驳回的对账单可以删除，删除后不再列表中展示
     * @param flowId
     * @param accessToken
     */
    void deleteAccountStatement(Long flowId, String accessToken);

    /**
     * 确认账单时，查询各个月份余额 28302
     * @param flowId
     * @return
     */
    AccountStatementMarginDto queryAccountStatementMargin(Long flowId);

    /**
     * 计算金额
     * @param flowId
     * @param settlementAmount
     * @param settlementRemark
     */
    void setUpSettlementAmount(Long flowId, Long settlementAmount, String settlementRemark, String accessToken);

    /**
     * 结算对账单
     * @param flowId
     * @param settlementAmount
     * @param settlementRemark
     * @param accessToken
     */
    void settlementAmount(Long flowId, Long settlementAmount, String settlementRemark, String accessToken);

    /**
     * 获取订单费用列表 28310
     *
     * @param orderStatementListInVo
     * @param pageNum
     * @param pageSize
     * @param accessToken
     */
    Page<OrderStatementListToDoubleOutDto> getAccountStatementOrders(Long flowId, OrderStatementListInVo orderStatementListInVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 该函数的功能描述:查询接收人待确认对账单数量
     */
    Integer getAccountStatementCount(Long userId);

    /**
     * app接口：28300 对账单列表
     */
    Page<AccountStatementAppDto> getAccountStatementFromApp(Long receiverUserId, String billMonth, String state, String tenantName, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 对账单--确认账单 -确认 28301
     * @param flowId
     * @param isPass
     * @param remark
     * @param accessToken
     */
    void confirmAccountStatement(Long flowId,String isPass,String remark, String accessToken);

    /**
     * 28303  对账单 --- 查看详情
     * @param flowId
     * @return
     */
    AccountStatementAppDto getAccountStatementDetailFromApp(Long flowId, String accessToken);

    /**
     * 28304 对账单--油卡押金明细
     */
    Page<OilCardPledgeOrderListDto> getOilCardDetailFromApp(Long flowId, Integer pageNum, Integer pageSize);

    /**
     * 尾款结算对账单
     * @param as
     * @param accessToken
     */
    void marginSettlementAmount(AccountStatement as, String accessToken);

    /**
     * 现金收取结算对账单
     * @param as
     * @param accessToken
     */
    void cashSettlementAmount(AccountStatement as, String accessToken);

    Page<DoQueryBillReceiverPageListDto> doQueryBillReceiverPageList(DoQueryBillReceiverPageListVo vo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 获取账上对账单代收人信息
     */
    Page<AccountStatementUserDto> queryAccountStatementUser(QueryAccountStatementUserVo vo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 创建招商对账单
     */
    void createAccountStatementNew(CreateAccountStatementVo vo, String accessToken);

    /**
     * PC端查询招商对账单详情
     */
    Page<QueryAccountStatementOrdersDto> queryAccountStatementOrders(QueryAccountStatementOrdersVo vo, String accessToken, Integer pageNum, Integer pageSize);
}
