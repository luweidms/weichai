package com.youming.youche.system.dto.pub;

import com.youming.youche.system.domain.pub.PubPayCity;
import com.youming.youche.system.domain.pub.PubPayProv;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PubPayDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private PubPayProv pubPayProv;

    private List<PubPayCity> pubPayCityList;

}
