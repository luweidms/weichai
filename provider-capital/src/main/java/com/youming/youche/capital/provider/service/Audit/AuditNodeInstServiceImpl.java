package com.youming.youche.capital.provider.service.Audit;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.capital.api.IPayFeeLimitService;
import com.youming.youche.capital.api.IPayFeeLimitVerService;
import com.youming.youche.capital.api.iaudit.IAuditNodeInstService;
import com.youming.youche.capital.api.iaudit.IAuditNodeInstVerService;
import com.youming.youche.capital.domain.audit.AuditNodeInst;
import com.youming.youche.capital.domain.audit.AuditNodeInstVer;
import com.youming.youche.capital.provider.mapper.audit.AuditNodeInstMapper;
import com.youming.youche.capital.provider.mapper.audit.AuditUserMapper;
import com.youming.youche.capital.provider.service.PayFeeLimitVerServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.audit.IAuditUserService;
import com.youming.youche.system.domain.audit.AuditUser;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审核节点实例表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */

@DubboService(version = "1.0.0")
public class AuditNodeInstServiceImpl extends ServiceImpl<AuditNodeInstMapper, AuditNodeInst>
		implements IAuditNodeInstService {


	@Resource
	AuditNodeInstMapper auditNodeInstMapper;

	@DubboReference(version = "1.0.0")
	IAuditUserService iAuditUserService;

	@Resource
	AuditUserMapper auditUserMapper;

	@DubboReference(version = "1.0.0")
	IUserDataInfoService iUserDataInfoService;

	@Resource
	IAuditNodeInstVerService iAuditNodeInstVerService;

	@Resource
	IPayFeeLimitVerService iPayFeeLimitVerService;


	@Override
	public List<AuditNodeInst> queryAuditIng(String busiCode, List<Long> busiId) {
		QueryWrapper<AuditNodeInst> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("audit_Code",busiCode).
				eq("STATUS", AuditConsts.Status.AUDITING).
				eq("AUDIT_RESULT", AuditConsts.RESULT.TO_AUDIT).in("BUSI_ID",busiId);
		return auditNodeInstMapper.selectList(queryWrapper);
	}

	@Override
	public AuditNodeInst queryAuditIng(String busiCode, Long busiId, Long tenantId) {
		QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("audit_Code", busiCode)
				.eq("busi_Id", busiId)
				.eq("tenant_Id", tenantId)
				.eq("status", AuditConsts.Status.AUDITING)
				.eq("audit_result", AuditConsts.RESULT.TO_AUDIT);
		List<AuditNodeInst> auditNodeInsts = baseMapper.selectList(queryWrapper);
		if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
			if (auditNodeInsts.size() == 1) {
				return auditNodeInsts.get(0);
			} else {
				throw new BusinessException("业务编码[" + busiCode + "],业务主键[" + busiId + "] 数据有多条在审核的流程");
			}
		}
		return null;
	}


	@Override
	public boolean isHasPermission(Long userId, AuditNodeInst auditNodeInst) {
		Long nodeId= auditNodeInst.getNodeId();
		if(nodeId==null){
			log.error("用户没有需要审核的节点");
			return false;
		}
		AuditUser auditUser= iAuditUserService.getAuditUser(nodeId, userId, AuditConsts.TargetObjType.USER_TYPE);
		if(auditUser!=null)
			return true;
		return false;
	}

	@Override
	public boolean isHasPermission(Long userId, Long auditNodeInstId) {
		AuditNodeInst auditNodeInst = getById(auditNodeInstId);
		return isHasPermission(userId,auditNodeInst);
	}


	@Override
	public Map<Long, Boolean> isHasPermission(String auditCode, List<Long> busiIdList,String accessToken) {
		if(busiIdList==null || busiIdList.size()==0){
			throw new BusinessException("传入的判断的数据列表为空");
		}
		Map<Long, Boolean> returnMap=new HashMap<Long, Boolean>();
		for (Long busiId : busiIdList) {
			returnMap.put(busiId, false);
		}
		LoginInfo user = iUserDataInfoService.getLoginInfoByAccessToken(accessToken);
//		Map<String,Object> userAttr= user.getAttrs();
//		List<Long> rolesIds=(List<Long>)userAttr.get("roleIds");
		List<AuditNodeInst> auditNodeInsts= this.queryAuditIng(auditCode, busiIdList);
		if(auditNodeInsts!=null && auditNodeInsts.size()>0){
			for (AuditNodeInst auditNodeInst : auditNodeInsts) {
				if(returnMap.get(auditNodeInst.getBusiId())!=null && this.isHasPermission(user.getUserInfoId(), auditNodeInst)){
					//有权限
					returnMap.put(auditNodeInst.getBusiId(), true);
				}
			}
		}
		return returnMap;
	}




	@Override
	public List<Long> getUserFromAuditUser(Long nodeId, Integer targetObjType) {
		QueryWrapper<AuditUser> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("NODE_ID",nodeId).eq("TARGET_OBJ_TYPE",targetObjType);
		queryWrapper.select("TARGET_OBJ_ID");
		List<AuditUser> list=auditUserMapper.selectList(queryWrapper);
		List<Long> longs=new ArrayList<>();
		if(list!=null && list.size()>0){
			for(AuditUser auditUser : list){
				longs.add(auditUser.getTargetObjId());
			}
		}
		return longs;
	}

	@Override
	public void nodeAuditCallback(int nodeIndex, long busiId, int targetObjectType, String targetObjId, String callBack) throws Exception {
		if(StringUtils.isEmpty(callBack)) {
			return;
		}
		Class cls = Class.forName(callBack);
		Method m = cls.getDeclaredMethod("notice",new Class[]{Integer.class,Long.class,Integer.class,String.class});
		m.invoke(cls.newInstance(),nodeIndex,busiId,targetObjectType,targetObjId);
	}

	@Override
	public Long getAuditingNodeNum(String busiCode, Long busiId) {
		QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("audit_Code", busiCode)
				.eq("busi_Id", busiId)
				.eq("status", AuditConsts.Status.AUDITING);
		Integer count = baseMapper.selectCount(queryWrapper);
		return Long.valueOf(count);
	}

	@Override
	public void updateAuditInstToFinish(Long auditId, Long busiId) {
		auditUserMapper.updateAuditInstToFinish(auditId, busiId);
	}

	@Override
	public void delInstToVer(String busiCode, Long busiId, Long tenantId) throws Exception {
		QueryWrapper<AuditNodeInst> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("audit_Code", busiCode)
				.eq("busi_Id", busiId)
				.eq("tenant_Id", tenantId)
				.eq("status", AuditConsts.Status.FINISH);
		List<AuditNodeInst> auditNodeInsts = baseMapper.selectList(queryWrapper);
		if (auditNodeInsts != null && auditNodeInsts.size() > 0) {
			for (AuditNodeInst auditNodeInst : auditNodeInsts) {
				AuditNodeInstVer auditNodeInstVer = new AuditNodeInstVer();
				BeanUtil.copyProperties(auditNodeInst, auditNodeInstVer);
				iAuditNodeInstVerService.save(auditNodeInstVer);
				removeById(auditNodeInst.getId());
			}
		}
	}

	@Override
	public void auditCallback(Long busiId, Integer result, String desc, Map paramsMap, String callback, String token) throws Exception {
		if (AuditConsts.RESULT.SUCCESS == result) {
			if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
				//资金风控审核成功
				iPayFeeLimitVerService.sucess(busiId,desc,paramsMap,token);
			}
		} else if (AuditConsts.RESULT.FAIL == result) {
			//	m = cls.getDeclaredMethod("fail",new Class[]{Long.class,String.class,Map.class});
			if (callback.equals("com.youming.youche.capital.api.IPayFeeLimitVerService")) {
				//资金风控审核失败
				iPayFeeLimitVerService.fail(busiId,desc,paramsMap,token);
			}
		} else {
			throw new BusinessException("传入的回调方法的结果类型只能1，2，传入的是[" + result + "]");
		}
	}

	@Override
	public String getAuditUserNameByNodeId(Long nodeId, Integer targetObjType) throws Exception {
		List<Long> userIdList = auditUserMapper.getUserFromAuditUser2(nodeId, targetObjType);
		StringBuffer alert = new StringBuffer();
		if (userIdList != null && userIdList.size() > 0) {
			alert.append("人:");
			List<String> userNameList = iUserDataInfoService.querStaffName(userIdList);
			alert.append(listToString(userNameList));
		} else {
			throw new BusinessException("下个节点的审核的对象类型设置错误");
		}
		return alert.toString();
	}

	public  String listToString(List<String> list){
		if(list==null){
			return null;
		}
		StringBuilder result = new StringBuilder();
		boolean first = true;
		//第一个前面不拼接","
		for(String string :list) {
			if(first) {
				first=false;
			}else{
				result.append(",");
			}
			result.append(string);
		}
		return result.toString();
	}

}
