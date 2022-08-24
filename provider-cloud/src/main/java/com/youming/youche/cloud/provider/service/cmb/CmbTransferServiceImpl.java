package com.youming.youche.cloud.provider.service.cmb;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.youming.youche.cloud.api.cmb.ICmbTransferService;
import com.youming.youche.cloud.cmb.midsrv.jyt.comment.bizUtils.SignUtils;
import com.youming.youche.cloud.constant.CmbIntfConst;
import com.youming.youche.cloud.dto.cmb.AccAuthPreChkRepDto;
import com.youming.youche.cloud.dto.cmb.AccAuthPreChkReqDto;
import com.youming.youche.cloud.dto.cmb.AccAuthReqQryRepDto;
import com.youming.youche.cloud.dto.cmb.AccAuthReqQryReqDto;
import com.youming.youche.cloud.dto.cmb.AccAuthReqRepDto;
import com.youming.youche.cloud.dto.cmb.AccAuthReqReqDto;
import com.youming.youche.cloud.dto.cmb.BnkAccBindReqDto;
import com.youming.youche.cloud.dto.cmb.BnkAccCnlReqDto;
import com.youming.youche.cloud.dto.cmb.BnkAccListQryRepDto;
import com.youming.youche.cloud.dto.cmb.BnkAccListQryReqDto;
import com.youming.youche.cloud.dto.cmb.ChargeFundListQryRepDto;
import com.youming.youche.cloud.dto.cmb.ChargeFundListQryReqDto;
import com.youming.youche.cloud.dto.cmb.FileDownloadRepDto;
import com.youming.youche.cloud.dto.cmb.ItaFundAdjustRepDto;
import com.youming.youche.cloud.dto.cmb.ItaFundAdjustReqDto;
import com.youming.youche.cloud.dto.cmb.ItaFundCallBackDto;
import com.youming.youche.cloud.dto.cmb.ItaFundListQryRepDto;
import com.youming.youche.cloud.dto.cmb.ItaFundListQryReqDto;
import com.youming.youche.cloud.dto.cmb.ItaTranQryRepDto;
import com.youming.youche.cloud.dto.cmb.ItaTranQryReqDto;
import com.youming.youche.cloud.dto.cmb.MbrBalQryRepDto;
import com.youming.youche.cloud.dto.cmb.MbrBalQryReqDto;
import com.youming.youche.cloud.dto.cmb.MbrBaseInfoQryRepDto;
import com.youming.youche.cloud.dto.cmb.MbrBaseInfoQryReqDto;
import com.youming.youche.cloud.dto.cmb.MbrChargeFundCallBackDto;
import com.youming.youche.cloud.dto.cmb.MbrInfoChgReqDto;
import com.youming.youche.cloud.dto.cmb.MerchRegCallBackDto;
import com.youming.youche.cloud.dto.cmb.MerchRegQryRepDto;
import com.youming.youche.cloud.dto.cmb.MerchRegQryReqDto;
import com.youming.youche.cloud.dto.cmb.MerchRegReqDto;
import com.youming.youche.cloud.dto.cmb.MgrInfoChgReqDto;
import com.youming.youche.cloud.dto.cmb.NtsResultDto;
import com.youming.youche.cloud.dto.cmb.OrderCheckOutRepDto;
import com.youming.youche.cloud.dto.cmb.OrderCheckOutReqDto;
import com.youming.youche.cloud.dto.cmb.OrderFundReserveRepDto;
import com.youming.youche.cloud.dto.cmb.OrderFundReserveReqDto;
import com.youming.youche.cloud.dto.cmb.OrderFundRevokeRepDto;
import com.youming.youche.cloud.dto.cmb.OrderFundRevokeReqDto;
import com.youming.youche.cloud.dto.cmb.OrderTransferRepDto;
import com.youming.youche.cloud.dto.cmb.OrderTransferReqDto;
import com.youming.youche.cloud.dto.cmb.ReceiptFileDownloadReqDto;
import com.youming.youche.cloud.dto.cmb.ReconFileDownloadReqDto;
import com.youming.youche.cloud.dto.cmb.RefundListQryRepDto;
import com.youming.youche.cloud.dto.cmb.RefundListQryReqDto;
import com.youming.youche.cloud.dto.cmb.SetTransferRepDto;
import com.youming.youche.cloud.dto.cmb.SetTransferReqDto;
import com.youming.youche.cloud.dto.cmb.SingleCashBatchSepQryRepDto;
import com.youming.youche.cloud.dto.cmb.SingleCashBatchSepQryReqDto;
import com.youming.youche.cloud.dto.cmb.SingleCashBatchSepRepDto;
import com.youming.youche.cloud.dto.cmb.SmsCodeApplyRepDto;
import com.youming.youche.cloud.dto.cmb.SmsCodeApplyReqDto;
import com.youming.youche.cloud.dto.cmb.TotalOrderFundQryRepDto;
import com.youming.youche.cloud.dto.cmb.TotalOrderFundQryReqDto;
import com.youming.youche.cloud.dto.cmb.TradeFileDownloadReqDto;
import com.youming.youche.cloud.dto.cmb.TranListQryRepDto;
import com.youming.youche.cloud.dto.cmb.TranListQryReqDto;
import com.youming.youche.cloud.dto.cmb.WithdrawRepDto;
import com.youming.youche.cloud.dto.cmb.WithdrawReqDto;
import com.youming.youche.cloud.provider.configure.cmb.JytfzConfiguration;
import com.youming.youche.system.api.mycenter.IBankAccountService;
import com.youming.youche.system.api.mycenter.IBankAccountTranService;
import com.youming.youche.util.Base64Utils;
import com.youming.youche.util.BeanMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @ClassName CmbTransferServiceImpl
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 15:41
 */
