package com.youming.youche.finance.api.payable;

import com.youming.youche.finance.dto.payable.BalanceJudgmentDto;
import com.youming.youche.finance.vo.payable.BalanceJudgmentVo;
import com.youming.youche.finance.vo.payable.ComfirmPaymentVo;

import java.util.Map;

/**
 * @InterfaceName IPlatformPayService
 * @Description 添加描述
 * @Author zag
 * @Date 2022/4/7 15:33
 */
public interface IPlatformPayService {
    /**
     * 接口编码：21169
     * 判断今日是否还需要输入密码  1今天不再输入  2今天还需要输入
     */
    String doQueryCheckPasswordErr(String accessToken);

    /**
     * 确认付款
     * @param comfirmPaymentVo
     * @param accessToken
     * @return void
     */
    void sure(ComfirmPaymentVo comfirmPaymentVo,String accessToken);

    /**
     * 线上打款查询余额是否充足
     *
     * @param balanceJudgmentVo
     */
    BalanceJudgmentDto balanceJudgment(String accessToken, BalanceJudgmentVo balanceJudgmentVo);

    /**
     * 流程结束，审核不通过的回调方法
     * @param busiId
     * @param desc
     * @param paramsMap
     */
    void fail(Long busiId, String desc, Map paramsMap, String accessToken);

    /**
     * 停止自动付款
     * @author zag
     * @date 2022/4/30 15:06
     * @param flowId
     * @param isNeedBill
     * @param accessToken
     * @return void
     */
    void stopAutomatic(Long flowId,Integer isNeedBill,String accessToken);
    /**
     * 同意线上退款判断余额是否充足
     * 接口编码 21124
     * @param
     * @return
     * @throws Exception
     */
    BalanceJudgmentDto babalanceJudgments(Long userId, Integer userType, Long balance, String payAcctId);
}