package com.youming.youche.capital.api.iaudit;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.capital.domain.audit.AuditNodeInst;

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
     * @param auditCode
     * @param busiId
     * @return
     */
    public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId);


    /**
     * 判断审核的当前的操作人 是否对审核的数据有权限操作
     * @param userId
     * @param orgId
     * @param rolesIds
     * @param auditNodeInst
     * @return
     */
    public boolean isHasPermission(Long userId, AuditNodeInst auditNodeInst);


    /**
     * 判断审核的当前的操作人 是否对审核的数据有权限操作
     *
     * @param userId
     * @param orgId
     * @param rolesIds
     * @param auditNodeInstId
     * @return
     */
    public boolean isHasPermission(Long userId, Long auditNodeInstId);

    /**
     * 判断当前的用户对传入的业务主键是否有审核权限
     * @param auditCode
     * @param busiIdList
     * @return
     *      key 对应的业务主键
     *      value true 表示有权限， false 表示没有权限
     */
    public Map<Long, Boolean> isHasPermission(String auditCode, List<Long> busiIdList, String accessToken);


    /**
     * 查询用户待审核的数据
     * @param nodeId
     * @param targetObjType
     * @return
     */
    List<Long> getUserFromAuditUser(Long nodeId, Integer targetObjType);

    /**
     * 轮到某个节点审核的时候，进行回调进行业务处理
     * @param nodeIndex   当前的审核节点处于流程的第几个节点
     * @param busiId 业务主键id
     * @param targetObjectType  see {@link}com.business.consts.AuditConsts.TargetObjType
     * @param targetObjId     对应的目标的值
     * @param callBack      回调的实现类
     */
    void nodeAuditCallback(int nodeIndex, long busiId, int targetObjectType, String targetObjId, String callBack)  throws Exception;



    /**
     * 获取当前业务的节点数
     * @param busiCode
     * @param busiId
     * @return
     */
    public Long getAuditingNodeNum(String busiCode, Long busiId);


    /**
     * 把某个业务的的具体的一个审核流程的所以实例数据更新为完成
     * @param auditId
     * @param busiId
     */
    public void updateAuditInstToFinish(Long auditId, Long busiId);

    public void delInstToVer(String busiCode, Long busiId, Long tenantId) throws Exception;

    /**
     * 流程结束调用的回调的方法
     * @param busiId
     * @param result
     * @param desc
     * @param paramsMap
     * @param callback
     */
    public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token)  throws Exception;


    /**
     * 根据节点id，查询该节点审核的人的名称的拼接的字符串，用逗号隔开
     * @param nodeId
     * @return
     */
    String getAuditUserNameByNodeId(Long nodeId, Integer targetObjType) throws Exception;
}