@DubboService(version = "1.0.0")
public class CmbTransferServiceImpl implements ICmbTransferService {

    private static final Logger log = LoggerFactory.getLogger(CmbTransferServiceImpl.class);

    @DubboReference(version = "1.0.0")
    IBankAccountService bankAccountService;

    @DubboReference(version = "1.0.0")
    IBankAccountTranService bankAccountTranService;

    @Autowired
    private JytfzConfiguration jytfzConfiguration;

    @Override
    public String getPlatformNo() {
        return jytfzConfiguration.getJytfzProperties().getPlatformNo();
    }

    @Override
    public String getBnkNo() {
        return jytfzConfiguration.getJytfzProperties().getBnkNo();
    }

    /**
     * @param merchRegReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto
     * @description 1.1 商户进件
     * @author zag
     * @date 2022/1/15 14:51
     */
    @Override
    public NtsResultDto merchReg(MerchRegReqDto merchRegReq) throws Exception {
        System.out.println(JSON.toJSONString(merchRegReq));
        //前台默认填充为图片url,转换为base64编码发送
        if (StringUtils.isNotEmpty(merchRegReq.getCertFrontPhoto())) {
            merchRegReq.setCertFrontPhoto(Base64Utils.imageUrlToBase64(merchRegReq.getCertFrontPhoto()));
        }
        if (StringUtils.isNotEmpty(merchRegReq.getCertBackPhoto())) {
            merchRegReq.setCertBackPhoto(Base64Utils.imageUrlToBase64(merchRegReq.getCertBackPhoto()));
        }
        if (StringUtils.isNotEmpty(merchRegReq.getMgrAuthLetter())) {
            merchRegReq.setMgrAuthLetter(Base64Utils.imageUrlToBase64(merchRegReq.getMgrAuthLetter()));
        }
        if (merchRegReq.getCpnInfo() != null) {
            if (StringUtils.isNotEmpty(merchRegReq.getCpnInfo().getChgrFrontPhoto())) {
                merchRegReq.getCpnInfo().setChgrFrontPhoto(Base64Utils.imageUrlToBase64(merchRegReq.getCpnInfo().getChgrFrontPhoto()));
            }
            if (StringUtils.isNotEmpty(merchRegReq.getCpnInfo().getChgrBackPhoto())) {
                merchRegReq.getCpnInfo().setChgrBackPhoto(Base64Utils.imageUrlToBase64(merchRegReq.getCpnInfo().getChgrBackPhoto()));
            }
        }
        Map<String, Object> param = BeanMapUtils.beanToMap(merchRegReq);
        //System.out.println(JSON.toJSONString(param));
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MERCHREG;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto result = JSONObject.parseObject(resultStr, NtsResultDto.class);
        System.out.println(result);
        return result;
    }

