package com.gms.web.admin.service.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.gms.web.admin.domain.manage.GasVO;

@Service
public class ExcelServiceImpl implements ExcelService {

	@Override
	public List<GasVO> uploadExcelFile(MultipartFile excelFile) {
        List<GasVO> list = new ArrayList<GasVO>();
        try {
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
                GasVO fruit = new GasVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }
                
                // 행의 두번째 열(이름부터 받아오기) 
                XSSFCell cell = row.getCell(1);
                if(null != cell) fruit.setGasNm(cell.getStringCellValue());
                // 행의 세번째 열 받아오기
                cell = row.getCell(2);
                if(null != cell) fruit.setGasCd(cell.getStringCellValue());
                // 행의 네번째 열 받아오기
                //cell = row.getCell(3);
                //if(null != cell) fruit.setGasId((Integer)cell.getNumericCellValue());
                
                list.add(fruit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
	}

}
