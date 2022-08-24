package com.youming.youche.order.business.controller.order;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.order.api.order.IOrderCostReportService;
import com.youming.youche.order.domain.order.OrderCostReport;
import com.youming.youche.order.dto.order.OrderCostReportCardDto;
import com.youming.youche.order.dto.order.OrderCostReportDto;
import com.youming.youche.order.vo.OrderCostOtherTypeVO;
import com.youming.youche.system.api.ISysOperLogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
*  前端控制器
* </p>
* @author liangyan
* @since 2022-03-22
*/
@RestController
@RequestMapping("order/cost/report")
public class OrderCostReportController extends BaseController<OrderCostReport,IOrderCostReportService> {

    @Override
    public IOrderCostReportService getService() {
        return iOrderCostReportService;
    }
    @DubboReference(version = "1.0.0")
    IOrderCostReportService iOrderCostReportService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    /**
     * 获取费用上报卡号列表
     * @return
     * @throws Exception
     */
    @GetMapping("/getOrderCostReportCard")
    public ResponseResult getOrderCostReportCard(
                                          Long orderId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            OrderCostReportCardDto page1 = iOrderCostReportService.queryAll( accessToken, orderId);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }


    /**
     * 获取其他费用类型
     * @return
     * @throws Exception
     */
    @GetMapping("/getOrderCostReportType")
    public ResponseResult getOrderCostReportType() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<OrderCostOtherTypeVO> page1 = iOrderCostReportService.getOrderCostOtherTypes( accessToken);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }

    /**
     * 根据订单号查询上报信息
     * @return
     * @throws Exception
     */
    @GetMapping("/getOrderCostDetailReportByOrderId")
    public ResponseResult getOrderCostDetailReportByOrderId(Long orderId) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            OrderCostReportDto  orderCostReportDto = iOrderCostReportService.getOrderCostDetailReportByOrderId(orderId,accessToken);
            return ResponseResult.success(orderCostReportDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }

    /**
     * 添加类型 （弃用）
     * @return
     * @throws Exception
     */
    @PostMapping("/addOrderCostReportType")
    public ResponseResult addOrderCostReportType(Long id, String typeName) {
        try {
            if(StringUtils.isBlank(typeName)) {
                throw new BusinessException("请输入类型名称！");
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iOrderCostReportService.addOrderCostOtherType(id,typeName,accessToken);
            return ResponseResult.success("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("添加失败");
        }
    }

    /**
     * 根据订单号上报信息
     * @return
     * @throws Exception
     */
    @PostMapping("/doSaveOrUpdate")
    public ResponseResult doSaveOrUpdate(@RequestBody OrderCostReportDto orderCostReportDto) {

            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iOrderCostReportService.doSaveOrUpdateNew(orderCostReportDto,accessToken);
            return ResponseResult.success("添加成功");
    }
}
