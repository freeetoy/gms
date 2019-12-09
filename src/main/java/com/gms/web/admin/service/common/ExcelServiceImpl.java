package com.gms.web.admin.service.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gms.web.admin.domain.manage.GasVO;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

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
                
                XSSFCell cell = row.getCell(0);
                //if(null != cell) fruit.setGasNm(cell.getNumericCellValue());
                logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());
                // 행의 두번째 열(이름부터 받아오기) 
                cell = row.getCell(1);
                if(null != cell) fruit.setGasNm(cell.getStringCellValue());
                logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getStringCellValue());
                // 행의 세번째 열 받아오기
                cell = row.getCell(2);
                if(null != cell) fruit.setGasCd(cell.getStringCellValue());
                logger.debug("ExcelSerive uploadExcelFile cell.getCellType()2== "+ cell.getCellType());
                logger.debug("ExcelSerive uploadExcelFile2 =="+ cell.getStringCellValue());
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
	
	
	public void readExcelXLSX(String excel) {
        try {
            String[] tempFileName = excel.split("/");
            String fileName = tempFileName[excel.split("/").length - 1];
 
           
            File file = new File(excel.trim());
 
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }
 
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);
 
            makeExcelToObject(wb.getSheetAt(0), "xlsx", fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void makeExcelToObject(Object sheet, String flag, String fileName) throws IOException {
        Row titles = null;
 
        for (Row row : (flag.equals("xls") ? (HSSFSheet) sheet : (XSSFSheet) sheet)) {
            if (row.getRowNum() == 0) {
                titles = row;
                continue;
            }
 
            updateData(titles, row);
        }
    }
    
    @Transactional
    private void updateData(Row titles, Row row) throws IOException {
        String colName = null;
        String colValue = null;
        String name = null;
        String value = null;
 
        // FOR Section (S)
        for (Cell cell : row) {
            if (titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")) {
                break;
            }
 
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
 
            switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                colValue = cell.getRichStringCellValue().getString();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    colValue = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        colValue = String.valueOf(roundVal);
                    } else {
                        colValue = String.valueOf(doubleVal);
                    }
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                colValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA:
                colValue = cell.getCellFormula();
                break;
 
            default:
                colValue = "";
            }
 
            
            
            colValue = colValue.trim();
 
            if (colName.equals("NAME")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    name = colValue;
                //}
            }
 
            if (colName.equals("VALUE")) {
                //if (!StringUtil.nullToBlank(colValue).isEmpty()) {
                    value = colValue;
                //}
            }
        }
        // FOR Section (E)
 
        // TODO : DAO 영역 메소드 호출 후 Data insert 또는 update 로직 구현
        // excel로부터 읽어들인 name, value 값을 DB에 insert하거나 update하는 로직을 구현할 수 있다.
 
    }


}
