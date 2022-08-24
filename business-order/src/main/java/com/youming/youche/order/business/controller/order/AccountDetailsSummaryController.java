package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IAccountDetailsSummaryService;
import com.youming.youche.order.domain.order.AccountDetailsSummary;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@RestController
@RequestMapping("account/details/summary")
public class AccountDetailsSummaryController extends BaseController<AccountDetailsSummary, IAccountDetailsSummaryService> {
    @DubboReference(version = "1.0.0")
    IAccountDetailsSummaryService accountDetailsSummaryService;
    @Override
    public IAccountDetailsSummaryService getService() {
        return accountDetailsSummaryService;
    }

    /**
     * App接口-已付金额 28318
     */
    @GetMapping("doQueryAmountPaidApp")
    public ResponseResult doQueryAmountPaidApp(Long orderId, Long payeeUserId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<AccountDetailsSummary> accountDetailsSummaries = accountDetailsSummaryService.doQueryAmountPaidApp(orderId, payeeUserId, accessToken);
        return ResponseResult.success(accountDetailsSummaries);

    }
}
