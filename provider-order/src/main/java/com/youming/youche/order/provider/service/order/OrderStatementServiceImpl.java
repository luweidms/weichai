package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IBillInfoService;
import com.youming.youche.order.api.order.ICmCustomerInfoService;
import com.youming.youche.order.api.order.IOilRechargeAccountService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderReportService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.domain.order.BillInfoReceiveRel;
import com.youming.youche.order.domain.order.CmCustomerInfo;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.dto.OilRechargeAccountDto;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.order.IntelligentMatchCustomerDto;
import com.youming.youche.order.dto.order.OilBalanceForOilAccountDto;
import com.youming.youche.order.dto.order.QueryOrderTitleBasicsInfoDto;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.dto.cm.CmCustomerLineDto;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 订单报表类
 * 
 * @author EditTheLife
 */
@DubboService(version = "1.0.0")
@Service
public class OrderStatementServiceImpl implements IOrderStatementService {

	@Resource
	private LoginUtils loginUtils;

	@Resource
	IBillInfoService iBillInfoService;

	@DubboReference(version = "1.0.0")
	ISysTenantDefService sysTenantDefService;

	@Resource
	IOilRechargeAccountService oilRechargeAccountService;

	@DubboReference(version = "1.0.0")
	ICmCustomerLineService cmCustomerLineService;

	@Resource
	ICmCustomerInfoService cmCustomerInfoService;

	@Resource
	private RedisUtil redisUtil;

	@Resource
	IOrderReportService iOrderReportService;

	@Resource
	IOrderInfoService orderInfoService;

	@Resource
	IOrderSchedulerService orderSchedulerService;

	@Resource
	IOrderSchedulerHService orderSchedulerHService;

	@Resource
	ReadisUtil readisUtil;

	/**
	 * 获取各油来源账户中可使用的油
	 * 油账户减去未支付的油
	 * @return
	 * @throws Exception
	 */
	public OilBalanceForOilAccountDto querOilBalanceForOilAccountType(String accessToken) {
		LoginInfo user = loginUtils.get(accessToken);
		if(user == null || user.getTenantId() == null){
			throw new BusinessException("用户未登录！");
		}
		Long tenantId= user.getTenantId();
		String billLookUp = null; // 发票抬头

		// 根据车队ID获取别设为默认收票主体的开票信息、收件人信息
		BillInfoReceiveRel billInfoReceiveRel=iBillInfoService.getDefaultBillInfoByTenantId(tenantId);
		if(billInfoReceiveRel !=null && billInfoReceiveRel.getBillInfo() !=null
				&&StringUtils.isNotBlank(billInfoReceiveRel.getBillInfo().getBillLookUp())) {
			billLookUp = billInfoReceiveRel.getBillInfo().getBillLookUp();
		}
		//各油来源账户余额
		Long userId = null;
		 SysTenantDef sysTenantDef = sysTenantDefService.getById(tenantId);
			if(sysTenantDef != null && sysTenantDef.getState() != null && sysTenantDef.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_YES){
				userId = sysTenantDef.getAdminUser();
			}
		//Map<String,Object>  rtnMap=new HashMap<String,Object>(4);
		OilBalanceForOilAccountDto oilBalanceForOilAccountDto = new OilBalanceForOilAccountDto();
		// 查询油信息
		OilRechargeAccountDto oilBalanceDto=oilRechargeAccountService.getOilRechargeAccount(userId, tenantId, SysStaticDataEnum.USER_TYPE.ADMIN_USER,billLookUp,user);
		//车队汇总
		if (oilBalanceDto != null){
			Long nonDeductOilBalance = oilBalanceDto.getNonDeductOilBalance();//非抵扣油余额
			if (nonDeductOilBalance != null){
				oilBalanceForOilAccountDto.setNonDeductOilBalance(nonDeductOilBalance < 0L ? 0L : nonDeductOilBalance);
			}
			//收款账户
			Long billLookUpBalance= oilBalanceDto.getBillLookUpBalance();;//抵扣油余额
			if (billLookUpBalance != null){
				oilBalanceForOilAccountDto.setBillLookUpBalance(billLookUpBalance < 0L ? 0L : billLookUpBalance);
			}
			//不开票金额
			Long custOilBalance = oilBalanceDto.getCustOilBalance();//已开票油余额=客户油+转移油+返利油
			if (custOilBalance != null){
				custOilBalance = custOilBalance<0?0:custOilBalance;
			}
			Long rebateOilBalance = 0L;
			if (oilBalanceDto.getRebateOilBalance() != null){
				rebateOilBalance = oilBalanceDto.getRebateOilBalance() < 0L ? 0L : oilBalanceDto.getRebateOilBalance();
			}
			oilBalanceForOilAccountDto.setRebateOilBalance(rebateOilBalance);
			Long transferOilBalance = 0L;
			if (oilBalanceDto.getTransferOilBalance() != null){
				transferOilBalance =oilBalanceDto.getTransferOilBalance() < 0L ? 0L : oilBalanceDto.getTransferOilBalance();
			}
			oilBalanceForOilAccountDto.setTransferOilBalance(transferOilBalance);
			oilBalanceForOilAccountDto.setCustOilBalance(custOilBalance);
		}
		return oilBalanceForOilAccountDto;
	}

