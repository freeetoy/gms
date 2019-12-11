package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsCustomerVO extends AbstractVO implements Serializable {

private static final long serialVersionUID = 8474003342300538104L;
	
	
	/**Customer_ID   */
	private Integer customerId ;
	
	/**Stat_Dt      */
	private String statDt;
	
	/**Order_Count  */
	private int orderCount;
	
	/**Order_Amount */
	private int orderAmount;
	
	/** searchCustomerId	*/
	private Integer searchCustomerId;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;
	
	/** periodType
	 * 1 daily
	 * 2 monthly	*/
	private int  periodType;
}
