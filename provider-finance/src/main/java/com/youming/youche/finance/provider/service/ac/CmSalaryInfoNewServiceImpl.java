package com.youming.youche.finance.provider.service.ac;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.ICmSalaryComplainNewService;
import com.youming.youche.finance.api.IOrderDriverSubsidyInfoService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewExtService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.api.ac.ICmSalarySendInfoService;
import com.youming.youche.finance.api.ac.ICmSalarySendOrderInfoService;
import com.youming.youche.finance.api.ac.ICmSalaryTemplateService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.ac.CmSalaryComplainNew;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNewExt;
import com.youming.youche.finance.domain.ac.CmSalaryOrderExt;
import com.youming.youche.finance.domain.ac.CmSalarySendInfo;
import com.youming.youche.finance.domain.ac.CmSalarySendOrderInfo;
import com.youming.youche.finance.domain.ac.CmSalaryTemplate;
import com.youming.youche.finance.domain.ac.CmTemplateField;
import com.youming.youche.finance.domain.ac.OrderDriverSubsidyInfo;
import com.youming.youche.finance.dto.CmSalaryInfoDto;
import com.youming.youche.finance.dto.CmSalaryOrderInfoDto;
import com.youming.youche.finance.dto.CompSalaryTemplateDto;
import com.youming.youche.finance.dto.SaveCmSalaryTemplateDto;
import com.youming.youche.finance.dto.SubsidyInfoDto;
import com.youming.youche.finance.dto.ac.CmSalaryComplainNewDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoExtDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoNewQueryDto;
import com.youming.youche.finance.dto.ac.CmSalaryInfoQueryDto;
import com.youming.youche.finance.dto.ac.CmSalarySendInfoDto;
import com.youming.youche.finance.dto.order.OrderListOutDto;
import com.youming.youche.finance.provider.mapper.OaLoanMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryComplainNewMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryInfoNewExtMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryInfoNewMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryOrderExtMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalarySendInfoMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalarySendOrderInfoMapper;
import com.youming.youche.finance.provider.mapper.ac.CmSalaryTemplateMapper;
import com.youming.youche.finance.provider.mapper.ac.CmTemplateFieldMapper;
import com.youming.youche.finance.provider.mapper.ac.OrderDriverSubsidyInfoMapper;
import com.youming.youche.finance.provider.mapper.munual.PayoutIntfMapper;
import com.youming.youche.finance.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.finance.provider.utils.ReadisUtil;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.CmSalaryInfoVo;
import com.youming.youche.finance.vo.CmSalaryVO;
import com.youming.youche.finance.vo.ModifySalaryInfoVo;
import com.youming.youche.finance.vo.ac.CmSalaryInfoNewQueryVo;
import com.youming.youche.finance.vo.ac.OrderListInVo;
import com.youming.youche.order.api.IOrderDriverSubsidyService;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOaLoanService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfExpansionService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderDriverSubsidy;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OaLoan;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.SysTenantDefDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.record.api.tenant.ITenantUserSalaryRelService;
import com.youming.youche.record.api.user.IUserSalaryInfoService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import com.youming.youche.record.dto.UserSalaryDto;
import com.youming.youche.record.vo.cm.CmCustomerInfoVo;
import com.youming.youche.system.api.ISysAttachService;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysAttach;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.BeanMapUtils;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.youming.youche.conts.EnumConsts.PayInter.DRIVER_SALARY;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zengwen
 * @since 2022-04-18
 */
@DubboService(version = "1.0.0")
public class CmSalaryInfoNewServiceImpl extends BaseServiceImpl<CmSalaryInfoNewMapper, CmSalaryInfoNew> implements ICmSalaryInfoNewService {
    private static final Logger log = LoggerFactory.getLogger(CmSalaryInfoNewServiceImpl.class);
    @Resource
    RedisUtil redisUtil;

    @Resource
    LoginUtils loginUtils;

    @Resource
    CmSalaryTemplateMapper cmSalaryTemplateMapper;

    @Resource
    CmTemplateFieldMapper cmTemplateFieldMapper;

    @Resource
    CmSalaryInfoNewExtMapper cmSalaryInfoNewExtMapper;

    @Resource
    CmSalaryInfoNewMapper cmSalaryInfoNewMapper;

    @Resource
    OrderInfoMapper orderInfoMapper;

    @Resource
    CmSalaryComplainNewMapper cmSalaryComplainNewMapper;

    @Resource
    OrderDriverSubsidyInfoMapper orderDriverSubsidyInfoMapper;

    @Resource
    ICmSalaryTemplateService cmSalaryTemplateService;
    @Resource
    CmSalaryOrderExtMapper cmSalaryOrderExtMapper;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService iSysSmsSendService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @Resource
    ICmSalaryComplainNewService iCmSalaryComplainNewService;
    @DubboReference(version = "1.0.0")
    IOaLoanService iOaLoanService1;
    @DubboReference(version = "1.0.0")
    IOrderDriverSubsidyService orderDriverSubsidyService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;

    @DubboReference(version = "1.0.0")
    IOrderDriverSwitchInfoService orderDriverSwitchInfoService;

    @DubboReference(version = "1.0.0")
    IUserSalaryInfoService userSalaryInfoService;

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;

    @Resource
    ICmSalaryInfoNewService cmSalaryInfoNewService;

    @DubboReference(version = "1.0.0")
    IOrderOilSourceService orderOilSourceService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IOpAccountService iOpAccountService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;

    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @DubboReference(version = "1.0.0")
    ISysAttachService sysAttachService;

    @Resource
    IOrderDriverSubsidyInfoService orderDriverSubsidyInfoService;

    @Resource
    ICmSalaryInfoNewExtService cmSalaryInfoNewExtService;

    @Resource
    private ReadisUtil readisUtil;

    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    @DubboReference(version = "1.0.0")
    ITenantUserSalaryRelService tenantUserSalaryRelService;

    @Resource
    OaLoanMapper oaLoanMapper;

    @DubboReference(version = "1.0.0")
    IPayoutIntfExpansionService payoutIntfExpansionService;

    @Resource
    PayoutIntfMapper payoutIntfMapper;

    @Resource
    ICmSalarySendInfoService cmSalarySendInfoService;

    @Resource
    ICmSalarySendOrderInfoService cmSalarySendOrderInfoService;

    @Resource
    CmSalarySendInfoMapper cmSalarySendInfoMapper;

    @Resource
    CmSalarySendOrderInfoMapper cmSalarySendOrderInfoMapper;

    @Override
    public Page<CmSalaryInfoNewQueryDto> queryCmSalaryInfoNew(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(tenantId);

        List<CmTemplateField> defaultFiledList = new ArrayList<CmTemplateField>();
        List<CmTemplateField> allFiledList = new ArrayList<CmTemplateField>();

        CmSalaryTemplate defaultCmSalaryTemplate = cmSalaryTemplateMapper.getDefaultCmSalaryTemplate();
        if (cmSalaryTemplate == null) {
            cmSalaryTemplate = defaultCmSalaryTemplate;
        } else {
            defaultFiledList = cmTemplateFieldMapper.getCmTemplateFiel(defaultCmSalaryTemplate.getId(), "", -1, EnumConsts.STATE.STATE_YES);
            allFiledList.addAll(defaultFiledList);
        }

        long templateId = cmSalaryTemplate.getId();
        List<CmTemplateField> filedList = cmTemplateFieldMapper.getCmTemplateFiel(templateId, "", -1, -1);
        allFiledList.addAll(filedList); // 工资单列表展示字段

        Page page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmSalaryInfoNew::getTenantId, tenantId);
        queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, cmSalaryInfoNewQueryVo.getSettleMonth());
        if (StringUtils.isNotEmpty(cmSalaryInfoNewQueryVo.getCarDriverName())) {
            queryWrapper.like(CmSalaryInfoNew::getCarDriverName, cmSalaryInfoNewQueryVo.getCarDriverName());
        }
        if (StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getCarDriverPhone())) {
            queryWrapper.like(CmSalaryInfoNew::getCarDriverPhone, cmSalaryInfoNewQueryVo.getCarDriverPhone());
        }
        if (cmSalaryInfoNewQueryVo.getSalaryComplainSts() != null && cmSalaryInfoNewQueryVo.getSalaryComplainSts() > -1) {
            if (cmSalaryInfoNewQueryVo.getSalaryComplainSts() == 1) {
                queryWrapper.gt(CmSalaryInfoNew::getComplainCount, 0);
            } else {
                queryWrapper.and(wrapper -> wrapper.eq(CmSalaryInfoNew::getComplainCount, 0).or().isNull(CmSalaryInfoNew::getComplainCount));
            }
        }
        if (StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getStartTime()) && StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getEndTime())) {
            try {
                queryWrapper.between(CmSalaryInfoNew::getPaidDate, DateUtil.formatStringToDate(cmSalaryInfoNewQueryVo.getStartTime() + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.formatStringToDate(cmSalaryInfoNewQueryVo.getEndTime() + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                throw new BusinessException("数据转换异常");
            }
        }
        if (cmSalaryInfoNewQueryVo.getState() != null && cmSalaryInfoNewQueryVo.getState() > -1) {
            queryWrapper.eq(CmSalaryInfoNew::getState, cmSalaryInfoNewQueryVo.getState());
        }
        if (cmSalaryInfoNewQueryVo.getUserType() != null && cmSalaryInfoNewQueryVo.getUserType() > -1) {
            queryWrapper.eq(CmSalaryInfoNew::getUserType, cmSalaryInfoNewQueryVo.getUserType());
        }

        // 分页查询工资单
        IPage<CmSalaryInfoNew> ipage = baseMapper.selectPage(page, queryWrapper);
        List<Long> salaryList = new ArrayList<Long>();
        for (CmSalaryInfoNew record : ipage.getRecords()) {
            salaryList.add(record.getId());
        }

        // 查询所有工资单的拓展信息
        List<CmSalaryInfoNewExt> listExt = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(salaryList)) {
            LambdaQueryWrapper<CmSalaryInfoNewExt> queryWrapperExt = new LambdaQueryWrapper<>();
            queryWrapperExt.in(CmSalaryInfoNewExt::getSalaryId, salaryList);
            listExt = cmSalaryInfoNewExtMapper.selectList(queryWrapperExt);
        }

        List<CmSalaryInfoNewQueryDto> list = new ArrayList<>();

        for (CmSalaryInfoNew record : ipage.getRecords()) {
            Map<String, Object> m = new HashMap();

            Date now = new Date();
            String settleMonthNow = DateUtil.formatDate(now, DateUtil.YEAR_MONTH_FORMAT);

            Date monthStart = null;
            Date monthEnd = null;
            try {
                monthStart = DateUtil.formatStringToDate(record.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
                monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new BusinessException("数据转换异常");
            }

            // 老系统是查指定月份全部的  不符合现在的逻辑
            Date date = null;
            Date lastDate = null;

            try {
                date = DateUtil.formatStringToDate(record.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
                lastDate = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(date) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                throw new BusinessException("数据转换异常");
            }
            OrderListInVo orderListInVo = new OrderListInVo();
            orderListInVo.setTenantId(tenantId);

            // 获取工资单对于补贴订单数据
            List<OrderListOutDto> orderListOutDtoList = orderInfoMapper.queryDriverMonthOrderInfoList(orderListInVo, monthStart, monthEnd, record.getCarDriverId());

            record.setOrderCount(orderListOutDtoList.size());

            m = BeanMapUtils.beanToMap(record);

            // 获取司机工资单的扩展信息
            Optional<CmSalaryInfoNewExt> cmSalaryInfoNewExtOptional = listExt.stream().filter(item -> item.getSalaryId().equals(record.getId())).findFirst();
            CmSalaryInfoNewExt cmSalaryInfoNewExt = new CmSalaryInfoNewExt();
            if (cmSalaryInfoNewExtOptional.isPresent()) {
                cmSalaryInfoNewExt = cmSalaryInfoNewExtOptional.get();
            }

            long salaryId = record.getId();

            m.putAll(BeanMapUtils.beanToMap(cmSalaryInfoNewExt));
            m.put("salaryId", salaryId);

            // 老系统的逻辑  新逻辑  补贴金额 = 所有已结算的补贴订单的补贴金额总和
//            long answerSubsidyFee = record.getAnswerSubsidyFee() == null ? 0L : record.getAnswerSubsidyFee();
//            m.put("answerSubsidyFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(answerSubsidyFee).longValue(), 2));
            Long answerSubsidyFee = 0L;
            for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                // 已结算
                if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                    answerSubsidyFee += orderListOutDto.getSettleMoney();
                }
            }

            m.put("answerSubsidyFee", answerSubsidyFee);
            m.put("answerSubsidyFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(answerSubsidyFee).longValue(), 2));

            long lastMonthDebt = record.getLastMonthDebt() == null ? 0L : record.getLastMonthDebt();
            m.put("lastMonthDebtDouble", CommonUtil.getDoubleFormatLongMoney(new Double(lastMonthDebt).longValue(), 2));

            long basicSalaryFee = record.getBasicSalaryFee() == null ? 0L : record.getBasicSalaryFee();
            m.put("basicSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(basicSalaryFee).longValue(), 2));

            //long monthMileage = record.getMonthMileage() == null ? 0L : record.getMonthMileage();
            Long monthMileage = 0L;
            for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                OrderInfoDto orderLine = orderSchedulerService.queryOrderLineString(orderListOutDto.getOrderId());
                orderListOutDto.setOrderLine(orderLine.getOrderLine());

                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderListOutDto.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    boolean isReplace = false;
                    Long newMieageNumber = 0L;
                    for (int i = 0; i < switchInfos.size(); i++) {
                        OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                        if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == record.getCarDriverId()) {
                            if (i + 1 < switchInfos.size()) {
                                OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                if (isReplace) {
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        orderListOutDto.setMieageNumber(newMieageNumber);
                    }
                }

                monthMileage += orderListOutDto.getMieageNumber();
            }
            m.put("monthMileageStr", CommonUtil.divide(monthMileage, 1000L));//转换成公里

            long saveOilBonus = record.getSaveOilBonus() == null ? 0L : record.getSaveOilBonus();
            m.put("saveOilBonusDouble", CommonUtil.getDoubleFormatLongMoney(new Double(saveOilBonus).longValue(), 2));

            long tripPayFee = record.getTripPayFee() == null ? 0L : record.getTripPayFee();
            m.put("tripPayFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(tripPayFee).longValue(), 2));

            long unwrittenLoanFee = record.getUnwrittenLoanFee() == null ? 0L : record.getUnwrittenLoanFee();
            m.put("unwrittenLoanFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(unwrittenLoanFee).longValue(), 2));

            //老平台逻辑
//            long realSalaryFee = record.getRealSalaryFee() == null ? 0L : record.getRealSalaryFee();
//            m.put("realSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(realSalaryFee).longValue(), 2));
            // 新平台实发工资逻辑：节油奖 + 基本工资 + 自定义项
            Long realSalaryFee = (record.getSaveOilBonus() == null ? 0L : record.getSaveOilBonus()) + (record.getBasicSalaryFee() == null ? 0L : record.getBasicSalaryFee()) + getExtSum(cmSalaryInfoNewExt, filedList);
            m.put("realSalaryFee", realSalaryFee);
            m.put("realSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(realSalaryFee).longValue(), 2));

            // 新平台逻辑   已结算工资= (未结算)实发工资+已结算补贴   (结算)结算工资
            long paidSalaryFee =
                    record.getState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED
                            ? record.getPaidSalaryFee() == null ? 0L : record.getPaidSalaryFee()
                            : realSalaryFee + answerSubsidyFee;
//            long paidSalaryFee = record.getPaidSalaryFee()  == null ? 0L : record.getPaidSalaryFee()+ answerSubsidyFee;
            m.put("paidSalaryFee", paidSalaryFee);
            m.put("paidSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(paidSalaryFee).longValue(), 2));

            Integer state = record.getState() == null ? 0 : record.getState();
            m.put("stateName", SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "SAL_SALARY_STATUS", String.valueOf(state)).getCodeName());

            Long complainCount = cmSalaryComplainNewMapper.getCmSalaryComplainNewCount(salaryId);
            m.put("complainCount", complainCount == null ? 0L : complainCount);


            try {
                list.add(BeanMapUtils.mapToBean(m, CmSalaryInfoNewQueryDto.class));
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("数据转换异常");
            }
        }

        Page<CmSalaryInfoNewQueryDto> dtoPage = new Page<>();
        dtoPage.setPages(ipage.getPages());
        dtoPage.setTotal(ipage.getTotal());
        dtoPage.setCurrent(ipage.getCurrent());
        dtoPage.setSize(ipage.getSize());
        dtoPage.setRecords(list);

        return dtoPage;
    }

    // 获取自定义项总和
    private Long getExtSum(CmSalaryInfoNewExt cmSalaryInfoNewExt, List<CmTemplateField> fieldList) {
        List<String> extStringList = fieldList.stream().filter(item -> item.getTableName().equals("cm_salary_info_new_ext")).map(CmTemplateField::getFieldCode).collect(Collectors.toList());
        Long extSum = 0L;
        try {
            if (extStringList.contains("fieldExt0") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt0())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt0()) ? cmSalaryInfoNewExt.getFieldExt0() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt1") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt1())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt1()) ? cmSalaryInfoNewExt.getFieldExt1() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt2") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt2())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt2()) ? cmSalaryInfoNewExt.getFieldExt2() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt3") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt3())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt3()) ? cmSalaryInfoNewExt.getFieldExt3() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt4") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt4())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt4()) ? cmSalaryInfoNewExt.getFieldExt4() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt5") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt5())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt5()) ? cmSalaryInfoNewExt.getFieldExt5() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt6") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt6())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt6()) ? cmSalaryInfoNewExt.getFieldExt6() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt7") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt7())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt7()) ? cmSalaryInfoNewExt.getFieldExt7() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt8") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt8())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt8()) ? cmSalaryInfoNewExt.getFieldExt8() : "0");
            } else {
                return extSum;
            }


            if (extStringList.contains("fieldExt9") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt9())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt9()) ? cmSalaryInfoNewExt.getFieldExt9() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt10") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt10())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt10()) ? cmSalaryInfoNewExt.getFieldExt10() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt11") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt11())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt11()) ? cmSalaryInfoNewExt.getFieldExt11() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt12") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt12())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt12()) ? cmSalaryInfoNewExt.getFieldExt12() : "0");
            } else {
                return extSum;
            }
            if (extStringList.contains("fieldExt13") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt13())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt13()) ? cmSalaryInfoNewExt.getFieldExt13() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt14") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt14())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt14()) ? cmSalaryInfoNewExt.getFieldExt14() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt15") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt15())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt15()) ? cmSalaryInfoNewExt.getFieldExt15() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt16") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt16())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt16()) ? cmSalaryInfoNewExt.getFieldExt16() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt17") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt17())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt17()) ? cmSalaryInfoNewExt.getFieldExt17() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt18") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt18())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt18()) ? cmSalaryInfoNewExt.getFieldExt18() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt19") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt19())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt19()) ? cmSalaryInfoNewExt.getFieldExt19() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt20") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt20())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt20()) ? cmSalaryInfoNewExt.getFieldExt20() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt21") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt21())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt21()) ? cmSalaryInfoNewExt.getFieldExt21() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt22") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt22())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt22()) ? cmSalaryInfoNewExt.getFieldExt22() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt23") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt23())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt23()) ? cmSalaryInfoNewExt.getFieldExt23() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt24") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt24())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt24()) ? cmSalaryInfoNewExt.getFieldExt24() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt25") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt25())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt25()) ? cmSalaryInfoNewExt.getFieldExt25() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt26") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt26())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt26()) ? cmSalaryInfoNewExt.getFieldExt26() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt27") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt27())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt27()) ? cmSalaryInfoNewExt.getFieldExt27() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt28") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt28())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt28()) ? cmSalaryInfoNewExt.getFieldExt28() : "0");
            } else {
                return extSum;
            }

            if (extStringList.contains("fieldExt29") && StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt29())) {
                extSum += doubleToLong(StringUtils.isNotEmpty(cmSalaryInfoNewExt.getFieldExt29()) ? cmSalaryInfoNewExt.getFieldExt29() : "0");
            } else {
                return extSum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("自定义项中有数据不是数字");
        }

        return extSum;
    }


    // 获取自定义项总和
    private List<CmSalaryInfoExtDto> getExtList(CmSalaryInfoNewExt cmSalaryInfoNewExt, List<CmTemplateField> fieldList) {
        List<String> extStringList = fieldList.stream().filter(item -> item.getTableName().equals("cm_salary_info_new_ext")).map(CmTemplateField::getFieldCode).collect(Collectors.toList());
        Map<String, String> extMap = new HashMap<>();
        for (CmTemplateField cmTemplateField : fieldList) {
            if (cmTemplateField.getTableName().equals("cm_salary_info_new_ext")) {
                extMap.put(cmTemplateField.getFieldCode(), cmTemplateField.getFieldDesc());
            }
        }
        List<CmSalaryInfoExtDto> cmSalaryInfoExtDtos = new ArrayList<>();
        try {
            if (extStringList.contains("fieldExt0")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt0", ""), cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt0() == null ? "0" : cmSalaryInfoNewExt.getFieldExt0()));
            }

            if (extStringList.contains("fieldExt1")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt1", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt1() == null ? "0" : cmSalaryInfoNewExt.getFieldExt1()));
            }

            if (extStringList.contains("fieldExt2")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt2", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt2() == null ? "0" : cmSalaryInfoNewExt.getFieldExt2()));
            }

            if (extStringList.contains("fieldExt3")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt3", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt3() == null ? "0" : cmSalaryInfoNewExt.getFieldExt3()));
            }

            if (extStringList.contains("fieldExt4")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt4", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt4() == null ? "0" : cmSalaryInfoNewExt.getFieldExt4()));
            }

            if (extStringList.contains("fieldExt5")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt5", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt5() == null ? "0" : cmSalaryInfoNewExt.getFieldExt5()));
            }

            if (extStringList.contains("fieldExt6")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt6", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt6() == null ? "0" : cmSalaryInfoNewExt.getFieldExt6()));
            }

            if (extStringList.contains("fieldExt7")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt7", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt7() == null ? "0" : cmSalaryInfoNewExt.getFieldExt7()));
            }

            if (extStringList.contains("fieldExt8")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt8", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt8() == null ? "0" : cmSalaryInfoNewExt.getFieldExt8()));
            }

            if (extStringList.contains("fieldExt9")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt9", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt9() == null ? "0" : cmSalaryInfoNewExt.getFieldExt9()));
            }

            if (extStringList.contains("fieldExt10")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt10", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt10() == null ? "0" : cmSalaryInfoNewExt.getFieldExt10()));
            }

            if (extStringList.contains("fieldExt11")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt11", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt11() == null ? "0" : cmSalaryInfoNewExt.getFieldExt11()));
            }

            if (extStringList.contains("fieldExt12")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt12", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt12() == null ? "0" : cmSalaryInfoNewExt.getFieldExt12()));
            }

            if (extStringList.contains("fieldExt13")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt13", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt13() == null ? "0" : cmSalaryInfoNewExt.getFieldExt13()));
            }

            if (extStringList.contains("fieldExt14")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt14", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt14() == null ? "0" : cmSalaryInfoNewExt.getFieldExt14()));
            }

            if (extStringList.contains("fieldExt15")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt15", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt15() == null ? "0" : cmSalaryInfoNewExt.getFieldExt15()));
            }

            if (extStringList.contains("fieldExt16")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt16", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt16() == null ? "0" : cmSalaryInfoNewExt.getFieldExt16()));
            }

            if (extStringList.contains("fieldExt17")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt17", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt17() == null ? "0" : cmSalaryInfoNewExt.getFieldExt17()));
            }

            if (extStringList.contains("fieldExt18")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt18", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt18() == null ? "0" : cmSalaryInfoNewExt.getFieldExt18()));
            }

            if (extStringList.contains("fieldExt19")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt19", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt19() == null ? "0" : cmSalaryInfoNewExt.getFieldExt19()));
            }

            if (extStringList.contains("fieldExt20")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt20", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt20() == null ? "0" : cmSalaryInfoNewExt.getFieldExt20()));
            }

            if (extStringList.contains("fieldExt21")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt21", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt21() == null ? "0" : cmSalaryInfoNewExt.getFieldExt21()));
            }

            if (extStringList.contains("fieldExt22")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt22", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt22() == null ? "0" : cmSalaryInfoNewExt.getFieldExt22()));
            }

            if (extStringList.contains("fieldExt23")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt23", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt23() == null ? "0" : cmSalaryInfoNewExt.getFieldExt23()));
            }

            if (extStringList.contains("fieldExt24")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt24", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt24() == null ? "0" : cmSalaryInfoNewExt.getFieldExt24()));
            }

            if (extStringList.contains("fieldExt25")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt25", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt25() == null ? "0" : cmSalaryInfoNewExt.getFieldExt25()));
            }

            if (extStringList.contains("fieldExt26")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt26", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt26() == null ? "0" : cmSalaryInfoNewExt.getFieldExt26()));
            }

            if (extStringList.contains("fieldExt27")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt27", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt27() == null ? "0" : cmSalaryInfoNewExt.getFieldExt27()));
            }

            if (extStringList.contains("fieldExt28")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt28", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt28() == null ? "0" : cmSalaryInfoNewExt.getFieldExt28()));
            }

            if (extStringList.contains("fieldExt29")) {
                cmSalaryInfoExtDtos.add(new CmSalaryInfoExtDto(extMap.getOrDefault("fieldExt29", ""),  cmSalaryInfoNewExt == null || cmSalaryInfoNewExt.getFieldExt29() == null ? "0" : cmSalaryInfoNewExt.getFieldExt29()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.info("自定义项中有数据不是数字");
        }

        return cmSalaryInfoExtDtos;
    }

    public Long doubleToLong(String money) {
        return Double.valueOf(new Double(money) * 100).longValue();
    }

    @Override
    public void sendBillBySalary(String salaryIds, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        String[] salaryIdArr = null;
        if (org.apache.commons.lang.StringUtils.isNotEmpty(salaryIds)) {
            salaryIdArr = salaryIds.split(",");
        }
        if (salaryIdArr != null && salaryIdArr.length > 0) {
            for (int i = 0; i < salaryIdArr.length; i++) {
                //发送工资单
                QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id", salaryIdArr[i]);
                OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);

                CmSalaryInfoNew cmSalaryInfoNew = this.getCmSalaryInfoNew(Long.valueOf(salaryIdArr[i]));

                if
//                (cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.SEND_OUT_BILL) {
//                    throw new BusinessException("["+cmSalaryInfoNew.getCarDriverName()+"]对账表已发送,不能重复发送");
//                }else if(cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.CONFIRM_BILL){
//                    throw new BusinessException("["+cmSalaryInfoNew.getCarDriverName()+"]账单已确认,不能发送对账单");
//                } else if
                (cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.PART_VERIFICATION || cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.VERIFICATION) {
                    throw new BusinessException("[" + cmSalaryInfoNew.getCarDriverName() + "]工资部分结算或已结算,不能发送对账单");
                } else if (cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.FINISHED_STATE1) {
                    throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]有未回单审核的订单，不能发放工资");
                }
                cmSalaryInfoNew.setState(OrderAccountConst.SALARY.SEND_OUT_BILL);
                cmSalaryInfoNew.setSendDate(LocalDateTime.now());
                cmSalaryInfoNew.setUpdateTime(LocalDateTime.now());

                //推送消息
                SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
                Map paraMap = new HashMap();
                paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
                try {
                    iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP,
                            SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                            SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap, accessToken);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 操作日志
                String remark = "[" + user.getName() + "]发送对账单[" + salaryIdArr[i] + "]";
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                        Long.parseLong(salaryIdArr[i]), SysOperLogConst.OperType.Update, remark, user.getTenantId());


                //确认工资单
                if (salaryIdArr == null) {
                    throw new BusinessException("输入的工资编号不正确!");
                }
                if (cmSalaryInfoNew == null) {
                    throw new BusinessException("根据工资编号：" + salaryIds + "未找到工资相应信息");
                }
                if (cmSalaryInfoNew.getState() != OrderAccountConst.SALARY.SEND_OUT_BILL) {
                    throw new BusinessException("已发送状态的工资才能确认!");
                }
                List<CmSalaryComplainNew> complainList = this.getCmSalaryComplainByCond(cmSalaryInfoNew.getId(), OrderAccountConst.EXAMINE.NO_EXAMINE);
                if (complainList != null && complainList.size() > 0) {
                    throw new BusinessException("工资申诉暂未处理!");
                }
                cmSalaryInfoNew.setState(OrderAccountConst.SALARY.CONFIRM_BILL);//修改状态为确认


                List<CmTemplateField> fieldList = cmSalaryTemplateService.getCmSalaryTemplateByMon(cmSalaryInfoNew.getTenantId(), cmSalaryInfoNew.getSettleMonth());
                if (fieldList != null && fieldList.size() > 0) {
                    for (CmTemplateField templateField : fieldList) {
                        //核销借支
                        if ("unwrittenLoanFee".equals(templateField.getFieldCode())) {
                            List<Integer> loanTypeOne = new ArrayList<Integer>();
                            loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT3);
                            loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT4);
                            loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT6);
                            loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT8);
                            loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT11);
                            List<OaLoan> oaLoanList = iOaLoanService1.queryOaLoan(cmSalaryInfoNew.getCarDriverId(), cmSalaryInfoNew.getTenantId(), loanTypeOne,
                                    cmSalaryInfoNew.getCarDriverPhone(), null);
                            if (oaLoanList != null && oaLoanList.size() > 0) {
                                for (OaLoan oa : oaLoanList) {
                                    long amount = oa.getAmount();//借支金额
                                    oa.setPayedAmount(amount);//全部核销
                                    iOaLoanService1.saveOrUpdate(oa);
                                }
                            }
                        }
                    }
                }
                this.saveOrUpdate(cmSalaryInfoNew);
                // 操作日志
                String remark1 = "[" + user.getName() + "]确认[" + cmSalaryInfoNew.getCarDriverName() + "]工资信息";
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary, cmSalaryInfoNew.getId(), SysOperLogConst.OperType.Update, remark1);


                //核销工资单
                if (org.apache.commons.lang.StringUtils.isEmpty(salaryIds)) {
                    throw new BusinessException("请勾选要核销的工资信息");
                }
