package com.youming.youche.market.provider.service.facilitator;

import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.market.api.facilitator.IAccountBankUserTypeRelService;
import com.youming.youche.market.domain.facilitator.AccountBankUserTypeRel;
import com.youming.youche.market.provider.mapper.facilitator.AccountBankUserTypeRelMapper;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankByTenantIdVO;
import com.youming.youche.market.vo.maintenanceaudit.AccountBankUserTypeRelVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 银行卡跟用户类型的关系表 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-24
 */
@DubboService(version = "1.0.0")
@Service
public class AccountBankUserTypeRelServiceImpl extends BaseServiceImpl<AccountBankUserTypeRelMapper, AccountBankUserTypeRel> implements IAccountBankUserTypeRelService {
    @Resource
    private AccountBankUserTypeRelMapper accountBankUserTypeRelMapper;

    @Override
    public Boolean isUserTypeBindCard(Long userId, Integer bankType, Integer userType) {
        List<String> list = accountBankUserTypeRelMapper.isUserTypeBindCard(userId, bankType, userType);
        if (list != null && list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<AccountBankUserTypeRelVo> queryAccountBankRel(Long userId) {


        List<AccountBankUserTypeRelVo> accountBankUserTypeRelVos = accountBankUserTypeRelMapper.queryAccountBankRel(userId);

        return accountBankUserTypeRelVos;
    }

    @Override
    public AccountBankByTenantIdVO getAccountBankRel(Long tenantId) {

        List<AccountBankByTenantIdVO> accountBankRel = accountBankUserTypeRelMapper.getAccountBankRel(tenantId);
        if(accountBankRel.size() > 1){
            return accountBankRel.get(0);
        }
        return new AccountBankByTenantIdVO();
    }
}
