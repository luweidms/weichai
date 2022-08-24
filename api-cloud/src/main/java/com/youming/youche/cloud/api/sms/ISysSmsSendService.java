package com.youming.youche.cloud.api.sms;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.domin.sms.MsgNotifySetting;
import com.youming.youche.cloud.dto.sys.SysSmsSendDto;
import com.youming.youche.cloud.dto.sys.SystemNotifyDto;
import com.youming.youche.cloud.dto.sys.UnReadCountDto;
import com.youming.youche.cloud.vo.sys.MessageFlagDto;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import javafx.scene.control.Pagination;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */

public interface ISysSmsSendService extends IBaseService<SysSmsSend> {

    /***
     * @Description: 短信发送存储
     * @Author: luwei
     * @Date: 2022/1/14 3:02 下午
     * @Param billId:
     * @Param templateId: 短信模版
     * @Param paraMap: 参数
     * @return: void
     * @Version: 1.0
     **/
    void sendSms(SysSmsSend sysSmsSend);

    /***
     * @Description: 配置短信通知查询
     * @Author: luwei
     * @Date: 2022/1/22 1:14 下午
     * @Param tenantId:
     * @return: com.youming.youche.cloud.domin.sms.MsgNotifySetting
     * @Version: 1.0
     **/
    MsgNotifySetting getMsgNotifySettingByTenantId(Long tenantId);

    /***
     * @Description: 短信通知
     * @Author: luwei
     * @Date: 2022/1/22 1:33 下午
     * @Param msgNotifySetting:
     * @return: java.lang.Integer
     * @Version: 1.0
     **/
    Boolean updateMsgNotifySetting(MsgNotifySetting msgNotifySetting);

    void sendSms(String billId, long templateId, long smsType, long objType, String objId, Map paraMap,String accessToken);

    void sendSms(String billId, long templateId, long smsType, String objType, String objId, Map paraMap,String accessToken);

    public void sendWxSms(String billId,long templateId,Map paraMap,String accessToken);

    /**
     * 接口编号：10039
     * 接口入参：
     * billId        手机号码
     * userId        用户编号
     * smsType       1 订单助手  2邀请消息  3进度通知    4 系统提醒
     * objectType	业务类型 :1：修改消息为已读状态2：删除短信
     * <p>
     * 消息中心修改短信消息状态
     */
    void updateMessageFlagAll(Long userId, String billId, Integer smsType, Integer objectType, String accessToken);

    Page<SysSmsSend> getSmsList(String billId, long userId, int smsType, Integer pageNum, Integer pageSize);

    /**
     * 接口编号：11008
     * 接口入参：
     * billId        手机号码
     * userId        用户编号
     * smsType       1 订单助手  2邀请消息  3进度通知    4 系统提醒
     * <p>
     * 接口出参：
     * sendFlag	           短信状态	 	0失败（未读） 、1成功（未读）、2已读、3删除（失效）
     * sendFlagName	 短信状态描述
     * smsType	          消息类型
     * objTypeName	对象类型名称
     * objType	          对象类型
     * smsContent	          消息内容
     * objId	                    业务编号
     * smsId	                    消息记录编号
     * channelType    渠道类型
     * sendDate	         发送时间	 	格式：2015-05-06
     * <p>
     * 我的消息中心消息列表查询
     */
    Page<SysSmsSendDto> querySendHis(String billId, Long userId, Integer smsType, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 接口编号：11009
     * 接口入参：
     *        listData [  修改集合 必传
     *                    smsId	         消息记录编号
     *                    sendDate	发送时间	 格式：2016-03-31
     *                    objectType	业务类型 :1：修改消息为已读状态2：删除短信
     *                 ]
     * 消息中心修改短信消息状态
     */
    void updateMessageFlag(List<MessageFlagDto> listData,String accessToken);

    /**
     * 接口编号：11010
     * 接口入参：
     * billId        手机号码
     * 接口出参：
     * unReadCount    消息未读数量
     * unReadCountOrderAssistant  订单助手未读数量
     * unReadCountInviteInfo  邀请信息未读数量
     * unReadCountProgress   进度通知未读数量
     * unReadCountSystemNotify    系统提醒未读数量
     * <p>
     * 我的消息首页未读数量
     */
    UnReadCountDto unReadCount(String billId, String accessToken);

    /**
     * 接口编号：11011
     * 接口入参：
     * smsId	         消息记录编号
     * 接口出参：
     * title	标题
     * smsContent	          消息内容
     * smsTenantName	      来源租户名称，如果是平台的话就是同心智行
     * createDate	         时间	 	格式：2015-05-06
     * 系统提醒详情页面
     */
    SystemNotifyDto querySystemNotify(Long smsId, String accessToken);



}
