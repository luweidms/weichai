package com.youming.youche.system.api.back;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.back.Backlist;
import com.youming.youche.system.vo.QueryBacklistParamVo;

import java.util.List;

/**
 * <p>
 * 黑名单 服务类
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
public interface IBacklistService extends IBaseService<Backlist> {

    /**
     * 小程序查询当前车队能看到的所有黑名单
     */
    Page<Backlist> query(String queryParam, Integer backType, String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 完整匹配查询
     */
    List<Backlist> query(String driverName, String identification, String driverPhone, long tenantId, Integer state);

    /**
     * 接口编号：14516
     * 发布黑名单
     */
    void save(Backlist backlist, String accessToken);

    /**
     * 接口编号：14518
     * 黑名单-查询司机信息，根据司机姓名查询司机信息
     * <p>
     * 司机列表查询
     * <p>
     * 入参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * state 认证状态
     * <p>
     * 出参：loginAcct 账号
     * linkman 姓名
     * carUserType 司机类型
     * carUserTypeName 司机类型名称
     * attachTenantId 归属车队id
     * attachTenantName 归属车队名称
     * attachTennantLinkman 车队联系人
     * attachTennantLinkPhone 车队联系人电话
     * vehicleNum 车辆数量
     * state 认证状态
     * stateName 认证状态名称
     * hasVer 审核状态
     * userId 用户编号
     * createDate 创建时间
     */
    Page doQueryCarDriver(String loginAcct, String linkman, Integer carUserType,
                          String attachTenantName, String attachTennantLinkman,
                          String attachTennantLinkPhone, Integer state,
                          String accessToken, Integer pageNum, Integer pageSize);

    /**
     * 接口编号：14515
     * 查询订单：判断一个司机一个月内与当前车队是否有合作订单
     */
    Boolean haveOrderLastMonth(Long userId, String accessToken);

    /**
     * 接口编号：14519
     * 检查黑名单
     */
    Page<Backlist> checkBacklist(QueryBacklistParamVo vo, String accessToken, Integer pageNum, Integer pageSize);

}
