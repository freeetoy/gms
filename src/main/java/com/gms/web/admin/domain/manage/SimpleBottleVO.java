package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleBottleVO implements Serializable {
	
	private static final long serialVersionUID = 3324009822300528104L;

	/** Bottle_ID		*/
	private String bottleId;	

	/** Bottle_BarCd	*/
	private String bottleBarCd;
	
	/** Product Nm */
	private String productNm ;	
	
	/** Bottle_Capa	*/
	private String bottleCapa;
	
	/** Receivable_Amount	*/    
	private int receivableAmount;
}
