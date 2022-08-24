package com.youming.youche.table.business.controller.receivable;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.receivable.IDailyAccountsReceivableService;
import com.youming.youche.table.domain.receivable.DailyAccountsReceivable;
import com.youming.youche.table.dto.receivable.ReceivableDetailsDto;
import com.youming.youche.table.vo.receivable.ReceivableDetailsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.youming.youche.commons.base.BaseController;

import java.util.List;

/**
 * <p>
 * 应收日报 前端控制器
 * </p>
 *
 * @author hzx
 * @since 2022-04-30
 */
@RestController
@RequestMapping("daily/accounts/receivable")
public class DailyAccountsReceivableController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyAccountsReceivableController.class);

    @DubboReference(version = "1.0.0")
    IDailyAccountsReceivableService iDailyAccountsReceivableService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return null;
    }

    /**
     * 查询应收日报
     *
     * @param name  客户名称
     * @param month 月份
     * @return
     */
    @GetMapping("query/day")
    public ResponseResult queryDay(String name, String month,
                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<DailyAccountsReceivable> query = iDailyAccountsReceivableService.queryDay(name, month, pageNum, pageSize, accessToken);
        return ResponseResult.success(query);
    }

    /**
     * 查询应收日报导出
     *
     * @param name  客户名称
     * @param month 月份
     * @return
     */
    @GetMapping("query/day/export")
    public ResponseResult queryExport(String name, String month) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("应收日报信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iDailyAccountsReceivableService.queryDayExport(name, month, record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败应收日报列表异常" + e);
            return ResponseResult.failure("导出成功应收日报异常");
        }
    }

    /**
     * 应收详情
     */
    @GetMapping("receivableDetails")
    public ResponseResult receivableDetails(ReceivableDetailsVo vo,
                                            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<ReceivableDetailsDto> receivableDetailsDtos = iDailyAccountsReceivableService.receivableDetails(vo, accessToken, pageNum, pageSize);
        return ResponseResult.success(receivableDetailsDtos);
    }

    /**
     * 应收详情导出
     */
    @GetMapping("receivableDetailsExport")
    public ResponseResult receivableDetailsExport(ReceivableDetailsVo vo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("应收详情信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iDailyAccountsReceivableService.receivableDetailsExport(vo, record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败应收详情列表异常" + e);
            return ResponseResult.failure("导出成功应收详情异常");
        }

    }

}
