package com.youming.youche.record.api.audit;

import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.record.vo.audit.AuditVo;

/**
 * @version:
 * @Title: IAuditSettingService 审核的流程
 * @Package: com.youming.youche.system.api.audit
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/10 9:49
 * @company:
 */
public interface IAuditSettingService {

    /**
     * 获取静态数据
     * @param codeType
     * @param codeValue
     * @return
     * @throws Exception
     */
    SysStaticData getSysStaticData(String codeType, String codeValue)throws Exception;


    /**
     * 审核操作
     * @param busiCode  业务编码
     * @param busiId    业务主键id
     * @param desc       审核描述
     * @param chooseResult  1 审核通过 2 审核不通过
     * @param instId 通过这个判断当前待审核的数据是否跟数据库一致
     * @throws Exception
     *       true 有下个节点
     *       false 流程结束
     */
    boolean sure(AuditVo auditVo, String token) throws Exception;

    /**
     * 获取当前待审核的实例的主键的id
     * @param busiCode
     * @param busiId
     * @return
     * @throws Exception
     */
    public Long getInstId(String busiCode, Long busiId,String token) throws Exception;


}
