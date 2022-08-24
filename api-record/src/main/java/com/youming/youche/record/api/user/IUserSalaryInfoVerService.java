package com.youming.youche.record.api.user;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;

import java.util.List;

/**
 * <p>
 * 司机里程模式版本表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IUserSalaryInfoVerService extends IService<UserSalaryInfoVer> {

    /**
     * 自有车查询薪资信息
     *
     * @param userId 用户编号
     */
    public List<UserSalaryInfoVer> getUserSalaryInfoVers(long userId) throws Exception;

    /**
     * 自有车查询薪资信息
     *
     * @param userId   用户编号
     * @param verState 审核数据状态
     */
    public List<UserSalaryInfoVer> getUserSalaryInfoVer(Long userId, int verState);
}
