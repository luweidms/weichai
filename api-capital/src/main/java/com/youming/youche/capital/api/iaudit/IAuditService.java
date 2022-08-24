//package com.youming.youche.capital.api.iaudit;
//
//import com.youming.youche.commons.exception.BusinessException;
//import com.youming.youche.conts.SysOperLogConst;
//
//import java.util.List;
//import java.util.Map;
//
//public interface IAuditService {
//    /**
//     * 业务启动流程
//     * @param auditCode 审核的业务编码
//     * @param busiId    审核业务的主键
//     * @param busiCode  操作日志的业务类型
//     * @return
//     *      true 表示走审核流程
//     *      false 表示不需要走审核流程
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String accessToken, Long...tenantId) throws BusinessException;
//
//
//}
