package com.youming.youche.finance.business.controller.order;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.order.IOrderInfoExtService;
import com.youming.youche.finance.domain.order.OrderInfoExt;
import com.youming.youche.finance.dto.OilCardRechargeDto;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.dto.order.TurnCashDto;
import com.youming.youche.finance.vo.OilCardRechargeVo;
import com.youming.youche.finance.vo.QueryPayOutVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


/**
* <p>
* 订单扩展表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-15
*/
@RestController
@RequestMapping("order/info/ext")
public class OrderInfoExtController extends BaseController<OrderInfoExt, IOrderInfoExtService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderInfoExtController.class);

    @DubboReference(version = "1.0.0")
    IOrderInfoExtService iOrderInfoExtService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Override
    public IOrderInfoExtService getService() {
        return null;
    }

    /**
     * 油卡和ETC转现(根据月份查询某个账号的油卡和ETC)
     */
    @GetMapping("/queryOilAndEtcBalance")
    public ResponseResult queryOilAndEtcBalance(Long userId, String month,
                                                String turnType, String turnOilType,
                                                Integer userType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        TurnCashDto turnCashDto = iOrderInfoExtService.queryOilAndEtcBalance(userId, month.replace("-", ""), turnType, turnOilType,
                userType, accessToken);
        return ResponseResult.success(turnCashDto);
    }


    /**
     * 支付结果
     * @param queryPayOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/queryPayOut")
    public ResponseResult queryPayOut(QueryPayOutVo queryPayOutVo,
                                      @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        queryPayOutVo.setPageNum(pageNum);
        queryPayOutVo.setPageSize(pageSize);
        Page<PayoutInfoOutDto> payoutInfoOutDtoPage = iOrderInfoExtService.queryPayOut(queryPayOutVo,accessToken);
        return ResponseResult.success(payoutInfoOutDtoPage);
    }

    /**
     * 支付结果导出
     * @param queryPayOutVo
     * @return
     */
    @GetMapping("/export")
    public ResponseResult export(QueryPayOutVo queryPayOutVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("支付结果信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderInfoExtService.queryPayOutListExport(queryPayOutVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败支付结果列表异常" + e);
            return ResponseResult.failure("导出成功支付结果异常");
        }
    }

    /**
     * 油转油卡列表查询
     * @param oilCardRechargeVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/oilCardRecharge")
    public ResponseResult oilCardRecharge(OilCardRechargeVo oilCardRechargeVo,
                                          @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        oilCardRechargeVo.setPageNum(pageNum);
        oilCardRechargeVo.setPageSize(pageSize);
        Page<OilCardRechargeDto> oilCardRechargeDtoPage = iOrderInfoExtService.oilCardRecharge(oilCardRechargeVo, accessToken);
        return ResponseResult.success(oilCardRechargeDtoPage);
    }

    /**
     * 油转油卡列表导出
     * @param oilCardRechargeVo
     * @return
     */
    @GetMapping("/oilCardRechargeExport")
    public ResponseResult oilCardRechargeExport(OilCardRechargeVo oilCardRechargeVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("油转油卡列表信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOrderInfoExtService.oilCardRechargeListExport(oilCardRechargeVo, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败油转油卡列表异常" + e);
            return ResponseResult.failure("导出成功油转油卡异常");
        }
    }


    /**
     * 油转实体油卡核销
     * @param ids
     * @return
     */
    @PostMapping("/verificatOilTurnEntity")
    public ResponseResult verificatOilTurnEntity(String ids){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        iOrderInfoExtService.verificatOilTurnEntity(ids,accessToken);
        return ResponseResult.success();
    }

}
