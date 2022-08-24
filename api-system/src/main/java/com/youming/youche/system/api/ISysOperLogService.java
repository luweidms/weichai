package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.system.domain.SysOperLog;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
public interface ISysOperLogService extends IBaseService<SysOperLog> {

    /**
     * 方法实现说明 新增日志
     * @author      terry
     * @param sysOperLog
     * @param accessToken 令牌
     * @return      boolean
     * @exception
     * @date        2022/5/31 11:55
     */
    boolean save(SysOperLog sysOperLog, String accessToken);

    /**
     * 方法实现说明 新增日志
     * @author      terry
     * @param sysOperLog
     * @param accessToken 令牌
     * @param tenantId 车队id
     * @return      boolean
     * @exception
     * @date        2022/5/31 11:55
     */
    boolean save(SysOperLog sysOperLog, Long tenantId, String accessToken);

    /**
     * 根据传入的业务编码
     * 保存日志操作
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    boolean save(SysOperLogConst.BusiCode busiCode, Long busiId,
                 SysOperLogConst.OperType operType, String operComment, String accessToken);

    /**
     * 根据传入的业务编码
     * 保存日志操作
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    boolean save(SysOperLogConst.BusiCode busiCode, Long busiId,
                 SysOperLogConst.OperType operType, String operComment, String opName,Long opId,Long tenantId);

    /**
     * 根据传入的业务编码
     * 保存日志操作
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    boolean save(SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType,
                 String operComment, LoginInfo loginInfo);

    /**
     * 根据传入的业务编码
     * 保存日志操作
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    void saveSysOperLog(LoginInfo user, SysOperLogConst.BusiCode busiCode, Long busiId,
                       SysOperLogConst.OperType operType, String operComment, Long...tenantId);



    /**
     * 根据传入的业务编码
     * 保存日志操作
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    void saveSysOperLogSys( SysOperLogConst.BusiCode busiCode, Long busiId,
                        SysOperLogConst.OperType operType, String operComment, Long...tenantId);

    /**
     * 方法实现说明 追加待审人的日志 回单审核日志
     * @author      terry
     * @param businessCode 业务编码
     * @param businessId 业务主键
     * @param querySource
     * @param accessToken 令牌
     * @return      java.util.List<com.youming.youche.system.domain.SysOperLog>
     * @exception
     * @date        2022/5/31 11:57
     */
    List<SysOperLog> selectAllByBusiCodeAndBusiIdAndAccessToken(Integer businessCode, Long businessId,Integer querySource, String accessToken);

    /**
     * 查询日志（时间排序）
     */
    List<SysOperLog> querySysOperLogOrderByCreateDate(int code, Long busiId, Boolean isAsc, String accessToken);

    /**
     * 查询日志（时间排序）
     */
    List<SysOperLog> querySysOperLogOrderByCreateDate(SysOperLogConst.BusiCode busiCode, Long busiId, Boolean isAsc, Long tenantId);

    /**
     * 查询日志
     */
    List<SysOperLog> queryAuditRealTimeOperation(Long orderId, Long[] busiCodes, String accessToken);

    /**
     * 查询日志
     */
    List<SysOperLog> querySysOperLog(SysOperLogConst.BusiCode busiCode, Long busiId, Boolean isAsc, Long tenantId, String auditCode, Long auditBusiId);

    /**
     * 查询所有业务的日志，不区分租户,没有待审核的数据
     */
    List<SysOperLog> querySysOperLogAll(int code, Long busiId, Boolean isAsc);

    /**
     * 查询所有业务的日志，不区分租户,后面的三个参数用于查询待审核的数据，如果为空不会进行查询
     */
    List<SysOperLog> querySysOperLogAll(int code, Long busiId,
                                        Boolean isAsc, Long tenantId, String auditCode, Long auditBusiId);
    /**
     * 根据传入的业务编码
     * 保存日志操作  定时任务专用
     *
     * @param busiCode 业务编码
     * @param busiId   业务主键
     * @param operType 业务操作类型
     * @param operComment 业务描述
     */
    void saveSysOperLogTime(SysOperLogConst.BusiCode busiCode, Long busiId,
                            SysOperLogConst.OperType operType, String operComment);

    /**
     * 查询日志公共接口  30042
     */
    List<SysOperLog> querySysOperLogIntf(Integer code, Long busiId, Boolean isAsc, String auditCode, Long auditBusiId, String accessToken);



    void saveSysOperLog(LoginInfo user, SysOperLogConst.BusiCode busiCode, Long busiId, SysOperLogConst.OperType operType, String operComment, Long tenantId, Date opDate);
}
