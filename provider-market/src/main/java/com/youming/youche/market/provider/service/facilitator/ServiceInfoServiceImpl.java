package com.youming.youche.market.provider.service.facilitator;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.workbench.WorkbenchDto;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.ServiceConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.api.facilitator.IConsumeOilFlowService;
import com.youming.youche.market.api.facilitator.IDriverInfoExtService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.IServiceInfoVerService;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlService;
import com.youming.youche.market.api.facilitator.IServiceInvitationDtlVerService;
import com.youming.youche.market.api.facilitator.IServiceInvitationService;
import com.youming.youche.market.api.facilitator.IServiceInvitationVerService;
import com.youming.youche.market.api.facilitator.IServiceProductService;
import com.youming.youche.market.api.facilitator.IServiceSerialService;
import com.youming.youche.market.api.facilitator.ITenantProductRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelVerService;
import com.youming.youche.market.api.facilitator.IUserDataInfoMarketService;
import com.youming.youche.market.api.facilitator.base.ServiceUserService;
import com.youming.youche.market.api.youka.IOilCardManagementService;
import com.youming.youche.market.domain.facilitator.ConsumeOilFlow;
import com.youming.youche.market.domain.facilitator.Facilitator;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.ServiceInfoVer;
import com.youming.youche.market.domain.facilitator.ServiceInvitation;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtl;
import com.youming.youche.market.domain.facilitator.ServiceInvitationDtlVer;
import com.youming.youche.market.domain.facilitator.ServiceInvitationVer;
import com.youming.youche.market.domain.facilitator.ServiceProduct;
import com.youming.youche.market.domain.facilitator.ServiceSerial;
import com.youming.youche.market.domain.facilitator.TenantProductRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.facilitator.TenantServiceRelVer;
import com.youming.youche.market.domain.facilitator.UserDataInfo;
import com.youming.youche.market.dto.facilitator.ConsumeOilFlowDto;
import com.youming.youche.market.dto.facilitator.CooperationDto;
import com.youming.youche.market.dto.facilitator.ProductSaveDto;
import com.youming.youche.market.dto.facilitator.ServiceInfoDto;
import com.youming.youche.market.dto.facilitator.ServiceInfoFleetDto;
import com.youming.youche.market.dto.facilitator.ServiceInvitationDto;
import com.youming.youche.market.dto.facilitator.ServiceSaveInDto;
import com.youming.youche.market.dto.facilitator.ServiceUserInDto;
import com.youming.youche.market.dto.facilitator.TenantProductRelOutDto;
import com.youming.youche.market.dto.facilitator.UserDataInfoInDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoDto;
import com.youming.youche.market.provider.mapper.facilitator.ServiceInfoMapper;
import com.youming.youche.market.provider.transfer.CooperationTenantTransfer;
import com.youming.youche.market.provider.transfer.FacilitatorDetailedTransfer;
import com.youming.youche.market.provider.transfer.FacilitatorTransfer;
import com.youming.youche.market.provider.transfer.ServiceInfoFleetTransfer;
import com.youming.youche.market.provider.transfer.ServiceInfoTransfer;
import com.youming.youche.market.provider.transfer.ServiceInvitationVoTransfer;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.provider.utis.ReadisUtil;
import com.youming.youche.market.provider.utis.SnowflakeIdWorker;
import com.youming.youche.market.provider.utis.SysStaticDataRedisUtils;
import com.youming.youche.market.vo.facilitator.ApplyServiceVo;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import com.youming.youche.market.vo.facilitator.InvitationInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoBasisVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoFleetVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceInvitationVo;
import com.youming.youche.market.vo.facilitator.ServiceProdcutByNameVo;
import com.youming.youche.market.vo.facilitator.ServiceinfoDetailedVo;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;

import com.youming.youche.order.api.order.IUserRepairMarginService;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.ConsumeOilFlowWxDto;
import com.youming.youche.order.dto.ConsumeOilFlowWxOutDto;
import com.youming.youche.order.vo.ConsumeOilFlowVo;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.system.api.IServiceProviderBillService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.audit.IAuditNodeInstService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.domain.ServiceProviderBill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_CITY;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_DISTRICT;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SYS_PROVINCE;
import static com.youming.youche.conts.SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS;
import static com.youming.youche.conts.SysStaticDataEnum.INVITE_AUTH_STATE.WAIT;
import static com.youming.youche.conts.SysStaticDataEnum.IS_AUTH.IS_AUTH1;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC;
import static com.youming.youche.conts.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL_CARD;
import static com.youming.youche.conts.SysStaticDataEnum.SYS_STATE_DESC.STATE_YES;
import static com.youming.youche.conts.SysStaticDataEnum.TENANT_TYPE.TENANT;
import static com.youming.youche.market.constant.facilitator.FacilitatorConstant.INVITE_AUTH_STATE;
import static com.youming.youche.market.constant.facilitator.ServiceProductConstant.SYS_SERVICE_BUSI_TYPE;
import static com.youming.youche.record.common.SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL;


/**
 * <p>
 * 服务商表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2022-01-22
 */
@DubboService(version = "1.0.0")
@Service
public class ServiceInfoServiceImpl extends BaseServiceImpl<ServiceInfoMapper, ServiceInfo> implements IServiceInfoService {
    private static final Log log = LogFactory.getLog(ServiceInfoServiceImpl.class);
    @Resource
    private ServiceInfoMapper serviceInfoMapper;
    @Lazy
    @Resource
    private FacilitatorDetailedTransfer facilitatorDetailedTransfer;
    @Lazy
    @Resource
    private ServiceInfoTransfer serviceInfoTransfer;
    @Lazy
    @Resource
    private FacilitatorTransfer facilitatorTransfer;
    @Lazy
    @Autowired
    private IServiceProductService serviceProductService;
    @Lazy
    @Autowired
    private IUserDataInfoMarketService userDataInfoService;
    @Lazy
    @Autowired
    private ServiceUserService serviceUserService;
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    @Lazy
    @Autowired
    private ITenantServiceRelVerService tenantServiceRelVerService;
    @Lazy
    @Autowired
    private ITenantServiceRelService tenantServiceRelService;
    @Lazy
    @Autowired
    private IServiceInfoVerService serviceInfoVerService;
    @Lazy
    @Autowired
    private IServiceSerialService serviceSerialService;
    @DubboReference(version = "1.0.0")
    IServiceProviderBillService serviceProviderBillService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;

    @Lazy
    @Autowired
    IDriverInfoExtService driverInfoExtService;

    @Lazy
    @Autowired
    ITenantProductRelService tenantProductRelService;
    @Autowired
    ReadisUtil readisUtil;

    @Autowired
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @Lazy
    @Autowired
    private IServiceInvitationService serviceInvitationService;
    @Lazy
    @Resource
    ServiceInfoFleetTransfer serviceInfoFleetTransfer;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    IAuditNodeInstService auditNodeInstService;
    @Lazy
    @Resource
    private ServiceInvitationVoTransfer serviceInvitationVoTransfer;
    @Lazy
    @Resource
    private CooperationTenantTransfer cooperationTenantTransfer;
    @Lazy
    @Resource
    IOilCardManagementService oilCardManagementService;
    @Lazy
    @Resource
    private IEtcMaintainService etcMaintainService;
    @Lazy
    @Autowired
    private IServiceInvitationVerService serviceInvitationVerService;
    @Lazy
    @Autowired
    private IServiceInvitationDtlService serviceInvitationDtlService;
    @Lazy
    @Autowired
    private IServiceInvitationDtlVerService serviceInvitationDtlVerService;
    @Lazy
    @Autowired
    IConsumeOilFlowService consumeOilFlowService;


//    @DubboReference(version = "1.0.0")
//    private ISysSmsSendService sysSmsSendService;


    /**
     * 服务商档案分页条件查询
     *
     * @param serviceInfoQueryCriteria
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<FacilitatorVo> queryFacilitator(ServiceInfoQueryCriteria serviceInfoQueryCriteria,
                                                Integer pageNum, Integer pageSize, String accessToken) {
        //获得当前登录用户信息
        LoginInfo user = loginUtils.get(accessToken);
        Page<Facilitator> page = new Page<>(pageNum, pageSize);
        if (serviceInfoQueryCriteria.getHasVer() != null && serviceInfoQueryCriteria.getHasVer() == 0) {
            Page<Facilitator> pageData = new Page<>(0, 500);
            Page<Facilitator> list = serviceInfoMapper.queryFacilitator(pageData, serviceInfoQueryCriteria, user);
            List<Facilitator> listRecords = list.getRecords();
            if (listRecords != null && listRecords.size() > 0) {
                //过滤排序得到需要审核的数据
                List<Facilitator> collect = listRecords.stream()
                        .filter(a -> a.getAuthFlag() == SysStaticDataEnum.EXPENSE_STATE.AUDIT)
                        .sorted(Comparator.comparing(Facilitator::getCreateDate).reversed())
                        .collect(Collectors.toList());
                //进行分页截取
                List<Facilitator> facilitators = collect.stream()
                        .skip((pageNum - 1) * pageSize)
                        .limit(pageSize)
                        .collect(Collectors.toList());
                List<FacilitatorVo> facilitatorVos = facilitatorTransfer.setFacilitatorVo(facilitators, accessToken);
                Page<FacilitatorVo> facilitatorVoPage = new Page<>();
                facilitatorVoPage.setRecords(facilitatorVos);
                facilitatorVoPage.setTotal(collect.size());
                facilitatorVoPage.setSize(pageSize);
                facilitatorVoPage.setCurrent(pageNum);
                return facilitatorVoPage;
            }
            return new Page<FacilitatorVo>().setTotal(0).setSize(pageSize).setCurrent(pageNum);
        }
        //开始分页
        Page<Facilitator> list = serviceInfoMapper.queryFacilitator(page, serviceInfoQueryCriteria, user);
        List<Facilitator> records = list.getRecords();
        Page<FacilitatorVo> facilitatorVoPage = null;
        //转Vo
        List<FacilitatorVo> facilitatorVos = null;
        if (records != null && records.size() > 0) {
            facilitatorVos = facilitatorTransfer.setFacilitatorVo(records, accessToken);
        }
        facilitatorVoPage = new Page<FacilitatorVo>();
        facilitatorVoPage.setRecords(facilitatorVos);
        facilitatorVoPage.setTotal(list.getTotal());
        facilitatorVoPage.setSize(pageSize);
        facilitatorVoPage.setCurrent(pageNum);
        return facilitatorVoPage;

    }


    /**
     * 服务商档案保存
     *
     * @param serviceSaveIn
     * @param accessToken
     * @return
     */
    @Override
    @Transactional
    public ResponseResult saveFacilitator(ServiceSaveInDto serviceSaveIn, String accessToken) {
        //获取当前用户
        LoginInfo user = loginUtils.get(accessToken);

        if (StringUtils.isEmpty(serviceSaveIn.getLoginAcct())) {
            throw new BusinessException("账号不能为空，请填写！");
        }
        if (!CommonUtil.isCheckMobiPhoneNew(serviceSaveIn.getLoginAcct())) {
            throw new BusinessException("账号格式不正确！");
        }
        if (StringUtils.isEmpty(serviceSaveIn.getServiceName())) {
            throw new BusinessException("服务商名称不能为空，请填写！");
        }
        if (serviceSaveIn.getServiceType() < 0) {
            throw new BusinessException("服务商类型不能为空，请选择！");
        }
        if (StringUtils.isEmpty(serviceSaveIn.getCompanyAddress())) {
            throw new BusinessException("公司地址不能为空，请填写！");
        }
        if (StringUtils.isEmpty(serviceSaveIn.getLinkman())) {
            throw new BusinessException("联系人姓名不能为空，请填写！");
        }

        if (serviceSaveIn.getIsBillAbility() == null || serviceSaveIn.getIsBillAbility() < 0) {
            serviceSaveIn.setIsBillAbility(1);
        }
        if (StringUtils.isNotBlank(serviceSaveIn.getReceiptCode()) && StringUtils.isBlank(serviceSaveIn.getReceiptLoginAcct())) {
            throw new BusinessException("上传了微信收款码，微信账号不能为空！");
        }
        if (StringUtils.isNotBlank(serviceSaveIn.getPuBankId())) {
            serviceSaveIn.setPuBankName(readisUtil.getSysStaticData("BANK_TYPE", serviceSaveIn.getPuBankId()).getCodeName());
        }
        if (StringUtils.isNotBlank(serviceSaveIn.getPvBankId())) {
            serviceSaveIn.setPvBankName(readisUtil.getSysStaticData("BANK_TYPE", serviceSaveIn.getPvBankId()).getCodeName());
        }
        Long serviceUserId = serviceSaveIn.getServiceUserId();
        if (CommonUtil.isNotBlankToLong(serviceUserId)) {
            this.updateServiceInfo(serviceSaveIn, user);
            return ResponseResult.success("修改成功");
        } else {
            this.saveService(serviceSaveIn, user);
            return ResponseResult.success("添加成功");
        }
    }