//                if (verificationSalary == null || verificationSalary < 0L) {
//                    throw new BusinessException("要核销的金额不能为空或小于0");
//                }
                if (cmSalaryInfoNew == null) {
                    throw new BusinessException("未找到工资流水号为：[" + salaryIds + "]的工资信息");
                }
                if (cmSalaryInfoNew.getState() == EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_5) {
                    throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]已核销完毕，不能结算");
                }
                if (cmSalaryInfoNew.getState() == EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_0) {
                    throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]未发送账单，不能结算");
                }
                if (cmSalaryInfoNew.getState() == EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_2) {
                    throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机未确认，不能结算");
                }
                Long MAX_OWN_DRIVER_TOTAL_SALARY = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_OWN_DRIVER_TOTAL_SALARY_203);
                Long realSalaryFee = cmSalaryInfoNew.getRealSalaryFee() == null ? 0L : cmSalaryInfoNew.getRealSalaryFee();
                if (realSalaryFee > MAX_OWN_DRIVER_TOTAL_SALARY) {
                    throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机实发工资大于规定上限，请前往资金风控中核查");
                }
                try {
                    Long paidSalaryFee = cmSalaryInfoNew.getPaidSalaryFee() == null ? 0L : cmSalaryInfoNew.getPaidSalaryFee();
//                    paidSalaryFee+=verificationSalary;
                    if (realSalaryFee > 0L && realSalaryFee < paidSalaryFee) {
                        throw new BusinessException("核销金额大于待核销金额，请检查");
                    }
                    cmSalaryInfoNew.setPaidDate(LocalDateTime.now());
                    cmSalaryInfoNew.setUpdateOpId(user.getId());
                    cmSalaryInfoNew.setPaidSalaryFee(paidSalaryFee);
                    if (realSalaryFee - paidSalaryFee > 0) {//未核销完成
                        cmSalaryInfoNew.setState(OrderAccountConst.SALARY.PART_VERIFICATION);
                    } else if (realSalaryFee - paidSalaryFee <= 0) {//核销完成
                        cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
                    }
                    //发放工资
                    this.paySalary(cmSalaryInfoNew, accessToken, null);
                    this.saveOrUpdate(cmSalaryInfoNew);
                } catch (Exception e) {
                    throw new BusinessException(e.getMessage());
                }
            }
        }
    }

    @Override
    public CmSalaryInfoNew getCmSalaryInfoNew(Long salaryId) {
        QueryWrapper<CmSalaryInfoNew> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", salaryId);
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectOne(queryWrapper);
        if (cmSalaryInfoNew != null) {
            return cmSalaryInfoNew;
        }
        return null;
    }

    @Override
    public SubsidyInfoDto getSubsidyInfo(Long id, Long tenantId, String accessToken) {
        SubsidyInfoDto subsidyInfo = cmSalaryInfoNewMapper.getSubsidyInfo(id, tenantId);
        return subsidyInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int balanceSubsidy(Long orderId, double salary, Long salaryId, Double paidSalaryFee, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (salary < 0) {
            throw new BusinessException("结算金额不能小于0");
        }
        if (salary != 0) {
            salary = salary * 100;
        }
        int i = cmSalaryInfoNewMapper.balanceSubsidy(orderId, salary);
        if (i > 0) {
            cmSalaryInfoNewMapper.subsidyState(orderId);
            if (paidSalaryFee != null) {
                paidSalaryFee = paidSalaryFee + salary;
                int number = cmSalaryInfoNewMapper.subsidyTotal(salaryId, paidSalaryFee * 100);
                CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
                if (salary != 0) {
                    this.subsidySettlement(orderId, cmSalaryInfoNew, (long) salary, accessToken);
                }
                // 操作日志
                String remark = "[" + loginInfo.getName() + "]结算补贴[" + orderId + "]";
                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.PaySalary, orderId, SysOperLogConst.OperType.Update, remark, loginInfo.getTenantId());
                return number;
            }
        }
        return 0;
    }

    @Override
    public List<CompSalaryTemplateDto> compSalaryTemplate(String templateMonth, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        String[] yyyyMMS = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")).toString().split("-");
//        String[] yyyyMMS = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")).toString().split("-");
//        if(Long.valueOf(templateMonth.replace("-", "")) < Long.parseLong(yyyyMMS[0])){
        if (Long.valueOf(templateMonth.replace("-", "")) < 201811L) {
            throw new BusinessException("当前月份不允许更新模板");
        }
        //最新的一个模板
        CmSalaryTemplate cmSalaryTemplate = cmSalaryInfoNewMapper.getCmSalaryTemplate(tenantId);
        //对应月份的模板
        CmSalaryTemplate newCmSalaryTemplate = cmSalaryInfoNewMapper.getCmSalaryTemplateMonth(tenantId, templateMonth);
        if (cmSalaryTemplate == null) {
            throw new BusinessException("根据租户id: " + tenantId + " 未找到该租户的最新的一个模板");
        }
        if (cmSalaryTemplate != null && newCmSalaryTemplate != null && newCmSalaryTemplate.getTemplateMonth().equals(cmSalaryTemplate.getTemplateMonth())) {
            throw new BusinessException("已是最新模板，无需更新");
        }
        //工资单数据量
        Long cmSalaryInfoCount = cmSalaryInfoNewMapper.getCmSalaryInfoCount(templateMonth, tenantId, -1, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (cmSalaryInfoCount > 0) {
            //已结算工资单数量
            Long verCount = cmSalaryInfoNewMapper.getCmSalaryInfoCount(templateMonth, tenantId, EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_5, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            if ((cmSalaryInfoCount + "").equals(verCount + "")) {
                throw new BusinessException("当前月份不允许修改");
            }
        }
        boolean isUpdate = false;//判断是否有更新
        List<CompSalaryTemplateDto> dto = new ArrayList<>();
        if (Long.valueOf(templateMonth.replaceAll("[-\\s:]", "")) < Long.valueOf(cmSalaryTemplate.getTemplateMonth().replaceAll("[-\\s:]", ""))) {
            List<CmTemplateField> newTemplateFields = cmSalaryInfoNewMapper.getCmSalaryTemplateByMon(tenantId, cmSalaryTemplate.getTemplateMonth());
            List<CmTemplateField> oldTemplateFields = cmSalaryInfoNewMapper.getCmSalaryTemplateByMon(tenantId, templateMonth);
            List<String> newFieldsNames = new ArrayList<>();
            for (CmTemplateField cmTemplateField : newTemplateFields) {
                newFieldsNames.add(cmTemplateField.getFieldDesc() + cmTemplateField.getIsDefault());
//                dto.setNewFieldsNames(newFieldsNames);
                for (CmTemplateField oldTemplateField : oldTemplateFields) {
                    if (oldTemplateField.getFieldCode().equals(cmTemplateField.getFieldCode()) && !oldTemplateField.getFieldIndex().equals(cmTemplateField.getFieldIndex())) {
                        isUpdate = true;
                    }
                }
            }
            List<String> oldFieldsNames = new ArrayList<>();
            for (CmTemplateField cmTemplateField : oldTemplateFields) {
                oldFieldsNames.add(cmTemplateField.getFieldDesc() + cmTemplateField.getIsDefault());
//                dto.setOldFieldsNames(oldFieldsNames);
            }

            for (String fileld : newFieldsNames) {
                CompSalaryTemplateDto cmCustomerInfoOutDto = new CompSalaryTemplateDto();
                cmCustomerInfoOutDto.setName(String.valueOf(fileld.subSequence(0, fileld.length() - 1)));
                if (oldFieldsNames.contains(fileld)) {//如果在旧模板队列中，保留
                    cmCustomerInfoOutDto.setType(1);//原有字段
                } else {
                    cmCustomerInfoOutDto.setType(2);//新增字段
                    isUpdate = true;
                }
                dto.add(cmCustomerInfoOutDto);
            }
            for (String fileld : oldFieldsNames) {
                CompSalaryTemplateDto cmCustomerInfoOutDto = new CompSalaryTemplateDto();
                if (!newFieldsNames.contains(fileld)) {//如果在旧模板队列中，保留
                    cmCustomerInfoOutDto.setName(String.valueOf(fileld.subSequence(0, fileld.length() - 1)));
                    cmCustomerInfoOutDto.setType(3);//刪除字段
                    isUpdate = true;
                    dto.add(cmCustomerInfoOutDto);
                }

//                List<Map<String, Object>> retMap1 = dto.getRetMap();
//                for (Map<String, Object> item:retMap) {
//                    retMap1.add(item);
//                }
//                dto.setRetMap(retMap1);
            }
            if (!isUpdate) {//没有更新
                throw new BusinessException("新旧模板字段一样，不需要更新模板");
            }
        }
        return dto;
    }

//    @Override
//    public void statisticsMenuTab(String name, String accessToken) {
//        LoginInfo loginInfo = loginUtils.get(accessToken);
//        Long tenantId = loginInfo.getTenantId();
//        if(loginInfo.getAttrs()!=null && loginInfo.getAttrs().get("tenantName")!=null) {
//            String tenantName=loginInfo.getAttrs().get("tenantName").toString();
//            StringBuffer logBuf=new StringBuffer();
//            String[] tabNames= name.split("-");
//            logBuf.append("#statisticsMenuTab#tabName=").append(tabNames[0]).append(",")
//                    .append("tenantId=").append(tenantId).append(",")
//                    .append("tenantName=").append(tenantName);
//            log.info(String.valueOf(logBuf));
//        }
//    }

    @Override
    public void doSaveCmSalaryTemplate(List<SaveCmSalaryTemplateDto> saveCmSalaryTemplateDto, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        Random random = new Random();
        long id = random.nextInt(99999999);
        //查询当前车队的最新模板
        CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(tenantId);
        CmSalaryTemplate cmSalaryTemplateNew = new CmSalaryTemplate();
        cmSalaryTemplateNew.setState(1);
        cmSalaryTemplateNew.setOpId(loginInfo.getUserInfoId());
        cmSalaryTemplateNew.setOpName(loginInfo.getName());
        cmSalaryTemplateNew.setId(id);
        cmSalaryTemplateNew.setCreateTime(LocalDateTime.now());
        cmSalaryTemplateNew.setUpdateTime(LocalDateTime.now());
        cmSalaryTemplateNew.setUpdateOpId(loginInfo.getUserInfoId());
        cmSalaryTemplateNew.setTenantId(loginInfo.getTenantId());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat simpleDateFormatDay = new SimpleDateFormat("yyyy-MM");
        String endDate = simpleDateFormatDay.format(c.getTime());
        cmSalaryTemplateNew.setTemplateMonth(endDate);
        if (null == cmSalaryTemplate) {
            cmSalaryTemplateNew.setVer("1.0");
        } else {
            //设置版本号
            if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryTemplate.getVer())) {
                BigDecimal ver = new BigDecimal(cmSalaryTemplate.getVer());
                BigDecimal value = new BigDecimal("0.1");
                cmSalaryTemplateNew.setVer(ver.add(value).setScale(1, BigDecimal.ROUND_DOWN).toString());
            } else {
                cmSalaryTemplateNew.setVer("1.0");
            }
            //删除同一月份已经存在的模板数据
            if (endDate.equals(cmSalaryTemplate.getTemplateMonth())) {
                cmSalaryTemplateService.removeById(cmSalaryTemplate.getId());
            }
        }
        //保存新模板
        try {
            cmTemplateFieldMapper.addTemplateNew(cmSalaryTemplateNew);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        String cmTemplateFieldString = DataFormat.getStringKey(inParam,"cmTemplateFieldList");
//        cmTemplateFieldString = cmTemplateFieldString.replaceAll("&quot;", "\"");
        try {
//            List<Map<String,String>> cmTemplateFieldList = JsonHelper.fromJson(cmTemplateFieldString,new TypeToken<List<Map<String,String>>>(){}.getType());
            doSaveTemplateField(cmSalaryTemplateNew, saveCmSalaryTemplateDto, accessToken);
        } catch (Exception e) {
            log.error(e.getMessage() + "异常消息");
        }

    }

    @Override
    public void doSaveTemplateField(CmSalaryTemplate cmSalaryTemplateNew, List<SaveCmSalaryTemplateDto> saveCmSalaryTemplateDto, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        int idx = 0;
        for (int i = 0; i < saveCmSalaryTemplateDto.size(); i++) {
//            Map map = saveCmSalaryTemplateDto.get(i);
            CmTemplateField cmTemplateField = new CmTemplateField();
            boolean IsSelect = saveCmSalaryTemplateDto.get(i).isSelect();
//            int select=IsSelect==true?1:0;
            BeanUtils.copyProperties(saveCmSalaryTemplateDto.get(i), cmTemplateField);
            cmTemplateField.setIsSelect(1);
            cmTemplateField.setTemplateId(cmSalaryTemplateNew.getId());
            cmTemplateField.setTenantId(loginInfo.getTenantId());
            cmTemplateField.setUpdateOpId(loginInfo.getUserInfoId());
            cmTemplateField.setCreateDate(cmSalaryTemplateNew.getCreateTime());
            cmTemplateField.setUpdateDate(cmSalaryTemplateNew.getUpdateTime());
            if (null == cmTemplateField.getIsCancel()) {
                cmTemplateField.setIsCancel(1);
            }
            if (null == cmTemplateField.getIsDefault()) {
                cmTemplateField.setIsDefault(0);
            }
            if (null == cmTemplateField.getChannelType()) {
                cmTemplateField.setChannelType("WEB");
            }

            if (!EnumConsts.CM_SALARY_TABLE.CM_SALARY_INFO_NEW.equals(cmTemplateField.getTableName())) {
                cmTemplateField.setFieldCode("fieldExt" + idx);
                idx++;
            }
            if (org.apache.commons.lang.StringUtils.isBlank(cmTemplateField.getTableName())) {
                cmTemplateField.setTableName(EnumConsts.CM_SALARY_TABLE.CM_SALARY_INFO_NEW_EXT);
            }
            cmTemplateFieldMapper.addTemplateField(cmTemplateField);
        }
    }

    /**
     * 批量发送工资单
     */
    @Override
    public String sendBillByExcel(List<List<String>> listsIn, String accessToken, String orderId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        for (List<String> os : listsIn) {
            List<CmSalaryInfoNew> cmSalaryInfoNews = cmSalaryInfoNewMapper.selectSalary(os.get(2).trim(), os.get(0).trim().substring(0, 7), loginInfo.getTenantId());
            if (cmSalaryInfoNews == null || cmSalaryInfoNews.size() == 0) {
                throw new BusinessException("未找到结算月份[" + os.get(0).trim().substring(0, 7) + "]手机号[" + os.get(2).trim() + "]工资信息，导入失败");
            }
            //sendBillBySalary(String.valueOf(cmSalaryInfoNews.get(0).getId()),accessToken,orderId);
        }
        return "Y";
    }

    @Override
    public String salaryAffirmByExcel(List<List<String>> listsIn, String accessToken, String orderId) {
        LoginInfo user = loginUtils.get(accessToken);
        Long tenantId = user.getTenantId();
        for (List<String> os : listsIn) {
            String carDriverPhone = os.get(2).trim();
            String settleMonth = os.get(0).trim().substring(0, 7);
            CmSalaryInfoNew salaryInfoNew = this.getCmSalaryInfoNew(settleMonth, carDriverPhone, tenantId, com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            if (salaryInfoNew == null) {
                throw new BusinessException("未找到结算月份[" + settleMonth + "]手机号[" + carDriverPhone + "]核销信息，导入失败");
            }
            this.salaryAffirm(salaryInfoNew.getId(), accessToken);
        }
        return "Y";
    }

    @Async
    @Transactional
    @Override
    public void batchImports(byte[] byteBuffer, ImportOrExportRecords records, Integer operType, String accessToken, String salaryId) {
        List<List<String>> lists = new ArrayList<>();
        List<CmSalaryVO> failureList = new ArrayList<>();
        StringBuffer reasonFailure = new StringBuffer();
        CmSalaryVO cmSalaryVO = new CmSalaryVO();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(byteBuffer);
            lists = getExcelContent(inputStream, 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (lists == null) {
            throw new BusinessException("请选择要导入的文件");
        }
        //移除表头行
        List<String> listsRows = lists.remove(0);
        int maxNum = 1000;
        if (lists.size() == 0) {
            throw new BusinessException("导入的数量为0，导入失败");
        }
        if (lists.size() > maxNum) {
            throw new BusinessException("导入不支持超过" + maxNum + "条数据，您当前条数[" + lists.size() + "]");
        }
        //表头列判断
        List<String> checkCheckRowNames = new ArrayList<>();
        checkCheckRowNames.add("订单号");
//        checkCheckRowNames.add("司机姓名");
//        checkCheckRowNames.add("司机手机");
//        checkCheckRowNames.add("结算金额");
        String messCheckRows = "";
        if (listsRows == null || listsRows.size() != checkCheckRowNames.size()) {
            throw new BusinessException("导入EXCEL列表头格式遵循：" + messCheckRows);
        }
        int i = 0;
        for (String row : listsRows) {
            if (org.apache.commons.lang.StringUtils.isEmpty(row) || !checkCheckRowNames.get(i).equals(row.trim())) {
                throw new BusinessException("导入EXCEL列表头格式请遵循：" + messCheckRows);
            }
            ;
            i++;
        }

        List<List<String>> listsIn = new ArrayList<List<String>>();
        //去重复 根据订单去重复
        int j = 1;
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (List<String> list : lists) {
            j++;
            if (list.size() < 1) {
                reasonFailure.append("第[" + j + "]行格式错误，必须输入 订单号");
            }
            String orderId = list.get(0);
            if (org.apache.commons.lang.StringUtils.isEmpty(orderId)) {
                reasonFailure.append("第[" + j + "]行的结算月份未输入，导入失败");
            }
            cmSalaryVO.setOrderId(orderId);
//            String carDriverName = list.get(1);
//            if (org.apache.commons.lang.StringUtils.isEmpty(carDriverName)) {
//                reasonFailure.append("第[" + j + "]行的司机姓名未输入，导入失败");
//            }
//            cmSalaryVO.setCarDriverName(carDriverName);
//            String carDriverPhone = list.get(2);
//            if (org.apache.commons.lang.StringUtils.isEmpty(carDriverPhone)) {
//                reasonFailure.append("第["+ j + "]行的司机手机未输入，导入失败");
//            }
//            cmSalaryVO.setCarDriverPhone(carDriverPhone);
//            String verificationSalary = list.get(3);
//            if (org.apache.commons.lang.StringUtils.isEmpty(verificationSalary)) {
//                reasonFailure.append("第["+ j + "]行的核销金额未输入，导入失败");
//            }
//            cmSalaryVO.setVerificationSalary(verificationSalary);
            String key = orderId;
            list.add(j + "");

            List<String> ls = map.get(key);
            if (ls == null) {
                ls = new ArrayList<String>();
                ls.add(key);
                ls.add(j + "");
            } else {
                //已重复
                reasonFailure.append("第[" + j + "]行与第[" + ls.get(1) + "]结算月份 + 手机号重复，导入失败");
            }
            map.put(key, ls);
            listsIn.add(list);

            if (StrUtil.isEmpty(reasonFailure)) {
                this.dealCheckSalaryInfos(listsIn, accessToken, orderId);
            }
        }
        if (StrUtil.isNotEmpty(reasonFailure)) {
            cmSalaryVO.setReasonFailure(reasonFailure.toString());
            failureList.add(cmSalaryVO);
        }


        int success = 0;
        if (CollectionUtils.isNotEmpty(failureList)) {
            String[] showName;
            String[] resourceFild;
            showName = new String[]{"结算金额", "失败原因"};
            resourceFild = new String[]{"getSettleMonth", "getCarDriverName", "getCarDriverPhone", "getSettlementAmount", "getReasonFailure"};
            try {
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, CmCustomerInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "驾驶员工资信息.xlsx", inputStream1.available());
                os.close();
                inputStream.close();
                inputStream1.close();
                records.setFailureUrl(failureExcelPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (success > 0) {
                records.setRemarks(success + "条成功" + failureList.size() + "条失败");
                records.setState(2);
            }
            if (success == 0) {
                records.setRemarks(failureList.size() + "条失败");
                records.setState(4);
            }
        } else {
            records.setRemarks(success + "条成功");
            records.setState(3);
        }
        importOrExportRecordsService.update(records);
    }

    @Override
    public String dealCheckSalaryInfos(List<List<String>> listsIn, String accessToken, String orderId) {
        LoginInfo user = loginUtils.get(accessToken);
        //循环去修改数据 （这个效率比较慢 小于 300条还是OK的）
        Map<String, Object> inParam = new HashMap<String, Object>();
        Long tenantId = user.getTenantId();
        BigDecimal b1 = new BigDecimal(100);
        for (List<String> os : listsIn) {
            String carDriverPhone = os.get(2).trim();
            String settleMonth = os.get(0).trim().substring(0, 7);
            BigDecimal b2 = new BigDecimal(os.get(3).trim());
            Long verificationSalary = b1.multiply(b2).longValue();
            if (verificationSalary < 0) {
                throw new BusinessException("结算司机手机号为：" + carDriverPhone + " 结算月份为：" + settleMonth + "的工资信息中结算金额无效");
            }
            CmSalaryInfoNew cmSalaryInfoNew = this.getCmSalaryInfoByPhoneAndMonth(carDriverPhone, settleMonth, tenantId, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
            if (cmSalaryInfoNew != null) {
                this.checkedSalaryBill(String.valueOf(cmSalaryInfoNew.getId()), verificationSalary, accessToken, orderId);
            } else {
                throw new BusinessException("未找到司机手机号为：" + carDriverPhone + " 结算月份为：" + settleMonth + "的工资信息");
            }
        }
        return "Y";
    }

    @Override
    @Transactional
    public String checkedSalaryBill(String salaryId, Long verificationSalary, String accessToken, String orderId) {
        LoginInfo user = loginUtils.get(accessToken);
        if (org.apache.commons.lang.StringUtils.isEmpty(salaryId)) {
            throw new BusinessException("请勾选要核销的工资信息");
        }
        if (verificationSalary == null || verificationSalary < 0L) {
            throw new BusinessException("要核销的金额不能为空或小于0");
        }
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewService.getCmSalaryInfoNew(Long.valueOf(salaryId));
        if (cmSalaryInfoNew == null) {
            throw new BusinessException("未找到工资流水号为：[" + salaryId + "]的工资信息");
        }

//          外层结算与里层结算无关
        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);

        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]已核销完毕，不能结算");
        }
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_SEND) {
            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]未发送账单，不能结算");
        }
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED) {
            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机未确认，不能结算");
        }
        Long MAX_OWN_DRIVER_TOTAL_SALARY = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_OWN_DRIVER_TOTAL_SALARY_203);
        Long realSalaryFee = cmSalaryInfoNew.getRealSalaryFee() == null ? 0L : cmSalaryInfoNew.getRealSalaryFee();
        if (realSalaryFee > MAX_OWN_DRIVER_TOTAL_SALARY) {
            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机实发工资大于规定上限，请前往资金风控中核查");
        }
        try {
            Long paidSalaryFee = cmSalaryInfoNew.getPaidSalaryFee() == null ? 0L : cmSalaryInfoNew.getPaidSalaryFee();
            paidSalaryFee += verificationSalary;
            if (realSalaryFee > 0L && realSalaryFee < paidSalaryFee) {
                throw new BusinessException("核销金额大于待核销金额，请检查");
            }
            cmSalaryInfoNew.setPaidDate(LocalDateTime.now());
            cmSalaryInfoNew.setUpdateOpId(user.getId());
            cmSalaryInfoNew.setPaidSalaryFee(paidSalaryFee);//结算会修改结算工资
            if (realSalaryFee - paidSalaryFee > 0) {//未核销完成
                cmSalaryInfoNew.setState(OrderAccountConst.SALARY.PART_VERIFICATION);
            } else if (realSalaryFee - paidSalaryFee <= 0) {//核销完成
                cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
            }
            //发放工资
//            this.checkedSalaryBill(salaryId,verificationSalary,accessToken,orderId);
            this.saveOrUpdate(cmSalaryInfoNew);
            paySalary(cmSalaryInfoNew, accessToken, null);
            //SysContexts.commitTransation();
        } catch (Exception e) {
            //SysContexts.rollbackTransation();
            throw new BusinessException(e.getMessage());
        }
