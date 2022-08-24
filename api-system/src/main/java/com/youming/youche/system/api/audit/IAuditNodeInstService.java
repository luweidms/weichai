package com.youming.youche.system.api.audit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.system.domain.audit.AuditNodeInst;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核节点实例表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditNodeInstService extends IService<AuditNodeInst> {


    /**
     * 批量查询业务是否处于待审核的状态
     * @param busiCode
     * @param busiId
     * @return
     */
    public List<AuditNodeInst> queryAuditIng(String busiCode, List<Long> busiId);

    /**
     * 查询业务是否处于待审核的状态
     * @param busiCode
     * @param busiId
     * @return
     */
    public AuditNodeInst queryAuditIng(String busiCode,Long busiId,Long tenantId);
    public List<AuditNodeInst> queryAuditIngList(String busiCode,Long busiId,Long tenantId);

    /**
     * 批量查询审核完成业务
     * @param busiCode
     * @param busiId
     * @return
     */
    public List<AuditNodeInst>  queryAuditNodeInst(String busiCode,Long busiId,Long tenantId);
    /**
     *  批量查询审核完成业务
     * @author      terry
     * @param busiCode
    * @param busiId
    * @param tenantId
    * @param status {@link com.youming.youche.system.constant.AuditConsts.Status}
     * @return      java.util.List<com.youming.youche.system.domain.audit.AuditNodeInst>
     * @exception
     * @date        2022/3/22 21:54
     */
    public List<AuditNodeInst>  selectByBusiCodeAndBusiIdAndTenantIdAntStatus(String busiCode,Long busiId,Long tenantId,Integer status);


    /**
     * 判断审核的当前的操作人 是否对审核的数据有权限操作
     *
     * @param userId
     * @param orgId
     * @param rolesIds
     * @param auditNodeInstId
     * @return
     */
    public boolean isHasPermission(Long userId,Long auditNodeInstId);

    /**
     * 判断审核的当前的操作人 是否对审核的数据有权限操作
     * @param userId
     * @param orgId
     * @param rolesIds
     * @param auditNodeInst
     * @return
     */
    public boolean isHasPermission(Long userId,AuditNodeInst auditNodeInst);

    /**
     * 判断当前的用户对传入的业务主键是否有审核权限
     * @param auditCode
     * @param busiIdList
     * @return
     *      key 对应的业务主键
     *      value true 表示有权限， false 表示没有权限
     */
    public Map<Long, Boolean> isHasPermission(String auditCode, List<Long> busiIdList,String accessToken);


    /**
     * 获取当前业务的节点数
     * @param busiCode
     * @param busiId
     * @return
     */
    public Long getAuditingNodeNum(String busiCode, Long busiId);

    /**
     * 根据节点id，查询该节点审核的人的名称的拼接的字符串，用逗号隔开
     * @param nodeId
     * @return
     */
    String getAuditUserNameByNodeId(Long nodeId,Integer targetObjType) throws BusinessException;

    /**
     * 把某个业务的的具体的一个审核流程的所以实例数据更新为完成
     * @param auditId
     * @param busiId
     */
    public void updateAuditInstToFinish(Long auditId,Long busiId);


    public void delInstToVer(String busiCode, Long busiId,Long tenantId) throws BusinessException;

    /**
     * 流程结束调用的回调的方法
     * @param busiId
     * @param result
     * @param desc
     * @param paramsMap
     * @param callback
     */
    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback,String token) throws Exception;



    /**
     * 查询用户待审核的数据,默认500条数据
     * @param auditCode
     * @param userId
     * @param tenantId
     * @return
     */
    List<Long> getBusiIdByUserId(String auditCode, Long userId, Long tenantId, Integer pageSize);


    /**
     * 条件统计数据
     * */
    Integer getAuditingNodeNum(String auditCode, Long busiId, Integer status);


    /**
     * 审核记录查询（查询最新的一批次查询）
     *
     * @param auditCode
     * @param busiId
     * @param tenantId
     */
    List<Map> getAuditNodeInstNew(String auditCode, long busiId, long tenantId, boolean isDesc, Integer... state);

    /**
     * 查询业务之前是否有启动过审核流程，查询原表，历史表是否有审核的实例数据
     *
     * @param auditCode
     * @param busiId
     * @return true 表示有节点进行审核
     * false 表示没有节点进行审核
     */
    Boolean isAudited(String busiCode, Long busiId, Long tenantId);

}
