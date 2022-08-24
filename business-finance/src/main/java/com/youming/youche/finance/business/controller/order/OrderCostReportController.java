package com.youming.youche.finance.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.order.IOrderCostReportService;
import com.youming.youche.finance.business.controller.OaLoanController;
import com.youming.youche.finance.domain.order.OrderCostReport;
import com.youming.youche.finance.dto.order.OrderCostReportDto;
import com.youming.youche.finance.dto.order.OrderMainReportDto;
import com.youming.youche.finance.vo.order.OrderCostReportVo;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *  聂杰伟
 *  订单成本上报公里数表
 * @author Terry
 * @since 2022-03-09
 */
@RestController
@RequestMapping("order/cost/report")
public class OrderCostReportController extends BaseController<OrderCostReport, IOrderCostReportService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCostReportController.class);
    @DubboReference(version = "1.0.0")
    IOrderCostReportService iOrderCostReportService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IOrderCostReportService getService() {
        return iOrderCostReportService;
    }

    /**
     *  接口说明 获取费用上报核实列表
     *  聂杰伟
     * @param orderId 订单号
     * @param plateNumber 车牌号
     * @param userName 司机姓名
     * @param linkPhone 手机号
     * @param startTime 提交时间
     * @param endTime 提交时间
     * @param subUserName 提交人
     * @param sourceRegion 出发城市
     * @param desRegion 目的城市
     * @param state
     * @param waitDeal 待我处理
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/queryReport")
    public ResponseResult selectReport(@RequestParam(value = "orderId",required = false) String orderId,
                                        @RequestParam(value = "plateNumber",required = false) String plateNumber,
                                        @RequestParam(value = "userName",required = false) String userName,
                                        @RequestParam(value = "linkPhone",required = false) String linkPhone,
                                        @RequestParam(value = "startTime",required = false) String startTime,
                                        @RequestParam(value = "endTime",required = false) String endTime,
                                        @RequestParam(value = "subUserName",required = false) String subUserName,
                                        @RequestParam(value = "sourceRegion",required = false) Integer sourceRegion,
                                        @RequestParam(value = "desRegion",required = false) Integer desRegion,
                                        @RequestParam(value = "state", required = false) Integer state,
                                        @RequestParam(value = "waitDeal", required = true, defaultValue = "false") Boolean waitDeal,
                                        @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<OrderCostReportDto> orderCostReportPage = iOrderCostReportService.selectReport(orderId, plateNumber, userName, linkPhone, startTime,
                    endTime, subUserName, state,sourceRegion,desRegion ,waitDeal, accessToken, pageNum, pageSize);
            return ResponseResult.success(orderCostReportPage);
    }

    /**
     * 接口说明 上报费用审核
     * 聂杰伟
     * @param busiCode
     * @param busiId
     * @param desc
     * @param chooseResult
     * @param loadMileage
     * @param capacityLoadMileage
     * @return
     */
    @PostMapping("/auditOrderCostReport")
    public  ResponseResult auditOrderCostReport(@RequestBody OrderCostReportVo orderCostReportVo){

            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iOrderCostReportService.auditOrderCostReport(orderCostReportVo.getBusiCode(),orderCostReportVo.getBusiId(),orderCostReportVo.getDesc(),
                    orderCostReportVo.getChooseResult(),orderCostReportVo.getLoadMileages(),orderCostReportVo.getCapacityLoadMileages(),accessToken);
            return  ResponseResult.success("审核成功");
    }

    /**
     * 聂杰伟
     * 2022-3-10
     * 接口说明；根据订单号查询上报费用详情
     * 上报费用页面  查看详情
     *
     * @param orderId
     * @return
     */
    @GetMapping("/getOrderCostDetailReportByOrderId")
    public ResponseResult getOrderCostDetailReportByOrderId(@RequestParam(value = "orderId") String orderId) {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            OrderCostReportDto orderCostReportDto = iOrderCostReportService.getOrderCostDetailReportByOrderId(orderId, accessToken);
            return ResponseResult.success(orderCostReportDto);
    }


    /**
     * 接口说明 费用上报审核 审核前调用 改为审核中
     * 聂杰伟
     * 2022-3-10-15：13
     * @param orderCostReportVo
     * @return
     */
    @PostMapping("/examineOrderCostReport")
    public  ResponseResult examineOrderCostReport(@RequestBody OrderCostReportVo orderCostReportVo){
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            iOrderCostReportService.examineOrderCostReport(orderCostReportVo,accessToken);
            return  ResponseResult.success("审核成功");
    }

    /**
     *  费用管理明细  列表
     * @param paymentWay //付款方式 1:油卡 2:现金 3:全部
     * @param tableTyp //数据类型 1油费 2路桥费
     * @param orderId //订单号
     * @param plateNumber  //车牌号
     * @param carDriverMan //司机名称
     * @param cardNo //卡号
     * @param startTime //靠台时间开始
     * @param endTime //靠台时间结束
     * @param pageNum 分页
     * @param pageSize
     * @return
     */
    @GetMapping("/queryOrderCostDetailReports")
    public ResponseResult queryOrderCostDetailReports(@RequestParam(value = "paymentWay") Integer paymentWay ,
                                                      @RequestParam(value = "typeId") Long typeId,
                                                      @RequestParam(value = "orderId") Long orderId,
                                                      @RequestParam(value = "plateNumber") String plateNumber,
                                                      @RequestParam(value = "carDriverMan")String carDriverMan,
                                                      @RequestParam(value = "cardNo") String cardNo,
                                                      @RequestParam(value = "startTime",required = false) String startTime,
                                                      @RequestParam(value = "endTime",required = false) String endTime,
                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        PageInfo<OrderMainReportDto> page = iOrderCostReportService.queryOrderCostDetailReports(paymentWay,
                typeId, orderId, plateNumber, carDriverMan, cardNo, startTime, endTime, pageNum, pageSize, accessToken);
        return  ResponseResult.success(page);
    }


    /**
     *  明细导出
     * @param paymentWay
     * @param typeId
     * @param orderId
     * @param plateNumber
     * @param carDriverMan
     * @param cardNo
     * @param startTime
     * @param endTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/exportParticulars")
    public ResponseResult exportParticulars(@RequestParam(value = "paymentWay") Integer paymentWay ,
                                                      @RequestParam(value = "typeId") Long typeId,
                                                      @RequestParam(value = "orderId") Long orderId,
                                                      @RequestParam(value = "plateNumber") String plateNumber,
                                                      @RequestParam(value = "carDriverMan")String carDriverMan,
                                                      @RequestParam(value = "cardNo") String cardNo,
                                                      @RequestParam(value = "startTime",required = false) String startTime,
                                                      @RequestParam(value = "endTime",required = false) String endTime,
                                                      @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            com.youming.youche.commons.domain.ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("上报费用明细导出");
            record.setBussinessType(2);
            record.setState(1);
            record= importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderCostReportService.exportParticulars(paymentWay,
                    typeId,orderId,plateNumber,carDriverMan,cardNo,startTime,endTime,pageNum,pageSize,accessToken,record);
            return  ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        }catch (Exception e){
            LOGGER.error("导出失败上报明细列表异常" + e);
            return ResponseResult.failure("导出失败上报明细列表异常");
        }
    }

    /**
     * 司机小程序
     * niejiewei
     * 50035
     * 是否可以上报
     * @param orderInfo
     * @return
     */
    @GetMapping("/isDoSave")
    public   ResponseResult isDoSave(OrderInfo orderInfo){
        return  ResponseResult.success( iOrderCostReportService.isDoSave(orderInfo));
    }

    /**
     * niejeiwei
     * 司机小程序
     * 费用审核
     * 50031
     * @param vo
     * @param
     */
    @PostMapping("/examineOrderCostReportByWx")
    public  ResponseResult examineOrderCostReportByWx(@RequestBody  OrderCostReportVo vo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderCostReportService.examineOrderCostReportByWx(vo,accessToken);
        return  ResponseResult.success();
    }
}
