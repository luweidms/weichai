package com.youming.youche.system.business.controller;


import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.aspect.SysOperatorLog;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 操作日志表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@RestController
@RequestMapping("/sys/oper/log")
public class SysOperLogController extends BaseController<SysOperLog, ISysOperLogService> {

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public ISysOperLogService getService() {
        return sysOperLogService;
    }

    /**
    * 方法实现说明  查询新增员工日志
    * @author      terry
    * @param
    * @return
    * @exception
    * @date        2022/5/31 11:51
    */
    @GetMapping({"get"})
    @SysOperatorLog(code = SysOperLogConst.BusiCode.Staff, type = SysOperLogConst.OperType.Add, comment = "a1231")
    public ResponseResult get() {
        return ResponseResult.success(sysOperLogService.get(1L));
    }

    /**
     * 方法实现说明 根据业务编码查询日志记录
     * @author      terry
     * @param businessCode 业务编码
     * @param businessId  业务主键id
     * @param querySource
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 11:52
     */
    @GetMapping({"get/{businessCode}/{businessId}"})
    public ResponseResult get(@PathVariable Integer businessCode, @PathVariable Long businessId, @RequestParam(value = "querySource", defaultValue = "3") Integer querySource) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysOperLog> logs = sysOperLogService.selectAllByBusiCodeAndBusiIdAndAccessToken(businessCode, businessId, querySource, accessToken);
        return ResponseResult.success(logs);
    }

    /**
     * 查询日志公共接口  30042
     */
    @GetMapping({"querySysOperLogIntf"})
    public ResponseResult querySysOperLogIntf(Integer code, Long busiId, Boolean isAsc, String auditCode, Long auditBusiId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<SysOperLog> sysOperLogs = sysOperLogService.querySysOperLogIntf(code, busiId, isAsc, auditCode, auditBusiId, accessToken);
        return ResponseResult.success(sysOperLogs);
    }
}
