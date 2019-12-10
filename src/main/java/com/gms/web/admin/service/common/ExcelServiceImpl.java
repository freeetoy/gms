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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.mapper.manage.BottleMapper;
import com.gms.web.admin.service.manage.BottleService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.GasService;

@Service
public class ExcelServiceImpl implements ExcelService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	
	@Autowired
	private BottleService bottleService;
	
	@Autowired
	private GasService gasService;
	
	@Autowired
	private CustomerService customerService;
	
	@Override
	@Transactional
	public int uploadBottleExcelFile(MultipartFile excelFile) {
		
        List<BottleVO> list = new ArrayList<BottleVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
                BottleVO bottle = new BottleVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }
                
                
                //용기	바코드/RFID	가스	품명	용기체적	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                //0		1			2	3	4		5		6		7		8		9		10	11	

                //N:자사소유
                //Y:타사소유
                String colValue="";
               
                
                for(int j=0; j< 12; j++) {
                	XSSFCell cell = row.getCell(j);
                //if(null != cell) fruit.setGasNm(cell.getNumericCellValue());
                	//logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
                	switch (cell.getCellType()) {
	                    case Cell.CELL_TYPE_STRING:
	                        colValue = cell.getRichStringCellValue().getString();
	                        break;
	                    case Cell.CELL_TYPE_NUMERIC:
	                        if (DateUtil.isCellDateFormatted(cell)) {
	                            colValue = cell.getDateCellValue().toString();
	                            if(j==6) bottle.setBottleChargeDt(cell.getDateCellValue());
	                            else if(j==8) bottle.setBottleCreateDt(cell.getDateCellValue());
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
                	
                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	
                	//용기	바코드/RFID	가스	품명	용기체적	충전용량	충전기한	충전압력	제조일	거래처	작업	소유	
                    //0		1			2	3	4		5		6		7		8		9		10	11	

                    //N:자사소유
                    //Y:타사소유
                	
                	if(j == 0) bottle.setBottleId(colValue);
                	else if(j == 1) bottle.setBottleBarCd(colValue);
                	else if(j == 2) bottle.setGasId(gasService.getGasDetailsByNm(colValue).getGasId());
                	//else if(j == 3) bottle.set
                	else if(j == 4) bottle.setBottleVolumn(colValue);
                	else if(j == 5) bottle.setBottleCapa(colValue);
                	//else if(j == 6) bottle.setBottleChargeDt(DateUtils.);
                	else if(j == 7) bottle.setBottleChargePrss(colValue);
                	//else if(j == 8) bottle.setBottleId(colValue);
                	else if(j == 9) {
                		if(customerService.getCustomerDetailsByNm(colValue) != null)
                			bottle.setCustomerId(customerService.getCustomerDetailsByNm(colValue).getCustomerId());
                	}
                	else if(j == 10) bottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0388"));
                	else if(j == 11) { 
                		if(colValue.equals("자사")) bottle.setBottleSalesYn("N");
                		else bottle.setBottleSalesYn("Y");
                	}
                }
                bottle.setBottleType(PropertyFactory.getProperty("Bottle.Type.Empty"));
                bottle.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                list.add(bottle);
            }
            
            result = bottleService.registerBottles(list);
            
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result);
            
        } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
        return result;
	}
	
	
	@Override
	public int uploadCustomerExcelFile(MultipartFile excelFile) {
		List<CustomerVO> list = new ArrayList<CustomerVO>();
        
        int result = 0;
        try {
        	
            OPCPackage opcPackage = OPCPackage.open(excelFile.getInputStream());
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            
            // 첫번째 시트 불러오기
            XSSFSheet sheet = workbook.getSheetAt(0);
            
            int COLUMN_COUNT = 8;
            for(int i=1; i<sheet.getLastRowNum() + 1; i++) {
            	
            	CustomerVO customer = new CustomerVO();
                XSSFRow row = sheet.getRow(i);
                
                // 행이 존재하기 않으면 패스
                if(null == row) {
                    continue;
                }                             
                
                //	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                //		0		1		2				3	4		5	6	7	
                             
                String colValue="";               
                
                for(int j=0; j< COLUMN_COUNT; j++) {
                	XSSFCell cell = row.getCell(j);
                //if(null != cell) fruit.setGasNm(cell.getNumericCellValue());
                	//logger.debug("ExcelSerive uploadExcelFile i=="+i+ "== "+ cell.getNumericCellValue());                	
                	
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
                	
                	logger.debug("ExcelSerive uploadExcelFile j =="+j+"=="+ colValue);
                	
                	//사업자등록번호	거래처명	거래처주소	거래처사업자등록번호	거래처전화	대표자	업태	종목	이메일
                    //			0		1		2				3		4		5	6	7	
                	if(j == 0) customer.setCustomerNm(colValue);
                	else if(j == 1) customer.setCustomerAddr(colValue);
                	else if(j == 2) customer.setBusinessRegId(colValue);
                	else if(j == 3) customer.setCustomerPhone(colValue);
                	else if(j == 4) customer.setCustomerRepNm(colValue);
                	else if(j == 5) customer.setCustomerBusiType(colValue);
                	else if(j == 6) customer.setCustomerItem(colValue);
                	else if(j == 7) customer.setCustomerEmail(colValue);
                	
                }
                
                customer.setMemberCompSeq(Integer.valueOf(PropertyFactory.getProperty("common.Member.Comp.Daehan")));
              
                list.add(customer);
            }
            
            result = customerService.registerCustomers(list);
            
            logger.debug("$$$$$$$$$$$$$$ ExcelService result "+ result);
            
        } catch (DataAccessException e) {
			// TODO => 데이터베이스 처리 과정에 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		} catch (Exception e) {
			// TODO => 알 수 없는 문제가 발생하였다는 메시지를 전달
			e.printStackTrace();
		}
        return result;
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
