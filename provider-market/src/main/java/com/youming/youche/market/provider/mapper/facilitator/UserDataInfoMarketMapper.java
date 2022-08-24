package com.youming.youche.market.provider.mapper.facilitator;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户资料信息Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
public interface UserDataInfoMarketMapper extends BaseMapper<UserDataInfo> {
    /**
     * 根据登陆人手机号查找用户信息（UserDataInfo）
     * @param loginAcct
     * @return
     */
    List<UserDataInfo> getUserDataInfoByLoginAcct(@Param("loginAcct") String loginAcct);


}
