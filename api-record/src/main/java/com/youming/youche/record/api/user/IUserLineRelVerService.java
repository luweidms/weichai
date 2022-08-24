package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.user.UserLineRelVer;

import java.util.List;

/**
 * <p>
 * 用户心愿线路版本表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IUserLineRelVerService extends IService<UserLineRelVer> {

    /**
     * 查询用户心愿路线
     *
     * @param userId   用户id
     * @param tenantId 车队id
     * @param verState 审核状态
     * @return
     */
    public List<UserLineRelVer> getUserLineRelVer(Long userId, Long tenantId, int verState);
}
