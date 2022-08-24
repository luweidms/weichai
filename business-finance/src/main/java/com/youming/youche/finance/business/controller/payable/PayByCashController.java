package com.youming.youche.finance.business.controller.payable;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.AesEncryptUtil;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.payable.IPayByCashService;

import com.youming.youche.finance.dto.DoQueryDto;
import com.youming.youche.finance.dto.PayManagerDto;
import com.youming.youche.finance.dto.PayManagerWXDto;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.vo.QueryPayManagerVo;
import com.youming.youche.system.dto.ac.OrderAccountOutDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zengwen
 * @date 2022/4/23 10:09
 */
@RestController
@RequestMapping("pay/by/cash")
public class PayByCashController {

    @Resource
    protected HttpServletRequest request;

    @DubboReference(version = "1.0.0")
    IPayByCashService payByCashService;

    private static final Logger log = LoggerFactory.getLogger(PayByCashController.class);

    /**
     * 是否已经输入错误3次
     */
    @PostMapping("/doQueryPassType")
    public ResponseResult doQueryPassType() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String passType = payByCashService.doQueryPassType(accessToken);
        return ResponseResult.success((Object)passType);
    }

    /**
     * 校验支付
     */
    @PostMapping("/doWithdrawal")
    public ResponseResult doWithdrawal(String payPasswd, Integer status) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payPasswd = AesEncryptUtil.desEncrypt(payPasswd);
        log.info("支付密码："+payPasswd);
        if (!StringUtils.isNotEmpty(payPasswd)) {
            throw new BusinessException("请输入支付密码");
        }
        return ResponseResult.success((Object)payByCashService.doWithdrawal(accessToken, payPasswd, status));
    }

    /**
     * 付款撤销
     */
    @PostMapping("/withdraw")
    public ResponseResult withdraw(Long flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payByCashService.withdraw(accessToken, flowId);
        return ResponseResult.success();
    }

    /**21166
     * 判断短信验证码是否正确
     * @param billId
     * @param captcha
     * @return
     */
    @GetMapping("/checkSmsCode")
    public ResponseResult checkSmsCode(String billId,String captcha){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = payByCashService.checkSmsCode(billId, captcha, accessToken);
        return b?ResponseResult.success():ResponseResult.failure();
    }


    /**
     * 获取验证码
     * @return
     */
    @GetMapping("/doQueryCode")
    public ResponseResult doQueryCode(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = payByCashService.doQueryCode(accessToken);
        return b?ResponseResult.success():ResponseResult.failure();
    }
    /**
     * 接口编码：21168
     * 校验支付密码
     */
    @PostMapping("checkPassword")
    public ResponseResult checkPassword(String payPasswd,Integer status){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String  flag = payByCashService.doWithdrawal(accessToken,payPasswd, status);
        return ResponseResult.success((Object) flag);
    }
    /**
     * 接口编码：21170
     * 获取验证码
     */
    @PostMapping("getdoQueryCodeWx")
    public ResponseResult getdoQueryCodeWx(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = payByCashService.doQueryCode(accessToken);
        return b?ResponseResult.success():ResponseResult.failure();
    }
    /**
     * 接口编码：21171
     * 今日是否已经输入错3次
     */
    @PostMapping("doQueryPassTypeWx")
    public  ResponseResult doQueryPassTypeWx(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String payssType = payByCashService.doQueryPassType(accessToken);
        return ResponseResult.success((Object)payssType);
    }
    /**
     * 微信付款申请接口编码21180
     */
    @PostMapping("doQueryAllPayManagerWX")
    public ResponseResult doQueryAllPayManagerWX(QueryPayManagerVo queryPayManagerVo, Long orgId, Long payAmt, Integer isNeedBiil, Integer payId, Integer pageSize, Integer pageNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<QueryPayManagerDto> payManagerWXDto = payByCashService.doQueryAllPayManagerWX(queryPayManagerVo,orgId,payAmt,isNeedBiil,payId,accessToken,pageSize,pageNum);
        return ResponseResult.success(payManagerWXDto);
    }
    /**
     * 微信付款申请付款类型数据获取接口编码21181
     */
    @PostMapping("doQueryPayType")
    public ResponseResult doQueryPayType(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<DoQueryDto> list = payByCashService.doQueryPayType(accessToken);
        return ResponseResult.success(list);
    }
    /**
     * 微信付款申请审核接口编码21182
     */
    @PostMapping("getPayManagerWX")
    public ResponseResult getPayManagerWX(Long payId,Integer state,Long payAmt,Integer isNeedBiil){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PayManagerDto payManagerDto = payByCashService.getPayManager(payId,state,payAmt,isNeedBiil,accessToken);
        return ResponseResult.success(payManagerDto);
    }
    /**
     * 付款撤回小程序接口接口编码：21190
     */
    @PostMapping("withdrawWX")
    public ResponseResult withdrawWX(Long flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payByCashService.withdraw(accessToken,flowId);
        return ResponseResult.success("Y");
    }

    /**
     * 借支-车险报告一级审核在22003
     * @throws Exception
     */
    @PostMapping("oaLoanDriverAudit")
    public  ResponseResult oaLoanDriverAudit(Long busiId, String desc, String chooseResult, String loanSubject, String remark,
            Integer loanTransReason, String accidentDate, String insuranceDate, String accidentType, String accidentReason,
            String dutyDivide, String accidentDivide, String insuranceFirm, String insuranceMoney,
            String reportNumber, String accidentExplain){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = payByCashService.oaLoanDriverAudit(busiId,desc,chooseResult,loanSubject,remark,
                loanTransReason,accidentDate,insuranceDate,accidentType,accidentReason,
                dutyDivide,accidentDivide,insuranceFirm,insuranceMoney,reportNumber,accidentExplain,accessToken);
        return ResponseResult.success(s);
    }
}
