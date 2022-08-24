package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyVerService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.domain.order.OrderDriverSubsidyVer;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.provider.mapper.order.OrderDriverSubsidyVerMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
* <p>
    *  服务实现类
    * </p>
* @author liangyan
* @since 2022-03-23
*/
@DubboService(version = "1.0.0")
@Service
public class OrderDriverSubsidyVerServiceImpl extends BaseServiceImpl<OrderDriverSubsidyVerMapper, OrderDriverSubsidyVer> implements IOrderDriverSubsidyVerService {
    @Autowired
    private IOrderDriverSubsidyService orderDriverSubsidyService;

    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;
    @Override
    public void saveDriverSubsidyVer(Long orderId, Long userId, String subsidyTime, Long subsidySalary, Integer driverType, Integer isPayed, LoginInfo user) {

        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        Long tenantId1 = user.getTenantId();
        //修改之前的订单司机补贴版本为已过期
        //orderDriverSubsidySV.updateDriverSubsidyVer(orderId, null,driverType, 1);

        if(StringUtils.isNotBlank(subsidyTime)&&userId!=null&&userId>0) {
            String[] subsidyDateArr=subsidyTime.split(" ");
            Date currDate=new Date();
            subsidySalary=subsidySalary==null||subsidySalary<0?0:subsidySalary;
            for (String subsidyDate : subsidyDateArr) {
                if(StringUtils.isBlank(subsidyDate)) {
                    continue;
                }
                OrderDriverSubsidyVer subsidy=new OrderDriverSubsidyVer();
                subsidy.setOrderId(orderId);
                subsidy.setDriverType(driverType);
               String s1 =  DateUtil.getYear(currDate)+"-"+subsidyDate;
                String s2 =  DateUtil.getYear(currDate)+"-"+subsidyDate+" 00:00:00";
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime parse = LocalDateTime.parse(s2, dateTimeFormatter);
                subsidy.setSubsidyDate(parse);
                subsidy.setSubsidy(subsidySalary);
                subsidy.setTenantId(user.getTenantId());
                subsidy.setUserId(userId);
                subsidy.setCreateTime(LocalDateTime.now());
                subsidy.setOpId(user.getId());
                subsidy.setIsPayed(isPayed);
                subsidy.setUpdateTime(subsidy.getCreateTime());
                this.saveOrUpdate(subsidy);
            }

        }

    }

    @Override
    public Boolean saveDriverSubsidy(Long orderId, Long userId, LocalDateTime subsidyDate,
                                     Long subsidySalary, Integer driverType,LoginInfo user) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        if(userId==null||userId<=0) {
            throw new BusinessException("缺少司机！");
        }
        Long tenantId=user.getTenantId();

        //1.判断司机是否当天存在补贴
        List list=orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, userId, tenantId, false,null);
        if(list==null||list.size()==0) {
            list=orderDriverSubsidyService.findDriverSubsidys(subsidyDate, null, null, userId, tenantId, true,null);
        }
        //2.当天不存在补贴则保存
        if(list==null||list.size()==0) {
            OrderDriverSubsidy subsidy=new OrderDriverSubsidy();
            subsidySalary=subsidySalary==null||subsidySalary<0?0:subsidySalary;
            subsidy.setOrderId(orderId);
            subsidy.setDriverType(driverType);
            subsidy.setSubsidyDate(subsidyDate);
            subsidy.setSubsidy(subsidySalary);
            subsidy.setTenantId(tenantId);
            subsidy.setUserId(userId);
            subsidy.setCreateTime(LocalDateTime.now());
            subsidy.setOpId(user.getId());
            subsidy.setIsPayed(OrderConsts.AMOUNT_FLAG.WILL_PAY);
            subsidy.setUpdateTime(subsidy.getCreateTime());
            orderDriverSubsidyService.saveOrUpdate(subsidy);
            return true;
        }
        return false;
    }
}