    /**
     * 保存服务商信息
     *
     * @param serviceSaveIn
     * @return
     * @throws Exception
     */
    public void saveService(ServiceSaveInDto serviceSaveIn, LoginInfo user) {
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfoByLoginAcct(serviceSaveIn.getLoginAcct());

        long serviceuserId = -1L;
        String password = null;
        if (userDataInfo == null) {
            if (StringUtils.isEmpty(serviceSaveIn.getIdentification())) {
                throw new BusinessException("身份证号不能为空，请填写！");
            }
            if (serviceSaveIn.getIdenPictureFront() == null || serviceSaveIn.getIdenPictureFront() < 0) {
                throw new BusinessException("身份证正面图片不能为空，请上传！");
            }
            if (StringUtils.isEmpty(serviceSaveIn.getIdenPictureFrontUrl())) {
                throw new BusinessException("身份证号正面url不能为空，请填写！");
            }
            if (serviceSaveIn.getIdenPictureBack() == null || serviceSaveIn.getIdenPictureBack() < 0) {
                throw new BusinessException("身份证背面图片不能为空，请上传！");
            }
            if (StringUtils.isEmpty(serviceSaveIn.getIdenPictureBackUrl())) {
                throw new BusinessException("身份证背面url不能为空，请填写！");
            }
            //保存账号信息
            ServiceUserInDto serviceUserIn = new ServiceUserInDto();
            BeanUtils.copyProperties(serviceSaveIn, serviceUserIn);
            //如果是违章、保险类型服务商则设置随机六位数的密码
//            if (serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.VR || serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.INS || serviceSaveIn.getServiceType() == OIL_CARD || serviceSaveIn.getServiceType() == ETC) {
//                serviceUserIn.setPassword(String.valueOf((int) ((Math.random() * 9 + 1) * 100000)));
//                password = serviceUserIn.getPassword();
//            }
            serviceuserId = doAddServ(serviceUserIn, user, serviceSaveIn);
        } else {
            serviceuserId = userDataInfo.getId();
//            if (serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.VR || serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.INS || serviceSaveIn.getServiceType() == OIL_CARD || serviceSaveIn.getServiceType() == ETC) {
//                password = passwordEncoder.encode(userDataInfo.getAccPassword());
//            }
        }

        serviceSaveIn.setServiceUserId(serviceuserId);
        //保存服务商基本信息
        ServiceInfo serviceInfo = saveServiceInfo(serviceuserId, serviceSaveIn, user);


        /**发送短信*/
//        Map paramMap = new HashMap();
//        paramMap.put("password", password);
//        SysSmsSend sysSmsSend = new SysSmsSend();
//        //短信变量值
//        sysSmsSend.setParamMap(paramMap);
//        //用户id
//        sysSmsSend.setUserId(serviceInfo.getServiceUserId());
//        //手机号
//        sysSmsSend.setBillId(serviceSaveIn.getLoginAcct());
//        //短信类型
//        sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.MESSAGE_AUTHENTICATION);
//        //模板id
//        sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.RESET_TENANT_SUPPER_MANAGER_PASSWORD);
//        //业务类型
//        sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.NOTIFY));
//        sysSmsSendService.sendSms(sysSmsSend);

        //操作日志
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, serviceInfo.getServiceUserId(), SysOperLogConst.OperType.Add, "新增服务商档案");
    }

    @Override
    public ServiceInfoVo seeServiceInfoMotorcade(Long serviceUserId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        ServiceInfoVo serviceInfoVo = serviceInfoTransfer.acquireServiceInfoVo(serviceUserId);
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), serviceUserId);
        TenantServiceRelVer tenantServiceRelVer = null;
        if (tenantServiceRel != null) {
            BeanUtils.copyProperties(tenantServiceRel, serviceInfoVo);
            if (tenantServiceRel.getUseQuotaAmt() != null) {
                serviceInfoVo.setUseQuotaAmtStr(tenantServiceRel.getUseQuotaAmt().toString());
            } else {
                serviceInfoVo.setUseQuotaAmtStr("0.0");
            }
            LambdaQueryWrapper<TenantServiceRelVer> lambda = new QueryWrapper<TenantServiceRelVer>().lambda();
            lambda.eq(TenantServiceRelVer::getRelId, tenantServiceRel.getId())
                    .orderByDesc(TenantServiceRelVer::getId).last("limit 1");
            tenantServiceRelVer = tenantServiceRelVerService.getOne(lambda);
        }
        ServiceInfoVo facilitatorDetailedVo = facilitatorDetailedTransfer.getFacilitatorDetailedVo(serviceInfoVo, tenantServiceRelVer);
        if (facilitatorDetailedVo.getBalanceType() != null) {
            Integer balanceType = facilitatorDetailedVo.getBalanceType();
            if (balanceType != null && balanceType == 1) {
                facilitatorDetailedVo.setBalanceTypeName("账期");
            } else if (balanceType != null && balanceType == 2) {
                facilitatorDetailedVo.setBalanceTypeName("月结");
            } else if (balanceType != null && balanceType == 3) {
                facilitatorDetailedVo.setBalanceTypeName("无账期");
            }
        }
        if (facilitatorDetailedVo.getState() != null) {
            String stateName = readisUtil.getSysStaticData("DATE_STS", facilitatorDetailedVo.getState().toString()).getCodeName();
            facilitatorDetailedVo.setStateName(stateName);
        }
        if (facilitatorDetailedVo.getServiceType() != null) {
            String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, facilitatorDetailedVo.getServiceType().toString()).getCodeName();
            facilitatorDetailedVo.setServiceTypeName(serviceTypeName);
        }


        return facilitatorDetailedVo;
    }

    @Override
    public ServiceInfoVo seeServiceInfoMotorcade(Long serviceUserId) {
        ServiceInfoVo serviceInfoVo = serviceInfoTransfer.acquireServiceInfoVo(serviceUserId);
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(serviceUserId);
        TenantServiceRelVer tenantServiceRelVer = null;
        if (tenantServiceRel != null) {
            BeanUtils.copyProperties(tenantServiceRel, serviceInfoVo);
            if (tenantServiceRel.getUseQuotaAmt() != null) {
                serviceInfoVo.setUseQuotaAmtStr(tenantServiceRel.getUseQuotaAmt().toString());
            } else {
                serviceInfoVo.setUseQuotaAmtStr("0.0");
            }
            LambdaQueryWrapper<TenantServiceRelVer> lambda = new QueryWrapper<TenantServiceRelVer>().lambda();
            lambda.eq(TenantServiceRelVer::getRelId, tenantServiceRel.getId())
                    .orderByDesc(TenantServiceRelVer::getId).last("limit 1");
            tenantServiceRelVer = tenantServiceRelVerService.getOne(lambda);
        }
        ServiceInfoVo facilitatorDetailedVo = facilitatorDetailedTransfer.getFacilitatorDetailedVo(serviceInfoVo, tenantServiceRelVer);
        if (facilitatorDetailedVo.getBalanceType() != null) {
            Integer balanceType = facilitatorDetailedVo.getBalanceType();
            if (balanceType != null && balanceType == 1) {
                facilitatorDetailedVo.setBalanceTypeName("账期");
            } else if (balanceType != null && balanceType == 2) {
                facilitatorDetailedVo.setBalanceTypeName("月结");
            } else if (balanceType != null && balanceType == 3) {
                facilitatorDetailedVo.setBalanceTypeName("无账期");
            }
        }
        if (facilitatorDetailedVo.getState() != null) {
            String stateName = readisUtil.getSysStaticData("DATE_STS", facilitatorDetailedVo.getState().toString()).getCodeName();
            facilitatorDetailedVo.setStateName(stateName);
        }
        if (facilitatorDetailedVo.getServiceType() != null) {
            String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, facilitatorDetailedVo.getServiceType().toString()).getCodeName();
            facilitatorDetailedVo.setServiceTypeName(serviceTypeName);
        }


        return facilitatorDetailedVo;
    }

    @Override
    public ServiceinfoDetailedVo seeServiceInfo(Long serviceUserId, Integer hasAudit, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        ServiceInfoVo serviceInfo = getServiceInfo(serviceUserId, user);
        ServiceinfoDetailedVo serviceInfoVos = new ServiceinfoDetailedVo();
        if (serviceInfo.getServiceType() != null) {
            String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceInfo.getServiceType().toString()).getCodeName();
            serviceInfo.setServiceTypeName(serviceTypeName);
        }
        serviceInfoVos.setServiceData(serviceInfo);
        if (hasAudit == 1) {
            ServiceInfoVo serviceInfoVer = serviceInfoVerService.getServiceInfoVer(serviceUserId, user);
            if (serviceInfoVer.getServiceType() != null) {
                String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceInfoVer.getServiceType().toString()).getCodeName();
                serviceInfoVer.setServiceTypeName(serviceTypeName);
            }
            serviceInfoVos.setServiceDataVer(serviceInfoVer);
        }
        return serviceInfoVos;
    }

    /**
     * 校验服务商有效性
     *
     * @param productSaveIn
     * @param serviceUserId
     * @param isUpdate
     * @return
     */
    @Override
    public Boolean checkServiceInfo(ProductSaveDto productSaveIn, Long serviceUserId, Boolean isUpdate) {
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
        ServiceInfo serviceInfo = this.getOne(lambda);
        if (serviceInfo == null) {
            throw new BusinessException("服务商信息有误，新增失败！");
        }
        Integer isBillAbility = serviceInfo.getIsBillAbility();//服务商开票能力
        Integer serviceState = serviceInfo.getState();//服务商状态
        Integer isFleet = productSaveIn.getIsFleet();
        Integer isBillAbByProduct = productSaveIn.getIsBillAbility();
        Integer productState = productSaveIn.getProductState();
        if (isUpdate) {
            if (CommonUtil.checkEnumIsNull(serviceState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                if (CommonUtil.checkEnumIsNotNull(productState, STATE_YES)) {
                    throw new BusinessException("无效服务商，站点无法设为有效！");
                }
                if (CommonUtil.checkEnumIsNotNull(productSaveIn.getState(), STATE_YES)) {
                    throw new BusinessException("无效服务商站点，不能修改成有效合作站点！");
                }
            }
        } else {
            if (CommonUtil.checkEnumIsNull(serviceState, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO)) {
                throw new BusinessException("无效服务商，不能新增站点！");
            }
            if (serviceInfo.getIsAuth() == null || serviceInfo.getIsAuth() != AUTH_PASS) {
                throw new BusinessException("未认证通过的服务商，不能新增站点！");
            }
        }
        if (CommonUtil.checkEnumIsNull(isBillAbility, SysStaticDataEnum.IS_BILL_ABILITY.NO) && isFleet != TENANT
                && CommonUtil.checkEnumIsNotNull(isBillAbByProduct, SysStaticDataEnum.IS_BILL_ABILITY.YSE)) {
            throw new BusinessException("服务商无开票能力，站点不能能选支持开票！");
        }
        return true;
    }

    @Override
    public Boolean isService(Long userId) {
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, userId);
        ServiceInfo serviceInfo = this.getOne(lambda);
        List<ServiceProduct> productList = serviceProductService.getServiceProductByChild(userId);
        return null != serviceInfo || CollectionUtils.isNotEmpty(productList);
    }

    @Override
    public ResponseResult modifyService(ServiceSaveInDto serviceSaveIn, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        long serviceUserId = serviceSaveIn.getServiceUserId();
        if (serviceSaveIn.getState() == null && serviceSaveIn.getState() < 0) {
            throw new BusinessException("状态不能为空，请选择！");
        }
        if (serviceSaveIn.getServiceType() != OIL_CARD) {

            if (serviceSaveIn.getIsBill() == null && serviceSaveIn.getIsBill() < 0) {
//                throw new BusinessException("是否开票不能为空，请选择！");
                serviceSaveIn.setIsBill(1);
            }
            if (serviceSaveIn.getIsBill() == null && serviceSaveIn.getPaymentDays() < 0) {
                throw new BusinessException("账期不能为空，请填写！");
            }
        }
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
        ServiceInfo serviceInfo = serviceInfoMapper.selectOne(lambda);
        if (serviceInfo == null) {
            throw new BusinessException("服务商信息有误！");
        }
        if (serviceInfo.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO && serviceSaveIn.getState() == STATE_YES) {
            throw new BusinessException("平台已停用该服务商，如有疑问请联系客服！");
        }
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), serviceUserId);
        tenantServiceRel.setIsAuth(IS_AUTH1);
        tenantServiceRelService.saveOrUpdate(tenantServiceRel, true, user);
        //操作日志

        if (serviceSaveIn.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            serviceProductService.loseTenantProduct(user, serviceUserId, serviceInfo.getServiceType(), user.getTenantId(), false);
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Update, "无效");
        } else {
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Update, "修改");
        }
        //启动审核流程
        //问题
        Map inMap = new HashMap();
        inMap.put("svName", "serviceInfoTF");
        boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_INFO, tenantServiceRel.getId(), SysOperLogConst.BusiCode.ServiceInfo, inMap, accessToken);
        if (!bool) {
            throw new BusinessException("数据异常，请联系客服！");
        }
        //保存历史消息
        TenantServiceRelVer tenantServiceRelVer = new TenantServiceRelVer();
        BeanUtils.copyProperties(tenantServiceRel, tenantServiceRelVer);
        tenantServiceRelVer.setIsBill(serviceSaveIn.getIsBill());
        tenantServiceRelVer.setBalanceType(serviceSaveIn.getBalanceType());
        if (null != serviceSaveIn.getBalanceType() && serviceSaveIn.getBalanceType() == 3) {
            tenantServiceRelVer.setPaymentDays(null);
            tenantServiceRelVer.setPaymentMonth(null);
            tenantServiceRelVer.setQuotaAmt(null);
        } else if (serviceSaveIn.getBalanceType() == 1) {
            tenantServiceRelVer.setPaymentMonth(null);
            tenantServiceRelVer.setPaymentDays(serviceSaveIn.getPaymentDays());
        } else {
            tenantServiceRelVer.setPaymentDays(serviceSaveIn.getPaymentDays());
            tenantServiceRelVer.setPaymentMonth(serviceSaveIn.getPaymentMonth());
            if (serviceSaveIn.getQuotaAmt() != null) {
                Double price = serviceSaveIn.getQuotaAmt() * 100;
                tenantServiceRelVer.setQuotaAmt(price.longValue());
            }
        }
        if (serviceSaveIn.getQuotaAmt() != null) {
            Double price = serviceSaveIn.getQuotaAmt() * 100;
            tenantServiceRelVer.setQuotaAmt(price.longValue());
        } else {
            tenantServiceRelVer.setQuotaAmt(null);
        }
        tenantServiceRelVer.setState(serviceSaveIn.getState());
        tenantServiceRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.NO);
        tenantServiceRelVer.setUpdateTime(LocalDateTime.now());
        tenantServiceRelVer.setUpdateOpId(user.getId());
        tenantServiceRelVer.setRelId(tenantServiceRel.getId());
        tenantServiceRelVer.setId(null);
        tenantServiceRelVerService.saveOrUpdate(tenantServiceRelVer);
        return ResponseResult.success();
    }

    @Override
    public void doSaveOrUpdate(ServiceInfo serviceInfo, Boolean isUpdate, LoginInfo baseUser) {
        if (isUpdate) {
            serviceInfo.setUpdateTime(LocalDateTime.now());
            serviceInfo.setOpId(baseUser.getId());
            this.update(serviceInfo);
        } else {
            serviceInfo.setCreateTime(LocalDateTime.now());
            serviceInfo.setState(STATE_YES);
            serviceInfo.setTenantId(baseUser.getTenantId());
            this.save(serviceInfo);
        }
    }

    /**
     * 保存与新增
     *
     * @param serviceInfo
     * @param isUpdate
     */
    public void doSaveOrUpdateBy(ServiceInfo serviceInfo, Boolean isUpdate, LoginInfo baseUser) {
        if (isUpdate) {
            serviceInfo.setUpdateTime(LocalDateTime.now());
            serviceInfo.setOpId(baseUser.getId());
        } else {
            serviceInfo.setCreateTime(LocalDateTime.now());
            serviceInfo.setOpId(baseUser.getId());
            serviceInfo.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        }
        this.saveOrUpdate(serviceInfo);
    }

    @Override
    public ServiceInfo getServiceInfoByServiceUserId(Long serviceUserId) {
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
        return this.getOne(lambda);
    }

    @Override
    public ServiceInfoDto checkRegisterService(String loginAcct, String accessToken) throws Exception {
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfoByLoginAcct(loginAcct);
        LoginInfo user = loginUtils.get(accessToken);
        ServiceInfoDto serviceInfoDto = serviceInfoTransfer.getServiceInfoDto(userDataInfo, loginAcct, user);
        if (serviceInfoDto.getInfo() == 2) {
            if (serviceInfoDto.getServiceType() != null) {
                String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceInfoDto.getServiceType().toString()).getCodeName();
                serviceInfoDto.setServiceTypeName(serviceTypeName);
            }
        }
        return serviceInfoDto;
    }

    @Override
    public Long doAddServ(ServiceUserInDto serviceUserIn, LoginInfo user, ServiceSaveInDto serviceSaveIn) {
        if (StringUtils.isEmpty(serviceUserIn.getLinkman())) {
            throw new BusinessException("联系人不能为空！");
        }

        serviceUserIn.setAuthFlg(1);

        Map map = serviceUserService.saveServiceUser(serviceUserIn, user, serviceSaveIn);
        UserDataInfo userDataInfo = (UserDataInfo) map.get("userDataInfo");
        return userDataInfo.getId();
    }

    @Override
    public ServiceInfo saveServiceInfo(Long serviceUserId, ServiceSaveInDto serviceSaveIn, LoginInfo user) {
        if (serviceUserId == null || serviceUserId < 0) {
            throw new BusinessException("服务商ID不能为空!");
        }
        serviceSaveIn.setServiceUserId(serviceUserId);

        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setLogoId(null);
        serviceInfo.setLogoUrl("image/discountsLogo.png");
        serviceInfo.setServiceUserId(serviceUserId);
        serviceInfo.setServiceName(serviceSaveIn.getServiceName());
        serviceInfo.setServiceType(serviceSaveIn.getServiceType());
        serviceInfo.setCompanyAddress(serviceSaveIn.getCompanyAddress());
        serviceInfo.setIsBillAbility(serviceSaveIn.getIsBillAbility());
        serviceInfo.setAuthFlag(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
        serviceInfo.setIsAuth(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);//未认证
        serviceInfo.setState(STATE_YES);//有效
        serviceInfo.setTenantId(user.getTenantId());
        serviceInfo.setAgentCollection(serviceSaveIn.getAgentCollection());
        serviceUserService.doSaveOrUpdate(serviceInfo, false, user);

        //保存历史数据
        serviceInfoVerService.saveServiceInfoHis(serviceSaveIn, serviceInfo.getTenantId(), serviceInfo.getIsAuth(), user);

        return serviceInfo;
    }

    @Override
    public ServiceInfoVo getServiceInfo(Long serviceUserId, LoginInfo user) {
        if (serviceUserId == null || serviceUserId <= 0) {
            throw new BusinessException("请输入服务商用户ID!");
        }
//        if(SysStaticDataEnum.PT_TENANT_ID!=user.getTenantId()) {
//            throw new BusinessException("当前用户不能查看平台服务商信息!");
//        }


        UserDataInfo userDataInfo = userDataInfoService.getById(serviceUserId);
        if (userDataInfo == null) {
            throw new BusinessException("用户信息有误！");
        }


        ServiceInfo serviceInfo = null;
        ServiceInfoVo serviceInfoOut = new ServiceInfoVo();

        if (userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.SERVER_CHILD_USER) {

            List<ServiceProduct> serviceProductByChild = serviceProductService.getServiceProductByChild(userDataInfo.getId());
            LambdaQueryWrapper<ServiceInfo> lambdaServiceInfo = new QueryWrapper<ServiceInfo>().lambda();
            lambdaServiceInfo.eq(ServiceInfo::getServiceUserId, serviceProductByChild.get(0).getServiceUserId());
            serviceInfo = this.getOne(lambdaServiceInfo);
            if (serviceInfo == null) {
                throw new BusinessException("未找到服务商信息!");
            }
            serviceInfoOut.setLoginAcct(userDataInfo.getMobilePhone());
            serviceInfoOut.setServiceType(serviceProductByChild.get(0).getBusinessType());
            serviceInfoOut.setServiceTypeName(serviceInfoOut.getServiceTypeName() + "子账号");
            serviceInfoOut.setServiceName(serviceInfo.getServiceName());
        } else {
            LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
            lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
            serviceInfo = this.getOne(lambda);
            if (serviceInfo == null) {
                throw new BusinessException("未找到服务商信息!");
            }

            BeanUtils.copyProperties(userDataInfo, serviceInfoOut);
            serviceInfoOut.setLoginAcct(userDataInfo.getMobilePhone());
            BeanUtils.copyProperties(serviceInfo, serviceInfoOut);
        }
        if (serviceInfoOut != null && serviceInfoOut.getServiceType() == SysStaticDataEnum.BUSINESS_TYPE.OIL_BUSINESS) {
            TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), serviceInfoOut.getServiceUserId());
            if (tenantServiceRel != null) {
                serviceInfoOut.setTenantServiceRel(tenantServiceRel);
            }
        }

        return serviceInfoOut;
    }

    @Override
    public ResponseResult sure(Long serviceUserId, Boolean pass, String describe, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (pass) {
            enableService(serviceUserId, describe, user);
        } else {
            disableService(serviceUserId, describe, user);
        }
        return ResponseResult.success("成功");
    }

    @Override
    public void enableService(Long serviceUserId, String desc, LoginInfo user) {
//        if(SysStaticDataEnum.PT_TENANT_ID!=user.getTenantId()) {
//            throw new BusinessException("当前用户不能查看平台服务商信息!");
//        }
        if (serviceUserId == null || serviceUserId < 0) {
            throw new BusinessException("服务商ID不能为空!");
        }
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
        ServiceInfo serviceInfo = this.getOne(lambda);
        if (serviceInfo == null || serviceInfo.getServiceUserId() == null) {
            throw new BusinessException("未找到对应服务商!");
        }
        if (STATE_YES == serviceInfo.getState()) {
            throw new BusinessException("服务商已经是有效状态!");
        }

        serviceInfo.setState(STATE_YES);
        doSaveOrUpdate(serviceInfo, true, user);

        //添加操作日志
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, serviceUserId, SysOperLogConst.OperType.Enable, "启用,原因：" + (StringUtils.isNotBlank(desc) ? desc : "无"));
    }

    @Override
    public void disableService(Long serviceUserId, String desc, LoginInfo user) {
//        if(SysStaticDataEnum.PT_TENANT_ID!=user.getTenantId()) {
//            throw new BusinessException("当前用户不能查看平台服务商信息!");
//        }
        if (serviceUserId == null || serviceUserId < 0) {
            throw new BusinessException("服务商ID不能为空!");
        }
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
        ServiceInfo serviceInfo = this.getOne(lambda);
        if (serviceInfo == null || serviceInfo.getServiceUserId() == null) {
            throw new BusinessException("未找到对应服务商!");
        }
        if (SysStaticDataEnum.SYS_STATE_DESC.STATE_NO == serviceInfo.getState()) {
            throw new BusinessException("服务商已经是无效状态!");
        }

        serviceInfo.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
        doSaveOrUpdateBy(serviceInfo, true, user);

        //禁用服务商站点
        serviceProductService.loseServiceProduct(serviceUserId, serviceInfo.getServiceType(), user);


        //租户服务商关联失效
        tenantServiceRelService.updateTenantServiceRelState(serviceUserId, SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);

        //强制服务商退出系统
        // SysContexts.kickUserEnds(serviceUserId);

        //添加操作日志
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, serviceUserId, SysOperLogConst.OperType.Disable, "停用,原因：" + (StringUtils.isNotBlank(desc) ? desc : "无"));
    }

    @Override
    public ServiceInfoBasisVo getServiceInfo(Long userId) {
        return serviceInfoMapper.getServiceInfo(userId);
    }

    @Override
    public ServiceProdcutByNameVo getServiceProdcutByName(String productName, Long serviceId, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        List<ServiceProduct> list = serviceProductService.getServiceProductByName(productName);
        ServiceProdcutByNameVo serviceProdcutByNameVo = new ServiceProdcutByNameVo();
        serviceProdcutByNameVo.setInfo(0);
        if (list != null && list.size() > 0) {
            TenantProductRel tenantProductRel = tenantProductRelService.getTenantProductRel(baseUser.getTenantId(), list.get(0).getId());
            if (tenantProductRel != null && tenantProductRel.getTenantId().equals(baseUser.getTenantId())) {
                serviceProdcutByNameVo.setInfo(2);
            } else if (serviceId != list.get(0).getServiceUserId()) {
                serviceProdcutByNameVo.setInfo(2);
            } else {
                serviceProdcutByNameVo.setInfo(1);
                serviceProdcutByNameVo.setServiceProduct(list.get(0));
            }
        }
        return serviceProdcutByNameVo;
    }

    @Override
    public Page<ServiceInfoFleetVo> queryServiceInfoPage(Integer pageSize, Integer pageNum, ServiceInfoFleetDto serviceInfoFleetDto, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        //开始分页
        Page<ServiceInfoFleetVo> page = new Page<>(pageNum, pageSize);
        Page<ServiceInfoFleetVo> infoFleetVoPage = serviceInfoMapper.queryServiceInfoPage(page, serviceInfoFleetDto, user);
        List<ServiceInfoFleetVo> pageRecords = infoFleetVoPage.getRecords();
        List<Long> busiIdList = new ArrayList<>();
        List<ServiceInfoFleetVo> serviceInfoFleetVos = serviceInfoFleetTransfer.toServiceInfoFleetVo(busiIdList, pageRecords, user);
        //设置是否有审核权限
        if (CollectionUtils.isNotEmpty(busiIdList)) {
            Map<Long, Boolean> hasPermission = auditNodeInstService.isHasPermission(AuditConsts.AUDIT_CODE.SERVICE_INFO, busiIdList, accessToken);
            for (ServiceInfoFleetVo serviceInfoFleetVo : serviceInfoFleetVos) {
                Boolean flg = hasPermission.get(serviceInfoFleetVo.getRelId());
                try {
                    serviceInfoFleetVo.setHasVer(flg ? 0 : 1);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                if (serviceInfoFleetVo.getServiceUserId() != null) {
                    if (accountBankRelService.isUserTypeBindCardAll(serviceInfoFleetVo.getServiceUserId(), null)) {
                        serviceInfoFleetVo.setIsBindCard(true);
                        serviceInfoFleetVo.setBindTypeName(readisUtil.getSysStaticData("BIND_STATE", com.youming.youche.record.common.EnumConsts.BIND_STATE.BIND_STATE_ALL + "").getCodeName());
                    } else {
                        serviceInfoFleetVo.setIsBindCard(false);
                        serviceInfoFleetVo.setBindTypeName(readisUtil.getSysStaticData("BIND_STATE", com.youming.youche.record.common.EnumConsts.BIND_STATE.BIND_STATE_NONE + "").getCodeName());
                    }
                }
            }
        }
        infoFleetVoPage.setRecords(serviceInfoFleetVos);
        return page;
    }

    @Override
    public ResponseResult doSaveService(ServiceSaveInDto serviceSaveIn, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Integer userType = user.getUserType();
        if (null != userType && userType != SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
            if (StringUtils.isEmpty(serviceSaveIn.getLoginAcct())) {
                throw new BusinessException("账号不能为空，请填写！");
            } else {
                if (CommonUtil.isCheckPhone(serviceSaveIn.getLoginAcct())) {
                    throw new BusinessException("请输入正确的手机号码！");
                }
            }
            if (StringUtils.isEmpty(serviceSaveIn.getServiceName())) {
                throw new BusinessException("服务商名称不能为空，请填写！");
            }
            if (null == serviceSaveIn.getServiceType() || serviceSaveIn.getServiceType() < 0) {
                throw new BusinessException("服务商类型不能为空，请选择！");
            }
//            if (null == serviceSaveIn.getPaymentDays() || serviceSaveIn.getPaymentDays() < 0) {
//                throw new BusinessException("账期不能为空，请选择！");
//            }
            if (null == serviceSaveIn.getIsBill() || serviceSaveIn.getIsBill() < 0) {
//                throw new BusinessException("是否开票不能为空，请选择！");
                serviceSaveIn.setIsBill(1);
            }

        }
        if (StringUtils.isEmpty(serviceSaveIn.getCompanyAddress())) {
            throw new BusinessException("公司地址不能为空，请填写！");
        }

        if (serviceSaveIn.getIsBillAbility() == null || serviceSaveIn.getIsBillAbility() < 0) {
//            throw new BusinessException("开票能力不能为空，请选择！");
            serviceSaveIn.setIsBillAbility(ServiceConsts.IS_BILL_ABILITY.YSE);
        }
        /*if (serviceSaveIn.getIsBillAbility() == ServiceConsts.IS_BILL_ABILITY.NO && serviceSaveIn.getIsBill() ==  1) {
            throw new BusinessException("该服务商没有开票能力，不能选择开票！");
        }*/
        if (StringUtils.isNotBlank(serviceSaveIn.getReceiptCode()) && StringUtils.isBlank(serviceSaveIn.getReceiptLoginAcct())) {
            throw new BusinessException("上传了微信收款码，微信账号不能为空！");
        }

        //微信端修改服务商信息判断服务商有对应站点不允许修改类型
        if (serviceSaveIn.getServiceUserId() != null && serviceSaveIn.getServiceUserId() > 0) {
            LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
            lambda.eq(ServiceInfo::getServiceUserId, serviceSaveIn.getServiceUserId());
            ServiceInfo serviceInfo = this.getOne(lambda);
            if (serviceInfo == null) {
                throw new BusinessException("未找到服务商信息！");
            }
            List<ServiceProduct> products = serviceProductService.getProductByServiceUserId(serviceSaveIn.getServiceUserId());
            if (products != null && products.size() > 0 && !serviceInfo.getServiceType().equals(serviceSaveIn.getServiceType())) {
                throw new BusinessException("该服务商存在对应站点，不允许修改服务商类型！");
            }
        }


        Long serviceUserId = serviceSaveIn.getServiceUserId();
        if (CommonUtil.isNotBlankToLong(serviceUserId)) {
            return updateServiceInfoObms(serviceSaveIn, user);
        } else {
            return saveServiceInfo(serviceSaveIn, user, accessToken);
        }
    }

    @Override
    public Page<ServiceInvitationVo> queryServiceInvitation(ServiceInvitationDto serviceInvitationDto, String accessToken, Integer pageSize, Integer pageNum) {
        LoginInfo user = loginUtils.get(accessToken);
        Page<ServiceInvitationVo> page = new Page<>(pageNum, pageSize);
        Page<ServiceInvitationVo> serviceInvitationVoPage = serviceInfoMapper.queryServiceInvitation(page, serviceInvitationDto, user);
        List<ServiceInvitationVo> invitationVos = serviceInvitationVoPage.getRecords();
        List<ServiceInvitationVo> invitationVoList = serviceInvitationVoTransfer.toServiceInvitationVo(invitationVos);
        serviceInvitationVoPage.setRecords(invitationVoList);
        return page;
    }

    @Override
    public InvitationInfoVo getInvitationInfo(Long serviceId, Long id, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        InvitationInfoVo invitationInfoVo = new InvitationInfoVo();
        UserDataInfo userDataInfo = userDataInfoService.getById(serviceId);
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, serviceId);
        ServiceInfo serviceInfo = this.getOne(lambda);
        if (userDataInfo != null) {
            if (userDataInfo.getLinkman() != null) {
                invitationInfoVo.setLinkman(userDataInfo.getLinkman());
            }
            if (userDataInfo.getLinkman() != null) {
                invitationInfoVo.setLinkman(userDataInfo.getLinkman());
            }
            if (userDataInfo.getMobilePhone() != null) {
                invitationInfoVo.setLoginAcct(userDataInfo.getMobilePhone());
            }
            if (userDataInfo.getUserPrice() != null) {
                invitationInfoVo.setUserPrice(userDataInfo.getUserPrice());
            }
        }
        if (serviceInfo != null) {
            if (serviceInfo.getCompanyAddress() != null) {
                invitationInfoVo.setCompanyAddress(serviceInfo.getCompanyAddress());
            }
            if (serviceInfo.getServiceName() != null) {
                invitationInfoVo.setServiceName(serviceInfo.getServiceName());
            }
        }
        if (serviceInfo != null && serviceInfo.getServiceType() != null) {
            String serviceTypeName = readisUtil.getSysStaticData(SYS_SERVICE_BUSI_TYPE, serviceInfo.getServiceType().toString()).getCodeName();
            invitationInfoVo.setServiceTypeName(serviceTypeName);
        }

        ServiceInvitation serviceInvitation = serviceInvitationService.getById(id);
        if (serviceInvitation != null) {
            if (serviceInvitation.getAuthReason() != null) {
                invitationInfoVo.setAuthReason(serviceInvitation.getAuthReason());
            }
            if (serviceInvitation.getApplyReason() != null) {
                invitationInfoVo.setApplyReason(serviceInvitation.getApplyReason());
            }
            if (serviceInvitation.getFileId() != null) {
                invitationInfoVo.setFileId(serviceInvitation.getFileId());
            }
        }

        List<CooperationDto> list = serviceInvitationService.queryCooperationList(serviceInvitation.getId());

        if (list != null && list.size() > 0) {
            for (CooperationDto mapRes : list) {
                /*String floatBalance = DataFormat.getStringKey(mapRes, "floatBalance");
                long fixedBalance = DataFormat.getLongKey(mapRes, "fixedBalance");
                String serviceCharge = DataFormat.getStringKey(mapRes, "serviceCharge");
                long originalPrice = DataFormat.getLongKey(mapRes, "oilPrice");*/
               /* long oilPrice = serviceProductTF.calculationSharePrice(floatBalance, fixedBalance, originalPrice, serviceCharge);
                mapRes.put("oilPrice", oilPrice);*/
//                mapRes.put("productInfo",getProductInfo(floatBalance,fixedBalance,serviceCharge,serviceInfo.getServiceType()));
                invitationInfoVo.setProductName(mapRes.getProductName());
                invitationInfoVo.setProductId(mapRes.getProductId());
                invitationInfoVo.setProvinceId(mapRes.getProvinceId());
                invitationInfoVo.setCityId(mapRes.getCityId());
                invitationInfoVo.setCountyId(mapRes.getCountyId());
                cooperationTenantTransfer.getProductInfo(invitationInfoVo, mapRes, serviceInfo.getServiceType());
                if (invitationInfoVo.getProvinceId() != null) {
                    String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE, mapRes.getProvinceId().toString()).getCodeName();
                    mapRes.setProvinceName(provinceName);
                }
                if (invitationInfoVo.getCityId() != null) {
                    String cityName = readisUtil.getSysStaticData(SYS_CITY, mapRes.getCityId().toString()).getCodeName();
                    mapRes.setCityName(cityName);
                }
                if (invitationInfoVo.getCountyId() != null) {
                    String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, mapRes.getCountyId().toString()).getCodeName();
                    mapRes.setCountyName(countyName);
                }
//                CommonUtil.queryResult(mapRes, "provinceId", 2, "");
//                CommonUtil.queryResult(mapRes, "cityId", 3, "");
//                CommonUtil.queryResult(mapRes, "countyId", 4, "");

            }
        }

        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), serviceInfo.getServiceUserId());
        if (tenantServiceRel != null) {
            invitationInfoVo.setIsBill(tenantServiceRel.getIsBill());
            invitationInfoVo.setPaymentDays(tenantServiceRel.getPaymentDays());
            invitationInfoVo.setPaymentMonth(tenantServiceRel.getPaymentMonth());
            invitationInfoVo.setBalanceType(tenantServiceRel.getBalanceType());
            if (null != tenantServiceRel.getQuotaAmt()) {
                invitationInfoVo.setQuotaAmtStr(CommonUtil.divide(tenantServiceRel.getQuotaAmt()));
            } else {
                invitationInfoVo.setQuotaAmtStr("");
            }
            if (null != tenantServiceRel.getUseQuotaAmt() && tenantServiceRel.getUseQuotaAmt() > 0) {
                invitationInfoVo.setUseQuotaAmtStr(CommonUtil.divide(tenantServiceRel.getUseQuotaAmt()));
            } else {
                invitationInfoVo.setUseQuotaAmtStr("");
            }
        }
        invitationInfoVo.setApplyList(list);
        invitationInfoVo.setInviteId(serviceInvitation.getId());
        if (serviceInvitation.getAuthState() != null) {
            String codeName = readisUtil.getSysStaticData(INVITE_AUTH_STATE, serviceInvitation.getAuthState().toString()).getCodeName();
            invitationInfoVo.setAuthStateName(codeName);
        }
        invitationInfoVo.setServiceType(serviceInfo.getServiceType());
        if (invitationInfoVo.getBalanceType() != null) {
            Integer balanceType = invitationInfoVo.getBalanceType();
            if (balanceType != null && balanceType == 1) {
                invitationInfoVo.setBalanceTypeName("账期");
            } else if (balanceType != null && balanceType == 2) {
                invitationInfoVo.setBalanceTypeName("月结");
            } else if (balanceType != null && balanceType == 3) {
                invitationInfoVo.setBalanceTypeName("无账期");
            }
        }
        if (invitationInfoVo.getProvinceId() != null) {
            String provinceName = readisUtil.getSysStaticData(SYS_PROVINCE, invitationInfoVo.getProvinceId().toString()).getCodeName();
            invitationInfoVo.setProvinceName(provinceName);
        }
        if (invitationInfoVo.getCityId() != null) {
            String cityName = readisUtil.getSysStaticData(SYS_CITY, invitationInfoVo.getCityId().toString()).getCodeName();
            invitationInfoVo.setCityName(cityName);
        }
        if (invitationInfoVo.getCountyId() != null) {
            String countyName = readisUtil.getSysStaticData(SYS_DISTRICT, invitationInfoVo.getCountyId().toString()).getCodeName();
            invitationInfoVo.setCountyName(countyName);
        }
        return invitationInfoVo;
    }


    @Override
    public Boolean delService(Long userId, String accessToken) {

        if (userId < 0) {
            throw new BusinessException("请选择需要移除的数据！");
        }
        LoginInfo user = loginUtils.get(accessToken);
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, userId);
        ServiceInfo serviceInfo = this.getOne(lambda);

        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), userId);
        if (tenantServiceRel.getState() != SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            throw new BusinessException("服务商状态不是无效，不能移除！");
        }
        //添加历史原因
        TenantServiceRelVer tenantServiceRelVer = new TenantServiceRelVer();
        BeanUtils.copyProperties(tenantServiceRel, tenantServiceRelVer);
        tenantServiceRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.YES);
        tenantServiceRelVer.setRelId(tenantServiceRel.getId());
        tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVer, false, user);

        //删除服务商关系
        tenantServiceRelService.remove(tenantServiceRel.getId());


        //找到该租户与该服务商合作站点，进行删除
        tenantProductRelService.delTenantProduct(userId, serviceInfo.getServiceType(), user.getTenantId(), user);
        if (serviceInfo.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
            //失效oil卡
            oilCardManagementService.loseOilCard(serviceInfo.getServiceUserId(), user);
        } else if (serviceInfo.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {
            //失效etc
            etcMaintainService.loseEtc(serviceInfo.getServiceUserId(), user);
        }
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Remove, "移除");
        return true;
    }

    @Override
    @Transactional
    public ApplyServiceVo applyService(ServiceSaveInDto serviceSaveIn, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        Long userId = serviceSaveIn.getServiceUserId();
        if (StringUtils.isBlank(serviceSaveIn.getServiceName())) {
            throw new BusinessException("服务商名称不能为空，请填写");
        }
        if (serviceSaveIn.getServiceType() == null && serviceSaveIn.getServiceType() < 0) {
            throw new BusinessException("服务商类型不能为空，请选择！");
        }
        if (StringUtils.isBlank(serviceSaveIn.getCompanyAddress())) {
            throw new BusinessException("公司地址不能为空，请填写！");
        }
        if (serviceSaveIn.getIsBillAbility() == null && serviceSaveIn.getIsBillAbility() < 0) {
            throw new BusinessException("开票能力不能为空，请选择！");
        }
        if (serviceSaveIn.getServiceType() != OIL_CARD) {
           /* if(serviceSaveIn.getIsBill() < 0){
                throw new BusinessException("是否开票不能为空，请选择！");
            }*/
//            if (null == serviceSaveIn.getPaymentDays() && serviceSaveIn.getPaymentDays() < 0) {
//                throw new BusinessException("账期不能为空，请填写！");
//            }
        }
        int isRegister = serviceSaveIn.getIsRegister();

        ServiceInfo serviceInfo = null;
        TenantServiceRel tenantServiceRel = null;
        boolean isServiceRel = true;//是否存在服务商和租户关系
        if (isRegister == 1) {
            //保存服务商信息
            serviceInfo = new ServiceInfo();
            serviceInfo.setServiceUserId(userId);
            serviceInfo.setServiceName(serviceSaveIn.getServiceName());
            serviceInfo.setServiceType(serviceSaveIn.getServiceType());
            serviceInfo.setCompanyAddress(serviceSaveIn.getCompanyAddress());
            serviceInfo.setIsBillAbility(serviceSaveIn.getIsBillAbility());
            this.doSaveOrUpdate(serviceInfo, false, user);
            isServiceRel = false;
        } else if (isRegister == 2) {
            tenantServiceRel = tenantServiceRelService.getTenantServiceRel(user.getTenantId(), userId);
            if (tenantServiceRel == null) {
                isServiceRel = false;
            }
        }
        if (!isServiceRel) {
            //保存服务商与租户关系
            tenantServiceRel = new TenantServiceRel();
        }
//        long zhouYouServiceUserId = Long.parseLong(String.valueOf(SysCfgUtil.getCfgVal("ZHAOYOU_TRADE_SERVICE_USER_ID", 0, String.class)));
//        if (zhouYouServiceUserId == userId) {
//            throw new BusinessException("该共享服务商暂不支持合作！");
//        }
        tenantServiceRel.setTenantId(user.getTenantId());
        tenantServiceRel.setServiceUserId(userId);
        tenantServiceRel.setPaymentDays(serviceSaveIn.getPaymentDays());
        tenantServiceRel.setBalanceType(serviceSaveIn.getBalanceType());
        tenantServiceRel.setPaymentMonth(serviceSaveIn.getPaymentMonth());
        tenantServiceRel.setIsBill(serviceSaveIn.getIsBill());
        if (serviceSaveIn.getQuotaAmt() != null) {
            tenantServiceRel.setQuotaAmt((long) (serviceSaveIn.getQuotaAmt() * 100));
        }
        tenantServiceRel.setIsAuth(IS_AUTH1);
        tenantServiceRel.setAuthState(AUTH_PASS);
        tenantServiceRel.setInvitationState(WAIT);//邀请状态
        tenantServiceRel.setCreateTime(LocalDateTime.now());
        tenantServiceRel.setCreatorId(user.getId());
        tenantServiceRel.setAuthState(SysStaticDataEnum.CUSTOMER_AUTH_STATE.WAIT_AUTH);
        tenantServiceRel.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        tenantServiceRelService.saveOrUpdate(tenantServiceRel);


        boolean isInvitation = false;
        //保存邀请
        List<Long> paramStr = serviceSaveIn.getApplyList();
        ServiceInvitation serviceInvitation = null;
        if (serviceSaveIn.getServiceType() == ETC || paramStr != null) {
            if (StringUtils.isBlank(serviceSaveIn.getApplyReason())) {
                throw new BusinessException("邀请原因不能为空，请填写！");
            }
            serviceInvitation = new ServiceInvitation();
            BeanUtils.copyProperties(serviceSaveIn, serviceInvitation);
            serviceInvitation.setTenantId(user.getTenantId());
            serviceInvitation.setUserId(userId);
            serviceInvitation.setServiceUserId(serviceSaveIn.getServiceUserId());
            serviceInvitation.setCooperationType(ServiceConsts.COOPERATION_TYPE.NEW_COOPERATION);

            serviceInvitation.setAuthState(SysStaticDataEnum.INVITE_AUTH_STATE.WAIT);
            serviceInvitation.setCreateTime(LocalDateTime.now());
            serviceInvitation.setCreateDate(LocalDateTimeUtil.presentTime());
            serviceInvitation.setIsRead(ServiceConsts.IS_READ.NO);
            serviceInvitationService.saveOrUpdate(serviceInvitation);

            //保存历史记录
            ServiceInvitationVer serviceInvitationVer = new ServiceInvitationVer();
            BeanUtils.copyProperties(serviceInvitation, serviceInvitationVer);
            serviceInvitationVer.setUpdateOpId(user.getId());
            serviceInvitationVer.setUpdateDate(LocalDateTimeUtil.presentTime());
            serviceInvitationVer.setId(null);
            serviceInvitationVerService.save(serviceInvitationVer);
            isInvitation = true;
        }
        if (paramStr != null && paramStr.size() > 0) {
//            paramStr = HtmlEncoder.decode(paramStr);
//            List applyList = JsonHelper.fromJson(paramStr, List.class);
//            if(applyList != null && applyList.size() > 0){
//                List<Long> productIds = new ArrayList<>();
//                for(Object obj : applyList){
//                    Map<String,Object> map = JsonHelper.parseJSON2Map(JsonHelper.json(obj));
//                    long productId = DataFormat.getLongKey(map,"productId");
//                    productIds.add(productId);
//                }
            List<TenantProductRelOutDto> tenantProductRelOuts = tenantProductRelService.getTenantProductList(SysStaticDataEnum.PT_TENANT_ID, paramStr);
            if (tenantProductRelOuts != null && tenantProductRelOuts.size() > 0) {
                for (TenantProductRelOutDto tenantProductRelOut : tenantProductRelOuts) {
                    ServiceInvitationDtl serviceInvitationDtl = new ServiceInvitationDtl();

                    //不开票价格
                    serviceInvitationDtl.setFloatBalance(tenantProductRelOut.getFloatBalance());
                    serviceInvitationDtl.setFixedBalance(tenantProductRelOut.getFixedBalance());
                    serviceInvitationDtl.setServiceCharge(tenantProductRelOut.getServiceCharge());

                    //开票价格
                    serviceInvitationDtl.setFixedBalanceBill(tenantProductRelOut.getFixedBalanceBill());
                    serviceInvitationDtl.setFloatBalanceBill(tenantProductRelOut.getFloatBalanceBill());
                    serviceInvitationDtl.setServiceChargeBill(tenantProductRelOut.getServiceChargeBill());

                    serviceInvitationDtl.setInviteId(serviceInvitation.getId());
                    serviceInvitationDtl.setProductId(tenantProductRelOut.getProductId());
                    serviceInvitationDtlService.save(serviceInvitationDtl);
                    serviceInvitationDtlVerService.checkHisSetNot(serviceInvitationDtl.getId(), user);
                    //保存历史记录
                    ServiceInvitationDtlVer serviceInvitationDtlVer = new ServiceInvitationDtlVer();
                    BeanUtils.copyProperties(serviceInvitationDtl, serviceInvitationDtlVer);
                    serviceInvitationDtlVer.setIsUse(ServiceConsts.COOPERATION_USE.YES);
                    serviceInvitationDtlVer.setUpdateOpId(user.getId());
                    serviceInvitationDtlVer.setUpdateTime(LocalDateTimeUtil.presentTime());
                    serviceInvitationDtlVer.setId(serviceInvitationDtl.getId());
                    serviceInvitationDtlVerService.save(serviceInvitationDtlVer);
                }
            }
//            }
        }


        if (!isServiceRel) {
            //保存历史消息
            TenantServiceRelVer tenantServiceRelVer = new TenantServiceRelVer();
            BeanUtils.copyProperties(tenantServiceRel, tenantServiceRelVer);
            tenantServiceRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.NO);
            tenantServiceRelVer.setRelId(tenantServiceRel.getId());
            tenantServiceRelVerService.saveTenantServiceRelHis(tenantServiceRelVer, false, user);
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Add, "新增");


        }

        ApplyServiceVo map = new ApplyServiceVo();
        map.setInfo(2);
        if (isInvitation) {
            map.setInfo(1);
        }
        if (null != serviceInvitation) {
            if (serviceInvitation.getId() != null) {
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.serviceInvitation, serviceInvitation.getId(), SysOperLogConst.OperType.Add, "邀请合作");
            }
        }

        return map;
    }

    @Override
    public void sucess(Long busiId, String desc, Map paramsMap, String token) {
        LoginInfo user = loginUtils.get(token);

        //IBaseBusi baseBusiSV = (BaseBusiSV) SysContexts.getBean("baseBusiSV");

        //校验是否修改是否开票和账期，如果修改则要进入服务商审核
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getById(busiId);
        Long id = null;
        if (tenantServiceRel.getId() != null) {
            id = tenantServiceRel.getId();
        }
        if (tenantServiceRel == null) {
            throw new BusinessException("未找到本车队服务商信息！");
        }

        TenantServiceRelVer tenantServiceRelVer = tenantServiceRelVerService.getTenantServiceRelVer(busiId);
        if (tenantServiceRelVer == null) {
            throw new BusinessException("未找到服务商修改记录！");
        }
        LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
        lambda.eq(ServiceInfo::getServiceUserId, tenantServiceRel.getServiceUserId());
        ServiceInfo serviceInfo = this.getOne(lambda);
        if (null == serviceInfo) {
            throw new BusinessException("未找到服务商记录！");
        }
        Long quotaAmt = tenantServiceRel.getQuotaAmt();
        if (quotaAmt == null) {
            quotaAmt = 0L;
        }
        Long amt = tenantServiceRelVer.getQuotaAmt();
        if (amt == null) {
            amt = 0L;
        }
        if ((tenantServiceRel.getIsBill() != null && tenantServiceRelVer.getIsBill() != null && !tenantServiceRel.getIsBill().equals(tenantServiceRelVer.getIsBill())
                || tenantServiceRel.getBalanceType() != null && tenantServiceRelVer.getBalanceType() != null && !tenantServiceRel.getBalanceType().equals(tenantServiceRelVer.getBalanceType())
                || tenantServiceRel.getPaymentDays() != null && tenantServiceRelVer.getPaymentDays() != null && !tenantServiceRel.getPaymentDays().equals(tenantServiceRelVer.getPaymentDays())
                || tenantServiceRel.getPaymentMonth() != null && tenantServiceRelVer.getPaymentMonth() != null && !tenantServiceRel.getPaymentMonth().equals(tenantServiceRelVer.getPaymentMonth())
                || !Objects.equals(tenantServiceRelVer.getQuotaAmt(), tenantServiceRel.getQuotaAmt()) || !quotaAmt.equals(amt))
                && serviceInfo.getServiceType() != OIL_CARD) {
            //生成一条修改申请合作信息
//            if (tenantServiceRel.getQuotaAmt() == null || tenantServiceRel.getQuotaAmt() == 0) {
//            }
            serviceInvitationService.saveInvitation(user, -1L, tenantServiceRel.getTenantId(), -1L,
                    null, -1L, null, null, tenantServiceRel.getServiceUserId(),
                    ServiceConsts.COOPERATION_TYPE.UPDATE_COOPERATION, null);
            tenantServiceRel.setInvitationState(ServiceConsts.SERVICE_AUTH_STATE.WAIT);
        } else {
            BeanUtils.copyProperties(tenantServiceRelVer, tenantServiceRel);
//            if (tenantServiceRel.getQuotaAmt() == null || tenantServiceRel.getQuotaAmt() == 0) {
//            }
            tenantServiceRel.setIsAuth(SysStaticDataEnum.IS_AUTH.IS_AUTH0);
        }
//        BeanUtils.copyProperties(tenantServiceRelVer, tenantServiceRel);
        tenantServiceRel.setPaymentDays(tenantServiceRelVer.getPaymentDays());
        tenantServiceRel.setAuthState(AUTH_PASS);
        tenantServiceRel.setAuthReason(null);
        tenantServiceRel.setOpId(user.getId());
        tenantServiceRel.setUpdateTime(LocalDateTime.now());
        if (id != null) {
            tenantServiceRel.setId(id);
        }
        tenantServiceRelService.update(tenantServiceRel);
       /* Object[] objects = baseBusiSV.doPublicAuth(TenantServiceRel.class,TenantServiceRelVer.class,busiId,
                SysOperLogConst.BusiCode.ServiceInfo,SysStaticDataEnum.CUSTOMER_AUTH_STATE.AUTH_PASS,desc
                ,"relId","setAuthState","setAuthReason");
        TenantServiceRel tenantServiceRel = (TenantServiceRel) objects[0];*/
        if (tenantServiceRel != null && tenantServiceRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
            serviceProductService.loseTenantProduct(user, tenantServiceRel.getServiceUserId(), serviceInfo.getServiceType(), user.getTenantId(), true);
            if (serviceInfo.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.OIL) {
                //失效oil卡
                oilCardManagementService.loseOilCard(serviceInfo.getServiceUserId(), user);
            } else if (serviceInfo.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.ETC) {
                //失效etc
                etcMaintainService.loseEtc(serviceInfo.getServiceUserId(), user);
            }
        }
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Audit, "审核通过" + (StringUtils.isNotBlank(desc) ? "，审核意见:" + desc : ""));
    }

    @Override
    public void fail(Long busiId, String desc, Map paramsMap, String token) {
        LoginInfo user = loginUtils.get(token);
        LambdaQueryWrapper<TenantServiceRel> lambda = new QueryWrapper<TenantServiceRel>().lambda();
        lambda.eq(TenantServiceRel::getId, busiId);
        TenantServiceRel tenantServiceRel = tenantServiceRelService.getOne(lambda);
        LambdaQueryWrapper<TenantServiceRelVer> lambdaTenantServiceRelVer = new QueryWrapper<TenantServiceRelVer>().lambda();
        lambdaTenantServiceRelVer.eq(TenantServiceRelVer::getRelId, busiId).orderByDesc(TenantServiceRelVer::getId)
                .last("limit 0,1");
        TenantServiceRelVer serviceRelVerServiceOne = tenantServiceRelVerService.getOne(lambdaTenantServiceRelVer);
        ServiceInfoVo serviceInfoVo = serviceInfoTransfer.acquireServiceInfoVo(serviceRelVerServiceOne.getServiceUserId());
        Long id = null;
        if (tenantServiceRel.getId() != null) {
            id = tenantServiceRel.getId();
        }
        Integer authState = tenantServiceRel.getAuthState();
        Integer isBill1 = tenantServiceRel.getIsBill();
        BeanUtils.copyProperties(serviceInfoVo, tenantServiceRel);
        tenantServiceRel.setIsAuth(1);
        tenantServiceRel.setIsBill(isBill1);
        tenantServiceRel.setAuthState(authState);
        tenantServiceRel.setAuthReason(desc);
        if (id != null) {
            tenantServiceRel.setId(id);
        }
        tenantServiceRelService.updateById(tenantServiceRel);
        Integer isBill = serviceRelVerServiceOne.getIsBill();
        BeanUtils.copyProperties(serviceInfoVo, serviceRelVerServiceOne);
        serviceRelVerServiceOne.setRelId(tenantServiceRel.getId());
        serviceRelVerServiceOne.setId(null);
        serviceRelVerServiceOne.setIsBill(isBill);
        tenantServiceRelVerService.save(serviceRelVerServiceOne);
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Audit, "审核不通过" + (StringUtils.isNotBlank(desc) ? "，审核意见:" + desc : ""));
    }

    @Override
    public Boolean isService(Long userId, Long tenantId) {
        if (null == tenantId) {
            return isService(userId);
        }

        TenantServiceRel tenantServiceRel = tenantServiceRelService.getTenantServiceRel(userId, tenantId);
        if (null != tenantServiceRel) {
            return true;
        }

        return CollectionUtils.isNotEmpty(serviceProductService.getServiceProductList(userId, tenantId));
    }

    @Override
    public ServiceInfoVo seeServiceInfo(Long serviceUserId) {
        return serviceInfoTransfer.acquireServiceInfoVo(serviceUserId);
    }

    /**
     * 保存服务商信息
     *
     * @param serviceSaveIn
     * @return
     * @throws Exception
     */
    public ResponseResult saveServiceInfo(ServiceSaveInDto serviceSaveIn, LoginInfo user, String accessToken) {
        UserDataInfo userDataInfo = userDataInfoService.getUserDataInfoByLoginAcct(serviceSaveIn.getLoginAcct());
        String password = null;
        //保存用户信息 -- 开始
        if (userDataInfo == null) {
            if (StringUtils.isEmpty(serviceSaveIn.getLinkman())) {
                throw new BusinessException("联系人姓名不能为空，请填写！");
            }
            if (StringUtils.isEmpty(serviceSaveIn.getIdentification())) {
                throw new BusinessException("身份证号不能为空，请填写！");
            }
            if (serviceSaveIn.getIdenPictureFront() == null || serviceSaveIn.getIdenPictureFront() < 0) {
                throw new BusinessException("身份证正面图片不能为空，请上传！");
            }
            if (StringUtils.isEmpty(serviceSaveIn.getIdenPictureFrontUrl())) {
                throw new BusinessException("身份证号正面url不能为空，请填写！");
            }
            if (serviceSaveIn.getIdenPictureBack() == null || serviceSaveIn.getIdenPictureBack() < 0) {
                throw new BusinessException("身份证背面图片不能为空，请上传！");
            }
            if (StringUtils.isEmpty(serviceSaveIn.getIdenPictureBackUrl())) {
                throw new BusinessException("身份证背面url不能为空，请填写！");
            }
            ServiceUserInDto serviceUserIn = new ServiceUserInDto();
            BeanUtils.copyProperties(serviceSaveIn, serviceUserIn);
            //如果是违章、保险类型服务商则设置随机六位数的密码
            if (serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.VR || serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.INS) {
                serviceUserIn.setPassword(String.valueOf((int) ((Math.random() * 9 + 1) * 100000)));
                password = serviceUserIn.getPassword();
            }
            Map map = serviceUserService.saveServiceUser(serviceUserIn, user, serviceSaveIn);
            Object dataInfo = map.get("userDataInfo");
            userDataInfo = (UserDataInfo) dataInfo;
            if (userDataInfo == null) {
                throw new BusinessException("保存用户信息有误！");
            }
            SysUser sysOperator = (SysUser) map.get("operator");
            if (sysOperator == null) {
                throw new BusinessException("保存用户信息有误！");
            }
        } else if (serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.VR || serviceSaveIn.getServiceType() == SysStaticDataEnum.SERVICE_BUSI_TYPE.INS) {
            password = passwordEncoder.encode(userDataInfo.getAccPassword());
        }
        //保存用户信息 -- 结束

        //保存服务商 --- 开始
        ServiceInfo serviceInfo = new ServiceInfo();
        BeanUtils.copyProperties(serviceSaveIn, serviceInfo);
        serviceInfo.setServiceUserId(userDataInfo.getId());
        serviceInfo.setAuthFlag(SysStaticDataEnum.EXPENSE_STATE.END);
        serviceInfo.setIsAuth(AUTH_PASS);
        serviceInfo.setLogoId(null);
        serviceInfo.setLogoUrl("image/discountsLogo.png");
        serviceUserService.doSaveOrUpdate(serviceInfo, false, user);
        ServiceInfoVer serviceInfoVer = new ServiceInfoVer();
        BeanUtils.copyProperties(serviceInfo, serviceInfoVer);
        serviceInfoVerService.saveServiceInfoHis(serviceInfoVer, user);
        //保存服务商 -- 结束

        //保存服务商与租户关系  --开始
        TenantServiceRel tenantServiceRel = new TenantServiceRel();
        tenantServiceRel.setIsBill(serviceSaveIn.getIsBill());
        tenantServiceRel.setPaymentDays(serviceSaveIn.getPaymentDays());
        tenantServiceRel.setBalanceType(serviceSaveIn.getBalanceType());
        tenantServiceRel.setPaymentMonth(serviceSaveIn.getPaymentMonth());
        tenantServiceRel.setServiceUserId(serviceInfo.getServiceUserId());
        tenantServiceRel.setTenantId(user.getTenantId());
        tenantServiceRel.setIsAuth(IS_AUTH1);
        if (serviceSaveIn.getQuotaAmt() != null) {
            Double price = serviceSaveIn.getQuotaAmt() * 100;
            tenantServiceRel.setQuotaAmt(price.longValue());
        }

        tenantServiceRelService.saveOrUpdateTenantServiceRel(tenantServiceRel, false, user);
        //保存服务商与租户关系  --结束


        serviceSaveIn.setServiceUserId(serviceInfo.getServiceUserId());
        //保存账户信息 -- 开始
        //对公
//        if(StringUtils.isNotBlank(serviceSaveIn.getPuAcctName()) ||
//                StringUtils.isNotBlank(serviceSaveIn.getPuAcctNo())||
//                StringUtils.isNotBlank(serviceSaveIn.getPuBankId())||
//                StringUtils.isNotBlank(serviceSaveIn.getPuBankName())||
//                StringUtils.isNotBlank(serviceSaveIn.getPuBranchName())||
//                (serviceSaveIn.getPuProvinceId() != null && serviceSaveIn.getPuProvinceId() > 0)){
//            saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_4,serviceSaveIn);
//        }
        //对私
//        if(StringUtils.isNotBlank(serviceSaveIn.getPvAcctName()) ||
//                StringUtils.isNotBlank(serviceSaveIn.getPvAcctNo())||
//                StringUtils.isNotBlank(serviceSaveIn.getPvBankId())||
//                StringUtils.isNotBlank(serviceSaveIn.getPvBankName())||
//                StringUtils.isNotBlank(serviceSaveIn.getPvBranchName())||
//                (serviceSaveIn.getPvProvinceId() != null && serviceSaveIn.getPvProvinceId() > 0)){
//            saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,serviceSaveIn);
//        }

        //微信账户信息
//        if(StringUtils.isNotBlank(serviceSaveIn.getReceiptLoginAcct())){
//            BankRelIn weChatBank = new BankRelIn();
//            saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_2,serviceSaveIn);
//        }
        //保存账户信息 -- 结束

        //保存历史消息
        TenantServiceRelVer tenantServiceRelVer = new TenantServiceRelVer();
        BeanUtils.copyProperties(tenantServiceRel, tenantServiceRelVer);
        tenantServiceRelVer.setIsDel(EnumConsts.SERVICE_IS_DEL.NO);
        tenantServiceRelVer.setRelId(tenantServiceRel.getId());
        tenantServiceRelVer.setUpdateOpId(user.getId());
        tenantServiceRelVer.setUpdateTime(LocalDateTime.now());
        tenantServiceRelVer.setId(null);
        tenantServiceRelVerService.saveTenantServiceRelVer(tenantServiceRelVer);
        //启动审核流程
        Map inMap = new HashMap();
        inMap.put("svName", "serviceInfoTF");
        boolean bool = auditService.startProcess(AuditConsts.AUDIT_CODE.SERVICE_INFO, tenantServiceRel.getId(), SysOperLogConst.BusiCode.ServiceInfo, inMap, accessToken);
        if (!bool) {
            throw new BusinessException("启动审核流程失败！");
        }
//        /**发送短信*/
//        SysSmsSendSV sysSmsSendSV = (SysSmsSendSV) SysContexts.getBean("smsSendSV");
//        if(StringUtils.isNotBlank(password)) {
//            sysSmsSendSV.addVoilationServiceInfo(serviceSaveIn.getLoginAcct(),userDataInfo.getUserId(),serviceInfo.getServiceName(),serviceSaveIn.getLoginAcct(),password);
//        }else {
//            sysSmsSendSV.addServiceInfo(serviceSaveIn.getLoginAcct(),userDataInfo.getUserId(),serviceInfo.getServiceName(),serviceSaveIn.getLoginAcct());
//        }

        //操作日志
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, tenantServiceRel.getId(), SysOperLogConst.OperType.Add, "新增");
        return ResponseResult.success();
    }


    /**
     * 修改服务商信息
     *
     * @param serviceSaveIn
     * @return
     * @throws Exception
     */
    public ResponseResult updateServiceInfoObms(ServiceSaveInDto serviceSaveIn, LoginInfo user) {
        long serviceUserId = serviceSaveIn.getServiceUserId();
        LambdaQueryWrapper<ServiceInfo> lambdaServiceInfo = new QueryWrapper<ServiceInfo>().lambda();
        lambdaServiceInfo.eq(ServiceInfo::getServiceUserId, serviceUserId);
        ServiceInfo serviceInfo = serviceInfoMapper.selectOne(lambdaServiceInfo);
        if (serviceInfo == null) {
            throw new BusinessException("无该服务商！");
        }
        UserDataInfo userDataInfo = userDataInfoService.getById(serviceUserId);
        if ((null == serviceSaveIn.getIdentification() && null != userDataInfo.getIdentification()) ||
                !serviceSaveIn.getIdentification().equals(userDataInfo.getIdentification()) ||
                (null == serviceSaveIn.getLinkman() && null != userDataInfo.getLinkman()) ||
                !serviceSaveIn.getLinkman().equals(userDataInfo.getLinkman())) {
            driverInfoExtService.updateLuGeAuthState(userDataInfo.getId(), false, null, 0);
        }

        UserDataInfoInDto userDataInfoIn = new UserDataInfoInDto();
        BeanUtils.copyProperties(serviceSaveIn, userDataInfoIn);
        userDataInfoIn.setUserId(serviceUserId);
        userDataInfoService.modifyUserDataInfo(userDataInfoIn, user);


        serviceInfo.setIsBillAbility(serviceSaveIn.getIsBillAbility());
        serviceInfo.setCompanyAddress(serviceSaveIn.getCompanyAddress());

        if (serviceInfo.getIsBillAbility() == ServiceConsts.IS_BILL_ABILITY.NO) {
            serviceProductService.notBillAbility(serviceInfo.getServiceUserId(), user);
        }
        doSaveOrUpdate(serviceInfo, true, user);

//        IAccountBankRelSV accountBankRelSV = (AccountBankRelSV) SysContexts.getBean("accountBankRelSV");
//        AccountBankRel puBankInfo = accountBankRelSV.getBankInfo(serviceUserId, EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_4, -1L);
//        保存账户信息 -- 开始
//        对私
//        saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0,serviceSaveIn);
//        对公
//        saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_4,serviceSaveIn);
//        微信
//        saveOrUpdateAccountBank(EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_2,serviceSaveIn);
//        保存账户信息 -- 结束

        return ResponseResult.success();
    }

