package com.youming.youche.record.common;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;

@Data
public class Monitor extends BaseDomain {
	private String name;
	private String lng;
	private String lat;
	private String city;
	private String district;

}
