package com.youming.youche.table.business.controller.workbench;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.table.api.workbench.IOperationWorkbenchInfoService;
import com.youming.youche.table.domain.workbench.OperationWorkbenchInfo;
import com.youming.youche.table.dto.workbench.BossWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.FinancialWorkbenchInfoDto;
import com.youming.youche.table.dto.workbench.WechatOperationWorkbenchInfoDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zengwen
 * @date 2022/5/3 20:35
 */
@RestController
@RequestMapping("operation/workbench/info")
public class OperationWorkbenchInfoController extends BaseController {

    @DubboReference(version = "1.0.0")
    IOperationWorkbenchInfoService iOperationWorkbenchInfoService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 获取运营工作台数据
     */
    @GetMapping("getOperationWorkbenchInfo")
    public ResponseResult getOperationWorkbenchInfo() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OperationWorkbenchInfo operationWorkbenchInfo = iOperationWorkbenchInfoService.getOperationWorkbenchInfo(accessToken);
        return ResponseResult.success(operationWorkbenchInfo);
    }

    /**
     * 获取财务工作台数据
     */
    @GetMapping("getFinancialWorkbenchInfo")
    public ResponseResult getFinancialWorkbenchInfo() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        FinancialWorkbenchInfoDto financialWorkbenchInfo = iOperationWorkbenchInfoService.getFinancialWorkbenchInfo(accessToken);
        return ResponseResult.success(financialWorkbenchInfo);
    }

    /**
     * 获取老板工作台数据
     */
    @GetMapping("getBossWorkbenchInfo")
    public ResponseResult getBossWorkbenchInfo() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        BossWorkbenchInfoDto bossWorkbenchInfo = iOperationWorkbenchInfoService.getBossWorkbenchInfo(accessToken);
        return ResponseResult.success(bossWorkbenchInfo);
    }

    /**
     * 获取微信车队小程序运营报表数据
     */
    @GetMapping("getWechatOperationWorkbenchInfo")
    public ResponseResult getWechatOperationWorkbenchInfo(Integer type) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        WechatOperationWorkbenchInfoDto wechatOperationWorkbenchInfo = iOperationWorkbenchInfoService.getWechatOperationWorkbenchInfo(accessToken, type);
        return ResponseResult.success(wechatOperationWorkbenchInfo);
    }
}
