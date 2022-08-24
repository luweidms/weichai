package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.dto.order.OaLoanDto;
import com.youming.youche.order.dto.order.OaLoanListDto;
import javafx.scene.control.Pagination;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 借支信息表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@RestController
@RequestMapping("oa/loan")
public class OaLoanController extends BaseController<OaLoan, IOaLoanService> {

    @DubboReference(version = "1.0.0")
    IOaLoanService oaLoanService;

    @Override
    public IOaLoanService getService() {
        return oaLoanService;
    }

    /**
     * 借支审核-借支列表查询  (小程序)
     * 接口编码：22004
     */
    @GetMapping("queryOaLoanList")
    public ResponseResult queryOaLoanList(String oaLoanId, String loanSubjects, Long orderId, String plateNumber,
                                          String userName, String mobilePhone, String states, Integer queryType, Boolean waitDeal,
                                          @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OaLoanListDto> oaLoanListDtoPage = oaLoanService.queryOaLoanList(oaLoanId, loanSubjects, orderId, plateNumber, userName,
                mobilePhone, states, queryType, waitDeal, accessToken, pageNum, pageSize);
        return ResponseResult.success(oaLoanListDtoPage);
    }

    /**
     * 小程序 借支审核-借支详情
     * 接口编码：22005
     *
     * @param lId 借支Id
     */
    @GetMapping("getOaLoanDetail")
    public ResponseResult getOaLoanDetail(Long lId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        OaLoanDto oaLoanOut = oaLoanService.queryOaLoanByIdWx(lId,accessToken);
        return ResponseResult.success(oaLoanOut);
    }

}
