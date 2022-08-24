package com.youming.youche.order.provider.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.order.IOrderDriverSubsidyVerService;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.domain.order.OrderDriverSubsidyH;
import com.youming.youche.order.domain.order.OrderDriverSubsidyVer;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.OrderDriverSubsidyDto;
import com.youming.youche.order.provider.mapper.OrderDriverSubsidyMapper;
import com.youming.youche.order.provider.mapper.order.OrderDriverSubsidyHMapper;
import com.youming.youche.order.provider.mapper.order.OrderDriverSubsidyVerMapper;
import com.youming.youche.order.provider.mapper.order.OrderSchedulerMapper;
import com.youming.youche.order.provider.utils.LocalDateTimeUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-12
 */
@DubboService(version = "1.0.0")
@Service
public class OrderDriverSubsidyServiceImpl extends BaseServiceImpl<OrderDriverSubsidyMapper, OrderDriverSubsidy> implements IOrderDriverSubsidyService {

    @Resource
    private OrderDriverSubsidyMapper orderDriverSubsidyMapper;

    @Resource
    private OrderSchedulerMapper orderSchedulerMapper;

    @Resource
    private OrderDriverSubsidyVerMapper orderDriverSubsidyVerMapper ;

    @Autowired
    private IOrderDriverSubsidyVerService orderDriverSubsidyVerService;
    @Autowired
    private IOrderDriverSubsidyService orderDriverSubsidyService;

    @Resource
    private OrderDriverSubsidyHMapper orderDriverSubsidyHMapper;

    @Override
    public OrderDriverSubsidy getSubsidyByUserId(Long userId, Integer driverType, Long tenantId) {
        QueryWrapper<OrderDriverSubsidy> orderDriverSubsidyQueryWrapper = new QueryWrapper<>();
        orderDriverSubsidyQueryWrapper.eq("user_id", userId)
                .eq("driver_type", driverType)
                .eq("tenant_id", tenantId);
        List<OrderDriverSubsidy> orderDriverSubsidies = orderDriverSubsidyMapper.selectList(orderDriverSubsidyQueryWrapper);
        if (orderDriverSubsidies == null || orderDriverSubsidies.size() == 0) {
            return new OrderDriverSubsidy();
        } else {
            return orderDriverSubsidies.get(0);
        }
    }

    @Override
    public void updateDriverSubsidyVer(Long orderId, Long userId, Integer driverType, Integer verStatus) {

        OrderDriverSubsidyVer orderDriverSubsidyVer = new OrderDriverSubsidyVer();
        orderDriverSubsidyVer.setVerStatus(verStatus);
        UpdateWrapper<OrderDriverSubsidyVer> orderDriverSubsidyVerUpdateWrapper = new UpdateWrapper<>();
        orderDriverSubsidyVerUpdateWrapper.eq("order_id", orderId);
        if (userId != null && userId > 0) {
            orderDriverSubsidyVerUpdateWrapper.eq("user_id", userId);
        }
        if (driverType != null && driverType > 0) {
            orderDriverSubsidyVerUpdateWrapper.eq("driver_type", driverType);
        }
        orderDriverSubsidyVerMapper.update(orderDriverSubsidyVer, orderDriverSubsidyVerUpdateWrapper);
    }

    @Override
    public List<OrderDriverSubsidy> findDriverSubsidysByOrder(Long orderId, Long userId, Long tenantId) {
        LambdaQueryWrapper<OrderDriverSubsidyVer> lambda=Wrappers.lambdaQuery();
        lambda.eq(OrderDriverSubsidyVer::getTenantId,tenantId);
        if(orderId!=null&&orderId>0) {
            lambda.eq(OrderDriverSubsidyVer::getOrderId,orderId);
        }
        if(userId!=null&&userId>0){
            lambda.eq(OrderDriverSubsidyVer::getUserId,userId);
        }
        lambda.eq(OrderDriverSubsidyVer::getVerStatus,0);

        List<OrderDriverSubsidyVer> list = orderDriverSubsidyVerService.list(lambda);
        List<OrderDriverSubsidy> orderDriverSubsidies=new ArrayList<>();
        for (OrderDriverSubsidyVer orderDriverSubsidyVer : list) {
            OrderDriverSubsidy orderDriverSubsidy=new OrderDriverSubsidy();
            BeanUtils.copyProperties( orderDriverSubsidyVer,orderDriverSubsidy);
            orderDriverSubsidies.add(orderDriverSubsidy);
        }
        return orderDriverSubsidies;
    }


    @Override
    public List<OrderDriverSubsidy> findDriverSubsidys(LocalDateTime startDate, LocalDateTime endDate,
                                                       Long orderId, Long userId, Long tenantId,
                                                       Boolean isHis, Long excludeOrderId) {
        String tableName=isHis?"order_driver_subsidy_h":"order_driver_subsidy";
        return orderDriverSubsidyMapper.findDriverSubsidys(startDate,endDate,orderId,userId,tenantId,tableName,excludeOrderId,isHis);
    }