//    public void  modifyUserName(long userId, String linkman){
//
//    }

    /**
     * 修改服务商信息
     *
     * @param serviceSaveIn
     * @return
     * @throws Exception
     */
    public void updateServiceInfo(ServiceSaveInDto serviceSaveIn, LoginInfo user) {
     //   try {
            Long serviceUserId = serviceSaveIn == null ? null : serviceSaveIn.getServiceUserId();
            if (serviceUserId == null || serviceUserId < 0) {
                throw new BusinessException("未找到该服务商信息！");
            }
            LambdaQueryWrapper<ServiceInfo> lambda = new QueryWrapper<ServiceInfo>().lambda();
            lambda.eq(ServiceInfo::getServiceUserId, serviceUserId);
            ServiceInfo serviceInfo = this.getOne(lambda);
            if (serviceInfo == null) {
                throw new BusinessException("未找到该服务商信息！");
            }

            List<ServiceProduct> products = serviceProductService.getProductByServiceUserId(serviceUserId);
            if (products != null && products.size() > 0 && !serviceInfo.getServiceType().equals(serviceSaveIn.getServiceType())) {
                throw new BusinessException("该服务商存在对应站点，不允许修改服务商类型！");
            }

            //1.修改服务商信息为待审核状态
            serviceInfo.setAuthFlag(SysStaticDataEnum.EXPENSE_STATE.AUDIT);
            doSaveOrUpdate(serviceInfo, true, user);

            //2.添加服务商版本记录
            serviceSaveIn.setState(serviceInfo.getState());
            serviceInfoVerService.saveServiceInfoHis(serviceSaveIn, serviceInfo.getTenantId(), serviceInfo.getIsAuth(), user);

            //3.修改用户信息
            UserDataInfoInDto userDataInfoIn = new UserDataInfoInDto();
            BeanUtils.copyProperties(serviceSaveIn, userDataInfoIn);
            userDataInfoIn.setUserId(serviceUserId);
            userDataInfoService.doUpdateServ(userDataInfoIn);


            //5.添加操作日志
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.ServiceInfo, serviceUserId, SysOperLogConst.OperType.Update, "修改服务商档案");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * 服务商用修改用户信息
     *
     * @param userDataInfoIn
     * @throws Exception
     */
    public void modifyUserDataInfo(UserDataInfoInDto userDataInfoIn) throws Exception {
        if (userDataInfoIn.getUserId() == null || userDataInfoIn.getUserId() <= 0) {
            throw new BusinessException("userId不能为空！");
        }
        UserDataInfo userDatainfo = userDataInfoService.getById(userDataInfoIn.getUserId());
        if (userDatainfo == null) {
            throw new BusinessException("用户信息不存在！");
        }
        BeanUtils.copyProperties(userDataInfoIn, userDatainfo);
        userDataInfoService.save(userDatainfo);
    }

    @Override
    public ServiceInfo getServiceInfoById(long userId) {
        LambdaQueryWrapper<ServiceInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(ServiceInfo::getServiceUserId, userId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public ServiceInfoDto seeServiceInfo(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        ServiceInfo serviceInfo = getServiceInfoById(loginInfo.getUserInfoId());
        if (null == serviceInfo) {
            throw new BusinessException("非服务商登录");
        }
        ServiceInfoDto serviceInfoDto = new ServiceInfoDto();
        BeanUtil.copyProperties(serviceInfo, serviceInfoDto);
        serviceInfoDto.setServiceTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil,
                EnumConsts.SysStaticData.SERVICE_BUSI_TYPE, serviceInfo.getServiceType() + "").getCodeName());
        return serviceInfoDto;
    }

    @Override
    public String seeServiceInfoChek(String phone) {
        List<SysUser> sysUsers = sysUserService.getPhoneUser(phone);
        int i = 0;
        if (sysUsers != null) {
            for (SysUser s : sysUsers
            ) {
                ServiceInfo serviceInfo = getServiceInfoById(s.getUserInfoId());
                if (serviceInfo != null && serviceInfo.getId() > 0) {
                    i++;
                }
            }
        }
        if (i < 1) {
            throw new BusinessException("非服务商登录");
        } else {
            return "Y";
        }
    }

    @Override
    public Map<String, Object> checkServiceShare(Long productId) {
        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("state", "1");
        rtnMap.put("msg", "校验通过");
        if (productId == null || productId < 0) {
            rtnMap.put("state", "0");
            rtnMap.put("msg", "站点参数错误");
            return rtnMap;
        }
        ServiceProduct serviceProduct = serviceProductService.getById(productId);
        if (null == serviceProduct) {
            rtnMap.put("state", "0");
            rtnMap.put("msg", "未找到站点信息");
            return rtnMap;
        }
        if (serviceProduct.getBusinessType() != OIL) {
            rtnMap.put("state", "1");
            rtnMap.put("msg", "校验通过");
            return rtnMap;
        }
        if (null != serviceProduct.getReServiceId() && serviceProduct.getReServiceId() > 0) {
            rtnMap.put("state", "1");
            rtnMap.put("msg", "校验通过");
            return rtnMap;
        } else {
            if (null != serviceProduct.getIsReService() && serviceProduct.getIsReService() == 1) {
                rtnMap.put("state", "0");
                rtnMap.put("msg", "该油站已是共享油站，不需再共享");
                return rtnMap;
            }
        }
        return rtnMap;
    }

    @Override
    public List<WorkbenchDto> getTableManagerCount() {
        return baseMapper.getTableManagerCount();
    }


    @Override
    public void dynamicUpdateServiceInfoBill() {
        List<ServiceInfoFleetVo> infoFleetVos = serviceInfoMapper.queryServiceInfo();
        LocalDateTime now = LocalDateTime.now();
        if (infoFleetVos != null && infoFleetVos.size() > 0) {
            for (ServiceInfoFleetVo infoFleetVo : infoFleetVos) {
                ServiceInfoVo serviceInfoVo = this.seeServiceInfoMotorcade(infoFleetVo.getServiceUserId());
                if (serviceInfoVo != null && serviceInfoVo.getServiceType() != null && (serviceInfoVo.getServiceType() == 1 || serviceInfoVo.getServiceType() == 2)) {
                    List<ServiceProviderBill> serviceProviderBills = new ArrayList<>();
                    List<ServiceProviderBill> billList = getServiceProviderBillList(now, infoFleetVo.getCreateTime(), infoFleetVo.getServiceUserId(), serviceInfoVo, infoFleetVo, serviceProviderBills);
                    if (billList != null && billList.size() > 0) {
                        for (ServiceProviderBill providerBill : billList) {
                            Long id = serviceProviderBillService.saveServiceProviderBillReturnId(providerBill);
                            //操作日志
                            sysOperLogService.saveSysOperLogSys(SysOperLogConst.BusiCode.ServiceInfo, id, SysOperLogConst.OperType.Add, "新增服务商账单", infoFleetVo.getTenantId());
//                            if (providerBill.getPaymentStatus() == 3) {
//                                //操作日志
//                                sysOperLogService.saveSysOperLogSys(SysOperLogConst.BusiCode.ServiceInfo, id, SysOperLogConst.OperType.Add, "系统自动打款", infoFleetVo.getTenantId());
//                            }
                        }
                    }


//                    ServiceProviderBill providerBill = serviceProviderBillService.queryServiceProviderBill(serviceInfoVo.getServiceUserId());
//                    if (providerBill != null) {
//                        setproviderBill(infoFleetVo, amount, billRecords, serviceInfoVo, providerBill);
//                        providerBill.setUpdateTime(LocalDateTime.now());
//                        serviceProviderBillService.update(providerBill);
//                    } else {
//                        ServiceProviderBill serviceProviderBill = new ServiceProviderBill();
//                        setproviderBill(infoFleetVo, amount, billRecords, serviceInfoVo, serviceProviderBill);
//                        serviceProviderBillService.save(serviceProviderBill);
//                    }
                }
            }
        }
    }

