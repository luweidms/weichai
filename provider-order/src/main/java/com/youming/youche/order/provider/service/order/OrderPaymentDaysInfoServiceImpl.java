package com.youming.youche.order.provider.service.order;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoService;
import com.youming.youche.order.api.order.IOrderPaymentDaysInfoVerService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfo;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoH;
import com.youming.youche.order.domain.order.OrderPaymentDaysInfoVer;
import com.youming.youche.order.dto.UpdateOrderPaymentDaysInfoInDto;
import com.youming.youche.order.provider.mapper.order.OrderPaymentDaysInfoMapper;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 订单收入账期表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-17
 */
@DubboService(version = "1.0.0")
@Service
public class OrderPaymentDaysInfoServiceImpl extends BaseServiceImpl<OrderPaymentDaysInfoMapper, OrderPaymentDaysInfo> implements IOrderPaymentDaysInfoService {
    @Autowired
    private IOrderPaymentDaysInfoVerService orderPaymentDaysInfoVerService;

    @Override
    public OrderPaymentDaysInfo queryOrderPaymentDaysInfo(Long orderId, Integer paymentDaysType) {
        LambdaQueryWrapper<OrderPaymentDaysInfo> lambda = new QueryWrapper<OrderPaymentDaysInfo>().lambda();
        if (orderId == null || orderId <= 0) {
            throw new BusinessException("订单号为空！请联系客服！");
        }
        if (paymentDaysType == null || paymentDaysType <= 0) {
            throw new BusinessException("账期类型为空！请联系客服！");
        }
        lambda.eq(OrderPaymentDaysInfo::getOrderId, orderId)
                .eq(OrderPaymentDaysInfo::getPaymentDaysType, paymentDaysType);
        lambda.orderByAsc(OrderPaymentDaysInfo::getId);

        List<OrderPaymentDaysInfo> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new OrderPaymentDaysInfo();
    }

    @Override
    public void saveOrUpdateOrderPaymentDaysInfo(OrderPaymentDaysInfo info) {
        OrderPaymentDaysInfo paymentDaysInfo = this.queryOrderPaymentDaysInfo(info.getOrderId(), info.getPaymentDaysType());
        if (paymentDaysInfo != null && paymentDaysInfo.getOrderId() != null) {
            UpdateOrderPaymentDaysInfoInDto in = new UpdateOrderPaymentDaysInfoInDto();
            BeanUtils.copyProperties(info, in);
            BeanUtils.copyProperties(in, paymentDaysInfo);
            this.saveOrUpdate(paymentDaysInfo);
        } else {
            this.saveOrUpdate(info);
        }
    }

    @Override
    public List<OrderPaymentDaysInfo> getOrderPaymentDaysInfo(Long orderId, Long tenantId) {
        LambdaQueryWrapper<OrderPaymentDaysInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderPaymentDaysInfo::getOrderId, orderId)
                .eq(OrderPaymentDaysInfo::getTenantId, tenantId);

