package com.youming.youche.order.business.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.api.IPayFeeLimitService;
import com.youming.youche.capital.vo.PayFeeLimitVo;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.DistanceGpsAPI;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.dto.AccountBankRelDto;
import com.youming.youche.finance.vo.OaLoanVo;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.dto.cm.CmCustomerLineOrderExtend;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.domain.TenantStaffRel;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单录入页面 前端控制器
 * </p>
 *
 * @author ly
 * @since 2022-02-24
 */
@RestController
@RequestMapping("/order/entry")
public class OrderEntryController extends BaseController<CmCustomerLine, ICmCustomerLineService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderEntryController.class);

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService iCmCustomerLineService;

    @DubboReference(version = "1.0.0")
    ITenantStaffRelService tenantStaffRelService;

    @DubboReference(version = "1.0.0")
    IOaLoanThreeService oaLoanService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;
    @DubboReference(version = "1.0.0")
    IPayFeeLimitService iPayFeeLimitService;

    @Override
    public ICmCustomerLineService getService() {
        return iCmCustomerLineService;
    }




    /**
     * @author 梁岩
     *  选择单条线路信息确认展示客户、承运、收货、收入数据
     * @date 16:48 2022/2/25
     * @Param []
     */
    @GetMapping("/line/selectLineListByAddress")
    public ResponseResult selectLineListByAddress(@RequestParam("customerId") Long customerId,
                                                  @RequestParam("lineCodeRule") String lineCodeRule) {
        try{
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            CmCustomerLineOrderExtend cmCustomerLineOrderExtend = iCmCustomerLineService.selectLineListByAddress(customerId, accessToken, lineCodeRule);
            return ResponseResult.success(cmCustomerLineOrderExtend);
        } catch (Exception e) {
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * @author 梁岩
     *  录入订单页面根据地址获取导航距离
     * @date 16:48 2022/3/2
     */
    @GetMapping("/line/getLongDistance")
    public ResponseResult getLongDistance(@RequestParam("startLat") String startLat,
                                          @RequestParam("startLng") String startLng,
                                          @RequestParam("endLat") String endLat,
                                          @RequestParam("endLng") String endLng) {

        Map startMap = new HashMap<String, String>();
        startMap.put("lng", startLng);
        startMap.put("lat", startLat);
        Map endMap = new HashMap<String, String>();
        endMap.put("lng",endLng);
        endMap.put("lat",endLat);
        double temp= DistanceGpsAPI.backDis(startMap, endMap);
        HashMap map = new HashMap<>();
        map.put("distance",temp);
        return ResponseResult.success(map);
    }

    /**
     * 录入订单页面: 查询跟单人列表
     *
     * @param staffName 员工姓名
     * @return
     */
    
    @GetMapping("/line/getStaffInfo")
    public ResponseResult getStaffInfo(@RequestParam("staffName")String staffName){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<TenantStaffRel> page = new Page<>(1,100);
            Page<TenantStaffRel> staffInfo = tenantStaffRelService.getOrderStaffInfoBytenantId(page, 1, accessToken, null, staffName);
            return ResponseResult.success(staffInfo);
    }

    /**
     * 录入订单页面  主驾司机借支新增
     * liangyan
     * 2022-3-8 10:00
     * @param plateNumber  车牌号
     * @param borrowName  司机姓名
     * @param borrowPhone  司机手机号
     * @param amount  借支金额
     * @param bankName 银行名称
     * @param bankAccount 银行账号
     * @return
     */
    @GetMapping("/saveMainDriverOaLoanCar")
    public  ResponseResult saveMainDriverOaLoanCar(@RequestParam("plateNumber") String plateNumber,
                                                   @RequestParam("amount") String amount,
                                                   @RequestParam("borrowName") String borrowName,
                                                   @RequestParam("borrowPhone") String borrowPhone,
                                                   @RequestParam("bankName") String bankName,
                                                   @RequestParam("bankAccount") String bankAccount,
                                                   @RequestParam("carDriverId") String carDriverId,
                                                   @RequestParam("orderId") String orderId){
        try{
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        List<PayFeeLimitVo> PayFeeLimitVolist = iPayFeeLimitService.queryPayFeeLimitCfg(accessToken, "PAY_FEE_LIMIT_SUB_TYPE", "1");
        if(PayFeeLimitVolist != null && PayFeeLimitVolist.size() > 0) {
            for (PayFeeLimitVo payFeeLimitVo : PayFeeLimitVolist) {
                Integer subType = payFeeLimitVo.getSubType();
                if (subType != null && subType == 4) {
                    Long value = payFeeLimitVo.getValue();
                    if (value == null) {
                        value = 0L;
                    }
                    if (StringUtils.isNotEmpty(amount)) {
                        long applyAmountL = (long) (Double.parseDouble(amount) * 100);
                        if (applyAmountL > value) {
                            return ResponseResult.success("借支金额不能超过司机借支上限！");
                        }
                    }
                }
            }
        }

        //查询该司机有没有银行卡信息
        Page<AccountBankRelDto> selfAndBusinessBank = oaLoanService.getSelfAndBusinessBank(borrowName, borrowPhone, 1, 10, accessToken);
        List<AccountBankRelDto> records = selfAndBusinessBank.getRecords();
        OaLoanVo oaLoanVo = new OaLoanVo();
        if(records != null && records.size() > 0){
            AccountBankRelDto accountBankRelDto = records.get(0);
            oaLoanVo.setAccNo(accountBankRelDto.getAcctNo());
            oaLoanVo.setAccName(accountBankRelDto.getAcctName());
            oaLoanVo.setBankName(accountBankRelDto.getBankName());
            oaLoanVo.setBorrowName(accountBankRelDto.getUserName());
            oaLoanVo.setBorrowPhone(accountBankRelDto.getMobilePhone());
        }else {
            oaLoanVo.setAccNo(null);
            oaLoanVo.setAccName(null);
            oaLoanVo.setBankName(null);
        }
        oaLoanVo.setPlateNumber(plateNumber);
        oaLoanVo.setAmount(amount);
        oaLoanVo.setCarOwnerName(borrowName);
        oaLoanVo.setCarPhone(borrowPhone);
        oaLoanVo.setBankName(bankName);
        oaLoanVo.setBankAccount(bankAccount);
        oaLoanVo.setOrderId(orderId);
        oaLoanVo.setAccUserId(carDriverId);
        //默认开票1
        oaLoanVo.setIsNeedBill("1");
        oaLoanVo.setLaunch(2);
        //订单录入专用
        oaLoanVo.setIdentification(1);
        oaLoanVo.setClassify(2);
//        oaLoanVo.setBusiness(1);

        oaLoanVo.setLoanSubject(11);
        oaLoanVo.setRemark("差旅费");
        oaLoanVo.setType(1);

        oaLoanService.saveOrUpdateOaLoanCar(oaLoanVo, accessToken);
        return ResponseResult.success("借支成功");
        } catch (Exception e) {
            return ResponseResult.failure("借支异常");
        }
    }

    /**
     * 录入订单页面  通过主驾司机账号查找银行账号
     * liangyan
     * 2022-3-8 10:00
     * @param userId 司机账号
     * @return
     */
    @GetMapping("/getAccountBankRel")
    public ResponseResult getAccountBankRel(@RequestParam("userId") Long userId){
        List<AccountBankRel> bankListByUserId = accountBankRelService.getBankListByUserId(userId);
//        List<AccountBankUserTypeRelVo> accountBankUserTypeRelVos = accountBankUserTypeRelService.getBankListByUserId(userId);

        return ResponseResult.success(bankListByUserId);
    }

    /**
     * 实现功能: 选择自有车时，选择报账模式接口 TODO 报账模式里只有需要空载油耗、载重油耗，这两个从线路带过来
     * 油卡余额、油卡充值、添加油卡应该都是调用
     *
     * @param vehicleCode  车辆id
     * @param plateNumbers 车牌
     * @return
     */
//    @GetMapping("/getVehicleInfoVer")
//    public ResponseResult getVehicleInfoVer(@RequestParam("vehicleCode") Long vehicleCode,
//                                               @RequestParam("plateNumbers") String plateNumbers) {
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        Map<String, Object> vehicleInfoVer = iVehicleDataInfoService.getAllVehicleInfoVer(vehicleCode, 1, plateNumbers, accessToken);
//        return ResponseResult.success(null);
//    }

}
