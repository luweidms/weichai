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
 * @Title: AuditCommonController ??????????????????
 * @Package: com.youming.youche.system.business.controller.audit
 * @Description: TODO(????????????????????????????????????)
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
     * @Description: ????????????
     * @Author: luwei
     * @Date: 2022/8/24 15:44
     * @Param auditVo: ?????????????????????
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @PostMapping("surecvehicle")
    @GlobalTransactional(timeoutMills = 300000, rollbackFor = Exception.class)
    public ResponseResult sureVehicle(@RequestBody AuditVo auditVo) {
        if (auditVo == null) {
            return ResponseResult.failure("????????????");
        }
        if ((AuditConsts.RESULT.FAIL == auditVo.getChooseResult() || AuditConsts.RESULT.TO_AUDIT == auditVo.getChooseResult()) && StringUtils.isBlank(auditVo.getDesc())) {
            return ResponseResult.failure("??????????????????????????????????????????");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        AuditCallbackDto sure = iAuditSettingService.sure(auditVo, token, false);
        if (null != sure && sure.getIsAudit() && sure.getIsNext()) {
            auditCallbackVehicle(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        } else if (null != sure && sure.getIsAudit()) {
            //???????????????
            auditCallbackVehicle(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        }
        return ResponseResult.success("????????????");
    }

    /**
     * ????????????
     *
     * @return
     * @throws Exception
     */
    @PostMapping("sure")
    public ResponseResult sure(@RequestBody AuditVo auditVo) {
        if (auditVo == null) {
            return ResponseResult.failure("????????????");
        }
        if ((AuditConsts.RESULT.FAIL == auditVo.getChooseResult() || AuditConsts.RESULT.TO_AUDIT == auditVo.getChooseResult()) && StringUtils.isBlank(auditVo.getDesc())) {
            return ResponseResult.failure("??????????????????????????????????????????");
        }
        String token = Header.getAuthorization(request.getHeader("Authorization"));
        AuditCallbackDto sure = iAuditSettingService.sure(auditVo, token, false);
        if (null != sure && sure.getIsAudit() && sure.getIsNext()) {
            // auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
            auditingCallBack(sure.getBusiId(), sure.getCallback());
        } else if (null != sure && sure.getIsAudit()) {
            //???????????????
            auditCallback(sure.getBusiId(), sure.getResult(), sure.getDesc(), sure.getParamsMap(), sure.getCallback(), sure.getToken());
        }
        return ResponseResult.success("????????????");
    }

    public void auditCallbackVehicle(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) {
        if (AuditConsts.RESULT.SUCCESS == result) {
            if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //??????????????????
                iVehicleDataInfoService.sucess(busiId, desc, paramsMap, token);
            }
        } else if (AuditConsts.RESULT.FAIL == result) {
            if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //??????????????????
                iVehicleDataInfoService.fail(busiId, desc, paramsMap, token);
            }
        }
    }

    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) {
        //Class cls = Class.forName(callback);
        //Method m=null;
        if (AuditConsts.RESULT.SUCCESS == result) {
            if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //????????????????????????
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //??????????????????
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //??????????????????
                iUserService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //??????????????????
                iVehicleDataInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //??????????????????
                auditCallBackService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //????????????
                iTrailerManagementService.sucess(busiId, desc, paramsMap, token);
            } /*else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //????????????????????????
                iPayFeeLimitVerService.sucess(busiId, desc, paramsMap, token);
            }*/ else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //????????????????????????
                iServiceProductService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                iServiceInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderInfoServiceImpl")) {
                //????????????????????????
                orderInfoService.orderUpdateAuditPass(busiId, desc, true, null, token);
            } else if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
                //??????????????????
                serviceRepairOrderService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderProblemInfoServiceImpl")) {
                //???????????????????????????????????????
                problemVerOptService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.IOaLoanThreeService")) {
                //  ??????????????????
                iOaLoanThreeService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
                //????????????????????????
                vehicleExpenseDetailedService.doPublicVehicleExpense(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE2, desc, token);
            } else if (callback.equals("com.youming.youche.finance.api.order.IOrderCostReportService")) {
                // ?????????????????? ??????
                iOrderCostReportService.sucess(busiId, desc, paramsMap, token);
            }else if(callback.equals("com.youming.youche.order.provider.service.order.OrderAgingAppealInfoServiceImpl")){
                //????????????????????????
                orderAgingAppealInfoService.sucess(busiId,desc,paramsMap,token);
                }
            else if (callback.equals("com.youming.youche.finance.api.IClaimExpenseInfoService")) {
                //?????? ????????????????????????
                iClaimExpenseInfoService.sucess(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.orderPriceCheck")) {
                //????????????
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
                // ??????????????????
                iUserRepairInfoService.sucess(busiId, desc, paramsMap, token);
            }
            //	m = cls.getDeclaredMethod("sucess",new Class[]{Long.class,String.class,Map.class});
        } else if (AuditConsts.RESULT.FAIL == result) {
            //	m = cls.getDeclaredMethod("fail",new Class[]{Long.class,String.class,Map.class});
            if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerInfoService")) {
                //????????????????????????
                cmCustomerInfoService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.cm.ICmCustomerLineService")) {
                //??????????????????
                cmCustomerLineService.doPublicAuth(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc);
            } else if (callback.equals("com.youming.youche.record.api.user.IUserService")) {
                //??????????????????
                iUserService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.IVehicleDataInfoService")) {
                //??????????????????
                iVehicleDataInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.vehicle.VehicleAuditCallBackService")) {
                //??????????????????
                auditCallBackService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.record.api.trailer.ITrailerManagementService")) {
                //????????????
                iTrailerManagementService.fail(busiId, desc, paramsMap, token);
            } /*else if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
                //????????????????????????
                iPayFeeLimitVerService.fail(busiId, desc, paramsMap, token);
            }*/ else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceProductService")) {
                //????????????????????????
                iServiceProductService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.market.api.facilitator.IServiceInfoService")) {
                iServiceInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderInfoServiceImpl")) {
                //????????????????????????
                orderInfoService.orderUpdateAuditNoPass(busiId, desc, token, null);
            } else if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
                //??????????????????
                serviceRepairOrderService.fail(busiId, desc, paramsMap);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.ProblemVerOptServiceImpl")) {
                //??????????????????
                problemVerOptService.fail(busiId, desc, token, true);
            } else if (callback.equals("com.youming.youche.order.provider.service.order.OrderProblemInfoServiceImpl")) {
                //??????????????????
                problemVerOptService.fail(busiId, desc, token, false);
            } else if (callback.equals("com.youming.youche.finance.api.IOaLoanThreeService")) {
                // ??????????????????
                iOaLoanThreeService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
                //????????????????????????
                vehicleExpenseDetailedService.doPublicVehicleExpense(busiId, SysStaticDataEnum.AUTH_STATE.AUTH_STATE3, desc, token);
            } else if (callback.equals("com.youming.youche.finance.api.order.IOrderCostReportService")) {
                //????????????????????????
                iOrderCostReportService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.IClaimExpenseInfoService")) {
                //?????? ????????????????????????
                iClaimExpenseInfoService.fail(busiId, desc, paramsMap, token);
            } else if (callback.equals("com.youming.youche.finance.api.orderPriceCheck")) {
                //??????????????????
                orderInfoService.auditPriceFail(busiId, desc, token);
            } else if (callback.equals("com.youming.youche.market.api.user.IUserRepairInfoService")) {
                // ??????????????????
                iUserRepairInfoService.fail(busiId, desc, paramsMap, token);
            } else if(callback.equals("com.youming.youche.order.provider.service.order.OrderAgingAppealInfoServiceImpl")){
                //???????????????????????????
                orderAgingAppealInfoService.verifyFail(busiId,desc,token);
            }
        } else {
            throw new BusinessException("??????????????????????????????????????????1???2???????????????[" + result + "]");
        }
    }

    /***
     * @Description: ???????????????????????????
     * @Author: luwei
     * @Date: 2022/3/28 12:41 ??????
     * @return: void
     * @Version: 1.0
     **/
    public void auditingCallBack(Long busiId, String callback) {
        if (callback.equals("com.youming.youche.record.api.service.IServiceRepairOrderService")) {
            //????????????
            serviceRepairOrderService.auditingCallBack(busiId);
        }
        if (callback.equals("com.youming.youche.finance.api.service.IVehicleExpenseService")) {
            //???????????????????????????????????????
            vehicleExpenseDetailedService.doPublicVehicleExpenseState(busiId);
        }
        if (callback.equals("com.youming.youche.market.api.user.IUserRepairInfoService")) {
            // ??????????????????
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
