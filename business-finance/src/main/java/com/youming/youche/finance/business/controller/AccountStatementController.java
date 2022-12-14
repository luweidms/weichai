package com.youming.youche.finance.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.IAccountStatementDetailsService;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.finance.dto.AccountStatementAppDto;
import com.youming.youche.finance.dto.AccountStatementUserDto;
import com.youming.youche.finance.dto.DoQueryBillReceiverPageListDto;
import com.youming.youche.finance.dto.GetAppDetailListDto;
import com.youming.youche.finance.dto.QueryAccountStatementOrdersDto;
import com.youming.youche.finance.dto.order.OrderStatementListToDoubleOutDto;
import com.youming.youche.finance.vo.AccountStatementOutVO;
import com.youming.youche.finance.vo.CreateAccountStatementVo;
import com.youming.youche.finance.vo.DoQueryBillReceiverPageListVo;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;
import com.youming.youche.finance.vo.QueryAccountStatementUserVo;
import com.youming.youche.finance.vo.order.AccountStatementCreateVo;
import com.youming.youche.finance.vo.order.OrderStatementListInVo;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
* <p>
*  ???????????????
* </p>
* @author luona
* @since 2022-04-11
*/
@RestController
@RequestMapping("account/statement")
public class AccountStatementController extends BaseController<AccountStatement,IAccountStatementService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountStatementController.class);

    @DubboReference(version = "1.0.0")
    IAccountStatementService accountStatementService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IAccountStatementDetailsService accountStatementDetailsService;

    @Override
    public IAccountStatementService getService() {
        return null;
    }

    /**
     * ?????????????????????
     * @param accountStatement
     * @param opType
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getAccountStatements")
    public ResponseResult getAccountStatements(AccountStatement accountStatement,Integer opType,
                                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountStatementOutVO> accountStatements = accountStatementService.getAccountStatements(accountStatement,opType, pageNum, pageSize, accessToken);
        return ResponseResult.success(accountStatements);
    }

    /**
     * ?????????????????????
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(AccountStatement accountStatement,Integer opType) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("???????????????????????????");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            accountStatementService.downloadExcelFile(accountStatement, opType, accessToken, record);
            return ResponseResult.success("??????????????????,???????????????????????????-?????????????????????????????????????????????");
        } catch (Exception e) {
            LOGGER.error("????????????????????????" + e);
            return ResponseResult.failure("??????????????????????????????");
        }
    }

    /**
     * ???????????????
     */
    @PostMapping("/createAccountStatement")
    public ResponseResult createAccountStatement(@RequestBody AccountStatementCreateVo accountStatementCreateVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        accountStatementService.createAccountStatement(accountStatementCreateVo, accessToken);
        return ResponseResult.success();
    }

    /**
     * ???????????????
     */
    @PostMapping("/sendAccountStatement")
    public ResponseResult sendAccountStatement(Long flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        accountStatementService.sendAccountStatement(flowId, accessToken);
        return ResponseResult.success();
    }

    /**
     * ???????????????
     */
    @PostMapping("/deleteAccountStatement")
    public ResponseResult deleteAccountStatement(Long flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        accountStatementService.deleteAccountStatement(flowId, accessToken);
        return ResponseResult.success();
    }

    /**
     * ?????????????????????????????????????????? 28302
     */
    @PostMapping("/queryAccountStatementMargin")
    public ResponseResult queryAccountStatementMargin(Long flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(accountStatementService.queryAccountStatementMargin(flowId));
    }

    /**
     * ?????????????????????
     */
    @PostMapping("/setUpSettlementAmount")
    public ResponseResult setUpSettlementAmount(Long flowId, String settlementAmountStr, String settlementRemark) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long settlementAmount = 0l;
        if (StringUtils.isEmpty(settlementAmountStr)) {
            throw new BusinessException("?????????????????????????????????");
        }else{
            if (CommonUtil.isNumber(settlementAmountStr)) {
                Double convertNum =  Double.parseDouble(settlementAmountStr) ;
                settlementAmount = Math.round( convertNum * 100);
            }else{
                throw new BusinessException("???????????????????????????????????????");
            }
        }
        accountStatementService.setUpSettlementAmount(flowId, settlementAmount, settlementRemark, accessToken);
        return ResponseResult.success();
    }

    /**
     * ???????????????
     */
    @PostMapping("settlementAmount")
    public ResponseResult settlementAmount(Long flowId, String settlementAmountStr, String settlementRemark) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long settlementAmount = 0l;
        if (StringUtils.isEmpty(settlementAmountStr)) {
            throw new BusinessException("?????????????????????????????????");
        }else{
            if (CommonUtil.isNumber(settlementAmountStr)) {
                Double convertNum =  Double.parseDouble(settlementAmountStr) ;
                settlementAmount = Math.round( convertNum * 100);
            }else{
                throw new BusinessException("???????????????????????????????????????");
            }
        }
        accountStatementService.settlementAmount(flowId, settlementAmount, settlementRemark, accessToken);
        return ResponseResult.success();
    }

    /**
     * ???????????????????????? 28310
     */
    @PostMapping("getAccountStatementOrders")
    public ResponseResult getAccountStatementOrders(OrderStatementListInVo orderStatementListInVo,
                                                    Long flowId,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderStatementListToDoubleOutDto> page = accountStatementService.getAccountStatementOrders(flowId, orderStatementListInVo, pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }

    /**
     * app?????????28300 ???????????????
     */
    @GetMapping("getAccountStatementFromApp")
    public ResponseResult getAccountStatementFromApp(Long receiverUserId, String billMonth, String state, String tenantName,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountStatementAppDto> accountStatementFromApp = accountStatementService.getAccountStatementFromApp(receiverUserId, billMonth, state, tenantName, accessToken, pageNum, pageSize);
        return ResponseResult.success(accountStatementFromApp);
    }

    /**
     * ?????????--???????????? -?????? 28301
     */
    @PostMapping("confirmAccountStatement")
    public ResponseResult confirmAccountStatement(Long flowId,String isPass,String remark) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        accountStatementService.confirmAccountStatement(flowId, isPass, remark, accessToken);
        return ResponseResult.success();
    }

    /**
     * 28303  ????????? --- ????????????
     */
    @GetMapping("getAccountStatementDetailFromApp")
    public ResponseResult getAccountStatementDetailFromApp(Long flowId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        AccountStatementAppDto accountStatementDetailFromApp = accountStatementService.getAccountStatementDetailFromApp(flowId, accessToken);
        return ResponseResult.success(accountStatementDetailFromApp);
    }

    /**
     * 28304 ?????????--??????????????????
     */
    @GetMapping("getOilCardDetailFromApp")
    public ResponseResult getOilCardDetailFromApp(Long flowId,
                                                  @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<OilCardPledgeOrderListDto> oilCardDetailFromApp = accountStatementService.getOilCardDetailFromApp(flowId, pageNum, pageSize);
        return ResponseResult.success(oilCardDetailFromApp);
    }

    /**
     * APP??????-??????????????????-???????????? ???????????????28305
     */
    @GetMapping("getAppDetailList")
    public ResponseResult getAppDetailList(Long accountStatementId, String carDriverName, String carDriverPhone, String plateNumber, Integer vehicleClass,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<GetAppDetailListDto> appDetailList = accountStatementDetailsService.getAppDetailList(accountStatementId, carDriverName, carDriverPhone, plateNumber, vehicleClass, pageNum, pageSize);
        return ResponseResult.success(appDetailList);
    }

    /**
     * ?????????????????????
     */
    @GetMapping("doQueryBillReceiverPageList")
    public ResponseResult doQueryBillReceiverPageList(DoQueryBillReceiverPageListVo vo,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<DoQueryBillReceiverPageListDto> ipage = accountStatementService.doQueryBillReceiverPageList(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(ipage);
    }

    /**
     * ?????????????????????  ?????????
     */
    @GetMapping("queryAccountStatementUser")
    public ResponseResult queryAccountStatementUser(QueryAccountStatementUserVo vo,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<AccountStatementUserDto> page = accountStatementService.queryAccountStatementUser(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(page);
    }

    /**
     * ?????????????????????
     */
    @PostMapping("createAccountStatementNew")
    public ResponseResult createAccountStatementNew(@RequestBody CreateAccountStatementVo vo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        accountStatementService.createAccountStatementNew(vo, accessToken);
        return ResponseResult.success();
    }

    /**
     * ?????????????????????????????????
     */
    @GetMapping("queryAccountStatementOrders")
    public ResponseResult queryAccountStatementOrders(QueryAccountStatementOrdersVo vo,
                                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<QueryAccountStatementOrdersDto> page = accountStatementService.queryAccountStatementOrders(vo, accessToken, pageNum, pageSize);
        return ResponseResult.success(page);
    }

}
