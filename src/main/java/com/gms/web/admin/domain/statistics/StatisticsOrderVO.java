package com.gms.web.admin.domain.statistics;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatisticsOrderVO extends AbstractVO implements Serializable {

	private static final long serialVersionUID = 8433003342300538104L;
	
	/**Stat_Dt		  */
	private String statDt;
	
	/**Order_Total_Count     */
	private int orderTotalCount;
	
	/**Order_Total_Amount    */
	private int orderTotalAmount;
	
	/**Order_Product_Count   */
	private int orderProductCount;
	
	/**Order_Cancel_Count    */
	private int orderCancelCount;
	
	/**Order_Retrieved_Count */
	private int orderRetrievedCount;
	
	/**Order_Check_Count     */
	private int orderCheckCount;
	
	/**Order_Etc_Count       */
	private int orderEtcCount;
	
	/** searchStatDt	*/
	private String  searchStatDt;
	
	/** searchStatDtFrom	*/
	private String  searchStatDtFrom;
	
	/** searchStatDtEnd	*/
	private String  searchStatDtEnd;
}