//    private void setproviderBill(ServiceInfoFleetVo infoFleetVo, Long amount, Integer billRecords, ServiceInfoVo serviceInfoVo, ServiceProviderBill providerBill) {
//        if (serviceInfoVo != null && serviceInfoVo.getBalanceType() != 3) {
//            //账期
//            if (serviceInfoVo.getBalanceType() == 1) {
//                //账单生成时间
//                if (providerBill != null) {
//                    //账单生成时间
//                    LocalDateTime createTime = providerBill.getCreateTime();
//                    //当前时间
//                    LocalDateTime time = LocalDateTime.now();
//                    //如果 账单最后生成时间是当前时间
//                    if (createTime != null && createTime.isAfter(time)) {
////账期
//                        List<ServiceProviderBill> serviceProviderBills=new ArrayList<>();
//                    }
//                } else {
//
//                }
//            }
//            List<ServiceProduct> serviceProducts = serviceProductService.queryServiceProduct(infoFleetVo.getServiceUserId(), infoFleetVo.getTenantId());
//            if (serviceProducts != null && serviceProducts.size() > 0) {
//                for (ServiceProduct serviceProduct : serviceProducts) {
//                    ConsumeOilFlowVo consumeOilFlowVo = new ConsumeOilFlowVo();
//                    consumeOilFlowVo.setUserId(infoFleetVo.getServiceUserId());
//                    consumeOilFlowVo.setUserType(infoFleetVo.getServiceType());
//                    consumeOilFlowVo.setProductId(serviceProduct.getId());
//                    ConsumeOilFlowWxOutDto dto = consumeOilFlowService.getConsumeOilFlowByWx(consumeOilFlowVo, 0, 500);
//                    if (dto != null && dto.getPage() != null && dto.getPage().getRecords() != null && dto.getPage().getRecords().size() > 0) {
//                        List<ConsumeOilFlowWxDto> records = dto.getPage().getRecords();
//                        for (ConsumeOilFlowWxDto record : records) {
//                            if (record.getState() != null && record.getState() == 0) {
//                                amount += record.getAmount();
//                            }
//                            ++billRecords;
//                        }
//                    }
//                }
//            }
//            providerBill.setBillAmount(amount);
//            providerBill.setBillRecords(billRecords);
//            providerBill.setCreateTime(LocalDateTime.now());
//            String billCycle = "";
//            if (serviceInfoVo.getBalanceType() == 1) {
//                billCycle = serviceInfoVo.getBalanceTypeName() + "/" + serviceInfoVo.getPaymentDays() + "天";
//            }
//            if (serviceInfoVo.getBalanceType() == 2) {
//                billCycle = serviceInfoVo.getBalanceTypeName() + "/" + serviceInfoVo.getPaymentMonth() + "月" + serviceInfoVo.getPaymentDays() + "天";
//            }
//            if (serviceInfoVo.getBalanceType() == 3) {
//                billCycle = "无账期";
//            }
//            providerBill.setBillCycle(billCycle);
//            providerBill.setTenantId(infoFleetVo.getTenantId());
//            providerBill.setServiceProviderType(infoFleetVo.getServiceType());
//            providerBill.setServiceUserId(infoFleetVo.getServiceUserId());
//            providerBill.setServiceProviderName(infoFleetVo.getServiceName());
//        }
//
//    }


    public List<ServiceProviderBill> getServiceProviderBillList(LocalDateTime now, LocalDateTime serviceCreateTime, Long serviceUserId,
                                                                ServiceInfoVo serviceInfoVo, ServiceInfoFleetVo serviceInfoFleetVo, List<ServiceProviderBill> serviceProviderBills) {

        if (serviceInfoVo.getBalanceType() == 1 && serviceInfoVo.getPaymentDays() != null && serviceInfoVo.getPaymentDays() != 0) {
            //账单生成时间
            LocalDateTime dateTime = serviceCreateTime.plusDays(serviceInfoVo.getPaymentDays());
            //如果账单生成时间是 当前时间 之前
            if (now.isAfter(dateTime)) {
                String date = LocalDateTimeUtil.convertDateToString(dateTime);
                String time = date.substring(0, 10);
                time = time + " 23:59:59";
                LocalDateTime convertStringToDate = LocalDateTimeUtil.convertStringToDate(time);
                ServiceProviderBill providerBill = serviceProviderBillService.queryServiceProviderBill(serviceUserId, convertStringToDate, serviceInfoFleetVo.getTenantId());
                if (providerBill == null) {
                    ServiceProviderBill serviceProviderBill = getServiceProviderBill(serviceCreateTime, serviceUserId,
                            serviceInfoVo, serviceInfoFleetVo, time, convertStringToDate);
                    serviceProviderBills.add(serviceProviderBill);
                }
                getServiceProviderBillList(now, convertStringToDate, serviceUserId, serviceInfoVo, serviceInfoFleetVo, serviceProviderBills);
            }
        }

        if (serviceInfoVo.getBalanceType() == 2 && serviceInfoVo.getPaymentMonth() != null && serviceInfoVo.getPaymentDays() != null
                && (serviceInfoVo.getPaymentMonth() != 0 || serviceInfoVo.getPaymentDays() != 0)) {
            //月份
            Integer month = serviceInfoVo.getPaymentMonth();
            //日
            Integer paymentDays = serviceInfoVo.getPaymentDays();
            LocalDateTime dateTime = serviceCreateTime.plusMonths(month).plusDays(paymentDays);
            if (now.isAfter(dateTime)) {
                String date = LocalDateTimeUtil.convertDateToString(dateTime);
                String time = date.substring(0, 10);
                time = time + " 23:59:59";
                LocalDateTime convertStringToDate = LocalDateTimeUtil.convertStringToDate(time);
                ServiceProviderBill providerBill = serviceProviderBillService.queryServiceProviderBill(serviceUserId, convertStringToDate, serviceInfoFleetVo.getTenantId());
                if (providerBill == null) {
                    ServiceProviderBill serviceProviderBill = getServiceProviderBill(serviceCreateTime, serviceUserId,
                            serviceInfoVo, serviceInfoFleetVo, time, convertStringToDate);
                    serviceProviderBills.add(serviceProviderBill);
                }
                getServiceProviderBillList(now, convertStringToDate, serviceUserId, serviceInfoVo, serviceInfoFleetVo, serviceProviderBills);
            }
        }
        return serviceProviderBills;
    }

    private ServiceProviderBill getServiceProviderBill(LocalDateTime serviceCreateTime, Long serviceUserId, ServiceInfoVo serviceInfoVo, ServiceInfoFleetVo serviceInfoFleetVo, String time, LocalDateTime convertStringToDate) {
        //开始时间
        String dateToString = LocalDateTimeUtil.convertDateToString(serviceCreateTime);
        //结束时间
        String convertDateToString = LocalDateTimeUtil.convertDateToString(convertStringToDate);
        ServiceProviderBill serviceProviderBill = new ServiceProviderBill();
        if (serviceInfoVo.getServiceType() == 1) {
            List<ConsumeOilFlowDto> oilFlowDtos = consumeOilFlowService.queryConsumeOilFlow(serviceUserId, dateToString, convertDateToString, serviceInfoFleetVo.getTenantId());
            if (oilFlowDtos != null && oilFlowDtos.size() > 0) {
                Double collect = oilFlowDtos.stream().collect(Collectors.summingDouble(ConsumeOilFlowDto::getAmount));
                BigDecimal bg = new BigDecimal(collect);
                double d1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                serviceProviderBill.setBillAmount(d1);
                serviceProviderBill.setBillRecords(oilFlowDtos.size());
                for (ConsumeOilFlowDto oilFlowDto : oilFlowDtos) {
//                    ConsumeOilFlow consumeOilFlow = new ConsumeOilFlow();
//                    consumeOilFlow.setState(1);
//                    consumeOilFlow.setId(oilFlowDto.getId());
//                    consumeOilFlow.setGetResult("处理成功");
//                    consumeOilFlowService.updateById(consumeOilFlow);
                    ServiceSerial serviceSerial = new ServiceSerial();
                    serviceSerial.setServiceUserId(serviceInfoVo.getServiceUserId());
                    serviceSerial.setServiceName(serviceInfoVo.getServiceName());
                    serviceSerial.setSerialData(oilFlowDto.getOrderId());
                    serviceSerial.setTypeName("油费");
                    serviceSerial.setDriverName(oilFlowDto.getOtherName());
                    serviceSerial.setAmount(oilFlowDto.getAmount());
                    serviceSerial.setTenantId(serviceInfoFleetVo.getTenantId());
                    serviceSerial.setTime(LocalDateTimeUtil.convertStringToDate(oilFlowDto.getCreateDate()));
                    serviceSerial.setPlateNumber(oilFlowDto.getPlateNumber());
                    serviceSerial.setMobile(oilFlowDto.getOtherUserBill());
                    serviceSerialService.save(serviceSerial);
                }

            } else {
                serviceProviderBill.setBillAmount(0D);
                serviceProviderBill.setBillRecords(0);
            }
        }
        if (serviceInfoVo.getServiceType() == 2) {
            List<UserRepairInfoDto> repairInfoDtos = consumeOilFlowService.queryUserRepairInfo(serviceUserId, dateToString, convertDateToString, serviceInfoFleetVo.getTenantId());
            if (repairInfoDtos != null && repairInfoDtos.size() > 0) {
                Double collect = repairInfoDtos.stream().collect(Collectors.summingDouble(UserRepairInfoDto::getAmount));
                BigDecimal bg = new BigDecimal(collect);
                double d1 = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                serviceProviderBill.setBillAmount(d1);
                serviceProviderBill.setBillRecords(repairInfoDtos.size());
                for (UserRepairInfoDto repairInfoDto : repairInfoDtos) {
//                    UserRepairMargin userRepairMargin = new UserRepairMargin();
//                    userRepairMargin.setState(1);
//                    userRepairMargin.setGetResult("处理成功");
//                    userRepairMargin.setId(repairInfoDto.getId());
//                    userRepairMarginService.updateById(userRepairMargin);
//
                    ServiceSerial serviceSerial = new ServiceSerial();
                    serviceSerial.setServiceUserId(serviceInfoVo.getServiceUserId());
                    serviceSerial.setServiceName(serviceInfoVo.getServiceName());
                    serviceSerial.setSerialData(repairInfoDto.getOrderId());
                    serviceSerial.setTypeName(repairInfoDto.getStateInfo() != null && repairInfoDto.getState() == 1 ? "保养" : "维修");
                    serviceSerial.setDriverName(repairInfoDto.getOtherName());
                    serviceSerial.setAmount(repairInfoDto.getAmount());
                    serviceSerial.setTime(LocalDateTimeUtil.convertStringToDate(repairInfoDto.getCreateTime()));
                    serviceSerial.setPlateNumber(repairInfoDto.getPlateNumber());
                    serviceSerial.setTenantId(serviceInfoFleetVo.getTenantId());
                    serviceSerial.setMobile(repairInfoDto.getOtherUserBill());
                    serviceSerialService.save(serviceSerial);
                }
            } else {
                serviceProviderBill.setBillAmount(0D);
                serviceProviderBill.setBillRecords(0);
            }
        }


        serviceProviderBill.setCreateTime(LocalDateTime.now());
        serviceProviderBill.setBillCycle(dateToString + "-" + convertDateToString);
        serviceProviderBill.setTenantId(serviceInfoFleetVo.getTenantId());
        serviceProviderBill.setServiceProviderType(serviceInfoVo.getServiceType());
        serviceProviderBill.setServiceUserId(serviceInfoVo.getServiceUserId());
        serviceProviderBill.setServiceProviderName(serviceInfoVo.getServiceName());
        if (serviceProviderBill.getBillAmount() == null || serviceProviderBill.getBillAmount() == 0) {
            serviceProviderBill.setPaymentStatus(3);
        } else {
            serviceProviderBill.setPaymentStatus(1);
        }
        serviceProviderBill.setPhone(serviceInfoVo.getLoginAcct());
        serviceProviderBill.setBillNo(SnowflakeIdWorker.generateOpenId());
        serviceProviderBill.setCreateTime(convertStringToDate);
        return serviceProviderBill;
    }
}
