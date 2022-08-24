package com.youming.youche.finance.vo;

import com.youming.youche.finance.domain.VehicleExpense;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CreateVehicleExpenseVo extends VehicleExpense implements Serializable {

	private static final long serialVersionUID = -2091976793303817142L;

	private List<Long> resourceIds;

}
