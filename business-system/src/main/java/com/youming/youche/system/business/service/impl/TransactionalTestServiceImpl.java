package com.youming.youche.system.business.service.impl;

import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.business.service.ITransactionalTestService;
import com.youming.youche.system.domain.UserDataInfo;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

/**
 * @author : [terry]
 * @version : [v1.0]
 * @className : TransactionalTestServiceImpl
 * @description : [描述说明该类的功能]
 * @createTime : [2022/5/4 16:54]
 */

@Service
@Slf4j
public class TransactionalTestServiceImpl implements ITransactionalTestService {

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    //    @GlobalTransactional()
    @GlobalTransactional(timeoutMills = 300000)
    @Override
    public boolean handleBusiness(Long l) {

        log.info("开始全局事务，XID = " + RootContext.getXID());
        SysUser sysUser = new SysUser();
        sysUser.setName("1234");
        sysUserService.save(sysUser);
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setLinkman("1234");
        userDataInfoService.save(userDataInfo);
        if (l==1){
            throw new BusinessException("yichang ");
        }
        return true;
    }

}
