package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderTransferInfoService;
import com.youming.youche.order.domain.order.OrderTransferInfo;
import com.youming.youche.order.dto.OrderTransferInfoDto;
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
@RequestMapping("order/transfer/info")
public class OrderTransferInfoController extends BaseController<OrderTransferInfo, IOrderTransferInfoService> {
    @DubboReference(version = "1.0.0")
    IOrderTransferInfoService orderTransferInfoService;
    @Override
    public IOrderTransferInfoService getService() {
        return null;
    }

    /**
     * 在线接单--列表查询（30034）
     * @param orderTransferInfoDto
     * @return
     */
    @GetMapping("/queryOrderTransferInfoList")
    public ResponseResult queryOrderTransferInfoList(OrderTransferInfoDto orderTransferInfoDto,
                                                     @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderTransferInfoDto> orderTransferInfoDtoPage = orderTransferInfoService.queryOrderTransferInfoList(orderTransferInfoDto, pageNum, pageSize, accessToken);
        return ResponseResult.success(orderTransferInfoDtoPage);
    }
}