    /**
     * @param merchRegQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.MerchRegQryRepDto>
     * @description 1.2 进件结果查询
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<MerchRegQryRepDto> merchRegQry(MerchRegQryReqDto merchRegQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(merchRegQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MERCHREGQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<MerchRegQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<MerchRegQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param accAuthPreChkReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.AccAuthPreChkRepDto>
     * @description 2.1 小额鉴权资格查询
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<AccAuthPreChkRepDto> accAuthPreChk(AccAuthPreChkReqDto accAuthPreChkReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(accAuthPreChkReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ACCAUTHPRECHK;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<AccAuthPreChkRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<AccAuthPreChkRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param accAuthReqReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.AccAuthReqRepDto>
     * @description 2.2 小额鉴权申请
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<AccAuthReqRepDto> accAuthReq(AccAuthReqReqDto accAuthReqReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(accAuthReqReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ACCAUTHREQ;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<AccAuthReqRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<AccAuthReqRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param accAuthReqQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.AccAuthReqQryRepDto>
     * @description 2.3 鉴权申请查询
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<AccAuthReqQryRepDto> accAuthReqQry(AccAuthReqQryReqDto accAuthReqQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(accAuthReqQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ACCAUTHREQQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<AccAuthReqQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<AccAuthReqQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param bnkAccBindReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto
     * @description 2.4 银行账户绑定
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto bnkAccBind(BnkAccBindReqDto bnkAccBindReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(bnkAccBindReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.BNKACCBIND;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto result = JSONObject.parseObject(resultStr, NtsResultDto.class);
        System.out.println(result);
        return result;
    }

    /**
     * @param bnkAccListQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.BnkAccListQryRepDto>
     * @description 2.5 已绑定帐户查询
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<BnkAccListQryRepDto> bnkAccListQry(BnkAccListQryReqDto bnkAccListQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(bnkAccListQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.BNKACCLISTQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<BnkAccListQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<BnkAccListQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param bnkAccCnlReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto
     * @description 2.6 银行账户解绑
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto bnkAccCnl(BnkAccCnlReqDto bnkAccCnlReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(bnkAccCnlReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.BNKACCCNL;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto result = JSONObject.parseObject(resultStr, NtsResultDto.class);
        System.out.println(result);
        return result;
    }

    /**
     * @param itaFundListQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.ItaFundListQryRepDto>
     * @description 3.1 匿名资金列表查询
     * @author zag
     * @date 2022/1/15 14:52
     */
    @Override
    public NtsResultDto<ItaFundListQryRepDto> itaFundListQry(ItaFundListQryReqDto itaFundListQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(itaFundListQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ITAFUNDLISTQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<ItaFundListQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<ItaFundListQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param itaFundAdjustReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.ItaFundAdjustRepDto>
     * @description 3.2 匿名资金调账
     * @author zag
     * @date 2022/1/15 14:53
     */
    @Override
    public NtsResultDto<ItaFundAdjustRepDto> itaFundAdjust(ItaFundAdjustReqDto itaFundAdjustReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(itaFundAdjustReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ITAFUNDADJUST;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<ItaFundAdjustRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<ItaFundAdjustRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param chargeFundListQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.ChargeFundListQryRepDto>
     * @description 3.3 充值资金查询
     * @author zag
     * @date 2022/1/15 14:53
     */
    @Override
    public NtsResultDto<ChargeFundListQryRepDto> chargeFundListQry(ChargeFundListQryReqDto chargeFundListQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(chargeFundListQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.CHARGEFUNDLISTQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<ChargeFundListQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<ChargeFundListQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param smsCodeApplyReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.SmsCodeApplyRepDto>
     * @description 4.1 交易短信验证申请
     * @author zag
     * @date 2022/1/15 14:53
     */
    @Override
    public NtsResultDto<SmsCodeApplyRepDto> smsCodeApply(SmsCodeApplyReqDto smsCodeApplyReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(smsCodeApplyReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.SMSCODEAPPLY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<SmsCodeApplyRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<SmsCodeApplyRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param orderFundReserveReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.OrderFundReserveRepDto>
     * @description 4.2 下单（冻结）请求
     * @author zag
     * @date 2022/1/15 14:53
     */
    @Override
    public NtsResultDto<OrderFundReserveRepDto> orderFundReserve(OrderFundReserveReqDto orderFundReserveReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(orderFundReserveReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ORDERFUNDRESERVE;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<OrderFundReserveRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<OrderFundReserveRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param orderFundRevokeReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.OrderFundRevokeRepDto>
     * @description 4.3 下单（冻结）撤销
     * @author zag
     * @date 2022/1/15 14:53
     */
    @Override
    public NtsResultDto<OrderFundRevokeRepDto> orderFundRevoke(OrderFundRevokeReqDto orderFundRevokeReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(orderFundRevokeReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ORDERFUNDREVOKE;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<OrderFundRevokeRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<OrderFundRevokeRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param orderCheckOutReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.OrderCheckOutRepDto>
     * @description 4.4 结单请求
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<OrderCheckOutRepDto> orderCheckOut(OrderCheckOutReqDto orderCheckOutReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(orderCheckOutReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ORDERCHECKOUT;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<OrderCheckOutRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<OrderCheckOutRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param orderTransferReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.OrderTransferRepDto>
     * @description 4.5 可用余额支付
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<OrderTransferRepDto> orderTransfer(OrderTransferReqDto orderTransferReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(orderTransferReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ORDERTRANSFER;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<OrderTransferRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<OrderTransferRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param itaTranQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.ItaTranQryRepDto>
     * @description 4.6 交易查询
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<ItaTranQryRepDto> itaTranQry(ItaTranQryReqDto itaTranQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(itaTranQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.ITATRANQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<ItaTranQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<ItaTranQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param tranListQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.TranListQryRepDto>
     * @description 4.7 交易列表查询
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<TranListQryRepDto> tranListQry(TranListQryReqDto tranListQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(tranListQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.TRANLISTQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<TranListQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<TranListQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param singleCashBatchSepRep
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.SingleCashBatchSepRepDto>
     * @description 4.8 分账请求
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<SingleCashBatchSepRepDto> singleCashBatchSep(SingleCashBatchSepRepDto singleCashBatchSepRep) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(singleCashBatchSepRep);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.SINGLECASHBATCHSEP;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<SingleCashBatchSepRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<SingleCashBatchSepRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param singleCashBatchSepQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.SingleCashBatchSepQryRepDto>
     * @description 4.9 分账查询
     * @author zag
     * @date 2022/1/15 14:54
     */
    @Override
    public NtsResultDto<SingleCashBatchSepQryRepDto> singleCashBatchSepQry(SingleCashBatchSepQryReqDto singleCashBatchSepQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(singleCashBatchSepQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.SINGLECASHBATCHSEPQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<SingleCashBatchSepQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<SingleCashBatchSepQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param setTransferReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.SetTransferRepDto>
     * @description 4.10 套账转账
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<SetTransferRepDto> setTransfer(SetTransferReqDto setTransferReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(setTransferReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.SETTRANSFER;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<SetTransferRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<SetTransferRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param withdrawReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.WithdrawRepDto>
     * @description 5.1 提现请求
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<WithdrawRepDto> withdraw(WithdrawReqDto withdrawReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(withdrawReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.WITHDRAW;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto<WithdrawRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<WithdrawRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param refundListQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.RefundListQryRepDto>
     * @description 5.3 退票明细查询
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<RefundListQryRepDto> refundListQry(RefundListQryReqDto refundListQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(refundListQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.REFUNDLISTQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<RefundListQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<RefundListQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param reconFileDownloadReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.FileDownloadRepDto>
     * @description 6.1 对账单下载
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<FileDownloadRepDto> reconFileDownload(ReconFileDownloadReqDto reconFileDownloadReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(reconFileDownloadReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.RECONFILEDOWNLOAD;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<FileDownloadRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<FileDownloadRepDto>>() {
        });
        System.out.println(result);
        if (result.isSuccess()) {
            String fileUrl = result.getData().getFileUrl();
//            int idx = url.lastIndexOf('/') + 1;
//            String fileName = url.substring(idx, url.length());
//            DownloadURLFileUtil.downLoadFromUrl(url, fileName, "D:/tempFiles");
//            String dir = fileName.replace(".zip", "");
//            ZipUtil.unZipFiles("D:/tempFiles/" + fileName, "D:/tempFiles/"+dir);//zip文件解压
//            //csv文件解析
//            File file = new File("D:/tempFiles/"+dir);
//            File[] tempList = file.listFiles();
//            for (int i = 0; i < tempList.length; i++) {
//                if (tempList[i].isFile()) {
//                    //List<String[]> list = CsvUtils.getCsvData(tempList[i].getPath());
//                    String csvFile = tempList[i].getPath();
//                    String line = "";
//                    String cvsSplitBy = "`";
//                    try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//                        while ((line = br.readLine()) != null) {
//                            // use comma as separator
//                            String[] country = line.split(cvsSplitBy);
//                            System.out.println("Country [code= " + country[4] + " , name=" + country[5] + "]");
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
        }
        return result;
    }

    /**
     * @param receiptFileDownloadReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.FileDownloadRepDto>
     * @description 7.1 电子回单下载
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<FileDownloadRepDto> receiptFileDownload(ReceiptFileDownloadReqDto receiptFileDownloadReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(receiptFileDownloadReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.RECEIPTFILEDOWNLOAD;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<FileDownloadRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<FileDownloadRepDto>>() {
        });
        System.out.println(result);
        if (result.isSuccess()) {
            String fileUrl = result.getData().getFileUrl();
//            int idx = url.lastIndexOf('/') + 1;
//            String fileName = url.substring(idx, url.length());
//            DownloadURLFileUtil.downLoadFromUrl(url,fileName,"C:/tempFiles");
        }
        return result;
    }

    /**
     * @param tradeFileDownloadReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.FileDownloadRepDto>
     * @description 7.2 交易流水单下载
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<FileDownloadRepDto> tradeFileDownload(TradeFileDownloadReqDto tradeFileDownloadReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(tradeFileDownloadReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.TRADEFILEDOWNLOAD;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<FileDownloadRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<FileDownloadRepDto>>() {
        });
        System.out.println(result);
        if (result.isSuccess()) {
            String fileUrl = result.getData().getFileUrl();
//            int idx = url.lastIndexOf('/') + 1;
//            String fileName = url.substring(idx, url.length());
//            DownloadURLFileUtil.downLoadFromUrl(url,fileName,"D:/tempFiles");
        }
        return result;
    }

    /**
     * @param mbrBaseInfoQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.MbrBaseInfoQryRepDto>
     * @description 8.1 子商户信息查询
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<MbrBaseInfoQryRepDto> mbrBaseInfoQry(MbrBaseInfoQryReqDto mbrBaseInfoQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(mbrBaseInfoQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MBRBASEINFOQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<MbrBaseInfoQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<MbrBaseInfoQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param mbrBalQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.MbrBalQryRepDto>
     * @description 8.2 子商户余额查询
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<MbrBalQryRepDto> mbrBalQry(MbrBalQryReqDto mbrBalQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(mbrBalQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MBRBALQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<MbrBalQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<MbrBalQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param totalOrderFundQryReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto<com.youming.youche.cloud.dto.cmb.TotalOrderFundQryRepDto>
     * @description 8.3 订单冻结资金总额查询
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto<TotalOrderFundQryRepDto> totalOrderFundQry(TotalOrderFundQryReqDto totalOrderFundQryReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(totalOrderFundQryReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.TOTALORDERFUNDQRY;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "get");
        System.out.println(resultStr);
        NtsResultDto<TotalOrderFundQryRepDto> result = JSONObject.parseObject(resultStr, new TypeReference<NtsResultDto<TotalOrderFundQryRepDto>>() {
        });
        System.out.println(result);
        return result;
    }

    /**
     * @param mgrInfoChgReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto
     * @description 8.4 管理员信息变更
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto mgrInfoChg(MgrInfoChgReqDto mgrInfoChgReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(mgrInfoChgReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MGRINFOCHG;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto result = JSONObject.parseObject(resultStr, NtsResultDto.class);
        System.out.println(result);
        return result;
    }

    /**
     * @param mbrInfoChgReq
     * @return com.youming.youche.cloud.dto.cmb.NtsResultDto
     * @description 8.5 子商户信息变更
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public NtsResultDto mbrInfoChg(MbrInfoChgReqDto mbrInfoChgReq) throws Exception {
        Map<String, Object> param = BeanMapUtils.beanToMap(mbrInfoChgReq);
        String url = jytfzConfiguration.getJytfzProperties().getAppUrl() + "/" + CmbIntfConst.TranFunc.MBRINFOCHG;
        String resultStr = jytfzConfiguration.getOpenApiService().OpenApiClient(url, param, "post");
        System.out.println(resultStr);
        NtsResultDto result = JSONObject.parseObject(resultStr, NtsResultDto.class);
        System.out.println(result);
        return result;
    }

    /**
     * @param param
     * @param type
     * @param sign
     * @return void
     * @description 9 通知回调
     * @author zag
     * @date 2022/1/15 14:55
     */
    @Override
    public void notifyCallBack(Map<String, Object> param, String type, String sign) throws Exception {
        log.info("[Cmb][收到银行通知回调信息]start");
        String correctSign = SignUtils.generateSign(param, jytfzConfiguration.getJytfzProperties().getSecretKey());
        StringBuilder sb = new StringBuilder();
        sb.append("接收到的sign：" + sign + "; 计算得到的sign：" + correctSign);
        sb.append("\r\n");
        sb.append("接收到的type：" + type + "; 接收到的body：" + param);
        log.info("[Cmb][收到银行通知回调信息]" + sb.toString());
        if (!correctSign.equals(sign)) {
            throw new Exception("sign验证失败");
        }
        try {
            switch (type) {
                //9.1 商户进件回调接口（type=A）
                case CmbIntfConst.CallBackType.A:
                    bankAccountService.merchRegCallBack(param);
                    break;
                //9.2 子商户注资回调接口（type=B）
                case CmbIntfConst.CallBackType.B:
                    bankAccountTranService.mbrChargeFundCallBack(param);
                    break;
                //9.3 匿名资金回调接口（type=C）
                case CmbIntfConst.CallBackType.C:
                    bankAccountTranService.itaFundCallBack(param);
                    break;
                //提现回调接口（type=H）
                case CmbIntfConst.CallBackType.H:
                    bankAccountTranService.withdrawCallBack(param);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[Cmb][收到银行通知回调信息]处理异常：" + e.getMessage());
        }
        log.info("[Cmb][收到银行通知回调信息]end");
    }

    /**
     * @param merchRegCallBack
     * @return void
     * @description 9.1 商户进件回调接口（type=A）
     * @author zag
     * @date 2022/1/15 14:56
     */
    @Override
    public void merchRegCallBack(MerchRegCallBackDto merchRegCallBack) throws Exception {


    }

    /**
     * @param mbrChargeFundCallBack
     * @return void
     * @description 9.2 子商户注资回调接口（type=B）
     * @author zag
     * @date 2022/1/15 14:56
     */
    @Override
    public void mbrChargeFundCallBack(MbrChargeFundCallBackDto mbrChargeFundCallBack) throws Exception {

    }

    /**
     * @param itaFundCallBack
     * @return void
     * @description 9.3 匿名资金回调接口（type=C）
     * @author zag
     * @date 2022/1/15 14:56
     */
    @Override
    public void itaFundCallBack(ItaFundCallBackDto itaFundCallBack) throws Exception {

    }
}
