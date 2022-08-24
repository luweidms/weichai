package com.youming.youche.system.provider.runner;

import com.youming.youche.commons.domain.SysCfg;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.system.api.ISysCfgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@Order(999)
public class SysCfgRunner implements CommandLineRunner {

	@Resource
	ISysCfgService sysCfgService;

	@Resource
	RedisUtil redisUtil;

	@Override
	public void run(String... args) throws Exception {

		List<SysCfg> sysCfgs = sysCfgService.selectAll();
		log.info(EnumConsts.SysCfg.SYS_CFG_NAME.concat("加载系统配置缓存开始--0%"));
		saveRedis(sysCfgs);
		// List<SysStaticData> o = (List<SysStaticData>)
		// redisUtil.get("sys_static_data:ACCOUNT_DETAILS_BOOK_TYPE");
		// log.info(o.toString());
	}

	void saveRedis(List<SysCfg> sysCfgs) {
		int size = sysCfgs.size();
		int base = size / 10;
		int coun = 0;
		int coun2 = 0;
		for (SysCfg sysCfg : sysCfgs) {
			coun++;
			if (coun % base == 0) {
				coun2++;
				log.info(EnumConsts.SysCfg.SYS_CFG_NAME.concat("加载配置缓存开始--" + coun2 + "0%"));
			}
			redisUtil.set(EnumConsts.SysCfg.SYS_CFG_NAME.concat(sysCfg.getCfgName()), sysCfg);
		}
		log.info(EnumConsts.SysCfg.SYS_CFG_NAME.concat("加载配置缓存完成"));
	}

}
