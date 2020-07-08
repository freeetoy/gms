package com.gms.web.admin.service.common;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface ExcelService {

	public int uploadBottleExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerPriceExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
	
	public int uploadCustomerBottleExcelFile(MultipartHttpServletRequest request, MultipartFile excelFile);
}
