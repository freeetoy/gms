package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsProductVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8484003342300538104L;
	
	
	/**Product_ID   */
	private Integer productId ;
	
	/**Stat_Dt      */
	private String statDt;
	
	/**Order_Count  */
	private int orderCount;
	
	/**Order_Amount */
	private int orderAmount;
	
	private Integer searchProductId;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;

}
