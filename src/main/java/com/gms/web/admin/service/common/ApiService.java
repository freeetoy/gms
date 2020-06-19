package com.gms.web.admin.service.common;

import java.util.List;

import com.gms.web.admin.domain.manage.CashFlowVO;
import com.gms.web.admin.domain.manage.SimpleBottleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;

public interface ApiService {
	
	public int registerWorkReportForSale(WorkReportVO param);
		
	public int registerWorkReportForChangeCd(WorkReportVO param);

	public int registerWorkReportNoGas(WorkBottleVO param);
	
	public int registerCashFlow(CashFlowVO param);
	
	public List<SimpleBottleVO> getCustomerSimpleBottleList(String customerNm);
	
	
	public int registerWorkReportGasAndBottle(WorkReportVO param);
}
