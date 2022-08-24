package com.youming.youche.system.business.controller.audit;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IClaimExpenseInfoService;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IVehicleExpenseDetailedService;
import com.youming.youche.finance.api.order.IOrderCostReportService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.order.api.order.IOrderAgingAppealInfoService;
import com.youming.youche.order.api.order.IOrderAgingInfoService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IProblemVerOptService;
import com.youming.youche.record.api.audit.IAuditCallBackService;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.api.service.IServiceRepairOrderService;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditSettingService;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.dto.AuditOutDto;
import com.youming.youche.system.vo.AuditVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @version:
 * @Title: AuditCommonController 审核公共接口
 * @Package: com.youming.youche.system.business.controller.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/19 17:39
 * @company:
 */
@RestController
@RequestMapping("/sys/auditCommon")
public class AuditCommonController extends BaseController {
    @Override
    public IBaseService getService() {
        return null;
    }


    @DubboReference(version = "1.0.0")
    IAuditSettingService iAuditSettingService;


    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService cmCustomerInfoService;

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService cmCustomerLineService;

    @DubboReference(version = "1.0.0")
    IUserService iUserService;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;

    @DubboReference(version = "1.0.0")
    IAuditCallBackService auditCallBackService;

    @DubboReference(version = "1.0.0")
    ITrailerManagementService iTrailerManagementService;

    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService;

    @DubboReference(version = "1.0.0")
    IServiceProductService iServiceProductService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;

//    @DubboReference(version = "1.0.0")
//    ISysOperLogService iSysOperLogService;

    @DubboReference(version = "1.0.0")
    IServiceRepairOrderService serviceRepairOrderService;

    @DubboReference(version = "1.0.0")
    IAuditOutService iAuditOutService;

    @DubboReference(version = "1.0.0")
    IProblemVerOptService problemVerOptService;

    @DubboReference(version = "1.0.0")
    IOaLoanThreeService iOaLoanThreeService;

    @DubboReference(version = "1.0.0")
    IVehicleExpenseDetailedService vehicleExpenseDetailedService;

    @DubboReference(version = "1.0.0")
    IOrderCostReportService iOrderCostReportService;

    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    IClaimExpenseInfoService iClaimExpenseInfoService;

    @DubboReference(version = "1.0.0")
    IOrderAgingAppealInfoService orderAgingAppealInfoService;


    @DubboReference(version = "1.0.0")
    IUserRepairInfoService iUserRepairInfoService;