    @Override
    public List<OrderDriverSubsidy> getDriverSubsidys(Long orderId, Long userId, Boolean isHis, LoginInfo user) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        if(userId==null||userId<=0) {
            throw new BusinessException("缺少用户Id");
        }
        Long tenantId=user.getTenantId();
        List<OrderDriverSubsidy> driverSubsidys = this.findDriverSubsidys(null, null, orderId, userId, tenantId, isHis, null);
        return driverSubsidys;
    }


    /**
     * 是否需要支付补贴
     * @param orderId 订单号
     * @return
     * @throws Exception
     */
    @Override
    public Boolean isPayDriverSubsidy(Long orderId,LoginInfo user) {
        QueryWrapper<OrderScheduler> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        OrderScheduler orderScheduler = orderSchedulerMapper.selectOne(wrapper);
        if (orderScheduler != null) {
            // TODO 查找司机未付补贴
            List<OrderDriverSubsidyDto> list = this.findDriverNoPaySubsidys(orderId, null, orderScheduler.getCarDriverId(), orderScheduler.getCopilotUserId(),
                    user.getTenantId(), false);
            if(list != null && list.size() >0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找司机未付补贴
     * 聂杰伟
     * @param orderId 订单号
     * @param userId
     * @param carDriverId 排除主驾ID
     * @param copilotUserId 排除副驾ID
     * @param tenantId
     * @param isHis 是否查历史 1 是查 2是不差
     * @return
     */
    @Override
    public List<OrderDriverSubsidyDto> findDriverNoPaySubsidys(Long orderId, Long userId, Long carDriverId, Long copilotUserId, Long tenantId, Boolean isHis) {
        if (isHis==null){
            throw  new BusinessException("不能为空");
        }
        String tableName=isHis?"1":"2";
//        String tableName=isHis?"order_driver_subsidy_h":"order_driver_subsidy";
        List<OrderDriverSubsidyDto> driverNoPaySubsidys = baseMapper.findDriverNoPaySubsidys(orderId, userId, carDriverId, copilotUserId, tenantId, tableName);
        return driverNoPaySubsidys;
    }

    @Override
    public void sycDriverSubsidyFromVer(Long orderId,LoginInfo user) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        Long tenantId=user.getTenantId();
        this.deleteDriverSubsidy(orderId, null,null);
        //查找历史版本记录的补贴
        List<OrderDriverSubsidy> verList=this.findDriverSubsidysByOrder(orderId, null, tenantId);
        if(verList!=null) {
            List list=null;
            Long optId=user.getId();
            for (OrderDriverSubsidy orderDriverSubsidy : verList) {
                //判断司机当天是否存在补贴
                list=orderDriverSubsidyService.findDriverSubsidys(orderDriverSubsidy.getSubsidyDate(),
                        null, null, orderDriverSubsidy.getUserId(), tenantId,
                        false,null);
                if(list==null||list.size()==0) {
                    list=orderDriverSubsidyService.findDriverSubsidys(orderDriverSubsidy.getSubsidyDate(),
                            null, null, orderDriverSubsidy.getUserId(),
                            tenantId, true,null);
                }
                //当天不存在补贴则保存
                if(list==null||list.size()==0) {
                    OrderDriverSubsidy newOrderDriverSubsidy=new OrderDriverSubsidy();
                    BeanUtils.copyProperties(orderDriverSubsidy,newOrderDriverSubsidy);
                    newOrderDriverSubsidy.setId(null);
                    newOrderDriverSubsidy.setCreateTime(LocalDateTime.now());
                    newOrderDriverSubsidy.setOpId(optId);
                    newOrderDriverSubsidy.setTenantId(tenantId);
                    newOrderDriverSubsidy.setUpdateTime(newOrderDriverSubsidy.getCreateTime());
                    this.saveOrUpdate(newOrderDriverSubsidy);
                }
            }
        }
    }

    @Override
    public void deleteDriverSubsidy(Long orderId,Long userId,Integer driverType) {
        if(orderId==null||orderId<=0) {
            throw new BusinessException("缺少订单号！");
        }
        LambdaQueryWrapper<OrderDriverSubsidy> lambda= Wrappers.lambdaQuery();
        lambda.eq(OrderDriverSubsidy::getOrderId,orderId);
        if(userId!=null&&userId>0) {
            lambda.eq(OrderDriverSubsidy::getUserId,userId);
        }
        if(driverType!=null&&driverType>0) {
            lambda.eq(OrderDriverSubsidy::getDriverType,driverType);
        }
        this.remove(lambda);
    }

    /**
     * 获取订单司机补贴
     * @param orderId
     * @param userId
     * @param isPayed 是否支付
     * @return
     * 聂杰伟
     */
    @Override
    public Long findOrderDriverSubSidyFee(Long orderId,Long userId,Long carDriverId,Long copilotUserId,Boolean isVer,Integer isPayed) {
        // 司机补贴-版本表  order_driver_subsidy_ver      司机补贴order_driver_subsidy
//        String tableName=isVer?"order_driver_subsidy_ver":"order_driver_subsidy";
        if (isVer==null){
            throw  new BusinessException("查询表不能为空");
        }
        Long sum = 0l;
        if (isVer){
            QueryWrapper<OrderDriverSubsidyVer> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", orderId)
                    .eq("user_id", userId)
                    .eq("user_id", carDriverId)
                    .eq("user_id", copilotUserId)
                    .eq("is_payed", isPayed);
            List<OrderDriverSubsidyVer> orderDriverSubsidies = orderDriverSubsidyVerMapper.selectList(wrapper);
            sum = orderDriverSubsidies.stream()
                    .filter(orderDriverSubsidy -> orderDriverSubsidy.getSubsidy() != null)//过滤调为空的
                    .mapToLong(OrderDriverSubsidyVer::getSubsidy)//统计 补贴的总数
                    .sum();
        }else {
            QueryWrapper<OrderDriverSubsidy> wrapper = new QueryWrapper<>();
            wrapper.eq("order_id", orderId)
                    .eq("user_id", userId)
                    .eq("user_id", carDriverId)
                    .eq("user_id", copilotUserId)
                    .eq("is_payed", isPayed);
            List<OrderDriverSubsidy> orderDriverSubsidies = baseMapper.selectList(wrapper);
             sum = orderDriverSubsidies.stream()
                    .filter(orderDriverSubsidy -> orderDriverSubsidy.getSubsidy() != null)//过滤调为空的
                    .mapToLong(OrderDriverSubsidy::getSubsidy)//统计 补贴的总数
                    .sum();
        }
        return sum;
    }
    /**
     * 获取订单司机补贴
     * @param orderId
     * @param userId
     * @param isPayed 是否支付
     * @return
     * @throws Exception
     */
    @Override
    public Long findOrderHDriverSubSidyFee(Long orderId, Long userId, Long carDriverId, Long copilotUserId, Integer isPayed) {
        QueryWrapper<OrderDriverSubsidyH> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id", orderId)
                .eq("user_id", userId)
                .eq("user_id", carDriverId)
                .eq("user_id", copilotUserId)
                .eq("is_payed", isPayed);
        List<OrderDriverSubsidyH> orderDriverSubsidies = orderDriverSubsidyHMapper.selectList(wrapper);
      Long  sum = orderDriverSubsidies.stream()
                .filter(orderDriverSubsidy -> orderDriverSubsidy.getSubsidy() != null)//过滤调为空的
                .mapToLong(OrderDriverSubsidyH::getSubsidy)//统计 补贴的总数
                .sum();
        return sum;
    }

    @Override
    public String updateDriverSubsidyPayType(Long orderId, Long userId, Integer isPayed) {
        if (orderId ==null || orderId <= 0) {
            throw new BusinessException("订单ID不存在！");
        }
        if (userId ==null || userId <= 0) {
            throw new BusinessException("司机ID不存在！");
        }
        if (isPayed ==null || isPayed < 0) {
            throw new BusinessException("支付状态不能为空！");
        }
        LambdaUpdateWrapper<OrderDriverSubsidy> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(OrderDriverSubsidy::getIsPayed, isPayed);
        updateWrapper.eq(OrderDriverSubsidy::getUserId, userId);
        updateWrapper.eq(OrderDriverSubsidy::getOrderId, orderId);
        this.update(updateWrapper);
        return "Y";
    }

    @Override
    public String[] getDriverSubsidyVer(Long orderId,Long tenantId) {
        String[] rtnArr=new String[3];
        //查找历史版本记录的补贴
        List<OrderDriverSubsidy> verList= orderDriverSubsidyService.findDriverSubsidysByOrder(orderId, null, tenantId);
        if(verList!=null) {
            StringBuffer driverSubsidyStr=new StringBuffer();
            StringBuffer colDriverSubsidyStr=new StringBuffer();
            StringBuffer updateDriverSubsidyStr=new StringBuffer();
            for (OrderDriverSubsidy orderDriverSubsidy : verList) {
                if(orderDriverSubsidy.getDriverType()==1) {
                    driverSubsidyStr.append(LocalDateTimeUtil.convertDateToStringMd(orderDriverSubsidy.getSubsidyDate())).append(" ");
                }else if(orderDriverSubsidy.getDriverType()==2) {
                    colDriverSubsidyStr.append(LocalDateTimeUtil.convertDateToStringMd(orderDriverSubsidy.getSubsidyDate())).append(" ");
                }else {
                    updateDriverSubsidyStr.append(LocalDateTimeUtil.convertDateToStringMd(orderDriverSubsidy.getSubsidyDate())).append(" ");
                }
            }
            rtnArr[0]=driverSubsidyStr.toString();
            rtnArr[1]=colDriverSubsidyStr.toString();
            rtnArr[2]=updateDriverSubsidyStr.toString();
        }
        return rtnArr;
    }
}
