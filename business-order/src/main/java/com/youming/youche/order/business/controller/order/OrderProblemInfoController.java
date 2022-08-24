package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.annotation.Dict;
import com.youming.youche.order.api.order.IOrderProblemInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderProblemInfo;
import com.youming.youche.order.domain.order.ProblemVerOpt;
import com.youming.youche.order.dto.OrderInfoListDto;
import com.youming.youche.order.dto.OrderProblemInfoDto;
import com.youming.youche.order.dto.OrderProblemInfoOutDto;
import com.youming.youche.order.dto.SaveProblemInfoDto;
import com.youming.youche.order.dto.orderSchedulerDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 订单异常登记表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@RestController
@RequestMapping("order/problem/info")
public class OrderProblemInfoController extends BaseController<OrderProblemInfo, IOrderProblemInfoService> {

    @DubboReference(version = "1.0.0")
    IOrderProblemInfoService iOrderProblemInfoService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @Override
    public IOrderProblemInfoService getService() {
        return null;
    }

    /**
     * 获取订单的异常列表
     */
    @GetMapping("/queryOrderProblems")
    public ResponseResult queryOrderProblems(Long orderId, Long problemId,
                                             String state, String problemType,
                                             Integer problemCondition,
                                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderProblemInfo> orderProblemInfoPage = iOrderProblemInfoService.queryOrderProblemInfoPag(orderId, problemId, state, problemType, problemCondition,accessToken, pageNum, pageSize);
        return ResponseResult.success(orderProblemInfoPage);
    }

    @GetMapping("/getOrderInfo")
    public ResponseResult getOrder(Long orderId){
        OrderInfoListDto orderProblemInfo = iOrderProblemInfoService.getOrder(orderId);
        return ResponseResult.success(orderProblemInfo);
    }

    @GetMapping("/getProblemInfo")
    @Dict
    public ResponseResult getProblemInfo(Long problemId){
        OrderProblemInfoDto orderProblemInfo = iOrderProblemInfoService.getOrderProblemInfo(problemId);
        return ResponseResult.success(orderProblemInfo);

    }

    /**
     * 获取异常责任司机[30053]
     * @return
     * @throws Exception
     */
    @GetMapping("/getCarDriversByOrderId")
    public ResponseResult getCarDriversByOrderId(Long orderId){
        List<orderSchedulerDto> orderSchedulerDtos = iOrderSchedulerService.queryOrderDriverList(orderId);
        return ResponseResult.success(orderSchedulerDtos);
    }

    @GetMapping("/queryProblemVerOptList")
    public ResponseResult queryProblemVerOptList(Long problemId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<ProblemVerOpt> problemVerOptByProId = iOrderProblemInfoService.getProblemVerOptByProId(problemId,accessToken);
        return ResponseResult.success(problemVerOptByProId);
    }

    /**
     * 保存订单异常信息
     * @return
     */
    @PostMapping("/saveProblemInfo")
    public ResponseResult saveProblemInfo(SaveProblemInfoDto saveProblemInfoDto){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = iOrderProblemInfoService.saveOrUpdateOrderProblemInfo(saveProblemInfoDto, accessToken);

        return b?ResponseResult.success("添加订单异常成功"):ResponseResult.success("修改订单异常成功");
    }

    /**
     * 取消异常
     * 聂杰伟
     * @param problemId
     * @return
     */
    @PostMapping("/cancleProblemInfo")
    public ResponseResult cancleProblemInfo(@RequestParam(value = "problemId") Long problemId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderProblemInfoService.cancelProblem(problemId,accessToken);
        return  ResponseResult.success("取消订单异常成功");
    }

    /**
     * 查询订单异常列表(30006)
     * @param orderId
     * @param isAudit
     * @param isWx
     * @return
     */
    @GetMapping("/queryOrderProblemInfoList")
    public ResponseResult queryOrderProblemInfoList(Long orderId,boolean isAudit,boolean isWx){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderProblemInfo> orderProblemInfos = iOrderProblemInfoService.queryOrderProblemInfoList(orderId, isAudit, isWx, accessToken);
        return ResponseResult.success(orderProblemInfos);
    }

    /**
     * App接口-异常补偿 28313
     */
    @GetMapping("doQueryAbnormalCompensation")
    public ResponseResult doQueryAbnormalCompensation(Long orderId, Long tenantId) {
        List<OrderProblemInfoOutDto> orderProblemInfoOutDtos = iOrderProblemInfoService.doQueryAbnormalCompensation(orderId, tenantId);
        return ResponseResult.success(orderProblemInfoOutDtos);
    }

    /**
     * App接口-异常扣减 28315
     */
    @GetMapping("doQueryAbnormalDeduction")
    public ResponseResult doQueryAbnormalDeduction(Long orderId, Long tenantId) {
        List<OrderProblemInfoOutDto> orderProblemInfoOutDtos = iOrderProblemInfoService.doQueryAbnormalDeduction(orderId, tenantId);
        return ResponseResult.success(orderProblemInfoOutDtos);
    }


}