	@Override
	public List<IntelligentMatchCustomerDto> queryIntelligentMatchCustomer(boolean isCustomer, String accessToken) throws Exception {
		LoginInfo user = loginUtils.get(accessToken);
		if(user == null || user.getTenantId() == null){
			throw new BusinessException("用户未登录！");
		}
		Long tenantId = user.getTenantId();
		Long userId = user.getId();
		String listStr = null;
		if (isCustomer) {
			listStr = (String) redisUtil.get(EnumConsts.RemoteCache.INTELLIGENT_MATCH_CUSTOMER+tenantId+"_"+userId);
		}else{
			listStr = (String) redisUtil.get(EnumConsts.RemoteCache.INTELLIGENT_MATCH_CUSTOMER_LINE+tenantId+"_"+userId);
		}
		if (StringUtils.isNotBlank(listStr)) {

			List<Map> list = JSON.parseArray(listStr, Map.class);
			FastDFSHelper client = FastDFSHelper.getInstance();
			if (!isCustomer) {
				List<IntelligentMatchCustomerDto> listOut = new ArrayList<>();
				for (Map map : list) {
					Long lineId = DataFormat.getLongKey(map, "lineId");
					try {
						CmCustomerLine line = cmCustomerLineService.getById(lineId);
						if (line != null) {
							if (line.getState() != null && line.getState().intValue() == SysStaticDataEnum.CUCUSTOMER_LINE_STATE.INVALID) {
								continue;
							}
							CmCustomerLineDto out = new CmCustomerLineDto();
							BeanUtil.copyProperties(out,line);
							if(out.getContractId() != null && out.getContractId() > 0){
								out.setContractUrl(client.getHttpURL(out.getContractUrl()));
							}
							//增加返回经停点信息
							out.setSubWayList(cmCustomerLineService.getCustomerLineSubwayList(line.getId()));
							Map outMap = JsonHelper.parseJSON2Map(JsonHelper.toJson(out));
							CmCustomerInfo customerInfo = cmCustomerInfoService.getById(line.getCustomerId());
							String companyName ="";
							if (customerInfo != null) {
								companyName = customerInfo.getCompanyName();
							}
							outMap.put("customerId", line.getCustomerId());
							outMap.put("companyName", companyName);
							IntelligentMatchCustomerDto out1 = new IntelligentMatchCustomerDto();
							BeanUtil.copyProperties(out1,outMap);
							listOut.add(out1);
						}
					} catch (Exception e) {
						continue;
					}
				}
				return listOut;
			}else{
				List<IntelligentMatchCustomerDto> listOutOne = new ArrayList<>();
				for (Map map : list) {
					IntelligentMatchCustomerDto out2 = new IntelligentMatchCustomerDto();
					BeanUtil.copyProperties(out2,map);
					listOutOne.add(out2);
				}
				return listOutOne;
			}
		}
		return null;
	}

	@Override
	public Map<String, Long> queryOrderOilEnRouteUse(Long userId, List<Long> oilIds) {
		Map<String, Long> map = new ConcurrentHashMap<String, Long>();
		if (userId == null || userId <= 0) {
			throw new BusinessException("用户ID为空，请联系客服！");
		}
		if (oilIds != null && oilIds.size() > 0) {
			for (Long oilId : oilIds) {
				List<Long> list = iOrderReportService.queryOrderOilEnRouteUse(userId, oilId);
				if (list != null && list.size() > 0) {
					Long orderId = Long.parseLong(String.valueOf(list.get(0)));
					map.put(oilId + "", orderId);
				}
			}
		}
		return map;
	}

	@Override
	public QueryOrderTitleBasicsInfoDto queryOrderTitleBasicsInfo(Long orderId) {
		QueryOrderTitleBasicsInfoDto dto = new QueryOrderTitleBasicsInfoDto();

		OrderInfo orderInfo = orderInfoService.getOrder(orderId);
		if (orderInfo == null) {
			OrderInfoH orderInfoH = orderInfoService.getOrderH(orderId);
			if (orderInfoH == null) {
				throw new BusinessException("未找到订单["+orderId+"]信息");
			}
			OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
			dto.setOrderId(orderId);
			OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(orderId);
			dto.setOrderLine(orderInfoDto.getOrderLine());
			dto.setOrderState(orderInfoH.getOrderState());
			if (orderInfoH.getOrderState() != null) {
				dto.setOrderStateName(readisUtil.getSysStaticData("ORDER_STATE", String.valueOf(orderInfoH.getOrderState())).getCodeName());
			}
			dto.setTenantId(orderInfoH.getTenantId());
			dto.setTenantName(orderInfoH.getTenantName());
			dto.setDependTime(orderSchedulerH.getDependTime());
		} else {
			OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
			dto.setOrderId(orderId);
			OrderInfoDto orderInfoDto = orderSchedulerService.queryOrderLineString(orderId);
			dto.setOrderLine(orderInfoDto.getOrderLine());
			dto.setOrderState(orderInfo.getOrderState());
			if (orderInfo.getOrderState() != null) {
				dto.setOrderStateName(readisUtil.getSysStaticData("ORDER_STATE", String.valueOf(orderInfo.getOrderState())).getCodeName());
			}
			dto.setTenantId(orderInfo.getTenantId());
			dto.setTenantName(orderInfo.getTenantName());
			dto.setDependTime(orderScheduler.getDependTime());
		}

		return dto;
	}
}
