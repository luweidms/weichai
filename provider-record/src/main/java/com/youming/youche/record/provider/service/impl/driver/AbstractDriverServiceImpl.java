package com.youming.youche.record.provider.service.impl.driver;

import cn.hutool.core.bean.BeanUtil;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.record.api.apply.IApplyRecordService;
import com.youming.youche.record.api.driver.IAbstractDriverService;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.ITenantUserRelVerService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.api.user.IUserDataInfoVerService;
import com.youming.youche.record.api.user.IUserLineRelService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.apply.ApplyRecord;
import com.youming.youche.record.domain.tenant.TenantUserRel;
import com.youming.youche.record.domain.tenant.TenantUserRelVer;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.domain.user.UserDataInfoVer;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;

/**
 * @version:
 * @Title: AbstractDriverServiceImpl
 * @Package: com.youming.youche.record.provider.service.impl.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2021/12/22 18:12
 * @company:
 */
@DubboService(version = "1.0.0")
public class AbstractDriverServiceImpl implements IAbstractDriverService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDriverServiceImpl.class);

    @Resource
    ITenantUserRelService iTenantUserRelService;


    @Resource
    IApplyRecordService iApplyRecordService;

    @Resource
    ITenantUserRelVerService iTenantUserRelVerService;

    @Resource
    ITenantVehicleRelService iTenantVehicleRelService;

    @Resource
    IUserDataInfoRecordService iUserDataInfoRecordService;


    @Resource
    IUserDataInfoVerService iUserDataInfoVerService;

    @Resource
    IUserLineRelService iUserLineRelService;

    @DubboReference(version = "1.0.0" )
    ISysOperLogService isysOperLogService;


    @Override
    public void delete(Long tenantUserRelId, String tenantVehicleIds,String accessToken) throws Exception {
        List<Long> tenantVehicleIdList = CommonUtil.convertLongIdList(tenantVehicleIds);
        TenantUserRel tenantUserRel = iTenantUserRelService.getTenantUserRelByRelId(tenantUserRelId);
        if (null == tenantUserRel) {
            LOGGER.error("删除司机错误，不存在的关系记录，relId:{}", tenantUserRelId);
            throw new BusinessException("数据错误");
        }
        Integer carUserType=tenantUserRel.getCarUserType();
        if (null == carUserType ) {
            throw new IllegalArgumentException("参数错误");
        }
        if (SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR == carUserType) {
        } else if (SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR == carUserType) {
        } else if (SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR == carUserType) {
        } else if (SysStaticDataEnum.CAR_USER_TYPE.ATTACH_CAR == carUserType) {
        }else {
            throw new IllegalArgumentException("参数错误");
        }
        deleteDriver(tenantUserRel, tenantVehicleIdList,carUserType,accessToken);
    }

    public void deleteDriver(TenantUserRel tenantUserRel, List<Long> tenantVehicleIdList,Integer carUserType,String accessToken) throws Exception {
        //判断是否在审核中的数据
        List<ApplyRecord> applyRecordList = iApplyRecordService.getApplyRecord(tenantUserRel.getUserId(), tenantUserRel.getTenantId(), SysStaticDataEnum.APPLY_STATE.APPLY_STATE0);
        if (CollectionUtils.isNotEmpty(applyRecordList)) {
            throw new BusinessException("司机处理中，请稍后再试！");
        }
        beforeDeleteTenantUserRel(tenantUserRel, tenantVehicleIdList,carUserType,accessToken);
        deleteTenantUserRel(tenantUserRel,accessToken);
    }

    void beforeDeleteTenantUserRel(TenantUserRel tenantUserRel,List<Long> tenantVehicleIdList,Integer carUserType,String accessToken) throws Exception{
          for (Long relId : tenantVehicleIdList) {
              iTenantVehicleRelService.doRemoveVehicleV30(relId,accessToken);
          }
          //剩下的车，把司机清空
          iTenantVehicleRelService.changeVehicleDriver(tenantUserRel.getTenantId(),tenantUserRel.getUserId(), null);
          if (carUserType==1){
              //归属清空
              UserDataInfo userDataInfo = iUserDataInfoRecordService.getUserDataInfo(tenantUserRel.getUserId());
              userDataInfo.setTenantId(-1L);
              iUserDataInfoRecordService.updateById(userDataInfo);
              UserDataInfoVer userDataInfoVer = iUserDataInfoVerService.getUserDataInfoVerNoTenant(tenantUserRel.getUserId());
              if (userDataInfoVer != null) {
                  userDataInfoVer.setTenantId(-1L);
                  iUserDataInfoVerService.updateById(userDataInfoVer);
              }
              iApplyRecordService.invalidBeApplyRecord(tenantUserRel.getUserId(), tenantUserRel.getTenantId());
              iUserLineRelService.removeUserLineRel(tenantUserRel.getUserId(),tenantUserRel.getTenantId(),accessToken);
              iUserLineRelService.removeTenantUserSalaryRel(tenantUserRel);

          }
    }

    protected void deleteTenantUserRel(TenantUserRel tenantUserRel,String token) throws Exception{
        TenantUserRelVer relVer = new TenantUserRelVer();
        BeanUtil.copyProperties(tenantUserRel,relVer);
        relVer.setVerState(SysStaticDataEnum.VER_STATE.VER_STATE_HIS);
        relVer.setRelId(tenantUserRel.getId());
        iTenantUserRelVerService.save(relVer);
        iTenantUserRelService.removeById(tenantUserRel);
        saveSysOperLog(SysOperLogConst.BusiCode.Driver,SysOperLogConst.OperType.Remove,"移除会员",token,tenantUserRel.getId());
    }

    private void saveSysOperLog(SysOperLogConst.BusiCode busiCode, SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        isysOperLogService.save(operLog, accessToken);
    }


}
