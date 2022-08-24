package com.youming.youche.finance.business.controller.order;


import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IManualPaymentService;
import com.youming.youche.finance.api.IOilEntityService;
import com.youming.youche.finance.api.order.IOrderMainReportService;
import com.youming.youche.finance.domain.order.OrderMainReport;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.other.IOperationOilService;
import com.youming.youche.order.util.OrderUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 * 聂杰伟
 * 订单成本上报主表
 * @author Terry
 * @since 2022-03-08
 */
@RestController
@RequestMapping("order/main/report")
public class OrderMainReportController extends BaseController<OrderMainReport, IOrderMainReportService> {


    @DubboReference(version = "1.0.0")
    IOrderMainReportService iOrderMainReportService;

    @Override
    public IOrderMainReportService getService() {
        return iOrderMainReportService;
    }


    @DubboReference(version = "1.0.0")
    IManualPaymentService iManualPaymentService;

    @DubboReference(version = "1.0.0")
    IOperationOilService iOperationOilService;
    @DubboReference(version = "1.0.0")
    IOilCardManagementService iOilCardManagementService;
    @Resource
    IOilEntityService iOilEntityService;



    /**
     * 接口说明 根据订单号查询订单信息
     * 聂杰伟
     * 2022-3-9
     * @param orderId
     * @return
     */
    @GetMapping("getOrderCostDetailReportByOrderId")
    public  ResponseResult getOrderCostDetailReportByOrderId(@RequestParam(value = "orderId" ,required = true) String orderId){
        try {
            if (StringUtils.isBlank(orderId)){
                throw  new BusinessException("订单号不能为空");

            }
            return ResponseResult.success();
        }catch (Exception e){
            return  ResponseResult.failure("查询错误");
        }
    }

    /**
     * 油卡充值 查询列表
     */
    @GetMapping("getOilEntitys")
    public ResponseResult getOilEntitys(OilEntityVo oilEntityVo, Integer pageSize, Integer pageNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<OilEntityInfoDto> list = iManualPaymentService.getOilEntitys(oilEntityVo,pageNum,pageSize,accessToken);
        return ResponseResult.success(list);
    }

    /**
     * 油卡充值 核销
     */
    @PostMapping("batchVerificatOrder")
    public ResponseResult batchVerificatOrder(String orderIds) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (orderIds == null || orderIds.trim().length() < 0) {
            throw new BusinessException("参数orderIds不能为空");
        }
        iManualPaymentService.batchVerificatOrder(orderIds,accessToken);
        return ResponseResult.success();
    }

    /**
     * 解冻
     * @param userId
     * @param state
     * @param remark
     * @param sourceTenantId
     * @param userType
     * @return
     */
    @PostMapping("doAccountIn")
    public ResponseResult doAccountIn(Long userId, Integer state, String remark, Long sourceTenantId, Long userType){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iManualPaymentService.doAccountIn(userId, state, remark, sourceTenantId, userType,accessToken);
        return ResponseResult.success();
    }

    /**
     * 充值油费
     */
    @PostMapping("doSaveOilFee")
    public ResponseResult doSaveOilFee(Long userId, String rechargeAmount, int isNeedBill, Integer oilAccountType,
                               int userType, Integer oilBillType){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (StringUtils.isBlank(rechargeAmount)){
            throw  new  BusinessException("请输入充值金额！");
        }
        Long rechargeAmountDouble = OrderUtil.objToLongMul100(rechargeAmount);
        iOperationOilService.rechargeOrderAccountOil(userId,rechargeAmountDouble, isNeedBill,
                oilAccountType,userType,oilBillType,accessToken);
        return ResponseResult.success("充值成功");
    }

    /**
     * 修改卡号
     */
    @PostMapping("doCheckOilCardNum")
    public String doCheckOilCardNum(String oilCarNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return iOilCardManagementService.doCheckOilCardNum(oilCarNum,accessToken) + "";
    }

    /**
     * 修改卡号
     */
    @PostMapping("updateOilCarNum")
    public ResponseResult updateOilCarNum(String orderId,String oilCarNum) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iManualPaymentService.updateOilCarNum(orderId, oilCarNum,accessToken);
        return ResponseResult.success();
    }

    /**
     * 撤销
     */
    @PostMapping("revokeOilCharge")
    public ResponseResult revokeOilCharge(String id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (StringUtils.isBlank(id)) {
            throw new BusinessException("参数orderIds不能为空");
        }
        iOilEntityService.revokeOilCharge(id,accessToken);
        return ResponseResult.success();
    }
}
