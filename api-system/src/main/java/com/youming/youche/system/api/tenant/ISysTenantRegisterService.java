package com.youming.youche.system.api.tenant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.tenant.SysTenantRegister;
import com.youming.youche.system.vo.TenantRegisterQueryVo;

/**
 * <p>
 * 车队注册表，用于收集车队信息 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
public interface ISysTenantRegisterService extends IBaseService<SysTenantRegister> {

    /**
     * 根据注册信息获取新增页面的信息<br/>
     * 在审核页面跳转到新增页面时，需要调用次方法获取已填写的部分数据
     */
    SysTenantRegister getTenantByRegisterId(Long registerId);

    /***
     * @Description: 分页查询车队注册信息
     * @Author: luwei
     * @Date: 2022/1/26 5:53 下午
     * @Param tenantRegisterQueryVo:
     * @Param pageNum:
     * @Param pageSize:
     * @return: com.github.pagehelper.PageInfo<com.youming.youche.system.domain.SysTenantDef>
     * @Version: 1.0
     **/
    IPage<SysTenantRegister> queryTenantRegister(TenantRegisterQueryVo tenantRegisterQueryVo, Integer pageNum, Integer pageSize);

    /**
     * 审核
     *
     * @param id
     * @param auditState   审核状态：1-待审核 2-审核通过 3-审核不通过
     * @param auditContent 审核原因
     */
    void audit(Long id, Integer auditState, String auditContent, String accessToken);

}
