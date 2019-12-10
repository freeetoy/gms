package com.gms.web.admin.service.common;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {

	public int uploadBottleExcelFile(MultipartFile excelFile);
	
	public int uploadCustomerExcelFile(MultipartFile excelFile);
}
