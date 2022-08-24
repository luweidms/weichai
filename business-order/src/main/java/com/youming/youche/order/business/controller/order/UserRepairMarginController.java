package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.order.api.order.IUserRepairMarginService;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.UserRepairMarginDto;
import com.youming.youche.order.dto.order.RepairAmountOutDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 维修保养记录表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-24
 */
@RestController
@RequestMapping("user/repair/margin")
public class UserRepairMarginController extends BaseController<UserRepairMargin, IUserRepairMarginService> {
    @DubboReference(version = "1.0.0")
    IUserRepairMarginService userRepairMarginService;
    @Override
    public IUserRepairMarginService getService() {
        return userRepairMarginService;
    }

    /**
     *40029
     * @param userId
     * @param serviceUserId
     * @param productId
     * @param amountFee
     * @param tenantId
     * @return
     */
    @GetMapping("queryAdvanceFeeByRepair")
    public ResponseResult queryAdvanceFeeByRepair(Long userId, Long serviceUserId, Long productId, Long amountFee, Long tenantId){
        RepairAmountOutDto repairAmountOutDto = userRepairMarginService.queryAdvanceFeeByRepair(userId,serviceUserId,productId,amountFee,tenantId);
        return ResponseResult.success(repairAmountOutDto);
    }


    /**
     * niejiewei
     * 司机小程序
     * APP-维修保养交易-支付详情
     * 40027
     * @param repairId
     * @return
     */
    @GetMapping("/queryRepairMarginDetail")
    public ResponseResult queryRepairMarginDetail (@RequestParam(value = "repairId") Long repairId ){
        UserRepairMarginDto dto = userRepairMarginService.queryRepairMarginDetail(repairId);
        return  ResponseResult.success(dto);
    }




}
