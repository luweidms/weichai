//package com.youming.youche.record.business.controller.sys;
//
//import com.youming.youche.commons.base.BaseController;
//import com.youming.youche.commons.response.ResponseResult;
//import com.youming.youche.commons.web.Header;
//import com.youming.youche.record.domain.sys.SysOperLog;
//import com.youming.youche.system.api.ISysOperLogService;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
///**
// * @Date:2022/1/6
// */
//@RestController
//@RequestMapping("sys/oper/log")
//public class SysOperLogController extends BaseController<SysOperLog, ISysOperLogService> {
//
//    @DubboReference(version = "1.0.0")
//    private ISysOperLogService iSysOperLogService;
//
//    @Override
//    public ISysOperLogService getService() {
//        return iSysOperLogService;
//    }
//
//    /**
//     * 实现功能: 查询日志
//     * @param code 业务编码
//     * @param busiId   业务主键
//     * @param auditCode 业务的审核编码
//     * @param auditBusiId 审核业务的busiId
//     * @return
//     *
//     */
//    @GetMapping("querySysOperLog")
//    public ResponseResult querySysOperLog(@RequestParam("code")Integer code,
//                                          @RequestParam("busiId")Long busiId,
//                                          @RequestParam("auditCode")String auditCode,
//                                          @RequestParam("auditBusiId")Long auditBusiId){
//        try {
//            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            List<SysOperLog> sysOperLogList = iSysOperLogService.querySysOperLog(code, busiId, auditCode, auditBusiId, accessToken);
//            return ResponseResult.success(sysOperLogList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseResult.failure("查询异常");
//        }
//    }
//}
