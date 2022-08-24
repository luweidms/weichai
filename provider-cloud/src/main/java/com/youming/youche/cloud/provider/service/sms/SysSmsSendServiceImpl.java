package com.youming.youche.cloud.provider.service.sms;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.IMsgNotifySettingService;
import com.youming.youche.cloud.api.sms.ISmsControllerService;
import com.youming.youche.cloud.api.sms.ISysSmsLogService;
import com.youming.youche.cloud.api.sms.ISysSmsParamService;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.api.sms.ISysSmsTemplateService;
import com.youming.youche.cloud.domin.sms.MsgNotifySetting;
import com.youming.youche.cloud.domin.sms.SmsController;
import com.youming.youche.cloud.domin.sms.SysSmsLog;
import com.youming.youche.cloud.domin.sms.SysSmsParam;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.cloud.domin.sms.SysSmsTemplate;
import com.youming.youche.cloud.dto.sys.SysSmsSendDto;
import com.youming.youche.cloud.dto.sys.SystemNotifyDto;
import com.youming.youche.cloud.dto.sys.UnReadCountDto;
import com.youming.youche.cloud.dto.sys.UnReadCountMiddleDto;
import com.youming.youche.cloud.provider.mapper.sms.SysSmsSendMapper;
import com.youming.youche.cloud.vo.sys.MessageFlagDto;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.sys.ISysSmsBusinessService;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.sys.SysSmsBusiness;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.MD5;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import com.framework.core.util.JsonHelper;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-12
 */
