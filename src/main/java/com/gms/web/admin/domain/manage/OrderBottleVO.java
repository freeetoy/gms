package com.gms.web.admin.domain.manage;


import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderBottleVO extends AbstractVO {

	private static final long serialVersionUID = 3111109822300528104L;
	
	
	/* Order_Bottle_Seq*/
	private Integer orderBottleSeq;
	
	/** Order_ID		*/
	private Integer orderId;
	
	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	
	/** Bottle_ID		*/
	private String bottleId;
}
