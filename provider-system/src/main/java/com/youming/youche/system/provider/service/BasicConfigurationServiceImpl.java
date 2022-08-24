package com.youming.youche.system.provider.service;

import cn.hutool.core.util.StrUtil;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.system.api.IBasicConfigurationService;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luwei
 */
@DubboService(version = "1.0.0")
@Service
public class BasicConfigurationServiceImpl implements IBasicConfigurationService {

    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Override
    public String doQuery(String codetype,String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        String key = codetype + "_" + loginInfo.getTenantId();
        String value = (String)redisUtil.get(key);
        if(StrUtil.isEmpty(value) && "PAYMANAGER_TYPE".equals(codetype)){
            doSave("PAYMANAGER_TYPE","固定成本-办公费,固定成本-租房费,费用-招待费,费用-装卸费",token);
            value = (String)redisUtil.get(key);
        }
        return value;
    }

    @Override
    public void doSave(String codetype, String basicType,String token) {
        LoginInfo loginInfo = loginUtils.get(token);
        String key = codetype + "_" + loginInfo.getTenantId();
        redisUtil.del(key);
        redisUtil.set(key,basicType);

    }

    @Override
    public String getBasicCodeName(String basicType,String codeName,String token){
        if(StringUtils.isBlank(codeName)){
            return null;
        }
        String basicName = doQuery(basicType,token);
        if(StringUtils.isNotEmpty(basicName)){
            String [] basicNames = basicName.split(",");
            for(String name:basicNames){
                if(StringUtils.isNotEmpty(name)){
                    String[] codeNames = name.split(":");
                    name = codeNames[0];
                    if(codeName.equals(name) && codeNames.length > 1){
                        return codeNames[1];
                    }

                }
            }
        }
        return null;
    }

}
