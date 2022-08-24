package com.youming.youche.table.business.controller.statistic;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.statistic.IStatementCustomerDayService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 客户日报 前端控制器
 * </p>
 *
 * @author luwei
 * @since 2022-05-09
 */
@RestController
@RequestMapping("statement/customerDay")
public class StatementCustomerDayController extends BaseController {

    @DubboReference(version = "1.0.0")
    IStatementCustomerDayService statementCustomerDayService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return statementCustomerDayService;
    }

    /**
     * 查询客户日报
     *
     * @param startDate  开始时间
     * @param endDate 结束时间
     * @return
     */
    @GetMapping("report")
    public ResponseResult report(String startDate, String endDate) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(statementCustomerDayService.report(startDate, endDate, accessToken));
    }

    /***
     * @Description: 导出客户日报
     * @Author: luwei
     * @Date: 2022/5/6 11:23 下午
     * @Param startDate:
     * @Param endDate:
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    @GetMapping("export")
    public ResponseResult exportReport(String startDate,String endDate){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("客户日报信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            statementCustomerDayService.exportReport(startDate,endDate,accessToken,record);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.failure("导出客户日报异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }
}
