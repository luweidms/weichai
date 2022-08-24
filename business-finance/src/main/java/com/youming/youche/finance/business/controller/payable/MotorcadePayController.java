package com.youming.youche.finance.business.controller.payable;

import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.payable.IPayByCashService;
import com.youming.youche.finance.vo.order.PayByCashVo;
import com.youming.youche.system.constant.AuditConsts;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName PlatfromPayController
 * @Description 应付逾期
 * @Author zag
 * @Date 2022/4/7 11:37
 */
@RestController
@RequestMapping("motorcadePay")
public class MotorcadePayController {

    @Resource
    protected HttpServletRequest request;

    @DubboReference(version = "1.0.0")
    IPayByCashService payByCashService;

    /**
     * 应付逾期-付款
     *
     * @return com.youming.youche.commons.response.ResponseResult
     * @author 卢威
     * @date 2022/4/7 15:40
     */
    @PostMapping("confirmation")
    public ResponseResult confirmation(@RequestBody PayByCashVo payByCashVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payByCashService.sure(
                AuditConsts.AUDIT_CODE.PAY_CASH_CODE,
                payByCashVo.getFlowId() + "",
                payByCashVo.getDesc(),
                payByCashVo.getChooseResult(),
                payByCashVo.getAccId(),
                payByCashVo.getPayAccId(),
                payByCashVo.getIsAutomatic(),
                payByCashVo.getUserType(),
                payByCashVo.getFileId(),
                null,
                payByCashVo.getFileId(),
                accessToken
                );
        return ResponseResult.success();
    }

    /**
     * 21154 车队小程序 付款
     * @param flowId
     * @param isAutomatic
     * @param status
     * @param userType
     * @param desc
     * @param chooseResult
     * @param accId
     * @param payAccId
     * @param fileId
     * @param serviceFee
     * @return
     */
    @PostMapping("confirmations")
    public ResponseResult confirmations(Long flowId, Integer isAutomatic, Integer status, Integer userType,
            String desc, Integer chooseResult, String accId, String payAccId, String fileId, String serviceFee ) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        payByCashService.sure(
                AuditConsts.AUDIT_CODE.PAY_CASH_CODE,
                flowId + "",
                desc,
                chooseResult,
                accId,
                payAccId,
                isAutomatic,
                userType,
                fileId,
                null,
                fileId,
                accessToken
        );
        return ResponseResult.success();
    }


}
