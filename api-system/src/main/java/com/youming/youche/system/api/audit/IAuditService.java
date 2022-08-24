package com.youming.youche.system.api.audit;


import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.dto.AuditDto;
import com.youming.youche.commons.exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * 审核的流程对外提供的调用方法
 *
 * @author liyiye
 *
 */
public interface IAuditService {


    /**
     * 业务启动流程
     * @param auditCode 审核的业务编码
     * @param busiId    审核业务的主键
     * @param busiCode  操作日志的业务类型
     * @return
     *      true 表示走审核流程
     *      false 表示不需要走审核流程
     */
    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String accessToken, Long...tenantId) throws BusinessException;

    /**
     * 业务启动流程
     * @param auditCode 审核的业务编码
     * @param busiId    审核业务的主键
     * @param busiCode  操作日志的业务类型
     * @return
     *      true 表示走审核流程
     *      false 表示不需要走审核流程
     */
     boolean startProcessOrder(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, String accessToken, Long...tenantId) throws BusinessException;


    public boolean startProcess(AuditDto auditDto) throws BusinessException;

    /**
     * 判断当前的用户对传入的业务主键是否有审核权限
     * @param auditCode
     * @param busiIdList
     * @return
     *      key 对应的业务主键
     *      value true 表示有权限， false 表示没有权限
     */
    public Map<Long, Boolean> isHasPermission(String auditCode,List<Long> busiIdList);


    boolean isAuditIng(String busiCode, Long busiId,Long tenantId);



}
