package com.youming.youche.table.business.controller.statistic;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.statistic.IStatementCustomerDayService;
import com.youming.youche.table.api.statistic.IStatementCustomerMonthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 客户月报 前端控制器
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
@RestController
@RequestMapping("statement/customerMonth")
public class StatementCustomerMonthController extends BaseController {

    @DubboReference(version = "1.0.0")
    IStatementCustomerMonthService statementCustomerMonthService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return statementCustomerMonthService;
    }


    /**
     * 查询客户月报
     *
     * @param startMonth  开始月
     * @param endMonth 结束月
     * @return
     */
    @GetMapping("report")
    public ResponseResult report(String startMonth, String endMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(statementCustomerMonthService.report(startMonth, endMonth, accessToken));
    }

    /**
     * 导出客户月报
     *
     * @param startMonth  开始月
     * @param endMonth 结束月
     * @return
     */
    @GetMapping("export")
    public ResponseResult export(String startMonth, String endMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("客户月报信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            statementCustomerMonthService.export(startMonth,endMonth,accessToken,record);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.failure("导出客户月报异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }


}