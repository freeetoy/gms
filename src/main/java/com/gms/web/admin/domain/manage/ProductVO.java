package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductVO extends AbstractVO  implements Serializable {
	
	private static final long serialVersionUID = 8484009842300528104L;
	
	/**Product_ID */
	private Integer productId;
		
	/**Product_Nm */
	private String productNm;	
	
	/**Gas_ID */
	private Integer gasId;	
		
	/**Prodcut_Status */
	private String productStatus;
	
	/**Member_Comp_Seq */ 
	private Integer memberCompSeq;
	

}
