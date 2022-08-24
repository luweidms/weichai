package com.youming.youche.system.api.audit;


import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.system.domain.audit.AuditUser;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 节点审核人 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
public interface IAuditUserService extends IService<AuditUser> {

    /**
     * 删除某个节点的审核用户的信息
     * 删除的节点的用户审核的信息移动到ver表
     *
     * @param nodeId
     */
    void delAuditUserByNodeId(Long nodeId) throws Exception;


    /**
     * 批量保存节点审核的用户信息
     *
     * @param nodeId        节点主键
     * @param targetObjIds  用户信息，以逗号隔开
     * @param targetObjType 类型
     * @param version       节点的版本号
     */
    void batchSaveAuditUser(Long nodeId, String targetObjIds, Integer targetObjType, Long version);

    /**
     * 批量保存节点审核的用户信息
     *
     * @param nodeId          节点主键
     * @param targetObjIdList 用户信息
     * @param targetObjType   类型
     * @param version         节点的版本号
     */
    void batchSaveAuditUser(Long nodeId, List<Long> targetObjIdList, Integer targetObjType, Long version);

    AuditUser getAuditUser(Long nodeId, Long targetObjId, Integer targetObjType);

    /**
     * 查询用户待审核的数据id
     * */
    List<Long> selectIdByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType);
    /**
     * 查询用户待审核的数据id
     *
     * @return*/
    Map<Long, List<Long>> selectIdByNodeIdAndTarGetObjType(List<Long> nodeIds, Integer targetObjType);
    /**
     * 查询用户待审核的数据TargetObjId
     * */
    List<Long> selectTargetObjIdByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType);
    List<AuditUser> selectByNodeIdAndTarGetObjType(Long nodeId, Integer targetObjType);

    /**
     *  查询待审核列表
     * */
    List<AuditUser> getAuditUserList(Long nodeId, Integer targetObjType, Long targetObjId);

}
