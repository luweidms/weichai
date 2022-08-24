package com.youming.youche.cloud.api.cmb;

import com.youming.youche.cloud.dto.cmb.*;

import java.util.Map;

/**
 * @InterfaceName ICmbTransferService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 15:25
 */
public interface ICmbTransferService {

    /** 获取平台号 */
    String getPlatformNo();

    /** 获取联行号 */
    String getBnkNo();

    /** 1.1 商户进件 */
    NtsResultDto merchReg(MerchRegReqDto merchRegReq) throws Exception;

    /** 1.2 进件结果查询 */
    NtsResultDto<MerchRegQryRepDto> merchRegQry(MerchRegQryReqDto merchRegQryReq) throws Exception;

    /** 2.1 小额鉴权资格查询 */
    NtsResultDto<AccAuthPreChkRepDto> accAuthPreChk(AccAuthPreChkReqDto accAuthPreChkReq) throws Exception;

    /** 2.2 小额鉴权申请 */
    NtsResultDto<AccAuthReqRepDto> accAuthReq(AccAuthReqReqDto accAuthReqReq) throws Exception;

    /** 2.3 鉴权申请查询 */
    NtsResultDto<AccAuthReqQryRepDto> accAuthReqQry(AccAuthReqQryReqDto accAuthReqQryReq) throws Exception;

    /** 2.4 银行账户绑定 */
    NtsResultDto bnkAccBind(BnkAccBindReqDto bnkAccBindReq) throws Exception;

    /** 2.5 已绑定帐户查询 */
    NtsResultDto<BnkAccListQryRepDto> bnkAccListQry(BnkAccListQryReqDto bnkAccListQryReq) throws Exception;

    /** 2.6 银行账户解绑 */
    NtsResultDto bnkAccCnl(BnkAccCnlReqDto bnkAccCnlReq) throws Exception;

    /** 3.1 匿名资金列表查询 */
    NtsResultDto<ItaFundListQryRepDto> itaFundListQry(ItaFundListQryReqDto itaFundListQryReq) throws Exception;

    /** 3.2 匿名资金调账 */
    NtsResultDto<ItaFundAdjustRepDto> itaFundAdjust(ItaFundAdjustReqDto itaFundAdjustReq) throws Exception;

    /** 3.3 充值资金查询 */
    NtsResultDto<ChargeFundListQryRepDto> chargeFundListQry(ChargeFundListQryReqDto chargeFundListQryReq) throws Exception;

    /** 4.1 交易短信验证申请 */
    NtsResultDto<SmsCodeApplyRepDto> smsCodeApply(SmsCodeApplyReqDto smsCodeApplyReq) throws Exception;

    /** 4.2 下单（冻结）请求 */
    NtsResultDto<OrderFundReserveRepDto> orderFundReserve(OrderFundReserveReqDto orderFundReserveReq) throws Exception;

    /** 4.3 下单（冻结）撤销 */
    NtsResultDto<OrderFundRevokeRepDto> orderFundRevoke(OrderFundRevokeReqDto orderFundRevokeReq) throws Exception;

    /** 4.4 结单请求 */
    NtsResultDto<OrderCheckOutRepDto> orderCheckOut(OrderCheckOutReqDto orderCheckOutReq) throws Exception;

    /** 4.5 可用余额支付 */
    NtsResultDto<OrderTransferRepDto> orderTransfer(OrderTransferReqDto orderTransferReq) throws Exception;

    /** 4.6 交易查询 */
    NtsResultDto<ItaTranQryRepDto> itaTranQry(ItaTranQryReqDto itaTranQryReq) throws Exception;

    /** 4.7 交易列表查询 */
    NtsResultDto<TranListQryRepDto> tranListQry(TranListQryReqDto tranListQryReq) throws Exception;

    /** 4.8 分账请求 */
    NtsResultDto<SingleCashBatchSepRepDto> singleCashBatchSep(SingleCashBatchSepRepDto singleCashBatchSepRep) throws Exception;

    /** 4.9 分账查询 */
    NtsResultDto<SingleCashBatchSepQryRepDto> singleCashBatchSepQry(SingleCashBatchSepQryReqDto singleCashBatchSepQryReq) throws Exception;

    /** 4.10 套账转账 */
    NtsResultDto<SetTransferRepDto> setTransfer(SetTransferReqDto setTransferReq) throws Exception;

    /** 5.1 提现请求 */
    NtsResultDto<WithdrawRepDto> withdraw(WithdrawReqDto withdrawReq) throws Exception;

    /** 5.3 退票明细查询 */
    NtsResultDto<RefundListQryRepDto> refundListQry(RefundListQryReqDto refundListQryReq) throws Exception;

    /** 6.1 对账单下载 */
    NtsResultDto<FileDownloadRepDto> reconFileDownload(ReconFileDownloadReqDto reconFileDownloadReq) throws Exception;

    /** 7.1 电子回单下载 */
    NtsResultDto<FileDownloadRepDto> receiptFileDownload(ReceiptFileDownloadReqDto receiptFileDownloadReq) throws Exception;

    /** 7.2 交易流水单下载 */
    NtsResultDto<FileDownloadRepDto> tradeFileDownload(TradeFileDownloadReqDto tradeFileDownloadReq) throws Exception;

    /** 8.1 子商户信息查询 */
    NtsResultDto<MbrBaseInfoQryRepDto> mbrBaseInfoQry(MbrBaseInfoQryReqDto mbrBaseInfoQryReq) throws Exception;

    /** 8.2 子商户余额查询 */
    NtsResultDto<MbrBalQryRepDto> mbrBalQry(MbrBalQryReqDto mbrBalQryReq) throws Exception;

    /** 8.3 订单冻结资金总额查询 */
    NtsResultDto<TotalOrderFundQryRepDto> totalOrderFundQry(TotalOrderFundQryReqDto totalOrderFundQryReq) throws Exception;

    /** 8.4 管理员信息变更 */
    NtsResultDto mgrInfoChg(MgrInfoChgReqDto mgrInfoChgReq) throws Exception;

    /** 8.5 子商户信息变更 */
    NtsResultDto mbrInfoChg(MbrInfoChgReqDto mbrInfoChgReq) throws Exception;

    /** 9 通知回调 */
    void notifyCallBack(Map<String, Object> param,String type,String sign) throws Exception;

    /** 9.1 商户进件回调接口（type=A） */
    void merchRegCallBack(MerchRegCallBackDto merchRegCallBack) throws Exception;

    /** 9.2 子商户注资回调接口（type=B） */
    void mbrChargeFundCallBack(MbrChargeFundCallBackDto mbrChargeFundCallBack) throws Exception;

    /** 9.3 匿名资金回调接口（type=C） */
    void itaFundCallBack(ItaFundCallBackDto itaFundCallBack) throws Exception;


}