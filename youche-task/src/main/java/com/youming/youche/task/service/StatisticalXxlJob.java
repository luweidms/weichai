package com.youming.youche.task.service;

import cn.hutool.core.util.StrUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.table.api.statistic.IStatementCustomerDayService;
import com.youming.youche.table.api.statistic.IStatementCustomerMonthService;
import com.youming.youche.table.api.statistic.IStatementDepartmentDayService;
import com.youming.youche.table.api.statistic.IStatementDepartmentMonthService;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CmbXxlJob
 * @Description 统计分析定时任务
 * @Author zag
 * @Date 2022/3/25 10:50
 */
@Component
public class StatisticalXxlJob {


    @DubboReference(version = "1.0.0")
    IStatementDepartmentDayService statementDepartmentDayService;

    @DubboReference(version = "1.0.0")
    IStatementDepartmentMonthService statementDepartmentMonthService;

    @DubboReference(version = "1.0.0")
    IStatementCustomerDayService statementCustomerDayService;

    @DubboReference(version = "1.0.0")
    IStatementCustomerMonthService statementCustomerMonthService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    /**
     * 部门日报统计
     *
     * @return void
     * @author luwei
     * @date 2022/3/25 11:26
     */
    @XxlJob("departmentDayReportJobHandler")
    public void departmentDayReportJobHandler() {
        XxlJobHelper.log("departmentReportJobHandler, 部门日报开始");
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isEmpty(param)) {
            param = "1";
        }
//        String startDate = "2022-04-01";
//        String endTime = "2022-05-08";
        //查询所有车队信息
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.list();
        String date = DateUtil.formatDate(DateUtil.diffDate(new Date(), Integer.valueOf(param)));
        //统计部门订单收入
        if (sysTenantDefList != null) {
            for (SysTenantDef s : sysTenantDefList
            ) {
                try {
                    XxlJobHelper.log(s.getName() + "车队开始统计部门日报");
                    statementDepartmentDayService.departmentStatisticsDayReport(date, date, s.getId());
                    XxlJobHelper.log(s.getName() + "车队计算部门日报统计完成");
                } catch (Exception e) {
                    XxlJobHelper.log(e.getMessage());
                }
            }
        }
        XxlJobHelper.log("departmentReportJobHandler, 部门日报开始结束");
    }

    /**
     * 客户日报统计
     *
     * @return void
     * @author luwei
     * @date 2022/3/25 11:26
     */
    @XxlJob("customerDayReportJobHandler")
    public void customerDayReportJobHandler() {
        XxlJobHelper.log("customerDayReportJobHandler, 客户日报开始");
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isEmpty(param)) {
            param = "1";
        }
//        String startDate = "2022-04-01";
//        String endTime = "2022-05-08";
        //查询所有车队信息
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.list();
        String date = DateUtil.formatDate(DateUtil.diffDate(new Date(), Integer.valueOf(param)));
        //统计部门订单收入
        if (sysTenantDefList != null) {
            for (SysTenantDef s : sysTenantDefList
            ) {
                try {
                    XxlJobHelper.log(s.getName() + "车队开始统计客户日报");
                    statementCustomerDayService.customerStatisticsDayReport(date, date, s.getId());
                    XxlJobHelper.log(s.getName() + "车队计算客户日报统计完成");
                } catch (Exception e) {
                    XxlJobHelper.log(e.getMessage());
                }
            }
        }
        XxlJobHelper.log("departmentReportJobHandler, 客户日报统计结束");
    }



    /**
     * 部门月报统计
     *
     * @return void
     * @author luwei
     * @date 2022/3/25 11:26
     */
    @XxlJob("departmentMonthReportJobHandler")
    public void departmentMonthReportJobHandler() {
        XxlJobHelper.log("departmentMonthReportJobHandler, 部门月报开始");
        Map<String ,String> dateStr = DateUtil.getLastOneMonthDay(null);
        //查询所有车队信息
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.list();
        //统计部门订单收入
        if (sysTenantDefList != null) {
            for (SysTenantDef s : sysTenantDefList
            ) {
                try {
                    XxlJobHelper.log(s.getName() + "车队开始统计部门月报");
                    statementDepartmentMonthService.departmentStatisticsMonthReport(dateStr.get("beginDate"), dateStr.get("endDate"), s.getId());
                    XxlJobHelper.log(s.getName() + "车队计算部门月报统计完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    XxlJobHelper.log(e.getMessage());
                }
            }
        }
        XxlJobHelper.log("departmentMonthReportJobHandler, 部门月报开始结束");
    }

    /**
     * 客户月报统计
     *
     * @return void
     * @author luwei
     * @date 2022/3/25 11:26
     */
    @XxlJob("customerMonthReportJobHandler")
    public void customerMonthReportJobHandler() {
        XxlJobHelper.log("customerMonthReportJobHandler, 客户月报开始");
        Map<String ,String> dateStr = DateUtil.getLastOneMonthDay(null);
        //查询所有车队信息
        List<SysTenantDef> sysTenantDefList = sysTenantDefService.list();
        //统计部门订单收入
        if (sysTenantDefList != null) {
            for (SysTenantDef s : sysTenantDefList
            ) {
                try {
                    XxlJobHelper.log(s.getName() + "车队开始统计客户月报");
                    statementCustomerMonthService.customerStatisticsMonthReport(dateStr.get("beginDate"), dateStr.get("endDate"), s.getId());
                    XxlJobHelper.log(s.getName() + "车队计算客户月报统计完成");
                } catch (Exception e) {
                    e.printStackTrace();
                    XxlJobHelper.log(e.getMessage());
                }
            }
        }
        XxlJobHelper.log("customerMonthReportJobHandler, 客户日报统计结束");
    }

}
