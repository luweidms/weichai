package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetails;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsDto;
import com.youming.youche.order.vo.OilRechargeAccountDetailsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@RestController
@RequestMapping("oil/recharge/account/details")
public class OilRechargeAccountDetailsController extends BaseController<OilRechargeAccountDetails, IOilRechargeAccountDetailsService> {
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Override
    public IOilRechargeAccountDetailsService getService() {
        return oilRechargeAccountDetailsService;
    }


    /**
     * niejeiwei
     * 预存资金明细-小程序接口
     * 50058
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getLockBalanceDetailsOrder")
    public ResponseResult getLockBalanceDetailsOrder(OilRechargeAccountDetailsVo vo,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OilRechargeAccountDetailsDto> lockBalanceDetailsOrder = oilRechargeAccountDetailsService.getLockBalanceDetailsOrder(vo, accessToken, pageNum, pageSize);
        return  ResponseResult.success(lockBalanceDetailsOrder);
    }
}
