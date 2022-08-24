package com.youming.youche.finance.provider.service.ac;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.ac.IHurryBillRecordService;
import com.youming.youche.finance.domain.ac.HurryBillRecord;
import com.youming.youche.finance.provider.mapper.ac.HurryBillRecordMapper;
import com.youming.youche.system.api.ISysTenantDefService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


/**
* <p>
    *  服务实现类
    * </p>
* @author zengwen
* @since 2022-04-24
*/
@DubboService(version = "1.0.0")
    public class HurryBillRecordServiceImpl extends BaseServiceImpl<HurryBillRecordMapper, HurryBillRecord> implements IHurryBillRecordService {

    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Override
    public Page<HurryBillRecord> doQueryNoticeOfReminder(String accessToken, Long userId, Integer type, Integer pageNum, Integer pageSize) {
        if (type == OrderAccountConst.PAY_TYPE.TENANT) {
            LoginInfo loginInfo = loginUtils.get(accessToken);
            Long tenantId = loginInfo.getTenantId();
            userId = sysTenantDefService.getSysTenantDef(tenantId).getAdminUser();
        }

        LambdaQueryWrapper<HurryBillRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HurryBillRecord::getOpenUserId, userId);
        queryWrapper.orderByDesc(HurryBillRecord::getCreateTime);

        Page<HurryBillRecord> page = new Page<>(pageNum, pageSize);

        return baseMapper.selectPage(page, queryWrapper);
    }
}