//        finally {
//            SysContexts.commitTransation();
//        }
        return "Y";
    }


    @Override
    @Transactional
    public String checkedSalaryBill(String salaryId, Long verificationSalary, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (org.apache.commons.lang.StringUtils.isEmpty(salaryId)) {
            throw new BusinessException("请勾选要核销的工资信息");
        }
        if (verificationSalary == null || verificationSalary < 0L) {
            throw new BusinessException("要核销的金额不能为空或小于0");
        }
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewService.getCmSalaryInfoNew(Long.valueOf(salaryId));
        if (cmSalaryInfoNew == null) {
            throw new BusinessException("未找到工资流水号为：[" + salaryId + "]的工资信息");
        }

//          外层结算与里层结算无关
//        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("order_id",orderId);
//        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);

//        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
//            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]已核销完毕，不能结算");
//        }
//        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_SEND) {
//            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]未发送账单，不能结算");
//        }
//        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED) {
//            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机未确认，不能结算");
//        }
        Long MAX_OWN_DRIVER_TOTAL_SALARY = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.MAX_OWN_DRIVER_TOTAL_SALARY_203);
        Long realSalaryFee = cmSalaryInfoNew.getRealSalaryFee() == null ? 0L : cmSalaryInfoNew.getRealSalaryFee();
        if (realSalaryFee > MAX_OWN_DRIVER_TOTAL_SALARY) {
            throw new BusinessException("结算月份[" + cmSalaryInfoNew.getSettleMonth() + "]手机号[" + cmSalaryInfoNew.getCarDriverPhone() + "]司机实发工资大于规定上限，请前往资金风控中核查");
        }
        try {
            Long paidSalaryFee = cmSalaryInfoNew.getPaidSalaryFee() == null ? 0L : cmSalaryInfoNew.getPaidSalaryFee();
            paidSalaryFee += verificationSalary;
            if (realSalaryFee > 0L && realSalaryFee < paidSalaryFee) {
                throw new BusinessException("核销金额大于待核销金额，请检查");
            }
            cmSalaryInfoNew.setPaidDate(LocalDateTime.now());
            cmSalaryInfoNew.setUpdateOpId(user.getId());
            cmSalaryInfoNew.setPaidSalaryFee(verificationSalary);//结算会修改结算工资
            //if (realSalaryFee - paidSalaryFee > 0) {//未核销完成
            //    cmSalaryInfoNew.setState(OrderAccountConst.SALARY.PART_VERIFICATION);
            //} else if (realSalaryFee - paidSalaryFee <= 0) {//核销完成
            //    cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
            //}

            cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
            //发放工资
            this.saveOrUpdate(cmSalaryInfoNew);
            paySalary(cmSalaryInfoNew, accessToken, DRIVER_SALARY);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return "Y";
    }

    private void paySalary(CmSalaryInfoNew salaryInfo, String accessToken, Long busiId) {
        LoginInfo user = loginUtils.get(accessToken);

        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
        if (salaryInfo == null) {
            throw new BusinessException("未找到工资信息!");
        }
        // TODO: 2022/5/3
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "paySalary" + salaryInfo.getCarDriverId(), 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(salaryInfo.getTenantId());
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(user.getUserInfoId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }

        CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(user.getTenantId());

        List<CmTemplateField> defaultFiledList = new ArrayList<CmTemplateField>();
        List<CmTemplateField> allFiledList = new ArrayList<CmTemplateField>();

        CmSalaryTemplate defaultCmSalaryTemplate = cmSalaryTemplateMapper.getDefaultCmSalaryTemplate();
        if (cmSalaryTemplate == null) {
            cmSalaryTemplate = defaultCmSalaryTemplate;
        } else {
            defaultFiledList = cmTemplateFieldMapper.getCmTemplateFiel(defaultCmSalaryTemplate.getId(), "", -1, EnumConsts.STATE.STATE_YES);
            allFiledList.addAll(defaultFiledList);
        }

        long templateId = cmSalaryTemplate.getId();
        List<CmTemplateField> filedList = cmTemplateFieldMapper.getCmTemplateFiel(templateId, "", -1, -1);
        allFiledList.addAll(filedList); // 工资单列表展示字段

        Date monthStart = null;
        Date monthEnd = null;
        try {
            monthStart = DateUtil.formatStringToDate(salaryInfo.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
            monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException("数据转换异常");
        }

        OrderListInVo orderListInVo = new OrderListInVo();
        orderListInVo.setTenantId(user.getTenantId());
        List<OrderListOutDto> orderListOutDtoList = orderInfoMapper.queryDriverMonthOrderInfoList(orderListInVo, monthStart, monthEnd, salaryInfo.getCarDriverId());

        Long answerSubsidyFee = 0L;
        for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
            // 已结算
            if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                answerSubsidyFee += orderListOutDto.getSettleMoney();
            }
        }

        //实发工资
        String vehicleAffiliation = "0";//工资单是不需要开票的
        String oilAffiliation = "0";//工资单是不需要开票的
        Long soNbr = CommonUtil.createSoNbr();
        // 根据用户ID和资金渠道类型获取账户信息(司机)
        OrderAccount driverAccount = orderAccountService.queryOrderAccount(salaryInfo.getCarDriverId(), vehicleAffiliation,
                0L, salaryInfo.getTenantId(), oilAffiliation, salaryInfo.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // 应收逾期(车管报销)
        BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
        amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);

        LambdaQueryWrapper<CmSalaryInfoNewExt> queryWrapperExt = new LambdaQueryWrapper<>();
        queryWrapperExt.eq(CmSalaryInfoNewExt::getSalaryId, salaryInfo.getId());
        CmSalaryInfoNewExt cmSalaryInfoNewExt = cmSalaryInfoNewExtMapper.selectOne(queryWrapperExt);
        // 结算金额 = 基本工资 + 节油奖 + 自定义项 + 已结算补贴
        Long sum = salaryInfo.getBasicSalaryFee() + salaryInfo.getSaveOilBonus() + getExtSum(cmSalaryInfoNewExt, allFiledList) + answerSubsidyFee;
        // 如果已经结算 结算金额为已结算金额
        if (salaryInfo.getState() == 5) {
            sum = salaryInfo.getPaidSalaryFee();
        }
        amountFeeSubjectsRel1.setAmountFee(sum);
        busiList.add(amountFeeSubjectsRel1);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, busiList);
        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantSysOperator.getUserInfoId(),
                tenantSysOperator.getName(), driverAccount, busiSubjectsRelList, soNbr, 0L, sysOperator.getName(),
                null, salaryInfo.getTenantId(), null, "", null, vehicleAffiliation, user);

        // 根据用户ID和资金渠道类型获取账户信息(车队)
        OrderAccount tenantAccount = orderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L,
                user.getTenantId(), oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
        // 应付逾期(车管报销)
        BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
        amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.SALARY_HANDLE);
        amountFeeSubjectsRel.setAmountFee(sum);
        tenantBusiList.add(amountFeeSubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> tenantSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantBusiList);
        // 写入账户明细表并修改账户金额费用


        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), tenantAccount, tenantSubjectsRels, soNbr, 0L,
                sysOperator.getName(), null, salaryInfo.getTenantId(), null, "", null, vehicleAffiliation, user);

        // 保存支出接口表
        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        payoutIntfDto.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现
        payoutIntfDto.setObjId(Long.parseLong(user.getTelPhone()));
        payoutIntfDto.setUserId(salaryInfo.getCarDriverId());
        //会员体系改造开始
        payoutIntfDto.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        payoutIntfDto.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //会员体系改造结束
        payoutIntfDto.setTxnAmt(sum);
        payoutIntfDto.setCreateDate(new Date());
        payoutIntfDto.setTenantId(-1L);
        payoutIntfDto.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        payoutIntfDto.setWithdrawalsChannel("4");
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_4);//工资打款
        //打款租户
        payoutIntfDto.setPayTenantId(salaryInfo.getTenantId());

        AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(salaryInfo.getCarDriverId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (defalutAccountBankRel != null) {
            payoutIntfDto.setBankCode(defalutAccountBankRel.getBankName());
            payoutIntfDto.setProvince(defalutAccountBankRel.getProvinceName());
            payoutIntfDto.setCity(defalutAccountBankRel.getCityName());
            payoutIntfDto.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
            payoutIntfDto.setAccName(defalutAccountBankRel.getAcctName());
        }

        /**
         * 初始状态 ： 即使系统自动打款,由于不开票，也要改为手动核销
         * 版本更替 >> 2018-07-04 liujl 接入平安银行改造，改为不需要手动核销司机工资
         */
        payoutIntfDto.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);//直接打款到银行卡
        payoutIntfDto.setRemark("直接打款到银行卡");

        payoutIntfDto.setPayObjId(sysTenantDef.getAdminUser());
        payoutIntfDto.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
        if (busiId != null) {
            payoutIntfDto.setBusiId(busiId);
        } else {
            payoutIntfDto.setBusiId(EnumConsts.PayInter.CAR_DRIVER_SALARY);
        }
