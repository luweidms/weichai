package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.domain.order.OrderAgingAppealInfo;
import com.youming.youche.order.domain.order.OrderAgingInfo;
import com.youming.youche.order.dto.AgingInfoDto;
import com.youming.youche.order.dto.AuditAgingDto;
import com.youming.youche.order.dto.OrderAgingInfoDto;
import com.youming.youche.order.dto.OrderAgingListDto;
import com.youming.youche.order.dto.OrderReportDto;
import com.youming.youche.order.dto.order.OrderAgingAppealInfoDto;
import com.youming.youche.order.dto.order.OrderAgingInfoOutDto;
import com.youming.youche.order.dto.order.OrderAgingListOutDto;
import com.youming.youche.order.vo.OrderAgingListInVo;
import com.youming.youche.order.vo.QueryOrderLineNodeListVo;
import com.youming.youche.order.vo.SaveAppealInfoVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 时效罚款表 前端控制器
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@RestController
@RequestMapping("order/aging/info")
public class OrderAgingInfoController extends BaseController<OrderAgingInfo, IOrderAgingInfoService> {

    @DubboReference(version = "1.0.0")
    IOrderAgingInfoService orderAgingInfoService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderAgingAppealInfoService orderAgingAppealInfoService;

    @Override
    public IOrderAgingInfoService getService() {
        return orderAgingInfoService;
    }

    /**
     * 获取时效处理列表【30041 isWx = true】
     * @return
     * @throws Exception
     */
    @GetMapping({"queryOrderAgingList"})
    public ResponseResult queryOrderAgingList(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                               OrderAgingListInVo orderAgingListInVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderAgingInfoDto> orderAgingInfoDtoPage = orderAgingInfoService.queryOrderAgingList(orderAgingListInVo,
                accessToken, pageSize, pageNum);
        return ResponseResult.success(orderAgingInfoDtoPage);
    }


    /**
     * 保存时效罚款[30064]
     * @param agingInfoDto
     * @return
     */
    @PostMapping("saveOrUpdateOrderAgingInfo")
    public ResponseResult saveOrUpdateOrderAgingInfo(@RequestBody AgingInfoDto agingInfoDto){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return orderAgingInfoService.saveOrUpdateOrderAgingInfo(agingInfoDto,accessToken)?
                ResponseResult.success("添加时效罚款成功"):ResponseResult.success("修改时效罚款成功");
    }

    /**
     * 审核操作 todo 有问题
     * @param auditAgingDto
     * @param
     * @return
     */
    @PostMapping("auditAging")
    public  ResponseResult auditAging(@RequestBody AuditAgingDto auditAgingDto){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean aBoolean = orderAgingInfoService.auditAging(auditAgingDto.getAgingId(),
                auditAgingDto.getVerifyDesc(), accessToken);
        return aBoolean?ResponseResult.success("审核时效罚款成功"):ResponseResult.failure("审核时效罚款失败");
    }

    /**
     * 司机申诉第一次审核
     * @return
     * @throws Exception
     */
    @PostMapping("auditAppeal")
    public ResponseResult auditAppeal(@RequestParam("appealId") Long appealId,@RequestParam("verifyDesc") String verifyDesc,@RequestParam("dealFinePrice") String dealFinePrice){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if(appealId==null||appealId<=0) {
            throw new BusinessException("未找到司机申诉信息，请联系客服！");
        }
        if(StringUtils.isEmpty(verifyDesc)) {
            throw new BusinessException("请输入审核意见！");
        }
        if (StringUtils.isBlank(dealFinePrice) || Double.parseDouble(dealFinePrice) < 0) {
            throw new BusinessException("处理金额不正确，请重新填写！");
        }
        Double dealFinePriceDouble = Double.parseDouble(dealFinePrice) * 100;

        orderAgingAppealInfoService.verifyFirst(appealId, verifyDesc,dealFinePriceDouble.longValue(),accessToken);
        return ResponseResult.success("审核成功");
    }

    /**
     * 获取司机申诉信息
     * @return
     * @throws Exception
     */
    @PostMapping("getAppealinfo")
    public ResponseResult getAppealinfo(@RequestParam("agingId") Long agingId){
        if(agingId==null||agingId<=0) {
            throw new BusinessException("未找到时效罚款，请联系客服！");
        }
        OrderAgingAppealInfo orderAgingAppealInfo=orderAgingAppealInfoService.getAppealInfoBYAgingId(agingId, false);
        if(orderAgingAppealInfo==null) {
            orderAgingAppealInfo=new OrderAgingAppealInfo();
        }
        return ResponseResult.success(orderAgingAppealInfo);
    }

    /**
     * 取消时效罚款
     * @return
     */
    @GetMapping("cancleAgingInfo")
    public ResponseResult cancleAgingInfo(@RequestParam("id")Long id){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return orderAgingInfoService.cancleAgingInfo(id,accessToken)?ResponseResult.success("取消时效罚款成功"):ResponseResult.failure("取消时效罚款失败");
    }



    /**
     * 查询订单时效列表[30055]
     * @return
     * @throws Exception
     */
    @GetMapping("queryOrderAgingInfoList")
    public ResponseResult queryOrderAgingInfoList(@RequestParam("orderId") Long orderId,
                                                  @RequestParam("selectType") Integer selectType){
        List<OrderAgingListOutDto> orderAgingListOutDtos = orderAgingInfoService.queryOrderAgingInfoList(orderId, selectType);
        return ResponseResult.success(orderAgingListOutDtos);
    }

