package com.youming.youche.record.api.sys;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.sys.SysSmsBusiness;

import java.util.Date;

/**
 * <p>
 * 短信服务商表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface ISysSmsBusinessService extends IService<SysSmsBusiness> {

    /**
     * 短信发送业务表主键id处理策略
     * @param phoneNumber, date (手机号码、当前时间(注意:这个号码一天发送超过999条要回复的信息会超出primaryKey字符串22位))
     * @return
     */
     String getPrimaryKey(String phoneNumber, Date date,String accessToken);
}
