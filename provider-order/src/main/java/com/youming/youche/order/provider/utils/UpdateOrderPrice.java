package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.dto.ParametersNewDto;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zengwen
 * @date 2022/6/14 13:19
 */
@Component
public class UpdateOrderPrice {

    public List dealToOrder(ParametersNewDto inParam, List<BusiSubjectsRel> rels, LoginInfo user) {
//		Session session = SysContexts.getEntityManager();
//		long orderId = DataFormat.getLongKey(inParam,"orderId");
//    	long businessId = DataFormat.getLongKey(inParam,"businessId");
//    	long userId = DataFormat.getLongKey(inParam, "userId");
//    	if(orderId==0L){
//			throw new BusinessException("预付业务订单ID不能为空!");
//    	}
//    	//查询订单限制数据
//    	List<OrderLimit> orderLimits = this.getOrderLimitByOrderId(orderId, userId);
//    	if(orderLimits==null || orderLimits.size()<=0){
//    		throw new BusinessException("订单信息不存在!");
//    	}
//    	if(rels==null){
//    		throw new BusinessException("预付明细不能为空!");
//    	}
//    	OrderLimit ol = orderLimits.get(0);
//    	Criteria ca = session.createCriteria(UserDataInfo.class);
//    	ca.add(Restrictions.eq("userId", userId));
//    	ca.add(Restrictions.eq("tenantId", ol.getTenantId()));
//    	UserDataInfo user = (UserDataInfo) ca.uniqueResult();
//    	if (user == null ) {
//    		throw new BusinessException("用户不存在!");
//		}
//    	//资金流向批次
//		inParam.put("batchId", CommonUtil.createSoNbr() + "");
//		int userType = 0;
//		long vehicleCode = 0;
//		OrderImpl impl = (OrderImpl) SysContexts.getBean("orderImpl");
//    	OrderInfo oInfo = impl.getOrder(orderId);
//    	if (oInfo != null) {
//			if (oInfo.getCarUserType() != null && oInfo.getCarUserType() > 0) {
//				userType = oInfo.getCarUserType() ;
//				vehicleCode = oInfo.getVehicleCode() == null ? 0:oInfo.getVehicleCode();
//			}
//		}else{
//			OrderInfoH infoH = impl.getOrderH(orderId);
//			if (infoH != null) {
//				if (infoH.getCarUserType() != null && infoH.getCarUserType() > 0) {
//					userType = infoH.getCarUserType() ;
//					vehicleCode = infoH.getVehicleCode() == null ? 0:infoH.getVehicleCode();
//				}
//			}
//		}
//    	log.info("carUserType="+userType +"  vehicleCode ="+vehicleCode);
//    	if (userType != 0 && userType != 3 && userType != 4) {
//			if (vehicleCode != 0) {
//				VehicleDataInfo vInfo = (VehicleDataInfo) session.get(VehicleDataInfo.class, vehicleCode);
//				if (vInfo != null) {
//					Criteria userCa = session.createCriteria(UserDataInfo.class);
//					userCa.add(Restrictions.eq("mobilePhone", vInfo.getContactNumber()));
//					userCa.add(Restrictions.eq("tenantId", ol.getTenantId()));
//			    	UserDataInfo userCar = (UserDataInfo) userCa.uniqueResult();
//			    	if (userCar != null) {
//			    		log.info("userCar="+userCar.getUserId() +" mobilePhone ="+vInfo.getContactNumber());
//			    		 Criteria payOutCa = session.createCriteria(PayoutIntf.class);
//			    	        payOutCa.add(Restrictions.eq("userId", userCar.getUserId()));
//			    	        payOutCa.add(Restrictions.or(Restrictions.eq("respCode", "0"),Restrictions.isNull("respCode")));
//			    	        List<PayoutIntf> payoutIntfList = payOutCa.list();
//			    	        if (payoutIntfList != null && payoutIntfList.size() > 0) {
//			    	            throw new BusinessException("车老板有提现中的业务，不能处理");
//			    	        }
//					}
//				}
//			}
//		}
//        //如果有提现的都不能修改
//        Criteria payOutCa = session.createCriteria(PayoutIntf.class);
//        payOutCa.add(Restrictions.eq("userId", userId));
//        payOutCa.add(Restrictions.or(Restrictions.eq("respCode", "0"),Restrictions.isNull("respCode")));
//        List<PayoutIntf> payoutIntfList = payOutCa.list();
//        if (payoutIntfList != null && payoutIntfList.size() > 0) {
//            throw new BusinessException("该用户有提现中的业务，不能处理");
//        }
//        if(ol.getPaidOil()>0){
//            String offSql= " select p.* from payout_intf p where p.user_id in (select FACE_USER_ID from order_fund_flow f where f.USER_ID = :userId and f.SUBJECTS_ID = :subjectId) and p.RESP_CODE = 0  ";
//            Query query = session.createSQLQuery(offSql).addEntity("p",PayoutIntf.class);
//            query.setParameter("userId", userId);
//            query.setParameter("subjectId", EnumConsts.PayInter.PAY_FOR_OIL_CODE);
//            List<PayoutIntf> payOutList = query.list();
//            if (payOutList != null && payOutList.size() > 0) {
//                throw new BusinessException("该用户消费油在提现中，不能处理");
//            }
//        }
//
//    	//处理业务费用明细
//    	for(BusiSubjectsRel rel:rels){
//    		//金额为0不进行处理
//			if(rel.getAmountFee()==0L){
//				continue;
//			}
//			OrderFundFlow off = new OrderFundFlow();
//	    	off.setAmount(rel.getAmountFee());//交易金额（单位分）
//	    	off.setOrderId(orderId);//订单ID
//	    	off.setBusinessId(businessId);//业务类型
//	    	off.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//	    	off.setBookType(Long.parseLong(rel.getBookType()));//账户类型
//	    	off.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
//	    	off.setSubjectsId(rel.getSubjectsId());//科目ID
//	    	off.setSubjectsName(rel.getSubjectsName());//科目名称
//			off.setCost(rel.getAmountFee());
//			//修改订单金额     (信息费不能改动,与我们无关)
//    		if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_LARGEN){
//    			ol.setNoPayCash(ol.getNoPayCash() + off.getAmount());
//                ol.setNoWithdrawCash(ol.getNoWithdrawCash() + off.getAmount());
//                ol.setOrderCash(ol.getOrderCash() + off.getAmount());
//    			off.setInoutSts("in");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_MINIFY){
//    			long dept = 0;
//    			/*if (ol.getFaceUserId() != null && ol.getFaceBalanceUnused() !=0) {//有经纪人
//    				BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
//    				amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.CASH_UPDATE_ORDER_MINIFY);
//    				if(ol.getNoPayCash() - ol.getFaceBalanceUnused()  > 0){//司机费用抵扣
//    					if (off.getAmount() > (ol.getNoPayCash() - ol.getFaceBalanceUnused())) {//余额小于差额,产生欠款
//    						this.finalForDebt(Math.abs(off.getAmount()-(ol.getNoPayCash() - ol.getFaceBalanceUnused())),ol,session,inParam);//未被抵扣的记入欠款
//        					off.setShouldAmount(off.getAmount()-(ol.getNoPayCash() - ol.getFaceBalanceUnused()));//未被抵扣的记入欠款
//    						ol.setNoPayCash(ol.getNoPayCash() - (ol.getNoPayCash() - ol.getFaceBalanceUnused()));//抵扣全额司机费用保留信息费
//        					ol.setNoWithdrawCash(ol.getNoWithdrawCash() - (ol.getNoWithdrawCash() < (ol.getNoPayCash() - ol.getFaceBalanceUnused()) ? ol.getNoWithdrawCash() : (ol.getNoPayCash() - ol.getFaceBalanceUnused())));
//        					ol.setOrderCash(ol.getOrderCash() - (ol.getOrderCash() < (ol.getNoPayCash() - ol.getFaceBalanceUnused()) ? ol.getOrderCash() : (ol.getNoPayCash() - ol.getFaceBalanceUnused())));
//    					}else if(off.getAmount() <= (ol.getNoPayCash() - ol.getFaceBalanceUnused())){//余额大于差额
//    						ol.setNoPayCash(ol.getNoPayCash() - off.getAmount());//司机费用抵扣全额差额
//        					ol.setNoWithdrawCash(ol.getNoWithdrawCash() - (ol.getNoWithdrawCash() < off.getAmount() ? ol.getNoWithdrawCash() : off.getAmount()));
//        					ol.setOrderCash(ol.getOrderCash() - (ol.getOrderCash() < off.getAmount() ? ol.getOrderCash() : off.getAmount()));
//    					}
//    				}else if(ol.getNoPayCash() - ol.getFaceBalanceUnused() <= 0){//余额就为信息费
//    					this.finalForDebt(Math.abs(off.getAmount()),ol,session,inParam);
//    					off.setShouldAmount(off.getAmount());////直接全额欠款
//    				}
//    			}else */
//    			if(ol.getNoPayCash()  < off.getAmount()){
//    				dept = ol.getNoPayCash()  - off.getAmount();
//    			}
//    			if(dept < 0){
////                    抵扣欠款
//                    this.finalForDebt(Math.abs(dept),ol,session,inParam);
//                    OrderFundFlow offDebt = new OrderFundFlow();
//                    offDebt.setAmount(Math.abs(dept));//交易金额（单位分）
//                    offDebt.setOrderId(orderId);//订单ID
//                    offDebt.setBusinessId(businessId);//业务类型
//                    offDebt.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//        	    	offDebt.setBookType(Long.parseLong(rel.getBookType()));//账户类型
//        	    	offDebt.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
//        	    	offDebt.setSubjectsId(EnumConsts.SubjectIds.UPDATE_ORDER_CASH_ARREARS);//科目ID
//        	    	offDebt.setSubjectsName("现金产生欠款");//科目名称
//        	    	offDebt.setCost(rel.getAmountFee());
//        	    	offDebt.setInoutSts("out");
//        	    	this.createOrderFundFlow(inParam,offDebt);
//        	    	session.saveOrUpdate(offDebt);
//
//                    off.setShouldAmount(dept);//记录抵扣金额
//                    ol.setNoWithdrawCash(ol.getNoWithdrawCash() - (ol.getNoWithdrawCash() < ol.getNoPayCash() ? ol.getNoWithdrawCash():ol.getNoPayCash()));
//                    ol.setNoPayCash(0l);
//                    ol.setOrderCash(ol.getOrderCash() - (ol.getOrderCash() < off.getAmount() ? ol.getOrderCash() : off.getAmount()));
//    			}else{
//    				ol.setNoPayCash(ol.getNoPayCash() - off.getAmount());
//                    ol.setNoWithdrawCash(ol.getNoWithdrawCash() - (ol.getNoWithdrawCash() < off.getAmount() ? ol.getNoWithdrawCash() : off.getAmount()));
//                    ol.setOrderCash(ol.getOrderCash() - (ol.getOrderCash() < off.getAmount() ? ol.getOrderCash() : off.getAmount()));
//    			}
//    			off.setInoutSts("out");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_LARGEN){//实体油改大
//    			ol.setOrderOil(ol.getOrderOil() + off.getAmount());
//    			ol.setOrderEntityOil(ol.getOrderEntityOil() + off.getAmount());
//    			ol.setCostEntityOil(ol.getCostEntityOil() + off.getAmount());
//    			off.setInoutSts("in");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ENTITY_OIL_MINIFY){//实体油改小
//    			long dept = 0;
//    			if(ol.getOrderEntityOil() < off.getAmount()){
//    				dept = ol.getOrderEntityOil() - off.getAmount();
//    			}
//    			if(dept < 0){
////                    抵扣欠款
//                    ol.setOrderOil(ol.getOrderOil() - (ol.getOrderOil() < off.getAmount() ? ol.getOrderOil():off.getAmount()));
//        			ol.setOrderEntityOil(0l);
//    			}else{
//    				ol.setOrderOil(ol.getOrderOil() - (ol.getOrderOil() < off.getAmount() ? ol.getOrderOil():off.getAmount()));
//        			ol.setOrderEntityOil(ol.getOrderEntityOil() - (ol.getOrderEntityOil()< off.getAmount() ? ol.getOrderEntityOil():off.getAmount()));
//    			}
//    			off.setInoutSts("out");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_LARGEN){//虚拟油改大
//    			ol.setOrderOil(ol.getOrderOil() + off.getAmount());
//    			ol.setNoPayOil(ol.getNoPayOil() + off.getAmount());
//    			off.setInoutSts("in");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_VIRTUAL_OIL_MINIFY){//虚拟油改小
//    			long dept = 0;
//    			if(ol.getNoPayOil() < off.getAmount()){
//    				dept = ol.getNoPayOil() - off.getAmount();
//    			}
//    			if(dept < 0){
////                    抵扣欠款
//    				this.finalForDebt(Math.abs(dept),ol,session,inParam);
//    				OrderFundFlow offDebt = new OrderFundFlow();
//                    offDebt.setAmount(Math.abs(dept));//交易金额（单位分）
//                    offDebt.setOrderId(orderId);//订单ID
//                    offDebt.setBusinessId(businessId);//业务类型
//                    offDebt.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//        	    	offDebt.setBookType(Long.parseLong(rel.getBookType()));//账户类型
//        	    	offDebt.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
//        	    	offDebt.setSubjectsId(EnumConsts.SubjectIds.UPDATE_ORDER_VIRTUAL_OIL_ARREARS);//科目ID
//        	    	offDebt.setSubjectsName("虚拟油卡产生欠款");//科目名称
//        	    	offDebt.setCost(rel.getAmountFee());
//        	    	offDebt.setInoutSts("out");
//        	    	this.createOrderFundFlow(inParam,offDebt);
//        	    	session.saveOrUpdate(offDebt);
//
//                    Integer state = DataFormat.getIntKey(inParam,"state");
//                    if (state != null && state == 1) {//修改
//                    	off.setShouldAmount(dept);//记录抵扣金额
//                        ol.setOrderOil(ol.getOrderOil() - (ol.getOrderOil() < off.getAmount() ? ol.getOrderOil():off.getAmount()));
//            			ol.setNoPayOil(0l);
//					}else{//撤单
//						Double problemPirce = 0.0;
//						OrderProblemInfoDaoImpl orderProblemInfoDao = (OrderProblemInfoDaoImpl)SysContexts.getBean("orderProblemDao");
//						List<OrderProblemInfo> list = orderProblemInfoDao.getOrderProblemInfos(orderId, ol.getTenantId());
//						for (OrderProblemInfo p : list) {
//							if (p.getState().intValue() == Exception_Deal_Status.DEAL_CANCEL.getStatus()) { continue; }
//							if (p.getProblemCondition().intValue() == SysStaticDataEnum.PROBLEM_CONDITION.COST) {
//									if (Exception_Deal_Status.FINANCIAL_DEPARTMENT_PASS.getStatus() == p.getState().intValue() && ("5".equals(p.getProblemType()) || "7".equals(p.getProblemType()) || "8".equals(p.getProblemType()) || "9".equals(p.getProblemType()))) {
//										problemPirce += p.getProblemDealPrice() == null ? 0.0 : p.getProblemDealPrice()/100;
//									}
//							}
//						}
//						long num = ol.getOrderOil() + (ol.getOilIncome() == null ? 0:ol.getOilIncome()) - (ol.getOrderEntityOil() == null ? 0 :ol.getOrderEntityOil());
//						double badNum = (ol.getOrderOil() + (ol.getOilIncome() == null ? 0:ol.getOilIncome()) - (ol.getOrderEntityOil() == null ? 0 :ol.getOrderEntityOil()) - off.getAmount()) / 100;//订单油+油利润 - 实体油 - 订单的业务虚拟金额=剩余现金异常油
//						double  scale =  (ol.getOilIncome() /100.0) / (num / 100.0);
//						if (badNum > 0) {//存在油以外的金额
//							if (badNum > problemPirce) {
//								ol.setOrderOil(CommonUtil.getDoubleFormat(problemPirce - (problemPirce * scale),2).longValue()*100);
//							}else{
//								ol.setOrderOil(CommonUtil.getDoubleFormat(badNum - (badNum * scale),2).longValue()*100);
//							}
//						}else{
//							ol.setOrderOil(0l);
//						}//<= 0 不存在其他金额
//						off.setShouldAmount(dept);//记录抵扣金额
//	        			ol.setNoPayOil(0l);
//					}
//    			}else{
//    				ol.setOrderOil(ol.getOrderOil() -  (ol.getOrderOil() < off.getAmount() ? ol.getOrderOil():off.getAmount()));
//         			ol.setNoPayOil(ol.getNoPayOil() - (ol.getNoPayOil() < off.getAmount() ? ol.getNoPayOil():off.getAmount()));
//    			}
//    			off.setInoutSts("out");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_LARGEN){//ETC改大
//    			ol.setOrderEtc(ol.getOrderEtc() + off.getAmount());
//    			ol.setNoPayEtc(ol.getNoPayEtc() + off.getAmount());
//    			off.setInoutSts("in");//收支状态:收in支out转io
//    		}else if(rel.getSubjectsId()==EnumConsts.SubjectIds.CASH_UPDATE_ORDER_ETC_MINIFY){//ETC改小
//    			long dept = 0;
//    			if(ol.getNoPayEtc() < off.getAmount()){
//    				dept = ol.getNoPayEtc()  - off.getAmount();
//    			}
//    			if(dept < 0){
////                    抵扣欠款
//    				this.finalForDebt(Math.abs(dept),ol,session,inParam);
//    				OrderFundFlow offDebt = new OrderFundFlow();
//                    offDebt.setAmount(Math.abs(dept));//交易金额（单位分）
//                    offDebt.setOrderId(orderId);//订单ID
//                    offDebt.setBusinessId(businessId);//业务类型
//                    offDebt.setBusinessName(SysStaticDataUtil.getSysStaticData("ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(businessId)).getCodeName());//业务名称
//        	    	offDebt.setBookType(Long.parseLong(rel.getBookType()));//账户类型
//        	    	offDebt.setBookTypeName(SysStaticDataUtil.getSysStaticData("ACCOUNT_BOOK_TYPE", rel.getBookType()).getCodeName());//账户类型
//        	    	offDebt.setSubjectsId(EnumConsts.SubjectIds.UPDATE_ORDER_ETC_ARREARS);//科目ID
//        	    	offDebt.setSubjectsName("ETC产生欠款");//科目名称
//        	    	offDebt.setCost(rel.getAmountFee());
//        	    	offDebt.setInoutSts("out");
//        	    	this.createOrderFundFlow(inParam,offDebt);
//        	    	session.saveOrUpdate(offDebt);
//
//                    off.setShouldAmount(dept);//记录抵扣金额
//                    ol.setOrderEtc(ol.getOrderEtc() - (ol.getOrderEtc() < off.getAmount() ? ol.getOrderEtc(): off.getAmount()));
//        			ol.setNoPayEtc(0l);
//    			}else{
//    				ol.setOrderEtc(ol.getOrderEtc() - (ol.getOrderEtc() < off.getAmount() ? ol.getOrderEtc(): off.getAmount()));
//        			ol.setNoPayEtc(ol.getNoPayEtc() - off.getAmount());
//    			}
//    			off.setInoutSts("out");//收支状态:收in支out转io
//    		}
//            this.createOrderFundFlow(inParam,off);
//    		session.saveOrUpdate(off);
//    		session.saveOrUpdate(ol);
//			//this.updateOrderLimit(ol.getOrderId(), userId, odps);
//    	}
        return null;
    }
}