//            payoutIntfDto.setBusiId(EnumConsts.PayInter.CAR_DRIVER_SALARY);
        payoutIntfDto.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//付款优先级别
        payoutIntfDto.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);
        /**
         * 版本更替 >> 2018-07-04 liujl
         * 判断车队是否勾选了自动打款
         * （1）、自动打款
         *       A、对公收款账户的余额转到对公付款账户。
         *       B、判断对公付款账户的余额是否大于预付金额，
         *        大于：转到司机的对私收款账户，在打款记录中增加一条已打款记录（payout_intf表）并已核销，同时触发提现接口。（若提现失败,记录提现失败记录到payout_intf表）
         *         小于：在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         * （2）、不是自动打款
         *       在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         */
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(user.getTenantId());
        if (isAutoTransfer) {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
        } else {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
        }

        Long type = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.DRIVER_SARALY_403);

        if (type == 0) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
        } else if (type == 1) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
        }
        iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto, accessToken);

        SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(salaryInfo.getCarDriverId(), salaryInfo.getCarDriverPhone(), EnumConsts.PayInter.CAR_DRIVER_SALARY, 0L, sum, vehicleAffiliation, "");
        parametersNewDto.setAccountDatailsId(payoutIntfDto.getId());
        parametersNewDto.setTenantId(salaryInfo.getTenantId());
        parametersNewDto.setSalaryId(salaryInfo.getId());
        org.springframework.beans.BeanUtils.copyProperties(sysTenantDef, sysTenantDefDto);
        parametersNewDto.setSysTenantDef(sysTenantDefDto);
        parametersNewDto.setBatchId(String.valueOf(soNbr));
        busiSubjectsRelList.addAll(tenantSubjectsRels);
        orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);
    }

    private CmSalaryInfoNew getCmSalaryInfoByPhoneAndMonth(String carDriverPhone, String settleMonth, Long tenantId, Integer userType) {
        QueryWrapper<CmSalaryInfoNew> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("carDriverPhone", carDriverPhone).eq("settleMonth", settleMonth).eq("tenantId", tenantId);
        if (userType > -1) {
            queryWrapper.eq("userType", userType);
        }
        List<CmSalaryInfoNew> list = this.list(queryWrapper);
        if (list == null || list.size() <= 0) {
            throw new BusinessException("找不到要修改的工资记录，请检查手机号和月份是否正确！");
        }
        return list.get(0);
    }

    @Async
    @Transactional
    @Override
    public void batchImport(byte[] byteBuffer, ImportOrExportRecords records, Integer operType, String token, String orderId) {
        List<List<String>> lists = new ArrayList<>();
        List<CmSalaryVO> failureList = new ArrayList<>();
        StringBuffer reasonFailure = new StringBuffer();
        CmSalaryVO cmSalaryVO = new CmSalaryVO();
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(byteBuffer);
            lists = getExcelContent(inputStream, 1, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //移除表头行
        List<String> listsRows = lists.remove(0);
        int maxNum = 1000;
        if (lists.size() == 0) {
            throw new BusinessException("导入的数量为0，导入失败");
        }
        if (lists.size() > maxNum) {
            throw new BusinessException("导入不支持超过" + maxNum + "条数据，您当前条数[" + lists.size() + "]");
        }
        List<String> titleList = new ArrayList<String>();
        for (String title : listsRows) {
            if (StringUtils.isNotBlank(title)) {
                titleList.add(title);
            }
        }

        List<String> checkConfirmRowNames = new ArrayList<>();
        checkConfirmRowNames.add("结算月份");
        checkConfirmRowNames.add("司机姓名");
        checkConfirmRowNames.add("司机手机号码");

        String messConfirmRows = "";
        for (int j = 0; j < checkConfirmRowNames.size(); j++) {
            messConfirmRows += "第" + (j + 1) + "列[" + checkConfirmRowNames.get(j) + "]" + ((j + 1) == checkConfirmRowNames.size() ? "" : "；");
        }
        //表头列判断
        if (titleList == null || titleList.size() != checkConfirmRowNames.size()) {
            reasonFailure.append("导入EXCEL列表头格式遵循：" + messConfirmRows);
        }
//			int i = 0;
////			for(String row : listsRows){
////				if(StringUtils.isEmpty(row) || !checkConfirmRowNames.get(i).equals(row.trim())){
////					throw new BusinessException("导入EXCEL列表头格式请遵循："+ messConfirmRows);
////				};
////				i++;
////			}

        List<List<String>> listsIn = new ArrayList<List<String>>();
        //去重复 根据订单去重复
        int j = 1;
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        for (List<String> l : lists) {

            j++;
            if (l.size() < 3) {
                reasonFailure.append("第[" + j + "]行格式错误，必须输入 结算月份、司机姓名、司机手机号码");
            }
            String settleMonth = l.get(0);
            if (StringUtils.isEmpty(settleMonth)) {
                reasonFailure.append("第[" + j + "]行的结算月份未输入，导入失败");
            }
            cmSalaryVO.setSettleMonth(settleMonth);

            String carDriverName = l.get(1);
            if (StringUtils.isEmpty(carDriverName)) {
                reasonFailure.append("第[" + j + "]行的司机姓名未输入，导入失败");
            }
            cmSalaryVO.setCarDriverName(carDriverName);
            String carDriverPhone = l.get(2);
            if (StringUtils.isEmpty(carDriverPhone)) {
                reasonFailure.append("第[" + j + "]行的司机手机未输入，导入失败");
            }
            cmSalaryVO.setCarDriverPhone(carDriverPhone);
            String key = settleMonth + "|" + carDriverPhone;
            l.add(j + "");

            List<String> ls = map.get(key);
            if (ls == null) {
                ls = new ArrayList<String>();
                ls.add(key);
                ls.add(j + "");
            } else {
                //已重复
                reasonFailure.append("第[" + j + "]行与第[" + ls.get(1) + "]结算月份 + 手机号重复，导入失败");
            }
            map.put(key, ls);
            listsIn.add(l);
        }
        if (StrUtil.isEmpty(reasonFailure)) {
            if (operType == 2) {
                this.sendBillByExcel(listsIn, token, orderId);
            } else {
                this.salaryAffirmByExcel(listsIn, token, orderId);
            }
        } else {
            cmSalaryVO.setReasonFailure(reasonFailure.toString());
            failureList.add(cmSalaryVO);
        }


        int success = 0;
        if (CollectionUtils.isNotEmpty(failureList)) {
            String[] showName;
            String[] resourceFild;
            showName = new String[]{"结算月份", "司机姓名", "司机手机号码", "失败原因"};
            resourceFild = new String[]{"getSettleMonth", "getCarDriverName", "getCarDriverPhone", "getReasonFailure"};
            try {
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, CmCustomerInfoVo.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "驾驶员工资信息.xlsx", inputStream1.available());
                os.close();
                inputStream.close();
                inputStream1.close();
                records.setFailureUrl(failureExcelPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (success > 0) {
                records.setRemarks(success + "条成功" + failureList.size() + "条失败");
                records.setState(2);
            }
            if (success == 0) {
                records.setRemarks(failureList.size() + "条失败");
                records.setState(4);
            }
        } else {
            records.setRemarks(success + "条成功");
            records.setState(3);
        }
        importOrExportRecordsService.update(records);
    }

    @Override
    public void salaryAffirm(Long salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (salaryId <= 0) {
            throw new BusinessException("输入的工资编号不正确!");
        }
//        if (orderId == null){
//            throw new BusinessException("请选择订单号!");
//        }
        CmSalaryInfoNew cmSalaryInfoNew = this.getCmSalaryInfoNew(salaryId);

//        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("order_id",orderId);
//        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
        if (cmSalaryInfoNew == null) {
            throw new BusinessException("根据工资编号：" + salaryId + "未找到工资相应信息");
        }
        if (cmSalaryInfoNew.getState() != OrderAccountConst.SALARY.SEND_OUT_BILL) {
            throw new BusinessException("已发送状态的工资才能确认!");
        }
        List<CmSalaryComplainNew> complainList = this.getCmSalaryComplainByCond(cmSalaryInfoNew.getId(), OrderAccountConst.EXAMINE.NO_EXAMINE);
        if (complainList != null && complainList.size() > 0) {
            throw new BusinessException("工资申诉暂未处理!");
        }
        // orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED);
        cmSalaryInfoNew.setState(OrderAccountConst.SALARY.CONFIRM_BILL);//修改状态为确认


        List<CmTemplateField> fieldList = cmSalaryTemplateService.getCmSalaryTemplateByMon(cmSalaryInfoNew.getTenantId(), cmSalaryInfoNew.getSettleMonth());
        if (fieldList != null && fieldList.size() > 0) {
            for (CmTemplateField templateField : fieldList) {
                //核销借支
                if ("unwrittenLoanFee".equals(templateField.getFieldCode())) {
                    List<Integer> loanTypeOne = new ArrayList<Integer>();
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT3);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT4);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT6);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT8);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT11);
                    List<OaLoan> oaLoanList = iOaLoanService1.queryOaLoan(cmSalaryInfoNew.getCarDriverId(), cmSalaryInfoNew.getTenantId(), loanTypeOne,
                            cmSalaryInfoNew.getCarDriverPhone(), null);
                    if (oaLoanList != null && oaLoanList.size() > 0) {
                        for (OaLoan oa : oaLoanList) {
                            Long amount = oa.getAmount();//借支金额
                            oa.setPayedAmount(amount);//全部核销
                            iOaLoanService1.saveOrUpdate(oa);
                        }
                    }
                }
            }
        }
        this.saveOrUpdate(cmSalaryInfoNew);
        // 操作日志
        String remark = "[" + user.getName() + "]确认[" + cmSalaryInfoNew.getCarDriverName() + "]工资信息";
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary, cmSalaryInfoNew.getId(), SysOperLogConst.OperType.Update, remark);
    }

    private List<CmSalaryComplainNew> getCmSalaryComplainByCond(Long salaryId, Integer state) {
        QueryWrapper<CmSalaryComplainNew> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sId", salaryId);
        if (state != null) {
            queryWrapper.eq("state", state);
        }
        List<CmSalaryComplainNew> list = iCmSalaryComplainNewService.list(queryWrapper);
        return list;
    }

    /**
     * 获取司机工资信息
     *
     * @throws Exception
     **/
    public CmSalaryInfoNew getCmSalaryInfoNew(String settleMonth, String mobilePhone, Long tenantId, Integer userType) {
        QueryWrapper<CmSalaryInfoNew> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("settleMonth", settleMonth).eq("carDriverPhone", mobilePhone).eq("tenantId", tenantId);
        if (userType > -1) {
            queryWrapper.eq("userType", userType);
        }
        CmSalaryInfoNew cmSalaryInfoNews = cmSalaryInfoNewMapper.selectOne(queryWrapper);
        if (cmSalaryInfoNews != null) {
            return cmSalaryInfoNews;
        }
        return null;
    }


    List<List<String>> getExcelContent(InputStream inputStream, int beginRow, ExcelFilesVaildate[] validates) throws Exception {
        DataFormatter dataFormatter = new DataFormatter();
        List<List<String>> fileContent = new ArrayList();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        if (rows >= 1000 + beginRow) {
            throw new BusinessException("excel文件的行数超过系统允许导入最大行数：" + 1000);
        } else {
            if (rows >= beginRow) {
                List rowList = null;
                Row row = null;

                for (int i = beginRow - 1; i < rows; ++i) {
                    row = sheet.getRow(i);
                    rowList = new ArrayList();
                    fileContent.add(rowList);
                    if (row != null) {
                        int cells = row.getLastCellNum();
                        if (cells > 200) {
                            throw new BusinessException("文件列数超过200列，请检查文件！");
                        }

                        for (int j = 0; j < cells; ++j) {
                            Cell cell = row.getCell(j);
                            String cellValue = "";
                            if (cell != null) {
                                log.debug("Reading Excel File row:" + i + ", col:" + j + " cellType:" + cell.getCellType());
                                switch (cell.getCellType()) {
                                    case 0:
                                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                            cellValue = DateUtil.formatDateByFormat(cell.getDateCellValue(), DateUtil.DATETIME_FORMAT);
                                        } else {
                                            cellValue = dataFormatter.formatCellValue(cell);
                                        }
                                        break;
                                    case 1:
                                        cellValue = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case 3:
                                        cellValue = "";
                                        break;
                                    case 4:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case 5:
                                        cellValue = String.valueOf(cell.getErrorCellValue());
                                }
                            }

                            if (validates != null && validates.length > j) {
                                if (cellValue == null) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }
                            }

                            rowList.add(cellValue);
                        }
                    }
                }
            }

            return fileContent;
        }
    }

    @Override
    public int changeSubsidy(Long id, Double subsidy, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
//        CmSalaryInfoNew salaryInfoNew = cmSalaryInfoNewMapper.salaryInfoById(id);
        subsidy = subsidy * 100;

        LambdaQueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDriverSubsidyInfo::getOrderId, id);
        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);

        orderDriverSubsidyInfo.setSubsidyFeeSum(subsidy.longValue());

        // 已确认修改补贴金额 -> 状态变更为待确认
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED) {
            orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED);
        }

        // 待确认修改补贴金额 -> 状态变更为待发送
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED) {
            orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_SEND);

            // 补贴明细变成待发送  需要将驾驶员工资发送信息删除掉
            LambdaQueryWrapper<CmSalarySendOrderInfo> query = new LambdaQueryWrapper<>();
            query.eq(CmSalarySendOrderInfo::getOrderId, id);
            CmSalarySendOrderInfo cmSalarySendOrderInfo = cmSalarySendOrderInfoMapper.selectOne(query);

            cmSalarySendInfoMapper.deleteById(cmSalarySendOrderInfo.getSendId());
            cmSalarySendOrderInfoMapper.deleteById(cmSalarySendOrderInfo.getId());

        }
        orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
        // 操作日志
        String remark = "[" + loginInfo.getName() + "]修改补贴金额[" + id + "]";
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.PaySalary, id, SysOperLogConst.OperType.Update, remark, loginInfo.getTenantId());
        return 1;
    }

    @Override
    public void modifySalaryInfo(ModifySalaryInfoVo modifySalaryInfoVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        CmSalaryInfoNew salaryInfoNew = cmSalaryInfoNewMapper.salaryInfoById(modifySalaryInfoVo.getId());
        if (salaryInfoNew == null) {
            throw new BusinessException("根据工资编号：" + modifySalaryInfoVo.getId() + " 未找到工资信息");
        } else {
            if (salaryInfoNew.getState() == OrderAccountConst.SALARY.PART_VERIFICATION || salaryInfoNew.getState() == OrderAccountConst.SALARY.VERIFICATION) {
                throw new BusinessException("结算月份[" + salaryInfoNew.getSettleMonth() + "]手机号[" + salaryInfoNew.getCarDriverPhone() + "]的工资已经处于结算状态，不能修改工资信息");
            }
        }
        modifySalaryInfoVo.setUpdateDate(LocalDateTime.now());
        modifySalaryInfoVo.setUpdateOpId(loginInfo.getId());
        modifySalaryInfoVo.setSaveOilBonus(modifySalaryInfoVo.getSaveOilBonus() * 100);
        modifySalaryInfoVo.setBasicSalaryFee(modifySalaryInfoVo.getBasicSalaryFee() * 100);
        cmSalaryInfoNewMapper.modifySalaryInfo(modifySalaryInfoVo);

        CmSalaryInfoNewExt extMap = new CmSalaryInfoNewExt();
        BeanUtils.copyProperties(modifySalaryInfoVo, extMap);
        if (extMap != null) {
            extMap.setUpdateDate(LocalDateTime.now());
            if (modifySalaryInfoVo.getId() != null) {
                QueryWrapper qw = new QueryWrapper();
                qw.eq("salary_id", modifySalaryInfoVo.getId());
                CmSalaryInfoNewExt one = cmSalaryInfoNewExtService.getOne(qw);
                if (one != null) {
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("salary_id", modifySalaryInfoVo.getId());
                    cmSalaryInfoNewExtService.update(extMap, queryWrapper);
                } else {
                    extMap.setSalaryId(modifySalaryInfoVo.getId());
                    cmSalaryInfoNewExtService.save(extMap);
                }
            }
        }
        // 操作日志
        String note = "[" + loginInfo.getName() + "]修改[" + salaryInfoNew.getCarDriverName() + "]工资信息";
        sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.PaySalary, salaryInfoNew.getId(), SysOperLogConst.OperType.Update, note);
    }

    @Override
    public void downloadExcelFile(CmSalaryInfoNewQueryVo cmSalaryInfoNewQueryVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        Date settleMonthDate = new Date();
        String settleMonth = DateUtil.formatDate(settleMonthDate, DateUtil.YEAR_MONTH_FORMAT);
        List<CmTemplateField> cmTemplateFieldList = cmSalaryTemplateService.getTemplateAllField(accessToken, settleMonth);

        LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmSalaryInfoNew::getTenantId, tenantId);
        queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, cmSalaryInfoNewQueryVo.getSettleMonth());
        if (StringUtils.isNotEmpty(cmSalaryInfoNewQueryVo.getCarDriverName())) {
            queryWrapper.like(CmSalaryInfoNew::getCarDriverName, cmSalaryInfoNewQueryVo.getCarDriverName());
        }
        if (StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getCarDriverPhone())) {
            queryWrapper.like(CmSalaryInfoNew::getCarDriverPhone, cmSalaryInfoNewQueryVo.getCarDriverPhone());
        }
        if (cmSalaryInfoNewQueryVo.getSalaryComplainSts() != null && cmSalaryInfoNewQueryVo.getSalaryComplainSts() > -1) {
            if (cmSalaryInfoNewQueryVo.getSalaryComplainSts() == 1) {
                queryWrapper.gt(CmSalaryInfoNew::getComplainCount, 0);
            } else {
                queryWrapper.or().eq(CmSalaryInfoNew::getComplainCount, 0).or().isNull(CmSalaryInfoNew::getComplainCount);
            }
        }
        if (StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getStartTime()) && StringUtils.isNotBlank(cmSalaryInfoNewQueryVo.getEndTime())) {
            try {
                queryWrapper.between(CmSalaryInfoNew::getPaidDate, DateUtil.formatStringToDate(cmSalaryInfoNewQueryVo.getStartTime() + " 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.formatStringToDate(cmSalaryInfoNewQueryVo.getEndTime() + " 23:59:59", "yyyy-MM-dd HH:mm:ss"));
            } catch (ParseException e) {
                throw new BusinessException("数据转换异常");
            }
        }
        if (cmSalaryInfoNewQueryVo.getState() != null && cmSalaryInfoNewQueryVo.getState() > -1) {
            queryWrapper.eq(CmSalaryInfoNew::getState, cmSalaryInfoNewQueryVo.getState());
        }
        if (cmSalaryInfoNewQueryVo.getUserType() != null && cmSalaryInfoNewQueryVo.getUserType() > -1) {
            queryWrapper.eq(CmSalaryInfoNew::getUserType, cmSalaryInfoNewQueryVo.getUserType());
        }

        List<CmSalaryInfoNew> cmSalaryInfoNewList = baseMapper.selectList(queryWrapper);

        List<Long> salaryList = new ArrayList<Long>();
        for (CmSalaryInfoNew cmSalaryInfoNew : cmSalaryInfoNewList) {
            salaryList.add(cmSalaryInfoNew.getId());
        }

        List<CmSalaryInfoNewExt> listExt = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(salaryList)) {
            LambdaQueryWrapper<CmSalaryInfoNewExt> queryWrapperExt = new LambdaQueryWrapper<>();
            queryWrapperExt.in(CmSalaryInfoNewExt::getSalaryId, salaryList);

            listExt = cmSalaryInfoNewExtMapper.selectList(queryWrapperExt);
        }

        List<CmSalaryInfoNewQueryDto> list = new ArrayList<>();

        for (CmSalaryInfoNew record : cmSalaryInfoNewList) {
            Map<String, Object> m = new HashMap();

            Date now = new Date();
            String settleMonthNow = DateUtil.formatDate(now, DateUtil.YEAR_MONTH_FORMAT);

            Date monthStart = null;
            Date monthEnd = null;
            try {
                monthStart = DateUtil.formatStringToDate(record.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
                monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
                throw new BusinessException("数据转换异常");
            }

            // 老系统是查指定月份全部的  不符合现在的逻辑
            Date date = null;
            Date lastDate = null;

            try {
                date = DateUtil.formatStringToDate(record.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
                lastDate = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(date) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                throw new BusinessException("数据转换异常");
            }
            OrderListInVo orderListInVo = new OrderListInVo();
            orderListInVo.setTenantId(tenantId);
            List<OrderListOutDto> orderListOutDtoList = orderInfoMapper.queryDriverMonthOrderInfoList(orderListInVo, monthStart, monthEnd, record.getCarDriverId());

            record.setOrderCount(orderListOutDtoList.size());

            m = BeanMapUtils.beanToMap(record);

            Optional<CmSalaryInfoNewExt> cmSalaryInfoNewExtOptional = listExt.stream().filter(item -> item.getSalaryId().equals(record.getId())).findFirst();
            CmSalaryInfoNewExt cmSalaryInfoNewExt = new CmSalaryInfoNewExt();
            if (cmSalaryInfoNewExtOptional.isPresent()) {
                cmSalaryInfoNewExt = cmSalaryInfoNewExtOptional.get();
            }

            long salaryId = record.getId();

            m.putAll(BeanMapUtils.beanToMap(cmSalaryInfoNewExt));
            m.put("salaryId", salaryId);

            // 老系统的逻辑
//            long answerSubsidyFee = record.getAnswerSubsidyFee() == null ? 0L : record.getAnswerSubsidyFee();
//            m.put("answerSubsidyFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(answerSubsidyFee).longValue(), 2));
            Long answerSubsidyFee = 0L;
            for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                // 已结算
                if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                    answerSubsidyFee += orderListOutDto.getSubsidyFeeSum();
                }
            }

            m.put("answerSubsidyFee", answerSubsidyFee);
            m.put("answerSubsidyFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(answerSubsidyFee).longValue(), 2));

            long lastMonthDebt = record.getLastMonthDebt() == null ? 0L : record.getLastMonthDebt();
            m.put("lastMonthDebtDouble", CommonUtil.getDoubleFormatLongMoney(new Double(lastMonthDebt).longValue(), 2));

            long basicSalaryFee = record.getBasicSalaryFee() == null ? 0L : record.getBasicSalaryFee();
            m.put("basicSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(basicSalaryFee).longValue(), 2));

            Long monthMileage = 0L;
            for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                OrderInfoDto orderLine = orderSchedulerService.queryOrderLineString(orderListOutDto.getOrderId());
                orderListOutDto.setOrderLine(orderLine.getOrderLine());

                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderListOutDto.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    boolean isReplace = false;
                    Long newMieageNumber = 0L;
                    for (int i = 0; i < switchInfos.size(); i++) {
                        OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                        if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == record.getCarDriverId()) {
                            if (i + 1 < switchInfos.size()) {
                                OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                if (isReplace) {
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        orderListOutDto.setMieageNumber(newMieageNumber);
                    }
                }

                monthMileage += orderListOutDto.getMieageNumber();
            }
            m.put("monthMileageStr", CommonUtil.divide(monthMileage, 1000L));//转换成公里

            long saveOilBonus = record.getSaveOilBonus() == null ? 0L : record.getSaveOilBonus();
            m.put("saveOilBonusDouble", CommonUtil.getDoubleFormatLongMoney(new Double(saveOilBonus).longValue(), 2));

            long tripPayFee = record.getTripPayFee() == null ? 0L : record.getTripPayFee();
            m.put("tripPayFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(tripPayFee).longValue(), 2));

            long unwrittenLoanFee = record.getUnwrittenLoanFee() == null ? 0L : record.getUnwrittenLoanFee();
            m.put("unwrittenLoanFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(unwrittenLoanFee).longValue(), 2));

            //老平台逻辑
//            long realSalaryFee = record.getRealSalaryFee() == null ? 0L : record.getRealSalaryFee();
//            m.put("realSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(realSalaryFee).longValue(), 2));
            // 新平台实发工资逻辑：节油奖 + 基本工资 + 自定义项
            Long realSalaryFee = (record.getSaveOilBonus() == null ? 0L : record.getSaveOilBonus()) + (record.getBasicSalaryFee() == null ? 0L : record.getBasicSalaryFee()) + getExtSum(cmSalaryInfoNewExt, cmTemplateFieldList);
            m.put("realSalaryFee", realSalaryFee);
            m.put("realSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(realSalaryFee).longValue(), 2));

            // 新平台逻辑   已结算工资= 实发工资+已结算补贴
            long paidSalaryFee =
                    record.getState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED
                            ? record.getPaidSalaryFee() == null ? 0L : record.getPaidSalaryFee()
                            : realSalaryFee + answerSubsidyFee;
            m.put("paidSalaryFee", paidSalaryFee);
            m.put("paidSalaryFeeDouble", CommonUtil.getDoubleFormatLongMoney(new Double(paidSalaryFee).longValue(), 2));

            Integer state = record.getState() == null ? 0 : record.getState();
            m.put("stateName", SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "SAL_SALARY_STATUS", String.valueOf(state)).getCodeName());

            Long complainCount = cmSalaryComplainNewMapper.getCmSalaryComplainNewCount(salaryId);
            m.put("complainCount", complainCount == null ? 0L : complainCount);


            try {
                list.add(BeanMapUtils.mapToBean(m, CmSalaryInfoNewQueryDto.class));
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("数据转换异常");
            }
        }

        try {
            List<String> showName = new ArrayList<>();
            // 状态不显示
            for (CmTemplateField cmTemplateField : cmTemplateFieldList) {
                // 将状态移除
                if (!cmTemplateField.getFieldCode().equals("state")) {
                    showName.add(cmTemplateField.getFieldDesc());
                }
            }

            List<String> resourceFild = new ArrayList<>();
            List<String> doubleFild = com.google.common.collect.Lists.newArrayList("answerSubsidyFee", "lastMonthDebt", "basicSalaryFee", "saveOilBonus", "tripPayFee", "unwrittenLoanFee", "realSalaryFee", "paidSalaryFee");
            for (CmTemplateField cmTemplateField : cmTemplateFieldList) {
                if (doubleFild.contains(cmTemplateField.getFieldCode())) {
                    resourceFild.add("get" + StrUtil.upperFirst(cmTemplateField.getFieldCode()) + "Double");
                } else if (cmTemplateField.getFieldCode().equals("monthMileage")) {
                    resourceFild.add("get" + StrUtil.upperFirst(cmTemplateField.getFieldCode()) + "Str");
                } else if (!cmTemplateField.getFieldCode().equals("state")) {// 将状态移除
                    resourceFild.add("get" + StrUtil.upperFirst(cmTemplateField.getFieldCode()));
                }
            }

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName.toArray(new String[]{}), resourceFild.toArray(new String[]{}), CmSalaryInfoNewQueryDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "司机工资报表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public Page<OrderListOutDto> getSalaryOrder(OrderListInVo orderListInVo, Integer pageNum, Integer pageSize) {
        CmSalaryInfoNew cmSalaryInfoNew = baseMapper.selectById(orderListInVo.getSalaryId());
        String settleMonth = cmSalaryInfoNew.getSettleMonth();
        long userId = cmSalaryInfoNew.getCarDriverId();

        if (orderListInVo.getOrderId() != null && orderListInVo.getOrderId() > 0) {
            orderListInVo.setOrderIds(String.valueOf(orderListInVo.getOrderId()));
            orderListInVo.setOrderIdList(Arrays.stream(orderListInVo.getOrderIds().replace("，", ",").split(",")).collect(Collectors.toList()));
        }

        if (StringUtils.isNotEmpty(orderListInVo.getBeginDependTime())) {
            orderListInVo.setBeginDependTime(orderListInVo.getBeginDependTime() + " 00:00:00");
        }

        if (StringUtils.isNotEmpty(orderListInVo.getEndDependTime())) {
            orderListInVo.setEndDependTime(orderListInVo.getEndDependTime() + " 23:59:59");
        }

        Long tenantId = cmSalaryInfoNew.getTenantId();
        orderListInVo.setTenantId(tenantId);

        Page<OrderListOutDto> page = new Page<>(pageNum, pageSize);

        Date now = new Date();
        String settleMonthNow = DateUtil.formatDate(now, DateUtil.YEAR_MONTH_FORMAT);

        Date monthStart = null;
        Date monthEnd = null;
        try {
            monthStart = DateUtil.formatStringToDate(settleMonth, DateUtil.YEAR_MONTH_FORMAT);
            monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException("数据转换异常");
        }
        Page<OrderListOutDto> orderListOutDtoPage = this.queryDriverMonthOrderInfo(page, orderListInVo, monthStart, monthEnd, userId);
        for (OrderListOutDto record : orderListOutDtoPage.getRecords()) {
            record.setCarDriverName(cmSalaryInfoNew.getCarDriverName());
            record.setCarDriverPhone(cmSalaryInfoNew.getCarDriverPhone());
            LambdaQueryWrapper<CmSalaryOrderExt> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CmSalaryOrderExt::getOrderId, record.getOrderId());
            CmSalaryOrderExt cmSalaryOrderExt = cmSalaryOrderExtMapper.selectOne(queryWrapper);
            if (cmSalaryOrderExt != null) {
                record.setFormerMileage(cmSalaryOrderExt.getFormerMileage() + "");
                record.setTripSalaryFee(cmSalaryOrderExt.getTripSalaryFee());
                record.setTripSalaryFeeDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getTripSalaryFee() == null ?
                        0 : cmSalaryOrderExt.getTripSalaryFee()).longValue(), 2));
                record.setSaveOilBonus(cmSalaryOrderExt.getSaveOilBonus());
                record.setSaveOilBonusDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getSaveOilBonus() == null ?
                        0 : cmSalaryOrderExt.getSaveOilBonus()).longValue(), 2));
            } else {
                record.setFormerMileage(record.getMieageNumber() == null ? "0" : record.getMieageNumber() + "");
            }
            record.setMieageNumberStr(CommonUtil.divide(record.getFormerMileage() == null ? 0L : Long.parseLong(record.getFormerMileage()), 1000L));
            record.setC(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ORDER_STATE", String.valueOf(record.getOrderState())).getCodeName());

            record.setSalaryPatternName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_PATTERN_STS", String.valueOf(record.getSalaryPattern())));
            // 该订单还未设置单挑的补贴金额
            if (record.getSubsidyFeeSum() == null) {
                //薪资模式
                if (record.getSalaryPattern() != null) {
                    // 普通模式
                    if (record.getSalaryPattern().equals(com.youming.youche.conts.SysStaticDataEnum.SALARY_PATTERN.NORMAL)) {
                        record.setSubsidyFeeSum(record.getSubsidyFee() * record.getSubsidyDay());
                    }

                    // 里程模式
                    if (record.getSalaryPattern().equals(com.youming.youche.conts.SysStaticDataEnum.SALARY_PATTERN.MILEAGE)) {
                        List<UserSalaryInfo> userSalaryInfos = userSalaryInfoService.getuserSalarInfo(record.getCarDriverId());
                        Long subsidayFeeSum = 0L;

                        for (UserSalaryInfo userSalaryInfo : userSalaryInfos) {
                            if (record.getMieageNumberStr() >= userSalaryInfo.getStartNum()) {
                                // 里程数超过某个阶段里程的最大数
                                if (record.getMieageNumberStr() >= userSalaryInfo.getEndNum()) {
                                    subsidayFeeSum += new Double((userSalaryInfo.getEndNum() - userSalaryInfo.getStartNum() + 1) * userSalaryInfo.getPrice()).longValue();
                                }

                                // 里程数没有超过某个阶段的最大数
                                if (record.getMieageNumberStr() < userSalaryInfo.getEndNum()) {

                                    subsidayFeeSum += new Double((record.getMieageNumberStr() - userSalaryInfo.getStartNum() + 1) * userSalaryInfo.getPrice()).longValue();
                                }
                            }
                        }
                        record.setSubsidyFeeSum(subsidayFeeSum);
                    }

                    // 按趟模式
                    if (record.getSalaryPattern().equals(com.youming.youche.conts.SysStaticDataEnum.SALARY_PATTERN.TIMES)) {
                        List<UserSalaryInfo> userSalaryInfos = userSalaryInfoService.getuserSalarInfo(record.getCarDriverId());
                        Long subsidayFeeSum = 0L;
                        for (UserSalaryInfo userSalaryInfo : userSalaryInfos) {
                            if (userSalaryInfo.getStartNum() <= 1 && userSalaryInfo.getEndNum() >= 1) {
                                subsidayFeeSum = userSalaryInfo.getPrice().longValue();
                                break; // 因为只有一趟所以  只要有一个满足就停掉
                            }
                        }

                        record.setSubsidyFeeSum(subsidayFeeSum);
                    }
                }

                // 添加补贴金额数据
                OrderDriverSubsidyInfo orderDriverSubsidyInfo = new OrderDriverSubsidyInfo();
                orderDriverSubsidyInfo.setOrderId(record.getOrderId());
                orderDriverSubsidyInfo.setDriverUserId(record.getCarDriverId());
                orderDriverSubsidyInfo.setSubsidyFeeSum(record.getSubsidyFeeSum());

                orderDriverSubsidyInfoMapper.insert(orderDriverSubsidyInfo);
            }

            // 目前  结算工资明细的时候  结算金额不会设置到补贴金额中  所以当补贴明细已结算的时候  补贴金额 = 已结算金额
            if (record.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                record.setSubsidyFeeSumDouble(record.getSettleMoneyDouble());
                record.setSubsidyFeeSum(record.getSettleMoney());
            }
        }

        return orderListOutDtoPage;
    }

    public Page<OrderListOutDto> queryDriverMonthOrderInfo(Page<OrderListOutDto> page, OrderListInVo orderListInVo, Date monthStart, Date monthEnd, Long userId) {
        Page<OrderListOutDto> ipage = orderInfoMapper.queryDriverMonthOrderInfo(page, orderListInVo, monthStart, monthEnd, userId);
        List<OrderListOutDto> list = ipage.getRecords();
        if (list != null && list.size() > 0) {
            for (OrderListOutDto orderListOutDto : list) {
                OrderInfoDto orderLine = orderSchedulerService.queryOrderLineString(orderListOutDto.getOrderId());
                orderListOutDto.setOrderLine(orderLine.getOrderLine());

                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderListOutDto.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    boolean isReplace = false;
                    Long newMieageNumber = 0L;
                    for (int i = 0; i < switchInfos.size(); i++) {
                        OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                        if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == userId.longValue()) {
                            if (i + 1 < switchInfos.size()) {
                                OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                if (isReplace) {
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        orderListOutDto.setMieageNumber(newMieageNumber);
                    }
                }
            }
        }

        return ipage;
    }

    @Override
    public void SalaryGoTo(String orderId, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderId == null) {
            throw new BusinessException("请选择订单号");
        }
        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
            throw new BusinessException("已结算不可以发送");
        }
        orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED);
        orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);

        // 保存工资单补贴发送信息
        CmSalarySendInfo cmSalarySendInfo = new CmSalarySendInfo();
        cmSalarySendInfo.setSalaryId(Long.valueOf(salaryId));
        cmSalarySendInfo.setSendTime(LocalDateTime.now());
        cmSalarySendInfoService.saveOrUpdate(cmSalarySendInfo);

        CmSalarySendOrderInfo cmSalarySendOrderInfo = new CmSalarySendOrderInfo();
        cmSalarySendOrderInfo.setSendId(cmSalarySendInfo.getId());
        cmSalarySendOrderInfo.setOrderId(Long.valueOf(orderId));
        cmSalarySendOrderInfoService.saveOrUpdate(cmSalarySendOrderInfo);


        //推送消息
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
        Map paraMap = new HashMap();
        paraMap.put("tenantName", sysTenantDef.getShortName());

        iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(orderDriverSubsidyInfo.getId()), paraMap, accessToken);

        // 操作日志
        String remark = "[" + user.getName() + "]发送对账单[" + orderId + "]";
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                Long.parseLong(orderId), SysOperLogConst.OperType.Update, remark, user.getTenantId());
    }

    @Override
    @Transactional
    public void SalaryGoTos(String orderIds, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderIds == null) {
            throw new BusinessException("请勾选订单号");
        }

        CmSalarySendInfo cmSalarySendInfo = new CmSalarySendInfo();
        cmSalarySendInfo.setSalaryId(Long.valueOf(salaryId));
        cmSalarySendInfo.setSendTime(LocalDateTime.now());
        cmSalarySendInfoService.saveOrUpdate(cmSalarySendInfo);
        List<CmSalarySendOrderInfo> cmSalarySendOrderInfoList = new ArrayList<>();

        String[] split = orderIds.split(",");
        for (String s : split) {
            QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", s);
            OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
            if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState().equals(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED)) {
                throw new BusinessException("已结算不可以发送");
            }
            orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED);
            orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);

            CmSalarySendOrderInfo cmSalarySendOrderInfo = new CmSalarySendOrderInfo();
            cmSalarySendOrderInfo.setSendId(cmSalarySendInfo.getId());
            cmSalarySendOrderInfo.setOrderId(Long.valueOf(s));
            cmSalarySendOrderInfoList.add(cmSalarySendOrderInfo);

            //推送消息
            CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
            Map paraMap = new HashMap();
            paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
            iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                    SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, salaryId, paraMap, accessToken);

            // 操作日志
            String remark = "[" + user.getName() + "]发送对账单[" + s + "]";
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                    Long.parseLong(s), SysOperLogConst.OperType.Update, remark, user.getTenantId());
        }

        if (CollectionUtils.isNotEmpty(cmSalarySendOrderInfoList)) {
            cmSalarySendOrderInfoService.saveBatch(cmSalarySendOrderInfoList);
        }
    }

    @Override
    public void SalaryConfirm(String orderId, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderId == null) {
            throw new BusinessException("请选择订单号");
        }
        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
            throw new BusinessException("已结算不可以再次确认");
        }
        orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED);
        orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
        //推送消息
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
        Map paraMap = new HashMap();
        paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
        iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap, accessToken);

        // 操作日志
        String remark = "[" + user.getName() + "]确认补贴[" + orderId + "]";
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                Long.parseLong(orderId), SysOperLogConst.OperType.Update, remark, user.getTenantId());
    }

    @Override
    public void SalaryConfirms(String orderIds, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderIds == null) {
            throw new BusinessException("请勾选订单号");
        }
        String[] split = orderIds.split(",");
        for (String s : split) {
            QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", s);
            OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
            if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                throw new BusinessException("已结算不可以再次确认");
            }
            orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED);
            orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
            //推送消息
            CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
            Map paraMap = new HashMap();
            paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
            iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                    SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap, accessToken);

            // 操作日志
            String remark = "[" + user.getName() + "]确认补贴[" + s + "]";
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                    Long.parseLong(s), SysOperLogConst.OperType.Update, remark, user.getTenantId());
        }
    }

    @Override
    public void SalarySettlement(String orderId, Long salaryFee, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderId == null) {
            throw new BusinessException("请选择订单号");
        }
        QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);
        OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
        if (orderDriverSubsidyInfo.getSubsidyFeeSumState().equals(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED)) {
            throw new BusinessException("已结算不可以再次结算");
        }
        orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED);
        orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
        //推送消息
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
        Long paidSalaryFee = cmSalaryInfoNew.getPaidSalaryFee();
        cmSalaryInfoNew.setPaidSalaryFee(orderDriverSubsidyInfo.getSubsidyFeeSum() + paidSalaryFee);
        orderDriverSubsidyInfo.setSubsidyFeeSum(salaryFee * 100);
        orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
        cmSalaryInfoNewMapper.updateById(cmSalaryInfoNew);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
        Map paraMap = new HashMap();
        paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
        iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap, accessToken);

        // 操作日志
        String remark = "[" + user.getName() + "]发送对账单[" + orderId + "]";
        sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                Long.parseLong(orderId), SysOperLogConst.OperType.Update, remark, user.getTenantId());
    }

    @Override
    @Transactional
    public void SalarySettlements(String orderIds, String salaryId, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (orderIds == null) {
            throw new BusinessException("请勾选订单号");
        }
        String[] split = orderIds.split(",");

        Long sumFee = 0L;
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryId);
        Long settleMoney = null;
        for (String s : split) {
            QueryWrapper<OrderDriverSubsidyInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", s);
            OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(queryWrapper);
            if (orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                throw new BusinessException("已结算不可以再次结算");
            }

            if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED) {
                throw new BusinessException("已确定的订单才能结算");
            }
            orderDriverSubsidyInfo.setSettleMoney(orderDriverSubsidyInfo.getSubsidyFeeSum());
            orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED);
            orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);
            settleMoney = orderDriverSubsidyInfo.getSettleMoney();//补贴明细结算的金额
