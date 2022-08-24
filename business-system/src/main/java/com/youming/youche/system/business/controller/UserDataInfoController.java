package com.youming.youche.system.business.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.dto.sys.SysSmsSendDto;
import com.youming.youche.cloud.dto.sys.UnReadCountDto;
import com.youming.youche.cloud.vo.sys.MessageFlagDto;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.AesEncryptUtil;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.aspect.SysOperatorSaveLog;
import com.youming.youche.system.business.dto.BusinessIdDto;
import com.youming.youche.system.business.util.IDCardUtil;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.dto.user.LocalUserInfoDto;
import com.youming.youche.system.utils.CommonUtils;
import com.youming.youche.system.vo.CreateUserVo;
import com.youming.youche.system.vo.UpdateUserVo;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户资料信息 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-12-25
 */
@RestController
@RequestMapping("/user/data/info")
public class UserDataInfoController extends BaseController<UserDataInfo, IUserDataInfoService> {

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Override
    public IUserDataInfoService getService() {
        return userDataInfoService;
    }

    /**
     * 方法实现说明 获取当前登录者的用户信息
     * @author      terry
     * @param
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:55
     */
    @GetMapping({"get"})
    public ResponseResult get() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(userDataInfoService.get(accessToken));
    }

    /**
     * 方法实现说明 新增用户信息
     * @author      terry
     * @param createUserVo {@link CreateUserVo}
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:55
     */
    @PostMapping("creates")
    @SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Staff, type = SysOperLogConst.OperType.Add, comment = "新增员工信息")
    public ResponseResult create(@Valid @RequestBody CreateUserVo createUserVo) {
        if (!CommonUtils.isCheckPhone(createUserVo.getLoginAcct())) {
            throw new BusinessException("用户名手机格式错误！");
        }
        if (!IDCardUtil.isIDCard(createUserVo.getIdentification())) {
            throw new BusinessException("请输入正确的身份证号!");
        }
        createUserVo.setPassword(AesEncryptUtil.desEncrypt(createUserVo.getPassword()));
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Long staffId = userDataInfoService.create(createUserVo, accessToken);
            return ResponseResult.success(BusinessIdDto.of().setId(staffId));
        } catch (BusinessException e) {
            return ResponseResult.failure(e.getMessage());
        }
    }
    /**
     * 方法实现说明 更新用户信息
     * @author      terry
     * @param updateUserVo {@link UpdateUserVo}
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:56
     */
    @PutMapping("updateUser")
    @SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Staff, type = SysOperLogConst.OperType.Update, comment = "修改员工信息")
    public ResponseResult update(@Valid @RequestBody UpdateUserVo updateUserVo) {

        if (!IDCardUtil.isIDCard(updateUserVo.getIdentification())) {
            throw new BusinessException("请输入正确的身份证号!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Long update = userDataInfoService.update(updateUserVo, accessToken);
            return ResponseResult.success(BusinessIdDto.of().setId(update));
        } catch (BusinessException e) {
          return   ResponseResult.failure(e.getMessage());
        }

    }

    /**
     * 方法实现说明 仅返回车队成员信息
     *
     * @param pageNum * @param pageSize * @param phone * @param linkman
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/1/6 10:59
     */
    @GetMapping({"getAll"})
    public ResponseResult getAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                 @RequestParam(value = "phone", required = false) String phone,
                                 @RequestParam(value = "linkman", required = false) String linkman) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        // userDataInfoService.
        // return ResponseResult.success(domain);
        return ResponseResult.success(userDataInfoService.get(accessToken, pageNum, pageSize, phone, linkman));
    }

    /**
     * 获取车队全部人员的用户信息
     */
    @GetMapping({"getAlls"})
    public ResponseResult getAlls(
            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "linkman", required = false) String linkman,
            @RequestParam(value = "number", required = false) String number,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "orgId", required = false) String orgId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        // userDataInfoService.
        // return ResponseResult.success(domain);
        return ResponseResult.success(userDataInfoService.get(accessToken, pageNum, pageSize, phone, linkman));
    }

    /**
     * 方法实现说明 获取部门成员
     *
     * @param pageNum * @param pageSize * @param orgId
     * @return com.youming.youche.commons.response.ResponseResult
     * @throws
     * @author terry
     * @date 2022/1/6 11:00
     */
    @GetMapping({"getOrganize"})
    public ResponseResult getOrganize(
            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
            @RequestParam(value = "orgId") Long orgId) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(userDataInfoService.get(accessToken, orgId, pageNum, pageSize));
    }

    /**
     * 方法实现说明 删除用户信息
     * @author      terry
     * @param Id 用户信息id
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:57
     */
    @Override
    @SysOperatorSaveLog(code = SysOperLogConst.BusiCode.Staff,type = SysOperLogConst.OperType.Del,comment = "删除员工信息")
    public ResponseResult remove(@PathVariable Long Id) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long deleted = userDataInfoService.remove(Id, accessToken);
        return deleted != 0 ? ResponseResult.success(BusinessIdDto.of().setId(deleted)) : ResponseResult.failure(ResponseCode.INTERFACE_ADDRESS_INVALID);
    }

    /**
     * 方法实现说明  查询代收车队列表
     * @author      terry
     * @param pageNum
    * @param pageSize
    * @param phone
    * @param name
    * @param includeDriver
     * @return      com.youming.youche.commons.response.ResponseResult
     * @exception
     * @date        2022/5/31 15:59
     */
     @GetMapping("getVirtualTenantList")
    public ResponseResult getVirtualTenantList(@RequestParam("pageNum") Integer pageNum,
                               @RequestParam("pageSize") Integer pageSize,
                               @RequestParam("phone") String phone,
                                String name,
                               @RequestParam("includeDriver") Boolean includeDriver){
       return ResponseResult.success( userDataInfoService.getVirtualTenantList(pageNum,pageSize,phone,name,includeDriver));
     }

    /**
     * 接口编号 10036
     * <p>
     * 接口入参：
     * userId         用户编号
     * 接口出参：
     * linkman        姓名
     * identification 身份证号码
     * loginAcct      账号
     * employeeNumber 员工工号
     * staffPosition  职位
     * tenantName     归属车队名称
     * adminUserMobile 超管手机号
     * 企业驻场——首页——个人信息
     */
    @GetMapping("getLocalUserInfo")
    public ResponseResult getLocalUserInfo(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException("不存在的用户");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        LocalUserInfoDto localUserInfo = userDataInfoService.getLocalUserInfo(userId, accessToken);
        return ResponseResult.success(localUserInfo);
    }

    @GetMapping("doQueryBackUserList")
    public ResponseResult doQueryBackUserList( @RequestParam("linkman")String linkman,
                                               @RequestParam("mobilePhone")String mobilePhone,
                                               @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                               @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return ResponseResult.success(
                userDataInfoService.doQueryBackUserList(linkman,mobilePhone,accessToken,pageSize,pageNum)
        );
    }

    /**
     * 40037
     * 加油二维码(找油网)
     */
    @GetMapping("orCodeByZhaoYou")
    public ResponseResult orCodeByZhaoYou(String plateNumber, Long userId, String userName, String userPhone) {
        if (StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("请选择车牌号！");
        }
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户ID不能为空！");
        }

        return ResponseResult.success(
                userDataInfoService.orCodeByZhaoYou(plateNumber, userId, userName, userPhone)
        );

    }

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
    @GetMapping("unReadCount")
    public ResponseResult unReadCount(String billId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        UnReadCountDto dto = sysSmsSendService.unReadCount(billId, accessToken);
        return ResponseResult.success(dto);
    }


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
    @PostMapping("querySendHis")
    public ResponseResult querySendHis(String billId, Long userId, Integer smsType,
                                       @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (smsType <= 0) {
            throw new BusinessException("输入的短信类型错误！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        Page<SysSmsSendDto> sysSmsSendDtoPage = sysSmsSendService.querySendHis(billId, userId, smsType, pageNum, pageSize, accessToken);
        return ResponseResult.success(sysSmsSendDtoPage);
    }


    /**
     * 接口编号：11009
     * 接口入参：
     * listData [  修改集合 必传
     * smsId	         消息记录编号
     * sendDate	发送时间	 格式：2016-03-31
     * objectType	业务类型 :1：修改消息为已读状态2：删除短信
     * ]
     * 消息中心修改短信消息状态
     */
    @PostMapping("updateMessageFlag")
    public ResponseResult updateMessageFlag(@RequestBody List<MessageFlagDto> listData) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        sysSmsSendService.updateMessageFlag(listData, accessToken);
        return ResponseResult.success();
    }


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
    @PostMapping("updateMessageFlagAll")
    public ResponseResult updateMessageFlagAll(Long userId, String billId, Integer smsType, Integer objectType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        sysSmsSendService.updateMessageFlagAll(userId, billId, smsType, objectType, accessToken);
        return ResponseResult.success();
    }


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
    @GetMapping("querySystemNotify")
    public ResponseResult querySystemNotify(Long smsId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        return ResponseResult.success(
                sysSmsSendService.querySystemNotify(smsId, accessToken)
        );
    }

}