    /**
     * 查询时效罚款[30052]
     */
    @GetMapping("queryOrderAgingInfo")
    public ResponseResult queryOrderAgingInfo(Long agingId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAgingListOutDto orderAgingListOutDto = orderAgingInfoService.queryOrderAgingInfo(agingId, accessToken);
        return ResponseResult.success(orderAgingListOutDto);
    }


    /**
     *  时效详情 30066
     * @param id 订单时效 id
     * @return
     */
    @GetMapping("/getAgingInfo")
    public ResponseResult getAgingInfo(@RequestParam(value = "id") Long id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (id == null || id <= 0) {
            throw new BusinessException("时效ID不能为空，请联系客服！");
        }
        OrderAgingInfo orderAgingInfo = orderAgingInfoService.getAgingInfo(id, accessToken);
        return ResponseResult.success(orderAgingInfo);
    }

    /**
     * 查询订单报备
     * @param orderId 订单号
     * @return
     */
    @GetMapping("/queryOrderReport")
    public ResponseResult queryOrderReport(@RequestParam("orderId") Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (orderId == null || orderId < 0) {
            throw new BusinessException("订单号不能为空，请联系客服！");
        }
        List<OrderReportDto> reportDtoList = orderAgingInfoService.queryOrderReport(orderId, accessToken);
        return ResponseResult.success(reportDtoList);
    }

    /**
     * 获取订单的各个路段 [30054]
     *
     * @param orderId 订单id
     * @return
     */
    @GetMapping("/getOrderLineNodeList")
    public ResponseResult getOrderLineNodeList(@RequestParam("orderId") Long orderId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<QueryOrderLineNodeListVo> maps = iOrderSchedulerService.queryOrderLineNodeList(orderId, accessToken);
        return ResponseResult.success(maps);
    }

    /**
     * 30056 WX接口-申诉第一次审核
     */
    @PostMapping("/agingAppealVerifyFirst")
    public ResponseResult agingAppealVerifyFirst(Long appealId,String verifyDesc,String dealFinePrice) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Double dealFinePriceDouble = Double.parseDouble(dealFinePrice) * 100;
        orderAgingAppealInfoService.verifyFirst(appealId, verifyDesc, dealFinePriceDouble.longValue(), accessToken);
        return ResponseResult.success();
    }

    /**
     * 时效第一次审核[30059]
     */
    @PostMapping("/agingInfoVerifyFirst")
    public ResponseResult agingInfoVerifyFirst(Long agingId,String verifyDesc) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Boolean aBoolean = orderAgingInfoService.auditAging(agingId,
                verifyDesc, accessToken);
        return aBoolean?ResponseResult.success("审核时效罚款成功"):ResponseResult.failure("审核时效罚款失败");
    }

    /**
     * 取消时效罚款 30065
     */
    @PostMapping("/cancelOrderAgingInfo")
    public ResponseResult cancelOrderAgingInfo(Long agingId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderAgingInfoService.cancelOrderAgingInfo(agingId, accessToken);
        return ResponseResult.success();
    }


    /**
     * 添加时效申诉（30025）
     * @param saveAppealInfoVo
     * @return
     */
    @PostMapping("/saveAppealInfo")
    public ResponseResult saveAppealInfo(SaveAppealInfoVo saveAppealInfoVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        boolean b = orderAgingInfoService.saveAppealInfo(saveAppealInfoVo, accessToken);
        return ResponseResult.success(b);
    }


    /**
     * app-查询时效罚款列表(30026)
     * @param orderId
     * @param userId
     * @return
     */
    @GetMapping("/getOrderAgingInfoList")
    public ResponseResult getOrderAgingInfoList(Long orderId,Long userId){
        List<OrderAgingListDto> orderAgingListDtos = orderAgingInfoService.getOrderAgingInfoList(orderId, userId);
        return ResponseResult.success(orderAgingListDtos);
    }


    /**
     * 查询时效申诉(30027)
     * @param agingId
     * @return
     */
    @GetMapping("/queryAppealInfo")
    public ResponseResult queryAppealInfo(Long agingId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAgingAppealInfoDto orderAgingAppealInfo = orderAgingInfoService.queryAppealInfo(agingId,accessToken);
        return ResponseResult.success(orderAgingAppealInfo);
    }


    /**
     * 保存订单报备(30004)
     * @param userId
     * @param orderId
     * @param imgIds
     * @param imgUrls
     * @param reportDesc
     * @param reportType
     * @return
     */
    @PostMapping("/saveOrderReport")
    public ResponseResult saveOrderReport(Long userId,Long orderId,String imgIds,String imgUrls,String reportDesc,Integer reportType){
        boolean b = orderAgingInfoService.saveOrderReport(userId, orderId, imgIds, imgUrls, reportDesc, reportType);
        return ResponseResult.success(b);
    }

    /**
     * App接口-时效罚款  28317
     */
    @GetMapping("doQueryTimeLimitFine")
    public ResponseResult doQueryTimeLimitFine(Long orderId, Long tenantId) {
        List<OrderAgingInfoOutDto> orderAgingInfoOutDtos = orderAgingInfoService.doQueryTimeLimitFine(orderId, tenantId);
        return ResponseResult.success(orderAgingInfoOutDtos);
    }

}
