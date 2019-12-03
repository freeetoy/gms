package com.gms.web.admin.service.common;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.gms.web.admin.domain.manage.GasVO;

public interface ExcelService {

	public List<GasVO> uploadExcelFile(MultipartFile excelFile);
}
