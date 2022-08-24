package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youming.youche.finance.api.munual.IPayoutIntfThreeService;
import com.youming.youche.finance.dto.OverdueReceivableDto;
import com.youming.youche.order.api.order.IOverdueReceivableService;
import com.youming.youche.order.domain.order.OverdueReceivable;
import com.youming.youche.order.dto.ReceivableOverdueBalanceDto;
import com.youming.youche.order.provider.mapper.order.OverdueReceivableMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import com.youming.youche.commons.base.BaseServiceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 应收逾期账单表 服务实现类
 * </p>
 *
 * @author wuhao
 * @since 2022-07-02
 */
@Slf4j
@DubboService(version = "1.0.0")
public class OverdueReceivableServiceImpl extends BaseServiceImpl<OverdueReceivableMapper, OverdueReceivable> implements IOverdueReceivableService {

    @DubboReference(version = "1.0.0")
    IPayoutIntfThreeService payoutIntfThreeService;

    @Override
    public OverdueReceivable getBusinessCode(String businessCode) {
        LambdaQueryWrapper<OverdueReceivable> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OverdueReceivable::getBusinessNumber, businessCode);
        List<OverdueReceivable> list = this.list(lambdaQueryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new OverdueReceivable();
    }

    @Override
    public Long sumOverdueReceivable(Long tanantId, String name, Long adminUserId,Integer type) {
        List<OverdueReceivableDto> infoOuts = new ArrayList<>();
        long sumOverdueReceivable = 0;
        List<OverdueReceivableDto> lists = baseMapper.sumOverdueReceivable(tanantId, name, adminUserId,null,type);
        //判断是否逾期
        for (OverdueReceivableDto dto : lists) {
            // 处理订单应收日期
            if (dto.getType() == 1) {
                if (dto.getBalanceType() == 1) {
                    // 预付全款
                    if (StringUtils.isBlank(dto.getCreateTime())) {
                        continue;
                    }
                    dto.setReceivableDate(dto.getCreateTime());
                } else if (dto.getBalanceType() == 2) {
                    // 预付 尾款账期
                    if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                        dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                    } else {
                        continue;
                    }

                } else if (dto.getBalanceType() == 3) {
                    // 预付 尾款月结
                    if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    } else {
                        continue;
                    }

                }
            } else {
                // 处理借支应收日期
                if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                    //打款第二天算逾期
                    dto.setReceivableDate(dto.getUpdateTime());
                } else {
                    continue;
                }
            }
            // 判断是否逾期，是否继续执行操作
            if (!equalTwoTimeStr(dto.getReceivableDate())) {
                continue;
            }
            infoOuts.add(dto);
        }
        if (infoOuts != null && infoOuts.size() > 0) {
            for (OverdueReceivableDto o : infoOuts
            ) {
                //逾期总额等于应收逾期-已核销金额
                sumOverdueReceivable += (o.getTxnAmt() - o.getPaid());
            }
        }
        return sumOverdueReceivable;
    }

    @Override
    public List<ReceivableOverdueBalanceDto> accountDetails(Long tanantId,String accessToken) {
        List<ReceivableOverdueBalanceDto> infoOuts = new ArrayList<>();
        List<OverdueReceivableDto> lists = baseMapper.sumOverdueReceivable(tanantId, null, null,1,null);
        //判断是否逾期
        for (OverdueReceivableDto dto : lists) {
            ReceivableOverdueBalanceDto receivableOverdueBalanceDto = new ReceivableOverdueBalanceDto();
            // 处理订单应收日期
            if (dto.getType() == 1) {
                if (dto.getBalanceType() == 1) {
                    // 预付全款
                    if (StringUtils.isBlank(dto.getCreateTime())) {
                        continue;
                    }
                    dto.setReceivableDate(dto.getCreateTime());
                } else if (dto.getBalanceType() == 2) {
                    // 预付 尾款账期
                    if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                        dto.setReceivableDate(getAccountPeriodDateStr(dto.getUpdateTime(), dto.getCollectionTime()));
                    } else {
                        continue;
                    }

                } else if (dto.getBalanceType() == 3) {
                    // 预付 尾款月结
                    if (StringUtils.isNotBlank(dto.getCarDependDate())) {
                        dto.setReceivableDate(getMonthlyDateStr(dto.getCarDependDate(), dto.getCollectionMonth(), dto.getCollectionDay()));
                    } else {
                        continue;
                    }
                }
            } else {
                // 处理借支应收日期
                if (StringUtils.isNotBlank(dto.getUpdateTime())) {
                    //打款第二天算逾期
                    dto.setReceivableDate(dto.getUpdateTime());
                } else {
                    continue;
                }
            }
            // 判断是否逾期，是否继续执行操作
            if (!equalTwoTimeStr(dto.getReceivableDate())) {
                continue;
            }
            receivableOverdueBalanceDto.setName(dto.getName());
            //receivableOverdueBalanceDto.setUserId(dto.getUserId());
            //车队明细应收逾期
            receivableOverdueBalanceDto.setReceivableOverdueBalance(dto.getTxnAmt()-dto.getPaid());
            //应付逾期
            // 原来的获取应付应收逾期的数据已经不适用于新系统的逻辑了
           // Long payableOverdueBalance = payoutIntfThreeService.getOverdueCDSum(null, null, null, null, dto.getUserId(), accessToken);
            //receivableOverdueBalanceDto.setPayableOverdueBalance(payableOverdueBalance);

            Long overdueSum = 0L;
            try {
                overdueSum = payoutIntfThreeService.getOverdueSum(dto.getUserId(), accessToken);
            } catch (Exception e) {
                log.info("查询three Exception :{}", e.getMessage());
            }
            receivableOverdueBalanceDto.setPayableOverdueBalance(overdueSum);

            infoOuts.add(receivableOverdueBalanceDto);
        }
        return infoOuts;
    }

    @Override
    public List<ReceivableOverdueBalanceDto> accountDetailPayable(Long tenantId, String accessToken) {
        List<ReceivableOverdueBalanceDto> infoOuts = new ArrayList<>();
        List<OverdueReceivableDto> overdueReceivableDtos = baseMapper.sumOverduePayable(tenantId);
        for (OverdueReceivableDto overdueReceivableDto : overdueReceivableDtos) {
            ReceivableOverdueBalanceDto receivableOverdueBalanceDto = new ReceivableOverdueBalanceDto();
            receivableOverdueBalanceDto.setName(overdueReceivableDto.getName());

            Long overdueSum = 0L;
            try {
                overdueSum = payoutIntfThreeService.getOverdueSum(overdueReceivableDto.getUserId(), accessToken);
            } catch (Exception e) {
                log.info("查询three Exception :{}", e.getMessage());
            }
            receivableOverdueBalanceDto.setPayableOverdueBalance(overdueSum);
            receivableOverdueBalanceDto.setReceivableOverdueBalance(0L);
            infoOuts.add(receivableOverdueBalanceDto);
        }
        return infoOuts;
    }

    /**
     * 计算 预付-尾款账期 应收日期
     *
     * @param data 审核通过时间 yyyy-mm-dd
     * @param num  收款期限
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getAccountPeriodDateStr(String data, Integer num) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);
        Integer day = Integer.valueOf(split[2]);

        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month);
        cld.set(Calendar.DATE, day);

        //调用Calendar类中的add()，增加时间量
        cld.add(Calendar.DATE, num);

        return cld.get(Calendar.YEAR) + "-" + cld.get(Calendar.MONTH) + "-" + cld.get(Calendar.DATE);
    }

    /**
     * 计算 预付 尾款月结 应收日期
     *
     * @param data            订单可靠时间 yyyy-mm-dd
     * @param collectionMonth 收款月（几个月以后）
     * @param collectionDay   收款天（几个月后的几号）
     * @return 收款期限几天后的时间 yyyy-mm-dd
     */
    private String getMonthlyDateStr(String data, Integer collectionMonth, Integer collectionDay) {
        String[] split = data.split("-");

        Integer year = Integer.valueOf(split[0]);
        Integer month = Integer.valueOf(split[1]);

        Calendar cld = Calendar.getInstance();
        cld.set(Calendar.YEAR, year);
        cld.set(Calendar.MONDAY, month);
        cld.set(Calendar.DATE, collectionDay);

        //调用Calendar类中的add()，增加时间量
        cld.add(Calendar.MONDAY, collectionMonth);

        return cld.get(Calendar.YEAR) + "-" + cld.get(Calendar.MONTH) + "-" + cld.get(Calendar.DATE);
    }

    /**
     * @return true 前面的时间大于后面的，false 前面小于后面的
     */
    private Boolean equalTwoTimeStr(String dateStr) {
        if (dateStr.length() != 10) {
            String[] split = dateStr.split("-");
            dateStr = split[0];
            if (split[1].length() != 2) {
                dateStr += "-0" + split[1];
            } else {
                dateStr += "-" + split[1];
            }
            if (split[2].length() != 2) {
                dateStr += "-0" + split[2];
            } else {
                dateStr += "-" + split[2];
            }
        }
        //  System.out.println(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = sdf.format(new Date());
        int dateFlag = dateNowStr.compareTo(dateStr);
        return dateFlag > 0;
    }
}
