package com.youming.youche.record.common;

import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 向子俊
 * @version 1.0.0
 * @ClassName Direction.java
 * @Description TODO
 * @createTime 2022年02月21日 22:59:00
 */
@Data
public class DirectionDto implements Serializable {
    private Long distance;
    private Long duration;
    private String originLat;
    private String originLng;
    private String destLat;
    private String destLng;
    List<CmCustomerLineSubway> lineSubwayList;

}
