package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.BankDto;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.PayoutInfoDto;
import com.youming.youche.order.dto.PayoutInfosOutDto;
import com.youming.youche.order.dto.WXShopDto;
import com.youming.youche.system.dto.AuditOutDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 支出接口表(提现接口表) 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("payout/intf")
public class PayoutIntfController extends BaseController<PayoutIntf, IPayoutIntfService>{
    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    @Override
    public IPayoutIntfService getService() {
        return payoutIntfService;
    }


    /*--------------WuHao---------------*/
    /**
     * 付款信息
     */
    @GetMapping("/doQueryPayOutInfo")
    public ResponseResult doQueryPayOutInfo(Long flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PayoutIntf payoutIntf = payoutIntfService.getPayoutIntfPay(flowId,null,accessToken);
        return ResponseResult.success(payoutIntf);
    }
    /**
     * app应收逾期
     */
    @GetMapping("getDueDateDetailsWX")
    public ResponseResult getDueDateDetailsWX(Long userId,Long flowId,String name,Long sourceTenantId,String state,Integer userType,Long pageSize,Long pageNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage page = payoutIntfService.getDueDateDetailsWX(userId,flowId,name,state,sourceTenantId,userType,pageSize,pageNum,accessToken);
        return ResponseResult.success(page);
    }

    /***
     *
     * 接口编码:21116
     * @Description: 微信接口-商家-首页
     * 接口入参：
     *
     * 接口出参：
    totalBalance	可提现	number
    totalMarginBalance	即将到期	number
    platformServiceCharge	平台服务费金额	number
    noVerificationAmount	未核销平台金额(分) number
    productNum	产品管理数量 number
    cooperationWaitAduitNum	合作邀请待审核数量 number
     *
     */
    @GetMapping("getServiceOverView")
    public ResponseResult getServiceOverView(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        WXShopDto wxShopDto = payoutIntfService.getServiceOverView(accessToken);
        return ResponseResult.success(wxShopDto);
    }
    /**
     * 应付逾期-列表查询 21120
     */
    @GetMapping("getOverdue")
    public ResponseResult getOverdue(Long userId, Long orderId, String userType, String name, String businessNumbers, String state,
                                     Long sourceTenantId, @RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize")Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<PayoutInfosOutDto> payoutInfoOutDtoPage = payoutIntfService.getOverdue(userId,orderId,userType,name,businessNumbers,state,sourceTenantId,pageSize,pageNum,accessToken);
        return ResponseResult.success(payoutInfoOutDtoPage);
    }
    /**
     * 应付逾期-同意线上退款21122
     */
    @PostMapping("confirmPaymentLine")
    public ResponseResult confirmPaymentLine(Long flowId,String payAcctId,String receAcctId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = payoutIntfService.confirmPaymentLine(flowId,payAcctId,receAcctId,accessToken);
        return ResponseResult.success(s);
    }

    /**
     * 接口编码21156
     * 应付逾期 付款详情
     */
    @GetMapping("paymentDetails")
    public ResponseResult paymentDetails(Long flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PayoutInfoDto payoutInfoDto = payoutIntfService.paymentDetails(flowId,accessToken);
        return ResponseResult.success(payoutInfoDto);
    }

    /**已线下退款
     * 接口编码 21123
     * param flowId  流水号
     * @return
     * @throws Exception
     */
    @PostMapping("confirmPaymentOffline")
    public ResponseResult confirmPaymentOffline(Long flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = payoutIntfService.confirmPaymentOffline(flowId,accessToken);
        return ResponseResult.success(s);
    }
    /**接口编码21157
     * 判断金额是否足够
     * @param
     * @return
     * @throws Exception
     */
    @PostMapping("balanceJudgmentCD")
    public ResponseResult balanceJudgmentCD(Long balance,Integer type,Integer userType,String payAcctId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BankDto bankDto = payoutIntfService.balanceJudgmentCD(balance,type,userType,payAcctId,accessToken);
        return ResponseResult.success(bankDto);
    }
    /**
     * 接口编码：21167
     * 判断现金付款业务当前操作员是否是最后一个审核人
     */
    @PostMapping("isLastPayCashAuditer")
    public ResponseResult isLastPayCashAuditer(String busiIdString){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<AuditOutDto> auditOutDtos = payoutIntfService.isLastPayCashAuditer(busiIdString,accessToken);
        return ResponseResult.success(auditOutDtos);
    }
    /**
     * 微信付款申请审核通过接口编码21183
     */
    @PostMapping("updatePayManagerStateWX")
    public ResponseResult updatePayManagerStateWX(String desc, Integer chooseResult, Long payId,Integer state){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = payoutIntfService.updatePayManagerState(desc,chooseResult,payId,state,accessToken);
        return ResponseResult.success(s);
    }
    /**
     * 21201
     * @Description: 账户明细-即将到期明细
     * @param userId 资金来源方ID
     * @param orderId 订单ID
     * @param startTime 靠台开始时间
     * @param endTime 靠台结束时间
     * @param sourceRegion 出发地
     * @param desRegion 目的地
     * @param userType 用户类型
     * @return
     * @throws Exception
     */
    @PostMapping("getAccountDetailsNoPay")
    public ResponseResult getAccountDetailsNoPay(String userId, String orderId, String startTime, String endTime, String sourceRegion, String desRegion, Integer userType,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderLimitOutDto> page = payoutIntfService.getAccountDetailsNoPay(userId, orderId, startTime, endTime, sourceRegion, desRegion, userType, pageSize, pageNum, accessToken);
        return ResponseResult.success(page);
    }

}
