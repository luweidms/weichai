package com.youming.youche.system.business.controller.tenant;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.tenant.ISysTenantRegisterService;
import com.youming.youche.system.aspect.SysOperatorSaveLog;
import com.youming.youche.system.constant.SysOperLogConst;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.tenant.SysTenantRegister;
import com.youming.youche.system.vo.TenantRegisterQueryVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * 车队注册表，用于收集车队信息 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@RestController
@RequestMapping("/sys/tenantRegister")
public class SysTenantRegisterController  extends BaseController<SysTenantRegister, ISysTenantRegisterService> {

    @DubboReference(version = "1.0.0")
    ISysTenantRegisterService sysTenantRegisterService;

    @Override
    public ISysTenantRegisterService getService() {
        return sysTenantRegisterService;
    }

    /**
     * 查询车队注册信息列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("queryTenantRegister")
    public ResponseResult queryTenantRegister(TenantRegisterQueryVo tenantRegisterQueryVo,
                                              @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String startDate = tenantRegisterQueryVo.getStartDate();
        String endDate = tenantRegisterQueryVo.getEndDate();
        if (StringUtils.isNotEmpty(startDate)) {
            startDate = startDate + " 00:00:00";
            tenantRegisterQueryVo.setStartDate(startDate);
        }
        if (StringUtils.isNotEmpty(endDate)) {
            endDate = endDate + " 23:59:59";
            tenantRegisterQueryVo.setEndDate(endDate);
        }
        IPage<SysTenantRegister> pagination = sysTenantRegisterService.queryTenantRegister(tenantRegisterQueryVo, pageNum, pageSize);
        return ResponseResult.success(pagination);
    }

    /**
     * 获取车队信息(注册车队的获取)
     *
     * @return
     * @throws Exception
     */
    @GetMapping("getTenantByRegisterId")
    public ResponseResult getTenantByRegisterId(@RequestParam("id") Long id) {
        if (null == id || id <= 0) {
            throw new BusinessException("输入车队ID错误");
        }
        return ResponseResult.success(sysTenantRegisterService.getTenantByRegisterId(id));
    }

    /**
     * 审核车队信息
     *
     * @return
     * @throws Exception
     */
    public ResponseResult audit(@RequestParam("auditState") Integer auditState,@RequestParam("id") Long id,@RequestParam(value = "auditContent",required = false) String auditContent) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (null == id || id <= 0) {
            throw new BusinessException("输入车队ID错误");
        }
        if (SysStaticDataEnum.REGISTER_AUDIT_STATE.AUDIT_FAIL == auditState && StringUtils.isEmpty(auditContent)) {
            throw new BusinessException("审核不通过,需要填写原因!");
        }
        sysTenantRegisterService.audit(id, auditState, auditContent,accessToken);
        return ResponseResult.success();
    }




}
