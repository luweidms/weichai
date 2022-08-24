package com.youming.youche.system.provider.service.back;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import com.youming.youche.record.api.user.IUserService;
import com.youming.youche.record.vo.driver.DoQueryDriversVo;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.back.IBacklistService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.domain.back.Backlist;
import com.youming.youche.system.provider.mapper.back.BacklistMapper;
import com.youming.youche.system.vo.QueryBacklistParamVo;
import com.youming.youche.util.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-22
 */
@DubboService(version = "1.0.0")
public class BacklistServiceImpl extends BaseServiceImpl<BacklistMapper, Backlist> implements IBacklistService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    ISysTenantDefService iSysTenantDefService;

    @Resource
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    IUserService iUserService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @Override
    public Page<Backlist> query(String queryParam, Integer backType, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        LambdaQueryWrapper<Backlist> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(queryParam)) {
            queryWrapper.or(wq -> wq.like(Backlist::getDriverName, queryParam).or()
                    .like(Backlist::getDriverPhone, queryParam).or()
                    .like(Backlist::getPlateNumber, queryParam));
        }

        if (null != backType) {
            queryWrapper.eq(Backlist::getBackType, backType);
        }

        queryWrapper.eq(Backlist::getTenantId, loginInfo.getTenantId());
        queryWrapper.eq(Backlist::getState, 1);

        return getPage(queryWrapper, pageNum, pageSize);
    }

    private Page<Backlist> getPage(LambdaQueryWrapper<Backlist> queryWrapper, Integer pageNum, Integer pageSize) {
        queryWrapper.orderByDesc(Backlist::getCreateTime);

        Page<Backlist> page = this.page(new Page<>(pageNum, pageSize), queryWrapper);

        List<Backlist> list = page.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return page;
        }

        for (Backlist backlist : list) {
            if (null != backlist.getBelongTenantId() && backlist.getBelongTenantId() > 0) {
                SysTenantDef sysTenantDef = iSysTenantDefService.getSysTenantDef(backlist.getBelongTenantId(), true);
                backlist.setBelongTenantName(sysTenantDef.getName());
            }
            UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(backlist.getCreateUserId());
            backlist.setCreateUserName(userDataInfo.getLinkman());
        }

        return page;
    }

    @Override
    public List<Backlist> query(String driverName, String identification, String driverPhone, long tenantId, Integer state) {
        LambdaQueryWrapper<Backlist> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Backlist::getTenantId, tenantId);
        queryWrapper.eq(Backlist::getIdentification, identification);
        queryWrapper.eq(Backlist::getDriverPhone, driverPhone);
        queryWrapper.eq(Backlist::getDriverName, driverName);
        queryWrapper.eq(Backlist::getState, state);

        return this.list(queryWrapper);
    }

    @Override
    public void save(Backlist backlist, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (areadlyExist(backlist, tenantId)) {
            throw new BusinessException("已存在相同的");
        }
        backlist.setId(null);
        backlist.setCreateTime(LocalDateTime.now());
        backlist.setUpdateTime(LocalDateTime.now());
        backlist.setTenantId(tenantId);
        backlist.setCreateUserId(loginInfo.getUserInfoId());
        backlist.setState(1);
        this.save(backlist);
    }

    @Override
    public Page doQueryCarDriver(String loginAcct, String linkman, Integer carUserType,
                                 String attachTenantName, String attachTennantLinkman,
                                 String attachTennantLinkPhone, Integer state,
                                 String accessToken, Integer pageNum, Integer pageSize) {
        DoQueryDriversVo vo = new DoQueryDriversVo();
        vo.setLoginAcct(loginAcct);
        vo.setLinkman(linkman);
        vo.setCarUserType(carUserType);
        vo.setAttachTenantName(attachTenantName);
        vo.setAttachTennantLinkman(attachTennantLinkman);
        vo.setAttachTennantLinkPhone(attachTennantLinkPhone);
        vo.setState(state);
        Page page = new Page();
        try {
            page = iUserService.doQueryCarDriver(vo, pageNum, pageSize, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return page;
    }

    @Override
    public Boolean haveOrderLastMonth(Long userId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Date now = new Date();
        Date lastMonth = DateUtil.addMonth(now, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List result = iOrderSchedulerService.queryDriverOrderInfo(userId, loginInfo.getTenantId(), sdf.format(lastMonth), sdf.format(now));
        return CollectionUtils.isNotEmpty(result);
    }

    @Override
    public Page<Backlist> checkBacklist(QueryBacklistParamVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<Backlist> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(vo.getDriverName()), Backlist::getDriverName, vo.getDriverName())
                .or().like(StringUtils.isNotBlank(vo.getDriverPhone()), Backlist::getDriverPhone, vo.getDriverPhone())
                .or().like(StringUtils.isNotBlank(vo.getPlateNumber()), Backlist::getPlateNumber, vo.getPlateNumber())
                .or().like(StringUtils.isNotBlank(vo.getIdentification()), Backlist::getIdentification, vo.getIdentification());

        queryWrapper.eq(Backlist::getState, 1);
        queryWrapper.or(qw -> qw.eq(Backlist::getTenantId, loginInfo.getTenantId())
                .eq(Backlist::getState, 1).eq(Backlist::getBackType, 2));

        return getPage(queryWrapper, pageNum, pageSize);
    }

    private boolean areadlyExist(Backlist backlist, Long tenantId) {
        List<Backlist> result = this.query(backlist.getDriverName(), backlist.getIdentification(),
                backlist.getDriverPhone(), tenantId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);

        return CollectionUtils.isNotEmpty(result);
    }


}
