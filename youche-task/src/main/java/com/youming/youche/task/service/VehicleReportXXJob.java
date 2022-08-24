package com.youming.youche.task.service;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.table.api.vehicleReport.IVehicleReportService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class VehicleReportXXJob {

    @DubboReference(version = "1.0.0")
    IVehicleReportService iVehicleReportService;

    @XxlJob("vehicleReportJobHandler")
    public void vehicleReportJobHandler() {

        XxlJobHelper.log("XXL-JOB, 车辆报表业务开始");
        iVehicleReportService.execute(getLastDate());
        XxlJobHelper.log("XXL-JOB, 车辆报表业务结束");

    }

    /**
     * @return yyyy-MM
     */
    private static String getLastDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, -1);
        return sdf.format(cal.getTime());
    }

}
