package com.youming.youche.finance.api.payable;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.payable.CheckPasswordErr;
/**
* <p>
    *  服务类
    * </p>
* @author zag
* @since 2022-04-07
*/
    public interface ICheckPasswordErrService extends IBaseService<CheckPasswordErr> {

        /**
         * 获取密码错误次数
         * @param userId
         * @param pwdType
         * @return com.youming.youche.finance.domain.payable.CheckPasswordErr
         */
        CheckPasswordErr getCheckInfo(long userId,Integer pwdType);
    }