//            sumFee+=settleMoney;
            //推送消息
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
            Map paraMap = new HashMap();
            paraMap.put("tenantName", sysTenantDef.getShortName());
//				//记录消息推送
            iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
                    SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap, accessToken);

            // 操作日志
            String remark = "[" + user.getName() + "]结算补贴[" + s + "]";
            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
                    Long.parseLong(s), SysOperLogConst.OperType.Update, remark, user.getTenantId());
            if (settleMoney != 0) {
                this.subsidySettlement(Long.valueOf(s), cmSalaryInfoNew, settleMoney, accessToken);
            }
        }

    }
    @Override
    public void subsidySettlement(Long orderId, CmSalaryInfoNew salaryInfo, Long sumFee, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
        if (salaryInfo == null) {
            throw new BusinessException("未找到补贴信息信息!");
        }

        Long tenantUserId = sysTenantDefService.getTenantAdminUser(salaryInfo.getTenantId());
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = iUserDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(user.getUserInfoId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }

        //实发工资
        String vehicleAffiliation = "0";//工资单是不需要开票的
        String oilAffiliation = "0";//工资单是不需要开票的
        Long soNbr = CommonUtil.createSoNbr();
        // 根据用户ID和资金渠道类型获取账户信息(司机)
        OrderAccount driverAccount = orderAccountService.queryOrderAccount(salaryInfo.getCarDriverId(), vehicleAffiliation,
                0L, salaryInfo.getTenantId(), oilAffiliation, salaryInfo.getUserType());
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // 应收逾期(车管报销)
//        BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
//        amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);
//
//        amountFeeSubjectsRel1.setAmountFee(sumFee);
//        busiList.add(amountFeeSubjectsRel1);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, busiList);
        //for (BusiSubjectsRel busiSubjectsRel : busiSubjectsRelList) {
        //    busiSubjectsRel.setAmountFee(sumFee);
        //}
        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantSysOperator.getUserInfoId(),
                tenantSysOperator.getName(), driverAccount, busiSubjectsRelList, soNbr, 0L, sysOperator.getName(),
                null, salaryInfo.getTenantId(), null, "", null, vehicleAffiliation, user);

        // 根据用户ID和资金渠道类型获取账户信息(车队)
        OrderAccount tenantAccount = orderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L,
                user.getTenantId(), oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
        // 应付逾期(车管报销)
