package com.youming.youche.finance.provider.service.ac;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.ac.IApplyOpenBillService;
import com.youming.youche.finance.domain.ac.ApplyOpenBill;
import com.youming.youche.finance.domain.munual.PayoutIntf;
import com.youming.youche.finance.dto.ac.BillManageDto;
import com.youming.youche.finance.provider.mapper.ac.ApplyOpenBillMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.ac.QueryInvoiceVo;
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.domain.order.PayoutIntfExpansion;
import com.youming.youche.system.api.ISysTenantDefService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 申请开票记录表 服务实现类
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@DubboService(version = "1.0.0")
public class ApplyOpenBillServiceImpl extends BaseServiceImpl<ApplyOpenBillMapper, ApplyOpenBill> implements IApplyOpenBillService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IPayoutIntfExpansionService iPayoutIntfExpansionService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Override
    public void payForBillServiceWriteBack(PayoutIntf payout, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (payout == null) {
            throw new BusinessException("请输入开票申请记录流水id");
        }
        Long flowId = payout.getId();
        PayoutIntfExpansion pie = iPayoutIntfExpansionService.getPayoutIntfExpansion(flowId);
        if (pie == null) {
            throw new BusinessException("根据支付记录流水id：" + flowId + " 未找到支付记录扩展表");
        }
        List<ApplyOpenBill> list = this.getApplyOpenBill(pie.getBusiCode());
        if (list == null || list.size() <= 0) {
            throw new BusinessException("根据申请记录号：" + pie.getBusiCode() + " 未找到申请记录");
        } else if (list.size() != 1) {
            throw new BusinessException("根据申请记录号：" + pie.getBusiCode() + " 找到多条申请记录");
        }
        ApplyOpenBill aob = list.get(0);
        if (aob.getApplyState() == null || aob.getApplyState() != OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE2) {
            throw new BusinessException("此申请记录状态不处于付款中");
        }
        aob.setApplyState(OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE3);
        aob.setPayTime(getDateToLocalDateTime(payout.getPayTime()));
        if (loginInfo != null) {
            aob.setUpdateOpId(loginInfo.getId());
            aob.setUpdateTime(LocalDateTime.now());
        }
        //抵扣票状态更改
        List<ApplyOpenBill> deductionList = this.getDeductionBillByParentFlowId(aob.getId());
        if (deductionList != null && deductionList.size() > 0) {
            for (ApplyOpenBill apply : deductionList) {
                apply.setUpdateTime(LocalDateTime.now());
                apply.setApplyState(OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE3);
                this.saveOrUpdate(apply);
            }
        }
        this.saveOrUpdate(aob);
    }

    @Override
    public List<ApplyOpenBill> getApplyOpenBill(String applyNum) {
        LambdaQueryWrapper<ApplyOpenBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyOpenBill::getApplyNum, applyNum);
        queryWrapper.eq(ApplyOpenBill::getState, OrderAccountConst.APPLY_OPEN_BILL.EFFECTIVE);
        return this.list(queryWrapper);
    }

    @Override
    public List<ApplyOpenBill> getDeductionBillByParentFlowId(Long parentFlowId) {
        LambdaQueryWrapper<ApplyOpenBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyOpenBill::getParentFlowId, parentFlowId);
        queryWrapper.eq(ApplyOpenBill::getState, OrderAccountConst.APPLY_OPEN_BILL.EFFECTIVE);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<BillManageDto> doQueryInvoice(String accessToken, QueryInvoiceVo queryInvoiceVo, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (queryInvoiceVo.getType() == OrderAccountConst.PAY_TYPE.TENANT) {
            queryInvoiceVo.setUserId(sysTenantDefService.getSysTenantDef(tenantId).getAdminUser());
        }

        Page page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ApplyOpenBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplyOpenBill::getOpenUserId, queryInvoiceVo.getUserId());
        queryWrapper.ge(ApplyOpenBill::getApplyState, OrderAccountConst.APPLY_OPEN_BILL.APPLY_STATE3);
        if (StringUtils.isNotEmpty(queryInvoiceVo.getApplyUserName())) {
            queryWrapper.like(ApplyOpenBill::getApplyUserName, queryInvoiceVo.getApplyUserName());
        }
        if (StringUtils.isNotEmpty(queryInvoiceVo.getApplyNum())) {
            queryWrapper.like(ApplyOpenBill::getApplyNum, queryInvoiceVo.getApplyNum());
        }
        if (CollectionUtils.isNotEmpty(queryInvoiceVo.getBillState())) {
            queryWrapper.in(ApplyOpenBill::getBillState, queryInvoiceVo.getBillState());
        }
        queryWrapper.orderByDesc(ApplyOpenBill::getCreateTime);

        IPage<ApplyOpenBill> ipage = baseMapper.selectPage(page, queryWrapper);
        List<BillManageDto> billManageOuts = new ArrayList<>();
        for (ApplyOpenBill applyOpenBill : ipage.getRecords()) {
            BillManageDto manageOut = new BillManageDto();
            BeanUtils.copyProperties(applyOpenBill, manageOut);

            manageOut.setOrderId(applyOpenBill.getId());
            if(manageOut.getBillState() != null && manageOut.getBillState()>=0){
                manageOut.setBillStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "INVOICE_STATE", manageOut.getBillState()+""));
            }

            if(manageOut.getApplyState() != null){
                manageOut.setApplyStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId,"APPLICATION_STATE", String.valueOf(manageOut.getApplyState())));
            }

            if(manageOut.getVehicleClass() != null){
                manageOut.setVehicleClassName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "VEHICLE_CLASS", String.valueOf(manageOut.getVehicleClass())));
            }

            if(manageOut.getBillType() != null){
                manageOut.setBillTypeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "APPLY_OPEN_BILL_TYPE", String.valueOf(manageOut.getBillType())));
            }

            if(manageOut.getBillState() != null){
                manageOut.setBillStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "BILL_STATE", String.valueOf(manageOut.getBillState())));
            }

            billManageOuts.add(manageOut);
        }

        IPage<BillManageDto> result = new Page<>();
        result.setPages(ipage.getPages());
        result.setCurrent(ipage.getCurrent());
        result.setTotal(ipage.getTotal());
        result.setSize(ipage.getSize());
        result.setRecords(billManageOuts);
        return result;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

}
