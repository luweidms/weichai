package com.youming.youche.order.provider.service.order;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.order.api.order.IAccountBankUserTypeRelService;
import com.youming.youche.order.domain.order.AccountBankUserTypeRel;
import com.youming.youche.order.provider.mapper.order.AccountBankUserTypeRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * <p>
 * 银行卡跟用户类型的关系表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class AccountBankUserTypeRelServiceImpl extends BaseServiceImpl<AccountBankUserTypeRelMapper, AccountBankUserTypeRel> implements IAccountBankUserTypeRelService {

    @Resource
    AccountBankUserTypeRelMapper accountBankUserTypeRelMapper;

    @Override
    public Boolean isUserTypeBindCard(long userId, int bankType, int userType) {
        Integer userTypeBindCard = accountBankUserTypeRelMapper.isUserTypeBindCard(userId, bankType, userType);
        if (userTypeBindCard != null && userTypeBindCard > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean isUserTypeBindCard(long userId, int userType) {
        Integer userTypeBindCardTwo = accountBankUserTypeRelMapper.isUserTypeBindCardTwo(userId, userType);
        if (userTypeBindCardTwo != null && userTypeBindCardTwo > 0) {
            return true;
        } else {
            return false;
        }
    }
}
