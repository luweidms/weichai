package com.youming.youche.record.api.user;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.user.UserSalaryInfo;

import java.util.List;

/**
 * <p>
 * 司机里程模式表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IUserSalaryInfoService extends IService<UserSalaryInfo> {

    /**
     * 自有车查询薪资信息
     *
     * @param userId        用户编号
     * @param salaryPattern 模式，1里程模式 2按趟模式
     */
    List<UserSalaryInfo> queryUserSalaryInfos(long userId, int salaryPattern);

    /**
     * 自有车查询薪资信息
     *
     * @param userId 用户编号
     */
    public List<UserSalaryInfo> getuserSalarInfo(Long userId);

}
