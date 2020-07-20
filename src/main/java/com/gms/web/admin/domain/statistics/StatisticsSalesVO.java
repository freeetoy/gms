package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsSalesVO implements Serializable {

	private static final long serialVersionUID = 8432003342300538104L;
	
	/**Stat_Dt		  */
	private String statDt;
	
	/**Sales_Count     */
	private int salesCount;
	
	/**Sales_Amount    */
	private int salesAmount;

	/**Rental_Count     */
	private int rentalCount;
	
	/**Income_Amount    */
	private int incomeAmount;
	
	/**Rental_Amount    */
	private int rentalAmount;
	
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