//        BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
//        amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.SALARY_HANDLE);
//        amountFeeSubjectsRel.setAmountFee(sumFee);
//        tenantBusiList.add(amountFeeSubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> tenantSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantBusiList);
        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), tenantAccount, tenantSubjectsRels, soNbr, 0L,
                sysOperator.getName(), null, salaryInfo.getTenantId(), null, "", null, vehicleAffiliation, user);

        // 保存支出接口表
        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        payoutIntfDto.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现
        if (StringUtils.isNotEmpty(user.getTelPhone())) {
            payoutIntfDto.setObjId(Long.parseLong(user.getTelPhone()));
        }
        payoutIntfDto.setUserId(salaryInfo.getCarDriverId());
        //会员体系改造开始
        payoutIntfDto.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        payoutIntfDto.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //会员体系改造结束
        payoutIntfDto.setTxnAmt(sumFee);
        payoutIntfDto.setCreateDate(new Date());
        payoutIntfDto.setTenantId(-1L);
        payoutIntfDto.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        payoutIntfDto.setWithdrawalsChannel("4");
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_4);//工资打款
        //打款租户
        payoutIntfDto.setPayTenantId(salaryInfo.getTenantId());

        AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(salaryInfo.getCarDriverId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (defalutAccountBankRel != null) {
            payoutIntfDto.setBankCode(defalutAccountBankRel.getBankName());
            payoutIntfDto.setProvince(defalutAccountBankRel.getProvinceName());
            payoutIntfDto.setCity(defalutAccountBankRel.getCityName());
            payoutIntfDto.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
            payoutIntfDto.setAccName(defalutAccountBankRel.getAcctName());
        }
        //获取订单数据
        if(orderId != null && orderId > -1) {
            com.youming.youche.order.domain.order.OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            String busiCode = null;
            String plateNumber = null;
            if (orderScheduler != null && orderScheduler.getOrderId() != null) {
                busiCode = String.valueOf(orderId);
                if (StrUtil.isNotEmpty(orderScheduler.getPlateNumber())) {
                    plateNumber = orderScheduler.getPlateNumber();
                }
            } else {
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                if (orderSchedulerH != null && orderSchedulerH.getOrderId() != null) {
                    busiCode = String.valueOf(orderId);
                    if (StrUtil.isNotEmpty(orderSchedulerH.getPlateNumber())) {
                        plateNumber = orderSchedulerH.getPlateNumber();
                    }
                }
            }
            payoutIntfDto.setBusiCode(busiCode);
            payoutIntfDto.setPlateNumber(plateNumber);
            payoutIntfDto.setOrderId(orderId);
        }
        /**
         * 初始状态 ： 即使系统自动打款,由于不开票，也要改为手动核销
         * 版本更替 >> 2018-07-04 liujl 接入平安银行改造，改为不需要手动核销司机工资
         */
        payoutIntfDto.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);//直接打款到银行卡
        payoutIntfDto.setRemark("直接打款到银行卡");

        payoutIntfDto.setPayObjId(sysTenantDef.getAdminUser());
        payoutIntfDto.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
        payoutIntfDto.setBusiId(EnumConsts.PayInter.CAR_DRIVER_SALARY);
        payoutIntfDto.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//付款优先级别
        payoutIntfDto.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);

        /**
         * 版本更替 >> 2018-07-04 liujl
         * 判断车队是否勾选了自动打款
         * （1）、自动打款
         *       A、对公收款账户的余额转到对公付款账户。
         *       B、判断对公付款账户的余额是否大于预付金额，
         *         大于：转到司机的对私收款账户，在打款记录中增加一条已打款记录（payout_intf表）并已核销，同时触发提现接口。（若提现失败,记录提现失败记录到payout_intf表）
         *         小于：在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         * （2）、不是自动打款
         *       在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         */
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(user.getTenantId());
        if (isAutoTransfer) {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
        } else {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
        }

        Long type = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.DRIVER_SARALY_403);

        if (type == 0) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
        } else if (type == 1) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
        }
        iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto, accessToken);

        SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(salaryInfo.getCarDriverId(), salaryInfo.getCarDriverPhone(), EnumConsts.PayInter.CAR_DRIVER_SALARY, 0L, sumFee, vehicleAffiliation, "");
        parametersNewDto.setAccountDatailsId(payoutIntfDto.getId());
        parametersNewDto.setTenantId(salaryInfo.getTenantId());
        parametersNewDto.setSalaryId(salaryInfo.getId());
        org.springframework.beans.BeanUtils.copyProperties(sysTenantDef, sysTenantDefDto);
        parametersNewDto.setSysTenantDef(sysTenantDefDto);
        parametersNewDto.setBatchId(String.valueOf(soNbr));
        busiSubjectsRelList.addAll(tenantSubjectsRels);
        orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);
//        //订单信息
//        LambdaQueryWrapper<PayoutIntf> queryWrapper=new LambdaQueryWrapper();
//        queryWrapper.eq(PayoutIntf::getOrderId,orderId);
//        queryWrapper.orderByDesc(PayoutIntf::getCreateTime);
//        PayoutIntf payoutIntf = payoutIntfMapper.selectOne(queryWrapper);
//
//        OrderSalaryInfoVo orderSalaryInfo = cmSalaryInfoNewMapper.getOrderSalaryInfo(orderId, user.getTenantId());
//        PayoutIntfExpansion payoutIntfExpansion=new PayoutIntfExpansion();
//        payoutIntfExpansion.setBusiCode(orderSalaryInfo.getOrderId());
//        payoutIntfExpansion.setDependTime(orderSalaryInfo.getDependDate());
//        payoutIntfExpansion.setSourceName(orderSalaryInfo.getSourceName());
//        payoutIntfExpansion.setPlateNumber(orderSalaryInfo.getPlateNumber());
//        payoutIntfExpansion.setCustomName(orderSalaryInfo.getCustomName());
//        payoutIntfExpansion.setCreateTime(LocalDateTime.now());
//        payoutIntfExpansion.setFlowId(payoutIntf.getId());
//        payoutIntfExpansionService.save(payoutIntfExpansion);
    }

    @Override
    public void sendBillToBySalary(String salaryIds, Long salaryFee, String accessToken, String orderId) {
        if (salaryIds == null) {
            throw new BusinessException("请选择需要发送的工资单");
        }
        if (salaryFee == null) {
            throw new BusinessException("请输入需要发放的工资");
        }

        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(salaryIds);
        if (cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.VERIFICATION) {
            throw new BusinessException("工资已经结算");
        }

        //cmSalaryInfoNew.setPaidSalaryFee(salaryFee * 100);
        //cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
        //cmSalaryInfoNew.setUpdateDate(LocalDateTime.now());
        //cmSalaryInfoNewMapper.updateById(cmSalaryInfoNew);
        this.checkedSalaryBill(salaryIds, salaryFee * 100, accessToken);

    }

    @Override
    @Transactional
    public void sendBillToBySalarys(String salaryIds, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (salaryIds == null) {
            throw new BusinessException("请勾选需要发送的工资单");
        }


        CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(user.getTenantId());

        List<CmTemplateField> defaultFiledList = new ArrayList<CmTemplateField>();
        List<CmTemplateField> allFiledList = new ArrayList<CmTemplateField>();

        CmSalaryTemplate defaultCmSalaryTemplate = cmSalaryTemplateMapper.getDefaultCmSalaryTemplate();
        if (cmSalaryTemplate == null) {
            cmSalaryTemplate = defaultCmSalaryTemplate;
        } else {
            defaultFiledList = cmTemplateFieldMapper.getCmTemplateFiel(defaultCmSalaryTemplate.getId(), "", -1, EnumConsts.STATE.STATE_YES);
            allFiledList.addAll(defaultFiledList);
        }

        long templateId = cmSalaryTemplate.getId();
        List<CmTemplateField> filedList = cmTemplateFieldMapper.getCmTemplateFiel(templateId, "", -1, -1);
        allFiledList.addAll(filedList); // 工资单列表展示字段

        String[] split = salaryIds.split(",");
        List salaryList = Arrays.stream(split).collect(Collectors.toList());
        List<CmSalaryInfoNewExt> listExt = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(salaryList)) {
            LambdaQueryWrapper<CmSalaryInfoNewExt> queryWrapperExt = new LambdaQueryWrapper<>();
            queryWrapperExt.in(CmSalaryInfoNewExt::getSalaryId, salaryList);
            listExt = cmSalaryInfoNewExtMapper.selectList(queryWrapperExt);
        }
        for (String s : split) {
            Optional<CmSalaryInfoNewExt> cmSalaryInfoNewExtOptional = listExt.stream().filter(item -> item.getSalaryId().equals(Long.valueOf(s))).findFirst();
            CmSalaryInfoNewExt cmSalaryInfoNewExt = new CmSalaryInfoNewExt();
            if (cmSalaryInfoNewExtOptional.isPresent()) {
                cmSalaryInfoNewExt = cmSalaryInfoNewExtOptional.get();
            }
            CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewMapper.selectById(s);
            if (cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.PART_VERIFICATION || cmSalaryInfoNew.getState() == OrderAccountConst.SALARY.VERIFICATION) {
                throw new BusinessException("工资部分结算或已结算,不能发送对账单");
            }

            OrderListInVo orderListInVo = new OrderListInVo();
            orderListInVo.setTenantId(user.getTenantId());

            Date monthStart = null;
            Date monthEnd = null;
            try {
                monthStart = DateUtil.formatStringToDate(cmSalaryInfoNew.getSettleMonth(), DateUtil.YEAR_MONTH_FORMAT);
                monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<OrderListOutDto> orderListOutDtoList = orderInfoMapper.queryDriverMonthOrderInfoList(orderListInVo, monthStart, monthEnd, cmSalaryInfoNew.getCarDriverId());
            Long answerSubsidyFee = 0L; // 补贴明细
            for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                // 已结算
                if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                    answerSubsidyFee += orderListOutDto.getSettleMoney();
                }
            }


            //结算金额为 节油奖+基本工资+自定义项和 + 补贴
            Long sendBillToBySalary = (cmSalaryInfoNew.getSaveOilBonus() == null ? 0L : cmSalaryInfoNew.getSaveOilBonus())
                    + (cmSalaryInfoNew.getBasicSalaryFee() == null ? 0L : cmSalaryInfoNew.getBasicSalaryFee()) + getExtSum(cmSalaryInfoNewExt, allFiledList)
                    + answerSubsidyFee;
            Long paidSalaryFee = cmSalaryInfoNew.getPaidSalaryFee();

            Date now = new Date();
            String settleMonthNow = DateUtil.formatDate(now, DateUtil.YEAR_MONTH_FORMAT);

//            cmSalaryInfoNew.setPaidSalaryFee(sendBillToBySalary+paidSalaryFee);
            cmSalaryInfoNew.setPaidSalaryFee(sendBillToBySalary);
            cmSalaryInfoNew.setState(OrderAccountConst.SALARY.VERIFICATION);
            cmSalaryInfoNew.setPaidDate(LocalDateTime.now());

            cmSalaryInfoNew.setUpdateDate(LocalDateTime.now());
            cmSalaryInfoNewMapper.updateById(cmSalaryInfoNew);
            //推送消息
