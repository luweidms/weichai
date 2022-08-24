package com.youming.youche.system.provider.runner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.system.api.ISysStaticDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@Order(998)
public class SysStaticDataRunner implements CommandLineRunner {

	@Resource
	ISysStaticDataService sysStaticDataService;

	@Resource
	RedisUtil redisUtil;

	@Override
	public void run(String... args) throws Exception {

		List<SysStaticData> sysStaticData = sysStaticDataService.selectAllByState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
		log.info(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("加载缓存开始--0%"));
		saveRedis(sysStaticData);
		// List<SysStaticData> o = (List<SysStaticData>)
		// redisUtil.get("sys_static_data:ACCOUNT_DETAILS_BOOK_TYPE");
		// log.info(o.toString());
	}

	void saveRedis(List<SysStaticData> sysStaticData) {
		Map<String, List<SysStaticData>> map = Maps.newHashMap();
		for (SysStaticData sysStaticDatum : sysStaticData) {
			List<SysStaticData> sysStaticData1 = map.get(sysStaticDatum.getCodeType());
			if (CollectionUtils.isNotEmpty(sysStaticData1)) {
				sysStaticData1.add(sysStaticDatum);
			}
			else {
				ArrayList<SysStaticData> arrayList = Lists.newArrayList();
				arrayList.add(sysStaticDatum);
				map.put(sysStaticDatum.getCodeType(), arrayList);
			}
		}
		int size = map.size();
		int base = size / 10;
		int coun = 0;
		int coun2 = 0;
		for (Map.Entry<String, List<SysStaticData>> entry : map.entrySet()) {
			coun++;
			if (coun % base == 0) {
				coun2++;
				log.info(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("加载缓存开始--" + coun2 + "0%"));
			}
			// String mapKey = entry.getKey();
			// List<SysStaticData> mapValue = entry.getValue();
			redisUtil.set(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(entry.getKey()), entry.getValue());
		}
		log.info(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat("加载缓存完成"));
	}

}
