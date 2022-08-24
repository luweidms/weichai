package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.api.IAccountStatementOrderInfoService;
import com.youming.youche.finance.domain.AccountStatementOrderInfo;
import com.youming.youche.finance.provider.mapper.AccountStatementOrderInfoMapper;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-06-27
*/
@DubboService(version = "1.0.0")
public class AccountStatementOrderInfoServiceImpl extends BaseServiceImpl<AccountStatementOrderInfoMapper, AccountStatementOrderInfo> implements IAccountStatementOrderInfoService {


    @Override
    public void deleteAccountStatementOrder(Long accountStatementId) {
        baseMapper.deleteAccountStatementOrder(accountStatementId);
    }

    @Override
    public List<AccountStatementOrderInfo> getAccountStatementOrder(Long accountStatementId) {
        LambdaQueryWrapper<AccountStatementOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatementOrderInfo::getAccountStatementId, accountStatementId);

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Page<AccountStatementOrderInfo> getAccountStatementOrderPage(Long accountStatementId, QueryAccountStatementOrdersVo vo, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<AccountStatementOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatementOrderInfo::getAccountStatementId, accountStatementId);

        if (vo.getOrderId() != null && vo.getOrderId() > 0) {
            queryWrapper.eq(AccountStatementOrderInfo::getOrderId, vo.getOrderId());
        }

        if (StringUtils.isNotEmpty(vo.getDependStartTime())) {
            queryWrapper.ge(AccountStatementOrderInfo::getDependTime, vo.getDependStartTime());
        }

        if (StringUtils.isNotEmpty(vo.getDependEndTime())) {
            queryWrapper.le(AccountStatementOrderInfo::getDependTime, vo.getDependEndTime());
        }

        if (vo.getOrderState() != null) {
            queryWrapper.eq(AccountStatementOrderInfo::getOrderState, vo.getOrderState());
        }

        if (StringUtils.isNotEmpty(vo.getCompanyName())) {
            queryWrapper.like(AccountStatementOrderInfo::getCompanyName, vo.getCompanyName());
        }

        if (StringUtils.isNotEmpty(vo.getSourceName())) {
            queryWrapper.like(AccountStatementOrderInfo::getSourceName, vo.getSourceName());
        }

        if (StringUtils.isNotEmpty(vo.getCarDriverMan())) {
            queryWrapper.like(AccountStatementOrderInfo::getCarDriverMan, vo.getCarDriverMan());
        }

        if (StringUtils.isNotEmpty(vo.getCarDriverPhone())) {
            queryWrapper.like(AccountStatementOrderInfo::getCarDriverPhone, vo.getCarDriverPhone());
        }

        if (StringUtils.isNotEmpty(vo.getPayee())) {
            queryWrapper.like(AccountStatementOrderInfo::getPayee, vo.getPayee());
        }

        if (StringUtils.isNotEmpty(vo.getPayeePhone())) {
            queryWrapper.like(AccountStatementOrderInfo::getPayeePhone, vo.getPayeePhone());
        }

        if (vo.getFinalState() != null) {
            queryWrapper.eq(AccountStatementOrderInfo::getFinalSts, vo.getFinalState());
        }

        if (CollectionUtils.isNotEmpty(vo.getOrderStateList())) {
            queryWrapper.in(AccountStatementOrderInfo::getOrderState, vo.getOrderStateList());
        }

        Page<AccountStatementOrderInfo> page = new Page<>(pageNum, pageSize);
        return baseMapper.selectPage(page, queryWrapper);
    }
}