    /***
     * @Description: 服务审核
     * @Author: luwei
     * @Date: 2022/8/24 15:44
     * @Param auditVo: 审核入参实体类
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @PostMapping("surecvehicle")
    @GlobalTransactional(timeoutMills = 300000, rollbackFor = Exception.class)
    public ResponseResult sureVehicle(@RequestBody AuditVo auditVo) {
        if (auditVo == null) {
            return ResponseResult.failure("参数错误");
        }
        if ((AuditConsts.RESULT.FAIL == auditVo.getChooseResult() || AuditConsts.RESULT.TO_AUDIT == auditVo.getChooseResult()) && StringUtils.isBlank(auditVo.getDesc())) {
            return ResponseResult.failure("审核不通过，审核原因需要填写");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        AuditCallbackDto sure = iAuditSettingService.sure(auditVo, token, false);
        if (null != sure && sure.getIsAudit() && sure.getIsNext()) {
            auditCallbackVehicle(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        } else if (null != sure && sure.getIsAudit()) {
            //走审核流程
            auditCallbackVehicle(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        }
        return ResponseResult.success("审核成功");
    }

    /**
     * 审核操作
     *
     * @return
     * @throws Exception
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody AuditVo auditVo) {
        if (auditVo == null) {
            return ResponseResult.failure("参数错误");
        }
        if ((AuditConsts.RESULT.FAIL == auditVo.getChooseResult() || AuditConsts.RESULT.TO_AUDIT == auditVo.getChooseResult()) && StringUtils.isBlank(auditVo.getDesc())) {
            return ResponseResult.failure("审核不通过，审核原因需要填写");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        AuditCallbackDto sure = iAuditSettingService.sure(auditVo, token, false);
        if (null != sure && sure.getIsAudit() && sure.getIsNext()) {
            // auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
            auditingCallBack(sure.getBusiId(), sure.getCallback());
        } else if (null != sure && sure.getIsAudit()) {
            //走审核流程
            auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        }
        return ResponseResult.success("审核成功");
    }

    public void auditCallbackVehicle(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) {
        if (AuditConsts.RESULT.SUCCESS == result) {
            if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核成功
                iVehicleDataInfoService.sucess(busiId, desc, paramsMap, token);
            }
        } else if (AuditConsts.RESULT.FAIL == result) {
            if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核失败
                iVehicleDataInfoService.fail(busiId, desc, paramsMap, token);
            }
        }
    }

    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) {
        //Class cls = Class.forName(callback);
        //Method m=null;
        if (AuditConsts.RESULT.SUCCESS == result) {
            if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //客户档案审核成功
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //客户线路审核
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //司机审核成功
                iUserService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核成功
                iVehicleDataInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //审核邀请车辆
                auditCallBackService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //挂车审核
                iTrailerManagementService.sucess(busiId, desc, paramsMap, token);
            } /*else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //资金风控审核成功
                iPayFeeLimitVerService.sucess(busiId, desc, paramsMap, token);
            }*/ else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //后服站点审核成功
                iServiceProductService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                iServiceInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderInfoServiceImpl")) {
                //修改订单审核成功
                orderInfoService.orderUpdateAuditPass(busiId, desc, true, null, token);
            } else if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
                //维保审核成功
                serviceRepairOrderService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderProblemInfoServiceImpl")) {
                //异常审核（多次审核走这里）
                problemVerOptService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.IOaLoanThreeService")) {
                //  借支审核成功
                iOaLoanThreeService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
                //车辆费用审核成功
                vehicleExpenseDetailedService.doPublicVehicleExpense(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc, token);
            } else if (callback.equals("com.youming.youche.finance.api.order.IOrderCostReportService")) {
                // 上报费用审核 成功
                iOrderCostReportService.sucess(busiId, desc, paramsMap, token);
            }else if(callback.equals("com.youming.youche.order.provider.service.order.OrderAgingAppealInfoServiceImpl")){
                //时效申诉审核成功
                orderAgingAppealInfoService.sucess(busiId,desc,paramsMap,token);
                }
            else if (callback.equals("com.youming.youche.finance.api.IClaimExpenseInfoService")) {
                //车管 司机报销审核成功
                iClaimExpenseInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.orderPriceCheck")) {
                //价格审核
                Object updateObj = paramsMap.get(AuditConsts.RuleMapKey.IS_UPDATE);
                Boolean isUpdate = false;
                try {
                    if (updateObj != null) {
                        isUpdate = Boolean.parseBoolean(updateObj.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                orderInfoService.auditPriceSuccess(busiId, isUpdate, desc, token);
            } else if (callback.equals("com.youming.youche.market.api.user.IUserRepairInfoService")) {
                // 维修工单审核
                iUserRepairInfoService.sucess(busiId, desc, paramsMap, token);
            }
            //	m = cls.getDeclaredMethod("sucess",new Class[]{Long.class,String.class,Map.class});
        } else if (AuditConsts.RESULT.FAIL == result) {
            //	m = cls.getDeclaredMethod("fail",new Class[]{Long.class,String.class,Map.class});
            if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //客户档案审核失败
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //客户线路审核
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //司机审核失败
                iUserService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //车辆审核失败
                iVehicleDataInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //审核邀请车辆
                auditCallBackService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //挂车审核
                iTrailerManagementService.fail(busiId, desc, paramsMap, token);
            } /*else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //资金风控审核失败
                iPayFeeLimitVerService.fail(busiId, desc, paramsMap, token);
            }*/ else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //后服站点审核失败
                iServiceProductService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                iServiceInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderInfoServiceImpl")) {
                //修改订单审核失败
                orderInfoService.orderUpdateAuditNoPass(busiId, desc, token, null);
            } else if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
                //维保审核失败
                serviceRepairOrderService.fail(busiId, desc, paramsMap);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.ProblemVerOptServiceImpl")) {
                //时效审核失败
                problemVerOptService.fail(busiId, desc, token, true);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderProblemInfoServiceImpl")) {
                //异常审核失败
                problemVerOptService.fail(busiId, desc, token, false);
            } else if (callback.equals("com.youming.youche.finance.api.IOaLoanThreeService")) {
                // 借支审核失败
                iOaLoanThreeService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
                //车辆费用审核失败
                vehicleExpenseDetailedService.doPublicVehicleExpense(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc, token);
            } else if (callback.equals("com.youming.youche.finance.api.order.IOrderCostReportService")) {
                //上报费用审核失败
                iOrderCostReportService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.IClaimExpenseInfoService")) {
                //车管 司机报销审核失败
                iClaimExpenseInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.orderPriceCheck")) {
                //订单价格审核
                orderInfoService.auditPriceFail(busiId, desc, token);
            } else if (callback.equals("com.youming.youche.market.api.user.IUserRepairInfoService")) {
                // 维修工单审核
                iUserRepairInfoService.fail(busiId, desc, paramsMap, token);
            } else if(callback.equals("com.youming.youche.order.provider.service.order.OrderAgingAppealInfoServiceImpl")){
                //时效申诉审核不通过
                orderAgingAppealInfoService.verifyFail(busiId,desc,token);
            }
        } else {
            throw new BusinessException("传入的回调方法的结果类型只能1，2，传入的是[" + result + "]");
        }
    }

    /***
     * @Description: 下一个节点审核处理
     * @Author: luwei
     * @Date: 2022/3/28 12:41 上午
     * @return: void
     * @Version: 1.0
     **/
    public void auditingCallBack(Long busiId, String callback) {
        if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
            //维保审核
            serviceRepairOrderService.auditingCallBack(busiId);
        }
        if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
            //车辆费用审核，修改为审核中
            vehicleExpenseDetailedService.doPublicVehicleExpenseState(busiId);
        }
        if (callback.equals("com.youming.youche.market.api.user.IUserRepairInfoService")) {
            // 维修工单审核
            iUserRepairInfoService.auditingCallBack(busiId);
        }

    }

    @GetMapping("getInstId")
    public ResponseResult getInstId(String busiCode, Long busiId) throws Exception {
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        Long instId = iAuditSettingService.getInstId(busiCode, busiId, token);
        return ResponseResult.success(instId);
    }


    @GetMapping("/doIAuditOut")
    public ResponseResult doIAuditOut(String flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo user = loginUtils.get(accessToken);
        String[] flowIdsString = flowId.split(",");
        List<Long> flowIds = new ArrayList();
        for (String string : flowIdsString) {
            flowIds.add(Long.valueOf(string));
        }
        Map<Long, Map<String, Object>> longMapMap = iAuditOutService.queryAuditRealTimeOperation(user.getId(), AuditConsts.AUDIT_CODE.PAY_CASH_CODE, flowIds, user.getTenantId());
        List<AuditOutDto> list = new ArrayList<>();
        for (Long key : longMapMap.keySet()) {
            Long keyLong = Long.valueOf(key.toString());
            Map<String, Object> value = longMapMap.get(keyLong);
            Object isFinallyNode = value.get("isFinallyNode");
            Object isAuditJurisdiction = value.get("isAuditJurisdiction");
            Object auditUserName = value.get("auditUserName");
            AuditOutDto auditOutDto = new AuditOutDto();
            auditOutDto.setIsFinallyNode((Boolean) isFinallyNode);
            auditOutDto.setIsAuditJurisdiction((Boolean) isAuditJurisdiction);
            auditOutDto.setAuditUserName((String) auditUserName);
            list.add(auditOutDto);
        }
        return ResponseResult.success(list);
    }
}
