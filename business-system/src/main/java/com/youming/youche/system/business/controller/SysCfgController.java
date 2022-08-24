package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.vo.CfgVO;
import com.youming.youche.system.api.ISysCfgService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 系统配置表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@RestController
@RequestMapping("/sys/cfg")
public class SysCfgController extends BaseController<SysCfg, ISysCfgService> {

    @DubboReference(version = "1.0.0")
    ISysCfgService sysCfgService;

    @Override
    public ISysCfgService getService() {
        return sysCfgService;
    }

    /**
     * 方法实现说明 根据参数查询参数信息
     *
     * @param cfgName 参数名
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/5/31 11:07
     */
    @GetMapping({"gets/{cfgName}"})
    public ResponseResult get(@PathVariable String cfgName) {
        if (StringUtils.isEmpty(cfgName)) {
            throw new BusinessException(ResponseCode.PARAM_IS_INVALID);
        }
        SysCfg sysCfg = sysCfgService.get(cfgName);
        return ResponseResult.success(sysCfg);
    }

}
