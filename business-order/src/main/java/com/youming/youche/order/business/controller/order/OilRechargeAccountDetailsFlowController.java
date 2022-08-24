package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOilRechargeAccountDetailsFlowService;
import com.youming.youche.order.domain.order.OilRechargeAccountDetailsFlow;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsFlowOutDto;
import com.youming.youche.order.vo.OilRechargeAccountDetailsFlowVo;
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
 * @author liangyan
 * @since 2022-03-23
 */
@RestController
@RequestMapping("oil/recharge/account/details/flow")
public class OilRechargeAccountDetailsFlowController extends BaseController<OilRechargeAccountDetailsFlow, IOilRechargeAccountDetailsFlowService> {
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Override
    public IOilRechargeAccountDetailsFlowService getService() {
        return oilRechargeAccountDetailsFlowService;
    }

    /**
     * niejiwei
     * 司机小程序
     * 充值流水列表-小程序接口
     * 50057
     * @return
     */
    @GetMapping("/getOilRechargeAccountDetailsFlow")
    public ResponseResult getOilRechargeAccountDetailsFlow(OilRechargeAccountDetailsFlowVo vo,
                                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OilRechargeAccountDetailsFlowOutDto> oilRechargeAccountDetailsFlow =
                oilRechargeAccountDetailsFlowService.getOilRechargeAccountDetailsFlow(vo, pageNum, pageSize, accessToken);
        return  ResponseResult.success(oilRechargeAccountDetailsFlow);
    }
}
