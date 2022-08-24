package com.youming.youche.order.dto.monitor;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Description:车辆事件信息
 *
 * @author hzx
 */
@Data
public class BaseEventsInfoDto implements Serializable {

    private static final long serialVersionUID = -1L;

    private String eventCode;

    private String eventName;

    /**
     * 事件发生地经度
     */
    private double eventsLongitude;

    /**
     * 事件发生地纬度
     */
    private double eventsLatitude;

    //靠台时间
    private Date dependTime;

    //晚靠台分钟数
    private double lateDependMinute;

    //距离靠台地
    private double dependDistance;

    //停留时间分钟数
    private double stopMinute;

    //堵车时间分钟数
    private double slowMinute;

    //偏离时间分钟数
    private double deviateMinute;

    //偏离距离(公里)
    private double deviateDistance;

    //离线分钟数
    private double offlineMinute;

    /**
     * 离线时间
     */
    private Date offlineTime;

    //离线位置
    private double offlinePosition;

    //预计到达时间
    private Date predictArriveDate;

    // (行驶时间/时效)*100
    private double timePercent;

    // (行驶距离/里程)*100
    private double mileagePercent;

    //迟到时间
    private double lateArriveMinute;

    //距目的地距离(公里)
    private double lateArriveDistance;

    //实际到达时间
    private Date actArriveDate;

    //离台时间
    private Date offStageDate;

    //里程(公里)
    private double mileage;

    //时效
    private double limitHour;

    //行驶距离(公里)
    private double drivingMileage;

    //行驶时间
    private double drivingTime;

    //备用时间
    private double freeHour;

    //上次年审
    private Date preAnnualTrialDate;

    //上次季审
    private Date preQuarterlyTrialDate;

    //上次保养时间
    private Date preMaintainDate;

    //上次保险时间
    private Date preInsuranceDate;

    @Override
    public String toString() {
        return "BaseEventsInfoDto [eventCode=" + eventCode + ", eventName=" + eventName + ", eventsLongitude="
                + eventsLongitude + ", eventsLatitude=" + eventsLatitude + ", dependTime=" + dependTime
                + ", lateDependMinute=" + lateDependMinute + ", dependDistance=" + dependDistance + ", stopMinute="
                + stopMinute + ", slowMinute=" + slowMinute + ", deviateMinute=" + deviateMinute + ", deviateDistance="
                + deviateDistance + ", offlineMinute=" + offlineMinute + ", offlinePosition=" + offlinePosition
                + ", predictArriveDate=" + predictArriveDate + ", lateArriveMinute=" + lateArriveMinute
                + ", lateArriveDistance=" + lateArriveDistance + ", actArriveDate=" + actArriveDate + ", offStageDate="
                + offStageDate + ", mileage=" + mileage + ", limitHour=" + limitHour + ", drivingMileage="
                + drivingMileage + ", drivingTime=" + drivingTime + ", freeHour=" + freeHour + ", preAnnualTrialDate="
                + preAnnualTrialDate + ", preQuarterlyTrialDate=" + preQuarterlyTrialDate + ", preMaintainDate="
                + preMaintainDate + ", preInsuranceDate=" + preInsuranceDate + "]";
    }

}
