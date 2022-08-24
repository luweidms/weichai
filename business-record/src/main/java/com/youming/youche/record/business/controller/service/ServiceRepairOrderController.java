package com.youming.youche.record.business.controller.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Maps;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.business.controller.user.UserController;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.service.ServiceRepairOrder;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDetailVerDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderDto;
import com.youming.youche.record.dto.service.ServiceRepairOrderUpdateEchoDto;
import com.youming.youche.record.vo.service.ServiceRepairOrderIUVo;
import com.youming.youche.record.vo.service.ServiceRepairOrderVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * hzx
 * 车辆维保
 *
 * @date 2022/1/8 10:51
 */
@RestController
@RequestMapping("service/repair/order")
public class ServiceRepairOrderController extends BaseController<ServiceRepairOrder, IServiceRepairOrderService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @DubboReference(version = "1.0.0")
    IServiceRepairOrderService iServiceRepairOrderService;


    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IServiceRepairOrderService getService() {
        return iServiceRepairOrderService;
    }

    /**
     * @param serviceRepairOrderVo 车辆维保查询
     * @param pageNum              页码
     * @param pageSize             页面展示条数
     * @return 车辆维保信息
     */
    @GetMapping("doQueryOrderList")
    public ResponseResult doQueryOrderList(ServiceRepairOrderVo serviceRepairOrderVo,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ServiceRepairOrderDto> objectPage = new Page<>(pageNum, pageSize);
        Page<ServiceRepairOrderDto> serviceRepairOrderDtoPage =
                iServiceRepairOrderService.doQueryOrderList(serviceRepairOrderVo, objectPage, accessToken);
        return ResponseResult.success(serviceRepairOrderDtoPage);

    }

    /**
     * @param flowId 维保信息主键id
     * @return 车辆维保详情
     */
    @PostMapping("getRepairOrder")
    public ResponseResult getRepairOrder(@RequestParam("flowId") Long flowId) {

        ServiceRepairOrderDetailDto repairOrderDetail = iServiceRepairOrderService.getRepairOrderDetail(flowId);
        return ResponseResult.success(repairOrderDetail);

    }

    /**
     * @param flowId 维保信息主键id
     * @return 车辆维保详情
     */
    @PostMapping("getRepairOrderVer")
    public ResponseResult getRepairOrderVer(@RequestParam("flowId") Long flowId) {

        ServiceRepairOrderDetailVerDto repairOrderDetailVer = iServiceRepairOrderService.getRepairOrderDetailVer(flowId);
        return ResponseResult.success(repairOrderDetailVer);

    }

    /**
     * 82018
     *
     * @param flowId 主键id
     * @return 车辆维保修改回显数据
     */
    @PostMapping("getServiceRepairOrder")
    public ResponseResult getServiceRepairOrder(@RequestParam("flowId") Long flowId) {

        ServiceRepairOrderUpdateEchoDto serviceRepairOrder = iServiceRepairOrderService.getServiceRepairOrder(flowId);
        return ResponseResult.success(serviceRepairOrder);

    }

    /**
     * 新增修改维修保养工单
     *
     * @param serviceRepairOrderIUVo 维修保养信息
     * @return
     */
    @PostMapping("doSaveOrUpdateServiceRepairOrder")
    public ResponseResult doSaveOrUpdateServiceRepairOrder(ServiceRepairOrderIUVo serviceRepairOrderIUVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iServiceRepairOrderService.doSaveOrUpdateServiceRepairOrder(serviceRepairOrderIUVo, accessToken);
        return ResponseResult.success();

    }

    /**
     * 取消维修保养申请 修改状态为废弃
     *
     * @param flowId 维修保养主键id
     * @return
     */
    @PostMapping("doCancel")
    public ResponseResult doCancel(@RequestParam("flowId") long flowId) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iServiceRepairOrderService.doCancel(flowId, accessToken);
        return ResponseResult.success();

    }

    /**
     * 小程序取消维保订单  82017
     */
    @PostMapping("doCancelWX")
    public ResponseResult doCancelWX(@RequestParam("flowId") long flowId) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iServiceRepairOrderService.doCancelWX(flowId, accessToken);
        return ResponseResult.success();

    }

    /**
     * 总费用(废弃)
     */
    @PostMapping("getTotalFee")
    public ResponseResult getTotalFee(ServiceRepairOrderVo serviceRepairOrderVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            HashMap<String, Object> map = Maps.newHashMap();
            map.put("totalFee", iServiceRepairOrderService.getTotalFee(serviceRepairOrderVo, accessToken));
            return ResponseResult.success(map);
        } catch (Exception e) {
            LOGGER.error("操作异常" + e);
            return ResponseResult.failure("操作异常");
        }
    }

    /**
     * 维保类型（废弃）
     */
    @PostMapping("getStaticDataOption")
    public ResponseResult getStaticDataOption(String codeType) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            String[] codeTypes = codeType.split(",");
            List<List> l = new ArrayList();
            for (int i = 0; i < codeTypes.length; i++) {
                List<SysStaticData> data = iServiceRepairOrderService.getStaticDataOption(codeTypes[i],accessToken);
                if (data != null && data.size() > 0) {
                    l.add(data);
                } else {
                    data = new ArrayList<>();
                    l.add(data);
                }
            }
            return ResponseResult.success(l);
        } catch (Exception e) {
            LOGGER.error("查询异常" + e);
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 修改上单保养里程数
     *
     * @param flowId
     * @param lastOrderMileage 上单保养里程数
     */
    @PostMapping("doSaveLastOrderMileage")
    public ResponseResult doSaveLastOrderMileage(@RequestParam("flowId") Long flowId,
                                                 @RequestParam("lastOrderMileage") String lastOrderMileage) {

        if (flowId < 0) {
            throw new BusinessException("参数异常");
        }
        if (StringUtils.isBlank(lastOrderMileage) || !CommonUtil.isNumber(lastOrderMileage)) {
            throw new BusinessException("请输入正确的里程数");
        }
        iServiceRepairOrderService.doSaveLastOrderMileage(flowId, lastOrderMileage);
        return ResponseResult.success();

    }

    /**
     * 车辆维保信息导出
     *
     * @param serviceRepairOrderVo 车辆维保查询
     * @return
     */
    @GetMapping("export")
    public ResponseResult export(ServiceRepairOrderVo serviceRepairOrderVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆维保信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iServiceRepairOrderService.doQueryOrderListExport(serviceRepairOrderVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败车辆维保列表异常" + e);
            return ResponseResult.failure("导出成功车辆维保异常");
        }
    }

}
