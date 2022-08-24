package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.order.api.order.IClaimExpenseInfoService;
import com.youming.youche.order.commons.AuditConsts;
import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import com.youming.youche.order.dto.ClaimExpenseCountDto;
import com.youming.youche.order.dto.ClaimExpenseInfoDto;
import com.youming.youche.order.provider.mapper.order.ClaimExpenseInfoMapper;
import com.youming.youche.order.vo.ClaimExpenseInfoInVo;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.api.audit.IAuditOutService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 车管报销表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-22
 */
@DubboService(version = "1.0.0")
public class ClaimExpenseInfoServiceImpl extends BaseServiceImpl<ClaimExpenseInfoMapper, ClaimExpenseInfo> implements IClaimExpenseInfoService {

    @Resource
    private ClaimExpenseInfoMapper claimExpenseInfoMapper;
    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;
    @DubboReference(version = "1.0.0")
    ISysStaticDataService sysStaticDataService;
    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;
    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;

    @Override
    public List<ClaimExpenseInfo> getValidExpenseInfoByOrder(long orderId) {
        QueryWrapper<ClaimExpenseInfo> claimExpenseInfoQueryWrapper = new QueryWrapper<>();
        claimExpenseInfoQueryWrapper.eq("order_id", orderId)
                .ne("sts", 4)
                .ne("sts", 5);
        List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoMapper.selectList(claimExpenseInfoQueryWrapper);
        return claimExpenseInfos;
    }


    /**
     * @Function: com.business.pt.ac.intf.IClaimExpenseInfoTF.java::countWaitAduitExpense
     * @Description: 该函数的功能描述:根据订单查询待审核和审核中报销数量
     * @returnl
     */
    @Override
    public Long countWaitAduitExpense(Long orderId) {
//        ClaimExpenseInfo infoIn  = new ClaimExpenseInfo();
//        infoIn.setOrderId(orderId);
//        infoIn.setExpenseStsList(ClaimExpenseConsts.EXPENSE_STS.WAIT_AUDIT+","+ClaimExpenseConsts.EXPENSE_STS.UNDER_AUDIT);
//        IClaimExpenseInfoSV claimExpenseInfoSV = (IClaimExpenseInfoSV)SysContexts.getBean("claimExpenseInfoSV");
//        return claimExpenseInfoSV.doQuery(infoIn).getTotalNum();
        List<Long> longList = new ArrayList<>();
        longList.add(1L);
        longList.add(2L);
        LambdaQueryWrapper<ClaimExpenseInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClaimExpenseInfo::getOrderId, orderId)
                .in(ClaimExpenseInfo::getSts, longList);
        List<ClaimExpenseInfo> claimExpenseInfos = claimExpenseInfoMapper.selectList(wrapper);

