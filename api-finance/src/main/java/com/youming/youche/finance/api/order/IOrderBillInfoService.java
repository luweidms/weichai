package com.youming.youche.finance.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.finance.domain.order.OrderBillCheckInfo;
import com.youming.youche.finance.domain.order.OrderBillInfo;
import com.youming.youche.finance.dto.order.OrderBillCheckInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInfoDto;
import com.youming.youche.finance.dto.order.OrderBillInvoiceDto;
import com.youming.youche.finance.vo.order.OrderBillInfoVo;

import java.io.File;
import java.util.List;

/**
 * 应收账单
 *
 * @author hzx
 * @date 2022/2/8 15:57
 */
public interface IOrderBillInfoService extends IBaseService<OrderBillInfo> {

    /**
     * 查询应收账单列表
     * @param objectPage
     * @param orderBillInfoVO
     * @param accessToken
     * @return
     */
    Page<OrderBillInfoDto> doQuery(Page<OrderBillInfoDto> objectPage, OrderBillInfoVo orderBillInfoVO, String accessToken);

    /**
     * 根据账单查询核销信息
     *
     * @param billNumber 账单号
     */
    List<OrderBillCheckInfoDto> queryChecksByBillNumber(String billNumber, String accessToken);

    /**
     * 保存核销信息
     *
     * @param billNumber          帐单号
     * @param orderBillCheckInfos 核销列表
     */
    void saveChecks(String billNumber, List<OrderBillCheckInfo> orderBillCheckInfos, String accessToken);

    /**
     * 查询账单发票列表
     */
    List<OrderBillInvoiceDto> queryBillReceipt(String billNumber, String accessToken);

    /**
     * 开票动作
     *
     * @param billNumberStr 帐单号
     *                      开票计算 CONFIRM_AMOUNT 确认应收款
     */
    void saveBillSts(String billNumberStr, String accessToken);

    /**
     * 保存发票信息
     *
     * @param billNumber  账单号
     * @param invoiceList 发票号 ”英文逗号分割“
     * @param amountList  发票金 ”英文逗号分割“
     */
    void saveOrderBillReceipt(String billNumber, String invoiceList, String amountList, String accessToken);

    /**
     * 账单撤销分3步骤：
     * a.有核销金额的账单（状态是部分核销），撤销后可以修改核销的金额，未撤销不能修改之前核销的金额，将账单的状态修改为“已开票”
     * b.已开票的账单（状态是已开票），撤销后将账单的状态改为“新建”，清除已核销的金额和已填写的发票
     * c.新建的账单（状态是新建），撤销后将账单打散（删除账单后），订单回到未打包状态（没有账单号）
     *
     * @param billNumberStr 账单号”英文逗号分割“
     */
    void undoBill(String billNumberStr, String accessToken);

    /**
     * 添加订单到账单
     *
     * @param billNumber  账单号
     * @param orderIdsStr 订单号，多个订单号用逗号分隔
     */
    void billAddOrders(String billNumber, String orderIdsStr, String accessToken);

    /**
     * 账单减少订单
     *
     * @param billNumber 账单号
     * @param orderIdStr 订单号(多个订单号用逗号分隔)
     */
    void billReduceOrders(String billNumber, String orderIdStr, String accessToken);

    /**
     * 创建账单
     *
     * @param orderIdStrs 订单号，多个订单号用英文逗号分隔
     */
    String createBill(String orderIdStrs, String accessToken);

    /**
     * 通过导入EXCEL创建账单
     * 导入方式创建账单格式如下：
     * 账单号为空，则只导入订单的确认差异，如果不存在账单单号，创建账单单号
     * 账单号非空，且为系统存在的账单号，则将该订单加入已创建的账单
     * 确认差额原因（对账、KPI、油价、开单、其它）
     */
    void createOrderBillByExcel(byte[] bytes, String fileName, ImportOrExportRecords record, String accessToken);

    /**
     * 导出打印
     */
    void exportQuery(String billNumbers, String customNames, String accessToken, ImportOrExportRecords importOrExportRecords);

    /**
     * 根据主键获取账单信息
     */
    OrderBillInfo getOrderBillInfo(String billNumber);

    /**
     * 订单对应金额通过平安账户打款成功的核销(进程调用)
     */
    void saveChecksByProcess(Long orderId, Long getAmount, String billNumber, String accessToken);

}
