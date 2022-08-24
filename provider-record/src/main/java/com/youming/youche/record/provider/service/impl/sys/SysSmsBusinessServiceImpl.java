package com.youming.youche.record.provider.service.impl.sys;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.record.api.sys.ISysSmsBusinessService;
import com.youming.youche.record.api.user.IUserDataInfoRecordService;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.domain.sys.SysSmsBusiness;
import com.youming.youche.record.domain.user.UserDataInfo;
import com.youming.youche.record.provider.mapper.sys.SysSmsBusinessMapper;
import com.youming.youche.system.api.ISysCfgService;
import com.youming.youche.util.DateUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

/**
 * <p>
 * 短信服务商表 服务实现类
 * </p>
 *
 * @author Terry
 * @since 2021-11-19
 */
@DubboService(version = "1.0.0")
public class SysSmsBusinessServiceImpl extends ServiceImpl<SysSmsBusinessMapper, SysSmsBusiness>
		implements ISysSmsBusinessService {


	@Resource
	SysSmsBusinessMapper sysSmsBusinessMapper;

	@DubboReference(version = "1.0.0")
	ISysCfgService iSysCfgRecordService;

	@Resource
	IUserDataInfoRecordService iUserDataInfoRecordService;

	@Override
	public String getPrimaryKey(String phoneNumber, Date date,String accessToken) {
		Integer max = 0;
		/***时间格式yyyyMMdd,截取年月份 eg：20150302132402 截取结果为：150302132402*/
		String strTime = DateUtil.formatDate(date,DateUtil.DATETIME12_FORMAT2);
		String con = phoneNumber + strTime.substring(2, 8);
		String phoneNstrTime = phoneNumber+ strTime.substring(2, 14);
		/**根据手机号码、当前时间 查询数据库主键*/
		/**查询当天这个手机号码最大的主键(主键生成策略)**/
		UserDataInfo user= iUserDataInfoRecordService.getUserDataInfoByAccessToken(accessToken);
		Long tenantId=user.getTenantId();
		String maxStr=sysSmsBusinessMapper.MAXBid(phoneNumber,tenantId);
		String primaryKey = "";
		if(maxStr!=null){
			max = Integer.parseInt(maxStr.substring(0, 3));
		}
		/**
		 * E讯通只是支持100个子端口
		 *
		 * 0表示原来的短信平台
		 *
		 */
		if(max>99){
//			Object switchVal= SysCfgUtil.getCfgVal(EnumConsts.SmsParam.SWITCH, 0, String.class);
			SysCfg switchVal= iSysCfgRecordService.getSysCfg(EnumConsts.SmsParam.SWITCH,accessToken);
			if(org.springframework.util.StringUtils.isEmpty(switchVal)){
				if(max>1000){
					log.error("SysSmsBusinessBO is error,billId["+phoneNumber+"] is send too much for need reply,send max["+max+"]");
				}
			}else{
				if(EnumConsts.SmsParam.SWITCH_DEFAULT.equals(switchVal.toString())){
					if(max>1000){
						log.error("SysSmsBusinessBO is error,billId["+phoneNumber+"] is send too much for need reply,send max["+max+"]");
					}
				}else if(EnumConsts.SmsParam.SWITCH_E.equals(switchVal.toString())){
					log.error("SysSmsBusinessBO is error,billId["+phoneNumber+"] is send too much for need reply,send max["+max+"]");
				}else {
					log.error("table sys_cfg cfg_name= SMS_PARAM_SWITCH cfg_value is not equal 0 or 1");
				}
			}
		}
		if(maxStr == null){
			primaryKey = "001" + con;
		}else{
			int MaxIn = max + 1;
			String bb = "";
			if((MaxIn+"").length() < 3){
				for(int i = 0;i < 3 - (MaxIn+"").length() ; i++){
					bb += "0";
				}
			}
			String key =  bb + MaxIn;
			primaryKey  = key + phoneNstrTime + getRandomNumber(2);
		}
		return primaryKey;
	}

	/**
	 * 得到n位长度的随机数
	 * @param n 随机数的长度
	 * @return 返回  n位的随机整数
	 */
	public static int getRandomNumber(int n){
		int temp = 0;
		int min = (int) Math.pow(10, n-1);
		int max = (int) Math.pow(10, n);
		Random rand = new Random();

		while(true){
			temp = rand.nextInt(max);
			if(temp >= min) {
				break;
			}
		}
		return temp;
	}
}
