package com.youming.youche.system.api.audit;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.system.domain.audit.AuditNodeUser;
import com.youming.youche.system.dto.AuditCallbackDto;
import com.youming.youche.system.dto.AuditNodePageDto;
import com.youming.youche.system.vo.AuditRuleNodeVo;
import com.youming.youche.system.vo.AuditVo;
import com.youming.youche.system.vo.SaveNodeAuditStaffVo;

import java.util.List;
import java.util.Map;

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

     /***
      * @Description: 查询审核
      * @Author: luwei
      * @Date: 2022/8/18 16:56
      * @Param Type: 审核类型
       * @Param token: 令牌
      * @return: java.util.Map<java.lang.String,java.util.List<com.youming.youche.system.dto.AuditNodePageDto>>
      * @Version: 1.0
      **/
     Map<String, List<AuditNodePageDto>> getAuditConfigList(int Type,String token)throws Exception;

    /***
     * @Description: 查询审核流程节点
     * @Author: luwei
     * @Date: 2022/8/18 16:57
     * @Param type: 审核类型
      * @Param token: 令牌
     * @return: com.youming.youche.commons.response.ResponseResult
     * @Version: 1.0
     **/
    ResponseResult queryAuditFlow(int type, String token) throws Exception;

    /***
     * @Description: 设置是否退回发起人
     * @Author: luwei
     * @Date: 2022/8/18 16:57
     * @Param flag: 审核失败是否退回发起人0是1否
      * @Param auditCode:
      * @Param token:
     * @return: void
     * @Version: 1.0
     **/
    void rollbackOriginator(Integer flag,String auditCode,String token);

    /**
     * 审核节点-审核人查询
     * @param nodeId：阶段id
     * @return
     */
    List<AuditNodeUser> getAuditNodeUserList(long nodeId) throws Exception;

    /**
     * 获取静态数据
     * @param codeType ：业务类型
     * @param codeValue ： 类型值
     * @return
     * @throws Exception
     */
    SysStaticData getSysStaticData(String codeType, String codeValue)throws Exception;

    /**
     * 审核节点新增修改
     *	isUpdate true 修改 false 新增
     * @return
     * @author qiulf
     */
    String saveOrUpdateAudit(SaveNodeAuditStaffVo saveNodeAuditStaffVo,String Token)throws Exception;

    /**
     * 审核规则
     * @param auditCode
     * @return
     * @author qiulf
     */
    Map<String,Object> getAuditRule(String auditCode,String token);

    /**
     * 保存节点规则
     * @param auditRuleNodeVo
     * @return
     * @author qiulf
     */
    String saveOrUpdateRuleNode(AuditRuleNodeVo auditRuleNodeVo,String token)throws Exception;

    /**
     * 删除节点
     * @param nodeId
     * @return
     * @author qiulf
     */
    String delAuditNode(Long nodeId,String token)throws Exception;

    /***
     * @Description: 审核操作
     * @Author: luwei
     * @Date: 2022/8/24 15:48
     * @Param auditVo: 审核入参
     * @Param token: 令牌
     * @Param isStart: 是否回调（true/false）
     * @return: com.youming.youche.system.dto.AuditCallbackDto
     * @Version: 1.0
     **/
    AuditCallbackDto sure(AuditVo auditVo, String token,Boolean isStart) throws BusinessException;


    /**
     * 审核操作
     * @param busiCode  业务编码
     * @param busiId    业务主键id
     * @param desc       审核描述
     * @param chooseResult  1 审核通过 2 审核不通过
     * @throws Exception
     *       true 有下个节点
     *       false 流程结束
     */
    AuditCallbackDto sure(String busiCode, Long busiId, String desc, Integer chooseResult,String token);


    /**
     * 审核操作
     * @param busiCode  业务编码
     * @param busiId    业务主键id
     * @param desc       审核描述
     * @param chooseResult  1 审核通过 2 审核不通过
     * @throws Exception
     *       true 有下个节点
     *       false 流程结束
     */
    AuditCallbackDto sureDataInfo(String busiCode, Long busiId, String desc, Integer chooseResult,String token);

    /**
     * 审核操作：现金打款的特殊的流程处理：
     *    在最后的一个节点的审核，调用该方法，触发审核的回调但是不修改审核的数据，还是保持当前的状态。
     *    由其他地方再调用一次这个方法，传入不同的类型，触发审核流程的真正完结，这个时候是不需要触发回调方法。
     * @param busiCode  业务编码
     * @param busiId    业务主键id
     * @param desc       审核描述
     * @param chooseResult  1 审核通过 2 审核不通过
     * @param instId 通过这个判断当前待审核的数据是否跟数据库一致
     * @param type 1:只是触发流程的回调函数 ，不对审核的数据做任何处理
     * 			   2 不触发流程的回调函数，处理审核的数据
     * @param tenantId 租户id,task没有办法通过session去取，需要传入进来
     * @throws Exception
     *       true 有下个节点
     *       false 流程结束
     */
    AuditCallbackDto sure(String busiCode, Long busiId, String desc,
                        Integer chooseResult,Long instId,Integer type,Long tenantId,String token) ;

    /**
     * 获取当前待审核的实例的主键的id
     * @param busiCode
     * @param busiId
     * @return
     * @throws Exception
     */
    public Long getInstId(String busiCode, Long busiId,String token) throws Exception;

    /**
     * 租户初始化审核节点
     * @param tenantId
     */
    public void initAuditNode(long tenantId,long rootUserId,String token);

    /***
     * @Description: 审核操作
     * @Author: luwei
     * @Date: 2022/8/24 15:53
     * @Param busiCode: 业务编码
      * @Param busiId: 业务id
      * @Param desc: 备注
      * @Param chooseResult: 审核通过标识（1审核通过其他审核不通）
      * @Param instId: 审核节点
      * @Param type:
      * @Param tennantId:
      * @Param token:
      * @Param isStart:
     * @return: com.youming.youche.system.dto.AuditCallbackDto
     * @Version: 1.0
     **/
    AuditCallbackDto surePri(String busiCode, Long busiId, String desc,
                             Integer chooseResult, Long instId, Integer type, Long tennantId, String token,Boolean isStart) throws Exception;

    /***
     * @Description: 批量结束审核流程
     * @Author: luwei
     * @Date: 2022/6/2 13:22
     * @Param auditCode: 审核编码
      * @Param desc: 审核备注
      * @Param busiId: 业务id
      * @Param user: 操作用户
     * @return: void
     * @Version: 1.0
     **/
    boolean successAuditNodeInstClose(String auditCode, String desc, Long busiId, LoginInfo user);
}
