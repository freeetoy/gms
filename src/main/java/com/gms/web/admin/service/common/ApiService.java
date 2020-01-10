package com.gms.web.admin.service.common;

import com.gms.web.admin.domain.manage.WorkReportVO;

public interface ApiService {
	
	public int registerWorkReportForSale(WorkReportVO param);
	
	public int registerWorkReportForChangeCd(WorkReportVO param);

}
