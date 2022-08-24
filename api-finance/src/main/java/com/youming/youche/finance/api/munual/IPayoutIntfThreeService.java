package com.youming.youche.finance.api.munual;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.finance.domain.ac.AcBusiOrderLimitRel;
import com.youming.youche.finance.domain.munual.MunualPaymentInfo;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.DueDateDetailsCDDto;
import com.youming.youche.finance.dto.PayableDayReportFinanceDto;
import com.youming.youche.finance.dto.PayableMonthReportFinanceDto;
import com.youming.youche.finance.dto.order.PayoutInfoDto;
import com.youming.youche.finance.vo.munual.QueryPayoutIntfsVo;
import com.youming.youche.finance.vo.order.QueryDouOverdueVo;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;

import java.util.List;
import java.util.Map;

/**
 * 现金打款服务类
 *
 * @author zengwen
 * @date 2022/4/7 9:04
 */
public interface IPayoutIntfThreeService extends IBaseService<PayoutIntf> {

    /**
     * 分页查询现金打款信息
     *
     * @param queryPayoutIntfsVo
     * @return
     */
    IPage<MunualPaymentInfo> getPayOutIntfs(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo);

    /**
     * 查询服务费
     */
    Map doQueryServiceFee(String accessToken, Long cash, Long flowId);

    /**
     * 查询未核销总金额
     *
     * @param accessToken
     * @param queryPayoutIntfsVo
     * @return
     */
    Map queryPayoutIntfsSum(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo);

    /**
     * 导出现金打款信息
     *
     * @param accessToken
     * @param queryPayoutIntfsVo
     */
    void downloadExcelFile(String accessToken, QueryPayoutIntfsVo queryPayoutIntfsVo, ImportOrExportRecords importOrExportRecords);

    /**
     * 应收逾期
     */
    IPage<PayoutInfoDto> doQueryDouOverdue(Integer pageNumber,Integer pageSize, QueryDouOverdueVo queryDouOverdueVo, String accessToken);

    /**
     * APP&&微信确认线下收款
     * 接口编码 21118
     * param flowId  流水号
     */
    void confirmPayment(Long flowId, String accessToken);

    /**
     * 确认线下收款
     */
    void confirmPaymentNew(Long flowId, String accessToken);

    /***
     * 处理200支付业务
     */
    boolean doPay200(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, Boolean isOnline, String accessToken);

    boolean doPay200Two(PayoutIntf payoutIntf, List<AcBusiOrderLimitRel> busiOrderLimitRels, Boolean isOnline, String accessToken);
    /**
     * 车队200附加运费打款成功反写附加运费记录
     */
    void dealAdditionalFee(PayoutIntf pay);

    /**
     * @Description: 该函数的功能描述:核销预支打款成功之后订单金额
     */
    void doOrderLimtByFlowId(PayoutIntf payoutIntf);

    /**
     * 业务回调
     */
    void callBack(PayoutIntf payout, String accessToken);

    /***
     * 支付违章服务费
     */
    PayoutIntf dealPayServiceFee(PayoutIntf violationFeePayoutIntf, String accessToken);

    PayoutIntf getPayOutIntfByXid(long xid);

    /**
     * @Description: 该函数的功能描述:打款成功后回调
     */
    void dealPaySuccess(PayoutIntf payoutIntf);


    /**
     * 根据流水号和租户id查询提现记录
     * @param flowId  流水号
     * @param tenantId	租户id
     * @return PayoutIntf
     * @throws Exception
     */
    PayoutIntf getPayoutIntfByFlowId(Long flowId , Long tenantId);

    /**
     * 应收逾期
     *
     * @param busiCode
     * @param name
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    IPage<DueDateDetailsCDDto> getDueDateDetailsCD(String busiCode, String name, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 逾期账户金额校验
     *
     * @param payoutIntf
     * @param accessToken
     */
    void checkOverdueAcc(PayoutIntf payoutIntf, String accessToken);

    /**
     * 付款逻辑校验  只能校验车队付款数据
     * @param payoutIntf
     * @param payoutIntfExpansion
     * @param accessToken
     */
    void paymentCheck(PayoutIntf payoutIntf, PayoutIntfExpansion payoutIntfExpansion, String accessToken);

    /**
     * 处理银行付完款后修改状态
     * @param accessToken
     */
    void cmbDealOnlineToOffline(String accessToken);

    /**
     * 处理线上转线下付款
     * @param accessToken
     */
    void dealOnlineToOffline(String accessToken);

    /**
     * 虚拟账户到虚拟账户之后的本地业务 [669]
     */
    void payOutToBusi();

    /**
     * CMB银行虚拟账户到虚拟账户的支付 [668]
     */
    void payOutToBank();

    /**
     * 线上打款自动确认收款[10030]
     */
    void confirmMoney();

    /**
     * 银行虚拟账户到虚拟账户的支付[2004]
     */
    void cmbOutToBank();

    /**
     * 车队  应付逾期 21152
     *
     * @param orderId
     * @param name
     * @param businessNumber
     * @param state
     * @param userId
     * @param pageSize
     * @param accessToken
     */
    Page<PayoutInfoDto> getOverdueCD(String orderId, String name, String businessNumber, String state, Long userId, Integer pageSize, Integer pageNum, String accessToken);

    /**
     * 获取应付逾期总金额
     */
    Long getOverdueCDSum(Long orderId, String name, String businessNumber, String state, Long userId, String accessToken);

    /**
     * 获取应付逾期已逾期的金额
     */
    Long getOverdueSum(Long userId, String accessToken);

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

    /**
     * 车队应收逾期接口21151
     * @param orderId
     * @param name
     * @param businessNumbers
     * @param state
     * @param userId
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    IPage<DueDateDetailsCDDto> getDueDateDetailsCDs(Long orderId, String name, String businessNumbers, String state, Long userId,Integer pageNum, Integer pageSize, String accessToken);
    /**
     *
     * 接口编码:21117
     * @Description: APP应收逾期明细
     * 接口入参
     * param userId    用户ID
     * param orderId   订单号
     * param name      车队名称
     * param businessNumber   业务类型
     * param state   状态
     * @return
     * @throws Exception
     */
    IPage<DueDateDetailsCDDto> getDueDateDetails(Long userId, Long orderId, String name, String businessNumbers, String state, Long sourceTenantId, String userType, Integer pageNum, Integer pageSize, String accessToken);


    Long getDueDateDetailsSum(Long userId, Long orderId, String name, String businessNumbers, String state, Long sourceTenantId, String userType, String accessToken);
}
