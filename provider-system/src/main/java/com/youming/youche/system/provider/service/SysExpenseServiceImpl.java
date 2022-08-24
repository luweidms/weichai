package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.ISysExpenseService;
import com.youming.youche.system.domain.SysExpense;
import com.youming.youche.system.provider.mapper.SysExpenseMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * <p>
 * 费用类型 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
@DubboService(version = "1.0.0")
public class SysExpenseServiceImpl extends BaseServiceImpl<SysExpenseMapper, SysExpense> implements ISysExpenseService {

    @Resource
    LoginUtils loginUtils;

    @Override
    public List<SysExpense> selectAll(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysExpense> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysExpense::getTenantId, loginInfo.getTenantId());
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean update(Long id, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysExpense> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysExpense::getTenantId,loginInfo.getTenantId()).eq(SysExpense::getId,id);
        SysExpense sysExpense = baseMapper.selectOne(wrapper);
        if (sysExpense.getRequired()==1){
            sysExpense.setRequired(2);
        }else {
            sysExpense.setRequired(1);
        }
        return updateById(sysExpense);
    }

    @Override
    public boolean create(SysExpense domain, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<SysExpense> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(SysExpense::getTenantId,loginInfo.getTenantId())
                .eq(SysExpense::getName,domain.getName());
        SysExpense sysExpense = baseMapper.selectOne(wrapper);
        //  在当前车队费用类型不能重复
        if (sysExpense!=null){
            if (sysExpense.getId()!=null){
                throw new BusinessException("输入的费用类型已存在,请重新输入！");
            }
        }
        domain.setOpId(loginInfo.getId()).setTenantId(loginInfo.getTenantId());
        return save(domain);
    }

    @Transactional(rollbackFor = { RuntimeException.class, Error.class })
    @Override
    public boolean createList(List<SysExpense> domains, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        for (SysExpense sysExpense : domains
        ) {
            sysExpense.setOpId(loginInfo.getId());
            save(sysExpense);
        }
        return true;
    }
}
