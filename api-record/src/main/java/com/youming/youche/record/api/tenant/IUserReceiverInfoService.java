package com.youming.youche.record.api.tenant;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.record.domain.tenant.TenantReceiverRel;
import com.youming.youche.record.domain.user.UserReceiverInfo;
import com.youming.youche.record.dto.tenant.TenantReceiverRelDto;
import com.youming.youche.record.vo.tenant.TenantReceiverRelVo;

public interface IUserReceiverInfoService extends IBaseService<UserReceiverInfo> {

    /**
     * 查询收款人管理列表信息
     *
     * @param pageNum             分页参数
     * @param pageSize            分页参数
     * @param tenantReceiverRelVo 首款人列表查询条件
     */
    Page<TenantReceiverRelDto> queryAll(Page<TenantReceiverRelDto> page, String accessToken, TenantReceiverRelVo tenantReceiverRelVo);

    /**
     * 根据id查询收款人信息
     *
     * @param relId 收款人主键
     * @return 收款人信息
     */
    TenantReceiverRel contractById(Long relId);

    /**
     * 收款人管理新增
     *
     * @param tenantReceiverRel 新增收款人信息
     */
    boolean tenantAdd(TenantReceiverRel tenantReceiverRel, String accessToken) throws Exception;

    /**
     * 修改收款人信息
     *
     * @param tenantReceiverRel 车队与收款人的关联
     */
    boolean tenantUpdate(TenantReceiverRel tenantReceiverRel, String accessToken);

    /**
     * 根据id删除收款人信息
     *
     * @param relid 主键id
     */
    boolean deleteTeantReceiverById(Long relid);

    /**
     * 根据手机号查询 收款人
     *
     * @param mobilePhone 手机号
     * @return 收款人信息
     */
    TenantReceiverRel checkUserReceiver(String mobilePhone, String accessToken);

    /**
     * 招商挂靠车，账单接收人在系统不存在，则在系统生成一个收款人
     *
     * @param billReceiverMobile
     * @param billReceiverName
     * @param token
     * @return
     */
    UserReceiverInfo initUserReceiverInfo(String billReceiverMobile, String billReceiverName, String token);

    UserReceiverInfo initUserReceiverInfo(String billReceiverMobile, String billReceiverName, LoginInfo loginInfo);

    /**
     * 是否为收款人
     *
     * @param userId
     * @param tenantId
     * @return
     */
    Boolean isReceiver(Long userId, Long tenantId);

    /**
     * 获取收款人信息
     *
     * @param userId 用户编号
     */
    UserReceiverInfo getUserReceiverInfoByUserId(long userId);

    /**
     * 修改收款人信息
     *
     * @param userReceiverInfo
     */
    void updateUserReceiverInfo(UserReceiverInfo userReceiverInfo);

    /**
     * 通过主键获取收款人信息
     */
    UserReceiverInfo getUserReceiverInfoById(Long receiverId);

    /**
     * 保存收款人信息
     */
    UserReceiverInfo saveReturnId(UserReceiverInfo userReceiverInfo);

    /**
     * 接口编号：10062
     * 查询代收人信息
     * 接口入参：
     * phone  手机号码
     * <p>
     * 接口出参：
     */
    UserReceiverInfo getUserReceiverInfo(String phone, String accessToken);

    /**
     * 新增收款人
     *
     * @param userId      用户id
     * @param tenantId    车队id
     * @param accessToken
     */
    void createTenantReceiverInfo(Long userId, Long tenantId, String accessToken);
}
