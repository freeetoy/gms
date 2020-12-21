package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsAgencyVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8483003342300538104L;
	
	/**Stat_Dt      */
	private String statDt;
	
	/**Customer_ID   */
	private Integer customerId ;
	
	/**Product_ID   */
	private Integer productId ;	
	
	/**Product_Price_SEq   */
	private Integer productPriceSeq ;	
	
	/**Bottle_Own_Count  */
	private int bottleOwnCount;
	
	/**Bottle_Rent_Count */
	private int bottleRentCount;
		
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
