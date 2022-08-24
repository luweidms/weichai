package com.youming.youche.finance.business.controller.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.order.IOrderInfoThreeService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.order.OrderDiffInfo;
import com.youming.youche.finance.domain.order.OrderInfo;
import com.youming.youche.finance.dto.LineInfoDto;
import com.youming.youche.finance.dto.order.OrderDiffInfoDto;
import com.youming.youche.finance.dto.order.OrderInfoDto;
import com.youming.youche.finance.dto.order.VehicleOrderDto;
import com.youming.youche.finance.vo.QueryPayOutVo;
import com.youming.youche.finance.vo.order.OrderDiffFeils;
import com.youming.youche.finance.vo.order.OrderDiffVo;
import com.youming.youche.finance.vo.order.OrderInfoVo;
import com.youming.youche.finance.vo.order.VehicleOrderVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 应收订单
 *
 * @author hzx
 * @date 2022/2/8 9:39
 */
@RestController
@RequestMapping("order/info")
public class OrderInfoController extends BaseController<OrderInfo, IOrderInfoThreeService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInfoController.class);

    @Override
    public IOrderInfoThreeService getService() {
        return orderInfoThreeService;
    }

    @DubboReference(version = "1.0.0")
    IOrderInfoThreeService orderInfoThreeService;

    @DubboReference(version = "1.0.0",async = true)
    IOrderInfoThreeService iOrderInfoThreeService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    /**
     * 列表
     */
    @GetMapping("queryReceviceManageOrder")
    public ResponseResult queryReceviceManageOrder(OrderInfoVo orderInfoVo,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        Page<OrderInfoDto> objectPage = new Page<>(pageNum, pageSize);
        PageInfo<OrderInfoDto> orderInfoDtoPage = orderInfoThreeService.queryReceviceManageOrder(orderInfoVo, accessToken, pageNum, pageSize);
        return ResponseResult.success(orderInfoDtoPage);

    }

    /**
     * 对账调整（列表）
     */
    @PostMapping("getOrderDiffList")
    public ResponseResult getOrderDiffList(@RequestParam("orderId") String orderId) {

        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException("查询订单号为空！");
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OrderDiffInfoDto> orderDiffList = orderInfoThreeService.getOrderDiffList(orderId, accessToken);
        return ResponseResult.success(orderDiffList);

    }

    /**
     * 对账调整（保存订单差异）
     * 并且填冲确认收入金额
     */
    @PostMapping("saveOrderDiff")
    public ResponseResult saveOrderDiff(@RequestBody OrderDiffVo orderDiffVo) {

        if (StringUtils.isBlank(orderDiffVo.getOrderId() + "")) {
            throw new BusinessException("订单号为空！");
        }
        if (orderDiffVo.getListData().size() == 0) {
            throw new BusinessException("未传入差异信息");
        }

        List<OrderDiffInfo> orderDiffInfos = new ArrayList<OrderDiffInfo>();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (OrderDiffFeils listDatum : orderDiffVo.getListData()) {
            OrderDiffInfo info = new OrderDiffInfo();
            if (StringUtils.isNotBlank(listDatum.getDiffFeeDouble()) && !"null".equals(listDatum.getDiffFeeDouble())) {
                info.setDiffFee(Math.round(Double.parseDouble(listDatum.getDiffFeeDouble()) * 100));
            } else {
                info.setDiffFee(0L);
            }
            if (StringUtils.isNotBlank(listDatum.getCreatorId()) && !"null".equals(listDatum.getCreatorId())) {
                info.setCreatorId(Long.parseLong(listDatum.getCreatorId()));
            }
            if (StringUtils.isNotBlank(listDatum.getOperId() + "") && !"null".equals(listDatum.getOperId())) {
                info.setOperId(listDatum.getOperId());
            }
            if (StringUtils.isNotBlank(listDatum.getCreateDate()) && !"null".equals(listDatum.getCreateDate())) {
                info.setCreateTime(LocalDateTime.parse(listDatum.getCreateDate(), df));
            }
            info.setDiffDesc(listDatum.getDiffDesc());
            if (StringUtils.isNotBlank(listDatum.getOrderId() + "") && !"null".equals(listDatum.getOrderId())) {
                info.setOrderId(listDatum.getOrderId());
            }
            if (StringUtils.isNotBlank(listDatum.getDiffType() + "") && !"null".equals(listDatum.getDiffType())) {
                info.setDiffType(listDatum.getDiffType());
            } else {
                throw new BusinessException("未传入差异类型");
            }
            info.setCreatorName("creatorName");
            if (StringUtils.isNotBlank(listDatum.getDiffId() + "") && !"null".equals(listDatum.getDiffId())) {
                info.setId(listDatum.getDiffId());
            }
            orderDiffInfos.add(info);
        }

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        orderInfoThreeService.saveOrderDiff(orderDiffVo.getOrderId(), orderDiffInfos, accessToken);
        return ResponseResult.success();

    }

    /**
     * 修改对账名称
     */
    @PostMapping("batchUpdateCheckName")
    public ResponseResult batchUpdateCheckName(String idListStr, String checkName) {

        if (StringUtils.isBlank(idListStr)) {
            throw new BusinessException("未选中任何记录");
        }
        if (StringUtils.isBlank(checkName)) {
            throw new BusinessException("请填写对账名称");
        }
        List<Long> idList = CommonUtil.convertLongIdList(idListStr);

        int count = orderInfoThreeService.batchUpdateCheckName(idList, checkName);

        return ResponseResult.success(count);
    }

    /**
     * 订单导出/账单详情导出
     */
    @GetMapping("export")
    public ResponseResult export(OrderInfoVo orderInfoVo, @RequestParam("isOrder") Boolean isOrder) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("应收管理订单信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoThreeService.export(orderInfoVo, isOrder, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出应收管理订单信息列表异常" + e);
            return ResponseResult.failure("导出成功应收管理订单信息异常");
        }
    }

    /**
     * 招商车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/merchantsVehicleOrder")
    public ResponseResult merchantsVehicleOrder(VehicleOrderVo vehicleOrderVo,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleOrderDto> vehicleOrderDtoPage = orderInfoThreeService.merchantsVehicleOrder(vehicleOrderVo, pageNum, pageSize,accessToken);
        return ResponseResult.success(vehicleOrderDtoPage);
    }

    /**
     * 招商车订单明细导出
     * @param vehicleOrderVo
     * @return
     */
    @GetMapping("/merchantsExport")
    public ResponseResult merchantsExport(VehicleOrderVo vehicleOrderVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName(" 招商车订单明细导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoThreeService.merchantsExport(vehicleOrderVo,record,accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败招商车订单明细列表异常" + e);
            return ResponseResult.failure("导出成功招商车订单明细异常");
        }
    }

    /**
     * 外调车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/transferVehicleOrder")
    public ResponseResult transferVehicleOrder(VehicleOrderVo vehicleOrderVo,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleOrderDto> vehicleOrderDtoPage = orderInfoThreeService.transferVehicleOrder(vehicleOrderVo, pageNum, pageSize,accessToken);
        return ResponseResult.success(vehicleOrderDtoPage);
    }

    /**
     * 外调车订单明细导出
     * @param vehicleOrderVo
     * @return
     */
    @GetMapping("/transferExport")
    public ResponseResult transferExport(VehicleOrderVo vehicleOrderVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName(" 外调车订单明细导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoThreeService.transferExport(vehicleOrderVo,record,accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败外调车订单明细列表异常" + e);
            return ResponseResult.failure("导出成功外调车订单明细异常");
        }
    }

    /**
     * 自有车订单明细
     * @param vehicleOrderVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/ownVehicleOrder")
    public ResponseResult ownVehicleOrder(VehicleOrderVo vehicleOrderVo,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleOrderDto> vehicleOrderDtoPage = orderInfoThreeService.ownVehicleOrder(vehicleOrderVo, pageNum, pageSize,accessToken);
        return ResponseResult.success(vehicleOrderDtoPage);
    }

    /**
     * 自有车订单明细导出
     * @param vehicleOrderVo
     * @return
     */
    @GetMapping("/ownVehicleExport")
    public ResponseResult ownVehicleExport(VehicleOrderVo vehicleOrderVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("自有车订单明细导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderInfoThreeService.ownExport(vehicleOrderVo,record,accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败自有车订单明细列表异常" + e);
            return ResponseResult.failure("导出成功自有车订单明细异常");
        }
    }

    /**
     * 线路报表
     * @param sourceName
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/lineStatements")
    public ResponseResult lineStatements(String sourceName, String beginTime, String endTime,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer page,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<LineInfoDto> lineInfoDtoPage = orderInfoThreeService.lineStatements(sourceName, beginTime, endTime, page, pageSize,accessToken);
        return ResponseResult.success(lineInfoDtoPage);
    }

    /**
     * 线路报表导出
     */
    @GetMapping("/lineStatementsExport")
    public ResponseResult lineStatementsExport(String sourceName, String beginTime, String endTime) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("线路报表导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderInfoThreeService.lineStatementsExport(sourceName,beginTime,endTime,accessToken,record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败线路报表异常" + e);
            return ResponseResult.failure("导出成功线路报表异常");
        }
    }

}
