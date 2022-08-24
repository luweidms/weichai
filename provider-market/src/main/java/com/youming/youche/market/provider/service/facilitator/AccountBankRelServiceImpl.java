package com.youming.youche.market.provider.service.facilitator;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.market.api.facilitator.IAccountBankRelService;
import com.youming.youche.market.domain.facilitator.AccountBankRel;
import com.youming.youche.market.provider.mapper.facilitator.AccountBankRelMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
@DubboService(version = "1.0.0")
@Service
public class AccountBankRelServiceImpl extends BaseServiceImpl<AccountBankRelMapper, AccountBankRel> implements IAccountBankRelService {
    @Resource
    private AccountBankRelMapper accountBankRelMapper;

    @Override
    public Boolean getBank(long userId, int bankType, int userType) {
        List<String> queryBank = accountBankRelMapper.queryBank(userId, bankType, userType);
        return queryBank !=null && queryBank.size()>0 ? true : false ;
    }


}