        return baseMapper.selectList(wrapper);
    }

    @Override
    public void setOrderPaymentDaysInfo(OrderPaymentDaysInfo oldObj, OrderPaymentDaysInfoVer newObj) {
        UpdateOrderPaymentDaysInfoInDto in = new UpdateOrderPaymentDaysInfoInDto();
        BeanUtils.copyProperties(newObj, in);
        BeanUtils.copyProperties(in, oldObj);
        newObj.setIsUpdate(OrderConsts.IS_UPDATE.NOT_UPDATE);
        orderPaymentDaysInfoVerService.saveOrUpdateOrderPaymentDaysInfoVer(newObj);
        this.saveOrUpdateOrderPaymentDaysInfo(oldObj);
    }

    @Override
    public Integer calculatePaymentDays(OrderPaymentDaysInfo info, LocalDateTime dependDate, Integer calculateType) {
        try {
            if (info != null && info.getBalanceType() != null && calculateType != null) {
                if (calculateType == OrderConsts.CALCULATE_TYPE.COLLECTION) {
                    if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                            || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                            && info.getCollectionTime() != null) {
                        return info.getCollectionTime();
                    } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                            && info.getCollectionMonth() != null && info.getCollectionDay() != null) {
//
//                    LocalDateTime dateAdd = dependDate.plusMonths(info.getCollectionMonth());
//                    Date dateEndDate = null;
//                    try {
//                        dateEndDate = DateUtil.formatStringToDate(DateUtil.formatDate(DateUtil.asDate(dateAdd), "yyyy-MM-" + (info.getCollectionDay() > 9 ? info.getCollectionDay() : "0" + info.getCollectionDay()) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    if (dateEndDate != null) {
//                        return DateUtil.diffDate(dateEndDate, DateUtil.asDate(dateAdd));
//                    }
                        Date dateAdd = DateUtil.addMonth(Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()), info.getCollectionMonth());
                        Date dateEnd = DateUtil.formatStringToDate(DateUtil.formatDate(dateAdd, "yyyy-MM-" + (info.getCollectionDay() > 8 ? (info.getCollectionDay()+1) : "0" + (info.getCollectionDay()+1)) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
                        return DateUtil.diffDate(dateEnd, Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        return null;
                    }
                } else if (calculateType == OrderConsts.CALCULATE_TYPE.INVOICE) {
                    if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                            || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                            && info.getInvoiceTime() != null) {
                        return info.getInvoiceTime();
                    } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                            && info.getInvoiceMonth() != null && info.getInvoiceDay() != null) {
//                    LocalDateTime dateAdd = dependDate.plusMonths(info.getInvoiceMonth());
//                    Date dateEndDate = null;
//                    try {
//                        dateEndDate = DateUtil.formatStringToDate(DateUtil.formatDate(DateUtil.asDate(dateAdd), "yyyy-MM-" + (info.getInvoiceDay() > 9 ? info.getInvoiceDay() : "0" + info.getInvoiceDay()) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    return DateUtil.diffDate(dateEndDate, DateUtil.asDate(dependDate));
                        Date dateAdd = DateUtil.addMonth(Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()), info.getInvoiceMonth());
                        Date dateEnd = DateUtil.formatStringToDate(DateUtil.formatDate(dateAdd, "yyyy-MM-" + (info.getInvoiceDay() > 8 ? (info.getInvoiceDay()+1) : "0" + (info.getInvoiceDay()+1)) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
                        return DateUtil.diffDate(dateEnd, Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        return null;
                    }
                } else if (calculateType == OrderConsts.CALCULATE_TYPE.RECIVE) {
                    if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                            || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                            && info.getReciveTime() != null) {
                        return info.getReciveTime();
                    } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                            && info.getReciveMonth() != null && info.getReciveDay() != null) {
//                        LocalDateTime dateAdd = dependDate.plusMonths(info.getReciveMonth());
//                        Date dateEndDate = null;
//                        try {
//                            dateEndDate = DateUtil.formatStringToDate(DateUtil.formatDate(DateUtil.asDate(dateAdd), "yyyy-MM-" + (info.getReciveDay() > 9 ? info.getReciveDay() : "0" + info.getReciveDay()) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        return DateUtil.diffDate(dateEndDate, DateUtil.asDate(dependDate));
                        Date dateAdd = DateUtil.addMonth(Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()), info.getReciveMonth());
                        Date dateEnd = DateUtil.formatStringToDate(DateUtil.formatDate(dateAdd, "yyyy-MM-" + (info.getReciveDay() > 8 ? (info.getReciveDay()+1) : "0" + (info.getReciveDay()+1)) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
                        return DateUtil.diffDate(dateEnd, Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        return null;
                    }
                } else if (calculateType == OrderConsts.CALCULATE_TYPE.RECONCILIATION) {
                    if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                            || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                            && info.getReconciliationTime() != null) {
                        return info.getReconciliationTime();
                    } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                            && info.getReconciliationMonth() != null && info.getReconciliationDay() != null) {
//                        LocalDateTime dateAdd = dependDate.plusMonths(info.getReconciliationMonth());
//                        Date dateEndDate = null;
//                        try {
//                            dateEndDate = DateUtil.formatStringToDate(DateUtil.formatDate(DateUtil.asDate(dateAdd), "yyyy-MM-" + (info.getReconciliationDay() > 9 ? info.getReconciliationDay() : "0" + info.getReconciliationDay()) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//
//                        return DateUtil.diffDate(dateEndDate, DateUtil.asDate(dependDate));
                        Date dateAdd = DateUtil.addMonth(Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()), info.getReconciliationMonth());
                        Date dateEnd = DateUtil.formatStringToDate(DateUtil.formatDate(dateAdd, "yyyy-MM-" + (info.getReconciliationDay() > 8 ? (info.getReconciliationDay()+1) : "0" + (info.getReconciliationDay()+1)) + "  HH:mm:ss"), DateUtil.DATETIME_FORMAT);
                        return DateUtil.diffDate(dateEnd, Date.from(dependDate.atZone(ZoneId.systemDefault()).toInstant()));
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer calculatePaymentDaysH(OrderPaymentDaysInfoH info, Date dependDate, Integer calculateType) {
        if (info != null && info.getBalanceType() != null && calculateType != null) {
            if (calculateType == OrderConsts.CALCULATE_TYPE.COLLECTION) {
                if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                        || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                        && info.getCollectionTime() != null) {
                    return info.getCollectionTime();
                } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                        && info.getCollectionMonth() != null && info.getCollectionDay() != null) {
                    Date dateAdd = DateUtil.addMonth(dependDate, info.getCollectionMonth());
                    Date dateEnd = DateUtil.addDate(dateAdd, info.getCollectionDay());
                    return DateUtil.diffDate(dateEnd, dependDate);
                } else {
                    return null;
                }
            } else if (calculateType == OrderConsts.CALCULATE_TYPE.INVOICE) {
                if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                        || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                        && info.getInvoiceTime() != null) {
                    return info.getInvoiceTime();
                } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                        && info.getInvoiceMonth() != null && info.getInvoiceDay() != null) {
                    Date dateAdd = DateUtil.addMonth(dependDate, info.getInvoiceMonth());
                    Date dateEnd = DateUtil.addDate(dateAdd, info.getInvoiceDay());
                    return DateUtil.diffDate(dateEnd, dependDate);
                } else {
                    return null;
                }
            } else if (calculateType == OrderConsts.CALCULATE_TYPE.RECIVE) {
                if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                        || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                        && info.getReciveTime() != null) {
                    return info.getReciveTime();
                } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                        && info.getReciveMonth() != null && info.getReciveDay() != null) {
                    Date dateAdd = DateUtil.addMonth(dependDate, info.getReciveMonth());
                    Date dateEnd = DateUtil.addDate(dateAdd, info.getReciveDay());
                    return DateUtil.diffDate(dateEnd, dependDate);
                } else {
                    return null;
                }
            } else if (calculateType == OrderConsts.CALCULATE_TYPE.RECONCILIATION) {
                if ((info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH
                        || info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_ALL)
                        && info.getReconciliationTime() != null) {
                    return info.getReconciliationTime();
                } else if (info.getBalanceType().intValue() == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER
                        && info.getReconciliationMonth() != null && info.getReconciliationDay() != null) {
                    Date dateAdd = DateUtil.addMonth(dependDate, info.getReconciliationMonth());
                    Date dateEnd = DateUtil.addDate(dateAdd, info.getReconciliationDay());
                    return DateUtil.diffDate(dateEnd, dependDate);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
