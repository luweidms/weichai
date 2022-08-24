package com.youming.youche.finance.business.controller.order;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.finance.api.order.IInfoHService;
import com.youming.youche.finance.dto.order.OrderDealInfoDto;
import com.youming.youche.finance.vo.order.OrderDealInfoVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* <p>
* 新的订单表 前端控制器
* </p>
* @author liangyan
* @since 2022-03-15
*/
@RestController
@RequestMapping("info-h")
public class InfoHController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoHController.class);

    @DubboReference(version = "1.0.0")
    private IInfoHService infoHService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return infoHService;
    }

    /**
     * 查询司机明细  未到期明细
     */
    @PostMapping("/getDealOrderInfo")
    public ResponseResult getDealOrderInfo(@RequestBody OrderDealInfoVo orderDealInfoVo) {
        orderDealInfoVo.setPageNum(orderDealInfoVo.getPageNum() - 1);
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<OrderDealInfoDto> page = infoHService.getDealOrderInfo(accessToken, orderDealInfoVo);
        return ResponseResult.success(page);
    }

    /**
     * 导出司机明细  未到期明细
     */
    @PostMapping("/downloadExcelFile")
    public ResponseResult downloadExcelFile(@RequestBody OrderDealInfoVo orderDealInfoVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("司机账户 - 未到期明细 导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            infoHService.downloadExcelFile(accessToken, orderDealInfoVo, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出异常审核异常" + e);
            return ResponseResult.failure("导出成功异常审核异常");
        }
    }
}
