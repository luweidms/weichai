package com.youming.youche.system.provider.service.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.tenant.IServiceChargeReminderService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.tenant.ServiceChargeReminder;
import com.youming.youche.system.provider.mapper.tenant.ServiceChargeReminderMapper;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


/**
 * <p>
 * 平台服务费到期记录表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-21
 */
@DubboService(version = "1.0.0")
public class ServiceChargeReminderServiceImpl extends BaseServiceImpl<ServiceChargeReminderMapper, ServiceChargeReminder> implements IServiceChargeReminderService {

    private static final String STR_FORMAT = "00000000";

    @Resource
    private ISysTenantDefService sysTenantDefService;

    @Override
    public void operateServiceChargeReminder(SysTenantDef sysTenantDef) throws Exception {
        //查询车队
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));//当前年份
        date.add(Calendar.YEAR, 1);
        Date y = date.getTime();
        String nextYear = format.format(y);//当前系统下一年

        Date currDate = format.parse(format.format(new Date()));

        Long serviceFee = sysTenantDef.getServiceFee();
        long userId = sysTenantDef.getAdminUser();
        if (StringUtils.isNoneBlank(sysTenantDef.getPayServiceFeeDate()) && (serviceFee != null && serviceFee > 0L)) {
            String payServiceFeeDate = sysTenantDef.getPayServiceFeeDate();
            ZoneId zoneId = ZoneId.systemDefault();
            Date payDate = Date.from(sysTenantDef.getPayDate().atZone(zoneId).toInstant());//付款时间（年月日）最后一次操作付款的打款时间
            if (payDate != null) {
                LocalDate localDate = payDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                year = (localDate.getYear() + 1) + "";
                nextYear = (localDate.getYear() + 2) + "";
            }
            boolean isExpire = false;
            Date expireDate = null;
            if (DateUtil.addDate(currDate, 3).after(format.parse(year + "-" + payServiceFeeDate))) {
                isExpire = true;
                expireDate = format.parse(year + "-" + payServiceFeeDate);
            } else if (DateUtil.addDate(currDate, 3).after(format.parse(nextYear + "-" + payServiceFeeDate))) {
                isExpire = true;
                expireDate = format.parse(nextYear + "-" + payServiceFeeDate);
            }

            //如果超期时间在付款时间之前，将提示状态改为失效
            if (payDate != null && (expireDate == null || expireDate.before(payDate))) {
                isExpire = false;
                baseMapper.updateStatus(sysTenantDef.getId(), payDate, 0);
            }
            if (isExpire) {//3天之内到期
                //到期缴费状态 0失效 1未交费 2缴费(打款中)3支付成功
                List<ServiceChargeReminder> serviceChargeReminderList = baseMapper.queryServiceChargeReminder(userId, expireDate);
                ServiceChargeReminder serviceChargeReminder = new ServiceChargeReminder();
                if (serviceChargeReminderList == null) {
                    baseMapper.updateStatus(sysTenantDef.getId(), null, 0);
                    serviceChargeReminder.setUserId(userId);
                    serviceChargeReminder.setAmount(serviceFee);
                    serviceChargeReminder.setExpireDate(expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    serviceChargeReminder.setState(1);
                    serviceChargeReminder.setTenantId(sysTenantDef.getId());
                    serviceChargeReminder.setReminderId("SP" + this.turnFlowId(serviceChargeReminder.getId() + ""));
                    baseMapper.insert(serviceChargeReminder);
                } else {
                    serviceChargeReminder = serviceChargeReminderList.get(0);
                    //如果已经存在，判断是否没有付款，并且已经逾期了
                    if (serviceChargeReminder.getExpireDate() != null && Date.from(serviceChargeReminder.getExpireDate().atZone(ZoneId.systemDefault()).toInstant()).before(currDate)
                            && serviceChargeReminder.getState() == 1) {//未交费并且逾期
                        SysTenantDef sysTenantDefUp = new SysTenantDef();
                        QueryWrapper queryWrapper = new QueryWrapper();
                        queryWrapper.eq("id", serviceChargeReminder.getTenantId());
                        sysTenantDefUp.setPayState(SysStaticDataEnum.TENANT_PAY_STATE.EXPIRED);
                        sysTenantDefService.update(sysTenantDefUp, queryWrapper);
                    }
                }
            } else {
                baseMapper.updateStatusSan(sysTenantDef.getId(), 0);
            }
        }
    }

    @Override
    public ServiceChargeReminder getServiceChargeReminder(String reminderId) {
        LambdaQueryWrapper<ServiceChargeReminder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceChargeReminder::getReminderId, reminderId);
        List<ServiceChargeReminder> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new ServiceChargeReminder();
    }

    public static String turnFlowId(String liuShuiHao) {
        Integer intHao = Integer.parseInt(liuShuiHao);
        DecimalFormat df = new DecimalFormat(STR_FORMAT);
        return df.format(intHao);
    }
}