@DubboService(version = "1.0.0")
public class SysSmsSendServiceImpl extends BaseServiceImpl<SysSmsSendMapper, SysSmsSend> implements ISysSmsSendService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SysSmsSendServiceImpl.class);

    private static final int objectTypes[] = new int[]{1, 2};

    @Resource
    public ISysSmsTemplateService sysSmsTemplateService;

    @Resource
    public ISysSmsParamService sysSmsParamService;

    @Resource
    public ISysSmsLogService sysSmsLogService;
    @DubboReference(version = "1.0.0")
    ISysSmsBusinessService sysSmsBusinessService;
    @Resource
    private RedisUtil redisUtil;
    @DubboReference(version = "1.0.0")
    ISysUserService sysOperatorService;
    @Resource
    private ISmsControllerService smsControllerService;


    @Resource
    public IMsgNotifySettingService msgNotifySettingService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Value("${sms.url}")
    String url;

    @Value("${sms.account}")
    String account;

    @Value("${sms.password}")
    String password;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    IApplyRecordService iApplyRecordService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService iSysTenantDefService;

    @DubboReference(version = "1.0.0")
    IOrderInfoService iOrderInfoService;

    /**
     * 发送短信
     *
     * @param sysSmsSend
     * @param
     * @throws Exception
     */
    @Override
    public void sendSms(SysSmsSend sysSmsSend) {
        LOGGER.info("---------------start for sending sms-----------");
        if (sysSmsSend != null) {
            if (sysSmsSend.getTemplateId() < 0) {
                throw new BusinessException("模板id不能为空");
            }
            if (sysSmsSend.getSmsType() < 0) {
                throw new BusinessException("短信类型不能为空");
            }
            if (sysSmsSend.getBillId() == null || "".equals(sysSmsSend.getBillId())) {
                throw new BusinessException("手机号码不能为空");
            }
            String smsContent = getSmsContent(sysSmsSend);
            //保存发送短信表
            sysSmsSend.setSmsContent(smsContent);
            sysSmsSend.setSendFlag(0);
            sysSmsSend.setSendTime(LocalDateTime.now());
            sysSmsSend.setExpId("0");
            if (StringUtils.isNotEmpty(sysSmsSend.getObjId())) {
                sysSmsSend.setObjId(sysSmsSend.getObjId());
            }
            if (StringUtils.isNotEmpty(sysSmsSend.getObjType())) {
                sysSmsSend.setObjType(sysSmsSend.getObjType());
            }
            //保存短信日志表 注册、密码重置、密码修改
            if (sysSmsSend.getTemplateId() == 1000000001 || sysSmsSend.getTemplateId() == 1000000005 || sysSmsSend.getTemplateId() == 1000000002) {
                SysSmsLog sysSmsLog = new SysSmsLog();
                sysSmsLog.setBillId(sysSmsSend.getBillId());
                sysSmsLog.setSendData(sysSmsSend.getSendTime());
                sysSmsLog.setSmsContent(sysSmsSend.getSmsContent());
                sysSmsLog.setSmsId(sysSmsSend.getId());
                sysSmsLog.setTemplateId(sysSmsSend.getTemplateId());
                sysSmsLog.setTenantId(sysSmsSend.getTenantId());
                sysSmsLogService.save(sysSmsLog);
            }
            int flag = baseMapper.insert(sysSmsSend);
            if (flag > 0) {
                try {
                    //发送短信
                    JSONObject body = null;
                    body = buildRequestBody(sysSmsSend.getBillId(), sysSmsSend.getTemplateNumber(), sysSmsSend.getParamMap(), account, password);
                    String results = HttpUtil.createPost(url).
                            header("Content-Type", "application/json;charset=UTF-8").
                            body(JSONUtil.toJsonStr(body)).execute().charset("utf-8").body();
                    LOGGER.info("sms:" + JSONUtil.toJsonStr(body));
                    if (results != null) {
                        JSONObject jsonObject = JSONUtil.parseObj(results);
                        if (jsonObject.get("resultCode") != null && jsonObject.get("resultCode").equals("0")) {
                            //短信发送成功修改状态
                            SysSmsSend upSysSmsSend = new SysSmsSend();
                            upSysSmsSend.setId(sysSmsSend.getId());
                            upSysSmsSend.setSendFlag(1);
                            baseMapper.updateById(upSysSmsSend);
                        } else {
                            //短信发送失败修改状态
                            SysSmsSend upSysSmsSend = new SysSmsSend();
                            upSysSmsSend.setId(sysSmsSend.getId());
                            upSysSmsSend.setSendFlag(2);
                            baseMapper.updateById(upSysSmsSend);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new BusinessException("短信服务商发送异常，请重试:" + e);
                }
            }
        }
    }


    /***
     * @Description: 配置短信通知查询
     * @Author: luwei
     * @Date: 2022/1/22 1:14 下午
     * @Param tenantId:
     * @return: com.youming.youche.cloud.domin.sms.MsgNotifySetting
     * @Version: 1.0
     **/
    @Override
    public MsgNotifySetting getMsgNotifySettingByTenantId(Long tenantId) {
        if (null == tenantId || tenantId <= 0) {
            return null;
        }
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("tenant_id", tenantId);
        MsgNotifySetting setting = msgNotifySettingService.getOne(queryWrapper);
        if (null == setting) {
            setting = new MsgNotifySetting();
            setting.setNotifyReceiver(false);
            setting.setNotifyTenant(false);
            setting.setTenantId(tenantId);
            msgNotifySettingService.save(setting);
        }
        return setting;
    }
    /***
     * @Description: 短信通知
     * @Author: luwei
     * @Date: 2022/1/22 1:33 下午
     * @Param msgNotifySetting:
     * @return: java.lang.Integer
     * @Version: 1.0
     **/
    @Override
    public Boolean updateMsgNotifySetting(MsgNotifySetting msgNotifySetting) {
        return msgNotifySettingService.update(msgNotifySetting);
    }
    /**
     * 发送短信
     *
     * @throws Exception
     */
    @Override
    public void sendSms(String billId, long templateId, long smsType, long objType, String objId, Map paraMap, String accessToken) {

        //Map<String, Object> smsMap = new HashMap<String, Object>();
        SysSmsSend sysSmsSend = new SysSmsSend();
//        smsMap.put("template_id", templateId);
//        smsMap.put("bill_id",billId);
//        smsMap.put("sms_type", smsType);
//        smsMap.put("OBJ_TYPE", objType);
//        smsMap.put("paraMap", paraMap);
//        smsMap.put("OBJ_ID",objId);
        sysSmsSend.setTemplateId(templateId);
        sysSmsSend.setBillId(billId);
        sysSmsSend.setSmsType(Integer.parseInt(String.valueOf(smsType)));
        sysSmsSend.setObjType(String.valueOf(objType));
        sysSmsSend.setParamMap(paraMap);
        sysSmsSend.setObjId(objId);
        sendSms(sysSmsSend);
    }

    /***
     * @Description: todo
     * @Author: luwei
     * @Date: 2022/8/19 22:25
     * @Param billId: 手机号
      * @Param templateId: 模版id
      * @Param smsType: 短信类型
      * @Param objType: 业务类型
      * @Param objId: 业务id
      * @Param paraMap: 短信内容
      * @Param accessToken: 验签
     * @return: void
     * @Version: 1.0
     **/
    @Override
    public void sendSms(String billId, long templateId, long smsType, String objType, String objId, Map paraMap, String accessToken) {

        //Map<String, Object> smsMap = new HashMap<String, Object>();
        SysSmsSend sysSmsSend = new SysSmsSend();
//        smsMap.put("template_id", templateId);
//        smsMap.put("bill_id",billId);
//        smsMap.put("sms_type", smsType);
//        smsMap.put("OBJ_TYPE", objType);
//        smsMap.put("paraMap", paraMap);
//        smsMap.put("OBJ_ID",objId);
        sysSmsSend.setTemplateId(templateId);
        sysSmsSend.setBillId(billId);
        sysSmsSend.setSmsType(Integer.parseInt(String.valueOf(smsType)));
        sysSmsSend.setObjType(objType);
        sysSmsSend.setParamMap(paraMap);
        sysSmsSend.setObjId(objId);
        sendSms(sysSmsSend);
    }

    @Override
    public void sendWxSms(String billId,long templateId,Map paraMap,String accessToken){
        //保存发送短信表
        SysSmsSend smsSend = new SysSmsSend();
        smsSend.setBillId(billId);
        smsSend.setTemplateId(templateId);
        smsSend.setSmsContent(JSONUtil.toJsonStr(paraMap));
        smsSend.setSmsType(SysStaticDataEnum.WX_SMS_TYPE.WX_NOTIFY);
        smsSend.setSendFlag(0);
        smsSend.setCreateTime(LocalDateTime.now());
        smsSend.setExpId("0");
        doSave(smsSend);
    }
    /**
     * 接口入参：
     * billId        手机号码
     * userId        用户编号
     * smsType       1 订单助手  2邀请消息  3进度通知    4 系统提醒
     * objectType	业务类型 :1：修改消息为已读状态2：删除短信
     * <p>
     * 消息中心修改短信消息状态
     */
    @Override
    public void updateMessageFlagAll(Long userId, String billId, Integer smsType, Integer objectType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isBlank(billId)) {
            throw new BusinessException("参数错误，手机号码不能为空！");
        }
        if (StringUtils.isNotBlank(billId) && !CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的手机号码！");
        }
        if (userId < 0) {
            userId = loginInfo.getId();
        }
        if (smsType <= 0) {
            throw new BusinessException("输入的短信类型错误！");
        }

        if (smsType == 1) {
            smsType = SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT;
        } else if (smsType == 2) {
            smsType = SysStaticDataEnum.SMS_TYPE.INVITE_INFO;
        } else if (smsType == 3) {
            smsType = SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS;
        } else if (smsType == 4) {
            smsType = SysStaticDataEnum.SMS_TYPE.SYSTEM_NOTIFY;
        }

        if (objectType <= 0) {
            throw new BusinessException("请输入业务类型!");
        }
        if (!CommonUtils.isContains(objectTypes, objectType)) {
            throw new BusinessException("输入的业务类型无效!");
        }

        Page<SysSmsSend> page = this.getSmsList(billId, userId, smsType, 1, 10);
        List<SysSmsSend> list = page.getRecords();

        if (list != null && !list.isEmpty()) {

            for (SysSmsSend sysSmsSend : list) {

                //司机车辆的特殊处理
                if (smsType == SysStaticDataEnum.SMS_TYPE.INVITE_INFO && objectType == SysStaticDataEnum.SMS_OBJECT_TYPE.OBJECT_TYPE_2) {
                    List<ApplyRecord> applyRecordList = iApplyRecordService.getUpdateMessageFlagAllApplyRecordData(Long.parseLong(sysSmsSend.getObjId())
                            , SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
                    if (applyRecordList != null && applyRecordList.size() != 0) {
                        for (ApplyRecord applyRecord : applyRecordList) {
                            if (applyRecord.getApplyType() == SysStaticDataEnum.APPLY_TYPE.USER) {
                                applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE2);
                                applyRecord.setAuditDate(LocalDateTime.now());
                                applyRecord.setAuditOpId(loginInfo.getId());
                                applyRecord.setAuditRemark("司机清空消息");
                                iApplyRecordService.update(applyRecord);
                            }
                        }
                    }
                }

                // 订单的特殊处理
                if (sysSmsSend.getSmsType() == SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT && sysSmsSend.getSendFlag() == SysStaticDataEnum.SMS_OBJECT_TYPE.OBJECT_TYPE_2) {
                    if (sysSmsSend.getTemplateId() == 4000000006L || sysSmsSend.getTemplateId() == 4000000009L) {
                        List<OrderInfo> orderInfos = iOrderInfoService.selectOrderInfosByOrderId(Long.valueOf(sysSmsSend.getObjId()));
                        if (orderInfos != null && orderInfos.size() != 0) {
                            for (OrderInfo orderInfo : orderInfos) {
                                iOrderInfoService.updateOrderIdMessage(orderInfo.getOrderId());
                            }
                        }
                    }
                }

            }

        }

        this.updateMessageFlagAll(billId, smsType, objectType);

    }

    /**
     * 消息中心修改短信消息状态
     */
    public void updateMessageFlagAll(String billId, int smsType, int objectType) {

        LambdaUpdateWrapper<SysSmsSend> updateWrapper = new LambdaUpdateWrapper<>();

        if (objectType == SysStaticDataEnum.SMS_OBJECT_TYPE.OBJECT_TYPE_1) {
            //已读
            updateWrapper.set(SysSmsSend::getSendFlag, 2);
            updateWrapper.eq(SysSmsSend::getBillId, billId);
            updateWrapper.eq(SysSmsSend::getSmsType, smsType);
            updateWrapper.eq(SysSmsSend::getSendFlag, 1).or().eq(SysSmsSend::getSendFlag, 0);
        } else {
            //删除
            updateWrapper.set(SysSmsSend::getSendFlag, 3);
            updateWrapper.eq(SysSmsSend::getBillId, billId);
            updateWrapper.eq(SysSmsSend::getSmsType, smsType);
            updateWrapper.ne(SysSmsSend::getSendFlag, 3);
        }
        this.update(updateWrapper);
    }

    @Override
    public Page<SysSmsSend> getSmsList(String billId, long userId, int smsType, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<SysSmsSend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper/*.eq(SysSmsSend::getUserId, userId).or()*/.eq(SysSmsSend::getBillId, billId);
        queryWrapper.eq(SysSmsSend::getSmsType, smsType);
        queryWrapper.notIn(SysSmsSend::getSendFlag, SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_3); // 已删除
        queryWrapper.orderByDesc(SysSmsSend::getSendTime);

        return this.page(new Page<SysSmsSend>(pageNum, pageSize), queryWrapper);
    }

    /**
     * 我的消息中心消息列表查询
     */
    @Override
    public Page<SysSmsSendDto> querySendHis(String billId, Long userId, Integer smsType, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (smsType == 1) {
            smsType = SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT;
        } else if (smsType == 2) {
            smsType = SysStaticDataEnum.SMS_TYPE.INVITE_INFO;
        } else if (smsType == 3) {
            smsType = SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS;
        } else if (smsType == 4) {
            smsType = SysStaticDataEnum.SMS_TYPE.SYSTEM_NOTIFY;
        }

        if (StringUtils.isNotEmpty(billId) && !CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的手机号码！");
        }

        if (loginInfo != null && userId < 0) {
            userId = loginInfo.getUserInfoId();
        }

        if (StringUtils.isBlank(billId) && userId <= 0) {
            throw new BusinessException("请输入用户编号或手机号!");
        }

        if (StringUtils.isBlank(billId) && userId > 0) {
            UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);

            if (userDataInfo == null) {
                throw new BusinessException("查询用户资料失败！");
            }
        }

        Page<SysSmsSend> page = this.getSmsList(billId, userId, smsType, pageNum, pageSize);
        List<SysSmsSend> list = page.getRecords();

        List<SysSmsSendDto> logArrayList = new ArrayList<SysSmsSendDto>();
        for (SysSmsSend sysSmsSend : list) {

            SysSmsSendDto sysSmsSendVO = new SysSmsSendDto();

            sysSmsSendVO.setBillId(sysSmsSend.getBillId());

            if (sysSmsSend.getSrcType() != null) {
                sysSmsSendVO.setChannelType(sysSmsSend.getSrcType() == 1 ? "微信" : "web");
            }

            if (sysSmsSend.getObjId() != null) {
                sysSmsSendVO.setObjId(sysSmsSend.getObjId());
            }

            if (sysSmsSend.getObjType() != null) {
                sysSmsSendVO.setObjType(sysSmsSend.getObjType());
                sysSmsSendVO.setObjTypeName(getSysStaticDataByTenantIdMinusOne(SysStaticDataEnum.SysStaticData.OBJ_TYPE, sysSmsSend.getObjType()).getCodeName());
            }

            sysSmsSendVO.setSmsContent(sysSmsSend.getSmsContent());

            //对于系统提醒需要截取长度
            if (smsType == SysStaticDataEnum.SMS_TYPE.SYSTEM_NOTIFY) {
                String smsContent = sysSmsSendVO.getSmsContent();
                sysSmsSendVO.setSmsContent(smsContent.length() > 30 ? smsContent.substring(0, 30) : smsContent);
            }

            sysSmsSendVO.setSmsId(sysSmsSend.getId() + "");

            if (sysSmsSend.getSmsType() != null) {
                int str = sysSmsSend.getSmsType();
                if (str == SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT) {
                    str = 1;
                } else if (str == SysStaticDataEnum.SMS_TYPE.INVITE_INFO) {
                    str = 2;
                } else if (str == SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS) {
                    str = 3;
                } else if (str == SysStaticDataEnum.SMS_TYPE.SYSTEM_NOTIFY) {
                    str = 4;
                }

                sysSmsSendVO.setSmsType(str + "");

            }

            if (sysSmsSend.getSendTime() != null) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                sysSmsSendVO.setSendDate(dateTimeFormatter.format(sysSmsSend.getSendTime()));
            }

            sysSmsSendVO.setSendFlag(sysSmsSend.getSendFlag() + "");

            sysSmsSendVO.setSendFlagName(getSysStaticData(SysStaticDataEnum.SysStaticData.SMS_SEND_FLAG, sysSmsSend.getSendFlag() + "").getCodeName());

            if ("0".equals(sysSmsSendVO.getSendFlag()) || "1".equals(sysSmsSendVO.getSendFlag())) {
                sysSmsSendVO.setSendFlagName("未读");
            }

            logArrayList.add(sysSmsSendVO);
        }

        Page<SysSmsSendDto> result = new Page<SysSmsSendDto>();
        result.setRecords(logArrayList);
        result.setCurrent(page.getCurrent());
        result.setTotal(page.getTotal());
        result.setSize(page.getSize());
        result.setPages(page.getPages());

        return result;
    }

    /**
     * 消息中心修改短信消息状态
     */
    @Override
    public void updateMessageFlag(List<MessageFlagDto> listData, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        Date queryDate = null;

        if (listData.size() == 0) {
            throw new BusinessException("请输入修改状态数据不能为空！");
        }
        String sendDate = "";
        long smsId = 0;
        int sendFlag = 0;
        int objectType = 0;
        for (MessageFlagDto dto : listData) {
            sendDate = dto.getSendDate();
            smsId = dto.getSmsId();
            objectType = dto.getObjectType();

            if (smsId <= 0) {
                throw new BusinessException("输入的修改状态的编号无效！");
            }

            if (StringUtils.isEmpty(sendDate)) {
                throw new BusinessException("输入的修改时间不能为空！");
            }

            try {
                queryDate = DateUtil.formatStringToDate(sendDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (queryDate == null) {
                throw new BusinessException("输入的修改状态时间无效！");
            }

            if (objectType <= 0) {
                throw new BusinessException("请输入业务类型!");
            }

            if (!CommonUtils.isContains(objectTypes, objectType)) {
                throw new BusinessException("输入的业务类型无效!");
            }

            SysSmsSend sysSmsSend = this.getById(smsId);
            if (sysSmsSend == null) {
                throw new BusinessException("查找短信消息失败！");
            }

            sendFlag = sysSmsSend.getSendFlag();
            if (sendFlag == SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_3) {
                log.error("短信消息在已删除状态不需要更新状态！");
                continue; //跳出这个循环
            }

            if (objectType == SysStaticDataEnum.SMS_OBJECT_TYPE.OBJECT_TYPE_1) {
                //已读
                sendFlag = SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_2;
            } else {
                //删除
                sendFlag = SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_3;
            }
            //司机车辆的特殊处理
            if (sysSmsSend.getSmsType() == SysStaticDataEnum.SMS_TYPE.INVITE_INFO && sendFlag == SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_3) {
                List<ApplyRecord> applyRecordList = iApplyRecordService.getUpdateMessageFlagAllApplyRecordData(Long.parseLong(sysSmsSend.getObjId())
                        , SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
                if (applyRecordList != null && applyRecordList.size() != 0) {
                    for (ApplyRecord applyRecord : applyRecordList) {
                        if (applyRecord.getApplyType() == SysStaticDataEnum.APPLY_TYPE.USER) {
                            applyRecord.setState(SysStaticDataEnum.APPLY_STATE.APPLY_STATE2);
                            applyRecord.setAuditDate(LocalDateTime.now());
                            applyRecord.setAuditOpId(loginInfo.getId());
                            applyRecord.setAuditRemark("司机清空消息");
                            iApplyRecordService.update(applyRecord);
                        }
                    }
                }
            }

            // 订单的特殊处理
            if (sysSmsSend.getSmsType() == SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT && sendFlag == SysStaticDataEnum.SMS_SEND_FLAG.SEND_FLAG_3) {
                if (sysSmsSend.getTemplateId() == 4000000006L || sysSmsSend.getTemplateId() == 4000000009L) {
                    List<OrderInfo> orderInfos = iOrderInfoService.selectOrderInfosByOrderId(Long.valueOf(sysSmsSend.getObjId()));
                    if (orderInfos != null && orderInfos.size() != 0) {
                        for (OrderInfo orderInfo : orderInfos) {
                            iOrderInfoService.updateOrderIdMessage(orderInfo.getOrderId());
                        }
                    }
                }
            }

            sysSmsSend.setSendFlag(sendFlag);
            this.update(sysSmsSend);
        }

    }

    /**
     * 我的消息首页未读数量
     */
    @Override
    public UnReadCountDto unReadCount(String billId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (StringUtils.isEmpty(billId)) {
            throw new BusinessException("输入的查询手机号码不能为空！");
        }

        if (!CommonUtils.isCheckPhone(billId)) {
            throw new BusinessException("请输入正确的查询手机号码！");
        }

        long userId = -1L;
        if (loginInfo != null) {
            userId = loginInfo.getUserInfoId();
        }
        if (userId <= 0) {
            throw new BusinessException("请输入用户编号!");
        }

        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("查询用户资料失败！");
        }

        List<UnReadCountMiddleDto> list = baseMapper.unReadCount(billId);

        UnReadCountDto dto = new UnReadCountDto();
        int sum = 0;
        dto.setUnReadCountOrderAssistant("0");//订单助手
        dto.setUnReadCountInviteInfo("0");//邀请信息
        dto.setUnReadCountProgress("0");//进度通知
        dto.setUnReadCountSystemNotify("0");//系统提醒
        for (UnReadCountMiddleDto obj : list) {
            if (Integer.parseInt(String.valueOf(obj.getSmsType())) == SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT) {
                dto.setUnReadCountOrderAssistant(String.valueOf(obj.getCut()));
            } else if (Integer.parseInt(String.valueOf(obj.getSmsType())) == SysStaticDataEnum.SMS_TYPE.INVITE_INFO) {
                dto.setUnReadCountInviteInfo(String.valueOf(obj.getCut()));
            } else if (Integer.parseInt(String.valueOf(obj.getSmsType())) == SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS) {
                dto.setUnReadCountProgress(String.valueOf(obj.getCut()));
            } else if (Integer.parseInt(String.valueOf(obj.getSmsType())) == SysStaticDataEnum.SMS_TYPE.SYSTEM_NOTIFY) {
                dto.setUnReadCountSystemNotify(String.valueOf(obj.getCut()));
            } else {
                continue;
            }
            sum += Integer.parseInt(String.valueOf(obj.getCut()));
        }
        dto.setUnReadCount(String.valueOf(sum));

        return dto;
    }

    /**
     * 系统提醒详情页面
     */
    @Override
    public SystemNotifyDto querySystemNotify(Long smsId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        UserDataInfo userDataInfo = null;

        long userId = -1L;
        if (loginInfo != null) {
            userId = loginInfo.getUserInfoId();
        }
        if (userId <= 0) {
            throw new BusinessException("请输入用户编号!");
        }

        userDataInfo = userDataInfoService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("查找用户编号[" + userId + "]的用户信息失败!");
        }

        if (smsId <= 0) {
            throw new BusinessException("输入的修改状态的编号无效！");
        }

        SysSmsSend sysSmsSendHis = this.getById(smsId);
        if (sysSmsSendHis == null) {
            throw new BusinessException("查找短信消息失败！");
        }

        SystemNotifyDto dto = new SystemNotifyDto();
        dto.setTitle(getSysStaticData(SysStaticDataEnum.SysStaticData.OBJ_TYPE, sysSmsSendHis.getObjType()).getCodeName());
        dto.setSmsContent(sysSmsSendHis.getSmsContent());
        if (sysSmsSendHis.getTenantId() == null || sysSmsSendHis.getTenantId() <= 0) {
            dto.setSmsTenantName("管好车");
        } else {
            dto.setSmsTenantName(iSysTenantDefService.getTenantName(sysSmsSendHis.getTenantId()));
        }
        dto.setCreateDate(sysSmsSendHis.getCreateTime());
        dto.setInfo("Y");

        return dto;
    }

    public SysCfg getSysCfg(String cfgName, String codeValue) {
        List<SysCfg> list = (List<SysCfg>) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(cfgName));
        if (list != null && list.size() > 0) {
            for (SysCfg sysStaticData : list) {
                if (sysStaticData.getId().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysCfg();
    }

    /**
     * 发送短信，延迟N分钟发送
     *
     * @param smsMap
     * @param delayMinis 延迟时间
     * @throws Exception
     */
    public void sendSms(Map smsMap, int delayMinis, String accessToken) {
        LOGGER.info("---------------start for sending sms-----------");
        if (smsMap != null) {
            long templateId = DataFormat.getLongKey(smsMap, "template_id");
            String billId = DataFormat.getStringKey(smsMap, "bill_id");
            int smsType = DataFormat.getIntKey(smsMap, "sms_type");
            String objType = DataFormat.getStringKey(smsMap, "OBJ_TYPE");
            String objId = DataFormat.getStringKey(smsMap, "OBJ_ID");
            Long tenantId = DataFormat.getLongKey(smsMap, "tenantId");

            UserDataInfo userInfo = userDataInfoService.getPhone(billId);

            if (templateId < 0) {
                throw new BusinessException("模板id不能为空");
            }
            if (smsType < 0) {
                throw new BusinessException("短信类型不能为空");
            }
            if (billId == null || "".equals(billId)) {
                throw new BusinessException("手机号码不能为空");
            }
            Map paramMap = (Map) smsMap.get("paraMap");
            String smsContent = getSmsContent(templateId, paramMap);
            //保存发送短信表
            SysSmsSend smsSend = new SysSmsSend();
            if (tenantId > 0) {
                smsSend.setTenantId(tenantId);
            }
            smsSend.setBillId(billId);
            if (userInfo != null) {
                smsSend.setUserId(userInfo.getId());
            }
            smsSend.setTemplateId(templateId);
            smsSend.setSmsContent(smsContent);
            smsSend.setSmsType(smsType);
            if (StringUtils.isNotEmpty(objId)) {
                smsSend.setObjId(objId);
            }
            if (StringUtils.isNotEmpty(objType)) {
                smsSend.setObjType(objType);
            }
            smsSend.setSendFlag(0);
            Date sendDate = DateUtil.addMinis(new Date(), delayMinis);
            smsSend.setSendTime(getLocalDateTimeByDate(sendDate));
            smsSend.setExpId("0");
            //保存短信日志表 注册、密码重置、密码修改
            if (templateId == 1000000001 || templateId == 1000000005 || templateId == 1000000002) {
                SysSmsLog sysSmsLog = new SysSmsLog();
                sysSmsLog.setBillId(billId);
                sysSmsLog.setSendData(smsSend.getSendTime());
                sysSmsLog.setSmsContent(smsSend.getSmsContent());
                sysSmsLog.setSmsId(smsSend.getId());
                sysSmsLog.setTemplateId(smsSend.getTemplateId());
                sysSmsLog.setTenantId(tenantId);
                doSave(sysSmsLog);
            }
            //发送短信，并且需要用户回复的，就要把相关信息存储在业务表中

            /**根据数据库templateId查询这个模板是否需要执行业务 (SMS_PERFORM:0不执行，1执行)**/
            if (doSel(templateId) == 1) {
                String primaryKey = sysSmsBusinessService.getPrimaryKey(billId, getDateByLocalDateTime(smsSend.getSendTime()), accessToken);
                SysSmsBusiness sysSmsBusiness = new SysSmsBusiness();
                sysSmsBusiness.setBId(primaryKey);
                sysSmsBusiness.setBusinessId(DataFormat.getLongKey(smsMap, "businessId"));
                sysSmsBusiness.setBusinessParam(DataFormat.getStringKey(smsMap, "businessParam"));
                sysSmsBusiness.setTemplateId(templateId);
                sysSmsBusiness.setBusinessDate(getDateByLocalDateTime(smsSend.getSendTime()));
                sysSmsBusiness.setBusinessDeal(0);
                sysSmsBusiness.setBusinessFlag(1);
                sysSmsBusiness.setBillId(billId);
                //需要二次确认的就要把EXPID保存到信息发送表中 如果不需要EXPID就为0，从BId中截取前三位
                smsSend.setExpId(primaryKey.substring(0, 3));
                doSave(sysSmsBusiness);
            }
            doSave(smsSend);
        }
    }

    /**
     * 根据数据库表templateId查询这个模板是否需要执行业务 (SMS_PERFORM:0不执行，1执行)
     **/
    public Integer doSel(long templateId) {
        SysSmsTemplate sysSmsTemplate = getSysCfg(templateId);
        if (sysSmsTemplate == null) {
            LOGGER.error("找不到指定的模板:" + templateId);
            return -1;
        }
        if (sysSmsTemplate.getSmsPerform() == null) {
            LOGGER.error("模板：" + templateId + "内容设置不完整,SMS_PERFORM字段必须不为空。注：SMS_PERFORM:0不执行，1执行");
            return -1;
        }
        return sysSmsTemplate.getSmsPerform();
    }

    /**
     * 获取指定系统参数 templateId
     *
     * @param templateId
     * @param
     * @return
     */
    public SysSmsTemplate getSysCfg(long templateId) {
        SysSmsTemplate sysSmsTemplate = sysSmsTemplateService.getById(templateId);
        if (sysSmsTemplate == null) {
            throw new BusinessException("没有找到对应的短信模板！");
        }
        return sysSmsTemplate;

    }

    /**
     * 转换短信内容
     *
     * @param templateId
     * @param paramMap
     * @return
     * @throws Exception
     */
    public String getSmsContent(long templateId, Map paramMap) {
        String smsContent = "";
        try {
            SysSmsTemplate sysSmsTem = sysSmsTemplateService.getById(templateId);
            QueryWrapper<SysSmsParam> sysSmsParamQueryWrapper = new QueryWrapper<>();
            if (templateId > 0) {
                sysSmsParamQueryWrapper.eq("template_id", templateId);
            }
            //所有租户一致
//			BaseUser user = SysContexts.getCurrentOperator();
//			if (user == null) {
//				ca.add(Restrictions.eq("tenantId",104L));
//			}else{
//				ca.add(Restrictions.eq("tenantId",user.getTenantId()));
//			}
            sysSmsParamQueryWrapper.eq("state", 1);
            List<SysSmsParam> smsParams = sysSmsParamService.list(sysSmsParamQueryWrapper);
            smsContent = sysSmsTem.getTemplateContent();
            if (smsParams != null && smsParams.size() > 0) {
                for (SysSmsParam smsParamItem : smsParams) {
                    String paraCode = "${" + smsParamItem.getParamCode() + "}";
                    String paraValue = String.valueOf(paramMap.get(smsParamItem.getParamCode()));
                    if (smsContent.indexOf(paraCode) > -1) {
                        smsContent = StringUtils.replace(smsContent, paraCode, paraValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("转换短信内容异常" + e);
        }
        return smsContent;
    }

    public void doSave(SysSmsSend sysSmsSendObj) {
        String smsContent = sysSmsSendObj.getSmsContent();
        String billId = sysSmsSendObj.getBillId();
        String sendDate = DateUtil.formatDateByFormat(getDateByLocalDateTime(sysSmsSendObj.getSendTime()), DateUtil.DATETIME12_FORMAT2);
        String md5 = MD5.eccryptMD5(smsContent + billId + sendDate).toUpperCase();
        boolean isNewMD5Set = false;
        // TODO 这里有问题 RemoteCacheUtil
//        if (!RemoteCacheUtil.exists(EnumConsts.RemoteCache.SMS_MD5)) {
//            isNewMD5Set = true;
//        }

        if (StringUtils.isNotBlank(smsContent) && smsContent.contains("${servicePhone}")) {
            SysCfg sysCfg = getSysCfg("CUSTOM_SERVICE_PHONE", "0");
            if (sysCfg != null) {
                smsContent = StringUtils.replace(smsContent, "${servicePhone}", sysCfg.getCfgValue() + "");
                sysSmsSendObj.setSmsContent(smsContent);
            }
        }

        boolean isSend = true;
        SysUser sysOperator = sysOperatorService.isExistUserInfo(null, billId);
        if (sysOperator != null) {
            long userId = sysOperator.getUserInfoId();
            QueryWrapper<SmsController> smsControllerQueryWrapper = new QueryWrapper<>();
            smsControllerQueryWrapper.eq("user_id", userId)
                    .eq("template_id", sysSmsSendObj.getTemplateId());
            List<SmsController> list = smsControllerService.list(smsControllerQueryWrapper);
            if (list.size() > 0) {
                isSend = false;
            }
        }
        if (isSend) {

            //  TODO RemoteCacheUtil不知道怎么来
//            if(!RemoteCacheUtil.sismember(EnumConsts.RemoteCache.SMS_MD5, md5) || sysSmsSendObj.getTemplateId()== EnumConsts.SmsTemplate.YONGZHOU_SAY){
//                sysSmsSendObj.setMd5(md5);
//
//                Map<String, String[]> map =  new HashMap<String, String[]>();
//                map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(),DateUtil.YEAR_MONTH_FORMAT2)});
//                //sysSmsSendObj.setTenantId(SysContexts.getCurrentOperator().getTenantId());
//                this.save(sysSmsSendObj);
//                RemoteCacheUtil.sadd(EnumConsts.RemoteCache.SMS_MD5, md5);
//                if (isNewMD5Set) {
//                    RemoteCacheUtil.expire(EnumConsts.RemoteCache.SMS_MD5, 30*(24*(60*60)));
//                }


        } else {
            LOGGER.error("短信MD5值存在，不保存短信表" + smsContent + billId + sendDate);
        }

    }

    public void doSave(SysSmsBusiness sysBusiness) {
        sysSmsBusinessService.save(sysBusiness);
    }

    public void doSave(SysSmsLog sysSmsLog) {
        sysSmsLogService.save(sysSmsLog);
    }


    public LocalDateTime getLocalDateTimeByDate(Date date) {
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = date.toInstant().atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    public Date getDateByLocalDateTime(LocalDateTime localDateTime) {
        //获取系统默认时区
        ZoneId zoneId = ZoneId.systemDefault();
        //时区的日期和时间
        ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
        //获取时刻
        Date date = Date.from(zonedDateTime.toInstant());
        return date;
    }


    /**
     * 转换短信内容
     *
     * @param sysSmsSend
     * @return
     * @throws Exception
     */
    public String getSmsContent(SysSmsSend sysSmsSend) {
        String smsContent = "";
        try {
            SysSmsTemplate sysSmsTem = sysSmsTemplateService.getById(sysSmsSend.getTemplateId());
            sysSmsSend.setTemplateNumber(sysSmsTem.getTemplateNumber());
            QueryWrapper queryWrapper = new QueryWrapper();
            if (sysSmsSend.getTemplateId() > 0) {
                queryWrapper.eq("template_id", sysSmsSend.getTemplateId());
            }
            queryWrapper.eq("state", 1);
            List<SysSmsParam> smsParams = sysSmsParamService.list(queryWrapper);
            smsContent = sysSmsTem.getTemplateContent();
            if (smsParams != null && smsParams.size() > 0) {
                for (SysSmsParam smsParamItem : smsParams) {
                    String paraCode = "{" + smsParamItem.getParamCode() + "}";
                    String paraValue = String.valueOf(sysSmsSend.getParamMap().get(smsParamItem.getParamCode()));
                    if (smsContent.indexOf(paraCode) > -1) {
                        smsContent = StrUtil.replace(smsContent, paraCode, paraValue);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("转换短信内容异常" + e);
            throw new BusinessException("转换短信内容异常");
        }
        return smsContent.replaceAll("\\$","");
    }

    public static JSONObject buildRequestBody(String msisdn, String smsTemplateId,
                                              Map<String, String> paramValues, String accout, String passward) {
        if (null == msisdn || null == smsTemplateId || null == accout || null == passward) {
            System.out.println(
                    "buildRequestBody(): mobiles, templateId or templateParas or account or password is null.");
            return null;
        }
        JSONObject jsonObject = JSONUtil.createObj();
        List<MtSmsMessage> requestLists = new ArrayList<MtSmsMessage>();
        MtSmsMessage mtSmsMessage = new MtSmsMessage();
        List<String> mobiles = new ArrayList<String>();
        mobiles.add(msisdn);
        mtSmsMessage.setMobiles(mobiles);
        mtSmsMessage.setTemplateId(smsTemplateId);
        mtSmsMessage.setTemplateParas(paramValues);
        mtSmsMessage.setSignature("【潍柴智能科技有限公司】");
        requestLists.add(mtSmsMessage);
        jsonObject.set("account", accout);
        jsonObject.set("password", passward);
        jsonObject.set("requestLists", requestLists);
        return jsonObject;
    }

    public static class MtSmsMessage {
        List<String> mobiles;


        String templateId;


        Map<String, String> templateParas;


        String signature;


        String messageId;


        String extCode;


        List<NamedPatameter> extendInfos;


        /**
         * 返回 mobiles
         *
         * @return mobiles值
         */
        public List<String> getMobiles() {
            return mobiles;
        }


        /**
         * 对mobiles进行赋值
         *
         * @param mobiles mobiles值
         */
        public void setMobiles(List<String> mobiles) {
            this.mobiles = mobiles;
        }


        /**
         * 返回 templateId
         *
         * @return templateId值
         */
        public String getTemplateId() {
            return templateId;
        }


        /**
         * 对templateId进行赋值
         *
         * @param templateId templateId值
         */
        public void setTemplateId(String templateId) {
            this.templateId = templateId;
        }


        /**
         * 返回 templateParas
         *
         * @return templateParas值
         */
        public Map<String, String> getTemplateParas() {
            return templateParas;
        }


        /**
         * 对templateParas进行赋值
         *
         * @param templateParas templateParas值
         */
        public void setTemplateParas(Map<String, String> templateParas) {
            this.templateParas = templateParas;
        }


        /**
         * 返回 signature
         *
         * @return signature值
         */
        public String getSignature() {
            return signature;
        }


        /**
         * 对signature进行赋值
         *
         * @param signature signature值
         */
        public void setSignature(String signature) {
            this.signature = signature;
        }


        /**
         * 返回 messageId
         *
         * @return messageId值
         */
        public String getMessageId() {
            return messageId;
        }


        /**
         * 对messageId进行赋值
         *
         * @param messageId messageId值
         */
        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }


        /**
         * 返回 extCode
         *
         * @return extCode值
         */
        public String getExtCode() {
            return extCode;
        }


        /**
         * 对extCode进行赋值
         *
         * @param extCode extCode值
         */
        public void setExtCode(String extCode) {
            this.extCode = extCode;
        }


        /**
         * 返回 extendInfos
         *
         * @return extendInfos值
         */
        public List<NamedPatameter> getExtendInfos() {
            return extendInfos;
        }


        /**
         * 对extendInfos进行赋值
         *
         * @param extendInfos extendInfos值
         */
        public void setExtendInfos(List<NamedPatameter> extendInfos) {
            this.extendInfos = extendInfos;
        }
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

        /**
     * 获取发送的消息内容
     *
     * @param smsContent
     *            [APP或SMS]
     * @return
     */
    private String getMessageContent(String smsContent) {
//        if ("APP".equals(type)) {
//            StringBuffer retBuf = new StringBuffer();
//            String tmp = smsContent;
//            int idx;
//            while ((idx = tmp.indexOf("${")) >= 0) {
//                retBuf.append(tmp.substring(0, idx));
//                tmp = tmp.substring(idx + 2);
//                if ((idx = tmp.indexOf("}")) >= 0) {
//                    tmp = tmp.substring(idx + 1);
//                }
//            }
//            // 没有需要替换的了，追加最后一截字符
//            retBuf.append(tmp);
//            return retBuf.toString();
//        } else if ("SMS".equals(type)) {
        //smsContent = StringUtils.replace(smsContent, "${", "");
        smsContent = smsContent.replaceAll("\\$","");
        LOGGER.info("sms:"+smsContent);
        return smsContent;
        //   }
        //  return null;
    }

    public SysStaticData getSysStaticDataByTenantIdMinusOne(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue) && sysStaticData.getTenantId() == -1) {
                    return sysStaticData;
                }
            }
        }
        return new SysStaticData();
    }

    public class NamedPatameter {
        String key;


        String value;


        /**
         * 返回 key
         *
         * @return key值
         */
        public String getKey() {
            return key;
        }


        /**
         * 对key进行赋值
         *
         * @param key key值
         */
        public void setKey(String key) {
            this.key = key;
        }


        /**
         * 返回 value
         *
         * @return value值
         */
        public String getValue() {
            return value;
        }


        /**
         * 对value进行赋值
         *
         * @param value value值
         */
        public void setValue(String value) {
            this.value = value;
        }


    }


}
