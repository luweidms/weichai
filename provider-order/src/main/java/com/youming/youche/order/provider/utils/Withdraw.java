package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderFundFlowService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.domain.BusiSubjectsDtlOperate;
import com.youming.youche.order.domain.order.BusiSubjectsDtl;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFundFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.provider.mapper.BusiSubjectsDtlOperateMapper;
import com.youming.youche.order.provider.mapper.order.OrderLimitMapper;
import com.youming.youche.order.provider.mapper.order.SubjectsInfoMapper;
import com.youming.youche.order.vo.OperDataParam;
import com.youming.youche.order.vo.QueryOrderLimitByCndVo;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author zengwen
 * @date 2022/6/13 17:12
 */
@Component
public class Withdraw {

    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @Resource
    SubjectsInfoMapper subjectsInfoMapper;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderFundFlowService orderFundFlowService;

    @Resource
    BusiSubjectsDtlOperateMapper busiSubjectsDtlOperateMapper;

    @Resource
    OrderLimitMapper orderLimitMapper;

    @Resource
    IOrderLimitService orderLimitService;


    public List dealToOrder(ParametersNewDto inParam,
                            List<BusiSubjectsRel> rels,
                            LoginInfo user) {
//		Session session = SysContexts.getEntityManager();
//		long businessId = DataFormat.getLongKey(inParam, "businessId");
//		long accountDatailsId = DataFormat.getLongKey(inParam,"accountDatailsId");
//		long flowId = DataFormat.getLongKey(inParam, "flowId");
//		String vehicleAffiliation = DataFormat.getStringKey(inParam, "vehicleAffiliation");
//		// 资金流向批次
//		inParam.put("batchId", CommonUtil.createSoNbr() + "");
//		try {
//			IUserDataInfo usSv = (IUserDataInfo)SysContexts.getBean("userBusSV");
//			PayoutIntf pay = (PayoutIntf) session.get(PayoutIntf.class, flowId);
//			log.info("payId=" + pay.getFlowId());
//			if (pay.getTxnType().equals("003") || pay.getTxnType().equals("008")) {
//				if (pay.getVerificationState() != 2) {
//					return null;
//				}
//			}
//			log.info("提现抽取");
//			// 通过手机获取用户信息
//			long objId = pay.getObjId();
//			long userId = pay.getUserId();
//			if (pay.getOutUserId() > 0L) {
//				if (pay.getOutBillId() != null) {
//					objId = Long.parseLong(pay.getOutBillId());
//				}
//				userId = pay.getOutUserId();
//			}
//			inParam.put("userId", userId+"");
//			UserDataInfo user = usSv.getUserByUserId(userId);
//			boolean isOilService = false;
//			// 服务商
//			if (user.getUserType() != null && user.getUserType().intValue() == 2) {
//				// 油卡服务商、维修商
//				if (user.getBusinessType() != null && (user.getBusinessType().intValue() == 1 || user.getBusinessType().intValue() == 3)) {
//					isOilService = true;
//				}
//			}
//			log.info("油卡服务商标识" + isOilService);
//			if("0".equals(vehicleAffiliation)){
//				//充值提现
//				if(isOilService==true){
//					//油老板提现
//					long amount = pay.getTxnAmt();
//			    	BusiSubjectsRel rel = rels.get(0);
//					//充值提现油老板
//					List<OrderFundFlow> cosumeOilOrders = this.getOrderFundFlowCash(null,userId,null, vehicleAffiliation, new Long[]{21000012L}, new Long[]{1306L},inParam);
//					for(OrderFundFlow off:cosumeOilOrders){
//		    			//提现金额小于等于此笔成本油费
//		    			if(off.getNoWithdrawOil() >= amount){
//							//资金流水操作
//							OrderFundFlow offnew = new OrderFundFlow();
//					    	offnew.setAmount(amount);//交易金额（单位分）
//					    	offnew.setBatchAmount(pay.getTxnAmt());
//					    	offnew.setBusinessId(rel.getBusinessId());//业务类型
//					    	offnew.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(rel.getBusinessId())).getCodeName());//业务名称
//					    	offnew.setBookType(EnumConsts.PayInter.PAY_CASH);//账户类型
//					    	offnew.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.PAY_CASH)).getCodeName());//账户类型
//					    	offnew.setSubjectsId(rel.getSubjectsId());//科目ID
//					    	offnew.setSubjectsName(SubjectInfoCacheDataUtil.getSubjectName(rel.getSubjectsId()));//科目名称
//					    	offnew.setBusiTable("payout_intf");//业务对象表
//					    	offnew.setBusiKey(pay.getFlowId());//业务流水ID
//					    	offnew.setBackIncome(0L);
//					    	offnew.setIncome(0L);
//					    	offnew.setInoutSts("out");//收支状态:收in支out转io
//			    	    	offnew.setToOrderId(off.getFlowId());
//			    	    	offnew.setPayState(1);//真正提现成功
//					    	this.createOrderFundFlow(inParam,offnew);
//							session.saveOrUpdate(offnew);
//				    		off.setNoWithdrawOil(off.getNoWithdrawOil() - amount);
//				    		session.saveOrUpdate(off);
//							break;
//		    			}
//		    			else
//		    			{
//		    				amount = amount - off.getNoWithdrawOil();
//		    				//资金流水操作
//							OrderFundFlow offnew = new OrderFundFlow();
//					    	offnew.setAmount(off.getNoWithdrawOil());//交易金额（单位分）
//					    	offnew.setBatchAmount(pay.getTxnAmt());
//					    	offnew.setOrderId(off.getOrderId());//订单ID
//					    	offnew.setBusinessId(businessId);//业务类型
//					    	offnew.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//					    	offnew.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
//					    	offnew.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
//					    	offnew.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
//					    	offnew.setSubjectsName(SubjectInfoCacheDataUtil.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_SUB));//科目名称
//					    	offnew.setBusiTable("payout_intf");//业务对象表
//					    	offnew.setBusiKey(pay.getFlowId());//业务流水ID
//					    	offnew.setBackIncome(0L);
//					    	offnew.setIncome(0L);
//			    	    	offnew.setInoutSts("out");//收支状态:收in支out转io
//			    	    	offnew.setToOrderId(off.getFlowId());
//			    	    	offnew.setPayState(1);//真正提现成功
//					    	this.createOrderFundFlow(inParam,offnew);
//				    		session.saveOrUpdate(offnew);
//				    		off.setNoWithdrawOil(0L);;//提现金额大于本单油费
//				    		session.saveOrUpdate(off);
//		    			}
//					}
//					return null;
//				}
//				else
//				{
//					//充值提现非油老板
//			    	BusiSubjectsRel rel = rels.get(0);
//					//资金流水操作
//					OrderFundFlow offnew = new OrderFundFlow();
//			    	offnew.setAmount(rel.getAmountFee());//交易金额（单位分）
//			    	offnew.setBusinessId(rel.getBusinessId());//业务类型
//			    	offnew.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(rel.getBusinessId())).getCodeName());//业务名称
//			    	offnew.setBookType(EnumConsts.PayInter.PAY_CASH);//账户类型
//			    	offnew.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.PAY_CASH)).getCodeName());//账户类型
//			    	offnew.setSubjectsId(rel.getSubjectsId());//科目ID
//			    	offnew.setSubjectsName(SubjectInfoCacheDataUtil.getSubjectName(rel.getSubjectsId()));//科目名称
//			    	offnew.setBusiTable("payout_intf");//业务对象表
//			    	offnew.setBusiKey(pay.getFlowId());//业务流水ID
//			    	offnew.setBackIncome(0L);
//			    	offnew.setIncome(0L);
//			    	offnew.setInoutSts("out");//收支状态:收in支out转io
//			    	this.createOrderFundFlow(inParam,offnew);
//					session.saveOrUpdate(offnew);
//					return null;
//				}
//			}
//			// 司机提现
//			if (isOilService == false) {
//				//真正提现成功
//				if (EnumConsts.CAPITAL_CHANNEL.CAPITAL_CHANNEL4.equals(vehicleAffiliation) && "success".equals(DataFormat.getStringKey(inParam, "withdrawals"))) {
//					this.payCashForDriver(session, inParam, userId, vehicleAffiliation, pay.getTxnAmt(), businessId, accountDatailsId,pay);
//				} else {
//					// 判断司机是否是经纪人
//					inParam.put("faceBalanceUnused","1");
//					if(user.getCarUserType() == null) throw new BusinessException("司机的车辆类型不正确!");
//					inParam.put("carUserType", String.valueOf(user.getCarUserType()));
//					List<OrderLimit> agentOrders = this.getAgentOrder(inParam);
//					// 司机提现
//					if (agentOrders == null || agentOrders.size() <= 0) {
//						inParam.put("sign", "1");//1司机、2经济人
//						this.payCashForDriver(session, inParam, userId, vehicleAffiliation, pay.getTxnAmt(), businessId, accountDatailsId,pay);
//					} else {
//						//经纪人提现
//						long faceBalanceUnusedSum =0L;
//						for(OrderLimit ol : agentOrders){
//							faceBalanceUnusedSum += ol.getFaceBalanceUnused();
//						}
//						if(faceBalanceUnusedSum >= pay.getTxnAmt()){
//							inParam.put("sign", "2");//1司机、2经济人
//							this.payCashForDriver(session, inParam, userId, vehicleAffiliation, pay.getTxnAmt(), businessId, accountDatailsId,pay);
//						}else{//既是经济人又是司机提现
//							if(faceBalanceUnusedSum > 0L){
//								inParam.put("sign", "2");
//								this.payCashForDriver(session, inParam, userId, vehicleAffiliation, faceBalanceUnusedSum, businessId, accountDatailsId,pay);
//							}
//							inParam.put("sign", "1");//1司机、2经济人
//							this.payCashForDriver(session, inParam, userId, vehicleAffiliation, pay.getTxnAmt() - faceBalanceUnusedSum, businessId, accountDatailsId,pay);
//						}
//
//					}
//				}
//			} else {
//				//油老板提现 (20180202增加维修商提现)
//				long amount = pay.getTxnAmt();
//				List<OrderFundFlow> cosumeOilOrders = this.getOrderFundFlow(null,userId,null, vehicleAffiliation, new Long[]{21000012L, 22000013L}, new Long[]{1007L,1008L,1505L},inParam);//1306自由资金不用反写订单限制表
//				//根据油费消费流水分摊提现金额到订单
//				for(OrderFundFlow off:cosumeOilOrders){
//					long tempBusinessId = off.getBusinessId();
//	    			//提现金额小于等于此笔成本油费
//					Criteria ca = session.createCriteria(OrderLimit.class);
//					ca.add(Restrictions.eq("orderId", off.getOrderId()));
//					ca.add(Restrictions.eq("userId", off.getUserId()));
//					OrderLimit orderTemp = (OrderLimit) ca.uniqueResult();
//	    			if(off.getNoWithdrawOil() >= amount){
//				    	//订单限制表操作
//				    	List<OperDataParam> odps = new ArrayList();
//				    	if (tempBusinessId == EnumConsts.PayInter.PAY_FOR_OIL_CODE) {
//				    		/*odps.add(new OperDataParam("WITHDRAW_OIL",String.valueOf(amount),"+"));
//			    			odps.add(new OperDataParam("NO_WITHDRAW_OIL",String.valueOf(amount),"-"));*/
//				    		orderTemp.setWithdrawOil((orderTemp.getWithdrawOil()== null ? 0L : orderTemp.getWithdrawOil()) + amount);
//				    		orderTemp.setNoWithdrawOil((orderTemp.getNoWithdrawOil()== null ? 0L : orderTemp.getNoWithdrawOil()) - amount);
//				    	}
//				    	if (tempBusinessId == EnumConsts.PayInter.PAY_FOR_REPAIR) {
//				    		/*odps.add(new OperDataParam("WITHDRAW_REPAIR",String.valueOf(amount),"+"));
//			    			odps.add(new OperDataParam("NO_WITHDRAW_REPAIR",String.valueOf(amount),"-"));*/
//				    		orderTemp.setWithdrawRepair((orderTemp.getWithdrawRepair() == null ? 0L : orderTemp.getWithdrawRepair()) + amount);
//				    		orderTemp.setNoWithdrawRepair((orderTemp.getNoWithdrawRepair() == null ? 0L : orderTemp.getNoWithdrawRepair()) - amount);
//				    	}
//						//this.updateOrderLimit(off.getOrderId(),off.getUserId() , odps);
//				    	session.saveOrUpdate(orderTemp);
//						//资金流水操作
//						OrderFundFlow offnew = new OrderFundFlow();
//				    	offnew.setAmount(amount);//交易金额（单位分）
//				    	offnew.setOrderId(off.getOrderId());//订单ID
//				    	offnew.setBusinessId(businessId);//业务类型
//				    	offnew.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//				    	offnew.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
//				    	offnew.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
//				    	offnew.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
//				    	offnew.setSubjectsName(SubjectInfoCacheDataUtil.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_SUB));//科目名称
//				    	offnew.setBusiTable("payout_intf");//业务对象表
//				    	offnew.setBusiKey(pay.getFlowId());//业务流水ID
//				    	offnew.setBackIncome(0L);
//				    	offnew.setIncome(0L);
//		    	    	offnew.setInoutSts("out");//收支状态:收in支out转io
//		    	    	offnew.setToOrderId(off.getFlowId());
//		    	    	offnew.setPayState(1);//真正提现成功
//				    	this.createOrderFundFlow(inParam,offnew);
//			    		session.saveOrUpdate(offnew);
//			    		off.setNoWithdrawOil(off.getNoWithdrawOil() - amount);
//			    		session.saveOrUpdate(off);
//						break;
//	    			}
//	    			else
//	    			{
//	    				//提现金额大于本单成本油费
//				    	List<OperDataParam> odps = new ArrayList();
//				    	if (tempBusinessId == EnumConsts.PayInter.PAY_FOR_OIL_CODE) {
//				    		/*odps.add(new OperDataParam("WITHDRAW_OIL",String.valueOf(amount),"+"));
//			    			odps.add(new OperDataParam("NO_WITHDRAW_OIL",String.valueOf(amount),"-"));*/
//				    		orderTemp.setWithdrawOil((orderTemp.getWithdrawOil()== null ? 0L : orderTemp.getWithdrawOil()) + off.getNoWithdrawOil());
//				    		orderTemp.setNoWithdrawOil((orderTemp.getNoWithdrawOil()== null ? 0L : orderTemp.getNoWithdrawOil()) - off.getNoWithdrawOil());
//				    	}
//				    	if (tempBusinessId == EnumConsts.PayInter.PAY_FOR_REPAIR) {
//				    		/*odps.add(new OperDataParam("WITHDRAW_REPAIR",String.valueOf(amount),"+"));
//			    			odps.add(new OperDataParam("NO_WITHDRAW_REPAIR",String.valueOf(amount),"-"));*/
//				    		orderTemp.setWithdrawRepair((orderTemp.getWithdrawRepair() == null ? 0L : orderTemp.getWithdrawRepair()) + off.getNoWithdrawOil());
//				    		orderTemp.setNoWithdrawRepair((orderTemp.getNoWithdrawRepair() == null ? 0L : orderTemp.getNoWithdrawRepair()) - off.getNoWithdrawOil());
//				    	}
//						//this.updateOrderLimit(off.getOrderId(),off.getUserId() , odps);
//				    	session.saveOrUpdate(orderTemp);
//	    				amount = amount - off.getNoWithdrawOil();
//	    				//资金流水操作
//						OrderFundFlow offnew = new OrderFundFlow();
//				    	offnew.setAmount(off.getNoWithdrawOil());//交易金额（单位分）
//				    	offnew.setOrderId(off.getOrderId());//订单ID
//				    	offnew.setBusinessId(businessId);//业务类型
//				    	offnew.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//				    	offnew.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
//				    	offnew.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
//				    	offnew.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
//				    	offnew.setSubjectsName(SubjectInfoCacheDataUtil.getSubjectName(EnumConsts.SubjectIds.WITHDRAWALS_SUB));//科目名称
//				    	offnew.setBusiTable("payout_intf");//业务对象表
//				    	offnew.setBusiKey(pay.getFlowId());//业务流水ID
//				    	offnew.setBackIncome(0L);
//				    	offnew.setIncome(0L);
//		    	    	offnew.setInoutSts("out");//收支状态:收in支out转io
//		    	    	offnew.setToOrderId(off.getFlowId());
//		    	    	offnew.setPayState(1);//真正提现成功
//				    	this.createOrderFundFlow(inParam,offnew);
//			    		session.saveOrUpdate(offnew);
//			    		off.setNoWithdrawOil(0L);;//提现金额大于本单油费
//			    		session.saveOrUpdate(off);
//	    			}
//				}
//			}
//		} catch (Exception ex) {
//			log.info("ExtractReportOriginPaidTaskExceptionTixian");
//			ex.printStackTrace();
//		} finally {
//
//		}
        /** 提现抽取结束 **/
        return null;
    }
    //司机提现
    public void payCashForDriver(ParametersNewDto inParam, Long userId, String vehicleAffiliation, Long txnAmt, Long businessId, Long accountDatailsId, PayoutIntf pay, LoginInfo user) throws Exception{
        QueryOrderLimitByCndVo queryOrderLimitByCndVo = new QueryOrderLimitByCndVo();
        queryOrderLimitByCndVo.setUserId(userId);
        queryOrderLimitByCndVo.setVehicleAffiliation(vehicleAffiliation);
        int carUserType = inParam.getCarUserType();
        String sign = inParam.getSign();
        //路歌和非路歌渠道
        if(EnumConsts.CAPITAL_CHANNEL.CAPITAL_CHANNEL4.equals(vehicleAffiliation)){
            OrderFundFlow off = new OrderFundFlow();
            //真正支付成功
            if("success".equals(inParam.getWithdrawals())){
                off.setAmount(txnAmt);//交易金额（单位分）
                off.setOrderId(pay.getOrderId());//订单ID
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
                off.setSubjectsName(subjectsInfoMapper.selectById(EnumConsts.SubjectIds.WITHDRAWALS_SUB).getSubjectsName());//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(pay.getId());//业务流水ID
                off.setBackIncome(0L);
                off.setIncome(0L);
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setPayState(1);//真正提现成功
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作
                List<OperDataParam> odps = new ArrayList();
                odps.add(new OperDataParam("NO_WITHDRAW_CASH",String.valueOf(off.getAmount()),"-"));
                odps.add(new OperDataParam("WITHDRAW_CASH",String.valueOf(off.getAmount()),"+"));
                orderFundFlowService.saveOrUpdate(off);
                this.updateOrderLimit(pay.getOrderId(), userId, odps);
            }else{
                long orderId = inParam.getOrderId();
                //20171109加(开始)
                if(pay.getOrderId() != null && pay.getOrderId() > 0L){
                    orderId = pay.getOrderId();
                }

                OrderLimit olTemp = orderLimitMapper.selectOrderLimitId(orderId, userId, sign);
                //20171109加(结束)
                off.setAmount(txnAmt);//交易金额（单位分）
                if(pay.getOrderId() != null && pay.getOrderId() > 0L){
                    off.setOrderId(pay.getOrderId());//订单ID
                }else{
                    off.setOrderId(orderId);//订单ID
                }
                off.setBusinessId(businessId);//业务类型
                off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
                off.setSubjectsName(subjectsInfoMapper.selectById(EnumConsts.SubjectIds.WITHDRAWALS_SUB).getSubjectsName());//科目名称
                off.setBusiTable("payout_intf");//业务对象表
                off.setBusiKey(pay.getId());//业务流水ID
                off.setBackIncome(0L);
                off.setIncome(0L);
                off.setInoutSts("out");//收支状态:收in支out转io
                off.setPayState(0);//发起提现
                this.createOrderFundFlow(inParam,off, user);
                //订单限制表操作

                //查出限定表的固定成本金额
                List<OperDataParam> odps = new ArrayList();
                //自有车司机或者个体户司机需要扣除固定司机成本
                if(carUserType == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR ){
                    OrderLimit ol = orderLimitMapper.getOneOrderLimitByOrderId(off.getOrderId());
                    Long noPayCash = ol.getNoPayCash();
                    if(noPayCash >= off.getAmount()){
                        odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(off.getAmount()),"-"));
                        odps.add(new OperDataParam("PAID_CASH",String.valueOf(off.getAmount()),"+"));
                    }else{
                        //先扣除预付现金，再扣除司机固定成本
                        odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(noPayCash),"-"));
                        odps.add(new OperDataParam("PAID_CASH",String.valueOf(noPayCash),"+"));
                        odps.add(new OperDataParam("DRIVER_COST_NOPAY",String.valueOf(off.getAmount()-noPayCash),"-"));
                        odps.add(new OperDataParam("DRIVER_COST_PAID",String.valueOf(off.getAmount()-noPayCash),"+"));
                    }
                }else{
                    if("1".equals(sign)){//司机
                        //odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(off.getAmount()),"-"));
                        //odps.add(new OperDataParam("PAID_CASH",String.valueOf(off.getAmount()),"+"));
                        olTemp.setNoPayCash(olTemp.getNoPayCash() - off.getAmount());
                        olTemp.setPaidCash(olTemp.getPaidCash() + off.getAmount());
                    }
                }

                orderFundFlowService.saveOrUpdate(off);
                orderLimitService.saveOrUpdate(olTemp);
	    		/*if(pay.getOrderId() != null && pay.getOrderId() > 0L){
	    			this.updateOrderLimit(pay.getOrderId(), userId, odps);
				}else{
					this.updateOrderLimit(orderId, userId, odps);
				}*/
            }
        }else{
            List<OrderLimit> orderLimits = null;
            if("1".equals(sign)){//司机
                orderLimits = orderLimitMapper.queryOrderLimitByCnd(queryOrderLimitByCndVo);
                this.matchAmountToOrder(txnAmt,0L,0L, "NoPayCash", orderLimits);
            }
            if("2".equals(sign)){//经济人
                inParam.setFaceBalanceUnused("1");
                orderLimits = this.getAgentOrder(inParam);
                this.matchAmountToOrder(txnAmt,0L,0L, "FaceBalanceUnused", orderLimits);
            }
            //将提现金额分摊给订单
            for(OrderLimit olTemp : orderLimits){
                if(olTemp.getMatchAmount()!=null){
                    //资金流水操作
                    OrderFundFlow off = new OrderFundFlow();
                    off.setAmount(olTemp.getMatchAmount());//交易金额（单位分）
                    off.setOrderId(olTemp.getOrderId());//订单ID
                    off.setBusinessId(businessId);//业务类型
                    off.setBusinessName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
                    off.setBookType(EnumConsts.PayInter.ACCOUNT_CODE);//账户类型
                    off.setBookTypeName(sysStaticDataRedisUtils.getSysStaticDataByCodeValue("ACCOUNT_BOOK_TYPE", String.valueOf(EnumConsts.PayInter.ACCOUNT_CODE)).getCodeName());//账户类型
                    off.setSubjectsId(EnumConsts.SubjectIds.WITHDRAWALS_SUB);//科目ID
                    off.setSubjectsName(subjectsInfoMapper.selectById(EnumConsts.SubjectIds.WITHDRAWALS_SUB).getSubjectsName());//科目名称
                    off.setBusiTable("payout_intf");//业务对象表
                    off.setBusiKey(pay.getId());//业务流水ID
                    off.setBackIncome(0L);
                    off.setIncome(0L);
                    off.setInoutSts("out");//收支状态:收in支out转io
                    off.setPayState(1);//真正提现成功
                    this.createOrderFundFlow(inParam, off, user);

                    //订单限制表操作
                    List<OperDataParam> odps = new ArrayList();
                    //自有车司机或者个体户司机需要扣除固定司机成本
                    if(carUserType == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR ){
                        OrderLimit ol = orderLimitMapper.selectOrderLimitByOrderAndUser(off.getOrderId(), off.getUserId());
                        Long noPayCash = ol.getNoPayCash();
                        if(noPayCash >= off.getAmount()){
                            odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("PAID_CASH",String.valueOf(off.getAmount()),"+"));
                            odps.add(new OperDataParam("NO_WITHDRAW_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("WITHDRAW_CASH",String.valueOf(off.getAmount()),"+"));
                        }else{
                            //先扣除预付现金，再扣除司机固定成本
                            odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(noPayCash),"-"));
                            odps.add(new OperDataParam("PAID_CASH",String.valueOf(noPayCash),"+"));
                            odps.add(new OperDataParam("DRIVER_COST_NOPAY",String.valueOf(off.getAmount()-noPayCash),"-"));
                            odps.add(new OperDataParam("DRIVER_COST_PAID",String.valueOf(off.getAmount()-noPayCash),"+"));
                            odps.add(new OperDataParam("NO_WITHDRAW_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("WITHDRAW_CASH",String.valueOf(off.getAmount()),"+"));
                        }
                    }else{
                        if("1".equals(sign)){//司机
                            odps.add(new OperDataParam("NO_PAY_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("PAID_CASH",String.valueOf(off.getAmount()),"+"));
                            odps.add(new OperDataParam("NO_WITHDRAW_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("WITHDRAW_CASH",String.valueOf(off.getAmount()),"+"));
                        }
                        if("2".equals(sign)){//经济人
                            odps.add(new OperDataParam("WITHDRAW_CASH",String.valueOf(off.getAmount()),"+"));
                            odps.add(new OperDataParam("NO_WITHDRAW_CASH",String.valueOf(off.getAmount()),"-"));
                            odps.add(new OperDataParam("FACE_BALANCE_UNUSED",String.valueOf(off.getAmount()),"-"));
                        }
                    }
                    orderFundFlowService.saveOrUpdate(off);
                    this.updateOrderLimit(olTemp.getOrderId(), userId, odps);
                }
            }
        }
    }
    /**
     * 路歌渠道提现匹配订单（司机、经纪人、车老板路歌渠道提现）
     * @param inParam
     * @return
     * @throws Exception
     */
    public List<OrderLimit> matchOrderInfoForWithdraw(Map<String,String> inParam) throws Exception{
//	   	long userId = DataFormat.getLongKey(inParam,"userId");
//    	if(userId==0L){
//			throw new BusinessException("用户ID不能为空!");
//    	}
//    	String vehicleAffiliation = DataFormat.getStringKey(inParam,"vehicleAffiliation");
//    	if(StringUtils.isEmpty(vehicleAffiliation)){
//    		throw new BusinessException("资金渠道不能为空!");
//    	}
//    	long amount = DataFormat.getLongKey(inParam,"amountFee");
//    	if(amount==0L){
//			throw new BusinessException("提现金额不能为空!");
//    	}
//    	//返回匹配金额后的订单限制数据
//    	List<OrderLimit> orderList = new ArrayList();
//    	//查询是否是经纪人提现
//    	inParam.put("faceBalanceUnused","1");
//    	List<OrderLimit> agentOrder = this.getAgentOrder(inParam);
//    	//经纪人订单匹配
//    	if(agentOrder != null && agentOrder.size() > 0){
//    		for(OrderLimit off : agentOrder){
//    			//提现金额小于等于信息费
//    			if(off.getFaceBalanceUnused() >= amount){
//    				off.setMatchAmount(amount);
//    				orderList.add(off);
//    				return orderList;
//    			}
//    			else
//    			{
//    				//提现金额大于本单信息费
//    				off.setMatchAmount(off.getFaceBalanceUnused());
//    				amount = amount - off.getFaceBalanceUnused();
//    				orderList.add(off);
//    			}
//    		}
//    	}
//    	//司机订单匹配
//		Map<String,String> queryCond = new HashMap();
//		queryCond.put("userId", String.valueOf(userId));
//		queryCond.put("vehicleAffiliation", vehicleAffiliation);
//		List<OrderLimit> orderLimits = this.queryOrderLimitByCnd(queryCond);
//		Session session = SysContexts.getEntityManager();
//		UserDataInfo user = (UserDataInfo) session.get(UserDataInfo.class, userId);
//		if(user == null){
//			throw new BusinessException("找不到用户信息");
//		}
//		if(user.getCarUserType() == null){
//			throw new BusinessException("找不到用户类型");
//		}
//		if(user.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR || user.getCarUserType() == SysStaticDataEnum.CAR_USER_TYPE.SELF_EMPLOYED_CAR){
//			this.matchAmountToOrderOwnCar(amount, 0L, 0L, orderLimits, "NoPayCash", "DriverCostNoPay");
//		}else{
//			this.matchAmountToOrder(amount,0L,0L, "NoPayCash", orderLimits);
//		}
//    	for(OrderLimit driverOrder:orderLimits){
//    		if(driverOrder.getMatchAmount()!=null){
//				orderList.add(driverOrder);
//    		}
//    	}
//    	return orderList;
        return null;
    }

    /*产生订单资金流水数据*/
    public OrderFundFlow createOrderFundFlow(ParametersNewDto inParam,OrderFundFlow off, LoginInfo user){
        if(off==null){
            off = new OrderFundFlow();
        }
        long userId = inParam.getUserId();
        String billId = inParam.getBillId();
        String batchId = inParam.getBatchId();
        //UserDataInfo userDataInfo = userDataInfoSV.getUserByUserId(userId);
        UserDataInfo userDataInfo = null;
        try {
            userDataInfo = userDataInfoService.getUserDataInfo(userId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long amount = inParam.getAmount();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        off.setUserId(userId);//用户ID
        off.setBillId(billId);//手机
        off.setUserName(userDataInfo.getLinkman());//用户名
        off.setBatchId(batchId);//批次
        off.setBatchAmount(amount);//操作总金额
        if(user != null){
            off.setOpId(user.getId());//操作人ID
            off.setOpName(user.getName());//操作人
            off.setUpdateOpId(user.getId());//修改人
        }
        off.setOpDate(LocalDateTime.now());//操作日期
        off.setVehicleAffiliation(vehicleAffiliation);//资金渠道
        if(off.getIncome()==null){
            off.setIncome(0L);
        }
        if(off.getBackIncome()==null){
            off.setBackIncome(0L);
        }
        off.setCost(CommonUtil.getNotNullValue(off.getAmount()) - CommonUtil.getNotNullValue(off.getIncome()) - CommonUtil.getNotNullValue(off.getBackIncome()));//支付对方成本（单位分）
        return off;
    }

    /*回写订单限制数据（包含userId，因为自有车可以包含副驾）*/
    public int updateOrderLimit(long orderId, long userId, List<OperDataParam> odps){
        StringBuffer cond = new StringBuffer();
        Map setParam = new HashMap();
        Set keysSet = new HashSet();
        OperDataParam odp = null;
        for(int i=0;i<odps.size();i++){
            odp = odps.get(i);
            if(odp.getOperate()==null||"".equals(odp.getOperate())){
                if(odp.getDataType()!=null && !"".equals(odp.getDataType())){
                    if("Date".equals(odp.getDataType())){
                        cond.append(odp.getColumnName() + " = str_to_date(" + "'" + odp.getColumnValue() + "','%Y%m%d%H%i%s'),");
                    }
                }
                else
                {
                    cond.append(odp.getColumnName() + " = " + " :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
            }
            else
            {
                if(keysSet.add(odp.getColumnName())){
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName() + ",");
                    setParam.put(odp.getColumnName(), odp.getColumnValue());
                }
                else
                {
                    cond.append(odp.getColumnName() + " = IFNULL("+ odp.getColumnName() + ",0) " + odp.getOperate() +" :" + odp.getColumnName( ) + i + ",");
                    setParam.put(odp.getColumnName() + i, odp.getColumnValue());
                }
            }
        }
        String strcond = cond.substring(0, cond.length() - 1);
        int ret = orderLimitMapper.updateOrderLimit(orderId, userId, strcond);
        return ret;
    }

    //查询经纪人订单
    public List<OrderLimit> getAgentOrder(ParametersNewDto inParam) {
        String faceBalanceUnused = inParam.getFaceBalanceUnused();
        String faceMarginUnused = inParam.getFaceMarginUnused();
        String vehicleAffiliation = inParam.getVehicleAffiliation();
        Long userId = inParam.getUserId();
        Integer userType = inParam.getUserType();
        List<OrderLimit> list = orderLimitMapper.getAgentOrder(userId, vehicleAffiliation, faceBalanceUnused, faceMarginUnused, userType);
        return list;
    }

    /*根据资金流向数据和业务科目明细下面操作拼接操作列表*/
    public List<OperDataParam> getOpateDateParam(BusiSubjectsDtl bsd, OrderFundFlow off) {
        List<OperDataParam> odps = new ArrayList();
        List<BusiSubjectsDtlOperate> bsdos = busiSubjectsDtlOperateMapper.queryBusiSubjectsDtlOperate(bsd.getId());
        for(BusiSubjectsDtlOperate bsdo:bsdos){
            try {
                OperDataParam odp = this.getOperDataParam(bsdo, off);
                odps.add(odp);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("转换异常");
            }
        }
        return odps;
    }

    public OperDataParam getOperDataParam(BusiSubjectsDtlOperate bdo,OrderFundFlow off) throws Exception{
        Method method = off.getClass().getDeclaredMethod("get" + bdo.getFlowColum());
        String value = String.valueOf(method.invoke(off));
        return new OperDataParam(bdo.getColumName(),value,bdo.getOperate());
    }

    private long matchAmountToOrder(Long amount, Long income, Long backIncome, String fieldName, List<OrderLimit> orderLimits) {
        //保存剩余金额
        long allAmount = amount;
        if(income==null){
            income = 0L;
        }
        if(backIncome==null){
            backIncome = 0L;
        }
        double incomeRatio = ((double)income)/((double)amount);
        double backIncomeRatio = ((double)backIncome)/((double)amount);
        try {
            for (OrderLimit ol : orderLimits) {
                Method method = ol.getClass().getDeclaredMethod("get" + fieldName);
                Long value = (Long) method.invoke(ol);
                if (value == 0L) {
                    continue;
                }
                if (allAmount > value) {
                    ol.setMatchAmount(value);
                    ol.setMatchIncome(new Double(value * incomeRatio).longValue());
                    ol.setMatchBackIncome(new Double(value * backIncomeRatio).longValue());
                    //剩余金额=总金额-分摊掉金额
                    allAmount = allAmount - value;
                    income -= ol.getMatchIncome();
                    backIncome -= ol.getMatchBackIncome();
                } else if (allAmount <= value) {
                    ol.setMatchAmount(allAmount);
                    ol.setMatchIncome(income);
                    ol.setMatchBackIncome(backIncome);
                    allAmount = 0L;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("系统异常");
        }
        return allAmount;
    }
}
