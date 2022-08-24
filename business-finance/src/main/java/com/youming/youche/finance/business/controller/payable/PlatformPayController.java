package com.youming.youche.finance.business.controller.payable;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.payable.IPlatformPayService;
import com.youming.youche.finance.dto.payable.BalanceJudgmentDto;
import com.youming.youche.finance.vo.payable.BalanceJudgmentVo;
import com.youming.youche.finance.vo.payable.ComfirmPaymentVo;
import com.youming.youche.order.api.order.IPayoutIntfService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName PlatfromPayController
 * @Description 平台支付
 * @Author zag
 * @Date 2022/4/7 11:37
 */
@RestController
@RequestMapping("platformpay")
public class PlatformPayController {

    @DubboReference(version = "1.0.0")
    IPlatformPayService platformPayService;

    @Resource
    protected HttpServletRequest request;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    /**
     * 判断是否确认今天不在输入密码 1:今天不再输入2:今天还需要输入
     * @author zag
     * @date 2022/4/7 15:40
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @GetMapping("doQueryCheckPasswordErr")
    public ResponseResult doQueryCheckPasswordErr(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Object result=platformPayService.doQueryCheckPasswordErr(accessToken);
        return ResponseResult.success(result);
    }

    /**
     * 确认付款
     * @author zag
     * @date 2022/4/8 16:07
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody ComfirmPaymentVo comfirmPaymentVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        platformPayService.sure(comfirmPaymentVo,accessToken);
        return ResponseResult.success();
    }

    /**
     * 停止自动付款
     * @author zag
     * @date 2022/4/8 16:07
     * @return com.youming.youche.commons.response.ResponseResult
     */
    @PostMapping("stopAutomatic")
    public ResponseResult stopAutomatic(Long flowId, Integer isNeedBill){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        platformPayService.stopAutomatic(flowId,isNeedBill,accessToken);
        return ResponseResult.success();
    }


/**
 * -------------------------------WuHao--------------------------
 */
    /**
     * 计算服务费
     * @return
     * @throws Exception
     */
    @GetMapping("/doQueryServiceFee")
    public Long doQueryServiceFee(String flowIds,String sumTxnFeeDouble){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<Long> flowIdList = new ArrayList<>();
        if(StringUtils.isBlank(flowIds)){
            throw new BusinessException("请选择要打款的信息");
        }
        String[] flowIdStrs = flowIds.split(",");
        if(flowIdStrs != null && flowIdStrs.length > 0){
            for(String flowIsStr : flowIdStrs){
                flowIdList.add(Long.valueOf(flowIsStr));
            }
        }
        Map map = new HashMap();
        long totalServiceFee = 0;
        if(flowIdList.size() == 1){
            if(StringUtils.isBlank(sumTxnFeeDouble) || sumTxnFeeDouble.equals("0")){
                sumTxnFeeDouble = "0";
            }
            Long cash = com.youming.youche.util.CommonUtils.multiply(sumTxnFeeDouble);
            totalServiceFee = payoutIntfService.queryOrdServiceFee(cash,flowIdList.get(0),accessToken);

        }else if(flowIdList.size() > 1){
            for(Long flowId : flowIdList) {
                long serviceFee = payoutIntfService.queryOrdServiceFee(0L,flowId,accessToken);
                totalServiceFee +=  serviceFee;
            }
        }
        return totalServiceFee;
    }

    /**
     * 线上打款查询余额是否充足
     */
    @PostMapping("/balanceJudgment")
    public ResponseResult balanceJudgment(BalanceJudgmentVo balanceJudgmentVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BalanceJudgmentDto balanceJudgmentDto = platformPayService.balanceJudgment(accessToken, balanceJudgmentVo);
        return ResponseResult.success(balanceJudgmentDto);
    }

    /**
     * 接口编码：21169
     * 判断今日是否还需要输入密码  1今天不再输入  2今天还需要输入
     */
    @PostMapping("isNeedPassword")
    public ResponseResult isNeedPassword(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = platformPayService.doQueryCheckPasswordErr(accessToken);
        return ResponseResult.success((Object)s);
    }

    /**
     * 同意线上退款判断余额是否充足
     * 接口编码 21124
     * @param
     * @return
     * @throws Exception
     */
    @GetMapping("balanceJudgments")
    public ResponseResult babalanceJudgments(Long userId, Integer userType, Long balance, String payAcctId){
        BalanceJudgmentDto balanceJudgmentDto = platformPayService.babalanceJudgments(userId,userType,balance,payAcctId);
        return ResponseResult.success(balanceJudgmentDto);
    }
}
