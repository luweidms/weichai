package com.youming.youche.system.api.audit;

import com.youming.youche.conts.SysOperLogConst;
import com.youming.youche.system.domain.audit.AuditNodeInst;
import com.youming.youche.system.domain.audit.AuditNodeOut;
import com.youming.youche.system.domain.audit.AuditNodeUser;

import java.util.List;
import java.util.Map;

/**
 * 审核的流程对外提供的调用方法
 *
 * @author liyiye
 */
public interface IAuditOutService {
//    /**
//     * 业务启动流程
//     *
//     * @param auditCode 审核的业务编码
//     * @param busiId    审核业务的主键
//     * @param busiCode  操作日志的业务类型
//     * @return true 表示走审核流程
//     * false 表示不需要走审核流程
//     */
//    public boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long... tenantId);
//
//    boolean startProcess(String auditCode, Long busiId, SysOperLogConst.BusiCode busiCode, Map<String, Object> params, Long opId, String opName, Long... tenantIds) throws Exception;

    /**
     * 判断当前的用户对传入的业务主键是否有审核权限
     *
     * @param auditCode
     * @param busiIdList
     * @return key 对应的业务主键
     * value true 表示有权限， false 表示没有权限
     */
    public Map<Long, Boolean> isHasPermission(String accessToken, String auditCode, List<Long> busiIdList);
//
//    /**
//     * 查询该节点的审核的日志
//     *
//     * @param auditCode
//     * @param busiId
//     * @param isAsc
//     * @return
//     */
//    public List<AuditNodeInst> queryAuditNodeInst(String auditCode, Long busiId, Boolean isAsc);
//
//
//    /**
//     *	查询审核
//     * @param busiType
//     * @return
//     */
////	public Map<String,List<AuditNodePage>> getAuditConfigList(int busiType)throws Exception;
//
//
//    /**
//     * 审核节点-审核人查询
//     *
//     * @param nodeId
//     * @return
//     */
//    List<AuditNodeUser> getAuditNodeUserList(long nodeId) throws Exception;
//
//    /**
//     * 审核记录查询（查询最新的一批次查询）
//     *
//     * @param auditCode
//     * @param busiId
//     * @param tenantId
//     * @return
//     */
//    List<Map> getAuditNodeInstNew(String auditCode, long busiId, long tenantId, boolean isDesc, Integer... state);
//
//
//    /**
//     * 检验是否有权限
//     *
//     * @param list
//     * @param keyId
//     * @param busiIdList
//     */
//    void isHasPermissionListMap(List<Map> list, String keyId, List<Long> busiIdList, String auditCode);
//
    /**
     * 查询用户待审核的数据,默认500条数据
     *
     * @param auditCode
     * @param userId
     * @param tenantId
     * @return
     */
    List<Long> getBusiIdByUserId(String auditCode, long userId, Long tenantId);

    /**
     * 查询用户待审核的数据
     *
     * @param auditCode
     * @param userId
     * @param tenantId
     * @param pageSize
     * @return
     */
    List<Long> getBusiIdByUserId(String auditCode, long userId, long tenantId, int pageSize);
//
//    /**
//     * 批量获取节点配置信息
//     *
//     * @param nodeIds
//     * @param tenantId
//     * @return
//     * @throws Exception
//     */
//    Map<Long, AuditNodeOut> getAuditNodeConfigListByNodeId(List<Long> nodeIds, long tenantId) throws Exception;
//
//    /**
//     * 查询业务下一节点
//     *
//     * @param nodeIds
//     * @param tenantId
//     * @return
//     * @throws Exception
//     */
//    Map<Long, AuditNodeOut> getNextAuditNodeConfig(List<Long> nodeIds, long tenantId) throws Exception;
//
    /**
     * 获取业务审核当前操作
     *
     * @param userId   操作人
     * @param busiCode 业务编码
     * @param busiIds  业务主键集合
     * @param tenantId 车队Id
     * @return
     * @throws Exception
     */
    Map<Long, Map<String, Object>> queryAuditRealTimeOperation(Long userId, String busiCode, List<Long> busiIds, long tenantId);
//
//    /**
//     * 查询审核实例表，查询待审核的数据，下个审核节点的审核人列表
//     * 如果不是待审核，返回空的列表
//     *
//     * @param auditCode
//     * @param tenantId  如果为空，取当前登录用户的租户id
//     * @return 没有数据为 null
//     * 有审核人返回：人:xxx,xxxx,xxx
//     */
//    String getAuditNextNodeUsers(String auditCode, Long busiId, Long tenantId) throws Exception;

    /**
     * 取消启动的流程
     *
     * @param auditCode
     * @param busiId
     * @param tenantId
     * @throws Exception
     */
    void cancelProcess(String auditCode, Long busiId, Long tenantId);

    /**
     * 查询用户待审核的数据,默认500条数据
     *
     * @param auditCode
     * @param userId
     * @param tenantId
     * @return
     */
   // List<Long> getBusiIdByUserId(String auditCode, long userId, long tenantId, String startTime, String endTime);
}
