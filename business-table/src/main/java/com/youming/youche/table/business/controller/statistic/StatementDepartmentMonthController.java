package com.youming.youche.table.business.controller.statistic;


import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.table.api.statistic.IStatementDepartmentMonthService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import com.youming.youche.commons.base.BaseController;

/**
 * <p>
 * 部门日报 前端控制器
 * </p>
 *
 * @author luwei
 * @since 2022-04-27
 */
@RestController
@RequestMapping("/statistic/departmentMonth")
public class StatementDepartmentMonthController extends BaseController {

    @DubboReference(version = "1.0.0")
    IStatementDepartmentMonthService statementDepartmentMonthService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IBaseService getService() {
        return statementDepartmentMonthService;
    }



    /**
     * 查询部门月报
     *
     * @param startMonth  开始月
     * @param endMonth 结束月
     * @return
     */
    @GetMapping("report")
    public ResponseResult report(String startMonth, String endMonth) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(statementDepartmentMonthService.report(startMonth, endMonth, accessToken));
    }

    /**
     * 导出部门月报
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
            record.setName("部门月报信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            statementDepartmentMonthService.export(startMonth,endMonth,accessToken,record);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.failure("导出部门月报异常");
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }


}
