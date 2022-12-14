package com.youming.youche.order.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.annotation.Dict;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.OaLoanOutDto;
import com.youming.youche.order.dto.OrderAccountBalanceDto;
import com.youming.youche.order.dto.OrderAccountsDto;
import com.youming.youche.order.dto.OrderLimitOutDto;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;
import com.youming.youche.order.vo.OrderAccountOutVo;
import com.youming.youche.order.vo.UserAccountOutVo;
import com.youming.youche.order.vo.UserAccountVo;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;


/**
 * <p>
 * ??????????????? ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-18
 */
@RestController
@RequestMapping("order/account")
public class OrderAccountController extends BaseController<OrderAccount, IOrderAccountService> {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrderAccountController.class);
    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @DubboReference(version = "1.0.0")
    IOilRechargeAccountService oilRechargeAccountService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService tenantUserRelService;

    @DubboReference(version = "1.0.0")
    IOverdueReceivableService overdueReceivableService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    LoginUtils loginUtils;

    @Override
    public IOrderAccountService getService() {
        return orderAccountService;
    }



    /***
     * @Description: ?????????????????????
     * @Author: luwei
     * @Date: 2022/4/9 5:41 ??????

     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("/getOilRechargeAccount")
    public ResponseResult getOilRechargeAccount(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(loginInfo == null || loginInfo.getUserInfoId() == null){
            throw new BusinessException("????????????????????????????????????");
        }
        return ResponseResult.success(oilRechargeAccountService.getOilRechargeAccount(loginInfo.getUserInfoId(),loginInfo.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER,"",loginInfo));
    }

    /**
     * ??????????????????
     * @param userAccountOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/doAccountQuery")
    public ResponseResult doAccountQuery(UserAccountOutVo userAccountOutVo,
                                         @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<UserAccountVo> userAccountVoPage = orderAccountService.doAccountQuery(userAccountOutVo, pageNum, pageSize,accessToken);
        return ResponseResult.success(userAccountVoPage);
    }

    /**
     * ????????????????????????
     * @param userAccountOutVo
     * @return
     */
    @GetMapping("/doAccountQuerySum")
    public ResponseResult doAccountQuerySum(UserAccountOutVo userAccountOutVo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(orderAccountService.doAccountQuerySum(userAccountOutVo,accessToken));
    }

    /**
     * ????????????????????????
     * @return
     */
    @GetMapping("/carUserType")
    public ResponseResult carUserType(long userId, long tenantId){
        Integer carUserType = tenantUserRelService.getCarUserType(userId, tenantId);
        return ResponseResult.success(carUserType == null ? 3 : carUserType);
    }

    /**
     * ???????????????????????????[30050]
     * @return
     */
    @GetMapping("/getCustomOil")
    public ResponseResult getDriverOil(Long carDriverId,Integer userType){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAccountBalanceDto driverOil = orderAccountService.getDriverOil(carDriverId, null, accessToken, userType);
        return ResponseResult.success(driverOil);
    }
    /**
     * ??????????????????
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(@RequestBody UserAccountOutVo userAccountOutVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO ??????token??????????????????
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("??????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            orderAccountService.OilCardList1(accessToken, record, userAccountOutVo);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("??????????????????????????????" + e);
            return ResponseResult.failure("????????????????????????????????????");
        }
    }

    /**
     * ??????????????????
     */
    @PostMapping("saveTurnCash")
    public ResponseResult saveTurnCash(Long userId, String turnMonth, String turnType, String turnOilType,
                                       String turnEntityOilCard, Long turnBalance, Long turnDiscountDouble,
                                       Integer userType) {
        turnMonth = turnMonth.replace("-", "");
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String s = orderAccountService.saveOilAndEtcTurnCashNew(userId, turnMonth, turnType, turnOilType,
                turnEntityOilCard, turnBalance, turnDiscountDouble, userType, accessToken);
        return ResponseResult.success(s);
    }

    /***
     *
     * ????????????:21200
     * @Description: ????????????-????????????(?????????)
     * ???????????????
     * @param
     *
     */
    @GetMapping("getAccountDetailsR")
    public ResponseResult getAccountDetailsR(String name,String accState,Integer userType, Integer pageSize, Integer pageNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<ReceivableOverdueBalanceDto> orderAccountOutDto = orderAccountService.getAccountDetailsR(name,accState,userType,pageSize,pageNum,accessToken);
        return ResponseResult.success(orderAccountOutDto);
    }
    /***
     *
     * ????????????:21002
     * @Description: ????????????-??????
     * ???????????????
     *
     * ???????????????
     *	depositBalance	??????	number
     *	totalBalance	?????????	number
     *	totalDebtAmount	??????	number
     *	totalEtcBalance	ETC??????	number
     *	totalMarginBalance	????????????	number
     *	totalOilBalance	?????????	number
     *	totalRepairFund	????????????	number
     *
     */
    @GetMapping("getAccountSum")
    public ResponseResult getAccountSum(){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAccountOutVo orderAccountOutDto = orderAccountService.getAccountSum(accessToken);
        return ResponseResult.success(orderAccountOutDto);
    }

    /***
     * @Description: ???????????????????????????
     * @Author: luwei
     * @Date: 2022/7/10 21:45

     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @PostMapping("confirmRefund")
    public ResponseResult confirmRefund(String flowId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OverdueReceivable overdueReceivable = overdueReceivableService.getById(flowId);
        if(overdueReceivable == null || overdueReceivable.getId() == null){
            throw new BusinessException("?????????????????????????????????????????????????????????");
        }
        overdueReceivable.setPayConfirm(3);
        overdueReceivableService.saveOrUpdate(overdueReceivable);
        String msg = "[" + loginInfo.getName() + "]" + "????????????";
        saveSysOperLog(SysOperLogConst.BusiCode.DueOverdue, SysOperLogConst.OperType.Audit, msg, accessToken, Long.valueOf(flowId));
        return ResponseResult.success();
    }
    /***
     *
     * ????????????:21003
     * @Description: ????????????-????????????
     * ???????????????
     * @param
     *
     */
    @GetMapping("getAccountDetails")
    public ResponseResult getAccountDetails(Long userId){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        OrderAccountsDto orderAccountOutDto = orderAccountService.getAccountDetails(userId,accessToken);
        return ResponseResult.success(orderAccountOutDto);
    }
    /***
     *
     * ????????????:21006
     * @Description: ????????????-????????????
     * ???????????????
     * @param userId ??????id
     *
     * ???????????????
     *
     *  userId	??????id	string
     *	items	??????	array<object>
     *  createDate	??????????????????	string
     *	desRegionName	?????????	string
     *
     *	fianlSts	????????????	string	0??????1??????2??????
     *	finalPlanDate	????????????	number
     *	noPayFinal	????????????(??????).	number
     *	orderFinal	??????	number
     *	orderId	??????Id	string
     *	orderPay	?????????	number
     *	paidFinalPay	????????????(??????).	number
     *	plateNumber	?????????	string
     *	sourceRegionName	?????????	string
     *	totalFee	??????	number
     *
     *
     */
    @GetMapping("getOaLoanList")
    public ResponseResult getOaLoanList(Long userId, String queryMonth, String loanSubjectList, String stateList, Long orderId,
                                        @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OaLoanOutDto> oaloanAndUserDateInfoDto = orderAccountService.getOaLoanList(userId, queryMonth, loanSubjectList, stateList, orderId, pageSize, pageNum, accessToken);
        return ResponseResult.success(oaloanAndUserDateInfoDto);
    }
    /***
     *
     * ????????????:21008
     * @Description: ????????????-????????????-????????????
     * ???????????????
     * @param
     *
     */
    @GetMapping("getAccountDetailsPledge")
    public ResponseResult getAccountDetailsPledge(Long userId, String tenantId,Integer userType,Integer pageSize,Integer pageNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderLimitOutDto> orderLimitPage = orderAccountService.getAccountDetailsPledge(userId,tenantId,userType,pageSize,pageNum,accessToken);
        return ResponseResult.success(orderLimitPage);
    }

    /**22023
     *APP-??????????????????
     * @param tenantId
     * @param userId
     * @return
     */
    @Dict
    @GetMapping("queryLoanDetail")
    public ResponseResult queryLoanDetail(@RequestParam("tenantId") Long tenantId,
                                          @RequestParam("userId") Long userId,
                                          @RequestParam("settleMonth")  String settleMonth,
                                          @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize){
        return ResponseResult.success(
                orderAccountService.queryLoanDetail(tenantId,userId,settleMonth,pageNum,pageSize)
        );
    }

    /**
     *22024  APP-????????????
     * @param userId
     * @param settleMonth
     * @return
     */
    @GetMapping("queryOrderDebtDetail")
    public ResponseResult OrderDebtDetail(@RequestParam("userId")Long userId,
                                          @RequestParam("settleMonth") String settleMonth) throws ParseException {
      return  ResponseResult.success(
              orderAccountService.queryOrderDebtDetail(userId,settleMonth)
      );
    }

    /**
     *22025  ????????????
     * @param userId
     * @param settleMonth
     * @return
     */
    @GetMapping("queryPeccancDetail")
    public ResponseResult queryPeccancDetail(@RequestParam("userId")Long userId,
                                             @RequestParam("settleMonth")String settleMonth){
        return ResponseResult.success(
                orderAccountService.queryPeccancDetail(userId,settleMonth)
        );
    }

    /**
     * ????????????
     */
    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }
}
