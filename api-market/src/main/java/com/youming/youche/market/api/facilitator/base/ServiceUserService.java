package com.youming.youche.market.api.facilitator.base;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.dto.facilitator.ServiceSaveInDto;
import com.youming.youche.market.dto.facilitator.ServiceUserInDto;


import java.util.HashMap;

public interface ServiceUserService {
    /**
     * 保存服务商信息
     * 入参：ServiceUserIn
     * <ul>
     * <li>loginAcct         账号（手机号）</li>
     * <li>linkman           姓名         </li>
     * <li>identification    身份证号码    </li>
     * <li>idenPicture       身份证图片    </li>
     * <li>password          登录密码，为空时随机生成6位</li>
     * </ul>
     * 出参：Map
     * <ul>
     * <li>operator       sys_operator表记录</li>
     * <li>userDataInfo   user_data_info表记录</li>
     * </ul>
     */
    HashMap<String,Object> saveServiceUser(ServiceUserInDto serviceUserInDto, LoginInfo user, ServiceSaveInDto serviceSaveIn) ;

    /**
     * 保存与新增
     * @param serviceInfo
     * @param isUpdate
     */
     void doSaveOrUpdate(ServiceInfo serviceInfo, boolean isUpdate, LoginInfo baseUser);
}
