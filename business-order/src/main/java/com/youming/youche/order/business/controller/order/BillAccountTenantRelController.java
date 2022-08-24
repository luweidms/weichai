package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IBillAccountTenantRelService;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.domain.order.BillAccountTenantRel;
import com.youming.youche.order.dto.order.OilBalanceForOilAccountDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("bill/account/tenant/rel")
public class BillAccountTenantRelController extends BaseController<BillAccountTenantRel, IBillAccountTenantRelService>{
    @DubboReference(version = "1.0.0")
    IBillAccountTenantRelService billAccountTenantRelService;

    @DubboReference(version = "1.0.0")
    IOrderStatementService orderStatementService;

    public IBillAccountTenantRelService getService() {
        return billAccountTenantRelService;
    }

    /**
     * ---------------------wuhao--------------------------
     */
    /**
     * 获取可使用的客户油[30046]
     *
     * 获取各分配油来源的余额
     * @return
     * @throws Exception
     */
    @PostMapping("/getCustomOil")
    public ResponseResult getCustomOil(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OilBalanceForOilAccountDto oilBalanceForOilAccountDto = orderStatementService.querOilBalanceForOilAccountType(accessToken);
        return ResponseResult.success(oilBalanceForOilAccountDto);
    }
}