        Integer size = claimExpenseInfos.size();
        Long sum = Long.valueOf(size);
        return sum;
    }

    @Override
    public ClaimExpenseCountDto queryClaimExpenseCount(Long orderId, Long userId) {
        LambdaQueryWrapper<ClaimExpenseInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ClaimExpenseInfo::getOrderId, orderId);
        queryWrapper.eq(ClaimExpenseInfo::getExpenseType, OaLoanConsts.EXPENSE_TYPE.DRIVER);//司机借支
        queryWrapper.eq(ClaimExpenseInfo::getSts, 3);//只取有效的
        if (null != userId) {
            queryWrapper.eq(ClaimExpenseInfo::getUserId, userId);
        }
        List<ClaimExpenseInfo> lists = this.baseMapper.selectList(queryWrapper);
        ClaimExpenseCountDto dto = new ClaimExpenseCountDto();
        Long amount = 0L;
        if (lists.size() > 0) {
            for (ClaimExpenseInfo cl : lists) {
                amount += cl.getAmount();
            }
        }
        int count = claimExpenseInfoMapper.queryClaimExpenseCount(orderId, userId);
        dto.setCount(count);
        dto.setAmount(amount);
        return dto;
    }

    @Override
    public Page<ClaimExpenseInfoDto> doQuery(ClaimExpenseInfoInVo infoIn, Integer pageSize, Integer pageNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        List<String> split = null;
        if (infoIn.getStairCategoryList() != null) {
            split = Arrays.stream(infoIn.getStairCategoryList().split(",")).collect(Collectors.toList());
//            split = StringUtils.split(infoIn.getStairCategoryList().replaceAll("，", ","), ",");
        }

        List<Long> lids = null;
        //判断是否有所有数据权限(操作员如果只有查看部门数据的权限，则只能所有归属于本部门的车管借支；如果有查看所有数据的权限，则可以查看所有的车管借支。)
        boolean hasAllDataPermission = false;
        try {
            //判断是否有所有数据权限
            hasAllDataPermission = sysRoleService.hasAllData(user);
            if(!hasAllDataPermission && infoIn.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {
                List<Long> orgList = sysOrganizeService.getSubOrgList(accessToken);
                if (orgList != null && orgList.size() > 0) {
                    infoIn.setOrgIds(orgList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (infoIn.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {// 车管报销
            lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.TubeExpense, user.getUserInfoId(), user.getTenantId());
        } else if (infoIn.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER) {// 司机报销
            lids = auditOutService.getBusiIdByUserId(AuditConsts.AUDIT_CODE.DriverExpense, user.getUserInfoId(), user.getTenantId());
        }
        Page<ClaimExpenseInfo> claimExpenseInfoInVoPage = claimExpenseInfoMapper.selectOr(infoIn, split, hasAllDataPermission, lids, new Page<>(pageNum, pageSize));

        List<ClaimExpenseInfo> lists = claimExpenseInfoInVoPage.getRecords();
        List<ClaimExpenseInfoDto> listOut = new ArrayList<>();
        Page<ClaimExpenseInfoDto> page = new Page<>();
        page.setTotal(claimExpenseInfoInVoPage.getTotal());
        page.setSize(claimExpenseInfoInVoPage.getSize());
        List<Long> busiIds = new ArrayList<>();
        Map<Long, Boolean> hasPermissionMap = new HashMap<Long, Boolean>();
        for (ClaimExpenseInfo expenseInfo : lists) {
            busiIds.add(expenseInfo.getExpenseId());
        }

        String auditCode = "";
        if (infoIn.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.VEHICLE) {// 车管报销
            auditCode = AuditConsts.AUDIT_CODE.TubeExpense;
        } else if (infoIn.getExpenseType() == OaLoanConsts.EXPENSE_TYPE.DRIVER) {// 司机报销
            auditCode = AuditConsts.AUDIT_CODE.DriverExpense;
        }
        if (!busiIds.isEmpty()) {
            hasPermissionMap = auditOutService.isHasPermission(accessToken, auditCode, busiIds);
        }

        for (ClaimExpenseInfo expenseInfo : lists) {
            ClaimExpenseInfoDto expenseInfoOut = new ClaimExpenseInfoDto();
            BeanUtil.copyProperties(expenseInfo, expenseInfoOut);
            boolean isTrue = false;
            if (hasPermissionMap.get(expenseInfo.getExpenseId()) != null && hasPermissionMap.get(expenseInfo.getExpenseId())) {
                isTrue = true;
            }
            //SysStaticDataUtil.getSysStaticDataCodeName("EXPENSE_STS", this.getSts()+"");
            expenseInfoOut.setIsHasPermission(isTrue);
            if (expenseInfoOut.getSts() != null) {
                expenseInfoOut.setStsString(sysStaticDataService.getSysStaticDatas("EXPENSE_STS", expenseInfoOut.getSts()));
            }
            listOut.add(expenseInfoOut);
        }

        page.setRecords(listOut);
        return page;
    }

//    public static void main(String[] args) {
//       String[] a = StringUtils.split("1,2".replaceAll("，", ","), ",");
//       System.out.println(a[0]);
//    }
}
