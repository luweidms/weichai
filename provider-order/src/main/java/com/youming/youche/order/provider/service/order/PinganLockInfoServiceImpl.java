package com.youming.youche.order.provider.service.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IPinganLockInfoService;
import com.youming.youche.order.domain.order.PinganLockInfo;
import com.youming.youche.order.dto.order.AccountBankRelDto;
import com.youming.youche.order.provider.mapper.order.PinganLockInfoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@DubboService(version = "1.0.0")
@Service
public class PinganLockInfoServiceImpl extends BaseServiceImpl<PinganLockInfoMapper, PinganLockInfo> implements IPinganLockInfoService {
    @Resource
    private PinganLockInfoMapper pinganLockInfoMapper;

    @Override
    public Long getAccountLockSum(Long userId) {
        Long accountLockSum = pinganLockInfoMapper.getAccountLockSum(userId);
        long allRow = 0;
        if(accountLockSum!=null){
            allRow = accountLockSum;
        }
        return allRow;
    }

    @Override
    public Page<AccountBankRelDto> queryLockBalanceDetails(Long userId,Integer pageNum,
                                                           Integer pageSize) {
        Page<AccountBankRelDto> page = new Page<>(pageNum, pageSize);
        Page<AccountBankRelDto> page1 = pinganLockInfoMapper.queryLockBalanceDetails(page, userId);
        return page1;
    }
}