//            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(cmSalaryInfoNew.getTenantId());
//            Map paraMap = new HashMap();
//            paraMap.put("tenantName", sysTenantDef.getShortName());
////				//记录消息推送
//            iSysSmsSendService.sendSms(cmSalaryInfoNew.getCarDriverPhone(), EnumConsts.SmsTemplate.SEND_DRIVER_SALARY_TEMP, SysStaticDataEnum.SMS_TYPE.PROGRESS_NOTIFICATIONS,
//                    SysStaticDataEnum.OBJ_TYPE.SEND_DRIVER_SALARY, String.valueOf(cmSalaryInfoNew.getId()), paraMap,accessToken);
//
//            // 操作日志
//            String remark = "["+user.getName()+"]发送对账单["+s+"]";
//            sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.PaySalary,
//                    Long.parseLong(s), SysOperLogConst.OperType.Update,remark,user.getTenantId());
            this.paySalary(cmSalaryInfoNew, accessToken, DRIVER_SALARY);
        }
    }

    @Override
    public void updateCmSalaryTemplate(String templateMonth, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(user.getTenantId());
        if (cmSalaryTemplate != null) {
            if (Long.valueOf(templateMonth.replaceAll("[-\\s:]", "")) < Long.valueOf(cmSalaryTemplate.getTemplateMonth().replaceAll("[-\\s:]", ""))) {
                cmSalaryTemplate.setTemplateMonth(templateMonth);
                cmSalaryTemplate.setUpdateTime(LocalDateTime.now());
                cmSalaryTemplateService.saveOrUpdate(cmSalaryTemplate);
            }
        }
    }

    @Override
    public Page<CmSalaryComplainNewDto> doQuerySalaryComplain(Long salaryId, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        Page<CmSalaryComplainNew> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CmSalaryComplainNew> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(CmSalaryComplainNew::getSid, salaryId);

        // 分页查询工资单申诉信息
        Page<CmSalaryComplainNew> cmSalaryComplainNewPage = cmSalaryComplainNewMapper.selectPage(page, lambdaQueryWrapper);
        Page<CmSalaryComplainNewDto> ipage = new Page<>();
        ipage.setSize(cmSalaryComplainNewPage.getSize());
        ipage.setTotal(cmSalaryComplainNewPage.getTotal());
        ipage.setCurrent(cmSalaryComplainNewPage.getCurrent());
        ipage.setPages(cmSalaryComplainNewPage.getPages());

        List<CmSalaryComplainNewDto> list = new ArrayList<>();
        for (CmSalaryComplainNew record : cmSalaryComplainNewPage.getRecords()) {
            CmSalaryComplainNewDto dto = new CmSalaryComplainNewDto();
            BeanUtil.copyProperties(record, dto);

            dto.setStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + ""));
            //审核结果
            if (record.getState() == 0) {
                dto.setComplainResult(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + ""));
            } else {
                dto.setComplainResult(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + "") + "," + record.getOpReason());
            }

            list.add(dto);
        }
        ipage.setRecords(list);

        return ipage;
    }

    @Async
    @Override
    public void downloadQuerySalaryComplainExcelFile(Long salaryId, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        List<CmSalaryComplainNew> cmSalaryComplainNew = cmSalaryComplainNewMapper.getCmSalaryComplainNew(salaryId);
        List<CmSalaryComplainNewDto> list = new ArrayList<>();
        for (CmSalaryComplainNew record : cmSalaryComplainNew) {
            CmSalaryComplainNewDto dto = new CmSalaryComplainNewDto();
            BeanUtil.copyProperties(record, dto);

            dto.setStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + ""));
            //审核结果
            if (record.getState() == 0) {
                dto.setComplainResult(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + ""));
            } else {
                dto.setComplainResult(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_COMPLAIN_STS", record.getState() + "") + "," + record.getOpReason());
            }

            list.add(dto);
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "申诉时间", "申诉类型", "申诉原因", "申诉人", "状态", "审核人", "审核人手机", "审核备注", "审核时间"
            };
            resourceFild = new String[]{
                    "getCreateTime", "getSalaryComplainTypeName", "getComplainReason", "getUserName", "getStateName", "getOpName", "getOpPhone", "getOpReason", "getOpDate"
            };

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, CmSalaryComplainNewDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "工资单申诉数据列表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public void checkSalaryComplain(Long id, Integer checkStatus, String checkResult, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();
        String userName = loginInfo.getName();
        String telphone = loginInfo.getTelPhone();
        CmSalaryComplainNew salaryComplainNew = cmSalaryComplainNewMapper.selectById(id);
        if (salaryComplainNew != null) {
            salaryComplainNew.setOpId(userInfoId);
            salaryComplainNew.setOpName(userName);
            salaryComplainNew.setOpPhone(telphone);
            salaryComplainNew.setState(checkStatus);
            salaryComplainNew.setOpReason(checkResult);
            salaryComplainNew.setOpDate(LocalDateTime.now());
            // 修改工资单申诉信息
            cmSalaryComplainNewMapper.updateById(salaryComplainNew);
        } else {
            throw new BusinessException("审核失败！");
        }
    }

    @Override
    public Integer getCmSalaryInfoCount(Long userId) {
        LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmSalaryInfoNew::getCarDriverId, userId);
        //queryWrapper.eq(CmSalaryInfoNew::getState, 2);

        List<CmSalaryInfoNew> cmSalaryInfoNews = baseMapper.selectList(queryWrapper);
        Integer sum = 0;
        for (CmSalaryInfoNew cmSalaryInfoNew : cmSalaryInfoNews) {
            List<CmSalarySendInfo> cmSalarySendInfos = cmSalarySendInfoMapper.selectSalaryList(cmSalaryInfoNew.getId());
            for (CmSalarySendInfo cmSalarySendInfo : cmSalarySendInfos) {
                List<OrderDriverSubsidyInfo> driverSubsidyInfos = orderDriverSubsidyInfoMapper.selectBySendId(cmSalarySendInfo.getId());
                Integer size = driverSubsidyInfos.stream().filter(item -> item.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED).collect(Collectors.toList()).size();
                if (size != null && size > 0) {
                    sum = sum + 1;
                }
            }
        }
        return sum;
    }

    /**
     * APP-查询工资列表新版
     *
     * @param cmSalaryInfoDto
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public Page<CmSalaryInfoVo> queryCmSalaryInfo(CmSalaryInfoDto cmSalaryInfoDto, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        cmSalaryInfoDto.setUserId(loginInfo.getUserInfoId());
        cmSalaryInfoDto.setUserType(loginInfo.getUserType());
        if (cmSalaryInfoDto.getState() != null && !"".equals(cmSalaryInfoDto.getState())) {
            String[] split = cmSalaryInfoDto.getState().split(",");
            List<String> list = Arrays.asList(split);
            cmSalaryInfoDto.setStates(list);
        }
        Page<CmSalaryInfoVo> page = new Page<>(pageNum, pageSize);
        Page<CmSalaryInfoVo> cmSalaryInfoVoPage = cmSalaryComplainNewMapper.queryCmSalaryInfo(page, cmSalaryInfoDto);
        List<CmSalaryInfoVo> records = cmSalaryInfoVoPage.getRecords();
        if (records != null && records.size() > 0) {
            for (CmSalaryInfoVo record : records) {
                if (record.getState() != null) {
                    String codeName = readisUtil.getSysStaticData("SAL_SALARY_STATUS", record.getState().toString()).getCodeName();
                    record.setStsName(codeName);
                }
            }
            cmSalaryInfoVoPage.setRecords(records);
        }
        return cmSalaryInfoVoPage;
    }

    public Page<CmSalaryInfoQueryDto> queryCmSalaryInfoNew(String accessToken, Integer pageNum, Integer pageSize, Long salaryId) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();
        Integer userType = loginInfo.getUserType();
        Long tenantId = loginInfo.getTenantId();

        //String settleMonth = DateUtil.formatDate(DateUtil.diffMonth(new Date(), 1), DateUtil.YEAR_MONTH_FORMAT);

        LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, settleMonth);
        queryWrapper.eq(CmSalaryInfoNew::getCarDriverId, userInfoId);
        if (userType != null && userType > -1) {
            queryWrapper.eq(CmSalaryInfoNew::getUserType, userType);
        }

        if (salaryId != null && salaryId > 0) {
            queryWrapper.eq(CmSalaryInfoNew::getId, salaryId);
        }

        queryWrapper.orderByDesc(CmSalaryInfoNew::getSettleMonth);


        Page<CmSalaryInfoNew> page = new Page<>(pageNum, pageSize);
        Page<CmSalaryInfoNew> cmSalaryInfoNewPage = baseMapper.selectPage(page, queryWrapper);
        Page<CmSalaryInfoQueryDto> dtoPage = new Page<>();
        dtoPage.setTotal(cmSalaryInfoNewPage.getTotal());
        dtoPage.setSize(cmSalaryInfoNewPage.getSize());
        dtoPage.setCurrent(cmSalaryInfoNewPage.getCurrent());
        dtoPage.setPages(cmSalaryInfoNewPage.getPages());
        List<CmSalaryInfoNew> cmSalaryInfoNews = cmSalaryInfoNewPage.getRecords();
        List<CmSalaryInfoQueryDto> dtoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(cmSalaryInfoNews)) {

            CmSalaryTemplate cmSalaryTemplate = cmSalaryTemplateMapper.getCmSalaryTemplate(tenantId);

            List<CmTemplateField> defaultFiledList = new ArrayList<CmTemplateField>();
            List<CmTemplateField> allFiledList = new ArrayList<CmTemplateField>();

            CmSalaryTemplate defaultCmSalaryTemplate = cmSalaryTemplateMapper.getDefaultCmSalaryTemplate();
            if (cmSalaryTemplate == null) {
                cmSalaryTemplate = defaultCmSalaryTemplate;
            } else {
                defaultFiledList = cmTemplateFieldMapper.getCmTemplateFiel(defaultCmSalaryTemplate.getId(), "", -1, EnumConsts.STATE.STATE_YES);
                allFiledList.addAll(defaultFiledList);
            }

            long templateId = cmSalaryTemplate.getId();
            List<CmTemplateField> filedList = cmTemplateFieldMapper.getCmTemplateFiel(templateId, "", -1, -1);
            allFiledList.addAll(filedList); // 工资单列表展示字段

            for (CmSalaryInfoNew cmSalaryInfoNew : cmSalaryInfoNews) {

                String settleMonth = cmSalaryInfoNew.getSettleMonth();

                Date monthStart = null;
                Date monthEnd = null;
                try {
                    monthStart = DateUtil.formatStringToDate(settleMonth, DateUtil.YEAR_MONTH_FORMAT);
                    monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new BusinessException("数据转换异常");
                }

                CmSalaryInfoNewExt cmSalaryInfoNewExt = cmSalaryInfoNewExtService.getCmSalaryExtBysalaryId(cmSalaryInfoNew.getId());

                Long answerSubsidyFee = 0L;
                OrderListInVo orderListInVo = new OrderListInVo();
                orderListInVo.setTenantId(tenantId);
                List<OrderListOutDto> orderListOutDtoList = orderInfoMapper.queryDriverMonthOrderInfoList(orderListInVo, monthStart, monthEnd, cmSalaryInfoNew.getCarDriverId());
                for (OrderListOutDto orderListOutDto : orderListOutDtoList) {
                    // 已结算
                    if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                        answerSubsidyFee += orderListOutDto.getSettleMoney();
                    }
                }

                CmSalaryInfoQueryDto dto = new CmSalaryInfoQueryDto();
                dto.setSalaryId(cmSalaryInfoNew.getId());
                dto.setSettleMonth(cmSalaryInfoNew.getSettleMonth());
                dto.setCarDriverId(cmSalaryInfoNew.getCarDriverId());
                dto.setCarDriverMan(cmSalaryInfoNew.getCarDriverName());
                dto.setCarDriverPhone(cmSalaryInfoNew.getCarDriverPhone());
                dto.setSaveOilBonus(cmSalaryInfoNew.getSaveOilBonus());
                dto.setSaveOilBonusDouble(CommonUtil.getDoubleFormatLongMoney(cmSalaryInfoNew.getSaveOilBonus(), 2));
                dto.setBasicSalaryFee(cmSalaryInfoNew.getBasicSalaryFee());
                dto.setBasicSalaryFeeDouble(CommonUtil.getDoubleFormatLongMoney(cmSalaryInfoNew.getBasicSalaryFee(), 2));
                dto.setCmSalaryInfoExts(getExtList(cmSalaryInfoNewExt, filedList));
                dto.setCreateTime(cmSalaryInfoNew.getCreateDate());
                dto.setState(cmSalaryInfoNew.getState());
                dto.setStateName(cmSalaryInfoNew.getState() != null && cmSalaryInfoNew.getState() == 5 ? "已结算" : "未结算");
                dto.setPaidSalaryFee(
                        Objects.equals(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED, cmSalaryInfoNew.getState()) ? cmSalaryInfoNew.getPaidSalaryFee() == null ? 0L : cmSalaryInfoNew.getPaidSalaryFee() :
                        (cmSalaryInfoNew.getSaveOilBonus() == null ? 0L : cmSalaryInfoNew.getSaveOilBonus()) + (cmSalaryInfoNew.getBasicSalaryFee() == null ? 0L : cmSalaryInfoNew.getBasicSalaryFee()) + getExtSum(cmSalaryInfoNewExt, filedList) + answerSubsidyFee);
                dto.setPaidSalaryFeeDouble(CommonUtil.getDoubleFormatLongMoney(dto.getPaidSalaryFee(), 2));
                Long complainCount = cmSalaryComplainNewMapper.getCmSalaryComplainNewCount(cmSalaryInfoNew.getId());
                dto.setComplainCount(complainCount);

                List<CmSalarySendInfoDto> cmSalarySendInfoDtos = new ArrayList<>();
                List<CmSalarySendInfo> cmSalarySendInfos = cmSalarySendInfoMapper.selectSalaryList(cmSalaryInfoNew.getId());
                if (CollectionUtils.isNotEmpty(cmSalarySendInfos)) {
                    for (CmSalarySendInfo cmSalarySendInfo : cmSalarySendInfos) {
                        CmSalarySendInfoDto infoDto = new CmSalarySendInfoDto();
                        infoDto.setSendId(cmSalarySendInfo.getId());
                        infoDto.setSendTime(DateUtil.formatLocalDateTime(cmSalarySendInfo.getSendTime(), DateUtil.DATE_FORMAT));

                        List<OrderDriverSubsidyInfo> driverSubsidyInfos = orderDriverSubsidyInfoMapper.selectBySendId(cmSalarySendInfo.getId());
                        Long subsidyFee = 0L;
                        int state = 3; //已确认
                        int settled = 0;
                        for (OrderDriverSubsidyInfo driverSubsidyInfo : driverSubsidyInfos) {
                            // 已结算的用已结算的补贴金额
                            if (driverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                                settled = settled + 1;
                                subsidyFee += driverSubsidyInfo.getSettleMoney() == null ? 0L : driverSubsidyInfo.getSettleMoney();
                            } else {
                                subsidyFee += driverSubsidyInfo.getSubsidyFeeSum()  == null ? 0L : driverSubsidyInfo.getSubsidyFeeSum();
                            }

                            // 待确认
                            if (driverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED) {
                                state = 2;
                            }
                        }

                        if (settled == driverSubsidyInfos.size()) {
                            state = 5;
                        }

                        infoDto.setSubsidyFee(subsidyFee);
                        infoDto.setSubsidyFeeDouble(CommonUtil.getDoubleFormatLongMoney(subsidyFee, 2));
                        infoDto.setOrderCount(driverSubsidyInfos.size());
                        infoDto.setState(state);

                        if (state == 3) {
                            infoDto.setStateName("已确认");
                        } else if (state == 2) {
                            infoDto.setStateName("待确认");
                        } else if (state == 5) {
                            infoDto.setStateName("已结算");
                        }
                        cmSalarySendInfoDtos.add(infoDto);
                    }
                }
                dto.setCmSalarySendInfoDtos(cmSalarySendInfoDtos);
                dtoList.add(dto);
            }
        }

        dtoPage.setRecords(dtoList);
        return dtoPage;
    }

    @Override
    @Transactional
    public void confirmSalarySendOrder(Long sendId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        LambdaQueryWrapper<CmSalarySendOrderInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmSalarySendOrderInfo::getSendId, sendId);

        List<CmSalarySendOrderInfo> cmSalarySendOrderInfoList = cmSalarySendOrderInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(cmSalarySendOrderInfoList)) {
            for (CmSalarySendOrderInfo cmSalarySendOrderInfo : cmSalarySendOrderInfoList) {
                QueryWrapper<OrderDriverSubsidyInfo> query = new QueryWrapper<>();
                query.eq("order_id", cmSalarySendOrderInfo.getOrderId());
                OrderDriverSubsidyInfo orderDriverSubsidyInfo = orderDriverSubsidyInfoMapper.selectOne(query);

                // 待确认的才需要确认
                if (orderDriverSubsidyInfo.getSubsidyFeeSumState() != null && orderDriverSubsidyInfo.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.TO_BE_CONFIRMED) {
                    orderDriverSubsidyInfo.setSubsidyFeeSumState(com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.CONFIRMED);
                    orderDriverSubsidyInfoMapper.updateById(orderDriverSubsidyInfo);


                    // 操作日志
                    String remark = "[" + loginInfo.getName() + "]确认补贴[" + cmSalarySendOrderInfo.getOrderId() + "]";
                    sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.PaySalary,
                            cmSalarySendOrderInfo.getOrderId(), SysOperLogConst.OperType.Update, remark, loginInfo.getTenantId());
                }
            }
        }
    }

    @Override
    public Map queryCmSalaryDetail(Long salaryId) {
        CmSalaryInfoNew cmSalaryInfoNew = this.getById(salaryId);
        Map rtnMap = new HashMap();
        //取模板
        List<CmTemplateField> cmTemplateFieldList = cmSalaryTemplateService.getCmSalaryTemplateByMon(cmSalaryInfoNew.getTenantId(), cmSalaryInfoNew.getSettleMonth());
        //查询所有工资数据
        if (null == cmSalaryInfoNew) {
            throw new BusinessException("查无工资单明细");
        }
        CmSalaryInfoNewExt cmSalaryInfoNewExt = cmSalaryInfoNewExtService.getCmSalaryExtBysalaryId(salaryId);
        Map cmSalaryInfoNewExtMap = new HashMap();
        Map cmSalaryInfoNewMap = JSON.parseObject(JSON.toJSONString(cmSalaryInfoNew), Map.class);
        rtnMap.putAll(cmSalaryInfoNewMap);
        if (null != cmSalaryInfoNewExt) {
            cmSalaryInfoNewExtMap = JSON.parseObject(JSON.toJSONString(cmSalaryInfoNewExt), Map.class);
        }
        //根据模板过滤
        List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < cmTemplateFieldList.size(); i++) {
            Map<String, Object> fieldMap = new HashMap<String, Object>();
            CmTemplateField cmTemplateField = cmTemplateFieldList.get(i);
            //过滤部分主表字段
            if (isFieldFilter(cmTemplateField.getFieldCode())) {
                continue;
            }
            if (EnumConsts.CM_SALARY_TABLE.CM_SALARY_INFO_NEW.equals(cmTemplateField.getTableName()) && org.apache.commons.lang.StringUtils.isNotBlank(cmTemplateField.getFieldCode())) {
                String value = DataFormat.getStringKey(cmSalaryInfoNewMap, cmTemplateField.getFieldCode());
                if (org.apache.commons.lang.StringUtils.isBlank(value)) {
                    cmTemplateField.setValue("--");
                } else {
                    cmTemplateField.setValue(value);
                }
                fieldMap.put("tableName", cmTemplateField.getTableName());
                fieldMap.put("fieldCode", cmTemplateField.getFieldCode());
                fieldMap.put("fieldDesc", cmTemplateField.getFieldDesc());
                fieldMap.put("value", cmTemplateField.getValue());
            } else if (EnumConsts.CM_SALARY_TABLE.CM_SALARY_INFO_NEW_EXT.equals(cmTemplateField.getTableName()) && org.apache.commons.lang.StringUtils.isNotBlank(cmTemplateField.getFieldCode())) {
                String value = DataFormat.getStringKey(cmSalaryInfoNewExtMap, cmTemplateField.getFieldCode());
                if (org.apache.commons.lang.StringUtils.isBlank(value)) {
                    cmTemplateField.setValue("--");
                } else {
                    cmTemplateField.setValue(value);
                }
                fieldMap.put("tableName", cmTemplateField.getTableName());
                fieldMap.put("fieldCode", cmTemplateField.getFieldCode());
                fieldMap.put("fieldDesc", cmTemplateField.getFieldDesc());
                fieldMap.put("value", cmTemplateField.getValue());
            }
            //处理主表金额
            dealAmount(cmTemplateField.getFieldCode(), cmTemplateField.getValue(), fieldMap);
            rtnList.add(fieldMap);
        }
        int state = DataFormat.getIntKey(rtnMap, "state");
        rtnMap.put("stsName", readisUtil.getSysStaticData("SAL_SALARY_STATUS", state + "").getCodeName());
        long tenantIdRtn = DataFormat.getLongKey(rtnMap, "tenantId");
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantIdRtn);
        if (null != sysTenantDef) {
            rtnMap.put("tenantName", sysTenantDef.getName());
        }
        FastDFSHelper client = null;
        try {
            client = FastDFSHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryInfoNew.getSalaryFiles())) {
            String[] salaryFiles = cmSalaryInfoNew.getSalaryFiles().split(",");
            List<String> salaryFilesUrls = new ArrayList<String>();
            for (int i = 0; i < salaryFiles.length; i++) {
                SysAttach sysAttach = sysAttachService.getById(salaryFiles[i]);
                if (null != sysAttach) {
                    try {
                        salaryFilesUrls.add(client.getHttpURL(sysAttach.getStorePath()).split("\\?")[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            rtnMap.put("salaryFilesUrl", String.join(",", salaryFilesUrls));

        }
        rtnMap.put("cmTemplateFieldList", rtnList);
        //查询申诉数量
        LambdaQueryWrapper<CmSalaryComplainNew> lambda = Wrappers.lambdaQuery();
        lambda.eq(CmSalaryComplainNew::getSid, salaryId);
        long count = iCmSalaryComplainNewService.count(lambda);
        rtnMap.put("complainCount", count);
        return rtnMap;
    }

    @Override
    public Map<String, Object> oCmSalaryComplain(Long salaryId, String complainType, String complainReason, String token) {
        CmSalaryInfoNew salaryInfoNew = this.getById(salaryId);
        if (salaryInfoNew == null) {
            throw new BusinessException("根据工资编号：" + salaryId + " 未找到工资信息");
        }
        CmSalaryComplainNew salaryComplain = new CmSalaryComplainNew();
        salaryComplain.setSid(salaryId);
        salaryComplain.setComplainReason(complainReason);
        LoginInfo user = loginUtils.get(token);
        salaryComplain.setUserId(user.getUserInfoId());
        salaryComplain.setUserName(user.getName());
        salaryComplain.setState(0);
        salaryComplain.setSalaryComplainType(Integer.valueOf(complainType));
        salaryComplain.setSalaryComplainTypeName(readisUtil.getSysStaticData("SALARY_COMPLAIN_TYPE", complainType + "").getCodeName());
        iCmSalaryComplainNewService.saveOrUpdate(salaryComplain);
        int complainCount = salaryInfoNew.getComplainCount() == null ? 0 : salaryInfoNew.getComplainCount();
        salaryInfoNew.setComplainCount(++complainCount);
        cmSalaryInfoNewService.saveOrUpdate(salaryInfoNew);
        Map<String, Object> retMap = new HashMap<String, Object>();
        retMap.put("flag", "Y");
        return retMap;
    }

    @Override
    public List<OrderListOutDto> getCmSalaryOrderInfo(CmSalaryOrderInfoDto cmSalaryOrderInfoDto) {
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewService.getById(cmSalaryOrderInfoDto.getSalaryId());
        String settleMonth = cmSalaryInfoNew.getSettleMonth();
        long userId = cmSalaryInfoNew.getCarDriverId();
        OrderListInVo orderListIn = new OrderListInVo();
        //  OrderListInDto orderListIn = new OrderListInDto();
        if (cmSalaryOrderInfoDto.getOrderId() != null && cmSalaryOrderInfoDto.getOrderId() > 0) {
            orderListIn.setOrderIds(cmSalaryOrderInfoDto.getOrderId() + "");
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryOrderInfoDto.getBeginDependTime())) {
            orderListIn.setBeginDependTime(cmSalaryOrderInfoDto.getBeginDependTime());
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryOrderInfoDto.getEndDependTime())) {
            orderListIn.setEndDependTime(cmSalaryOrderInfoDto.getEndDependTime());
        }
        if (cmSalaryOrderInfoDto.getSourceRegion() != null && cmSalaryOrderInfoDto.getSourceRegion() > 0) {
            orderListIn.setSourceRegion(cmSalaryOrderInfoDto.getSourceRegion());
        }
        if (cmSalaryOrderInfoDto.getDesRegion() != null && cmSalaryOrderInfoDto.getDesRegion() > 0) {
            orderListIn.setDesRegion(cmSalaryOrderInfoDto.getDesRegion());
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryOrderInfoDto.getCustomName())) {
            orderListIn.setCustomName(cmSalaryOrderInfoDto.getCustomName());
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(cmSalaryOrderInfoDto.getPlateNumber())) {
            orderListIn.setPlateNumber(cmSalaryOrderInfoDto.getPlateNumber());
        }
//        if(cmSalaryOrderInfoDto.getOrderState() != null && cmSalaryOrderInfoDto.getOrderState()>0){
//            orderListIn.setOrderState(cmSalaryOrderInfoDto.getOrderState());
//        }
        Long tenantId = cmSalaryInfoNew.getTenantId();
        orderListIn.setTenantId(tenantId);
        Date monthStart = null;
        Date monthEnd = null;
        if (StringUtils.isNotBlank(settleMonth)) {
            try {
                monthStart = DateUtil.formatStringToDate(settleMonth, DateUtil.YEAR_MONTH_FORMAT);
                monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<OrderListOutDto> list = orderInfoMapper.queryDriverMonthOrderInfoList(orderListIn, monthStart, monthEnd, userId);
        if (list != null && list.size() > 0) {
            for (OrderListOutDto map : list) {
                Long orderId = map.getOrderId();
                OrderInfoDto orderLineMap = orderSchedulerService.queryOrderLineString(orderId);
                map.setOrderLine(orderLineMap.getOrderLine());
                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    boolean isReplace = false;
                    Long newMieageNumber = 0L;
                    for (int i = 0; i < switchInfos.size(); i++) {
                        OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                        if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == userId) {
                            if (i + 1 < switchInfos.size()) {
                                OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                if (isReplace) {
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        map.setMieageNumber(newMieageNumber);
                    }
                }
            }
        }
        for (OrderListOutDto orderListOutDto : list) {
            LambdaQueryWrapper<CmSalaryOrderExt> lambda = Wrappers.lambdaQuery();
            lambda.eq(CmSalaryOrderExt::getOrderId, orderListOutDto.getOrderId());
            CmSalaryOrderExt cmSalaryOrderExt = cmSalaryOrderExtMapper.selectOne(lambda);
            if (cmSalaryOrderExt != null) {
                orderListOutDto.setFormerMileage(cmSalaryOrderExt.getFormerMileage() == null ? "0" : cmSalaryOrderExt.getFormerMileage() + "");
                orderListOutDto.setTripSalaryFee(cmSalaryOrderExt.getTripSalaryFee());
                orderListOutDto.setTripSalaryFeeDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getTripSalaryFee() == null ?
                        0 : cmSalaryOrderExt.getTripSalaryFee()).longValue(), 2));
                orderListOutDto.setSaveOilBonus(cmSalaryOrderExt.getSaveOilBonus());
                orderListOutDto.setSaveOilBonusDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getSaveOilBonus() == null ?
                        0 : cmSalaryOrderExt.getSaveOilBonus()).longValue(), 2));
            } else {
                orderListOutDto.setFormerMileage(orderListOutDto.getMieageNumber() == null ? "0" : orderListOutDto.getMieageNumber() + "");

            }
            orderListOutDto.setMieageNumberStr(CommonUtil.divide(orderListOutDto.getFormerMileage() == null ? 0L : Long.parseLong(orderListOutDto.getFormerMileage()), 1000L));
            if (orderListOutDto.getOrderState() != null) {
                orderListOutDto.setC(readisUtil.getSysStaticData("ORDER_STATE", orderListOutDto.getOrderState() + "").getState());
            }
        }
        return list;
    }

    @Override
    public List<OrderListOutDto> getCmSalaryOrderInfoNew(Long sendId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        CmSalarySendInfo cmSalarySendInfo = cmSalarySendInfoMapper.selectById(sendId);
        CmSalaryInfoNew cmSalaryInfoNew = baseMapper.selectById(cmSalarySendInfo.getSalaryId());
        Long userId = cmSalaryInfoNew.getCarDriverId();

        List<OrderListOutDto> orderListOutDtos = orderInfoMapper.queryDriverMonthOrderInfoListNew(sendId);
        if (orderListOutDtos != null && orderListOutDtos.size() > 0) {
            for (OrderListOutDto orderListOutDto : orderListOutDtos) {
                OrderInfoDto orderLine = orderSchedulerService.queryOrderLineString(orderListOutDto.getOrderId());
                orderListOutDto.setOrderLine(orderLine.getOrderLine());

                List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderListOutDto.getOrderId(), OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                if (switchInfos != null && switchInfos.size() > 0) {
                    boolean isReplace = false;
                    Long newMieageNumber = 0L;
                    for (int i = 0; i < switchInfos.size(); i++) {
                        OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                        if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == userId) {
                            if (i + 1 < switchInfos.size()) {
                                OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                if (isReplace) {
                                    isReplace = true;
                                }
                            }
                        }
                    }
                    if (isReplace) {
                        orderListOutDto.setMieageNumber(newMieageNumber);
                    }
                }

                LambdaQueryWrapper<CmSalaryOrderExt> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CmSalaryOrderExt::getOrderId, orderListOutDto.getOrderId());
                CmSalaryOrderExt cmSalaryOrderExt = cmSalaryOrderExtMapper.selectOne(queryWrapper);
                if (cmSalaryOrderExt != null) {
                    orderListOutDto.setFormerMileage(cmSalaryOrderExt.getFormerMileage() + "");
                    orderListOutDto.setTripSalaryFee(cmSalaryOrderExt.getTripSalaryFee());
                    orderListOutDto.setTripSalaryFeeDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getTripSalaryFee() == null ?
                            0 : cmSalaryOrderExt.getTripSalaryFee()).longValue(), 2));
                    orderListOutDto.setSaveOilBonus(cmSalaryOrderExt.getSaveOilBonus());
                    orderListOutDto.setSaveOilBonusDouble(CommonUtil.getDoubleFormatLongMoney(new Double(cmSalaryOrderExt.getSaveOilBonus() == null ?
                            0 : cmSalaryOrderExt.getSaveOilBonus()).longValue(), 2));
                } else {
                    orderListOutDto.setFormerMileage(orderListOutDto.getMieageNumber() == null ? "0" : orderListOutDto.getMieageNumber() + "");
                }
                orderListOutDto.setMieageNumberStr(CommonUtil.divide(orderListOutDto.getFormerMileage() == null ? 0L : Long.parseLong(orderListOutDto.getFormerMileage()), 1000L));
                orderListOutDto.setC(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ORDER_STATE", String.valueOf(orderListOutDto.getOrderState())).getCodeName());

                orderListOutDto.setSalaryPatternName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, tenantId, "SALARY_PATTERN_STS", String.valueOf(orderListOutDto.getSalaryPattern())));

                // 目前  结算工资明细的时候  结算金额不会设置到补贴金额中  所以当补贴明细已结算的时候  补贴金额 = 已结算金额
                if (orderListOutDto.getSubsidyFeeSumState() == com.youming.youche.conts.EnumConsts.SUBSIDY_FEE_SUM_STATE.SETTLED) {
                    orderListOutDto.setSubsidyFeeSumDouble(orderListOutDto.getSettleMoneyDouble());
                    orderListOutDto.setSubsidyFeeSum(orderListOutDto.getSettleMoney());
                }

                orderListOutDto.setCarDriverId(cmSalaryInfoNew.getCarDriverId());
                orderListOutDto.setCarDriverName(cmSalaryInfoNew.getCarDriverName());
                orderListOutDto.setCarDriverPhone(cmSalaryInfoNew.getCarDriverPhone());
            }
        }

        return orderListOutDtos;
    }

    @Override
    public Map<String, Object> getDriverSubsidyInfo(Long salaryId, String channelType) {
        CmSalaryInfoNew cmSalaryInfoNew = cmSalaryInfoNewService.getCmSalaryInfoNew(salaryId);
        String settleMonth = cmSalaryInfoNew.getSettleMonth();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        Date beginDate = null;
        Date endDate = null;
        try {
            date = sdf.parse(settleMonth + "-01 00:00:00");
            beginDate = sdf.parse(TimeUtil.getMonthFirstDay(date) + " 00:00:00");
            endDate = sdf.parse(TimeUtil.getMonthLastDay(date) + " 23:59:59");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Map<String, Object> retMap = new HashMap<String, Object>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        int year = cal.get(Calendar.YEAR);
        int moth = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DATE);
        int maxDays = getDaysByYearMonth(year, moth);
        int paidDays = 0;
        List<OrderDriverSubsidy> list = orderDriverSubsidyService.findDriverSubsidys(LocalDateTimeUtil.of(beginDate),
                LocalDateTimeUtil.of(endDate), null, cmSalaryInfoNew.getCarDriverId(),
                cmSalaryInfoNew.getTenantId(), false, null);
        for (OrderDriverSubsidy orderDriverSubsidy : list) {
            LocalDateTime subsidyDate = orderDriverSubsidy.getSubsidyDate();
            if (orderDriverSubsidy.getIsPayed() == 1) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(DateUtil.asDate(subsidyDate));
                int subsidyDay = cal1.get(Calendar.DATE);
                if ("WEB".equals(channelType)) {
                    retMap.put("fieldExt" + subsidyDay, "已发" + CommonUtil.getDoubleFormatLongMoney(new Double(orderDriverSubsidy.getSubsidy()).longValue(), 2));//已付
                } else {
                    retMap.put("fieldExt" + subsidyDay, CommonUtil.getDoubleFormatLongMoney(new Double(orderDriverSubsidy.getSubsidy()).longValue(), 2));//已付

                }
                paidDays++;
            }

        }
        List<OrderDriverSubsidy> listH = orderDriverSubsidyService.findDriverSubsidys(LocalDateTimeUtil.of(beginDate),
                LocalDateTimeUtil.of(endDate), null, cmSalaryInfoNew.getCarDriverId(), cmSalaryInfoNew.getTenantId(),
                true, null);
        for (OrderDriverSubsidy orderDriverSubsidy : listH) {
            LocalDateTime subsidyDate = orderDriverSubsidy.getSubsidyDate();
            if (orderDriverSubsidy.getIsPayed() == 1) {
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(DateUtil.asDate(subsidyDate));
                int subsidyDay = cal1.get(Calendar.DAY_OF_MONTH);
                if ("WEB".equals(channelType)) {
                    retMap.put("fieldExt" + subsidyDay, "已发" + CommonUtil.getDoubleFormatLongMoney(new Double(orderDriverSubsidy.getSubsidy()).longValue(), 2));//已付
                } else {
                    retMap.put("fieldExt" + subsidyDay, CommonUtil.getDoubleFormatLongMoney(new Double(orderDriverSubsidy.getSubsidy()).longValue(), 2));//已付
                }
                paidDays++;
            }

        }
        retMap.put("settleMonth", settleMonth);//月份
        retMap.put("carDriverName", cmSalaryInfoNew.getCarDriverName());//司机名称
        retMap.put("carDriverPhone", cmSalaryInfoNew.getCarDriverPhone());//司机手机
        retMap.put("paidDays", paidDays);//已发补贴
        retMap.put("noPayDays", maxDays - paidDays);//未发补贴
        retMap.put("maxDays", maxDays);//月最大日
        retMap.put("paidSalaryFee", cmSalaryInfoNew.getAnswerSubsidyFee());//已结算金额
        //来源车队
        SysTenantDef tenantDef = sysTenantDefService.getById(cmSalaryInfoNew.getTenantId());
        retMap.put("sourceName", tenantDef.getName());
        return retMap;
    }

    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    private boolean isFieldFilter(String fieldCode) {
        //工资月份
        if ("settleMonth".equals(fieldCode)) {
            return true;
        }
        //月里程
        if ("orderCount".equals(fieldCode)) {
            return true;
        }
        //状态
        if ("state".equals(fieldCode)) {
            return true;
        }
        //已发补贴
        if ("answerSubsidyFee".equals(fieldCode)) {
            return true;
        }
        //月里程
        if ("monthMileage".equals(fieldCode)) {
            return true;
        }
        //实发工资
        if ("realSalaryFee".equals(fieldCode)) {
            return true;
        }
        //已结算工资
        if ("paidSalaryFee".equals(fieldCode)) {
            return true;
        }
        //司机手机号码
        if ("carDriverPhone".equals(fieldCode)) {
            return true;
        }
        //司机姓名
        if ("carDriverName".equals(fieldCode)) {
            return true;
        }
        //附件
        if ("salaryFiles".equals(fieldCode)) {
            return true;
        }
        //申述数量
        if ("complainCount".equals(fieldCode)) {
            return true;
        }
        //备注
        if ("remark".equals(fieldCode)) {
            return true;
        }
        return false;
    }


    public void dealAmount(String code, String value, Map<String, Object> fieldMap) {
        BigDecimal defaultValue = new BigDecimal("100");
        NumberFormat nf = new DecimalFormat("##.##");
        if ("basicSalaryFee".equals(code) && CommonUtil.isNumber(value)) {
            BigDecimal bd = new BigDecimal(value);
            fieldMap.put("value", bd.divide(defaultValue));
        } else if ("tripPayFee".equals(code) && CommonUtil.isNumber(value)) {
            BigDecimal bd = new BigDecimal(value);
            fieldMap.put("value", bd.divide(defaultValue));
        } else if ("lastMonthDebt".equals(code) && CommonUtil.isNumber(value)) {
            BigDecimal bd = new BigDecimal(value);
            fieldMap.put("value", bd.divide(defaultValue));
        } else if ("saveOilBonus".equals(code) && CommonUtil.isNumber(value)) {
            BigDecimal bd = new BigDecimal(value);
            fieldMap.put("value", bd.divide(defaultValue));
        }
    }

    @Override
    @Transactional
    public void newSalaryData() {
        log.info("NewSalaryDataTask:跑工资进程（新）开始。。。。。。");

        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue());
        if (loginInfo == null) {
            loginInfo = new LoginInfo();
            loginInfo.setName(((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_USER_NAME))).getCfgValue());
            loginInfo.setUserInfoId(Long.valueOf(((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_USER_ID))).getCfgValue()));
            loginInfo.setTenantId(-1L);

            redisUtil.set("loginInfo:" + ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue(), loginInfo);
        }

        String accessToken = ((SysCfg) redisUtil.get(com.youming.youche.conts.EnumConsts.SysCfg.SYS_CFG_NAME.concat(com.youming.youche.conts.EnumConsts.TASK_USER.TASK_TOKEN_NAME))).getCfgValue();

        Date now = new Date();
        String lastMonthBeginDate = DateUtil.getLastMonthFirstDay(now) + " 00:00:00";
        String lastMonthEndDate = DateUtil.getLastMonthLastDay(now) + " 23:59:59";

        Long totalRows = sysTenantDefService.getTenantIdCount();
        Long pageSize = 1L;
        Long totalPage = totalRows % pageSize == 0 ? totalRows / pageSize : (totalRows / pageSize) + 1;

        log.info("NewSalaryDataTask:租户数量" + totalRows + "总共" + totalPage + "页");
        //上个月工资
        log.info("NewSalaryDataTask:上个月工资" + lastMonthBeginDate + "-" + lastMonthEndDate);

        for (int i = 1; i <= totalPage; i++) {
            List<Long> list = sysTenantDefService.getTenantId(i, pageSize.intValue() * (i - 1));
            log.info("NewSalaryDataTask:第" + i + "页" + list.size() + "条");
            if (list != null && list.size() > 0) {
                dealTenantSalary(list,lastMonthBeginDate,lastMonthEndDate,accessToken);
            }
        }

        //本月工资
        String thisMonthBeginDate = TimeUtil.getMonthFirstDay(now) + " 00:00:00";
        String thisMonthEndDate = TimeUtil.getMonthLastDay(now) + " 23:59:59";
        log.info("NewSalaryDataTask:本月工资" + thisMonthBeginDate + "-" + thisMonthEndDate);
        for (int i = 1; i <= totalPage; i++) {
            List<Long> list = sysTenantDefService.getTenantId(i, pageSize.intValue() * (i - 1));
            if (list != null && list.size() > 0) {
                dealTenantSalary(list, thisMonthBeginDate, thisMonthEndDate, accessToken);
            }
        }
        log.info("NewSalaryDataTask:跑工资进程（新）结束。。。。。。");
    }

    public void dealTenantSalary(List<Long> tenantIds, String beginDate, String endDate, String accessToken) {
        if (tenantIds != null && tenantIds.size() > 0) {
            for (Long tenantId : tenantIds) {
                if (tenantIds == null) {
                    continue;
                }
                this.createCmSalaryInfo(beginDate, endDate, tenantId, accessToken);
            }
        }
    }

    public void createCmSalaryInfo(String beginDate, String endDate, Long tenantId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();

        if (StringUtils.isEmpty(beginDate)) {
            throw new BusinessException("请输入工资结算月份开始时间");
        }
        if (StringUtils.isEmpty(endDate)) {
            throw new BusinessException("请输入工资结算月份结束时间");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入工租户id");
        }
        Date date = null;
        try {
            date = DateUtil.formatStringToDate(beginDate, "yyyy-MM-dd HH:mm:ss");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("数据转换异常");
        }
        String settleMonth = DateUtil.formatDate(date, DateUtil.YEAR_MONTH_FORMAT);
        String thisMonth = DateUtil.formatDate(date, DateUtil.YEAR_MONTH_FORMAT);
        // 结算工资月份的上一个月份
        String lastMonth = DateUtil.formatDate(DateUtil.addMonth(date, -1), DateUtil.YEAR_MONTH_FORMAT);
        List<UserSalaryDto> userList = tenantUserSalaryRelService.doQueryOwnDriverAll(tenantId);
        log.info("NewSalaryDataTask:" + tenantId + "租户自有车司机" + userList.size());

        if (userList != null && userList.size() > 0) {
            for (UserSalaryDto userDataInfo : userList) {
                Long userId = userDataInfo.getTenantUserSalaryRel().getUserId();

                LambdaQueryWrapper<CmSalaryInfoNew> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(CmSalaryInfoNew::getSettleMonth, settleMonth);
                queryWrapper.eq(CmSalaryInfoNew::getCarDriverId, userId);
                queryWrapper.eq(CmSalaryInfoNew::getTenantId, tenantId);
                queryWrapper.eq(CmSalaryInfoNew::getUserType, com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);

                try {

                    CmSalaryInfoNew cmSalaryInfoNew = baseMapper.selectOne(queryWrapper);
                    if (cmSalaryInfoNew == null) {
                        cmSalaryInfoNew = new CmSalaryInfoNew();
                        cmSalaryInfoNew.setTripPayFee(0L);// 趟数工资
                        cmSalaryInfoNew.setSaveOilBonus(0L);// 节油奖
                        cmSalaryInfoNew.setSettleMonth(settleMonth);// 结算月份
                        cmSalaryInfoNew.setCarDriverId(userId);// 司机ID
                        cmSalaryInfoNew.setUserType(com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);// 司机ID
                        cmSalaryInfoNew.setCarDriverName(userDataInfo.getUserDataInfo().getLinkman());// 司机姓名
                        cmSalaryInfoNew.setCarDriverPhone(userDataInfo.getUserDataInfo().getMobilePhone());// 司机手机号码
                        cmSalaryInfoNew.setBasicSalaryFee(userDataInfo.getTenantUserSalaryRel().getSalary());// 基本工资
                        cmSalaryInfoNew.setChannelType("prosessTask");//渠道类型
                        cmSalaryInfoNew.setCreateDate(LocalDateTime.now());//创建时间
                        cmSalaryInfoNew.setState(EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_0);
                        cmSalaryInfoNew.setOpId(userInfoId);
                        cmSalaryInfoNew.setTenantId(tenantId);//租户ID

                    } else {
                        if (cmSalaryInfoNew.getState() == EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_4 || cmSalaryInfoNew.getState() == EnumConsts.SAL_SALARY_STATUS.SAL_SALARY_STATUS_5) {
                            continue;
                        }
                    }

                    Long answerSubsidyFee = 0L;// 已发补贴
                    List<OrderDriverSubsidy> orderDriverSubsidys = orderDriverSubsidyService.findDriverSubsidys(
                            DateUtil.asLocalDateTime(DateUtil.parseDate(beginDate, "yyyy-MM-dd HH:mm:ss")),
                            DateUtil.asLocalDateTime(DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss")),
                            -1L, userId, tenantId, false,
                            -1L);

                    for (OrderDriverSubsidy os : orderDriverSubsidys) {
                        if (os.getIsPayed() == com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            answerSubsidyFee += os.getSubsidy();
                        }
                    }

                    List<OrderDriverSubsidy> orderDriverSubsidyHs = orderDriverSubsidyService.findDriverSubsidys(
                            DateUtil.asLocalDateTime(DateUtil.parseDate(beginDate, "yyyy-MM-dd HH:mm:ss")),
                            DateUtil.asLocalDateTime(DateUtil.parseDate(endDate, "yyyy-MM-dd HH:mm:ss")), -1L, userId, tenantId, true,
                            -1L);

                    for (OrderDriverSubsidy os : orderDriverSubsidyHs) {
                        if (os.getIsPayed() == com.youming.youche.order.commons.OrderConsts.AMOUNT_FLAG.ALREADY_PAY) {
                            answerSubsidyFee += os.getSubsidy();
                        }
                    }

                    cmSalaryInfoNew.setAnswerSubsidyFee(answerSubsidyFee);
                    List<Integer> loanTypeOne = new ArrayList<Integer>();
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT3);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT4);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT6);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT8);
                    loanTypeOne.add(OaLoanConsts.LOAN_SUBJECT.LOANSUBJECT11);

                    OrderListInVo orderListIn = new OrderListInVo();
                    orderListIn.setTenantId(cmSalaryInfoNew.getTenantId());

                    Date now = new Date();
                    String settleMonthNow = DateUtil.formatDate(now, DateUtil.YEAR_MONTH_FORMAT);
                    Date monthStart = null;
                    Date monthEnd = null;
                    try {
                        // 如果当前时间为本月
                        if (settleMonthNow.equals(thisMonth)) {
                            // 开始时间为本月初
                            monthStart = DateUtil.formatStringToDate(thisMonth, DateUtil.YEAR_MONTH_FORMAT);
                            // 结束时间为当天前一天
                            monthEnd = DateUtil.formatStringToDate(DateUtil.formatDateByFormat(DateUtil.diffDate(now, 1), DateUtil.DATE_FULLTIME_FORMAT), DateUtil.DATETIME_FORMAT);
                        } else {
                            monthStart = DateUtil.formatStringToDate(thisMonth, DateUtil.YEAR_MONTH_FORMAT);
                            monthEnd = DateUtil.formatStringToDate(CommonUtil.getLastMonthDate(monthStart) + " 23:59:59", DateUtil.DATETIME_FORMAT);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                        throw new BusinessException("数据转换异常");
                    }

                    List<OrderListOutDto> revocationLists = orderInfoMapper.queryDriverMonthOrderInfoList(orderListIn, monthStart, monthEnd, userId);
                    if (revocationLists != null && revocationLists.size() > 0) {
                        for (OrderListOutDto map : revocationLists) {
                            Long orderId = map.getOrderId();
                            OrderInfoDto orderLineMap = orderSchedulerService.queryOrderLineString(orderId);
                            map.setOrderLine(orderLineMap.getOrderLine());
                            List<OrderDriverSwitchInfo> switchInfos = orderDriverSwitchInfoService.getSwitchInfosByOrderId(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE1);
                            if (switchInfos != null && switchInfos.size() > 0) {
                                boolean isReplace = false;
                                Long newMieageNumber = 0L;
                                for (int i = 0; i < switchInfos.size(); i++) {
                                    OrderDriverSwitchInfo orderDriverSwitchInfo = switchInfos.get(i);
                                    if (orderDriverSwitchInfo.getReceiveUserId() != null && orderDriverSwitchInfo.getReceiveUserId().longValue() == userId) {
                                        if (i + 1 < switchInfos.size()) {
                                            OrderDriverSwitchInfo nextOrderDriverSwitchInfo = switchInfos.get(i + 1);
                                            newMieageNumber += nextOrderDriverSwitchInfo.getFormerMileage() - orderDriverSwitchInfo.getReceiveMileage();
                                            if (isReplace) {
                                                isReplace = true;
                                            }
                                        }
                                    }
                                }
                                if (isReplace) {
                                    map.setMieageNumber(newMieageNumber);
                                }
                            }
                        }


                        cmSalaryInfoNew.setOrderCount(revocationLists == null ? 0 : revocationLists.size());//订单数
                        Long mothMieage = 0L;//月里程
                        Long advinceFee = 0L;// 未核销借支
                        for (OrderListOutDto revocation : revocationLists) {
                            List<com.youming.youche.finance.domain.OaLoan> oaLoanList = oaLoanMapper.queryOaLoan(userId, tenantId, loanTypeOne,
                                    userDataInfo.getUserDataInfo().getMobilePhone(), revocation.getOrderId());
                            if (oaLoanList != null && oaLoanList.size() > 0) {
                                for (com.youming.youche.finance.domain.OaLoan ob : oaLoanList) {
                                    advinceFee += (ob.getAmount()
                                            - (ob.getPayedAmount() == null ? 0 : ob.getPayedAmount()));
                                }
                            }
                            mothMieage += (revocation.getMieageNumber() == null ? 0L : revocation.getMieageNumber());
                            cmSalaryInfoNew.setMonthMileage(mothMieage);//月里程
                            cmSalaryInfoNew.setUnwrittenLoanFee(advinceFee);// 未核销借支

                            LambdaQueryWrapper<CmSalaryInfoNew> wrapper = new LambdaQueryWrapper<>();
                            wrapper.eq(CmSalaryInfoNew::getSettleMonth, lastMonth);
                            wrapper.eq(CmSalaryInfoNew::getCarDriverId, userId);
                            wrapper.eq(CmSalaryInfoNew::getTenantId, tenantId);
                            wrapper.eq(CmSalaryInfoNew::getUserType, com.youming.youche.conts.SysStaticDataEnum.USER_TYPE.DRIVER_USER);

                            CmSalaryInfoNew lastMonthcmSalaryInfo = baseMapper.selectOne(wrapper);
                            if (lastMonthcmSalaryInfo != null && lastMonthcmSalaryInfo.getRealSalaryFee() != null
                                    && lastMonthcmSalaryInfo.getRealSalaryFee() < 0) {
                                cmSalaryInfoNew.setLastMonthDebt(lastMonthcmSalaryInfo.getRealSalaryFee()
                                        - (lastMonthcmSalaryInfo.getPaidSalaryFee() != null ? lastMonthcmSalaryInfo.getPaidSalaryFee() : 0L));// 上月欠款
                            }

                            cmSalaryInfoNew.setUpdateOpId(userInfoId);//修改操作员ID
                            cmSalaryInfoNew.setUpdateDate(LocalDateTime.now());//更新时间
                            this.saveOrUpdate(cmSalaryInfoNew);
                        }
                    } else {
                        this.saveOrUpdate(cmSalaryInfoNew);
                    }

                } catch (Exception e) {
                    log.info("工资基础数据初始失败" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        log.info("------生成工资单基础数据结束------");
    }
}
